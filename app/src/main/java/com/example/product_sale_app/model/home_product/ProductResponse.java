
package com.example.product_sale_app.model.home_product;

import java.util.List;

public class ProductResponse {
    private Data data;
    private String message;
    private int statusCode;
    private String code;

    public static class Data {
        private List<Product> items;
        private int pageNumber;
        private int totalPages;
        private int totalCount;
        private int pageSize;
        private boolean hasPreviousPage;
        private boolean hasNextPage;

        public List<Product> getItems() { return items; }
        public int getPageNumber() { return pageNumber; }
        public int getTotalPages() { return totalPages; }
        public int getTotalCount() { return totalCount; }
        public int getPageSize() { return pageSize; }
        public boolean isHasPreviousPage() { return hasPreviousPage; }
        public boolean isHasNextPage() { return hasNextPage; }
    }

    public Data getData() { return data; }
    public String getMessage() { return message; }
    public int getStatusCode() { return statusCode; }
    public String getCode() { return code; }
}
