package Client;

import Server.User;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

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
        this.PORT = 1234;

        this.loginUI = new LoginUI(this);
    }

    public static void main(String[] args)  {
        Client client = new Client();
    }

    public void connectClicked(String username, ClientUI clientUI, boolean login) throws IOException {
        this.name = username;
        this.imageIcon = imageIcon;
        this.clientUI = clientUI;

        if (!login){
            ImageIcon imageIcon = new ImageIcon(getPicture());
            clientUI.updateImageIcon(imageIcon);
        }

        clientUI.updatePane(IP, PORT);

        socket = new Socket(IP, PORT);
        clientUI.writeConnectMessage(socket);

        this.oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        this.ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

        oos.writeObject(new Message<User>(new User(name, imageIcon)));
        oos.flush();
       // System.out.println("Skickar namn");



       // this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
       // this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //input = new BufferedReader(new InputStreamReader(server.getInputStream()));
        // output = new PrintWriter(server.getOutputStream(), true);

        // send nickname to server
        //bufferedWriter.write(name);
       // bufferedWriter.newLine();
        //bufferedWriter.flush();

        //output.println(name);

        read = new Read();
        read.start();

    }

    public void sendMessage(String text) {
        try {
            if (text.equals("")) {
                return;
            }
            clientUI.setOldMsg(text);
            System.out.println(text);
            oos.writeObject(new Message<String>(text));
            System.out.println("sent");
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
            System.out.println(image);
            oos.writeObject(new Message<ImageIcon>(image));
            System.out.println("sent image");
            oos.flush();
            clientUI.updateChatPanel();

        } catch (Exception ex) {
            clientUI.showExceptionMessage(ex);
            System.exit(0);
        }
    }

    public String getIP(){
        return IP;
    }

    public void disconnectPressed() {
        try {
            read.interrupt();
            clientUI.disconnectUpdate();
            ois.close();
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
                        System.out.println(newMessage);
                        System.out.println(" got message");
                        clientUI.updateUsersMessage(newMessage);
                    } else if (msg.getPayload() instanceof ImageIcon) {
                        clientUI.updateImage((ImageIcon) msg.getPayload());
                    } else if (msg.getPayload() instanceof ArrayList userList){
                        System.out.println("när körs detta?");
                        System.out.println(userList);
                        clientUI.updateUsersList(userList);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
