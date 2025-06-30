package com.example.product_sale_app.model.cart;

import java.util.List;

public class CartHolder {
    private static List<CartItemDTO> items;

    public static void setItems(List<CartItemDTO> cartItems) {
        items = cartItems;
    }

    public static List<CartItemDTO> getItems() {
        return items;
    }

    public static void clear() {
        items = null;
    }
}