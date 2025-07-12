package com.example.product_sale_app.ui.chat;

import static com.example.product_sale_app.ui.home.LoginActivity.PREFS_NAME;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.product_sale_app.R;
import com.example.product_sale_app.network.service.ChatApiService;
import com.example.product_sale_app.model.chat.ChatMessageDto;
import com.example.product_sale_app.repository.ChatRepository;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {
    private int localUserId;
    private String JWT;
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

        // Toolbar + back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Load token & extract userId
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = settings.getString("token", null);
        if (token == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        JWT = "Bearer " + token;
        localUserId = ChatRepository.extractUserIdFromJwt(token);

        // RecyclerView & adapter
        rvMessages = findViewById(R.id.rvMessages);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(new ArrayList<>(), localUserId);
        rvMessages.setAdapter(adapter);

        // Box ID from intent
        boxId = getIntent().getIntExtra("boxId", 0);

        // Retrofit + OkHttp
        HttpLoggingInterceptor log = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);
        Interceptor auth = chain -> {
            Request req = chain.request()
                    .newBuilder()
                    .header("Authorization", JWT)
                    .build();
            return chain.proceed(req);
        };
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(log)
                .addInterceptor(auth)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5006/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        repo = new ChatRepository(retrofit.create(ChatApiService.class), JWT);

        // Load chat history
        repo.loadBoxMessages(boxId, new ChatRepository.CallbackFn<List<ChatMessageDto>>() {
            @Override
            public void onSuccess(List<ChatMessageDto> msgs) {
                runOnUiThread(() -> {
                    adapter.updateData(msgs);
                    rvMessages.scrollToPosition(adapter.getItemCount() - 1);
                });
            }
            @Override
            public void onError(Throwable t) {
                Log.e("ChatActivity", "Load failed", t);
            }
        });

        // Send new messages
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (text.isEmpty()) return;

            repo.sendMessage(boxId, text, new ChatRepository.CallbackFn<ChatMessageDto>() {
                @Override
                public void onSuccess(ChatMessageDto msg) {
                    runOnUiThread(() -> {
                        adapter.addMessage(msg);
                        rvMessages.scrollToPosition(adapter.getItemCount() - 1);
                        etMessage.setText("");
                    });
                }
                @Override
                public void onError(Throwable t) {
                    Log.e("ChatActivity", "Send failed", t);
                    Toast.makeText(ChatActivity.this, "Failed to send", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
