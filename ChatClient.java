import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatClient extends JFrame {
    static final String DEFAULT_USERNAME = "Anonymous";
    static final String AVATAR_FILE_NAME = "avatar.png";
    static final String CONFIG_FILE_NAME = "config.ini";
    static final int AVATAR_SIZE = 48;
    static final String DEFAULT_SERVER = "localhost";
    static final int DEFAULT_DELAY = 1000;

    JLabel nameLabel;
    JPanel msgList;
    JScrollPane scroll;
    ChatAvatar avatar;
    String username;
    Long lastTime;
    PrintWriter out;
    BufferedReader in;

    Timer timer = new Timer();

    public ChatClient(String server, int port) {
        super("Simple Chat Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ChatAvatar a = ChatAvatar.fromFile(AVATAR_FILE_NAME);
        setAvatar(a);

        username = DEFAULT_USERNAME;
        try {
            BufferedReader in = new BufferedReader(new FileReader(CONFIG_FILE_NAME));
            String line = in.readLine();
            if(line != null) {
                username = line;
            }
            in.close();
        } catch(Exception e3) {
        
        }

        JPanel c = (JPanel)getContentPane();

        msgList = new JPanel();
        msgList.setLayout(new BoxLayout(msgList, BoxLayout.Y_AXIS));

        scroll = new JScrollPane(msgList);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        c.add("Center", scroll);

        JPanel south = new JPanel(new BorderLayout());
        JTextField msgField = new JTextField("");
        JPanel iconPanel = new JPanel() {
            public void paint(Graphics g) {
                g.drawImage(avatar.getImage(), 0, 0, this);
            }
        };
        iconPanel.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        south.add("West", iconPanel);
        nameLabel = new JLabel(username);
        nameLabel.setForeground(Color.gray);
        JPanel south2 = new JPanel(new BorderLayout());
        south2.add("North", nameLabel);
        south2.add("Center", msgField);
        south.add("Center", south2);
        c.add("South", south);

        msgField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ChatMessage msg = new ChatMessage(avatar, username, msgField.getText());
                addMsg(msg);
                msgField.setText("");
                try {
                    sendMessageToServer(msg);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        c.setPreferredSize(new Dimension(500, 150));

        JMenuBar b = new JMenuBar();
        JMenu fm = new JMenu("File");
        JMenuItem fmi1 = new JMenuItem("Choose Avatar...");
        fm.add(fmi1);
        JMenuItem fmi2 = new JMenuItem("Change Name...");
        fm.add(fmi2);
        b.add(fm);
        setJMenuBar(b);

        final JFrame t = this;

        fmi2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(t, "Enter new name:");
                if(name != null) setUsername(name);
            }
        });

        fmi1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser(".");
                int result = fc.showOpenDialog(t);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    setAvatar(ChatAvatar.fromFile(file));
                }
            }
        });

        pack();

        try {
            new Thread() {
                public void run() {
                    Socket s;

                    while(true) {
                        try {
                            s = new Socket(InetAddress.getByName(server), port);

                            System.out.println("Socket connected: "+s);
                            break;
                        } catch(Exception ex) {
                            try {
                                Thread.sleep(DEFAULT_DELAY);
                            } catch(Exception ex2) {}
                            continue;
                        }
                    }

                    try {
                        out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);
                        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    } catch(Exception ex) {
                        System.err.println("Could not open streams");
                        ex.printStackTrace();
                    }

                    timer.scheduleAtFixedRate(new TimerTask() {
                        public void run() {
                            try {
                                List<ChatMessage> msgs = getNewMessagesFromServer();
                                if(msgs != null) {
                                    for(ChatMessage m : msgs) {
                                        addMsg(m);
                                    }
                                }
                            } catch(Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }, 0, DEFAULT_DELAY);
                }
            }.start();
        } catch(Exception ex2) {
            ex2.printStackTrace();
        }
    }

    // TODO - this should ask the server for new messages, and return them in a list
    synchronized List<ChatMessage> getNewMessagesFromServer() throws IOException {


        out.println("GET_MESSAGES");

        avatar.write(out);
        String input = in.readLine();
        //new_message from server to client starts with new
        //if the input starts with allMesssages, it serves prev unread messages 
        if(input.startsWith("new")){
            String[] newMessageArray = input.split(",");
            List<ChatMessage> newBroadcast = new ArrayList<ChatMessage>();
            String username = newMessageArray[1].split("=",2)[1].trim();
            String text = newMessageArray[2].split("=",2)[1].trim();
            // lastTime = Long.valueOf(newMessageArray[0].split("=",2)[1].trim());
            ChatMessage m = new ChatMessage(avatar, username, text);
            newBroadcast.add(m);
            
            return newBroadcast;

        }else if (input.startsWith("allMessages")) {
            String[] allMsgArray = input.split(",");
            List<ChatMessage> allMessageList = new ArrayList<ChatMessage>();
            // System.out.println(allMsgArray);
            // System.out.println(allMsgArray.length);
            for (int i = 0; i <= allMsgArray.length - 3; i = i+3){
            String username = allMsgArray[i+1].split("=",2)[1].trim();
            String text = allMsgArray[i+2].split("=",2)[1].trim(); 
            ChatMessage m = new ChatMessage(avatar, username, text);
            allMessageList.add(m);
            }
            return allMessageList;

        }else{
            return null;
        }

        // return null;
    }

    // TODO - this should send our new message to the server
    synchronized void sendMessageToServer(ChatMessage msg) throws IOException {
        // this actually sends the message typed in client text box to server.
        out.println("NEW_MESSAGE,"+ msg);
        // lastTime = System.currentTimeMillis();
        // System.out.println(lastTime);
        avatar.write(out);
        // System.out.println(msg);
    }

    void addMsg(ChatMessage msg) {
        msgList.add(new MsgPanel(msg));
        scroll.revalidate();
        SwingUtilities.invokeLater(new Thread() {
            public void run() {
                JScrollBar v = scroll.getVerticalScrollBar();
                v.setValue(v.getMaximum());
            }
        });
    }

    void setUsername(String name) {
        username = name;
        nameLabel.setText(username);
        try {
            PrintWriter out = new PrintWriter(new FileWriter(CONFIG_FILE_NAME), true);
            out.println(username);
            out.close();
        } catch(IOException ex) {
        
        }
    }

    void setAvatar(ChatAvatar icon) {
        if(icon == null) {
            icon = ChatAvatar.fromIcon(UIManager.getIcon("OptionPane.informationIcon"));
        }

        avatar = icon.getScaled(AVATAR_SIZE, AVATAR_SIZE);
        repaint();

        avatar.toFile(AVATAR_FILE_NAME);
    }

    class MsgPanel extends JLabel {
        MsgPanel(ChatMessage msg) {
            super("<html><font size=\"+1\" color=\"#9a9a9a\">"+msg.username+"</font><br>"+msg.text+"</html>", msg.avatar, SwingConstants.LEFT);
            setBackground(Color.red);

            Dimension d = getMaximumSize();
            Dimension d2 = new Dimension(d.width, getMinimumSize().height);
            setMaximumSize(d2);

        }
    }

    public static void main(String[] argv) {
        String server = DEFAULT_SERVER;
        int port = ChatServer.DEFAULT_PORT;

        if(argv.length >= 2) {
            try {
                server = argv[0];
                port = Integer.parseInt(argv[1]);
            } catch(Exception ex) {
            
            }
        }

        ChatClient c = new ChatClient(server, port);
        c.setVisible(true);
    }
}
