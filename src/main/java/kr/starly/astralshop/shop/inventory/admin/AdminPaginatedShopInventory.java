package kr.starly.astralshop.shop.inventory.admin;

import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopPage;
import kr.starly.astralshop.shop.ShopPageImpl;
import kr.starly.astralshop.shop.controlbar.impl.DynamicControlBar;
import kr.starly.astralshop.shop.inventory.BaseShopPaginatedInventory;
import kr.starly.astralshop.shop.inventory.DynamicPaginationHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public abstract class AdminPaginatedShopInventory extends BaseShopPaginatedInventory {

    public AdminPaginatedShopInventory(Shop shop, String title, int rows, boolean cancel) {
        super(shop, title, rows, cancel);
    }

    @Override
    protected void displayPageItems(Inventory inventory, ShopPage currentShopPage, Player player) {
        currentShopPage.getItems().forEach((slot, shopItem) -> {
            ItemStack itemStack = shopItem.getItemStack();
            inventory.setItem(slot, itemStack);
        });

        int currentPage = paginationManager.getCurrentPage();
        int totalPages = paginationManager.getTotalPages();
        DynamicPaginationHelper paginationHelper = new DynamicPaginationHelper(currentPage, totalPages);
        DynamicControlBar dynamicControlBar = new DynamicControlBar(paginationHelper);
        dynamicControlBar.applyToInventory(inventory, player);
    }

    @Override
    protected void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int clickedSlot = event.getRawSlot();
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory != null && clickedInventory.equals(inventory)) {
            DynamicPaginationHelper paginationHelper = new DynamicPaginationHelper(paginationManager.getCurrentPage(), paginationManager.getTotalPages());
            if (clickedSlot >= inventory.getSize() - 9) {
                event.setCancelled(true);

                if (clickedSlot == inventory.getSize() - 9) {
                    handleControlBarInteraction(clickedSlot, player);
                } else if (clickedSlot >= inventory.getSize() - 8 && clickedSlot < inventory.getSize() - 1) {
                    handlePageNumberInteraction(clickedSlot, player, paginationHelper);
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
            if (paginationManager.isValidPage(controlSlot)) {
                paginationManager.setCurrentPage(controlSlot);
            }
        }

        updateInventory(player);
    }

    private void handlePageNumberInteraction(int clickedSlot, Player player, DynamicPaginationHelper paginationHelper) {
        int baseSlot = inventory.getSize() - 9;
        int pageNumberSlot = clickedSlot - baseSlot - 1;
        int targetPage = paginationHelper.getStartPage() + pageNumberSlot;

        boolean isCreatePageButton = pageNumberSlot >= 6 && targetPage >= paginationHelper.getTotalPages() + 1;

        if (isCreatePageButton || !paginationHelper.isValidPage(targetPage)) {
            handleCreatePageInteraction(player, paginationHelper);
            return;
        }

        paginationManager.setCurrentPage(targetPage);
        updateInventory(player);
    }

    private void handleCreatePageInteraction(Player player, DynamicPaginationHelper paginationHelper) {
        shop.getShopPages().add(new ShopPageImpl(paginationHelper.getTotalPages() + 1, shop.getGuiTitle(), shop.getRows(), new HashMap<>()));

        paginationManager.setCurrentPage(paginationHelper.getTotalPages() + 1);
        updateInventory(player);
    }

    protected abstract void handleItemInteraction(InventoryClickEvent event);
}