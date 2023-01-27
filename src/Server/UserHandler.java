package Server;

import java.util.Scanner;

public class UserHandler implements Runnable {
    private Server server;
    private User user;

    public UserHandler(Server server, User newUser) {
        this.server = server;
        this.user = newUser;
        this.server.broadcastAllUsers();
    }

    @Override
    public void run() {
        String message;

        Scanner sc = new Scanner(this.user.getInputStream());

        while (sc.hasNextLine()) {
            message = sc.nextLine();

            // hantera meddelanden till en specifik person
            if (message.charAt(0) == '@'){
                if(message.contains(" ")){
                    System.out.println("private msg : " + message);
                    int firstSpace = message.indexOf(" ");
                    String userPrivate= message.substring(1, firstSpace);
                    server.sendMessageToUser(message.substring(firstSpace+1), user, userPrivate);
                }

            }else{
                // update user list
                server.broadcastMessages(message, user);
            }
        }
        server.removeUser(user);
        this.server.broadcastAllUsers();
        sc.close();

    }
}
