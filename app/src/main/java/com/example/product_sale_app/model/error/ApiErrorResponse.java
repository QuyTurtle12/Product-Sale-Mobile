package com.example.product_sale_app.model;

import com.google.gson.annotations.SerializedName;

public class ApiErrorResponse {
    @SerializedName("errorCode")
    private String errorCode;

    @SerializedName("errorMessage")
    private String errorMessage;

    // Default constructor (needed for Gson)
    public ApiErrorResponse() {
    }

    // Getters
    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "ApiErrorResponse{" +
                "errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
