package com.example.product_sale_app.ui.payment;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import com.example.product_sale_app.ui.home.HomeActivity;

import androidx.appcompat.app.AppCompatActivity;

import com.example.product_sale_app.R;
import com.example.product_sale_app.ui.order.OrderActivity;

public class PaymentResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);

        TextView txtResultTitle = findViewById(R.id.txt_resultTitle);
        TextView txtResultMessage = findViewById(R.id.txt_resultMessage);
        ImageView imgResultIcon = findViewById(R.id.img_resultIcon);
        TextView txtOrderId = findViewById(R.id.txt_orderId);
        TextView txtPaymentTime = findViewById(R.id.txt_paymentTime);
        Button btnBackHome = findViewById(R.id.btn_backHome);
        Button btnViewOrders = findViewById(R.id.btn_viewOrders);

        // Get intent extras
        String status = getIntent().getStringExtra("status");
        String message = getIntent().getStringExtra("message");
        int orderId = getIntent().getIntExtra("orderId", 0);
        String time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
                .format(new java.util.Date());

        txtResultTitle.setText(status.equals("success") ? "Payment Successful" : "Payment Failed");
        imgResultIcon.setImageResource(status.equals("success") ? R.drawable.ic_success : R.drawable.ic_failure);
        txtResultMessage.setText(message);
        txtOrderId.setText("Order ID: " + orderId);
        txtPaymentTime.setText("Time: " + time);

        // Button actions
        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentResultActivity.this, HomeActivity.class); // replace with your actual home activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnViewOrders.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentResultActivity.this, OrderActivity.class); // replace with your order screen
            startActivity(intent);
        });

    }
}