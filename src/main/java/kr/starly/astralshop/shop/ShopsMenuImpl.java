package kr.starly.astralshop.shop;

import kr.starly.astralshop.api.shop.ShopMenuItem;
import kr.starly.astralshop.api.shop.ShopsMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class ShopsMenuImpl implements ShopsMenu {

    private boolean enabled;
    private Map<Integer, ShopMenuItem> items;
}