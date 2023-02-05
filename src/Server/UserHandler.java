package Server;

import Client.Message;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class UserHandler implements Runnable {
    private Socket socket;
    private Server server;
    private User user;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public UserHandler(Server server, Socket socket, User newUser) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.server = server;
            this.user = newUser;
            this.server.broadcastAllUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fixa detta
     */
    @Override
    public void run() {
        try {
            while (socket.isConnected()){
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(inputStream);
                Message<?> readObject = (Message<?>) ois.readObject();
                if(readObject.getPayload() instanceof String){
                    String message = (String) readObject.getPayload();
                    if (message.charAt(0) == '@'){
                        if(message.contains(" ")){
                            System.out.println("private msg : " + message);
                            int firstSpace = message.indexOf(" ");
                            String userPrivate= message.substring(1, firstSpace);
                            server.sendMessageToUser(message.substring(firstSpace+1), user, userPrivate);
                        }
                    }else if(message.startsWith("/add")){
                        if (message.contains(" ")){
                            System.out.println("add friend " + message);
                            String userPrivate = message.substring(5);
                            System.out.println(userPrivate + " userprivate");
                            String msg = user + " added you to their contacts. Type /add " + user + " to add them to your contacts";
                            server.sendFriendRequestToUser(msg, user, userPrivate);
                         }
                     }else{
                        // update user list
                        server.broadcastMessages(message, user);
                        // server.broadcastImages(image,user);
                    }
                }
                else if(readObject.getPayload() instanceof ImageIcon){
                    System.out.println(readObject.getPayload());
                    System.out.println("Bild");
                }
            }
            server.removeUser(user);
            this.server.broadcastAllUsers();
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void TestaLäsa() throws IOException, ClassNotFoundException {
        String message;
        ImageIcon image;

        Scanner sc = new Scanner(this.user.getInputStream());

        while (sc.hasNextLine()) {
            message = sc.nextLine();
            // hantera meddelanden till en specifik person
            if (message.charAt(0) == '@'){
                if(message.contains(" ")){
                    System.out.println("private msg : " + message);
                    int firstSpace = message.indexOf(" ");
                    String userPrivate= message.substring(1, firstSpace);
                    server.sendMessageToUser(message.substring(firstSpace+1), user, userPrivate);
                }

            }else if(message.startsWith("/add")){
                if (message.contains(" ")){
                    System.out.println("add friend " + message);
                    String userPrivate = message.substring(5);

                    System.out.println(userPrivate + " userprivate");

                    String msg = user + " added you to their contacts. Type /add " + user + " to add them to your contacts";

                    server.sendFriendRequestToUser(msg, user, userPrivate);
                }
            } else{
                // update user list
                server.broadcastMessages(message, user);
                // server.broadcastImages(image,user);
            }
        }
        server.removeUser(user);
        this.server.broadcastAllUsers();
        sc.close();

    }
}
