package xyz.starly.astralshop.shop.inventory.admin;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.api.shop.ShopPage;
import xyz.starly.astralshop.shop.controlbar.DynamicControlBar;
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
        DynamicControlBar dynamicControlBar = new DynamicControlBar(currentPage, totalPages);
        dynamicControlBar.applyToInventory(inventory, player);
    }

    @Override
    protected void inventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        int clickedSlot = event.getRawSlot();
        int inventorySize = inventory.getSize();

        if (clickedSlot >= inventorySize - 9) {
            handleControlBarInteraction(clickedSlot, player);
        } else {
            handleItemInteraction(event);
        }
    }

    protected abstract void handleItemInteraction(InventoryClickEvent event);
}