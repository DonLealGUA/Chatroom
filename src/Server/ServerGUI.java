package Server;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class ServerGUI extends JFrame {
    private JFrame frame;
    private JTextArea textArea;

    public ServerGUI() {
        // Initialize the frame
        frame = new JFrame("My Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // Initialize the text area
        textArea = new JTextArea();
        textArea.setEditable(false);
        frame.getContentPane().add(new JScrollPane(textArea));

        // Show the frame
        frame.setVisible(true);
    }

    public void updateText(String text) {
        // Update the text area with the new text
        textArea.setText(text);
    }
}