package Client;

import Server.Reader;
import Server.User;
import Server.Write;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import java.io.*;
import java.net.Socket;
import java.util.*;

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

    public Client(){
        this.IP = "localhost";
        this.PORT = 1233;

        this.loginUI = new LoginUI(this);
    }

    public static void main(String[] args)  {
        Client client = new Client();
    }

    public void connectClicked(String username, ClientUI clientUI, boolean login) throws IOException {
        this.name = username;
        this.imageIcon = imageIcon;
        this.clientUI = clientUI;

        socket = new Socket(IP, PORT);
        clientUI.writeConnectMessage(socket);

        this.oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

        if (!login){
            this.imageIcon = new ImageIcon(getPicture());
            //clientUI.updateImageIcon(imageIcon);
            clientUI.updateImageIcon(imageIcon);

            Write.writeAddUser(username,imageIcon);

            oos.writeObject(new Message<User>(new User(name, imageIcon)));
            oos.flush();
        }
        if (login){
            ImageIcon temp = (ImageIcon) Reader.readUsers().get(username);
            if (temp == null){
                imageIcon = new ImageIcon("files/Stockx_logo.png");
            }

            oos.writeObject(new Message<User>(new User(name, temp)));
            oos.flush();

            clientUI.updateImageIcon(temp);
        }

       // clientUI.updateImageIcon(imageIcon);
        clientUI.updatePane(IP, PORT);


        ArrayList<List<String>> Friends = Reader.readFriends();
        for (List<String> friendList : Friends) {
            if (Objects.equals(friendList.get(0), username)) {
                // Found the username in the current friend list
                System.out.println("Found " + username + " in the friend list: " + friendList.get(1));


                // Do something with the friend list, e.g. display it in the UI
                //clientUI.updateFriendList(friendList); //todo byta färg eller något.
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
}
