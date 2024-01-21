package xyz.starly.astralshop.shop.handler.impl;

import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MusicInstrumentMeta;
import xyz.starly.astralshop.shop.handler.ItemTypeHandler;

public class InstrumentTypeHandler implements ItemTypeHandler {

    @Override
    public void serialize(ItemStack itemStack, ConfigurationSection section) {
        // TODO 구현
    }

    @Override
    public void deserialize(ItemStack itemStack, ConfigurationSection section) {
        try {
            if (itemStack.getType() == Material.GOAT_HORN && section.contains("instrument")) {
                String instrumentName = section.getString("instrument").toLowerCase() + "_goat_horn";
                MusicInstrumentMeta meta = (MusicInstrumentMeta) itemStack.getItemMeta();

                NamespacedKey key = NamespacedKey.minecraft(instrumentName);
                MusicInstrument instrument = MusicInstrument.getByKey(key);

                if (instrument != null) {
                    meta.setInstrument(instrument);
                }

                itemStack.setItemMeta(meta);
            }
        } catch (NoClassDefFoundError ignored) {}
    }
}