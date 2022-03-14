#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/un.h>
#include <pthread.h>
#include <time.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>


#define MAX_CLIENTS 100
#define BUFF_SIZE 512
#define NICK_LEN 20
#define ROOM_NUM 5

#define LEAVE_CHAT_COMMAND "LVE"
#define SEARCH_FRIEND_COMMAND "SRC"
#define SEND_MESSAGE_COMMAND "SND"

#define FRIEND_FIND_CODE "201\n"
#define FRIEND_NOT_FIND_CODE "204\n"
#define CLIENT_ERR_CODE "400\n"
#define ROOM_INFO_CODE "205"



struct Client{
  char nickname[NICK_LEN];
  int sd;
  int is_searching;
  int searching_room;
  struct Client *friend;
  struct Client *last_friend;
};



/******************
* Global Variables
*/

struct Client *clients[MAX_CLIENTS];
int rooms_size[ROOM_NUM] = {0};
pthread_mutex_t clients_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t room_mutex = PTHREAD_MUTEX_INITIALIZER;


/**********************
* Functions prototypes
*/

void add_client(struct Client *c);
void remove_client(struct Client *c);

int search_friend(struct Client *c);
char *parse_message(char *str);
void leave_chat(struct Client *c);

void send_error_to(struct Client *c);
void send_broadcast(char *msg);

char *get_room_info();
void increase_room_clients(int room_num);
void decrease_room_clients(int room_num);
void increaseNsend_room(int room_num);
void decreaseNsend_room(int room_num);


/***************************
* Client connection handler
*/

void *handle_connection(void *arg) {

  char buffer_in[BUFF_SIZE];
  char buffer_out[BUFF_SIZE];
  int leave_flag = 0;
  struct Client *client = malloc(sizeof(struct Client));
  client->sd = *((int *)arg);
  add_client(client);

  int n = read(client->sd, buffer_in, NICK_LEN);
  if(n <= 0) {
    leave_flag = 1;
  }else {
    printf("%s has joined!\n", buffer_in);
    strcpy(client->nickname, buffer_in);
    char *room_info = get_room_info();
    write(client->sd, room_info, strlen(room_info));
    free(room_info);
  }

  while(!leave_flag) {

    memset(buffer_in, 0, BUFF_SIZE);
    memset(buffer_out, 0, BUFF_SIZE);

    int n = read(client->sd, buffer_in, BUFF_SIZE); 

    if(n <= 0) {
      leave_flag = 1;
    }else {

      char command[4] = {buffer_in[0], buffer_in[1], buffer_in[2], '\0'};

      if(strcmp(command, SEARCH_FRIEND_COMMAND) == 0) {                                          

        /*EXPECTED: SRC <num> WHERE num is ROOM NUMBER*/
        if(n < 5) {                                  
          send_error_to(client);
          continue;
        }

        int searching_room = atoi(&buffer_in[4]);
        if(searching_room < 0 || searching_room >= ROOM_NUM) {
          send_error_to(client);
          continue;
        }

        client->searching_room = searching_room;
        increaseNsend_room(searching_room);
        
        printf("%s is searching a friend...\n", client->nickname);
        int found = search_friend(client);

        if(!found) {

          decreaseNsend_room(client->searching_room);

          printf("No user available for %s\n", client->nickname);

          strcpy(buffer_out, FRIEND_NOT_FIND_CODE);
          write(client->sd, buffer_out, strlen(buffer_out));

        }else {

          printf("%s is matching with %s\n", client->nickname, (client->friend)->nickname);
          strcpy(buffer_out, FRIEND_FIND_CODE);
          strcat(buffer_out, (client->friend)->nickname);
          write(client->sd, buffer_out, strlen(buffer_out));

        }

      }else if(strcmp(command, SEND_MESSAGE_COMMAND) == 0) {

        /*EXPECTED: SND <len> <message>, WHERE len is MESSAGE LENGTH*/
        if(strlen(buffer_in) < 5) {
          send_error_to(client);
          continue;
        }
        
        if(client->friend != NULL) {
          char *msg = parse_message(&buffer_in[4]);
          printf("%s is sending a message to %s\n", client->nickname, (client->friend)->nickname);
          write((client->friend)->sd, msg, strlen(msg));
          free(msg);
        }else {
          strcpy(buffer_out, FRIEND_NOT_FIND_CODE);
          write(client->sd, buffer_out, strlen(buffer_out));
        }
        

      }else if(strcmp(command, LEAVE_CHAT_COMMAND) == 0) {
        leave_chat(client);
      }

    }  
  }

  leave_chat(client);
  remove_client(client);
  close(client->sd);
  free(client);
  printf("%s left the app!\n", client->nickname);
  pthread_exit(0);

}


/*********************
* Clients queue utils
*/

void add_client(struct Client *c) {
  pthread_mutex_lock(&clients_mutex);
  for(int i = 0; i < MAX_CLIENTS; ++i) {
    if(clients[i] == NULL) {
      clients[i] = c;
      break;
    }
  }
  pthread_mutex_unlock(&clients_mutex);
}


void remove_client(struct Client *c) {
  pthread_mutex_lock(&clients_mutex);
  for(int i = 0; i < MAX_CLIENTS; ++i) {
    if(clients[i] != NULL && clients[i] == c) {
      clients[i] = NULL;
      break; 
    }
  }
  pthread_mutex_unlock(&clients_mutex);
}


/************
* Chat Utils
*/

int search_friend(struct Client *c) {

  c->is_searching = 1;
  int is_found = 0;
  time_t start_time;
  time_t end_time;
  double search_time = 0;
  time(&start_time);
  
  do{

    pthread_mutex_lock(&clients_mutex);
    if(!c->is_searching) {
      pthread_mutex_unlock(&clients_mutex);
      return 1;
    }
    for(int i = 0; i < MAX_CLIENTS; ++i) {
      if(clients[i] != NULL && clients[i] != c && clients[i]->is_searching) {
        if(clients[i]->searching_room == c->searching_room && clients[i] != c->last_friend) {
          is_found = 1;
          c-> is_searching = 0;
          clients[i]->is_searching = 0;
          c->friend = clients[i];
          clients[i]->friend = c;
          c->last_friend = clients[i];
          clients[i]->last_friend = c;
          break;
        }
      }
    }
    pthread_mutex_unlock(&clients_mutex);
    if(is_found) break;
    sleep(2);
    time(&end_time);
    search_time = difftime(end_time, start_time);

  }while(search_time < 20);

  if(c->is_searching) {
    pthread_mutex_lock(&clients_mutex);
    if(c->is_searching == 0) {
      is_found = 1;
    }
    c->is_searching = 0;
    pthread_mutex_unlock(&clients_mutex);
  }

  return is_found;

}


char *parse_message(char *str) {

  char *ptr = strchr(str, ' ');
  int index = ptr - str;

  char str_size[3];
  memcpy(str_size, str, index);
  int len = atoi(str_size);

  char *msg = malloc(sizeof(char)*len);
  memcpy(msg, &str[index+1], len);

  return msg;

}


void leave_chat(struct Client *c) {
  
  if(c->friend != NULL) {
    char response_info[4];
    strcpy(response_info, FRIEND_NOT_FIND_CODE);
    write((c->friend)->sd, response_info, strlen(response_info));
    (c->friend)->friend = NULL;
    c->friend = NULL;
    decrease_room_clients(c->searching_room);
    decrease_room_clients(c->searching_room);
    char *room_info = get_room_info();
    send_broadcast(room_info);
    free(room_info);
  }
  
}


/*********************
* Info Response Utils
*/

void send_error_to(struct Client *c) {
  char response_code[4];
  strcpy(response_code, CLIENT_ERR_CODE);
  write(c->sd, response_code, strlen(response_code));
}


void send_broadcast(char *msg) {
  pthread_mutex_lock(&clients_mutex);
  for(int i = 0; i < MAX_CLIENTS; ++i) {
    if(clients[i] != NULL) {
      write(clients[i]->sd, msg, strlen(msg));
    }
  }
  pthread_mutex_unlock(&clients_mutex);
}


/*************
* Rooms Utils
*/

char *get_room_info() {
  pthread_mutex_lock(&room_mutex);
  char *buffer = malloc(sizeof(char) * (ROOM_NUM+3));
  strcpy(buffer, ROOM_INFO_CODE);

  for(int i = 0; i < ROOM_NUM; ++i) {
    char room_size[4];
    sprintf(room_size, "%d", rooms_size[i]);
    strcat(buffer, room_size);
  }
  
  strcat(buffer, '\n');
  pthread_mutex_unlock(&room_mutex);
  return buffer;
} 


void increase_room_clients(int room_num) {
  pthread_mutex_lock(&room_mutex);
  rooms_size[room_num]++;
  pthread_mutex_unlock(&room_mutex);
}


void decrease_room_clients(int room_num) {
  pthread_mutex_lock(&room_mutex);
  rooms_size[room_num]--;
  pthread_mutex_unlock(&room_mutex);
}


void increaseNsend_room(int room_num) {
  increase_room_clients(room_num);
  char *room_info = get_room_info();
  send_broadcast(room_info);
  free(room_info);
}


void decreaseNsend_room(int room_num) {
  decrease_room_clients(room_num);
  char *room_info = get_room_info();
  send_broadcast(room_info);
  free(room_info);
}
