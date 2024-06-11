package game;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Balloon {
    private short x;
    public short counter = 0;
    BufferedImage image = GamePanel.getRandomBalloon();

    public BufferedImage getImage() {
        return image;
    }

    public BalloonDirection direction = BalloonDirection.random();

    public Balloon() {
        x = (short) Math.round(Math.random() * 1390);
    }

    public void changeDirection() {
        direction = BalloonDirection.random();
    }

    public short getX() {
        return x;
    }

    public void addX(int d) {
        x += (short) d;
    }

    public void goLeft() {
        direction = (byte) (Math.round(Math.random())) == 0 ? BalloonDirection.LEFT : BalloonDirection.LEFT_LOW;
    }

    public void goRight() {
        direction = (byte) (Math.round(Math.random())) == 0 ? BalloonDirection.RIGHT : BalloonDirection.RIGHT_LOW;
    }

    public Rectangle getRectangle(short offsetX, float widthModifier, float heightModifier, short centerX, short centerY) {
        return new Rectangle((int) ((offsetX + x - 400) * widthModifier + centerX), (int) ((200 + getAdder()) * heightModifier + centerY), (int) (90 * widthModifier), (int) (125 * heightModifier));
    }

    public byte getAdder() {
        return (byte) (Math.sin(Math.toRadians(counter)) * 40);
    }
}