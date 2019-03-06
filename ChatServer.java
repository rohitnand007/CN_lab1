import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    public static final int DEFAULT_PORT = 8080;
    // Below line instantiates a global list of chat messages coming to this server
    public List<ChatMessage> chatMessages = new LinkedList<ChatMessage>();

    public ChatServer(int port) {
        System.out.printf("Server starting on localhost, port: %d\n", port);

        List<Handler> handlers = new LinkedList<Handler>();

        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("Server started");
            while (true) {
                Socket clientSocket = server.accept();
                Handler h = new Handler(clientSocket.getInputStream(), clientSocket.getOutputStream(), handlers);
                handlers.add(h);
                h.start();
            }
        } catch(Exception ex) {
            System.err.println("Could not open socket");
            ex.printStackTrace();
        }
    }

    public static void main(String[] argv) {
        int port = DEFAULT_PORT;

        if(argv.length > 1) {
            try {
                port = Integer.parseInt(argv[0]);
            } catch(Exception ex) {
                
            }
        }

        new ChatServer(port);
    }

    class Handler extends Thread {
        BufferedReader in;
        PrintWriter out;
        List<Handler> handlers;
        Handler(InputStream in, OutputStream out, List<Handler> handlers) {
            this.in = new BufferedReader(new InputStreamReader(in));
            this.out = new PrintWriter(new OutputStreamWriter(out), true);
            this.handlers = handlers;
        }

        public void run() {
            try {
                while(true) {

                    String line = in.readLine();
                    // The if block handles the broadcast of messages to all clients
                    // first else if block serves all unread messages to a new client
                    // second else if block serves default string for initial startup
                  if(line.startsWith("NEW_MESSAGE")) {
                    String username = line.split(",",4)[2].split("=",2)[1].trim();
                    String text = line.split(",",4)[3].split("=",2)[1].trim();
                        if(!text.equals("getUnreadMessages")){
                            ChatAvatar avatar = ChatAvatar.read(in);                        
                            ChatMessage m = new ChatMessage(avatar, username, text);
                            chatMessages.add(m);
                             Handler h = this;
                            for (Handler element : handlers ){
                                if(h != element){
                                    element.out.println("new!"+m);
                                }
                            }
                        }else if(text.equals("getUnreadMessages")){
                            ChatAvatar avatar = ChatAvatar.read(in);
                            Handler h = this;
                            h.out.println("allMessages!"+chatMessages);
                        }       
                
                    }
                    else if(line.startsWith("GET_MESSAGES")) {
                        ChatAvatar avatar = ChatAvatar.read(in);
                        Handler h = this;
                        h.out.println("Default string for preventing client to freeze");
                           
            }else {
                
            }
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
