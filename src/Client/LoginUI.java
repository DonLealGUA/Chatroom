package Client;

import javax.swing.*;
import java.awt.*;

public class LoginUI {
    ClientUI clientUI;
    JFrame frame;
    JButton login;
    JButton newUser;
    int width;
    int height;
    JPanel mainPanel;
    JPanel loginPanel;
    JButton connect;
    JTextField jtfName;
    Client client;
    JPanel registerPanel;


    public LoginUI(Client client){
        this.frame = new JFrame("Login");
        this.width = 500;
        this.height = 400;
        this.client = client;

        //main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setSize(width, height);

        //login button
        login = new JButton("Login");
        login.setEnabled(true);
        login.setSize(300, 50);
        login.setLayout(null);
        login.setLocation(width/2-150, height/2);
        login.addActionListener(l -> loginButton(client));
        mainPanel.add(login);

        newUser = new JButton("Register");
        newUser.setEnabled(true);
        newUser.setSize(300, 50);
        newUser.setLayout(null);
        newUser.setLocation(width/2-150, height/2+60);
        newUser.addActionListener(l -> registerButton());
        mainPanel.add(newUser);

        frame.setPreferredSize(new Dimension(width, height));
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLayout(null);
        frame.setVisible(true);
        frame.add(mainPanel);
        frame.setContentPane(mainPanel);
        frame.revalidate();
        frame.repaint();

    }

    private void loginButton(Client client) {
        loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setSize(width, height);

        jtfName = new JTextField();
        jtfName.setBounds(width/2-200, height/2-50, 400, 40);
        jtfName.setLayout(null);
        jtfName.setFont(jtfName.getFont().deriveFont(15.0F));
        jtfName.setMargin(new Insets(6, 6, 6, 6));
        loginPanel.add(jtfName);

        connect = new JButton("Connect");
        connect.setEnabled(true);
        connect.setLayout(null);
        connect.setSize(400, 40);
        connect.setLocation(width/2-200, height/2);
        connect.addActionListener(l -> clientUI = new ClientUI(client, getUsername(), true));
        loginPanel.add(connect);

        frame.remove(mainPanel);

        frame.add(loginPanel);
        frame.setContentPane(loginPanel);
        frame.revalidate();
        frame.repaint();


    }

    public void registerButton(){
        registerPanel = new JPanel();
        registerPanel.setLayout(null);
        registerPanel.setSize(width, height);

        jtfName = new JTextField();
        jtfName.setBounds(width/2-200, height/2-50, 400, 40);
        jtfName.setLayout(null);
        jtfName.setFont(jtfName.getFont().deriveFont(15.0F));
        jtfName.setMargin(new Insets(6, 6, 6, 6));
        registerPanel.add(jtfName);

        connect = new JButton("Connect");
        connect.setEnabled(true);
        connect.setLayout(null);
        connect.setSize(400, 40);
        connect.setLocation(width/2-200, height/2);
        connect.addActionListener(l -> clientUI = new ClientUI(client, getUsername(), false));
        registerPanel.add(connect);

        frame.remove(mainPanel);

        frame.add(registerPanel);
        frame.setContentPane(registerPanel);
        frame.revalidate();
        frame.repaint();
    }

    private String getUsername() {
        return jtfName.getText();
    }


}
