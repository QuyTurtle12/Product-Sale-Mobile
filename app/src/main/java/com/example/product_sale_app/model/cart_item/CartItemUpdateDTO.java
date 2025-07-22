package com.example.product_sale_app.model.cart_item;

public class CartItemUpdateDTO {
    public int getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    private int cartItemId;
    private int productId;
    private int cartId;
    private int quantity;

    public CartItemUpdateDTO(int cartItemId, int productId, int cartId, int quantity) {
        this.cartItemId = cartItemId;
        this.productId = productId;
        this.cartId = cartId;
        this.quantity = quantity;
    }


}
