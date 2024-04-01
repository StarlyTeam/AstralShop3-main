package kr.starly.astralshop.shop;

import kr.starly.astralshop.api.shop.ShopItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class ShopItemImpl implements ShopItem {

    private final ItemStack itemStack;

    @Setter private double buyPrice;
    @Setter private double sellPrice;
    @Setter private int stock;
    @Setter private int remainStock;

    @Setter private boolean hideLore;
    @Setter private List<String> commands;
}