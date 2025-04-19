package enemies;

import game.Balloon;
import game.Level;
import game.bingo.BingoHandler;
import game.bingo.BingoTask;
import main.GamePanel;
import utils.GameEvent;
import utils.GameType;
import utils.Pepitimer;
import utils.RepeatingPepitimer;

import java.util.Locale;

public class MSI extends Enemy {
    public boolean active = false;
    public byte arrivalSeconds = (byte) ((Math.random() * 90 + 45) / modifier);
    public boolean left = true;

    public MSI(GamePanel panel, boolean isBirthday) {
        super(panel);

        crisscross = isBirthday || GamePanel.isAprilFools;
    }

    public boolean moved = false;
    public boolean movedWrong = false;
    public boolean arriving = false;
    public boolean firstAction = false;

    public boolean isHell = false;
    public boolean isShadow = false;
    public boolean crisscross;

    public void move(boolean movedLeft) {
        if(isHell) {
            movedLeft = !movedLeft;
        }

        if(!movedWrong) {
            moved = movedLeft == left;
            movedWrong = movedLeft != left;
        } else {
            g.fadeOutStatic(0.5F, 0, 0.02F);
        }
    }

    Rat the_rat_king;

    public void resetCounter() {
        if(g.sensor.isEnabled()) {
            arrivalSeconds = (byte) ((Math.random() * 80 + 36) / modifier);
        } else {
            arrivalSeconds = (byte) ((Math.random() * 135 + 65) / modifier);
        }
    }

    public boolean isActive() {
        return active;
    }

    RepeatingPepitimer timer;

    public void spawn() {
        Level night = g.getNight();
        
        if(night.getBoykisser().isActive() && !night.getBoykisser().isAwaitingResponse()) {
            g.getNight().getBoykisser().leave();
        }
        
        g.sound.play("msiArrival", 0.15);
        additionalTint = 0;
        arriving = true;
        killed = false;

        night.addEventPercent(0.3F);

        g.getNight().cancelAfterGame.add(new Pepitimer(() -> {
            if(night.getEvent() == GameEvent.NONE || night.getEvent() == GameEvent.ASTARTA || night.getEvent() == GameEvent.ENDING_BASEMENT) {
                additionalTint = 50;
                shake = 10;
                new Pepitimer(() -> {
                    shake = 0;
                }, 60);

                active = true;
                firstAction = true;

                if(night.getEvent() != GameEvent.ASTARTA && night.getEvent() != GameEvent.ENDING_BASEMENT && !night.getType().isEndless()) {
                    byte hell = (byte) (Math.random() * 153);
                    if (hell == 1) {
                        isHell = true;
                    }
                }

                final byte[] leftRight = {0};
                byte untilStop = (byte) (Math.round(Math.random() * 3 * modifier) + 6 + (AI / 2));

                if(isShadow) {
                    isHell = false;

                    if(night.getType() == GameType.SHADOW) {
                        untilStop = (byte) (Math.round(Math.random() * 3) + 6);
                    } else {
                        untilStop = 127;
                        leftRight[0] = -128;
                    }
                }

                if(g.offsetX < 5 || g.offsetX > 355) {
                    g.offsetX = 200;
                    g.onResizeEvent();
                }

                arriving = false;

                if(left) {
                    g.sound.playButPan(!isShadow ? "left" : "tfel", 0.09, -0.2);

                    for(Balloon balloon : GamePanel.balloons) {
                        balloon.goLeft();
                    }
                } else {
                    g.sound.playButPan(!isShadow ? "right" : "thgir", 0.09, 0.2);

                    for(Balloon balloon : GamePanel.balloons) {
                        balloon.goRight();
                    }
                }

                float d = modifier;
                if(AI > 3) {
                    d += 0.3F;
                }

                short interval = (short) (1900 / Math.min(d, 2.6));

                byte finalUntilStop = untilStop;
                timer = new RepeatingPepitimer(() -> {
                    leftRight[0]++;
                    firstAction = false;

                    if(leftRight[0] >= finalUntilStop || !active || !(night.getEvent() == GameEvent.NONE || night.getEvent() == GameEvent.ASTARTA || night.getEvent() == GameEvent.ENDING_BASEMENT)) {
                        if(leftRight[0] >= finalUntilStop || g.getNight().getEvent() == GameEvent.WINNING) {
                            BingoHandler.completeTask(BingoTask.SURVIVE_MSI);
                        }
                        timer.cancel(true);
                        left = true;
                        active = false;
                        g.sound.play("msiOut", 0.08);
                        resetCounter();

                        movedWrong = false;
                        moved = false;

                        if(g.adblocker.isEnabled()) {
                            g.randomCharacter = "qwertyuiopfghjzxvbn2345789".split("")[(int) Math.round(Math.random() * 25)];

                            g.console.add(GamePanel.getString("sensorMsiAdblocker1"));
                            g.console.add(GamePanel.getString("sensorMsiAdblocker2").replace("%d%", g.randomCharacter.toUpperCase(Locale.ROOT)));
                            g.adBlocked = true;
                        }
                    } else {
                        additionalTint += (short) (60 + (Math.max(0, Math.min(leftRight[0], 5)) * 5) - AI * 5);
                        shake = (byte) (10 + Math.abs(leftRight[0]) * 2);
                        new Pepitimer(() -> {
                            shake = 0;
                        }, 60);

                        if(moved) {
                            left = !left;
                            if(AI >= 5) {
                                if(AI == 5 || AI == 6) {
                                    if((byte) (Math.round(Math.random() * 7)) == 0) {
                                        left = !left;
                                    }
                                } else {
                                    if((byte) (Math.round(Math.random() * 3)) == 0) {
                                        left = !left;
                                    }
                                }
                            }

                            night.getA90().forgive = Math.min(night.getA90().forgive + 0.01F, 1);

                            g.keyHandler.mouseHeld = false;

                            if (left) {
                                g.sound.playButPan(!isShadow ? "left" : "tfel", 0.08, -0.4);

                                for(Balloon balloon : GamePanel.balloons) {
                                    balloon.goLeft();
                                }
                            } else {
                                g.sound.playButPan(!isShadow ? "right" : "thgir", 0.08, 0.4);

                                for(Balloon balloon : GamePanel.balloons) {
                                    balloon.goRight();
                                }
                            }

                            if(g.offsetX == 0 || g.offsetX == 400) {
                                g.offsetX = 200;
                            }
                        } else if(!killed) {
                            g.jumpscare("msi", g.getNight().getId());
                        }
                    }
                    moved = false;
                    movedWrong = false;
                }, (short) (Math.max(2200 / d, 800)), interval);
                
                g.getNight().cancelAfterGame.add(timer);
            } else {
                arriving = false;
            }
        }, 1800));
    }

    public void tick(boolean isShadow) {
        if(AI <= 0)
            return;

        arrivalSeconds--;
        if(arrivalSeconds == 0) {
            if(isShadow) {
                this.isShadow = true;
            }

            spawn();
        }
    }

    public boolean killed = false;

    public void kill(boolean removeSensor, boolean glitcher) {
        killed = true;

        if(g.getNight().getEvent() != GameEvent.ASTARTA && glitcher) {
            if (g.getNight().getGlitcher().isEnabled()) {
                g.getNight().getGlitcher().spawn();
            }
        }
        g.sound.play("msiKill", 0.15);

        BingoHandler.completeTask(BingoTask.SURVIVE_MSI);
        BingoHandler.completeTask(BingoTask.FLASH_MSI);

        if(g.sensor.isEnabled()) {
            g.console.add(GamePanel.getString("sensorMsiKill1"));
            g.console.add("=(");
            g.console.add("=(");
            g.console.add("=D");
            g.console.add("=(");
            g.console.add(GamePanel.getString("sensorMsiKill2"));
            g.console.add(GamePanel.getString("sensorMsiKill3"));
        }

        timer.cancel(true);

        new Pepitimer(() -> {
            left = true;
            active = false;
            g.sound.play("msiOut", 0.08);
            resetCounter();

            movedWrong = false;
            moved = false;

            if(removeSensor) {
                g.sensor.disable();
            }
            g.repaintOffice();

            killed = false;
        }, 3800);
    }

    public void quickKill(boolean sound) {
        if(timer != null) {
            timer.cancel(true);
        }
        
        left = true;
        active = false;
        if(sound) {
            g.sound.play("msiOut", 0.08);
        }
        resetCounter();

        movedWrong = false;
        moved = false;

        g.repaintOffice();
    }

    public void disappearShadow() {
        if(!active)
            return;
        if(!isShadow)
            return;
        if(g.getNight().getType() == GameType.SHADOW)
            return;

        left = true;
        active = false;
        g.sound.play("msiOut", 0.08);
        resetCounter();

        movedWrong = false;
        moved = false;

        g.repaintOffice();
        isShadow = false;
    }

    short additionalTint = 0;
    public short getAdditionalTint() {
        return additionalTint;
    }
    public void setAdditionalTint(short i) {
        additionalTint = i;
    }

    byte shake = 0;
    public byte getShake() {
        return shake;
    }
    
    @Override
    public int getArrival() {
        return arrivalSeconds;
    }

    @Override
    public void fullReset() {
        quickKill(false);
    }
}
