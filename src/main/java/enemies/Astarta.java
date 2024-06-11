package enemies;

import game.bingo.BingoHandler;
import game.bingo.BingoTask;
import main.GamePanel;
import utils.RepeatingPepitimer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Astarta extends Enemy {
    public byte door = 0;
    public short arrivalSeconds = (short) (((Math.random() * 40 + 35) / (modifier * 1.5)) + modifier);
    public byte leaveSeconds = 0;

    public boolean active = false;

    public Astarta(GamePanel panel) {
        super(panel);
    }

    public boolean isActive() {
        return active;
    }

    RepeatingPepitimer timer;
    public int animation = 0;
    public boolean blinker = false;

    public void spawn() {
        List<Integer> list = new ArrayList<>(g.getNight().getDoors().keySet());
        Collections.shuffle(list);
        door = (byte) (int) (list.get(0));

        if(g.sensor.isEnabled()) {
            int random = (int) Math.round(Math.random());
            if (random == 0) {
                g.console.add("movement detected at door N" + (door + 1));
            }
        }

        g.getNight().addEventPercent(0.05F);

        if(g.getNight().getDoors().get((int) door).isClosed()) {
            arrivalSeconds = (short) ((Math.random() * 5 + 6) / modifier);

            BingoHandler.completeTask(BingoTask.SURVIVE_ASTARTA);

            if(g.getNight().isBlizzardModifier()) {
                if(g.getNight().getBlizzardTime() > 0) {
                    arrivalSeconds = 4;
                }
            }
        } else {
            animation = 0;
            blinker = false;
            active = true;

            if(timer != null) {
                timer.cancel(true);
            }
            short[] delay = {(short) (9000 / (modifier / 1.5))};

            timer = new RepeatingPepitimer(() -> {
                delay[0] -= 200;
                if(delay[0] < 2000) {
                    blinker = !blinker;
                }
                if(delay[0] > 0)
                    return;

                if(active) {
                    if (g.getNight().getDoors().get((int) door).isClosed() || g.getNight().getDoors().get((int) door).getBlockade() > 0) {
                        if(g.getNight().getDoors().get((int) door).isClosed()) {
                            g.sound.play("knock", 0.03);
                        }
                        if(g.getNight().getDoors().get((int) door).getBlockade() > 0) {
                            g.getNight().getDoors().get((int) door).addBlockade(-1);;
                            g.sound.play("blockadeHit", 0.1);

                            if(g.getNight().getDoors().get((int) door).getBlockade() == 0) {
                                g.sound.play("blockadeBreak", 0.1);
                                g.repaintOffice();
                            }
                        }

                        if(g.sensor.isEnabled()) {
                            byte random2 = (byte) Math.round(Math.random());
                            if (random2 == 0) {
                                g.console.add("movement detected at door N" + (door + 1));
                            }
                        }
                        leaveSeconds = 4;
                    } else {
                        g.jumpscare("astarta");
                    }
                    resetCounter();
                    active = false;
                }
            }, 200, 200);

            timer.affectByFreeze();
        }
    }

    public void leaveEarly() {
        stopService();

        try {
            g.sound.play("knock", 0.02);

            if (g.sensor.isEnabled()) {
                byte random2 = (byte) Math.round(Math.random());
                if (random2 == 0) {
                    g.console.add("movement detected at door N" + (door + 1));
                }
            }
            leaveSeconds = 4;

            resetCounter();
            active = false;
        } catch (Exception ignored) { }
    }

    public void tick() {
        if(AI <= 0)
            return;

        arrivalSeconds--;
        if(arrivalSeconds == 0) {
            spawn();
        }
        if(leaveSeconds > 0) {
            leaveSeconds--;
        }
    }

    public void stopService() {
        try {
            timer.cancel(true);
        } catch (Exception ignored) { }
    }

    public void resetCounter() {
        arrivalSeconds = (short) (((Math.random() * 35 + 30) / (modifier * 1.5)) + modifier);

        if(g.getNight().isBlizzardModifier()) {
            if(g.getNight().getBlizzardTime() > 0) {
                arrivalSeconds = 4;
            }
        }
    }
}
