package com.example.product_sale_app.network.service;

import com.example.product_sale_app.model.payment.PaymentPostDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PaymentApiService {

    @POST("/api/payments")
    Call<Void> createPayment(@Body PaymentPostDTO payment);

}
