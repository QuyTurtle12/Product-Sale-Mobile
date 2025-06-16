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
    private ChatRepository repo;
    private ChatListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        RecyclerView rv = findViewById(R.id.rvChats);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatListAdapter(new ArrayList<>(), boxId -> {
            Intent i = new Intent(this, ChatActivity.class);
            i.putExtra("boxId", boxId);
            startActivity(i);
        });
        rv.setAdapter(adapter);

        HttpLoggingInterceptor log = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(log)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5006/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ChatApiService api = retrofit.create(ChatApiService.class);
        repo = new ChatRepository(api);

        // fetch all and show last per box
        repo.loadAllMessages(16, new ChatRepository.CallbackFn<List<ChatMessageDto>>() {
            @Override public void onSuccess(List<ChatMessageDto> msgs) {
                List<ChatMessageDto> lastPerBox = ChatListAdapter.extractLastPerBox(msgs);
                runOnUiThread(() -> adapter.updateData(lastPerBox));
            }
            @Override public void onError(Throwable t) { /* TODO: show error */ }
        });
    }
}
