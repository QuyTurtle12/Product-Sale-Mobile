package com.example.product_sale_app.ui.cart;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.product_sale_app.R;
import com.example.product_sale_app.adapter.CartAdapter;
import com.example.product_sale_app.model.cart.CartDTO;
import com.example.product_sale_app.model.cart.CartResponse;
import com.example.product_sale_app.repository.CartRepository;

import java.util.List;


public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private CartRepository cartRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // 1. Find RecyclerView by ID
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        // 2. Set layout manager
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 3. Initialize repository
        cartRepository = new CartRepository();
        // 4. Fetch carts
//        fetchCartsFromApi();
    }

//    private void fetchCartsFromApi() {
//        cartRepository.fetchCarts(1, 10, null, 3, null, new CartRepository.CartCallback() {
//            @Override
//            public void onSuccess(CartResponse response) {
//                List<CartDTO> cartList = response.getItems();
//
//                runOnUiThread(() -> {
//                    cartAdapter = new CartAdapter(cartList);
//                    cartRecyclerView.setAdapter(cartAdapter);
//                });
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                runOnUiThread(() -> {
//                    Toast.makeText(CartActivity.this, errorMessage, Toast.LENGTH_LONG).show();
//                    Log.e("CartActivity", "Error fetching carts: " + errorMessage);
//                });
//            }
//        });
//    }


}