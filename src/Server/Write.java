package Server;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Write {

    public static void main(String[] args) {
        ImageIcon imageIcon = new ImageIcon("files/Stockx_logo.png");
        Write.writeAddUser("Testare",imageIcon);
    }

    public static void writeChat(String User, String messege){
        try{
            FileWriter fstream = new FileWriter("files/chat.txt",true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(User + " " + messege);
            out.close();
        }catch (Exception e){
            System.err.println("Error while writing to file: " +
                    e.getMessage());
        }
    }


    public static void writeAddUser(String User, ImageIcon image){
        try{
            FileWriter fstream = new FileWriter("files/Users.txt",true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(User + " " + image);
            out.close();
        }catch (Exception e){
            System.err.println("Error while writing to file: " +
                    e.getMessage());
        }
    }

    public static void writeFriends(String User, String isFriendsWith){
        try{
            FileWriter fstream = new FileWriter("files/Friends.txt",true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(User + " " + isFriendsWith);
            out.close();
        }catch (Exception e){
            System.err.println("Error while writing to file: " +
                    e.getMessage());
        }

    }

    public static void writePrivatChat(String User,String isFriendWith,String messege){
        File file = new File("files/" +User+ "/" +isFriendWith+ ".txt");
        try {
            if (file.createNewFile()) {
                System.out.println("File has been created.");
                FileWriter fstream = new FileWriter(file,true);
                BufferedWriter out = new BufferedWriter(fstream);
                out.write(messege);
                out.close();

            } else {
                System.out.println("File already exists.");
                FileWriter fstream = new FileWriter(file,true);
                BufferedWriter out = new BufferedWriter(fstream);
                out.write(messege);
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
