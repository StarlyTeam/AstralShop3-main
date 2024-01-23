package xyz.starly.astralshop.shop.inventory.global;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.api.shop.ShopPage;
import xyz.starly.astralshop.shop.controlbar.ShopControlBar;
import xyz.starly.astralshop.shop.controlbar.ShopControlBarItem;
import xyz.starly.astralshop.shop.inventory.BaseShopPaginatedInventory;

public class PaginatedShopInventory extends BaseShopPaginatedInventory {

    private ShopControlBar shopControlBar;

    public PaginatedShopInventory(Shop shop) {
        super(shop, shop.getShopPages().get(0).getGuiTitle(), shop.getShopPages().get(0).getRows(), true);
    }

    @Override
    protected void displayPageItems(Inventory inventory, ShopPage currentShopPage, Player player) {
        currentShopPage.getItems().forEach((slot, shopItem) -> {
            ItemStack itemStack = shopItem.getItemStack();
            inventory.setItem(slot, itemStack);
        });

        int currentPage = paginationManager.getCurrentPage();
        int totalPages = paginationManager.getTotalPages();
        this.shopControlBar = new ShopControlBar(currentPage, totalPages);
        shopControlBar.applyToInventory(inventory, player);
    }

    private void handleItemInteraction(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        // TODO 상점 | 구매/판매 기능
    }

    @Override
    protected void inventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);

        int clickedSlot = event.getRawSlot();
        Player player = (Player) event.getWhoClicked();

        if (clickedSlot >= inventory.getSize() - 9 && clickedSlot < inventory.getSize()) {
            ShopControlBarItem controlItem = shopControlBar.getItem(clickedSlot % 9);
            if (controlItem != null) {
                switch (controlItem.getAction()) {
                    case PREV_PAGE:
                        paginationManager.prevPage();
                        updateInventory(player);
                        break;
                    case NEXT_PAGE:
                        paginationManager.nextPage();
                        updateInventory(player);
                        break;
                }
            }
        } else {
            handleItemInteraction(event);
        }
    }

    @Override
    protected void inventoryClose(InventoryCloseEvent event) {
    }
}