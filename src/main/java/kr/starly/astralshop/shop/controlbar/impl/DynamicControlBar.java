package kr.starly.astralshop.shop.controlbar.impl;

import kr.starly.astralshop.shop.controlbar.ControlBar;
import kr.starly.astralshop.shop.inventory.DynamicPaginationHelper;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

        ItemStack item1, item2;
        try {
            item1 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
            item2 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11);
        } catch (IllegalArgumentException ignored) {
            item1 = new ItemStack(Material.valueOf("RED_STAINED_GLASS_PANE"));
            item2 = new ItemStack(Material.valueOf("BLUE_STAINED_GLASS_PANE"));
        }

        inventory.setItem(controlBarSlot, createControlItem(item1, "Prev Page", hasPrevPage));
        inventory.setItem(controlBarSlot + 8, createControlItem(item2, "Next Page", hasNextPage));
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

    private ItemStack createControlItem(ItemStack itemStack, String name, boolean active) {
        return createItemStack(active ? itemStack : new ItemStack(Material.BARRIER), name, false);
    }

    private ItemStack createPageItem(int pageNumber) {
        if (pageNumber > 64) {
            return null;
        }
        return createItemStack(new ItemStack(Material.BOOK, pageNumber), "Page " + pageNumber, pageNumber == paginationHelper.getCurrentPage());
    }

    private ItemStack createCreatePageItem() {
        ItemStack item;
        try {
            item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
        } catch (IllegalArgumentException ignored) {
            item = new ItemStack(Material.valueOf("GREEN_STAINED_GLASS_PANE"));
        }

        return createItemStack(item, "Create Page", false);
    }

    private ItemStack createItemStack(ItemStack item, String displayName, boolean isCurrentPage) {
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