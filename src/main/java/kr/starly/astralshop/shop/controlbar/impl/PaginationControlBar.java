package kr.starly.astralshop.shop.controlbar.impl;

import kr.starly.astralshop.shop.controlbar.ControlBar;
import kr.starly.astralshop.shop.inventory.old.PaginationHelper;
import kr.starly.libs.inventory.item.builder.ItemBuilder;
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

import static kr.starly.astralshop.message.MessageContext.CONTROL_PREFIX;
import static kr.starly.astralshop.message.MessageContext.parseMessage;

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

        inventory.setItem(controlBarSlot, createControlItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), "<light_purple>이전 페이지", hasPrevPage));
        inventory.setItem(controlBarSlot + 8, createControlItem(new ItemStack(Material.BLUE_STAINED_GLASS_PANE), "<light_purple>다음 페이지", hasNextPage));
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
                "<aqua>" + pageNumber + " 페이지",
                pageNumber == paginationHelper.getCurrentPage(),
                modifiable ? new String[]{
                        CONTROL_PREFIX + "<gold>Shift+우클릭 <white>시, 페이지를 삭제합니다."
                } : new String[]{}
        );
    }

    private ItemStack createCreatePageItem() {
        return createItemStack(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), "<yellow>페이지 생성", false);
    }

    private ItemStack createEmptyPageItem() {
        return createItemStack(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), "<gray>빈 페이지", false);
    }

    private ItemStack createItemStack(ItemStack item, String displayName, boolean isCurrentPage, String... lore) {
        ItemBuilder itemBuilder = new ItemBuilder(item)
                .setDisplayName(parseMessage(displayName))
                .setLore(parseMessage(lore));
        if (isCurrentPage) {
            itemBuilder.addEnchantment(Enchantment.LUCK, 1, true);
            itemBuilder.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        return itemBuilder.get();
    }
}