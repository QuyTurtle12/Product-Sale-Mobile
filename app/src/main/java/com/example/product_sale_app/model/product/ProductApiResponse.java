package com.example.product_sale_app.model.product;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductApiResponse {
    @SerializedName("data")
    private ProductData data;

    @SerializedName("additionalData")
    private Object additionalData;

    @SerializedName("message")
    private String message;

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("code")
    private String code;

    // Nested class ProductData
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

    // Getters
    public ProductData getData() {
        return data;
    }

    public Object getAdditionalData() {
        return additionalData;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getCode() {
        return code;
    }
}