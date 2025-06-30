package com.example.product_sale_app.model.cart;

public class CartUpdateDTO {
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    private int userId;

    public CartUpdateDTO(int userId) {
        this.userId = userId;
    }


}
