package Model;

import Controller.Controller;

import javax.naming.ldap.Control;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable {
    private String username;
    private ImageIcon imageIcon;
    private boolean isOnline;
    private boolean isFriend;
    private ArrayList<User> users;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public User(String username, ImageIcon imageIcon, boolean isFriend, boolean isOnline){
        this.username = username;
        this.imageIcon = imageIcon;
        this.isFriend = isFriend;
        this.isOnline = isOnline;
        users = new ArrayList<>();
    }

    public String getUserName() {
        return username;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public ImageIcon getIconImage() {
        return imageIcon;
    }

    public void setOnlineStatus(boolean status){
        this.isOnline = status;
    }

    public void setIsFriend(boolean status){
        this.isFriend = status;
    }

    public User getSpecificUser(String username){
        User selectedUser = null;
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getUserName() == username){
               selectedUser = users.get(i);
            }
        }
        return selectedUser;
    }
}
