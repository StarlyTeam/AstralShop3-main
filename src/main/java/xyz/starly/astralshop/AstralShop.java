package xyz.starly.astralshop;

import lombok.Getter;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.starly.astralshop.api.AstralShopPlugin;
import xyz.starly.astralshop.api.registry.ShopRegistry;
import xyz.starly.astralshop.command.TestShopCommand;
import xyz.starly.astralshop.command.TestShopItemCommand;
import xyz.starly.astralshop.database.ConnectionPoolManager;
import xyz.starly.astralshop.registry.SQLShopRegistry;
import xyz.starly.astralshop.registry.YamlShopRegistry;

public class AstralShop extends JavaPlugin implements AstralShopPlugin {

    @Getter
    private static AstralShop instance;

    @Getter
    private ShopRegistry shopRegistry;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if (getConfig().getBoolean("mysql.use")) {
            ConnectionPoolManager.initializingPoolManager(getConfig());
            ConnectionPoolManager pool = ConnectionPoolManager.getInternalPool();

            getLogger().info("성공적으로 MYSQL 연결하였습니다.");

            shopRegistry = new SQLShopRegistry(pool);
        } else {
            shopRegistry = new YamlShopRegistry(this);
        }

        shopRegistry.loadShops();
        getCommand("shop").setExecutor(new TestShopCommand(shopRegistry));
        getCommand("shopitem").setExecutor(new TestShopItemCommand(shopRegistry));
    }

    @Override
    public void onDisable() {
        ConnectionPoolManager pool = ConnectionPoolManager.getInternalPool();
        if (pool != null) pool.closePool();
    }
}