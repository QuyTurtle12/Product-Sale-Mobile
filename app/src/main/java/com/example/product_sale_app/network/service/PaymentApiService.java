package com.example.product_sale_app.network.service;

import com.example.product_sale_app.model.payment.PaymentPostDTO;
import com.example.product_sale_app.model.payment.VNPayPaymentResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PaymentApiService {

    @POST("/api/payments")
    Call<Void> createPayment(@Body PaymentPostDTO payment);

    @POST("api/payments/vnpay/createpayment")
    Call<VNPayPaymentResponse> createPayment(@Query("orderId") int orderId);

}
