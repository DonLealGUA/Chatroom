package Server;

import javax.swing.*;

public class ServerGUI extends JFrame {
    private JFrame frame;
    private JTextArea textArea;

    public ServerGUI() {
        // Initialize the frame
        frame = new JFrame("Server");
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
        // Append the new text to a new line in the text area
        textArea.append(text + "\n");
    }
}
