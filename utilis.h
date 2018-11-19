#define MAX_LEN 512

// read a single line from socket, return number of characters read
// (read until we see "\r\n" - CRLF)
int read_line(int sock, char *buffer, int max) {
    int i = 0;
    while(i < max-1) {
        // read a character from socket
        int num = recv(sock, buffer, 1, 0);
        if(num == 0) continue; // no data was read (try again)
        else if(num < 0) break; // read error (we're done)
        if(*buffer == '\r') continue; // if carriage return (CR), skip it
        else if(*buffer == '\n') break; // if newline (LR), we're done
        ++buffer;
        ++i;
    }
    *buffer = 0; // put null character at end
    return i;
}


