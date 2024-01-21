package xyz.starly.astralshop.shop.handler.impl;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import xyz.starly.astralshop.shop.handler.ItemTypeHandler;

public class SpawnerTypeHandler implements ItemTypeHandler {

    @Override
    public void serialize(ItemStack itemStack, ConfigurationSection section) {
        // TODO 구현
    }

    @Override
    public void deserialize(ItemStack itemStack, ConfigurationSection section) {
        if (itemStack.getType() == Material.SPAWNER && section.contains("spawnerType")) {
            String spawnerTypeStr = section.getString("spawnerType");
            EntityType entityType = EntityType.valueOf(spawnerTypeStr.toUpperCase());

            if (itemStack.getItemMeta() instanceof BlockStateMeta) {
                BlockStateMeta meta = (BlockStateMeta) itemStack.getItemMeta();

                if (meta.getBlockState() instanceof CreatureSpawner) {
                    CreatureSpawner spawner = (CreatureSpawner) meta.getBlockState();
                    spawner.setSpawnedType(entityType);

                    meta.setBlockState(spawner);
                    itemStack.setItemMeta(meta);
                }
            }
        }
    }
}