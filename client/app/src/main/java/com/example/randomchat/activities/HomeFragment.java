package com.example.randomchat.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.randomchat.R;
import com.example.randomchat.activities.adapters.RoomAdapter;
import com.example.randomchat.controller.Controller;
import com.example.randomchat.entities.Room;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private Bundle bundle;
    private RoomAdapter roomAdapter;

    public HomeFragment() { Controller.getInstance().setHomeFragment(this); }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        assert getArguments() != null;
        bundle = getArguments();
        ArrayList<Room> rooms = (ArrayList<Room>) bundle.getSerializable("rooms");

        recyclerView = view.findViewById(R.id.rooms_recycleView);
        if(roomAdapter == null)
            roomAdapter = new RoomAdapter(rooms);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(roomAdapter);

    }

    @SuppressWarnings("unchecked")
    public void updateRooms(int position, int newSize) {
        ArrayList<Room> rooms = (ArrayList<Room>) bundle.getSerializable("rooms");
        rooms.get(position).setSize(newSize);
        if(this.isVisible()) {
            requireActivity().runOnUiThread(()->
                    Objects.requireNonNull(recyclerView.getAdapter()).notifyItemChanged(position)
            );
        }
    }

}
