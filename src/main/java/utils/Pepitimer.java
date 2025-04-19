package utils;

public class Pepitimer {
    Runnable runnable;
    float miliseconds;
    boolean paused;
    boolean gamePaused;
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
    public void gamePause() {
        gamePaused = true;
    }
    public void gameResume() {
        gamePaused = false;
    }
    

    public void decrease(float delta) {
        if(paused || gamePaused)
            return;

        if(miliseconds > delta / 2) {
            miliseconds -= delta;
        } else {
            runnable.run();
            StaticLists.timers.remove(this);
        }
    }

    public void setMiliseconds(float miliseconds) {
        this.miliseconds = miliseconds;
    }

    public float getMiliseconds() {
        return miliseconds;
    }

    public void cancel() {
        StaticLists.timers.remove(this);
    }
}
