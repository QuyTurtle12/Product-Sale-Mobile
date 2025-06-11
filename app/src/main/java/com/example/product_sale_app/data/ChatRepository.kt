// ChatRepository.kt
package com.example.product_sale_app.data

class ChatRepository(private val api: ChatApiService) {

    // Fetch the full, unfiltered list of messages for this user
    suspend fun loadAllMessages(
        userId: Int,
        pageIndex: Int = 1,
        pageSize:  Int = 100
    ): List<ChatMessageDto> =
        api.getMessages(
            pageIndex  = pageIndex,
            pageSize   = pageSize,
            chatBoxId  = null,
            userId     = userId
        ).data.items

    suspend fun loadBoxMessages(boxId: Int, page: Int = 1, size: Int = 20) =
        api.getBoxMessages(boxId, page, size).data.items

    suspend fun send(boxId: Int, text: String) =
        api.sendMessage(SendChatMessageRequestDTO(chatBoxId = boxId, message = text))
            .data
}
