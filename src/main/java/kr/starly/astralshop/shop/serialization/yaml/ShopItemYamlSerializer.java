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
        section.set("commands", shopItem.getCommands());
        return section;
    }

    public static ShopItem deserialize(ConfigurationSection section) {
        ItemStack itemStack = section.getItemStack("itemStack");

        if (section.getBoolean("enchantmentGlint", false)) {
            applyEnchantmentGlint(itemStack);
        }

        ShopItem shopItem = new ShopItemImpl(itemStack);

        shopItem.setBuyPrice(section.getDouble("buyPrice"));
        shopItem.setSellPrice(section.getDouble("sellPrice"));
        shopItem.setStock(section.getInt("stock"));
        shopItem.setRemainStock(section.getInt("remainStock"));

        List<String> commands = section.getStringList("commands");
        shopItem.setCommands(commands);

        return shopItem;
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