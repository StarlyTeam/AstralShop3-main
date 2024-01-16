package xyz.starly.astralshop;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.starly.astralshop.api.AstralShopPlugin;

public class AstralShop extends JavaPlugin implements AstralShopPlugin {

    @Getter
    private static AstralShop instance;

    @Override
    public void onEnable() {
        instance = this;
    }
}