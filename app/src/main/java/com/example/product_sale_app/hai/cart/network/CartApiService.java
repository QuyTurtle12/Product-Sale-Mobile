package com.example.product_sale_app.hai.cart.network;

<<<<<<< Updated upstream:app/src/main/java/com/example/product_sale_app/hai/cart/network/CartApiService.java
import com.example.product_sale_app.hai.cart.model.CartResponse;
import com.example.product_sale_app.hai.cart.model.BaseResponseModel;
=======
import com.example.product_sale_app.model.cart.CartApiResponse;
import com.example.product_sale_app.model.cart.CartResponse;
import com.example.product_sale_app.model.BaseResponseModel;
>>>>>>> Stashed changes:app/src/main/java/com/example/product_sale_app/network/service/CartApiService.java

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface CartApiService {
    @GET("api/carts") // Adjust the path if your controller route is different
    Call<CartApiResponse> getPaginatedCarts(
            @Query("pageIndex") int pageIndex,
            @Query("pageSize") int pageSize,
            @Query("idSearch") Integer idSearch,
            @Query("userIdSearch") Integer userIdSearch,
            @Query("statusSearch") String statusSearch
    );
}
