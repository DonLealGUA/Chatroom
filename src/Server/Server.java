package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

        while (true) {
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
            new Thread(new UserHandler(this, newUser)).start();
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

    public void sendMessageToUser(String msg, User userSender, String user) {
        boolean find = false;
        for (User client : this.clients) {
            if (client.getUsername().equals(user) && client != userSender) {
                find = true;

                //TODO fixa tid och annat
                userSender.getOutStream().println(userSender.toString() + " -> " + client.toString() +": " + msg);
                client.getOutStream().println(
                        "(<b>Private</b>)" + userSender.toString() + "<span>: " + msg+"</span>");
            }
        }
        if (!find) {
            userSender.getOutStream().println("Sorry, this user doesn't exist ");
        }
    }

    // skicka meddelande till alla
    public void broadcastMessages(String msg, User userSender) {
        for (User client : this.clients) {
            client.getOutStream().println(userSender.toString() + "<span>: " + msg+"</span>");
        }
    }

    // ta bort användare från listan
    public void removeUser(User user){
        this.clients.remove(user);
    }
}
