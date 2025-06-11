package com.example.product_sale_app.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.product_sale_app.data.ChatRepository

class ChatViewModelFactory(
    private val boxId: Int,
    private val repo: ChatRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(boxId, repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
