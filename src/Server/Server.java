package Server;

import Client.Message;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Server {

    private int port;
    private List<User> clients;
    private ServerSocket server;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private HashMap<User, UserHandler> clientHashmap = new HashMap<User, UserHandler>();

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new Server(1233).start();
    }

    private void start() throws IOException, ClassNotFoundException {
        server = new ServerSocket(port);
        System.out.println("Port" + port + " is now open.");

        while(true) {
            // accepts a new client
            Socket client = server.accept();

            this.oos = new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream()));
            oos.flush();

            this.ois = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));

            // get username of newUser
            Message<?> readObject = (Message<?>) ois.readObject();
            User newUser = (User) readObject.getPayload();

            System.out.println("New Client: \"" + newUser.getUsername() + "\"\n\t     Host:" + client.getInetAddress().getHostAddress());

            // create new User
           // User newUser = new User(username);

            UserHandler userHandler = new UserHandler(this, client, newUser, ois, oos);

            this.clientHashmap.put(newUser, userHandler);

            // add newUser message to list
            this.clients.add(newUser);
            broadcastAllUsers();

            // Welcome msg
            oos.writeObject(new Message<String>("<b>Welcome</b> " + newUser.toString()));
            oos.flush();

            // create a new thread for newUser incoming messages handling
            new Thread(userHandler).start();
        }

    }

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<User>();
    }

    public void broadcastAllUsers() throws IOException {
        for (User client : this.clients) {
            try {
                UserHandler userHandler = clientHashmap.get(client);
                userHandler.getOos().writeObject(new Message<String>(this.clients.toString()));
                System.out.println(this.clients.toString());
                userHandler.getOos().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendFriendRequestToUser(String msg, User userSender, String user) {
        boolean find = false;
        for (User client : this.clients) {
            if (client.getUsername().equals(user) && client != userSender) {
                find = true;

                try {
                    UserHandler userHandler = clientHashmap.get(userSender);
                    userHandler.getOos().writeObject(new Message<String>("Added " + client.toString() + " to your contacts."));
                    userHandler.getOos().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    UserHandler userHandler = clientHashmap.get(client);
                    userHandler.getOos().writeObject(new Message<String>(msg));
                    userHandler.getOos().flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try {
                    broadcastAllUsers();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Write.writeFriends(userSender.getUsername(), user);
            }
        }
    }

    public void sendMessageToUser(String msg, User userSender, String user) {
        boolean find = false;
        for (User client : this.clients) {
            if (client.getUsername().equals(user) && client != userSender) {
                find = true;

                try {
                    UserHandler userHandler = clientHashmap.get(userSender);
                    userHandler.getOos().writeObject(new Message<String>(userSender.toString() + " -> " + client.toString() +": " + msg));
                    userHandler.getOos().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    UserHandler userHandler = clientHashmap.get(client);
                    userHandler.getOos().writeObject(new Message<String>("(<b>Private</b>)" + userSender.toString() + "<span> " + getTime() + msg+"</span>"));
                    userHandler.getOos().flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        Write.writePrivatChat(String.valueOf(userSender),user,msg);
        if (!find) {
            try {
                UserHandler userHandler = clientHashmap.get(userSender);
                userHandler.getOos().writeObject(new Message<String>("Sorry, this user doesn't exist "));
                userHandler.getOos().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // skicka meddelande till alla
    public void broadcastMessages(String msg, User userSender) {
        String message = null;
        for (User client : this.clients) {
            try {
                UserHandler userHandler = clientHashmap.get(client);
                message = (": " + getTime() + msg);
                System.out.println(message + " got message broadcastmessages");
                userHandler.getOos().writeObject(new Message<String>(userSender.toString() + message));
                userHandler.getOos().flush();
                System.out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Write.writeChat(userSender.getUsername(),message);
    }


    // skicka meddelande till alla
    public void broadcastImages(ImageIcon image, User userSender) {
        for (User client : this.clients) {
            try {
                UserHandler userHandler = clientHashmap.get(client);
                System.out.println(image);
                userHandler.getOos().writeObject(new Message<ImageIcon>(image));
                System.out.println(new Message<ImageIcon>(image));
                userHandler.getOos().flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Write.writeChat(userSender.getUsername(),image.toString());
    }

    // ta bort användare från listan
    public void removeUser(User user) throws IOException {
        this.clients.remove(user);
        broadcastAllUsers();
    }


    public String getTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String date = "(" + dtf.format(now) + "): ";

        return date;

    }

    public void addUser(User user) {
        this.clients.add(user);
    }
}
