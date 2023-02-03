package Client;

import java.awt.*;
import java.io.Serializable;

public class msg implements Serializable {
    private final String text;
    private final Image image;

    public msg(String text, Image image) {
        this.text = text;
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public Image getImage() {
        return image;
    }
}
