// src/main/java/com/example/product_sale_app/ui/chat/ChatListActivity.java
package com.example.product_sale_app.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.product_sale_app.R;
import com.example.product_sale_app.data.ChatApiService;
import com.example.product_sale_app.data.ChatMessageDto;
import com.example.product_sale_app.data.ChatRepository;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatListActivity extends AppCompatActivity {
    private RecyclerView rvChats;
    private ChatListAdapter adapter;
    private ChatRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        rvChats = findViewById(R.id.rvChats);
        rvChats.setLayoutManager(new LinearLayoutManager(this));

        // Pass initial empty list + listener
        adapter = new ChatListAdapter(new ArrayList<>(), boxId -> {
            Intent i = new Intent(this, ChatActivity.class);
            // NO CAST needed — boxId is already an int
            i.putExtra("boxId", boxId);
            startActivity(i);
        });
        rvChats.setAdapter(adapter);

        // Build Retrofit + repo
        HttpLoggingInterceptor log = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(log)
                .build();
        ChatApiService api = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5006/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ChatApiService.class);
        repo = new ChatRepository(api);

        // Load all messages, then show only the last per box
        repo.loadAllMessages(16, new ChatRepository.CallbackFn<List<ChatMessageDto>>() {
            @Override public void onSuccess(List<ChatMessageDto> msgs) {
                List<ChatMessageDto> lastPerBox =
                        ChatListAdapter.extractLastPerBox(msgs);
                runOnUiThread(() ->
                        adapter.updateData(lastPerBox)
                );
            }
            @Override public void onError(Throwable t) {
                // TODO: show an error toast/snackbar
            }
        });
    }
}
