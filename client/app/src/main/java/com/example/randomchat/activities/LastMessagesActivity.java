package com.example.randomchat.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.randomchat.R;
import com.example.randomchat.activities.adapters.MessageAdapter;
import com.example.randomchat.entities.Message;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;


public class LastMessagesActivity extends AppCompatActivity {

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        MaterialToolbar materialToolbar = findViewById(R.id.topAppBar);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        RelativeLayout sendLayout = findViewById(R.id.sendMessage);
        sendLayout.setVisibility(RecyclerView.GONE);

        TextView timerTextView = findViewById(R.id.time);
        timerTextView.setVisibility(View.INVISIBLE);

        Bundle bundle = getIntent().getExtras();

        materialToolbar.setTitle(bundle.getString("friend"));
        materialToolbar.setNavigationOnClickListener(view -> finish());

        ArrayList<Message> messages = (ArrayList<Message>) bundle.getSerializable("messages");
        MessageAdapter messageAdapter = new MessageAdapter(messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(messageAdapter);

    }


}