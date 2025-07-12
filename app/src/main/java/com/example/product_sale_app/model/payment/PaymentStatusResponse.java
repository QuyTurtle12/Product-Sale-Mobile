package com.example.product_sale_app.model.payment;

public class PaymentStatusResponse {
    private String status;
    private int orderId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
