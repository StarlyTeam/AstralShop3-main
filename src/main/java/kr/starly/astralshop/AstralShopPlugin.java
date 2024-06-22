package kr.starly.astralshop;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.registry.ItemAttributeRegistry;
import kr.starly.astralshop.api.repository.ShopRepository;
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
import kr.starly.astralshop.repository.SQLShopRepository;
import kr.starly.astralshop.registry.SimpleTransactionHandlerRegistry;
import kr.starly.astralshop.repository.YamlShopRepository;
import kr.starly.astralshop.shop.transaction.SimpleTransactionHandler;
import kr.starly.astralshop.shop.inventory.BaseShopInventory;
import lombok.Getter;

@Getter
public class AstralShopPlugin extends AstralShop {

    @Getter private static AstralShopPlugin instance;

    private ShopRepository shopRepository;
    private ItemAttributeRegistry itemAttributeRegistry;
    private TransactionHandlerRegistry transactionHandlerRegistry;

    @Override
    public void onEnable() {
        AstralShop.setInstance(this);
        instance = this;

        // API Hook
        PlaceholderAPIHook.initializeHook(this);

        // Configuration
        saveDefaultConfig();
        MessageContext.getInstance().loadMessagesFromConfig(getConfig());

        // TransactionHandler Registry
        this.transactionHandlerRegistry = new SimpleTransactionHandlerRegistry();
        transactionHandlerRegistry.register(new SimpleTransactionHandler());

        // Shop Registry
        if (getConfig().getBoolean("mysql.use")) {
            ConnectionPoolManager.initializePoolManager(getConfig());
            shopRepository = new SQLShopRepository(this);
        } else {
            shopRepository = new YamlShopRepository(this);
            shopRepository.loadShops();
        }

        // ItemAttribute Registry
        this.itemAttributeRegistry = new ItemAttributeRegistryImpl();

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

        shopRepository.saveShops();
    }
}