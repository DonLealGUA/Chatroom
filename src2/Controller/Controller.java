package Controller;
import Model.*;
import View.*;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Controller {
    private MainFrame mainFrame;
    private Message message;

    public ClientTest getClient() {
        return client;
    }

    private ClientTest client;
    private LoginPane loginPane;
    private User user;
    private String msgToSend;

    public Controller(ClientTest client, User user) throws IOException {
        this.client = client;
        client.setController(this);
        mainFrame = new MainFrame(740, 550, this, user.getUserName(), user.getIconImage());
        loginPane = new LoginPane();
        this.user = user;
        message = new Message();
        message.setController(this);
    }

    public void updateBoolean(Boolean bool) {
        mainFrame.updateBoolean(bool);
    }

    public void sendMessage(Message message){
        client.putMessageInBuffer(message);
    }

    public void updateList(ArrayList<User> allUsers){
       // mainFrame.getMainPanel().getFriendsPanel().clearPanel();
        //mainFrame.getMainPanel().getAllOnlinePanel().clearPanel();

        int amountOfFriends = 0;
        int amountOfNonFriends = 0;
        int allOnline = 0;
        for(int i = 0; i < allUsers.size(); i++){
            if(allUsers.get(i).isFriend()){
                amountOfFriends++;
            } else{
                amountOfNonFriends++;
            }
        }
        String[] usernameFriends = new String[amountOfFriends];
        ImageIcon[] imageIconFriends = new ImageIcon[amountOfFriends];

        String[] usernameAll = new String[amountOfNonFriends];
        ImageIcon[] imageIconsAll = new ImageIcon[amountOfNonFriends];

        int[] positionOnline = new int[allOnline];
        int friendIndex = 0;
        int allIndex = 0;

        ImageIcon imageIcon = new ImageIcon("files/fotoicon.png");
        Image image = imageIcon.getImage(); // transform it
        Image newimg = image.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        ImageIcon icon = new ImageIcon(newimg);
        new User("Peter",icon,true,true);

        for(int i = 0; i < allUsers.size(); i++){
            User user = allUsers.get(i);
            if (allUsers.get(i).isFriend()) {
                usernameFriends[friendIndex] = user.getUserName();
                imageIconFriends[friendIndex] = user.getIconImage();
                friendIndex++;
            } else {
                usernameAll[allIndex] = user.getUserName();
                imageIconsAll[allIndex] = user.getIconImage();
                allIndex++;
            }
        }

        mainFrame.getMainPanel().getAllOnlinePanel().addUsers(usernameAll, imageIconsAll, positionOnline);
        mainFrame.getMainPanel().getFriendsPanel().addUsers(usernameFriends ,imageIconFriends);
    }

    public void setSelectedImage(ImageIcon imageIcon){
        message.setImageIcon(imageIcon);
    }

    public void setMessageText (String messageText){
        msgToSend = messageText;
    }

    public String getMessageText (){
        return msgToSend;
    }

    public ChatPanel getChatP(){
        ChatPanel chatpanel = mainFrame.getMainPanel().getchat();
        return chatpanel;
    }

    public void askIfFriend(String userName){
        loginPane.giveOptions(userName, this);
    }

    public void addFriend(String username){
        user.getSpecificUser(username).setFriend(true);
        User user1 = user.getSpecificUser(username);
        client.updateOfUser(user1);
    }

    public void setFriendName(String text) {
        mainFrame.setFriendName(text);
    }
    public void connectWithMessage(){
        message.sendThisMessage();
    }
 }








