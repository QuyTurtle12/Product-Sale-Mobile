package com.example.product_sale_app.ui.product;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.product_sale_app.R;
import com.example.product_sale_app.model.product.Product;
import com.example.product_sale_app.model.product.ProductApiResponse;
import com.example.product_sale_app.model.product.ProductData;
import com.example.product_sale_app.network.service.ProductApiService;
import com.google.android.material.card.MaterialCardView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;

public class ProductActivity extends AppCompatActivity {
    private static final String TAG = "ProductActivity";

    private EditText searchEditText;
    private GridLayout productGrid;
    private List<Product> productList;
    private Spinner sortSpinner;
    private Button filterButton;
    private int currentPage = 1;
    private int pageSize = 10;
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        ImageButton backButton = findViewById(R.id.back_button);
        ImageButton moreButton = findViewById(R.id.more_button);
        searchEditText = findViewById(R.id.search_edit_text);
        productGrid = findViewById(R.id.product_grid);
        sortSpinner = findViewById(R.id.sort_spinner);
        filterButton = findViewById(R.id.filter_button);

        backButton.setOnClickListener(v -> finish());
        moreButton.setOnClickListener(v -> Toast.makeText(this, "Tùy chọn khác", Toast.LENGTH_SHORT).show());

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applySort(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        filterButton.setOnClickListener(v -> showFilterDialog());

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchEditText.getText().toString().trim();
            loadProducts(currentPage, pageSize, query, null, null, null, null, null, null);
            return true;
        });

        loadProducts(currentPage, pageSize, null, null, null, null, null, null, null);
    }

    private void loadProducts(int pageIndex, int pageSize, String nameSearch, String sortBy, String sortOrder,
                              Integer categoryId, Integer brandId, Integer minPrice, Integer maxPrice) {
        OkHttpClient unsafeClient = getUnsafeOkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://10.0.2.2:7050/api/")
                .client(unsafeClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProductApiService apiService = retrofit.create(ProductApiService.class);
        Call<ProductApiResponse> call = apiService.getPaginatedProducts(pageIndex, pageSize, null, nameSearch,
                sortBy, sortOrder, categoryId, brandId, minPrice, maxPrice, null);

        call.enqueue(new Callback<ProductApiResponse>() {
            @Override
            public void onResponse(Call<ProductApiResponse> call, Response<ProductApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductApiResponse apiResponse = response.body();
                    if ("SUCCESS".equals(apiResponse.getCode()) && apiResponse.getData() != null) {
                        ProductData productData = apiResponse.getData();
                        List<Product> products = productData.getItems();
                        if (products != null && !products.isEmpty()) {
                            productList = new ArrayList<>(products);
                            populateProductGrid();
                        } else {
                            productList = new ArrayList<>();
                            populateProductGrid();
                            Toast.makeText(ProductActivity.this, "Không có sản phẩm.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "API Error: " + apiResponse.getMessage());
                        Toast.makeText(ProductActivity.this, "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "API call failed: " + response.code() + " - " + response.message());
                    Toast.makeText(ProductActivity.this, "Lỗi kết nối: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductApiResponse> call, Throwable t) {
                Log.e(TAG, "Connection failed: " + t.getMessage(), t);
                Toast.makeText(ProductActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                productList = new ArrayList<>();
                productList.add(new Product(1, "Smartphone X2", "High-end smartphone", "A premium smartphone", "4GB RAM, 128GB", new BigDecimal("10000"), List.of("https://via.placeholder.com/150"), "Electronics"));
                productList.add(new Product(2, "Sofa Deluxe", "Luxury 3-seat sofa", "A high-end sofa", "200x90x80 cm", new BigDecimal("20000"), List.of("https://via.placeholder.com/152"), "Furniture"));
                productList.add(new Product(3, "Winter Jacket", "Warm winter wear", "A cozy jacket", "Wool, 200g/m²", new BigDecimal("30000"), List.of("https://via.placeholder.com/154"), "Clothing"));
                populateProductGrid();
            }
        });
    }

    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
                        @Override public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
                        @Override public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[]{}; }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);

            return builder.build();
        } catch (Exception e) {
            Log.e(TAG, "SSL configuration failed: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void populateProductGrid() {
        productGrid.removeAllViews();
        for (Product product : productList) {
            MaterialCardView cardView = new MaterialCardView(this);
            cardView.setRadius(8f);
            cardView.setCardElevation(2f);
            cardView.setUseCompatPadding(true);

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(8, 8, 8, 8);

            ImageView productImage = new ImageView(this);
            productImage.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 100));
            productImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

            List<String> imageUrls = product.getImageUrls();
            String imageUrl = (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.get(0) : "https://via.placeholder.com/150";
            Glide.with(this).load(imageUrl).into(productImage);

            TextView productName = new TextView(this);
            productName.setText(product.getProductName());
            productName.setTextSize(14);
            productName.setTextColor(Color.parseColor("#333333"));

            TextView productPrice = new TextView(this);
            productPrice.setText(currencyFormatter.format(product.getPrice())); // Sử dụng NumberFormat
            productPrice.setTextSize(14);
            productPrice.setTextColor(Color.parseColor("#333333"));

            TextView productDescription = new TextView(this);
            productDescription.setText(product.getBriefDescription());
            productDescription.setTextSize(12);
            productDescription.setTextColor(Color.parseColor("#666666"));

            layout.addView(productImage);
            layout.addView(productName);
            layout.addView(productPrice);
            layout.addView(productDescription);


            cardView.addView(layout);
            cardView.setLayoutParams(new GridLayout.LayoutParams(
                    new ViewGroup.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)));
            ((GridLayout.LayoutParams) cardView.getLayoutParams()).columnSpec =
                    GridLayout.spec(GridLayout.UNDEFINED, 1f);
            ((GridLayout.LayoutParams) cardView.getLayoutParams()).setMargins(8, 8, 8, 8);

            cardView.setOnClickListener(v -> {
                Intent intent = new Intent(ProductActivity.this, ProductDetailActivity.class);
                intent.putExtra("product", product);
                startActivity(intent);
            });

            productGrid.addView(cardView);
        }
    }

    private void applySort(int position) {
        String sortBy = null;
        String sortOrder = null;
        switch (position) {
            case 0: sortBy = "price"; sortOrder = "asc"; break;
            case 1: sortBy = "price"; sortOrder = "desc"; break;
            case 2: sortBy = "popularity"; sortOrder = "desc"; break;
            case 3: sortBy = "category"; sortOrder = "asc"; break;
        }
        loadProducts(1, pageSize, null, sortBy, sortOrder, null, null, null, null);
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lọc sản phẩm");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter, null);
        builder.setView(dialogView);

        EditText minPriceEditText = dialogView.findViewById(R.id.min_price);
        EditText maxPriceEditText = dialogView.findViewById(R.id.max_price);
        Spinner categorySpinner = dialogView.findViewById(R.id.category_spinner);
        Spinner brandSpinner = dialogView.findViewById(R.id.brand_spinner);
        Button resetButton = dialogView.findViewById(R.id.reset_button);
        Button applyButton = dialogView.findViewById(R.id.apply_button);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.category_options, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> brandAdapter = ArrayAdapter.createFromResource(this,
                R.array.brand_options, android.R.layout.simple_spinner_item);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandSpinner.setAdapter(brandAdapter);

        AlertDialog dialog = builder.create();

        resetButton.setOnClickListener(v -> {
            minPriceEditText.setText("");
            maxPriceEditText.setText("");
            categorySpinner.setSelection(0);
            brandSpinner.setSelection(0);
        });

        applyButton.setOnClickListener(v -> {
            int minPrice = minPriceEditText.getText().toString().isEmpty() ? 0 :
                    Integer.parseInt(minPriceEditText.getText().toString());
            int maxPrice = maxPriceEditText.getText().toString().isEmpty() ? Integer.MAX_VALUE :
                    Integer.parseInt(maxPriceEditText.getText().toString());

            String category = categorySpinner.getSelectedItem().toString();
            Integer categoryId = category.equals("All") ? null : getCategoryId(category);
            String brand = brandSpinner.getSelectedItem().toString();
            Integer brandId = brand.equals("All") ? null : getBrandId(brand);

            loadProducts(1, pageSize, null, null, null, categoryId, brandId, minPrice, maxPrice);
            dialog.dismiss();
        });

        dialog.show();
    }

    private Integer getCategoryId(String category) {
        switch (category) {
            case "Electronics": return 1;
            case "Furniture": return 2;
            case "Clothing": return 3;
            default: return null;
        }
    }

    private Integer getBrandId(String brand) {
        switch (brand) {
            case "Apple": return 1;
            case "Samsung": return 2;
            case "Nike": return 3;
            default: return null;
        }
    }

    private void filterProducts(String query) {
        loadProducts(1, pageSize, query, null, null, null, null, null, null);
    }
}