package kr.starly.astralshop.shop.inventory;

import kr.starly.astralshop.api.shop.ShopPage;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class PaginationHelper {

    private final PaginationManager paginationManager;

    public List<ShopPage> getPages() {
        return paginationManager.getPages();
    }

    public int getCurrentPage() {
        return paginationManager.getCurrentPage();
    }

    public boolean hasNextPage() {
        return paginationManager.hasNextPage();
    }

    public boolean hasPrevPage() {
        return paginationManager.hasPrevPage();
    }

    public boolean isValidPage(int pageNumber) {
        return paginationManager.isValidPage(pageNumber);
    }

    public ShopPage getCurrentPageData() {
        return paginationManager.getCurrentPageData();
    }

    public int getTotalPages() {
        return paginationManager.getTotalPages();
    }


    public int getStartPage() {
        if (paginationManager.getCurrentPage() >= 61) {
            return 58;
        }
        return Math.max(1, paginationManager.getCurrentPage() - 3);
    }

    public int getEndPage() {
        if (paginationManager.getCurrentPage() >= 61) {
            return Math.min(64, paginationManager.getTotalPages());
        }
        return Math.min(getStartPage() + 6, paginationManager.getTotalPages());
    }
}