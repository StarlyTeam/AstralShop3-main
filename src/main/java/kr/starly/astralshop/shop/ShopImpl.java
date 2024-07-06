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
@Setter
public class ShopImpl implements Shop {

    private String name;
    private boolean enabled;
    private ShopAccessibility accessibility;

    private String guiTitle;
    private int rows;

    private TransactionHandler transactionHandler;
    private final List<ShopPage> shopPages;
}