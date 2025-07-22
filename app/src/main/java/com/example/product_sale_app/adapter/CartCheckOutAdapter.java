package com.example.product_sale_app.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.product_sale_app.R;
import com.example.product_sale_app.model.cart.CartItemDTO;

import java.math.BigDecimal;
import java.util.List;

public class CartCheckOutAdapter extends RecyclerView.Adapter<CartCheckOutAdapter.ViewHolder> {

    private List<CartItemDTO> cartItems;

    public CartCheckOutAdapter(List<CartItemDTO> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_item, parent, false); // You’ll need to create this layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Log.d("AdapterDebug", "Binding: " + position);

        CartItemDTO item = cartItems.get(position);

        Log.d("CartCheckOutAdapter", "Binding item at position: " + position + ", name: " + item.getProductName());

        holder.txtOrderNumber.setText((position + 1) + ".");
        holder.txtProductName.setText(item.getProductName());
        holder.txtQuantity.setText("Quantity: " + item.getQuantity());
        holder.txtTotalPrice.setText(String.format("%,.0f₫",
                item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))));

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderNumber, txtProductName, txtQuantity, txtTotalPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderNumber = itemView.findViewById(R.id.txt_orderNumber);
            txtProductName = itemView.findViewById(R.id.txt_productName);
            txtQuantity = itemView.findViewById(R.id.txt_productQuantity);
            txtTotalPrice = itemView.findViewById(R.id.txt_price);
        }
    }
}