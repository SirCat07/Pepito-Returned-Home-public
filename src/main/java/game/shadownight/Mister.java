package game.shadownight;

import game.Level;
import main.GamePanel;
import main.SoundMP3;
import utils.Pepitimer;
import utils.PepitoImage;
import utils.RepeatingPepitimer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mister {
    public Mister(AstartaBoss boss) {
        this.boss = boss;
    }
    AstartaBoss boss;

    boolean isBeingHeld = false;

    boolean spawned = false;
    boolean active = false;
    Point point = new Point(0, 0);

    float bloomTransparency = 0;
    float countdown = 12;
    float startCountdown = 12;


    public void spawn() {
        spawned = true;
        boss.misterCount++;
        if(boss.isFirstMister() && boss.g.shadowCheckpointUsed != 0) {
            startCountdown = 9;
        }
        countdown = startCountdown;

        int[] times = new int[] {0};
        RepeatingPepitimer[] timer = new RepeatingPepitimer[1];

        timer[0] = new RepeatingPepitimer(() -> {
            point = new Point((int) (Math.random() * 1180), (int) (Math.random() * 440));
            if(times[0] > 4) {
                timer[0].cancel(true);

                actualSpawn();
            }
            times[0]++;
        }, 50, 50);
    }

    public void actualSpawn() {
        boss.g.everyFixedUpdate.put("misterCountdown", () -> {
            reduceCountdown(0.01666F * boss.getSpeedModifier());
            if(!spawned) {
                boss.g.everyFixedUpdate.remove("misterCountdown");
            }
        });
        point = new Point((int) (Math.random() * 1180), (int) (Math.random() * 440));
        active = true;
    }

    public void remove() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }
    public boolean isSpawned() {
        return spawned;
    }

    public float getBloomTransparency() {
        return bloomTransparency;
    }
    public void setBloomTransparency(float bloomTransparency) {
        this.bloomTransparency = bloomTransparency;
    }

    public void cursorToPoint(Point pos, boolean mirror, GamePanel g) {
        if(mirror) {
            pos = new Point(g.getWidth() - pos.x, pos.y);
        }
        int x = (int) ((pos.x - g.centerX) / ((g.getWidth() - g.centerX * 2) / 1080.0));
        x = x - g.offsetX;

        int y = (int) ((pos.y - g.centerY) / ((g.getHeight() - g.centerY * 2) / 640.0));

        x += 250;
        y -= 100;

        x = Math.min(1210, Math.max(-30, x));
        y = Math.min(465, Math.max(-30, y));

        point = new Point(x, y);
    }

    public void translate(int dx, int dy) {
        int x = Math.min(1210, Math.max(-30, point.x + dx));
        int y = Math.min(465, Math.max(-30, point.y + dy));
        point = new Point(x, y);
    }

    public boolean isInsideHitbox(Point pos, boolean mirror, GamePanel g) {
        if(mirror) {
            pos = new Point(g.getWidth() - pos.x, pos.y);
        }
        int x = (int) ((pos.x - g.centerX) / ((g.getWidth() - g.centerX * 2) / 1080.0));
        x = x - g.offsetX;

        int y = (int) ((pos.y - g.centerY) / ((g.getHeight() - g.centerY * 2) / 640.0));

        Level night = boss.g.getNight();
        if(night.getMSI().isActive() || night.getScaryCat().isActive()) {
            return new Rectangle(point.x - 375, point.y + 25, 250, 150).contains(new Point(x, y));
        }
        return new Rectangle(point.x - 400, point.y, 300, 200).contains(new Point(x, y));
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Point getPoint() {
        return point;
    }

    public boolean isBeingHeld() {
        return isBeingHeld;
    }

    public void setBeingHeld(boolean beingHeld) {
        isBeingHeld = beingHeld;
    }

    public float vx = 0;
    public float vy = 0;

    public void addVelocity(float vx, float vy) {
        this.vx += vx;
        this.vy += vy;
    }

    public float getCountdown() {
        return countdown;
    }


    public void reduceCountdown(float reduce) {
        countdown -= reduce;
        if(countdown <= 0) {
            attack();
        }
    }

    PepitoImage image = new PepitoImage("");
    short flashAlpha = 0;
    boolean attacking = false;


    private void attack() {
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            keys.add(i + ".png");
        }
        Collections.shuffle(keys);
        attacking = true;
        spawned = false;
        active = false;

        boss.g.everyFixedUpdate.put("misterAlpha", () -> {
            if (flashAlpha > 0) {
                flashAlpha -= 6;
            }
            if(!attacking) {
                boss.g.everyFixedUpdate.remove("misterAlpha");
            }
        });

        if (boss.isFirstMister()) {
            boss.setFirstMister(false);
            startCountdown = 9;

            new Pepitimer(() -> boss.activatePhase2(), 3000);
        }

        switch (boss.getPhase()) {
            case 1 -> boss.untilPhase2 += 2;
            case 2 -> boss.untilPhase1 += 2;
        }

        image.setPath("/game/entities/astartaBoss/misterImgs/" + keys.get(0));
        thing();

        new Pepitimer(() -> {
            image.setPath("/game/entities/astartaBoss/misterImgs/" + keys.get(1));
            thing();

            new Pepitimer(() -> {
                image.setPath("/game/entities/astartaBoss/misterImgs/" + keys.get(2));
                thing();

                new Pepitimer(() -> {
                    attacking = false;
                    if(isBeingHeld) {
                        boss.g.unholdMister(this);
                    }

                    untilMisterSpawn = 10 + (int) (Math.round(Math.random() * 24) * 0.5F);
                }, 750);
            }, 600);
        }, 600);
    }

    // executes spawn(); when reaches 0
    float untilMisterSpawn = 100000;

    private void thing() {
        image.reload();
        flashAlpha = 180;

        boss.g.getNight().setEnergy(Math.min(boss.g.getNight().getEnergy() + 5, boss.g.getNight().getMaxEnergy()));

        if(boss.isFirstMister()) {
            if (boss.getHitbox().intersects(getHitbox())) {
                boss.damage(5 + Math.round(Math.random() * 2));
            }
        } else {
            if(boss.getHitbox().intersects(getSmallerHitbox())) {
                boss.damage(5 + Math.round(Math.random() * 2));
            }
        }

        SoundMP3 sound = boss.g.sound;
        sound.play("vineboom", 0.08);
        sound.play("sodaOpen", 0.03);
        sound.play("camOut", 0.1);
    }


    public boolean isAttacking() {
        return attacking;
    }

    public PepitoImage getImage() {
        return image;
    }

    public short getFlashAlpha() {
        return flashAlpha;
    }

    public float getStartCountdown() {
        return startCountdown;
    }

    public Rectangle getHitbox() {
        return new Rectangle(point.x, point.y, 300, 200);
    }

    public Rectangle getSmallerHitbox() {
        return new Rectangle(point.x + 25, point.y + 25, 250, 150);
    }
}