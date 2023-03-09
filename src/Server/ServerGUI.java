package Server;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI för server-sidan
 */
public class ServerGUI extends JFrame {
    private final JTextArea textArea;

    /**
     * skapar server-GUI:t
     */
    public ServerGUI() {
        // Initialize the frame
        JFrame frame = new JFrame("Server");
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);

        // Initialize the text area
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(0,50,450,220);
        frame.getContentPane().add(scrollPane);

        //skapa input-panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(null);
        inputPanel.setBounds(0,0,450,50);
        JLabel startLabel = new JLabel("Start time: ");
        startLabel.setBounds(0,3,70,20);

        //skriver in start-tid
        JTextArea startText = new JTextArea();
        startText.setBounds(70,3,100,20);

        JLabel endLabel = new JLabel("End time: ");
        endLabel.setBounds(180,3, 60, 20);

        //skriver in slut-tid
        JTextArea endText = new JTextArea();
        endText.setBounds(250,3,100,20);

        //show-messages knapp
        JButton showButton = new JButton("Show Messages");
        showButton.setBounds(200,30, 130, 20);
        showButton.addActionListener(e -> showMessages(startText.getText(), endText.getText()));

        JLabel format = new JLabel("Format: dd/MM/yyyy HH:mm");
        format.setBounds(0, 30,200,15);

        inputPanel.add(startLabel);
        inputPanel.add(startText);
        inputPanel.add(endLabel);
        inputPanel.add(endText);
        inputPanel.add(showButton);
        inputPanel.add(format);

        frame.add(inputPanel);

        // Show the frame
        frame.setVisible(true);
    }

    /**
     * metod för att visa meddelanden mellan 2 tidpunkter
     * @param startDate start-tidpunkt
     * @param endDate slut-tidpunkt
     */
    public void showMessages(String startDate, String endDate) {
        JFrame messageFrame = new JFrame();
        messageFrame.setLayout(null);
        messageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        messageFrame.setSize(500, 300);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);

        JTextArea messageTextArea = new JTextArea();
        messageTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageTextArea);
        scrollPane.setBounds(0,50,450,220);
        messageFrame.getContentPane().add(scrollPane);

        messageFrame.setVisible(true);

        //skapar en lista med meddelanden mellan två valda tidpunkter
        List<String> messagesBetween = getMessagesBetween(startDateTime, endDateTime);

        //skriver ut texten som är i listan till GUI:t
        for (String s : messagesBetween) {
            messageTextArea.append(s + "\n");
        }
    }

    /**
     * skriver ut text till GUI:t
     * @param text texten som ska skrivas ut
     */
    public void updateText(String text) {
        //Append the new text to a new line in the text area
        textArea.append(text + "\n");
    }

    /**
     * hämtar meddelanden som skickats mellan 2 valda tidpunkter
     * @param start start-tidpunkt
     * @param end slut-tidpunkt
     * @return lista med meddelanden
     */
    public List<String> getMessagesBetween(LocalDateTime start, LocalDateTime end) {
        ArrayList<String> messages = Reader.readServerLogg();

        List<String> result = new ArrayList<>();

        for (String message : messages) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(message.substring(1,17), formatter);

            if ((dateTime.isAfter(start) || dateTime.isEqual(start)) && (dateTime.isBefore(end) || dateTime.isEqual(end))) {
                result.add(message);
            }
        }
        return result;
    }
}
