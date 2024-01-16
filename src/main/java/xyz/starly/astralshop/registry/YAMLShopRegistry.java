package xyz.starly.astralshop.registry;

import xyz.starly.astralshop.api.registry.ShopRegistry;
import xyz.starly.astralshop.api.shop.Shop;

import java.util.List;

public class YAMLShopRegistry implements ShopRegistry {

    @Override
    public boolean createShop(String s, int i) {
        return false;
    }

    @Override
    public boolean deleteShop(String s) {
        return false;
    }

    @Override
    public boolean updateShop(String s) {
        return false;
    }

    @Override
    public boolean updateShops() {
        return false;
    }

    @Override
    public Shop getShop(String s) {
        return null;
    }

    @Override
    public List<Shop> getShops() {
        return null;
    }
}