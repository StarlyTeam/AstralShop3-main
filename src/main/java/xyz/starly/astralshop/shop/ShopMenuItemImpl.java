package xyz.starly.astralshop.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import xyz.starly.astralshop.api.shop.ShopMenuItem;

@AllArgsConstructor
@Getter
public class ShopMenuItemImpl implements ShopMenuItem {

    private int slot;
    private ItemStack itemStack;
    private String shop;
}