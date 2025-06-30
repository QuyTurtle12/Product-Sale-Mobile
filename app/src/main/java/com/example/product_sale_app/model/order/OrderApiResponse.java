package com.example.product_sale_app.model.order;

import com.example.product_sale_app.model.cart.CartResponse;

import java.util.List;

public class OrderApiResponse {



    private OrderResponse data;

    public OrderResponse getData() {
        return data;
    }

    public void setData(OrderResponse data) {
        this.data = data;
    }
    private String code;

    public Object getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Object additionalData) {
        this.additionalData = additionalData;
    }

    private String message;
    private int statusCode;
    private Object additionalData;


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
}

