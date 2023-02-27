package Client;

import Server.User;

import java.io.Serializable;
import java.util.List;

public class Message<T extends Serializable> implements Serializable {
    private T payload;

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

}
