package kr.starly.astralshop.shop;

import kr.starly.astralshop.api.shop.ShopItem;
import kr.starly.astralshop.api.shop.ShopPage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
public class ShopPageImpl implements ShopPage {

    private int pageNum;
    private Map<Integer, ShopItem> items;
}