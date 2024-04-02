package kr.starly.astralshop;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.addon.TransactionHandler;
import kr.starly.astralshop.api.registry.ItemAttributeRegistry;
import kr.starly.astralshop.api.registry.ShopRegistry;
import kr.starly.astralshop.api.registry.TransactionHandlerRegistry;
import kr.starly.astralshop.command.ShopAdminCommand;
import kr.starly.astralshop.command.ShopCommand;
import kr.starly.astralshop.database.ConnectionPoolManager;
import kr.starly.astralshop.dispatcher.EntityInteractDispatcher;
import kr.starly.astralshop.hook.PlaceholderAPIHook;
import kr.starly.astralshop.listener.AdminShopInventoryListener;
import kr.starly.astralshop.listener.EntityInteractListener;
import kr.starly.astralshop.message.MessageContext;
import kr.starly.astralshop.registry.ItemAttributeRegistryImpl;
import kr.starly.astralshop.registry.SQLShopRegistry;
import kr.starly.astralshop.registry.TransactionHandlerRegistryImpl;
import kr.starly.astralshop.registry.YamlShopRegistry;
import kr.starly.astralshop.service.SimpleTransactionHandler;
import kr.starly.astralshop.shop.inventory.BaseShopInventory;
import lombok.Getter;

public class AstralShopPlugin extends AstralShop {

    @Getter private static AstralShopPlugin instance;

    @Getter private ShopRegistry shopRegistry;
    @Getter private ItemAttributeRegistry itemAttributeRegistry;
    @Getter private TransactionHandlerRegistry transactionHandlerRegistry;

    @Override
    public void onEnable() {
        AstralShop.setInstance(this);
        instance = this;

        // API Hook
        PlaceholderAPIHook.initializeHook(this);

        // Configuration
        saveDefaultConfig();
        MessageContext.getInstance().loadMessagesFromConfig(getConfig());

        // Shop Registry
        if (getConfig().getBoolean("mysql.use")) {
            ConnectionPoolManager.initializePoolManager(getConfig());
            shopRegistry = new SQLShopRegistry(this);
        } else {
            shopRegistry = new YamlShopRegistry(this);
            shopRegistry.loadShops();
        }

        // ItemAttribute Registry
        this.itemAttributeRegistry = new ItemAttributeRegistryImpl();

        // TransactionHandler Registry
        this.transactionHandlerRegistry = new TransactionHandlerRegistryImpl();
        transactionHandlerRegistry.register(new SimpleTransactionHandler());

        // Command
        new ShopAdminCommand(this);
        getCommand("shop").setExecutor(new ShopCommand());

        // Listener
        EntityInteractDispatcher.register(this);
        getServer().getPluginManager().registerEvents(new EntityInteractListener(), this);
        getServer().getPluginManager().registerEvents(new AdminShopInventoryListener(), this);
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

        shopRegistry.saveShops();
    }
}