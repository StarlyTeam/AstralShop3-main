package xyz.starly.astralshop.shop.handler;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public interface ItemTypeHandler {

    void serialize(ItemStack itemStack, ConfigurationSection section);

    void deserialize(ItemStack itemStack, ConfigurationSection section);
}