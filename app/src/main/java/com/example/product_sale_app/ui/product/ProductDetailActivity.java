package com.example.product_sale_app.ui.product;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.product_sale_app.R;
import com.example.product_sale_app.model.cart.CartApiResponse;
import com.example.product_sale_app.model.cart.CartDTO;
import com.example.product_sale_app.model.cart.CartItemDTO;
import com.example.product_sale_app.model.cart.CartUpdateDTO;
import com.example.product_sale_app.model.cart_item.CartItemAddDTO;
import com.example.product_sale_app.model.cart_item.CartItemUpdateDTO;
import com.example.product_sale_app.model.product.Product;
import com.example.product_sale_app.network.RetrofitClient;
import com.example.product_sale_app.network.service.CartApiService;
import com.example.product_sale_app.ui.chat.ChatListActivity;
import com.example.product_sale_app.ui.home.HomeActivity;
import com.example.product_sale_app.ui.home.LoginActivity;

import java.math.BigDecimal;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
    private static final String TAG = "ProductDetailActivity";

    private ImageButton backButton;
    private ImageButton moreButton;
    private ViewPager productImagePager;
    private TextView productName;
    private TextView productPrice;
    private TextView productDescription;
    private TextView productSpecification;
    private Button addToCartButton;
    private Button chatButton;
    private ImagePagerAdapter imagePagerAdapter;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Khởi tạo các thành phần UI
        backButton = findViewById(R.id.back_button);
        moreButton = findViewById(R.id.more_button);
        productImagePager = findViewById(R.id.product_image_pager);
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        productDescription = findViewById(R.id.product_description);
        productSpecification = findViewById(R.id.product_specification);
        addToCartButton = findViewById(R.id.add_to_cart_button);
        chatButton = findViewById(R.id.chat_button);

        // Lấy dữ liệu từ Intent
        product = getIntent().getParcelableExtra("product");
        if (product == null) {
            Log.e(TAG, "Product is null from Intent");
            Toast.makeText(this, "No products found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cấu hình ViewPager cho nhiều ảnh
        setupImagePager();

        // Hiển thị thông tin sản phẩm
        updateUI();

        // Xử lý sự kiện
        setupEventListeners();
    }

    private void setupImagePager() {
        List<String> imageUrls = product.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            imagePagerAdapter = new ImagePagerAdapter(this, imageUrls);
            productImagePager.setAdapter(imagePagerAdapter);
        } else {
            Log.w(TAG, "No imageUrls available, using placeholder");
            imagePagerAdapter = new ImagePagerAdapter(this, List.of("https://via.placeholder.com/150"));
            productImagePager.setAdapter(imagePagerAdapter);
        }
    }

    private void updateUI() {
        productName.setText("Name: " + (product.getProductName() != null ? product.getProductName() : "N/A"));
        productPrice.setText("Price: " + (product.getPrice() != null ? String.format("%,dđ", product.getPrice().intValue()) : "N/A"));
        productDescription.setText("Description: " + (product.getBriefDescription() != null ? product.getBriefDescription() : "N/A"));
        productSpecification.setText("Specs: " + (product.getTechnicalSpecifications() != null ? product.getTechnicalSpecifications() : "N/A"));
    }

    private void setupEventListeners() {
        backButton.setOnClickListener(v -> {
            Log.d(TAG, "Back button clicked, navigating to ProductActivity");
            Intent intent = new Intent(ProductDetailActivity.this, ProductActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Đảm bảo quay về ProductActivity
            startActivity(intent);
            finish(); // Đóng activity hiện tại
        });
        moreButton.setOnClickListener(v -> Toast.makeText(this, "Other options", Toast.LENGTH_SHORT).show());

        addToCartButton.setOnClickListener(v -> {

            // check login
            if (!isUserLoggedIn()) {
                Toast.makeText(this, "Please login to able to add products to cart!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProductDetailActivity.this, LoginActivity.class);
                startActivity(intent);
                return;
            }

            // save product to cart
            Toast.makeText(this, (product.getProductName() != null ? product.getProductName() : "Product") + " added to cart!", Toast.LENGTH_SHORT).show();
            getLatestCartAndAddProduct();
        });

//        chatButton.setOnClickListener(v -> Toast.makeText(this, "Mở chat", Toast.LENGTH_SHORT).show());
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, ChatListActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imagePagerAdapter != null) {
            imagePagerAdapter = null;
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back pressed, navigating to ProductActivity");
        Intent intent = new Intent(ProductDetailActivity.this, ProductActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        // super.onBackPressed(); // Bỏ qua để dùng Intent điều hướng thủ công
    }

    // Lay gio hang moi nhat cua khach hang va them vao
    private void getLatestCartAndAddProduct() {
        CartApiService apiService = RetrofitClient.createService(this, CartApiService.class);
        Call<CartApiResponse> call = apiService.getPaginatedCarts(1, 10, null, null, null, true);

        call.enqueue(new Callback<CartApiResponse>() {
            @Override
            public void onResponse(Call<CartApiResponse> call, Response<CartApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getData().getItems().isEmpty()) {
                    CartDTO latestCart = response.body().getData().getItems().get(0);
                    int cartId = latestCart.getCartId();

                    // Check if product already exists
                    for (CartItemDTO item : latestCart.getCartItems()) {
                        if (item.getProductId() == product.getProductId()) {
                            // Update quantity (increase by 1 for now)
                            int newQuantity = item.getQuantity() + 1;
                            updateCartItemQuantity(item.getCartItemId(), cartId, product.getProductId(), newQuantity);
                            return;
                        }
                    }

                    // If product not found in cart, add it
                    addProductToCart(cartId, product.getProductId(), 1);
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Need to login", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartApiResponse> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Them vao gio hang
    private void addProductToCart(int cartId, int productId, int quantity) {
        CartApiService apiService = RetrofitClient.createService(this, CartApiService.class);
        CartItemAddDTO request = new CartItemAddDTO(cartId, productId, quantity);

        Call<Void> call = apiService.addCartItem(request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductDetailActivity.this, "Product added to cart", Toast.LENGTH_SHORT).show();

                    // Update total price
                    updateCartTotalPrice(cartId, 0);
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Add to cart failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Connection error when adding product", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Cap nhap gio hang neu san pham co trong cart
    private void updateCartItemQuantity(int cartItemId, int cartId, int productId, int newQuantity) {
        CartApiService apiService = RetrofitClient.createService(this, CartApiService.class);
        CartItemUpdateDTO request = new CartItemUpdateDTO(cartItemId, productId, cartId, newQuantity);

        apiService.updateCartItem(cartItemId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductDetailActivity.this, "Product quantity updated", Toast.LENGTH_SHORT).show();

                    // Update total price
                    updateCartTotalPrice(cartId, 0);
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Connection error while updating", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCartTotalPrice(int cartId, int userId) {
        CartApiService apiService = RetrofitClient.createService(this, CartApiService.class);
        CartUpdateDTO updateDTO = new CartUpdateDTO(userId);

        apiService.updateCartTotalPrice(cartId, updateDTO).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Update total price successfully");
                } else {
                    Log.w(TAG, "Total price update failed");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error updating total price: " + t.getMessage());
            }
        });
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString("token", null);
        return token != null && !token.isEmpty();
    }


}