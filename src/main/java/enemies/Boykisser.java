package enemies;

import game.bingo.BingoHandler;
import game.bingo.BingoTask;
import main.GamePanel;
import utils.Pepitimer;

import java.util.concurrent.ScheduledFuture;

public class Boykisser extends Enemy {
    public short arrivalSeconds = (short) ((Math.random() * 120 + 45));

    public Boykisser(GamePanel panel) {
        super(panel);
    }

    boolean active = false;
    boolean awaitResponse = false;
    ScheduledFuture<?>[] service = new ScheduledFuture<?>[1];

    public boolean isActive() {
        return active;
    }

    public boolean isAwaitingResponse() {
        return awaitResponse;
    }

    public void spawn() {
        int miliseconds = 3200;

        if((byte) (Math.random() * 6) == 0) {
            miliseconds = 7800;
            g.sound.playRate("boykisserLong", 0.1, 1.0 / Math.max(1, (modifier / 2)));
        } else {
            g.sound.playRate("boykisser", 0.1, 1.0 / Math.max(1, (modifier / 2)));
        }

        active = true;
        awaitResponse = false;

        new Pepitimer(() -> {
            awaitResponse = true;
        }, (int) (miliseconds * Math.max(1, (modifier / 2))));
    }

    public void leave() {
        active = false;
        awaitResponse = false;

        BingoHandler.completeTask(BingoTask.SURVIVE_BOYKISSER);

        arrivalSeconds = (short) ((Math.random() * 120 + 45));

        g.sound.playRate("boykisserOut", 0.1, 1.0 / Math.max(1, (modifier / 2)));
    }

    public void reset() {
        awaitResponse = false;
        active = false;
    }

    public void tick(float reconsideration) {
        if(AI <= 0)
            return;

        if(reconsideration >= 1) {
            float chance = 1.1F - AI * 0.135F;
            float progress = 1 + (float) (g.getNight().seconds - g.getNight().secondsAtStart) / g.getNight().getDuration();
            chance /= progress;

            if(Math.random() < chance) {
                System.out.println(getClass().getName() + " reconsidered! | chance: " + chance);
                return;
            }
        }

        arrivalSeconds--;
        if(arrivalSeconds == 0) {
            spawn();
        }
    }

    @Override
    public int getArrival() {
        return arrivalSeconds;
    }

    @Override
    public void fullReset() {
        reset();
    }
}