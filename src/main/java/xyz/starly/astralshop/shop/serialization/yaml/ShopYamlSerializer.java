package xyz.starly.astralshop.shop.serialization.yaml;

import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.potion.PotionEffect;
import xyz.starly.astralshop.AstralShop;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.api.shop.ShopItem;
import xyz.starly.astralshop.api.shop.ShopPage;
import xyz.starly.astralshop.shop.ShopImpl;
import xyz.starly.astralshop.shop.ShopPageImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ShopYamlSerializer {

    private static final Logger LOGGER = AstralShop.getInstance().getLogger();

    public static void saveShop(Shop shop, File file) throws IOException {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("shop.gui_title", shop.getGuiTitle());
        config.set("shop.npc", shop.getNpc());

        int index = 1;
        for (ShopPage page : shop.getShopPages()) {
            String basePath = "pages.page_" + index;
            config.set(basePath + ".gui_title", page.getGuiTitle());
            config.set(basePath + ".gui_rows", page.getRows());

            Map<Integer, ShopItem> items = page.getItems();
            for (Map.Entry<Integer, ShopItem> entry : items.entrySet()) {
                config.set(basePath + ".items." + entry.getKey(), ShopItemYamlSerializer.serialize(entry.getValue()));
            }

            index++;
        }

        config.save(file);
    }

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_CYAN = "\u001B[36m";

    public static Shop loadShop(File file) throws IOException {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        String guiTitle = config.getString("shop.gui_title");
        String npc = config.getString("shop.npc");

        List<ShopPage> pages = new ArrayList<>();
        int index = 1;
        while (config.contains("pages.page_" + index)) {
            String basePath = "pages.page_" + index;
            String pageGuiTitle = config.getString(basePath + ".gui_title");
            int rows = config.getInt(basePath + ".gui_rows");

            LOGGER.info("Page " + index + ": " + pageGuiTitle + " with " + rows + " rows");

            Map<Integer, ShopItem> items = new HashMap<>();
            ConfigurationSection itemsSection = config.getConfigurationSection(basePath + ".items");
            if (itemsSection != null) {
                for (String key : itemsSection.getKeys(false)) {
                    ShopItem item = ShopItemYamlSerializer.deserialize(itemsSection.getConfigurationSection(key));
                    items.put(Integer.parseInt(key), item);

                    /* TODO 삭제 해야함 | 로그 부분 2 */
                    LOGGER.info(" - Slot " + key + ":");
                    LOGGER.info("   - Item: " + item.getItemStack().getType());
                    LOGGER.info("   - Amount: " + item.getItemStack().getAmount());

                    LOGGER.info("   - Buy Price: " + item.getBuyPrice());
                    LOGGER.info("   - Sell Price: " + item.getSellPrice());

                    LOGGER.info("   - Stock: " + item.getStock());
                    LOGGER.info("   - Remain Stock: " + item.getRemainStock());
                    LOGGER.info("   - Commands: " + item.getCommands());

                    LOGGER.info(" ");
                    /* TODO 삭제 해야함 | 여기까지 */
                }
            }

            ShopPage page = new ShopPageImpl(index, pageGuiTitle, rows, items);
            pages.add(page);
            index++;
        }

        Shop shop = new ShopImpl(guiTitle, npc, pages);
        return shop;
    }
}