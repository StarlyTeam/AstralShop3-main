package xyz.starly.astralshop.shop.handler.impl;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import xyz.starly.astralshop.shop.enums.StewPotionType;
import xyz.starly.astralshop.shop.handler.ItemTypeHandler;

public class SuspiciousStewTypeHandler implements ItemTypeHandler {

    @Override
    public void serialize(ItemStack itemStack, ConfigurationSection section) {
        // TODO 구현
    }

    @Override
    public void deserialize(ItemStack itemStack, ConfigurationSection section) {
        if (itemStack.getType() == Material.SUSPICIOUS_STEW && section.contains("stewEffect")) {
            String effectName = section.getString("stewEffect");
            if (effectName != null) {
                StewPotionType potionType = StewPotionType.valueOf(effectName.toUpperCase());

                SuspiciousStewMeta stewMeta = (SuspiciousStewMeta) itemStack.getItemMeta();
                if (stewMeta != null) {
                    stewMeta.addCustomEffect(potionType.getEffect(), true);
                    itemStack.setItemMeta(stewMeta);
                }
            }
        }
    }
}