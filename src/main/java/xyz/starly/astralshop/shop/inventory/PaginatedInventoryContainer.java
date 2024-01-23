package xyz.starly.astralshop.shop.inventory;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.api.shop.ShopPage;
import xyz.starly.astralshop.shop.controlbar.ControlBarItem;
import xyz.starly.astralshop.shop.controlbar.ShopControlBar;

public abstract class PaginatedInventoryContainer extends ShopInventory {

    protected final PaginationManager paginationManager;
    private final ShopControlBar controlBar;

    public PaginatedInventoryContainer(Shop shop, String title, int rows, boolean cancel) {
        super(shop, title, rows, cancel);
        this.paginationManager = new PaginationManager(shop.getShopPages());
        this.controlBar = new ShopControlBar();
    }

    @Override
    protected void initializeInventory(Inventory inventory, Player player) {
        ShopPage currentPage = paginationManager.getCurrentPageData();
        displayPageItems(inventory, currentPage, player);
    }

    public void updateInventory(Player player) {
        int currentPageIndex = paginationManager.getCurrentPage() - 1;
        ShopPage currentPage = shop.getShopPages().get(currentPageIndex);

        int inventorySize = currentPage.getRows() * 9;
        inventory = plugin.getServer().createInventory(this, inventorySize, currentPage.getGuiTitle());

        displayPageItems(inventory, currentPage, player);

        player.openInventory(inventory);
    }

    private void displayPageItems(Inventory inventory, ShopPage currentPage, Player player) {
        currentPage.getItems().forEach((slot, shopItem) -> {
            ItemStack itemStack = shopItem.getItemStack();
            inventory.setItem(slot, itemStack);
        });

        int currentPageNumber = paginationManager.getCurrentPage();
        int totalPages = paginationManager.getTotalPages();
        boolean isPaginated = currentPageNumber > 1;
        boolean isLastPage = paginationManager.isLastPage();

        controlBar.applyToInventory(inventory, currentPage.getRows(), isPaginated, isLastPage, currentPageNumber, totalPages, player);
    }

    @Override
    protected void inventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);

        int clickedSlot = event.getRawSlot();
        Player player = (Player) event.getWhoClicked();

        if (clickedSlot >= inventory.getSize() - 9 && clickedSlot < inventory.getSize()) {
            ControlBarItem controlItem = controlBar.getItem(clickedSlot % 9);
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

    protected abstract void handleItemInteraction(InventoryClickEvent event);
}