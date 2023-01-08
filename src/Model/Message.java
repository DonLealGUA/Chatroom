package Model;
import Controller.Controller;

import javax.swing.*;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Date;

public class Message implements Serializable {
    private String message;
    private ImageIcon imageIcon;
    private User sender;
    private ArrayList<User> recievers;
    private String date;
    private transient Controller controller;

    public Message() throws MalformedURLException {
        this.message = getMessage();
        this.imageIcon = null;
        this.sender = new User("test123", new ImageIcon(new URL("http://i.stack.imgur.com/UvHN4.png")), false, true);
        this.recievers = new ArrayList<>();
        recievers.add(new User("testABC", new ImageIcon(new URL("http://i.stack.imgur.com/UvHN4.png")), true, true));
        recievers.add(new User("testBCD", new ImageIcon(new URL("http://i.stack.imgur.com/UvHN4.png")), true, false));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();
        this.date = dateTimeFormatter.format(now);
    }

    public String getMessage() {
        return message;
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public User getSender() {
        return sender;
    }

    public ArrayList<User> getRecievers() {
        return recievers;
    }

    public String getDate() {
        return date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setRecievers(ArrayList<User> recievers) {
        this.recievers = recievers;
    }

    public void sendThisMessage(){
        controller.sendMessage(this);
    }

    public void setController(Controller controller){
        this.controller = controller;
    }
}
