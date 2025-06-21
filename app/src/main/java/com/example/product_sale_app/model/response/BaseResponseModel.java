package com.example.product_sale_app.data;

import com.google.gson.annotations.SerializedName;

public class BaseResponseModel<T> {
    @SerializedName("data")
    public PaginatedList<T> data;

    @SerializedName("additionalData")
    public Object additionalData;

    @SerializedName("message")
    public String message;

    @SerializedName("statusCode")
    public int statusCode;

    @SerializedName("code")
    public String code;
}
