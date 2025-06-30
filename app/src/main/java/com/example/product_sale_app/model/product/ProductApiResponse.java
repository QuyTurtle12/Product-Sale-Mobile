package com.example.product_sale_app.model.product;

import com.google.gson.annotations.SerializedName;

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
