package com.example.product_sale_app.api;

import com.example.product_sale_app.model.Product;
import com.example.product_sale_app.model.ProductResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/Products")
    Call<ProductResponse> getProducts(
            @Query("pageIndex") int pageIndex,
            @Query("pageSize") int pageSize,
            @Query("idSearch") Integer idSearch,
            @Query("nameSearch") String nameSearch,
            @Query("sortBy") String sortBy,
            @Query("sortOrder") String sortOrder,
            @Query("categoryId") Integer categoryId,
            @Query("minPrice") Integer minPrice,
            @Query("maxPrice") Integer maxPrice,
            @Query("brandId") Integer brandId

    );
}