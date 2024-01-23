package xyz.starly.astralshop.shop.controlbar;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.starly.astralshop.AstralShop;

import java.util.HashMap;
import java.util.Map;

public class ShopControlBar {

    private final Map<Integer, ControlBarItem> items;

    public ShopControlBar() {
        this.items = new HashMap<>();
        loadItems();
    }

    private void loadItems() {
        ConfigurationSection section = AstralShop.getInstance().getConfig().getConfigurationSection("shop_control_bar.items");
        if (section != null) {
            section.getKeys(false).forEach(key -> {
                try {
                    int slot = Integer.parseInt(key) - 1;
                    ControlBarItem item = new ControlBarItem(section.getConfigurationSection(key));
                    items.put(slot, item);
                } catch (NumberFormatException e) {
                    AstralShop.getInstance().getLogger().warning("Invalid slot number format in shop_control_bar: " + key);
                }
            });
        }
    }

    public void applyToInventory(Inventory inventory, int rows, boolean isPaginated, boolean isLastPage) {
        int baseSlot = (rows - 1) * 9;
        items.forEach((slot, controlBarItem) -> {
            int actualSlot = baseSlot + slot;
            inventory.setItem(actualSlot, controlBarItem.toItemStack(isPaginated, isLastPage));
        });
    }

    public ControlBarItem getItem(int slot) {
        return items.get(slot);
    }
}