package kr.starly.astralshop.repository;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.addon.TransactionHandler;
import kr.starly.astralshop.api.registry.TransactionHandlerRegistry;
import kr.starly.astralshop.api.repository.ShopRepository;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopAccessibility;
import kr.starly.astralshop.api.shop.ShopItem;
import kr.starly.astralshop.api.shop.ShopPage;
import kr.starly.astralshop.database.ConnectionPoolManager;
import kr.starly.astralshop.shop.ShopImpl;
import kr.starly.astralshop.shop.ShopItemImpl;
import kr.starly.astralshop.shop.ShopPageImpl;
import kr.starly.libs.json.JSONArray;
import kr.starly.libs.json.JSONObject;
import kr.starly.libs.util.EncodeUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class SQLShopRepository implements ShopRepository {

    private final Logger logger;

    public SQLShopRepository(JavaPlugin plugin) {
        this.logger = plugin.getLogger();
        initializeTables();
    }

    private void initializeTables() {
        createTableIfNotExists("CREATE TABLE IF NOT EXISTS shops ("
                + "name VARCHAR(255), "
                + "enabled BOOLEAN, "
                + "accessibility VARCHAR(15), "
                + "gui_title VARCHAR(255), "
                + "rows INT, "
                + "transaction_handler VARCHAR(50), "
                + "PRIMARY KEY (name)"
                + ");");

        createTableIfNotExists("CREATE TABLE IF NOT EXISTS shop_pages ("
                + "shop_name VARCHAR(255), "
                + "shop_page INT, "
                + "PRIMARY KEY (shop_name, shop_page)"
                + ");");

        createTableIfNotExists("CREATE TABLE IF NOT EXISTS shop_items ("
                + "shop_name VARCHAR(255), "
                + "shop_page INT, "
                + "shop_slot INT, "
                + "item_stack TEXT, "
                + "buy_price DOUBLE, "
                + "sell_price DOUBLE, "
                + "stock INT, "
                + "remain_stock INT, "
                + "marker BOOLEAN, "
                + "commands JSON, "
                + "PRIMARY KEY (shop_name, shop_page, shop_slot)"
                + ");");
    }

    private void createTableIfNotExists(String sql) {
        try (Connection conn = ConnectionPoolManager.getInternalPool().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {
            logger.warning("Error occurred while creating table: " + e);
        }
    }

    @Override
    public void loadShops() {
        throw new UnsupportedOperationException("Cannot bulk load shops with SQLRepository.");
    }

    @Override
    public Shop loadShop(String name) {
        return null;
    }

    private List<ShopPage> loadShopPages(String name) {
        List<ShopPage> pages = new ArrayList<>();
        try (Connection conn = ConnectionPoolManager.getInternalPool().getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM shop_pages WHERE shop_name=?")) {
            pstmt.setString(1, name);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int pageNum = rs.getInt("shop_page");
                Map<Integer, ShopItem> items = loadShopItems(name, pageNum);

                ShopPage page = new ShopPageImpl(pageNum, items);
                pages.add(page);
            }
        } catch (SQLException e) {
            logger.warning("Error occurred while loading shop pages: " + e);
        }

        return pages;
    }

    private Map<Integer, ShopItem> loadShopItems(String name, int pageNum) {
        Map<Integer, ShopItem> items = new HashMap<>();
        try (Connection conn = ConnectionPoolManager.getInternalPool().getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM shop_items WHERE shop_name=? AND shop_page=?")) {
            pstmt.setString(1, name);
            pstmt.setInt(2, pageNum);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int slot = rs.getInt("shop_slot");
                ItemStack itemStack = EncodeUtils.deserialize(rs.getString("item_stack"), ItemStack.class);
                double buyPrice = rs.getDouble("buy_price");
                double sellPrice = rs.getDouble("sell_price");
                int stock = rs.getInt("stock");
                int remainStock = rs.getInt("remain_stock");
                boolean hideLore = rs.getBoolean("marker");

                String rawCommands = rs.getString("commands");
                List<String> commands = new ArrayList<>();
                if (rawCommands != null && !rawCommands.isEmpty()) {
                    JSONArray jsonArray = new JSONArray(rawCommands);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        commands.add(jsonArray.getString(i));
                    }
                }

                String rawAttributes = rs.getString("attributes");
                Map<String, Object> attributes = new HashMap<>();
                if (rawAttributes != null && !rawAttributes.isEmpty()) {
                    JSONObject jsonObject = new JSONObject(rawAttributes);
                    for (String key : jsonObject.keySet()) {
                        Object value = jsonObject.get(key);
                        attributes.put(key, value);
                    }
                }

                ShopItem shopItem = new ShopItemImpl(itemStack, buyPrice, sellPrice, stock, remainStock, hideLore, commands, attributes);
                items.put(slot, shopItem);
            }
        } catch (SQLException e) {
            logger.warning("Error occurred while loading shop items: " + e);
        }
        return items;
    }

    @Override
    public void saveShops() {
        throw new UnsupportedOperationException("Cannot bulk save shops with SQLRepository.");
    }

    @Override
    public void saveShop(Shop shop) {
        try (Connection conn = ConnectionPoolManager.getInternalPool().getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM shops WHERE name=?")) {
                pstmt.setString(1, shop.getName());
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO shops VALUES (?, ?, ?, ?, ?, ?)")) {
                pstmt.setString(1, shop.getName());
                pstmt.setBoolean(2, shop.isEnabled());
                pstmt.setString(3, shop.getAccessibility().name());
                pstmt.setString(4, shop.getGuiTitle());
                pstmt.setInt(5, shop.getRows());
                pstmt.setString(5, shop.getTransactionHandler().getName());
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM shop_pages WHERE shop_name=?")) {
                pstmt.setString(1, shop.getName());
                pstmt.executeUpdate();
            }

            for (ShopPage page : shop.getShopPages()) {
                saveShopPage(conn, shop.getName(), page);
            }
        } catch (SQLException e) {
            logger.warning("Error occurred while saving shop: " + e);
        }
    }

    private void saveShopPage(Connection conn, String name, ShopPage page) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO shop_pages VALUES (?, ?)")) {
            pstmt.setString(1, name);
            pstmt.setInt(2, page.getPageNum());
            pstmt.executeUpdate();
        }

        page.getItems().forEach((slot, shopItem) -> {
            try {
                try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM shop_items WHERE shop_name=? AND shop_page=? AND shop_slot=?")) {
                    pstmt.setString(1, name);
                    pstmt.setInt(2, page.getPageNum());
                    pstmt.setInt(3, slot);
                    pstmt.executeUpdate();
                }

                if (shopItem != null && shopItem.getItemStack() != null && shopItem.getItemStack().getType() != Material.AIR) {
                    try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO shop_items VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                        pstmt.setString(1, name);
                        pstmt.setInt(2, page.getPageNum());
                        pstmt.setInt(3, slot);
                        pstmt.setString(4, EncodeUtils.serialize(shopItem));
                        pstmt.setDouble(5, shopItem.getBuyPrice());
                        pstmt.setDouble(6, shopItem.getSellPrice());
                        pstmt.setInt(7, shopItem.getStock());
                        pstmt.setInt(8, shopItem.getRemainStock());
                        pstmt.setBoolean(9, shopItem.isMarker());
                        pstmt.setString(10, new JSONArray(shopItem.getCommands()).toString());
                        pstmt.setString(11, new JSONObject(shopItem.getAttributes()).toString());
                        pstmt.executeUpdate();
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public boolean createShop(@NotNull String name) {
        try (Connection conn = ConnectionPoolManager.getInternalPool().getConnection()) {
            if (isShopExists(conn, name)) {
                return false;
            }

            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO shops VALUES (?, ?, ?, ?, ?)")) {
                pstmt.setString(1, name);
                pstmt.setBoolean(2, false);
                pstmt.setString(3, ShopAccessibility.NONE.name());
                pstmt.setString(4, name);
                pstmt.setString(5, "기본");
                pstmt.executeUpdate();
            }

            try (PreparedStatement pageStmt = conn.prepareStatement("INSERT INTO shop_pages VALUES (?, ?, ?, ?)")) {
                pageStmt.setString(1, name);
                pageStmt.setInt(2, 1);
                pageStmt.setString(3, name);
                pageStmt.setInt(4, 6);
                pageStmt.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            logger.warning("Error occurred while creating shop: " + e);
            return false;
        }
    }

    private boolean isShopExists(Connection conn, String name) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) AS `count` FROM shops WHERE name=?")) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            rs.next();
            return rs.getInt("count") > 0;
        }
    }

    @Override
    public boolean deleteShop(@NotNull String name) {
        try (Connection conn = ConnectionPoolManager.getInternalPool().getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM shops WHERE name=?")) {
                pstmt.setString(1, name);
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            logger.warning("Error occurred while deleting shop: " + e);
            return false;
        }
    }

    @Override
    public @NotNull List<Shop> getShops() {
        List<Shop> shops = new ArrayList<>();
        try (Connection conn = ConnectionPoolManager.getInternalPool().getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM shops");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                shops.add(getShop(name));
            }
        } catch (SQLException e) {
            logger.warning("Error occurred while getting shops: " + e);
        }
        return shops;
    }

    @Override
    public Shop getShop(String name) {
        try (Connection conn = ConnectionPoolManager.getInternalPool().getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM shops WHERE name=?")) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                boolean enabled = rs.getBoolean("enabled");
                ShopAccessibility accessibility = ShopAccessibility.valueOf(
                        rs.getString("accessibility")
                );
                String guiTitle = rs.getString("gui_title");
                int rows = rs.getInt("rows");

                String transactionHandlerName = rs.getString("transaction_handler");
                TransactionHandlerRegistry transactionHandlerRegistry = AstralShop.getInstance().getTransactionHandlerRegistry();
                TransactionHandler transactionHandler = transactionHandlerRegistry.getHandler(transactionHandlerName);
                if (transactionHandler == null) transactionHandler = transactionHandlerRegistry.getHandler("기본");

                List<ShopPage> shopPages = loadShopPages(name);

                return new ShopImpl(name, enabled, accessibility, guiTitle, rows, transactionHandler, shopPages);
            }
        } catch (SQLException e) {
            logger.warning("Error occurred while loading shop: " + e);
        }
        return null;
    }
}