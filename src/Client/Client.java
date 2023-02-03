package Client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
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
    //PrintWriter output;
    Socket server;
    ClientUI clientUI;
    LoginUI loginUI;

    public Client(){
        this.serverName = "localhost";
        this.PORT = 1234;
        this.name = "nickname";
        this.loginUI = new LoginUI(this);
    }

    public static void main(String[] args)  {
        Client client = new Client();

    }

    public void sendMessage(String text) {
        try {
            String message = text;
            if (message.equals("")) {
                return;
            }

            String messageToSend = message;

            clientUI.setOldMsg(messageToSend);
            //this.oldMsg = message;
            bufferedWriter.write(message);
            System.out.println(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
           // output.println(message);

            clientUI.updateChatPanel();

        } catch (Exception ex) {
            clientUI.showExceptionMessage(ex);
            System.exit(0);
        }
    }

    public void sendMessageSpara(){

    }

    /***
     * Fixa detta
     */
    public void sendPicture(ImageIcon imageIcon) {
        try {
            ImageIcon image = imageIcon;
            if (image == null) {
                return;
            }
            clientUI.setOldImage(image);
            //output.println(message);
            clientUI.updateChatPanel();

        } catch (Exception ex) {
            clientUI.showExceptionMessage(ex);
            System.exit(0);
        }
    }

    public String getServerName(){
        return serverName;
    }
    public void connectClicked(String username, ClientUI clientUI, boolean login) throws IOException {
        this.name = username;
        this.clientUI = clientUI;

        if (!login){
            ImageIcon imageIcon = new ImageIcon(getPicture());
            clientUI.updateImage(imageIcon);
        }

        clientUI.updatePane(serverName, PORT);

        server = new Socket(serverName, PORT);

        clientUI.writeConnectMessage(server);

        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
        this.input = new BufferedReader(new InputStreamReader(server.getInputStream()));
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

    public void disconnectPressed() {
        try {
            read.interrupt();
            clientUI.disconnectUpdate();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // read new incoming messages
    class Read extends Thread {

        public void SkickaObjekt() throws IOException {

            /*OutputStream outputStream = server.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(image);
            System.out.println(image);
            server.close();*/
        }

        public void GamlaLäsa(){
           /* String message;
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
            }*/
        }

        @Override
        public void run() {
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
