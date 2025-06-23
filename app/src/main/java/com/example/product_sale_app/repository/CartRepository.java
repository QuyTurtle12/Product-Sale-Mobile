package com.example.product_sale_app.repository;

import android.util.Log;

import com.example.product_sale_app.model.cart.CartResponse;
import com.example.product_sale_app.model.response.BaseResponseModel;
import com.example.product_sale_app.network.RetrofitClient;
import com.example.product_sale_app.network.service.CartApiService;

import retrofit2.Call;
import retrofit2.Callback;

public class CartRepository {
    private static final String TAG = "CartRepository";

    public interface CartCallback {
        void onSuccess(CartResponse response);
        void onFailure(String errorMessage);
    }

//    public void fetchCarts(int pageIndex, int pageSize, Integer idSearch, Integer userIdSearch, String statusSearch, CartCallback callback) {
//        CartApiService api = RetrofitClient.createService(CartRepository.this, CartApiService.class);
//        Call<BaseResponseModel<CartResponse>> call = api.getPaginatedCarts(pageIndex, pageSize, idSearch, userIdSearch, statusSearch);
//
//        call.enqueue(new Callback<>() {
//            @Override
//            public void onResponse(Call<BaseResponseModel<CartResponse>> call, Response<BaseResponseModel<CartResponse>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    callback.onSuccess(response.body().getData());
//                } else {
//                    callback.onFailure("Failed to load carts: " + response.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BaseResponseModel<CartResponse>> call, Throwable t) {
//                Log.e(TAG, "API Failure", t);
//                callback.onFailure("Error: " + t.getMessage());
//            }
//        });
//    }
}
