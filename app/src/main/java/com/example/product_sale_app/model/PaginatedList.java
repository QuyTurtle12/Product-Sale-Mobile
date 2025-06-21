package com.example.product_sale_app.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PaginatedList<T> {
    @SerializedName("items")
    public List<T> items;

    @SerializedName("pageNumber")
    public int pageNumber;

    @SerializedName("totalPages")
    public int totalPages;

    @SerializedName("totalCount")
    public int totalCount;

    @SerializedName("pageSize")
    public int pageSize;

    @SerializedName("hasPreviousPage")
    public boolean hasPreviousPage;

    @SerializedName("hasNextPage")
    public boolean hasNextPage;
}
