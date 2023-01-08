package Model;

public class MessageManager {
    public Buffer<Message> unsentMessages;


    public MessageManager(){
        unsentMessages = new Buffer<>();
    }

    public void addUnsentMessage(Message message){
        unsentMessages.put(message);
    }

    public Buffer<Message> getUnsentMessages() {
        return unsentMessages;
    }

    public void put(Message message){
        unsentMessages.put(message);
    }
}
