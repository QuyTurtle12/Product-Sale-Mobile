package com.example.product_sale_app.model.payment;

public class VNPayPaymentResponse {
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Object getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Object additionalData) {
        this.additionalData = additionalData;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public VNPayPaymentResponse(String data, Object additionalData, String message, int statusCode, String code) {
        this.data = data;
        this.additionalData = additionalData;
        this.message = message;
        this.statusCode = statusCode;
        this.code = code;
    }

    private String data;
    private Object additionalData;
    private String message;
    private int statusCode;
    private String code;
}
