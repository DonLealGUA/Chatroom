package Client;

import java.io.Serializable;

/**
 * Meddelande klass
 * @param <T> vilken typ av meddelande det är
 */
public class Message<T extends Serializable> implements Serializable {
    private T payload; //vad meddelandet innehåller

    /**
     * skapar ett meddelande objekt
     * @param data vad meddelandet ska innehålla
     */
    public Message(T data) {
        super();
        setPayload(data);
    }

    //getter och setter
    public T getPayload() {
        return payload;
    }

    public void setPayload(T aPayload) {
        payload = aPayload;
    }

}
