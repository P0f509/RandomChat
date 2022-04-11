package com.example.randomchat.infrastructure;

import com.example.randomchat.controller.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSocket {

    private static ClientSocket instance = null;
    private Socket mSocket;
    private PrintWriter out;
    private BufferedReader in;

    private ClientSocket(){}

    public static ClientSocket getInstance() {
        if(instance == null)
            instance = new ClientSocket();
        return instance;
    }


    public void initSocket() {
        
        if(mSocket != null && mSocket.isConnected()) 
            return;
        
        new Thread(()-> {
            final String SERVER_ADR = "snake.switzerlandnorth.cloudapp.azure.com";
            final int SERVER_PORT = 5200;
            try {
                mSocket = new Socket(SERVER_ADR, SERVER_PORT);
                mSocket.setKeepAlive(true);

                out = new PrintWriter(mSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                new Thread(new SocketListener()).start();
            }catch (IOException e) {
                if(mSocket != null) {
                    closeConnection();
                }
            }
        }).start();
        
    }


    public void setNickname(String nickname) {
        new Thread(()-> {
            if(mSocket != null && mSocket.isConnected()) {
                out.write(nickname);
                out.flush();
            }
        }).start();

    }


    public void sendMessage(String msg) {
        new Thread(()-> {
            if(mSocket != null && mSocket.isConnected()) {
                out.write(msg);
                out.flush();
            }else {
                Controller.getInstance().onSocketError();
            }
        }).start();
    }


    public void closeConnection() {
        try {
            mSocket.close();
            in.close();
            out.close();

            mSocket = null;
            in = null;
            out = null;
        }catch(IOException ignored) {}
    }

    
    private void handleMessage(String msg) {

        if(msg.length() < 3)
            return;

        int responseCode = Integer.parseInt(msg.substring(0, 3));

        switch (responseCode) {
            case 100:
                Controller.getInstance().updateRooms(msg.substring(3));
                break;

            case 201:
                Controller.getInstance().startChat(msg.substring(3));
                break;

            case 202:
                Controller.getInstance().receiveFromFriend(msg.substring(3));
                break;

            case 204:
                Controller.getInstance().onFriendNotFound();
                break;

            case 300:
                Controller.getInstance().onFriendLeave();
                break;

            case 400:
                Controller.getInstance().onGenericError();
                break;

            default:
                break;
        }
    }


    /**
     * Socket listener
     **/

    private class SocketListener implements Runnable {
        @Override
        public void run() {
            while(mSocket != null && mSocket.isConnected()) {
                try {
                    final String msg = in.readLine();
                    if(msg == null) {
                        closeConnection();
                        Controller.getInstance().onSocketError();
                        break;
                    }else handleMessage(msg);
                }catch(IOException e) {
                    closeConnection();
                    Controller.getInstance().onSocketError();
                    break;
                }
            }
        }
    }

}
