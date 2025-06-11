package com.example.product_sale_app.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.product_sale_app.data.ChatMessageDto
import com.example.product_sale_app.data.ChatRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatViewModel(
    private val boxId: Long,
    private val repo: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessageDto>>(emptyList())
    val messages: StateFlow<List<ChatMessageDto>> = _messages

    private val _sending = MutableStateFlow(false)
    val sending: StateFlow<Boolean> = _sending

    init { refresh() }

    fun refresh() = viewModelScope.launch {
        _messages.value = repo.loadMessages(boxId)
    }

    fun send(text: String) = viewModelScope.launch {
        _sending.value = true
        repo.postMessage(boxId, text)
        refresh()
        _sending.value = false
    }
}
