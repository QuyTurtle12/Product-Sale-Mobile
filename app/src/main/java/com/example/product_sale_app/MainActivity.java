package com.example.product_sale_app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.product_sale_app.ui.chat.ChatListActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Kick straight into your chat list
        startActivity(new Intent(this, ChatListActivity.class));
        finish();
    }
}
