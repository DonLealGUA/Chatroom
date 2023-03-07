package Client;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * User interface för när användaren ska logga in eller registrera sig
 */
public class LoginUI {
    private final JFrame frame;
    private final int width;
    private final int height;
    private final JPanel mainPanel;
    private JButton connect;
    private JTextField jtfName;
    private final Client client;

    /**
     * skapar första sidan av GUI:t
     * @param client klienten som vill logga in
     */
    public LoginUI(Client client){
        //skapar frame
        this.frame = new JFrame("Login");
        this.width = 500;
        this.height = 400;
        this.client = client;

        //main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setSize(width, height);

        //login button
        JButton login = new JButton("Login");
        login.setEnabled(true);
        login.setSize(300, 50);
        login.setLayout(null);
        login.setLocation(width/2-150, height/2);
        login.addActionListener(l -> loginButton());
        mainPanel.add(login);

        //register button
        JButton newUser = new JButton("Register");
        newUser.setEnabled(true);
        newUser.setSize(300, 50);
        newUser.setLayout(null);
        newUser.setLocation(width/2-150, height/2+60);
        newUser.addActionListener(l -> registerButton());
        mainPanel.add(newUser);

        //skapar frame
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

    /**
     * om användaren klickade på logga in
     */
    private void loginButton() {
        JPanel loginPanel = new JPanel();
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
        //connect.addActionListener(l -> clientUI = new ClientUI(client, getUsername(), true));
        connect.addActionListener(l -> {
            try {
                client.connectClicked(jtfName.getText(), true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        connect.addActionListener(l -> frame.dispose());

        loginPanel.add(connect);

        frame.remove(mainPanel);

        frame.add(loginPanel);
        frame.setContentPane(loginPanel);
        frame.revalidate();
        frame.repaint();


    }

    /**
     * om klienten klickade på registrera
     */
    public void registerButton(){
        JPanel registerPanel = new JPanel();
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
        connect.addActionListener(l -> {
            try {
                client.connectClicked(jtfName.getText(), false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        connect.addActionListener(l -> frame.dispose());
        registerPanel.add(connect);

        frame.remove(mainPanel);

        frame.add(registerPanel);
        frame.setContentPane(registerPanel);
        frame.revalidate();
        frame.repaint();
    }

    /**
     * stäng ner frame om ingen användare med det namnet finns
     */
    public void noUserExist() {
        frame.dispose();
    }
}
