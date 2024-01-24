package xyz.starly.astralshop.registry;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import xyz.starly.astralshop.api.registry.ShopMenuRegistry;
import xyz.starly.astralshop.api.shop.ShopItem;
import xyz.starly.astralshop.api.shop.ShopMenuItem;
import xyz.starly.astralshop.shop.ShopMenuItemImpl;
import xyz.starly.astralshop.shop.serialization.yaml.ShopItemYamlSerializer;

import java.io.File;
import java.util.*;

public class ShopMenuRegistryImpl implements ShopMenuRegistry {

    private final File shopsMenuFile;
    private final List<ShopMenuItem> menuItems;

    @Getter private String guiTitle;
    @Getter private int rows;

    @SneakyThrows
    @SuppressWarnings("unused")
    public ShopMenuRegistryImpl(JavaPlugin plugin) {
        this.shopsMenuFile = new File(plugin.getDataFolder(), "shops_menu.yml");
        this.menuItems = new ArrayList<>();

        if (!shopsMenuFile.exists()) {
            boolean isCreated = shopsMenuFile.createNewFile();
        }
    }

    @Override
    public void loadMenuItems() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(shopsMenuFile);
        this.guiTitle = config.getString("shops_menu.gui_title", "");
        this.rows = config.getInt("shops_menu.rows", 6);
        ConfigurationSection menuSection = config.getConfigurationSection("shops_menu.items");
        if (menuSection != null) {
            for (String key : menuSection.getKeys(false)) {
                ConfigurationSection itemSection = menuSection.getConfigurationSection(key);
                if (itemSection != null) {
                    ShopItem shopItem = ShopItemYamlSerializer.deserialize(itemSection);
                    String shop = itemSection.getString("shop");
                    menuItems.add(new ShopMenuItemImpl(Integer.parseInt(key), shopItem.getItemStack(), shop));
                }
            }
        }
    }

    @Override
    public boolean addMenuItem(@NotNull ShopMenuItem menuItem) {
        return menuItems.add(menuItem);
    }

    @Override
    public boolean removeMenuItem(@NotNull String shopName) {
        return menuItems.removeIf(item -> item.getShop().equals(shopName));
    }

    @Override
    public Optional<ShopMenuItem> getMenuItem(String shopName) {
        return menuItems.stream().filter(item -> item.getShop().equals(shopName)).findFirst();
    }

    @Override
    public @NotNull List<ShopMenuItem> getMenuItems() {
        return new ArrayList<>(menuItems);
    }
}