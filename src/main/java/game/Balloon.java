package game;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Balloon {
    private short x;
    public short counter = 0;
    public short y = 0;
    BufferedImage image = GamePanel.getRandomBalloon();

    public BufferedImage getImage() {
        return image;
    }

    public BalloonDirection direction = BalloonDirection.random();

    public Balloon() {
        x = (short) Math.round(Math.random() * 1390);
    }
    public Balloon(float alpha) {
        this.alpha = alpha;
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
    
    public float alpha = 1;

    public void goLeft() {
        direction = (byte) (Math.round(Math.random())) == 0 ? BalloonDirection.LEFT : BalloonDirection.LEFT_LOW;
    }

    public void goRight() {
        direction = (byte) (Math.round(Math.random())) == 0 ? BalloonDirection.RIGHT : BalloonDirection.RIGHT_LOW;
    }

    public Rectangle getRectangle(short offsetX, int maxOffset) {
        return new Rectangle(offsetX + x - maxOffset, 200 + getAdder(), 90, 125);
    }

    public short getAdder() {
        return (short) (Math.sin(Math.toRadians(counter)) * 40 - y);
    }
}