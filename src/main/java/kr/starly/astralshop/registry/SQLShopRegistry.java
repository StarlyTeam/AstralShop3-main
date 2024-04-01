package kr.starly.astralshop.registry;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.registry.ShopRegistry;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopAccessibility;
import kr.starly.astralshop.api.shop.ShopItem;
import kr.starly.astralshop.api.shop.ShopPage;
import kr.starly.astralshop.database.ConnectionPoolManager;
import kr.starly.astralshop.listener.EntityInteractListener;
import kr.starly.astralshop.shop.ShopImpl;
import kr.starly.astralshop.shop.ShopItemImpl;
import kr.starly.astralshop.shop.ShopPageImpl;
import kr.starly.astralshop.shop.inventory.BaseShopInventory;
import kr.starly.astralshop.shop.serialization.sql.ShopItemSQLSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class SQLShopRegistry implements ShopRegistry {

    private final Logger LOGGER;

    public SQLShopRegistry(JavaPlugin plugin) {
        this.LOGGER = plugin.getLogger();
        initializeTables();
    }

    private void initializeTables() {
        createTableIfNotExists("CREATE TABLE IF NOT EXISTS shops ("
                + "name VARCHAR(255), "
                + "enabled BOOLEAN, "
                + "accessibility VARCHAR(15), "
                + "gui_title VARCHAR(255), "
                + "npc VARCHAR(255), "
                + "PRIMARY KEY (name)"
                + ");");

        createTableIfNotExists("CREATE TABLE IF NOT EXISTS shop_pages ("
                + "shop_name VARCHAR(255), "
                + "shop_page INT, "
                + "gui_title VARCHAR(255), "
                + "`rows` INT, "
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
                + "hide_lore BOOLEAN, "
                + "commands JSON, "
                + "PRIMARY KEY (shop_name, shop_page, shop_slot)"
                + ");");
    }

    private void createTableIfNotExists(String sql) {
        try (Connection conn = ConnectionPoolManager.getInternalPool().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {
            LOGGER.warning("Error creating table: " + e);
        }
    }

    @Override
    public void loadShops() {
        throw new UnsupportedOperationException("'loadShops()' are not supported in SQL-Repository.");
    }

    @Override
    public Shop loadShop(String name) {
        try (Connection conn = ConnectionPoolManager.getInternalPool().getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM shops WHERE name=?")) {
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String guiTitle = rs.getString("gui_title");
                    String npc = rs.getString("npc");
                    List<ShopPage> shopPages = loadShopPages(name);

                    return new ShopImpl(name, false, ShopAccessibility.PRIVATE, guiTitle, npc, shopPages);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

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
                String guiTitle = rs.getString("gui_title");
                int rows = rs.getInt("rows");
                Map<Integer, ShopItem> items = loadShopItems(name, pageNum);

                ShopPage page = new ShopPageImpl(pageNum, guiTitle, rows, items);
                pages.add(page);
            }
        } catch (SQLException e) {
            LOGGER.warning("Error load shop pages: " + e);
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
                String serializedItemStack = rs.getString("item_stack");
                double buyPrice = rs.getDouble("buy_price");
                double sellPrice = rs.getDouble("sell_price");
                int stock = rs.getInt("stock");
                int remainStock = rs.getInt("remain_stock");
                boolean hideLore = rs.getBoolean("hide_lore");

                String rawCommands = rs.getString("commands");
                List<String> commands = new ArrayList<>();
                if (rawCommands != null && !rawCommands.isEmpty()) {
                    JSONArray jsonArray = new JSONArray(rawCommands);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        commands.add(jsonArray.getString(i));
                    }
                }

                ItemStack itemStack = ShopItemSQLSerializer.deserialize(serializedItemStack);
                ShopItem shopItem = new ShopItemImpl(itemStack, buyPrice, sellPrice, stock, remainStock, hideLore, commands);
                items.put(slot, shopItem);
            }
        } catch (SQLException e) {
            LOGGER.warning("Error load shop items: " + e);
        }
        return items;
    }

    public void saveShops() {
        throw new UnsupportedOperationException("'saveShops()' are not supported in SQL-Repository.");
    }

    @Override
    public void saveShop(Shop shop) {
        try (Connection conn = ConnectionPoolManager.getInternalPool().getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM shops WHERE name=?")) {
                pstmt.setString(1, shop.getName());
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO shops VALUES (?, ?, ?, ?, ?)")) {
                pstmt.setString(1, shop.getName());
                pstmt.setBoolean(2, shop.isEnabled());
                pstmt.setString(3, shop.getAccessibility().name());
                pstmt.setString(4, shop.getGuiTitle());
                pstmt.setString(5, shop.getNpc());
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
            LOGGER.warning("Error saving shop: " + e);
        }

        // Refresh
        AstralShop.getInstance().getServer().getOnlinePlayers().forEach((player) -> {
            Inventory openInventory = player.getOpenInventory().getTopInventory();
            if (openInventory.getHolder() instanceof BaseShopInventory openInventory1) {
                openInventory1.updateInventory(player);
            }
        });
    }

    private void saveShopPage(Connection conn, String name, ShopPage page) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO shop_pages VALUES (?, ?, ?, ?)")) {
            pstmt.setString(1, name);
            pstmt.setInt(2, page.getPageNum());
            pstmt.setString(3, page.getGuiTitle());
            pstmt.setInt(4, page.getRows());
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
                    String serializedItemStack = ShopItemSQLSerializer.serialize(shopItem);
                    try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO shop_items VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                        pstmt.setString(1, name);
                        pstmt.setInt(2, page.getPageNum());
                        pstmt.setInt(3, slot);
                        pstmt.setString(4, serializedItemStack);
                        pstmt.setDouble(5, shopItem.getBuyPrice());
                        pstmt.setDouble(6, shopItem.getSellPrice());
                        pstmt.setInt(7, shopItem.getStock());
                        pstmt.setInt(8, shopItem.getRemainStock());
                        pstmt.setBoolean(9, shopItem.isHideLore());
                        pstmt.setString(10, new JSONArray(shopItem.getCommands()).toString());
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
                pstmt.setString(3, ShopAccessibility.PRIVATE.name());
                pstmt.setString(4, name);
                pstmt.setString(5, "");
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
            LOGGER.warning("Error creating shop: " + e);
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
            String sql = "DELETE FROM shops WHERE name=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            LOGGER.warning("Error deleting shop: " + e);
            return false;
        }
    }

    @Override
    public Shop getShop(String name) {
        String sql = "SELECT * FROM shops WHERE name=?";
        try (Connection conn = ConnectionPoolManager.getInternalPool().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                boolean enabled = rs.getBoolean("enabled");
                ShopAccessibility accessibility = ShopAccessibility.valueOf(rs.getString("accessibility"));
                String guiTitle = rs.getString("gui_title");
                String npc = rs.getString("npc");
                List<ShopPage> shopPages = loadShopPages(name);

                return new ShopImpl(name, enabled, accessibility, guiTitle, npc, shopPages);
            }
        } catch (SQLException e) {
            LOGGER.warning("Error getting shop: " + e);
        }
        return null;
    }

    @Override
    public @NotNull List<Shop> getShops() {
        List<Shop> shops = new ArrayList<>();
        String sql = "SELECT * FROM shops";
        try (Connection conn = ConnectionPoolManager.getInternalPool().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                shops.add(getShop(name));
            }
        } catch (SQLException e) {
            LOGGER.warning("Error getting shops: " + e);
        }
        return shops;
    }
}