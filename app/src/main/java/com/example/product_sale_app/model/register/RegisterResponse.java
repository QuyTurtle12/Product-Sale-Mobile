package com.example.product_sale_app.model.register;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    @SerializedName("data")
    private RegisterData data;

    @SerializedName("additionalData")
    private Object additionalData;

    @SerializedName("message")
    private String message;

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("code")
    private String code;

    // Default constructor
    public RegisterResponse() {
    }

    // Getters (and Setters if needed)
    public RegisterData getData() { return data; }
    public void setData(RegisterData data) { this.data = data; }
    public Object getAdditionalData() { return additionalData; }
    public void setAdditionalData(Object additionalData) { this.additionalData = additionalData; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
