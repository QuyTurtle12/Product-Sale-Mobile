package com.example.product_sale_app.network;

import com.example.product_sale_app.model.cart.CartResponse;
import com.example.product_sale_app.model.BaseResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface CartApiService {
    @GET("api/Cart") // Adjust the path if your controller route is different
    Call<BaseResponseModel<CartResponse>> getPaginatedCarts(
            @Query("pageIndex") int pageIndex,
            @Query("pageSize") int pageSize,
            @Query("idSearch") Integer idSearch,
            @Query("userIdSearch") Integer userIdSearch,
            @Query("statusSearch") String statusSearch
    );
}
