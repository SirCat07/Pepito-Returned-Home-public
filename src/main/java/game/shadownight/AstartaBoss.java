package game.shadownight;

import enemies.A90;
import game.achievements.AchievementHandler;
import game.achievements.Achievements;
import javafx.scene.media.MediaPlayer;
import main.GamePanel;
import utils.GameState;
import utils.Pepitimer;
import utils.RepeatingPepitimer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AstartaBoss {
    float startCutsceneY = 640;
    boolean isStartingCutscene = false;
    boolean isFighting = false;

    List<AstartaMinecart> minecarts = new ArrayList<>();

    GamePanel g;
    public AstartaBoss(GamePanel g) {
        this.g = g;
        mister = new Mister(this);

        rouletteResults.add((byte) 0);
        rouletteResults.add((byte) 1);
        rouletteResults.add((byte) 2);
        Collections.shuffle(rouletteResults);
    }


    public void start() {
        isStartingCutscene = true;
        g.music.stop();
        g.sound.play("astartaSiren", 0.1);
        g.everyFixedUpdate.put("astartaStartingCutscene", () -> {
            if(startCutsceneY > 0) {
                decreaseStartCutscene();
            } else {
                startBoss();
            }
        });

        final boolean[] isEveryOtherSecond = {false};
        final boolean[] fadePhase = {false};
        final boolean[] isFirstFade = {true};

        g.everySecond.put("astartaStartingBlinking", () -> {
            isEveryOtherSecond[0] = !isEveryOtherSecond[0];
            if(isEveryOtherSecond[0]) {
                fadePhase[0] = !fadePhase[0];
                if(fadePhase[0]) {
                    if(isFirstFade[0]) {
                        g.fadeOut((int) g.tintAlpha, 255, 2);
                        isFirstFade[0] = false;
                    } else {
                        g.fadeOut(0, 255, 2);
                    }
                } else {
                    g.fadeOut(255, 0, 2);
                }
            }
        });
    }

    public int getStartCutsceneY() {
        return (int) startCutsceneY;
    }

    public void decreaseStartCutscene() {
        startCutsceneY -= 0.35F;
    }

    public void skipStartCutscene() {
        startCutsceneY = 0;

        startBoss();
    }

    int minecartYadd = 100;

    public int getMinecartYadd() {
        return minecartYadd;
    }


    private void startBoss() {
        g.everyFixedUpdate.remove("astartaStartingCutscene");
        g.everySecond.remove("astartaStartingBlinking");
        g.fadeOut((int) g.tintAlpha, 0, 1);
        g.getNight().setEnergy(503F);
        g.getNight().setMaxEnergy(503F);

        new Pepitimer(() -> {
            g.sound.stop();
            g.sound.play("boop", 0.1);
            isStartingCutscene = false;

            new Pepitimer(() -> {
                minecarts.clear();
                for(int i = 0; i < 12; i++) {
                    minecarts.add(new AstartaMinecart(i * -500 + 1080));
                }
                Collections.reverse(minecarts);
                minecartYadd = 100;

                health = 200;

                isFighting = true;
                g.music.play("astartaFight", 0.06, true);

                g.everyFixedUpdate.put("astartaMinecartEnter", () -> {
                    minecartYadd--;

                    if(minecartYadd <= 0) {
                        g.everyFixedUpdate.remove("astartaMinecartEnter");
                    }
                });
                g.everyFixedUpdate.put("astartaMinecartMove", () -> {
                    A90 a90 = g.getNight().getA90();
                    if(a90.isActive())
                        return;

                    for(AstartaMinecart minecart : minecarts) {
                        if(roulette) {
                            minecart.move((int) (10 * speedModifier));
                            continue;
                        }
                        minecart.move((int) (20 * speedModifier));

                        if(minecart.shine > 0) {
                            minecart.shine -= 0.01F;
                        }

                        if(minecart.getX() > g.offsetX - 400 && minecart.getX() < g.offsetX - 400 + 1480 && minecart.getItem() != AstartaMinecartItem.NONE) {
                            if(canUseItem(minecart.getItem()) && minecart.getShine() <= 0) {
                                minecart.shine = 0.8F;
                            }

                            new Pepitimer(() -> {
                                if(canUseItem(minecart.getItem())) {
                                    switch (minecart.getItem()) {
                                        case MSI -> {
                                            if(msiCooldown <= 0 && scaryCatCooldown <= 10 && uncannyBoxes.isEmpty()) {
                                                minecart.spawn(this, g);
                                                msiCooldown = 25;
                                            }
                                        }
                                        case SCARYCAT -> {
                                            if(scaryCatCooldown <= 0 && msiCooldown <= 10 && uncannyBoxes.isEmpty()) {
                                                minecart.spawn(this, g);
                                                scaryCatCooldown = 30;
                                            }
                                        }
                                        case SODA -> {
                                            if(!g.soda.isEnabled()) {
                                                minecart.spawn(this, g);
                                            }
                                        }
                                        case MINISODA -> {
                                            if(!g.miniSoda.isEnabled()) {
                                                minecart.spawn(this, g);
                                            }
                                        }
                                        case SOUP -> {
                                            if(!g.soup.isEnabled()) {
                                                minecart.spawn(this, g);
                                            }
                                        }
                                        default -> {
                                            minecart.spawn(this, g);
                                        }
                                    }
                                }
                            }, 800);
                        }
                    }
                    if(minecarts.get(0).getX() >= 0) {
                       for(AstartaMinecart cart : minecarts) {
                           cart.move(-4000);
                       }
                       for(int i = 3; i < minecarts.size() - 3; i++) {
                           AstartaMinecart cart = minecarts.get(i);
                           cart.setItem(AstartaMinecartItem.NONE);

                           if(Math.random() < 0.4) {
                               int displayX = g.offsetX - 400 + cart.getX();

                               if(displayX < -1000) {
                                   List<AstartaMinecartItem> list = new ArrayList<>(List.of(AstartaMinecartItem.values()));
                                   Collections.shuffle(list);
                                   AstartaMinecartItem item = list.get(0);

                                   // rerolls
                                   if (Math.random() < 0.25) {
                                       while (item.isBuff()) {
                                           list.add(AstartaMinecartItem.NONE);
                                           Collections.shuffle(list);
                                           item = list.get(0);
                                       }
                                   }
                                   // rerolls
                                   if(item == AstartaMinecartItem.MISTER) {
                                       if(Math.random() < 0.3) {
                                           list.add(AstartaMinecartItem.NONE);
                                           Collections.shuffle(list);
                                           item = list.get(0);
                                       }
                                   }

                                   // cannot set item
                                   if(canUseItem(item)) {
                                       cart.setItem(item);
                                   }
                               }
                           }
                       }
                    }
                });

                g.everySecond.put("astartaEverySecond", () -> {
                    if(awaitForDeath) {
                        if(g.getNight().getMSI().isActive())
                            return;
                        if(g.getNight().getScaryCat().isActive())
                            return;
                        if(g.getNight().getMirrorCat().isActive())
                            return;
                        if(mister.isAttacking() || mister.isSpawned())
                            return;

                        if(dyingStage == 0) {
                            startDeath();
                        }
                        return;
                    }
                    g.getNight().getA90().forgive = Math.min(g.getNight().getA90().forgive + 0.005F, 1);

                    if(batterySaveMode && mister.isSpawned() && Math.random() < 0.7) {
                        float random = (float) (Math.random() * 6.28F);
                        mister.addVelocity((float) (Math.cos(random) * 200), (float) (Math.sin(random) * 200));
                    }

                    if(msiCooldown > 0) {
                        msiCooldown--;
                    }
                    if(scaryCatCooldown > 0) {
                        scaryCatCooldown--;
                    }

                    if(g.getNight().getEnergy() < 25 && !batterySaveMode && !g.soda.isEnabled() && !g.miniSoda.isEnabled()) {
                        g.state = GameState.BATTERY_SAVER;
                        GamePanel.mirror = false;
                        g.music.stop();
                    }

                    if(uncannyEventSeconds > 0) {
                        uncannyEventSeconds--;

                        uncannyBoxes.add(new AstartaUncannyBox());
                        g.sound.playRate("uncannyBoxSound", 0.1, 0.9 + Math.random() / 5);

                        if(Math.random() < 0.3) {
                            new Pepitimer(() -> {
                                uncannyBoxes.add(new AstartaUncannyBox());
                                g.sound.playRate("uncannyBoxSound", 0.1, 0.9 + Math.random() / 5);
                            }, 500);
                        }

                        if(!g.everyFixedUpdate.containsKey("uncannyBoxEvent")) {
                            g.everyFixedUpdate.put("uncannyBoxEvent", () -> {
                                for(int i = 0; i < uncannyBoxes.size(); i++) {
                                    AstartaUncannyBox box = uncannyBoxes.get(i);

                                    box.setX(box.getX() + (box.left ? -1 : 1));
                                    if(Math.random() < 0.01) {
                                        box.left = !box.left;
                                    } else {
                                        if (box.getX() < 0) {
                                            box.left = true;
                                        }
                                        if (box.getX() > 1480) {
                                            box.left = false;
                                        }
                                    }

                                    box.setY(box.getY() + 2);

                                    if(box.getY() >= 290) {
                                        uncannyBoxes.remove(box);
                                        if(!g.getNight().getA90().isDying()) {
                                            g.getNight().getA90().spawn();
                                        }
                                    }
                                }
                                if(uncannyBoxes.isEmpty() && !g.everySecond10th.containsKey("uncannyBoxEvent")) {
                                    g.everyFixedUpdate.remove("uncannyBoxEvent");
                                }
                            });
                        }
                    }
                    if(dvdEventSeconds > 0) {
                        dvdEventSeconds--;

                        if(!g.everyFixedUpdate.containsKey("dvdEvent")) {
                            dvdVectorX = 12; // default values
                            dvdVectorY = 6;
                            dvdPosX = 240;
                            dvdPosY = 120;
                            dvdShake = 0;

                            g.everyFixedUpdate.put("dvdEvent", () -> {
                                if(dvdShake > 0) {
                                    dvdShake /= 1.4F;
                                    if(dvdShake < 0.05F) {
                                        dvdShake = 0;
                                    }
                                }
                                dvdPosX += dvdVectorX;
                                dvdPosY += dvdVectorY;

                                if(dvdPosX + 600 > 1480 || dvdPosX < 0) {
                                    dvdVectorX = - dvdVectorX;
                                    dvdShake = 40;
                                }
                                if(dvdPosY + 300 > 640 || dvdPosY < 0) {
                                    dvdVectorY = - dvdVectorY;
                                    dvdShake = 40;
                                }

                                if(dvdEventSeconds <= 0) {
                                    dvdShake = 0;
                                    g.fadeOut(255, 0, 3);
                                    g.everyFixedUpdate.remove("dvdEvent");
                                }
                            });
                        }
                    }
                    if(holeEventSeconds > 0) {
                        holeEventSeconds--;


                        if(!g.everyFixedUpdate.containsKey("holeEvent")) {
                            g.everyFixedUpdate.put("holeEvent", () -> {
                                for (int i = 0; i < blackHoles.size(); i++) {
                                    AstartaBlackHole hole = blackHoles.get(i);
                                    hole.size = (hole.size * 7F + hole.goalSize) / 8F;
                                    if(hole.size < 0.01) {
                                        blackHoles.remove(hole);
                                    } else if(hole.size >= 7.95) {
                                        blackHoles.remove(hole);
                                        g.fadeOut(255, 0, 3);
                                        GamePanel.mirror = !GamePanel.mirror;
                                    }
                                }

                                if(holeEventSeconds <= 0) {
                                    for (int i = 0; i < blackHoles.size(); i++) {
                                        AstartaBlackHole hole = blackHoles.get(i);
                                        new Pepitimer(hole::shrink, (int) (Math.random() * 1000));
                                    }
                                    if(g.everyFixedUpdate.containsKey("holeEvent") && blackHoles.isEmpty()) {
                                        g.everyFixedUpdate.remove("holeEvent");
                                        g.fadeOut(255, 0, 3);
                                        GamePanel.mirror = true;
                                    }
                                }
                            });
                        }

                        for(int i = 0; i < blackHoles.size(); i++) {
                            AstartaBlackHole hole = blackHoles.get(i);

                            hole.setLifetimeSeconds(hole.getLifetimeSeconds() + 1);
                            if (hole.getLifetimeSeconds() > 4 && Math.random() < 0.8) {
                                new Pepitimer(hole::shrink, (int) (Math.random() * 1000));
                            }
                        }
                        if(Math.random() < 0.8 && blackHoles.size() < 3) {
                            AstartaBlackHole hole = new AstartaBlackHole((int) (Math.random() * 1080 + 200), (int) (Math.random() * 240 + 200));
                            blackHoles.add(hole);
                            g.sound.play("astartaBlackHole", 0.1);
                        }
                    }
                });

                g.everyFixedUpdate.put("astartaTimers", () -> {
                    if(awaitForDeath)
                        return;

                    untilPhase1 -= 0.01666F;
                    if(untilPhase1 <= 3F && !warnedAboutPhaseChange) {
                        warnedAboutPhaseChange = true;
                        g.sound.play("phaseChange2", 0.15);
                    }
                    if(untilPhase1 <= 0) {
                        untilPhase1 = 100000;
                        activatePhase1();
                    }

                    untilPhase2 -= 0.01666F;
                    if(untilPhase2 <= 3F && !warnedAboutPhaseChange) {
                        warnedAboutPhaseChange = true;
                        g.sound.play("phaseChange1", 0.15);
                    }
                    if(untilPhase2 <= 0) {
                        untilPhase2 = 100000;
                        activatePhase2();
                    }

                    mister.untilMisterSpawn -= 0.01666F;
                    if(mister.untilMisterSpawn <= 0) {
                        mister.untilMisterSpawn = 100000;
                        mister.spawn();
                    }
                    rouletteTimer -= 0.01666F;
                    if(rouletteTimer <= 0) {
                        rouletteTimer = 100000;
                        activateRoulette();
                    }
                });

                new Pepitimer(() -> {
                    mister.spawn();
                }, 4000);
            }, 2000);
        }, 3000);
    }

    private boolean canUseItem(AstartaMinecartItem item) {
        if((item == AstartaMinecartItem.MSI || item == AstartaMinecartItem.SCARYCAT || item == AstartaMinecartItem.WIRES) &&
                (g.getNight().getMSI().isActive() || g.getNight().getScaryCat().isActive() || g.getNight().getWires().isActive())) {
            return false;
        }
        if(isFirstMister())
            return false;
        if(mister.isAttacking())
            return false;
        if(item == AstartaMinecartItem.MISTER && (mister.isSpawned() || mister.isAttacking()))
            return false;
        if(roulette && !item.isBuff())
            return false;

        return !awaitForDeath;
    }

    int msiCooldown = 0;
    int scaryCatCooldown = 0;

    boolean changingPhase = false;

    List<AstartaBlackHole> blackHoles = new ArrayList<>();
    List<AstartaUncannyBox> uncannyBoxes = new ArrayList<>();
    public byte uncannyEventSeconds = 0;

    float dvdVectorX = 12;
    float dvdVectorY = 6;
    float dvdPosX = 240;
    float dvdPosY = 120;
    float dvdShake = 0;
    public byte dvdEventSeconds = 0;

    public byte holeEventSeconds = 0;


    Mister mister;
    short misterCount = 0;

    public void reset() {
        untilPhase1 = 100000;
        untilPhase2 = 100000;
        mister.untilMisterSpawn = 100000;

        g.everySecond.remove("astartaEverySecond");

        g.everyFixedUpdate.remove("astartaMinecartMove");
        g.everyFixedUpdate.remove("misterCountdown");
        g.everyFixedUpdate.remove("astartaTimers");
        g.everyFixedUpdate.remove("astartaEndingHole");

        g.everyFixedUpdate.remove("eventRoulette");
        g.everyFixedUpdate.remove("uncannyBoxEvent");
        g.everyFixedUpdate.remove("dvdEvent");
        g.everyFixedUpdate.remove("holeEvent");

        for(Pepitimer pepitimer : allDeathPepitimers) {
            pepitimer.cancel();
        }
    }

    public float jumpscareBrightness = 1;
    public int jumpscareOffset = 0;

    float health = 200;
    float healthMargin = 0;

    // executes activatePhase1(); when reaches 0
    float untilPhase1 = 100000;
    // executes activatePhase2(); when reaches 0
    float untilPhase2 = 100000;
    boolean warnedAboutPhaseChange = false;
    boolean awaitForDeath = false;

    boolean hasMusicSpedUp = false;

    public boolean hasMusicSpedUp() {
        return hasMusicSpedUp;
    }

    public void damage(float damage) {
        if(phase == 1) {
            damage /= 3;
            damage *= 2;
        }

        if(health - damage <= 0) {
            healthMargin += health;
        } else {
            healthMargin += damage;
        }
        health = Math.max(0, health - damage);
        g.sound.play("astartaDamage", 0.08);

        if(health < 40 && !hasMusicSpedUp) {
            hasMusicSpedUp = true;
            for (MediaPlayer player : g.music.clips) {
                player.setRate(player.getRate() * 1.1F);
            }
        }

        if(health <= 0) {
            health = 0;
            new Pepitimer(() -> {
                awaitForDeath = true;
            }, 1000);
        }

        g.everyFixedUpdate.put("astartaHealthMargin", () -> {
            healthMargin /= 1.1F;
            if((int) (healthMargin) == 0) {
                healthMargin = 0;
                g.everyFixedUpdate.remove("astartaHealthMargin");
            }
        });
    }


    boolean batterySaveMode = false;
    public boolean isBatterySaveMode() {
        return batterySaveMode;
    }
    public void setBatterySaveMode(boolean batterySaveMode) {
        this.batterySaveMode = batterySaveMode;
    }

    boolean batterySaveChoice = true;
    public void setBatterySaveChoice(boolean batterySaveChoice) {
        this.batterySaveChoice = batterySaveChoice;
    }
    public boolean getBatterySaveChoice() {
        return batterySaveChoice;
    }




    byte dyingStage = 0;
    AstartaDeathShape deathShape = AstartaDeathShape.NONE;

    public AstartaDeathShape getDeathShape() {
        return deathShape;
    }

    public byte getDyingStage() {
        return dyingStage;
    }

    public void setDyingStage(byte dyingStage) {
        this.dyingStage = dyingStage;
    }

    List<Pepitimer> allDeathPepitimers = new ArrayList<>();

    public void startDeath() {
        new Pepitimer(() -> {
            if(dyingStage >= 1)
                return;

            if(health > 0) {
                damage(100);
            }

            AchievementHandler.obtain(g, Achievements.SHADOWNIGHT);
            g.pixelation = 1;
            g.music.stop();
            g.sound.playRateLooped("astartaEvilSound", 0.2, 0.5);
            dyingStage = 1;

            g.everyFixedUpdate.put("astartaMinecartExit", () -> {
                minecartYadd++;

                if (minecartYadd > 100) {
                    g.everyFixedUpdate.remove("astartaMinecartExit");

                    allDeathPepitimers.add(new Pepitimer(this::deathSequence2, 2000));
                }
            });
        }, 1000);
    }

    public void deathSequence2() {
        dyingStage = 2;

        if(phase == 2) {
            activatePhase1();
        }
        untilPhase2 = 100000;

        allDeathPepitimers.add(new Pepitimer(this::deathSequence3, (int) (17000 + (Math.round(Math.random() * 10) * 1000))));
    }

    public void deathSequence3() {
        dyingStage = 3;

        g.sound.stop();
        g.sound.play("astartaDeath", 0.1);

        List<AstartaDeathShape> shapes = new ArrayList<>(List.of(AstartaDeathShape.values()));

        RepeatingPepitimer timer = new RepeatingPepitimer(() -> {
            AstartaDeathShape oldShape = deathShape;
            Collections.shuffle(shapes);
            deathShape = shapes.get(0);

            while (deathShape == oldShape || deathShape == AstartaDeathShape.NONE) {
                Collections.shuffle(shapes);
                deathShape = shapes.get(0);
            }
        }, 1470, 420);

        allDeathPepitimers.add(timer);

        allDeathPepitimers.add(new Pepitimer(() -> timer.setDelay(400), 5000));
        allDeathPepitimers.add(new Pepitimer(() -> timer.setDelay(380), 7900));
        allDeathPepitimers.add(new Pepitimer(() -> timer.setDelay(340), 10000));
        allDeathPepitimers.add(new Pepitimer(() -> {
            dyingStage = 4;
            g.fadeOut(255, 0, 4);
        }, 8400));
        allDeathPepitimers.add(new Pepitimer(() -> timer.setDelay(300), 11600));
        allDeathPepitimers.add(new Pepitimer(() -> {
            timer.setDelay(260);
            dyingStage = 5;
            g.fadeOut(255, 0, 4);
        }, 14000));
        allDeathPepitimers.add(new Pepitimer(() -> timer.setDelay(220), 16000));
        allDeathPepitimers.add(new Pepitimer(() -> timer.setDelay(200), 17000));
        allDeathPepitimers.add(new Pepitimer(() -> timer.setDelay(160), 18400));
        allDeathPepitimers.add(new Pepitimer(() -> timer.setDelay(120), 19600));

        allDeathPepitimers.add(new Pepitimer(() -> {
            timer.cancel(true);
            deathShape = AstartaDeathShape.NONE;
            dyingStage = 6;

            g.everyFixedUpdate.put("astartaEndingHole", () -> {
                allDeathPepitimers.add(new Pepitimer(() -> {
                    switch (dyingStage) {
                        case 6 -> endingHoleSize = (endingHoleSize * 44F + 10) / 45F;
                        case 7 -> {
                            endingHoleSize = (endingHoleSize * 29F) / 30F;
                            endingAstartaSize = (endingAstartaSize * 34F) / 35F;
                        }
                        case 8 -> endingOfficeSize = (endingOfficeSize * 49F) / 50F;
                        case 9 -> {
                            endingHoleSize = (endingHoleSize * 69F + 1) / 70F;
                            if(endingHoleSize > 0.995) {
                                new Pepitimer(() -> {
                                    if(dyingStage >= 10)
                                        return;

                                    dyingStage = 10;
                                    endingHoleSize = 1;

                                    g.sound.playRateLooped("astartaEvilSound", 0.3, 0.33);
                                }, 2000);
                            }
                        }
                        case 10 -> {
                            if(endingTextAlpha < 1) {
                                endingTextAlpha = Math.min(endingTextAlpha + 0.01F, 1);
                            }
                        }
                        case 11 -> {
                            endingHoleSize = (endingHoleSize * 59F + 9) / 60F;
                        }
                    }
                }, 3500));
            });
        }, 20300));

        allDeathPepitimers.add(new Pepitimer(() -> {
            timer.cancel(true);
            dyingStage = 7;

            allDeathPepitimers.add(new Pepitimer(() -> {
                dyingStage = 8;

                allDeathPepitimers.add(new Pepitimer(() -> {
                    endingHoleSize = 0.01F;
                    dyingStage = 9;
                }, 6000));
            }, 5000));
        }, 26000));
    }

    float endingHoleSize = 0.01F;
    float endingAstartaSize = 1.2F;
    float endingOfficeSize = 1F;
    float endingTextAlpha = 0F;
    boolean endingChoice = true;

    public boolean getEndingChoice() {
        return endingChoice;
    }

    public void setEndingChoice(boolean endingChoice) {
        this.endingChoice = endingChoice;
    }

    public float getEndingHoleSize() {
        return endingHoleSize;
    }

    public float getEndingAstartaSize() {
        return endingAstartaSize;
    }

    public float getEndingOfficeSize() {
        return endingOfficeSize;
    }

    public float getEndingTextAlpha() {
        return endingTextAlpha;
    }



    public float rouletteTimer = 60;
    public byte[] roulette1 = new byte[58];
    public byte[] roulette2 = new byte[58];
    public byte[] roulette3 = new byte[58];
    boolean roulette = false;
    float rouletteY = 0;
    List<Byte> rouletteResults = new ArrayList<>();

    public void activateRoulette() {
        rouletteY = 0;

        g.sound.play("eventRoulette", 0.1);
        g.everyFixedUpdate.put("eventRoulette", () -> {
           if(rouletteY > 9660) {
               g.everyFixedUpdate.remove("eventRoulette");
               rouletteY = 9660;
               g.redrawRouletteScreen(this);
           } else {
               rouletteY += 28.333F;
               g.redrawRouletteScreen(this);
           }
        });

        for(int i = 0; i < 58; i++) {
            roulette1[i] = (byte) (Math.random() * 3);
            roulette2[i] = (byte) (Math.random() * 3);
            roulette3[i] = (byte) (Math.random() * 3);

            if(roulette1[i] == roulette2[i] && roulette2[i] == roulette3[i]) {
                while (roulette1[i] == roulette2[i] && roulette2[i] == roulette3[i]) {
                    roulette1[i] = (byte) (Math.random() * 3);
                    roulette2[i] = (byte) (Math.random() * 3);
                    roulette3[i] = (byte) (Math.random() * 3);
                }
            }
        }
        roulette = true;

        if(g.getNight().getMSI().isActive()) {
            g.getNight().getMSI().quickKill();
        }
        g.getNight().getMirrorCat().kill();
        g.getNight().getWires().leave();
        g.getNight().getScaryCat().leave();

        byte result;

        if(rouletteResults.isEmpty()) {
            result = (byte) (Math.random() * 3);
        } else {
            result = rouletteResults.get(0);
            rouletteResults.remove(0);
        }
        roulette1[57] = result;
        roulette2[57] = result;
        roulette3[57] = result;

        new Pepitimer(() -> {
            g.fadeOut(255, 0, 4);
            rouletteTimer = 60;

            switch (result) {
                case 0 -> uncannyEventSeconds = 7;
                case 1 -> dvdEventSeconds = 25;
                case 2 -> holeEventSeconds = 25;
            }
            roulette = false;
        }, 7200);
    }

    public boolean isRoulette() {
        return roulette;
    }

    public int getRouletteY() {
        return (int) (rouletteY);
    }

    int phase = 1;
    int phase2X = 580;
    float cosCounter = 0;
    boolean firstMister = true;

    public void activatePhase2() {
        phase2X = 580;
        cosCounter = 1;
        phase = 2;

        g.everyFixedUpdate.remove("astartaPhase2Move");
        g.everyFixedUpdate.putIfAbsent("astartaPhase2Move", () -> {
            if(mister.isAttacking())
                return;
            cosCounter += 1 * speedModifier;
            phase2X = (int) (Math.sin(cosCounter / 40F) * 540 + 540);
        });

        untilPhase1 = (int) (16 + Math.round(Math.random() * 9));
        warnedAboutPhaseChange = false;
    }


    public void activatePhase1() {
        g.everyFixedUpdate.remove("astartaPhase2Move");
        phase = 1;

        untilPhase2 = (int) (12 + Math.round(Math.random() * 11));
        warnedAboutPhaseChange = false;
    }

    public Rectangle getVisualHitbox() {
        if(phase == 1) {
            return new Rectangle(g.offsetX - 400 + 380, 0, 800, 640);
        }
        return new Rectangle(g.offsetX - 400 + phase2X, 140, 400, 500);
    }

    public Rectangle getHitbox() {
        if(phase == 1) {
            return new Rectangle(380, 0, 800, 640);
        }
        return new Rectangle(phase2X, 140, 400, 500);
    }

    public boolean isFirstMister() {
        return firstMister;
    }

    public void setFirstMister(boolean firstMister) {
        this.firstMister = firstMister;
    }

    float speedModifier = 1;

    public boolean isStartingCutscene() {
        return isStartingCutscene;
    }

    public boolean isFighting() {
        return isFighting;
    }

    public int getPhase() {
        return phase;
    }

    public int getX() {
        return phase2X;
    }

    public List<AstartaMinecart> getMinecarts() {
        return minecarts;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getHealthMargin() {
        return healthMargin;
    }


    public List<AstartaBlackHole> getBlackHoles() {
        return blackHoles;
    }

    public List<AstartaUncannyBox> getUncannyBoxes() {
        return uncannyBoxes;
    }

    public float getDvdPosX() {
        return dvdPosX;
    }

    public float getDvdPosY() {
        return dvdPosY;
    }

    public float getDvdShake() {
        return dvdShake;
    }

    public Mister getMister() {
        return mister;
    }

    public float getSpeedModifier() {
        return speedModifier;
    }

    public void setSpeedModifier(float speedModifier) {
        this.speedModifier = speedModifier;
    }

    public boolean isChangingPhase() {
        return changingPhase;
    }



    public BufferedImage astartaOfficeStuff(BufferedImage office, GamePanel g) {
        switch (dyingStage) {
            case 0 -> {
                int distort = (int) (100 - health / 2);
                if(distort > 10) {
                    office = g.wobble(office, distort, 2, 0.02F, 0.2F);
                    office = g.vertWobble(office, distort, 2, 0.02F, 0.2F);
                }
            }
            case 3 -> {
                office = g.wobble(office, 10, 40, 0.02F, 0.2F);
            }
            case 4 -> {
                office = g.wobble(office, 40, 40, 0.04F, 0.3F);
                office = g.vertWobble(office, 40, 40, 0.02F, 0.3F);
            }
            case 5 -> {
                office = g.wobble(office, 50, 40, 0.08F, 0.4F);
                office = g.vertWobble(office, 50, 40, 0.06F, 0.4F);
            }
            case 6 -> {
                office = g.wobble(office, 50, 40, 0.08F, 0.4F);
                office = g.vertWobble(office, 50, 40, 0.06F, 0.4F);
                office = g.wobble(office, 50, 40, 0.04F, 0.4F);
                office = g.vertWobble(office, 50, 40, 0.03F, 0.4F);
            }
            case 8, 9, 10, 11 -> {
                float size = endingOfficeSize;
                if(size > 0.005F) {
                    return GamePanel.resize(office, (int) (1480 * size), (int) (640 * size), BufferedImage.SCALE_FAST);
                } else {
                    return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                }
            }
        }
        return office;
    }


    public void drawStar(Graphics2D graphics2D, int fixedUpdatesAnim) {
        List<Point> polyPoints = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            double s = 300 * Math.sin(fixedUpdatesAnim * 0.01);
            if(i % 2 != 0) {
                s = 150 * Math.cos(fixedUpdatesAnim * 0.01);
            }

            int x = (int) (540 + Math.cos(i * 0.628 + fixedUpdatesAnim * 0.005) * s);
            int y = (int) (320 + Math.sin(i * 0.628 + fixedUpdatesAnim * 0.005) * s);
            polyPoints.add(new Point(x, y));
        }
        graphics2D.drawPolygon(GamePanel.getPolygon(polyPoints));
    }

    public void drawCube(Graphics2D graphics2D, int fixedUpdatesAnim) {
        Point v = new Point((int) (Math.cos(fixedUpdatesAnim * 0.05) * 120), (int) (Math.sin(fixedUpdatesAnim * 0.05) * 120));
        int ax = 360;
        int ay = 170;
        int w = 300;
        int h = 300;
        float speedDiff = 2;
        int vxSpeedDiff = (int) (v.x / speedDiff);
        int vySpeedDiff = (int) (v.y / speedDiff);

        graphics2D.drawRect(v.x + ax, v.y + ay, w, h);
        graphics2D.drawRect(vxSpeedDiff + ax, vySpeedDiff + ay, w, h);

        graphics2D.drawLine(vxSpeedDiff + ax, vySpeedDiff + ay, v.x + ax, v.y + ay);
        graphics2D.drawLine(vxSpeedDiff + ax + w, vySpeedDiff + ay, v.x + ax + w, v.y + ay);
        graphics2D.drawLine(vxSpeedDiff + ax + w, vySpeedDiff + ay + h, v.x + ax + w, v.y + ay + h);
        graphics2D.drawLine(vxSpeedDiff + ax, vySpeedDiff + ay + h, v.x + ax, v.y + ay + h);
    }

    public void drawPlanet(Graphics2D graphics2D, int fixedUpdatesAnim) {
        int h1 = (int) (400 * Math.abs(Math.sin(fixedUpdatesAnim * 0.04)));
        int ya = (int) (80 * Math.cos(fixedUpdatesAnim * 0.03));
        graphics2D.drawOval(390, 160 + ya, 300, 300);
        graphics2D.drawOval(290, 180 + 150 - h1 / 2 + ya, 500, h1);
    }

    public void drawIntro(Graphics2D graphics2D, int fixedUpdatesAnim) {
        List<Point> polyPoints = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            double s = 300 * Math.sin(fixedUpdatesAnim * 0.01);
            if(i % 2 != 0) {
                s = 150 * Math.cos(fixedUpdatesAnim * 0.01);
            }
            if(s < 0) {
                s *= 6;
            }

            int x = (int) (540 + Math.cos(i * 0.628 + fixedUpdatesAnim * 0.005) * s);
            int y = (int) (320 + Math.sin(i * 0.628 + fixedUpdatesAnim * 0.005) * s);
            polyPoints.add(new Point(x, y));
        }
        graphics2D.drawPolygon(GamePanel.getPolygon(polyPoints));
    }

    public void drawEye(Graphics2D graphics2D, int fixedUpdatesAnim) {
        List<Point> polyPoints = new ArrayList<>();
        List<Point> polyPoints2 = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            double s1 = 300;
            double s2 = 300 * Math.sin(fixedUpdatesAnim * 0.02);

            int x = (int) (540 + Math.cos(i * 0.628) * s1 * 1.2);
            int y = (int) (320 + Math.sin(i * 0.628) * s2 * 0.5);
            polyPoints.add(new Point(x, y));

            int y2 = (int) (320 + Math.sin(i * 0.628) * s2 * 0.4);
            polyPoints2.add(new Point(x, y2));
        }
        graphics2D.drawPolygon(GamePanel.getPolygon(polyPoints));
        graphics2D.drawPolygon(GamePanel.getPolygon(polyPoints2));

        List<Point> polyPoints3 = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            double s = 100;
            int x = (int) (540 + Math.cos(i * 1.57 + fixedUpdatesAnim * 0.01) * s);
            int y = (int) (320 + Math.sin(i * 1.57 + fixedUpdatesAnim * 0.01) * s);

            polyPoints3.add(new Point(x, y));
        }
        graphics2D.fillPolygon(GamePanel.getPolygon(polyPoints3));
    }

    public void drawRotate(Graphics2D graphics2D, int fixedUpdatesAnim) {
        List<Point> polyPoints = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            double s = 300;
            int x = (int) (540 + Math.cos(i * 1.57 + fixedUpdatesAnim * 0.02) * s);
            int y = (int) (320 + Math.sin(i * 1.57 + fixedUpdatesAnim * 0.02) * s);

            polyPoints.add(new Point(x, y));
        }
        graphics2D.drawPolygon(GamePanel.getPolygon(polyPoints));

        polyPoints = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            double s = 250;
            int x = (int) (540 + Math.cos(i * 1.57 + -fixedUpdatesAnim * 0.02) * s);
            int y = (int) (320 + Math.sin(i * 1.57 + -fixedUpdatesAnim * 0.02) * s);

            polyPoints.add(new Point(x, y));
        }
        graphics2D.drawPolygon(GamePanel.getPolygon(polyPoints));

        polyPoints = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            double s = 200;
            int x = (int) (540 + Math.cos(i * 1.57 + fixedUpdatesAnim * 0.02) * s);
            int y = (int) (320 + Math.sin(i * 1.57 + fixedUpdatesAnim * 0.02) * s);

            polyPoints.add(new Point(x, y));
        }
        graphics2D.drawPolygon(GamePanel.getPolygon(polyPoints));
    }

    public void drawAtom(Graphics2D graphics2D, int fixedUpdatesAnim) {
        List<Point> polyPoints = new ArrayList<>();
        for(int i = 0; i < 22; i++) {
            double s = 200;

            int x = (int) (540 + Math.cos(i * 0.314 + fixedUpdatesAnim * 0.015) * s);
            int y = (int) (320 + (Math.sin(i * 0.314) - Math.cos((i - 2) * 0.314)) * s);
            polyPoints.add(new Point(x, y));

            if(i == 0) {
                double z = fixedUpdatesAnim * 0.1;
                x = (int) (540 + Math.cos(z * 0.314 + fixedUpdatesAnim * 0.015) * s);
                y = (int) (320 + (Math.sin(z * 0.314) - Math.cos((z - 2) * 0.314)) * s);

                graphics2D.fillOval(x - 20, y - 20, 40, 40);
            }
        }
        graphics2D.drawPolygon(GamePanel.getPolygon(polyPoints));

        polyPoints = new ArrayList<>();
        for(int i = 0; i < 22; i++) {
            double s = 150;

            int x = (int) (540 - Math.cos(i * 0.314 + fixedUpdatesAnim * 0.015) * s);
            int y = (int) (320 + (Math.sin(i * 0.314) - Math.cos(i * 0.314)) * s);
            polyPoints.add(new Point(x, y));

            if(i == 0) {
                double z = -fixedUpdatesAnim * 0.1;
                x = (int) (540 - Math.cos(z * 0.314 + fixedUpdatesAnim * 0.015) * s);
                y = (int) (320 + (Math.sin(z * 0.314) - Math.cos(z * 0.314)) * s);

                graphics2D.fillOval(x - 20, y - 20, 40, 40);
            }
        }
        graphics2D.drawPolygon(GamePanel.getPolygon(polyPoints));

        graphics2D.fillOval(540 - 60, 320 - 60, 120, 120);
    }
}