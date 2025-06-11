package com.example.product_sale_app.data

import retrofit2.http.*

interface ChatApiService {
    @GET("api/chat/box/{boxId}")
    suspend fun getBoxMessages(
        @Path("boxId") boxId: Long,
        @Query("pageIndex") pageIndex: Int = 1,
        @Query("pageSize")  pageSize: Int = 50
    ): PagedResponse<ChatMessageDto>

    @POST("api/chat")
    suspend fun sendMessage(@Body req: SendMessageRequest)
}
