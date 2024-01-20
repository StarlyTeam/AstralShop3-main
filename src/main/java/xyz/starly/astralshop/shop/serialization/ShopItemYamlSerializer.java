package xyz.starly.astralshop.shop.serialization;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.starly.astralshop.api.shop.ShopItem;
import xyz.starly.astralshop.shop.ShopItemImpl;
import xyz.starly.astralshop.shop.handler.ItemTypeHandler;
import xyz.starly.astralshop.shop.handler.impl.PotionTypeHandler;

import java.util.*;

public class ShopItemYamlSerializer {

    private static final Map<Material, ItemTypeHandler> handlers = new HashMap<>();

    static {
        handlers.put(Material.POTION, new PotionTypeHandler());
    }

    public static ConfigurationSection serialize(ShopItem shopItem) {
        ConfigurationSection section = new MemoryConfiguration();

        ItemStack itemStack = shopItem.getItemStack();
        if (itemStack != null) {
            section.set("item", itemStack.getType().toString());
            section.set("amount", itemStack.getAmount());
        }

        section.set("buyPrice", shopItem.getBuyPrice());
        section.set("sellPrice", shopItem.getSellPrice());
        section.set("stock", shopItem.getStock());
        section.set("remainStock", shopItem.getRemainStock());


        section.set("commands", shopItem.getCommands());

        return section;
    }

    public static ShopItemImpl deserialize(ConfigurationSection section) {
        Material material = Material.valueOf(section.getString("item"));
        int amount = section.getInt("amount", 1);

        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta meta = itemStack.getItemMeta();

        if (section.contains("name")) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("name")));
        }

        if (section.isList("lore")) {
            List<String> lore = new ArrayList<>();
            for (String line : section.getStringList("lore")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(lore);
        }

        if (section.contains("leatherColor") && meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
            int colorValue = section.getInt("leatherColor");
            leatherMeta.setColor(Color.fromRGB(colorValue));
        }

        // TODO 다중 버전 지원 제작
        if (material == Material.valueOf("PLAYER_HEAD") && meta instanceof SkullMeta) {
            if (section.contains("skullOwner")) {
                String skullOwner = section.getString("skullOwner");
                SkullMeta skullMeta = (SkullMeta) meta;
                skullMeta.setOwner(skullOwner);
                itemStack.setItemMeta(skullMeta);
            }
        }

        itemStack.setItemMeta(meta);

        applyEnchantments(itemStack, section);

        if (section.getBoolean("enchantmentGlint", false)) {
            applyEnchantmentGlint(itemStack);
        }

        ItemTypeHandler handler = handlers.get(itemStack.getType());
        if (handler != null) {
            handler.deserialize(itemStack, section);
        }

        ShopItemImpl shopItem = new ShopItemImpl(itemStack);

        shopItem.setBuyPrice(section.getDouble("buyPrice"));
        shopItem.setSellPrice(section.getDouble("sellPrice"));
        shopItem.setStock(section.getInt("stock"));
        shopItem.setRemainStock(section.getInt("remainStock"));

        List<String> commands = section.getStringList("commands");
        shopItem.setCommands(commands);

        return shopItem;
    }

    private static void applyEnchantments(ItemStack itemStack, ConfigurationSection section) {
        if (section.isList("enchantments")) {
            for (String enchantStr : section.getStringList("enchantments")) {
                String[] parts = enchantStr.split(":");
                if (parts.length == 2) {
                    Enchantment enchantment = Enchantment.getByName(parts[0].toUpperCase());
                    int level = Integer.parseInt(parts[1]);
                    if (enchantment != null) {
                        itemStack.addUnsafeEnchantment(enchantment, level);
                    }
                }
            }
        }
    }

    private static void applyEnchantmentGlint(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(meta);
    }
}