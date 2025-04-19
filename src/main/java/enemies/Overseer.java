package enemies;

import game.enviornments.HChamber;
import main.GamePanel;
import utils.GameType;
import utils.Pepitimer;

public class Overseer extends Enemy {
    
    int arrivalSeconds = (int) (Math.random() * 7 + 5);

    public Overseer(GamePanel panel) {
        super(panel);
    }
    
    int x = 0;
    int height = 1;
    float rage = 0;
    
    public Pepitimer beepTimer;
    
    int radius = 0;
    
    
    public void spawn() {
        untilRelocation = 4 + (int) (Math.random() * 4);
        
        height = 1;
        rage = 0;
        if(Math.round(Math.random()) == 0) {
            x = 0;
        } else {
            x = 1280;
        }
        radius = 0;
        
        active = true;
    }
    
    public void relocate() {
        untilRelocation = 4 + (int) (Math.random() * 4);

        if(beepTimer != null) {
            beepTimer.cancel();
            beepTimer = null;
        }

        height = 180;
        rage = 0;
        if(active && x == 1280) {
            x = 0;
        } else {
            x = 1280;
        }
        radius = 0;

        active = true;
    }
    

    public void disappear() {
        if(!active)
            return;
        
        if(beepTimer != null) {
            beepTimer.cancel();
            beepTimer = null;
        }
        
        active = false;
        arrivalSeconds = (int) (Math.random() * 24 + 16);
        rage = 0;
    }

    public void tick() {
        if(AI <= 0)
            return;

        untilRelocation--;
        if(untilRelocation <= 0 && active) {
            relocate();
        }
        
        
        if(g.type == GameType.HYDROPHOBIA) {
            HChamber chamber = (HChamber) g.getNight().env();
            if(chamber.getRoom() <= 1)
                return;
//            if(chamber.timer.x < 1480)
//                return;
        }
        if(g.getNight().getBeast().isActive())
            return;

        arrivalSeconds--;
        if(arrivalSeconds == 0 && !active) {
            spawn();
        }
    }
    
    int untilRelocation = 10;
    

    
    public boolean active = false;

    public boolean isActive() {
        return active;
    }

    public float getRage() {
        return rage;
    }

    public void addRage(float add) {
        this.rage = Math.max(0, this.rage + add);
    }

    public int getX() {
        return x;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void addRadius(int add) {
        this.radius = Math.max(0, this.radius + add);
    }

    public int getRadius() {
        return radius;
    }

    public void setUntilRelocation(int untilRelocation) {
        this.untilRelocation = untilRelocation;
    }

    public int getUntilRelocation() {
        return untilRelocation;
    }

    
    public void stopTimer() {
        if(beepTimer != null) {
            beepTimer.cancel();
            beepTimer = null;
        }
    }

    @Override
    public int getArrival() {
        return arrivalSeconds;
    }

    @Override
    public void fullReset() {
        disappear();
    }
}
