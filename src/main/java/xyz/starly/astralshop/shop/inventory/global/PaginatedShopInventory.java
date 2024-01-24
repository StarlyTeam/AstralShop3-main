package xyz.starly.astralshop.shop.inventory.global;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.api.shop.ShopItem;
import xyz.starly.astralshop.api.shop.ShopPage;
import xyz.starly.astralshop.shop.controlbar.impl.ShopControlBar;
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

        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.getType() == Material.AIR) {
            return;
        }

        int currentPage = paginationManager.getCurrentPage() - 1;
        int clickedSlot = event.getSlot();

        ShopItem shopItem = shop.getShopPages().get(currentPage).getItems().get(clickedSlot);
        double buyPrice = shopItem.getBuyPrice();
        double sellPrice = shopItem.getSellPrice();
        player.sendMessage("구매 가격: " + buyPrice);
        player.sendMessage("판매 가격: " + sellPrice);

        // TODO 구매/판매 시스템
    }

    @Override
    protected void inventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);

        int clickedSlot = event.getRawSlot();
        Player player = (Player) event.getWhoClicked();

        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.getType() == Material.AIR) {
            return;
        }

        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory != null && clickedInventory.equals(inventory)) {
            if (clickedSlot >= inventory.getSize() - 9 && clickedSlot < inventory.getSize()) {
                shopControlBar.getItem(clickedSlot % 9).ifPresent(controlBarItem -> {
                    switch (controlBarItem.getAction()) {
                        case PREV_PAGE:
                            paginationManager.prevPage();
                            updateInventory(player);
                            break;
                        case NEXT_PAGE:
                            paginationManager.nextPage();
                            updateInventory(player);
                            break;
                        case BACK:
                            new ShopMainInventory().open(player);
                            break;
                        case CLOSE:
                            player.closeInventory();;
                            break;
                    }
                });
            } else {
                handleItemInteraction(event);
            }
        }
    }

    @Override
    protected void inventoryClose(InventoryCloseEvent event) {
    }
}