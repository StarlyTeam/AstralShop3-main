package kr.starly.astralshop.shop.controlbar.impl;

import kr.starly.astralshop.shop.controlbar.ControlBar;
import kr.starly.astralshop.shop.inventory.PaginationHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class PaginationControlBar implements ControlBar {

    private final PaginationHelper paginationHelper;
    private final boolean modifiable;

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
        } catch (NoSuchFieldError ignored) {
            item1 = new ItemStack(Material.valueOf("RED_STAINED_GLASS_PANE"));
            item2 = new ItemStack(Material.valueOf("BLUE_STAINED_GLASS_PANE"));
        }

        inventory.setItem(controlBarSlot, createControlItem(item1, "&d이전 페이지", hasPrevPage));
        inventory.setItem(controlBarSlot + 8, createControlItem(item2, "&d다음 페이지", hasNextPage));
    }

    private void setupPageItems(Inventory inventory, int controlBarSlot) {
        int startPage = paginationHelper.getStartPage();
        int endPage = paginationHelper.getEndPage();

        for (int i = 0; i < 7; i++) {
            int slot = controlBarSlot + i + 1;
            int pageNumber = startPage + i;
            ItemStack item = pageNumber <= endPage ? createPageItem(pageNumber) : (modifiable ? createCreatePageItem() : createEmptyPageItem());
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

        return createItemStack(
                new ItemStack(Material.BOOK, pageNumber),
                pageNumber + " 페이지",
                pageNumber == paginationHelper.getCurrentPage(),
                modifiable ? new String[]{
                        "&e&l| &6Shift+좌클릭 &f시, 페이지 속성창이 열립니다.",
                        "&e&l| &6Shift+우클릭 &f시, 페이지를 삭제합니다."
                } : new String[]{}
        );
    }

    private ItemStack createCreatePageItem() {
        ItemStack item;
        try {
            item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
        } catch (NoSuchFieldError ignored) {
            item = new ItemStack(Material.valueOf("GREEN_STAINED_GLASS_PANE"));
        }

        return createItemStack(item, "&e페이지 생성", false);
    }

    private ItemStack createEmptyPageItem() {
        ItemStack item;
        try {
            item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
        } catch (NoSuchFieldError ignored) {
            item = new ItemStack(Material.valueOf("GRAY_STAINED_GLASS_PANE"));
        }

        return createItemStack(item, "&7빈 페이지", false);
    }

    private ItemStack createItemStack(ItemStack item, String displayName, boolean isCurrentPage, String... lore) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&r" + displayName));

            if (isCurrentPage) {
                meta.addEnchant(Enchantment.LUCK, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } if (lore != null && lore.length > 0) {
                List<String> newLore = new ArrayList<>();
                for (String loreLine : lore) {
                    newLore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
                }

                meta.setLore(newLore);
            }

            item.setItemMeta(meta);
        }
        return item;
    }
}