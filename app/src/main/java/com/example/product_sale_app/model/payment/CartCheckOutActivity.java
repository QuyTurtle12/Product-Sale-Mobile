package com.example.product_sale_app.model.payment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.product_sale_app.R;
import com.example.product_sale_app.adapter.CartCheckOutAdapter;
import com.example.product_sale_app.model.cart.CartHolder;
import com.example.product_sale_app.model.cart.CartItemDTO;

import java.math.BigDecimal;
import java.util.List;

public class CartCheckOutActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView txtTotal;
    private EditText txtShippingAddress;
    private RadioGroup shippingMethodGroup;
    private Button btnPayInCash, btnVNPay;

    private List<CartItemDTO> cartItems;
    private CartCheckOutAdapter adapter;
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_check_out);

        recyclerView = findViewById(R.id.checkoutRecyclerView);
        txtTotal = findViewById(R.id.txt_total_checkout);
        txtShippingAddress = findViewById(R.id.txt_shipping_address);
        shippingMethodGroup = findViewById(R.id.shippingMethodGroup);
        btnPayInCash = findViewById(R.id.btn_payInCash);
        btnVNPay = findViewById(R.id.btn_VNPay);

        // TODO: Replace this with actual data from cart
        cartItems = CartHolder.getItems(); // You can create a singleton or ViewModel to hold data between activities
        Log.d("CartCheckOutActivity", "Received cart items count: " + (cartItems == null ? 0 : cartItems.size()));

        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "No cart items found", Toast.LENGTH_SHORT).show();
            return;
        }

        adapter = new CartCheckOutAdapter(cartItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        Log.d("CartCheckOutActivity", "Adapter item count: " + adapter.getItemCount());

        calculateTotal();

        btnPayInCash.setOnClickListener(v -> {
            Toast.makeText(this, "Pay in Cash selected", Toast.LENGTH_SHORT).show();
            // handle logic here
        });

        btnVNPay.setOnClickListener(v -> {
            Toast.makeText(this, "VNPay selected", Toast.LENGTH_SHORT).show();
            // handle VNPay logic here
        });
    }

    private void calculateTotal() {
        totalPrice = BigDecimal.ZERO;
        for (CartItemDTO item : cartItems) {
            totalPrice = totalPrice.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        txtTotal.setText(String.format("%,.0f₫", totalPrice));
    }
}