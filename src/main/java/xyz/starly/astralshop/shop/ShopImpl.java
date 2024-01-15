package xyz.starly.astralshop.shop;

import lombok.Getter;
import lombok.Setter;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.api.shop.SoundType;

import java.util.HashMap;
import java.util.Map;

public class ShopImpl implements Shop {

    @Setter @Getter private String name;
    @Getter @Setter private String npc;
    private final Map<SoundType, String> sound;

    public ShopImpl(String name, String npc) {
        this.name = name;
        this.npc = npc;
        this.sound = new HashMap<>();
    }

    @Override
    public String getSound(SoundType soundType) {
        return sound.get(soundType);
    }

    @Override
    public void setSound(SoundType soundType, String name) {
        sound.put(soundType, name);
    }
}