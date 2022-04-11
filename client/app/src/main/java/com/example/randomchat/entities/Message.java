package com.example.randomchat.entities;

import java.io.Serializable;
import java.time.LocalTime;


public class Message implements Serializable {

    private final String     body;
    private final LocalTime  time;
    private final User       sender;


    /**CONSTRUCTOR
     */

    public Message(String body, LocalTime time, User sender) {
        this.body = body;
        this.time = time;
        this.sender = sender;
    }


    /**GETTERS
     */

    public String getBody() { return body; }

    public LocalTime getTime() { return time; }

    public User getSender() {return sender; }

}
