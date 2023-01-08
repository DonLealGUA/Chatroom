package Controller;

import Model.Buffer;
import Model.Message;
import Model.User;
import View.ChatPanel;
import View.LoginPane;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.net.Socket;

public class ClientTest {
    private Buffer<Object> unhandledObjects = new Buffer<>();

    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String message;
    private String username;
    private ChatPanel chatPanel;
    private User user;
    private Socket socket;
    private  Controller controller;


    public ClientTest(Socket socket, User user) {
        try {
            this.socket =socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.user = user;
            this.username = user.getUserName();
        } catch (IOException e) {
            closeEverything(socket,bufferedReader,bufferedWriter);
            e.printStackTrace();
        }
    }

    public String setupMSG(){
        String msg = controller.getMessageText();
        return msg;
    }

    /**
     * Skicka meddelanden i chatten
     */
    public void sendMessage(String msg){
        try{
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            while(socket.isConnected() ){
                bufferedWriter.write(msg); //Problem med denna
                System.out.println(msg);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    /**
     * Lyssnar för meddelanden och skickar till gui
     */
    public void listenForMesseges(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromchat;
                while(socket.isConnected()){
                    try {
                        msgFromchat = bufferedReader.readLine();
                        System.out.println(msgFromchat);
                        controller.getChatP().addRecivedMessage(msgFromchat);

                    } catch (IOException e) {
                        e.printStackTrace();
                        closeEverything(socket,bufferedReader,bufferedWriter);
                        break;
                    }
                }
            }
        }).start();
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

    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * idk
     */
    public void putMessageInBuffer(Message message){
        unhandledObjects.put(message);
    }

    public void updateOfUser(User user){
        unhandledObjects.put(user);
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        LoginPane loginPane = new LoginPane();
        String username = loginPane.usernamePopUp();
        System.out.println(username);
        ImageIcon imageIcon = new ImageIcon(getPicture());
        User user = new User(username, imageIcon, true, true);
        Socket socket = new Socket("localhost", 1234);
        ClientTest client = new ClientTest(socket,user);
        Controller controller = new Controller(client, user);
        client.listenForMesseges();
        client.sendMessage("Joined");
    }
}

