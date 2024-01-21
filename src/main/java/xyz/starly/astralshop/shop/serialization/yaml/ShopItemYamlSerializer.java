package xyz.starly.astralshop.shop.serialization.yaml;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.starly.astralshop.api.shop.ShopItem;
import xyz.starly.astralshop.shop.ShopItemImpl;
import xyz.starly.astralshop.shop.handler.ItemTypeHandler;
import xyz.starly.astralshop.shop.handler.impl.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopItemYamlSerializer {

    private static final Map<Material, ItemTypeHandler> handlers = new HashMap<>();

    static {
        handlers.put(Material.POTION, new PotionTypeHandler());
        handlers.put(Material.SPAWNER, new SpawnerTypeHandler());
        handlers.put(Material.GOAT_HORN, new InstrumentTypeHandler());
        handlers.put(Material.SUSPICIOUS_STEW, new SuspiciousStewTypeHandler());
        handlers.put(Material.KNOWLEDGE_BOOK, new RecipeTypeHandler());
        handlers.put(Material.FIREWORK_ROCKET, new FireworkRocketTypeHandler());
        handlers.put(Material.PLAYER_HEAD, new SkullTypeHandler());

        LeatherArmorColorTypeHandler leatherArmorColorTypeHandler = new LeatherArmorColorTypeHandler();
        handlers.put(Material.LEATHER_HELMET, leatherArmorColorTypeHandler);
        handlers.put(Material.LEATHER_CHESTPLATE, leatherArmorColorTypeHandler);
        handlers.put(Material.LEATHER_LEGGINGS, leatherArmorColorTypeHandler);
        handlers.put(Material.LEATHER_BOOTS, leatherArmorColorTypeHandler);

        ArmorTrimTypeHandler armorTrimHandler = new ArmorTrimTypeHandler();
        handlers.put(Material.NETHERITE_CHESTPLATE, armorTrimHandler);
        handlers.put(Material.NETHERITE_HELMET, armorTrimHandler);
        handlers.put(Material.NETHERITE_LEGGINGS, armorTrimHandler);
        handlers.put(Material.NETHERITE_BOOTS, armorTrimHandler);
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
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null) {
            if (section.contains("name")) {
                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("name")));
            }

            if (section.isList("lore")) {
                List<String> lore = new ArrayList<>();
                for (String line : section.getStringList("lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                itemMeta.setLore(lore);
            }

            if (section.contains("customModelData")) {
                itemMeta.setCustomModelData(section.getInt("customModelData"));
            }

            if (section.getBoolean("unbreakable")) {
                itemMeta.setUnbreakable(true);
            }
        }

        itemStack.setItemMeta(itemMeta);

        applyEnchantments(itemStack, section);

        if (section.getBoolean("enchantmentGlint", false)) {
            applyEnchantmentGlint(itemStack);
        }

        ItemTypeHandler handler = handlers.get(itemStack.getType());
        if (handler != null) {
            handler.deserialize(itemStack, section);
        }

        PDCTypeHandler pdcHandler = new PDCTypeHandler();
        pdcHandler.deserialize(itemStack, section);

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