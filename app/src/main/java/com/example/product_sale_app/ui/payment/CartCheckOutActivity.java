package com.example.product_sale_app.ui.payment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.product_sale_app.R;
import com.example.product_sale_app.adapter.CartCheckOutAdapter;
import com.example.product_sale_app.model.cart.CartHolder;
import com.example.product_sale_app.model.cart.CartItemDTO;
import com.example.product_sale_app.model.order.OrderPostDTO;
import com.example.product_sale_app.model.order.OrderPostResponseDTO;
import com.example.product_sale_app.model.payment.PaymentPostDTO;
import com.example.product_sale_app.network.RetrofitClient;
import com.example.product_sale_app.network.service.OrderApiService;
import com.example.product_sale_app.network.service.PaymentApiService;
import com.example.product_sale_app.ui.cart.CartActivity;
import com.example.product_sale_app.ui.home.HomeActivity;

import java.math.BigDecimal;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartCheckOutActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView txtTotal;
    private EditText txtShippingAddress;
    private RadioGroup shippingMethodGroup;
    private Button btnPayInCash, btnVNPay;

    private List<CartItemDTO> cartItems;
    private CartCheckOutAdapter adapter;
    private BigDecimal totalPrice = BigDecimal.ZERO;

    private ImageView backPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_check_out);

        recyclerView = findViewById(R.id.checkoutRecyclerView);
        txtTotal = findViewById(R.id.txt_total_checkout);
//        txtShippingAddress = findViewById(R.id.txt_shipping_address);
//        shippingMethodGroup = findViewById(R.id.shippingMethodGroup);
        btnPayInCash = findViewById(R.id.btn_payInCash);
        btnVNPay = findViewById(R.id.btn_VNPay);

        cartItems = CartHolder.getItems(); // You can create a singleton or ViewModel to hold data between activities
        Log.d("CartCheckOutActivity", "Received cart items count: " + (cartItems == null ? 0 : cartItems.size()));

        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "No cart items found", Toast.LENGTH_SHORT).show();
            return;
        }

        // When users choose Store Pick up, the address field disappear
        shippingMethodGroup = findViewById(R.id.shippingMethodGroup);
        txtShippingAddress = findViewById(R.id.txt_shipping_address);

        shippingMethodGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioPickup) {
                txtShippingAddress.setVisibility(View.GONE);
                txtShippingAddress.setText("");
            } else {
                txtShippingAddress.setVisibility(View.VISIBLE);
            }
        });

        // Handle Back Page
        backPage = findViewById(R.id.btn_back);
        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartCheckOutActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        // Handle Payment
        adapter = new CartCheckOutAdapter(cartItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        Log.d("CartCheckOutActivity", "Adapter item count: " + adapter.getItemCount());

        calculateTotal();

        btnPayInCash.setOnClickListener(v -> {
            // Toast.makeText(this, "Pay in Cash selected", Toast.LENGTH_SHORT).show();
            // handle logic here
            placeCashOrder();

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


    private void placeCashOrder() {
        int userId = getIntent().getIntExtra("userId", 0);
        int cartId = getIntent().getIntExtra("cartId", 0);

        // Handle save Billing Address to database
        String billingAddress = getValidatedBillingAddress();
        if (billingAddress == null) return; // Stop if invalid

        // String billingAddress = selectedShippingMethod + ": " + txtShippingAddress.getText().toString();

        OrderPostDTO order = new OrderPostDTO(
                cartId,
                userId,
                "Cash On Deliver",
                billingAddress,
                "Pending"
        );

        OrderApiService orderService = RetrofitClient.createService(this, OrderApiService.class);
        PaymentApiService paymentService = RetrofitClient.getRetrofitInstance().create(PaymentApiService.class);

        Log.d("CartCheckOutActivity", "Creating order with cartId=" + cartId + " and userId=" + userId);

        Call<OrderPostResponseDTO> call = orderService.createOrder(order);
        call.enqueue(new Callback<OrderPostResponseDTO>() {
            @Override
            public void onResponse(Call<OrderPostResponseDTO> call, Response<OrderPostResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int orderId = response.body().getData();

                    PaymentPostDTO payment = new PaymentPostDTO(orderId, "Pending");

                    Log.d("CartCheckOutActivity", "Sending payment for orderId: " + orderId);
                    paymentService.createPayment(payment).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Log.d("CartCheckOutActivity", "Payment response code: " + response.code());
                            if (response.isSuccessful()) {
                                // Toast.makeText(CartCheckOutActivity.this, "Order & payment successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CartCheckOutActivity.this, PaymentResultActivity.class);
                                intent.putExtra("status", "success");
                                intent.putExtra("message", "Your order was placed successfully.");
                                intent.putExtra("orderId", orderId); // Optional: pass orderId
                                startActivity(intent);
                                finish(); // Optional: close checkout screen

                            } else {
                                Toast.makeText(CartCheckOutActivity.this, "Payment failed, code: " + response.code(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CartCheckOutActivity.this, PaymentResultActivity.class);
                                intent.putExtra("status", "failure");
                                intent.putExtra("message", "Something went wrong while processing your payment.");
                                startActivity(intent);
                                finish();

                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            // Log.e("CartCheckOutActivity", "Payment failed: " + t.getMessage(), t);
                            // Toast.makeText(CartCheckOutActivity.this, "Payment failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CartCheckOutActivity.this, PaymentResultActivity.class);
                            intent.putExtra("status", "failure");
                            intent.putExtra("message", "Something went wrong while processing your payment.");
                            startActivity(intent);
                            finish();

                        }
                    });
                } else {
                    Toast.makeText(CartCheckOutActivity.this, "Order failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderPostResponseDTO> call, Throwable t) {
                Toast.makeText(CartCheckOutActivity.this, "Order error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private String getValidatedBillingAddress() {
        int selectedId = shippingMethodGroup.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(this, "Please select a shipping method", Toast.LENGTH_SHORT).show();
            return null;
        }

        String shippingMethod = "";
        String billingAddress = "";

        if (selectedId == R.id.radioStandardShipping) {
            shippingMethod = "Standard";
            String address = txtShippingAddress.getText().toString().trim();
            if (address.isEmpty()) {
                txtShippingAddress.setError("Address is required for Standard shipping");
                txtShippingAddress.requestFocus();
                return null;
            }
            billingAddress = shippingMethod + ": " + address;
        } else if (selectedId == R.id.radioExpressShipping) {
            shippingMethod = "Express";
            String address = txtShippingAddress.getText().toString().trim();
            if (address.isEmpty()) {
                txtShippingAddress.setError("Address is required for Express shipping");
                txtShippingAddress.requestFocus();
                return null;
            }
            billingAddress = shippingMethod + ": " + address;
        } else {
            // Store Pickup
            shippingMethod = "Store Pickup";
            billingAddress = shippingMethod;
        }

        return billingAddress;
    }



}


