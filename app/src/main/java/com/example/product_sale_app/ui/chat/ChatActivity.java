// src/main/java/com/example/product_sale_app/ui/chat/ChatActivity.java
package com.example.product_sale_app.ui.chat;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class ChatActivity extends AppCompatActivity {
    private static final int LOCAL_USER_ID = 16;

    private EditText etMessage;
    private Button btnSend;
    private ChatRepository repo;
    private int boxId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // RecyclerView + adapter
        RecyclerView rv = findViewById(R.id.rvMessages);
        rv.setLayoutManager(new LinearLayoutManager(this));
        MessageAdapter adapter = new MessageAdapter(new ArrayList<>(), LOCAL_USER_ID);
        rv.setAdapter(adapter);

        // Toolbar with back‐arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Retrieve boxId
        boxId = getIntent().getIntExtra("boxId", 0);

        etMessage = findViewById(R.id.etMessage);
        btnSend   = findViewById(R.id.btnSend);

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

        // Load existing messages
        repo.loadBoxMessages(boxId, new ChatRepository.CallbackFn<List<ChatMessageDto>>() {
            @Override public void onSuccess(List<ChatMessageDto> msgs) {
                runOnUiThread(() -> {
                    adapter.updateData(msgs);
                    rv.scrollToPosition(adapter.getItemCount() - 1);
                });
            }
            @Override public void onError(Throwable t) {
                Log.e("ChatActivity", "Load failed", t);
            }
        });

        // Send new message
        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (text.isEmpty()) return;
            repo.sendMessage(boxId, text, new ChatRepository.CallbackFn<ChatMessageDto>() {
                @Override public void onSuccess(ChatMessageDto msg) {
                    runOnUiThread(() -> {
                        adapter.addMessage(msg);
                        rv.scrollToPosition(adapter.getItemCount() - 1);
                        etMessage.setText("");
                    });
                }
                @Override public void onError(Throwable t) {
                    Log.e("ChatActivity", "Send failed", t);
                }
            });
        });
    }
}
