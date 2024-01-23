package xyz.starly.astralshop;

import lombok.Getter;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.starly.astralshop.api.AstralShopPlugin;
import xyz.starly.astralshop.api.registry.ShopRegistry;
import xyz.starly.astralshop.command.ShopAdminCommand;
import xyz.starly.astralshop.command.ShopCommand;
import xyz.starly.astralshop.command.TestShopItemCommand;
import xyz.starly.astralshop.database.ConnectionPoolManager;
import xyz.starly.astralshop.lang.MessageContext;
import xyz.starly.astralshop.shop.inventory.ShopInventory;
import xyz.starly.astralshop.listener.AdminShopInventoryListener;
import xyz.starly.astralshop.registry.SQLShopRegistry;
import xyz.starly.astralshop.registry.YamlShopRegistry;

public class AstralShop extends JavaPlugin implements AstralShopPlugin {

    @Getter
    private static AstralShop instance;

    @Getter private ShopRegistry shopRegistry;
    @Getter private Economy economy;

    @Override
    public void onEnable() {
        instance = this;

        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();
        MessageContext.getInstance().initialize(getFile());

        setupShopRegistry();

        new ShopAdminCommand(this);
        getCommand("shop").setExecutor(new ShopCommand());

        getServer().getPluginManager().registerEvents(new AdminShopInventoryListener(), this);

        // TODO 테스트 코드 | 삭제
        getCommand("shopitem").setExecutor(new TestShopItemCommand(shopRegistry));
    }

    private void setupShopRegistry() {
        if (getConfig().getBoolean("mysql.use")) {
            shopRegistry = new SQLShopRegistry(this);
        } else {
            shopRegistry = new YamlShopRegistry(this);
        }
        shopRegistry.loadShops();
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