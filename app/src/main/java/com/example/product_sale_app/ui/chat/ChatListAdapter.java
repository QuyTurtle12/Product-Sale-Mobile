// src/main/java/com/example/product_sale_app/ui/chat/ChatListAdapter.java
package com.example.product_sale_app.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.product_sale_app.R;
import com.example.product_sale_app.data.ChatMessageDto;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatListAdapter
        extends RecyclerView.Adapter<ChatListAdapter.VH> {

    public interface Listener {
        void onChatClicked(int boxId);
    }

    private final List<ChatMessageDto> items;
    private final Listener listener;

    public ChatListAdapter(List<ChatMessageDto> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_box, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        ChatMessageDto dto = items.get(position);
        holder.tvTitle.setText("Chat #" + dto.boxId);
        holder.tvLast.setText(dto.text);
        // parse sentAt into Date for formatting:
        Date dt = Date.from(java.time.Instant.parse(dto.sentAt + "Z"));
        holder.tvTime.setText(
                new SimpleDateFormat("HH:mm", Locale.getDefault()).format(dt)
        );
        holder.itemView.setOnClickListener(v -> listener.onChatClicked(dto.boxId));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /** Replace the adapter’s data and refresh. */
    public void updateData(List<ChatMessageDto> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    /**
     * From a flat list of messages, pick the last (most recent) message
     * in each chatBoxId, preserving box‐order by first appearance.
     */
    public static List<ChatMessageDto> extractLastPerBox(List<ChatMessageDto> allMessages) {
        Map<Integer, ChatMessageDto> lastMap = new LinkedHashMap<>();
        for (ChatMessageDto msg : allMessages) {
            // overwrite as we go → finally holds the last message per box
            lastMap.put(msg.boxId, msg);
        }
        return new ArrayList<>(lastMap.values());
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLast, tvTime;
        VH(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLast  = itemView.findViewById(R.id.tvLastMessage);
            tvTime  = itemView.findViewById(R.id.tvTime);
        }
    }
}
