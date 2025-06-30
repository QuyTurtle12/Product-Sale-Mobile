package com.example.product_sale_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.product_sale_app.api.ApiService;
import com.example.product_sale_app.model.Product;
import com.example.product_sale_app.model.ProductResponse;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

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

    private EditText searchEditText;
    private GridLayout productGrid;
    private List<Product> productList;
    private List<Product> originalProductList;
    private Spinner sortSpinner;
    private Spinner brandSpinner; // Thêm Spinner cho brand
    private Button filterButton;
    private boolean isFirstSort = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        ImageButton backButton = findViewById(R.id.back_button);
        ImageButton moreButton = findViewById(R.id.more_button);
        searchEditText = findViewById(R.id.search_edit_text);
        productGrid = findViewById(R.id.product_grid);
        sortSpinner = findViewById(R.id.sort_spinner);
        brandSpinner = findViewById(R.id.brand_spinner); // Khởi tạo brandSpinner
        filterButton = findViewById(R.id.filter_button);

        backButton.setOnClickListener(v -> finish());
        moreButton.setOnClickListener(v -> Toast.makeText(this, "Tùy chọn khác", Toast.LENGTH_SHORT).show());

        // Khởi tạo adapter cho sortSpinner
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstSort) {
                    isFirstSort = false;
                    return;
                }
                applySort(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Khởi tạo adapter cho brandSpinner
        ArrayAdapter<CharSequence> brandAdapter = ArrayAdapter.createFromResource(this,
                R.array.brand_options, android.R.layout.simple_spinner_item);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandSpinner.setAdapter(brandAdapter);

        filterButton.setOnClickListener(v -> showFilterDialog());

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchEditText.getText().toString().trim();
            filterProducts(query);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeProductListFromApi(1, 10, null, null, null, null, null, null, null, null);
    }

    private void initializeProductListFromApi(int pageIndex, int pageSize, Integer idSearch, String nameSearch,
                                              String sortBy, String sortOrder, Integer categoryId, Integer minPrice, Integer maxPrice, Integer brandId) {
        productList = new ArrayList<>();
        originalProductList = new ArrayList<>();

        OkHttpClient unsafeClient = getUnsafeOkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://10.0.2.2:7050/")
                .client(unsafeClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<ProductResponse> call = apiService.getProducts(pageIndex, pageSize, idSearch, nameSearch,
                sortBy, sortOrder, categoryId, minPrice, maxPrice, brandId);

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductResponse.Data data = response.body().getData();
                    if (data != null && data.getItems() != null) {
                        productList.clear();
                        originalProductList.clear();
                        productList.addAll(data.getItems());
                        originalProductList.addAll(data.getItems());
                        populateProductGrid();
                    } else {
                        Toast.makeText(ProductActivity.this, "Không có dữ liệu từ API", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProductActivity.this, "Lỗi khi gọi API: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(ProductActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                productList.clear();
                originalProductList.clear();
                productList.add(new Product(1, "Electronics", "Smartphone X2", "High-end smartphone", "", "", 10000, "https://via.placeholder.com/150", 1)); // Thêm brandId
                productList.add(new Product(2, "Furniture", "Sofa Deluxe", "Luxury 3-seat sofa", "", "", 20000, "https://via.placeholder.com/150", 2)); // Thêm brandId
                productList.add(new Product(3, "Clothing", "Winter Jacket", "Warm winter wear", "", "", 30000, "https://via.placeholder.com/150", 3)); // Thêm brandId
                originalProductList.addAll(productList);

                populateProductGrid();
            }
        });
    }

    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
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

            String imageUrl = product.getImageUrl();
            int imageResId = getResources().getIdentifier(imageUrl, "drawable", getPackageName());
            if (imageResId != 0) {
                Glide.with(this).load(imageResId).into(productImage);
            } else {
                Glide.with(this).load(imageUrl).into(productImage);
            }

            TextView productName = new TextView(this);
            productName.setText(product.getProductName());
            productName.setTextSize(14);
            productName.setTextColor(Color.parseColor("#333333"));

            TextView productPrice = new TextView(this);
            productPrice.setText(String.format("%,dđ", product.getPrice()));
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
            case 3: sortBy = "categoryName"; sortOrder = "asc"; break;
        }
        initializeProductListFromApi(1, 10, null, null, sortBy, sortOrder, null, null, null, null);
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lọc sản phẩm");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter, null);
        builder.setView(dialogView);

        EditText minPriceEditText = dialogView.findViewById(R.id.min_price);
        EditText maxPriceEditText = dialogView.findViewById(R.id.max_price);
        Spinner categorySpinner = dialogView.findViewById(R.id.category_spinner);
        Spinner brandSpinner = dialogView.findViewById(R.id.brand_spinner_dialog);
        Button resetButton = dialogView.findViewById(R.id.reset_button);
        Button applyButton = dialogView.findViewById(R.id.apply_button);

        // Khởi tạo adapter cho categorySpinner
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.category_options, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Khởi tạo adapter cho brandSpinner
        ArrayAdapter<CharSequence> brandAdapter = ArrayAdapter.createFromResource(this,
                R.array.brand_options, android.R.layout.simple_spinner_item);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandSpinner.setAdapter(brandAdapter);

        AlertDialog dialog = builder.create();

        resetButton.setOnClickListener(v -> {
            minPriceEditText.setText("");
            maxPriceEditText.setText("");
            categorySpinner.setSelection(0); // Reset to "All"
            brandSpinner.setSelection(0); // Reset to "Tất cả"
        });

        applyButton.setOnClickListener(v -> {
            int minPrice = minPriceEditText.getText().toString().isEmpty() ? 0 :
                    Integer.parseInt(minPriceEditText.getText().toString());
            int maxPrice = maxPriceEditText.getText().toString().isEmpty() ? Integer.MAX_VALUE :
                    Integer.parseInt(maxPriceEditText.getText().toString());

            String category = categorySpinner.getSelectedItem().toString();
            Integer categoryId = null;
            switch (category) {
                case "Electronics": categoryId = 1; break;
                case "Furniture": categoryId = 2; break;
                case "Clothing": categoryId = 3; break;
            }

            String brand = brandSpinner.getSelectedItem().toString();
            Integer brandId = null;
            switch (brand) {
                case "Samsung": brandId = 1; break;
                case "Apple": brandId = 2; break;
                case "Sony": brandId = 3; break;
                case "Tất cả": brandId = null; break;
            }

            initializeProductListFromApi(1, 10, null, null, null, null, categoryId, minPrice, maxPrice, brandId);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void filterProducts(String query) {
        initializeProductListFromApi(1, 10, null, query, null, null, null, null, null, null);
    }

    private boolean isAscending = true;

    private void toggleSortOrder() {
        isAscending = !isAscending;
        String sortOrder = isAscending ? "asc" : "desc";
        initializeProductListFromApi(1, 10, null, null, "price", sortOrder, null, null, null, null);
        Toast.makeText(this, isAscending ? "Sắp xếp tăng dần" : "Sắp xếp giảm dần", Toast.LENGTH_SHORT).show();
    }
}