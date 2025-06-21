package com.example.product_sale_app.ui.product;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.product_sale_app.R;
import com.example.product_sale_app.model.home_product.Product;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ImageButton backButton = findViewById(R.id.back_button);
        ImageButton moreButton = findViewById(R.id.more_button);
        ImageView productImage = findViewById(R.id.product_image);
        TextView productName = findViewById(R.id.product_name);
        TextView productPrice = findViewById(R.id.product_price);
        TextView productDescription = findViewById(R.id.product_description);
        TextView productSpecification = findViewById(R.id.product_specification);
        Button addToCartButton = findViewById(R.id.add_to_cart_button);
        Button chatButton = findViewById(R.id.chat_button);

        Product product = getIntent().getParcelableExtra("product");
        if (product == null) {
            Toast.makeText(this, "No product found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ✅ Load ảnh từ URL hoặc tên file (drawable)
        String imageUrl = product.getImageUrl();
        int imageResId = getResources().getIdentifier(imageUrl, "drawable", getPackageName());
        if (imageResId != 0) {
            Glide.with(this).load(imageResId).into(productImage);
        } else {
            Glide.with(this).load(imageUrl).into(productImage);
        }
        productName.setText("Product Name: " + product.getProductName());
        productPrice.setText(String.format("Price: %,dđ", product.getPrice()));
        productDescription.setText("Describe: " + product.getFullDescription());
        productSpecification.setText("Specification: " + product.getTechnicalSpecifications());

        backButton.setOnClickListener(v -> finish());
        moreButton.setOnClickListener(v ->
                Toast.makeText(this, "Order choice", Toast.LENGTH_SHORT).show()
        );
        addToCartButton.setOnClickListener(v -> {
            Toast.makeText(this, product.getProductName() + " add to cart successfully", Toast.LENGTH_SHORT).show();
        });
        chatButton.setOnClickListener(v ->
                Toast.makeText(this, "Open chat", Toast.LENGTH_SHORT).show()
        );
    }
}