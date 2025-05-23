package enemies;

import main.GamePanel;
import utils.GameEvent;
import utils.Pepitimer;

import java.util.ArrayList;
import java.util.List;

public class A120 extends Enemy {
    public A120(GamePanel panel) {
        super(panel);
    }
    boolean active = false;

    private short x = 2860;
    public short getX() {
        return x;
    }
    
    List<Pepitimer> timers = new ArrayList<>();

    public void spawn() {
        timers.clear();
        
        active = true;
        g.sound.play("a120SoundRight", 0.4, 0.8);
        g.sound.play("a120SoundLeft", 0.4, -0.8);
        x = 2860;

        g.getNight().getPepito().scare();
        g.getNight().getAstarta().leaveEarly();
        g.getNight().getMaki().scare();

        timers.add(new Pepitimer(() -> {
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

                x -= 6;
                if (x < -620) {
                    g.everySecond20th.remove("a120");
                    active = false;
                    if(g.getNight().getEvent() == GameEvent.A120) {
                        g.getNight().setEvent(GameEvent.NONE);
                    }
                }

                int screenPos = g.offsetX - 200 + x;
                if(screenPos < 1080 && screenPos > 0 && !g.inLocker) {
                    new Pepitimer(() -> {
                        if(!g.inLocker) {
                            g.jumpscare("a120", g.getNight().getId());
                            g.everySecond20th.remove("a120");
                        }
                    }, 1000);
                }
            });
        }, 1200));
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public int getArrival() {
        return -1;
    }

    @Override
    public void fullReset() {
        active = false;
        timers.forEach(Pepitimer::cancel);
        
        if(g.getNight().getEvent() == GameEvent.A120) {
            g.getNight().setEvent(GameEvent.NONE);
        }
        g.everySecond20th.remove("a120");
    }
}