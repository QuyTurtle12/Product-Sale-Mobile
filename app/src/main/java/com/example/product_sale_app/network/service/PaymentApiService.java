package com.example.product_sale_app.network.service;

import com.example.product_sale_app.model.payment.PaymentPostDTO;
import com.example.product_sale_app.model.payment.PaymentStatusResponse;
import com.example.product_sale_app.model.payment.VNPayPaymentResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Path;

public interface PaymentApiService {

    @POST("/api/payments")
    Call<Void> createPayment(@Body PaymentPostDTO payment);

    @POST("api/payments/vnpay/createpayment")
    Call<VNPayPaymentResponse> createPayment(@Query("orderId") int orderId);

    @GET("api/payments/payment-status/{orderId}")
    Call<PaymentStatusResponse> checkPaymentStatus(@Path("orderId") int orderId);

}
