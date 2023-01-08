package Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servertest {

    private ServerSocket serverSocket;

    public Servertest(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try{
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Servertest servertest = new Servertest(serverSocket);
        servertest.startServer();


    }
}
