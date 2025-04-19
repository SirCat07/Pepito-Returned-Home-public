package enemies;

import main.GamePanel;
import utils.Pepitimer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Shock extends Enemy {
    public int arrivalSeconds = (int) ((Math.random() * 70 + 65) / modifier);
    boolean active = false;
    boolean doom = false;
    int door = 0;
    public float doomCountdown = 1;

    public Shock(GamePanel panel) {
        super(panel);
    }

    public boolean isActive() {
        return active;
    }

    public boolean isDoom() {
        return doom;
    }

    public int getDoor() {
        return door;
    }

    public float getDoomCountdown() {
        return doomCountdown;
    }

    public void spawn() {
        List<Integer> list = new ArrayList<>(g.getNight().getDoors().keySet());
        Collections.shuffle(list);
        door = list.get(0);
        
        doom = false;
        active = true;

        g.getNight().addEventPercent(0.1F);
        
        g.shockSound.play("shockSfx", 0.1);

        timer[0] = new Pepitimer(() -> {
            g.repaintOffice();
            g.fullOffice = imgFilter(g.fullOffice, 6, 2);

            g.getNight().addEventPercent(0.2F);
            
            doomCountdown = 1;
            doom = true;
            
            timer[0] = new Pepitimer(() -> {
                if (g.getNight().getDoors().get(door).isLocked()) {
                    if(g.getNight().getDoors().get(door).isClosed()) {
                        g.sound.play("knock", 0.06);
                    }
                    if(g.getNight().getDoors().get(door).getBlockade() > 0) {
                        g.getNight().getDoors().get(door).addBlockade(-1);;
                        g.sound.play("blockadeHit", 0.12);

                        if(g.getNight().getDoors().get(door).getBlockade() == 0) {
                            g.sound.play("blockadeBreak", 0.12);
                            g.repaintOffice();
                        }
                    }

                    stop();
                } else {
                    g.jumpscare("shock", g.getNight().getId());
                }
            }, 5500);
        }, 9500);
    }
    
    Pepitimer[] timer = new Pepitimer[1];
    

    public void tick() {
        if(AI <= 0)
            return;

        arrivalSeconds--;
        if(arrivalSeconds == 0) {
            spawn();
        }
    }

    public void stop() {
        active = false;
        doom = false;
        stopTimer();
        arrivalSeconds = (int) ((Math.random() * 70 + 65) / modifier);
        
        g.repaintOffice();
        door = 0;
        g.fadeOut(255, g.endFade, 2);
        
        g.shockSound.stop();
    }

    public void stopTimer() {
        if(timer[0] != null) {
            timer[0].cancel();
        }
    }

    public BufferedImage imgFilter(BufferedImage img, int intensity, int precision) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics2D graphics2D = (Graphics2D) result.getGraphics();

        for (int x = 0; x < img.getWidth(); x += precision) {
            int h = (int) (intensity * Math.random());
            graphics2D.drawImage(img.getSubimage(x, 0, 1, 640 - h), x, 0, 1, 640, null);
        }
        
        graphics2D.dispose();

        short x = 0;
        while (x < result.getWidth()) {
            short y = 0;
            while (y < result.getHeight()) {
                Color color = new Color(result.getRGB(x, y), true);

                if(color.getAlpha() > 0) {
                    Color newColor = new Color((color.getRed() + color.getGreen() + color.getBlue()) / 3, 0, 0, color.getAlpha());
                    result.setRGB(x, y, newColor.getRGB());
                }
                y++;
            }
            x++;
        }

        return result;
    }

    @Override
    public int getArrival() {
        return arrivalSeconds;
    }

    @Override
    public void fullReset() {
        stop();
    }
}
