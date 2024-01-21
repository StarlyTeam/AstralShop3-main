package xyz.starly.astralshop.shop.serialization;

import kr.starly.core.util.ItemSerializeUtil;
import org.bukkit.inventory.ItemStack;
import xyz.starly.astralshop.api.shop.ShopItem;

public class ShopItemSQLSerializer {
    public static String serialize(ShopItem shopItem) {
        ItemStack itemStack = shopItem.getItemStack();
        return ItemSerializeUtil.encode(itemStack);
    }


    public static ItemStack deserialize(String itemStack) {
        return ItemSerializeUtil.decode(itemStack);
    }
}