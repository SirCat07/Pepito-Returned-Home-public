package enemies;

import game.Door;
import main.GamePanel;
import utils.RepeatingPepitimer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Wires extends Enemy {
    public Wires(GamePanel panel) {
        super(panel);
    }

    boolean active = false;
    Rectangle hitbox;
    short health = 3;
    float acceleration = 0.1F;
    

    public void spawn() {
        health = (short) (4 + AI / 2);
        if(g.getNight().isTimerModifier()) {
            health = (short) Math.round(health / 2F - 0.1F);
        }
        
        List<Rectangle> list = new ArrayList<>();
        list.add(g.getNight().env.boop);
        
        for(Door door : g.getNight().doors.values()) {
            list.add(door.getButtonHitbox(0, 0));
        }
        Collections.shuffle(list);
        
        hitbox = list.get(0);
        
        acceleration = 0.1F * modifier;
        active = true;

        AtomicInteger times = new AtomicInteger();

        if(timer != null) {
            timer.cancel(true);
        }
        timer = new RepeatingPepitimer(() -> {
            acceleration += 0.01F + acceleration * 0.01F;
            g.getNight().addEnergy(-acceleration);
            times.getAndIncrement();

            if(health <= 0 || times.get() > 100) {
                active = false;
                timer.cancel(true);
                arrivalSeconds = (short) ((40 + Math.random() * 20) / modifier);
            }
        }, 100, 100);
    }

    RepeatingPepitimer timer;

    public void stopService() {
        if(timer != null) {
            timer.cancel(true);
        }
    }

    public void leave() {
        active = false;
        arrivalSeconds = (short) ((40 + Math.random() * 20) / modifier);
        stopService();
    }

    short arrivalSeconds =  (short) (30 + Math.random() * 30);

    public void tick(float reconsideration) {
        if(AI > 0 && !active) {
            arrivalSeconds--;
            if(arrivalSeconds == 0) {
                if(reconsideration >= 1) {
                    float chance = 1.1F - AI * 0.135F;
                    float progress = 1 + (float) (g.getNight().seconds - g.getNight().secondsAtStart) / g.getNight().getDuration();
                    chance /= progress;

                    if(Math.random() < chance) {
                        System.out.println(getClass().getName() + " reconsidered! | chance: " + chance);
                        return;
                    }
                }
                
                spawn();
            }
        }
    }

    public void hit() {
        health--;
        g.sound.play("wiresHit", 0.05);
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public int getArrival() {
        return arrivalSeconds;
    }

    @Override
    public void fullReset() {
        leave();
    }
}
