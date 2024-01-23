package xyz.starly.astralshop;

import lombok.Getter;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.starly.astralshop.api.AstralShopPlugin;
import xyz.starly.astralshop.api.registry.ShopRegistry;
import xyz.starly.astralshop.command.ShopAdminCommand;
import xyz.starly.astralshop.command.TestShopCommand;
import xyz.starly.astralshop.command.TestShopItemCommand;
import xyz.starly.astralshop.database.ConnectionPoolManager;
import xyz.starly.astralshop.shop.inventory.ShopInventory;
import xyz.starly.astralshop.listener.AdminShopInventoryListener;
import xyz.starly.astralshop.registry.SQLShopRegistry;
import xyz.starly.astralshop.registry.YamlShopRegistry;

import java.sql.SQLException;

public class AstralShop extends JavaPlugin implements AstralShopPlugin {

    @Getter
    private static AstralShop instance;

    @Getter private ShopRegistry shopRegistry;
    @Getter private static Economy economy;

    @Override
    public void onEnable() {
        instance = this;

        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();

        if (getConfig().getBoolean("mysql.use")) {
            ConnectionPoolManager.initializingPoolManager(getConfig());
            ConnectionPoolManager pool = ConnectionPoolManager.getInternalPool();

            try {
                pool.getConnection();
            } catch (SQLException e) {
                getLogger().info(e.getMessage());
            }

            getLogger().info("성공적으로 MYSQL 연결하였습니다.");

            shopRegistry = new SQLShopRegistry(this, pool);
            getCommand("test").setExecutor(new TestShopCommand((SQLShopRegistry) shopRegistry));
        } else {
            shopRegistry = new YamlShopRegistry(this);
        }

        shopRegistry.loadShops();

        new ShopAdminCommand(this);
        getCommand("shopitem").setExecutor(new TestShopItemCommand(shopRegistry));

        getServer().getPluginManager().registerEvents(new AdminShopInventoryListener(), this);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    @Override
    public void onDisable() {
        shopRegistry.saveShops();

        ConnectionPoolManager pool = ConnectionPoolManager.getInternalPool();
        if (pool != null) pool.closePool();

        getServer().getOnlinePlayers().forEach(player -> {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof ShopInventory) {
                player.closeInventory();
            }
        });
    }
}