package xyz.starly.astralshop.registry;

import xyz.starly.astralshop.api.registry.ShopRegistry;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.database.SQLInjector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SQLShopRegistry implements ShopRegistry {

    private SQLInjector sqlInjector;

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
        String selectSQL = "SHOW TABLES FROM " + sqlInjector.getDbName() + " LIKE '" + name + "_shop'";
        String createTableSQL = "CREATE TABLE IF NOT EXISTS `" + name + "_shop` (`pageNum` INTEGER, `guiTitle` VARCHAR(255), `guiRows` INTEGER);";

        try {
            ResultSet rs = sqlInjector.executeQuery(selectSQL);
            if (!rs.next()) {
                sqlInjector.executeUpdate(createTableSQL);

                String insertSQL = "INSERT INTO " + name + "_shop (pageNum, guiTitle, guiRows) values (?,?,?)";
                PreparedStatement preparedStatement = sqlInjector.preparedStatement(insertSQL);

                preparedStatement.setInt(1, 1);
                preparedStatement.setString(2, guiTitle);
                preparedStatement.setInt(3, 5);

                preparedStatement.executeUpdate();

                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deleteShop(String name) {
        String selectSQL = "SHOW TABLES FROM " + sqlInjector.getDbName() + " LIKE '" + name + "_shop'";

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
        return null;
    }
}