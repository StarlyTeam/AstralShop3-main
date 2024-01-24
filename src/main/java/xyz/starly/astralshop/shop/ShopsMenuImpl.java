package xyz.starly.astralshop.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import xyz.starly.astralshop.api.shop.ShopMenuItem;
import xyz.starly.astralshop.api.shop.ShopsMenu;

import java.util.Map;

@AllArgsConstructor
@Getter
public class ShopsMenuImpl implements ShopsMenu {

    private boolean enabled;
    private Map<Integer, ShopMenuItem> items;
}