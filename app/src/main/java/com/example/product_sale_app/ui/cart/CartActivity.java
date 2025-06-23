package com.example.product_sale_app.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.product_sale_app.R;
import com.example.product_sale_app.adapter.CartAdapter;
import com.example.product_sale_app.model.cart.CartApiResponse;
import com.example.product_sale_app.model.cart.CartDTO;
import com.example.product_sale_app.model.cart.CartItemDTO;
import com.example.product_sale_app.network.RetrofitClient;
import com.example.product_sale_app.network.service.CartApiService;
import com.example.product_sale_app.repository.CartRepository;
import com.example.product_sale_app.ui.home.HomeActivity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private CartRepository cartRepository;
    private ImageView backPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        backPage = findViewById(R.id.btn_back);
        // 1. Find RecyclerView by ID
        cartRecyclerView = findViewById(R.id.cartRecyclerView);

        // 2. Set layout manager
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 3. Initialize repository
        cartRepository = new CartRepository();
        // 4. Fetch carts
        fetchCartsFromApi();

        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fetchCartsFromApi() {
        CartApiService apiService = RetrofitClient.createService(this, CartApiService.class);

        Call<CartApiResponse> call = apiService.getPaginatedCarts(1, 10, 9, null, null); // Example with idSearch = 14

        call.enqueue(new Callback<CartApiResponse>() {
            @Override
            public void onResponse(Call<CartApiResponse> call, Response<CartApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CartDTO> cartDTOs = response.body().getData().getItems();

                    if (cartDTOs != null && !cartDTOs.isEmpty()) {
                        List<CartItemDTO> allCartItems = new ArrayList<>();
                        BigDecimal totalPrice = BigDecimal.ZERO;

                        for (CartDTO cart : cartDTOs) {
                            if (cart.getCartItems() != null) {
                                allCartItems.addAll(cart.getCartItems());
                            }

                            if (cart.getTotalPrice() != null) {
                                totalPrice = totalPrice.add(cart.getTotalPrice());
                            }
                        }

                        cartAdapter = new CartAdapter(allCartItems, () -> updateTotalPrice(allCartItems));
                        cartRecyclerView.setLayoutManager(new LinearLayoutManager(CartActivity.this));
                        cartRecyclerView.setAdapter(cartAdapter);

                        // Update total price text
                        TextView totalPriceView = findViewById(R.id.txt_totalPriceCart);
                        totalPriceView.setText(String.format("%,.0f₫", totalPrice));
                    } else {
                        Toast.makeText(CartActivity.this, "No cart data found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CartActivity.this, "Error fetching carts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartApiResponse> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotalPrice(List<CartItemDTO> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItemDTO item : items) {
            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        TextView totalPriceView = findViewById(R.id.txt_totalPriceCart);
        totalPriceView.setText(String.format("%,.0f₫", total));
    }



}