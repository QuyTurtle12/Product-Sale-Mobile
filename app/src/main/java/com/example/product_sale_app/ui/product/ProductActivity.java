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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

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

    // 👉 Variables to store filter / search / sort state
    private Integer selectedCategoryId = null;
    private Integer selectedBrandId = null;
    private Integer selectedMinPrice = null;
    private Integer selectedMaxPrice = null;

    private String currentSearch = null;
    private String currentSortBy = null;
    private String currentSortOrder = null;

    private boolean isLoading = false;
    private int totalPages = 1;
    private NestedScrollView nestedScrollView;
    private ProgressBar progressBarProducts;

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
        nestedScrollView        = findViewById(R.id.product_nested_scroll_view);
        progressBarProducts     = findViewById(R.id.progress_bar_products);

        backButton.setOnClickListener(v -> finish());
        moreButton.setOnClickListener(v -> Toast.makeText(this, "Other option", Toast.LENGTH_SHORT).show());

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

        // Restore search / sort UI when Activity is recreated
        if (currentSearch != null) {
            searchEditText.setText(currentSearch);
        }
        restoreSortSelection();

        filterButton.setOnClickListener(v -> showFilterDialog());

        // Send search event using IME action "search"
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            currentSearch = searchEditText.getText().toString().trim();
            currentPage = 1; // reset to first page
            loadProducts();
            return true;
        });
    }

    // Reload data when Activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        setupPagination();
    }

    private void setupPagination() {
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    // Check if scrolled to bottom
                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        Log.d(TAG, "Reached bottom. CurrentPage: " + currentPage + ", TotalPages: " + totalPages);
                        // Check if not loading and more pages exist
                        if (!isLoading && currentPage <= totalPages) {
                            loadMoreProducts();
                        }
                    }
                });
    }

    private void loadMoreProducts() {
        // This loads the next page and appends to existing products
        loadProducts(currentPage, pageSize, currentSearch, currentSortBy, currentSortOrder,
                selectedCategoryId, selectedBrandId, selectedMinPrice, selectedMaxPrice);
    }

    private void loadProducts() {
        // Reset products when applying new filters or search
        productList = new ArrayList<>();
        currentPage = 1;
        populateProductGrid(); // Clear existing grid

        // Show progress indicator
        progressBarProducts.setVisibility(View.VISIBLE);

        loadProducts(currentPage, pageSize, currentSearch, currentSortBy, currentSortOrder,
                selectedCategoryId, selectedBrandId, selectedMinPrice, selectedMaxPrice);
    }

    private void loadProducts(int pageIndex, int pageSize, String nameSearch, String sortBy, String sortOrder,
                              Integer categoryId, Integer brandId, Integer minPrice, Integer maxPrice) {

        if (isLoading) {
            Log.d(TAG, "Already loading products.");
            return;
        }

        isLoading = true;
        progressBarProducts.setVisibility(View.VISIBLE);

        ProductApiService apiService = RetrofitClient.createService(ProductActivity.this, ProductApiService.class);

        Call<ProductApiResponse> call = apiService.getPaginatedProducts(
                pageIndex, pageSize, null, nameSearch,
                sortBy, sortOrder, categoryId, brandId, minPrice, maxPrice, null);

        call.enqueue(new Callback<ProductApiResponse>() {
            @Override
            public void onResponse(Call<ProductApiResponse> call, Response<ProductApiResponse> response) {
                isLoading = false;
                progressBarProducts.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    ProductApiResponse apiResponse = response.body();
                    if ("SUCCESS".equals(apiResponse.getCode()) && apiResponse.getData() != null) {
                        List<Product> products = apiResponse.getData().getItems();

                        // Get total pages info
                        totalPages = apiResponse.getData().getTotalPages();

                        if (products != null && !products.isEmpty()) {
                            if (pageIndex == 1) {
                                // Replace products if it's first page
                                productList = new ArrayList<>(products);
                            } else {
                                // Append products for pagination
                                productList.addAll(products);
                            }

                            populateProductGrid();
                            currentPage++; // Increment for next page
                        } else if (pageIndex == 1) {
                            // No products found on first page
                            Toast.makeText(ProductActivity.this, "No products found.", Toast.LENGTH_SHORT).show();
                            productList = new ArrayList<>();
                            populateProductGrid();
                        }

                        // Check if we've reached the end
                        if (pageIndex >= totalPages) {
                            if (pageIndex > 1) {
                                Toast.makeText(ProductActivity.this, "You've reached the end!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(ProductActivity.this, "Error: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProductActivity.this, "Connection Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductApiResponse> call, Throwable t) {
                isLoading = false;
                progressBarProducts.setVisibility(View.GONE);
                Toast.makeText(ProductActivity.this, "Connection Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

            int imageHeight = (int) (200 * getResources().getDisplayMetrics().density);
            productImage.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, imageHeight));

            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int cardWidth = screenWidth / 2 - 24; // 24 for margin

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cardWidth;
            params.setMargins(12, 12, 12, 12); // 12dp margin

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
            cardView.setLayoutParams(params);

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
        builder.setTitle("Filter Products");

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