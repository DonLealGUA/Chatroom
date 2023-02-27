package Server;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class User {
    private static int nbUser = 0;
    private int userId;
    private PrintStream streamOut;
    private InputStream streamIn;
    private String username;
    private Socket client;

    private ImageIcon imageIcon;

    public User(Socket client, String username) throws IOException {
        this.streamIn = client.getInputStream();
        this.client = client;
        this.username = username;
        this.userId = nbUser;
        this.imageIcon = imageIcon;
        nbUser += 1;
    }

    public PrintStream getOutStream(){
        return this.streamOut;
    }

    public Socket getClient() {
        return client;
    }

    public String getUsername() {
        return this.username;
    }

    public String toString(){

        return this.getUsername();

    }

}
