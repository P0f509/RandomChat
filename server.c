#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <signal.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include "connection.h"


int main(int argc, char** argv) {
  
  if(argc != 2) {
    printf("Usage: %s <port number>\n", argv[0]);
    exit(EXIT_FAILURE);
  }

  printf("Starting server...\n");

  const char* ip = "127.0.0.1";
  int port = atoi(argv[1]);

  pthread_t tid;
  int listen_sd, client_sd;

  struct sockaddr_in serv_addr, client_addr;

  serv_addr.sin_family = AF_INET;
  serv_addr.sin_addr.s_addr = inet_addr(ip); 
  serv_addr.sin_port = htons(port);

  
  signal(SIGPIPE, SIG_IGN);


  listen_sd = socket(AF_INET, SOCK_STREAM, 0);
  if(listen_sd < 0) {
    perror("Socket error"), exit(EXIT_FAILURE);
  }


  if(bind(listen_sd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) {
    perror("Bind"), exit(EXIT_FAILURE);
  }

  
  if(listen(listen_sd, MAX_CLIENTS) < 0) {
    perror("Listen error"), exit(EXIT_FAILURE);
  }


  printf("Done.\nServer is listening on port %s.\n", argv[1]);
  

  while(1) {

    socklen_t client_len = sizeof(client_addr);
    client_sd = accept(listen_sd, (struct sockaddr *) &client_addr, &client_len);

    printf("***New Connection accepted.\n");
   
    pthread_create(&tid, NULL, handle_connection, (void *)&client_sd);

  }

  return 0;

}
