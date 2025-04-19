package enemies;

import main.GamePanel;
import utils.GameEvent;
import utils.GameType;
import utils.Pepitimer;

import java.awt.*;

public class MirrorCat extends Enemy {
    boolean active = false;

    public MirrorCat(GamePanel panel) {
        super(panel);
    }

    boolean closed = false;
    short x = 0;
    boolean first = true;

    public void spawn() {
        if(active)
            return;

        g.everySecond.remove("mirrorCat");

        closed = false;
        active = true;
        exploded = false;
        x = (short) (Math.random() * 880 + 215);
        g.sound.play("mirrorCatAppear", 0.08);

        g.everySecond.put("mirrorCat", () -> {
            g.sound.playRate("mirrorCatTick", 0.05, g.type == GameType.SHADOW ? Math.random() + 0.5 : 1);

            if(g.getNight().getEvent() == GameEvent.ASTARTA) {
                g.getNight().getAstartaBoss().setHealth(g.getNight().getAstartaBoss().getHealth() + 0.2F);
            }
            if(!g.getNight().getEvent().canTimeTick())
                return;
            g.getNight().seconds = (short) Math.max(g.getNight().seconds - 2, g.getNight().secondsAtStart);
        });
    }
    boolean exploded = false;

    public void kill() {
        if(closed || !active)
            return;

        first = false;
        closed = true;
        g.sound.play("cageClose", 0.1);
        arrivalSeconds = (short) (Math.random() * (40 - AI * 1.5) + 5);
        g.everySecond.remove("mirrorCat");

        new Pepitimer(() -> {
            exploded = true;
            g.sound.play("cageExplode", 0.1);

            new Pepitimer(() -> {
                active = false;

                new Pepitimer(() -> {
                    exploded = false;
                }, 100);
            }, 100);
        }, 1000);
    }

    short arrivalSeconds = (short) (Math.random() * (40 - AI * 1.5) + 16);

    public void tick() {
        if(AI > 0 && !active) {
            arrivalSeconds--;
            if(arrivalSeconds == 0) {
                spawn();
            }
        }
    }

    public boolean isActive() {
        return active;
    }
    public boolean isClosed() {
        return closed;
    }
    public boolean isExploded() {
        return exploded;
    }

    public short getX() {
        return x;
    }

    public boolean isFirst() {
        return first;
    }
    
    public boolean isInside(Point point) {
        int rectX = g.offsetX - g.getNight().env().maxOffset() + g.getNight().getMirrorCat().getX() + g.currentWaterPos * 2;
        if(GamePanel.isMirror()) {
            rectX = 895 - rectX;
        }

        Rectangle rect = new Rectangle(rectX, 540 - g.waterLevel(), 185, 100);
        return rect.contains(point);
    }

    public boolean isInsideUnmirrored(Point point) {
        int rectX = g.offsetX - g.getNight().env().maxOffset() + g.getNight().getMirrorCat().getX() + g.currentWaterPos * 2;

        Rectangle rect = new Rectangle(rectX, 540 - g.waterLevel(), 185, 100);
        return rect.contains(point);
    }

    @Override
    public int getArrival() {
        return arrivalSeconds;
    }

    @Override
    public void fullReset() {
        kill();
    }
}