package com.example.product_sale_app.ui.chat;

import static com.example.product_sale_app.ui.home.LoginActivity.PREFS_NAME;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.product_sale_app.R;
import com.example.product_sale_app.adapter.ChatListAdapter;
import com.example.product_sale_app.model.chat.ChatMessageDto;
import com.example.product_sale_app.model.BaseResponseModel;
import com.example.product_sale_app.network.RetrofitClient;
import com.example.product_sale_app.network.service.AuthApiService;
import com.example.product_sale_app.network.service.ChatApiService;
import com.example.product_sale_app.repository.ChatRepository;
import com.example.product_sale_app.ui.home.LoginActivity;
import com.example.product_sale_app.ui.map.StoreMapActivity;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ChatListActivity extends AppCompatActivity {
    private int localUserId;
    private String JWT;
    private ChatRepository repo;
    private ChatListAdapter adapter;
    private HubConnection hub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Load token & userId
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = settings.getString("token", null);
        if (token == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        JWT = "Bearer " + token;
        localUserId = ChatRepository.extractUserIdFromJwt(token);

        // Prepare Retrofit & repo
        ChatApiService apiService = RetrofitClient.createService(this, ChatApiService.class);
        repo = new ChatRepository(apiService, JWT);

        // Role check
        String role = ChatRepository.extractRoleFromJwt(token);
        if ("Customer".equalsIgnoreCase(role)) {
            Intent i = new Intent(this, ChatActivity.class);
            i.putExtra("boxId", localUserId);
            startActivity(i);
            finish();
            return;
        }

        // RecyclerView & adapter
        RecyclerView rv = findViewById(R.id.rvChats);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatListAdapter(new ArrayList<>(), boxId -> {
            Intent i = new Intent(this, ChatActivity.class);
            i.putExtra("boxId", boxId);
            startActivity(i);
        });
        rv.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Setup SignalR
        if (hub == null) {
            hub = HubConnectionBuilder.create(RetrofitClient.HUB_URL)
                    .withHeader("Authorization", JWT)
                    .build();
            hub.on("ReceiveMessage", msg -> runOnUiThread(() ->
                    adapter.upsertLastMessage(msg)
            ), ChatMessageDto.class);
        }
        hub.start().subscribe(
                () -> repo.loadAllMessages(null, new ChatRepository.CallbackFn<List<ChatMessageDto>>() {
                    @Override public void onSuccess(List<ChatMessageDto> msgs) {
                        List<ChatMessageDto> lastPerBox = ChatListAdapter.extractLastPerBox(msgs);
                        runOnUiThread(() -> {
                            adapter.updateData(lastPerBox);

                            for (ChatMessageDto m : lastPerBox) {
                                int boxId = m.getChatBoxId();
                                hub.invoke("JoinBox", String.valueOf(boxId));

                                // — fetch username for this boxId —
                                AuthApiService authApi =
                                        RetrofitClient.createService(
                                                ChatListActivity.this,
                                                AuthApiService.class
                                        );
                                authApi.getUsernameById(boxId)
                                        .enqueue(new retrofit2.Callback<BaseResponseModel<String>>() {
                                            @Override
                                            public void onResponse(
                                                    Call<BaseResponseModel<String>> call,
                                                    Response<BaseResponseModel<String>> resp
                                            ) {
                                                if (resp.isSuccessful() && resp.body() != null) {
                                                    String name = resp.body().getData();
                                                    runOnUiThread(() ->
                                                            adapter.setUsername(boxId, name)
                                                    );
                                                }
                                            }

                                            @Override
                                            public void onFailure(
                                                    Call<BaseResponseModel<String>> call,
                                                    Throwable t
                                            ) {
                                                // ignore or log
                                            }
                                        });
                            }
                        });
                    }
                    @Override public void onError(Throwable t) {
                        runOnUiThread(() ->
                                Toast.makeText(
                                        ChatListActivity.this,
                                        "Failed to load chats",
                                        Toast.LENGTH_SHORT
                                ).show()
                        );
                    }
                }),
                error -> {
                    Log.e("ChatListActivity", "SignalR start failed", error);
                    // fallback load
                    repo.loadAllMessages(null, new ChatRepository.CallbackFn<List<ChatMessageDto>>() {
                        @Override
                        public void onSuccess(List<ChatMessageDto> msgs) {
                            List<ChatMessageDto> lastPerBox = ChatListAdapter.extractLastPerBox(msgs);
                            runOnUiThread(() -> adapter.updateData(lastPerBox));
                            for (ChatMessageDto m : lastPerBox) {
                                hub.invoke("JoinBox", String.valueOf(m.getChatBoxId()));
                            }
                        }
                        @Override
                        public void onError(Throwable t) { /* … */ }
                    });
                }
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (hub != null) {
            hub.stop();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_store, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.action_store) {
            startActivity(new Intent(this, StoreMapActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // clear saved login state:
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().clear().apply();

        // return to login screen
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
