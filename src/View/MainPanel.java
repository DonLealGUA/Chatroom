package View;

import Controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.security.PublicKey;

public class MainPanel extends JPanel {
    private TPanel tPanel;
    private ChatPanel chatPanel;
    private Boolean friendBoolean;
    private FriendsPanel friendsPanel;
    private AllOnlinePanel allOnlinePanel;

    public MainPanel(int width, int height, Controller controller, Boolean friendBoolean, String userName, ImageIcon imageIcon) {
        super(null);
        this.setSize(width, height);
        this.friendBoolean = friendBoolean;

        chatPanel = new ChatPanel(width - 200, height-75, userName, controller);
        chatPanel.setBackground(Color.WHITE);
        add(chatPanel);


        friendsPanel = new FriendsPanel(200, height-75, controller);
        friendsPanel.setBackground(Color.WHITE);
        add(friendsPanel);

        allOnlinePanel = new AllOnlinePanel(200, height-75, controller);
        allOnlinePanel.setBackground(Color.GREEN);
        add(allOnlinePanel);

        if (friendBoolean){
            this.allOnlinePanel.setVisible(false);
        }
        else{
            this.friendsPanel.setVisible(false);
        }

        this.allOnlinePanel = allOnlinePanel;
        this.friendsPanel = friendsPanel;

        tPanel = new TPanel(width, 75, userName, imageIcon);
        tPanel.setBackground(Color.WHITE);
        add(tPanel);
    }

    public void updateBoolean(Boolean friendBoolean){
        this.friendBoolean = friendBoolean;
        if (friendBoolean){
            this.allOnlinePanel.setVisible(false);
            this.friendsPanel.setVisible(true);
        }
        else{
            this.allOnlinePanel.setVisible(true);
            this.friendsPanel.setVisible(false);
        }
    }

    public ChatPanel getchat() {
        return chatPanel;
    }

    public FriendsPanel getFriendsPanel() {
        return friendsPanel;
    }

    public AllOnlinePanel getAllOnlinePanel() {
        return allOnlinePanel;
    }

    public void setFriendName(String text) {
        tPanel.setFriendName(text);
    }

}
