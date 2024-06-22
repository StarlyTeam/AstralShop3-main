package kr.starly.astralshop.shop.serialization.yaml;

import kr.starly.astralshop.api.shop.ShopItem;
import kr.starly.astralshop.shop.ShopItemImpl;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ShopItemYamlSerializer {

    public static ConfigurationSection serialize(ShopItem shopItem) {
        ConfigurationSection section = new MemoryConfiguration();

        section.set("itemStack", shopItem.getItemStack());
        section.set("buyPrice", shopItem.getBuyPrice());
        section.set("sellPrice", shopItem.getSellPrice());
        section.set("stock", shopItem.getStock());
        section.set("remainStock", shopItem.getRemainStock());
        section.set("hideLore", shopItem.isMarker());
        section.set("commands", shopItem.getCommands());
        return section;
    }

    public static ShopItem deserialize(ConfigurationSection section) {
        ItemStack itemStack = section.getItemStack("itemStack");

        double buyPrice = section.getDouble("buyPrice");
        double sellPrice = section.getDouble("sellPrice");
        int stock = section.getInt("stock");
        int remainStock = section.getInt("remainStock");
        boolean hideLore = section.getBoolean("hideLore");
        List<String> commands = section.getStringList("commands");

        return new ShopItemImpl(itemStack, buyPrice, sellPrice, stock, remainStock, hideLore, commands);
    }

    private static void applyEnchantmentGlint(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.addEnchant(Enchantment.LUCK, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemStack.setItemMeta(itemMeta);
        }
    }
}