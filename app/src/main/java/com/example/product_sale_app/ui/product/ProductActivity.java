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
import com.example.product_sale_app.network.RetrofitClient;
import com.example.product_sale_app.network.service.ProductApiService;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductActivity extends AppCompatActivity {

    private static final String TAG = "ProductActivity";

    private EditText searchEditText;
    private GridLayout productGrid;
    private Spinner sortSpinner;
    private Button filterButton;

    private List<Product> productList;

    private int currentPage = 1;
    private final int pageSize = 10;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    // 👉 Biến lưu trạng thái lọc / tìm kiếm / sắp xếp
    private Integer selectedCategoryId = null;
    private Integer selectedBrandId = null;
    private Integer selectedMinPrice = null;
    private Integer selectedMaxPrice = null;

    private String currentSearch = null;
    private String currentSortBy = null;
    private String currentSortOrder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        ImageButton backButton  = findViewById(R.id.back_button);
        ImageButton moreButton  = findViewById(R.id.more_button);
        searchEditText          = findViewById(R.id.search_edit_text);
        productGrid             = findViewById(R.id.product_grid);
        sortSpinner             = findViewById(R.id.sort_spinner);
        filterButton            = findViewById(R.id.filter_button);

        backButton.setOnClickListener(v -> finish());
        moreButton.setOnClickListener(v -> Toast.makeText(this, "Tùy chọn khác", Toast.LENGTH_SHORT).show());

        // --- Sort spinner
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applySort(position);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Khôi phục search / sort UI khi Activity được tạo lại (ví dụ xoay màn hình)
        if (currentSearch != null) {
            searchEditText.setText(currentSearch);
        }
        restoreSortSelection();

        filterButton.setOnClickListener(v -> showFilterDialog());

        // Gửi sự kiện search bằng IME action "search"
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            currentSearch = searchEditText.getText().toString().trim();
            currentPage = 1; // reset về trang đầu khi tìm kiếm mới
            loadProducts();
            return true;
        });
    }

    // ✅ Luôn reload data khi Activity quay trở lại foreground
    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    /** Convenience: build và gọi API với trạng thái hiện tại */
    private void loadProducts() {
        loadProducts(currentPage, pageSize, currentSearch, currentSortBy, currentSortOrder,
                selectedCategoryId, selectedBrandId, selectedMinPrice, selectedMaxPrice);
    }

    private void loadProducts(int pageIndex, int pageSize, String nameSearch, String sortBy, String sortOrder,
                              Integer categoryId, Integer brandId, Integer minPrice, Integer maxPrice) {

        ProductApiService apiService = RetrofitClient.createService(ProductActivity.this, ProductApiService.class);

        Call<ProductApiResponse> call = apiService.getPaginatedProducts(
                pageIndex, pageSize, null, nameSearch,
                sortBy, sortOrder, categoryId, brandId, minPrice, maxPrice, null);

        call.enqueue(new Callback<ProductApiResponse>() {
            @Override
            public void onResponse(Call<ProductApiResponse> call, Response<ProductApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductApiResponse apiResponse = response.body();
                    if ("SUCCESS".equals(apiResponse.getCode()) && apiResponse.getData() != null) {
                        List<Product> products = apiResponse.getData().getItems();
                        productList = products != null ? new ArrayList<>(products) : new ArrayList<>();
                        populateProductGrid();
                    } else {
                        Toast.makeText(ProductActivity.this, "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProductActivity.this, "Lỗi kết nối: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductApiResponse> call, Throwable t) {
                Toast.makeText(ProductActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateProductGrid() {
        productGrid.removeAllViews();
        if (productList == null) return;

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
            productPrice.setText(currencyFormatter.format(product.getPrice()));
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
        switch (position) {
            case 0: currentSortBy = "price"; currentSortOrder = "asc"; break;
            case 1: currentSortBy = "price"; currentSortOrder = "desc"; break;
            case 2: currentSortBy = "popularity"; currentSortOrder = "desc"; break;
            case 3: currentSortBy = "category"; currentSortOrder = "asc"; break;
            default: currentSortBy = null; currentSortOrder = null;
        }
        currentPage = 1;
        loadProducts();
    }

    private void restoreSortSelection() {
        if (currentSortBy == null || currentSortOrder == null) return;
        if ("price".equals(currentSortBy) && "asc".equals(currentSortOrder)) {
            sortSpinner.setSelection(0);
        } else if ("price".equals(currentSortBy) && "desc".equals(currentSortOrder)) {
            sortSpinner.setSelection(1);
        } else if ("popularity".equals(currentSortBy) && "desc".equals(currentSortOrder)) {
            sortSpinner.setSelection(2);
        } else if ("category".equals(currentSortBy) && "asc".equals(currentSortOrder)) {
            sortSpinner.setSelection(3);
        }
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lọc sản phẩm");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter, null);
        builder.setView(dialogView);

        EditText minPriceEditText  = dialogView.findViewById(R.id.min_price);
        EditText maxPriceEditText  = dialogView.findViewById(R.id.max_price);
        Spinner categorySpinner    = dialogView.findViewById(R.id.category_spinner);
        Spinner brandSpinner       = dialogView.findViewById(R.id.brand_spinner);
        Button resetButton         = dialogView.findViewById(R.id.reset_button);
        Button applyButton         = dialogView.findViewById(R.id.apply_button);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.category_options, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> brandAdapter = ArrayAdapter.createFromResource(this,
                R.array.brand_options, android.R.layout.simple_spinner_item);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandSpinner.setAdapter(brandAdapter);

        // --- Restore selections
        if (selectedMinPrice != null) minPriceEditText.setText(String.valueOf(selectedMinPrice));
        if (selectedMaxPrice != null) maxPriceEditText.setText(String.valueOf(selectedMaxPrice));

        if (selectedCategoryId != null) {
            switch (selectedCategoryId) {
                case 1: categorySpinner.setSelection(categoryAdapter.getPosition("Electronics")); break;
                case 2: categorySpinner.setSelection(categoryAdapter.getPosition("Furniture")); break;
                case 3: categorySpinner.setSelection(categoryAdapter.getPosition("Clothing")); break;
            }
        }

        if (selectedBrandId != null) {
            switch (selectedBrandId) {
                case 1: brandSpinner.setSelection(brandAdapter.getPosition("Apple")); break;
                case 2: brandSpinner.setSelection(brandAdapter.getPosition("Samsung")); break;
                case 3: brandSpinner.setSelection(brandAdapter.getPosition("Nike")); break;
            }
        }

        AlertDialog dialog = builder.create();

        resetButton.setOnClickListener(v -> {
            selectedMinPrice = null;
            selectedMaxPrice = null;
            selectedCategoryId = null;
            selectedBrandId = null;
            minPriceEditText.setText("");
            maxPriceEditText.setText("");
            categorySpinner.setSelection(0);
            brandSpinner.setSelection(0);
        });

        applyButton.setOnClickListener(v -> {
            selectedMinPrice = minPriceEditText.getText().toString().isEmpty() ? null :
                    Integer.parseInt(minPriceEditText.getText().toString());
            selectedMaxPrice = maxPriceEditText.getText().toString().isEmpty() ? null :
                    Integer.parseInt(maxPriceEditText.getText().toString());

            String category = categorySpinner.getSelectedItem().toString();
            selectedCategoryId = "Categories".equals(category) ? null : getCategoryId(category);

            String brand = brandSpinner.getSelectedItem().toString();
            selectedBrandId = "Brands".equals(brand) ? null : getBrandId(brand);

            currentPage = 1;
            loadProducts();
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
}