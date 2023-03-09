package Server;

import Client.Message;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

/**
 * Tråd för Servern som lyssnar på meddelanden osm Klienter skickar.
 */
public class UserHandler implements Runnable {
    private Socket socket;
    private Server server;
    private User user;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    /**
     * Constructor som setter alla värden
     * @param server servern.
     * @param socket socket:en som ska lyssnas av.
     * @param newUser den nya användaren.
     * @param ois ObjectInputStream för att lyssna på meddelade.
     * @param oos ObjectOutputStream för att skicka meddelanden.
     */
    public UserHandler(Server server, Socket socket, User newUser, ObjectInputStream ois, ObjectOutputStream oos) {
        try {
            this.socket = socket;
            this.server = server;
            this.user = newUser;
            this.ois = ois;
            this.oos = oos;
            server.broadcastAllUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run metod som lyssnar på meddelanden som skickas av klienter till servern.
     * Baserat på typen av meddelande och vad meddelanden innehåller kallar denna metod på andra metoder i Servern klassen.
     */
    @Override
    public void run() {
        try {
            while (socket.isConnected()){
                Message<?> readObject = (Message<?>) ois.readObject();
                if(readObject.getPayload() instanceof String message){
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
                     }else if (message.startsWith("(")) {
                        // extract the timestamp string between the parentheses
                        int start = message.indexOf("(") + 1;
                        int end = message.indexOf(")");
                        String timestamp = message.substring(start, end);
                        Writer.writeServerLogg("(" + timestamp + ")" + "Client received message at ");
                        server.getServerGUI().updateText("Client received message at " + timestamp);
                    }else{
                        // update user list
                        server.broadcastMessages(message, user);
                    }
                }
                else if(readObject.getPayload() instanceof ImageIcon messageImage){
                    server.broadcastImages(messageImage,user);
                }
            }
            ois.close();
            server.removeUser(user);
            socket.close();
            this.server.broadcastAllUsers();
        } catch (EOFException e) {
            closeEverything(socket, ois);
        } catch (IOException | ClassNotFoundException e) {
            closeEverything(socket, ois);
            e.printStackTrace();
        }
    }

    /**
     * Stänger ner socket & ObjectInputStream.
     * @param socket socket.
     * @param ois ObjectInputStream.
     */
    public void closeEverything(Socket socket, ObjectInputStream ois) {
        try {
            server.removeUser(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (ois != null) {
                ois.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter för ObjectOutputStream.
     */
    public ObjectOutputStream getOos() {
        return oos;
    }

}
