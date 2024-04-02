package kr.starly.astralshop.shop.controlbar;

import kr.starly.astralshop.api.shop.ShopItem;
import kr.starly.astralshop.hook.PlaceholderAPIHook;
import kr.starly.astralshop.shop.serialization.yaml.ShopItemYamlSerializer;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Objects;

@Getter
public class ShopControlBarItem {

    private final ShopItem shopItem;
    private final ShopItem orElseShopItem;
    private final ShopControlBarAction action;
    private final boolean paginated;

    public ShopControlBarItem(ConfigurationSection section) {
        this.shopItem = ShopItemYamlSerializer.deserialize(section);
        this.action = ShopControlBarAction.fromString(section.getString("action"));
        this.paginated = section.getBoolean("paginated.enabled", false);

        ConfigurationSection orElseSection = section.getConfigurationSection("paginated.orElse");
        this.orElseShopItem = orElseSection != null ? ShopItemYamlSerializer.deserialize(orElseSection) : null;
    }

    @SuppressWarnings("deprecation")
    public ItemStack toItemStack(boolean isPaginated, boolean isLastPage, int currentPage, int totalPages, Player player) {
        ItemStack itemStack = paginated && orElseShopItem != null
                && ((isLastPage && action == ShopControlBarAction.NEXT_PAGE) || (!isPaginated && action == ShopControlBarAction.PREV_PAGE))
                ? orElseShopItem.getItemStack() : shopItem.getItemStack();

        if (itemStack != null && itemStack.getType() != Material.AIR && itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta instanceof SkullMeta skullMeta) {
                String skullOwner = replacePlaceholders(skullMeta.getOwner(), currentPage, totalPages, player);
                skullMeta.setOwner(skullOwner);
            }

            if (itemMeta != null) {
                if (itemMeta.hasDisplayName()) {
                    String displayName = replacePlaceholders(itemMeta.getDisplayName(), currentPage, totalPages, player);
                    itemMeta.setDisplayName(displayName);
                }

                if (itemMeta.hasLore()) {
                    List<String> lore = Objects.requireNonNull(itemMeta.getLore()).stream()
                            .map(line -> replacePlaceholders(line, currentPage, totalPages, player))
                            .toList();
                    itemMeta.setLore(lore);
                }
            }

            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    private String replacePlaceholders(String text, int currentPage, int totalPages, Player player) {
        int displayTotalPages = Math.min(totalPages, 64);

        return PlaceholderAPIHook.getHook().setPlaceholders(player, text)
                .replace("%current_page%", String.valueOf(currentPage))
                .replace("%total_page%", String.valueOf(displayTotalPages));
    }
}