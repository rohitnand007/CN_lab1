#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <netdb.h>

#define MAX_LEN 512

// usage:
// ./client <url> <port>

int main(int argc, char **argv) {
    printf("Client starting...\n");

    int port = 8081,sock=0,n=0; // port
char recvbuf[1024];
struct sockaddr_in serv_addr;
struct hostent *he;
    char hostname[MAX_LEN] = "localhost"; // host name of server
    char filename[MAX_LEN] = "/"; // file to get from server

    char url[2*MAX_LEN + 8]; // temporary space to store "http://<hostname>/<filename>"

    // note that argv[0] is the command itself (NOT the first argument)

    // inside this "if" statement, we parse the <URL> parameter
    if(argc > 1) {
        strncpy(url, argv[1], MAX_LEN); // copy argument 1 into the hostname string

        if(strncmp(url, "http://", 7) != 0) {
            fprintf(stderr, "URL must start with 'http://'\n");
            return 1;
        }

        char *h = &url[7];
        char *c = strchr(h, '/');
        if(c != NULL) {
            strncpy(filename, c, MAX_LEN);
            *c = '\0';
        }

        strncpy(hostname, h, MAX_LEN);
    }

    // inside this "if" statement, we parse the <port> parameter
    if(argc > 2) {
        port = atoi(argv[2]); // convert argument 2
    }

    // NOTE - here is the diagram of how sockets work
    // https://i.stack.imgur.com/YQ5ES.png
    
    // here's a really great guide to the Sockets API:
    // http://beej.us/guide/bgnet/html/single/bgnet.html

    // TODO 1 - "socket()" - create a socket
    if((sock=socket(AF_INET, SOCK_STREAM,0))<0)// creation of socket
	{
	printf("/n socket creation error\n");
	return -1;
	}

    // - use the function called "socket" to create a new socket
    //   (http://pubs.opengroup.org/onlinepubs/7908799/xns/socket.html)
	memset(&serv_addr, '0' , sizeof(serv_addr));// setting the server details to 0
	serv_addr.sin_family=AF_INET;//specifying address family to IPv4 address
	serv_addr.sin_port=htons(port);// allocating port number
	
    // TODO 2 - "connect()" - connect the socket to a remote host

    printf("Connecting to '%s', port %d\n", hostname, port);

    // - use the function called "gethostbyname" to get IP address for host name
    //   (http://pubs.opengroup.org/onlinepubs/7908799/xns/gethostbyname.html)

    // - use the function called "connect" to connect to the IP address via the socket
    //   (http://pubs.opengroup.org/onlinepubs/7908799/xns/connect.html)
if ( (he = gethostbyname(hostname) ) == NULL ) {//getting the host by name
      exit(1); /* error */
  }

  /* copy the network address to sockaddr_in structure */
  memcpy(&serv_addr.sin_addr, he->h_addr_list[0], he->h_length);// copying the address of network to the server structure

  /* and now  you can connect */
  if ( connect(sock, (struct sockaddr *)&serv_addr, sizeof(serv_addr))<0 ) {// establishing connection to the created socket
      exit(1); /* error */
  }	
    // TODO 3 - "read()" and "write()" - communicate through the socket

    printf("Requesting file '%s'\n", filename);

    // - use the functions called "send" and "recv" to write/read from socket
    //   (http://pubs.opengroup.org/onlinepubs/7908799/xns/send.html)
    //   (http://pubs.opengroup.org/onlinepubs/7908799/xns/recv.html)
	send(sock, "GET /HTTP/1.0\r\n\r\r" , 1024,0);// sending GET request 
while((n=recv(sock,recvbuf,50000,0))>0)// receiving  the complete GET response by keeping in loop
{
//	if((n=recv(sock,recvbuf,50000,0))==-1)
//	{
//	printf("error");
//	}
//	else
	recvbuf[n]='\0';
	printf("%s",recvbuf);// printing the recieved buffer for each loop
	printf("\n");
}
    // TODO 4 - "close()" - terminate the connection
close(sock);// closing the socket

    // - use the function called "close" to end the connection and delete the socket
    //   (http://pubs.opengroup.org/onlinepubs/7908799/xsh/close.html)

    return 0;
}
