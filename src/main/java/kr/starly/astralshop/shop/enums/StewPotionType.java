package kr.starly.astralshop.shop.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@AllArgsConstructor
@Getter
public enum StewPotionType {

    NIGHT_VISION(PotionEffectType.NIGHT_VISION, 100),
    JUMP_BOOST(PotionEffectType.JUMP, 120),
    WEAKNESS(PotionEffectType.WEAKNESS, 180),
    BLINDNESS(PotionEffectType.BLINDNESS, 160),
    POISON(PotionEffectType.POISON, 240),
    SATURATION(PotionEffectType.SATURATION, 7),
    FIRE_RESISTANCE(PotionEffectType.FIRE_RESISTANCE, 80),
    REGENERATION(PotionEffectType.REGENERATION, 160),
    WITHER(PotionEffectType.WITHER, 160);

    private final PotionEffectType type;
    private final int duration;

    public PotionEffect getEffect() {
        return new PotionEffect(this.type, this.duration, 1);
    }
}
