package View;
import Controller.*;
import javax.swing.*;
public class LoginPane {
    JFrame f;
    Controller controller;

    public LoginPane() {
        f = new JFrame();
        this.controller = controller;
    }

    public String usernamePopUp(){
        String name = JOptionPane.showInputDialog(f, "Enter Username");
        return name;
    }

    public void giveOptions(String username, Controller controller){
        int a=JOptionPane.showConfirmDialog(f,"Do you want to add this person as your friend?");
        if(a==JOptionPane.YES_OPTION) {
            controller.addFriend(username);
        }
    }
}

