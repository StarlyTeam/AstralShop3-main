package xyz.starly.astralshop;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.starly.astralshop.api.AstralShopPlugin;
import xyz.starly.astralshop.api.registry.ShopRegistry;
import xyz.starly.astralshop.command.TestShopItemCommand;
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

        if (getConfig().getBoolean("database.use")) {
            shopRegistry = new SQLShopRegistry();
        } else {
            shopRegistry = new YamlShopRegistry(this);
        }
        shopRegistry.loadShops();

        getCommand("shopitem").setExecutor(new TestShopItemCommand(shopRegistry));
    }
}