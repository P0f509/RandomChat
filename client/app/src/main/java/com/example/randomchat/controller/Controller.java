package com.example.randomchat.controller;

import android.content.Intent;

import com.example.randomchat.activities.HomeFragment;
import com.example.randomchat.activities.LastChatsFragment;
import com.example.randomchat.activities.LastMessagesActivity;
import com.example.randomchat.infrastructure.ClientSocket;
import com.example.randomchat.R;
import com.example.randomchat.activities.HomeActivity;
import com.example.randomchat.activities.MessagesActivity;
import com.example.randomchat.activities.WelcomeActivity;
import com.example.randomchat.activities.dialogs.LoadingDialog;
import com.example.randomchat.entities.Friend;
import com.example.randomchat.entities.Message;
import com.example.randomchat.entities.Room;
import com.example.randomchat.entities.User;

import java.time.LocalTime;
import java.util.ArrayList;

public class Controller {

    private static Controller instance = null;
    private final ClientSocket clientSocket;

    private User currentUser;
    private Friend friend;

    private final int ROOM_NUM = 5;
    private final ArrayList<Room> rooms = new ArrayList<>();

    private WelcomeActivity welcomeActivity;
    private HomeActivity homeActivity;
    private HomeFragment homeFragment;
    private LastChatsFragment lastChatsFragment;
    private MessagesActivity messagesActivity;

    private LoadingDialog loadingDialog;


    private Controller() {
        clientSocket = ClientSocket.getInstance();
        clientSocket.initSocket();
    }

    public static Controller getInstance() {
        if(instance == null)
            instance = new Controller();
        return instance;
    }


    /*******
     * SET UP
     */

    public void setUp(String nickname) {
        for(int i = 0; i < ROOM_NUM; ++i) {
            String name = welcomeActivity.getResources().getStringArray(R.array.rooms_names)[i];
            String description = welcomeActivity.getResources().getStringArray(R.array.rooms_descriptions)[i];
            rooms.add(new Room(name, i, description));
        }
        currentUser = new User(nickname);
        clientSocket.setNickname(nickname);
        Intent intent = new Intent(welcomeActivity, HomeActivity.class);
        intent.putExtra("rooms", rooms);
        intent.putExtra("nickname", nickname);
        welcomeActivity.startActivity(intent);
        welcomeActivity.finish();
    }


    /******
     * CHAT
     */

    public void startChat(String nickname) {
        friend = new Friend(nickname);
        loadingDialog.dismissDialog();
        Intent intent = new Intent(homeActivity, MessagesActivity.class);
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("friend", friend.getNickname());
        homeActivity.startActivity(intent);
    }

    public void sendToFriend(Message msg) {
        String command = "SND "+msg.getBody().length();
        clientSocket.sendMessage(command+" "+msg.getBody());
        lastChatsFragment.updateChats(friend, msg);
    }

    public void receiveFromFriend(String msg) {
        if(messagesActivity != null && !messagesActivity.isDestroyed()) {
            Message message = new Message(msg, LocalTime.now(), friend);
            messagesActivity.updateChat(message);
            lastChatsFragment.updateChats(friend, message);
        }
    }

    public void leaveChat() {
        clientSocket.sendMessage("LVE");
    }

    public void showLastChat(String friend, ArrayList<Message> messages) {
        Intent intent = new Intent(homeActivity, LastMessagesActivity.class);
        intent.putExtra("friend", friend);
        intent.putExtra("messages", messages);
        homeActivity.startActivity(intent);
    }


    /*******
     * ROOMS
     */

    public void searchFriend(int roomNumber) {
        currentUser.setRoom(rooms.get(roomNumber));
        loadingDialog = new LoadingDialog(homeActivity, homeActivity.getResources().getStringArray(R.array.rooms_names)[roomNumber]);
        loadingDialog.startLoading();
        clientSocket.sendMessage("SRC "+roomNumber);
    }

    public void leaveRoom() {
        clientSocket.sendMessage("QIT");
    }

    public void updateRooms(String roomInfo) {
        for(int i = 0; i < ROOM_NUM; ++i) {
            int size = Integer.parseInt(roomInfo.substring(i, i+1));
            if(size != rooms.get(i).getSize()) {
                rooms.get(i).setSize(size);
                if(homeFragment != null)
                    homeFragment.updateRooms(i, size);
            }
        }
    }


    /**************
     * INFORMATIONAL
     */

    public void onFriendNotFound() {
        loadingDialog.dismissDialog();
        homeActivity.onFail(homeActivity.getResources().getString(R.string.friend_not_found));
    }

    public void onFriendLeave() {
        if(messagesActivity != null && !messagesActivity.isDestroyed()) {
            messagesActivity.finish();
            homeActivity.onFail(homeActivity.getResources().getString(R.string.friend_leave_text));
        }
    }

    public void onSocketError() {
        if(homeActivity != null)
            homeActivity.onFail(homeActivity.getResources().getString(R.string.generic_error_text));
        clientSocket.initSocket();
        clientSocket.setNickname(currentUser.getNickname());
    }

    public void onGenericError() {
        homeActivity.onFail(homeActivity.getResources().getString(R.string.generic_error_text));
    }


    /*****************
     * ACTIVITY SETTERS
     */

    public void setWelcomeActivity(WelcomeActivity welcomeActivity) {
        this.welcomeActivity = welcomeActivity;
    }

    public void setHomeActivity(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    public void setMessagesActivity(MessagesActivity messagesActivity) {
        this.messagesActivity = messagesActivity;
    }

    public void setHomeFragment(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    public void setLastChatsFragment(LastChatsFragment lastChatsFragment) { this.lastChatsFragment = lastChatsFragment; }

}
