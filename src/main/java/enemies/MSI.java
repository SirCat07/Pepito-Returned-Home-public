package enemies;

import game.bingo.BingoHandler;
import game.bingo.BingoTask;
import main.GamePanel;
import utils.GameEvent;
import utils.GameType;
import utils.Pepitimer;
import utils.RepeatingPepitimer;

import java.util.Calendar;
import java.util.Locale;

public class MSI extends Enemy {
    public boolean active = false;
    public byte arrivalSeconds = (byte) ((Math.random() * 80 + 30) / modifier);
    public boolean left = true;

    public MSI(GamePanel panel, boolean isBirthday) {
        super(panel);

        crisscross = isBirthday;

        Calendar calendar = Calendar.getInstance();
        if(calendar.get(Calendar.MONTH) == Calendar.APRIL) {
            if(calendar.get(Calendar.DAY_OF_MONTH) <= 7) {
                crisscross = true;
            }
        }
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
        arrivalSeconds = (byte) ((Math.random() * 80 + 30) / modifier);
    }

    public boolean isActive() {
        return active;
    }

    RepeatingPepitimer timer;

    public void spawn() {
        g.sound.playRate("msiArrival", 0.15, GamePanel.freezeModifier);
        additionalTint = 0;
        arriving = true;
        killed = false;

        g.getNight().addEventPercent(0.3F);

        new Pepitimer(() -> {
            if(g.getNight().getEvent() == GameEvent.NONE || g.getNight().getEvent() == GameEvent.ASTARTA) {
                additionalTint = 50;
                shake = 10;
                new Pepitimer(() -> {
                    shake = 0;
                }, 60);

                active = true;
                firstAction = true;

                if(g.getNight().getEvent() != GameEvent.ASTARTA && !g.getNight().getType().isEndless()) {
                    byte hell = (byte) (Math.random() * 153);
                    if (hell == 1) {
                        isHell = true;
                    }
                }

                final byte[] leftRight = {0};
                byte untilStop = (byte) (Math.round(Math.random() * 3 * modifier) + 7 + (AI / 2));

                if(isShadow) {
                    isHell = false;

                    if(g.getNight().getType() == GameType.SHADOW) {
                        untilStop = (byte) (Math.round(Math.random() * 3) + 7);
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
                    g.sound.playButPanWithFreeze(!isShadow ? "left" : "tfel", 0.09, -0.2);
                } else {
                    g.sound.playButPanWithFreeze(!isShadow ? "right" : "thgir", 0.09, 0.2);
                }

                float d = modifier;
                if(AI > 3) {
                    d += 0.3F;
                }

                short interval = (short) (1600 / Math.min(d, 2.6));

                byte finalUntilStop = untilStop;
                timer = new RepeatingPepitimer(() -> {
                    leftRight[0]++;
                    firstAction = false;

                    if(leftRight[0] >= finalUntilStop || !active || !(g.getNight().getEvent() == GameEvent.NONE || g.getNight().getEvent() == GameEvent.ASTARTA)) {
                        if(leftRight[0] >= finalUntilStop || g.getNight().getEvent() == GameEvent.WINNING) {
                            BingoHandler.completeTask(BingoTask.SURVIVE_MSI);
                        }
                        timer.cancel(true);
                        left = true;
                        active = false;
                        g.sound.playRate("msiOut", 0.08, GamePanel.freezeModifier);
                        resetCounter();

                        movedWrong = false;
                        moved = false;

                        if(g.adblocker.isEnabled()) {
                            g.randomCharacter = "qwertyuiopfghjzxvbn2345789".split("")[(int) Math.round(Math.random() * 25)];

                            g.console.add("MSI has disabled your adblocker!");
                            g.console.add("Press " + g.randomCharacter.toUpperCase(Locale.ROOT) + " to restart your adblocker.");
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

                            g.getNight().getA90().forgive = Math.min(g.getNight().getA90().forgive + 0.01F, 1);

                            g.keyHandler.mouseHeld = false;

                            if (left) {
                                g.sound.playButPanWithFreeze(!isShadow ? "left" : "tfel", 0.08, -0.4);
                            } else {
                                g.sound.playButPanWithFreeze(!isShadow ? "right" : "thgir", 0.08, 0.4);
                            }

                            if(g.offsetX == 0 || g.offsetX == 400) {
                                g.offsetX = 200;
                            }
                        } else if(!killed) {
                            g.jumpscare("msi");
                        }
                    }
                    moved = false;
                    movedWrong = false;
                }, (short) (Math.max(2000 / d, 800)), interval);

                timer.affectByFreeze();
            }
        }, 1800).affectByFreeze();
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

    boolean killed = false;

    public void kill(boolean removeSensor) {
        killed = true;

        if(g.getNight().getEvent() != GameEvent.ASTARTA) {
            if (g.getNight().getGlitcher().isEnabled()) {
                g.getNight().getGlitcher().spawn();
            }
        }
        g.sound.play("msiKill", 0.15);

        BingoHandler.completeTask(BingoTask.SURVIVE_MSI);
        BingoHandler.completeTask(BingoTask.FLASH_MSI);

        if(g.sensor.isEnabled()) {
            g.console.add("oopsies, your program crashed");
            g.console.add("=(");
            g.console.add("=(");
            g.console.add("=D");
            g.console.add("=(");
            g.console.add("disabling SENSOR");
            g.console.add("disabling MSI");
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

    public void quickKill() {
        timer.cancel(true);

        left = true;
        active = false;
        g.sound.play("msiOut", 0.08);
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
}
