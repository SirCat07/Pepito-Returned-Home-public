package enemies;

import javafx.scene.media.MediaPlayer;
import main.GamePanel;
import utils.GameEvent;
import utils.Pepitimer;

import java.awt.*;

public class LemonadeCat extends Enemy {
    boolean active;

    public LemonadeCat(GamePanel panel) {
        super(panel);
    }
    
    boolean nine = false;
    boolean nineBossfight = false;
    
    boolean playerDying = false;
    int playerDyingMillis = 0;

    byte currentTry = 0;

    public void spawn() {
        damaged = false;

        g.getNight().addEventPercent(0.2F);

        cursorZoom = 1;
        rotation = 0;
        hitboxAlpha = 0;

        active = true;
        if(g.getNight().getEvent().isInGame()) {
            g.getNight().setEvent(GameEvent.LEMONADE);
        }
        currentTry = 0;
        speed = 1 * modifier;
        
        playerDying = false;
        playerDyingMillis = 0;

        left = true;
        currentFunction = 0;
        currentX = 0;
        health = (byte) (5 * modifier);

        nineBossfight = false;
        nuclearBackflips = false;
        nuclearOxygen = false;
        backflipRadians = 0;
        oxygenLevel = 100;

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
            playerDying = true;
        }
    }

    boolean left = true;
    float currentX = 0;
    float currentFunction = 0;
    
    public float hitboxAlpha = 0;

    public short getX() {
        return (short) currentX;
    }
    public float getCurrentFunction() {
        return currentFunction;
    }
    public byte getCurrentTry() {
        return currentTry;
    }

    public float speed = 1 * modifier;

    public Point[] lemonadePos = new Point[4];
    public float[] lemonadeZoom = new float[4];

    public void recalcY() {
        float j = 2 * speed;

        if (left) {
            currentX += j;
        } else {
            currentX -= j;
        }

        if(nine) {
            if(nuclearBackflips) {
//                backflipRadians = currentFunction * 2 * (left ? 1 : -1);
                backflipRadians = currentFunction * (left ? 1 : -1);
            }
            if(nuclearOxygen) {
                oxygenLevel -= 0.3F;

                if (oxygenLevel < 0) {
                    g.jumpscare("lemonadeCat", g.getNight().getId());
                }
            }
        }
        
        if(playerDying) {
            playerDyingMillis += 17;
            if(playerDyingMillis > 1000) {
                new Pepitimer(() -> {
                    speed = 4 * modifier;

                    new Pepitimer(() -> {
                        if(active) {
                            g.jumpscare("lemonadeCat", g.getNight().getId());
                        }
                    }, 3000);
                }, 1000);
            }
        }
        
        hitboxAlpha = Math.max(0, hitboxAlpha - 0.02F);
        
        currentFunction += 0.05F * speed;

        if(currentFunction >= 3.14) {
            currentFunction = 0;
            g.sound.playRate("boing", nineBossfight ? 0.04 : 0.12, (speed / modifier));
        }

        if(currentX >= 1220 || currentX <= 0) {
            left = !left;
        }
    }
    
    public boolean damaged = false;

    byte health = (byte) (5 * modifier);
    
    public Polygon lastHitbox = null;

    public void recalcLemonade() {
        for(byte i = 0; i < 4; i++) {
            float zoom = lemonadeZoom[i];

            if(zoom != 0) {
                if (zoom > 0.1) {
                    lemonadeZoom[i] -= 0.03F;
                } else {
                    Point point = lemonadePos[i];

                    int offset = g.offsetX - g.getNight().env.maxOffset();
                    Polygon poly = GamePanel.rectangleToPolygon(new Rectangle(offset + getX(), (int) (440 - Math.sin(getCurrentFunction()) * 150), 260, 200));
                    poly = GamePanel.rotatePolygon(poly, offset + getX() + 130, (int) (400 - Math.sin(getCurrentFunction()) * 150) + 120, backflipRadians);
                    
                    if(poly.contains(new Point(g.offsetX + point.x - (int) (200 * zoom), (int) (point.y - 150 * zoom)))) {
                        Polygon poly2 = GamePanel.rectangleToPolygon(new Rectangle(getX(), (int) (440 - Math.sin(getCurrentFunction()) * 150), 260, 200));
                        poly2 = GamePanel.rotatePolygon(poly2, getX() + 130, (int) (400 - Math.sin(getCurrentFunction()) * 150) + 120, backflipRadians);
                        
                        lastHitbox = poly2;
                        hitboxAlpha = 1;

                        g.sound.play("lemonHit", 0.2);

                        if(timer != null) {
                            timer.cancel();
                        }
                        damaged = true;
                        timer = new Pepitimer(() -> {
                            damaged = false;
                        }, 400);

                        health--;
                        
                        currentTry--;
                        playerDying = false;
                        playerDyingMillis = 0;
                        
                        oxygenLevel = Math.min(100, oxygenLevel + 30);
                        

                        if(health <= 0) {
                            if(nine) {
                                if(!nineBossfight) {
                                    playerDying = false;
                                    playerDyingMillis = 0;
                                    
                                    nineBossfight = true;
                                    health = 22;
                                    lemonadePos = new Point[4];
                                    lemonadeZoom = new float[4];
                                    currentTry = 0;
                                    g.music.play("lemonadeCat9", 0.2, true);
                                    
                                    nuclearOxygen = true;
                                    new Pepitimer(() -> {
                                        nuclearBackflips = true;
                                    }, 5000);
                                    
                                    return;
                                }
                            }
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


        for(MediaPlayer player : g.music.clips) {
            if(player.getMedia().getSource().contains("lemonadeCat9.mp3")) {
                g.music.clipVolume.put(player, 0d);
                player.stop();
                player.dispose();
                g.music.clips.remove(player);
            }
        }
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

    public byte getHealth() {
        return health;
    }

    public void setNine(boolean nine) {
        this.nine = nine;
    }
    public boolean isNine() {
        return nine;
    }
    public boolean isNineBossfight() {
        return nineBossfight;
    }

    public boolean nuclearBackflips = false;
    public float backflipRadians = 0;
    public float getBackflipRadians() {
        return backflipRadians;
    }

    public boolean nuclearOxygen = false;
    public float oxygenLevel = 100;
    public float getOxygenLevel() {
        return getOxygenLevel();
    }
    

    @Override
    public int getArrival() {
        return startSeconds;
    }

    @Override
    public void fullReset() {
        leave();
        g.getNight().seconds -= 10;
        g.getNight().updateClockString();
    }
}
