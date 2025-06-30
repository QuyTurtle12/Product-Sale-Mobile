package com.example.product_sale_app.model.login;

import com.google.gson.annotations.SerializedName;

public class LoginResponse
{
    @SerializedName("data")
    private LoginData data; // This will hold the nested data object

    @SerializedName("additionalData")
    private Object additionalData;

    @SerializedName("message")
    private String message;

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("code")
    private String code; // e.g., "SUCCESS"

    // Constructor
    public LoginResponse() {
    }

    // Getters
    public LoginData getData() {
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
