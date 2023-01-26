package View;
import Controller.Controller;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.util.Map;

public class TPanel extends JPanel {
    private int height;
    private int width;
    private JLabel friendName;
    private JLabel clientName;
    private JPanel image;

    public TPanel(int width, int height, String userName, ImageIcon imageIcon) {
        this.setLayout(null);
        this.width = width;
        this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        this.height = height;
        this.setSize(width, height);
        setLocation(0, 0);
        setUp(userName, imageIcon);
    }

    private void setUp(String userName, ImageIcon imageIcon) {
        friendName = new JLabel("Chattrum");
        friendName.setLocation(350, 5);
        friendName.setSize(200, 80);
        friendName.setFont(friendName.getFont().deriveFont(25.0F));
        friendName.setHorizontalAlignment(JLabel.CENTER);
        this.add(friendName);

        clientName = new JLabel(userName);
        clientName.setLocation(60,0);
        clientName.setSize(200,80);
        clientName.setFont(clientName.getFont().deriveFont(15.0F));
        clientName.setHorizontalAlignment(JLabel.LEFT);
        this.add(clientName);

        image = new JPanel();
        image.setBackground(Color.WHITE);

        Image image2 =imageIcon.getImage(); // transform it
        Image newimg = image2.getScaledInstance(50, 50,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        ImageIcon imageIcon2 = new ImageIcon(newimg);
        image.add(new JLabel(imageIcon2));
        image.setLocation(5,10);
        image.setSize(50,50);
        image.setVisible(true);
        this.add(image);
    }

    public void setFriendName(String text){
        friendName.setText(text);

    }

    /*
    public ImageIcon setFriendPicture(String fileName){
        friendPicture = new ImageIcon(fileName);
        Image friendImage = friendPicture.getImage();
        Image newFriendImage = friendImage.getScaledInstance(50,50, java.awt.Image.SCALE_SMOOTH);
        friendPicture = new ImageIcon(newFriendImage);

        return friendPicture;
    } */
}
