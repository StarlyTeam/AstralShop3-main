package xyz.starly.astralshop.registry;

import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import xyz.starly.astralshop.api.registry.ShopRegistry;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.shop.ShopImpl;
import xyz.starly.astralshop.shop.serialization.yaml.ShopYamlSerializer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class YamlShopRegistry implements ShopRegistry {

    private final Logger LOGGER;
    private final File shopFolder;
    private final Map<String, Shop> shopMap;

    @SuppressWarnings("unused")
    public YamlShopRegistry(JavaPlugin plugin) {
        this.LOGGER = plugin.getLogger();
        this.shopFolder = new File(plugin.getDataFolder(), "shops/");
        this.shopMap = new HashMap<>();

        if (!shopFolder.exists()) {
            boolean isCreated = shopFolder.mkdirs();
        }
    }

    @Override
    public void loadShops() {
        File[] shopFiles = shopFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (shopFiles != null) {
            for (File shopFile : shopFiles) {
                try {
                    String shopName = shopFile.getName().replace(".yml", "");
                    Shop shop = ShopYamlSerializer.loadShop(shopFile);
                    shopMap.put(shopName, shop);
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
    @SneakyThrows
    public boolean createShop(@NotNull String name) {
        if (shopMap.containsKey(name)) {
            return false;
        }

        Shop newShop = new ShopImpl(name, "", new ArrayList<>());

        File shopFile = new File(shopFolder, name + ".yml");
        if (shopFile.exists()) {
            return false;
        }

        if (!shopFile.createNewFile()) {
            LOGGER.warning("상점 파일을 생성하는 도중 오류가 발생하였습니다. " + shopFile.getName());
            return false;
        }

        ShopYamlSerializer.saveShop(newShop, shopFile);
        shopMap.put(name, newShop);
        return true;
    }

    @Override
    public boolean deleteShop(@NotNull String name) {
        if (!(shopMap.containsKey(name))) {
            return false;
        }

        File shopFile = new File(shopFolder, name + ".yml");

        if (shopFile.exists()) {
            if (!shopFile.delete()) {
                LOGGER.warning("상점 파일을 삭제하는 도중 오류가 발생하였습니다. " + shopFile.getName());
                return false;
            }
        }

        shopMap.remove(name);
        return true;
    }

    @Override
    public Shop getShop(@NotNull String name) {
        return shopMap.get(name);
    }

    @Override
    public @NotNull List<Shop> getShops() {
        return new ArrayList<>(shopMap.values());
    }

    @Override
    public List<String> getShopNames() {
        return new ArrayList<>(shopMap.keySet());
    }
}