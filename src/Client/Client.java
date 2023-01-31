package Client;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Client {
    private String serverName;
    private int PORT;
    private String name;
    private Thread read;
    BufferedReader input;
    PrintWriter output;
    Socket server;
    ClientUI clientUI;

    public Client(){
        this.serverName = "localhost";
        this.PORT = 1234;
        this.name = "nickname";

        this.clientUI = new ClientUI(this);
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();
    }


    public void sendMessage(String text) {
        try {
            String message = text;
            if (message.equals("")) {
                return;
            }
            clientUI.setOldMsg(message);
            //this.oldMsg = message;
            output.println(message);

            clientUI.updateChatPanel();

        } catch (Exception ex) {
            clientUI.showExceptionMessage(ex);
            System.exit(0);
        }
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

    public int getPORT(){
        return PORT;
    }

    public String getNameUser(){
        return name;
    }

    public void connectClicked(String newName, int newPort, String newServerName) throws IOException {
        this.name = newName;
        this.PORT = newPort;
        this.serverName = newServerName;

        clientUI.updatePane(serverName, PORT);

        server = new Socket(serverName, PORT);

        clientUI.writeConnectMessage(server);

        input = new BufferedReader(new InputStreamReader(server.getInputStream()));
        output = new PrintWriter(server.getOutputStream(), true);

        // send nickname to server
        output.println(name);

        read = new Read();
        read.start();

    }

    public void disconnectPressed() {
        read.interrupt();

        clientUI.disconnectUpdate();

        output.close();
    }

    // read new incoming messages
    class Read extends Thread {
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
}
