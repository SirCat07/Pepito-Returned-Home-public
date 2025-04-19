package enemies;

import game.Door;
import game.Level;
import game.bingo.BingoHandler;
import game.bingo.BingoTask;
import game.enviornments.Basement;
import main.GamePanel;
import utils.GameType;
import utils.Pepitimer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class PepitoAndNotPepito {

    GamePanel g;
    public PepitoAndNotPepito(GamePanel panel) {
        g = panel;
    }

    public byte pepitoAI = 0;
    public byte notPepitoAI = 0;
    private float pepitoModifier = (int) Math.max(pepitoAI / 3.9 + 0.6, 1);
    private float notPepitoModifier = (int) Math.max(notPepitoAI / 3.9 + 0.6, 1);


    public byte pepitoStepsLeft = 5;
    public byte pepitoKnocksLeft = 4;
    public byte notPepitoRunsLeft = 2;
    public byte notPepitoKnocksLeft = (byte) (3 * notPepitoModifier);

    public byte seconds = (byte) ((Math.random() * 18 + 13) / pepitoModifier);
    private byte door = 0;

    public float notPepitoChance = 10F;
    public boolean isNotPepito = false;

    boolean isFirst = true;

    public void tick(float reconsideration) {
        if (seconds > 0) {
            if(seconds == 1) {
                List<Integer> list = new ArrayList<>(g.getNight().getDoors().keySet());
                Collections.shuffle(list);
                door = (byte) (int) (list.get(0));
            } else if(seconds == 2) {
                
                boolean basementEndingEncounter = false;
                
                if(g.getNight().getType().isBasement()) {
                    Basement basement = (Basement) g.getNight().env();
                    basementEndingEncounter = basement.getStage() == 5;
                }
                
                if((notPepitoAI > 0 && (Math.random() * 100 / (notPepitoChance + notPepitoModifier) <= 0.5 || pepitoAI <= 0) && !isFirst) || basementEndingEncounter) {
                    isNotPepito = true;
                    g.sound.play("notPepitoSound", 0.4);
                    
                    notPepitoRunsLeft++;
                    
                    if(g.sensor.isEnabled()) {
                        g.console.add(GamePanel.getString("sensorNotPepito"), 3);
                    }
                }
            }
            
            
            if(pepitoAI > 0 || notPepitoAI > 0) {
                if(g.getNight().getShock().isDoom()) {
                    reconsideration += 0.9F;
                }
                if(reconsideration >= 1) {
                    float chance = 1F - pepitoAI * 0.1F;
                    if(isNotPepito) chance = 1F - notPepitoAI * 0.1F;
                    float progress = 1 + (float) (g.getNight().seconds - g.getNight().secondsAtStart) / g.getNight().getDuration();
                    chance /= progress;

                    if(Math.random() < chance) {
                        System.out.println(getClass().getName() + " reconsidered! | chance: " + chance + " | isNotPepito: " + isNotPepito);
                        return;
                    }
                }
                
                seconds--;
            }
        } else if (seconds == 0) {
            if(g.getNight().getA90().animation == 0 && !g.getNight().getA90().isActive()) {
                if (isNotPepito) {
                    tickNotPepito();
                } else {
                    if(pepitoAI > 0) {
                        tickPepito();
                    }
                }
            }
        } else {
            // seconds < 0
            if(pepitoAI > 0) {
                if (pepitoStepsLeft > 4 && !isNotPepito) {
                    if ((int) Math.round(Math.random() * 3) == 0) {
                        g.console.add(GamePanel.getString("sensorPepito"));
                    }
                }
            }
        }

        pepitoScareSeconds = (byte) Math.max(0, pepitoScareSeconds - 1);
        notPepitoScareSeconds = (byte) Math.max(0, notPepitoScareSeconds - 1);
    }

    int flicker = 0;

    private void tickNotPepito() {
        boolean basementEndingEncounter = false;

        if(g.getNight().getType().isBasement()) {
            Basement basement = (Basement) g.getNight().env();
            basementEndingEncounter = basement.getStage() == 5;
        }
        
        if (notPepitoRunsLeft <= 0) {
            flicker = 0;
            if (!(g.getNight().getDoors().get((int) door).isLocked())) {
                new Pepitimer(() -> {
                    if(!(g.getNight().getDoors().get((int) door).isLocked())) {
                        g.jumpscare("notPepito", g.getNight().getId());
                    } else {
                        g.sound.play("notPepitoReflect", 0.2);
                    }
                }, basementEndingEncounter ? 500 : 200);
            } else {
                notPepitoKnocksLeft--;

                knock(1.5F);

                if (notPepitoKnocksLeft < 0) {
                    scare();
                }
            }
        } else {
            flicker = 60;
            Door d = g.getNight().getDoors().get((int) door);
            double volume = 0.08 + ((2 - notPepitoKnocksLeft) / 100F);
            if(basementEndingEncounter) volume += 0.045;
            
            g.sound.play("notPepitoRun", volume, Level.getDoorPan(d, g.getNight().getType()));
            g.getNight().addEventPercent(0.2F);

            notPepitoRunsLeft--;
        }
    }

    private void tickPepito() {
        if (pepitoStepsLeft <= 0) {
            if (!(g.getNight().getDoors().get((int) door).isLocked())) {
                g.jumpscare("pepito", g.getNight().getId());
            } else {
                pepitoKnocksLeft--;

                knock(0.75F);

                if (pepitoKnocksLeft < 0) {
                    scare();
                }
            }
        } else {
            if ((byte) Math.round(Math.random() * 2) <= 1 || isFirst) {
                if (pepitoStepsLeft <= 4) {
                    Door d = g.getNight().getDoors().get((int) door);
                    g.sound.play("pepitoWalk", 0.03 + ((4 - pepitoStepsLeft) / 150F), Level.getDoorPan(d, g.getNight().getType()));
                }
                g.getNight().addEventPercent(0.05F);

                pepitoStepsLeft--;
            }
        }
    }

    private void knock(float energyLoss) {
        if(g.getNight().getDoors().get((int) door).getBlockade() > 0) {
            g.sound.play("blockadeHit", 0.1, (door - 0.5) / 3);
            g.getNight().getDoors().get((int) door).addBlockade(-1);

            if(g.getNight().getDoors().get((int) door).getBlockade() == 0) {
                g.sound.play("blockadeBreak", 0.1);
                g.repaintOffice();
            }
        } else if(g.getNight().getDoors().get((int) door).isClosed()) {
            g.sound.play("knock", 0.05, Level.getDoorPan(g.getNight().getDoors().get((int) door), g.getNight().getType()));
            g.getNight().addEnergy(-energyLoss);
        }
    }

    public byte pepitoScareSeconds = 0;
    public byte notPepitoScareSeconds = 0;

    byte pepitoDefaultScareSeconds = 7;
    byte notPepitoDefaultScareSeconds = 6;

    public void scare() {
        if(seconds == 0) {
            if(isNotPepito) {
                resetNotPepito();
                seconds = (byte) ( (((Math.random() * 18 + 12) / pepitoModifier) + (14F / (pepitoModifier * 2))) * g.getNight().heatDistort());
                notPepitoScareSeconds = notPepitoDefaultScareSeconds;
                isNotPepito = false;

                g.camLayer0 = g.camStates[4];
                flicker = 0;
            } else {
                resetPepito();
                seconds = (byte) ( (((Math.random() * 18 + 14) / pepitoModifier) + (20F / (pepitoModifier * 2))) * g.getNight().heatDistort());
                pepitoScareSeconds = pepitoDefaultScareSeconds;

                if (g.sensor.isEnabled()) {
                    if ((byte) Math.round(Math.random() * 5) != 0) {
                        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                        g.console.add(GamePanel.getString("sensorPepitoLeave").replace("%d%", timeStamp));
                    }
                }

                notPepitoChance += 0.8F;

                g.camLayer0 = g.camStates[2];

                BingoHandler.completeTask(BingoTask.SURVIVE_PEPITO);
            }
            if(g.getNight().isBlizzardModifier()) {
                if(g.getNight().getBlizzardTime() > 0) {
                    seconds = 1;
                }
            }
            
            if(g.getNight().getType().isBasement()) {
                Basement basement = (Basement) g.getNight().env();
                if(basement.getStage() == 5) {
                    seconds = (byte) (Math.random() * 3 + 3);
                    
                    if(g.getNight().getType().isBasement()) {
                        new Pepitimer(() -> {
                            if(g.getNight().getType() == GameType.BASEMENT)
                                g.getNight().basementDoorBlocks();
                        }, 1500);
                        
                    }
                }
                if(isFirst && g.getNight().getType() == GameType.BASEMENT) {
                    new Pepitimer(() -> {
                        if(g.getNight().getType() == GameType.BASEMENT)
                            g.getNight().basementDoorBlocks();
                    }, 1500);
                    
                }
            }
            
            isFirst = false;
        }
    }

    public void setFlicker(int flicker) {
        this.flicker = flicker;
    }

    public byte getDoor() {
        return door;
    }

    public void setPepitoAI(int AIlevel) {
        pepitoAI = (byte) AIlevel;
        pepitoModifier = Math.max(pepitoAI / 4F + 0.6F, 1);
        resetPepito();
    }
    public void setNotPepitoAI(int AIlevel) {
        notPepitoAI = (byte) AIlevel;
        notPepitoModifier = Math.max(notPepitoAI / 4F + 0.6F, 1);
        resetNotPepito();
    }

    public void reset() {
        resetPepito();
        resetNotPepito();
    }
    public void resetPepito() {
        pepitoKnocksLeft = 4;
        pepitoStepsLeft = 5;
    }
    public void resetNotPepito() {
        notPepitoRunsLeft = 2;
        notPepitoKnocksLeft = (byte) (3 * notPepitoModifier);
    }

    public void glitcherReset() {
        if(g.getNight().getPepito().seconds == 0) {
            g.getNight().getPepito().reset();
        }
        seconds = (byte) (Math.random() * 22 / g.getNight().getPepito().pepitoModifier * g.getNight().heatDistort());

        if(g.getNight().isBlizzardModifier()) {
            if(g.getNight().getBlizzardTime() > 0) {
                seconds = 1;
            }
        }
    }

    public int getFlicker() {
        return flicker;
    }

    public boolean isEnabled() {
        return pepitoAI > 0 || notPepitoAI > 0;
    }
    
    Pepito pepitoHolder = new Pepito(g);
    NotPepito notPepitoHolder = new NotPepito(g);
    public Enemy getPepito() {
        return pepitoHolder;
    }
    public Enemy getNotPepito() {
        return notPepitoHolder;
    }
}

class Pepito extends Enemy {
    public Pepito(GamePanel panel) {
        super(panel);
    }
    @Override
    public int getArrival() {
        return -1;
    }
    @Override
    public void fullReset() {
        
    }
}

class NotPepito extends Enemy {
    public NotPepito(GamePanel panel) {
        super(panel);
    }
    @Override
    public int getArrival() {
        return -1;
    }
    @Override
    public void fullReset() {

    }
}