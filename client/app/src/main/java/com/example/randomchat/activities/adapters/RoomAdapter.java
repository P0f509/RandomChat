package com.example.randomchat.activities.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.randomchat.R;
import com.example.randomchat.controller.Controller;
import com.example.randomchat.entities.Room;

import java.util.ArrayList;


public class RoomAdapter extends RecyclerView.Adapter {

    private final ArrayList<Room> rooms;
    private final ArrayList<Integer> draws = new ArrayList<>();

    public RoomAdapter(ArrayList<Room> rooms) {
        this.rooms = rooms;

        draws.add(R.drawable.ic_games);
        draws.add(R.drawable.ic_music);
        draws.add(R.drawable.ic_movie);
        draws.add(R.drawable.ic_sport);
        draws.add(R.drawable.ic_food);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room, parent, false);
        return new RoomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String name = rooms.get(position).getName();
        int size = rooms.get(position).getSize();
        String description = rooms.get(position).getDescription();
        ((RoomHolder) holder).bind(name, size, description, draws.get(position));
    }

    @Override
    public int getItemCount() { return rooms.size(); }


    private static class RoomHolder extends RecyclerView.ViewHolder {

         TextView roomName;
         TextView roomSize;
         TextView roomDescription;
         ImageView drawable;

         RoomHolder(@NonNull View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.roomName_textView);
            roomSize = itemView.findViewById(R.id.roomSize_textView);
            roomDescription = itemView.findViewById(R.id.roomDescription_textView);
            drawable = itemView.findViewById(R.id.room_draw);
            itemView.setOnClickListener(view -> Controller.getInstance().searchFriend(getAdapterPosition()));
        }

        @SuppressLint("SetTextI18n")
        void bind(String name, int size, String description, int draw) {
            roomName.setText(name);
            roomSize.setText(size+" online");
            roomDescription.setText(description);
            drawable.setImageResource(draw);
        }

    }

}
