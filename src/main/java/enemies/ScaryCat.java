package enemies;

import game.enviornments.Enviornment;
import game.particles.NuclearCatEye;
import main.GamePanel;
import utils.GameType;
import utils.Pepitimer;
import utils.RepeatingPepitimer;

public class ScaryCat extends Enemy {
    public byte arrivalSeconds = (byte) (Math.random() * 86 + 42);
    boolean active = false;

    public ScaryCat(GamePanel panel) {
        super(panel);
    }

    public boolean isActive() {
        return active;
    }

    public float x = 0;
    boolean nine = false;
    float eyesRotation = 0;


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
        if((g.getNight().getMSI().isEnabled() && g.getNight().getMSI().arrivalSeconds < 21) || g.getNight().getMSI().isActive()) {
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
        Enviornment env = g.getNight().env();

        g.everyFixedUpdate.put("scaryCat", () -> {
            float startX = x;
            int camX = 1080 - env.maxOffset() - g.offsetX;
            float newX = ((99 * x) + camX) / (100);
            float diff = newX - x;

            float minDiff = Math.min(6, 2.4F * modifier);
            if(Math.abs(diff) < minDiff) {
                if(diff > 0) {
                    diff = minDiff;
                } else {
                    diff = -minDiff;
                }
            }
            x += diff;

            alpha -= 0.01F;
            
            if(nine) {
                float oldEyesRotation = eyesRotation;
                eyesRotation += (float) (0.04F + Math.random() / 10F);

                if (oldEyesRotation < 3.14F && eyesRotation >= 3.14F && Math.abs(newX - startX) > 1.15) {
                    x = (camX * 2 + x) / 3;
                    g.sound.playRate("nuclearCatTeleport", 0.08, 0.75 + Math.random() / 4);
                    
                    eyesRotation += (float) Math.random();

                    int sinY = (int) (Math.sin(eyesRotation) * 340);
                    int cosX = (int) (Math.cos(eyesRotation) * 340);
                    int modifier = (int) - ((newX - startX) / Math.abs(newX - startX));
                    g.nuclearCatEyes.add(new NuclearCatEye((int) (x + 250 + (modifier * cosX)), 320 + (modifier * sinY)));
                }
                if (eyesRotation >= 3.14F) {
                    eyesRotation -= 3.14F;
                }
            }
        });

        if(nine) {
            g.scaryCatSound.play("nuclear cat", 0.1);
        } else {
            g.scaryCatSound.play("scaryCat" + (g.type == GameType.SHADOW ? "Shadow" : ""), 0.08);
        }
        
        
        repeatingPepitimer = new RepeatingPepitimer(() -> {
            alpha = 1;

            distance = Math.abs(1080 - 400 - g.offsetX - x);
            if(distance < 60) {
                if(count == 4) {
                    g.jumpscare("scaryCat", g.getNight().getId());
                    leave();
                } else {
                    count++;
                    g.sound.play(("scaryCatAttack" + (Math.random() < 0.5 ? "Slow" : "")), 0.15);
                }
            } else if(count > 0) {
                count--;
            }
        }, 1000, 1000);

        new Pepitimer(this::leave, 21500);
    }

    public void leave() {
        if(repeatingPepitimer != null) {
            repeatingPepitimer.cancel(false);
        }
        g.scaryCatSound.stop();
        g.everyFixedUpdate.remove("scaryCat");
        active = false;
        arrivalSeconds = (byte) (Math.random() * 86 + 42);
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

    @Override
    public int getArrival() {
        return arrivalSeconds;
    }

    public void setNine(boolean nine) {
        this.nine = nine;
    }

    public boolean isNine() {
        return nine;
    }

    public float getEyesRotation() {
        return eyesRotation;
    }

    @Override
    public void fullReset() {
        leave();
    }
}
