package Server;

import Client.Message;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Server-klassen som hanterar alla Klienter
 */
public class Server {
    private final int port;
    private final List<User> clients; //lista av klienter online
    private final ServerGUI serverGUI;
    private final HashMap<User, UserHandler> clientHashmap = new HashMap<>(); //lista med alla registrerade användare

    /**
     * Startar servern
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new Server(1234).start();
    }

    /**
     * Sätter porten samt initierar Array:en som innehåller alla Klienter.
     * Startar serverGUI samt uppdaterar GUI:t med gammal logg historik.
     * @param port servern porten alla klienter ska ansluta till.
     */
    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<User>();
        serverGUI = new ServerGUI();

        ArrayList<String> serverLogg = Reader.readServerLogg(); //Visar gammal serverLogg historik
        for (String text : serverLogg) {
            serverGUI.updateText(text);
        }

        serverGUI.updateText("-------" + getTime() + "-------");
       //Writer.writeServerLogg("-------" + getTime() + "-------");
    }

    /**
     * Startar servern och väntar på anslutningar från nya klienter.
     * När en Klient kopplar upp sig skapar den ObjectOutputStream & ObjectInputStream för användaren.
     * Läser namnet som Klienten skickar från deras sida.
     * Skapar en UserHandler med Klientens information.
     * Kollar även ifall den nya användaren har meddelanden som väntar på dem och skickar dem till användaren ifall de har väntande meddelanden.
     * Uppdaterar även serverGUI samt skriver ner det i serverlogg.txt.
     */
    private void start() throws IOException, ClassNotFoundException {
        ServerSocket server = new ServerSocket(port);
        System.out.println("Port" + port + " is now open.");

        while(true) {
            // accepts a new client
            Socket client = server.accept();


            //startar input och output kanalerna
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream()));
            oos.flush();
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));

            // får username av newUser
            Message<?> readObject = (Message<?>) ois.readObject();
            User newUser = (User) readObject.getPayload();

            //Skriver ut i consol & serverGUI att en client har anslutit sig till servern
            System.out.println("New Client: \"" + newUser.getUsername() + "\"\n\t  Host:" + client.getInetAddress().getHostAddress());
            serverGUI.updateText("New Client: " + newUser.getUsername() +" Joined");
            Writer.writeServerLogg(getTime() + "New Client: " + newUser.getUsername() +" Joined");


            //Ny instans av userhandler för den nya klienten. Lägger av till den i Client listorna och skickar en lista med alla användare till Klienterna
            UserHandler userHandler = new UserHandler(this, client, newUser, ois, oos);
            this.clientHashmap.put(newUser, userHandler);
            this.clients.add(newUser);
            broadcastAllUsers();

            //Skickar o-skickade meddelanden till användaren.
            try {
                List<String> unsentMessages = Reader.getUnsentMessages(newUser.getUsername());
                oos.writeObject(new Message<String>("<b>You have:<b> " + unsentMessages.size() + "<b> missed messages<b>"));
                oos.flush();
                for (String message : unsentMessages) {
                    broadcastMessages(message,newUser);
                }
                oos.writeObject(new Message<String>("<b>----------------------------------------------------------------------------<b>"));
                oos.flush();

            } catch (IOException e) {
                System.err.println("Error reading unsent messages file: " + e.getMessage());
            }
            serverGUI.updateText("Client: " + newUser.getUsername() +" Received old messages");
            Writer.writeServerLogg(getTime() + "Client: " + newUser.getUsername() +" Received old messages");

            // Welcome msg
            oos.writeObject(new Message<String>("<b>Welcome</b> " + newUser.toString()));
            oos.flush();

            // create a new thread for newUser incoming messages handling
            new Thread(userHandler).start();
        }

    }

    /**
     * Skickar en lista med online användare till alla uppkopplade Klienter
     */
    public void broadcastAllUsers() throws IOException {
        for (User client : this.clients) { //Går igenom alla uppkopplade Klienter
            try {
                UserHandler userHandler = clientHashmap.get(client);
                userHandler.getOos().writeObject(new Message<String>(this.clients.toString())); //Skickar ett meddelande av typen String
                System.out.println(this.clients.toString());
                userHandler.getOos().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Skickar till user att userSender har blivit tillagd i deras vänLista.
     * Skickar ett meddelande till userSender att de har blivit tillagda av user.
     * Sparar i Friends.txt att user är vän med userSender.
     * Uppdaterar serverGUI & skriver ner till serverLogg.
     * @param msg Meddelandet som ska skickas till Klienten.
     * @param userSender personen som skickar vänförfrågan
     * @param user personen som ska få meddelande om att den har blivit tillagd av userSender.
     */
    public void sendFriendRequestToUser(String msg, User userSender, String user) {
        boolean find = false;
        for (User client : this.clients) { //Går igenom alla uppkopplade Klienter
            if (client.getUsername().equals(user) && client != userSender) { //Kollar så klienten som ska få meddelandet inte är klienten som skickade meddelandet
                find = true;

                try {
                    UserHandler userHandler = clientHashmap.get(userSender);
                    userHandler.getOos().writeObject(new Message<String>("Added " + client.toString() + " to your contacts.")); // Skickar meddelande till personen som la till vännen
                    userHandler.getOos().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    UserHandler userHandler = clientHashmap.get(client);
                    userHandler.getOos().writeObject(new Message<String>(msg)); // meddelande till personen som blev tillagd
                    userHandler.getOos().flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try {
                    broadcastAllUsers();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Writer.writeFriends(userSender.getUsername(), user); //Sparar vem som är vänner
                serverGUI.updateText(userSender.getUsername() + " Added " + user + " as a friend");
                Writer.writeServerLogg(getTime() + userSender.getUsername() + " Added " + user + " as a friend");
            }
        }
    }


    /**
     * Skickar ett privatmeddelande till userSender.
     * Ifall personen inte är online så sparas meddelandet i unSentMessages så den kan skickas när personen loggar in.
     * @param msg Meddelandet som ska skickas till Klienten.
     * @param userSender personen som skickar vänförfrågan
     * @param user personen som ska få meddelande om att den har blivit tillagd av userSender.
     */
    public void sendMessageToUser(String msg, User userSender, String user) {
        boolean find = false;
        for (User client : this.clients) {
            if (client.getUsername().equals(user) && client != userSender) { //Kollar så det skickas till rätt person och inte alla
                find = true;

                try {
                    UserHandler userHandler = clientHashmap.get(userSender);
                    userHandler.getOos().writeObject(new Message<String>(userSender.toString() + " -> " + client.toString() +": " + msg)); //Skickas till personen som skickade meddelandet
                    userHandler.getOos().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    UserHandler userHandler = clientHashmap.get(client);
                    userHandler.getOos().writeObject(new Message<String>("(<b>Private</b>)" + userSender.toString() + "<span> " + getTime() + msg+"</span>")); //skickar till vem meddelandet är till
                    userHandler.getOos().flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        serverGUI.updateText(userSender.getUsername() + " sent the message " + msg + " to " + user);
        Writer.writeServerLogg(getTime() + userSender.getUsername() + " sent the message " + msg + " to " + user);
        if (!find) { //Ifall personen inte finns
            try {
                UserHandler userHandler = clientHashmap.get(userSender);
                HashMap userList = Reader.readUsers();
                if(userList.containsKey(user)){ //kollar ifall personen är registrerad
                    userHandler.getOos().writeObject(new Message<String>("Sorry, this user is Offline and will receive your message once they log in ")); //Skriver att meddelandet skickas när personen går online
                    userHandler.getOos().flush();

                    Writer.writeUnsentMessage(user,"(<b>Private</b>)" + userSender.toString() + "<span> " + getTime() + msg+"</span>"); //sparar meddelandet
                }else{
                    userHandler.getOos().writeObject(new Message<String>("Sorry, this user doesn't exist ")); // annars skickas error meddelande att personen inte finns.
                    userHandler.getOos().flush();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Skickar meddelande till alla Klinter som är uppkopplade.
     * @param msg meddelandet som ska skickas.
     * @param userSender personen som skickade meddelandet.
     */
    public void broadcastMessages(String msg, User userSender) {
        String message = null;
        for (User client : this.clients) { //Skickar meddelande till alla klienter
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
        serverGUI.updateText("Received message from " + userSender.getUsername() + "@ " + getTime() + msg);
        Writer.writeServerLogg(getTime() + "Received message from " + userSender.getUsername() + "@ " + getTime() + msg);
        Writer.writeChat(userSender.getUsername(),message);
    }

    /**
     * Skickar bilder till alla Klinter som är uppkopplade.
     * @param image bilden som ska skickas.
     * @param userSender personen som skickade meddelandet.
     */
    public void broadcastImages(ImageIcon image, User userSender) {
        for (User client : this.clients) { //Skickar bilder till alla klienter
            try {
                UserHandler userHandler = clientHashmap.get(client);
                System.out.println(image);
                userHandler.getOos().writeObject(new Message<String>(userSender.toString() + ": ")); //Skickar fört ett meddelande om vem som skickade bilden.
                userHandler.getOos().flush();

                userHandler.getOos().writeObject(new Message<ImageIcon>(image));
                System.out.println(new Message<ImageIcon>(image));
                userHandler.getOos().flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        serverGUI.updateText("Received Image from " + userSender.getUsername() + "@ " + getTime() + image.toString());
        Writer.writeServerLogg(getTime() + "Received Image from " + userSender.getUsername() + "@ " + getTime() + image.toString());
        Writer.writeChat(userSender.getUsername(),image.toString());
    }


    /**
     * Tar bort en användare som disconnected:ar från Listan.
     * Skickar en ny lista med alla användare till Klienter.
     * @param user användaren som har lämnat.
     */
    public void removeUser(User user) throws IOException {
        this.clients.remove(user);
        broadcastAllUsers();
        serverGUI.updateText("Client: " + user.getUsername() +" Disconnected");
        Writer.writeServerLogg(getTime() + "Client: " + user.getUsername() +" Disconnected");
    }

    /**
     * @return en String med Dag/Månad/År : Timme: Minut
     */
    public String getTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String date = "(" + dtf.format(now) + "): ";

        return date;
    }

    //todo Ta bort?
    public void addUser(User user) {
        this.clients.add(user);
    }

}
