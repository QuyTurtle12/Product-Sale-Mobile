package com.example.product_sale_app.data

class ChatRepository(private val api: ChatApiService) {
    suspend fun loadMessages(boxId: Long) =
        api.getBoxMessages(boxId).data.items

    suspend fun postMessage(boxId: Long, text: String) {
        api.sendMessage(SendMessageRequest(chatBoxId = boxId, message = text))
    }
}
