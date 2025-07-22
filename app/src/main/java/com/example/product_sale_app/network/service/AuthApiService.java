package com.example.product_sale_app.network.service;

import com.example.product_sale_app.model.login.LoginRequest;
import com.example.product_sale_app.model.login.LoginResponse;
import com.example.product_sale_app.model.register.RegisterRequest;
import com.example.product_sale_app.model.register.RegisterResponse;
import com.example.product_sale_app.model.BaseResponseModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AuthApiService {
    @POST("api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("api/auth/register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @GET("api/auth/{id}")
    Call<BaseResponseModel<String>> getUsernameById(
            @Path("id") int userId
    );

}
