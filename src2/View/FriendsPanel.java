package View;

import Controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendsPanel extends JPanel {
    private int width;
    private int height;
    private JButton friends;
    private JButton onlinePeople;
    private Controller controller;
    private static Map<String, ImageIcon> imageMap;
    private JPanel panel = new JPanel();
    private JList list = new JList();
    String[] nameList = new String[0];
    ImageIcon[] imageIcons = new ImageIcon[0];


    public FriendsPanel(int width, int height, Controller controller){
        this.setLayout(null);
        this.controller = controller;
        this.width = width;
        this.height = height;
        this.setBorder(BorderFactory.createLineBorder(Color.white, 5));
        this.setSize(width, height);
        setLocation(0, 75);
        setUp();
        this.add(panel,BorderLayout.CENTER);
        panel.setSize(width - 0, height - 90);
        panel.setLocation(5,20);


    }
    private String name;
    private ImageIcon icon;

    public void addUsers(String[] userNames, ImageIcon[] iconImages) {
        this.nameList=userNames;
        this.imageIcons=iconImages;
        list = new JList<>(nameList);
        panel.remove(list);

        for (int i = 0; i < imageIcons.length; i++) {
            Image image = imageIcons[i].getImage(); // transform it
            Image newimg = image.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
            ImageIcon imageIcon2 = new ImageIcon(newimg);
            imageIcons[i] = imageIcon2;
        }

        Map<Object, ImageIcon> icons = new HashMap<Object, ImageIcon>();
        ArrayList<String> arrayList = new ArrayList<String>();
        for (int i = 0; i < imageIcons.length; i++) {
            for (int y = 0; y < nameList.length; y++) {
                icons.put(nameList[y], imageIcons[i]);
                arrayList.add(nameList[y]);
            }
        }
        list.setListData(arrayList.toArray());
        list.setCellRenderer(new IconListRenderer(icons));
        list.addListSelectionListener(l -> controller.askIfFriend(nameList[list.getSelectedIndex()]));


        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(200, 800));
        panel.add(scroll);
        panel.setVisible(true);
        repaint();
        revalidate();

    }

    private void setUp() {
        friends = new JButton("Vänner");
        friends.setEnabled(true);
        friends.setSize(width / 2, 25);
        friends.setLocation(0, 2);
        friends.addActionListener(l -> controller.updateBoolean(true));
        this.add(friends);

        onlinePeople = new JButton("Alla");
        onlinePeople.setEnabled(true);
        onlinePeople.setSize(width / 2, 25);
        onlinePeople.setLocation(width/2, 2);
        onlinePeople.addActionListener(l -> controller.updateBoolean(false));
        this.add(onlinePeople);


        Map<Object, ImageIcon> icons = new HashMap<Object, ImageIcon>();
        ArrayList<String> arrayList = new ArrayList<String>();
        for(int i = 0; i < imageIcons.length; i++){
            for(int y = 0; y < nameList.length; y++){
                icons.put(nameList[i],imageIcons[i]);
                arrayList.add(nameList[i]);
            }
        }
        list.setListData(arrayList.toArray());
        list.setCellRenderer(new IconListRenderer(icons));
        list.repaint();

        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(200, 800));
        panel.add(scroll);
        panel.setVisible(true);

    }



}
