package com.example.product_sale_app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.product_sale_app.R;
import com.example.product_sale_app.adapter.ProductAdapter;
import com.example.product_sale_app.adapter.TopProductAdapter;
import com.example.product_sale_app.model.product.Product;
import com.example.product_sale_app.model.product.ProductApiResponse;
import com.example.product_sale_app.model.product.ProductData;
import com.example.product_sale_app.network.service.ProductApiService;
import com.example.product_sale_app.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    // All Products
    private RecyclerView recyclerViewAllProducts;
    private ProgressBar progressBarAllProducts;
    private NestedScrollView nestedScrollView;
    private ProductAdapter allProductsAdapter;
    private List<Product> allProductList;
    // New elements for New Products
    private RecyclerView recyclerViewNewProducts;
    private ProductAdapter newProductsAdapter;
    private List<Product> newProductList;
    private ImageView userProfile;

    // New elements for Top Products
    private RecyclerView recyclerViewTopProducts;
    private TopProductAdapter topProductsAdapter;
    private List<Product> topProductList;
    private ProductApiService productApiService;
    private boolean isLoading = false;
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages = 1;
    // Constants for limits
    private static final int NEW_PRODUCTS_LIMIT = 5;
    private static final int TOP_PRODUCTS_LIMIT = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize ProductApiService
        productApiService = RetrofitClient.createService(HomeActivity.this, ProductApiService.class);

        recyclerViewAllProducts = findViewById(R.id.recyclerViewAllProducts);
        progressBarAllProducts = findViewById(R.id.progressBarAllProducts);
        nestedScrollView = findViewById(R.id.nested_scroll_view);

        recyclerViewAllProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAllProducts.setNestedScrollingEnabled(false);

        allProductList = new ArrayList<>();
        allProductsAdapter = new ProductAdapter(this, allProductList);
        recyclerViewAllProducts.setAdapter(allProductsAdapter);

        userProfile = findViewById(R.id.user_icon);

        loadProducts(currentPage);
        setupPagination();

        // --- New Products Setup ---
        recyclerViewNewProducts = findViewById(R.id.recyclerViewNewProducts);

        // Use a horizontal layout manager for a carousel effect
        LinearLayoutManager newProductsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewNewProducts.setLayoutManager(newProductsLayoutManager);

        recyclerViewNewProducts.setNestedScrollingEnabled(false); // Important

        newProductList = new ArrayList<>();
        // You can reuse ProductAdapter if the item layout and functionality are the same
        newProductsAdapter = new ProductAdapter(this, newProductList);
        recyclerViewNewProducts.setAdapter(newProductsAdapter);
        // Load "New Products"
        loadNewProducts();

        // --- Top Products Setup ---
        recyclerViewTopProducts = findViewById(R.id.recyclerViewTopProducts);

        // Use a horizontal layout manager
        LinearLayoutManager topProductsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTopProducts.setLayoutManager(topProductsLayoutManager);

        recyclerViewTopProducts.setNestedScrollingEnabled(false);

        topProductList = new ArrayList<>();
        // Use TopProductAdapter
        topProductsAdapter = new TopProductAdapter(this, topProductList);
        recyclerViewTopProducts.setAdapter(topProductsAdapter);
        // Load "Top Products"
        loadTopProducts();

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadProducts(int pageIndexToLoad) {
        if (isLoading) {
            Log.d("HomeActivity", "Already loading products.");
            return;
        }
        if (pageIndexToLoad > totalPages && totalPages > 0 && pageIndexToLoad != 1) {
            Log.d("HomeActivity", "All pages already loaded.");
            isLoading = false;
            progressBarAllProducts.setVisibility(View.GONE);
            return;
        }

        isLoading = true;
        if (pageIndexToLoad == 1 || progressBarAllProducts.getVisibility() == View.GONE) {
            progressBarAllProducts.setVisibility(View.VISIBLE);
        }
        Log.d("HomeActivity", "Loading products for page index: " + pageIndexToLoad);

        // Use the renamed service variable
        Call<ProductApiResponse> call = productApiService.getProducts(pageIndexToLoad, pageSize);

        call.enqueue(new Callback<ProductApiResponse>() {
            @Override
            // BEFORE: public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
            public void onResponse(Call<ProductApiResponse> call, Response<ProductApiResponse> response) {
                isLoading = false;
                // progressBarAllProducts.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    // BEFORE: ApiResponse apiResponse = response.body();
                    ProductApiResponse productApiResponse = response.body();

                    // Use the new variable name:
                    if ("SUCCESS".equals(productApiResponse.getCode()) && productApiResponse.getData() != null) {
                        ProductData productData = productApiResponse.getData();
                        List<Product> fetchedProducts = productData.getItems();
                        totalPages = productData.getTotalPages();

                        Log.d("HomeActivity", "Response: Page " + productData.getPageNumber() + "/" + totalPages + ". Items: " + (fetchedProducts != null ? fetchedProducts.size() : 0));

                        if (fetchedProducts != null && !fetchedProducts.isEmpty()) {
                            if (productData.getPageNumber() == 1) {
                                allProductsAdapter.updateProducts(fetchedProducts);
                            } else {
                                allProductsAdapter.addProducts(fetchedProducts);
                            }
                            currentPage = productData.getPageNumber() + 1;
                        } else {
                            if (productData.getPageNumber() == 1) {
                                allProductsAdapter.updateProducts(new ArrayList<>());
                                Toast.makeText(HomeActivity.this, "No products found.", Toast.LENGTH_SHORT).show();
                            } else {
                                // Toast.makeText(HomeActivity.this, "No more products on this page.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (productData.getPageNumber() >= totalPages || !productData.isHasNextPage()) {
                            progressBarAllProducts.setVisibility(View.GONE);
                            if (productData.getPageNumber() >= totalPages && productData.getPageNumber() != 1 && (fetchedProducts == null || fetchedProducts.isEmpty())) {
                                // ...
                            } else if (productData.getPageNumber() >= totalPages && productData.getPageNumber() != 1) {
                                Toast.makeText(HomeActivity.this, "You've reached the end!", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } else {
                        Log.e("HomeActivity", "API Error: " + (productApiResponse != null ? productApiResponse.getMessage() : "Unknown error in response body") + " (Code: " + (productApiResponse != null ? productApiResponse.getCode() : "N/A") + ")");
                        Toast.makeText(HomeActivity.this, "Error: " + (productApiResponse != null ? productApiResponse.getMessage() : "Failed to process data"), Toast.LENGTH_LONG).show();
                        progressBarAllProducts.setVisibility(View.GONE);
                    }
                } else {
                    Log.e("HomeActivity", "Failed to load products. Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(HomeActivity.this, "Failed to load products (HTTP " + response.code() + ")", Toast.LENGTH_LONG).show();
                    progressBarAllProducts.setVisibility(View.GONE);
                }
            }

            @Override
            // BEFORE: public void onFailure(Call<ApiResponse> call, Throwable t) {
            public void onFailure(Call<ProductApiResponse> call, Throwable t) { // ***** CHANGED TYPE *****
                isLoading = false;
                progressBarAllProducts.setVisibility(View.GONE);
                Log.e("HomeActivity", "Network failure: " + t.getMessage(), t);
                Toast.makeText(HomeActivity.this, "Network error. Please check your connection.", Toast.LENGTH_LONG).show();
            }

        });


        // --- Optional: Implement if you add click listener to adapter ---
        // @Override
        // public void onProductClick(Product product, int position) {
        //    Toast.makeText(this, "Clicked: " + product.getProductName(), Toast.LENGTH_SHORT).show();
        //    // Navigate to product detail activity, etc.
        //    // Intent intent = new Intent(this, ProductDetailActivity.class);
        //    // intent.putExtra("PRODUCT_ID", product.getProductId());
        //    // startActivity(intent);
        // }
        //

    }

    private void setupPagination() {
        if (nestedScrollView == null) { // Defensive check
            Log.e("HomeActivity", "NestedScrollView is null in setupPagination. Make sure it's initialized.");
            return;
        }
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    // Check if the NestedScrollView has reached the bottom
                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        Log.d("HomeActivity", "NestedScrollView reached bottom. CurrentPage to load: " + currentPage + ", TotalPages: " + totalPages);
                        // Check if not already loading AND if there are more pages to load
                        if (!isLoading && currentPage <= totalPages) {
                            loadProducts(currentPage);
                        } else if (!isLoading && currentPage > totalPages && totalPages > 0) {
                            // No more line
                        }
                    }
                });
    }

    private void loadNewProducts() {
        Call<ProductApiResponse> call = productApiService.getNewProducts(NEW_PRODUCTS_LIMIT, "desc");

        call.enqueue(new Callback<ProductApiResponse>() {
            @Override
            public void onResponse(Call<ProductApiResponse> call, Response<ProductApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductApiResponse productApiResponse = response.body();
                    if ("SUCCESS".equals(productApiResponse.getCode()) && productApiResponse.getData() != null) {
                        List<Product> fetchedProducts = productApiResponse.getData().getItems();
                        if (fetchedProducts != null && !fetchedProducts.isEmpty()) {
                            newProductsAdapter.updateProducts(fetchedProducts);
                        } else {
                            Log.d("HomeActivity", "No new products found.");
                        }
                    } else {
                        Log.e("HomeActivity", "API Error for New Products: " + productApiResponse.getMessage());
                        Toast.makeText(HomeActivity.this, "Could not load new products.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("HomeActivity", "Failed to load new products. Code: " + response.code());
                    Toast.makeText(HomeActivity.this, "Failed to load new products.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductApiResponse> call, Throwable t) {
                Log.e("HomeActivity", "Network Failure for New Products: " + t.getMessage(), t);
                Toast.makeText(HomeActivity.this, "Network error loading new products.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTopProducts() {
        // Assuming your API service has a method like getTopSellingProducts()
        Call<ProductApiResponse> call = productApiService.getTopProducts(TOP_PRODUCTS_LIMIT);

        call.enqueue(new Callback<ProductApiResponse>() {
            @Override
            public void onResponse(Call<ProductApiResponse> call, Response<ProductApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductApiResponse productApiResponse = response.body();
                    if ("SUCCESS".equals(productApiResponse.getCode()) && productApiResponse.getData() != null) {
                        List<Product> fetchedProducts = productApiResponse.getData().getItems();
                        if (fetchedProducts != null && !fetchedProducts.isEmpty()) {
                            topProductsAdapter.updateProducts(fetchedProducts);
                        } else {
                            Log.d("HomeActivity", "No top products found.");
                        }
                    } else {
                        Log.e("HomeActivity", "API Error for Top Products: " + (productApiResponse != null ? productApiResponse.getMessage() : "Unknown error"));
                        Toast.makeText(HomeActivity.this, "Could not load top products: " + (productApiResponse != null ? productApiResponse.getMessage() : ""), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("HomeActivity", "Failed to load top products. Code: " + response.code());
                    Toast.makeText(HomeActivity.this, "Failed to load top products (HTTP " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductApiResponse> call, Throwable t) {
                Log.e("HomeActivity", "Network Failure for Top Products: " + t.getMessage(), t);
                Toast.makeText(HomeActivity.this, "Network error loading top products.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}