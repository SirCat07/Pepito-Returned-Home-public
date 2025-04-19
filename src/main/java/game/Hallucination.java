package game;

import utils.PepitoImage;

import java.awt.image.BufferedImage;

public class Hallucination {
    PepitoImage image = new PepitoImage("/game/hallucination/1.png");

    int x;
    int y;
    float z;

    public Hallucination() {
        int index = (int) (Math.random() * 7 + 1);

        image = new PepitoImage("/game/hallucination/" + index + ".png");
        BufferedImage requested = image.request();

        x = (int) (Math.random() * (1480 - requested.getWidth()));
        y = (int) (Math.random() * (640 - requested.getHeight()));
        z = (float) (1 - (Math.max(0.1F, Math.sqrt(Math.random())) / 6F));
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

    public float getZ() {
        return z;
    }
}
