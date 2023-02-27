package Server;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class Reader {


    public static ArrayList readChat(){
        try {
            ArrayList<String> chat = new ArrayList<String>();
            File myObj = new File("files/chat.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                chat.add(myReader.nextLine());
            }
            System.out.println(chat);
            return chat;


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList readPrivateChat(String User, String isFriendWith){
        try {
            ArrayList<String> chat = new ArrayList<String>();
            File myObj = new File("files/privateMesseges/\" +User+ \"/\" +isFriendWith+ \".txt");
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


    public static HashMap readUsers(){
        try {
            File myObj = new File("files/Users.txt");
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(myObj)));
            HashMap<String, ImageIcon> chat = new HashMap<>();
            while (true) {
                String line = r.readLine();
                if (line == null) { break; }
                List<Object> res = List.of(line.split(" "));
                String username = (String) res.get(0);
                ImageIcon image = new ImageIcon((String) res.get(1)); //TODO kanske behöver fixas 
                 chat.put(username,image);
            }
            return chat;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static ArrayList<List<String>> readFriends() throws IOException {
        String fnam = "files/Friends.txt";
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(fnam)));
        ArrayList<List<String>> testCaseData = new ArrayList<>();
        ArrayList<String> test = new ArrayList<>();

        while (true) {
            String line = r.readLine();
            if (line == null) { break; }
            assert line.length() == 11; // indatakoll, om man kör med assertions på
            List<String> items = new ArrayList<String>(Arrays.asList(line.split("\n")));
            test.add(items.get(0).toString());
        }
        for(int i = 0; i < test.size(); i++){
            List<String> items = new ArrayList<String>(Arrays.asList(test.get(i).split(" ")));
            testCaseData.add(items);
        }
        System.out.println(testCaseData);
        return testCaseData;
    }
}
