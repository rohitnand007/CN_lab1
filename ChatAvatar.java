import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;

public class ChatAvatar extends ImageIcon {
    public ChatAvatar(byte[] data) {
        super(data);
    }

    public ChatAvatar(Image img) {
        super(img);
    }

    public void write(PrintWriter out) throws IOException {
        BufferedImage image = getImage();

        // convert the BufferedImage into an array of bytes
    	ByteArrayOutputStream b = new ByteArrayOutputStream();
    	ImageIO.write(image, "png", b);
        byte [] data = b.toByteArray();

    	int length = data.length;
        out.println(""+length); // SEND how many "characters" long the image is

    	// convert the array of bytes into an array of characters
    	char[] c = new char[length];
    	for(int i = 0; i < length; i++) {
        	c[i] = (char)data[i];
    	}

    	out.print(c);
        out.flush(); // send the image data as a string

    }

    public static ChatAvatar read(BufferedReader in) throws IOException {
        int length = Integer.parseInt(in.readLine()); // READ the next line as the image size

        // read the image data into an array of characters
        char[] data = new char[length];
        in.read(data, 0, length);

        byte[] b = new byte[length];

        // convert the character array into a byte array
        for(int i = 0; i < length; i++) {
            b[i] = (byte)data[i];
        }

        // create an ImageIcon from the image bytes
        return new ChatAvatar(b);
    }

    public BufferedImage getImage() {
        BufferedImage image = new BufferedImage(getIconWidth(), getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        paintIcon(null, g, 0, 0);
        g.dispose();

        return image;
    }

    public ChatAvatar getScaled(int width, int height) {
        Image img = getImage();
        Image newImg = img.getScaledInstance(width, height,  java.awt.Image.SCALE_SMOOTH);
        return new ChatAvatar(newImg);
    }

    public boolean toFile(String filename) {
        try {
            ImageIO.write(getImage(), "png", new File(filename));
        } catch(Exception ex) {
            return false;
        }

        return true;
    }

    public static ChatAvatar fromFile(String filename) {
        try {
            return new ChatAvatar(ImageIO.read(new File(filename)));
        } catch(Exception ex) {
            return null;
        }
    }

    public static ChatAvatar fromFile(File f) {
        try {
            return new ChatAvatar(ImageIO.read(f));
        } catch(Exception ex) {
            return null;
        }
    }

    public static ChatAvatar fromIcon(Icon icon) {
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return new ChatAvatar(image);
    }
}
