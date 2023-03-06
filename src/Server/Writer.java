package Server;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {


    public static void writeChat(String User, String message){
        try{
            FileWriter fstream = new FileWriter("files/chat.txt",true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(User + " " + message +"\n");
            out.close();
        }catch (Exception e){
            System.err.println("Error while writing to file: " + e.getMessage());
        }
    }


    public static void writeServerLogg(String message){
        try{
            FileWriter fstream = new FileWriter("files/server.txt",true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write( message +"\n");
            out.close();
        }catch (Exception e){
            System.err.println("Error while writing to file: " + e.getMessage());
        }
    }

    public static void writeAddUser(String User, ImageIcon image){
        try{
            FileWriter fstream = new FileWriter("files/Users.txt",true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(User + " " + image +"\n");
            out.close();
        }catch (Exception e){
            System.err.println("Error while writing to file: " + e.getMessage());
        }
    }


    public static void writeFriends(String User, String isFriendsWith){
        try{
            FileWriter fstream = new FileWriter("files/Friends.txt",true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(User + " " + isFriendsWith + System.lineSeparator()); // write with line separator
            out.close();
        }catch (Exception e){
            System.err.println("Error while writing to file: " + e.getMessage());
        }
    }

    /**
     * Tror inte den behövs längre
     */
    public static void writePrivatChat(String User,String isFriendWith,String messege){
        File file = new File("files/privateMesseges/" +User+ "/" +isFriendWith+ ".txt");
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

    public static void writeUnsentMessage(String userSender,String message){
        try{
            FileWriter fstream = new FileWriter("files/unSentMessages.txt",true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(userSender + " " + message );
            out.close();
        }catch (Exception e){
            System.err.println("Error while writing to file: " + e.getMessage());
        }
    }
}
