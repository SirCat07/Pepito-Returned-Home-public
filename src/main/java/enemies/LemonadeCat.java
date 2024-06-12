package enemies;

import main.GamePanel;
import utils.GameEvent;
import utils.Pepitimer;

import java.awt.*;

public class LemonadeCat extends Enemy {
    boolean active;

    public LemonadeCat(GamePanel panel) {
        super(panel);
    }

    byte currentTry = 0;

    public void spawn() {
        g.lemonadeGato = GamePanel.loadImg("/game/entities/lemonade/gato.png");

        g.getNight().addEventPercent(0.2F);

        cursorZoom = 1;
        rotation = 0;

        active = true;
        g.getNight().setEvent(GameEvent.LEMONADE);
        currentTry = 0;
        speed = 1 * modifier;

        left = true;
        currentFunction = 0;
        currentX = 0;
        health = (byte) (5 * modifier);

        lemonadePos = new Point[4];
        lemonadeZoom = new float[4];

        g.getNight().getPepito().scare();
        g.getNight().getAstarta().leaveEarly();
        g.getNight().getMaki().scare();
    }

    public void throwLemonade(Point pos, boolean mirror) {
        if(currentTry > 2)
            return;

        rotation = 12;
        cursorZoom = 3;

        currentTry++;
        g.sound.play("throw", 0.1);

        int x = (int) ((pos.x - g.centerX) / ((g.getWidth() - g.centerX * 2) / 1080.0));
        if(mirror) {
            x = 1080 - x;
        }
        x = x - g.offsetX;

        lemonadePos[currentTry] = new Point(x, (int) ((pos.y - g.centerY) / ((g.getHeight() - g.centerY * 2) / 640.0)));
        lemonadeZoom[currentTry] = 1;

        if (currentTry == 3) {
            new Pepitimer(() -> {
                speed = 4 * modifier;

                new Pepitimer(() -> {
                    if(active) {
                        g.jumpscare("lemonadeCat");
                    }
                }, 3000);
            }, 1000);
        }
    }

    boolean left = true;
    float currentX = 0;
    float currentFunction = 0;

    public short getX() {
        return (short) currentX;
    }
    public float getY() {
        return currentFunction;
    }
    public byte getCurrentTry() {
        return currentTry;
    }

    public float speed = 1 * modifier;

    public Point[] lemonadePos = new Point[4];
    public float[] lemonadeZoom = new float[4];

    public void recalcY() {
        float j = 2 * speed * GamePanel.freezeModifier;

        if (left) {
            currentX += j;
        } else {
            currentX -= j;
        }

        currentFunction += 0.05F * speed * GamePanel.freezeModifier;

        if(currentFunction >= 3.14) {
            currentFunction = 0;
            g.sound.playRate("boing", 0.15, (speed / modifier) * GamePanel.freezeModifier);
        }

        if(currentX >= 1220 || currentX <= 0) {
            left = !left;
        }
    }

    byte health = (byte) (5 * modifier);

    public void recalcLemonade() {
        for(byte i = 0; i < 4; i++) {
            float zoom = lemonadeZoom[i];

            if(zoom != 0) {
                if (zoom > 0.1) {
                    lemonadeZoom[i] -= 0.03F;
                } else {
                    Point point = lemonadePos[i];

                    if(new Rectangle(g.offsetX - 400 + getX(), (int) (440 - Math.sin(getY()) * 150), 260, 200)
                            .contains(new Point(g.offsetX + point.x - (int) (200 * zoom), (int) (point.y - 150 * zoom)))) {

                        g.sound.play("lemonHit", 0.2);

                        if(timer != null) {
                            timer.cancel();
                        }
                        g.lemonadeGato = g.loadImg("/game/entities/lemonade/gato2.png");
                        timer = new Pepitimer(() -> {
                            g.lemonadeGato = g.loadImg("/game/entities/lemonade/gato.png");
                        }, 400);

                        health--;
                        if(currentTry != 3) {
                            currentTry--;
                        }

                        if(health <= 0) {
                            new Pepitimer(() -> {
                                leave();
                            }, 350);

                        }
                    }

                    lemonadeZoom[i] = 0;
                }
            }
        }
    }

    public void leave() {
        active = false;

        new Pepitimer(() -> {
            if(g.getNight().getEvent() == GameEvent.LEMONADE) {
                g.getNight().setEvent(GameEvent.NONE);
            }
            startSeconds = (short) ((Math.random() * 140 + 80));

            g.getNight().seconds += 10;
            g.getNight().updateClockString();
        }, 2000);
    }

    float rotation = 0;
    float cursorZoom = 1;

    Pepitimer timer;

    public short startSeconds = (short) ((Math.random() * 140 + 80));

    public void tick() {
        if(AI > 0 && !active) {
            startSeconds--;
            if (startSeconds <= 0) {
                spawn();
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getCursorZoom() {
        return cursorZoom;
    }

    public void setCursorZoom(float cursorZoom) {
        this.cursorZoom = cursorZoom;
    }
}
