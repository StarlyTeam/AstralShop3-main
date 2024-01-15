package xyz.starly.astralshop.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import xyz.starly.astralshop.api.shop.ShopItem;
import xyz.starly.astralshop.api.shop.ShopPage;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ShopPageImpl implements ShopPage {

    private int pageNum;
    private List<ShopItem> items;
    private String inventoryName;
    private int rows;
}