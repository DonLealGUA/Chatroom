package Server;

import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * Reader-klass som läser från textfiler
 */
public class Reader {

    /**
     * läser fil med användare för att kolla om en viss användare finns med
     * @param newUsername användaren som man vill kolla om den finns med
     * @return true om användaren finns med, false annars
     */
    public static boolean readIfUserExist(String newUsername){
        try {
            File myObj = new File("files/Users.txt");
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(myObj)));
            while (true) {
                String line = r.readLine();
                if (line == null) { break; }
                List<Object> res = List.of(line.split(" "));
                String username = (String) res.get(0);
                if (newUsername.equals(username)){
                    return true;
                }
            }
            return false;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    //TODO ta bort?
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

    /**
     * Läser ur server-textfilen för att skriva ut det i server-GUI:t
     * @return en array med server-loggen
     */
    public static ArrayList readServerLogg(){
        try {
            ArrayList<String> chat = new ArrayList<String>();
            File myObj = new File("files/server.txt");
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

    //TODO ta bort?
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

    /**
     * Läser ur unSentMessages-textfilen för att kolla om en användare har missat några meddelanden
     * @param username användaren man ska kolla om den har meddelanden
     * @return en lista med missade meddelanden
     */
    public static List<String> getUnsentMessages(String username) throws IOException {
        List<String> unsentMessages = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader("files/unSentMessages.txt"));
        PrintWriter writer = new PrintWriter(new FileWriter("files/unSentMessages_temp.txt"));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ", 2);
            if (parts.length == 2 && parts[0].equals(username)) {
                unsentMessages.add(parts[1]);
            } else {
                writer.println(line);
            }
        }
        reader.close();
        writer.close();
        boolean deleteSuccess = new File("files/unSentMessages.txt").delete();
        boolean renameSuccess = new File("files/unSentMessages_temp.txt").renameTo(new File("files/unSentMessages.txt"));
        if (!deleteSuccess || !renameSuccess) {
            throw new IOException("Failed to update unsent messages file.");
        }
        return unsentMessages;
    }

    /**
     * Läser från Users-textfilen för att hämta lista med användare
     * @return en hashmap med string och imageicon
     */
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
                ImageIcon image = new ImageIcon((String) res.get(1));
                chat.put(username,image);
            }
            return chat;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Läser ur filen Friends
     * @return en lista med text från filen
     */
    public static ArrayList<List<String>> readFriends() throws IOException {
        String fnam = "files/Friends.txt";
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(fnam)));
        ArrayList<List<String>> testCaseData = new ArrayList<>();
        ArrayList<String> test = new ArrayList<>();

        while (true) {
            String line = r.readLine();
            if (line == null) { break; }
            assert line.length() == 11;
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
