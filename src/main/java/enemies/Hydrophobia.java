package enemies;

import main.GamePanel;
import utils.Pepitimer;

public class Hydrophobia extends Enemy {

    public Hydrophobia(GamePanel panel) {
        super(panel);
    }
    
    int secondsUntilStep = 25;
    int currentPos = 5;
    
    public void tick() {
        if(AI <= 0)
            return;

        secondsUntilStep--;
        if(secondsUntilStep <= 0) {
            secondsUntilStep = 30;

            if(currentPos < 2) {
                currentPos++;
            } else {
                currentPos--;
            }

            if(distance() <= 2 && distance() > 0) {
                g.getNight().setFlicker(100);
                g.sound.play("flicker" + (int) (Math.round(Math.random()) + 1), 0.05);
            }
            
            
            if(AI > 0 && distance() > 0) {
                resetSound();
            }
            
            
            if(distance() <= 0) {
                new Pepitimer(() -> {
                    if(distance() <= 0) {
                        if(AI <= 0)
                            return;
                        
                        g.jumpscare("hydrophobia", g.getNight().getId());
                    }
                }, 1000);
            }
            
        }
    }
    
    public void move() {
        if(AI <= 0)
            return;
        
        currentPos++;
        
        boolean lastResort = false;
        
//        lastResort = distance() <= 1 && secondsUntilStep < 30 && Math.random() < 0.8;
        
        if(Math.random() < 0.45 || lastResort) {
            if(currentPos > 2) {
                currentPos = 0;
                secondsUntilStep = 38;
            }
        }

        if(distance() <= 2 && distance() > 0) {
            g.getNight().setFlicker(100);
            g.sound.play("flicker" + (int) (Math.round(Math.random()) + 1), 0.05);
        }

        if(distance() <= 0) {
            new Pepitimer(() -> {
                if(distance() <= 0) {
                    if(AI <= 0)
                        return;
                    
                    g.jumpscare("hydrophobia", g.getNight().getId());
                }
            }, 1000);
        }
    }
    
    public void resetSound() {
        if(AI <= 0)
            return;
        
        g.fadeOut(255, 180, 1);
        g.music.stop();

        int secondsLeft = (Math.max(0, g.getNight().getHydrophobia().distance() - 1)) * 25 + g.getNight().getHydrophobia().getSecondsUntilStep();
        g.music.playFromSeconds("hydrophobiaSounds", 0.19, Math.max(0, 80 - secondsLeft));
    }
    

    public void turn() {
        currentPos = -(currentPos - 2) + 2;
    }
    
    public int distance() {
        return Math.abs(currentPos - 2);
    }

    
    public void setSecondsUntilStep(int secondsUntilStep) {
        this.secondsUntilStep = secondsUntilStep;
    }

    public int getSecondsUntilStep() {
        return secondsUntilStep;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public int getCurrentPos() {
        return currentPos;
    }

    @Override
    public int getArrival() {
        return secondsUntilStep;
    }

    @Override
    public void fullReset() {
    }
}
