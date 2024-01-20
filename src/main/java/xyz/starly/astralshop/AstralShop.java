package xyz.starly.astralshop;

import lombok.Getter;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.starly.astralshop.api.AstralShopPlugin;
import xyz.starly.astralshop.api.registry.ShopRegistry;
import xyz.starly.astralshop.command.TestShopCommand;
import xyz.starly.astralshop.command.TestShopItemCommand;
import xyz.starly.astralshop.database.SQLInjector;
import xyz.starly.astralshop.registry.SQLShopRegistry;
import xyz.starly.astralshop.registry.YamlShopRegistry;

import java.sql.SQLException;
import java.util.logging.Level;

public class AstralShop extends JavaPlugin implements AstralShopPlugin {

    @Getter
    private static AstralShop instance;

    @Getter
    private ShopRegistry shopRegistry;

    @Override
    public void onEnable() {
        instance = this;

        if (getConfig().getBoolean("mysql.use")) {
            SQLInjector sqlInjector = null;

            String url = getConfig().getString("mysql.url");
            String user = getConfig().getString("mysql.user");
            String password = getConfig().getString("mysql.password");
            String dbName = getConfig().getString("mysql.dbName");

            try {
                sqlInjector = new SQLInjector(url, user, password, dbName);
                getLogger().info("성공적으로 MYSQL 연결하였습니다.");
            } catch (ClassNotFoundException e) {
                getLogger().log(Level.WARNING, "JDBC Driver 클래스를 찾을 수 없습니다. " + e.getMessage());
            } catch (SQLException e) {
                getLogger().log(Level.WARNING, "MYSQL 연결 오류 " + e.getMessage());
            }

            shopRegistry = new SQLShopRegistry(sqlInjector);
        } else {
            shopRegistry = new YamlShopRegistry(this);
        }

        shopRegistry.loadShops();
        getCommand("shop").setExecutor(new TestShopCommand(shopRegistry));
        getCommand("shopitem").setExecutor(new TestShopItemCommand(shopRegistry));

        saveDefaultConfig();
    }
}