package com.example.product_sale_app.model.home_product;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private int productId;
    private String categoryName;
    private String productName;
    private String briefDescription;
    private String fullDescription;
    private String technicalSpecifications;
    private int price;
    private String imageUrl;

    // Constructor
    public Product(int productId, String categoryName, String productName, String briefDescription,
                   String fullDescription, String technicalSpecifications, int price, String imageUrl) {
        this.productId = productId;
        this.categoryName = categoryName;
        this.productName = productName;
        this.briefDescription = briefDescription;
        this.fullDescription = fullDescription;
        this.technicalSpecifications = technicalSpecifications;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Parcelable implementation
    protected Product(Parcel in) {
        productId = in.readInt();
        categoryName = in.readString();
        productName = in.readString();
        briefDescription = in.readString();
        fullDescription = in.readString();
        technicalSpecifications = in.readString();
        price = in.readInt();
        imageUrl = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(productId);
        dest.writeString(categoryName);
        dest.writeString(productName);
        dest.writeString(briefDescription);
        dest.writeString(fullDescription);
        dest.writeString(technicalSpecifications);
        dest.writeInt(price);
        dest.writeString(imageUrl);
    }

    // Getters
    public int getProductId() { return productId; }
    public String getCategoryName() { return categoryName; }
    public String getProductName() { return productName; }
    public String getBriefDescription() { return briefDescription; }
    public String getFullDescription() { return fullDescription; }
    public String getTechnicalSpecifications() { return technicalSpecifications; }
    public int getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}