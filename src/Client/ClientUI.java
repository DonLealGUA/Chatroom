package Client;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.Socket;

/**
 * User interface för Klienten
 */
public class ClientUI {
    private final JTextPane jtextFilDiscu = new JTextPane(); //rutan där alla chatt-meddelanden finns
    private final JTextPane jtextListUsers = new JTextPane(); //rutan där användare online finns
    private final JTextField jtextInputChat = new JTextField(); //där användare skriver in meddelande
    private final JLabel jLabelUsername;
    private final JPanel image;

    /**
     * Startar upp GUI:t
     * @param client klienten som startar GUI:t
     * @param newUsername klientens användarnamn
     */
    public ClientUI(Client client, String newUsername){
        String fontfamily = "Arial, sans-serif";
        Font font = new Font(fontfamily, Font.PLAIN, 15);

        //skapar JFrame
        final JFrame jfr = new JFrame("Chat");
        jfr.getContentPane().setLayout(null);
        jfr.setSize(700, 500);
        jfr.setResizable(false);
        jfr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //skapar chatt-rutan
        jtextFilDiscu.setBounds(25, 25, 490, 320);
        jtextFilDiscu.setFont(font);
        jtextFilDiscu.setMargin(new Insets(6, 6, 6, 6));
        jtextFilDiscu.setEditable(false);
        JScrollPane jtextFilDiscuSP = new JScrollPane(jtextFilDiscu);
        jtextFilDiscuSP.setBounds(25, 25, 490, 320);
        jtextFilDiscu.setContentType("text/html");
        jtextFilDiscu.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

        //rutan där användarnamn och bild finns
        JPanel jtextUserInfo = new JPanel();
        jtextUserInfo.setBounds(520, 25, 156, 48);
        jtextUserInfo.setFont(font);
        jtextUserInfo.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        jtextUserInfo.setFont(jtextUserInfo.getFont().deriveFont(15.0F));
        jtextUserInfo.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        //användarbild
        image = new JPanel();
        image.setBackground(Color.WHITE);
        image.setLocation(5,5);
        image.setSize(30,30);
        image.setVisible(true);
        jtextUserInfo.add(image);

        //användarnamnet
        jLabelUsername = new JLabel();
        jLabelUsername.setBounds(50, 5, 156, 20);
        jLabelUsername.setFont(jLabelUsername.getFont().deriveFont(15.0F));
        jLabelUsername.setHorizontalAlignment(JLabel.LEFT);
        jtextUserInfo.add(jLabelUsername);

        //användarlista
        jtextListUsers.setBounds(520, 75, 156, 270);
        jtextListUsers.setEditable(true);
        jtextListUsers.setFont(font);
        jtextListUsers.setMargin(new Insets(6, 6, 6, 6));
        jtextListUsers.setEditable(false);
        JScrollPane jsplistuser = new JScrollPane(jtextListUsers);
        jsplistuser.setBounds(520, 75, 156, 270);
        jtextListUsers.setContentType("text/html");
        jtextListUsers.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

        //chattmeddelande-rutan (det användaren skriver in)
        jtextInputChat.setBounds(0, 350, 400, 50);
        jtextInputChat.setFont(font);
        jtextInputChat.setMargin(new Insets(6, 6, 6, 6));
        final JScrollPane jtextInputChatSP = new JScrollPane(jtextInputChat);
        jtextInputChatSP.setBounds(25, 350, 650, 50);

        //send knapp
        final JButton jsbtn = new JButton("Send");
        jsbtn.setFont(font);
        jsbtn.setBounds(575, 410, 100, 35);

        //Disconnect knapp
        final JButton jsbtndeco = new JButton("Disconnect");
        jsbtndeco.setFont(font);
        jsbtndeco.setBounds(25, 410, 130, 35);


        //send picture knapp
        final JButton sendPicture = new JButton("Send Picture");
        sendPicture.setFont(font);
        sendPicture.setBounds(440, 410, 120, 35);

        //om man klickar enter ska meddelandet skickas
        jtextInputChat.addKeyListener(new KeyAdapter() {
            // send message on Enter
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    client.sendMessage(jtextInputChat.getText().trim());
                    updateChatPanel();
                }
            }
        });

        //när man trycker på skicka
        jsbtn.addActionListener(ae -> {
            client.sendMessage(jtextInputChat.getText().trim());
            updateChatPanel();
        });

        //när man skickar bild
        sendPicture .addActionListener(ae -> {
            String imagePath = Client.getPicture();
            ImageIcon imageIcon = new ImageIcon(imagePath);
            Image image = imageIcon.getImage(); // transform it
            Image newimg = image.getScaledInstance(200, 200,  Image.SCALE_SMOOTH); // scale it
            imageIcon = new ImageIcon(newimg);
            client.sendPicture(imageIcon);
            updateChatPanel();
        });


        //lägger till saker på frame
        jfr.add(jtextFilDiscuSP);
        jfr.add(jsplistuser);
        jfr.add(jtextUserInfo);
        jfr.add(sendPicture);
        jfr.setVisible(true);

        //när man klickar disconnect
        jsbtndeco.addActionListener(ae -> {
            client.disconnectPressed();
            jsbtndeco.setEnabled(false);
            jsbtn.setEnabled(false);
            jtextInputChat.setEnabled(false);
        });

        //lägger till alla knappar och sätter färg
        try {
            updateUsername(newUsername);
            jfr.add(jsbtn);
            jfr.add(jtextInputChatSP);
            jfr.add(jsbtndeco);
            jfr.revalidate();
            jfr.repaint();
            jtextFilDiscu.setBackground(Color.WHITE);
            jtextListUsers.setBackground(Color.WHITE);
            jtextUserInfo.setBackground(Color.WHITE);
        } catch (Exception ex) {
            appendToPane(jtextFilDiscu, "Could not connect to Server",Color.black);
            JOptionPane.showMessageDialog(jfr, ex.getMessage());
        }
    }

    /**
     * uppdaterar det man skrivit i chatten så det försvinner
     */
    public void updateChatPanel() {
        jtextInputChat.requestFocus();
        jtextInputChat.setText(null);
    }

    /**
     * visar exception meddelande
     * @param ex meddelandet som ska visas
     */
    public void showExceptionMessage(Exception ex) {
        JOptionPane.showMessageDialog(null, ex.getMessage());
    }

    /**
     * uppdaterar användare-online-listan så den blir tom
     */
    public void updateUsers() {
        jtextListUsers.setText(null);
    }

    /**
     * uppdaterar användare-online-listan så användaren skrivs ut med svart
     * @param user användaren som ska skrivas ut
     */

    public void updateUsersPane(String user) {
        appendToPane(jtextListUsers, "@" + user,Color.black);
    }

    /**
     * skickar meddelande till alla
     * @param message meddelandet som ska skickas
     */
    public void updateUsersMessage(String message) {
        appendToPane(jtextFilDiscu, message,Color.black);
    }

    /**
     * uppdaterar användare-online-listan så användaren skrivs ut med blått
     * @param user användaren som ska skrivas ut
     */
    public void updateUsersFriendsMessage(String user) {
        appendToPane(jtextListUsers, "@" + user,Color.ORANGE);
        jtextListUsers.setForeground(Color.BLUE);
    }

    /**
     * skriver ut ett meddelande till klienten
     * @param serverName ip
     * @param PORT port
     */
    public void updatePane(String serverName, int PORT) {
        appendToPane(jtextFilDiscu, "Connecting to " + serverName + " on port " + PORT + "...",Color.black);

    }

    /**
     * skriver ut ett meddelande till klienten
     * @param server socket
     */
    public void writeConnectMessage(Socket server) {
        appendToPane(jtextFilDiscu, "Connected to " + server.getRemoteSocketAddress(), Color.black);

    }

    /**
     * skickar ut meddelande till chatten
     * @param tp vilken panel det ska till
     * @param msg meddelandet
     * @param color vilken färg det ska va
     */
    public void appendToPane(JTextPane tp, String msg, Color color) {
        HTMLDocument doc = (HTMLDocument) tp.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit) tp.getEditorKit();
        try {
            String colorHex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            String message = "<font color='" + colorHex + "'>" + msg + "</font>";
            editorKit.insertHTML(doc, doc.getLength(), message, 0, 0, null);
            tp.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * uppdaterar användarnamnet
     * @param text användarnamnet
     */
    public void updateUsername(String text) {
        jLabelUsername.setText(text);
    }

    /**
     * uppdaterar bild som skickats
     * @param imageIcon bilden
     */
    public void updateImage(ImageIcon imageIcon) {
        jtextFilDiscu.insertIcon(imageIcon);
    }

    /**
     * uppdaterar profilbild
     * @param imageIcon profilbilden
     */
    public void updateImageIcon(ImageIcon imageIcon){
        Image image2 = imageIcon.getImage(); // transform it
        Image newimg = image2.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        ImageIcon imageIcon2 = new ImageIcon(newimg);

        JLabel imgLabel = new JLabel(imageIcon2);

        image.add(imgLabel);
    }

}