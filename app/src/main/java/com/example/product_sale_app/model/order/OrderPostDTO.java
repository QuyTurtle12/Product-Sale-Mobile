package com.example.product_sale_app.model.order;

public class OrderPostDTO {
    public OrderPostDTO(int cartId, int userId, String paymentMethod, String billingAddress, String orderStatus) {
        this.cartId = cartId;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.billingAddress = billingAddress;
        this.orderStatus = orderStatus;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    private int cartId;
    private int userId;
    private String paymentMethod;
    private String billingAddress;
    private String orderStatus;




}
