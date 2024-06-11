package game;

import utils.PepitoImage;

import java.awt.image.BufferedImage;

public class Hallucination {
    PepitoImage image = new PepitoImage("/game/hallucination/1.png");

    int x;
    int y;

    public Hallucination() {
        int index = (int) (Math.random() * 7 + 1);

        image = new PepitoImage("/game/hallucination/" + index + ".png");
        BufferedImage requested = image.request();

        x = (int) (Math.random() * (1480 - requested.getWidth()));
        y = (int) (Math.random() * (640 - requested.getHeight()));
    }

    public PepitoImage getImage() {
        return image;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
