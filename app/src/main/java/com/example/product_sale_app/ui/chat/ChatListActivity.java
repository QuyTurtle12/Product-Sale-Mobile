package com.example.product_sale_app.ui.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import static com.example.product_sale_app.ui.home.LoginActivity.PREFS_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.product_sale_app.R;
import com.example.product_sale_app.network.service.ChatApiService;
import com.example.product_sale_app.model.chat.ChatMessageDto;
import com.example.product_sale_app.repository.ChatRepository;
import com.example.product_sale_app.ui.map.StoreMapActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatListActivity extends AppCompatActivity {
    private static final int LOCAL_USER_ID = 16;
    private String JWT;
    private ChatRepository repo;
    private ChatListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // — Toolbar —
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = settings.getString("token", null);
        if (token == null) {
            // no token saved – you may want to redirect to login
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        JWT = "Bearer " + token;

        // — Build Retrofit + OKHttp with logging & auth header —
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

        // — Instantiate repo with the JWT —
        repo = new ChatRepository(retrofit.create(ChatApiService.class), JWT);

        // — Role check —
        String role = ChatRepository.extractRoleFromJwt(repo.getJwtToken());
        if ("Customer".equalsIgnoreCase(role)) {
            // Customers: find *their* chat-box id, then go straight to ChatActivity
            repo.loadAllMessages(LOCAL_USER_ID, new ChatRepository.CallbackFn<List<ChatMessageDto>>() {
                @Override
                public void onSuccess(List<ChatMessageDto> msgs) {
                    // collect distinct boxIds
                    Set<Integer> boxIds = new HashSet<>();
                    for (ChatMessageDto m : msgs) {
                        boxIds.add(m.boxId);
                    }
                    if (boxIds.isEmpty()) {
                        runOnUiThread(() ->
                                Toast.makeText(ChatListActivity.this,
                                        "No conversations found", Toast.LENGTH_SHORT).show()
                        );
                        return;
                    }
                    // take the first boxId (or implement your own logic)
                    int boxId = boxIds.iterator().next();
                    runOnUiThread(() -> {
                        Intent i = new Intent(ChatListActivity.this, ChatActivity.class);
                        i.putExtra("boxId", boxId);
                        startActivity(i);
                        finish();
                    });
                }
                @Override
                public void onError(Throwable t) {
                    runOnUiThread(() ->
                            Toast.makeText(ChatListActivity.this,
                                    "Failed to load your conversations", Toast.LENGTH_SHORT).show()
                    );
                }
            });
            return;
        }

        // — Admins: show the list as before —
        RecyclerView rv = findViewById(R.id.rvChats);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatListAdapter(new ArrayList<>(), boxId -> {
            Intent i = new Intent(this, ChatActivity.class);
            i.putExtra("boxId", boxId);
            startActivity(i);
        });
        rv.setAdapter(adapter);

        // load all messages and display last per box
        repo.loadAllMessages(LOCAL_USER_ID, new ChatRepository.CallbackFn<List<ChatMessageDto>>() {
            @Override
            public void onSuccess(List<ChatMessageDto> msgs) {
                List<ChatMessageDto> lastPerBox = ChatListAdapter.extractLastPerBox(msgs);
                runOnUiThread(() -> adapter.updateData(lastPerBox));
            }
            @Override
            public void onError(Throwable t) {
                runOnUiThread(() ->
                        Toast.makeText(ChatListActivity.this,
                                "Failed to load chats", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_store, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_store) {
            startActivity(new Intent(this, StoreMapActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
