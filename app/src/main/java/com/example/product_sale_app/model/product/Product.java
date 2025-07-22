package com.example.product_sale_app.model.product;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Product implements Parcelable {
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

        @SerializedName("rating")
        private BigDecimal rating;

        @SerializedName("imageUrls")
        private List<String> imageUrls;

        // Constructors
        public Product(int productId, String productName, String briefDescription, String fullDescription,
                       String technicalSpecifications, BigDecimal price, List<String> imageUrls, String categoryName, BigDecimal rating) {
                this.productId = productId;
                this.productName = productName != null ? productName : "";
                this.briefDescription = briefDescription != null ? briefDescription : "";
                this.fullDescription = fullDescription != null ? fullDescription : "";
                this.technicalSpecifications = technicalSpecifications != null ? technicalSpecifications : "";
                this.price = price != null ? price : BigDecimal.ZERO;
                this.imageUrls = imageUrls != null ? new ArrayList<>(imageUrls) : new ArrayList<>();
                this.categoryName = categoryName != null ? categoryName : "";
                this.rating = rating != null ? rating : BigDecimal.ZERO;
        }

        public Product() {
                // Constructor mặc định cho GSON
                this(0, "", "", "", "", BigDecimal.ZERO, new ArrayList<>(), "", BigDecimal.ZERO);
        }

        // Getters
        public int getProductId() { return productId; }
        public String getCategoryName() { return categoryName; }
        public String getProductName() { return productName; }
        public String getBriefDescription() { return briefDescription; }
        public String getFullDescription() { return fullDescription; }
        public String getTechnicalSpecifications() { return technicalSpecifications; }
        public BigDecimal getPrice() { return price; }
        public BigDecimal getRating() { return rating; }
        public List<String> getImageUrls() { return new ArrayList<>(imageUrls); } // Trả về bản sao để tránh sửa đổi trực tiếp

        // Setters
        public void setProductId(int productId) { this.productId = productId; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName != null ? categoryName : ""; }
        public void setProductName(String productName) { this.productName = productName != null ? productName : ""; }
        public void setBriefDescription(String briefDescription) { this.briefDescription = briefDescription != null ? briefDescription : ""; }
        public void setFullDescription(String fullDescription) { this.fullDescription = fullDescription != null ? fullDescription : ""; }
        public void setTechnicalSpecifications(String technicalSpecifications) { this.technicalSpecifications = technicalSpecifications != null ? technicalSpecifications : ""; }
        public void setPrice(BigDecimal price) { this.price = price != null ? price : BigDecimal.ZERO; }
        public void setRating(BigDecimal rating) { this.rating = rating != null ? rating : BigDecimal.ZERO; }
        public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls != null ? new ArrayList<>(imageUrls) : new ArrayList<>(); }

        // Parcelable implementation
        protected Product(Parcel in) {
                productId = in.readInt();
                categoryName = in.readString();
                productName = in.readString();
                briefDescription = in.readString();
                fullDescription = in.readString();
                technicalSpecifications = in.readString();
                price = (BigDecimal) in.readSerializable();
                rating = (BigDecimal) in.readSerializable();
                imageUrls = in.createStringArrayList();
                if (imageUrls == null) imageUrls = new ArrayList<>();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(productId);
                dest.writeString(categoryName);
                dest.writeString(productName);
                dest.writeString(briefDescription);
                dest.writeString(fullDescription);
                dest.writeString(technicalSpecifications);
                dest.writeSerializable(price);
                dest.writeSerializable(rating);
                dest.writeStringList(imageUrls);
        }

        @Override
        public int describeContents() {
                return 0;
        }

        public static final Creator<Product> CREATOR = new Creator<Product>() {
                @Override
                public Product createFromParcel(Parcel in) {
                        return new Product(in);
                }

                @Override
                public Product[] newArray(int size) {
                        return new Product[size];
                }
        };
}