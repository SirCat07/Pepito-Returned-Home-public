package utils;

import main.GamePanel;

public class Pepitimer {
    Runnable runnable;
    int miliseconds;
    boolean paused;
    boolean track;

    public Pepitimer(Runnable runnable, int miliseconds) {
        this.runnable = runnable;
        this.miliseconds = miliseconds;

        StaticLists.timers.add(this);
    }

    public Pepitimer(Runnable runnable, int miliseconds, boolean track) {
        this.runnable = runnable;
        this.miliseconds = miliseconds;
        this.track = track;

        StaticLists.timers.add(this);
    }

    public void pause() {
        paused = true;
    }
    public void resume() {
        paused = false;
    }
    boolean freezeAffected = false;
    public void affectByFreeze() {
        freezeAffected = true;
    }

    public void decrease(int delta) {
        if(paused)
            return;

        if(freezeAffected)
            delta = (int) (delta * GamePanel.freezeModifier);

        if(miliseconds > delta / 2) {
            miliseconds -= delta;
        } else {
            runnable.run();
            StaticLists.timers.remove(this);
        }
    }

    public void setMiliseconds(int miliseconds) {
        this.miliseconds = miliseconds;
    }

    public int getMiliseconds() {
        return miliseconds;
    }

    public void cancel() {
        StaticLists.timers.remove(this);
    }
}
