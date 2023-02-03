package Client;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.Socket;

public class ClientUI {

    final JTextPane jtextFilDiscu = new JTextPane();
    final JTextPane jtextListUsers = new JTextPane();
    final JTextField jtextInputChat = new JTextField();
    final JPanel jtextUserInfo = new JPanel();
    JLabel jLabelUsername;
    JPanel image;
    String username;

    private String oldMsg = "";
    private ImageIcon oldImage;

    public ClientUI(Client client, String newUsernameTest, boolean existingUser){
        this.username = newUsernameTest;
        String fontfamily = "Arial, sans-serif";
        Font font = new Font(fontfamily, Font.PLAIN, 15);

        final JFrame jfr = new JFrame("Chat");
        jfr.getContentPane().setLayout(null);
        jfr.setSize(700, 500);
        jfr.setResizable(false);
        jfr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // chatt-rutan
        jtextFilDiscu.setBounds(25, 25, 490, 320);
        jtextFilDiscu.setFont(font);
        jtextFilDiscu.setMargin(new Insets(6, 6, 6, 6));
        jtextFilDiscu.setEditable(false);
        JScrollPane jtextFilDiscuSP = new JScrollPane(jtextFilDiscu);
        jtextFilDiscuSP.setBounds(25, 25, 490, 320);

        jtextFilDiscu.setContentType("text/html");
        jtextFilDiscu.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

        //sin egen profil
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


        JButton everyoneButton = new JButton("Everyone");
        everyoneButton.setEnabled(true);
        everyoneButton.setLayout(null);
        everyoneButton.setLocation(520,75);
        everyoneButton.setSize(156, 50);
        jfr.add(everyoneButton);


        // användarlista
        jtextListUsers.setBounds(520, 125, 156, 220);
        jtextListUsers.setEditable(true);
        jtextListUsers.setFont(font);
        jtextListUsers.setMargin(new Insets(6, 6, 6, 6));
        jtextListUsers.setEditable(false);
        JScrollPane jsplistuser = new JScrollPane(jtextListUsers);
        jsplistuser.setBounds(520, 125, 156, 220);

        jtextListUsers.setContentType("text/html");
        jtextListUsers.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

        // text meddelande rutan (det användaren skriver in)
        jtextInputChat.setBounds(0, 350, 400, 50);
        jtextInputChat.setFont(font);
        jtextInputChat.setMargin(new Insets(6, 6, 6, 6));
        final JScrollPane jtextInputChatSP = new JScrollPane(jtextInputChat);
        jtextInputChatSP.setBounds(25, 350, 650, 50);

        // sent knapp
        final JButton jsbtn = new JButton("Send");
        jsbtn.setFont(font);
        jsbtn.setBounds(575, 410, 100, 35);

        // Disconnect knapp
        final JButton jsbtndeco = new JButton("Disconnect");
        jsbtndeco.setFont(font);
        jsbtndeco.setBounds(25, 410, 130, 35);


        // button send picture
        final JButton sendPicture = new JButton("Send Picture");
        sendPicture.setFont(font);
        sendPicture.setBounds(440, 410, 120, 35);

        jtextInputChat.addKeyListener(new KeyAdapter() {
            // send message on Enter
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    client.sendMessage(jtextInputChat.getText().trim());
                }

                // Get last message typed
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    String currentMessage = jtextInputChat.getText().trim();
                    jtextInputChat.setText(oldMsg);
                    oldMsg = currentMessage;
                }

                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    String currentMessage = jtextInputChat.getText().trim();
                    jtextInputChat.setText(oldMsg);
                    oldMsg = currentMessage;
                }
            }
        });

        // Click on send button
        jsbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                client.sendMessage(jtextInputChat.getText().trim());
            }
        });

        //Send picture
        sendPicture .addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String imagepath = getPicture();
                ImageIcon imageIcon = new ImageIcon(imagepath);
                Image image = imageIcon.getImage(); // transform it
                Image newimg = image.getScaledInstance(200, 200,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
                imageIcon = new ImageIcon(newimg);
                jtextFilDiscu.insertIcon(imageIcon);
                //TODO fixa att man skickar bild till andra klienter
                client.sendPicture(imageIcon);

            }
        });

        // Connection view
        final JTextField jtfName = new JTextField(client.getNameUser());
        final JTextField jtfport = new JTextField(Integer.toString(client.getPORT()));
        final JTextField jtfAddr = new JTextField(client.getServerName());
        final JButton jcbtn = new JButton("Connect");

        jtextFilDiscu.setBackground(Color.LIGHT_GRAY);
        jtextListUsers.setBackground(Color.LIGHT_GRAY);
        jtextUserInfo.setBackground(Color.LIGHT_GRAY);

        jfr.add(jtextFilDiscuSP);
        jfr.add(jsplistuser);
        jfr.add(jtextUserInfo);
        jfr.setVisible(true);


        // On connect
        jcbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    client.connectClicked(jtfName.getText(), Integer.parseInt(jtfport.getText()), jtfAddr.getText());

                    jfr.remove(jtfName);
                    jfr.remove(jtfport);
                    jfr.remove(jtfAddr);
                    jfr.remove(jcbtn);
                    jfr.add(jsbtn);
                    jfr.add(jtextInputChatSP);
                    jfr.add(jsbtndeco);
                    jfr.add(sendPicture);
                    jfr.revalidate();
                    jfr.repaint();
                    jtextFilDiscu.setBackground(Color.WHITE);
                    jtextListUsers.setBackground(Color.WHITE);
                } catch (Exception ex) {
                    appendToPane(jtextFilDiscu, "<span>Could not connect to Server</span>");
                    JOptionPane.showMessageDialog(jfr, ex.getMessage());
                }
            }

        });

        // on deco
        jsbtndeco.addActionListener(new ActionListener()  {
            public void actionPerformed(ActionEvent ae) {
                jfr.add(jtfName);
                jfr.add(jtfport);
                jfr.add(jtfAddr);
                jfr.add(jcbtn);
                jfr.remove(jsbtn);
                jfr.remove(jtextInputChatSP);
                jfr.remove(jsbtndeco);
                jfr.remove(sendPicture);
                jfr.revalidate();
                jfr.repaint();

        // on disconnect
        jsbtndeco.addActionListener(new ActionListener()  {
            public void actionPerformed(ActionEvent ae) {
                client.disconnectPressed();
                jsbtndeco.setEnabled(false);
                jsbtn.setEnabled(false);
                jtextInputChat.setEnabled(false);
            }
        });

        try {
            client.connectClicked(username, this, existingUser);
            updateUsername(username);
            jfr.add(jsbtn);
            jfr.add(jtextInputChatSP);
            jfr.add(jsbtndeco);
            jfr.revalidate();
            jfr.repaint();
            jtextFilDiscu.setBackground(Color.WHITE);
            jtextListUsers.setBackground(Color.WHITE);
            jtextUserInfo.setBackground(Color.WHITE);
        } catch (Exception ex) {
            appendToPane(jtextFilDiscu, "<span>Could not connect to Server</span>");
            JOptionPane.showMessageDialog(jfr, ex.getMessage());
        }

    }

    public void disconnectUpdate(){
        jtextListUsers.setText(null);
        jtextFilDiscu.setBackground(Color.LIGHT_GRAY);
        jtextListUsers.setBackground(Color.LIGHT_GRAY);
        appendToPane(jtextFilDiscu, "<span>Connection closed.</span>");
    }

    public void updateChatPanel() {
        jtextInputChat.requestFocus();
        jtextInputChat.setText(null);
    }

    public static String getPicture() {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        File selectedFile = null;
        int returnValue = jfc.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = jfc.getSelectedFile();
            System.out.println(selectedFile.getAbsolutePath());
        }
        return selectedFile.getAbsolutePath();
    }

    public void showExceptionMessage(Exception ex) {
        JOptionPane.showMessageDialog(null, ex.getMessage());
    }

    public void updateUsers() {
        jtextListUsers.setText(null);
    }

    public void updateUsersPane(String user){
        appendToPane(jtextListUsers, "@" + user);
    }

    public void updateUsersMessage(String message) {
        appendToPane(jtextFilDiscu, message);
    }

    public void printError() {
        System.err.println("Failed to parse incoming message");
    }

    public void setOldMsg(String message) {
        this.oldMsg = message;
    }

    public void setOldImage(ImageIcon image) {
        this.oldImage = image;
    }


    public class TextListener implements DocumentListener {
        JTextField jtf1;
        JTextField jtf2;
        JTextField jtf3;
        JButton jcbtn;

        public TextListener(JTextField jtf1, JTextField jtf2, JTextField jtf3, JButton jcbtn){
            this.jtf1 = jtf1;
            this.jtf2 = jtf2;
            this.jtf3 = jtf3;
            this.jcbtn = jcbtn;
        }

        public void changedUpdate(DocumentEvent e) {}

        public void removeUpdate(DocumentEvent e) {
            if(jtf1.getText().trim().equals("") ||
                    jtf2.getText().trim().equals("") ||
                    jtf3.getText().trim().equals("")
            ){
                jcbtn.setEnabled(false);
            }else{
                jcbtn.setEnabled(true);
            }
        }
        public void insertUpdate(DocumentEvent e) {
            if(jtf1.getText().trim().equals("") ||
                    jtf2.getText().trim().equals("") ||
                    jtf3.getText().trim().equals("")
            ){
                jcbtn.setEnabled(false);
            }else{
                jcbtn.setEnabled(true);
            }
        }

    }

    public void updatePane(String serverName, int PORT){
        appendToPane(jtextFilDiscu, "<span>Connecting to " + serverName + " on port " + PORT + "...</span>");
    }

    public void writeConnectMessage(Socket server){
        appendToPane(jtextFilDiscu, "<span>Connected to " + server.getRemoteSocketAddress()+"</span>");

    }

    public void appendToPane(JTextPane tp, String msg){
        HTMLDocument doc = (HTMLDocument)tp.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit)tp.getEditorKit();
        try {
            editorKit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
            tp.setCaretPosition(doc.getLength());
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updateUsername(String text){
        jLabelUsername.setText(text);
    }

    public void updateImage(ImageIcon imageIcon){
        Image image2 =imageIcon.getImage(); // transform it
        Image newimg = image2.getScaledInstance(30, 30,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        ImageIcon imageIcon2 = new ImageIcon(newimg);
        image.add(new JLabel(imageIcon2));
    }

}
