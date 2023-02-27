package Server;

import Client.Message;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class UserHandler implements Runnable {
    private Socket socket;
    private Server server;
    private User user;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private ObjectInputStream ois;

    private ObjectOutputStream oos;

    public UserHandler(Server server, Socket socket, User newUser, ObjectInputStream ois, ObjectOutputStream oos) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.server = server;
            this.user = newUser;
            this.server.broadcastAllUsers();
            this.ois = ois;
            this.oos = oos;
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
                        System.out.println(message);
                        // server.broadcastImages(image,user);
                    }
                }
                else if(readObject.getPayload() instanceof ImageIcon messageImage){
                    System.out.println("körs detta?");
                    server.broadcastImages(messageImage,user);
                }
            }
            server.removeUser(user);
            this.server.broadcastAllUsers();
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ObjectOutputStream getOos() {
        return oos;
    }

}
