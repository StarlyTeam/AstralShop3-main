package kr.starly.astralshop.shop;

import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopItem;
import kr.starly.astralshop.api.shop.ShopTransaction;
import kr.starly.astralshop.api.shop.ShopTransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Date;

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
}