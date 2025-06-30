package com.example.product_sale_app.model;

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
    private int brandId; // Thêm trường brandId

    // Constructor mới với brandId
    public Product(int productId, String categoryName, String productName, String briefDescription,
                   String fullDescription, String technicalSpecifications, int price, String imageUrl, int brandId) {
        this.productId = productId;
        this.categoryName = categoryName;
        this.productName = productName;
        this.briefDescription = briefDescription;
        this.fullDescription = fullDescription;
        this.technicalSpecifications = technicalSpecifications;
        this.price = price;
        this.imageUrl = imageUrl;
        this.brandId = brandId; // Gán giá trị brandId
    }

    // Constructor cũ (8 tham số, giữ nguyên để backward compatibility nếu cần)
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
        this.brandId = -1; // Giá trị mặc định nếu không có brandId
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
        brandId = in.readInt(); // Đọc brandId từ Parcel
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
        dest.writeInt(brandId); // Ghi brandId vào Parcel
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
    public int getBrandId() { return brandId; } // Thêm getter cho brandId

    // Setter (nếu cần)
    public void setBrandId(int brandId) { this.brandId = brandId; }
}