package kr.starly.astralshop.shop.inventory.old;

import kr.starly.astralshop.api.shop.Shop;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public abstract class BaseShopPaginatedInventory extends BaseShopInventory {

    protected PaginationManager paginationManager;

    public BaseShopPaginatedInventory(Shop shop, String title, int rows, boolean cancel) {
        super(shop, title, rows, cancel);
        this.paginationManager = new PaginationManager(shop.getShopPages());
    }

    @Override
    protected void createInventory(Player player) {
        this.title = shop.getGuiTitle();
        this.rows = shop.getRows();

        super.createInventory(player);
    }

    @Override
    public void updateData() {
        super.updateData();

        int currentPage = paginationManager.getCurrentPage();
        this.paginationManager = new PaginationManager(shop.getShopPages());
        this.paginationManager.setCurrentPage(currentPage);
    }
}