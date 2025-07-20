package gg.supervisor.util.misc;

import java.util.List;

public class Paginator<T> {
    private final List<T> items;
    private final int itemsPerPage;
    private final int totalPages;

    public Paginator(List<T> items, int itemsPerPage) {
        this.items = items;
        this.itemsPerPage = itemsPerPage;
        this.totalPages = (int) Math.ceil((double) items.size() / itemsPerPage);
    }

    public List<T> getPage(int pageNumber) {
        int validPage = Math.max(1, Math.min(pageNumber, totalPages));

        int startIndex = (validPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, items.size());

        return items.subList(startIndex, endIndex);
    }

    public int getTotalPages() {
        return totalPages;
    }
}
