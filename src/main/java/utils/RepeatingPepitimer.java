package utils;

public class RepeatingPepitimer extends Pepitimer {
    float delay;

    public RepeatingPepitimer(Runnable runnable, int miliseconds, int delay) {
        super(runnable, miliseconds);

        this.delay = delay;
    }

    @Override
    public void decrease(float delta) {
        if(paused || gamePaused)
            return;

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

    public void setDelay(float delay) {
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
