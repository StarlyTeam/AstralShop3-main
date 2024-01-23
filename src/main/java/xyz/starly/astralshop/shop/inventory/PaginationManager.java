package xyz.starly.astralshop.shop.inventory;

import lombok.Getter;
import xyz.starly.astralshop.api.shop.ShopPage;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PaginationManager {

    private final List<ShopPage> pages;
    private int currentPage;

    public PaginationManager(List<ShopPage> shopPages) {
        this.pages = new ArrayList<>(shopPages);
        this.currentPage = 1;
    }

    public void nextPage() {
        if (currentPage < pages.size()) {
            currentPage++;
        }
    }

    public void prevPage() {
        if (currentPage > 1) {
            currentPage--;
        }
    }

    public ShopPage getCurrentPageData() {
        return pages.get(currentPage - 1);
    }

    public int getTotalPages() {
        return pages.size();
    }

    public boolean isLastPage() {
        return currentPage >= pages.size();
    }
}