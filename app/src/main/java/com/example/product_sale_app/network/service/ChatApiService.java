// src/main/java/com/example/product_sale_app/data/ChatApiService.java
package com.example.product_sale_app.data;

import com.example.product_sale_app.model.chat.ChatMessageDto;
import com.example.product_sale_app.model.response.BaseResponseModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatApiService {
    @GET("api/chat")
    Call<BaseResponseModel<ChatMessageDto>> getMessages(
            @Query("pageIndex") int pageIndex,
            @Query("pageSize") int pageSize,
            @Query("chatBoxId") Integer chatBoxId,
            @Query("userId") Integer userId
    );

    @GET("api/chat/box/{chatBoxId}")
    Call<BaseResponseModel<ChatMessageDto>> getBoxMessages(
            @Path("chatBoxId") int boxId,
            @Query("pageIndex") int pageIndex,
            @Query("pageSize") int pageSize
    );

    // <-- switched to SingleResponseModel here!
    @POST("api/chat")
    Call<SingleResponseModel<ChatMessageDto>> sendMessage(
            @Body SendChatMessageRequestDTO req
    );
}
