package enemies;

import game.Level;
import game.bingo.BingoHandler;
import game.bingo.BingoTask;
import main.GamePanel;
import utils.GameEvent;
import utils.Pepitimer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class A90 extends Enemy {
    public byte arrivalSeconds = 40;
    public boolean active = false;
    public boolean dying = false;
    public byte animation = 0;

    public short x = 440;
    public short y = 230;

    public A90(GamePanel panel) {
        super(panel);
    }

    public boolean isActive() {
        return active;
    }

    byte shots = 0;

    public float forgive = 1;
    public short margin = 0;
    public boolean drawStopSign = false;

    // https://drive.google.com/file/d/1qHRAY4hassUPur9nKyMGsNqXMtxvCNp0/view?usp=sharing

    public List<Point> points = new ArrayList<>();
    public int distance = 0;

    public void spawn() {
        if(g.getNight().getScaryCat().isActive()) {
            arrivalSeconds += 3;
            return;
        }
        if (g.getNight().getEvent() == GameEvent.ASTARTA) {
            if (g.getNight().getAstartaBoss().getDyingStage() > 0) {
                return;
            }
        }

        g.getNight().addEventPercent(0.05F);

        points.clear();
        distance = 0;
        g.sound.pause();
        g.sound.playRate("a90Alert", 0.1, GamePanel.freezeModifier);
        x = (short) (Math.random() * 540 + 120);
        y = (short) (Math.random() * 100 + 120);
        animation = 1;

        g.everySecond20th.put("a90glitch", () -> {
            points.add(new Point((int) (x - 50 + Math.random() * 400), (int) (y + Math.random() * 300)));
        });

        Level night = g.getNight();

        if(night.getPepito().seconds == 0) {
            if(night.getPepito().isNotPepito) {
                if(night.getPepito().notPepitoRunsLeft != 0) {
                    night.getPepito().notPepitoRunsLeft++;
                }
            } else {
                if(night.getPepito().pepitoStepsLeft != 0) {
                    night.getPepito().pepitoStepsLeft++;
                }
            }
        }

        new Pepitimer(() -> {
            g.everyFixedUpdate.put("a90bg", () -> {
                distance += 30;
            });

            margin = 0;
            animation = 2;
            new Pepitimer(() -> {
                animation = 3;
                active = true;
                drawStopSign = true;
                g.sound.playRate("a90Arrive", 0.04, GamePanel.freezeModifier * night.heatDistort());
                new Pepitimer(() -> {
                    animation = 4;
                    new Pepitimer(() -> {
                        animation = 0;
                        g.everySecond20th.remove("a90glitch");

                        new Pepitimer(() -> {
                            animation = 4;
                            drawStopSign = false;
                            boolean forgiveConditions = (night.getMSI().isActive() || night.getShark().isActive()) && forgive > 0.7F;

                            for(int i = 0; i < 10; i++) {
                                points.add(new Point((int) (x - 75 + Math.random() * 300), (int) (y + Math.random() * 300)));
                            }

                            if(dying) {
                                g.everySecond20th.put("a90anim", () -> g.anim ^= 1);

                                if(forgiveConditions) {
                                    g.sound.playRate("a90FuckingDies", 0.05, GamePanel.freezeModifier * night.heatDistort());
                                    forgive -= 0.7F;
                                    margin = 70;

                                    BingoHandler.completeTask(BingoTask.A90_FORGIVE);

                                    new Pepitimer(() -> {
                                        animation = 0;
                                        g.everySecond20th.remove("a90anim");
                                    }, 350).affectByFreeze();
                                } else {
                                    g.sound.playRate("a90Dead", 0.08, GamePanel.freezeModifier * night.heatDistort());
                                    animation = 5;
                                    shots++;
                                    g.getNight().getShark().leftBeforeBite++;

                                    if (shots == 2) {
                                        g.jumpscare("a90");
                                    } else {
                                        new Pepitimer(() -> {
                                            animation = 0;
                                            g.everySecond20th.remove("a90anim");
                                        }, 2300).affectByFreeze();
                                    }
                                }
                            } else {
                                g.sound.playRate("a90Alive", 0.08, GamePanel.freezeModifier * night.heatDistort());
                                forgive = Math.min(forgive + 0.03F, 1);

                                BingoHandler.completeTask(BingoTask.SURVIVE_A90);
                                if(g.getNight().getMSI().isActive()) {
                                    BingoHandler.completeTask(BingoTask.SURVIVE_A90_WITH_MSI);
                                }

                                new Pepitimer(() -> {
                                    animation = 0;

                                    if(g.getNight().getMSI().isActive()) {
                                        forgive = Math.min(forgive + 0.1F, 1);
                                    }
                                }, 100).affectByFreeze();
                            }

                            g.sound.resume();
                            dying = false;
                            active = false;
                            arrivalSeconds = (byte) (((Math.random() * 84 * night.heatDistort() * night.heatDistort() + 9) / (modifier + 0.001) * night.heatDistort()) + 4);
                            g.getNight().getShark().leftBeforeBite++;
                            g.everyFixedUpdate.remove("a90bg");
                        }, 350).affectByFreeze();
                    }, 50).affectByFreeze();
                }, 70).affectByFreeze();
            }, (byte) (70 * night.heatDistort() / (modifier + 0.001))).affectByFreeze();
        }, (short) (500 * night.heatDistort() / Math.max(1, modifier / 1.5))).affectByFreeze();
    }

    public String forgiveText = "FORGIVE";

    public void tick() {
        if(AI <= 0)
            return;

        if (arrivalSeconds == 0) {
            spawn();

            if(!g.getNight().getEvent().isInGame()) {
                setAILevel(0);
            }
        }
        if (!active) {
            arrivalSeconds--;
        }
    }

    public boolean isDying() {
        return dying;
    }
}
