package kr.starly.astralshop.shop.controlbar.impl;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.shop.controlbar.ControlBar;
import kr.starly.astralshop.shop.controlbar.ShopControlBarItem;
import kr.starly.astralshop.shop.inventory.PaginationHelper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ShopControlBar implements ControlBar { // TODO

    private final PaginationHelper paginationManager;

    private final Map<Integer, ShopControlBarItem> items = new HashMap<>();

    public ShopControlBar(PaginationHelper paginationManager) {
        this.paginationManager = paginationManager;
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
        ConfigurationSection controlBarSection = AstralShop.getInstance().getConfig().getConfigurationSection("shop_control_bar");
        if (controlBarSection != null && controlBarSection.getBoolean("enabled", false)) {
            int rows = inventory.getSize() / 9;
            int baseSlot = (rows - 1) * 9;
            items.forEach((slot, item) -> {
                int actualSlot = baseSlot + slot;
                boolean isPaginated = paginationManager.getCurrentPage() > 1;
                boolean isLastPage = paginationManager.getCurrentPage() >= paginationManager.getTotalPages();
                inventory.setItem(actualSlot, item.toItemStack(isPaginated, isLastPage, paginationManager.getCurrentPage(), paginationManager.getTotalPages(), player));
            });
        }
    }

    public Optional<ShopControlBarItem> getItem(int slot) {
        return Optional.ofNullable(items.get(slot));
    }
}