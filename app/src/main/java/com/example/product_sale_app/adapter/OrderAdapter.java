package com.example.product_sale_app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
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
        String paymentMethod = order.getPaymentMethod();
        String billingAddress = order.getBillingAddress();
        String orderStatus = order.getOrderStatus();

        // Order ID
        holder.orderIdText.setText("Order ID: #" + orderId);
        // Order Date
        holder.orderDateText.setText("Date: " + formatDate(orderDate));
        // Order Total
        holder.orderTotalText.setText("Total: " + formatPrice(totalPrice));
        holder.orderPaymentMethod.setText("Payment method: " + paymentMethod);
        holder.orderBillingAddress.setText("Address: " + billingAddress);
        holder.orderStatus.setText("Status: " + orderStatus);
        // Change color or style based on orderStatus
        styleStatusBadge(holder.orderStatus, orderStatus);

        // Append cart item names
//            StringBuilder items = new StringBuilder();
//            for (CartItemDTO item : order.getCart().getCartItems()) {
//                items.append("- ").append(item.getProductName())
//                     .append(" x").append(item.getQuantity())
//                     .append("\n");
//            }
            // holder.productListText.setText(items.toString().trim());

        holder.toggleButton.setOnClickListener(v -> {
            if (holder.orderItemsContainer.getVisibility() == View.VISIBLE) {
                holder.orderItemsContainer.setVisibility(View.GONE);
                holder.toggleButton.setText("Show Items");
            } else {
                holder.orderItemsContainer.setVisibility(View.VISIBLE);
                holder.toggleButton.setText("Hide Items");
            }
        });


        // Handle list of items in order
        // Reuse CartAdapter to display cart items inside order
        CartAdapter cartAdapter = new CartAdapter(
                order.getCart().getCartItems(),
                () -> {}, // No-op: no need to update total when inside order history
                order.getUserId(), // provide userId if needed, or 0 if not relevant
                false
        );
        holder.productRecyclerView.setAdapter(cartAdapter);
        holder.productRecyclerView.setLayoutManager(new LinearLayoutManager(context));




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
        TextView orderIdText, orderDateText, orderTotalText, orderPaymentMethod, orderBillingAddress, orderStatus;
        RecyclerView productRecyclerView;
        Button toggleButton;
        LinearLayout orderItemsContainer;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.orderIdText);
            orderDateText = itemView.findViewById(R.id.orderDateText);
            orderTotalText = itemView.findViewById(R.id.orderTotalText);
            orderPaymentMethod = itemView.findViewById(R.id.orderPaymentMethod);
            orderBillingAddress = itemView.findViewById(R.id.orderAddressBilling);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            productRecyclerView = itemView.findViewById(R.id.productRecyclerView);

            // Show/Hide button
            toggleButton = itemView.findViewById(R.id.btn_toggleItems);
            orderItemsContainer = itemView.findViewById(R.id.orderItemsContainer);
        }
    }




    // Styling order status
    private void styleStatusBadge(TextView statusView, String status) {
        GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(20); // Rounded corners

        switch (status) {
            case "Pending":
                background.setColor(Color.parseColor("#FF5722")); // Deep orange
                break;
            case "Processing":
                background.setColor(Color.parseColor("#1976D2")); // Blue
                break;
            case "Delivered":
                background.setColor(Color.parseColor("#2E7D32")); // Dark green
                break;
            default:
                background.setColor(Color.GRAY);
                break;
        }

        statusView.setBackground(background);
        statusView.setTextColor(Color.WHITE);
        statusView.setText("Status: " + status);
        statusView.setPadding(30, 15, 30, 15);
        statusView.setTextSize(13);
        statusView.setTypeface(null, Typeface.BOLD);
    }
}

