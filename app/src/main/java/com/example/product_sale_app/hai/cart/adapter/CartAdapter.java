package com.example.product_sale_app.hai.cart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.product_sale_app.R;
import com.example.product_sale_app.hai.cart.model.CartDTO;
import com.example.product_sale_app.hai.cart.model.CartItemDTO;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartDTO> cartList;

    public CartAdapter(List<CartDTO> cartList) {
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartDTO cart = cartList.get(position);

        // Show Cart ID and Total Price
        holder.txt_productName.setText("Cart #" + cart.getCartId());
        holder.txt_price.setText("Total: " + cart.getTotalPrice().toPlainString() + "₫");

        // Optional: Show status
        holder.txt_productDescription.setText("Status: " + cart.getStatus());

        // Show number of items
        int quantity = 0;
        for (CartItemDTO item : cart.getCartItems()) {
            quantity += item.getQuantity();
        }

        holder.txt_quantity.setText(String.valueOf(quantity));
        holder.txt_productTotal.setText("Items: " + cart.getCartItems().size());
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView txt_productName, txt_price, txt_productDescription, txt_quantity, txt_productTotal;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_productName = itemView.findViewById(R.id.txt_productName);
            txt_price = itemView.findViewById(R.id.txt_price);
            txt_productDescription = itemView.findViewById(R.id.txt_productDescription);
            txt_quantity = itemView.findViewById(R.id.txt_quantity);
            txt_productTotal = itemView.findViewById(R.id.txt_productTotal);
        }
    }
}
