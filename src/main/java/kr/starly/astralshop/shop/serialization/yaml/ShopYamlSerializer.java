package kr.starly.astralshop.shop.serialization.yaml;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.addon.TransactionHandler;
import kr.starly.astralshop.api.registry.TransactionHandlerRegistry;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopAccessibility;
import kr.starly.astralshop.api.shop.ShopItem;
import kr.starly.astralshop.api.shop.ShopPage;
import kr.starly.astralshop.shop.ShopImpl;
import kr.starly.astralshop.shop.ShopPageImpl;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ShopYamlSerializer {

    public static void saveShop(Shop shop, File file) throws IOException {
        FileConfiguration config = new YamlConfiguration();

        config.set("shop.enabled", shop.isEnabled());
        config.set("shop.accessibility", shop.getAccessibility().name());
        config.set("shop.gui_title", shop.getGuiTitle());
        config.set("shop.rows", shop.getRows());
        config.set("shop.transaction_handler", shop.getTransactionHandler().getName());

        int index = 1;
        for (ShopPage page : shop.getShopPages()) {
            String basePath = "pages.page_" + index;

            Map<Integer, ShopItem> items = page.getItems();
            for (Map.Entry<Integer, ShopItem> entry : items.entrySet()) {
                config.set(basePath + ".items." + entry.getKey(), ShopItemYamlSerializer.serialize(entry.getValue()));
            }

            index++;
        }

        config.save(file);
    }

    public static Shop loadShop(File file) throws IOException {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        String name = file.getName().replace(".yml", "");
        boolean enabled = config.getBoolean("shop.enabled");
        ShopAccessibility accessibility = ShopAccessibility.valueOf(
                config.getString("shop.accessibility")
        );
        String guiTitle = config.getString("shop.gui_title");
        int rows = config.getInt("shop.rows");

        String transactionHandlerName = config.getString("shop.transaction_handler");
        TransactionHandlerRegistry transactionHandlerRegistry = AstralShop.getInstance().getTransactionHandlerRegistry();
        TransactionHandler transactionHandler = transactionHandlerRegistry.getHandler(transactionHandlerName);
        if (transactionHandler == null) transactionHandler = transactionHandlerRegistry.getHandler("기본");

        List<ShopPage> pages = new ArrayList<>();
        int index = 1;
        while (config.contains("pages.page_" + index)) {
            String basePath = "pages.page_" + index;

            Map<Integer, ShopItem> items = new HashMap<>();
            ConfigurationSection itemsSection = config.getConfigurationSection(basePath + ".items");
            if (itemsSection != null) {
                for (String key : itemsSection.getKeys(false)) {
                    ShopItem item = ShopItemYamlSerializer.deserialize(Objects.requireNonNull(itemsSection.getConfigurationSection(key)));
                    items.put(Integer.parseInt(key), item);
                }
            }

            ShopPage page = new ShopPageImpl(index, items);
            pages.add(page);
            index++;
        }

        return new ShopImpl(name, enabled, accessibility, guiTitle, rows, transactionHandler, pages);
    }
}