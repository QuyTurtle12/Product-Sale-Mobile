// src/main/java/com/example/product_sale_app/ui/chat/ChatListAdapter.java
package com.example.product_sale_app.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.product_sale_app.R;
import com.example.product_sale_app.data.ChatMessageDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatListAdapter
        extends RecyclerView.Adapter<ChatListAdapter.VH> {

    public interface Listener {
        void onChatClicked(int boxId);
    }

    private final List<ChatMessageDto> items;
    private final Listener listener;

    /** Now takes both an initial list *and* a click‐listener. */
    public ChatListAdapter(List<ChatMessageDto> initialItems, Listener listener) {
        this.items = new ArrayList<>(initialItems);
        this.listener = listener;
    }

    /** Replace the entire contents of the list and redraw. */
    public void updateData(List<ChatMessageDto> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    /**
     * From a full stream of messages, pick exactly one (the latest)
     * per chatBoxId.
     */
    public static List<ChatMessageDto> extractLastPerBox(List<ChatMessageDto> allMessages) {
        Map<Integer, ChatMessageDto> lastByBox = new HashMap<>();
        for (ChatMessageDto msg : allMessages) {
            ChatMessageDto prev = lastByBox.get(msg.boxId);
            if (prev == null || msg.sentAt.compareTo(prev.sentAt) > 0) {
                lastByBox.put(msg.boxId, msg);
            }
        }
        return new ArrayList<>(lastByBox.values());
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

        // Quickly pull HH:mm out of ISO timestamp
        String time = dto.sentAt.length() >= 16
                ? dto.sentAt.substring(11, 16)
                : dto.sentAt;
        holder.tvTime.setText(time);

        holder.itemView.setOnClickListener(v ->
                listener.onChatClicked(dto.boxId)
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvLast, tvTime;
        VH(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLast  = itemView.findViewById(R.id.tvLastMessage);
            tvTime  = itemView.findViewById(R.id.tvTime);
        }
    }
}
