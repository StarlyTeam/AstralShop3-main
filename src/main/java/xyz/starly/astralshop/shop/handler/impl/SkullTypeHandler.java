package xyz.starly.astralshop.shop.handler.impl;

import kr.starly.core.util.PlayerSkullUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.starly.astralshop.shop.handler.ItemTypeHandler;

public class SkullTypeHandler implements ItemTypeHandler {

    @Override
    public void serialize(ItemStack itemStack, ConfigurationSection section) {
        // TODO 구현
    }

    @Override
    public void deserialize(ItemStack itemStack, ConfigurationSection section) {
        if (itemStack.getType() == Material.PLAYER_HEAD) {
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();

            if (section.contains("skullTexture")) {
                String texture = section.getString("skullTexture");
                ItemStack customSkull = PlayerSkullUtil.getCustomSkull(texture);
                itemStack.setItemMeta(customSkull.getItemMeta());
            } else if (section.contains("skullOwner")) {
                String skullOwner = section.getString("skullOwner");
                skullMeta.setOwner(skullOwner);
                itemStack.setItemMeta(skullMeta);
            }
        }
    }
}