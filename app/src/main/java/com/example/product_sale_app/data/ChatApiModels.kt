package com.example.product_sale_app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Generic paged wrapper
@Serializable
data class PagedResponse<T>(
    val data: PageData<T>
)

@Serializable
data class PageData<T>(
    val items: List<T>,
    val pageNumber: Int,
    val totalPages: Int,
    val pageSize: Int
)

// Message DTO
@Serializable
data class ChatMessageDto(
    @SerialName("chatMessageId") val id: Long,
    @SerialName("chatBoxId")     val boxId: Long,
    @SerialName("userId")        val userId: Long,
    @SerialName("username")      val username: String,
    @SerialName("message")       val text: String,
    @SerialName("sentAt")        val sentAt: String   // ISO timestamp
)

// Send-message request body
@Serializable
data class SendMessageRequest(
    val chatBoxId: Long,
    val message: String
)
