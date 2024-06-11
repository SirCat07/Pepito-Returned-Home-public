package game.playmenu;

import main.GamePanel;

import java.awt.image.BufferedImage;

public class PlayMenuElement {
    String id;
    BufferedImage[] images = new BufferedImage[2];
    String text;
    String subtext;

    public String getID() {
        return id;
    }
    public BufferedImage getIcon() {
        return images[0];
    }

    public BufferedImage getInactiveIcon() {
        return images[1];
    }

    public String getText() {
        return text;
    }

    public String getSubtext() {
        return subtext;
    }

    public PlayMenuElement(String id, BufferedImage image, String text, String subtext) {
        this.id = id;
        this.images[0] = image;
        this.text = text;
        this.subtext = subtext;

        images[1] = GamePanel.resize(GamePanel.darkify(images[0], 2), (int) (images[0].getWidth() * 0.75), (int) (images[0].getHeight() * 0.75), BufferedImage.SCALE_SMOOTH);
    }
}
