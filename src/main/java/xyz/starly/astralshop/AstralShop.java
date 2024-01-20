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

public class AstralShop extends JavaPlugin implements AstralShopPlugin {

    @Getter
    private static AstralShop instance;

    @Getter
    private ShopRegistry shopRegistry;

    @Override
    public void onEnable() {
        instance = this;

        if (getConfig().getBoolean("database.use")) {
            SQLInjector sqlInjector;

            String url = getConfig().getString("url");
            String user = getConfig().getString("user");
            String password = getConfig().getString("password");
            String dbName = getConfig().getString("dbName");

            try {
                sqlInjector = new SQLInjector(url, user, password, dbName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            shopRegistry = new SQLShopRegistry(sqlInjector);
        } else {
            shopRegistry = new YamlShopRegistry(this);
        }

        shopRegistry.loadShops();
        getCommand("shop").setExecutor(new TestShopCommand(shopRegistry));
        getCommand("shopitem").setExecutor(new TestShopItemCommand(shopRegistry));
    }
}