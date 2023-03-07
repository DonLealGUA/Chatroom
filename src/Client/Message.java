package Client;

import Server.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Message<T extends Serializable> implements Serializable {
    private T payload;
    private LocalDateTime serverTime;
    private LocalDateTime clientTime;

    public Message(List<User> clients) {
        super();
    }

    public Message(T data) {
        super();
        setPayload(data);
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T aPayload) {
        payload = aPayload;
    }

    public void setServerTime(LocalDateTime time){
        this.serverTime = time;
    }

    public void setClientTime(LocalDateTime time){
        this.clientTime = time;
    }

    public LocalDateTime getServerTime(){
        return serverTime;
    }

    public LocalDateTime getClientTime(){
        return clientTime;
    }

}
