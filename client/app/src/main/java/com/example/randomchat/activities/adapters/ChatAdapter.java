package com.example.randomchat.activities.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.randomchat.R;
import com.example.randomchat.controller.Controller;
import com.example.randomchat.entities.Friend;
import com.example.randomchat.entities.Message;
import com.example.randomchat.entities.User;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final HashMap<Friend, ArrayList<Message>> mChats;
    private final ArrayList<Friend> mUsers;


    public ChatAdapter(HashMap<Friend, ArrayList<Message>> chats) {
        this.mChats = chats;
        mUsers = new ArrayList<>(mChats.keySet());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User iUser = mUsers.get(position);
        String user =  iUser.getNickname();
        holder.chatUser.setText(user);

        ArrayList<Message> messages = mChats.get(iUser);
        assert messages != null;
        Message lastMessage = messages.get(messages.size()-1);
        holder.chatTime.setText(lastMessage.getTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        holder.chatLastMessage.setText(lastMessage.getBody());
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView chatUser;
        TextView chatTime;
        TextView chatLastMessage;


        ViewHolder(View itemView) {
            super(itemView);
            chatUser = itemView.findViewById(R.id.chat_user);
            chatTime = itemView.findViewById(R.id.chat_time);
            chatLastMessage = itemView.findViewById(R.id.chat_last_message);
            itemView.setOnClickListener(view -> {
                Friend friend = mUsers.get(getAdapterPosition());
                Controller.getInstance().showLastChat(friend.getNickname(), mChats.get(friend));
            });
        }

    }


}