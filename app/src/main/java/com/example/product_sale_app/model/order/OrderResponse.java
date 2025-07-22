package com.example.product_sale_app.model.order;


import java.util.List;

public class OrderResponse {
    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public List<OrderDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderDTO> items) {
        this.items = items;
    }

    private List<OrderDTO> items;
    private int pageNumber;
    private int totalPages;
    private int totalCount;
    private int pageSize;
    private boolean hasPreviousPage;
    private boolean hasNextPage;
}

