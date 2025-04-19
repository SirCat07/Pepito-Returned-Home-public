package enemies;

import main.GamePanel;
import utils.GameType;
import utils.Pepitimer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Glitcher extends Enemy {

    public Glitcher(GamePanel panel) {
        super(panel);
    }

    public float counter = 0;
    public float intensity = -1;
    public boolean isGlitching = false;

    public byte spawnChance() {
        return (byte) (Math.round(Math.random() * 97 / (modifier + 0.001)) + 3);
    }

    public void spawn() {
        if(g.getNight().getType() == GameType.ENDLESS_NIGHT) {
            if (g.portalActive && g.endless.getNight() < 12)
                return;
        }

        isGlitching = true;
        g.soggyBallpitCap += 12;
        intensity = -1;

        g.camOut(false);

        g.sound.play("glitchArrive", 0.15);

        short firstGlitch = (short) (Math.random() * 100 + 55);
        g.fadeOut(firstGlitch, firstGlitch, 0);

        if(g.getNight().getA90().arrivalSeconds < 3) {
            g.getNight().getA90().arrivalSeconds += 3;
        }

        g.everySecond10th.put("glitcher", () -> {
            g.glitchX[0] = (short) (Math.random() * 1380 - 300);
            g.glitchY[0] = (short) (Math.random() * 890 - 150);
            g.glitchX[1] = (short) (Math.random() * 1380 - 300);
            g.glitchY[1] = (short) (Math.random() * 890 - 150);
        });

        if(g.sensor.isEnabled()) {
            g.console.add(GamePanel.getString("sensorGlitchingSounds"));
        }
        if(g.type == GameType.SHADOW) {
            addShadowGlitch();
        }

        new Pepitimer(() -> {
            g.sound.play("glitch1", 0.1);

            if(g.sensor.isEnabled()) {
                g.console.add(GamePanel.getString("sensorGlitchingSounds"));
            }
            if(g.type == GameType.SHADOW) {
                addShadowGlitch();
            }

            short secondGlitch = (short) (Math.random() * 200 + 55);
            g.fadeOut(secondGlitch, secondGlitch, 0);

            new Pepitimer(() -> {
                g.sound.play("glitch2", 0.08);

                g.getNight().getPepito().notPepitoChance += 1.5F * modifier;

                g.pixelation = (int) (Math.random() * 12 * modifier) + 1;

                g.getNight().getPepito().glitcherReset();

                short thirdGlitch = (short) (Math.random() * 200 + 55);
                g.fadeOut(thirdGlitch, thirdGlitch, 0);

                new Pepitimer(() -> {
                    isGlitching = false;
                    g.everySecond10th.remove("glitcher");
                }, (int) (500 * modifier));

                if(g.sensor.isEnabled()) {
                    g.console.add(GamePanel.getString("sensorGlitchingSounds"));
                }
                if(g.type == GameType.SHADOW) {
                    addShadowGlitch();
                }

                intensity = (float) (Math.random() / 8 * AI);
                g.fadeOutStatic(0, intensity * 0.5F, 0.01F);

                counter = 3 * modifier - 3;
            }, (short) (500 * modifier));
        }, (short) (500 * modifier));
    }

    public void increaseCounter() {
        if (isEnabled()) {
            counter += g.getNight().getMaki().isActive() ? 0.2F : 0.6F;

            int randomGlitch = spawnChance();
            if ((randomGlitch - counter) < -8) {
                spawn();
            }
        }
    }

    public void decreaseCounter() {
        if(AI > 0) {
            if (counter > 15) {
                counter = Math.max(0, counter - 0.5F);
            } else {
                counter = Math.max(0, counter - 0.2F);
            }
        }
    }
    
    public int visualFormTicks = 0;


    List<Point> shadowGlitches = new ArrayList<>();
    public void addShadowGlitch() {
        g.sound.play("shadowGlitch", 0.05);
        shadowGlitches.add(new Point((int) (Math.random() * 115) * 10, (int) (Math.random() * 52) * 10));
    }

    public List<Point> getShadowGlitches() {
        return shadowGlitches;
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && (!g.adblocker.isEnabled() && !g.adBlocked);
    }

    @Override
    public int getArrival() {
        return -1;
    }

    @Override
    public void fullReset() {
        g.pixelation = 1;
    }
}
