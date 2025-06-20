package com.example.product_sale_app.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.product_sale_app.LoginActivity;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        SharedPreferences settings = context.getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        String token = settings.getString("token", null);

        if (token == null) {
            return chain.proceed(originalRequest);
        }

        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(newRequest);
    }
}