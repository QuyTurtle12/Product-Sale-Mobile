package com.example.product_sale_app.ui.product;

import android.content.Intent;
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
import com.example.product_sale_app.model.product.Product;

import java.math.BigDecimal;
import java.util.List;

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
            Toast.makeText(this, "Không tìm thấy sản phẩm!", Toast.LENGTH_SHORT).show();
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
        moreButton.setOnClickListener(v -> Toast.makeText(this, "Tùy chọn khác", Toast.LENGTH_SHORT).show());
        addToCartButton.setOnClickListener(v -> {
            Toast.makeText(this, (product.getProductName() != null ? product.getProductName() : "Sản phẩm") + " đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
        });
        chatButton.setOnClickListener(v -> Toast.makeText(this, "Mở chat", Toast.LENGTH_SHORT).show());
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
}