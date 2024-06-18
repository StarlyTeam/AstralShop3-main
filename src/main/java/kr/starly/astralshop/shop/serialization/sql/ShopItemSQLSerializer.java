package kr.starly.astralshop.shop.serialization.sql;

import kr.starly.astralshop.api.shop.ShopItem;
import kr.starly.libs.util.EncodeUtils;
import org.bukkit.inventory.ItemStack;

public class ShopItemSQLSerializer {

    public static String serialize(ShopItem shopItem) {
        ItemStack itemStack = shopItem.getItemStack();
        return EncodeUtils.serialize(itemStack);
    }

    public static ItemStack deserialize(String itemStack) {
        return EncodeUtils.deserialize(itemStack, ItemStack.class);
    }
}