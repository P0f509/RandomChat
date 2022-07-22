# RandomChat

This is a college project for Operative System course, composite of two components: An Android Client developed using JAVA and a server developed in C using Unix libraries. Essentially, the project is an application for messagging with other people, based on topic rooms. After users choose a room, the server will randomly match users in the same room.

Examples of topic rooms:
- *Games*
- *Movies*
- *Music*
- *Sport*
and so on

In case there are no users in a room, the server stops the search after a default time of 60 seconds, but at any time users can stop the search for other users to start a chat.

## Communication protocol

Messages exchanged beetween client and server respects a communication protocol at Application Layer based on TCP/IP. In short, client sends a command to the server from the following list:

- **SRC** <room number> 
  (search an enduser in the specified room)
- **SND <message length> <message>** 
  (send the message to the associated enduser)
- **LVE** 
  (leave the current chat)
- **QIT** 
  (stop the search and leave the current room)

Server replies to the client with a responseCode and an optional body, depending on the case. 
For other information, to inspect how the protocol works, you can read the documentation, available in Italian.

## Set up the C server

The first task is changing the ***ip String*** in the server.c file (line 22): you have to provide the ip-address of the machine you are using to run the server.
Follow the following commands to compile and run the server. Notice you must provide a port number as argument of C programm.

### Compilation

```console
gcc -pthread -o server server.c connection.c
```
### Execution

```console
./server <port number>
```

## Set up the Android Client

First of all, as for the server, you must provide the **ip address** you have chosen for the server, and the same port number you used to run the server.
Then you can import the project using the IDE you want and start the application using an Android Emulator or an Android device.

