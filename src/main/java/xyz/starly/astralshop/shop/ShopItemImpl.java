package xyz.starly.astralshop.shop;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import xyz.starly.astralshop.api.shop.ShopItem;

import java.util.*;

public class ShopItemImpl implements ShopItem {

    @Getter private ItemStack itemStack;
    @Getter @Setter private double buyPrice;
    @Getter @Setter private double sellPrice;
    @Getter @Setter private int stock;
    @Getter @Setter private int remainStock;
    @Getter @Setter private List<String> commands;

    public ShopItemImpl(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.commands = new ArrayList<>();
    }
}