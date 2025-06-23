package com.example.product_sale_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.product_sale_app.R;
import com.example.product_sale_app.model.cart.CartItemDTO;
import com.example.product_sale_app.model.cart.CartUpdateDTO;
import com.example.product_sale_app.network.RetrofitClient;
import com.example.product_sale_app.network.service.CartApiService;

import java.math.BigDecimal;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    public interface OnCartItemChangeListener {
        void onCartUpdated();
    }
    private List<CartItemDTO> cartItems;
    private OnCartItemChangeListener listener;
    private int userId; // delete userId after login has

    public CartAdapter(List<CartItemDTO> cartItems, OnCartItemChangeListener listener, int userId) { // delete userId after login has
        this.cartItems = cartItems;
        this.listener = listener;
        this.userId = userId;
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
        CartItemDTO item = cartItems.get(position);
        int userId = this.userId; // delete this line after login function has
        // Placeholder product name, you should fetch product name by productId if needed
        // holder.txt_productName.setText("Product #" + item.getProductId());

        String productName = item.getProductName();
        String fullDescription = item.getFullDescription();
        String imageUrl = item.getImageUrl();
        BigDecimal price = item.getPrice();
        int quantity = item.getQuantity();
        BigDecimal total = price.multiply(BigDecimal.valueOf(quantity));


        holder.txt_productName.setText(productName);
        holder.txt_productDescription.setText(fullDescription);
        holder.txt_price.setText(String.format("%,.0f₫", price));
        holder.txt_quantity.setText(String.valueOf(quantity));
        holder.txt_productTotal.setText("Total: " + String.format("%,.0f₫", total));

        // Handle delete button click
        holder.btn_increase.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
            listener.onCartUpdated();
        });

        holder.btn_decrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
                listener.onCartUpdated();
            }
        });

        holder.btn_delete.setOnClickListener(v -> {
            int cartItemId = item.getCartItemId();
            int cartId = item.getCartId();

            CartApiService apiService = RetrofitClient.createService(holder.itemView.getContext(), CartApiService.class);

            apiService.deleteCartItem(cartItemId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        int currentPosition = holder.getBindingAdapterPosition(); // 💡 Get latest position
                        if (currentPosition != RecyclerView.NO_POSITION) {
                            // Remove item out of cart
                            cartItems.remove(currentPosition);
                            notifyItemRemoved(currentPosition);
                            listener.onCartUpdated();
                            Toast.makeText(holder.itemView.getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                            // Call PUT to update cart total
                            CartUpdateDTO updateDTO = new CartUpdateDTO(userId);
                            apiService.updateCartTotalPrice(cartId, updateDTO).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (!response.isSuccessful()) {
                                        Toast.makeText(holder.itemView.getContext(), "Failed to update cart total", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(holder.itemView.getContext(), "Error updating cart: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(holder.itemView.getContext(), "Failed to delete item", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(holder.itemView.getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        public ImageView btn_delete;
        TextView txt_productName, txt_price, txt_productDescription, txt_quantity, txt_productTotal;
        ImageView btn_increase, btn_decrease;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_productName = itemView.findViewById(R.id.txt_productName);
            txt_price = itemView.findViewById(R.id.txt_price);
            txt_productDescription = itemView.findViewById(R.id.txt_productDescription);
            txt_quantity = itemView.findViewById(R.id.txt_quantity);
            txt_productTotal = itemView.findViewById(R.id.txt_productTotal);

            btn_increase = itemView.findViewById(R.id.btn_increase);
            btn_decrease = itemView.findViewById(R.id.btn_decrease);
            btn_delete = itemView.findViewById(R.id.btn_delete);
        }
    }
}

