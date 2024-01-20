package xyz.starly.astralshop.registry;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import xyz.starly.astralshop.api.registry.ShopRegistry;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.shop.serialization.ShopYamlSerializer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class YamlShopRegistry implements ShopRegistry {

    private final Logger LOGGER;
    private final File shopFolder;
    private final List<Shop> shops;

    public YamlShopRegistry(JavaPlugin plugin) {
        this.LOGGER = plugin.getLogger();
        this.shopFolder = new File(plugin.getDataFolder(), "shops/");
        this.shops = new ArrayList<>();

        if (!shopFolder.exists()) {
            shopFolder.mkdirs();
        }
    }

    @Override
    public void loadShops() {
        File[] shopFiles = shopFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (shopFiles != null) {
            for (File shopFile : shopFiles) {
                try {
                    Shop shop = ShopYamlSerializer.loadShop(shopFile);
                    shops.add(shop);
                } catch (IOException e) {
                    LOGGER.severe("Could not load shop from file: " + shopFile.getName());
                }
            }
        }
    }

    @Override
    public void saveShops() {
    }

    @Override
    public boolean createShop(@NotNull String name, String guiTitle) {
        return true;
    }

    @Override
    public boolean deleteShop(@NotNull String name) {
        return true;
    }

    @Override
    public Shop getShop(@NotNull String name) {
        for (Shop shop : shops) {
            if (shop.getName().equalsIgnoreCase(name)) {
                return shop;
            }
        }
        return null;
    }

    @Override
    public @NotNull List<Shop> getShops() {
        return shops;
    }
}