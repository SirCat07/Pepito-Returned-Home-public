package enemies;

import game.enviornments.HChamber;
import main.GamePanel;
import utils.GameType;
import utils.Pepitimer;

public class Beast extends Enemy {

    public short arrivalSeconds = (short) (10 + Math.random() * 10);
    
    public Beast(GamePanel panel) {
        super(panel);
    }
    
    boolean active = false;
    int x = 2860;


    public void spawn() {
        active = true;
        int speed;
        
        if(Math.random() < 0.5) {
            g.sound.play("beastWarnRight", 0.2, 0.5);
            speed = -12;
            x = 2860;
        } else {
            g.sound.play("beastWarnLeft", 0.2, -0.5);
            speed = 12;
            x = -1380;
        }
        
        new Pepitimer(() -> {
            g.getNight().setFlicker(100);
            g.sound.play("flicker" + (int) (Math.round(Math.random()) + 1), 0.05);
        }, 1000 + (int) (Math.random() * 3000));


        new Pepitimer(() -> {
            g.sound.play("beastRush", 0.15);

            new Pepitimer(() -> {
                g.everyFixedUpdate.put("beastAttack", () -> {
                    boolean kill = true;
                    if (g.type == GameType.HYDROPHOBIA) {
                        HChamber chamber = (HChamber) g.getNight().env();

                        kill = AI > 0;

                        if(kill) {
                            if (chamber.getShake() < 60) {
                                chamber.setShake(chamber.getShake() + 1);
                            }
                        }
                    }

                    if(kill) {
                        x += speed;

                        int pos = g.offsetX - g.getNight().env().maxOffset() + x;

                        if (pos > -440 && pos < 1040 && !g.inLocker) {
                            g.jumpscare("beast", g.getNight().getId());
                        }
                        if (x > 2860 || x < -1380) {
                            g.everyFixedUpdate.remove("beastAttack");

                            active = false;
                            arrivalSeconds = (short) (20 + Math.random() * 20);
                        }
                    } else {
                        g.everyFixedUpdate.remove("beastAttack");
                    }
                });
            }, 800);
        }, 2000 + (int) (Math.random() * 4000));
    }
    

    public void tick() {
        if(AI <= 0 || active)
            return;

        if (g.type == GameType.HYDROPHOBIA) {
            HChamber chamber = (HChamber) g.getNight().env();
            
            if(!chamber.hasLocker())
                return;
            if(chamber.getRoom() <= 0)
                return;
        }

        arrivalSeconds--;
        if(arrivalSeconds == 0) {
            spawn();
        }
    }


    public int getX() {
        return x;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public int getArrival() {
        return arrivalSeconds;
    }

    public void setX(int x) {
        this.x = x;
    }
    
    @Override
    public void fullReset() {
        g.everyFixedUpdate.remove("beastAttack");
        x = 2860;
    }
}
