package com.example.randomchat.entities;

import java.io.Serializable;

public class User implements Serializable {

    private final String nickname;
    private       Room   currentRoom;


    /**CONSTRUCTOR
     */

    public User(String nickname) {
        this.nickname = nickname;
    }


    /** GETTERS
     * & SETTERS
     */

    public String getNickname() {
        return nickname;
    }


    public void setRoom(Room room) { currentRoom = room; }

    public Room getRoom() { return currentRoom; }

}
