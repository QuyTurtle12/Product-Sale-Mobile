package com.example.product_sale_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.product_sale_app.model.Product;

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
            Toast.makeText(this, "Không tìm thấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Glide.with(this).load(product.getImageUrl()).into(productImage);
        productName.setText("Tên sản phẩm: " + product.getProductName());
        productPrice.setText(String.format("Giá: %,dđ", product.getPrice()));
        productDescription.setText("Mô tả: " + product.getFullDescription());
        productSpecification.setText("Thông số kỹ thuật: " + product.getTechnicalSpecifications());

        backButton.setOnClickListener(v -> finish());
        moreButton.setOnClickListener(v ->
                Toast.makeText(this, "Tùy chọn khác", Toast.LENGTH_SHORT).show()
        );
        addToCartButton.setOnClickListener(v -> {
            Toast.makeText(this, product.getProductName() + " đã được thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });
        chatButton.setOnClickListener(v ->
                Toast.makeText(this, "Mở giao diện chat", Toast.LENGTH_SHORT).show()
        );
    }
}