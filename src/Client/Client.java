package Client;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Client {
    private String IP;
    private int PORT;
    private String name;
    private Thread read;
    BufferedReader input;
    BufferedWriter bufferedWriter;
    ObjectInputStream ois;
    ObjectOutputStream oos;
    Socket socket;
    ClientUI clientUI;
    LoginUI loginUI;

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
        this.clientUI = clientUI;

        if (!login){
            ImageIcon imageIcon = new ImageIcon(getPicture());
            clientUI.updateImage(imageIcon);
        }

        clientUI.updatePane(IP, PORT);

        socket = new Socket(IP, PORT);
        clientUI.writeConnectMessage(socket);

        this.oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        this.ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

        oos.writeObject(new Message<String>(name));
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

    /***
     * Fixa detta
     */
    public void sendPicture (ImageIcon imageIcon) {
        try {
            ImageIcon image = imageIcon;
            if (image == null) {
                return;
            }
            clientUI.setOldImage(image);


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
                    if (msg.getPayload() instanceof String) {
                        //clientUI.appendToPane((String) msg.getPayload()); //todo fix
                    } else if (msg.getPayload() instanceof ImageIcon) {
                        clientUI.updateImage((ImageIcon) msg.getPayload());
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
