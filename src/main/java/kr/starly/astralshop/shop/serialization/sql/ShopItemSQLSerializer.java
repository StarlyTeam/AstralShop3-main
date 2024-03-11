package kr.starly.astralshop.shop.serialization.sql;

import kr.starly.astralshop.api.shop.ShopItem;
import kr.starly.core.util.ItemSerializeUtil;
import org.bukkit.inventory.ItemStack;

public class ShopItemSQLSerializer {

    public static String serialize(ShopItem shopItem) {
        ItemStack itemStack = shopItem.getItemStack();
        return ItemSerializeUtil.encode(itemStack);
    }

    public static ItemStack deserialize(String itemStack) {
        return ItemSerializeUtil.decode(itemStack);
    }
}