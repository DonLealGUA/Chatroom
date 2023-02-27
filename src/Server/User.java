package Server;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class User implements Serializable {
    private static int nbUser = 0;
    private int userId;
    private String username;

    private ImageIcon imageIcon;

    public User(String username, ImageIcon imageIcon) throws IOException {
        this.imageIcon = imageIcon;
        this.username = username;
        this.userId = nbUser;
        this.imageIcon = imageIcon;
        nbUser += 1;
    }


    public String getUsername() {
        return this.username;
    }

    public ImageIcon getImageIcon(){
        return imageIcon;
    }

    public String toString(){

        return this.getUsername();

    }

}
