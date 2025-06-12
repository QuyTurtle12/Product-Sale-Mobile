// src/main/java/com/example/product_sale_app/ui/chat/MessageAdapter.java
package com.example.product_sale_app.ui.chat;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.product_sale_app.R;
import com.example.product_sale_app.data.ChatMessageDto;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.VH> {
    private final List<ChatMessageDto> data;
    private final int localUserId;

    public MessageAdapter(List<ChatMessageDto> data, int localUserId) {
        this.data = data;
        this.localUserId = localUserId;
    }

    /** Completely replace the list and redraw. */
    public void updateData(List<ChatMessageDto> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    /** Add one new message at the bottom. */
    public void addMessage(ChatMessageDto msg) {
        data.add(msg);
        notifyItemInserted(data.size() - 1);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new VH(item);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        ChatMessageDto msg = data.get(position);
        boolean isSent = msg.userId != null && msg.userId == localUserId;

        // background
        holder.tv.setBackgroundResource(
                isSent
                        ? R.drawable.bg_message_sent
                        : R.drawable.bg_message_received
        );

        // text color via ContextCompat for API 21+
        int colorRes = isSent
                ? R.color.chat_sent_text
                : R.color.chat_received_text;
        holder.tv.setTextColor(ContextCompat.getColor(holder.tv.getContext(), colorRes));

        // left vs right alignment
        FrameLayout.LayoutParams lp =
                (FrameLayout.LayoutParams) holder.tv.getLayoutParams();
        lp.gravity = isSent ? Gravity.END : Gravity.START;
        holder.tv.setLayoutParams(lp);

        // text content
        holder.tv.setText(msg.username + ": " + msg.text);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tv;
        VH(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tvMessage);
        }
    }
}
