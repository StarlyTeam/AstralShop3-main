package xyz.starly.astralshop.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import xyz.starly.astralshop.api.shop.ShopItem;
import xyz.starly.astralshop.api.shop.ShopPage;

import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
public class ShopPageImpl implements ShopPage {

    private int pageNum;
    private String guiTitle;
    private int rows;
    private Map<Integer, ShopItem> items;
}