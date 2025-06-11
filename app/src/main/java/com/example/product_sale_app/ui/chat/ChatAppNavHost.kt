// ChatAppNavHost.kt
package com.example.product_sale_app.ui.chat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.navArgument
import com.example.product_sale_app.data.ChatRepository
import java.time.Instant
import java.util.*

private val TS_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
fun parseInstant(ts: String): Instant {
    val zts = if (ts.endsWith("Z")) ts else "${ts}Z"
    return Instant.parse(zts)
}


@Composable
fun ChatAppNavHost(
    repo: ChatRepository,
    localUserId: Int
) {
    val navController = rememberNavController()

    // 1) Load all messages once and build chat‐boxes
    val chatBoxes by produceState(initialValue = emptyList<ChatBox>()) {
        try {
            val all = repo.loadAllMessages(localUserId)
            value = all.groupBy { it.boxId }
                .map { (boxId, msgs) ->
                    val last = msgs.maxByOrNull { parseInstant(it.sentAt) }!!
                    ChatBox(
                        id          = boxId.toLong(),
                        title       = "Chat #$boxId",
                        lastMessage = last.text,
                        timestamp   = Date.from(parseInstant(last.sentAt))
                    )
                }
        } catch (t: Throwable) {
            Log.e("ChatAppNavHost", "Error loading messages", t)
            value = emptyList()
        }
    }


    NavHost(navController, startDestination = "chat_list") {
        composable("chat_list") {
            ChatListScreen(
                chats = chatBoxes,
                onChatSelected = { navController.navigate("chat/$it") }
            )
        }
        composable("chat/{boxId}", arguments = listOf(navArgument("boxId"){ type = NavType.LongType })) { back ->
            val boxId = back.arguments!!.getLong("boxId")
            val factory = ChatViewModelFactory(boxId.toInt(), repo)
            val vm: ChatViewModel = viewModel(factory = factory)
            val dtos by vm.messages.collectAsState()
            ChatScreen(
                chatTitle = "Chat #$boxId",
                messages  = dtos.map { dto ->
                    MessageUi(
                        id        = dto.id.toString(),
                        text      = dto.text,
                        isMine    = (dto.userId == localUserId),
                        timestamp = Date.from(parseInstant(dto.sentAt))
                    )
                },
                onSend = { vm.send(it) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
