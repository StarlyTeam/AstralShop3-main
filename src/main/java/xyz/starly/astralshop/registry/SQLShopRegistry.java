package xyz.starly.astralshop.registry;

import xyz.starly.astralshop.api.registry.ShopRegistry;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.database.SQLInjector;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class SQLShopRegistry implements ShopRegistry {
    private Logger LOGGER;

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
        String sql = "CREATE TABLE " + name + "_shop ( name VARCHAR(259) )";

        try {
            sqlInjector.createTable(sql);
        } catch (SQLException e) {
            LOGGER.info("mysql 에러 " + e);
        }

        return false;
    }

    @Override
    public boolean deleteShop(String name) {
        String sql = "";

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