package com.example.product_sale_app.model.product;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductData {
    @SerializedName("items")
    private List<Product> items;

    @SerializedName("pageNumber")
    private int pageNumber;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("totalCount")
    private int totalCount;

    @SerializedName("pageSize")
    private int pageSize;

    @SerializedName("hasPreviousPage")
    private boolean hasPreviousPage;

    @SerializedName("hasNextPage")
    private boolean hasNextPage;

    // Getters
    public List<Product> getItems() {
        return items;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }
}
