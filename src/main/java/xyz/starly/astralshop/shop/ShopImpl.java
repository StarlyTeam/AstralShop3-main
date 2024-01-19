package xyz.starly.astralshop.shop;

import lombok.Getter;
import lombok.Setter;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.api.shop.ShopPage;
import xyz.starly.astralshop.api.sound.SoundType;

import java.util.HashMap;
import java.util.Map;

public class ShopImpl implements Shop {

    @Getter @Setter private String name;
    @Getter @Setter private String guiTitle;
    @Getter @Setter private String npc;
    private final Map<SoundType, String> sound;
    private ShopPage shopPage;

    public ShopImpl(String name, String guiTitle, String npc, ShopPage shopPage) {
        this.name = name;
        this.guiTitle = guiTitle;
        this.npc = npc;
        this.sound = new HashMap<>();
        this.shopPage = shopPage;
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