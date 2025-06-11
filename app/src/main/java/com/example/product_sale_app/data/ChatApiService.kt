package com.example.product_sale_app.data

import retrofit2.http.*

interface ChatApiService {
    @GET("api/chat")
    suspend fun getMessages(
        @Query("pageIndex")  pageIndex: Int,
        @Query("pageSize")   pageSize: Int,
        @Query("chatBoxId")  chatBoxId: Int? = null,
        @Query("userId")     userId: Int?     = null
    ): BaseResponseModel<ChatMessageDto>

    @GET("api/chat/box/{chatBoxId}")
    suspend fun getBoxMessages(
        @Path("chatBoxId") boxId: Int,
        @Query("pageIndex") pageIndex: Int = 1,
        @Query("pageSize")  pageSize: Int  = 20
    ): BaseResponseModel<ChatMessageDto>

    @POST("api/chat")
    suspend fun sendMessage(
        @Body req: SendChatMessageRequestDTO
    ): BaseResponseModel<ChatMessageDto>
}
