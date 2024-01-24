package xyz.starly.astralshop.shop.controlbar.impl;

import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.starly.astralshop.shop.controlbar.ControlBar;
import xyz.starly.astralshop.shop.inventory.DynamicPaginationHelper;

@AllArgsConstructor
public class DynamicControlBar implements ControlBar {

    private final DynamicPaginationHelper paginationHelper;

    @Override
    public void applyToInventory(Inventory inventory, Player player) {
        int rows = inventory.getSize() / 9;
        int controlBarSlot = (rows - 1) * 9;

        setupControlItems(inventory, controlBarSlot);
        setupPageItems(inventory, controlBarSlot);
    }

    private void setupControlItems(Inventory inventory, int controlBarSlot) {
        boolean hasPrevPage = paginationHelper.getCurrentPage() > 1;
        boolean hasNextPage = paginationHelper.getCurrentPage() < 64 && paginationHelper.getCurrentPage() < paginationHelper.getTotalPages();

        inventory.setItem(controlBarSlot, createControlItem(Material.RED_STAINED_GLASS_PANE, "Prev Page", hasPrevPage));
        inventory.setItem(controlBarSlot + 8, createControlItem(Material.BLUE_STAINED_GLASS_PANE, "Next Page", hasNextPage));
    }

    private void setupPageItems(Inventory inventory, int controlBarSlot) {
        int startPage = paginationHelper.getStartPage();
        int endPage = paginationHelper.getEndPage();

        for (int i = 0; i < 7; i++) {
            int slot = controlBarSlot + i + 1;
            int pageNumber = startPage + i;
            ItemStack item = pageNumber <= endPage ? createPageItem(pageNumber) : createCreatePageItem();
            inventory.setItem(slot, item);
        }
    }

    private ItemStack createControlItem(Material material, String name, boolean active) {
        Material itemMaterial = active ? material : Material.BARRIER;
        return createItemStack(itemMaterial, name, 1, false);
    }

    private ItemStack createPageItem(int pageNumber) {
        if (pageNumber > 64) {
            return null;
        }
        return createItemStack(Material.BOOK, "Page " + pageNumber, pageNumber, pageNumber == paginationHelper.getCurrentPage());
    }

    private ItemStack createCreatePageItem() {
        return createItemStack(Material.GREEN_STAINED_GLASS_PANE, "Create Page", 1, false);
    }

    private ItemStack createItemStack(Material material, String displayName, int amount, boolean isCurrentPage) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            if (isCurrentPage) {
                meta.addEnchant(Enchantment.LUCK, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}