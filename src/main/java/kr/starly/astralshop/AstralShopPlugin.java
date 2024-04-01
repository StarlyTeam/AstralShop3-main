package kr.starly.astralshop;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.addon.TransactionHandler;
import kr.starly.astralshop.api.registry.ItemAttributeRegistry;
import kr.starly.astralshop.api.registry.ShopRegistry;
import kr.starly.astralshop.command.ShopAdminCommand;
import kr.starly.astralshop.command.ShopCommand;
import kr.starly.astralshop.database.ConnectionPoolManager;
import kr.starly.astralshop.dispatcher.EntityInteractDispatcher;
import kr.starly.astralshop.listener.AdminShopInventoryListener;
import kr.starly.astralshop.listener.EntityInteractListener;
import kr.starly.astralshop.message.MessageContext;
import kr.starly.astralshop.registry.ItemAttributeRegistryImpl;
import kr.starly.astralshop.registry.SQLShopRegistry;
import kr.starly.astralshop.registry.YamlShopRegistry;
import kr.starly.astralshop.service.SimpleTransactionHandler;
import kr.starly.astralshop.shop.inventory.BaseShopInventory;
import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;

import java.sql.Connection;

public class AstralShopPlugin extends AstralShop {

    @Getter private static AstralShopPlugin instance;
    @Getter private boolean papiAvailable;

    @Getter private ShopRegistry shopRegistry;
    @Getter private ItemAttributeRegistry itemAttributeRegistry;
    @Getter @Setter private TransactionHandler transactionHandler;

    @Override
    public void onEnable() {
        AstralShop.setInstance(this);
        instance = this;

        // Configuration
        saveDefaultConfig();
        MessageContext.getInstance().loadMessagesFromConfig(getConfig());

        // ShopRegistry
        if (getConfig().getBoolean("mysql.use")) {
            ConnectionPoolManager.initializePoolManager(getConfig());
            shopRegistry = new SQLShopRegistry(this);
        } else {
            shopRegistry = new YamlShopRegistry(this);
            shopRegistry.loadShops();
        }

        // itemAttributeRegistry
        itemAttributeRegistry = new ItemAttributeRegistryImpl();

        // Transaction Handler
        transactionHandler = new SimpleTransactionHandler();

        // Command
        new ShopAdminCommand(this);
        getCommand("shop").setExecutor(new ShopCommand());

        // Listener
        EntityInteractDispatcher.register(this);
        getServer().getPluginManager().registerEvents(new EntityInteractListener(), this);
        getServer().getPluginManager().registerEvents(new AdminShopInventoryListener(), this);

        // External Plugin
        try {
            PlaceholderAPI.getPlaceholders();
            papiAvailable = true;
        } catch (NoClassDefFoundError | NullPointerException ex) {
            papiAvailable = false;
        }
    }

    @Override
    public void onDisable() {
        ConnectionPoolManager pool = ConnectionPoolManager.getInternalPool();
        if (pool != null) pool.closePool();

        getServer().getOnlinePlayers().forEach(player -> {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof BaseShopInventory) {
                player.closeInventory();
            }
        });

        if (!(shopRegistry instanceof SQLShopRegistry)) {
            shopRegistry.saveShops();
        }
    }
}