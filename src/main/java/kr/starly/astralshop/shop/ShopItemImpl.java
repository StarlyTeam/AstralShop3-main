package kr.starly.astralshop.shop;

import com.google.common.collect.ImmutableMap;
import kr.starly.astralshop.api.shop.ShopItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class ShopItemImpl implements ShopItem {

    private final ItemStack itemStack;

    @Setter
    private double buyPrice;
    @Setter
    private double sellPrice;
    @Setter
    private int stock;
    @Setter
    private int remainStock;

    @Setter
    private boolean marker;
    @Setter
    private List<String> commands;
    @Setter
    private Map<String, Object> attributes;

    public ImmutableMap<String, Object> getAttributes() {
        return ImmutableMap.copyOf(attributes);
    }
}