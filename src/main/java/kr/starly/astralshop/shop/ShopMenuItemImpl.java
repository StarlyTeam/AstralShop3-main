package kr.starly.astralshop.shop;

import kr.starly.astralshop.api.shop.ShopMenuItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public class ShopMenuItemImpl implements ShopMenuItem {

    private int slot;
    private ItemStack itemStack;
    private String shop;
}