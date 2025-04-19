package enemies;

import game.Door;
import game.enviornments.Basement;
import main.GamePanel;
import utils.Pepitimer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ToleTole extends Enemy {
    int arrivalSeconds = 45 + (int) (Math.random() * 30);
    boolean active = false;
    
    float x = 0;
    int goalX = 0;
    
    public ToleTole(GamePanel panel) {
        super(panel);
    }
    
    float currentFunction = 0;

    public float speed = 1 * modifier;
    boolean aimingDoor = false;
    public boolean gonnaLeave = false;
    float alpha = 1;
    Door doorToAim = null;
    boolean flash = false;
    
    float currentSize = 1F;

    public void recalc() {
        x = (goalX + x * 19) / 20;
        currentFunction += 0.06F * speed;
        
        if(alpha > 0) {
            alpha -= 0.008F;
            if(alpha < 0) {
                alpha = 0;
            }
        }
        
        if(doorToAim != null) {
            currentSize = (currentSize * 19F + doorToAim.getVisualSize() * 0.8F) / 20F;
        }
        
        if(currentFunction >= 3.14) {
            currentFunction = 0;

            if(gonnaLeave) {
                if(doorToAim.isClosed()) {
                    g.sound.play("notPepitoReflect", 0.2);
                    leave();
                    
                    flash = true;
                    new Pepitimer(() -> flash = false, 80);

                    if(g.getNight().getType().isBasement()) {
                        Basement env = (Basement) g.getNight().env();
                        
                        env.addCoins(75 + (int) (Math.random() * 75));
                        
                        new Pepitimer(() -> {
                            g.sound.play("dabloonGet", 0.1);
                        }, 100);

                        GamePanel.toleToleKilled++;
                    }
                } else {
                    returnLater();
                }
            } else {
                g.sound.playRate("boing", 0.09, (speed / modifier));

                if(!aimingDoor) {
                    findDoor();
                }
            }
            
            if(aimingDoor && Math.abs(x - goalX) < 10 && Math.random() < 0.7) {
                if(doorToAim.isClosed()) {
                    findDoor();
                } else {
                    gonnaLeave = true;
                    alpha = 1;
                }
            }
        }
    }

    public boolean isAimingDoor() {
        return aimingDoor;
    }

    public boolean isGonnaLeave() {
        return gonnaLeave;
    }

    public Door getDoorToAim() {
        return doorToAim;
    }

    public float getCurrentSize() {
        return currentSize;
    }

    public boolean isFlash() {
        return flash;
    }

    public boolean isActive() {
        return active;
    }

    void returnLater() {
        leave();
        new Pepitimer(this::spawn, (int) (1000 + Math.random() * 2000));
    }
    
    public void findDoor() {
        if(gonnaLeave)
            return;
        
        List<Door> list = new ArrayList<>(g.getNight().getDoors().values());
        Collections.shuffle(list);
        Door door = list.get(0);

        if (Math.random() < 0.2 && !door.isClosed()) {
            goalX = door.getHitbox().getBounds().x + door.getHitbox().getBounds().width / 2 - 90;
            doorToAim = door;
            aimingDoor = true;
        } else {
            goalX = (int) (Math.random() * 1220);
            aimingDoor = false;
        }
    }
    
    public void spawn() {
        active = true;
        speed = 1 * modifier;
        gonnaLeave = false;
        alpha = 1;

        currentFunction = 0;

        List<Door> list = new ArrayList<>(g.getNight().getDoors().values());
        Collections.shuffle(list);
        Door door = list.get(0);
        
        if(door.isClosed()) {
            returnLater();
        }
        
        goalX = door.getHitbox().getBounds().x + door.getHitbox().getBounds().width / 2 - 90;
        x = goalX;
    }
    
    public void leave() {
        active = false;
        arrivalSeconds = 50 + (int) (Math.random() * 20);
        aimingDoor = false;
        doorToAim = null;
        gonnaLeave = false;
        alpha = 1;
    }

    public int getX() {
        return (int) x;
    }

    public int getGoalX() {
        return goalX;
    }

    public float getY() {
        return currentFunction;
    }

    public float getAlpha() {
        return alpha;
    }

    public void tick() {
        if(AI <= 0)
            return;

        arrivalSeconds--;
        if(arrivalSeconds == 0) {
            spawn();
            GamePanel.toleToleSpawned++;
        }
    }

    @Override
    public int getArrival() {
        return arrivalSeconds;
    }

    @Override
    public void fullReset() {
        leave();
    }
}
