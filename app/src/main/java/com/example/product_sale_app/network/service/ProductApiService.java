package com.example.product_sale_app.network.service;

import com.example.product_sale_app.model.home_product.ProductResponse;
import com.example.product_sale_app.model.product.ProductApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProductApiService {
    @GET("api/products")
    Call<ProductApiResponse> getProducts(
            @Query("pageIndex") int pageIndex,
            @Query("pageSize") int pageSize
    );

    @GET("api/products")
    Call<ProductResponse> getProducts(
            @Query("pageIndex") int pageIndex,
            @Query("pageSize") int pageSize,
            @Query("idSearch") Integer idSearch,
            @Query("nameSearch") String nameSearch,
            @Query("sortBy") String sortBy,
            @Query("sortOrder") String sortOrder,
            @Query("categoryId") Integer categoryId,
            @Query("minPrice") Integer minPrice,
            @Query("maxPrice") Integer maxPrice);

    @GET("api/products")
    Call<ProductApiResponse> getNewProducts(@Query("pageSize") int pageSize,
                                            @Query("sortOrder") String sortOrder);

    @GET("api/products")
    Call<ProductApiResponse> getTopProducts(@Query("pageSize") int pageSize);


    @GET("api/products")
    Call<ProductApiResponse> getPaginatedProducts(
            @Query("pageIndex") int pageIndex,
            @Query("pageSize") int pageSize,
            @Query("idSearch") Integer idSearch,
            @Query("nameSearch") String nameSearch,
            @Query("sortBy") String sortBy,
            @Query("sortOrder") String sortOrder,
            @Query("categoryId") Integer categoryId,
            @Query("brandId") Integer brandId,
            @Query("minPrice") Integer minPrice,
            @Query("maxPrice") Integer maxPrice,
            @Query("minRating") Integer minRating
    );
}
