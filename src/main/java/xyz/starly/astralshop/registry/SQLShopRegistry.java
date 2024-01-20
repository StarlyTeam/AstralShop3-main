package xyz.starly.astralshop.registry;

import xyz.starly.astralshop.AstralShop;
import xyz.starly.astralshop.api.registry.ShopRegistry;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.api.shop.ShopPage;
import xyz.starly.astralshop.database.SQLInjector;
import xyz.starly.astralshop.shop.ShopImpl;
import xyz.starly.astralshop.shop.ShopPageImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLShopRegistry implements ShopRegistry {

    private SQLInjector sqlInjector;

    private final String shop_suffix = "_shop";

    private static final Logger LOGGER = AstralShop.getInstance().getLogger();

    public SQLShopRegistry(SQLInjector sqlInjector) {
        this.sqlInjector = sqlInjector;
    }

    @Override
    public void loadShops() {

    }

    @Override
    public void saveShops() {

    }

    @Override
    public boolean createShop(String name, String guiTitle) {
        String selectSQL = "SHOW TABLES FROM " + sqlInjector.getDbName() + " LIKE '" + name + "" + shop_suffix + "'";
        String createTableSQL = "CREATE TABLE IF NOT EXISTS `" + name + "_shop` (`pageNum` INTEGER, `guiTitle` VARCHAR(255), `guiRows` INTEGER);";

        try {
            ResultSet rs = sqlInjector.executeQuery(selectSQL);
            if (!rs.next()) {
                sqlInjector.executeUpdate(createTableSQL);

                String insertSQL = "INSERT INTO " + name + shop_suffix + "(pageNum, guiTitle, guiRows) values (?,?,?)";
                PreparedStatement preparedStatement = sqlInjector.preparedStatement(insertSQL);

                preparedStatement.setInt(1, 1);
                preparedStatement.setString(2, guiTitle);
                preparedStatement.setInt(3, 5);

                preparedStatement.executeUpdate();

                return true;
            }
        } catch (SQLException e) {
            LOGGER.warning(e.getMessage());
        }

        return false;
    }

    @Override
    public boolean deleteShop(String name) {
        String selectSQL = "SHOW TABLES FROM " + sqlInjector.getDbName() + " LIKE '" + name + shop_suffix + "'";

        try {
            ResultSet rs = sqlInjector.executeQuery(selectSQL);
            if (rs.next()) {
                String deleteSQL = "DROP TABLE " + name + "_shop";
                sqlInjector.execute(deleteSQL);
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
            ResultSet showResultSet = sqlInjector.executeQuery("show TABLES");

            while (showResultSet.next()) {
                String shopName = showResultSet.getString(1);
                if (shopName.contains(shop_suffix)) {
                    ResultSet selectResultSet = sqlInjector.executeQuery("select " + shopName);
                    List<ShopPage> shopPageList = new ArrayList<>();

                    while (selectResultSet.next()) {
                        int pageNum = selectResultSet.getInt(1);
                        String guiTitle = selectResultSet.getString(2);
                        int guiRows = selectResultSet.getInt(3);

                        //TODO 상점 table 구조 다시 짜야함.
                        ShopPageImpl shopPage = new ShopPageImpl(pageNum, guiTitle, guiRows, null);
                        shopPageList.add(shopPage);

                        Shop shop = new ShopImpl(shopName, guiTitle, null, shopPageList);
                        shopList.add(shop);
                    }

                    LOGGER.info(shopName);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "모든 상점을 불러오는 중 오류가 발생하였습니다 : ");
            LOGGER.log(Level.WARNING, e.getMessage());
        }

        return shopList;
    }
}