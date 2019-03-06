# defaule rule (builds everything)
all:	client server ChatClient.class ChatServer.class

# rule for building C client
client:	client.c
	gcc client.c -o client

# rule for building C server
server:	server.c
	gcc server.c -o server

# rule for building Java chat client
ChatClient.class:	ChatClient.java ChatMessage.java
			javac ChatClient.java

# rule for building Java chat server
ChatServer.class:	ChatServer.java ChatMessage.java
			javac ChatServer.java
