// com/example/product_sale_app/ui/chat/ChatActivity.java
package com.example.product_sale_app.ui.chat;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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

public class ChatActivity extends AppCompatActivity {
    private RecyclerView rv;
    private EditText et;
    private Button btn;
    private MessageAdapter adapter;
    private ChatRepository repo;
    private int boxId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        boxId = getIntent().getIntExtra("boxId", 0);

        rv = findViewById(R.id.rvMessages);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(new ArrayList<>());
        rv.setAdapter(adapter);

        et  = findViewById(R.id.etMessage);
        btn = findViewById(R.id.btnSend);

        HttpLoggingInterceptor log = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(log).build();

        ChatApiService api = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5006/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ChatApiService.class);

        repo = new ChatRepository(api);

        // load box history
        repo.loadBoxMessages(boxId, new ChatRepository.CallbackFn<List<ChatMessageDto>>() {
            @Override public void onSuccess(List<ChatMessageDto> msgs) {
                runOnUiThread(() -> {
                    adapter.updateData(msgs);
                    rv.scrollToPosition(adapter.getItemCount() - 1);
                });
            }
            @Override public void onError(Throwable t) {
                Log.e("ChatActivity","Load failed", t);
            }
        });

        // send new
        btn.setOnClickListener(v -> {
            String text = et.getText().toString().trim();
            if (text.isEmpty()) return;
            repo.sendMessage(boxId, text, new ChatRepository.CallbackFn<ChatMessageDto>() {
                @Override public void onSuccess(ChatMessageDto msg) {
                    runOnUiThread(() -> {
                        adapter.addMessage(msg);
                        rv.scrollToPosition(adapter.getItemCount() - 1);
                        et.setText("");
                    });
                }
                @Override public void onError(Throwable t) {
                    Log.e("ChatActivity","Send failed", t);
                }
            });
        });
    }
}
