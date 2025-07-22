package com.example.product_sale_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.product_sale_app.R;
import com.example.product_sale_app.model.chat.ChatMessageDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatListAdapter
        extends RecyclerView.Adapter<ChatListAdapter.VH> {

    public interface Listener { void onChatClicked(int boxId); }

    private final List<ChatMessageDto> items;
    private final Listener listener;
    private final Map<Integer, String> usernames = new HashMap<>();

    public ChatListAdapter(List<ChatMessageDto> initial, Listener listener) {
        this.items = new ArrayList<>(initial);
        this.listener = listener;
    }
    public void setUsername(int boxId, String username) {
        usernames.put(boxId, username);
        notifyDataSetChanged();
    }

    public void updateData(List<ChatMessageDto> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void upsertLastMessage(ChatMessageDto msg) {
        // find existing
        int existingIndex = -1;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).boxId == msg.boxId) {
                existingIndex = i;
                break;
            }
        }
        if (existingIndex != -1) {
            items.remove(existingIndex);
        }
        // add newest at front
        items.add(0, msg);
        notifyDataSetChanged();
    }

    public static List<ChatMessageDto> extractLastPerBox(List<ChatMessageDto> all) {
        Map<Integer, ChatMessageDto> map = new HashMap<>();
        for (ChatMessageDto x : all) {
            ChatMessageDto prev = map.get(x.boxId);
            if (prev == null || x.sentAt.compareTo(prev.sentAt) > 0) {
                map.put(x.boxId, x);
            }
        }
        return new ArrayList<>(map.values());
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_box, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        ChatMessageDto d = items.get(position);
        String title = usernames.containsKey(d.boxId)
                ? usernames.get(d.boxId)
                : ("Chat #" + d.boxId);
        holder.tvTitle.setText(title);

        holder.tvLast.setText(d.text);
        String t = d.sentAt.length() >= 16
                ? d.sentAt.substring(11, 16)
                : d.sentAt;
        holder.tvTime.setText(t);

        holder.itemView.setOnClickListener(v -> listener.onChatClicked(d.boxId));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvLast, tvTime;
        VH(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvLast  = v.findViewById(R.id.tvLastMessage);
            tvTime  = v.findViewById(R.id.tvTime);
        }
    }
}