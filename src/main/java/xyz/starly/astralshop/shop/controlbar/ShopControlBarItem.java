package xyz.starly.astralshop.shop.controlbar;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.starly.astralshop.AstralShop;
import xyz.starly.astralshop.api.shop.ShopItem;
import xyz.starly.astralshop.shop.serialization.yaml.ShopItemYamlSerializer;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        ItemStack itemStack = paginated && ((isLastPage && action == ShopControlBarAction.NEXT_PAGE) || (!isPaginated && action == ShopControlBarAction.PREV_PAGE)) && orElseShopItem != null
                ? orElseShopItem.getItemStack()
                : shopItem.getItemStack();

        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) itemMeta;
                String skullOwner = replacePlaceholders(Objects.requireNonNull(skullMeta.getOwner()), currentPage, totalPages, player);
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
                            .collect(Collectors.toList());
                    itemMeta.setLore(lore);
                }
            }

            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    private String replacePlaceholders(String text, int currentPage, int totalPages, Player player) {
        return text.replace("%current_page%", String.valueOf(currentPage))
                .replace("%total_page%", String.valueOf(totalPages))
                .replace("%player_name%", player.getName())
                .replace("%player_displayname%", player.getDisplayName())
                .replace("%player_balance%", String.valueOf(AstralShop.getInstance().getEconomy().getBalance(player)));
    }
}