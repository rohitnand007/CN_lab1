# team03
Lalita Aditya Vishnu Sai Anche	<vishnuanche@unm.edu>, 
Sharat Chandra Reddy Tangella	<chandrasarath546@unm.edu>, 
Rohit Yerramsetty	<rohityerramsetty@unm.edu>

## Part1 - Simple Chat Client/Server using Java
### Overview
In this part, we designed a protocol for working of a Multi Client/Server application. We were able to code a working Client/Server-- in which, multiple clients can connect to a running server and chat with remianing clients using that server.
### Working of the System
After compiling the code,

Run the server in the terminal using:

```
java ChatServer 8080
```
and start multiple clients in different terminals using:

```
java ChatClient hostname 8080
```
As soon as we start a client, an interface pops with a text box to type messages and also displays new messages sent from all the other connected clients.

If a new client joins the chat and want to retrieve all the unread messages, if he sends a command `getUnreadMessages` through the chat interface, the server sends all the unread messages to the client which requested for it and doesn't broadcast these messages to other connected clients.


## Part2 -HTTP Client/Server using C
### Overview
In this part, we developed a simple HTTP client and server using c.

In the client program, we give the url address as the first command line argument and the the port number as the second argument. By running the client program we will be able to see the corresponding HTML code for the web-page.

In the server program, we give the directory name as the first argument and the port number as the second argument. By running the server program and typing “localhost:8080/index.html”, we will see the output for the index.html file in the browser.
## Description
#### Working of Client
After compiling the code, we run the code as:
```
client http://unm.edu/ 80
```
`http://unm.edu/` is the URL address and `80` is the port number.
As an output we get the corresponding html code for the requested page. We can retrieve data from any other page by using it's URL.
#### Working of Sever
Compile the code and run it on the terminal as:
```
server . 8080 
```
The `.`is the current directory and `8080` is the port number.
Open the web browser and type:
````
localhost:8080/index.html
```
We will see the output for the HTML code on the browser.

`localhost:8080/` will also give the same output.

## Acknowledgements
We are thankful to our Instructor, Prof. Jedidiah McClurg and TA Jeff Sharpe for helping in clarifying our doubts.