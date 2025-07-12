package com.example.product_sale_app.ui.order;

import static com.example.product_sale_app.ui.home.LoginActivity.PREFS_NAME;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.example.product_sale_app.ui.chat.ChatActivity;
import com.example.product_sale_app.ui.home.HomeActivity;
import com.example.product_sale_app.ui.home.LoginActivity;
import com.example.product_sale_app.ui.product.ProductActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {
    private RecyclerView orderRecyclerView;
    private int currentPage = 1;
    private int totalPages = 1;
    private int pageSize = 10;
    // private boolean isLastPage = false;
    private TextView pageIndicator;
    private Button btnNext, btnPrevious;
    private LinearLayout btnProduct;


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

        fetchOrdersFromApi();

        // Remain function on Top Bar
        onCreateHomeTitleArea();

        // Function below top bar
        onCreateCurrentPageBar();

        // Pagination
        OnCreatePagination();

        // Navigation Function
        onCreateNavigationBar();
    }


    private void fetchOrdersFromApi() {
        OrderApiService apiService = RetrofitClient.createService(this, OrderApiService.class);

//        Call<OrderApiResponse> call = apiService.getOrders(1, 10, null
//                ,null, null,null,null,
//                null,null,null,null, true);

        Call<OrderApiResponse> call = apiService.getOrders(currentPage, pageSize, null
                ,null, null,null,null,
                null,null,null,null, true);

        call.enqueue(new Callback<OrderApiResponse>() {
            @Override
            public void onResponse(Call<OrderApiResponse> call, Response<OrderApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderApiResponse body = response.body();
                    // get List
                    List<OrderDTO> orders = body.getData().getItems();
                    // set Last page for pagination
                    // isLastPage = !body.getData().isHasNextPage();
                    totalPages = response.body().getData().getTotalPages();
                    currentPage = response.body().getData().getPageNumber();
                    // Set up RecyclerView
                    OrderAdapter adapter = new OrderAdapter(OrderActivity.this, orders);
                    orderRecyclerView.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
                    orderRecyclerView.setAdapter(adapter);

                    pageIndicator.setText("Page " + currentPage + " of " + totalPages);

                    btnPrevious.setEnabled(currentPage > 1);
                    btnNext.setEnabled(currentPage < totalPages);
                    Log.d("OrderActivity", "Orders received: " + orders.size());
                } else {
                    Toast.makeText(OrderActivity.this, "You have to login to view order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderApiResponse> call, Throwable t) {
                Toast.makeText(OrderActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupUserProfileClick() {
        ImageView userProfile = findViewById(R.id.user_icon);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        userProfile.setOnClickListener(v -> {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String token = settings.getString("token", null);

            if (token == null) {
                // Not logged in - go to login
                Intent intent = new Intent(OrderActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                // Show drawer
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Logout navigation
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_logout) {
                // Clear preferences
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                settings.edit().clear().apply();

                // Redirect to login
                Intent intent = new Intent(OrderActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
    private void onCreateHomeTitleArea(){

        TextView homeTextView = findViewById(R.id.home_title_text);

        homeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this, HomeActivity.class);
                startActivity(intent);
                // finish();
            }
        });

        ImageView cartButton = findViewById(R.id.cart_icon);

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onCreateCurrentPageBar(){

        ImageView previousPage = findViewById(R.id.btn_back);

        previousPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
//                startActivity(intent);
                finish();
            }
        });
    }

    private void OnCreatePagination(){

        // TextView txtPageIndex = findViewById(R.id.txtPageIndex);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious  = findViewById(R.id.btnPrevious);
        pageIndicator = findViewById(R.id.pageIndicator);

        //        btnNext.setOnClickListener(v -> {
//            if (!isLastPage) {
//                currentPage++;
//                fetchOrdersFromApi();
//                txtPageIndex.setText("Page " + currentPage);
//            }
//        });
//
//        btnPrevious.setOnClickListener(v -> {
//            if (currentPage > 1) {
//                currentPage--;
//                fetchOrdersFromApi();
//                txtPageIndex.setText("Page " + currentPage);
//            }
//        });

        btnPrevious.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                fetchOrdersFromApi();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                fetchOrdersFromApi();
            }
        });

    }

    private void onCreateNavigationBar(){

        // Home icon
        LinearLayout homeButton = findViewById(R.id.nav_home_button);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        // Chat icon
        LinearLayout chatButton = findViewById(R.id.nav_chat_button);

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        // Product icon
        btnProduct = findViewById(R.id.nav_product_button);

        btnProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this, ProductActivity.class);
                startActivity(intent);
            }
        });
    }


}