package xyz.starly.astralshop.shop.handler.impl;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import xyz.starly.astralshop.shop.handler.ItemTypeHandler;

import java.util.ArrayList;
import java.util.List;

public class FireworkRocketTypeHandler implements ItemTypeHandler {

    @Override
    public void serialize(ItemStack itemStack, ConfigurationSection section) {
        // TODO 구현
    }

    @Override
    public void deserialize(ItemStack itemStack, ConfigurationSection section) {
        if (itemStack.getType() == Material.FIREWORK_ROCKET) {
            FireworkMeta fireworkMeta = (FireworkMeta) itemStack.getItemMeta();

            if (section.contains("firework")) {
                ConfigurationSection fireworkSection = section.getConfigurationSection("firework");

                if (fireworkSection.contains("duration")) {
                    int duration = fireworkSection.getInt("duration");
                    fireworkMeta.setPower(duration);
                }

                List<Color> colors = new ArrayList<>();
                if (fireworkSection.isList("colors")) {
                    for (String colorName : fireworkSection.getStringList("colors")) {
                        Color color = getColorByName(colorName);
                        if (color != null) {
                            colors.add(color);
                        }
                    }
                }

                List<Color> fadeColors = new ArrayList<>();
                if (fireworkSection.isList("fadeColors")) {
                    for (String colorName : fireworkSection.getStringList("fadeColors")) {
                        Color color = getColorByName(colorName);
                        if (color != null) {
                            fadeColors.add(color);
                        }
                    }
                }

                boolean flicker = fireworkSection.getBoolean("flicker", false);
                boolean trail = fireworkSection.getBoolean("trail", false);

                FireworkEffect.Type shape = FireworkEffect.Type.valueOf(fireworkSection.getString("shape", "BALL").toUpperCase());

                FireworkEffect effect = FireworkEffect.builder()
                        .withColor(colors)
                        .withFade(fadeColors)
                        .flicker(flicker)
                        .trail(trail)
                        .with(shape)
                        .build();

                fireworkMeta.addEffect(effect);
                itemStack.setItemMeta(fireworkMeta);
            }
        }
    }

    private Color getColorByName(String colorName) {
        try {
            return (Color) Color.class.getField(colorName.toUpperCase()).get(null);
        } catch (Exception e) {
            return null;
        }
    }
}