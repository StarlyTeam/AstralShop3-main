package kr.starly.astralshop.shop.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DynamicPaginationHelper {

    private final int currentPage;
    private final int totalPages;

    public int getStartPage() {
        if (currentPage >= 61) {
            return 58;
        }
        return Math.max(1, currentPage - 3);
    }

    public int getEndPage() {
        if (currentPage >= 61) {
            return Math.min(64, totalPages);
        }
        return Math.min(getStartPage() + 6, totalPages);
    }


    public boolean isValidPage(int pageNumber) {
        return pageNumber >= 1 && pageNumber <= totalPages;
    }
}