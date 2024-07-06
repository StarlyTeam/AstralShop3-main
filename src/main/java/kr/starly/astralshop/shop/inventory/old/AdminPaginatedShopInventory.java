package kr.starly.astralshop.shop.inventory.old;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopPage;
import kr.starly.astralshop.shop.ShopPageImpl;
import kr.starly.astralshop.shop.controlbar.ControlBar;
import kr.starly.astralshop.shop.controlbar.impl.PaginationControlBar;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public abstract class AdminPaginatedShopInventory extends BaseShopPaginatedInventory {

    public AdminPaginatedShopInventory(Shop shop, String title, int rows, boolean cancel) {
        super(shop, title, rows, cancel);
    }

    @Override
    protected void initializeInventory(Inventory inventory, Player player) {
        int size = rows * 9;
        paginationManager.getCurrentPageData().getItems().forEach((slot, shopItem) -> {
            if (slot >= size) return;

            ItemStack itemStack = shopItem.getItemStack();
            inventory.setItem(slot, itemStack);
        });

        PaginationHelper paginationHelper = new PaginationHelper(paginationManager);
        ControlBar controlBar = new PaginationControlBar(paginationHelper, true);
        controlBar.applyToInventory(inventory, player);
    }

    @Override
    protected void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int clickedSlot = event.getSlot();
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory != null) {
            if (clickedSlot >= inventory.getSize() - 9) {
                event.setCancelled(true);

                if (clickedSlot == inventory.getSize() - 9) {
                    handleControlBarInteraction(clickedSlot, player);
                } else if (clickedSlot > inventory.getSize() - 9 && clickedSlot < inventory.getSize() - 1) {
                    PaginationHelper paginationHelper = new PaginationHelper(paginationManager);
                    handlePageNumberInteraction(clickedSlot, event.getClick(), player, paginationHelper);
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

        boolean isCreatePageButton = pageNumberSlot >= 6 && targetPage > paginationManager.getTotalPages();
        if (isCreatePageButton || !paginationHelper.isValidPage(targetPage)) {
            if (click != ClickType.LEFT) return;

            handleCreatePageInteraction(player, paginationHelper);
            return;
        }

        if (click == ClickType.LEFT) {
            paginationManager.setCurrentPage(targetPage);
        } else if (click == ClickType.SHIFT_RIGHT) {
            List<ShopPage> pages = paginationManager.getPages();
            if (pages.size() == 1) return;

            pages.remove(targetPage - 1);
            for (int i = 0; i < pages.size(); i++) pages.get(i).setPageNum(i + 1);

            int currentPage = paginationManager.getCurrentPage();
            if (targetPage <= currentPage) paginationManager.setCurrentPage(currentPage - 1);

            AstralShop.getInstance().getShopRepository().saveShop(shop);
        }

        updateInventory(player);
    }

    private void handleCreatePageInteraction(Player player, PaginationHelper paginationHelper) {
        shop.getShopPages().add(new ShopPageImpl(paginationHelper.getTotalPages() + 1, new HashMap<>()));

        paginationManager.setCurrentPage(paginationHelper.getTotalPages());
        updateInventory(player);
    }

    protected abstract void handleItemInteraction(InventoryClickEvent event);
}