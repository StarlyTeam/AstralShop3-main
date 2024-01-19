package xyz.starly.astralshop.shop;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import xyz.starly.astralshop.api.shop.ShopItem;

import java.util.*;

public class ShopItemImpl implements ShopItem {

    private ItemStack itemStack;
    @Getter @Setter private double buyPrice = -1;
    @Getter @Setter private double sellPrice = -1;
    @Getter @Setter private int stock = -1;
    @Getter @Setter private int remainStock = -1;
    @Getter private Map<UUID, Integer> limitBuyCount;
    @Getter private Map<UUID, Integer> limitSellCount;
    @Getter private List<String> commands;

    public ShopItemImpl(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.limitBuyCount = new HashMap<>();
        this.limitSellCount = new HashMap<>();
        this.commands = new ArrayList<>();
    }


    @Override
    public void setLimitBuyCount(UUID uuid, int i) {
    }

    @Override
    public void setLimitSellCount(UUID uuid, int i) {

    }
}