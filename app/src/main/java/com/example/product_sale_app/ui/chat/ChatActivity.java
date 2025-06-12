package com.example.product_sale_app.ui.chat;

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
import com.example.product_sale_app.data.ChatApiService;
import com.example.product_sale_app.data.ChatMessageDto;
import com.example.product_sale_app.data.ChatRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {
    private static final int LOCAL_USER_ID = 16;
    private static final String HARDCODED_JWT = "Bearer " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxNiIsInVuaXF1ZV9uYW1lIjoiY3VzdG9tZXIiLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3JvbGUiOiJDdXN0b21lciIsImVtYWlsIjoiY3VzdG9tZXJAY3VzdG9tZXIuY29tIiwianRpIjoiOWZjOTE4YjMtYzIwMC00OGNkLWE2MGItNzAxODIyZDlhYmQ0IiwibmJmIjoxNzQ5NzE2MDU1LCJleHAiOjE3NDk3MTk2NTUsImlzcyI6IlBSTTM5MiIsImF1ZCI6IlBSTTM5MiJ9.rlIDvOSFeu4K8_c0RPW7fuuLcD8EB50VvjtjJlDYCcs";
    private ChatRepository repo;
    private int boxId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 1) Toolbar + back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // 2) RecyclerView + adapter
        RecyclerView rv = findViewById(R.id.rvMessages);
        rv.setLayoutManager(new LinearLayoutManager(this));
        MessageAdapter adapter = new MessageAdapter(new ArrayList<>(), LOCAL_USER_ID);
        rv.setAdapter(adapter);

        // 3) Input widgets
        EditText etMessage = findViewById(R.id.etMessage);
        Button btnSend   = findViewById(R.id.btnSend);

        // 4) Box ID
        boxId = getIntent().getIntExtra("boxId", 0);

        // 5) OkHttp + logging + JWT interceptor
        HttpLoggingInterceptor log = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);
        Interceptor auth = new Interceptor() {
            @Override public Response intercept(Chain chain) throws IOException {
                Request req = chain.request()
                        .newBuilder()
                        .header("Authorization", HARDCODED_JWT)
                        .build();
                return chain.proceed(req);
            }
        };
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(log)
                .addInterceptor(auth)
                .build();

        // 6) Retrofit → API → Repo
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5006/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ChatApiService api = retrofit.create(ChatApiService.class);
        repo = new ChatRepository(api);

        // 7) Load history
        repo.loadBoxMessages(boxId, new ChatRepository.CallbackFn<List<ChatMessageDto>>() {
            @Override public void onSuccess(List<ChatMessageDto> msgs) {
                runOnUiThread(() -> {
                    adapter.updateData(msgs);
                    rv.scrollToPosition(adapter.getItemCount() - 1);
                });
            }
            @Override public void onError(Throwable t) {
                Log.e("ChatActivity","Load failed",t);
            }
        });

        // 8) Send button
        btnSend.setOnClickListener(v -> {
            Log.d("ChatActivity","SEND clicked");
            Toast.makeText(this,"SEND clicked",Toast.LENGTH_SHORT).show();

            String text = etMessage.getText().toString().trim();
            if (text.isEmpty()) return;
            repo.sendMessage(boxId, text, new ChatRepository.CallbackFn<ChatMessageDto>() {
                @Override public void onSuccess(ChatMessageDto m) {
                    runOnUiThread(() -> {
                        adapter.addMessage(m);
                        rv.scrollToPosition(adapter.getItemCount() - 1);
                        etMessage.setText("");
                    });
                }
                @Override public void onError(Throwable t) {
                    Log.e("ChatActivity","Send failed",t);
                }
            });
        });
    }
}
