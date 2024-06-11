package enemies;

import game.Door;
import game.bingo.BingoHandler;
import game.bingo.BingoTask;
import main.GamePanel;
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

    private byte pepitoAI = 0;
    private byte notPepitoAI = 0;
    private float pepitoModifier = (int) Math.max(pepitoAI / 3.9 + 0.6, 1);
    private float notPepitoModifier = (int) Math.max(notPepitoAI / 3.9 + 0.6, 1);


    public byte pepitoStepsLeft = 5;
    public byte pepitoKnocksLeft = 4;
    public byte notPepitoRunsLeft = 2;
    public byte notPepitoKnocksLeft = (byte) (3 * notPepitoModifier);

    public byte seconds = (byte) ((Math.random() * 12 + 14) / pepitoModifier);
    private byte door = 0;

    public float notPepitoChance = 10F;
    public boolean isNotPepito = false;


    public void tick() {
        if (seconds > 0) {
            if(seconds == 1) {
                List<Integer> list = new ArrayList<>(g.getNight().getDoors().keySet());
                Collections.shuffle(list);
                door = (byte) (int) (list.get(0));
            } else if(seconds == 2) {
                if(notPepitoAI > 0 && (Math.random() * 100 / (notPepitoChance + notPepitoModifier) <= 0.5 || pepitoAI <= 0)) {
                    isNotPepito = true;
                    g.sound.play("notPepitoSound", 0.4);

                    if(g.sensor.isEnabled()) {
                        g.console.add("NOT PEPITO ALERT ! ! !", 3);
                    }
                }
            }

            if(pepitoAI > 0 || notPepitoAI > 0) {
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
                        g.console.add("pépito appeared!");
                    }
                }
            }
        }

        pepitoScareSeconds = (byte) Math.max(0, pepitoScareSeconds - 1);
        notPepitoScareSeconds = (byte) Math.max(0, notPepitoScareSeconds - 1);
    }

    int flicker = 0;

    private void tickNotPepito() {
        if (notPepitoRunsLeft <= 0) {
            flicker = 0;
            if (!(g.getNight().getDoors().get((int) door).isClosed() || g.getNight().getDoors().get((int) door).getBlockade() > 0)) {
                new Pepitimer(() -> {
                    if(!(g.getNight().getDoors().get((int) door).isClosed() || g.getNight().getDoors().get((int) door).getBlockade() > 0)) {
                        g.jumpscare("notPepito");
                    } else {
                        g.sound.play("notPepitoReflect", 0.2);
                    }
                }, 200);
            } else {
                notPepitoKnocksLeft--;

                knock(2F, 2);

                if (notPepitoKnocksLeft < 0) {
                    scare();
                }
            }
        } else {
            flicker = 60;
            Door d = g.getNight().getDoors().get((int) door);
            int doorPos = (int) (d.getHitbox().getBounds().x + d.getHitbox().getBounds().width / 2F);
            float pan = (float) Math.sin(1.57 * (doorPos / 740F - 1)) / 1.3F;
            g.sound.play("notPepitoRun", 0.08 + ((2 - notPepitoKnocksLeft) / 100F), pan);
            g.getNight().addEventPercent(0.2F);

            notPepitoRunsLeft--;
        }
    }

    private void tickPepito() {
        if (pepitoStepsLeft <= 0) {
            if (!(g.getNight().getDoors().get((int) door).isClosed() || g.getNight().getDoors().get((int) door).getBlockade() > 0)) {
                g.jumpscare("pepito");
            } else {
                pepitoKnocksLeft--;

                knock(1F, 3);

                if (pepitoKnocksLeft < 0) {
                    scare();
                }
            }
        } else {
            if ((byte) Math.round(Math.random() * 2) <= 1) {
                if (pepitoStepsLeft <= 4) {
                    Door d = g.getNight().getDoors().get((int) door);
                    int doorPos = (int) (d.getHitbox().getBounds().x + d.getHitbox().getBounds().width / 2F);
                    float pan = (float) Math.sin(1.57 * (doorPos / 740F - 1)) / 1.3F;
                    g.sound.play("pepitoWalk", 0.03 + ((4 - pepitoStepsLeft) / 150F), pan);
                }
                g.getNight().addEventPercent(0.05F);

                pepitoStepsLeft--;
            }
        }
    }

    private void knock(float energyLoss, int divide) {
        if(g.getNight().getDoors().get((int) door).getBlockade() > 0) {
            g.sound.play("blockadeHit", 0.1, (door - 0.5) / 3);
            g.getNight().getDoors().get((int) door).addBlockade(-1);

            if(g.getNight().getDoors().get((int) door).getBlockade() == 0) {
                g.sound.play("blockadeBreak", 0.1);
                g.repaintOffice();
            }
        } else if(g.getNight().getDoors().get((int) door).isClosed()) {
            g.sound.play("knock", 0.05, (door - 0.5) / divide);
            g.getNight().addEnergy(-energyLoss);
        }
    }

    public byte pepitoScareSeconds = 0;
    public byte notPepitoScareSeconds = 0;

    byte pepitoDefaultScareSeconds = 6;
    byte notPepitoDefaultScareSeconds = 5;

    public void scare() {
        if(seconds == 0) {
            if(isNotPepito) {
                resetNotPepito();
                seconds = (byte) ( (((Math.random() * 19 + 14) / pepitoModifier) + (14F / (pepitoModifier * 2))) * g.getNight().heatDistort());
                notPepitoScareSeconds = notPepitoDefaultScareSeconds;
                isNotPepito = false;

                g.cam = g.camStates[4];
            } else {
                resetPepito();
                seconds = (byte) ( (((Math.random() * 17 + 16) / pepitoModifier) + (20F / (pepitoModifier * 2))) * g.getNight().heatDistort());
                pepitoScareSeconds = pepitoDefaultScareSeconds;

                if (g.sensor.isEnabled()) {
                    if ((byte) Math.round(Math.random() * 5) != 0) {
                        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                        g.console.add("Pepito is out (" + timeStamp + ")");
                    }
                }

                notPepitoChance += 0.8F;

                g.cam = g.camStates[2];

                BingoHandler.completeTask(BingoTask.SURVIVE_PEPITO);
            }
            if(g.getNight().isBlizzardModifier()) {
                if(g.getNight().getBlizzardTime() > 0) {
                    seconds = 1;
                }
            }
        }
    }

    public void setPepitoAI(byte AIlevel) {
        pepitoAI = AIlevel;
        pepitoModifier = (float) Math.max(pepitoAI / 3.9 + 0.6, 1);
        resetPepito();
    }
    public void setNotPepitoAI(byte AIlevel) {
        notPepitoAI = AIlevel;
        notPepitoModifier = (float) Math.max(notPepitoAI / 3.9 + 0.6, 1);
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
        seconds = (byte) (Math.random() * 24 / g.getNight().getPepito().pepitoModifier * g.getNight().heatDistort());

        if(g.getNight().isBlizzardModifier()) {
            if(g.getNight().getBlizzardTime() > 0) {
                seconds = 1;
            }
        }
    }

    public int getFlicker() {
        return flicker;
    }
}
