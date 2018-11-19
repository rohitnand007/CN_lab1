import javax.swing.*;

public class ChatMessage {
    long timestamp;
    ChatAvatar avatar;
    String username;
    String text;

    public ChatMessage(ChatAvatar avatar, String username, String text) {
        timestamp = System.currentTimeMillis();
        this.avatar = avatar;
        this.username = username;
        this.text = text;
    }

    public String toString() {
        return String.format("timestamp = %d, username = %s, text = %s", timestamp, username, text);
    }
}
