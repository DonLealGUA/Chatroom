package View;

import Controller.Controller;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

public class MainFrame {
    private Controller controller;
    private MainPanel mainPanel;
    private JFrame frame = new JFrame("Chattrum");
    private String userName;

    public MainFrame(int width, int height, Controller controller, String userName, ImageIcon imageIcon) {
        this.userName = userName;
        mainPanel = new MainPanel(width, height, controller,true, userName, imageIcon);
        this.controller = controller;
        frame.setPreferredSize(new Dimension(width, height));
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.add(this.mainPanel);
        frame.setContentPane(mainPanel);
        frame.revalidate();
        frame.repaint();

    }

    public void updateBoolean(Boolean bool){
        mainPanel.updateBoolean(bool);
    }

    public MainPanel getMainPanel() {
        return mainPanel;
    }

    public void setMainPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public void setFriendName(String text) {
        mainPanel.setFriendName(text);
    }
}
