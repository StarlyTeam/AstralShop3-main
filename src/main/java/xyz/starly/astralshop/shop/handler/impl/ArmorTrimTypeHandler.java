package xyz.starly.astralshop.shop.handler.impl;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import xyz.starly.astralshop.shop.handler.ItemTypeHandler;

public class ArmorTrimTypeHandler implements ItemTypeHandler {

    @Override
    public void serialize(ItemStack itemStack, ConfigurationSection section) {
        // TODO 구현
    }

    @Override
    public void deserialize(ItemStack itemStack, ConfigurationSection section) {
        try {
            if ((itemStack.getType() == Material.NETHERITE_CHESTPLATE ||
                    itemStack.getType() == Material.NETHERITE_HELMET ||
                    itemStack.getType() == Material.NETHERITE_LEGGINGS ||
                    itemStack.getType() == Material.NETHERITE_BOOTS) &&
                    section.contains("armorTrim")) {

                ConfigurationSection trimSection = section.getConfigurationSection("armorTrim");
                if (trimSection != null) {
                    String materialStr = trimSection.getString("type");
                    String patternStr = trimSection.getString("pattern");

                    TrimMaterial material = Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(materialStr.toLowerCase()));
                    TrimPattern pattern = Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(patternStr.toLowerCase()));

                    if (material != null && pattern != null) {
                        ArmorMeta armorMeta = (ArmorMeta) itemStack.getItemMeta();
                        ArmorTrim trim = new ArmorTrim(material, pattern);
                        armorMeta.setTrim(trim);

                        itemStack.setItemMeta(armorMeta);
                    }
                }
            }
        } catch (NoSuchFieldError ignored) {}
    }
}