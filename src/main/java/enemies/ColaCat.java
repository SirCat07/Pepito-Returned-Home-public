package enemies;

import game.bingo.BingoHandler;
import game.bingo.BingoTask;
import main.GamePanel;
import utils.Pepitimer;
import utils.RepeatingPepitimer;

public class ColaCat extends Enemy {
    public byte arrivalSeconds = (byte) (Math.random() * 100 + 40 / modifier);
    public byte currentState = 0;

    public boolean active = false;

    Pepitimer timer;

    public ColaCat(GamePanel panel) {
        super(panel);
    }

    public boolean isActive() {
        return active;
    }

    void spawn() {
        currentState = 1;
        active = true;
        timer = new RepeatingPepitimer(() -> {
            g.getNight().addEventPercent(0.08F);

            currentState++;
            if(currentState > 2) {
                if (currentState >= 7) {
                    g.jumpscare("colaCat", g.getNight().getId());
                } else {
                    if (g.sensor.isEnabled()) {
                        if (Math.round(Math.random() * 2) == 0) {
                            g.console.add(GamePanel.getString("sensorSusSoda1"));
                            g.console.add(GamePanel.getString("sensorSusSoda2"));
                        }
                    }
                }
            }
        }, (short) (5000 / ((modifier + 0.1) / 1.5)), (short) (5000 / ((modifier + 0.1) / 1.5)));
    }
    
    
    int sinceLastMegaCola = 0;

    public void tick() {
        if(AI > 0) {
            if(g.soda.isEnabled()) {
                arrivalSeconds--;
                if(arrivalSeconds == 0) {
                    spawn();
                }
            }

            megaSodaWithheld--;
            if(megaSodaWithheld <= 0) {
                sinceLastMegaCola++;
                
                if((Math.random() < 0.006 || sinceLastMegaCola >= 120) && sinceLastMegaCola >= 6) {
                    spawnMegaCola();
                }
            }
        }
    }

    public void stopService() {
        try {
            timer.cancel();
        } catch (Exception ignored) { }
    }

    public void leave() {
        stopService();
        currentState = 0;

        BingoHandler.completeTask(BingoTask.SURVIVE_COLA_CAT);

        active = false;
    }
    
//    public float megaColaY = 1000;
    public float megaColaY = 12000;
    public float megaColaX = 0;
    public int megaSodaWithheld = 0;
    
    public void spawnMegaCola() {
        if(!g.megaSoda.isEnabled())
            return;
        if(g.getNight().megaSodaUses <= 0)
            return;
        if(megaColaY < 12000)
            return;
        
        g.sound.play("colaCatFly", 0.2);
        
        g.everyFixedUpdate.put("megaColaY", () -> {
            if(megaColaY > 0) {
                megaColaY /= 1.04F;
                megaColaY -= 0.05F;
            } else {
                megaColaY = 0;
                
                new Pepitimer(() -> {
                    // action
                    megaSodaWithheld = 20 + (int) (Math.random() * 20);
                    
                    g.everyFixedUpdate.put("megaColaX", () -> {
                        if(megaColaX < 1500) {
                            megaColaX++;
                            megaColaX *= 1.05F;

                            megaColaY *= 1.07F;
                            megaColaY += 0.1F;
                        } else {
                            g.everyFixedUpdate.remove("megaColaX");

                            megaColaY = 12000;
                            megaColaX = 0;
                            sinceLastMegaCola = 0;
                        }
                    });
                }, 500);

                g.everyFixedUpdate.remove("megaColaY");
            }
        });
    }

    @Override
    public int getArrival() {
        return arrivalSeconds;
    }

    @Override
    public void fullReset() {
        stopService();
        megaSodaWithheld = 0;
    }
}
