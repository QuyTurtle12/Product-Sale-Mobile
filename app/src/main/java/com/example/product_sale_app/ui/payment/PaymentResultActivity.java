package com.example.product_sale_app.ui.payment;

import static com.example.product_sale_app.ui.home.LoginActivity.PREFS_NAME;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;

import com.example.product_sale_app.ui.cart.CartActivity;
import com.example.product_sale_app.ui.chat.ChatActivity;
import com.example.product_sale_app.ui.chat.ChatListActivity;
import com.example.product_sale_app.ui.home.HomeActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.product_sale_app.R;
import com.example.product_sale_app.ui.home.LoginActivity;
import com.example.product_sale_app.ui.order.OrderActivity;
import com.example.product_sale_app.ui.product.ProductActivity;
import com.google.android.material.navigation.NavigationView;

public class PaymentResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);

        // ✅ Handle deep link data if opened from a custom scheme
        Intent intent = getIntent();
        Uri data = intent.getData();
        String status = null;
        String message = null;
        int orderId = 0;

        if (data != null) {
            // If opened via deep link (myapp://paymentresult?orderId=123&status=success)
            String orderIdParam = data.getQueryParameter("orderId");
            String statusParam = data.getQueryParameter("status");
            orderId = orderIdParam != null ? Integer.parseInt(orderIdParam) : 0;
            status = statusParam != null ? statusParam : "failed";
            message = "Returned from VNPay"; // Or parse more params if you pass them
        } else {
            // Otherwise, use extras passed directly from another activity
            status = getIntent().getStringExtra("status");
            message = getIntent().getStringExtra("message");
            orderId = getIntent().getIntExtra("orderId", 0);
        }

        // After extracting data, update UI
        TextView txtResultTitle = findViewById(R.id.txt_resultTitle);
        TextView txtResultMessage = findViewById(R.id.txt_resultMessage);
        ImageView imgResultIcon = findViewById(R.id.img_resultIcon);
        TextView txtOrderId = findViewById(R.id.txt_orderId);
        TextView txtPaymentTime = findViewById(R.id.txt_paymentTime);
        Button btnBackHome = findViewById(R.id.btn_backHome);
        Button btnViewOrders = findViewById(R.id.btn_viewOrders);

        // Get intent extras
//        String status = getIntent().getStringExtra("status");
//        String message = getIntent().getStringExtra("message");
//        int orderId = getIntent().getIntExtra("orderId", 0);
        String time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
                .format(new java.util.Date());

        txtResultTitle.setText(status.equals("success") ? "Payment Successful" : "Payment Failed");
        imgResultIcon.setImageResource(status.equals("success") ? R.drawable.ic_success : R.drawable.ic_failure);
        txtResultMessage.setText(message);
        txtOrderId.setText("Order ID: " + orderId);
        txtPaymentTime.setText("Time: " + time);

        // Button actions
        btnBackHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(PaymentResultActivity.this, HomeActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
        });

        btnViewOrders.setOnClickListener(v -> {
            Intent orderIntent = new Intent(PaymentResultActivity.this, OrderActivity.class);
            startActivity(orderIntent);
            finish();
        });

//        // Setup user profile click
        setupUserProfileClick();
//
//        // Remain function on Top Bar
        onCreateHomeTitleArea();
//
//        // Navigation Function
        onCreateNavigationBar();

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
                Intent intent = new Intent(PaymentResultActivity.this, LoginActivity.class);
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
                Intent intent = new Intent(PaymentResultActivity.this, LoginActivity.class);
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
                Intent intent = new Intent(PaymentResultActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void onCreateNavigationBar(){

        // Home icon
        LinearLayout homeButton = findViewById(R.id.nav_home_button);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentResultActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Chat icon
        LinearLayout chatButton = findViewById(R.id.nav_chat_button);

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentResultActivity.this, ChatListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        LinearLayout orderButton = findViewById(R.id.nav_order_button);

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentResultActivity.this, OrderActivity.class);
                startActivity(intent);
                finish();
            }
        });

        LinearLayout productButton = findViewById(R.id.nav_product_button);

        productButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentResultActivity.this, ProductActivity.class);
                startActivity(intent);
            }
        });

    }


}