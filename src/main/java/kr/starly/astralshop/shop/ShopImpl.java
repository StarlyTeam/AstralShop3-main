package kr.starly.astralshop.shop;

import kr.starly.astralshop.api.addon.TransactionHandler;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopAccessibility;
import kr.starly.astralshop.api.shop.ShopPage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ShopImpl implements Shop {

    @Setter private String name;
    @Setter private boolean enabled;
    @Setter private ShopAccessibility accessibility;

    @Setter private String guiTitle;
    @Setter private String npc;

    @Setter private TransactionHandler transactionHandler;
    private final List<ShopPage> shopPages;
}