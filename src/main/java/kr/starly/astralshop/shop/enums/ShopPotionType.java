package kr.starly.astralshop.shop.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ShopPotionType {

    AWKWARD("AWKWARD", 0, false, 0, false, new String[0]),
    FIRE_RESISTANCE("FIRE_RESISTANCE", 3600, false, 0, false, new String[]{"RESISTANCE_FIRE"}),
    HARMING("INSTANT_DAMAGE", 1, false, 0, false, new String[]{"INSTANT_DAMAGE", "HARM", "DAMAGE_INSTANT"}),
    HEALING("INSTANT_HEAL", 1, false, 0, false, new String[]{"INSTANT_HEALTH", "HEAL"}),
    INVISIBILITY("INVISIBILITY", 3600, false, 0, false, new String[]{"INVIS"}),
    LEAPING("JUMP", 3600, false, 0, false, new String[]{"JUMP_BOOST", "JUMP"}),
    LONG_FIRE_RESISTANCE("FIRE_RESISTANCE", 9600, true, 0, false, new String[]{"FIRE_RESISTANCE_LONG", "RESISTANCE_FIRE_LONG"}),
    LONG_INVISIBILITY("INVISIBILITY", 9600, true, 0, false, new String[]{"LONG_INVIS"}),
    LONG_LEAPING("JUMP", 9600, true, 0, false, new String[]{"LONG_JUMP_BOOST", "LONG_JUMP"}),
    LONG_NIGHT_VISION("NIGHT_VISION", 9600, true, 0, false, new String[0]),
    LONG_POISON("POISON", 2400, true, 0, false, new String[0]),
    LONG_REGENERATION("REGEN", 2400, true, 0, false, new String[]{"LONG_REGEN"}),
    LONG_SLOW_FALLING("SLOW_FALLING", 4800, true, 0, false, new String[0]),
    LONG_SLOWNESS("SLOWNESS", 4800, true, 0, false, new String[]{"LONG_SLOW"}),
    LONG_STRENGTH("STRENGTH", 9600, true, 0, false, new String[0]),
    LONG_SWIFTNESS("SPEED", 9600, true, 0, false, new String[]{"LONG_SPEED"}),
    LONG_TURTLE_MASTER("TURTLE_MASTER", 800, true, 0, false, new String[0]),
    LONG_WATER_BREATHING("WATER_BREATHING", 9600, true, 0, false, new String[0]),
    LONG_WEAKNESS("WEAKNESS", 4800, true, 0, false, new String[0]),
    LUCK("LUCK", 6000, false, 0, false, new String[]{"LUCKY"}),
    MUNDANE("MUNDANE", 0, false, 0, false, new String[0]),
    NIGHT_VISION("NIGHT_VISION", 3600, false, 0, false, new String[0]),
    POISON("POISON", 900, false, 0, false, new String[0]),
    REGENERATION("REGEN", 900, false, 0, false, new String[]{"REGEN"}),
    SLOW_FALLING("SLOW_FALLING", 1800, false, 0, false, new String[0]),
    SLOWNESS("SLOWNESS", 1800, false, 0, false, new String[]{"SLOW"}),
    SWIFTNESS("SPEED", 3600, false, 0, false, new String[]{"SPEED"}),
    STRENGTH("STRENGTH", 3600, false, 0, false, new String[0]),
    STRONG_HARMING("INSTANT_DAMAGE", 1, false, 1, true, new String[]{"STRONG_INSTANT_DAMAGE", "STRONG_HARM", "STRONG_DAMAGE_INSTANT"}),
    STRONG_HEALING("INSTANT_HEAL", 1, false, 1, true, new String[]{"STRONG_INSTANT_HEALTH", "STRONG_HEAL"}),
    STRONG_LEAPING("JUMP", 1800, false, 1, true, new String[]{"STRONG_JUMP_BOOST", "STRONG_JUMP"}),
    STRONG_POISON("POISON", 440, false, 0, true, new String[0]),
    STRONG_REGENERATION("REGEN", 440, false, 1, true, new String[]{"STRONG_REGEN"}),
    STRONG_SLOWNESS("SLOWNESS", 200, false, 3, true, new String[]{"STRONG_SLOW"}),
    STRONG_STRENGTH("STRENGTH", 1800, false, 1, true, new String[0]),
    STRONG_SWIFTNESS("SPEED", 1800, false, 1, true, new String[]{"STRONG_SPEED"}),
    STRONG_TURTLE_MASTER("TURTLE_MASTER", 400, false, 1, true, new String[0]),
    THICK("THICK", 0, false, 0, false, new String[0]),
    TURTLE_MASTER("TURTLE_MASTER", 400, false, 0, false, new String[0]),
    WATER("WATER", 0, false, 0, false, new String[]{"EMPTY"}),
    WATER_BREATHING("WATER_BREATHING", 3600, false, 0, false, new String[0]),
    WEAKNESS("WEAKNESS", 1800, false, 0, false, new String[0]);

    private final String name;
    private final int duration;
    private final boolean extended;
    private final int amplifier;
    private final boolean upgraded;
    private final String[] aliases;
}