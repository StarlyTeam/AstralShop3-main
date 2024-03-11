package kr.starly.astralshop.shop.controlbar.impl;

import kr.starly.astralshop.AstralShop;
import kr.starly.astralshop.shop.controlbar.ControlBar;
import kr.starly.astralshop.shop.controlbar.ShopControlBarItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ShopMainControlBar implements ControlBar {

    private final Map<Integer, ShopControlBarItem> items;

    public ShopMainControlBar() {
        this.items = new HashMap<>();
        loadItems();
    }

    private void loadItems() {
        ConfigurationSection section = AstralShop.getInstance().getConfig().getConfigurationSection("shop_main_control_bar.items");
        if (section != null) {
            section.getKeys(false).forEach(key -> {
                try {
                    int slot = Integer.parseInt(key) - 1;
                    ShopControlBarItem item = new ShopControlBarItem(section.getConfigurationSection(key));
                    items.put(slot, item);
                } catch (NumberFormatException e) {
                    AstralShop.getInstance().getLogger().warning("Invalid slot number format in shop_main_control_bar: " + key);
                }
            });
        }
    }

    @Override
    public void applyToInventory(Inventory inventory, Player player) {
        ConfigurationSection controlBarSection = AstralShop.getInstance().getConfig().getConfigurationSection("shop_main_control_bar");
        if (controlBarSection != null && controlBarSection.getBoolean("enabled", false)) {
            int baseSlot = inventory.getSize() - 9;
            items.forEach((slot, item) -> {
                int actualSlot = baseSlot + slot;
                inventory.setItem(actualSlot, item.toItemStack(false, false, 0, 0, player));
            });
        }
    }

    public Optional<ShopControlBarItem> getItem(int slot) {
        return Optional.ofNullable(items.get(slot));
    }
}