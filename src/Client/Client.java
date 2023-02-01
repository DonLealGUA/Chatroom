package Client;

import javax.swing.*;
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
