package utils;

import main.GamePanel;

public class RepeatingPepitimer extends Pepitimer {
    int delay;

    public RepeatingPepitimer(Runnable runnable, int miliseconds, int delay) {
        super(runnable, miliseconds);

        this.delay = delay;
    }

    @Override
    public void decrease(int delta) {
        if(paused)
            return;

        if(freezeAffected)
            delta = (int) (delta * GamePanel.freezeModifier);

        if(miliseconds > delta / 2) {
            miliseconds -= delta;
        } else {
            if(stopNext) {
                StaticLists.timers.remove(this);
                return;
            }

            runnable.run();
            miliseconds = delay;
        }
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void cancel(boolean interrupt) {
        if(interrupt) {
            StaticLists.timers.remove(this);
        }
        stopNext = !interrupt;
    }

    boolean stopNext = false;
}
