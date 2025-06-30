package com.example.product_sale_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.product_sale_app.R;
import com.example.product_sale_app.model.cart.CartItemDTO;
import com.example.product_sale_app.model.order.OrderDTO;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<OrderDTO> orderList;
    private Context context;

    public OrderAdapter(Context context, List<OrderDTO> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderDTO order = orderList.get(position);

        int orderId = order.getOrderId();
        String orderDate = order.getOrderDate();
        BigDecimal totalPrice = order.getCart().getTotalPrice();

        // Order ID
        holder.orderIdText.setText("Order ID: #" + orderId);
        // Order Date
        holder.orderDateText.setText("Date: " + formatDate(orderDate));
        // Order Total
        holder.orderTotalText.setText("Total: " + formatPrice(totalPrice));

        // Append cart item names
            StringBuilder items = new StringBuilder();
            for (CartItemDTO item : order.getCart().getCartItems()) {
                items.append("- ").append(item.getProductName())
                     .append(" x").append(item.getQuantity())
                     .append("\n");
            }
            // holder.productListText.setText(items.toString().trim());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    // Optional: Convert date string to readable format
    private String formatDate(String rawDate) {
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = input.parse(rawDate);

            SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            return output.format(date);
        } catch (Exception e) {
            return rawDate;
        }
    }

    private String formatPrice(BigDecimal price) {
        return String.format(Locale.getDefault(), "%,.0fđ", price);
    }

    // ViewHolder class
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdText, orderDateText, orderTotalText;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.orderIdText);
            orderDateText = itemView.findViewById(R.id.orderDateText);
            orderTotalText = itemView.findViewById(R.id.orderTotalText);
        }
    }
}

