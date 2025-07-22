package com.example.product_sale_app.model.order;

public class OrderPostResponseDTO {
    private int data; // <- because you're returning orderId in "data"
    private String message;
    private int statusCode;
    private String code;

    public int getData() { return data; }
    public void setData(int data) { this.data = data; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}

