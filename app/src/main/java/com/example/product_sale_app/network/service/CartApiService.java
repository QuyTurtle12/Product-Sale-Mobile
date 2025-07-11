package com.example.product_sale_app.network.service;

import com.example.product_sale_app.model.cart.CartApiResponse;
import com.example.product_sale_app.model.cart.CartItemDTO;
import com.example.product_sale_app.model.cart.CartResponse;
import com.example.product_sale_app.model.BaseResponseModel;
import com.example.product_sale_app.model.cart.CartUpdateDTO;
import com.example.product_sale_app.model.cart_item.CartItemAddDTO;
import com.example.product_sale_app.model.cart_item.CartItemUpdateDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface CartApiService {
    @GET("api/carts") // Adjust the path if your controller route is different
    Call<CartApiResponse> getPaginatedCarts(
            @Query("pageIndex") int pageIndex,
            @Query("pageSize") int pageSize,
            @Query("idSearch") Integer idSearch,
            @Query("userIdSearch") Integer userIdSearch,
            @Query("statusSearch") String statusSearch,
            @Query("getUserLastestCart") boolean getUserLastestCart
    );

    @PUT("api/carts/{id}")
    Call<Void> updateCartTotalPrice(@Path("id") int cartId, @Body CartUpdateDTO updateDTO);

    @DELETE("api/cartitems/{id}")
    Call<Void> deleteCartItem(@Path("id") int cartItemId);

    @PUT("api/cartitems/{id}")
    Call<Void> updateCartItem(@Path("id") int cartItemId, @Body CartItemDTO updateDTO);

    @PUT("api/cartitems/{id}")
    Call<Void> updateCartItem(
            @Path("id") int cartItemId,
            @Body CartItemUpdateDTO request
    );

    @POST("api/cartitems")
    Call<Void> addCartItem(@Body CartItemAddDTO addCartItemRequest);





}
