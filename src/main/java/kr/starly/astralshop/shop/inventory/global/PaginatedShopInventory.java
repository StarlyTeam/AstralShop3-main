package kr.starly.astralshop.shop.inventory.global;

import kr.starly.astralshop.api.shop.*;
import kr.starly.astralshop.shop.inventory.BaseShopPaginatedInventory;
import kr.starly.astralshop.shop.inventory.PaginationHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public abstract class PaginatedShopInventory extends BaseShopPaginatedInventory {

    public PaginatedShopInventory(Shop shop) {
        super(shop, shop.getGuiTitle(), shop.getShopPages().get(0).getRows(), true);
    }

    @Override
    protected void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ClickType click = event.getClick();
        int clickedSlot = event.getSlot();
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory != null) {
            PaginationHelper paginationHelper = new PaginationHelper(paginationManager);
            if (clickedSlot >= inventory.getSize() - 9) {
                event.setCancelled(true);

                if (clickedSlot == inventory.getSize() - 9) {
                    handleControlBarInteraction(clickedSlot, player);
                } else if (clickedSlot >= inventory.getSize() - 8 && clickedSlot < inventory.getSize() - 1) {
                    handlePageNumberInteraction(clickedSlot, click, player, paginationHelper);
                } else if (clickedSlot == inventory.getSize() - 1) {
                    handleControlBarInteraction(clickedSlot, player);
                }
            } else {
                handleItemInteraction(event);
            }
        }
    }

    private void handleControlBarInteraction(int clickedSlot, Player player) {
        int controlSlot = clickedSlot % 9;

        if (controlSlot == 0 && paginationManager.hasPrevPage()) {
            paginationManager.prevPage();
        } else if (controlSlot == 8 && paginationManager.hasNextPage()) {
            paginationManager.nextPage();
        } else if (controlSlot > 0 && controlSlot < 8) {
            if (paginationManager.getCurrentPage() != controlSlot) return;

            if (paginationManager.isValidPage(controlSlot)) {
                paginationManager.setCurrentPage(controlSlot);
            }
        }

        updateInventory(player);
    }

    private void handlePageNumberInteraction(int clickedSlot, ClickType click, Player player, PaginationHelper paginationHelper) {
        int baseSlot = inventory.getSize() - 9;
        int pageNumberSlot = clickedSlot - baseSlot - 1;
        int targetPage = paginationHelper.getStartPage() + pageNumberSlot;

        boolean isCreatePageButton = pageNumberSlot >= 6 && targetPage >= paginationHelper.getTotalPages() + 1;
        if (isCreatePageButton || !paginationHelper.isValidPage(targetPage)) return;

        if (click == ClickType.LEFT) {
            paginationManager.setCurrentPage(targetPage);
        }

        updateInventory(player);
    }

    @Override
    protected void inventoryClose(InventoryCloseEvent event) {}

    protected abstract void handleItemInteraction(InventoryClickEvent event);
}