package Server;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Writer-klass som skriver till textfiler
 */
public class Writer {
    /**
     * skriver alla meddelanden som skickas till chat-textfilen
     * @param User
     * @param message
     */
    //TODO vi läser aldrig från den filen så osäker om denna metod behövs
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

    /**
     * Skriver allt som händer i chatten till textfilen server. T.ex om en användare kopplat upp sig eller skickat ett meddelande
     * @param message det vi ska skriva
     */
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

    /**
     * Skriver in den nya användaren som registrerat sig till users-textfilen
     * @param User användarnamn
     * @param image profilbild
     */
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

    /**
     * skriver in om någon har lagt till som vän
     * @param User användaren som lagt till någon
     * @param isFriendsWith den som användaren la till
     */
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
     * skriver in meddelanden som inte har kunnat skickas eftersom mottagaren är offline
     * @param userSender användaren som skickade
     * @param message meddelandet
     */
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
