package com.example.product_sale_app.ui.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
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
    private int localUserId;
    private String JWT;
    private ChatRepository repo;
    private ChatListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // same behavior as Up button
        toolbar.setNavigationOnClickListener(v -> finish());

        // Load token
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = settings.getString("token", null);
        if (token == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Build JWT and extract userId
        JWT = "Bearer " + token;
        localUserId = ChatRepository.extractUserIdFromJwt(token);

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

        // — Role check —
        String role = ChatRepository.extractRoleFromJwt(repo.getJwtToken());
        if ("Customer".equalsIgnoreCase(role)) {
            // Customers only ever see their own box
            Intent i = new Intent(this, ChatActivity.class);
            i.putExtra("boxId", localUserId);
            startActivity(i);
            finish();
            return;
        }

        // — Admin sees _all_ chats —
        RecyclerView rv = findViewById(R.id.rvChats);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatListAdapter(new ArrayList<>(), boxId -> {
            Intent i = new Intent(this, ChatActivity.class);
            i.putExtra("boxId", boxId);
            startActivity(i);
        });
        rv.setAdapter(adapter);

        // load all messages (no userId filter → admin)
        repo.loadAllMessages(null, new ChatRepository.CallbackFn<List<ChatMessageDto>>() {
                    @Override
                    public void onSuccess(List<ChatMessageDto> msgs) {
                        List<ChatMessageDto> lastPerBox =
                                ChatListAdapter.extractLastPerBox(msgs);
                        runOnUiThread(() -> adapter.updateData(lastPerBox));
                    }
                    @Override
                    public void onError(Throwable t) {
                        runOnUiThread(() ->
                                Toast.makeText(ChatListActivity.this,
                                        "Failed to load chats", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_store, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle the Up (back arrow) button
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        // handle your store icon
        if (item.getItemId() == R.id.action_store) {
            startActivity(new Intent(this, StoreMapActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
