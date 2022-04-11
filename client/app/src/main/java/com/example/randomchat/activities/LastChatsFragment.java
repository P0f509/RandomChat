package com.example.randomchat.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.randomchat.R;
import com.example.randomchat.activities.adapters.ChatAdapter;
import com.example.randomchat.controller.Controller;
import com.example.randomchat.entities.Friend;
import com.example.randomchat.entities.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class LastChatsFragment extends Fragment {

    private final HashMap<Friend, ArrayList<Message>> chats = new HashMap<>();


    public LastChatsFragment() {
        Controller.getInstance().setLastChatsFragment(this);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_last_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        LottieAnimationView animationView = view.findViewById(R.id.empty_animation);
        RecyclerView recyclerView = view.findViewById(R.id.chats);
        TextView infoTextView = view.findViewById(R.id.bottomTextView);

        if(!chats.isEmpty()) {
            animationView.setVisibility(View.GONE);
            infoTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(new ChatAdapter(chats));
        }else {
            animationView.setVisibility(View.VISIBLE);
            infoTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

    }


    public void updateChats(Friend friend, Message msg) {
        if(chats.containsKey(friend)) {
            Objects.requireNonNull(chats.get(friend)).add(msg);
        }else {
            ArrayList<Message> messages = new ArrayList<>();
            messages.add(msg);
            chats.put(friend, messages);
        }
    }


}
