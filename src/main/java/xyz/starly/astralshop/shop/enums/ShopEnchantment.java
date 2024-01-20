package xyz.starly.astralshop.shop.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ShopEnchantment {

    ARROW_DAMAGE(false, new String[]{"POWER", "ARROW_DAMAGE", "ARROW_POWER", "AD"}),
    ARROW_FIRE(false, new String[]{"FLAME", "FLAME_ARROW", "FIRE_ARROW", "AF"}),
    ARROW_INFINITE(false, new String[]{"INFINITY", "INF_ARROWS", "INFINITE_ARROWS", "INFINITE", "UNLIMITED", "UNLIMITED_ARROWS", "AI"}),
    ARROW_KNOCKBACK(false, new String[]{"PUNCH", "ARROW_KNOCKBACK", "ARROWKB", "ARROW_PUNCH", "AK"}),
    BINDING_CURSE(true, new String[]{"BINDING_CURSE", "BIND_CURSE", "BINDING", "BIND"}),
    CHANNELING(true, new String[]{"CHANNELLING", "CHANELLING", "CHANELING", "CHANNEL"}),
    DAMAGE_ALL(false, new String[]{"SHARPNESS", "ALL_DAMAGE", "ALL_DMG", "SHARP", "DAL"}),
    DAMAGE_ARTHROPODS(false, new String[]{"BANE_OF_ARTHROPODS", "ARDMG", "BANE_OF_ARTHROPOD", "ARTHROPOD", "DAR"}),
    DAMAGE_UNDEAD(false, new String[]{"SMITE", "UNDEAD_DAMAGE", "DU"}),
    DEPTH_STRIDER(true, new String[]{"DEPTH", "STRIDER"}),
    DIG_SPEED(false, new String[]{"EFFICIENCY", "MINE_SPEED", "CUT_SPEED", "DS", "EFF"}),
    DURABILITY(false, new String[]{"UNBREAKING", "DURA"}),
    FIRE_ASPECT(true, new String[]{"FIRE", "MELEE_FIRE", "MELEE_FLAME", "FA"}),
    FROST_WALKER(true, new String[]{"FROST", "WALKER"}),
    IMPALING(true, new String[]{"IMPALE", "OCEAN_DAMAGE", "OCEAN_DMG"}),
    SOUL_SPEED(true, new String[]{"SPEED_SOUL", "SOUL_RUNNER"}),
    SWIFT_SNEAK(true, new String[]{"SPEED_SNEAK"}),
    KNOCKBACK(true, new String[]{"K_BACK", "KB"}),
    LOOT_BONUS_BLOCKS(false, new String[]{"FORTUNE", "BLOCKS_LOOT_BONUS", "FORT", "LBB"}),
    LOOT_BONUS_MOBS(false, new String[]{"LOOTING", "MOB_LOOT", "MOBS_LOOT_BONUS", "LBM"}),
    LOYALTY(true, new String[]{"LOYAL", "RETURN"}),
    LUCK(false, new String[]{"LUCK_OF_THE_SEA", "LUCK_OF_SEA", "LUCK_OF_SEAS", "ROD_LUCK"}),
    LURE(true, new String[]{"ROD_LURE"}),
    MENDING(true, new String[0]),
    MULTISHOT(true, new String[]{"TRIPLE_SHOT"}),
    OXYGEN(false, new String[]{"RESPIRATION", "BREATH", "BREATHING", "O2", "O"}),
    PIERCING(true, new String[0]),
    PROTECTION_ENVIRONMENTAL(false, new String[]{"PROTECTION", "PROTECT", "PROT"}),
    PROTECTION_EXPLOSIONS(false, new String[]{"BLAST_PROTECTION", "BLAST_PROTECT", "EXPLOSIONS_PROTECTION", "EXPLOSION_PROTECTION", "BLAST_PROTECTION", "PE"}),
    PROTECTION_FALL(false, new String[]{"FEATHER_FALLING", "FALL_PROT", "FEATHER_FALL", "FALL_PROTECTION", "FEATHER_FALLING", "PFA"}),
    PROTECTION_FIRE(false, new String[]{"FIRE_PROTECTION", "FIRE_PROT", "FIRE_PROTECT", "FIRE_PROTECTION", "FLAME_PROTECTION", "FLAME_PROTECT", "FLAME_PROT", "PF"}),
    PROTECTION_PROJECTILE(false, new String[]{"PROJECTILE_PROTECTION", "PROJECTILE_PROTECTION", "PROJ_PROT", "PP"}),
    QUICK_CHARGE(true, new String[]{"QUICKCHARGE", "QUICK_DRAW", "FAST_CHARGE", "FAST_DRAW"}),
    RIPTIDE(true, new String[]{"RIP", "TIDE", "LAUNCH"}),
    SILK_TOUCH(true, new String[]{"SOFT_TOUCH", "ST"}),
    SWEEPING_EDGE(false, new String[]{"SWEEPING", "SWEEP_EDGE"}),
    THORNS(true, new String[]{"HIGHCRIT", "THORN", "HIGHERCRIT", "T"}),
    VANISHING_CURSE(true, new String[]{"VANISHING_CURSE", "VANISH_CURSE", "VANISHING", "VANISH"}),
    WATER_WORKER(false, new String[]{"AQUA_AFFINITY", "WATER_WORKER", "AQUA_AFFINITY", "WATER_MINE", "WW"});

    private final boolean self;
    private final String[] aliases;
}
