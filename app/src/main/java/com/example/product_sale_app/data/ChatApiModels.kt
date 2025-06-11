package com.example.product_sale_app.data

import com.google.gson.annotations.SerializedName

data class BaseResponseModel<T>(
    val data: PaginatedList<T>,
    val additionalData: Any? = null,
    val message: String,
    val statusCode: Int,
    val code: String
)

data class PaginatedList<T>(
    val items: List<T>,
    val pageNumber: Int,
    val totalPages: Int,
    val totalCount: Int,
    val pageSize: Int,
    val hasPreviousPage: Boolean,
    val hasNextPage: Boolean
)

data class ChatMessageDto(
    @SerializedName("chatMessageId") val id: Int,
    @SerializedName("userId")        val userId: Int?,
    @SerializedName("username")      val username: String,
    @SerializedName("message")       val text: String,
    @SerializedName("sentAt")        val sentAt: String,
    @SerializedName("chatBoxId")     val boxId: Int
)

data class SendChatMessageRequestDTO(
    val chatBoxId: Int,
    val message: String
)
