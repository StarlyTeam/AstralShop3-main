package kr.starly.astralshop.shop.inventory;

import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopPage;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@Getter
public abstract class BaseShopPaginatedInventory extends BaseShopInventory {

    protected final PaginationManager paginationManager;

    public BaseShopPaginatedInventory(Shop shop, String title, int rows, boolean cancel) {
        super(shop, title, rows, cancel);
        this.paginationManager = new PaginationManager(shop.getShopPages());
    }

    @Override
    protected void createInventory(Player player) {
        ShopPage currentPage = paginationManager.getCurrentPageData();
        this.title = currentPage.getGuiTitle();
        this.rows = currentPage.getRows();

        super.createInventory(player);
    }

    @Override
    public void updateInventory(Player player) {
        boolean listenEvent = isEventListening();
        try {
            setEventListening(false);

            createInventory(player);
            player.openInventory(inventory);
        } finally {
            setEventListening(listenEvent);
        }
    }
}