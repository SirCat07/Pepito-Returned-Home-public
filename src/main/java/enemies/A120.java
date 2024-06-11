package enemies;

import main.GamePanel;
import utils.GameEvent;
import utils.Pepitimer;

public class A120 extends Enemy {
    public A120(GamePanel panel) {
        super(panel);
    }
    boolean active = false;

    private short x = 2860;
    public short getX() {
        return x;
    }

    public void spawn() {
        active = true;
        g.sound.play("a120SoundRight", 0.4, 0.8);
        g.sound.play("a120SoundLeft", 0.4, -0.8);
        x = 2860;

        new Pepitimer(() -> {
            g.getNight().setEvent(GameEvent.A120);

            if (g.sensor.isEnabled()) {
                if (Math.round(Math.random()) == 0) {
                    g.console.add("UNKNOWN ENTITY DETECTED");
                    g.console.add("MOVE TO THE LOCKER IMMEDIATELY");
                }
            }

            g.everySecond20th.put("a120", () -> {
                if (g.getNight().getA90().isActive())
                    return;

                x -= 5;
                if (x < -620) {
                    g.everySecond20th.remove("a120");
                    active = false;
                    g.getNight().setEvent(GameEvent.NONE);
                }

                int screenPos = g.offsetX - 200 + x;
                if(screenPos < 1080 && screenPos > 0 && !g.inLocker) {
                    new Pepitimer(() -> {
                        if(!g.inLocker) {
                            g.jumpscare("a120");
                            g.everySecond20th.remove("a120");
                        }
                    }, 1000);
                }
            });
        }, 1200);
    }

    public boolean isActive() {
        return active;
    }
}