package com.example.product_sale_app.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.product_sale_app.R;
import com.example.product_sale_app.adapter.OrderAdapter;
import com.example.product_sale_app.model.cart.CartApiResponse;
import com.example.product_sale_app.model.order.OrderApiResponse;
import com.example.product_sale_app.model.order.OrderDTO;
import com.example.product_sale_app.model.order.OrderResponse;
import com.example.product_sale_app.network.RetrofitClient;
import com.example.product_sale_app.network.service.CartApiService;
import com.example.product_sale_app.network.service.OrderApiService;
import com.example.product_sale_app.ui.cart.CartActivity;
import com.example.product_sale_app.ui.home.HomeActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    private ImageView backPage;
    private RecyclerView orderRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        orderRecyclerView = findViewById(R.id.orderRecyclerView);

        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        backPage = findViewById(R.id.btn_back);
        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        fetchOrdersFromApi();
    }


    private void fetchOrdersFromApi() {
        OrderApiService apiService = RetrofitClient.createService(this, OrderApiService.class);

        Call<OrderApiResponse> call = apiService.getOrders(1, 10, null
                ,null, null,null,null,
                null,null,null,null, true);

        call.enqueue(new Callback<OrderApiResponse>() {
            @Override
            public void onResponse(Call<OrderApiResponse> call, Response<OrderApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderDTO> orders = response.body().getData().getItems();
                    // Now populate RecyclerView with this data
                    OrderAdapter adapter = new OrderAdapter(OrderActivity.this, orders);
                    orderRecyclerView.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
                    orderRecyclerView.setAdapter(adapter);
                    Log.d("OrderActivity", "Orders received: " + orders.size());
                } else {
                    Toast.makeText(OrderActivity.this, "Error fetching orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderApiResponse> call, Throwable t) {
                Toast.makeText(OrderActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}