package com.example.product_sale_app.ui.payment;

import static com.example.product_sale_app.ui.home.LoginActivity.PREFS_NAME;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.product_sale_app.R;
import com.example.product_sale_app.adapter.CartCheckOutAdapter;
import com.example.product_sale_app.model.StoreLocationDto;
import com.example.product_sale_app.model.cart.CartHolder;
import com.example.product_sale_app.model.cart.CartItemDTO;
import com.example.product_sale_app.model.order.OrderPostDTO;
import com.example.product_sale_app.model.order.OrderPostResponseDTO;
import com.example.product_sale_app.model.payment.PaymentPostDTO;
import com.example.product_sale_app.model.payment.PaymentStatusResponse;
import com.example.product_sale_app.model.payment.VNPayPaymentResponse;
import com.example.product_sale_app.model.BaseResponseModel;
import com.example.product_sale_app.network.RetrofitClient;
import com.example.product_sale_app.network.service.OrderApiService;
import com.example.product_sale_app.network.service.PaymentApiService;
import com.example.product_sale_app.network.service.StoreApiService;
import com.example.product_sale_app.ui.cart.CartActivity;
import com.example.product_sale_app.ui.chat.ChatActivity;
import com.example.product_sale_app.ui.home.HomeActivity;
import com.example.product_sale_app.ui.home.LoginActivity;
import com.example.product_sale_app.ui.order.OrderActivity;
import com.google.android.material.navigation.NavigationView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartCheckOutActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView txtTotal;
    private EditText txtShippingAddress;
    private RadioGroup shippingMethodGroup;
    private Spinner spinnerStoreLocation;
    private List<StoreLocationDto> storeLocations = new ArrayList<>();
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

        // Function below top bar
        onCreateCurrentPageBar();

        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "No cart items found", Toast.LENGTH_SHORT).show();
            return;
        }

        // When users choose Store Pick up, the address field disappear
        shippingMethodGroup = findViewById(R.id.shippingMethodGroup);
        txtShippingAddress = findViewById(R.id.txt_shipping_address);
        // Store location option appear
        spinnerStoreLocation = findViewById(R.id.spinnerStoreLocation);

//        shippingMethodGroup.setOnCheckedChangeListener((group, checkedId) -> {
//            if (checkedId == R.id.radioPickup) {
//                txtShippingAddress.setVisibility(View.GONE);
//                txtShippingAddress.setText("");
//            } else {
//                txtShippingAddress.setVisibility(View.VISIBLE);
//            }
//        });
        setupShippingMethodListener();
        fetchStoreLocations();


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
            placeVNPayOrder();
        });


        // Setup user profile click
        setupUserProfileClick();

        // Remain function on Top Bar
        onCreateHomeTitleArea();

        // Navigation Function
        onCreateNavigationBar();

    }

    private boolean shouldCheckPayment = false;
    private int currentOrderId = 0;

    @Override
    protected void onResume() {
        super.onResume();

        if (shouldCheckPayment && currentOrderId > 0) {
            checkPaymentStatus(currentOrderId);
            shouldCheckPayment = false; // reset so it doesn't run again accidentally
        }
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

    private void payInVNPayPayment(int orderId) {
        PaymentApiService api = RetrofitClient.getRetrofitInstance().create(PaymentApiService.class);

        Call<VNPayPaymentResponse> call = api.createPayment(orderId);
        call.enqueue(new Callback<VNPayPaymentResponse>() {
            @Override
            public void onResponse(Call<VNPayPaymentResponse> call, Response<VNPayPaymentResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    String paymentUrl = response.body().getData();

                    // Open in browser
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
                    startActivity(browserIntent);

                    
                } else {
                    Toast.makeText(CartCheckOutActivity.this, "Failed to get VNPay URL", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VNPayPaymentResponse> call, Throwable t) {
                Toast.makeText(CartCheckOutActivity.this, "API call error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void placeVNPayOrder() {
        int userId = getIntent().getIntExtra("userId", 0);
        int cartId = getIntent().getIntExtra("cartId", 0);

        String billingAddress = getValidatedBillingAddress();
        if (billingAddress == null) return;

        OrderPostDTO order = new OrderPostDTO(
                cartId,
                userId,
                "VNPay",
                billingAddress,
                "Pending"
        );

        OrderApiService orderService = RetrofitClient.createService(this, OrderApiService.class);
        Call<OrderPostResponseDTO> call = orderService.createOrder(order);
        call.enqueue(new Callback<OrderPostResponseDTO>() {
            @Override
            public void onResponse(Call<OrderPostResponseDTO> call, Response<OrderPostResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int orderId = response.body().getData();
                    payInVNPayPayment(orderId);  // Call VNPay API after order created
                } else {
                    Toast.makeText(CartCheckOutActivity.this, "Order creation failed", Toast.LENGTH_SHORT).show();
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
            // billingAddress = shippingMethod;
            int selectedPosition = spinnerStoreLocation.getSelectedItemPosition();
            if (selectedPosition < 0 || storeLocations == null || storeLocations.isEmpty()) {
                Toast.makeText(this, "Please select a store location", Toast.LENGTH_SHORT).show();
                return null;
            }

            StoreLocationDto selectedStore = storeLocations.get(selectedPosition);
            billingAddress = shippingMethod + ": " + selectedStore.getAddress();
        }

        return billingAddress;
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
                Intent intent = new Intent(CartCheckOutActivity.this, LoginActivity.class);
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
                Intent intent = new Intent(CartCheckOutActivity.this, LoginActivity.class);
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
                Intent intent = new Intent(CartCheckOutActivity.this, HomeActivity.class);
                startActivity(intent);
                // finish();
            }
        });

        ImageView cartButton = findViewById(R.id.cart_icon);

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartCheckOutActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onCreateCurrentPageBar(){

        ImageView previousPage = findViewById(R.id.btn_back);

        previousPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(CartCheckOutActivity.this, CartActivity.class);
//                startActivity(intent);
                finish();
            }
        });
    }

    private void setupShippingMethodListener() {
        shippingMethodGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioPickup) {
                spinnerStoreLocation.setVisibility(View.VISIBLE);
                txtShippingAddress.setVisibility(View.GONE);
                txtShippingAddress.setText("");
            } else {
                spinnerStoreLocation.setVisibility(View.GONE);
                txtShippingAddress.setVisibility(View.VISIBLE);
            }
        });
    }


    private void fetchStoreLocations() {
        StoreApiService storeApi = RetrofitClient.createService(this, StoreApiService.class);
        storeApi.getAllLocations().enqueue(new Callback<BaseResponseModel<List<StoreLocationDto>>>() {
            @Override
            public void onResponse(Call<BaseResponseModel<List<StoreLocationDto>>> call, Response<BaseResponseModel<List<StoreLocationDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    storeLocations = response.body().getData();
                    populateStoreSpinner();
                } else {
                    Toast.makeText(CartCheckOutActivity.this, "Không thể lấy danh sách cửa hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponseModel<List<StoreLocationDto>>> call, Throwable t) {
                Toast.makeText(CartCheckOutActivity.this, "Lỗi khi lấy vị trí cửa hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateStoreSpinner() {
        List<String> storeNames = new ArrayList<>();
        for (StoreLocationDto store : storeLocations) {
            storeNames.add(store.getAddress()); // Or a combination of address + id
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, storeNames);
        spinnerStoreLocation.setAdapter(adapter);
    }


    private void onCreateNavigationBar(){

        // Home icon
        LinearLayout homeButton = findViewById(R.id.nav_home_button);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartCheckOutActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        // Chat icon
        LinearLayout chatButton = findViewById(R.id.nav_chat_button);

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartCheckOutActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout orderButton = findViewById(R.id.nav_order_button);

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartCheckOutActivity.this, OrderActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkPaymentStatus(int orderId) {
        PaymentApiService api = RetrofitClient.getRetrofitInstance().create(PaymentApiService.class);
        Call<PaymentStatusResponse> call = api.checkPaymentStatus(orderId);

        call.enqueue(new Callback<PaymentStatusResponse>() {
            @Override
            public void onResponse(Call<PaymentStatusResponse> call, Response<PaymentStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String status = response.body().getStatus();
                    if ("Paid".equalsIgnoreCase(status)) {
                        Intent intent = new Intent(CartCheckOutActivity.this, PaymentResultActivity.class);
                        intent.putExtra("status", "success");
                        intent.putExtra("message", "Payment successful");
                        intent.putExtra("orderId", orderId);
                        startActivity(intent);
                    } else {
                        Toast.makeText(CartCheckOutActivity.this, "Payment status: " + status, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CartCheckOutActivity.this, "Could not check payment status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentStatusResponse> call, Throwable t) {
                Toast.makeText(CartCheckOutActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}


