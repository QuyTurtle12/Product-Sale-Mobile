// src/main/java/com/example/product_sale_app/ui/chat/AuthInterceptor.java
package com.example.product_sale_app.ui.chat;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final String token;

    // You can inject your test JWT here or swap it out later after login
    public AuthInterceptor() {
        this.token = "Bearer eyJhbGciOi…"; // TODO: replace with real token
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request withAuth = original.newBuilder()
                .header("Authorization", token)
                .build();
        return chain.proceed(withAuth);
    }
}
