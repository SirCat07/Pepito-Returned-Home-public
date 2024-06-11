package enemies;

import game.bingo.BingoHandler;
import game.bingo.BingoTask;
import main.GamePanel;
import utils.RepeatingPepitimer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class Maki extends Enemy {
    boolean active = false;

    public Maki(GamePanel panel) {
        super(panel);
    }
    public float alpha = 0.5F;

    RepeatingPepitimer timer;

    void spawn() {
        g.sound.playRate("makiSound", 0.1, GamePanel.freezeModifier);
        List<Integer> list = new ArrayList<>(g.getNight().getDoors().keySet());
        Collections.shuffle(list);
        door = (byte) (int) (list.get(0));
        alpha = 0.5F;
        active = true;

        if(g.sensor.isEnabled()) {
            int random = (int) Math.round(Math.random() * 2);
            if (random == 0) {
                g.console.add("INTRUSION AT DOOR N" + (door + 1));
            }
        }

        g.everySecond20th.put("makiAlpha", () -> {
           alpha -= 0.02F;
           if(alpha <= 0) {
               g.everySecond20th.remove("makiAlpha");
           }
        });

        if(timer != null) {
            timer.cancel(false);
        }

        timer = new RepeatingPepitimer(() -> {
            if(makiStepsLeft > 0) {
                makiStepsLeft--;
                g.sound.playRate("makiWalk", 0.05, GamePanel.freezeModifier);
            } else if(makiKnocksLeft >= 0) {
                if (!(g.getNight().getDoors().get((int) door).isClosed() || g.getNight().getDoors().get((int) door).getBlockade() > 0)) {
                    g.jumpscare("maki");
                } else {
                    makiKnocksLeft--;

                    if (g.getNight().getDoors().get((int) door).getBlockade() > 0) {
                        g.sound.playRate("blockadeHit", 0.1, GamePanel.freezeModifier);
                        g.getNight().getDoors().get((int) door).addBlockade(-1);

                        if (g.getNight().getDoors().get((int) door).getBlockade() == 0) {
                            g.sound.play("blockadeBreak", 0.1);
                            g.repaintOffice();
                        }
                    }
                    if(g.getNight().getDoors().get((int) door).isClosed()) {
                        g.sound.playRate("knock", 0.05, GamePanel.freezeModifier);
                        g.getNight().addEnergy(-0.5F);
                    }
                }

                if (makiKnocksLeft == 0) {
                    scare();
                }
            }
        }, 2500 + (short) (Math.random() * 600), 1100);

        timer.affectByFreeze();
    }

    public byte makiStepsLeft = 6;
    public byte makiKnocksLeft = 4;
    public byte secondsUntilMaki = 14;
    byte door = 0;

    public void stopService() {
        if(timer != null) {
            timer.cancel(true);
        }
    }

    public void tick() {
        if(AI <= 0)
            return;

        if (g.getNight().getPepito().seconds > 13 && g.getNight().getAstarta().arrivalSeconds > 13 && !g.getNight().getMSI().isActive()) {
            secondsUntilMaki--;
            if (secondsUntilMaki == 0) {
                spawn();
            }
        }
    }

    public void scare() {
        if(secondsUntilMaki <= 0) {
            makiKnocksLeft = 4;
            makiStepsLeft = 6;
            secondsUntilMaki = (byte) (23 - AI);

            BingoHandler.completeTask(BingoTask.SURVIVE_MAKI);

            if(g.sensor.isEnabled()) {
                byte random = (byte) Math.round(Math.random() * 5);
                if (random != 0) {
                    String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                    g.console.add("That was not Pepito! (" + timeStamp + ")");
                }
            }

            active = false;
            timer.cancel(false);
        }
    }

    public boolean isActive() {
        return active;
    }

    public byte getDoor() {
        return door;
    }
}
