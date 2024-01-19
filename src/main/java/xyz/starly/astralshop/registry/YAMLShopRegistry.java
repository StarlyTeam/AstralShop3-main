package xyz.starly.astralshop.registry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.starly.astralshop.AstralShop;
import xyz.starly.astralshop.api.registry.ShopRegistry;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.api.shop.ShopItem;
import xyz.starly.astralshop.api.shop.ShopPage;
import xyz.starly.astralshop.shop.ShopImpl;
import xyz.starly.astralshop.shop.ShopItemImpl;
import xyz.starly.astralshop.shop.ShopPageImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class YAMLShopRegistry implements ShopRegistry {

    private final Logger LOGGER = AstralShop.getInstance().getLogger();

    private final File shopFolder;
    private final Map<String, Shop> shopMap;

    @SuppressWarnings("unused")
    public YAMLShopRegistry(File dataFolder) {
        this.shopFolder = new File(dataFolder, "shops/");
        this.shopMap = new HashMap<>();
        if (!shopFolder.exists()) {
            boolean isCreated = shopFolder.mkdirs();
        }
    }

    public void load() {
        File[] files = shopFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection section = config.getConfigurationSection("shop");
                String name = section.getString("name");
                String guiTitle = section.getString("gui_title");
                String npc = section.getString("npc");

                ConfigurationSection pageSection = config.getConfigurationSection("pages");
                pageSection.getKeys(false).forEach(it -> {
                    int pageNum = Integer.parseInt(it);
                    String pageGuiTitle = pageSection.getString("gui_title");
                    int pageGuiRows = pageSection.getInt("gui_rows");
                    List<ShopItem> shopItem = new ArrayList<>();

                    ShopPage shopPage = new ShopPageImpl(pageNum, pageGuiTitle, pageGuiRows, shopItem);
                    Shop shop = new ShopImpl(name, guiTitle, npc, shopPage);
                    shopMap.put(name, shop);
                });
            }
        }
    }

    @SuppressWarnings("unused")
    public void save() {
        shopMap.forEach((shopName, shop) -> {
            String name = shop.getName();
            String guiTitle = shop.getGuiTitle();
            String npc = shop.getNpc();

            File file = new File(shopName + ".yml");
            if (!file.exists()) {
                try {
                    boolean isCreated = file.createNewFile();
                } catch (IOException e) {
                    LOGGER.warning("파일을 생성하는 도중 오류가 발생하였습니다. " + e);
                }
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection section = config.getConfigurationSection("shop");
            section.set("name", name);
            section.set("gui_title", guiTitle);
            section.set("npc", npc);
        });
    }

    @Override
    public boolean createShop(String name, String guiTitle) {
        if (shopMap.containsKey(name)) {
            return false;
        }

//        Shop shop = new ShopImpl(name, guiTitle, "");
//        shopMap.put(name, shop);
        return true;
    }

    @SuppressWarnings("unused")
    @Override
    public boolean deleteShop(String name) {
        if (!shopMap.containsKey(name)) {
            return false;
        }

        File file = new File(shopFolder, name + ".yml");
        if (!file.exists()) {
            boolean isDeleted = file.delete();
        }

        shopMap.remove(name);
        return true;
    }

    @Override
    public boolean updateShop(String name) {

        return false;
    }

    @Override
    public boolean updateShops() {
        return false;
    }

    @Override
    public Shop getShop(String name) {
        return shopMap.get(name);
    }

    @Override
    public List<Shop> getShops() {
        return new ArrayList<>(shopMap.values());
    }
}