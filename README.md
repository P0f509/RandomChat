# RandomChat

An application for messagging with other people, based on topic rooms. After users choose a room, the server will randomly match users in the same room.

Examples of topic rooms:
- *Games*
- *Movies*
- *Music*
- *Sport*
and so on

In case there are no users in a room, the server stops the search after a default time of 60 seconds, but at any time users can stop the search for other users to start a chat.

## Set up the server

The first task is changing the ***ip String*** in the server.c file (line 22): you have to provide the ip-address of the machine you are using to run the server.

### Compilation

'''console
gcc -pthread -o server server.c connection.c
'''

### Execution

'''console
./server <port number>
'''
