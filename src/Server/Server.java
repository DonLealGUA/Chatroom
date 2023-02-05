package Server;

import Client.Message;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

    private int port;
    private List<User> clients;
    private ServerSocket server;

    public static void main(String[] args) throws IOException {
        new Server(1234).start();
    }

    private void start() throws IOException {
        server = new ServerSocket(port);
        System.out.println("Port 1234 is now open.");

        while(true) {
            // accepts a new client
            Socket client = server.accept();

            // get username of newUser
            String username = (new Scanner( client.getInputStream() )).nextLine();
            System.out.println("New Client: \"" + username + "\"\n\t     Host:" + client.getInetAddress().getHostAddress());

            // create new User
            User newUser = new User(client, username);

            // add newUser message to list
            this.clients.add(newUser);

            // Welcome msg
            newUser.getOutStream().println("<b>Welcome</b> " + newUser.toString());

            // create a new thread for newUser incoming messages handling
            new Thread(new UserHandler(this, client, newUser)).start();
        }

    }

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<User>();
    }

    public void broadcastAllUsers() {
        for (User client : this.clients) {
            client.getOutStream().println(this.clients);
        }
    }

    /**
     * Skickar privata meddelanden
     */
    public void sendMessageToUser(String msg, User userSender, String user) {
        boolean find = false;
        for (User client : this.clients) {
            if (client.getUsername().equals(user) && client != userSender) {
                find = true;

                userSender.getOutStream().println(userSender.toString() + " -> " + client.toString() +": " + msg);
                client.getOutStream().println(
                        "(<b>Private</b>)" + userSender.toString() + "<span> " + getTime() + msg+"</span>");
            }
        }
        if (!find) {
            userSender.getOutStream().println("Sorry, this user doesn't exist ");
        }
    }

    public void sendFriendRequestToUser(String msg, User userSender, String user){
        boolean find = false;
        for (User client : this.clients) {
            if (client.getUsername().equals(user) && client != userSender) {
                find = true;

                userSender.getOutStream().println("Added " + client.toString() + " to your contacts.");
                client.getOutStream().println(msg);
                Write.writeFriends(userSender.getUsername(),user);
            }
        }
        if (!find) {
            userSender.getOutStream().println("Sorry, this user doesn't exist ");
        }
    }

    // skicka meddelande till alla
    public void broadcastMessages(String msg, User userSender) {
        for (User client : this.clients) {
            try {
                OutputStream outputStream = client.getClient().getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                String message = (userSender.toString() + "<span> " + getTime() + msg+"</span>");
                objectOutputStream.writeObject(new Message<String>(message));
                System.out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }





        }
    }

    // skicka meddelande till alla
    public void broadcastImages(ImageIcon image, User userSender) {
        for (User client : this.clients) {
            client.getOutStream().println(userSender.toString() + image);
        }
    }

    // ta bort användare från listan
    public void removeUser(User user){
        this.clients.remove(user);
    }


    public String getTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String date = "(" + dtf.format(now) + "): ";

        return date;

    }

}
