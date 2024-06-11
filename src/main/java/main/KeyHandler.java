package main;

import cutscenes.Cutscene;
import cutscenes.Presets;
import enemies.Rat;
import game.*;
import game.achievements.AchievementHandler;
import game.achievements.Achievements;
import game.bingo.BingoHandler;
import game.bingo.BingoTask;
import game.custom.CustomNight;
import game.custom.CustomNightEnemy;
import game.custom.CustomNightModifier;
import game.playmenu.PlayMenu;
import game.shadownight.AstartaBlackHole;
import game.shadownight.Mister;
import javafx.scene.media.MediaPlayer;
import utils.*;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class KeyHandler implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
    GamePanel g;

    Point pointerPosition = new Point(0, 0);
    Cursor defaultCursor;

    public KeyHandler(GamePanel gamePanel) {
        this.g = gamePanel;

        fanSounds = new SoundMP3(g,"fan");
        camSounds = new SoundMP3(g,"cam");
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    SoundMP3 fanSounds;
    SoundMP3 camSounds;

    byte newScreenshot = 0;

    GameState previous = GameState.MENU;
    public void unpause() {
        if(previous == GameState.UNLOADED) {
            previous = GameState.MENU;
        }
        g.state = previous;
        g.pauseDieSelected = false;

        g.resumeAllSound();

        for (Pepitimer pepitimer : StaticLists.timers) {
            pepitimer.resume();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if(e.isControlDown() && e.isShiftDown() && code == KeyEvent.VK_X) {
            Console.toggle();
            return;
        }
        if(Console.isOn()) {
            if(!e.isAltDown() && !e.isControlDown() && code != KeyEvent.VK_CAPS_LOCK && code != KeyEvent.VK_SHIFT) {
                switch (code) {
                    case KeyEvent.VK_ENTER -> Console.enter(true);
                    case KeyEvent.VK_BACK_SPACE -> Console.removeLast();
                    case KeyEvent.VK_TAB -> Console.autoFill();
                    default -> Console.type(e.getKeyChar() + "");
                }
            }
            return;
        }
        if(g.state == GameState.GAME) {
            g.getNight().afk = 17;
            // below is testing code
//            if (code == KeyEvent.VK_G) {
//                g.getNight().seconds += 4;
//            }
        }

        if(e.isShiftDown()) {
            if(code == KeyEvent.VK_Z) {
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Calendar.getInstance().getTime());
                Path path = Paths.get(g.gameDirectory + "\\screenshots" + "\\screenshot-" + timeStamp + ".png");
                try {
                    if(g.saveScreenshots) {
                        try {
                            Files.createFile(path);
                            newScreenshot = 0;
                        } catch (FileAlreadyExistsException exception) {
                            newScreenshot++;
                            path = Paths.get(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "\\screenshot-" + timeStamp + "-" + newScreenshot + ".png");
                            Files.createFile(path);
                        }
                    }

                    BufferedImage newUnshaded = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D overGraphics2D = newUnshaded.createGraphics();
                    overGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    overGraphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

                    if(g.pixelation > 1) {
                        overGraphics2D.drawImage(g.lastFullyRenderedUnshaded.getScaledInstance(1080 / (int) g.pixelation, 640 / (int) g.pixelation, Image.SCALE_FAST), 0, 0, 1080, 640, null);
                    } else {
                        overGraphics2D.drawImage(g.lastFullyRenderedUnshaded, 0, 0, 1080, 640, null);
                    }

                    overGraphics2D.setColor(Color.GREEN);

                    if(g.state != GameState.UNLOADED) {
                        if (g.sensor.isEnabled() || g.adBlocked) {
                            overGraphics2D.setFont(new Font("Arial", Font.BOLD, 30));
                            byte i = 0;
                            while (i < g.console.list.size()) {
                                String chat = g.console.list.get(i);

                                overGraphics2D.drawString(chat, 5, 40 * i + 60);
                                i++;
                            }
                        }
                    }

                    overGraphics2D.dispose();

                    if(g.saveScreenshots) {
                        ImageIO.write(newUnshaded, "png", new File(String.valueOf(path)));
                    }
                    g.sound.play("sodaOpen", 0.03);
                    g.fadeOut(0, g.endFade, 20);
                    ClipboardUtils.copy(newUnshaded);

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                return;
            }
        }

        boolean stopPause = false;

        if(!g.state.equals(GameState.UNLOADED)) {
            if (!e.isControlDown() && !e.isShiftDown() && !e.isAltDown()) {
                if(g.state == GameState.DISCLAIMER) {
                    g.launch();
                    return;
                }
                if (g.pressAnyKey) {
                    if(g.riftItems.size() >= 2 && g.endless.getNight() >= 3) {
                        if(g.riftTransparency <= 0) {
                            g.enterRift();
                        }
                    } else {
                        if(processDeathScreenPress())
                            return;

                        g.stopGame(true);
                    }
                    return;
                }
            }
            switch (g.state) {
                case MENU -> {
                    if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE || code == KeyEvent.VK_Z) {
                        clickMenuButton();
                    }if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
                        if(g.selectedOption - g.menuButtonOffset == 3) {
                            g.menuButtonOffset++;
                        }

                        g.selectedOption++;

                        if(g.selectedOption == g.menuButtons.size()) {
                            g.selectedOption = 0;
                            g.menuButtonOffset = 0;
                        }
                        g.visibleMenuButtons = g.menuButtons.subList(g.menuButtonOffset, Math.min(g.menuButtonOffset + 4, g.menuButtons.size()));
                    } else if(code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
                        if(g.selectedOption - g.menuButtonOffset == 0 && g.menuButtonOffset > 0) {
                            g.menuButtonOffset--;
                        }

                        g.selectedOption--;

                        if(g.selectedOption == -1) {
                            g.selectedOption = (byte) (g.menuButtons.size() - 1);
                            g.menuButtonOffset = (byte) (g.menuButtons.size() - g.visibleMenuButtons.size());
                        }
                        g.visibleMenuButtons = g.menuButtons.subList(g.menuButtonOffset, Math.min(g.menuButtonOffset + 4, g.menuButtons.size()));
                    }
                }
                case ITEMS -> {
                    if(g.everySecond20th.containsKey("startSimulation"))
                        return;

                    if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
                        if (g.selectedItemY == g.rows - 1) {
                            g.startButtonSelected = true;
                        }
                        g.selectedItemY++;

                        if(g.selectedItemY * 170 + (210 - 40 * Math.min(g.rows, 4)) - g.itemScrollY + 170 > 640 && !g.startButtonSelected) {
                            g.itemScrollY = (short) (Math.max(0, g.itemScrollY + 170));

                            while (g.selectedItemY * 170 + (210 - 40 * Math.min(g.rows, 4)) - g.itemScrollY + 170 < 640) {
                                g.itemScrollY--;
                            }
                            g.redrawItemsMenu();
                        }
                    } else if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
                        if (g.startButtonSelected) {
                            g.selectedItemX = (byte) (g.columns - 1);
                            g.selectedItemY = (byte) (g.rows - 1);

                            g.startButtonSelected = false;
                        } else {
                            g.selectedItemY = (byte) Math.max(0, g.selectedItemY - 1);

                            if(g.selectedItemY * 170 + (210 - 40 * Math.min(g.rows, 4)) - g.itemScrollY < 0) {
                                g.itemScrollY = (short) (Math.max(0, g.itemScrollY - 170));

                                g.redrawItemsMenu();
                            }
                        }
                    }

                    if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
                        if (g.selectedItemX == g.columns - 1) {
                            g.selectedItemX = 0;
                        } else {
                            g.selectedItemX++;
                        }
                    } else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
                        if (g.selectedItemX == 0) {
                            g.selectedItemX = (byte) (g.columns - 1);
                        } else {
                            g.selectedItemX--;
                        }
                    }

                    if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE || code == KeyEvent.VK_Z) {
                        if (g.startButtonSelected) {
                            g.startGameThroughItems();
                        } else {
                            try {
                                Item item = g.itemList.get(g.selectedItemX + (g.selectedItemY * g.columns));

                                if (item.getAmount() != 0) {
                                    if (item.isSelected()) {
                                        item.deselect();
                                        g.itemLimit -= item.getItemLimitAdd();
                                    } else {
                                        if (g.checkItemsAmount() < g.itemLimit && !item.isMarkedConflicting()) {
                                            item.select();
                                            g.itemLimit += item.getItemLimitAdd();
                                            g.sound.play("select", 0.1, false);
                                        } else {
                                            g.sound.play("selectFail", 0.12, false);

                                            if(item.isMarkedConflicting()) {
                                                item.setShakeIntensity((byte) 60);
                                            }
                                        }
                                    }

                                    g.updateItemList();
                                    g.redrawItemsMenu();
                                } else {
                                    g.sound.play("selectFail", 0.12, false);
                                }
                            } catch (IndexOutOfBoundsException ignored) {
                                g.sound.play("selectFail", 0.12, false);
                            }
                        }
                    }

                    if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_BACK_SPACE) {
                        stopPause = true;
                        if(g.type == GameType.CUSTOM) {
                            g.startChallengeMenu(false);
                        } else {
                            g.backToMainMenu();
                        }
                    }
                }
                case BINGO -> {
                    if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_BACK_SPACE) {
                        stopPause = true;
                        g.backToMainMenu();
                    } else if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE || code == KeyEvent.VK_Z) {
                        clickBingoButton();
                    }
                }
                case CHALLENGE -> {
                    if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_BACK_SPACE) {
                        stopPause = true;

                        cancelLimbo();

                        g.backToMainMenu();
                    } else if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE || code == KeyEvent.VK_Z) {
                        cancelLimbo();

                        g.type = GameType.CUSTOM;
                        g.startItemSelect();
                    }
                }
                case PLAY -> {
                    if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
                        if (PlayMenu.index == PlayMenu.list.size() - 1) {
                            PlayMenu.index = 0;
                        } else {
                            PlayMenu.index++;
                        }
                        g.sound.playRate("playMenuChange", 0.05, 2);
                        PlayMenu.movedMouse = false;

                    } else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
                        if (PlayMenu.index == 0) {
                            PlayMenu.index = PlayMenu.list.size() - 1;
                        } else {
                            PlayMenu.index--;
                        }
                        g.sound.playRate("playMenuChange", 0.05, 2);
                        PlayMenu.movedMouse = false;

                    } else if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE || code == KeyEvent.VK_Z) {
                        clickPlayButton();
                    } else if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_BACK_SPACE) {
                        stopPause = true;
                        g.backToMainMenu();
                    }
                }
                case SETTINGS -> {
                    if(code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D || code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
                        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
                            g.volume = Math.min(1, Math.max(0, g.volume + 0.05F));
                        } else {
                            g.volume = Math.min(1, Math.max(0, g.volume - 0.05F));
                        }

                        g.stopAllSound();
                        g.music.play("pepito", 0.2, true);
                    }

                    if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_BACK_SPACE) {
                        stopPause = true;
                        g.backToMainMenu();
                    }
                }
                case ACHIEVEMENTS -> {
                    if(!g.shiftingAchievements) {
                        if ((code == KeyEvent.VK_RIGHT && !g.achievementState) || (code == KeyEvent.VK_LEFT && g.achievementState)) {
                            shiftAchievements();
                        }
                        if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_BACK_SPACE) {
                            stopPause = true;
                            g.backToMainMenu();
                        }
                    }
                }
                case GAME -> {
                    if (g.getNight().getEvent().canUseItems()) {
                        if (g.night.getA90().isActive()) {
                            g.night.getA90().dying = true;
                        }

                        switch (code) {
                            case KeyEvent.VK_M -> { // metal pipe
                                if (g.metalPipe.isEnabled()) {
                                    if (g.metalPipeCooldown == 0) {
                                        g.sound.play("metalPipe", 0.025);

                                        BingoHandler.completeTask(BingoTask.METAL_PIPE_USE);
                                        g.getNight().usedMetalPipes++;
                                        if(g.getNight().usedMetalPipes >= 6) {
                                            BingoHandler.completeTask(BingoTask.METAL_PIPE_USE_6_TIMES);
                                        }

                                        if(!g.night.getPepito().isNotPepito && g.night.getPepito().pepitoStepsLeft < 6) {
                                            g.night.getPepito().scare();
                                        }
                                        if (g.night.getMaki().secondsUntilMaki <= 0) {
                                            g.night.getMaki().scare();
                                        }

                                        g.metalPipeCooldown = 22;
                                        g.night.getPepito().notPepitoChance += 1;
                                    } else {
                                        g.sound.play("error", 0.08);
                                    }
                                    if(g.getNight().getEvent() == GameEvent.FLOOD) {
                                        g.sound.play("bubbles", 0.05);
                                    }
                                }
                            }

                            case KeyEvent.VK_F -> { // fan
                                if (g.night.hasPower()) {
                                    if (g.fan.isEnabled()) {
                                        g.fanActive = !g.fanActive;

                                        if (g.fanActive) {
                                            fanSounds.play("fanSound", 0.22, true);
                                            g.sound.play("startFan", 0.15);
                                            g.usage++;

                                            g.everySecond20th.put("fan", () -> {
                                                g.fanDegrees += 46;
                                            });
                                        }
                                        if (!g.fanActive) {
                                            fanSounds.stop();
                                            g.sound.play("stopFan", 0.15);
                                            g.usage--;
                                            g.fanDegrees = 0;

                                            g.everySecond20th.remove("fan");
                                        }
                                        g.redrawUsage();
                                    }
                                } else {
                                    g.sound.play("error", 0.08);
                                }
                            }

                            case KeyEvent.VK_C -> {
                                if (!g.night.hasPower() || g.night.getGlitcher().isGlitching) {
                                    g.sound.play("error", 0.08);
                                } else if(!g.portalTransporting) {
                                    g.inCam = !g.inCam;

                                    if (g.inCam) {
                                        g.sound.play("camPull", 0.12);
                                        camSounds.play("buzzlight", 0.25, true);
                                        g.fadeOutStatic(1F, 0.3F, 0.01F);

                                        g.usage++;
                                        g.night.addEnergy(-0.2F);
                                        g.redrawUsage();

                                        g.night.getMSI().disappearShadow();

                                        if(g.getNight().getType().isEndless()) {
                                            double rand = Math.random();
                                            g.portalActive = g.endless.getNight() >= 4 && rand < 0.015F + g.endless.getNight() / 150F;
                                            if (g.portalActive) {
                                                camSounds.play("shadowPortal", 0.1, true);
                                            }
                                        }
                                        if(!g.portalActive) {
                                            g.outOfLuck = true;
                                            new Pepitimer(() -> g.outOfLuck = false, 120);
                                        }

                                        g.updateCam();
                                        if (g.night.getGlitcher().isEnabled()) {
                                            g.night.getGlitcher().counter += 0.4F;

                                            if(g.night.getGlitcher().counter > 12) {
                                                new Pepitimer(() -> {
                                                    if(g.inCam) {
                                                        g.fadeOutStatic(1F, 0.2F, 0.005F);
                                                        g.sound.play("camPull", 0.12);
                                                    }
                                                }, 400 + (short) (Math.random() * 400));
                                            }
                                        }
                                    }
                                    if (!g.inCam) {
                                        g.camOut(true);
                                    }
                                }
                            }

                            case KeyEvent.VK_S -> {
                                if (g.soda.isEnabled()) {
                                    if(g.getNight().getEnergy() <= 5) {
                                        BingoHandler.completeTask(BingoTask.USE_SODA_AT_1_ENERGY);
                                    }

                                    if(g.getNight().hasPower()) {
                                        if (g.night.getEnergy() > g.night.getMaxEnergy()) {
                                            g.night.setMaxEnergy(g.night.getEnergy());
                                        }
                                        g.night.setEnergy(Math.min(g.night.getEnergy() + 200, g.night.getMaxEnergy()));
                                    } else {
                                        new Notification("Your Soda was expired", 2000);
                                    }
                                    g.sound.play("sodaOpen", 0.03);
                                    g.soda.disable();
                                    g.night.getColaCat().leave();
                                }
                            }

                            case KeyEvent.VK_D -> {
                                if (g.miniSoda.isEnabled()) {
                                    if(g.getNight().hasPower()) {
                                        if (g.night.getEnergy() > g.night.getMaxEnergy()) {
                                            g.night.setMaxEnergy(g.night.getEnergy());
                                        }
                                        g.night.setEnergy(Math.min(g.night.getEnergy() + 50, g.night.getMaxEnergy()));
                                    } else {
                                        new Notification("Your Mini Soda was expired", 2000);
                                    }
                                    g.sound.playRate("sodaOpen", 0.05, 0.5);
                                    g.sound.play("minnesota", 0.2);
                                    g.miniSoda.disable();

                                    g.repaintOffice();
                                }
                            }

                            case KeyEvent.VK_I -> {
                                if(g.freezePotion.isEnabled()) {
                                    GamePanel.freezeModifier -= 0.25F;
                                    g.sound.play("icePotionUse", 0.1F);

                                    freezeChange = new Pepitimer(() -> {
                                        GamePanel.freezeModifier += 0.25F;
                                    }, 30000);

                                    g.freezePotion.disable();
                                    g.repaintOffice();
                                }
                            }

                            case KeyEvent.VK_L -> {
                                if(g.starlightBottle.isEnabled()) {
                                    g.sound.play("drinkStarlight", 0.1F);

                                    g.starlightBottle.disable();
                                    g.starlightMillis = 7000;
                                    g.repaintOffice();
                                }
                            }

                            case KeyEvent.VK_U -> {
                                if (g.soup.isEnabled()) {
                                    g.sound.play("sodaOpen", 0.03);

                                    g.soup.disable();
                                    g.night.getAstarta().leaveEarly();
                                    g.night.getPepito().scare();
                                    g.night.getMaki().scare();
                                    if(g.night.getMSI().isActive()) {
                                        g.night.getMSI().kill(false);
                                    }
                                    g.night.getColaCat().leave();

                                    if(g.night.getShark().isActive()) {
                                        g.night.getShark().floodDuration = 1;
                                    }
                                    if(g.night.getBoykisser().isAwaitingResponse()) {
                                        g.night.getBoykisser().leave();
                                    }
                                    g.night.getLemonadeCat().leave();
                                    g.night.getMirrorCat().kill();
                                    g.night.getWires().leave();
                                    g.night.getScaryCat().leave();
//                                    g.night.getA120().ac = false;
                                    //stop

                                    if(g.getNight().getEvent() == GameEvent.ASTARTA) {
                                        g.getNight().getAstartaBoss().damage(2);
                                    }

                                    g.repaintOffice();
                                }
                            }
                            case KeyEvent.VK_B -> holdingB = true;
                        }

                        if(g.planks.isEnabled() && holdingB) {
                            if(Character.isDigit(e.getKeyChar())) {
                                int dorsik = Integer.parseInt(String.valueOf(e.getKeyChar())) - 1;

                                if (g.getNight().getDoors().containsKey(dorsik)) {
                                    g.planks.disable();
                                    g.night.getDoors().get(dorsik).addBlockade(12);
                                    g.sound.play("planks", 0.2);
                                    g.repaintOffice();
                                }
                            }
                        }


                        g.usedItems.removeIf(item -> !item.isEnabled());

                        if (g.adBlocked) {
                            if (String.valueOf(e.getKeyChar()).equals(g.randomCharacter)) {
                                g.console.add("Your subscription has been renewed.");
                                g.adBlocked = false;

                                g.sound.play("a90Alive", 0.08);
                            }
                        }
                    }
                    if(g.getNight().frog != null) {
                        if (code == KeyEvent.VK_F || code == KeyEvent.VK_R || code == KeyEvent.VK_O || code == KeyEvent.VK_G) {
                            g.getNight().frog.frogActivation += e.getKeyChar();
                        }
                    }
                    if(g.night.getBoykisser().isAwaitingResponse()) {
                        if (code == KeyEvent.VK_1 || code == KeyEvent.VK_2) {
                            g.night.getBoykisser().leave();
                        }
                    }
                    if(g.getNight().getEvent() == GameEvent.ASTARTA) {
                        if(g.getNight().getAstartaBoss() != null) {
                            if(g.getNight().getAstartaBoss().getDyingStage() == 10) {
                                if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D || code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
                                    g.sound.play("select", 0.1);
                                    g.getNight().getAstartaBoss().setEndingChoice(!g.getNight().getAstartaBoss().getEndingChoice());
                                }
                                if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE || code == KeyEvent.VK_Z) {
                                    g.sound.play("riftSelect", 0.3);
                                    g.fadeOut(0, 255, 1);
                                    g.getNight().getAstartaBoss().setDyingStage((byte) 11);

                                    new Pepitimer(() -> {
                                        g.sound.stop();
                                        GamePanel.mirror = false;

                                        Cutscene cutscene = Presets.voidEnding(g);
                                        cutscene.setAntiAliasing(true);

                                        g.currentCutscene = cutscene;

                                        g.stopAllSound();
                                        if(g.inCam) {
                                            g.camOut(false);
                                        }

                                        g.state = GameState.CUTSCENE;

                                        g.fadeOut(255, 20, 4);

                                        g.music.play("void", 0.1);

                                        new Pepitimer(cutscene::nextScene, 30060);
                                        new Pepitimer(() -> g.stopGame(true), 32500);
                                    }, 5000);
                                }
                            }
                        }
                    }
                    if(g.getNight().isInGeneratorMinigame()) {
                        if (code == KeyEvent.VK_Z || code == KeyEvent.VK_SPACE) {
                            int x0 = g.fixedUpdatesAnim * 5 + (int) (Math.sin(g.fixedUpdatesAnim * 0.05) * 14);
                            while (x0 > 630) {
                                x0 -= 630;
                            }
                            Rectangle rect1 = new Rectangle(218 + x0, 485, 14, 130);

                            int order = 0;
                            for(short x : g.getNight().generatorXes.clone()) {
                                Rectangle rect2 = new Rectangle(220 + x, 495, 25, 110);

                                if(rect1.intersects(rect2) && g.getNight().generatorXes[order] != -1) {
                                    g.getNight().generatorXes[order] = -1;

                                    short[] array = g.getNight().generatorXes;
                                    if(array[0] == -1 && array[1] == -1 && array[2] == -1) {
                                        g.getNight().generatorStage++;
                                        g.getNight().generatorXes[0] = (short) (30 + Math.random() * 580);
                                        g.getNight().generatorXes[1] = (short) (30 + Math.random() * 580);
                                        g.getNight().generatorXes[2] = (short) (30 + Math.random() * 580);

                                        g.generatorSound.play("generatorNextStage", 0.1);

                                        if(g.getNight().generatorStage >= 4) {
                                            g.getNight().setGeneratorEnergy(103);
                                            g.getNight().inGeneratorMinigame = false;
                                            g.generatorSound.stop();
                                            g.generatorSound.play("generatorNextStage", 0.1);
                                            g.generatorSound.play("generatorOver", 0.1);
                                        }
                                    } else {
                                        g.generatorSound.play("generatorSuccess", 0.1);
                                    }
                                    return;
                                }
                                order++;
                            }

                            if(Math.random() < 0.6) {
                                if (g.getNight().generatorStage > 0) {
                                    g.getNight().generatorStage--;
                                }
                            }
                            g.getNight().generatorXes[0] = (short) (30 + Math.random() * 580);
                            g.getNight().generatorXes[1] = (short) (30 + Math.random() * 580);
                            g.getNight().generatorXes[2] = (short) (30 + Math.random() * 580);

                            g.generatorSound.play("generatorFail", 0.1);
                        }
                    }
                }
                case BATTERY_SAVER -> {
                    if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D || code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
                        g.sound.play("select", 0.1);
                        g.getNight().getAstartaBoss().setBatterySaveChoice(!g.getNight().getAstartaBoss().getBatterySaveChoice());
                    }

                    if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE || code == KeyEvent.VK_Z) {
                        g.sound.play("select", 0.1);
                        g.getNight().getAstartaBoss().setBatterySaveMode(g.getNight().getAstartaBoss().getBatterySaveChoice());

                        if(g.getNight().getAstartaBoss().getBatterySaveChoice()) {
                            if (g.night.getEnergy() > g.night.getMaxEnergy()) {
                                g.night.setMaxEnergy(g.night.getEnergy());
                            }
                            g.night.setEnergy(Math.min(g.night.getEnergy() + 250, g.night.getMaxEnergy()));
                        }

                        g.state = GameState.GAME;
                        GamePanel.mirror = true;
                        float rate = g.getNight().getAstartaBoss().hasMusicSpedUp() ? 1.1F : 1;
                        g.music.playRateLooped("astartaFight", 0.06, rate);
                    }
                }
                case MILLY -> {
                    if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
                        if(g.selectedMillyItem < 4) {
                            byte started = g.selectedMillyItem;
                            g.selectedMillyItem++;

                            while (g.millyShopItems[g.selectedMillyItem] == null) {
                                g.selectedMillyItem++;

                                if(g.selectedMillyItem > 4) {
                                    g.selectedMillyItem = started;
                                    break;
                                }
                            }
                        }
                        if(g.millyBackButtonSelected) {
                            g.selectedMillyItem = 0;
                            g.millyBackButtonSelected = false;

                            while (g.millyShopItems[g.selectedMillyItem] == null && g.selectedMillyItem < 4) {
                                g.selectedMillyItem++;

                                if(g.selectedMillyItem == 4) {
                                    g.millyBackButtonSelected = true;
                                }
                            }
                        }
                    } else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
                        if (g.selectedMillyItem > 0) {
                            g.selectedMillyItem--;

                            while (g.millyShopItems[g.selectedMillyItem] == null) {
                                g.selectedMillyItem--;

                                if(g.selectedMillyItem < 0) {
                                    g.millyBackButtonSelected = true;
                                    break;
                                }
                            }
                        } else {
                            g.millyBackButtonSelected = true;
                        }
                    }
                    if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE || code == KeyEvent.VK_Z) {
                        processMillyPress();
                    }
                }
                case SOUNDTEST -> {
//                        if(code == KeyEvent.VK_X) {
//                            soundTest.valueWav++;
//                        }
//                        if(code == KeyEvent.VK_Z) {
//                            soundTest.valueWav--;
//                        }
                    if (code == KeyEvent.VK_V) {
                        soundTest.valueMP3 = soundTest.mathRound(soundTest.valueMP3 + 0.01);
                    }
                    if (code == KeyEvent.VK_C) {
                        soundTest.valueMP3 = soundTest.mathRound(soundTest.valueMP3 - 0.01);
                    }
//                        if(code == KeyEvent.VK_O) {
//                            Sound s = soundTest.sound;
//                            s.stop();
//                            s.play(soundTest.getCode(), soundTest.valueWav);
//                        }
                    if (code == KeyEvent.VK_P) {
                        SoundMP3 s = soundTest.sound;
                        s.stop();
                        s.play(soundTest.getCode(), soundTest.valueMP3);
                    }
                    if (code == KeyEvent.VK_UP) {
                        if (soundTest.currentCode < soundTest.sound.jfxPlayer.soundSet.keySet().size() - 1) {
                            soundTest.currentCode++;
                        } else {
                            soundTest.currentCode = 0;
                        }
                    }
                    if (code == KeyEvent.VK_DOWN) {
                        if (soundTest.currentCode > 0) {
                            soundTest.currentCode--;
                        } else {
                            soundTest.currentCode = soundTest.sound.jfxPlayer.soundSet.keySet().size() - 1;
                        }
                    }
                    if (code == KeyEvent.VK_SPACE) {
                        soundTest.sound.stop();
//                            soundTest.soundMP3.stop();
                    }
                }
                case RIFT -> {
                    if(g.riftItemsSelected[1] == null && g.riftText.equals("Welcome to the rift!")
                    && !g.everyFixedUpdate.containsKey("riftAccelerateUp")) {

                        byte index = (byte) (g.riftItems.indexOf(g.selectedRiftItem));

                        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
                            if(index >= g.riftItems.size() - 1) {
                                index = (byte) (0);
                            } else {
                                index++;
                            }
                            g.sound.play("select", 0.1);
                        } else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
                            if(index <= 0) {
                                index = (byte) (g.riftItems.size() - 1);
                            } else {
                                index--;
                            }
                            g.sound.play("select", 0.1);
                        }
                        g.selectedRiftItem = g.riftItems.get(index);

                        if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE || code == KeyEvent.VK_Z) {
                            g.sound.play("riftSelect", 0.3);
                            g.riftItems.remove(g.selectedRiftItem);

                            if (g.riftItemsSelected[0] == null) {
                                g.riftItemsSelected[0] = g.selectedRiftItem;
                                Statistics.ITEMS_RIFTED.increment();
                            } else {
                                g.riftItemsSelected[1] = g.selectedRiftItem;
                                Statistics.ITEMS_RIFTED.increment();

                                g.riftItemsSelected[0].safeAdd(1);
                                g.riftItemsSelected[1].safeAdd(1);

                                if (g.riftItemsSelected[0].getId().equals("birthdayHat") || g.riftItemsSelected[1].getId().equals("birthdayHat")) {
                                    g.birthdayHat.safeAdd(9);
                                }

                                new Pepitimer(() -> {
                                    g.riftTint = 0;
                                    g.everySecond20th.put("riftTint", () -> {
                                        if(g.riftTint < 251) {
                                            g.riftTint += 4;
                                        } else {
                                            g.everySecond20th.remove("riftTint");
                                        }
                                    });

                                    new Pepitimer(() -> {
                                        try {
                                            g.fadeOut(255, 160, 1);
                                            g.stopGame(true);
                                            AchievementHandler.obtain(g, Achievements.RIFT);
                                        } catch (Exception ignored) { }
                                    }, 3400);
                                }, 2000);
                            }

                            byte newIndex = 0;
                            while (newIndex < g.riftItems.size()) {
                                if (g.riftItems.get(newIndex) != null) {
                                    g.selectedRiftItem = g.riftItems.get(newIndex);
                                    break;
                                }
                                newIndex++;
                            }
                        }
                    }
                }
                case PLATFORMER -> {
                    switch (code) {
                        case KeyEvent.VK_SPACE -> {
                            g.platformer.jump();
                        }
                        case KeyEvent.VK_A -> {
                            g.platformer.pressedLeft = true;
                        }
                        case KeyEvent.VK_D -> {
                            g.platformer.pressedRight = true;
                        }
                    }
                }
            }

//            if (String.valueOf(e.getKeyChar()).equals("-")) {
            if (code == KeyEvent.VK_F3) {
                g.stopAllSound();
                g.state = GameState.SOUNDTEST;
                soundTest = new SoundTest(g);
                g.fadeOutStatic(0, 0, 0);
            }

            String str = String.valueOf(e.getKeyChar());
            if(str.equals("-") || str.equals("+") || str.equals("=")) {
                if(str.equals("-")) {
                    g.volume = Math.min(1, Math.max(0, g.volume - 0.05F));
                } else {
                    g.volume = Math.min(1, Math.max(0, g.volume + 0.05F));
                }

                for(MediaPlayer player : g.music.clips) {
                    player.setVolume(g.music.clipVolume.get(player) * g.volume);
                }
                for(MediaPlayer player : g.sound.clips) {
                    if(g.sound.clipVolume.containsKey(player)) {
                        player.setVolume(g.sound.clipVolume.get(player) * g.volume);
                    }
                }
                for(MediaPlayer player : g.scaryCatSound.clips) {
                    player.setVolume(g.scaryCatSound.clipVolume.get(player) * g.volume);
                }
                for(MediaPlayer player : g.generatorSound.clips) {
                    player.setVolume(g.generatorSound.clipVolume.get(player) * g.volume);
                }
                for(MediaPlayer player : g.bingoSound.clips) {
                    player.setVolume(g.bingoSound.clipVolume.get(player) * g.volume);
                }

                g.quickVolumeSeconds = 2;
                g.quickVolumeY = 0;

                g.everySecond.remove("quickVolumeSeconds");
                g.everyFixedUpdate.remove("quickVolumeY");

                g.everySecond.put("quickVolumeSeconds", () -> {
                   if(g.quickVolumeSeconds > 0) {
                       g.quickVolumeSeconds--;
                   } else {
                       g.everyFixedUpdate.put("quickVolumeY", () -> {
                           if(g.quickVolumeY > -120) {
                               g.quickVolumeY -= 3;
                           } else {
                               g.everyFixedUpdate.remove("quickVolumeY");
                           }
                       });
                       g.everySecond.remove("quickVolumeSeconds");
                   }
                });
            }

//            if(code == KeyEvent.VK_NUMPAD0) {
//                g.debugMode = !g.debugMode;
//            }


//            if(g.debugMode) {
//                if(e.isControlDown()) {
//                    if(code == KeyEvent.VK_NUMPAD1) {
//                        g.jumpscare("pepito");
//                    }
//                    if(code == KeyEvent.VK_NUMPAD2) {
//                        g.fadeOut(0, 255, 1);
//                    }
//                    if(code == KeyEvent.VK_NUMPAD3) {
//                        if(e.isShiftDown()) {
//                            g.adblocker.add(1);
//                        } else {
//                            g.maxwell.add(1);
//                        }
//                        g.updateItemList();
//                        g.sound.play("boop", -25F);
//                    }
//                    if (code == KeyEvent.VK_NUMPAD4) {
//                        g.usage++;
//                    }
//                    if (code == KeyEvent.VK_NUMPAD5) {
//                        g.notPepitoChance = 99;
//                    }
//                    if (code == KeyEvent.VK_NUMPAD6) {
//                        g.energy = 20;
//                    }
//                    if (code == KeyEvent.VK_NUMPAD7) {
//                        g.nightSeconds = (short) (g.nightDuration - 2);
//                    }
//                    if (code == KeyEvent.VK_NUMPAD8) {
//                        g.nightSeconds++;
//                    }
//                    if(code == KeyEvent.VK_NUMPAD9) {
//                        g.adblockerStatus = 1;
//                        g.adblockerTimer = 10;
//
//                        g.adblockerPoint.x = (short) (20 + Math.round(Math.random() * 1040));
//                        g.adblockerPoint.y = (short) (20 + Math.round(Math.random() * 600));
//                        g.adblockerButton = new Rectangle((short) (g.adblockerPoint.x * g.widthModifier + g.centerX), (short) (g.adblockerPoint.y * g.heightModifier + g.centerY), (short) (100 * g.widthModifier), (short) (100 * g.heightModifier));
//                    }
//                } else {
//                    // CONTROL UP
//                    if (code == KeyEvent.VK_NUMPAD1) {
//                        g.glitcher.spawn();
//                    }
//                    if (code == KeyEvent.VK_NUMPAD2) {
//                        g.a90.arrivalSeconds = 1;
//                    }
//                    if (code == KeyEvent.VK_NUMPAD3) {
//                        g.msi.arrivalSeconds = 1;
//                    }
//                    if (code == KeyEvent.VK_NUMPAD4) {
//                        g.secondsUntilPepito = 1;
//                    }
//                    if (code == KeyEvent.VK_NUMPAD5) {
//                        g.astarta.spawn();
//                    }
//                }
//          }
        }

        if(stopPause)
            return;

        if(e.getKeyChar() == KeyEvent.VK_ESCAPE) {
            g.lastBeforePause = g.lastFullyRenderedUnshaded;
            g.lastBeforePause = GamePanel.darkify(g.lastBeforePause, 3);
            g.paused = !g.paused;

            if(g.paused) {
                previous = g.state;
                g.state = GameState.UNLOADED;
                g.pauseAllSound();
                for (Pepitimer pepitimer : StaticLists.timers) {
                    pepitimer.pause();
                }
            } else {
                unpause();
            }
        }
    }

    Pepitimer freezeChange;
    boolean infiniteMoneyGlitch = false;

    public void processMillyPress() {
        if(g.millyBackButtonSelected) {
            g.state = GameState.GAME;
            g.music.stop();
            g.sound.stop();
            g.sound.play("select", 0.08);
            if(g.secondsInMillyShop >= 3600) {
//                g.sound.play("dreadDie", 0.3);
            }
            g.repaintOffice();
            g.fadeOut(255, 80, 1);

            if(millyDisco != null) {
                millyDisco.cancel(false);
            }
        } else {
            if (g.endless.getCoins() >= g.millyShopItems[g.selectedMillyItem].getPrice() || infiniteMoneyGlitch) {
                g.endless.addCoins(-g.millyShopItems[g.selectedMillyItem].getPrice());
                Statistics.ITEMS_BOUGHT.increment();

                if(g.millyShopItems[g.selectedMillyItem].getItem().getId().equals("starlightBottle")) {
                    g.starlightBottle.safeAdd(1);
                } else {
                    g.millyShopItems[g.selectedMillyItem].getItem().enable();

                    if (g.fullItemList.contains(g.millyShopItems[g.selectedMillyItem].getItem())) {
                        g.usedItems.add(g.millyShopItems[g.selectedMillyItem].getItem());
                    }
                }

                if(g.millyShopItems[g.selectedMillyItem].getItem().getId().equals("adblocker")) {
                    g.adBlocked = false;
                }
                if(g.millyShopItems[g.selectedMillyItem].getItem() instanceof Corn c) {
                    c.reset();
                }
                g.millyShopItems[g.selectedMillyItem] = null;

                g.sound.play("select", 0.08);
                g.sound.play("sellsYourBalls", 0.2);

                AchievementHandler.obtain(g, Achievements.MILLY);

                if(Arrays.equals(g.millyShopItems, new MillyItem[5])) {
                    AchievementHandler.obtain(g, Achievements.SHOPPING_SPREE);
                }

                g.redrawMillyShop();

                int i = 0;
                while(i < 5) {
                    if(g.millyShopItems[i] != null) {
                        if (g.millyRects[i].contains(pointerPosition)) {
                            g.selectedMillyItem = (byte) i;
                            g.millyBackButtonSelected = false;
                            break;
                        }
                    }
                    i++;
                }

                while (g.millyShopItems[g.selectedMillyItem] == null) {
                    g.selectedMillyItem--;

                    if (g.selectedMillyItem < 0) {
                        g.millyBackButtonSelected = true;
                        break;
                    }
                }
            } else {
                g.sound.play("error", 0.08);
            }
        }
    }

    boolean holdingB = false;

    SoundTest soundTest;

    Rat a;

    public boolean mouseHeld = false;
    public boolean isRightClick = false;
    boolean holdingVolumeButton = false;
    boolean hoveringNightReset = false;
    boolean confirmNightReset = false;
    boolean hoveringFpsCap = false;
    boolean hoveringJumpscareShake = false;

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_B) {
            holdingB = false;
        }
        if(g.state == GameState.PLATFORMER) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A -> {
                    g.platformer.pressedLeft = false;
                }
                case KeyEvent.VK_D -> {
                    g.platformer.pressedRight = false;
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent event) {
        boolean cancelMouseHolding = false;

        Point rescaledPoint = new Point((int) ((pointerPosition.x - g.centerX) / g.widthModifier), (int) ((pointerPosition.y - g.centerY) / g.heightModifier));

        if(g.state == GameState.DISCLAIMER) {
            g.launch();
            return;
        }
        if(g.pressAnyKey) {
            if(g.riftItems.size() >= 2 && g.endless.getNight() >= 3) {
                if(g.riftTransparency <= 0) {
                    g.enterRift();
                }
            } else {
                if(processDeathScreenPress())
                    return;

                g.stopGame(true);
            }
            cancelMouseHolding = true;
        } else {

            isRightClick = event.getButton() == MouseEvent.BUTTON3;

            switch (g.state) {
                case UNLOADED -> {
                    if(previous == GameState.GAME) {
                        if(g.pauseDieSelected && g.getNight().getEvent().isInGame()) {
                            unpause();
                            g.paused = false;
                            g.jumpscare("pause");
                        }
                    }
                }
                case MENU -> {
                    if (g.discord == g.discordStates[1]) {
                        try {
                            g.discord = g.discordStates[0];
                            Desktop.getDesktop().browse(new URL("https://discord.gg/r3re2hXu7k").toURI());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (pointerPosition.x < 520 * g.widthModifier + g.centerX) {
                        short x = (short) g.visibleMenuButtons.size();
                        if (pointerPosition.y > (-40 * x + 520) * g.heightModifier + g.centerY) {
                            clickMenuButton();
                        }
                    }
                    if (g.huh.contains(pointerPosition)) {
                        g.sound.play("boop", 0.1);
                        g.music.stop();
                        g.music.play("zut", 0.05, true);
                        g.state = GameState.CREDITS;
                        g.fadeOutStatic(1F, 0.3F, 0.00005F);
                    }
                }
                case GAME -> {
                    g.getNight().afk = 17;
                    g.recalculateButtons(GameState.GAME);

                    if(g.getNight().getType() == GameType.SHADOW) {
                        if (GamePanel.mirror) {
                            rescaledPoint = new Point(1080 - rescaledPoint.x, rescaledPoint.y);
                        }
                    }

                    if (isRightClick) {
                        if(g.getNight().getMirrorCat().isActive()) {
                            int rectX = g.offsetX - 400 + g.getNight().getMirrorCat().getX() + g.currentWaterPos * 2;
                            if(GamePanel.isMirror()) {
                                rectX = 895 - rectX;
                            }

                            Rectangle rect = new Rectangle((int) (rectX * g.widthModifier) + g.centerX, (int) ((540 - g.kys()) * g.heightModifier) + g.centerY,  (int) (185 * g.widthModifier), (int) (100 * g.heightModifier));
                            if(rect.contains(pointerPosition)) {
                                g.getNight().getMirrorCat().kill();
                                return;
                            }
                        }

                        if (g.flashlight.isEnabled() && (g.getNight().getEvent() == GameEvent.NONE)) {
                            if (g.night.hasPower()) {
                                if (g.flashLightCooldown == 0) {
                                    g.sound.play("sodaOpen", 0.03);
                                    g.sound.play("camOut", 0.1);
                                    g.fadeOut(0, g.endFade, 20);
                                    g.night.addEnergy(-20F);
                                    g.flashLightCooldown = 28;

                                    if (g.night.getMSI().isActive()) {
                                        g.night.getMSI().kill(true);
                                        g.night.addEnergy(-4F);
                                    } else {
                                        for (int i : g.getNight().getDoors().keySet()) {
                                            Door door = g.getNight().getDoors().get(i);
                                            Polygon hitbox = new Polygon(door.getHitbox().xpoints, door.getHitbox().ypoints, door.getHitbox().npoints);
                                            hitbox.translate(g.offsetX - 400, 0);

                                            if (hitbox.contains(rescaledPoint)) {
                                                door.setHovering(door.getHitbox().contains(rescaledPoint));
                                                if(!door.isClosed()) {
                                                    if (g.night.getAstarta().isActive()) {
                                                        if (g.night.getAstarta().door == i) {
                                                            g.night.getAstarta().leaveEarly();

                                                            BingoHandler.completeTask(BingoTask.SURVIVE_ASTARTA);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    g.sound.play("error", 0.08);
                                }
                            } else {
                                g.sound.play("error", 0.08);
                            }
                        } else if(g.getNight().getEvent() == GameEvent.LEMONADE) {
                            g.getNight().getLemonadeCat().throwLemonade(pointerPosition, GamePanel.isMirror());
                        }
                    } else if (g.boopButton.contains(pointerPosition)) {
                        g.sound.play("boop", 0.08);
                        cancelMouseHolding = true;

                        BingoHandler.completeTask(BingoTask.BOOP_PEPITO);

                        if (g.sensor.isEnabled()) {
                            g.console.add("boop");
                        }
                        if (g.getNight().getWires().isActive() && g.getNight().getWires().getState() == 0) {
                            g.getNight().getWires().hit();
                        }
                    } else if(g.manualFirstButtonHover) {
                        cancelMouseHolding = true;

                        if(g.manualY < 535) {
                            g.manualHide();
                        } else {
                            g.manualOpen();
                        }
                    } else if(g.manualSecondButtonHover) {
                        cancelMouseHolding = true;

                        g.manualClose();
                    } else {
                        for (int i : g.getNight().getDoors().keySet()) {
                            Door door = g.getNight().getDoors().get(i);

                            if (door.getButtonHitbox(g.offsetX).contains(rescaledPoint)) {
                                if (!g.night.getA120().isActive()) {
                                    if (g.night.hasPower() || g.night.getGeneratorEnergy() > 0) {
                                        if(g.night.isTimerModifier()) {
                                            g.doorTimerStuff(door);
                                        } else {
                                            if (door.isClosed()) {
                                                g.usage--;
                                            } else {
                                                g.usage++;
                                            }

                                            door.setClosed(!door.isClosed());
                                        }
                                        g.redrawUsage();

                                        int doorPos = (int) (door.getHitbox().getBounds().x + door.getHitbox().getBounds().width / 2F);
                                        float pan = (float) Math.sin(1.57 * (doorPos / 740F - 1)) / 1.3F;
                                        g.sound.play("doorSlam", 0.08, pan);

                                        if(g.getNight().getWires().isActive() && g.getNight().getWires().getState() == i + 1) {
                                            g.getNight().getWires().hit();
                                        }
                                    } else {
                                        g.sound.play("error", 0.08);
                                    }
                                    cancelMouseHolding = true;
                                    break;
                                } else if (i == 1 && door.isClosed()) {
                                    g.sound.play("error", 0.08);
                                    new Notification("Something is blocking this door.");
                                }
                                cancelMouseHolding = true;
                            }
                        }
                    }
                    if(g.getNight().getAstartaBoss() != null) {
                        if(g.getNight().getAstartaBoss().isStartingCutscene()) {
                            if(g.shadowCheckpointUsed != 0 && g.getNight().getAstartaBoss().getStartCutsceneY() != 0) {
                                g.getNight().getAstartaBoss().skipStartCutscene();
                                g.fadeOut(255, 0, 3);
                                return;
                            }
                        }
                        Mister mister = g.getNight().getAstartaBoss().getMister();
                        if(mister.isActive()) {
                            if (mister.isInsideHitbox(pointerPosition, GamePanel.isMirror(), g)) {
                                mister.setBeingHeld(true);
                                mister.setBloomTransparency(1F);

                                if(g.getNight().getAstartaBoss().isFirstMister()) {
                                    g.getNight().getAstartaBoss().setSpeedModifier(0.5F);
                                } else {
                                    g.getNight().getAstartaBoss().setSpeedModifier(0.8F);
                                }
                                for (MediaPlayer player : g.music.clips) {
                                    player.setRate(player.getRate() * g.getNight().getAstartaBoss().getSpeedModifier());
                                }
                                mouseHeld = true;
                                mister.cursorToPoint(pointerPosition, GamePanel.isMirror(), g);

                                defaultCursor = g.getCursor();
                                BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                                Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                                        cursorImg, new Point(0, 0), "blank cursor");
                                g.setCursor(blankCursor);
                                return;
                            }
                        }
                    }

                    if(g.shadowTicket.isEnabled()) {
                        if(Achievements.HALFWAY.isObtained() && g.shadowCheckpointUsed == 0) {
                            Point point1 = new Point(540, 320);
                            Point point2 = null;

                            if(g.reachedAstartaBoss) {
                                point1 = new Point(320, 320);
                                point2 = new Point(760, 320);
                            }

                            if(point1.distance(rescaledPoint) < 160) {
                                g.shadowCheckpointUsed = 1;
                                g.sound.play("checkpointSelect", 0.12);
                                if(g.shadowTicketTimer != null) {
                                    g.shadowTicketTimer.setMiliseconds(Math.min(400, g.shadowTicketTimer.getMiliseconds()));
                                }
                                return;
                            }
                            if(point2 != null) {
                                if(point2.distance(rescaledPoint) < 160) {
                                    g.sound.play("checkpointSelect", 0.12);
                                    if(g.shadowTicketTimer != null) {
                                        g.shadowTicketTimer.setMiliseconds(Math.min(400, g.shadowTicketTimer.getMiliseconds()));
                                    }
                                    g.shadowCheckpointUsed = 2;
                                    return;
                                }
                            }
                        }
                    } else if(g.getNight().isPowerModifier()) {
                        if (new Rectangle(g.offsetX - 400 + 790, 480, 220, 150).contains(rescaledPoint) && !g.getNight().isInGeneratorMinigame() && !(g.getNight().getGeneratorEnergy() != -1)) {
                            g.getNight().startGeneratorMinigame();
                            g.hoveringGenerator = false;
                        }
                    }

                    for(int i = 0; i < GamePanel.balloons.size(); i++) {
                        Rectangle button = GamePanel.balloons.get(i).getRectangle(g.offsetX, g.widthModifier, g.heightModifier, g.centerX, g.centerY);
                        if (button.contains(pointerPosition)) {
                            GamePanel.balloons.remove(GamePanel.balloons.get(i));
                            g.sound.play("balloonPop", 0.05);

                            if(GamePanel.balloons.isEmpty()) {
                                if(g.getNight().getEvent() != GameEvent.MAXWELL && g.getNight().getType() == GameType.PREPARTY) {
                                    g.type = GameType.PARTY;
                                    g.startGame();
                                }
                            }
                            return;
                        }
                    }

                    if(g.getNight().getType() == GameType.DAY) {
                        if (new Rectangle((int) ((g.offsetX + 685) * g.widthModifier) + g.centerX, (int) (315 * g.heightModifier) + g.centerY, (int) (315 * g.widthModifier), (int) (325 * g.heightModifier)).contains(pointerPosition)) {
                            g.state = GameState.MILLY;
                            g.secondsInMillyShop = 0;
                            g.dreadUntilGrayscale = 1;
                            g.dreadUntilVignette = 1;
                            g.sound.stop();

                            if (g.fanActive) {
                                fanSounds.stop();
                                g.fanActive = false;
                                g.usage--;

                                g.everySecond20th.remove("fan");
                            }

                            if (g.inCam) {
                                g.camOut(false);
                            }

                            if(g.endless.getNight() == 6) {
                                g.music.play("partyFavors", 0.08, true);

                                final float[] millyPartyHue = {0};
                                HashMap<Float, BufferedImage> millyPartyCache = new HashMap<>();

                                millyDisco = new RepeatingPepitimer(() -> {
                                    millyPartyHue[0] += 0.05F;
                                    if (millyPartyHue[0] >= 1) {
                                        millyPartyHue[0] = 0;
                                    }
                                    if(!millyPartyCache.containsKey(millyPartyHue[0])) {
                                        millyPartyCache.put(millyPartyHue[0], GamePanel.changeHue(g.millyShopColors.request(), millyPartyHue[0]));
                                    }
                                    g.millyShopColorsChanging = millyPartyCache.get(millyPartyHue[0]);
                                }, 100, 100);
                            } else {
                                g.music.play("millyShop", 0.1, true);
                            }
                            g.fadeOut(255, 0, 2);

                            g.redrawMillyShop();
                            g.recalculateMillyRects();
                        }
                    }

                    if (g.inCam) {
                        if (g.adblockerStatus == 1) {
                            if (g.adblockerButton.contains(pointerPosition)) {
                                g.adblockerStatus = 2;
                                g.sound.play("a90Alive", 0.07);

                                BingoHandler.completeTask(BingoTask.FIND_ADBLOCKER);
                            }
                        } else if(g.getNight().getEvent() == GameEvent.MAXWELL) {
                            Rectangle button = new Rectangle((int) (690 * g.widthModifier + g.centerX), (int) (50 * g.heightModifier + g.centerY), (int) (340 * g.widthModifier), (int) (110 * g.heightModifier));

                            if(!g.maxwellActive) {
                                if (button.contains(pointerPosition)) {
                                    g.maxwellActive = true;
                                    g.sound.play("blip", 0.15);

//                                    BingoHandler.completeTask(BingoTask.GET_BURN_ENDING);

                                    new Pepitimer(() -> {
                                        Cutscene cutscene = Presets.maxwellEnding(g);
                                        cutscene.setAntiAliasing(true);

                                        g.currentCutscene = cutscene;

                                        g.stopAllSound();
                                        GamePanel.balloons.clear();
                                        g.camOut(false);

                                        g.state = GameState.CUTSCENE;

                                        g.fadeOut(255, 20, 4);

                                        g.music.play("malh", 0.2);

                                        new Pepitimer(() -> {
                                            g.fadeOut(200, 20, 10);
                                            cutscene.nextScene();

                                            new Pepitimer(() -> {
                                                g.fadeOut(255, 20, 20);
                                                cutscene.nextScene();

                                                new Pepitimer(() -> {
                                                    g.fadeOut(255, 40, 4);
                                                    cutscene.nextScene();

                                                    new Pepitimer(() -> {
                                                        g.fadeOut(255, 40, 2);
                                                        cutscene.nextScene();

                                                        new Pepitimer(() -> {
                                                            g.fadeOut(255, 40, 2);
                                                            cutscene.nextScene();

                                                            new Pepitimer(() -> {
                                                                g.winCount++;
                                                                g.pressAnyKey = true;

                                                                g.sound.play("boop", 0.05);
                                                            }, 14000);
                                                        }, 4000);
                                                    }, 3000);
                                                }, 3200);
                                            }, 3200);
                                        }, 2000);
                                    }, 1000);

                                    // 0 - maxwell rising
                                    // 1 - maxwell firing
                                    // 2 - exploded office
                                    // 3 - office burning
                                    // 4 - enemies burning
                                    // 5 - pepito looks at the office
                                }
                            }
                        } else if(g.portalActive) {
                            Rectangle button = new Rectangle((int) (690 * g.widthModifier + g.centerX), (int) (50 * g.heightModifier + g.centerY), (int) (340 * g.widthModifier), (int) (110 * g.heightModifier));

                            if(!g.portalTransporting) {
                                if (button.contains(pointerPosition)) {
                                    g.portalTransporting = true;
                                    g.sound.play("blip", 0.15);

                                    g.riftTint = 0;
                                    g.everySecond20th.put("riftTint", () -> {
                                        if (g.riftTint < 251) {
                                            g.riftTint += 4;
                                        } else {
                                            g.everySecond20th.remove("riftTint");
                                        }
                                    });

                                    new Pepitimer(() -> {
                                        g.loading = true;
                                        g.state = GameState.UNLOADED;
                                        g.camOut(true);

                                        new Pepitimer(() -> {
                                            GamePanel.mirror = true;
                                            g.type = GameType.SHADOW;
                                            g.fadeOutStatic(0, 0, 0);
                                            g.state = GameState.HALFLOADED;
                                            g.soggyBallpit.disable();
                                            g.soggyBallpitActive = false;

                                            g.riftAnimation(() -> {
                                                g.startGame();

                                                g.endless = null;
                                                g.portalTransporting = false;
                                                g.riftTint = 0;
                                                g.portalActive = false;
                                            });
                                        }, 8000);
                                    }, 4000);
                                }
                            }
                        }
                    }
                }
                case BINGO -> {
                    if (g.closeButton.contains(pointerPosition)) {
                        g.backToMainMenu();
                    } else {
                        Rectangle rect = new Rectangle(g.centerX, (int) (530 * g.widthModifier + g.centerY), (int) (480 * g.widthModifier), (int) (100 * g.heightModifier));

                        if(rect.contains(pointerPosition)) {
                            clickBingoButton();
                        }
                    }
                }
                case ACHIEVEMENTS -> {
                    if (g.closeButton.contains(pointerPosition)) {
                        g.backToMainMenu();
                    } else {
                        if (!g.shiftingAchievements) {
                            if (g.achievementState) {
                                if (pointerPosition.x < 140 * g.widthModifier + g.centerX) {
                                    shiftAchievements();
                                }
                            } else if (pointerPosition.x > 940 * g.widthModifier + g.centerX) {
                                shiftAchievements();
                            }
                        }
                    }
                }
                case PLAY -> {
                    if (g.closeButton.contains(pointerPosition)) {
                        g.backToMainMenu();
                    } else {
                        clickPlayButton();
                    }
                }
                case SETTINGS -> {
                    if (new Rectangle((int) (g.volume * 800 + 115), 220 + g.settingsScrollY, 50, 50).contains(rescaledPoint)) {
                        holdingVolumeButton = true;
                    } else if (new Rectangle(550, 310 + g.settingsScrollY, 60, 60).contains(rescaledPoint)) {
                        g.blackBorders = !g.blackBorders;
                        g.onResizeEvent();
                        if (g.blackBorders) {
                            g.sound.play("select", 0.1);
                        }
                    } else if (new Rectangle(612, 390 + g.settingsScrollY, 60, 60).contains(rescaledPoint)) {
                        g.headphones = !g.headphones;
                        if (g.headphones) {
                            g.sound.play("select", 0.1);
                        }
                    } else if(new Rectangle(720, 630 + g.settingsScrollY, 60, 60).contains(rescaledPoint)) {
                        g.disclaimer = !g.disclaimer;
                        if (g.disclaimer) {
                            g.sound.play("select", 0.1);
                        }
                    } else if(new Rectangle(640, 710 + g.settingsScrollY, 60, 60).contains(rescaledPoint)) {
                        g.showManual = !g.showManual;
                        if (g.showManual) {
                            g.sound.play("select", 0.1);
                        }
                    } else if(new Rectangle(750, 790 + g.settingsScrollY, 60, 60).contains(rescaledPoint)) {
                        g.saveScreenshots = !g.saveScreenshots;
                        if (g.saveScreenshots) {
                            g.sound.play("select", 0.1);
                        }
                    } else if(new Rectangle(880, 870 + g.settingsScrollY, 60, 60).contains(rescaledPoint)) {
                        g.bloom = !g.bloom;
                        if (g.bloom) {
                            g.sound.play("select", 0.1);
                        }
                    } else if(new Rectangle(590, 950 + g.settingsScrollY, 60, 60).contains(rescaledPoint)) {
                        g.fpsCounters[0] = !g.fpsCounters[0];
                        if (g.fpsCounters[0]) {
                            g.sound.play("select", 0.1);
                        }
                    } else if (hoveringNightReset) {
                        if (confirmNightReset) {
                            g.sound.play("select", 0.1);
                            g.sound.play("metalPipe", 0.04);
                            g.currentNight = 1;
                            confirmNightReset = false;

                            g.reloadMenuButtons();
                        } else {
                            confirmNightReset = true;
                            g.sound.play("select", 0.1);
                        }
                    } else if(hoveringFpsCap) {
                        switch (g.fpsCap) {
                            case -1 -> g.fpsCap = 30;
                            case 30 -> g.fpsCap = 60;
                            case 120 -> g.fpsCap = 240;
                            case 240 -> g.fpsCap = -1;
                            default -> g.fpsCap = 120;
                        }
                        g.thousandFPS = Math.max(1, 1000 / g.fpsCap);

                        g.sound.play("select", 0.1);
                    } else if(hoveringJumpscareShake) {
                        switch (g.shake) {
                            case 0 -> g.shake = 1;
                            case 1 -> g.shake = 2;
                            default -> g.shake = 0;
                        }

                        g.sound.play("select", 0.1);
                    } else if (g.closeButton.contains(pointerPosition)) {
                        g.backToMainMenu();
                    }
                }
                case ITEMS -> {
                    if(g.everySecond20th.containsKey("startSimulation") && g.startSimulationTimer.getMiliseconds() > 300) {
                        g.greenItemsMenu = g.bloom(g.greenItemsMenu);
                        g.startSimulationTimer.setMiliseconds(300);
                        return;
                    }
                    if(g.everySecond20th.containsKey("startSimulation"))
                        return;

                    if (g.startButtonSelected) {
                        g.startGameThroughItems();
                    } else {
                        if (g.closeButton.contains(pointerPosition)) {
                            if(g.type == GameType.CUSTOM) {
                                g.startChallengeMenu(false);
                            } else {
                                g.backToMainMenu();
                            }
                        } else {
                            try {
                                Item item = g.itemList.get(g.selectedItemX + (g.selectedItemY * g.columns));

                                if (item.getAmount() != 0) {
                                    if (item.isSelected()) {
                                        item.deselect();
                                        g.itemLimit -= item.getItemLimitAdd();
                                    } else {
                                        if (g.checkItemsAmount() < g.itemLimit && !item.isMarkedConflicting()) {
                                            item.select();
                                            g.itemLimit += item.getItemLimitAdd();
                                            g.sound.play("select", 0.1, false);
                                        } else {
                                            g.sound.play("selectFail", 0.12, false);

                                            if(item.isMarkedConflicting()) {
                                                item.setShakeIntensity((byte) 60);
                                            }
                                        }
                                    }

                                    g.updateItemList();
                                    g.redrawItemsMenu();
                                } else {
                                    g.sound.play("selectFail", 0.12);
                                }
                            } catch (IndexOutOfBoundsException ignored) {
                                g.sound.play("selectFail", 0.12);
                            }
                        }
                    }
                }
                case MILLY -> {
                    processMillyPress();
                    cancelMouseHolding = true;
                }
                case CHALLENGE -> {
                    try {
                        if (rescaledPoint.x > 660 && rescaledPoint.y > 220) {
                            if (rescaledPoint.x < 1080 && rescaledPoint.y < 440) {
                                if(CustomNight.selectedElement instanceof CustomNightModifier) {
                                    if(CustomNight.isCustom()) {
                                        ((CustomNightModifier) CustomNight.selectedElement).toggle();
                                        g.sound.play("buttonPress", 0.08);
                                    } else {
                                        g.sound.play("lowSound", 0.05);
                                    }
                                }
                            }
                        }
                        if (rescaledPoint.x > 20 && rescaledPoint.y > 40) {
                            if (rescaledPoint.x < 640 && rescaledPoint.y < 420) {
                                if(CustomNight.selectedElement instanceof CustomNightEnemy) {
                                    if (CustomNight.isCustom()) {
                                        if (isRightClick) {
                                            ((CustomNightEnemy) CustomNight.selectedElement).declick();
                                        } else {
                                            ((CustomNightEnemy) CustomNight.selectedElement).click();
                                        }

                                        if(((CustomNightEnemy) CustomNight.selectedElement).getAI() == 0) {
                                            g.sound.play("lowSound", 0.05);
                                        } else {
                                            if (isRightClick) {
                                                g.sound.play("aiDown", 0.05);
                                            } else {
                                                g.sound.play("aiUp", 0.05);
                                            }
                                        }
                                    } else {
                                        CustomNightEnemy e = ((CustomNightEnemy) CustomNight.selectedElement);
                                        e.setWobbleIntensity(6);
                                        g.sound.play("lowSound", 0.05);
                                    }
                                }
                            }
                        }
                        if(CustomNight.startSelected) {
                            cancelLimbo();

                            g.type = GameType.CUSTOM;
                            g.startItemSelect();
                        }
                        if(CustomNight.backSelected) {
                            cancelLimbo();

                            g.backToMainMenu();
                        }
                        if(CustomNight.prevSelected) {
                            CustomNight.previousChallenge();
                            g.sound.play("buttonPress", 0.08);
                        }
                        if(CustomNight.nextSelected) {
                            CustomNight.nextChallenge();
                            g.sound.play("buttonPress", 0.08);
                        }
                        if(CustomNight.shuffleSelected) {
                            g.sound.play("buttonPress", 0.08);

                            if(CustomNight.isCustom()) {
                                byte newId = (byte) (Math.random() * 100 + 1);
                                CustomNight.limboId = newId;
                                g.music.stop();
                                g.music.play("limbos", 0.1);

                                new Pepitimer(() -> {
                                    if(CustomNight.limboId == newId) {
                                        CustomNight.limbo(g);
                                    }
                                }, 9800);
                                new Pepitimer(() -> {
                                    if(CustomNight.limboId == newId) {
                                        g.music.play("torture", 0.05, true);
                                    }
                                }, 19400);
                            }
                        }
                        if(CustomNight.customSelected) {
                            g.sound.play("buttonPress", 0.08);
                            CustomNight.custom = !CustomNight.custom;
                            CustomNight.setEntityAIs();

                            if(!CustomNight.custom) {
                                CustomNight.setEntityAIs();
                            }
                            cancelLimbo();
                        }
                    } catch (NullPointerException ignored) { }
                }
            }
        }

        if(!cancelMouseHolding) {
            mouseHeld = true;
        }
    }

    RepeatingPepitimer millyDisco;

    private boolean processDeathScreenPress() {
        if(g.state == GameState.GAME) {
            if(g.getNight().getEvent() == GameEvent.DYING) {
                if(g.deathScreenY != 640) {
                    if (!g.afterDeathText.isEmpty()) {
                        deathScreenYThing();
                        return true;
                    } else {
                        g.stopGame(true);
                        return true;
                    }
                } else if(g.afterDeathCurText.length() != g.afterDeathText.length()) {
                    g.afterDeathCurText = g.afterDeathText;
                    g.redrawDeathScreen();
                    return true;
                } else {
                    g.stopGame(true);
                    g.sound.play("riftSelect", 0.1);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseHeld = false;
        isRightClick = false;

        pointerPosition = e.getPoint().getLocation();
        setPoint();

        if(holdingVolumeButton) {
            g.music.stop();
            g.music.play("pepito", 0.2, true);
            holdingVolumeButton = false;
        }

        if(g.state == GameState.GAME) {
            if (g.getNight().getAstartaBoss() != null) {
                Mister mister = g.getNight().getAstartaBoss().getMister();
                if (mister.isActive() && mister.isBeingHeld()) {
                    g.unholdMister(mister);
                }
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(g.state == GameState.SETTINGS) {
            if(holdingVolumeButton) {
                pointerPosition = e.getPoint().getLocation();
                setPoint();

                float oldVolume = g.volume;
                g.volume = Math.min(1, Math.max((float) (((pointerPosition.x - g.centerX) / g.widthModifier - 140) / 800.0), 0));

                for(MediaPlayer player : g.music.clips) {
                    player.setVolume(player.getVolume() / oldVolume * g.volume);
                }
            }
        } else if(g.state == GameState.GAME) {
            g.recalculateButtons(GameState.GAME);

            if(g.getNight().getAstartaBoss() != null) {
                Mister mister = g.getNight().getAstartaBoss().getMister();
                if(mister.isActive()) {
                    if (mister.isBeingHeld()) {

                        Point newPoint = e.getPoint().getLocation();

                        if(robotMovement) {
                            pointerPosition = newPoint;
                            robotMovement = false;
                            return;
                        }

                        int dx = newPoint.x - pointerPosition.x;
                        if(GamePanel.isMirror())
                            dx = -dx;
                        int dy = newPoint.y - pointerPosition.y;
                        mister.translate(dx, dy);

                        if(g.getNight().getAstartaBoss().isBatterySaveMode()) {
                            dx *= 2;
                            dy *= 2;
                        }
                        mister.addVelocity(dx / 2F, dy / 2F);

                        pointerPosition = newPoint;
                        g.pointX = (int) (((GamePanel.isMirror() ? -mister.getPoint().x : mister.getPoint().x) - g.offsetX + 1080 + 250) * g.widthModifier + g.centerX);
                        g.pointY = (int) ((mister.getPoint().y + 100) * g.heightModifier + g.centerY);

                        if(pointerPosition.x > 100 * g.widthModifier + g.centerX && pointerPosition.x < 1000 * g.widthModifier + g.centerX && pointerPosition.y < 600 * g.heightModifier + g.centerY && pointerPosition.y > 120  * g.heightModifier + g.centerY)
                            return;

                        try {
                            robotMovement = true;
                            Robot robot = new Robot();
                            robot.mouseMove(g.center.x, g.center.y);
                        } catch (AWTException ex) {
                            throw new RuntimeException(ex);
                        }

                    }
                }
            }
        }
    }

    boolean robotMovement = false;

    Rat b;

    @Override
    public void mouseMoved(MouseEvent e) {
        Point oldPoint = pointerPosition;
        pointerPosition = e.getPoint().getLocation();
        setPoint();
        Point rescaledPoint = new Point((int) ((pointerPosition.x - g.centerX) / g.widthModifier), (int) ((pointerPosition.y - g.centerY) / g.heightModifier));

        switch (g.state) {
            case UNLOADED -> {
                if(previous == GameState.GAME) {
                    if (GamePanel.mirror) {
                        rescaledPoint = new Point(1080 - rescaledPoint.x, rescaledPoint.y);
                    }

                    g.pauseDieSelected = new Rectangle(100, 500, 220, 80).contains(rescaledPoint);
                }
            }
            case MENU -> {
                if(g.discordButton.contains(pointerPosition)) {
                    g.discord = g.discordStates[1];
                } else {
                    g.discord = g.discordStates[0];

                    short x = (short) g.visibleMenuButtons.size();
                    byte z = 0;
                    while (z < x) {
                        Rectangle rect = new Rectangle(g.centerX, (short) ((-10 * x * z - 40 * x + 100 * z + 530) * g.heightModifier + g.centerY),
                                (short) (520 * g.widthModifier), (short) ((-10 * x + 100) * g.heightModifier));

                        if(rect.contains(pointerPosition)) {
                            g.selectedOption = (byte) (z + g.menuButtonOffset);
                            break;
                        }
                        z++;
                    }
                }
            }
            case PLAY -> {
                if(pointerPosition.distance(oldPoint) > 2) {
                    g.playSelectorWaitCounter--;
                    PlayMenu.movedMouse = true;
                }
            }
            case ITEMS -> {
                if(rescaledPoint.x > 20 && rescaledPoint.y > (190 - 40 * Math.min(g.rows, 4) - g.itemScrollY)) {
                    if (rescaledPoint.x < 170 * g.columns && rescaledPoint.y < g.getItemMenuLimit()) {
                        g.startButtonSelected = false;
                        g.selectedItemX = (byte) ((rescaledPoint.x - 20) / 170.0);
                        g.selectedItemY = (byte) ((rescaledPoint.y - (190 - 40 * Math.min(g.rows, 4)) + g.itemScrollY) / 170.0);
                    }
                    if (rescaledPoint.x > 750 && rescaledPoint.y > 500) {
                        g.startButtonSelected = true;
                    }
                }
            }
            case SETTINGS -> {
                hoveringNightReset = new Rectangle(146, 500 + g.settingsScrollY, 328, 80).contains(rescaledPoint);
                if(!hoveringNightReset) {
                    confirmNightReset = false;
                }
                hoveringFpsCap = new Rectangle(490, 1025 + g.settingsScrollY, 360, 80).contains(rescaledPoint);
                hoveringJumpscareShake = new Rectangle(230, 1200 + g.settingsScrollY, 500, 80).contains(rescaledPoint);
            }
            case MILLY -> {
                int i = 0;
                while(i < 5) {
                    if(g.millyShopItems[i] != null) {
                        if(g.millyRects[i] != null) {
                            if (g.millyRects[i].contains(pointerPosition)) {
                                g.selectedMillyItem = (byte) i;
                                g.millyBackButtonSelected = false;
                                break;
                            }
                        }
                    }
                    i++;
                }
                if(rescaledPoint.x < 280 && rescaledPoint.y > 560) {
                    g.millyBackButtonSelected = true;
                }
            }
            case GAME -> {
                g.getNight().afk = 17;
                g.manualFirstButtonHover = g.manualFirst.contains(pointerPosition);
                g.manualSecondButtonHover = g.manualSecond.contains(pointerPosition);

//                g.recalculateButtons(GameState.GAME);
                // ^ this is probably HORRIBLE for performance and
                // i have to fix this sometime later,but my game is releasing in 19 days
                // and it's 22:47 on a sunday night, so i am NOT doing this right now

                if (GamePanel.mirror) {
                    rescaledPoint = new Point(1080 - rescaledPoint.x, rescaledPoint.y);
                }

                for(Door door : g.getNight().getDoors().values()) {
                    Polygon hitbox = new Polygon(door.getHitbox().xpoints, door.getHitbox().ypoints, door.getHitbox().npoints);
                    hitbox.translate(g.offsetX - 400, 0);
                    door.setHovering(hitbox.contains(rescaledPoint));
                }

                if(g.getNight().getType() == GameType.SHADOW) {
                    if(g.getNight().getAstartaBoss() != null) {
                        for (AstartaBlackHole hole : g.getNight().getAstartaBoss().getBlackHoles()) {
                            if (new Point(g.offsetX - 400 + hole.getX(), hole.getY()).distance(rescaledPoint) < 160) {
                                hole.expand();

                                for (AstartaBlackHole hole2 : g.getNight().getAstartaBoss().getBlackHoles()) {
                                    hole2.shrink();
                                }
                                break;
                            }
                        }
                    }
                }

                if(g.shadowTicket.isEnabled()) {
                    if(Achievements.HALFWAY.isObtained() && g.shadowCheckpointUsed == 0) {
                        Point point1 = new Point(540, 320);
                        Point point2 = null;

                        if(g.reachedAstartaBoss) {
                            point1 = new Point(320, 320);
                            point2 = new Point(760, 320);
                        }

                        boolean selectedSomething = false;
                        if(point1.distance(rescaledPoint) < 160) {
                            g.shadowCheckpointSelected = 1;
                            selectedSomething = true;
                        }
                        if(point2 != null) {
                            if(point2.distance(rescaledPoint) < 160) {
                                g.shadowCheckpointSelected = 2;
                                selectedSomething = true;
                            }
                        }
                        if(!selectedSomething) {
                            g.shadowCheckpointSelected = 0;
                        }
                    }
                } else if(g.getNight().isPowerModifier() && !g.getNight().isInGeneratorMinigame() && g.getNight().getGeneratorEnergy() == -1) {
                    g.hoveringGenerator = new Rectangle(g.offsetX - 400 + 790, 480, 220, 150).contains(rescaledPoint);
                }
            }
            case CHALLENGE -> {
                if (rescaledPoint.x > 20 && rescaledPoint.y > 40) {
                    if (rescaledPoint.x < 640 && rescaledPoint.y < 420) {
                        for (int i = 0; i < CustomNight.getEnemies().size(); i++) {
                            int x = i % 6 * 105 + 20;
                            int y = i / 6 * 130 + 40;

                            if (new Rectangle(x, y, 95, 120).contains(rescaledPoint)) {
                                CustomNight.selectedElement = CustomNight.getEnemies().get(i);
                                CustomNight.setLoadedPreviewPath(CustomNight.getEnemies().get(i).getPreview());
                                break;
                            }
                        }
                    }
                }
                if (rescaledPoint.x > 660 && rescaledPoint.y > 220) {
                    if (rescaledPoint.x < 1080 && rescaledPoint.y < 440) {
                        for (int i = 0; i < CustomNight.modifiers.size(); i++) {
                            int x = (i / 3 * 200 + 675);
                            int y = (i % 3 * 60 + 248);

                            if (new Rectangle(x, y, 190, 50).contains(rescaledPoint)) {
                                CustomNight.selectedElement = CustomNight.modifiers.get(i);
                                CustomNight.setLoadedPreviewPath(CustomNight.modifiers.get(i).getPreview());
                                break;
                            }
                        }
                    }
                }
                if(rescaledPoint.x > 810 && rescaledPoint.y > 440) {
                    CustomNight.backSelected = rescaledPoint.y > 540;
                    CustomNight.startSelected = !(rescaledPoint.y > 540);
                    CustomNight.selectedElement = null;
                } else {
                    CustomNight.backSelected = false;
                    CustomNight.startSelected = false;
                }
                if(rescaledPoint.x > 3 && rescaledPoint.y > 578 && rescaledPoint.x < 373 && rescaledPoint.y < 638) {
                    CustomNight.customSelected = true;
                    CustomNight.selectedElement = null;
                } else {
                    CustomNight.customSelected = false;
                }

                if(CustomNight.isCustom()) {
                    if(rescaledPoint.x > 10 && rescaledPoint.y > 505 && rescaledPoint.x < 250 && rescaledPoint.y < 555) {
                        CustomNight.shuffleSelected = true;
                        CustomNight.selectedElement = null;
                    } else {
                        CustomNight.shuffleSelected = false;
                    }
                } else {
                    if(rescaledPoint.x > 10 && rescaledPoint.y > 505 && rescaledPoint.x < 140 && rescaledPoint.y < 555) {
                        CustomNight.prevSelected = true;
                        CustomNight.selectedElement = null;
                    } else {
                        CustomNight.prevSelected = false;
                    }

                    if(rescaledPoint.x > 650 && rescaledPoint.y > 505 && rescaledPoint.x < 800 && rescaledPoint.y < 555) {
                        CustomNight.nextSelected = true;
                        CustomNight.selectedElement = null;
                    } else {
                        CustomNight.nextSelected = false;
                    }
                }
            }
        }
    }

    private void setPoint() {
        g.pointX = pointerPosition.x;
        g.pointY = pointerPosition.y;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        switch (g.state) {
            case MENU -> {
                if (pointerPosition.x < 520 * g.widthModifier + g.centerX) {
                    short x = (short) g.visibleMenuButtons.size();
                    if (pointerPosition.y > (-40 * x + 520) * g.heightModifier + g.centerY) {
                        // up
                        if (e.getWheelRotation() < 0) {
                            if (g.selectedOption - g.menuButtonOffset == 0 && g.menuButtonOffset > 0) {
                                g.menuButtonOffset--;
                            }

                            g.selectedOption--;

                            if (g.selectedOption == -1) {
                                g.selectedOption = (byte) (g.menuButtons.size() - 1);
                                g.menuButtonOffset = (byte) (g.menuButtons.size() - g.visibleMenuButtons.size());
                            }
                        } else {
                            if (g.selectedOption - g.menuButtonOffset == 3) {
                                g.menuButtonOffset++;
                            }

                            g.selectedOption++;

                            if (g.selectedOption == g.menuButtons.size()) {
                                g.selectedOption = 0;
                                g.menuButtonOffset = 0;
                            }
                        }
                        g.visibleMenuButtons = g.menuButtons.subList(g.menuButtonOffset, Math.min(g.menuButtonOffset + 4, g.menuButtons.size()));
                    }
                }
            }
            case RIFT -> {
                byte index = (byte) (g.riftItems.indexOf(g.selectedRiftItem));

                // up
                if (e.getWheelRotation() < 0) {
                    if (index <= 0) {
                        index = (byte) (g.riftItems.size() - 1);
                    } else {
                        index--;
                    }
                    g.sound.play("select", 0.1);
                } else {
                    if (index >= g.riftItems.size() - 1) {
                        index = (byte) (0);
                    } else {
                        index++;
                    }
                    g.sound.play("select", 0.1);
                }

                g.selectedRiftItem = g.riftItems.get(index);
            }
            case ITEMS -> {
                if(g.rows >= 4) {
                    g.itemScrollY = (short) (Math.max(0, g.itemScrollY + e.getWheelRotation() * 30));

                    while (g.rows * 170 + (207 - 40 * Math.min(g.rows, 4)) - g.itemScrollY < 640) {
                        g.itemScrollY -= (short) (e.getWheelRotation());
                    }
                    g.redrawItemsMenu();
                }
            }
            case ACHIEVEMENTS -> {
                if(!g.achievementState) {
                    g.achievementsScrollY = (short) (Math.max(0, g.achievementsScrollY + e.getWheelRotation() * 30));

                    int length = Math.max(4, Achievements.values().length);

                    while (30 + length * 155 - g.achievementsScrollY < 530) {
                        g.achievementsScrollY -= (short) (e.getWheelRotation());
                    }
                    g.redrawAchievements();
                } else {
                    g.statisticsScrollY = (short) (Math.max(0, g.statisticsScrollY + e.getWheelRotation() * 30));

                    while (150 + Statistics.values().length * 40 - g.statisticsScrollY < 650) {
                        g.statisticsScrollY -= (short) (e.getWheelRotation());
                    }
                }
            }

            case SETTINGS -> {
                if(g.rows >= 4) {
                    g.settingsScrollY = Math.max(-720, Math.min(0, g.settingsScrollY - e.getWheelRotation() * 30));
                }
            }
        }
    }

    private void shiftAchievements() {
        g.shiftingAchievements = true;
        g.achievementMargin = 1080;
    }

    private void clickMenuButton() {
        if(g.selectedOption == 0) {
            g.startPlayMenu();
        } else {
            switch (g.menuButtons.get(g.selectedOption)) {
                case ">> settings" -> g.startSettings();
                case ">> bingo" -> g.startBingo();
                case ">> achievements" -> g.startAchievements();
            }
        }
    }

    private void clickBingoButton() {
        if(g.bingoCard.isFailed() || !g.bingoCard.isGenerated() || g.bingoCard.isCompleted()) {
            g.bingoCard.generate();
        }
        if(g.bingoCard.isGenerated()) {
            g.bingoCard.fail();
        }
        g.sound.play("select", 0.1);
    }

    private void clickPlayButton() {
        switch (PlayMenu.getList().get(PlayMenu.index).getID()) {
            case "normal" -> {
                g.type = GameType.CLASSIC;
                g.startItemSelect();
            }
            case "challenge" -> {
                g.startChallengeMenu(true);
            }
            case "endless" -> {
                g.type = GameType.ENDLESS_NIGHT;
                g.endless = new EndlessGame();
                g.startItemSelect();
            }
        }
    }

    private void cancelLimbo() {
        if(CustomNight.limboId > 0) {
            g.music.stop();
            g.music.play("torture", 0.05, true);
            if(CustomNight.limboTimer[0] != null) {
                CustomNight.limboTimer[0].cancel(false);
                CustomNight.limboTimer[0] = null;
            }
            CustomNight.limboId = -1;
        }
    }

    private void deathScreenYThing() {
        if(g.everyFixedUpdate.containsKey("deathScreenY")) {
            g.deathScreenY = 640;
            g.everyFixedUpdate.remove("deathScreenY");
            g.music.play("orca", 0.06, true);

            if (g.killedBy.contains("died from radiation") && !g.afterDeathText.contains("After the first nuclear strike")) {
                new Pepitimer(() -> {
                    if(g.afterDeathText.contains("After the first nuclear strike"))
                        return;

                    g.afterDeathText = "After the first nuclear strike, you must IMMEDIATELY travel to the Indonesian island of Sumatra, where Lake Toba is located. On this Lake Toba there is the island of Samosir. On the island of Samosir there is a Lutheran church \"GEREJA HKBP BOLON PANGURURAN\". Opposite this church there is a hill where my tent will be located!!! - Larry Zhou";
                    g.afterDeathCurText = "After the first nucl";
                }, 4500);
            }
        } else {
            g.everyFixedUpdate.put("deathScreenY", () -> {
                if (g.deathScreenY < 640) {
                    g.deathScreenY += 8 + (g.deathScreenY) / 19 - (g.deathScreenY * g.deathScreenY) / 10240;
                } else {
                    g.deathScreenY = 640;
                    g.everyFixedUpdate.remove("deathScreenY");
                    g.music.play("orca", 0.06, true);

                    if (g.killedBy.contains("died from radiation") && !g.afterDeathText.contains("After the first nuclear strike")) {
                        new Pepitimer(() -> {
                            if(g.afterDeathText.contains("After the first nuclear strike"))
                                return;

                            g.afterDeathText = "After the first nuclear strike, you must IMMEDIATELY travel to the Indonesian island of Sumatra, where Lake Toba is located. On this Lake Toba there is the island of Samosir. On the island of Samosir there is a Lutheran church \"GEREJA HKBP BOLON PANGURURAN\". Opposite this church there is a hill where my tent will be located!!! - Larry Zhou";
                            g.afterDeathCurText = "After the first nucl";
                        }, 4500);
                    }
                }
            });
        }
    }
}