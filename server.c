#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <netdb.h>

#include "utilis.h"

// usage:
// ./server <directory> <port>

int main(int argc, char **argv) {
struct sockaddr_in address;
int new_socket,sock=0;
//char myIP[16];
int addrlen=sizeof(address),n=0;
char recvbuf[MAX_LEN],sdbuf[MAX_LEN],file_buffer[MAX_LEN];
    printf("Server starting...\n");

    char dir[MAX_LEN] = "."; // space to store the directory name
    int port = 8080; // port

    // note that argv[0] is the command itself (NOT the first argument)

    // inside this "if" statement, we parse the <file> parameter
    if(argc > 1) {
        strncpy(dir, argv[1], MAX_LEN); // copy argument 1 into the hostname string
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
  if((new_socket=socket(AF_INET, SOCK_STREAM,0))<0)//creating a new socket
        {
        printf("/n socket creation error\n");
        return -1;
        }

    // - use the function called "socket" to create a new socket
    //   (http://pubs.opengroup.org/onlinepubs/7908799/xns/socket.html)

    // TODO 2 - "bind()" - name the socket using a port number
  address.sin_family= AF_INET;//assigning the address family to IPv4 address
        address.sin_port =htons(port);// assigning the value of port
        address.sin_addr.s_addr= INADDR_ANY;// assignig the address to revieve packets from all interfaces
        if(bind(new_socket, (struct sockaddr *)&address,sizeof(address))<0)//binding the socket to the address
{
perror("bind failed");
exit(EXIT_FAILURE);
}

    // - use the "bind" function
    //   (http://pubs.opengroup.org/onlinepubs/7908799/xns/bind.html)

    printf("Starting server in directory '%s', port %d\n", dir, port);

    // TODO 3 - "listen()" - start listening for connections on the socket
if(listen(new_socket,50)<0)//listening for new connections to the socket to maximum of 50
{
        perror("listen");
        exit(EXIT_FAILURE);
}

    // - use the "listen" function
    //   (http://pubs.opengroup.org/onlinepubs/7908799/xns/listen.html)

    // TODO 4 - "accept()" - accept a connection

    // - use the "accept" function
    //   (http://pubs.opengroup.org/onlinepubs/7908799/xns/accept.html)
    // TOD0O: int socket = accept(...);
//while((sock=accept(new_socket,(struct sockaddr *)&address,(socklen_t *)&addrlen))>0)
//{
while(1)
{
if((sock=accept(new_socket,(struct sockaddr *)&address,(socklen_t *)&addrlen))<0)//accepting the connection to the socket
{
        perror("accept");
        exit(EXIT_FAILURE);
}
    // we now use "read()" and "write()" - communicate through the socket

    // - we use the functions called "send" and "recv" to write/read from socket
    //   (http://pubs.opengroup.org/onlinepubs/7908799/xns/send.html)
    //   (http://pubs.opengroup.org/onlinepubs/7908799/xns/recv.html)

    char buffer[MAX_LEN]; // allocate space

    // read an HTTP request from client
    int num = read_line(sock, buffer, MAX_LEN); // buffer now contains "GET ..."

    char filename[MAX_LEN] = "index.html";
    int version = 0;
    // scan filename and HTTP version from the request line
    sscanf(buffer, "GET %s HTTP/1.%d", filename, &version);

    printf("got filename='%s', version=%d\n", filename, version);

    // read (and simply discard) all other headers
    while(true) {
        int num = read_line(sock, buffer, MAX_LEN); // header
        if(num <= 0) break; // num==0 means that we've hit the blank line (end of headers)
        printf("got header: '%s'\n", buffer);
    }

    // now we need to look up the file, and send HTTP response

    // first, we build the full file path, then open the file

    char full[2*MAX_LEN]; // space to store full path

    strncpy(full, dir, MAX_LEN); // put dir at the beginning of full
    strncat(full, filename, MAX_LEN); // append filename to full
     if(full[strlen(full)-1] == '/') strcat(full, "index.html");
    // at this point, "full" is the full path of the file

    FILE *f = fopen(full, "r"); // open for reading
    if(f == NULL) {
        // if file not found (or error opening), send "404 NOT FOUND"
        sprintf(buffer, "HTTP/1.0 404 NOT FOUND\r\n\r\n");
        send(sock, buffer, strlen(buffer), 0);
    } else {
        // read and send contents
        int len=0; // TODO: int len = ...; // (GET LENGTH OF FILE!)
/*	fread(file_buffer, 1024*1024*4, 1, f);
	len=strlen(file_buffer);
	printf("length=%i",len);*/	
        // print response OK line
        sprintf(buffer, "HTTP/1.0 200 OK\r\n");
        send(sock, buffer, strlen(buffer), 0);

        // print Content-Length header, followed by blank line
        sprintf(buffer, "Content-Length: %d\r\n\r\n", len);
        send(sock, buffer, strlen(buffer), 0);

        // output the file contents
        while(true) {
           int num = fread(buffer, sizeof(char), MAX_LEN, f);
            if(num < 0) break;
           send(sock, buffer, num, 0);
       }

       fclose(f); // close file
	close(sock);
 }
//memset(buffer, 0, sizeof(buffer));
    // TODO 6 - "close()" - terminate the connection
}
close(new_socket);//closing the connections 
//memset(buffer, 0, sizeof(buffer));
    // - use the function called "close" to end the connection and delete the socket
    //   (http://pubs.opengroup.org/onlinepubs/7908799/xsh/close.html)
    return 0;
}

