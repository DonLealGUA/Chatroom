package Controller;

import Model.Buffer;
import Model.Message;
import Model.User;
import View.ChatPanel;
import View.LoginPane;
import View.MainFrame;
import com.sun.tools.javac.Main;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Client {
   private User user;
    private Socket socket;
    private Controller controller;
    private ArrayList<User> allPeople;
    private boolean activatedCommunicator = true;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Buffer<Object> unhandledObjects = new Buffer<>();
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String message;
    private ChatPanel chatPanel;


    public Client(String ipAdress, int port, User user) {
        try {

            socket = new Socket(ipAdress, port);
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.user = user;
            System.out.println("här");
        } catch (IOException e) {
            e.printStackTrace();
        }
        thread thread = new thread();
        thread.start();
    }

    /**
     * Skicka meddelanden i chatten
       * */

    public void sendMessage(){
        try{
            bufferedWriter.write(user.getUserName());
            bufferedWriter.newLine();
            bufferedWriter.flush();
            while(socket.isConnected()){
                bufferedWriter.write(user.getUserName()+ ": " + controller.getMessageText());
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lyssnar för meddelanden och skikcar till gui   * */

    public void listenForMesseges(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromchat;
                while(socket.isConnected()){
                    try {
                        msgFromchat = bufferedReader.readLine();
                        chatPanel.objectList.add(msgFromchat);

                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }

                }
            }
        }).start();
    }



    public void putMessageInBuffer(Message message){
        unhandledObjects.put(message);
    }

    public void updateOfUser(User user){
        unhandledObjects.put(user);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void disconnect() throws IOException {
        socket.close();
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

    public class thread extends Thread{
        @Override
        public void run() {
            try {
                oos.writeObject(user);
                oos.flush();
                new InputCommunicator().start();
                System.out.println("igen");
                new OutputCommunicator().start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public class InputCommunicator extends Thread{
        @Override
        public void run(){
            System.out.println("ännu igen");
            Object temp;
            try {
                while(true){
                    System.out.println("Test BEFORE");
                    temp = ois.readObject(); //Fel på denna????!!!!!!
                    System.out.println("Test After");
                    if(temp instanceof User[]) {
                         allPeople = new ArrayList<User>(List.of((User[]) temp));
                         user.setUsers(allPeople);
                        System.out.println("it arrived!");
                        controller.updateList(allPeople);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
            }
        }
    }

    public class OutputCommunicator extends Thread {
        public void run() {
            try {
                while (!Thread.interrupted()) {
                        Object unhandledObject = unhandledObjects.get();
                        oos.writeObject(unhandledObject);
                        oos.flush();
                }
            } catch (InterruptedException | IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        LoginPane loginPane = new LoginPane();
        String username = loginPane.usernamePopUp();
        System.out.println(username);
        ImageIcon imageIcon = new ImageIcon(getPicture());
        User user = new User(username, imageIcon, true, true);
        Client client = new Client("localhost", 1234, user);
        //Controller controller = new Controller(client, user);
    }

}
