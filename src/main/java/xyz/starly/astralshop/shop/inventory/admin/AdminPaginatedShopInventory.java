package xyz.starly.astralshop.shop.inventory.admin;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.api.shop.ShopPage;
import xyz.starly.astralshop.shop.controlbar.DynamicControlBar;
import xyz.starly.astralshop.shop.inventory.DynamicPaginationHelper;
import xyz.starly.astralshop.shop.inventory.BaseShopPaginatedInventory;

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
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        int clickedSlot = event.getRawSlot();
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory != null && clickedInventory.equals(inventory)) {
            DynamicPaginationHelper paginationHelper = new DynamicPaginationHelper(paginationManager.getCurrentPage(), paginationManager.getTotalPages());
            if (clickedSlot >= inventory.getSize() - 9) {
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

    private void handlePageNumberInteraction(int clickedSlot, Player player, DynamicPaginationHelper paginationHelper) {
        int baseSlot = inventory.getSize() - 9;
        int pageNumberSlot = clickedSlot - baseSlot - 1;
        int targetPage = paginationHelper.getStartPage() + pageNumberSlot;

        boolean isCreatePageButton = pageNumberSlot >= 6 && targetPage >= paginationHelper.getTotalPages() + 1;

        if (isCreatePageButton || !paginationHelper.isValidPage(targetPage)) {
            handleCreatePageInteraction(player);
            return;
        }

        paginationManager.setCurrentPage(targetPage);
        updateInventory(player);
    }

    private void handleCreatePageInteraction(Player player) {
        player.sendMessage("새 페이지 생성 기능은 현재 사용할 수 없습니다.");
    }

    protected abstract void handleItemInteraction(InventoryClickEvent event);
}