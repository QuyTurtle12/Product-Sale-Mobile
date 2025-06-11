package com.example.product_sale_app.ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

data class ChatBox(
    val id: Long,
    val title: String,
    val lastMessage: String,
    val timestamp: Date
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    chats: List<ChatBox>,
    onChatSelected: (Long) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Chats") }) }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(chats) { chat ->
                ChatListItem(chat) { onChatSelected(chat.id) }
                Divider()
            }
        }
    }
}

@Composable
fun ChatListItem(chat: ChatBox, onClick: () -> Unit) {
    val time = SimpleDateFormat("HH:mm", Locale.getDefault())
        .format(chat.timestamp)
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(chat.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(chat.lastMessage, maxLines = 1, style = MaterialTheme.typography.bodyMedium)
        }
        Text(time, style = MaterialTheme.typography.bodySmall)
    }
}
