package com.example.randomchat.activities.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.randomchat.R;
import com.example.randomchat.entities.Friend;
import com.example.randomchat.entities.Message;

import java.time.format.DateTimeFormatter;
import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private final List<Message> mMessageList;


    public MessageAdapter(List<Message> messagesList) {
        mMessageList = messagesList;
    }


    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);

        if (message.getSender() instanceof Friend) {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }else {
            return VIEW_TYPE_MESSAGE_SENT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);

        }else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
        }

        return new MessageHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = mMessageList.get(position);
        ((MessageHolder) holder).bind(message);
    }


    private static class MessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        MessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {
            messageText.setText(message.getBody());

            DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("HH:mm");
            String sentTime = formatter.format(message.getTime());
            timeText.setText(sentTime);
        }
    }

}