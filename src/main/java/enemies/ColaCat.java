package enemies;

import game.bingo.BingoHandler;
import game.bingo.BingoTask;
import main.GamePanel;
import utils.Pepitimer;
import utils.RepeatingPepitimer;

public class ColaCat extends Enemy {
    public byte arrivalSeconds = (byte) (Math.random() * 100 + 40 / modifier);
    public byte currentState = 0;

    public boolean active = false;

    Pepitimer timer;

    public ColaCat(GamePanel panel) {
        super(panel);
    }

    public boolean isActive() {
        return active;
    }

    void spawn() {
        currentState = 1;
        active = true;
        timer = new RepeatingPepitimer(() -> {
            g.getNight().addEventPercent(0.08F);

            currentState++;
            if(currentState > 2) {
                if (currentState >= 7) {
                    g.jumpscare("colaCat");
                } else {
                    if (g.sensor.isEnabled()) {
                        if (Math.round(Math.random() * 2) == 0) {
                            g.console.add("idk man your soda has been seeming real");
                            g.console.add("real SUSPICIOUS lately");
                        }
                    }
                }
            }
        }, (short) (5000 / ((modifier + 0.1) / 1.5)), (short) (5000 / ((modifier + 0.1) / 1.5)));

        timer.affectByFreeze();
    }

    public void tick() {
        if(AI > 0 && g.soda.isEnabled()) {
            arrivalSeconds--;
            if(arrivalSeconds == 0) {
                spawn();
            }
        }
    }

    public void stopService() {
        try {
            timer.cancel();
        } catch (Exception ignored) { }
    }

    public void leave() {
        stopService();
        currentState = 0;

        BingoHandler.completeTask(BingoTask.SURVIVE_COLA_CAT);

        active = false;
    }
}
