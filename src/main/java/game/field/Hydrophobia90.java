package game.field;

import main.GamePanel;
import utils.Pepitimer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Hydrophobia90 {
    public boolean active = false;
    public boolean dying = false;
    public byte animation = 0;

    public short x = 440;
    public short y = 230;
    
    GamePanel g;
    public Hydrophobia90(GamePanel panel) {
        this.g = panel;
    }

    public boolean isActive() {
        return active;
    }

    public byte shots = 0;

    public short margin = 0;
    public boolean drawStopSign = false;

    public List<Point> points = new ArrayList<>();
    public int distance = 0;

    public void spawn() {
        points.clear();
        distance = 0;
        
        g.sound.playRate("a90Alert", 0.1, (float) (Math.random() + 0.5F));
        x = (short) (Math.random() * 540 + 120);
        y = (short) (Math.random() * 100 + 120);
        animation = 1;

        g.everySecond20th.put("a90glitch", () -> {
            points.add(new Point((int) (x - 50 + Math.random() * 400), (int) (y + Math.random() * 300)));
        });
        
        
        new Pepitimer(() -> {
            g.everyFixedUpdate.put("a90bg", () -> {
                distance += 30;
            });

            margin = 0;
            animation = 2;
            new Pepitimer(() -> {
                animation = 3;
                active = true;
                drawStopSign = true;
                g.sound.playRate("a90Arrive", 0.04, (float) (Math.random() + 0.5F));
                new Pepitimer(() -> {
                    animation = 4;
                    new Pepitimer(() -> {
                        animation = 0;
                        g.everySecond20th.remove("a90glitch");

                        new Pepitimer(() -> {
                            animation = 4;
                            drawStopSign = false;

                            for(int i = 0; i < 10; i++) {
                                points.add(new Point((int) (x - 75 + Math.random() * 300), (int) (y + Math.random() * 300)));
                            }

                            if(dying) {
                                g.everySecond20th.put("a90anim", () -> g.anim ^= 1);
                                
                                g.sound.playRate("a90Dead", 0.08, (float) (Math.random() + 0.5F));
                                animation = 5;
                                shots++;

                                if (shots == 2) {
//                                    g.jumpscare("a90");
                                    g.field.kill(g, "fieldA90");
                                    
                                    new Pepitimer(() -> {
                                        animation = 0;
                                        g.everySecond20th.remove("a90anim");
                                    }, 2300);
                                } else {
                                    new Pepitimer(() -> {
                                        animation = 0;
                                        g.everySecond20th.remove("a90anim");
                                    }, 2300);
                                }
                            } else {
                                g.sound.playRate("a90Alive", 0.08, (float) (Math.random() + 0.5F));

                                new Pepitimer(() -> {
                                    animation = 0;
                                }, 200);
                            }

                            g.sound.resume(false);
                            dying = false;
                            active = false;
                            g.everyFixedUpdate.remove("a90bg");
                        }, 350);
                    }, 250);
                }, 70);
            }, 180);
        }, 500);
    }
    
    public boolean isDying() {
        return dying;
    }
}
