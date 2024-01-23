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

    public boolean hasNextPage() {
        return currentPage < pages.size();
    }

    public boolean hasPrevPage() {
        return currentPage > 1;
    }

    public boolean isValidPage(int pageNumber) {
        return pageNumber >= 1 && pageNumber <= pages.size();
    }

    public void setCurrentPage(int pageNumber) {
        if (isValidPage(pageNumber)) {
            this.currentPage = pageNumber;
        }
    }

    public ShopPage getCurrentPageData() {
        return pages.get(currentPage - 1);
    }

    public int getTotalPages() {
        return pages.size();
    }
}