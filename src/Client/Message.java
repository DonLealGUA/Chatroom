package Client;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class Message implements Serializable {
    private  String text;
    private ImageIcon image;

    public Message(String text) {
        this.text = text;
    }

    public Message(ImageIcon image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public ImageIcon getImage() {
        return image;
    }
}
