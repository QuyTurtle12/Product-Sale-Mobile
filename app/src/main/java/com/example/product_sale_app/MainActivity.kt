
package com.example.product_sale_app
import java.util.Date
import androidx.navigation.navArgument

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.example.product_sale_app.ui.chat.*

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.product_sale_app.ui.theme.ProductSaleAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProductSaleAppTheme {
                val nav = rememberNavController()

                NavHost(nav, startDestination = "chat_list") {
                    // 1) Chat list screen
                    composable("chat_list") {
                        // TODO: replace with real data
                        val dummyChats = listOf(
                            ChatBox(0, "General", "Welcome!", Date()),
                            ChatBox(1, "Support", "How can we help?", Date())
                        )

                        ChatListScreen(dummyChats) { boxId ->
                            nav.navigate("chat/$boxId")
                        }
                    }

                    // 2) Single chat screen
                    composable(
                        "chat/{boxId}",
                        arguments = listOf(navArgument("boxId") { type = NavType.LongType })
                    ) { back ->
                        val boxId = back.arguments!!.getLong("boxId")

                        // TODO: hook up ViewModel here
                        // for now just show an empty chat
                        ChatScreen(
                            chatTitle = "Box #$boxId",
                            messages = emptyList(),
                            onSend = { /* TODO: send via API */ },
                            onBack = { nav.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
