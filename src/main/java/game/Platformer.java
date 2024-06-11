package game;

import main.GamePanel;

import java.awt.*;

public class Platformer {
    public float x;
    float y;
    float yV = 0;
    public boolean pressedLeft = false;
    public boolean pressedRight = false;
    Point spawn = new Point(0, 0);
    GamePanel g;

    int width = 32;
    int height = 24;
    //6x2
    public int[][] map = new int[width][height];

    public Platformer(GamePanel g) {
        this.g = g;
        x = spawn.x;
        y = spawn.y;

        g.everyFixedUpdate.put("platformerMovement", this::allPlatformerMovement);

        for(int x = 0; x < getWidth(); x++) {
            for(int y = 0; y < getHeight(); y++) {
                map[x][y] = (int) (Math.round(Math.random()));
            }
        }
    }

    public void die() {
//        g.sound.play("platformerDeath", 0.1F);
        x = spawn.x;
        y = spawn.y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void allPlatformerMovement() {
        velocity();
        move();
        gravity();
    }

    public void velocity() {
        if(yV < 0.5F) {
            y -= yV;
            yV = 0;
        }

        y -= yV * 0.8F;
        yV *= 0.8F;
    }

    public void jump() {
        yV += 3;
    }

    public void move() {
        float i = 0;

        if(pressedRight) {
            i += 0.2F;
        }
        if(pressedLeft) {
            i -= 0.2F;
        }

        try {
            Rectangle rect = new Rectangle((int) x, (int) y, 1, 1);

        } catch (ArrayIndexOutOfBoundsException e) {
            x += i;
        }
    }

    float yAcceleration = 0;
    public void gravity() {
        try {
            if (map[(int) x][(int) (y + yAcceleration)] != 1) {
                fall();

                if(yAcceleration < 0.2F) {
                    y += yAcceleration;
                    yAcceleration = 0;
                }
            } else {
                yAcceleration = 0;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            fall();
        }
    }

    void fall() {
        y += yAcceleration;

        if(yAcceleration == 0) {
            yAcceleration = 0.2F;
        }
        if(yAcceleration < terminalVelocity) {
            yAcceleration *= 1.2F;
        }
    }

    public float terminalVelocity = 1F;
}
