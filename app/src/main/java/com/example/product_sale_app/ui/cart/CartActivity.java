package com.example.product_sale_app.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.product_sale_app.model.cart.CartHolder;
import com.example.product_sale_app.model.cart.CartItemDTO;
import com.example.product_sale_app.ui.payment.CartCheckOutActivity;
import com.example.product_sale_app.network.RetrofitClient;
import com.example.product_sale_app.network.service.CartApiService;
import com.example.product_sale_app.repository.CartRepository;
import com.example.product_sale_app.ui.home.HomeActivity;
import com.example.product_sale_app.utils.BadgeUtils;

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
    private Button checkOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        backPage = findViewById(R.id.btn_back);
        checkOutButton = findViewById(R.id.btn_checkOut);
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

        // this will go to Order page
//        checkOutButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(CartActivity.this, OrderActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });




    }

    private void fetchCartsFromApi() {
        CartApiService apiService = RetrofitClient.createService(this, CartApiService.class);

        // Call<CartApiResponse> call = apiService.getPaginatedCarts(1, 10, 1, null, null, null); // Test fetch API on UI
        Call<CartApiResponse> call = apiService.getPaginatedCarts(1, 10, null, null, null, true);

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

                        // Update badge count with total items
                        int totalItems = 0;
                        for (CartItemDTO item : allCartItems) {
                            totalItems += item.getQuantity();
                        }
                        BadgeUtils.updateBadgeCount(CartActivity.this, totalItems);

                        // int userId = 1; // this one for test purpose. Will delete after login successful
                        // This code below will get userId from API
                        int userId = 0;
                        List<CartDTO> carts = response.body().getData().getItems();
                        if (carts != null && !carts.isEmpty()) {
                            userId = carts.get(0).getUserId();
                            Log.d("CartActivity", "User ID: " + userId);
                        } else {
                            Log.d("CartActivity", "Cart list is empty");
                        }

                        cartAdapter = new CartAdapter(allCartItems, () -> updateTotalPrice(allCartItems), userId, true); // delete userId after login has
                        cartRecyclerView.setLayoutManager(new LinearLayoutManager(CartActivity.this));
                        cartRecyclerView.setAdapter(cartAdapter);

                        // Set data only after adapter is ready
                        CartHolder.setItems(cartAdapter.getCartItems());
                        Log.d("CartActivity", "CartHolder set with items count: " + cartAdapter.getCartItems().size());

                        int currentUserId = userId; // This will pass userId to Check Out page
                        int currentCartId = cartDTOs.get(0).getCartId(); // This will pass cartId to Check Out page
                        checkOutButton.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(CartActivity.this, CartCheckOutActivity.class);
                                intent.putExtra("userId", currentUserId);
                                intent.putExtra("cartId", currentCartId);
                                startActivity(intent);
                                finish();
                            }
                        });

                        // Update total price text
                        TextView totalPriceView = findViewById(R.id.txt_totalPriceCart);
                        totalPriceView.setText(String.format("%,.0f₫", totalPrice));
                    } else {
                        Toast.makeText(CartActivity.this, "No cart data found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Toast.makeText(CartActivity.this, "Error fetching carts", Toast.LENGTH_SHORT).show();
                    Toast.makeText(CartActivity.this, "You have to login to use cart", Toast.LENGTH_SHORT).show();
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