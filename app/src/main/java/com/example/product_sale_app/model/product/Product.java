package com.example.product_sale_app.model.product;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class Product {
        @SerializedName("productId")
        private int productId;

        @SerializedName("categoryName")
        private String categoryName;

        @SerializedName("productName")
        private String productName;

        @SerializedName("briefDescription")
        private String briefDescription;

        @SerializedName("fullDescription")
        private String fullDescription;

        @SerializedName("technicalSpecifications")
        private String technicalSpecifications;

        @SerializedName("price")
        private BigDecimal price;

        @SerializedName("imageUrl")
        private String imageUrl;

        // Constructors
        public Product(int productId, String productName, String briefDescription, BigDecimal price, String imageUrl, String categoryName) {
                this.productId = productId;
                this.productName = productName;
                this.briefDescription = briefDescription;
                this.price = price;
                this.imageUrl = imageUrl;
                this.categoryName = categoryName;
        }

        // Getters
        public int getProductId() { return productId; }
        public String getCategoryName() { return categoryName; }
        public String getProductName() { return productName; }
        public String getBriefDescription() { return briefDescription; }
        public String getFullDescription() { return fullDescription; }
        public String getTechnicalSpecifications() { return technicalSpecifications; }
        public BigDecimal getPrice() { return price; }
        public String getImageUrl() { return imageUrl; }
}
