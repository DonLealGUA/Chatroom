package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientThread extends Thread {

    private BufferedReader input;
    private ClientGUI gui;

    public ClientThread(BufferedReader input, ClientGUI gui){
        this.input = input;
        this.gui = gui;
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
                        ArrayList<String> ListUser = new ArrayList<String>(Arrays.asList(message.split(", ")));
                        gui.setjTextListUsers(null);
                        for (String user : ListUser) {
                            gui.updateList(user);
                        }
                    }else{
                        gui.updateMessage(message);
                    }
                }
            }
            catch (IOException ex) {
                System.err.println("Failed to parse incoming message");
            }
        }
    }
}
