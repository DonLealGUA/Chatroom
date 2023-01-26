package Controller;

import Model.Buffer;
import Model.Message;
import Model.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server{
    private int port;
    private HashMap<String,ArrayList<Message>> clientsUnsentMessage = new HashMap<>();
    private HashMap<String, ClientHandler> onlineUsers = new HashMap<>();
    private HashMap<String, String> friendRequest = new HashMap<>();
    private HashMap<String,String> friends = new HashMap<>();
    public User[] allMembersArray;
    private ArrayList<User> allMembers = new ArrayList<>();
    private Buffer<Message> allMessages = new Buffer<>();
    private ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    public Server(int port) throws IOException {
        this.port = port;
        new ThreadCreator(port);
        createFile();
        run();
    }

    public void updateAllGui(){
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendUsersToClient();
        }
    }

    private void createFile() throws IOException {
        File file = new File("DataLog.dat");
        if (file.createNewFile()) {
            System.out.println("File has been created.");
        } else {
            System.out.println("File already exists.");
        }
    }


    public void run(){
        while (!Thread.interrupted()){
            try {
                Message message = allMessages.get();
                writeToDataLog(message);
                String textContent = message.getDate() + message.getSender().getUserName() + message.getMessage();
                //serverGUI.putText(textContent);
                if(!(message.getImageIcon() == null)){
                    //serverGUI.putImage(message.getImageIcon());
                }
                for(User reciever: message.getRecievers()){
                    String username = reciever.getUserName();
                    if(onlineUsers.containsKey(username)){
                       onlineUsers.get(username).put(message);
                    } else {
                        if(clientsUnsentMessage.containsKey(username)){
                            clientsUnsentMessage.get(username).add(message);
                        }
                        else {
                            clientsUnsentMessage.put(username, new ArrayList<>());
                            clientsUnsentMessage.get(username).add(message);
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void writeToDataLog(Message message){
        try (ObjectOutputStream ois = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("DataLog.dat")))) {
            ois.writeObject(message);
            ois.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void printGUILog() {
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream("DataLog.dat")))) {
            ArrayList<Message> logList = new ArrayList<>();
            while (ois.readObject() != null) {
                logList.add((Message) ois.readObject());
            }
        } catch(ClassNotFoundException e){

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public class ThreadCreator extends Thread {
        private int port;
        private ServerSocket serverSocket;
        private Socket socket;

        public ThreadCreator(int port) throws IOException {
            this.port = port;
            this.start();
        }

        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (!Thread.interrupted()) {
                    Socket socket = serverSocket.accept();
                    new ClientHandler(socket);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public class ClientHandler {
        private Socket socket;
        private User user;
        private Buffer<Message> messageBuffer = new Buffer<>();
        private ObjectInputStream ois;
        private ObjectOutputStream oos;
        private boolean outputActivated = true;

        public ClientHandler(Socket socket) throws Exception {
            this.socket = socket;
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            Object user = ois.readObject();
            this.user = (User) user;
            onlineUsers.put((String) ((User) user).getUserName(), this);
            clientHandlers.add(this);
            checkStatus((User) user);
            if (!outputActivated){
                outputActivated = true;
                new OutputHandler().start();
            }
            new InputHandler().start();
        }

        public void checkStatus(User user) {
            user.setOnlineStatus(true);
            if (memberFound(user) == false) {
                allMembers.add(user);
                updateAllGui();
                System.out.println(allMembers.size());
            }
        }

        public boolean memberFound(User user) {
            boolean found = false;
            for (int i = 0; i < allMembers.size(); i++) {
                if (allMembers.get(i).getUserName() == user.getUserName()) {
                    found = true;
                }
            }
            return found;
        }

        public void sendUsersToClient(){
            try {
                allMembersArray = new User[allMembers.size()];
                for(int i = 0; i < allMembersArray.length; i++){
                    allMembersArray[i] = allMembers.get(i);
                }
                oos.writeObject(allMembersArray);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void put(Message message){
            messageBuffer.put(message);
        }

        public void sendFriendRequest(String friendName, String myName){
            friendRequest.put(friendName, myName);
        }

        private class InputHandler extends Thread {
            public void run() {
                try {
                    while (!Thread.interrupted()) {
                        Object temp = ois.readObject();
                        if (temp instanceof Message) {
                            Message message = (Message) temp;
                            allMessages.put((Message) message);
                        }
                        else if(temp instanceof String){
                            String username = (String) temp;
                            sendFriendRequest(username, (String) user.getUserName());
                        }
                    }
                } catch(IOException | ClassNotFoundException e){
                    // closeEverything(socket, bufferedReader);
                }
            }
        }

        private class OutputHandler extends Thread {
            public OutputHandler(){
            }

            public void run() {
                while (!Thread.interrupted()) {
                    try {

                        Message message = messageBuffer.get();
                        oos.writeObject(message);
                        oos.flush();

                    } catch (IOException | InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        private void closeEverything(Socket socket, BufferedReader bufferedReader) {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(1234);
    }
}
