package xyz.starly.astralshop.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.api.shop.ShopPage;

import java.util.List;

@AllArgsConstructor
@Getter
public class ShopImpl implements Shop {

    @Setter private String guiTitle;
    @Setter private String npc;
    private final List<ShopPage> shopPages;
}