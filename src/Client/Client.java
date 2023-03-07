package Client;

import Server.Reader;
import Server.User;
import Server.Writer;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * En klient som kan ansluta sig till systemet
 */
public class Client {
    private final String IP;
    private final int PORT;
    private String name;
    private Thread read;
    ObjectInputStream ois;
    ObjectOutputStream oos;
    Socket socket;
    ClientUI clientUI;
    LoginUI loginUI;
    ImageIcon imageIcon;

    /**
     * Sparar IP och PORT. Startar ett GUI där klienten ska logga in eller registrera sig
     */
    public Client(){
        this.IP = "localhost";
        this.PORT = 1234;

        this.loginUI = new LoginUI(this);
    }

    /**
     * när en klient tryck på connect i loginGUIt sparas användarnamnet. Klienten ska välja en bild
     * eller inte beroende på om den loggar in eller registrerar sig.
     * @param username
     * @param login
     * @throws IOException
     */
    public void connectClicked(String username, boolean login) throws IOException {
        this.name = username;

        if (!login){ //om klienten inte tryck på logga in ska klienten välja en bild som profilbild
            this.imageIcon = new ImageIcon(getPicture());
            this.clientUI = new ClientUI(this, username); //skapar ett user interface för klienten
            clientUI.updateImageIcon(imageIcon); //uppdaterar klientens profilbild

            socket = new Socket(IP, PORT); //startar socket
            clientUI.writeConnectMessage(socket);

            //ObjectOutputStream och ObjectInputStream som används för att läsa och skriva till servern
            this.oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            this.ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

            //Skriver in användaren till filen med registrerade användare
            Writer.writeAddUser(username,imageIcon);

            //skickar användaren till servern
            oos.writeObject(new Message<User>(new User(name, imageIcon)));
            oos.flush();

            startConnection(username);
        }

        if (login){ //om användaren valt att logga in
            if (Reader.readIfUserExist(username)){ //kollar om användarnamnet finns bland registreade användare
                ImageIcon temp = (ImageIcon) Reader.readUsers().get(username);

                //TODO vet inte vad som händer, får en default bild om det inte finns nån?
                if (temp == null) {
                    imageIcon = new ImageIcon("files/Stockx_logo.png");
                }

                this.clientUI = new ClientUI(this, username); //Skapar ett UI för klienten

                socket = new Socket(IP, PORT); //startar socket
                clientUI.writeConnectMessage(socket);

                //ObjectOutputStream och ObjectInputStream som används för att läsa och skriva till servern
                this.oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                this.ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

                //skickar användaren till servern
                oos.writeObject(new Message<User>(new User(name, temp)));
                oos.flush();

                //uppdaterar klientens profilbild
                clientUI.updateImageIcon(temp);

                startConnection(username);
            }else{ //skriver om ingen användare finns registrerad
                JOptionPane.showMessageDialog(null, "No user with the name " + username + " is registered.", "Login", JOptionPane.INFORMATION_MESSAGE);
                loginUI.noUserExist();
                new LoginUI(this);
            }

        }

    }

    /**
     * Hämtar vänner och startar en tråd som läser meddelanden
     * @param username användarnamn på klienten
     * @throws IOException
     */
    public void startConnection(String username) throws IOException {
        clientUI.updatePane(IP, PORT);

        //gör en lista och lägger till vänner i den
        ArrayList<List<String>> Friends = Reader.readFriends();
        for (List<String> friendList : Friends) {
            if (Objects.equals(friendList.get(0), username)) { //skriver ut om en vän har hittats
                System.out.println("Found " + username + " in the friend list: " + friendList.get(1));
            }
        }

        read = new Read();
        read.start();
    }

    public void sendMessage(String text) {
        try {
            if (text.equals("")) {
                return;
            }
            clientUI.setOldMsg(text);
            oos.writeObject(new Message<String>(text));
            oos.flush();
            clientUI.updateChatPanel();

        } catch (Exception ex) {
            clientUI.showExceptionMessage(ex);
            System.exit(0);
        }
    }
    public void sendPicture (ImageIcon imageIcon) {
        try {
            ImageIcon image = imageIcon;
            if (image == null) {
                return;
            }
            clientUI.setOldImage(image);
            oos.writeObject(new Message<ImageIcon>(image));
            oos.flush();
            clientUI.updateChatPanel();

        } catch (Exception ex) {
            clientUI.showExceptionMessage(ex);
            System.exit(0);
        }
    }

    public void disconnectPressed() {
        try {
            ois.close();
            read.interrupt();
            clientUI.disconnectUpdate();
            oos.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    // read new incoming messages
    class Read extends Thread {
        @Override
        public void run() {
            try {
                while (socket.isConnected()) {
                    Message<?> msg = (Message<?>) ois.readObject();
                    if (msg.getPayload() instanceof String newMessage) {
                        String message = (String) msg.getPayload();
                        if (message != null) {
                            if (message.charAt(0) == '[') {
                                System.out.println(message);
                                message = message.substring(1, message.length() - 1);
                                ArrayList<String> ListUser = new ArrayList<String>(Arrays.asList(message.split(", ")));
                                ArrayList<List<String>> Friends = Reader.readFriends();
                                clientUI.updateUsers();


                                for (String user : ListUser) {
                                    boolean isFriend = false;
                                    for (List<String> friendList : Friends) {
                                        if (Objects.equals(friendList.get(0), name) && Objects.equals(friendList.get(1), user)) {
                                            System.out.println("Vänner");
                                            clientUI.updateUsersFriendsMessage(user);
                                            isFriend = true;
                                            break;
                                        }
                                    }
                                    if (!isFriend) {
                                        System.out.println("Inte Vänner");
                                        clientUI.updateUsersPane(user);
                                    }
                                }
                            } else {
                                clientUI.updateUsersMessage(newMessage);
                            }
                        }
                    } else if (msg.getPayload() instanceof ImageIcon) {
                        clientUI.updateImage((ImageIcon) msg.getPayload());
                    } else if (msg.getPayload() instanceof ArrayList userList) {
                        System.out.println("när körs detta?");
                        System.out.println(userList);
                        //clientUI.ClearUserpane();
                        //clientUI.updateUsersList(userList);

                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * main metod för att starta en ny klient
     * @param args
     */
    public static void main(String[] args)  {
        Client client = new Client();
    }
}
