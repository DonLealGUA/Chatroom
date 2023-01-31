package Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ReadChat {


    public static ArrayList readChat(){
        try {
            ArrayList<String> chat = new ArrayList<String>();
            File myObj = new File("src/Server/chat.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                chat.add(myReader.nextLine());
            }
            return chat;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
