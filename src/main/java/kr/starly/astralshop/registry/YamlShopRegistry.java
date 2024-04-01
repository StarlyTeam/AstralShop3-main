package kr.starly.astralshop.registry;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.registry.ShopRegistry;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopAccessibility;
import kr.starly.astralshop.api.shop.ShopPage;
import kr.starly.astralshop.listener.EntityInteractListener;
import kr.starly.astralshop.shop.ShopImpl;
import kr.starly.astralshop.shop.ShopPageImpl;
import kr.starly.astralshop.shop.inventory.BaseShopInventory;
import kr.starly.astralshop.shop.serialization.yaml.ShopYamlSerializer;
import lombok.SneakyThrows;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

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
                    String name = shopFile.getName().replace(".yml", "");
                    Shop shop = ShopYamlSerializer.loadShop(shopFile);
                    shopMap.put(name, shop);
                } catch (IOException e) {
                    LOGGER.severe("Could not load shop from file: " + shopFile.getName());
                }
            }
        }
    }

    @Override
    public Shop loadShop(String name) {
        File shopFile = new File(shopFolder, name + ".yml");
        if (!shopFile.exists()) {
            LOGGER.warning("Could not load shop from file: " + shopFile.getName());
            return null;
        }

        try {
            return ShopYamlSerializer.loadShop(shopFile);
        } catch (IOException e) {
            LOGGER.severe("상점 파일을 불러오는 도중 오류가 발생하였습니다: " + shopFile.getName());
            return null;
        }
    }

    @Override
    public void saveShops() {
        shopMap.values().forEach(this::saveShop);
    }

    @Override
    @SneakyThrows
    public void saveShop(Shop shop) {
        String name = shop.getName();

        File shopFile = new File(shopFolder, name + ".yml");
        if (!shopFile.exists()) return;

        ShopYamlSerializer.saveShop(shop, shopFile);

        // Refresh
        AstralShop.getInstance().getServer().getOnlinePlayers().forEach((player) -> {
            Inventory openInventory = player.getOpenInventory().getTopInventory();
            if (openInventory.getHolder() instanceof BaseShopInventory openInventory1) {
                openInventory1.updateInventory(player);
            }
        });
    }

    @Override
    @SneakyThrows
    public boolean createShop(@NotNull String name) {
        if (shopMap.containsKey(name)) {
            return false;
        }

        List<ShopPage> shopPages = new ArrayList<>();
        Shop newShop = new ShopImpl(name, false, ShopAccessibility.PRIVATE, name, "", shopPages);
        shopPages.add(new ShopPageImpl(1, newShop.getGuiTitle(), 6, new HashMap<>()));

        File shopFile = new File(shopFolder, name + ".yml");
        if (shopFile.exists()) {
            return false;
        }

        if (!shopFile.createNewFile()) {
            LOGGER.warning("Could not delete shop: " + shopFile.getName());
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
                LOGGER.warning("Could not delete shop: " + shopFile.getName());
                return false;
            }
        }

        shopMap.remove(name);
        return true;
    }

    @Override
    public @NotNull List<Shop> getShops() {
        return new ArrayList<>(shopMap.values());
    }

    @Override
    public Shop getShop(@NotNull String name) {
        return shopMap.get(name);
    }
}