package xyz.starly.astralshop.shop.controlbar;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import xyz.starly.astralshop.AstralShop;

import java.util.HashMap;
import java.util.Map;

public class ShopControlBar implements ControlBar {

    private final int currentPage;
    private final int totalPages;
    private final Map<Integer, ShopControlBarItem> items;

    public ShopControlBar(int currentPage, int totalPages) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.items = new HashMap<>();
        loadItems();
    }

    private void loadItems() {
        ConfigurationSection section = AstralShop.getInstance().getConfig().getConfigurationSection("shop_control_bar.items");
        if (section != null) {
            section.getKeys(false).forEach(key -> {
                try {
                    int slot = Integer.parseInt(key) - 1;
                    ShopControlBarItem item = new ShopControlBarItem(section.getConfigurationSection(key));
                    items.put(slot, item);
                } catch (NumberFormatException e) {
                    AstralShop.getInstance().getLogger().warning("Invalid slot number format in shop_control_bar: " + key);
                }
            });
        }
    }

    @Override
    public void applyToInventory(Inventory inventory, Player player) {
        int rows = inventory.getSize() / 9;
        int baseSlot = (rows - 1) * 9;
        ConfigurationSection section = AstralShop.getInstance().getConfig().getConfigurationSection("shop_control_bar.items");
        if (section != null) {
            section.getKeys(false).forEach(key -> {
                try {
                    int slot = Integer.parseInt(key) - 1;
                    int actualSlot = baseSlot + slot;

                    ShopControlBarItem controlBarItem = new ShopControlBarItem(section.getConfigurationSection(key));
                    boolean isPaginated = currentPage > 1;
                    boolean isLastPage = currentPage >= totalPages;

                    inventory.setItem(actualSlot, controlBarItem.toItemStack(isPaginated, isLastPage, currentPage, totalPages, player));
                } catch (NumberFormatException e) {
                    AstralShop.getInstance().getLogger().warning("Invalid slot number format in shop_control_bar: " + key);
                }
            });
        }
    }

    public ShopControlBarItem getItem(int slot) {
        return items.get(slot);
    }
}