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

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {
    private static final int LOCAL_USER_ID = 16;
    private static final String HARDCODED_JWT =
            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxNiIsInVuaXF1ZV9uYW1lIjoiY3VzdG9tZXIiLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3JvbGUiOiJDdXN0b21lciIsImVtYWlsIjoiY3VzdG9tZXJAY3VzdG9tZXIuY29tIiwianRpIjoiOTU5YmYzNzQtMTU3Ny00YmVlLTgzMjItYzhjNmUyMjI4MmNjIiwibmJmIjoxNzQ5NzMxNzQ3LCJleHAiOjE3NDk3MzUzNDcsImlzcyI6IlBSTTM5MiIsImF1ZCI6IlBSTTM5MiJ9.9qyPR-wRNDa0i5FP8nGNJiaFYog9mq8VDJ9Z1TivBH8";  // your test token

    private ChatRepository repo;
    private MessageAdapter adapter;
    private RecyclerView rvMessages;
    private EditText etMessage;
    private Button btnSend;
    private int boxId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // — Toolbar with back arrow —
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // — RecyclerView + adapter —
        rvMessages = findViewById(R.id.rvMessages);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(new ArrayList<>(), LOCAL_USER_ID);
        rvMessages.setAdapter(adapter);

        // — Input field + Send button —
        etMessage = findViewById(R.id.etMessage);
        btnSend   = findViewById(R.id.btnSend);

        // — Which chat-box ID did we tap on? —
        boxId = getIntent().getIntExtra("boxId", 0);

        // — Build OkHttpClient (logging + JWT) —
        HttpLoggingInterceptor log = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);
        Interceptor auth = chain -> {
            Request req = chain.request()
                    .newBuilder()
                    .header("Authorization", HARDCODED_JWT)
                    .build();
            return chain.proceed(req);
        };
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(log)
                .addInterceptor(auth)
                .build();

        // — Retrofit → service → repo —
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5006/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        repo = new ChatRepository(retrofit.create(ChatApiService.class));

        // — Load history (async) —
        repo.loadBoxMessages(boxId, new ChatRepository.CallbackFn<List<ChatMessageDto>>() {
            @Override public void onSuccess(List<ChatMessageDto> msgs) {
                runOnUiThread(() -> {
                    adapter.updateData(msgs);
                    rvMessages.scrollToPosition(adapter.getItemCount() - 1);
                });
            }
            @Override public void onError(Throwable t) {
                Log.e("ChatActivity", "Load failed", t);
            }
        });

        // — Send button (async) — no finish()
        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (text.isEmpty()) return;

            repo.sendMessage(boxId, text, new ChatRepository.CallbackFn<ChatMessageDto>() {
                @Override public void onSuccess(ChatMessageDto msg) {
                    runOnUiThread(() -> {
                        adapter.addMessage(msg);
                        rvMessages.scrollToPosition(adapter.getItemCount() - 1);
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
