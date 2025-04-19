package enemies;

import main.GamePanel;
import utils.GameEvent;
import utils.RepeatingPepitimer;

public class Kiji extends Enemy {
    int arrivalSeconds = 5000;
    boolean active = false;
    
    public Kiji(GamePanel panel) {
        super(panel);
    }

    public boolean isActive() {
        return active;
    }
    
    public void spawn() {
        progress = 0.005F;
        active = true;
        range = 20F;
        state = 0;
        mouseEverReleased = false;

        g.getNight().addEventPercent(0.1F);
        
        timer[0] = new RepeatingPepitimer(() -> {
            if(progress < 1F) {
                progress *= 1.4F;
            } else {
                if(range > 0.5F) {
                    range /= 1.2F;
                } else {
                    timer[0].cancel(true);
                    
                    int[] times = new int[] {0};
                    g.sound.play("kijiAppear", 0.15F);

                    timer[0] = new RepeatingPepitimer(() -> {
                        if(g.getNight().getEvent().isInGame()) {
                            g.getNight().setEvent(GameEvent.KIJI);
                        }
                        
                        int limit = 5 + (int) (Math.random() * 4);
                        
                        times[0]++;
                        if(times[0] > limit) {
                            stop();
                        } else {
                            boolean taskDone = false;
                            
                            if(state == 0 || state == 2) {
                                if(mouseEverReleased || state == 0) {
                                    taskDone = true;
                                }
                                
                                state = 1;
                                g.sound.playRate("kijiHold", 0.15F, 0.9F + Math.random() / 5);
                            } else {
                                state = 2;
                                g.sound.playRate("kijiRelease", 0.15F, 0.9F + Math.random() / 5);
                                
                                if(g.keyHandler.trueMouseHeld) {
                                    taskDone = true;
                                }
                                mouseEverReleased = false;
                            }
                            
                            if(!taskDone) {
                                g.jumpscare("kiji", g.getNight().getId());
                                timer[0].cancel(true);
                            }
                            
                            int number = 350 + (int) (Math.random() * 350);
                            timer[0].setDelay(number);
                            timer[0].setMiliseconds(number);
                        }
                    }, 600, 600);
                    
                }
            }
        }, 20, 20);

        g.getNight().getPepito().scare();
        g.getNight().getAstarta().leaveEarly();
        g.getNight().getMaki().scare();
    }
    
    boolean mouseEverReleased = false;
    
    float progress = 0;
    float range = 20F;
    byte state = 0;
    RepeatingPepitimer[] timer = new RepeatingPepitimer[1];

    public float getRange() {
        return range;
    }

    public float getProgress() {
        return progress;
    }

    public byte getState() {
        return state;
    }

    public void setMouseEverReleased(boolean mouseEverReleased) {
        this.mouseEverReleased = mouseEverReleased;
    }

    public void tick() {
        if(AI <= 0)
            return;

        arrivalSeconds--;
        if(arrivalSeconds == 0) {
            spawn();
        }
    }
    
    public void stop() {
        active = false;
        progress = 0F;
        range = 20F;
        state = 0;
        if(g.getNight().getEvent() == GameEvent.KIJI) {
            g.getNight().setEvent(GameEvent.NONE);
        }
        stopTimer();
        arrivalSeconds = 5000;
    }
    
    public void stopTimer() {
        if(timer[0] != null) {
            timer[0].cancel(true);
        }
    }
    
    @Override
    public int getArrival() {
        return arrivalSeconds;
    }

    @Override
    public void fullReset() {
        stop();
    }
}
