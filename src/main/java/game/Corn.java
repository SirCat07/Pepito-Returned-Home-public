package game;

import game.items.Item;

import java.awt.image.BufferedImage;

public class Corn extends Item {
    short x = (short) (Math.random() * 1080);
    byte stage = 0;
    BufferedImage img;
    BufferedImage cornStage1;

    public Corn(BufferedImage icon, String name, String description, int amount, String id, BufferedImage img) {
        super(icon, name, description, amount, id, "");
        this.img = img;
        cornStage1 = img;
    }

    public short getX() {
        return x;
    }
    public byte getStage() {
        return stage;
    }

    public BufferedImage getImage() {
        return img;
    }

    public void setImage(BufferedImage img) {
        this.img = img;
    }

    public void increment() {
        stage++;
    }

    public void reset() {
        x = (short) (Math.random() * 1080);
        stage = 0;
        img = cornStage1;
    }
}
