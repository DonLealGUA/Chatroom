package Server;

import javax.swing.*;
import java.io.*;

/**
 * User-klass där användare sparas
 */
public class User implements Serializable {
    private final String username; //användarnamn
    private final ImageIcon imageIcon; //profilbild

    /**
     * skapar en user
     * @param username användarnamn
     * @param imageIcon profilbild
     */
    public User(String username, ImageIcon imageIcon) throws IOException {
        this.username = username;
        this.imageIcon = imageIcon;
    }


    //getter och setter
    public String getUsername() {
        return this.username;
    }

    public ImageIcon getImageIcon(){
        return imageIcon;
    }

    //toString-metod
    public String toString(){
        return this.getUsername();
    }

}
