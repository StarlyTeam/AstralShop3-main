package xyz.starly.astralshop.shop.handler.impl;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import xyz.starly.astralshop.shop.enums.ShopPotionType;
import xyz.starly.astralshop.shop.handler.ItemTypeHandler;

import java.util.List;

public class PotionTypeHandler implements ItemTypeHandler {

    @Override
    public void serialize(ItemStack itemStack, ConfigurationSection configurationSection) {
        // TODO 구현
    }

    @Override
    public void deserialize(ItemStack itemStack, ConfigurationSection configurationSection) {
        if (itemStack.getType() == Material.POTION || itemStack.getType() == Material.SPLASH_POTION || itemStack.getType() == Material.LINGERING_POTION) {
            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();

            if (configurationSection.isList("potionTypes")) {
                List<String> potionTypeStrList = configurationSection.getStringList("potionTypes");

                if (!potionTypeStrList.isEmpty()) {
                    String firstPotionTypeStr = potionTypeStrList.get(0);
                    ShopPotionType firstPotionType = ShopPotionType.valueOf(firstPotionTypeStr.toUpperCase());

                    if (potionMeta != null) {
                        potionMeta.setBasePotionData(new PotionData(PotionType.valueOf(firstPotionType.getName()), firstPotionType.isExtended(), firstPotionType.isUpgraded()));

                        potionMeta.clearCustomEffects();
                        for (int i = 1; i < potionTypeStrList.size(); i++) {
                            String potionTypeStr = potionTypeStrList.get(i);
                            ShopPotionType potionType = ShopPotionType.valueOf(potionTypeStr.toUpperCase());
                            PotionEffectType effectType = PotionEffectType.getByName(potionType.getName());

                            if (effectType != null) {
                                PotionEffect effect = new PotionEffect(effectType, potionType.getDuration(), potionType.getAmplifier());
                                potionMeta.addCustomEffect(effect, true);
                            }
                        }
                    }
                }
            }

            itemStack.setItemMeta(potionMeta);
        }
    }
}