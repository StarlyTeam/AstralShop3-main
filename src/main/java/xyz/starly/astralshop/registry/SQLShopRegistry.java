package xyz.starly.astralshop.registry;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.starly.astralshop.AstralShop;
import xyz.starly.astralshop.api.registry.ShopRegistry;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.api.shop.ShopPage;

import xyz.starly.astralshop.database.ConnectionPoolManager;
import xyz.starly.astralshop.shop.ShopImpl;
import xyz.starly.astralshop.shop.ShopPageImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class SQLShopRegistry implements ShopRegistry {

    private final Logger LOGGER;
    private ConnectionPoolManager connectionPoolManager;
    private final Map<String, Shop> shopMap;
    private final String SHOP_SUFFIX = "_shop";

    public SQLShopRegistry(JavaPlugin plugin, ConnectionPoolManager connectionPoolManager) {
        this.LOGGER = plugin.getLogger();
        this.shopMap = new HashMap<>();
        this.connectionPoolManager = connectionPoolManager;
    }

    @Override
    public void loadShops() {
    }

    @Override
    public void saveShops() {

    }

    @Override
    public boolean createShop(String name) {
        String selectSQL = "SHOW TABLES FROM " + connectionPoolManager.getDatabase() + " LIKE '" + name + SHOP_SUFFIX + "'";
        String createShopTableSQL = "CREATE TABLE IF NOT EXISTS `" + name + SHOP_SUFFIX + "` (`shop_name` VARCHAR(255), `permission` VARCHAR(255), `npc` VARCHAR(255));";
        String createShopPageTableSQL = "CREATE TABLE IF NOT EXISTS `" + name + "_shop_page` (`gui_title` VARCHAR(255), `gui_rows` INTEGER);";
        String createShopItemTableSQL = "CREATE TABLE IF NOT EXISTS `" + name + "_shop_items` (" +
                "`itemstack` VARCHAR(255), " +
                "`slot` INTEGER(2), " +
                "`sell_price` INTEGER, " +
                "`buy_price` INTEGER, " +
                "`max_stock` INTEGER, " +
                "`remain_stock` INTEGER, " +
                "`refill_time` TIMESTAMP, " +
                "`total_sell_count` BIGINT, " +
                "`total_buy_count` BIGINT, " +
                "`popularity` INTEGER" +
                ");";

        try {
            Statement createShopStatement = connectionPoolManager.getConnection().createStatement();
            ResultSet createShopResultSet = createShopStatement.executeQuery(selectSQL);

            if (createShopResultSet.next()) {
                return false;
            }

            connectionPoolManager.getConnection().createStatement().executeUpdate(createShopTableSQL);

            String insertSQL = "INSERT INTO " + name + SHOP_SUFFIX + " (shop_name, permission, npc) values (?,?,?)";
            PreparedStatement preparedStatement = connectionPoolManager.getConnection().prepareStatement(insertSQL);

            preparedStatement.setString(1, "");
            preparedStatement.setString(2, "");
            preparedStatement.setString(3, "");

            preparedStatement.executeUpdate();

            connectionPoolManager.getConnection().createStatement().executeUpdate(createShopPageTableSQL);
            connectionPoolManager.getConnection().createStatement().executeUpdate(createShopItemTableSQL);

        } catch (SQLException e) {
            LOGGER.warning(e.getMessage());
        }

        return true;
    }

    @Override
    public boolean deleteShop(String name) {
        String selectSQL = "SHOW TABLES FROM " + connectionPoolManager.getDatabase() + " LIKE '" + name + SHOP_SUFFIX + "'";

        try {
            ResultSet resultSet = connectionPoolManager.getConnection().createStatement().executeQuery(selectSQL);
            if (resultSet.next()) {
                String deleteSQL = "DROP TABLE " + name + SHOP_SUFFIX;
                connectionPoolManager.getConnection().createStatement().execute(deleteSQL);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Shop getShop(String name) {
        return null;
    }

    @Override
    public List<Shop> getShops() {
        List<Shop> shopList = new ArrayList<>();

        try {
            ResultSet showResultSet = connectionPoolManager.getConnection().createStatement().executeQuery("show TABLES");

            while (showResultSet.next()) {
                String shopName = showResultSet.getString(1);
                if (shopName.contains(SHOP_SUFFIX)) {
                    ResultSet selectResultSet = connectionPoolManager.getConnection().createStatement().executeQuery("select " + shopName);
                    List<ShopPage> shopPageList = new ArrayList<>();

                    while (selectResultSet.next()) {
                        int pageNum = selectResultSet.getInt(1);
                        String guiTitle = selectResultSet.getString(2);
                        int guiRows = selectResultSet.getInt(3);

                        //TODO 상점 table 구조 다시 짜야함.
                        ShopPageImpl shopPage = new ShopPageImpl(pageNum, guiTitle, guiRows, null);
                        shopPageList.add(shopPage);

                        Shop shop = new ShopImpl(guiTitle, null, shopPageList);
                        shopList.add(shop);
                    }

                }
            }

        } catch (SQLException e) {

        }

        return shopList;
    }

    @Override
    public List<String> getShopNames() {
        return null;
    }
}