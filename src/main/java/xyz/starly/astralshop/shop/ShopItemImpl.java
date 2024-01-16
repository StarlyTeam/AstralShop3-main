package xyz.starly.astralshop.shop;

import org.bukkit.inventory.ItemStack;
import xyz.starly.astralshop.api.shop.ShopItem;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShopItemImpl implements ShopItem {

    private ItemStack itemStack;
    private double buyPrice;
    private double sellPrice;
    private int stock;
    private int remainStock;
    private Map<UUID, Integer> limitBuyCount;
    private Map<UUID, Integer> limitSellCount;
    private List<String> commands;
}