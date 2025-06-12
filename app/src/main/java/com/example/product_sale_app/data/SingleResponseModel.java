// src/main/java/com/example/product_sale_app/data/SingleResponseModel.java
package com.example.product_sale_app.data;

import com.google.gson.annotations.SerializedName;

public class SingleResponseModel<T> {
    @SerializedName("data")
    public T data;

    @SerializedName("message")
    public String message;

    @SerializedName("statusCode")
    public int statusCode;

    @SerializedName("code")
    public String code;
}
