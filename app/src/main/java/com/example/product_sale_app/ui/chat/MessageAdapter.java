package com.example.product_sale_app.ui.chat;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.product_sale_app.data.ChatMessageDto;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.VH> {
    private final List<ChatMessageDto> data;

    public MessageAdapter(List<ChatMessageDto> data) {
        this.data = data;
    }

    /** Replace entire list of messages */
    public void updateData(List<ChatMessageDto> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    /** Append a single new message */
    public void addMessage(ChatMessageDto msg) {
        data.add(msg);
        notifyItemInserted(data.size() - 1);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        // Just a plain TextView per item
        TextView tv = new TextView(parent.getContext());
        tv.setPadding(16, 8, 16, 8);
        return new VH(tv);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        ChatMessageDto m = data.get(position);
        holder.tv.setText(m.username + ": " + m.text);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tv;
        VH(View itemView) {
            super(itemView);
            tv = (TextView) itemView;
        }
    }
}
