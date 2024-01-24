package xyz.starly.astralshop.shop.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.api.shop.ShopPage;

public abstract class BaseShopPaginatedInventory extends ShopInventory {

    protected final PaginationManager paginationManager;

    public BaseShopPaginatedInventory(Shop shop, String title, int rows, boolean cancel) {
        super(shop, title, rows, cancel);
        this.paginationManager = new PaginationManager(shop.getShopPages());
    }

    @Override
    protected void initializeInventory(Inventory inventory, Player player) {
        ShopPage currentPage = paginationManager.getCurrentPageData();
        displayPageItems(inventory, currentPage, player);
    }

    protected void updateInventory(Player player) {
        int currentPageIndex = paginationManager.getCurrentPage() - 1;
        ShopPage currentPage = shop.getShopPages().get(currentPageIndex);

        int inventorySize = currentPage.getRows() * 9;
        inventory = plugin.getServer().createInventory(this, inventorySize, currentPage.getGuiTitle());

        displayPageItems(inventory, currentPage, player);

        player.openInventory(inventory);
    }

    protected abstract void displayPageItems(Inventory inventory, ShopPage currentPage, Player player);
}