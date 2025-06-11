package com.example.product_sale_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.product_sale_app.data.ChatApiService
import com.example.product_sale_app.data.ChatRepository
import com.example.product_sale_app.ui.chat.AuthInterceptor
import com.example.product_sale_app.ui.chat.ChatAppNavHost
import com.example.product_sale_app.ui.theme.ProductSaleAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1) HTTP logging interceptor
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val auth = AuthInterceptor()

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(auth)          // ← add the JWT header here
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://localhost:5006/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api  = retrofit.create(ChatApiService::class.java)
        val repo = ChatRepository(api)

        setContent {
            ProductSaleAppTheme {
                // replace 16 with your authenticated user ID
                ChatAppNavHost(repo = repo, localUserId = 16)
            }
        }
    }
}
