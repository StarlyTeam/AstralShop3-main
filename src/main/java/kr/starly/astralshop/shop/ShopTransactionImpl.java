package kr.starly.astralshop.shop;

import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopItem;
import kr.starly.astralshop.api.shop.ShopTransaction;
import kr.starly.astralshop.api.shop.ShopTransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
public class ShopTransactionImpl implements ShopTransaction {

    private final Player player;
    private final ShopTransactionType type;
    private final Date date;

    private final Shop shop;
    private final int page;
    private final int slot;

    private final ShopItem item;
    private final int amount;

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put("player", player.getUniqueId().toString());
        result.put("type", type.name());
        result.put("date", date.getTime());
        result.put("shop", shop.getName());
        result.put("page", page);
        result.put("slot", slot);
        result.put("amount", amount);

        return result;
    }
}