package Client;

import Server.Reader;
import Server.User;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * En klient som kan ansluta sig till systemet
 */
public class Client {
    private final String IP;
    private final int PORT;
    private String name;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket socket;
    private ClientUI clientUI;
    private final LoginUI loginUI;


    /**
     * Sparar IP och PORT. Startar ett GUI där klienten ska logga in eller registrera sig
     */
    public Client(){
        this.IP = "localhost";
        this.PORT = 1234;

        this.loginUI = new LoginUI(this);
    }

    /**
     * När en klient tryck på connect i loginGUIt sparas användarnamnet. Klienten ska välja en bild
     * eller inte beroende på om den loggar in eller registrerar sig.
     * @param username klientens användarnamn
     * @param login true om klienten loggar in, false om klienten registrerar sig
     */
    public void connectClicked(String username, boolean login) throws IOException {
        this.name = username;
        ImageIcon imageIcon = null;
        boolean userValid = false;

        if (!login){ //om klienten inte tryck på logga in ska klienten välja en bild som profilbild
            imageIcon = new ImageIcon(getPicture());
            userValid = true;
        }else { //om användaren valt att logga in
            if (Reader.readIfUserExist(username)){ //kollar om användarnamnet finns bland registrerade användare
                imageIcon = (ImageIcon) Reader.readUsers().get(username);
                userValid = true;
            }else{ //skriver om ingen användare finns registrerad
                JOptionPane.showMessageDialog(null, "No user with the name " + username + " is registered.", "Login", JOptionPane.INFORMATION_MESSAGE);
                loginUI.noUserExist();
                new LoginUI(this);
            }
        }

        if (userValid) {
            this.clientUI = new ClientUI(this, username); //skapar ett user interface för klienten

            socket = new Socket(IP, PORT); //startar socket
            clientUI.writeConnectMessage(socket);

            //ObjectOutputStream och ObjectInputStream som används för att läsa och skriva till servern
            this.oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            this.ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

            //skickar användaren till servern
            oos.writeObject(new Message<>(new User(name, imageIcon)));
            oos.flush();

            //uppdaterar klientens profilbild
            clientUI.updateImageIcon(imageIcon);

            clientUI.updatePane(IP, PORT);

            listenForMessages();
        }
    }

    /**
     * När en klient skrivit in ett meddelande i GUI:t skickas det till servern här
     * @param text meddelandet klienten skrev in
     */
    public void sendMessage(String text) {
        try {
            if (text.equals("")) {
                return;
            }
            //skickar meddelandet till servern
            oos.writeObject(new Message<>(text));
            oos.flush();
        } catch (Exception ex) {
            clientUI.showExceptionMessage(ex);
            System.exit(0);
        }
    }

    /**
     * När en klient skickat en bild i GUI:t skickas det till servern här
     * @param imageIcon bilden klienten skickade
     */
    public void sendPicture (ImageIcon imageIcon) {
        try {
            if (imageIcon == null) {
                return;
            }
            //skickar bilden till servern
            oos.writeObject(new Message<>(imageIcon));
            oos.flush();
        } catch (Exception ex) {
            clientUI.showExceptionMessage(ex);
            System.exit(0);
        }
    }

    /**
     * när en klient trycker på disconnect
     */
    public void disconnectPressed() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (ois != null) {
                ois.close();
            }
            if (oos != null) {
                oos.close();
            }
        } catch (IOException e) {
            System.exit(0); //TODO jag la till den nyss, testa
        }
    }

    /**
     * hämtar tid just nu
     * @return tiden just nu formaterad som string
     */
    public String getTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String date = "(" + dtf.format(now) + ")";

        return date;
    }

    /**
     * Väljer en fil på datorn
     * @return filen som blev vald
     */
    public static String getPicture() {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        File selectedFile = null;
        int returnValue = jfc.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = jfc.getSelectedFile();
            System.out.println(selectedFile.getAbsolutePath());
        }
        return selectedFile.getAbsolutePath();

    }

    public void updateUsers(){
        clientUI.updateUsers();
    }

    public void updateUsersFriendsMessage(String user){
        clientUI.updateUsersFriendsMessage(user);
    }

    public void updateUsersPane(String user){
        clientUI.updateUsersPane(user);
    }

    public void updateUsersMessage(String newMessage){
        clientUI.updateUsersMessage(newMessage);
    }

    public void updateImage(ImageIcon imageIcon){
        clientUI.updateImage(imageIcon);
    }

    /**
     * lyssnar på meddelanden från servern via ObjectInputStream
     */
    public void listenForMessages(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    while (socket.isConnected()) {
                        Message<?> msg = (Message<?>) ois.readObject(); //hämtar meddelande från servern
                        if (msg.getPayload() instanceof String newMessage) { //om meddelandet innehåller en String
                            String message = (String) msg.getPayload();
                            String time = getTime(); //tid meddelandet levererades till mottagaren
                            if (message != null) {
                                if (message.charAt(0) == '[') { //om första char är '[' betyder det är det är en lista som skickas
                                    message = message.substring(1, message.length() - 1);
                                    ArrayList<String> ListUser = new ArrayList<>(Arrays.asList(message.split(", "))); //gör en arraylist av strängen vi fick in
                                    //läser vilka vänner användaren har och uppdaterar GUI:t
                                    ArrayList<List<String>> Friends = Reader.readFriends();
                                    updateUsers();
                                    for (String user : ListUser) { //går igenom varje sträng i listUser
                                        boolean isFriend = false;
                                        for (List<String> friendList : Friends) { //går igenom varje sträng i friendList
                                            if (Objects.equals(friendList.get(0), name) && Objects.equals(friendList.get(1), user)) {
                                                updateUsersFriendsMessage(user); //uppdaterar listan på användare med gul färg om de är vänner
                                                isFriend = true;
                                                break;
                                            }
                                        }
                                        if (!isFriend) {
                                            updateUsersPane(user); //skriver ut användaren med svart om de inte är vänner
                                        }
                                    }
                                } else { //annars är meddelandet ett chatt-meddelande och då skickas en chatt ut till valda
                                    updateUsersMessage(newMessage);
                                    Message<String> timeMsg = new Message<>(time); // create a new message containing the time
                                    oos.writeObject(timeMsg); // send the time message back to the server
                                }
                            }
                        } else if (msg.getPayload() instanceof ImageIcon) { //om meddelandet är en imageIcon är det en bild som skickas
                            updateImage((ImageIcon) msg.getPayload()); //skriver ut bilden på GUI:t
                            System.out.println(getTime());
                            // sendMessage("|" + getTime());
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * main metod för att starta en ny klient
     */
    public static void main(String[] args)  {
        new Client();
    }
}
