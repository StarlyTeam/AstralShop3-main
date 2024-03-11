package kr.starly.astralshop.shop;

import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopPage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ShopImpl implements Shop {

    @Setter private String name;
    @Setter private String guiTitle;
    @Setter private String npc;
    @Setter private int rows;
    private final List<ShopPage> shopPages;
}