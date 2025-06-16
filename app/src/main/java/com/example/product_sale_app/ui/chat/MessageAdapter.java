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

    public void updateData(List<ChatMessageDto> d) {
        data.clear();
        data.addAll(d);
        notifyDataSetChanged();
    }
    public void addMessage(ChatMessageDto m) {
        data.add(m);
        notifyItemInserted(data.size()-1);
    }

    @Override public VH onCreateViewHolder(ViewGroup p,int i) {
        View v = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_message,p,false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(VH h,int pos) {
        ChatMessageDto m = data.get(pos);
        boolean sent = m.userId!=null && m.userId==localUserId;
        h.tv.setBackgroundResource(
                sent ? R.drawable.bg_message_sent
                        : R.drawable.bg_message_received
        );
        int clr = sent
                ? R.color.chat_sent_text
                : R.color.chat_received_text;
        h.tv.setTextColor(ContextCompat.getColor(h.tv.getContext(), clr));

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)h.tv.getLayoutParams();
        lp.gravity = sent? Gravity.END:Gravity.START;
        h.tv.setLayoutParams(lp);

        h.tv.setText(m.text);
    }

    @Override public int getItemCount(){ return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tv;
        VH(View v) {
            super(v);
            tv = v.findViewById(R.id.tvMessage);
        }
    }
}
