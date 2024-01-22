package xyz.starly.astralshop.registry;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

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

import java.util.*;
import java.util.logging.Logger;

public class SQLShopRegistry implements ShopRegistry {

    private final Logger LOGGER;
    private ConnectionPoolManager connectionPoolManager;
    private final Map<String, Shop> shopMap;

    private final String SHOP_SUFFIX = "_shop";
    private final String SHOP_ITEM_SUFFIX = "_items";
    private final String SHOP_PAGE_SUFFIX = "_page";

    public SQLShopRegistry(JavaPlugin plugin, ConnectionPoolManager connectionPoolManager) {
        this.LOGGER = plugin.getLogger();
        this.shopMap = new HashMap<>();
        this.connectionPoolManager = connectionPoolManager;
    }

    @Override
    public void loadShops() {
        try {
            ResultSet showResultSet = connectionPoolManager.getConnection().createStatement().executeQuery("show TABLES");
            List<ShopPage> shopPageList = new ArrayList<>();
            Shop shop;
            String npc = null;

            Map<Integer, ShopItem> shopItems = new HashMap<>();

            while (showResultSet.next()) {
                String shopName = showResultSet.getString(1);
                if (shopName.contains(SHOP_SUFFIX) && !shopName.contains(SHOP_SUFFIX + SHOP_ITEM_SUFFIX)
                        && !shopName.contains(SHOP_SUFFIX + SHOP_PAGE_SUFFIX)) {

                    String selectSQL = "SELECT * FROM " + shopName;

                    try {
                        ResultSet resultSet = connectionPoolManager.getConnection().createStatement().executeQuery(selectSQL);

                        while (resultSet.next()) {
                            npc = resultSet.getString(2);
                        }

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                } else if (shopName.contains(SHOP_SUFFIX + SHOP_ITEM_SUFFIX) && !shopName.contains(SHOP_SUFFIX + SHOP_PAGE_SUFFIX)) {
                    String selectSQL = "SELECT * FROM " + shopName;

                    try {
                        ResultSet resultSet = connectionPoolManager.getConnection().createStatement().executeQuery(selectSQL);

                        while (resultSet.next()) {
                            String encodedItemStack = resultSet.getString(1);
                            int slot = resultSet.getInt(2);

                            ShopItem shopItem = new ShopItemImpl(ShopItemSQLSerializer.deserialize(encodedItemStack));
                            shopItems.put(slot, shopItem);
                        }
                        
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                } else if (shopName.contains(SHOP_SUFFIX + SHOP_PAGE_SUFFIX)) {
                    String selectSQL = "SELECT * FROM " + shopName;

                    try {
                        ResultSet resultSet = connectionPoolManager.getConnection().createStatement().executeQuery(selectSQL);

                        while (resultSet.next()) {
                            int pageNum = resultSet.getInt(1);
                            String guiTitle = resultSet.getString(2);
                            int guiRows = resultSet.getInt(3);

                            ShopPage shopPage = new ShopPageImpl(pageNum, guiTitle, guiRows, shopItems);
                            shopPageList.add(shopPage);
                            shop = new ShopImpl(shopName, npc, shopPageList);
                            shopMap.put(shopName, shop);
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.warning("상점을 로드하는 도중 오류가 발생하였습니다. \n" + e.getMessage());
        }
    }

    @Override
    public void saveShops() {

    }

    public void addItems(String name, ItemStack itemStack) {
        String insertSQL = "INSERT INTO " + name + "_shop_items " +
                "(itemstack, slot, sell_price, buy_price, max_stock, remain_stock, refill_time, total_sell_count, total_buy_count, popularity, commands) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connectionPoolManager.getConnection().prepareStatement(insertSQL);

            ShopItem shopItem = new ShopItemImpl(itemStack);

            preparedStatement.setString(1, ShopItemSQLSerializer.serialize(shopItem));
            preparedStatement.setInt(2, 1);
            preparedStatement.setInt(3, 1);
            preparedStatement.setInt(4, 1);
            preparedStatement.setInt(5, 1);
            preparedStatement.setInt(6, 1);
            preparedStatement.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setLong(8, 1);
            preparedStatement.setLong(9, 1);
            preparedStatement.setInt(10, 1);
            preparedStatement.setString(11, "");

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.warning("상점 아이템을 추가하는 도중 오류가 발생하였습니다. \n" + e.getMessage());
        }
    }

    @Override
    public boolean createShop(String name) {
        String selectSQL = "SHOW TABLES FROM " + connectionPoolManager.getDatabase() + " LIKE '" + name + SHOP_SUFFIX + "'";
        String createShopTableSQL = "CREATE TABLE IF NOT EXISTS `" + name + SHOP_SUFFIX + "` (`shop_name` VARCHAR(255), `permission` VARCHAR(255), `npc` VARCHAR(255));";
        String createShopPageTableSQL = "CREATE TABLE IF NOT EXISTS `" + name + SHOP_SUFFIX + SHOP_PAGE_SUFFIX + "` (`pageNum` INTEGER,`gui_title` VARCHAR(255), `gui_rows` INTEGER);";
        String createShopItemTableSQL = "CREATE TABLE IF NOT EXISTS `" + name + SHOP_SUFFIX + SHOP_ITEM_SUFFIX + "` (" +
                "`itemstack` VARCHAR(1000), " +
                "`slot` INTEGER(2), " +
                "`sell_price` INTEGER, " +
                "`buy_price` INTEGER, " +
                "`max_stock` INTEGER, " +
                "`remain_stock` INTEGER, " +
                "`refill_time` TIMESTAMP, " +
                "`total_sell_count` BIGINT, " +
                "`total_buy_count` BIGINT, " +
                "`popularity` INTEGER," +
                "`commands` VARCHAR(1000)" +
                ");";

        try {
            Statement createShopStatement = connectionPoolManager.getConnection().createStatement();
            ResultSet createShopResultSet = createShopStatement.executeQuery(selectSQL);

            if (createShopResultSet.next()) {
                return false;
            }

            connectionPoolManager.getConnection().createStatement().executeUpdate(createShopTableSQL);

            String insertSQL = "INSERT INTO " + name + SHOP_SUFFIX + " (shop_name, permission, npc) VALUES (?,?,?)";
            PreparedStatement preparedStatement = connectionPoolManager.getConnection().prepareStatement(insertSQL);

            preparedStatement.setString(1, "");
            preparedStatement.setString(2, "");
            preparedStatement.setString(3, "");

            preparedStatement.executeUpdate();

            connectionPoolManager.getConnection().createStatement().executeUpdate(createShopPageTableSQL);
            connectionPoolManager.getConnection().createStatement().executeUpdate(createShopItemTableSQL);

            String insertPageSQL = "INSERT INTO " + name + SHOP_SUFFIX + SHOP_PAGE_SUFFIX + " (pageNum, gui_title, gui_rows) VALUES (?,?,?)";
            preparedStatement = connectionPoolManager.getConnection().prepareStatement(insertPageSQL);

            preparedStatement.setInt(1, 1);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, 5);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            LOGGER.warning("상점을 생성하는 도중 오류가 발생하였습니다. \n" + e.getMessage());
        }

        return true;
    }

    @Override
    public boolean deleteShop(String name) {
        String selectSQL = "SHOW TABLES FROM " + connectionPoolManager.getDatabase() + " LIKE '" + name + SHOP_SUFFIX + "'";

        try {
            ResultSet resultSet = connectionPoolManager.getConnection().createStatement().executeQuery(selectSQL);
            if (resultSet.next()) {
                String deleteShopSQL = "DROP TABLE " + name + SHOP_SUFFIX;
                String deleteShopItemsSQL = "DROP TABLE " + name + SHOP_SUFFIX + SHOP_ITEM_SUFFIX;
                String deleteShopPageSQL = "DROP TABLE " + name + SHOP_SUFFIX + SHOP_PAGE_SUFFIX;

                connectionPoolManager.getConnection().createStatement().execute(deleteShopSQL);
                connectionPoolManager.getConnection().createStatement().execute(deleteShopItemsSQL);
                connectionPoolManager.getConnection().createStatement().execute(deleteShopPageSQL);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.warning("상점을 제거하는 도중 오류가 발생하였습니다. \n" + e.getMessage());
        }

        return false;
    }

    @Override
    public Shop getShop(String name) {
        Shop shop = null;

        String selectShopSQL = "SELECT * FROM " + name + SHOP_SUFFIX;
        String selectShopItemsSQL = "SELECT * FROM " + name + SHOP_SUFFIX + SHOP_ITEM_SUFFIX;
        String selectShopPageSQL = "SELECT * FROM " + name + SHOP_SUFFIX + SHOP_PAGE_SUFFIX;

        try {
            ResultSet selectShopRs = connectionPoolManager.getConnection().createStatement().executeQuery(selectShopSQL);

            if (!selectShopRs.next()) {
                return null;
            }

            ResultSet selectShopItemsRs = connectionPoolManager.getConnection().createStatement().executeQuery(selectShopItemsSQL);
            ResultSet selectShopPageRs = connectionPoolManager.getConnection().createStatement().executeQuery(selectShopPageSQL);

            Map<Integer, ShopItem> shopItemMap = new HashMap<>();

            while (selectShopPageRs.next()) {
                int pageNum = selectShopPageRs.getInt(1);
                String guiPageTitle = selectShopPageRs.getString(2);
                int guiRows = selectShopPageRs.getInt(3);

                while (selectShopItemsRs.next()) {
                    List<ShopPage> shopPageList = new ArrayList<>();

                    String encodedItemStack = selectShopItemsRs.getString(1);
                    int slot = selectShopItemsRs.getInt(2);

                    ShopItem shopItem = new ShopItemImpl(ShopItemSQLSerializer.deserialize(encodedItemStack));
                    shopItemMap.put(slot, shopItem);

                    ShopPage shopPage = new ShopPageImpl(pageNum, guiPageTitle, guiRows, shopItemMap);
                    shopPageList.add(shopPage);

                    String guiTitle = selectShopItemsRs.getString(1);
                    String npc = selectShopItemsRs.getString(3);

                    shop = new ShopImpl(guiTitle, npc, shopPageList);
                }
            }

            return shop;

        } catch (SQLException e) {
            LOGGER.warning("상점을 불러오는 도중 오류가 발생하였습니다. \n" + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Shop> getShops() {
        return new ArrayList<>(shopMap.values());
    }

    @Override
    public List<String> getShopNames() {
        return new ArrayList<>(shopMap.keySet());
    }
}