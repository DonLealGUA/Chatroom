package Client;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Client {
    private String serverName;
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
        this.serverName = "localhost";
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

        clientUI.updatePane(serverName, PORT);

        socket = new Socket(serverName, PORT);

        clientUI.writeConnectMessage(socket);

        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //input = new BufferedReader(new InputStreamReader(server.getInputStream()));
        // output = new PrintWriter(server.getOutputStream(), true);

        // send nickname to server
        bufferedWriter.write(name);
        bufferedWriter.newLine();
        bufferedWriter.flush();

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
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(new Message<String>(text));
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

    public String getServerName(){
        return serverName;
    }

    public void disconnectPressed() {
        try {
            read.interrupt();
            clientUI.disconnectUpdate();
            bufferedWriter.close();
            ois.close();
            oos.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // read new incoming messages
    class Read extends Thread {

        public void GamlaLäsa(){
            String message;
            while(!Thread.currentThread().isInterrupted()){
                try {
                    message = input.readLine();
                    if(message != null){
                        if (message.charAt(0) == '[') {
                            message = message.substring(1, message.length()-1);
                            ArrayList<String> ListUser = new ArrayList<String>(
                                    Arrays.asList(message.split(", "))
                            );
                            clientUI.updateUsers();
                            for (String user : ListUser) {
                                clientUI.updateUsersPane(user);
                            }
                        }else{

                            clientUI.updateUsersMessage(message);
                        }
                    }
                }
                catch (IOException ex) {
                    clientUI.printError();
                }
            }
        }

        /**
         * invalid stream header:
         * Problem här
         */
        @Override
        public void run() {
            while (socket.isConnected()) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    Message<?> msg = (Message<?>) ois.readObject();

                    if (msg.getPayload() instanceof String) {
                        System.out.println("String");
                        System.out.println(msg.getPayload());
                    } else if (msg.getPayload() instanceof ImageIcon) {
                        System.out.println(msg.getPayload());
                        System.out.println("Bild");
                    }

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("Probem");
                }
            }
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

}
