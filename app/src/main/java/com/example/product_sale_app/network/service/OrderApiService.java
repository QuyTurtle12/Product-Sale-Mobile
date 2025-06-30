package com.example.product_sale_app.network.service;

import com.example.product_sale_app.model.order.OrderApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OrderApiService {
    @GET("/api/orders")
    Call<OrderApiResponse> getOrders(
            @Query("pageIndex") int pageIndex,
            @Query("pageSize") int pageSize,
            @Query("idSearch") Integer idSearch,
            @Query("cartIdSearch") Integer cartIdSearch,
            @Query("userIdSearch") Integer  userIdSearch,
            @Query("paymentMethodSearch") String paymentMethodSearch,
            @Query("addressSearch") String addressSearch,
            @Query("statusSearch") String statusSearch,
            @Query("orderDateSearch") String orderDateSearch,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );



}
