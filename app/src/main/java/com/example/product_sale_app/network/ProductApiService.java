package com.example.product_sale_app.network;

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
    Call<ProductApiResponse> getNewProducts(@Query("pageSize") int pageSize,
                                            @Query("sortOrder") String sortOrder);

    @GET("api/products")
    Call<ProductApiResponse> getTopProducts(@Query("pageSize") int pageSize);
}
