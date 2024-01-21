package xyz.starly.astralshop.shop.handler.impl;

import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import xyz.starly.astralshop.shop.handler.ItemTypeHandler;

public class LeatherArmorColorTypeHandler implements ItemTypeHandler {

    @Override
    public void serialize(ItemStack itemStack, ConfigurationSection section) {
        // TODO 구현
    }

    @Override
    public void deserialize(ItemStack itemStack, ConfigurationSection section) {
        if (itemStack.getItemMeta() instanceof LeatherArmorMeta && section.contains("leatherColor")) {
            LeatherArmorMeta leatherMeta = (LeatherArmorMeta) itemStack.getItemMeta();
            int colorValue = section.getInt("leatherColor");
            leatherMeta.setColor(Color.fromRGB(colorValue));
            itemStack.setItemMeta(leatherMeta);
        }
    }
}