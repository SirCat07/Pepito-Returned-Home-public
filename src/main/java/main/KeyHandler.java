package main;

import cutscenes.Cutscene;
import cutscenes.Presets;
import enemies.*;
import game.*;
import game.achievements.AchievementHandler;
import game.achievements.Achievements;
import game.bingo.BingoHandler;
import game.bingo.BingoTask;
import game.cornfield.Player;
import game.custom.CustomNight;
import game.custom.CustomNightEnemy;
import game.custom.CustomNightModifier;
import game.enviornments.Basement;
import game.enviornments.BasementKeyOffice;
import game.enviornments.Enviornment;
import game.enviornments.HChamber;
import game.field.Field;
import game.items.Item;
import game.items.Triggerable;
import game.particles.BubbleParticle;
import game.particles.WaterParticle;
import game.playmenu.PlayMenu;
import game.shadownight.AstartaBlackHole;
import game.shadownight.Mister;
import javafx.scene.media.MediaPlayer;
import utils.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

    public Point pointerPosition = new Point(0, 0);
    public Cursor defaultCursor;

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

        g.resumeAllSound(true);

        for (Pepitimer pepitimer : StaticLists.timers) {
            pepitimer.gameResume();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if(e.isShiftDown()) {
            holdingShift = true;
        }

        if(code == KeyEvent.VK_F11 || (e.isAltDown() && code == KeyEvent.VK_ENTER)) {
            GamePanel.fullscreen = !GamePanel.fullscreen;
            if(GamePanel.fullscreen) {
                g.toFullscreen();
            } else {
                g.removeFullscreen();
            }
            return;
        }

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
//            if (code == KeyEvent.VK_NUMPAD2) {
//                g.getNight().seconds += 4;
//            }
//            if (code == KeyEvent.VK_NUMPAD3) {
//                g.getNight().getKiji().spawn();
//            }
//            if (code == KeyEvent.VK_NUMPAD4) {
//                g.getNight().getShock().spawn();
//            }
//            if (code == KeyEvent.VK_NUMPAD5) {
//                g.getNight().getToleTole().spawn();
//            }
//            if(code == KeyEvent.VK_NUMPAD6) {
//                g.pepitoClockProgress = 0;
//            }
            if(code == KeyEvent.VK_NUMPAD7) {
                g.window.setExtendedState(JFrame.MAXIMIZED_BOTH);
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                g.window.setSize(screenSize.width,screenSize.height);
            }
        }

        if (code == KeyEvent.VK_NUMPAD1) {
            g.debugMode = !g.debugMode;
        }
//        if(code == KeyEvent.VK_NUMPAD2) {
//            g.fieldIntro();
//        }
//        if(code == KeyEvent.VK_NUMPAD3) {
//            g.getNight().getToleTole().spawn();
//            GamePanel.toleToleSpawned++;
//        }
//        if(code == KeyEvent.VK_NUMPAD4) {
//            g.getNight().getShock().spawn();
//        }
//        if(code == KeyEvent.VK_NUMPAD5) {
//            g.getNight().getKiji().spawn();
//        }
//        if (code == KeyEvent.VK_NUMPAD2) {
//            g.enviornmentEditor = !g.enviornmentEditor;
//        }

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
                    if(g.type == GameType.HYDROPHOBIA && g.getNight().getEvent() == GameEvent.DYING) {
                        HChamber chamber = (HChamber) g.getNight().env;
                        if(chamber.showDeathOptions) {
                            if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S || code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
                                chamber.selectedDeathOption = (byte) (1 - chamber.selectedDeathOption);
                                return;
                            }
                        }
                    }
                    pressAnyKeyStuff();
                    return;
                }
            }
            switch (g.state) {
                case MENU -> {
                    if(code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT) {
                        krunlicActivation += code;
                        if(krunlicActivation.endsWith("3739373937393739")) {
                            
                            if(GamePanel.krunlicPhase == 0) {
                                if (!GamePanel.krunlicMode) {
                                    g.version += "krunlic";
                                    g.initializeFontMetrics();
                                    g.sound.play("krunlicTrigger", 0.2);
                                    g.fadeOutStatic(1, 0.05F, 0.01F);
                                    g.fadeOut(255, 160, 1);
                                } else {
                                    g.version = g.version.replace("krunlic", "");
                                    g.initializeFontMetrics();
                                    g.fadeOut(255, 160, 2);
                                    g.fadeOutStatic(0.05F, 0.05F, 0);
                                    
                                    new Notification("opted out");
                                }
                                
                                GamePanel.krunlicMode = !GamePanel.krunlicMode;
                                
                            } else {
                                new Notification("CAN'T OPT OUT NOW =)");
                            }

                            krunlicActivation = "";
                        }
                    } else {
                        krunlicActivation = "";
                    }
                    
                    if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE || code == KeyEvent.VK_Z) {
                        clickMenuButton();
                    }
                    if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
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

                                clickItem(item);
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
                case MUSIC_MENU -> {
                    if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_BACK_SPACE) {
                        stopPause = true;
                        g.backToMainMenu();
                    }
                }
                case PLAY -> {
                    if(e.isShiftDown() && PlayMenu.index == 1 && Achievements.ALL_NIGHTER.isObtained()) {
                        if (code == KeyEvent.VK_D) {
                            g.currentNight++;
                            if(g.currentNight > 4) {
                                g.currentNight = 1;
                            }
                            g.sound.play("select", 0.1);
                        } else if (code == KeyEvent.VK_A) {
                            g.currentNight--;
                            if(g.currentNight < 1) {
                                g.currentNight = 4;
                            }
                            g.sound.play("select", 0.1);
                        }

                        String subtext1 = ">> " + GamePanel.getString("pmPlay");
                        PlayMenu.getList().get(1).setSubtext(g.currentNight == 1 ? subtext1 : subtext1 + " - " + GamePanel.getString("pmNight") + " " + g.currentNight);
                    } else {
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
                        }
                    }

                    if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE || code == KeyEvent.VK_Z) {
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
                        g.music.play(g.menuSong, 0.15, true);
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
                case INVESTIGATION -> {
                    if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_BACK_SPACE) {
                        stopPause = true;
                        g.backToMainMenu();
                    }
                }
                
                case GAME -> {
                    if (g.getNight().getEvent().canUseItems()) {
                        if (g.night.getA90().isActive()) {
                            if(e.getKeyCode() != KeyEvent.VK_TAB && e.getKeyCode() != KeyEvent.VK_ESCAPE) {
                                g.night.getA90().dying = true;
                            }
                        }
                        
                        for(Item item : g.usedItems) {
                            if(item instanceof Triggerable triggerable) {
                                if(String.valueOf(e.getKeyChar()).equals(triggerable.getKey())) {
                                    triggerable.run();
                                }
                            }
                        }

                        switch (code) {
                            case KeyEvent.VK_M -> { // metal pipe
                                if (g.metalPipe.isEnabled()) {
                                    if(g.getNight().getEvent().isFlood()) {
                                        Enviornment env = g.getNight().env();
                                        Rectangle rect = env.metalPipe;
                                        
                                        if(rect.y > g.currentWaterLevel) {
                                            g.sound.play("bubbles", 0.05);

                                            BufferedImage bubbleImage = g.getBubbleImage(0.5F);
                                            short phase = (short) (g.fixedUpdatesAnim % 251);
                                            byte limit = (byte) Math.round(3 + Math.random());

                                            for (int i = 0; i < limit; i++) {
                                                g.bubbles.add(new BubbleParticle(rect.x, rect.y + 12 + rect.height / 2, phase, 30, bubbleImage));
                                                g.bubbles.add(new BubbleParticle(rect.x + rect.width, rect.y + 12, phase, 30, bubbleImage));
                                            }
                                            return;
                                        }
                                    }
                                    
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

                                        g.metalPipeCooldown = 25;
                                        g.night.getPepito().notPepitoChance += 1;
                                    } else {
                                        g.sound.play("error", 0.08);
                                    }
                                }
                            }

                            case KeyEvent.VK_F -> { // fan
                                if (g.night.hasPower()) {
                                    if (g.fan.isEnabled() && g.type != GameType.HYDROPHOBIA) {
                                        g.fanActive = !g.fanActive;

                                        if (g.fanActive) {
                                            fanSounds.play("fanSound", 0.22, true);
                                            g.sound.play("startFan", 0.15);
                                            g.usage++;

                                            g.everySecond20th.put("fan", () -> {
                                                g.fanDegrees += 46;
                                                g.rotatedFanBlade = g.rotate(g.fanImg[2], g.fanDegrees);
                                                g.redrawBHO = true;
                                            });
                                        }
                                        if (!g.fanActive) {
                                            fanSounds.stop();
                                            g.sound.play("stopFan", 0.15);
                                            g.usage--;
                                            g.fanDegrees = 0;
                                            g.rotatedFanBlade = g.rotate(g.fanImg[2], g.fanDegrees);

                                            g.everySecond20th.remove("fan");
                                        }
                                        g.redrawUsage();
                                    }
                                } else {
                                    g.sound.play("error", 0.08);
                                }
                            }

                            case KeyEvent.VK_C -> {
                                if(g.getNight().getGlitcher().visualFormTicks > 0)
                                    return;
                                
                                if (!g.night.hasPower() || g.night.getGlitcher().isGlitching) {
                                    g.sound.play("error", 0.08);
                                } else if(!g.portalTransporting) {
                                    g.inCam = !g.inCam;

                                    if (g.inCam) {
                                        g.sound.play("camPull", 0.12);
                                        camSounds.play("buzzlight", 0.25, true);
                                        g.fadeOutStatic(1F, 0.25F, 0.01F);

                                        g.usage++;
                                        g.night.addEnergy(-0.2F);
                                        g.redrawUsage();

                                        g.night.getMSI().disappearShadow();

                                        if(g.getNight().getType().isEndless()) {
                                            double rand = Math.random();
                                            g.portalActive = g.endless.getNight() >= 5 && rand < 0.015F + g.endless.getNight() / 150F && g.night.getEvent().isGuiEnabled();
                                            if (g.portalActive) {
                                                camSounds.play("shadowPortal", 0.1, true);
                                            }
                                        }
                                        if(!g.portalActive) {
                                            g.outOfLuck = true;
                                            new Pepitimer(() -> g.outOfLuck = false, 120);
                                        }
                                        if(g.getNight().getType() == GameType.HYDROPHOBIA) {
                                            HChamber chamber = (HChamber) g.getNight().env();
                                            chamber.cameraGuidelineAlpha = 0;
                                        }

                                        g.updateCam();
                                        if (g.night.getGlitcher().isEnabled()) {
                                            g.night.getGlitcher().counter += 0.45F;

                                            if(g.night.getGlitcher().counter > 12) {
                                                new Pepitimer(() -> {
                                                    if(g.inCam) {
                                                        g.fadeOutStatic(1F, 0.2F, 0.005F);
                                                        g.sound.play("camPull", 0.12);
                                                    }
                                                }, 400 + (short) (Math.random() * 400));
                                            }
                                        }
                                    } else { // if not incam
                                        g.camOut(true);

                                        if(g.night.getGlitcher().counter > 13 && Math.random() < (g.night.getGlitcher().counter - 11) / 3) {
                                            g.getNight().getGlitcher().visualFormTicks = 25;
                                            g.sound.stop();
                                            g.sound.play("glitcherScare", 0.1);
                                        }
                                    }
                                }
                            }

                            case KeyEvent.VK_S -> {
                                if (g.soda.isEnabled()) {
                                    g.redrawBHO = true;
                                    
                                    if(g.getNight().getEnergy() <= 5) {
                                        BingoHandler.completeTask(BingoTask.USE_SODA_AT_1_ENERGY);
                                    }

                                    if(g.getNight().hasPower()) {
                                        if (g.night.getEnergy() > g.night.getMaxEnergy()) {
                                            g.night.setMaxEnergy(g.night.getEnergy());
                                        }
                                        g.getNight().addEnergyLimited(200);
                                    } else {
                                        new Notification(GamePanel.getString("sodaExpired"), 2);
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
                                        g.getNight().addEnergyLimited(50);
                                    } else {
                                        new Notification(GamePanel.getString("miniSodaExpired"), 2);
                                    }
                                    g.sound.playRate("sodaOpen", 0.05, 0.5);
                                    g.sound.play("minnesota", 0.2);
                                    g.miniSoda.disable();

                                    g.repaintOffice();
                                }
                            }

                            case KeyEvent.VK_O -> {
                                if (g.megaSoda.isEnabled()) {
                                    if(g.getNight().getColaCat().megaSodaWithheld > 0) {
                                        g.sound.play("error", 0.08);
                                    } else {
                                        if (g.night.getEnergy() > g.night.getMaxEnergy()) {
                                            g.night.setMaxEnergy(g.night.getEnergy());
                                        }
                                        g.getNight().addEnergyLimited(100);

                                        if (!g.getNight().hasPower()) {
                                            g.lightsOn();
                                            g.getNight().megaSodaLightsOnTicks = 16;

                                            g.getNight().setFlicker(60 + (int) (Math.random() * 20));
                                            g.sound.play("flicker" + (int) (Math.round(Math.random()) + 1), 0.05);
                                        }

                                        g.sound.playRate("sodaOpen", 0.05, 0.5);
                                        g.sound.play("megasota", 0.2);

                                        if (g.getNight().megaSodaUses == 1) {
                                            g.megaSoda.disable();
                                        }
                                        g.getNight().megaSodaUses--;
                                    }
                                }
                            }

                            case KeyEvent.VK_I -> {
                                if(g.freezePotion.isEnabled()) {
                                    GamePanel.freezeModifier -= 0.25F;

                                    GamePanel.universalGameSpeedModifier = GamePanel.originalGameSpeedModifier * GamePanel.freezeModifier;
                                    g.allTimers.shutdown();
                                    g.startupTimers();
                                    
                                    g.sound.play("icePotionUse", 0.1F);

                                    freezeChange = new Pepitimer(() -> {
                                        GamePanel.freezeModifier += 0.25F;

                                        GamePanel.universalGameSpeedModifier = GamePanel.originalGameSpeedModifier * GamePanel.freezeModifier;
                                        g.allTimers.shutdown();
                                        g.startupTimers();
                                    }, (int) Math.max(1, (40000 * GamePanel.universalGameSpeedModifier)));

                                    g.freezePotion.disable();
                                    g.repaintOffice();
                                }
                            }

                            case KeyEvent.VK_T -> {
                                if(g.iceBucket.isEnabled()) {
                                    g.sound.play("icePotionUse", 0.15F);

                                    if(g.getNight().getTemperature() > 15) {
                                        g.getNight().setTemperature(Math.max(15, g.getNight().getTemperature() - 35));
                                    }
                                    
                                    g.iceBucket.disable();
                                    g.repaintOffice();
                                }
                            }
                            
                            case KeyEvent.VK_R -> {
                                if(g.red40.isEnabled()) {
                                    if(g.getNight().getEvent().isFlood()) {
                                        g.getNight().red40Phase++;
                                        
                                        g.getNight().getShark().setAILevel(g.getNight().getShark().getAILevel() * 2);
                                        g.getNight().getDsc().setAILevel(g.getNight().getDsc().getAILevel() * 2);
                                        
                                        float hueOffset = -1.58F;
                                        
                                        float[] hsb = new float[3];
                                        Color.RGBtoHSB(g.currentWaterColor.getRed(), g.currentWaterColor.getGreen(), g.currentWaterColor.getBlue(), hsb);
                                        Color hsbColor = Color.getHSBColor(hsb[0] + hueOffset, hsb[1], hsb[2] / 2);
                                        g.currentWaterColor = new Color((120 << 24) | (hsbColor.getRed() << 16) | (hsbColor.getGreen() << 8) | (hsbColor.getBlue()), true);
                                      
                                        hsb = new float[3];
                                        Color.RGBtoHSB(g.currentWaterColor2.getRed(), g.currentWaterColor2.getGreen(), g.currentWaterColor2.getBlue(), hsb);
                                        hsbColor = Color.getHSBColor(hsb[0] + hueOffset, hsb[1], hsb[2] / 2);
                                        g.currentWaterColor2 = new Color((180 << 24) | (hsbColor.getRed() << 16) | (hsbColor.getGreen() << 8) | (hsbColor.getBlue()), true);
                                    
                                            
                                        if(g.currentWaterImage != null) {
                                            g.currentWaterImage = GamePanel.changeHSB(g.currentWaterImage, hueOffset, 1, 0.5F);
                                        }
                                        g.sound.play("sodaOpen", 0.03);
                                        
                                        g.red40.disable();
                                        g.repaintOffice();
                                        
                                    } else {
                                        new Notification(GamePanel.getString("red40NeedWater"));
                                        g.sound.play("error", 0.08);
                                    }
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
                                        g.night.getMSI().kill(false, false);
                                    }
                                    g.night.getColaCat().leave();

                                    if(g.night.getEvent() == GameEvent.FLOOD) {
                                        g.night.getShark().fullReset();
                                    }
                                    if(g.night.getEvent() == GameEvent.DEEP_FLOOD) {
                                        g.night.getDsc().fullReset();
                                    }
                                    if(g.night.getBoykisser().isAwaitingResponse()) {
                                        g.night.getBoykisser().leave();
                                    }
                                    g.night.getLemonadeCat().leave();
                                    g.night.getMirrorCat().kill();
                                    g.night.getWires().leave();
                                    g.night.getScaryCat().leave();
                                    g.night.getElAstarta().leaveEarly();
                                    g.night.getKiji().stop();
                                    g.night.getShock().stop();
                                    g.night.getA120().fullReset();
                                    g.night.getBeast().fullReset();
                                    g.night.getOverseer().disappear();
                                    //stop

                                    Hydrophobia hydrophobia = g.night.getHydrophobia();
                                    if(hydrophobia.isEnabled()) {
                                        if (hydrophobia.getCurrentPos() < 2) {
                                            hydrophobia.setCurrentPos(hydrophobia.getCurrentPos() - 1);
                                        } else {
                                            hydrophobia.setCurrentPos(hydrophobia.getCurrentPos() + 1);
                                        }
                                        hydrophobia.resetSound();
                                    }
                                    

                                    if(g.getNight().getEvent() == GameEvent.ASTARTA) {
                                        g.getNight().getAstartaBoss().damage(2);
                                    }

                                    g.repaintOffice();
                                }
                            }
                            case KeyEvent.VK_P -> { // styrofoam pipeh
                                if (g.styroPipe.isEnabled()) {
                                    g.sound.play("styrofoamPipe", 0.1);
                                }
                            }
                            case KeyEvent.VK_G -> { // sunglasses
                                if (g.sunglasses.isEnabled()) {
                                    g.fadeOut(255, g.endFade, 3);
                                    g.sunglassesOn = !g.sunglassesOn;
                                    
                                    if(g.sunglassesOn) {
                                        g.sgRadius = 1;
                                        g.sound.play("camPull", 0.12);
                                        if(!g.inCam) {
                                            g.fadeOutStatic(1, 0F, 0.05F);
                                        }
                                        
                                        g.sgPolygons.clear();
                                        if(g.getNight().env instanceof HChamber chamber) {
                                            g.sgPolygons.add(GamePanel.rectangleToPolygon(chamber.exit));
                                        } else {
                                            for (Door door : g.getNight().doors.values()) {
                                                Polygon hitbox = new Polygon(door.getHitbox().xpoints, door.getHitbox().ypoints, door.getHitbox().npoints);
                                                g.sgPolygons.add(hitbox);
                                            }
                                        }
                                        
                                        new Pepitimer(() -> g.sound.play("larryGlasses", 0.1), 0);
                                    } else {
                                        g.sound.play("camOut", 0.15);
                                        if(!g.inCam) {
                                            g.fadeOutStatic(0.3F, 0F, 0.03F);
                                        }
                                        
//                                        new Pepitimer(() -> g.sound.play("larryGlasses", 0.1), 0);
                                    }
                                }
                            }
                            
                            case KeyEvent.VK_W -> {
                                if (g.inLocker) {
                                    g.inLocker = false;
                                    g.sound.play("lockerOut", 0.15);

                                    g.fadeOut(255, 180, 0.7F);

                                    if (g.type == GameType.HYDROPHOBIA) {
                                        HChamber chamber = (HChamber) g.getNight().env();
                                        chamber.lockerGuidelineAlpha = 0;
                                        
                                        chamber.setShake(Math.max(15, chamber.getShake()));
                                    }
                                }
                            }

                            case KeyEvent.VK_B -> holdingB = true;
                            case KeyEvent.VK_TAB -> {
                                for(Item item : g.fullItemList) {
                                    if(item.isEnabled() && !g.usedItems.contains(item)) {
                                        g.usedItems.add(item);
                                    }
                                }
                                g.usedItems.removeIf(item -> !item.isEnabled());

                                holdingTab = true;
                            }
                            case KeyEvent.VK_E -> holdingE = true;
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
                                g.console.add(GamePanel.getString("sensorSubRenewed"));
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
                                        if(g.getNight().env() instanceof Basement basement) {
                                            cutscene = Presets.voidEndingPEPITOLESS(g);
                                        }
                                        cutscene.setAntiAliasing(true);

                                        Statistics.GOTTEN_VOID_ENDING.increment();

                                        g.currentCutscene = cutscene;

                                        g.stopAllSound();
                                        if(g.inCam) {
                                            g.camOut(false);
                                        }

                                        g.state = GameState.CUTSCENE;

                                        g.fadeOut(255, 20, 4);

                                        g.music.play("void", 0.1);

                                        new Pepitimer(cutscene::nextScene, 30040);
                                        new Pepitimer(() -> g.stopGame(true), 32500);
                                    }, 5000);
                                }
                            }
                        }
                    }
                    if(g.getNight().isInGeneratorMinigame()) {
                        if (code == KeyEvent.VK_Z || code == KeyEvent.VK_SPACE) {
                            int x0 = (g.fixedUpdatesAnim * 5 + (int) (Math.sin(g.fixedUpdatesAnim * 0.05) * 14)) % 630;
                            Rectangle rect1 = new Rectangle(218 + x0, 485, 14, 130);

                            int order = 0;
                            for(short x : g.getNight().generatorXes.clone()) {
                                Rectangle rect2 = new Rectangle(220 + x, 495, 35, 110);

                                if(rect1.intersects(rect2) && g.getNight().generatorXes[order] != -1) {
                                    g.getNight().generatorXes[order] = -1;

                                    short[] array = g.getNight().generatorXes;
                                    if(array[0] == -1 && array[1] == -1 && array[2] == -1) {
                                        g.getNight().generatorStage++;
                                        g.getNight().regenerateGeneratorXes();

                                        g.generatorSound.play("generatorNextStage", 0.1);

                                        if(g.getNight().generatorStage >= 3) {
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

                            if(Math.random() < 0.8 && g.getNight().generatorStage > 1) {
                                g.getNight().generatorStage--;
                            }
                            g.getNight().regenerateGeneratorXes();

                            g.generatorSound.play("generatorFail", 0.1);
                        }
                    }
                    
                    
                    // NOW THE SAME THING BUT FOR THE BASEMENT
                    if(g.getNight().getType().isBasement()) {
                        Basement basement = (Basement) g.getNight().env();
                        
                        if(basement.isInGeneratorMinigame()) {
                            if (code == KeyEvent.VK_Z || code == KeyEvent.VK_SPACE) {
                                int x0 = g.fixedUpdatesAnim * 5 + (int) (Math.sin(g.fixedUpdatesAnim * 0.05) * 14);

                                if(basement.getStage() == 5) {
                                    x0 += g.fixedUpdatesAnim / 2;
                                }
                                x0 = x0 % 630;
                            
                                Rectangle rect1 = new Rectangle(218 + x0, 485, 14, 130);

                                int order = 0;
                                for (short x : basement.generatorXes.clone()) {
                                    Rectangle rect2 = new Rectangle(220 + x, 495, 35, 110);

                                    if (rect1.intersects(rect2) && basement.generatorXes[order] != -1) {
                                        basement.generatorXes[order] = -1;

                                        short[] array = basement.generatorXes;
                                        if (array[0] == -1 && array[1] == -1 && array[2] == -1) {
                                            basement.setGeneratorStage((byte) (basement.getGeneratorStage() + 1));
                                            basement.regenerateGeneratorXes();

                                            if (basement.getStage() < 5) {
                                                basement.setStage((byte) 5);
                                                g.getNight().getPepito().seconds = 5;
                                                g.getNight().getA90().shots = 0;
                                                g.restartBasementSong();
                                                g.getNight().setMaxEnergy(500F);
                                                g.getNight().setEnergy(500F);
                                                basement.setShake(45);

                                                g.getNight().shirtfart.cancel(false);

                                                g.generatorSound.stop();
                                                g.generatorSound.play("basementTheme5Generator", 0.1, true);
                                            }

                                            g.generatorSound.play("generatorNextStage", 0.1);

                                            if(basement.getGeneratorStage() >= 16) {
                                                g.generatorSound.stop();
                                                g.getNight().redrawBasementScreen();
                                                basement.setStage((byte) 6);
                                                basement.setInGeneratorMinigame(false);
                                                
                                                g.basementSound.stop();

                                                g.getNight().basementDoorBlocks();
                                                basement.blockedWalls.clear();
                                                g.getNight().setDefaultBasementDoors();
                                                g.repaintOffice();

                                                g.getNight().cancelAfterGame.add(new Pepitimer(() -> {
                                                    g.sound.play("challenger", 0.5);
                                                    basement.setGasLeakMillis(4000);
                                                }, 1000));

                                                g.getNight().cancelAfterGame.add(new Pepitimer(() -> {
                                                    g.getNight().getMSI().setAILevel(1);
                                                    float d = 1.615F;
                                                    g.getNight().getMSI().modifier = d;
                                                    g.getNight().getMSI().spawn();

                                                    g.getNight().cancelAfterGame.add(new Pepitimer(() -> {
                                                        if(basement.doWiresWork()) {
                                                            g.restartBasementSong();

                                                            g.getNight().cancelAfterGame.add(new Pepitimer(() -> {
                                                                basement.spark();
                                                                g.sound.playRate("sparkSound", 0.15, 0.9 + Math.random() / 5F);

                                                                g.getNight().cancelAfterGame.add(new Pepitimer(() -> {
                                                                    g.sound.play("explosionSound", 0.2);
                                                                    basement.setShake(55);
                                                                    basement.setStage((byte) 7);
                                                                    basement.setWhiteScreen(255);
                                                                    basement.setRedAlarmY(-100);
                                                                    g.repaintOffice();
                                                                    g.getNight().setTemperature(0);
                                                                    g.getNight().redrawBasementScreen();
                                                                    g.getNight().doors.clear();
                                                                    
                                                                    g.white200 = Color.WHITE;
                                                                    g.basementHyperOptimization = true;
                                                                    g.redrawBHO = true;
                                                                    g.sound.play("helicopter", 0.2, true);
                                                                }, 100));
                                                            }, 38150));
                                                        } else {
                                                            g.basementSound.play("gasLeakSound", 0.15);
                                                            
                                                            new Pepitimer(() -> {
                                                                g.startCorn();
                                                                Statistics.GOTTEN_CORN_ENDING.increment();
                                                            }, 21000);
                                                        }
                                                    }, (int) (1860 + 300 / d)));
                                                }, 4000));
                                            }
                                        } else {
                                            g.generatorSound.play("generatorSuccess", 0.1);
                                        }
                                        
                                        g.getNight().redrawBasementScreen();
                                        return;
                                    }
                                    order++;
                                }

//                                if (Math.random() < 0.8) {
//                                    if (g.getNight().generatorStage > 0) {
//                                        g.getNight().generatorStage--;
//                                    }
//                                }
                                basement.regenerateGeneratorXes();

                                g.generatorSound.play("generatorFail", 0.1);
                            } else if(code == KeyEvent.VK_ESCAPE) {
                                basement.setInGeneratorMinigame(false);
                                
                                if(basement.getStage() == 4) {
                                    g.generatorSound.stop();
                                } else {
                                    for (MediaPlayer player : g.generatorSound.clips) {
                                        player.setVolume(0);
                                    }
                                }
                                stopPause = true;
                            }
                        }
                    }
                }
                case UH_OH -> {
                    stopPause = true;
                }
                case CRATE -> {
                    if (!e.isControlDown() && !e.isShiftDown() && !e.isAltDown()) {
                        crateClick();
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
                            g.getNight().addEnergyLimited(250);
                        }

                        g.state = GameState.GAME;
                        GamePanel.mirror = true;
                        float rate = g.getNight().getAstartaBoss().hasMusicSpedUp() ? 1.1F : 1;
                        g.music.playRateLooped("astartaFight", 0.08, rate);
                    }
                }
                case DRY_CAT_GAME -> {
                    if(code == KeyEvent.VK_E) {
                        if(g.dryCatGame.isDoorOpen() && !g.everyFixedUpdate.containsKey("doorAnimationEnter")) {
                            g.everyFixedUpdate.put("doorAnimationEnter", () -> {
                                g.dryCatGame.daZoom += 2;
                                g.dryCatGame.daZoom *= 1.2F;

                                if (g.dryCatGame.daZoom > 320) {
                                    Level oldNight = g.getNight();
                                    g.getNight().resetTimers();
                                    g.getNight().getShock().stop();
                                    
                                    g.type = GameType.HYDROPHOBIA;
                                    g.startGame();

                                    HChamber env = (HChamber) g.getNight().env();
                                    env.setOldNight(oldNight);
                                    g.everyFixedUpdate.remove("doorAnimationEnter");
                                }
                            });
                        }
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
                            
                            g.riftFramesDoingNothing = -15000;
                            g.riftMoonAlpha = 0;
                        } else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
                            if(index <= 0) {
                                index = (byte) (g.riftItems.size() - 1);
                            } else {
                                index--;
                            }
                            g.sound.play("select", 0.1);

                            g.riftFramesDoingNothing = -15000;
                            g.riftMoonAlpha = 0;
                        }
                        g.selectedRiftItem = g.riftItems.get(index);

                        if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE || code == KeyEvent.VK_Z) {
                            g.riftFramesDoingNothing = -15000;
                            g.riftMoonAlpha = 0;
                            
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
                                if (g.riftItemsSelected[0].getId().equals("basementKey") || g.riftItemsSelected[1].getId().equals("basementKey")) {
                                    g.basementKey.setAmount(-1);
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

                                            g.riftTransparency = 0;
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
                        case KeyEvent.VK_A -> g.platformer.setMovingLeft(true);
                        case KeyEvent.VK_D -> g.platformer.setMovingRight(true);
                        case KeyEvent.VK_SPACE -> g.platformer.jump();
                    }
                }
                case CORNFIELD -> {
                    switch (code) {
                        case KeyEvent.VK_W -> holdingW = true;
                        case KeyEvent.VK_S -> holdingS = true;
                        case KeyEvent.VK_D -> holdingD = true;
                        case KeyEvent.VK_A -> holdingA = true;
                    }
                }
                case FIELD -> {
                    if(g.field.isInGeneratorMinigame()) {
                        
                        if (code == KeyEvent.VK_Z || code == KeyEvent.VK_SPACE) {
                            int x0 = ((int) (g.fixedUpdatesAnim * 6.5F) + (int) (Math.sin(g.fixedUpdatesAnim * 0.05) * 12)) % 630;
                            Rectangle rect1 = new Rectangle(215 + x0, 485, 20, 130);

                            int order = 0;
                            for(short x : g.field.generatorXes.clone()) {
                                Rectangle rect2 = new Rectangle(220 + x, 495, 60, 110);

                                if(rect1.intersects(rect2) && g.field.generatorXes[order] != -1) {
                                    g.field.generatorXes[order] = -1;

                                    short[] array = g.field.generatorXes;
                                    if(array[0] == -1 && array[1] == -1) {
                                        g.field.regenerateGeneratorXes();

                                        g.generatorSound.play("generatorNextStage", 0.1);
                                        // SHOOT IMPULSE HERE
                                        g.field.impulseInterp = 1;
                                        g.sound.playRate("beep", 0.1, 0.1F);
                                        g.sound.playRate("enterNewRoom", 0.1, 0.2F);
                                        g.sound.playRate("harpoonShoot", 0.16, 0.8F);
                                        
                                        if(g.field.getBlimp().lockedOn) {
                                            g.field.getBlimp().lockedOn = false;
                                            g.field.getBlimp().untilNextAttack = 5 + (int) (Math.random() * 5);
                                            
                                            new Pepitimer(() -> {
                                                g.field.getBlimp().untilDirects = 5;
                                            }, 2000);
                                        }
                                    } else {
                                        g.generatorSound.play("generatorSuccess", 0.1);
                                    }
                                    return;
                                }
                                order++;
                            }

                            g.field.regenerateGeneratorXes();

                            g.generatorSound.play("generatorFail", 0.1);
                        }
                    }
                    
                    if (g.field.a90.isActive()) {
                        g.field.a90.dying = true;
                    }
                    if(!g.field.lockedIn) {
                        switch (code) {
                            case KeyEvent.VK_W -> holdingW = true;
                            case KeyEvent.VK_S -> holdingS = true;
                            case KeyEvent.VK_D -> holdingD = true;
                            case KeyEvent.VK_A -> holdingA = true;
//                        case KeyEvent.VK_SHIFT -> holdingShift = true;
//                        case KeyEvent.VK_SPACE -> holdingSpace = true;
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
                    if(g.getNight() != null) {
                        g.getNight().setSoundless(false);
                    }
                }

                for(MediaPlayer player : g.music.clips) {
                    player.setVolume(g.music.clipVolume.get(player) * Math.sqrt(g.volume));
                }
                for(MediaPlayer player : g.sound.clips) {
                    if(g.sound.clipVolume.containsKey(player)) {
                        player.setVolume(g.sound.clipVolume.get(player) * Math.sqrt(g.volume));
                    }
                }
                for(MediaPlayer player : g.scaryCatSound.clips) {
                    player.setVolume(g.scaryCatSound.clipVolume.get(player) * Math.sqrt(g.volume));
                }
                for(MediaPlayer player : g.bingoSound.clips) {
                    player.setVolume(g.bingoSound.clipVolume.get(player) * Math.sqrt(g.volume));
                }
                for(MediaPlayer player : g.rainSound.clips) {
                    player.setVolume(g.rainSound.clipVolume.get(player) * Math.sqrt(g.volume));
                }
                for(MediaPlayer player : g.basementSound.clips) {
                    player.setVolume(g.basementSound.clipVolume.get(player) * Math.sqrt(g.volume));
                }
                for(MediaPlayer player : g.krunlicSound.clips) {
                    player.setVolume(g.krunlicSound.clipVolume.get(player) * Math.sqrt(g.volume));
                }
                for(MediaPlayer player : g.shockSound.clips) {
                    player.setVolume(g.shockSound.clipVolume.get(player) * Math.sqrt(g.volume));
                }

                for(MediaPlayer player : fanSounds.clips) {
                    player.setVolume(fanSounds.clipVolume.get(player) * Math.sqrt(g.volume));
                }
                for(MediaPlayer player : camSounds.clips) {
                    player.setVolume(camSounds.clipVolume.get(player) * Math.sqrt(g.volume));
                }
                
                
                boolean changeGeneratorSound = true;
                if(g.getNight() != null) {
                    if (g.getNight().getType().isBasement()) {
                        Basement basement = (Basement) g.getNight().env();

                        if (!basement.isInGeneratorMinigame()) {
                            changeGeneratorSound = false;
                        }
                    }
                }
                if(changeGeneratorSound) {
                    for(MediaPlayer player : g.generatorSound.clips) {
                        player.setVolume(g.generatorSound.clipVolume.get(player) * Math.sqrt(g.volume));
                    }
                }

                g.quickVolumeSeconds = 1;
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
            if(GamePanel.mirror) {
                g.lastFullyRenderedUnshaded = g.mirror(g.lastFullyRenderedUnshaded, 1);
            }
            g.lastBeforePause = g.lastFullyRenderedUnshaded;
            g.lastBeforePause = GamePanel.darkify(g.lastBeforePause, 3);
            g.paused = !g.paused;

            if(g.paused) {
                previous = g.state;
                g.state = GameState.UNLOADED;
                g.pauseAllSound(true);
                for (Pepitimer pepitimer : StaticLists.timers) {
                    pepitimer.gamePause();
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

            for(Pepitimer timer : g.getNight().cancelAfterGame) {
                timer.resume();
            }
            if(g.getNight().getType().isBasement()) {
                g.basementSound.resume(false);
            }
        } else {
            int coins = 0;
            if(g.getNight().getType().isEndless()) {
                coins = g.endless.getCoins();
            } else if(g.getNight().getType().isBasement()) {
                Basement env = (Basement) g.getNight().env();
                coins = env.getCoins();
            }
            
            if (coins >= g.millyShopItems[g.selectedMillyItem].getPrice() || infiniteMoneyGlitch) {
                
                if(g.getNight().getType().isEndless()) {
                    g.endless.addCoins(-g.millyShopItems[g.selectedMillyItem].getPrice());
                    
                } else if(g.getNight().getType().isBasement()) {
                    Basement env = (Basement) g.getNight().env();
                    env.addCoins(-g.millyShopItems[g.selectedMillyItem].getPrice());
                }
                
                Statistics.ITEMS_BOUGHT.increment();
                
                
                Item item = g.millyShopItems[g.selectedMillyItem].getItem();

                if(item.getId().equals("starlightBottle")) {
                    g.starlightBottle.safeAdd(1);
                } else {
                    item.enable();

                    if (g.fullItemList.contains(item)) {
                        g.usedItems.add(item);
                    }

                    if(item.getId().equals("birthdayHat") || item.getId().equals("basementKey")) {
                        g.riftGlitch.enable();
                        g.usedItems.add(g.riftGlitch);
                        System.out.println("enabled rift glitches");
                    }
                }

                if(item.getId().equals("adblocker")) {
                    g.adBlocked = false;
                }
                if(item.getId().equals("megaSoda")) {
                    g.getNight().megaSodaUses = 4;
                    
                    if(g.getNight().getColaCat().getAILevel() < 1) {
                        g.getNight().getColaCat().setAILevel(1);
                    }
                }
                if(item.getId().equals("shadowblocker")) {
                    g.getNight().setShadowblocker(new Shadowblocker(1));
                }
                if(item instanceof Corn c) {
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
    
    public void startMillyShop() {
        for(Pepitimer timer : g.getNight().cancelAfterGame) {
            timer.pause();
        }
        
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

        g.fadeOut(255, 0, 2);

        g.redrawMillyShop();
        g.recalculateMillyRects();
    }

    boolean holdingB = false;
    boolean holdingTab = false;
    boolean holdingE = false;
    boolean holdingShift = false;
    boolean holdingSpace = false;
    
    public boolean holdingW = false;
    public boolean holdingS = false;
    public boolean holdingD = false;
    public boolean holdingA = false;
    
    public boolean holdingI = false;
    public boolean holdingK = false;
    public boolean holdingJ = false;
    public boolean holdingL = false;
    
    
    public boolean holdingFlashlight = false;
    

    boolean isInEnemiesRectangle = false;
    boolean hoveringPepitoClock = false;
    boolean hoveringNeonSog = false;
    boolean hoveringNeonSogSign = false;
    boolean hoveringShadowblockerButton = false;


    SoundTest soundTest;

    Rat a;
    
    String krunlicActivation = "";

    public boolean trueMouseHeld = false;
    public boolean mouseHeld = false;
    public boolean isRightClick = false;
    boolean holdingVolumeButton = false;
    boolean hoveringNightReset = false;
    boolean confirmNightReset = false;
    boolean hoveringFpsCap = false;
    boolean hoveringJumpscareShake = false;
    boolean hoveringLanguage = false;

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_B -> holdingB = false;
            case KeyEvent.VK_TAB -> holdingTab = false;
            case KeyEvent.VK_E -> {
                holdingE = false;
                g.holdingEFrames = 0;
            }
        }
        if(!e.isShiftDown()) {
            holdingShift = false;
        }
        
        if(g.state == GameState.PLATFORMER) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A -> g.platformer.setMovingLeft(false);
                case KeyEvent.VK_D -> g.platformer.setMovingRight(false);
            }
        } else if(g.state == GameState.CORNFIELD) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W -> holdingW = false;
                case KeyEvent.VK_S -> holdingS = false;
                case KeyEvent.VK_D -> holdingD = false;
                case KeyEvent.VK_A -> holdingA = false;
            }
        } else if(g.state == GameState.FIELD || g.state == GameState.UH_OH) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W -> holdingW = false;
                case KeyEvent.VK_S -> holdingS = false;
                case KeyEvent.VK_D -> holdingD = false;
                case KeyEvent.VK_A -> holdingA = false;
                case KeyEvent.VK_SHIFT -> holdingShift = false;
                case KeyEvent.VK_SPACE -> holdingSpace = false;

                case KeyEvent.VK_I -> holdingI = false;
                case KeyEvent.VK_J -> holdingJ = false;
                case KeyEvent.VK_K -> holdingK = false;
                case KeyEvent.VK_L -> holdingL = false;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent event) {
        boolean cancelMouseHolding = false;
        trueMouseHeld = true;

        Point rescaledPoint = new Point((int) ((pointerPosition.x - g.centerX) / g.widthModifier), (int) ((pointerPosition.y - g.centerY) / g.heightModifier));

        if(g.state == GameState.DISCLAIMER) {
            g.launch();
            return;
        }
        if(g.pressAnyKey) {
            pressAnyKeyStuff();
            cancelMouseHolding = true;
        } else {

            isRightClick = event.getButton() == MouseEvent.BUTTON3;

            switch (g.state) {
                case UNLOADED -> {
                    if(previous == GameState.GAME) {
                        if(g.pauseDieSelected && g.getNight().getEvent().isInGame()) {
                            unpause();
                            g.paused = false;
                            g.jumpscare("pause", g.getNight().getId());
                        }
                    }
                    if(previous == GameState.FIELD) {
                        if(g.pauseDieSelected) {
                            unpause();
                            g.paused = false;
                            g.field.kill(g, "pause");
                        }
                    }
                }
                case MENU -> {
                    if (new Rectangle(1025, 20, 35, 35).contains(rescaledPoint)) {
                        g.save();
                        System.exit(0);
                    }
                    
                    if(g.hoveringPlatButton) {
                        g.startPlatformer();
                    }
                    if (g.discord == g.discordStates[1]) {
                        try {
                            g.discord = g.discordStates[0];
                            Desktop.getDesktop().browse(new URI("https://discord.gg/r3re2hXu7k"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if(g.musicMenu == g.musicMenuStates[1]) {
                        if(g.musicDiscs.size() > 1) {
                            g.startMusicMenu();
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
                    
                    if(g.hoveringPepVoteButton) {
                        try {
                            Desktop.getDesktop().browse(g.pepVoteButton.getUrl().toURI());
                        } catch (IOException | URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                case GAME -> {
                    Enviornment env = g.getNight().env;
                    int maxOffset = env.maxOffset();
                    
                    g.getNight().afk = 17;
                    g.recalculateButtons(GameState.GAME);

                    if(g.getNight().getKiji().getState() != 0) {
                        if(g.getNight().getKiji().getState() == 1) {
                            g.sound.play("kijiSuccess", 0.1F);
                        }
                        return;
                    }

                    if(g.getNight().getType() == GameType.SHADOW) {
                        if (GamePanel.mirror) {
                            rescaledPoint = new Point(1080 - rescaledPoint.x, rescaledPoint.y);
                        }
                    }
                    
                    if(g.soggyPen.isEnabled() && holdingShift) {
                        Graphics2D graphics2D = (Graphics2D) g.getNight().soggyPenCanvas.getGraphics();
                        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        int green = (int) ((Math.sin(g.fixedUpdatesAnim / 400F) / 2 + 1) * 255);
                        graphics2D.setColor(isRightClick ? Color.WHITE : new Color(0, Math.max(0, Math.min(255, green)), 255));
                        int dia = isRightClick ? 5 : 3;

                        graphics2D.fillOval(rescaledPoint.x - 2 - g.offsetX + maxOffset, rescaledPoint.y - 2, dia, dia);
                        graphics2D.dispose();

                        return;
                    }

                    if(g.getNight().getEvent() == GameEvent.BASEMENT_KEY) {
                        BasementKeyOffice office = ((BasementKeyOffice) env);

                        office.setHoveringCanvas(new Rectangle(g.fixedOffsetX - maxOffset + 12, 239, 288, 184).contains(rescaledPoint) ||
                                new Rectangle(g.fixedOffsetX - maxOffset + 3045, 239, 288, 184).contains(rescaledPoint));
                        
                        if(office.isHoveringCanvas()) {
                            Graphics2D graphics2D = (Graphics2D) office.getCanvas().getGraphics();
                            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            graphics2D.setColor(isRightClick ? new Color(211, 191, 179) : Color.BLACK);
                            int dia = isRightClick ? 5 : 3;
                            
                            graphics2D.fillOval(rescaledPoint.x - 2 - 17 - g.offsetX, rescaledPoint.y - 2 - 244, dia, dia);
                            graphics2D.fillOval(rescaledPoint.x - 2 - 17 - g.offsetX + 3033, rescaledPoint.y - 2 - 244, dia, dia);
                            graphics2D.dispose();
                         
                            return;
                        }
                        
                        if(office.isHoveringEvilDoor()) {
                            if (office.getEvilDoorPercent() == 1) {
                                office.setEvilDoorPercent(0.999F);
                                
                                g.sound.play("evilDoorOpen", 0.1);
                                
                                new Pepitimer(() -> {
                                    g.everySecond.put("evilDoor", () -> {
                                        if(!g.getNight().getEvent().isInGame()) {
                                            g.everySecond.remove("evilDoor");
                                        }
                                        office.setEvilDoorPercent(office.getEvilDoorPercent() - 0.05F);
                                        g.sound.play("evilDoorSqueak" + (int) (Math.random() * 6 + 1), 0.1);

                                        if (office.getEvilDoorPercent() <= 0) {
                                            office.setEvilDoorPercent(0);
                                            g.everySecond.remove("evilDoor");
                                        }
                                    });
                                }, 4000);
                            }
                            if (office.getEvilDoorPercent() == 0) {
                                if(g.basementEnterTimer != null)
                                    return;

                                g.sound.playRate(g.everEnteredBasement ? "evilDoorEnterShort" : "evilDoorEnter", 0.15, 0.15);
                                g.fadeOut((int) g.tintAlpha, 255, 0.2F);
                                
                                g.basementEnterTimer = new Pepitimer(() -> {
                                    g.basementKey.disable();
                                    g.usedItems.remove(g.basementKey);
                                    
                                    g.state = GameState.HALFLOADED;
                                    GameType oldType = g.type;
                                    g.type = GameType.BASEMENT;
                                    g.rainSound.stop();
                                    g.startGame();

                                    ((Basement) g.getNight().env).setDoWiresWork(!office.powerOff);
                                    
                                    if(oldType == GameType.SHADOW) {
                                        g.type = GameType.SHADOW;
                                        g.getNight().setType(GameType.SHADOW);
                                        g.repaintOffice();
                                        g.getNight().repaintMonitorImage();
                                        g.music.play("theShadow", 0.1);
                                    }

                                    g.everEnteredBasement = true;
                                    
                                }, g.everEnteredBasement ? 0 : 4000);
                            }

                            return;
                        }
                        
                        if((Achievements.BASEMENT.isObtained() || Achievements.BASEMENT_PARTY.isObtained()) && !office.powerOff) {
                            if(new Rectangle(690 - maxOffset + g.offsetX, 516, 49, 84).contains(rescaledPoint) ||
                                    new Rectangle(690+3033 - maxOffset + g.offsetX, 516, 49, 84).contains(rescaledPoint)) {
                                office.powerOff = true;
                                g.sound.play("powerdown", 0.15);
                                g.sound.playRate("select", 0.1, 0.5);
                                
                                for(MediaPlayer player : g.music.clips) {
                                    if(player.getMedia().getSource().contains("brokenRadioSong.mp3")) {
                                        player.stop();
                                        player.dispose();
                                        g.music.clips.remove(player);
                                    }
                                }

                                return;
                            }
                        }
                    }
                    
                    if(g.getNight().getType() == GameType.HYDROPHOBIA && g.getNight().getEvent().isInGame()) {
                        HChamber chamber = ((HChamber) g.getNight().env());

                        if (!g.inLocker) {
                            if (chamber.isHoveringCompass()) {
                                chamber.setShowCompassHint(false);
                                chamber.setGoalCompassRotation(chamber.getGoalCompassRotation() + 180);

                                if (chamber.getGoalCompassRotation() > 360) {
                                    chamber.setGoalCompassRotation(chamber.getGoalCompassRotation() - 360);
                                    chamber.setCompassRotation(chamber.getCompassRotation() - 360);
                                }

                                g.sound.playRate("compassTurn", 0.08, 0.9F + Math.random() / 10F);

                                g.getNight().getHydrophobia().turn();
                                
                                return;
                            }
                            if(chamber.hasCup() && chamber.table.x < 1480) {
                                Rectangle table = chamber.table;
                                Rectangle newTable = new Rectangle(g.offsetX - maxOffset + table.x, table.y - table.height / 10, table.width, table.height / 5);
                                if(newTable.contains(rescaledPoint)) {
                                    chamber.setHasCup(false);
                                    chamber.ermIndex = 0;

                                    float percent = ((rescaledPoint.x - g.offsetX + maxOffset - table.x) * 1F) / table.width;
                                    float size = (table.width / 900F);
                                    int width = (int) (141 * size);
                                    int height = (int) (105 * size);
                                    chamber.cup = new Rectangle((int) (table.x + percent * (table.width - width)), (int) (table.y + table.height / 14F - height), width, height);
                                    
                                    g.repaintOffice();
                                    g.sound.play("select", 0.1);
                                    return;
                                }
                            }
                            if (chamber.getBarrierRotation() <= -90 && chamber.isHoveringExit()) {
                                if(chamber.getPrefieldCount() == 5) {
                                    if(g.getNight().lockedIn)
                                        return;
                                    
                                    // FIELD INTRO
                                    g.getNight().lockedIn = true;
                                    
                                    g.everyFixedUpdate.put("fieldIntroOffsetX", () -> {
                                        g.offsetX = (short) ((g.offsetX * 6F + 200) / 7F);
                                        
                                        if(Math.abs(g.offsetX - 200) < 8) {
                                            g.everyFixedUpdate.remove("fieldIntroOffsetX");
                                            
                                            g.everyFixedUpdate.put("fieldIntroOffsetX2", () -> {
                                                if(g.offsetX < 200) {
                                                    g.offsetX++;
                                                } else if(g.offsetX > 200) {
                                                    g.offsetX--;
                                                }
                                                
                                                if(g.offsetX == 200) {
                                                    g.everyFixedUpdate.remove("fieldIntroOffsetX2");

                                                    new Pepitimer(() -> {
                                                        g.getNight().lockedIn = true;
                                                        g.fieldIntro();
                                                    }, chamber.isRespawnCheckpoint() ? 1000 : 1500);
                                                }
                                            });
                                        }
                                    });
                                    // FIELD INTRO
                                    return;
                                }
                                
                                if(chamber.isInDustons()) {
                                    chamber.setInDustons(false);
                                    chamber.regenerateFurniture();
                                    chamber.setMaxOffset(400);
                                    g.offsetX = 200;
                                    
                                    g.repaintOffice();
                                    
                                    if(g.sunglassesOn) {
                                        g.sgPolygons.clear();
                                        g.sgPolygons.add(GamePanel.rectangleToPolygon(chamber.exit));
                                    }
                                    for(MediaPlayer clip : g.sound.clips) {
                                        if(clip.getMedia().getSource().contains("dustonTune")) {
                                            clip.setVolume(0);
                                            g.sound.clipVolume.put(clip, 0d);
                                        }
                                    }
                                    g.fadeOut(255, 70, 0.4F);
                                    g.sound.play("enterNewRoom", 0.17);
                                    return;
                                }
                                
                                if (chamber.getBarrierRotation() <= -90) {
                                    
                                    if(chamber.isRewardRoom()) {
                                        g.everyFixedUpdate.put("hydroChamberTransition", () -> {
                                            chamber.daZoom += 1.5F;
                                            chamber.daZoom *= 1.15F;

                                            if (chamber.daZoom > 315) {
                                                g.everyFixedUpdate.remove("hydroChamberTransition");
                                                
                                                g.sound.stop();
                                                g.music.stop();

                                                g.getNight().resetTimers();
                                                short secondsAtStart = chamber.getOldNight().secondsAtStart;
                                                
                                                g.type = chamber.getOldNight().getType();
                                                g.night = chamber.getOldNight();
                                                g.getNight().start();
                                                g.getNight().secondsAtStart = secondsAtStart;
                                                
                                                g.starlightMillis = 7000;
                                                g.announceNight((byte) 1, GameType.HYDROPHOBIA);
                                                g.nightAnnounceText = GamePanel.getString("returnFromHydro");

                                                ((Basement) g.getNight().env).beenToHydrophobiaChamber = true;

                                                AchievementHandler.obtain(g, Achievements.EXIT);
                                                
                                                g.restartBasementSong();

                                                g.repaintOffice();

                                                short endValue = 200;
                                                float speed = 0.1F;
                                                if (g.type == GameType.BASEMENT) {
                                                    endValue = 160;
                                                    speed = 0.2F;
                                                }
                                                g.fadeOut(255, endValue, g.flashlight.isEnabled() ? speed * 2 : speed);
                                            }
                                        });
                                    } else {
                                        if(chamber.isPendingPrefield()) {
                                            chamber.setPrefieldCount(chamber.getPrefieldCount() + 1);
                                            chamber.setInPrefield(true);
                                        }

                                        if(chamber.table.x < 1480) {
                                            if (chamber.roomsTillKey <= 0) {
                                                chamber.roomsTillKey = 1000;
                                            }
                                        }
                                        chamber.roomsTillKey--;

                                        
                                        enterNewHydrophobiaRoom(chamber);

                                        
                                        if (!chamber.isRewardRoom() && !chamber.isInPrefield()) {
                                            g.music.stop();
                                            int secondsLeft = (Math.max(0, g.getNight().getHydrophobia().distance() - 1)) * 25 + g.getNight().getHydrophobia().getSecondsUntilStep();
                                            g.music.playFromSeconds("hydrophobiaSounds", 0.19, Math.max(0, 80 - secondsLeft));
                                        }
                                        if(chamber.isRewardRoom()) {
                                            g.music.stop();
                                            g.music.play("scaryAhhDoor", 0.25, true);
                                        }
                                        if(chamber.isInPrefield() && chamber.getPrefieldCount() == 1) {
                                            g.music.stop();
                                            g.music.play("halfwayHallway", 0.13, true);
                                            
                                            g.sound.play("dustonTune", 0, true);
                                        }
                                    }
                                } else {
                                    g.sound.play("error", 0.08);
                                }
                                return;
                            }
                            if (chamber.isHoveringLocker()) {
                                g.inLocker = true;
                                g.fadeOut(255, 180, 1);
                                chamber.setShake(Math.max(15, chamber.getShake()));
                                g.sound.play("lockerEnter", 0.15);

                                chamber.setHoveringLocker(false);
                                return;
                            }
                            if(chamber.isHoveringPen()) {
                                chamber.setHoveringPen(false);
                                chamber.setPen(false);
                                g.soggyPen.safeAdd(9);
                                g.repaintOffice();

                                g.sound.playRate("waterSpray" + (int) (Math.random() * 3 + 1), 0.06, 0.9F + Math.random() / 10F);
                                g.sound.play("select", 0.1);
                                g.sound.playRate("icePotionUse", 0.2, 0.75);

                                return;
                            }
                            if(chamber.isHoveringKey()) {
                                chamber.key = new Rectangle(2000, 1000, 1, 1);
                                chamber.setHoveringKey(false);
                                chamber.setHasKey(true);

                                g.repaintOffice();
                                
                                g.sound.play("select", 0.1);
                                g.sound.playRate("icePotionUse", 0.15, 0.3);
                                g.sound.playRate("icePotionUse", 0.15, 0.2);
                                return;
                            }
                            if(chamber.isHoveringCup()) {
                                chamber.cup = new Rectangle(2000, 1000, 1, 1);
                                chamber.setHoveringCup(false);
                                chamber.setHasCup(true);

                                g.repaintOffice();

                                g.sound.play("select", 0.1);
                                return;
                            }
                            
                            if(chamber.isInPrefield() && chamber.getPrefieldCount() == 1) {
                                if(chamber.isHoveringReinforced()) {
                                    if(chamber.hasKey()) {
                                        if (chamber.getReinforcedDoorPercent() == 1) {
                                            chamber.setReinforcedDoorPercent(0.99F);

                                            g.sound.play("reinforcedDoorOpen", 0.1);

                                            new Pepitimer(() -> {
                                                g.everySecond.put("reinforcedDoor", () -> {
                                                    if (!g.getNight().getEvent().isInGame()) {
                                                        g.everySecond.remove("reinforcedDoor");
                                                    }
                                                    chamber.setReinforcedDoorPercent(chamber.getReinforcedDoorPercent() - 0.1F);
                                                    g.sound.play("evilDoorSqueak" + (int) (Math.random() * 6 + 1), 0.1);

                                                    if (chamber.getReinforcedDoorPercent() <= 0) {
                                                        chamber.setReinforcedDoorPercent(0);
                                                        g.everySecond.remove("reinforcedDoor");
                                                    }
                                                    g.repaintOffice();
                                                });
                                            }, 3000);

                                            return;
                                        }
                                    } else {
                                        g.sound.play("error", 0.08);

                                        return;
                                    }
                                    if (chamber.getReinforcedDoorPercent() == 0) {
                                        chamber.setInDustons(true);
                                        chamber.regenerateFurniture();
                                        chamber.setHoveringReinforced(false);
                                        chamber.setMaxOffset(200);
                                        g.offsetX = 100;
                                        g.repaintOffice();

                                        for(MediaPlayer clip : g.sound.clips) {
                                            if(clip.getMedia().getSource().contains("dustonTune")) {
                                                clip.setVolume(0.16 * Math.sqrt(g.volume));
                                                g.sound.clipVolume.put(clip, 0.16);
                                            }
                                        }
                                        g.fadeOut(255, 70, 0.4F);
                                        g.sgPolygons.clear();
                                        g.sound.play("enterNewRoom", 0.17);
                                        
                                        return;
                                    }
                                }
                            }
                        } else {
                            return;
                        }
                    }


                    if(g.getNight().getType() == GameType.DAY) {
                        if(hoveringPepitoClock) {
                            g.night.seconds += 24;
                            g.sound.play("endlessClockSound", 0.1);
                            g.getNight().updateClockString();
                            g.fadeOut(g.endFade, Math.min(255, g.endFade + 40), 2);
                            return;
                        }
                        if(g.neonSogX < 0) {
                            if (new Rectangle(g.offsetX - maxOffset + 20, 0, 150, 260).contains(rescaledPoint)) {
                                if(!g.everyFixedUpdate.containsKey("neonSogAnim")) {
                                    g.sound.play("boop", 0.1);
                                }
                                g.everyFixedUpdate.put("neonSogAnim", () -> {
                                    g.neonSogX = Math.round(g.neonSogX / 1.1F);
                                    g.neonSogX++;
                                    
                                    if(g.neonSogX >= 0) {
                                        g.neonSogX = 0;
                                        g.everyFixedUpdate.remove("neonSogAnim");
                                        
                                        g.sound.play("metalPipe", 0.08);
                                    }
                                });
                            }
                        } else if(hoveringNeonSog) {
                            if(g.endless.getCoins() >= 100 && g.neonSogSkips < 5) {
                                g.sound.play("select", 0.08);
                                g.sound.play("sellsYourBalls", 0.2);
                                g.endless.addCoins(-100);
                                
                                g.neonSogSkips = 5;
                            } else {
                                g.sound.play("error", 0.08);
                            }
                            return;
                        }
                    }
                    if(g.getNight().getShadowblocker().state > 0) {
                        if(g.getNight().getShadowblocker().state < 3) {
                            if (new Rectangle(18, 18, 120, 120).contains(rescaledPoint)) {
                                g.getNight().getShadowblocker().state = (byte) (-(g.getNight().getShadowblocker().state) + 3);
                                g.sound.play("select", 0.08);

                                return;
                            }
                        }
                        if(g.getNight().getShadowblocker().selected != -1 && g.getNight().getShadowblocker().state == 2) {
                            int enemiesSize = CustomNight.getEnemies().size();
                            for (int i = 0; i < 24; i++) {
                                int x = i % 6 * 105 + 230;
                                int y = i / 6 * 130 + 130;

                                if (new Rectangle(x, y, 95, 120).contains(rescaledPoint)) {
                                    if (i < enemiesSize) {
                                        CustomNightEnemy enemy = CustomNight.getEnemies().get(g.getNight().getShadowblocker().selected);
                                        
                                        Enemy en = g.getNight().getEnemies()[enemy.getId()];
                                        enemy.setAI(en.getAILevel());

                                        switch (enemy.getId()) {
                                            case 0 -> enemy.setAI(g.getNight().getPepito().pepitoAI);
                                            case 1 -> enemy.setAI(g.getNight().getPepito().notPepitoAI);
                                        }

                                        if (enemy.getAI() > 0) {
                                            g.getNight().getShadowblocker().slop = g.getShadowblockerThing(enemy, new Color(140, 0, 255));
                                            g.getNight().getShadowblocker().slopInt[0] = x;
                                            g.getNight().getShadowblocker().slopInt[1] = y;
                                            g.getNight().getShadowblocker().slopName = GamePanel.getString(enemy.getName());
                                            g.sound.play("select", 0.08);
                                            
                                            new Pepitimer(() -> {
                                                g.sound.play("shadowblockerErase", 0.1);
                                            }, 1700);
                                            
                                            g.getNight().getShadowblocker().state = 3;
                                        }
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    if(g.hoveringBasementMonitor) {
                        Basement basement = ((Basement) env);
                        
                        if(basement.getStage() < 6) {
                            if(basement.getStage() >= 4) {
                                if (basement.getCharge() >= 1) {
                                    if(!basement.isInGeneratorMinigame()) {
                                        basement.setInGeneratorMinigame(true);

                                        if (basement.getStage() == 4) {
                                            g.generatorSound.play("connectToGeneratorEmpty", 0.15, true);
                                        } else {
                                            for (MediaPlayer player : g.generatorSound.clips) {
                                                player.setVolume(g.generatorSound.clipVolume.get(player) * Math.sqrt(g.volume));
                                            }
                                        }
                                    }
                                    return;
                                } else if (basement.getCharge() == 0 && basement.getMonitorHeight() >= 115) {
                                    g.everySecond10th.put("basementMonitorCharge", () -> {
                                        g.generatorSound.playRate("generatorNextStage", 0.08, 0.8 + basement.getCharge());
                                        basement.setCharge(basement.getCharge() + 0.05F);
    
                                        if (basement.getCharge() >= 1) {
                                            basement.setCharge(1);
                                            g.everySecond10th.remove("basementMonitorCharge");
                                        }
                                        g.getNight().redrawBasementScreen();
                                    });
    
                                    return;
                                }
                            }
                        }
                    }
                    if(g.basementLadderHovering) {
                        g.basementLadderHeld = true;
                        return;
                    }

                    if(!isRightClick) {
                        Wires wires = g.getNight().getWires();
                        if(wires.isActive()) {
                            Rectangle h = wires.getHitbox();
                            Rectangle hitbox = new Rectangle(g.offsetX - maxOffset + h.x, h.y, h.width, h.height + 10);
                            
                            if(hitbox.contains(rescaledPoint)) {
                                wires.hit();
                            }
                        }
                    }
                    
                    if (isRightClick) {
                        MirrorCat mirrorCat = g.getNight().getMirrorCat();
                        if(mirrorCat.isActive()) {
                            if(mirrorCat.isInsideUnmirrored(rescaledPoint)) {
                                mirrorCat.kill();
                                return;
                            }
                        }

                        if (g.flashlight.isEnabled()) {
                            // FLASHLIGHT STUFF
                            holdingFlashlight = true;
                            g.holdingFlashlightFrames = 0;
                        }
                        
                        
                        if(g.getNight().getEvent() == GameEvent.NONE) {
                            
                        } else if(g.getNight().getEvent() == GameEvent.LEMONADE) {
                            g.getNight().getLemonadeCat().throwLemonade(pointerPosition, GamePanel.isMirror());
                            
                        } else if(g.getNight().getEvent() == GameEvent.DEEP_FLOOD) {
                            DeepSeaCreature dsc = g.getNight().getDsc();
                            
                            if(dsc.isFight() && dsc.getGunExtend() == 0 && !dsc.cantShoot() && !dsc.floodReceding) {
                                dsc.setGunExtend(260);

                                dsc.setShake(60);
                                dsc.setFlash(true);
                                new Pepitimer(() -> dsc.setFlash(false), 70);

                                dsc.attack(rescaledPoint);

                                BufferedImage bubbleImage = g.getBubbleImage(0.25F);
                                short phase = (short) (g.fixedUpdatesAnim % 251);
                                for (int i = 0; i < 10; i++) {
                                    g.bubbles.add(new BubbleParticle(rescaledPoint.x - g.offsetX + maxOffset, rescaledPoint.y + 70, phase, 130, bubbleImage));
                                }
                            }
                        }
                    } else if (g.getNight().env().boop.contains(new Point(rescaledPoint.x - g.offsetX + maxOffset, rescaledPoint.y))) {
                        g.sound.play("boop", 0.08);
                        cancelMouseHolding = true;

                        BingoHandler.completeTask(BingoTask.BOOP_PEPITO);

                        if (g.sensor.isEnabled() && g.console.getSize() < 16 && !(g.getNight().getWires().isActive() && g.getNight().getWires().getHitbox() == g.getNight().env.boop)) {
                            g.console.add(GamePanel.getString("sensorBoop"));
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

                            Rectangle hitbox = door.getButtonHitbox(g.offsetX, g.getNight().env().maxOffset());
                            if (hitbox.contains(rescaledPoint)) {
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

                                        g.sound.play("doorSlam", 0.08, Level.getDoorPan(door, g.getNight().getType()));
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
                            if(g.shadowCheckpointUsed == 2 && g.getNight().getAstartaBoss().getStartCutsceneY() != 0) {
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
                        if (new Rectangle(g.offsetX - maxOffset + env.generator.x, env.generator.y, env.generator.width, env.generator.height).contains(rescaledPoint) && !g.getNight().isInGeneratorMinigame() && !(g.getNight().getGeneratorEnergy() != -1)) {
                            g.getNight().startGeneratorMinigame();
                            g.hoveringGenerator = false;
                            return;
                        }
                    }

                    if(g.getNight().env().getBgIndex() != 4 && !isRightClick) {
                        for (int i = 0; i < GamePanel.balloons.size(); i++) {
                            if(GamePanel.balloons.get(i) == null)
                                continue;
                            
                            Rectangle button = GamePanel.balloons.get(i).getRectangle(g.offsetX, maxOffset);
                            if (button.contains(rescaledPoint)) {
                                GamePanel.balloons.remove(GamePanel.balloons.get(i));
                                g.sound.play("balloonPop", 0.05);
                                
                                Statistics.BALLOONS_POPPED.increment();

                                if (GamePanel.balloons.isEmpty()) {
                                    if (g.getNight().getEvent() != GameEvent.MAXWELL) {
                                        if (g.getNight().getType() == GameType.PREPARTY) {
                                            g.type = GameType.PARTY;
                                            g.startGame();
                                        } else if (g.getNight().getType() == GameType.BASEMENT) {
                                            Basement basement = (Basement) g.getNight().env();
                                            if(basement.getStage() < 4) {
                                                g.type = GameType.BASEMENT_PARTY;
                                                g.startGame();
                                                ((Basement) g.getNight().env).setDoWiresWork(basement.doWiresWork());
                                            }
                                        }
                                    }
                                }
                                g.redrawBHO = true;
                                return;
                            }
                        }
                    }

                    if(g.getNight().getType() == GameType.DAY && g.getNight().getEvent().isInGame()) {
                        if (new Rectangle((int) ((g.offsetX + 685) * g.widthModifier) + g.centerX, (int) (315 * g.heightModifier) + g.centerY, (int) (315 * g.widthModifier), (int) (325 * g.heightModifier)).contains(pointerPosition)) {
                            startMillyShop();

                            if (g.endless.getNight() == 3) {
                                g.music.play("partyFavors", 0.08, true);

                                AchievementHandler.obtain(g, Achievements.VISIT_PARTY);

                                final float[] millyPartyHue = {0};
                                HashMap<Float, BufferedImage> millyPartyCache = new HashMap<>();

                                millyDisco = new RepeatingPepitimer(() -> {
                                    millyPartyHue[0] += 0.05F;
                                    if (millyPartyHue[0] >= 1) {
                                        millyPartyHue[0] = 0;
                                    }
                                    if (!millyPartyCache.containsKey(millyPartyHue[0])) {
                                        millyPartyCache.put(millyPartyHue[0], GamePanel.changeHSB(g.millyShopColors.request(), millyPartyHue[0], 1, 1));
                                    }
                                    g.millyShopColorsChanging = millyPartyCache.get(millyPartyHue[0]);
                                }, 100, 100);
                            } else if (g.endless.getNight() == 6) {
                                g.music.play("larrySweep", 0.15, true);
                            } else {
                                g.music.play("millyShop", 0.1, true);
                            }
                        }
                    }
                    if(g.getNight().getType().isBasement() && g.getNight().getEvent().isInGame()) {
                        Basement basement = (Basement) g.getNight().env();
                        
                        if(basement.isMillyVisiting()) {
                            Rectangle rect = new Rectangle((int) (g.offsetX - maxOffset + basement.getMillyX() + 60), 230, 380, 390);
                            
                            if(rect.contains(rescaledPoint)) {
                                startMillyShop();

                                g.basementSound.pause(false);
                                g.music.play("spookers", 0.1, true);
                                
                                if(basement.doWiresWork()) {
                                    g.doMillyFlicker = true;
                                    g.sound.play("flicker" + (int) (Math.round(Math.random()) + 1), 0.05);

                                    new Pepitimer(() -> {
                                        g.sound.play("flicker" + (int) (Math.round(Math.random()) + 1), 0.05);

                                        new Pepitimer(() -> {
                                            g.doMillyFlicker = false;
                                        }, 250);
                                    }, 400);
                                } else {
                                    g.basementMillyLight = g.alphaify(g.basementMillyLightSource.request(), 0.97F);
                                }
                            }
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

                                        Statistics.GOTTEN_BURN_ENDING.increment();

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
                case FIELD -> {
                    Field field = g.field;

                    if (!field.isInCar() && !field.lockedIn && field.isHoveringCar()) {
                        field.addDistance(2.5F);
//                        field.addDistance(360);
//                        field.addDistance(660);
//                        field.addDistance(860);
                        field.setInCar(true);
                        
                        field.setYaw(0);
                        field.setPitch(0);
                        
                        field.saveSnapshot();
                        
                        g.sound.play("fieldCarDoorClose", 0.15F);
                        g.fadeOut(255, 0, 1.2F);
                        
                        return;
                    }
                    if(field.isInCar()) {
                        if (field.isHoveringLever()) {
                            if(field.isInGeneratorMinigame()) {
                                field.quitGenerator();
                                g.sound.playRate("fieldLever", 0.1, 0.9);
                            } else {
                                field.regenerateGeneratorXes();
                                field.setInGeneratorMinigame(true);
                                field.leverDegreesGoal = (float) (Math.PI * 3 / 2);
                                g.sound.playRate("fieldLever", 0.1, 1);
                            }
                            return;
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
                            } else {
                                if (pointerPosition.x > 940 * g.widthModifier + g.centerX) {
                                    shiftAchievements();
                                }
                                int categories = 4;
                                double d = (double) (g.achievementsScrollY) / (160 + 30 + Achievements.values().length * 155 + categories * 80 - 530);
                                int height = 530 / Achievements.values().length * 2;
                                if(new Rectangle(-g.achievementsScrollX, (int) (d * (530 - height)) + 110, 10, height).contains(rescaledPoint)) {
                                    g.holdingAchievementSlider = true;
                                }
                            }

                            if(g.hoveringInvestigation) {
                                g.startInvestigation();
                                g.sound.play("select", 0.1);
                            }
                        }
                    }
                }
                case INVESTIGATION -> {
                    if (new Rectangle(1020, 20, 35, 35).contains(rescaledPoint)) {
                        g.backToMainMenu();
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
                    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
                    Graphics2D graphics2D = (Graphics2D) image.getGraphics();
                    graphics2D.setFont(g.yuGothicPlain60);
                    
                    
                    if (new Rectangle((int) (g.volume * 800 + 115), 220 + g.settingsScrollY, 50, 50).contains(rescaledPoint)) {
                        holdingVolumeButton = true;
                    } else if (new Rectangle(160 + g.textLength(graphics2D, ">> " + GamePanel.getString("fixedRatio")), 310 + g.settingsScrollY, 60, 60).contains(rescaledPoint)) {
                        g.blackBorders = !g.blackBorders;
                        g.onResizeEvent();
                        if (g.blackBorders) {
                            g.sound.play("select", 0.1);
                        }
                    } else if (new Rectangle(160 + g.textLength(graphics2D, ">> " + GamePanel.getString("headphones")), 390 + g.settingsScrollY, 60, 60).contains(rescaledPoint)) {
                        g.headphones = !g.headphones;
                        if (g.headphones) {
                            g.sound.play("select", 0.1);
                        }
                    } else if(new Rectangle(160 + g.textLength(graphics2D, ">> " + GamePanel.getString("showDisclaimer")), 630 + g.settingsScrollY, 60, 60).contains(rescaledPoint)) {
                        g.disclaimer = !g.disclaimer;
                        if (g.disclaimer) {
                            g.sound.play("select", 0.1);
                        }
                    } else if(new Rectangle(160 + g.textLength(graphics2D, ">> " + GamePanel.getString("showManual")), 710 + g.settingsScrollY, 60, 60).contains(rescaledPoint)) {
                        g.showManual = !g.showManual;
                        if (g.showManual) {
                            g.sound.play("select", 0.1);
                        }
                    } else if(new Rectangle(160 + g.textLength(graphics2D, ">> " + GamePanel.getString("saveScreenshots")), 790 + g.settingsScrollY, 60, 60).contains(rescaledPoint)) {
                        g.saveScreenshots = !g.saveScreenshots;
                        if (g.saveScreenshots) {
                            g.sound.play("select", 0.1);
                        }
                    } else if(new Rectangle(160 + g.textLength(graphics2D, ">> " + GamePanel.getString("rtx")), 870 + g.settingsScrollY, 60, 60).contains(rescaledPoint)) {
                        g.bloom = !g.bloom;
                        if (g.bloom) {
                            g.sound.play("select", 0.1);
                        }
                    } else if(new Rectangle(160 + g.textLength(graphics2D, ">> " + GamePanel.getString("fpsCounter")), 950 + g.settingsScrollY, 60, 60).contains(rescaledPoint)) {
                        g.fpsCounters[0] = !g.fpsCounters[0];
                        if (g.fpsCounters[0]) {
                            g.sound.play("select", 0.1);
                        }
                    } else if(new Rectangle(160 + g.textLength(graphics2D, ">> " + GamePanel.getString("screenShake")), 1390 + g.settingsScrollY, 60, 60).contains(rescaledPoint)) {
                        g.screenShake = !g.screenShake;
                        if (g.screenShake) {
                            g.sound.play("select", 0.1);
                        }
                    } else if(new Rectangle(160 + g.textLength(graphics2D, ">> " + GamePanel.getString("disableFlickering")), 1470 + g.settingsScrollY, 60, 60).contains(rescaledPoint)) {
                        GamePanel.disableFlickering = !GamePanel.disableFlickering;
                        if (GamePanel.disableFlickering) {
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
                        switch (g.jumpscareShake) {
                            case 0 -> g.jumpscareShake = 1;
                            case 1 -> g.jumpscareShake = 2;
                            default -> g.jumpscareShake = 0;
                        }

                        g.sound.play("select", 0.1);
                    } else if(hoveringLanguage) {
                        switch (g.language) {
                            case "english" -> g.language = "russian";
                            case "russian" -> g.language = "english";
                        }

                        g.loadLanguage(g.language);
                        g.initializeFontMetrics();
                        g.initializeItemNames();
                        g.reloadMenuButtons();

                        g.sound.play("select", 0.1);
                    } else if (g.closeButton.contains(pointerPosition)) {
                        g.backToMainMenu();
                    }
                    
                    graphics2D.dispose();
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

                                clickItem(item);
                            } catch (IndexOutOfBoundsException ignored) {
                                g.sound.play("selectFail", 0.12);
                            }
                        }
                    }
                }
                case MUSIC_MENU -> {
                    if(new Rectangle(0, 253, 1080, 387).contains(rescaledPoint)) {
                        if(!g.menuSong.equals(g.hoveringMusicDisc)) {
                            g.menuSong = g.hoveringMusicDisc;
                            g.music.stop();
                            g.music.play(g.menuSong, 0.15, true);
                            g.musicMenuDiscX = 0;

                            g.sound.play("select", 0.1);
                        }
                    }
                    if (g.closeButton.contains(pointerPosition)) {
                        g.backToMainMenu();
                    }
                }
                case DRY_CAT_GAME -> {
                    g.dryCatGame.particles.add(new WaterParticle(rescaledPoint.x, rescaledPoint.y));
                    g.dryCatGame.attack(rescaledPoint);

                    g.sound.playRate("waterSpray" + (int) (Math.random() * 3 + 1), 0.06, 0.9F + Math.random() / 10F);
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
                        if (CustomNight.enemiesRectangle.contains(rescaledPoint)) {
                            if(CustomNight.selectedElement instanceof CustomNightEnemy) {
                                if (CustomNight.isCustom()) {
                                    if (isRightClick) {
                                        ((CustomNightEnemy) CustomNight.selectedElement).declick();
                                    } else {
                                        ((CustomNightEnemy) CustomNight.selectedElement).click(false);
                                    }
                                    CustomNight.holdingEnemyFrames = 0;

                                    if(((CustomNightEnemy) CustomNight.selectedElement).getAI() == 0) {
                                        g.sound.play("lowSound", 0.05);
                                    } else {
                                        if (isRightClick) {
                                            g.sound.play("aiDown", 0.05);
                                        } else {
                                            g.sound.play("aiUp", 0.05);
                                        }
                                        
                                        if(((CustomNightEnemy) CustomNight.selectedElement).getId() == 13) {
                                            if (((CustomNightEnemy) CustomNight.selectedElement).getAI() == 5) {
                                                g.sound.play("five alert", 0.15);
                                            }
                                        }
                                        if (((CustomNightEnemy) CustomNight.selectedElement).getAI() == 9) {
                                            g.sound.play("nine alert", 0.15);
                                        }
                                    }
                                } else {
                                    CustomNightEnemy e = ((CustomNightEnemy) CustomNight.selectedElement);
                                    e.setWobbleIntensity(6);
                                    g.sound.play("lowSound", 0.05);
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
                                int newId = (int) (Math.random() * 10000 + 1);
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
                                        g.music.play("tension", 0.05, true);
                                    }
                                }, 19400);
                            }
                        }
                        if(CustomNight.customSelected) {
                            g.sound.play("buttonPress", 0.08);
                            cancelLimbo();

                            if(CustomNight.custom) {
                                for (CustomNightEnemy enemy : CustomNight.getEnemies()) {
                                    CustomNight.customEnemyAIs.put(enemy, enemy.getAI());
                                }
                                for (CustomNightModifier modifier : CustomNight.getModifiers()) {
                                    CustomNight.customModifiers.put(modifier, modifier.isActive());
                                }
                            }

                            CustomNight.custom = !CustomNight.custom;
                            CustomNight.setEntityAIs();

                            if(!CustomNight.custom) {
                                CustomNight.setEntityAIs();
                            }
                        }
                    } catch (NullPointerException ignored) { }
                }
                case CRATE -> {
                    crateClick();
                }
            }
        }

        if(!cancelMouseHolding) {
            mouseHeld = true;
        }
    }

    RepeatingPepitimer millyDisco;

    // DONT GO TO MENU IF TRUE
    private boolean processDeathScreenPress() {
        if(g.state == GameState.GAME) {
            Level night = g.getNight();
            
            if(night.getEvent() == GameEvent.DYING) {
                if(night.getType() == GameType.HYDROPHOBIA) {
                    HChamber old = ((HChamber) night.env);
                    int deaths = old.getDeaths();
                    
                    if(deaths >= 2) {
                        if(old.showDeathOptions) {
                            if(old.allowDeathButtons) {
                                if (old.selectedDeathOption == 1) {
                                    doHydrophobiaRespawn();
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                return true;
                            }
                        } else {
                            old.showDeathOptions = true;
                            new Pepitimer(() -> {
                                old.allowDeathButtons = true;
                            }, 700);
                            return true;
                        }
                    } else {
                        doHydrophobiaRespawn();
                        return true;
                    }
                }
                
                
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
            } else if(night.getEvent() == GameEvent.WINNING) {
                if(night.getType().isBasement()) {
                    int hydroBonus = ((Basement) night.env).beenToHydrophobiaChamber ? 2 : 0;
                    
                    if(night.getType() == GameType.BASEMENT) {
                        g.createCrate(1 + hydroBonus);
                    }
                    if(night.getType() == GameType.BASEMENT_PARTY) {
                        g.createCrate(3 + hydroBonus);
                    }
                    
                    g.pressAnyKey = false;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseHeld = false;
        trueMouseHeld = false;
        isRightClick = false;

        pointerPosition = e.getPoint().getLocation();
        setPoint();

        if(holdingVolumeButton) {
            g.music.stop();
            g.music.play(g.menuSong, 0.15, true);
            holdingVolumeButton = false;
        }

        if(g.state == GameState.GAME) {
            if (g.getNight().getAstartaBoss() != null) {
                Mister mister = g.getNight().getAstartaBoss().getMister();
                if (mister.isActive() && mister.isBeingHeld()) {
                    g.unholdMister(mister);
                }
            }
            if(g.getNight().getKiji().isActive()) {
                g.getNight().getKiji().setMouseEverReleased(true);

                if(g.getNight().getKiji().getState() != 0) {
                    if (g.getNight().getKiji().getState() == 2) {
                        g.sound.play("kijiSuccess", 0.15F);
                    } else {
                        g.sound.play("kijiFail", 0.08F);
                    }
                }
            }
            g.basementLadderHeld = false;
            g.basementLadderFrames = 0;


            if(e.getButton() == MouseEvent.BUTTON3 && holdingFlashlight) {
                // FLASHLIGHT STUFF
                holdingFlashlight = false;
                
                if(g.holdingFlashlightFrames < 24) {
                    if (g.flashlight.isEnabled() && (g.getNight().getEvent() == GameEvent.NONE)) {
                        Point rescaledPoint = new Point((int) ((pointerPosition.x - g.centerX) / g.widthModifier), (int) ((pointerPosition.y - g.centerY) / g.heightModifier));
                        Enviornment env = g.getNight().env;
                        int maxOffset = env.maxOffset();

                        if (g.night.hasPower()) {
                            if (g.flashLightCooldown == 0) {
                                g.sound.play("sodaOpen", 0.03);
                                g.sound.play("camOut", 0.1);
                                g.fadeOut(0, g.endFade, 20);
                                g.night.addEnergy(-20F);
                                g.flashLightCooldown = 28;

                                if (g.night.getMSI().isActive()) {
                                    g.night.getMSI().kill(true, true);
                                    g.night.addEnergy(-4F);
                                } else {
                                    for (int i : g.getNight().getDoors().keySet()) {
                                        Door door = g.getNight().getDoors().get(i);
                                        Polygon hitbox = new Polygon(door.getHitbox().xpoints, door.getHitbox().ypoints, door.getHitbox().npoints);
                                        hitbox.translate(g.offsetX - maxOffset, 0);

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
                    }
                }

                g.holdingFlashlightFrames = 0;
            }
            
            
        } else if(g.state == GameState.CHALLENGE) {
            CustomNight.holdingEnemyFrames = 0;
            
        } else if(g.state == GameState.ACHIEVEMENTS) {
            g.holdingAchievementSlider = false;
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
        switch (g.state) {
            case SETTINGS -> {
                if(holdingVolumeButton) {
                    pointerPosition = e.getPoint().getLocation();
                    setPoint();

                    float oldVolume = g.volume;
                    g.volume = Math.min(1, Math.max((float) (((pointerPosition.x - g.centerX) / g.widthModifier - 140) / 800.0), 0));

                    for(MediaPlayer player : g.music.clips) {
                        player.setVolume(player.getVolume() / oldVolume * g.volume);
                    }
                }
            }
            case GAME -> {
                g.recalculateButtons(GameState.GAME);

                
                if(holdingFlashlight) {
                    pointerPosition = e.getPoint().getLocation();
                    setPoint();
                }
                if(g.getNight().env().getBgIndex() == 4) {
                    BasementKeyOffice office = (BasementKeyOffice) g.getNight().env();

                    if(office.isHoveringCanvas()) {
                        Point oldPoint = pointerPosition;
                        pointerPosition = e.getPoint().getLocation();
                        g.pointX = pointerPosition.x;
                        g.pointY = pointerPosition.y;
                        Point rescaledPoint = new Point((int) ((pointerPosition.x - g.centerX) / g.widthModifier), (int) ((pointerPosition.y - g.centerY) / g.heightModifier));
                        Point oldRescaledPoint = new Point((int) ((oldPoint.x - g.centerX) / g.widthModifier), (int) ((oldPoint.y - g.centerY) / g.heightModifier));

                        if(GamePanel.mirror) {
                            rescaledPoint = new Point(1080 - rescaledPoint.x, rescaledPoint.y);
                            oldRescaledPoint = new Point(1080 - oldRescaledPoint.x, oldRescaledPoint.y);
                        }

                        Graphics2D graphics2D = (Graphics2D) office.getCanvas().getGraphics();
                        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        graphics2D.setColor(isRightClick ? new Color(211, 191, 179) : Color.BLACK);
                        graphics2D.setStroke(new BasicStroke(isRightClick ? 5 : 3));

                        graphics2D.drawLine(oldRescaledPoint.x - 2 - 17 - g.offsetX, oldRescaledPoint.y - 2 - 244, rescaledPoint.x - 2 - 17 - g.offsetX, rescaledPoint.y - 2 - 244);
                        graphics2D.drawLine(oldRescaledPoint.x - 2 - 17 - g.offsetX + 3033, oldRescaledPoint.y - 2 - 244, rescaledPoint.x - 2 - 17 - g.offsetX + 3033, rescaledPoint.y - 2 - 244);

                        graphics2D.dispose();
                    }
                }

                if(g.soggyPen.isEnabled() && holdingShift) {
                    Point oldPoint = pointerPosition;
                    pointerPosition = e.getPoint().getLocation();
                    g.pointX = pointerPosition.x;
                    g.pointY = pointerPosition.y;
                    Point rescaledPoint = new Point((int) ((pointerPosition.x - g.centerX) / g.widthModifier), (int) ((pointerPosition.y - g.centerY) / g.heightModifier));
                    Point oldRescaledPoint = new Point((int) ((oldPoint.x - g.centerX) / g.widthModifier), (int) ((oldPoint.y - g.centerY) / g.heightModifier));

                    if(GamePanel.mirror) {
                        rescaledPoint = new Point(1080 - rescaledPoint.x, rescaledPoint.y);
                        oldRescaledPoint = new Point(1080 - oldRescaledPoint.x, oldRescaledPoint.y);
                    }

                    Graphics2D graphics2D = (Graphics2D) g.getNight().soggyPenCanvas.getGraphics();
                    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int green = (int) ((Math.sin(g.fixedUpdatesAnim / 200F) / 2 + 0.5) * 255);
                    graphics2D.setColor(isRightClick ? Color.WHITE : new Color(0, green, 255));

                    graphics2D.setStroke(new BasicStroke(isRightClick ? 5 : 3));

                    graphics2D.drawLine(oldRescaledPoint.x - 2 - g.offsetX + g.getNight().env().maxOffset(), oldRescaledPoint.y - 2, rescaledPoint.x - 2 - g.offsetX + g.getNight().env().maxOffset(), rescaledPoint.y - 2);

                    graphics2D.dispose();
                }


                if(g.getNight().getAstartaBoss() != null) {
                    Mister mister = g.getNight().getAstartaBoss().getMister();
                    if (mister.isActive()) {
                        if (mister.isBeingHeld()) {

                            Point newPoint = e.getPoint().getLocation();

                            if (robotMovement) {
                                pointerPosition = newPoint;
                                robotMovement = false;
                                return;
                            }

                            int dx = newPoint.x - pointerPosition.x;
                            if (GamePanel.isMirror())
                                dx = -dx;
                            int dy = newPoint.y - pointerPosition.y;
                            mister.translate(dx, dy);

                            if (g.getNight().getAstartaBoss().isBatterySaveMode()) {
                                dx *= 2;
                                dy *= 2;
                            }
                            mister.addVelocity(dx / 2F, dy / 2F);

                            pointerPosition = newPoint;
                            g.pointX = (int) (((GamePanel.isMirror() ? -mister.getPoint().x : mister.getPoint().x) - g.offsetX + 1080 + 250) * g.widthModifier + g.centerX);
                            g.pointY = (int) ((mister.getPoint().y + 100) * g.heightModifier + g.centerY);

                            if (pointerPosition.x > 100 * g.widthModifier + g.centerX && pointerPosition.x < 1000 * g.widthModifier + g.centerX && pointerPosition.y < 600 * g.heightModifier + g.centerY && pointerPosition.y > 120 * g.heightModifier + g.centerY)
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
            case FIELD -> {
                pointerPosition = e.getPoint().getLocation();
                setPoint();
            }
            case ACHIEVEMENTS -> {
                if(g.holdingAchievementSlider) {
                    pointerPosition = e.getPoint().getLocation();
                    setPoint();
                    
                    Point rescaledPoint = new Point((int) ((pointerPosition.x - g.centerX) / g.widthModifier), (int) ((pointerPosition.y - g.centerY) / g.heightModifier));
                    float percentage = Math.min(1, Math.max(0, rescaledPoint.y - 110) / 530F);

                    int length = Math.max(4, Achievements.values().length);
                    int categories = 4;
                    int max = 160 + 30 + length * 155 + categories * 80 - 530;
                    
                    g.achievementsScrollY = (int) (max * percentage);
                    
                    g.redrawAchievements();
                }
            }
        }
    }

    boolean robotMovement = false;

    Rat b;

    @Override
    public void mouseMoved(MouseEvent e) {
        if(g.state == GameState.CORNFIELD) {
            Player player = g.cornField3D.player;
            Point newPointer = e.getPoint();

            if(robotMovement) {
                pointerPosition = newPointer;
                robotMovement = false;
                return;
            }

            player.yaw += (float) (newPointer.x - pointerPosition.x) / 2;
            if(player.yaw > 360) player.yaw -= 360;
            if(player.yaw < 0) player.yaw += 360;

            player.pitch += (float) (pointerPosition.y - newPointer.y) / 4;
            if(player.pitch > 90) player.pitch = 90;
            if(player.pitch < -90) player.pitch = -90;
            pointerPosition = newPointer;

            if(pointerPosition.x > 100 && pointerPosition.x < 1000 && pointerPosition.y < 600 && pointerPosition.y > 120)
                return;

            try {
                robotMovement = true;
                Robot robot = new Robot();
                robot.mouseMove(g.center.x, g.center.y);
            } catch (AWTException ex) {
                throw new RuntimeException(ex);
            }
            
        } else if(g.state == GameState.GAME) {
            if(g.getNight().getEvent() == GameEvent.MR_MAZE) {
                MrMaze mrMaze = g.getNight().getMrMaze();
                Point newPointer = e.getPoint();

                if (robotMovement) {
                    pointerPosition = newPointer;
                    robotMovement = false;
                    return;
                }

                mrMaze.moveX += (newPointer.x - pointerPosition.x) / 2d;
                mrMaze.moveY += (newPointer.y - pointerPosition.y) / 2d;
                pointerPosition = newPointer;

                if (pointerPosition.x > 200 && pointerPosition.x < 900 && pointerPosition.y < 500 && pointerPosition.y > 220)
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
                if(previous == GameState.FIELD) {
                    g.pauseDieSelected = new Rectangle(100, 500, 220, 80).contains(rescaledPoint);
                }
            }
            case MENU -> {
                if(GamePanel.isAprilFools) {
                    g.hoveringPlatButton = new Rectangle(650, 50, 400, 45).contains(rescaledPoint);
                }
                if(new Rectangle(950, 510, 100, 100).contains(rescaledPoint)) {
                    g.discord = g.discordStates[1];
                    
                } else if(new Rectangle(950, 400, 100, 100).contains(rescaledPoint)) {
                    g.musicMenu = g.musicMenuStates[1];
                    
                } else {
                    g.discord = g.discordStates[0];
                    g.musicMenu = g.musicMenuStates[0];
                    
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
                
                Polygon pepVotePolygon = GamePanel.rectangleToPolygon(new Rectangle(770, 188, 260, 165));
                pepVotePolygon = GamePanel.rotatePolygon(pepVotePolygon, 900, 270, (Math.sin(g.fixedUpdatesAnim / 60F) * 0.2f));
                g.hoveringPepVoteButton = pepVotePolygon.contains(rescaledPoint);
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
                BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2D = (Graphics2D) image.getGraphics();
                graphics2D.setFont(g.yuGothicPlain60);
                
                hoveringNightReset = new Rectangle(146, 500 + g.settingsScrollY, 540, 80).contains(rescaledPoint);
                if(!hoveringNightReset) {
                    confirmNightReset = false;
                }
                hoveringFpsCap = new Rectangle(280 + g.textLength(graphics2D, GamePanel.getString("fpsCap")), 1025 + g.settingsScrollY, 540, 80).contains(rescaledPoint);
                hoveringJumpscareShake = new Rectangle(230, 1200 + g.settingsScrollY, 6540, 80).contains(rescaledPoint);
                hoveringLanguage = new Rectangle(280 + g.textLength(graphics2D, GamePanel.getString("languageSelect")), 1290 + g.settingsScrollY, 540, 80).contains(rescaledPoint);
                
                graphics2D.dispose();
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

                Enviornment env = g.getNight().env;
                int maxOffset = env.maxOffset();
                int offset = g.offsetX - env.maxOffset();

//                g.recalculateButtons(GameState.GAME);
                // ^ this is probably HORRIBLE for performance and
                // i have to fix this sometime later,but my game is releasing in 19 days
                // and it's 22:47 on a sunday night, so i am NOT doing this right now
                // update 5 months later: this function sucks lmao im removing this shit

                if (GamePanel.mirror) {
                    rescaledPoint = new Point(1080 - rescaledPoint.x, rescaledPoint.y);
                }

                g.hoveringAnyDoorButton = false;
                
                for(Door door : g.getNight().getDoors().values()) {
                    Polygon hitbox = new Polygon(door.getHitbox().xpoints, door.getHitbox().ypoints, door.getHitbox().npoints);
                    hitbox.translate(g.offsetX - maxOffset, 0);
                    door.setHovering(hitbox.contains(rescaledPoint));
                    
                    g.hoveringAnyDoorButton = g.hoveringAnyDoorButton || door.getButtonHitbox(g.offsetX, maxOffset).contains(rescaledPoint);
                }
                
                if(g.getNight().getType() == GameType.SHADOW) {
                    if(g.getNight().getAstartaBoss() != null) {
                        for (AstartaBlackHole hole : g.getNight().getAstartaBoss().getBlackHoles()) {
                            if (new Point(g.offsetX - maxOffset + hole.getX(), hole.getY()).distance(rescaledPoint) < 160) {
                                hole.expand();

                                for (AstartaBlackHole hole2 : g.getNight().getAstartaBoss().getBlackHoles()) {
                                    hole2.shrink();
                                }
                                break;
                            }
                        }
                    }
                }

                if(g.getNight().getDsc().isFight()) {
                    final Point lastRescaledPoint = rescaledPoint;
                    new Pepitimer(() -> g.getNight().getDsc().recalculateEndVector(lastRescaledPoint), 120);
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
                    g.hoveringGenerator = new Rectangle(offset + env.generator.x, env.generator.y, env.generator.width, env.generator.height).contains(rescaledPoint);
                }

                if(g.getNight().getEvent() == GameEvent.BASEMENT_KEY) {
                    BasementKeyOffice office = ((BasementKeyOffice) g.getNight().env());

                    office.setHoveringEvilDoor(new Rectangle(327 - maxOffset + g.offsetX, 301, 216, 316).contains(rescaledPoint) ||
                            new Rectangle(3360 - maxOffset + g.offsetX, 301, 216, 316).contains(rescaledPoint));
                }
                
                if(g.getNight().getType() == GameType.DAY) {
                    hoveringPepitoClock = g.pepitoClockProgress >= 160 && new Rectangle(offset + 465, 330, 105, 140).contains(rescaledPoint);
                    hoveringNeonSog = g.neonSogX == 0 &&  new Rectangle(offset + g.neonSogX, 0, 400, 640).contains(rescaledPoint);
                    hoveringNeonSogSign = new Rectangle(offset + 20, 0, 150, 260).contains(rescaledPoint);
                }
                if(g.getNight().getType().isBasement()) {
                    Basement basement = ((Basement) g.getNight().env());
                    if(basement.getStage() >= 4) {
                        Rectangle rect = new Rectangle(offset + 628, 236, 225, (int) (basement.getMonitorHeight()));
                        
                        g.hoveringBasementMonitor = rect.contains(rescaledPoint);
                    }
                    if(basement.getStage() == 7) {
                        g.basementLadderHovering = new Rectangle(offset + 610, 137, 217, 452).contains(rescaledPoint);
                    }
                }
                if(g.getNight().getShadowblocker().state > 0) {
                    hoveringShadowblockerButton = new Rectangle(18, 18, 120, 120).contains(rescaledPoint);

                    if(g.getNight().getShadowblocker().state == 2) {
                        if (rescaledPoint.x >= 230 && rescaledPoint.x <= 850 && rescaledPoint.y >= 130 && rescaledPoint.y <= 511) {
                            int enemiesSize = CustomNight.getEnemies().size();
                            for (int i = 0; i < 24; i++) {
                                int x = i % 6 * 105 + 230;
                                int y = i / 6 * 130 + 130;
                                
                                if (new Rectangle(x, y, 95, 120).contains(rescaledPoint)) {
                                    if (i >= enemiesSize) {
                                        g.getNight().getShadowblocker().selected = -1;
                                    } else {
                                        g.getNight().getShadowblocker().selected = (byte) i;
                                    }
                                    break;
                                }
                            }
                        } else {
                            g.getNight().getShadowblocker().selected = -1;
                        }
                    }
                }
                
                if(g.getNight().getType() == GameType.HYDROPHOBIA) {
                    HChamber ch = (HChamber) g.getNight().env();

                    boolean oldHoveringCompass = ch.isHoveringCompass();

                    ch.setHoveringExit(new Rectangle(offset + ch.exit.x, ch.exit.y, ch.exit.width, ch.exit.height).contains(rescaledPoint));
                    ch.setHoveringCompass(new Rectangle(offset + ch.compass.x, ch.compass.y, ch.compass.width, ch.compass.height).contains(rescaledPoint));
                    ch.setHoveringLocker(ch.hasLocker() && !g.inLocker && new Rectangle(offset + ch.locker.x, ch.locker.y, ch.locker.width, ch.locker.height).contains(rescaledPoint));

                    if (!oldHoveringCompass) {
                        ch.setShowCompassHint(ch.isHoveringCompass());
                    }

                    ch.setHoveringConditioner(!g.inLocker && new Rectangle(offset + ch.conditioner.x, ch.conditioner.y, ch.conditioner.width, ch.conditioner.height).contains(rescaledPoint));
                    ch.setHoveringPen(ch.isRewardRoom() && ch.penExists() && new Rectangle(offset + 360, 334, 98, 122).contains(rescaledPoint));

                    if (ch.isInPrefield() && ch.getPrefieldCount() == 1 && !ch.isInDustons()) {
                        ch.setHoveringReinforced(new Rectangle(offset + 979, 323, 196, 297).contains(rescaledPoint));
                    }
                    if (ch.key.width > 1) {
                        ch.setHoveringKey(new Rectangle(offset + ch.key.x, ch.key.y, ch.key.width, ch.key.height).contains(rescaledPoint));
                    }
                    if(ch.cup.width > 1) {
                        ch.setHoveringCup(new Rectangle(offset + ch.cup.x, ch.cup.y, ch.cup.width, ch.cup.height).contains(rescaledPoint));
                    }


                    if (g.getNight().getEvent() == GameEvent.DYING) {
                        if (ch.showDeathOptions) {
                            ch.selectedDeathOption = (byte) ((rescaledPoint.y > 320) ? 1 : 0);
                        }
                    }
                }
            }
            case FIELD -> {
                Field field = g.field;

                if (!field.isInCar() && !field.lockedIn) {
                    field.setHoveringCar(new Rectangle(field.getYaw() + 520, field.getPitch() + 215, 263, 237).contains(rescaledPoint));
                }
                if(field.isInCar()) {
                    field.setHoveringLever(new Rectangle(field.getYaw() - 540 + 1030, field.getPitch() - 320 + 300, 275, 150).contains(rescaledPoint));
                }
            }
            case CHALLENGE -> {
                Rectangle o = CustomNight.enemiesRectangle;
                Rectangle enemiesRect = new Rectangle(o.x, o.y, o.width, Math.max(440, o.height));

                if (enemiesRect.contains(rescaledPoint)) {
                    for (int i = 0; i < CustomNight.getEnemies().size(); i++) {
                        int x = i % 6 * 105 + 20;
                        int y = i / 6 * 130 + 40;

                        if (new Rectangle(x, y, 95, 120).contains(rescaledPoint)) {
                            CustomNight.selectedElement = CustomNight.getEnemies().get(i);
                            CustomNight.setLoadedPreviewPath(CustomNight.getEnemies().get(i).getPreview());
                            break;
                        }
                    }
                    isInEnemiesRectangle = true;
                    return;
                } else {
                    isInEnemiesRectangle = false;
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
            case MUSIC_MENU -> {
                int i = 0;
                for(String name : g.musicDiscs) {
                    if(new Rectangle(16 + i * 190, 266, 183, 183).contains(rescaledPoint)) {
                        g.hoveringMusicDisc = name;
                        return;
                    }
                    i++;
                }
                if(!new Rectangle(0, 253, 1080, 387).contains(rescaledPoint)) {
                    g.hoveringMusicDisc = "";
                }
            }
            case ACHIEVEMENTS -> {
                if(!g.achievementState && !g.shiftingAchievements) {
                    Rectangle rect = new Rectangle(25, 130 - g.achievementsScrollY, 900, 155);
                    boolean old = g.hoveringInvestigation;
                    g.hoveringInvestigation = rect.contains(rescaledPoint) && Achievements.ALL_NIGHTER.isObtained();
                    
                    if(old != g.hoveringInvestigation) {
                        g.redrawAchievements();
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
                    
                    g.riftFramesDoingNothing = -15000;
                    g.riftMoonAlpha = 0;
                } else {
                    if (index >= g.riftItems.size() - 1) {
                        index = (byte) (0);
                    } else {
                        index++;
                    }
                    g.sound.play("select", 0.1);
                    
                    g.riftFramesDoingNothing = -15000;
                    g.riftMoonAlpha = 0;
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
                    g.achievementsScrollY = (short) (Math.max(0, g.achievementsScrollY + e.getWheelRotation() * 40));

                    int length = Math.max(4, Achievements.values().length);
                    int categories = 4;

                    while (160 + 30 + length * 155 + categories * 80 - g.achievementsScrollY < 530) {
                        g.achievementsScrollY -= (short) (e.getWheelRotation());
                    }
                    g.redrawAchievements();
                } else {
                    g.statisticsScrollY = (short) (Math.max(0, g.statisticsScrollY + e.getWheelRotation() * 40));

                    while (160 + 150 + Statistics.values().length * 40 - g.statisticsScrollY < 650) {
                        g.statisticsScrollY -= (short) (e.getWheelRotation());
                    }
                }
            }

            case SETTINGS -> {
                g.settingsScrollY = Math.max(-1060, Math.min(0, g.settingsScrollY - e.getWheelRotation() * 30));
            }
        }
    }

    
    private void crateClick() {
        if(!g.everyFixedUpdate.containsKey("crateAnimation")) {
            if(g.crateY > 640) {

                g.backToMainMenu();
            } else {
                if(g.crateRewards.size() <= 1) {
                    g.crateItemDistance = 0;
                }

                g.everyFixedUpdate.put("crateAnimation", () -> {
                    if (g.crateY >= 1000) {
                        if(g.crateY != 1000) {
                            g.crateShake = 15;
                            g.sound.play("blockadeBreak", 0.1);
               
                            new Pepitimer(() -> {
                                g.sound.playRate("icePotionUse", 0.2, 0.75);
                                g.sound.play("boop", 0.1);
                            }, 700);
                        }
                        g.crateY = 1000;
                        g.crateItemDistance -= 0.008F;
                        g.crateItemDistance *= 0.99F;

                        if (g.crateItemDistance < 0) {
                            g.crateItemDistance = 0;
                            g.everyFixedUpdate.remove("crateAnimation");
                        }
                    } else {
                        g.crateY++;
                        g.crateY *= 1.05F;
                    }
                });
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
            String selectedOption = g.menuButtons.get(g.selectedOption);

            if(selectedOption.equals(">> " + GamePanel.getString("settings"))) {
                g.startSettings();
            } else if(selectedOption.equals(">> " + GamePanel.getString("bingo"))) {
                g.startBingo();
            } else if(selectedOption.equals(">> " + GamePanel.getString("achievementsSmall"))) {
                g.startAchievements();
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
            g.music.play("tension", 0.05, true);
            if(CustomNight.limboTimer[0] != null) {
                CustomNight.limboTimer[0].cancel(false);
                CustomNight.limboTimer[0] = null;
                CustomNight.resetEntityPositions();
            }
            CustomNight.limboId = -1;
        }
    }

    private void deathScreenYThing() {
        if(g.everyFixedUpdate.containsKey("deathScreenY")) {
            g.deathScreenY = 640;
            g.everyFixedUpdate.remove("deathScreenY");
            g.music.play("orca", 0.055, true);

            if (g.killedBy.contains(GamePanel.getString("kbRadiation")) && !g.afterDeathText.contains(GamePanel.getString("afterTheFirstNuclearStrike").substring(0, 30))) {
                new Pepitimer(() -> {
                    if(g.afterDeathText.contains(GamePanel.getString("afterTheFirstNuclearStrike").substring(0, 30)))
                        return;

                    g.afterDeathText = GamePanel.getString("afterTheFirstNuclearStrike");
                    g.afterDeathCurText = GamePanel.getString("afterTheFirstNuclearStrike").substring(0, 20);
                }, 4500);
            }
        } else {
            g.everyFixedUpdate.put("deathScreenY", () -> {
                if (g.deathScreenY < 640) {
                    g.deathScreenY += 8 + (g.deathScreenY) / 19 - (g.deathScreenY * g.deathScreenY) / 10240;
                } else {
                    g.deathScreenY = 640;
                    g.everyFixedUpdate.remove("deathScreenY");
                    g.music.play("orca", 0.055, true);

                    if (g.killedBy.contains(GamePanel.getString("kbRadiation")) && !g.afterDeathText.contains(GamePanel.getString("afterTheFirstNuclearStrike").substring(0, 30))) {
                        new Pepitimer(() -> {
                            if(g.afterDeathText.contains(GamePanel.getString("afterTheFirstNuclearStrike").substring(0, 30)))
                                return;

                            g.afterDeathText = GamePanel.getString("afterTheFirstNuclearStrike");
                            g.afterDeathCurText = GamePanel.getString("afterTheFirstNuclearStrike").substring(0, 20);
                        }, 4500);
                    }
                }
            });
        }
    }

    
    public void clickItem(Item item) {
        if (item.getAmount() != 0) {
            if (item.isSelected()) {
                item.deselect();
                g.itemLimit -= item.getItemLimitAdd();

                int selectedItems = 0;
                for(Item allItems : g.itemList) {
                    if(allItems.isSelected()) {
                        selectedItems++;
                    }
                }
                if(selectedItems > g.itemLimit) {
                    g.sound.play("select", 0.1, false);
                    g.sound.play("selectFail", 0.12, false);
                    
                    int index = 0;
                    while (selectedItems > g.itemLimit && g.itemList.get(index) != null) {
                        g.itemList.get(index).deselect();
                        selectedItems--;
                        index++;
                    }
                }
            } else {
                if ((g.checkItemsAmount() < g.itemLimit || item.getItemLimitAdd() > 0) && !item.isMarkedConflicting()) {
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
    }
    
    
    private void pressAnyKeyStuff() {
        if(g.state == GameState.ENDLESS_DISCLAIMER) {
            g.state = GameState.PLAY;
            g.sound.play("select", 0.1);
            g.fadeOut(255, 0, 3);
            g.seenEndlessDisclaimer = true;
            g.pressAnyKey = false;
            return;
        }
        
        if(g.riftItems.size() >= 2 && g.endless.getNight() >= 3) {
            if(g.riftTransparency <= 0) {
                g.enterRift();
            }
        } else {
            if(processDeathScreenPress())
                return;

            g.stopGame(true);
        }
    }
    
    public boolean doHydrophobiaRespawn() {
        Level nightBefore = g.getNight();
        HChamber old = ((HChamber) g.getNight().env);
        Level oldNight = old.getOldNight();
        int deaths = old.getDeaths();
        
        g.stopGame(false);
        g.type = GameType.HYDROPHOBIA;
        g.startGame();

        
        HChamber chamber = (HChamber) g.getNight().env;
        chamber.setOldNight(oldNight);
        chamber.setDeaths(deaths + 1);
        
        chamber.setHasCup(old.hasCup());
        
        switch (old.checkpoint) {
            case 1 -> {
                chamber.setPendingPrefieldRoom(true);
                chamber.setInPrefield(true);
                chamber.setPrefieldCount(5);
                g.getNight().seconds = 211;
                
                chamber.regenerateFurniture();      
                g.repaintOffice();
                chamber.setRespawnCheckpoint(true);
                chamber.setRooms(6);

                g.getNight().getHydrophobia().setAILevel(0);
                g.getNight().getBeast().setAILevel(0);
                g.getNight().getOverseer().setAILevel(0);

                chamber.roomsTillKey = 1000;
            }
            case 2 -> {
                chamber.resetField();
                chamber.setRooms(6);
                
                g.getNight().seconds = 211;
                chamber.regenerateSeed(false);
                chamber.regenerateFurniture();
                g.repaintOffice();
                chamber.setRespawnCheckpoint(true);

                chamber.roomsTillKey = 1000;
            }
        }
        return true;
    }
    
    
    public void enterNewHydrophobiaRoom(HChamber chamber) {
        boolean requiresLocker = (g.getNight().getBeast().isActive() && !chamber.isPendingRewardRoom() && !chamber.isPendingPrefield());

        int iterations = 0;
        while (iterations == 0 || (chamber.roomsTillKey <= 0 && chamber.table.x >= 1480 && !chamber.isPendingRewardRoom() && !chamber.isPendingPrefield())) {
            int oldSeed = chamber.getSeed();
            while (chamber.getSeed() == oldSeed) {
                chamber.regenerateSeed(requiresLocker);
            }
            chamber.regenerateFurniture();

            iterations++;
        }
//                                        System.out.println("IS BEAST ACTIVE? " + g.getNight().getBeast().isActive());
//                                        System.out.println("DO WE NEED A LOCKER? " + requiresLocker);
//                                        System.out.println("DO WE GOT A LOCKER? " + chamber.hasLocker());


        g.sound.stop();

        chamber.setRooms(chamber.getRoom() + 1);

        if (chamber.hasConditioner()) {
            g.sound.play("conditionerSounds", (chamber.isRewardRoom() || chamber.isInPrefield()) ? 0.08 : 0.15, true);
        }

        if(g.sunglassesOn) {
            g.sgPolygons.clear();
            g.sgPolygons.add(GamePanel.rectangleToPolygon(chamber.exit));
        }

        chamber.resetResettable();

        chamber.setShake(0);

        g.fadeOut(255, 180, 0.4F);

        g.getNight().getHydrophobia().move();
        g.getNight().getOverseer().disappear();

        if (chamber.isPendingRewardRoom() || chamber.isPendingPrefield()) {
            g.getNight().getHydrophobia().setAILevel(0);
            g.getNight().getBeast().setAILevel(0);
            g.getNight().getOverseer().setAILevel(0);

            g.getNight().getBeast().fullReset();
            g.getNight().getOverseer().disappear();

            g.fadeOut(255, chamber.isPendingRewardRoom() ? 0 : 70, 0.4F);
        }

        g.repaintOffice();

        chamber.setHoveringExit(false);

        g.sound.play("enterNewRoom", 0.16);
    }
}