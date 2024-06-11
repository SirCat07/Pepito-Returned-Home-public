package enemies;

import main.GamePanel;
import utils.GameType;
import utils.Pepitimer;
import utils.RepeatingPepitimer;

public class ScaryCat extends Enemy {
    public byte arrivalSeconds = (byte) (Math.random() * 80 + 40);
    boolean active = false;

    public ScaryCat(GamePanel panel) {
        super(panel);
    }

    public boolean isActive() {
        return active;
    }

    public float x = 0;

    public void tick() {
        if(AI <= 0)
            return;

        if (!active) {
            arrivalSeconds--;
            if (arrivalSeconds == 0) {
                spawn();
            }
        }
    }

    float alpha = 1;

    int count = 0;

    RepeatingPepitimer repeatingPepitimer;

    float distance = 1000;

    public void spawn() {
        if(g.getNight().getMSI().arrivalSeconds < 21 || g.getNight().getMSI().isActive()) {
            arrivalSeconds += 5;
            return;
        }

        g.getNight().addEventPercent(0.15F);

        leave();
        active = true;
        x = -500;
        distance = 1000;
        alpha = 1;
        count = 0;

        g.everyFixedUpdate.put("scaryCat", () -> {
            int camX = 1080 - 400 - g.offsetX;
            float newX = ((99 * x) / GamePanel.freezeModifier + camX) / (100 / GamePanel.freezeModifier);
            float diff = newX - x;

            float minDiff = Math.min(6, 2.5F * modifier);
            if(Math.abs(diff) < minDiff) {
                if(diff > 0) {
                    diff = minDiff;
                } else {
                    diff = -minDiff;
                }
            }
            x += diff;

            alpha -= 0.01F * GamePanel.freezeModifier;
        });

        g.scaryCatSound.playRate("scaryCat" + (g.type == GameType.SHADOW ? "Shadow" : ""), 0.08, GamePanel.freezeModifier);

        repeatingPepitimer = new RepeatingPepitimer(() -> {
            alpha = 1;

            distance = Math.abs(1080 - 400 - g.offsetX - x);
            if(distance < 60) {
                if(count == 4) {
                    g.jumpscare("scaryCat");
                    leave();
                } else {
                    count++;
                    g.sound.play(("scaryCatAttack" + (Math.random() < 0.5 ? "Slow" : "")), 0.15);
                }
            } else if(count > 0) {
                count--;
            }
        }, 1000, 1000);
        repeatingPepitimer.affectByFreeze();

        new Pepitimer(this::leave, (int) (21500 / GamePanel.freezeModifier));
    }

    public void leave() {
        if(repeatingPepitimer != null) {
            repeatingPepitimer.cancel(false);
        }
        g.scaryCatSound.stop();
        g.everyFixedUpdate.remove("scaryCat");
        active = false;
        arrivalSeconds = (byte) (Math.random() * 80 + 40);
    }

    public int getX() {
        return (int) x;
    }

    public float getAlpha() {
        return alpha;
    }

    public int getCount() {
        return count;
    }

    public float getDistance() {
        return distance;
    }
}
