package xyz.starly.astralshop.registry;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import xyz.starly.astralshop.api.registry.ShopRegistry;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.api.shop.ShopItem;
import xyz.starly.astralshop.api.shop.ShopPage;
import xyz.starly.astralshop.database.ConnectionPoolManager;
import xyz.starly.astralshop.shop.ShopImpl;
import xyz.starly.astralshop.shop.ShopItemImpl;
import xyz.starly.astralshop.shop.ShopPageImpl;
import xyz.starly.astralshop.shop.serialization.sql.ShopItemSQLSerializer;

import java.sql.*;
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
//        loadShops();
    }

    private void initializeTables() {
        createTableIfNotExists("CREATE TABLE IF NOT EXISTS shops ("
                + "shop_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(255) NOT NULL, "
                + "gui_title VARCHAR(255), "
                + "npc VARCHAR(255)"
                + ");");

        createTableIfNotExists("CREATE TABLE IF NOT EXISTS shop_pages ("
                + "page_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "shop_id INT, "
                + "page_num INT, "
                + "gui_title VARCHAR(255), "
                + "`rows` INT, "
                + "FOREIGN KEY (shop_id) REFERENCES shops(shop_id)"
                + ");");

        createTableIfNotExists("CREATE TABLE IF NOT EXISTS shop_items ("
                + "item_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "page_id INT, "
                + "slot INT, "
                + "item_stack TEXT, "
                + "buy_price DOUBLE, "
                + "sell_price DOUBLE, "
                + "stock INT, "
                + "remain_stock INT, "
                + "commands TEXT, "
                + "FOREIGN KEY (page_id) REFERENCES shop_pages(page_id)"
                + ");");
    }

    private void createTableIfNotExists(String sql) {
        try (Connection conn = ConnectionPoolManager.getInternalPool().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            LOGGER.warning("Error creating table: " + e);
        }
    }

//    @Override
//    public void loadShops() {
//        String sql = "SELECT * FROM shops";
//        try (Connection conn = ConnectionPoolManager.getInternalPool().getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql);
//             ResultSet rs = stmt.executeQuery()) {
//
//            while (rs.next()) {
//                String shopName = rs.getString("name");
//                String guiTitle = rs.getString("gui_title");
//                String npc = rs.getString("npc");
//
//                List<ShopPage> shopPages = loadShopPages(shopName);
//                Shop shop = new ShopImpl(shopName, guiTitle, npc, shopPages);
//            }
//        } catch (SQLException e) {
//            LOGGER.warning("Error load shops: " + e);
//        }
//    }

    private List<ShopPage> loadShopPages(String shopName) {
        List<ShopPage> pages = new ArrayList<>();
        String sql = "SELECT sp.* FROM shop_pages sp JOIN shops s ON sp.shop_id = s.shop_id WHERE s.name = ?";
        try (Connection conn = ConnectionPoolManager.getInternalPool().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, shopName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int pageNum = rs.getInt("page_num");
                String guiTitle = rs.getString("gui_title");
                int rows = rs.getInt("rows");

                Map<Integer, ShopItem> items = loadShopItems(rs.getInt("page_id"));
                ShopPage page = new ShopPageImpl(pageNum, guiTitle, rows, items);
                pages.add(page);
            }
        } catch (SQLException e) {
            LOGGER.warning("Error load shop pages: " + e);
        }
        return pages;
    }

    private Map<Integer, ShopItem> loadShopItems(int pageId) {
        Map<Integer, ShopItem> items = new HashMap<>();
        String sql = "SELECT * FROM shop_items WHERE page_id = ?";
        try (Connection conn = ConnectionPoolManager.getInternalPool().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pageId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int slot = rs.getInt("slot");
                String serializedItemStack = rs.getString("item_stack");
                double buyPrice = rs.getDouble("buy_price");
                double sellPrice = rs.getDouble("sell_price");
                int stock = rs.getInt("stock");
                int remainStock = rs.getInt("remain_stock");
                String commandsJson = rs.getString("commands");
                List<String> commands = new ArrayList<>();
                if (commandsJson != null && !commandsJson.isEmpty()) {
                    JSONArray jsonArray = new JSONArray(commandsJson);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        commands.add(jsonArray.getString(i));
                    }
                }

                ItemStack itemStack = ShopItemSQLSerializer.deserialize(serializedItemStack);
                ShopItem shopItem = new ShopItemImpl(itemStack, buyPrice, sellPrice, stock, remainStock, commands);
                items.put(slot, shopItem);
            }
        } catch (SQLException e) {
            LOGGER.warning("Error load shop items: " + e);
        }
        return items;
    }

    public void saveShops() {
    }

    @Override
    public void loadShops() {

    }

    @Override
    public void loadShop(Shop shop) {

    }

    @Override
    public void saveShop(Shop shop) {
        try (Connection conn = ConnectionPoolManager.getInternalPool().getConnection()) {
            String updateShopSql = "UPDATE shops SET gui_title = ?, npc = ? WHERE name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateShopSql)) {
                stmt.setString(1, shop.getGuiTitle());
                stmt.setString(2, shop.getNpc());
                stmt.setString(3, shop.getName());
                stmt.executeUpdate();
            }

            for (ShopPage page : shop.getShopPages()) {
                saveShopPage(conn, shop.getName(), page);
            }
        } catch (SQLException e) {
            LOGGER.warning("Error saving shop: " + e);
        }
    }

    private void saveShopPage(Connection conn, String shopName, ShopPage page) throws SQLException {
        String updatePageSql = "UPDATE shop_pages SET gui_title = ?, `rows` = ? WHERE shop_id = (SELECT shop_id FROM shops WHERE name = ?) AND page_num = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updatePageSql)) {
            stmt.setString(1, page.getGuiTitle());
            stmt.setInt(2, page.getRows());
            stmt.setString(3, shopName);
            stmt.setInt(4, page.getPageNum());
            stmt.executeUpdate();
        }

        for (Map.Entry<Integer, ShopItem> entry : page.getItems().entrySet()) {
            saveShopItem(conn, shopName, page.getPageNum(), entry.getKey(), entry.getValue());
        }
    }

    private void saveShopItem(Connection conn, String shopName, int pageNum, int slot, ShopItem shopItem) throws SQLException {
        if (shopItem == null || shopItem.getItemStack() == null || shopItem.getItemStack().getType() == Material.AIR) {
            deleteShopItem(conn, shopName, pageNum, slot);
        } else {
            String serializedItemStack = ShopItemSQLSerializer.serialize(shopItem);

            String checkItemSql = "SELECT item_id FROM shop_items WHERE page_id = (SELECT page_id FROM shop_pages WHERE shop_id = (SELECT shop_id FROM shops WHERE name = ?) AND page_num = ?) AND slot = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkItemSql)) {
                checkStmt.setString(1, shopName);
                checkStmt.setInt(2, pageNum);
                checkStmt.setInt(3, slot);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    String updateItemSql = "UPDATE shop_items SET item_stack = ?, buy_price = ?, sell_price = ?, stock = ?, remain_stock = ?, commands = ? WHERE item_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateItemSql)) {
                        updateStmt.setString(1, serializedItemStack);
                        updateStmt.setDouble(2, shopItem.getBuyPrice());
                        updateStmt.setDouble(3, shopItem.getSellPrice());
                        updateStmt.setInt(4, shopItem.getStock());
                        updateStmt.setInt(5, shopItem.getRemainStock());
                        updateStmt.setString(6, new JSONArray(shopItem.getCommands()).toString());
                        updateStmt.setInt(7, rs.getInt("item_id"));
                        updateStmt.executeUpdate();
                    }
                } else {
                    String insertItemSql = "INSERT INTO shop_items (page_id, slot, item_stack, buy_price, sell_price, stock, remain_stock, commands) VALUES ((SELECT page_id FROM shop_pages WHERE shop_id = (SELECT shop_id FROM shops WHERE name = ?) AND page_num = ?), ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertItemSql)) {
                        insertStmt.setString(1, shopName);
                        insertStmt.setInt(2, pageNum);
                        insertStmt.setInt(3, slot);
                        insertStmt.setString(4, serializedItemStack);
                        insertStmt.setDouble(5, shopItem.getBuyPrice());
                        insertStmt.setDouble(6, shopItem.getSellPrice());
                        insertStmt.setInt(7, shopItem.getStock());
                        insertStmt.setInt(8, shopItem.getRemainStock());
                        insertStmt.setString(9, new JSONArray(shopItem.getCommands()).toString());
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
    }

    private void deleteShopItem(Connection conn, String shopName, int pageNumber, int slot) throws SQLException {
        String deleteItemSql = "DELETE FROM shop_items WHERE page_id = (SELECT page_id FROM shop_pages WHERE shop_id = (SELECT shop_id FROM shops WHERE name = ?) AND page_num = ?) AND slot = ?";
        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteItemSql)) {
            deleteStmt.setString(1, shopName);
            deleteStmt.setInt(2, pageNumber);
            deleteStmt.setInt(3, slot);
            deleteStmt.executeUpdate();
        }
    }

    @Override
    public boolean createShop(@NotNull String shopName) {
        try (Connection conn = ConnectionPoolManager.getInternalPool().getConnection()) {
            if (shopExists(conn, shopName)) {
                return false;
            }

            String insertShopSql = "INSERT INTO shops (name, gui_title, npc) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertShopSql)) {
                stmt.setString(1, shopName);
                stmt.setString(2, shopName);
                stmt.setString(3, "");
                stmt.executeUpdate();
            }

            String insertPageSql = "INSERT INTO shop_pages (shop_id, page_num, gui_title, `rows`) VALUES ((SELECT shop_id FROM shops WHERE name = ?), ?, ?, ?)";
            try (PreparedStatement pageStmt = conn.prepareStatement(insertPageSql)) {
                pageStmt.setString(1, shopName);
                pageStmt.setInt(2, 1);
                pageStmt.setString(3, shopName);
                pageStmt.setInt(4, 6);
                pageStmt.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            LOGGER.warning("Error creating shop: " + e);
            return false;
        }
    }

    private boolean shopExists(Connection conn, String shopName) throws SQLException {
        String checkShopSql = "SELECT shop_id FROM shops WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkShopSql)) {
            stmt.setString(1, shopName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    @Override
    public boolean deleteShop(@NotNull String shopName) {
        try (Connection conn = ConnectionPoolManager.getInternalPool().getConnection()) {
            String sql = "DELETE FROM shops WHERE name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, shopName);
                int affectedRows = stmt.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            LOGGER.warning("Error deleting shop: " + e);
            return false;
        }
    }

    @Override
    public Shop getShop(String shopName) {
        String sql = "SELECT * FROM shops WHERE name = ?";
        try (Connection conn = ConnectionPoolManager.getInternalPool().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, shopName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String guiTitle = rs.getString("gui_title");
                String npc = rs.getString("npc");
                List<ShopPage> shopPages = loadShopPages(shopName);
                return new ShopImpl(shopName, guiTitle, npc, shopPages);
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
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String shopName = rs.getString("name");
                shops.add(getShop(shopName));
            }
        } catch (SQLException e) {
            LOGGER.warning("Error getting shops: " + e);
        }
        return shops;
    }
}