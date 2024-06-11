package enemies;

import main.GamePanel;
import utils.RepeatingPepitimer;

import java.util.concurrent.atomic.AtomicInteger;

public class Wires extends Enemy {
    public Wires(GamePanel panel) {
        super(panel);
    }

    boolean active = false;
    byte state = 0;
    short health = 3;
    float acceleration = 0.1F;

    public void spawn() {
        health = (short) (4 + AI / 2);
        state = (byte) (Math.random() * 3);
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

    public void tick() {
        if(AI > 0 && !active) {
            arrivalSeconds--;
            if(arrivalSeconds == 0) {
                spawn();
            }
        }
    }

    public void hit() {
        health--;
        g.sound.play("wiresHit", 0.05);
    }

    public byte getState() {
        return state;
    }

    public boolean isActive() {
        return active;
    }
}
