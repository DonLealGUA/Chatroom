package Client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class Client{
    private String serverName;
    private int PORT;
    private String username;
    private ClientGUI gui;
    private String oldMsg = "";
    PrintWriter output;
    Socket server;
    BufferedReader input;
    Thread clientThread;

    public static void main(String[] args) throws Exception {
        Client client = new Client();
    }

    public Client()  {
        this.serverName = "localhost";
        this.PORT = 1234;
        this.username = "nickname";

        gui = new ClientGUI(this);

    }

    public void createServerConnection(String name, int p) throws IOException {
        server = new Socket(name, p);
    }

    public void sendMessage() {
        try {
            String message = gui.jtextInputChat.getText().trim();
            if (message.equals("")) {
                    return;
            }
            this.oldMsg = message;
            output.println(message);
            gui.jtextInputChat.requestFocus();
            gui.jtextInputChat.setText(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            System.exit(0);
        }
    }

    public String getOldMsg() {
        return oldMsg;
    }

    public void setOldMsg(String currentMessage) {
        oldMsg = currentMessage;
    }

    public String getUsername(){
        return username;
    }

    public int getPort() {
        return PORT;
    }

    public String getServerName() {
        return serverName;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public void connectPressed(String name, String serverName, int port) throws IOException {
        this.username = name;
        this.serverName = serverName;
        this.PORT = port;

        createServerConnection(serverName, port);

        gui.printMessage(serverName,port,server.getRemoteSocketAddress());

        input = new BufferedReader(new InputStreamReader(server.getInputStream()));
        output = new PrintWriter(server.getOutputStream(), true);

        // send nickname to server
        output.println(name);

        this.clientThread = new ClientThread(input, gui);
        clientThread.start();

    }

    public void disconnectClicked() {
        clientThread.interrupt();
    }

    public void disconnect() {
        output.close();
    }
}
