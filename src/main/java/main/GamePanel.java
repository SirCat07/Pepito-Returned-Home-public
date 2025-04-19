package main;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.ThumbnailDetails;
import cutscenes.Cutscene;
import cutscenes.Presets;
import enemies.*;
import game.*;
import game.achievements.AchievementHandler;
import game.achievements.Achievements;
import game.bingo.BingoCard;
import game.bingo.BingoHandler;
import game.bingo.BingoTask;
import game.cornfield.CornField3D;
import game.custom.CustomNight;
import game.custom.CustomNightEnemy;
import game.custom.CustomNightModifier;
import game.dryCat.DryCat;
import game.dryCat.DryCatGame;
import game.enviornments.Basement;
import game.enviornments.BasementKeyOffice;
import game.enviornments.Enviornment;
import game.enviornments.HChamber;
import game.field.*;
import game.fruitRainEvent.FreStats;
import game.investigation.Investigation;
import game.investigation.InvestigationPaper;
import game.items.Item;
import game.items.ItemTag;
import game.particles.*;
import game.playmenu.PlayMenu;
import game.playmenu.PlayMenuElement;
import game.shadownight.*;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.BoundingBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import org.jetbrains.annotations.NotNull;
import utils.*;
import utils.composites.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GamePanel extends JPanel implements Runnable {

    public short fpsCap = -1; // -1 - unlocked
    public int thousandFPS = Math.max(1, 1000 / fpsCap); // update this when changing fps, minimal is 1, max is 1000 / FPS
    private int actualFPS = 60;

    public boolean inCam = false;

    float volume = 0.8F;
    boolean blackBorders = false; // fixed ratio
    boolean headphones = false;
    boolean[] fpsCounters = new boolean[] {false, false, false}; // fps, ups, fups
    public byte jumpscareShake = 1; // 0 - window + image shake, 1 - image shake, 2 - no shake
    public boolean screenShake = true;
    public static boolean disableFlickering = false;
    int staticSpeed = 1; // the more - the slower; default - 1; <=0 does nothing
    public boolean showManual = true; // shows manual, ignores geiger counter
    boolean saveScreenshots = true; // save screenshots in a folder
    boolean disclaimer = true; // disclaimer at the start of the game
    boolean saveItems = true; // save items
    public boolean receivedShadowblocker = false;
    
    public boolean beatShadownightBasement = false;
    
    public boolean seenEndlessDisclaimer = false;

    int settingsScrollY = 0;

    public SoundMP3 music = new SoundMP3(this, "music");
    public SoundMP3 sound = new SoundMP3(this);
    public SoundMP3 scaryCatSound = new SoundMP3(this, "scaryCat");
    public SoundMP3 generatorSound = new SoundMP3(this, "generator");
    public SoundMP3 rainSound = new SoundMP3(this, "rain");
    public SoundMP3 bingoSound = new SoundMP3(this, "bingo");
    public SoundMP3 basementSound = new SoundMP3(this, "basement");
    public SoundMP3 shockSound = new SoundMP3(this, "shock");
    public SoundMP3 krunlicSound = new SoundMP3(this, "krunlic"); // this one doesnt give a shit
    
    public String menuSong = "pepito";
    List<Point> visualizerPoints = new ArrayList<>();
    float musicMenuDiscX = 140;
    Set<String> musicDiscs = new LinkedHashSet<>();
    String hoveringMusicDisc = "";
    HashMap<String, PepitoImage> discMap = new HashMap<>();


    short quickVolumeSeconds = 0;
    short quickVolumeY = -120;

    public static NoiseGenerator noise = new NoiseGenerator();

    Path gameDirectory;

    boolean debugMode = false;

    public KeyHandler keyHandler = new KeyHandler(this);

    final FPSCounter fpscnt;
    private final FPSCounter upscnt;
    private final FPSCounter fupscnt;

    boolean isFocused = true;

    public GameState state = GameState.UNLOADED;

    String version = "2.1.3";
    short versionTextLength = 0;
    
    int currentLeftPan = 0;
    int currentRightPan = 0;

    public Item soda;
    public Item flashlight;
    public Item fan;
    public Item metalPipe;
    public Item sensor;
    Item maxwell;
    public Item adblocker;
    public Item shadowblocker;
    public Item megaSoda;
    Item freezePotion;
    Item planks;
    public Item miniSoda;
    public Item soup;
    Item birthdayHat;
    Item birthdayMaxwell;
    Item bingoCardItem;
    public Item soggyBallpit;
    public Item manual;
    Corn[] corn = new Corn[2];
    Item sunglasses;
    Item riftGlitch;
    Item weatherStation;
    Item soggyPen;
    public boolean sunglassesOn = false;
    private float sgAlpha = 0;
    private float sgGamma = 0;
    short sgRadius = 0;
    List<Polygon> sgPolygons = new ArrayList<>();

    short radiationCursor = 50;
    
    Item speedrunTimer;
    int timerY = -240;
    Item starlightBottle;
    int starlightMillis = 0;
    Item shadowTicket;
    byte shadowCheckpointSelected = 0; // 0 - nothing; 1 - halfway;f2 - astarta
    public byte shadowCheckpointUsed = 0; // 0 - nothing; 1 - halfway; 2 - astarta
    public Item styroPipe;
    public Item basementKey;
    public Item hisPicture;
    public Item hisPainting;
    public Item pishPish;
    public Item iceBucket;
    public Item red40;

    public boolean inLocker = false;

    public static float freezeModifier = 1;
    public static float originalGameSpeedModifier = 1;
    public static float universalGameSpeedModifier = 1;
    public boolean invincible = false;

//    private final JFXThread jfxAudioPlayer;

    public boolean unlockedBingo = false;

    public boolean adBlocked = false;
    public String randomCharacter = "";
    byte adblockerStatus = 0;
    byte adblockerTimer = 0;
    Point adblockerPoint = new Point(20, 20);
    Rectangle adblockerButton = new Rectangle(20, 20, 100, 100);

    Rat a;

    public byte metalPipeCooldown = 5;
    public byte flashLightCooldown = 5;

    List<Item> fullItemList = new ArrayList<>();
    List<Item> itemList = new ArrayList<>();
    List<Item> usedItems = new ArrayList<>();
    HashMap<Item, Boolean> isItemUsed = new HashMap<>();
    int holdingEFrames = 0;
    
    
    HashMap<Item, Integer> crateRewards = new HashMap<>();
    float crateY = 0;
    float crateItemDistance = 0.95F;
    float crateShake = 0;

    CornField3D cornField3D;
    
    public Field field;
    

    
    boolean startButtonSelected = false;


    short width = 1080;
    short height = 640;

    protected Main audioPlayer;

    
    JFrame window;
    
    public GamePanel(Main jfxAudioPlayer, JFrame window) {
        this.audioPlayer = jfxAudioPlayer;
        this.window = window;

        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);

        this.setFocusable(true);
        this.addKeyListener(keyHandler);
        this.addMouseListener(keyHandler);
        this.addMouseMotionListener(keyHandler);
        this.addMouseWheelListener(keyHandler);
        setFocusTraversalKeysEnabled(false);

        fpscnt = new FPSCounter();
        fpscnt.start();
        upscnt = new FPSCounter();
        upscnt.start();
        fupscnt = new FPSCounter();
        fupscnt.start();

        center = new Point(window.getX() + window.getWidth() / 2, window.getY() + window.getHeight() / 2);
    }
    Point center;

    Thread thread;

    BufferedImage[] bg = new BufferedImage[4];
    byte currentBG = 1;

    public SensorConsole console = new SensorConsole();

    
    Rectangle closeButton = new Rectangle(20, 20, 35, 35);

    Rectangle huh = new Rectangle(470, 250, 35, 35);

    Rectangle manualFirst = new Rectangle(825, 592, 90, 25);
    Rectangle manualSecond = new Rectangle(925, 592, 90, 25);


    @SuppressWarnings("SuspiciousNameCombination")
    public void onResizeEvent() {
        widthModifier = width / 1080.0F;
        heightModifier = height / 640.0F;
        overallModifier = (widthModifier + heightModifier) / 2;

        debugFont = new Font("Arial", Font.BOLD, (short) (20 * overallModifier));
        consoleFont = new Font("Arial", Font.BOLD, (short) (30 * overallModifier));

        if (blackBorders) {
            if(widthModifier > heightModifier) {
                widthModifier = heightModifier;
            } else {
                heightModifier = widthModifier;
            }
        }
        centerX = (short) ((width - 1080.0 * widthModifier) / 2);
        centerY = (short) ((height - 640.0 * heightModifier) / 2);

        center = new Point(window.getX() + window.getWidth() / 2, window.getY() + window.getHeight() / 2);

        recalculateButtons(state);
    }

    public void recalculateButtons(GameState state) {
        switch (state) {
            case SETTINGS, ITEMS, BINGO, ACHIEVEMENTS, PLAY, MUSIC_MENU -> closeButton = new Rectangle((int) (20 * widthModifier) + centerX, (int) (20 * heightModifier) + centerY, (int) (35 * widthModifier), (int) (35 * heightModifier));
            case MENU -> {
                huh = new Rectangle((int) (470 * widthModifier) + centerX, (int) (250 * heightModifier) + centerY, (int) (35 * widthModifier), (int) (35 * heightModifier));
                closeButton = new Rectangle((int) (20 * widthModifier) + centerX, (int) (20 * heightModifier) + centerY, (int) (35 * widthModifier), (int) (35 * heightModifier));
            }
            case GAME -> {
                if(mirror) {
                    adblockerButton = new Rectangle((short) ((1080 - adblockerPoint.x - 100) * widthModifier + centerX), (short) (adblockerPoint.y * heightModifier + centerY), (short) (100 * widthModifier), (short) (100 * heightModifier));
                } else {
                    adblockerButton = new Rectangle((short) (adblockerPoint.x * widthModifier + centerX), (short) (adblockerPoint.y * heightModifier + centerY), (short) (100 * widthModifier), (short) (100 * heightModifier));
                }
                recalcManualButtons();
            }
            case MILLY -> recalculateMillyRects();
        }
    }

    public void recalcManualButtons() {
        if(mirror) {
            manualFirst = new Rectangle((int) ((1080 - 825 - 90) * widthModifier + centerX), (int) ((manualY + 55) * heightModifier + centerY), (int) (90 * widthModifier), (int) (25 * heightModifier));
            manualSecond = new Rectangle((int) ((1080 - 925 - 90) * widthModifier + centerX), (int) ((manualY + 55) * heightModifier + centerY), (int) (90 * widthModifier), (int) (25 * heightModifier));
        } else {
            manualFirst = new Rectangle((int) (825 * widthModifier + centerX), (int) ((manualY + 55) * heightModifier + centerY), (int) (90 * widthModifier), (int) (25 * heightModifier));
            manualSecond = new Rectangle((int) (925 * widthModifier + centerX), (int) ((manualY + 55) * heightModifier + centerY), (int) (90 * widthModifier), (int) (25 * heightModifier));
        }
    }

    int everyMinuteCounter = 10;

    public HashMap<String, Runnable> every6s = new HashMap<>();
    public HashMap<String, Runnable> everySecond = new HashMap<>();
    public HashMap<String, Runnable> everySecond10th = new HashMap<>();
    public HashMap<String, Runnable> everySecond20th = new HashMap<>();
    public HashMap<String, Runnable> everyFixedUpdate = new HashMap<>();
    
    boolean shader3am = false;

    public void startThread() {
        thread = new Thread(this);
        thread.start();
        
        every6s.put("formatChange", () -> {
            everyMinuteCounter--;
            if(everyMinuteCounter <= 0) {
                // checking if its 3 AM to add the creepy filter / else change it back
                Calendar cal = Calendar.getInstance();
                shader3am = cal.get(Calendar.HOUR_OF_DAY) == 3;

                save();
                everyMinuteCounter = 10;
            }
        });
        
        startupTimers();

        Console.initialize(this);



//        ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
//
//        Thread threadB = new Thread(() -> {
//            serverExecutor.submit(Server::init);
//        });
//        threadB.start();
    }

    
    ScheduledExecutorService allTimers;
    
    public void startupTimers() {
        allTimers = Executors.newScheduledThreadPool(1);
        
        // every 6 seconds
        allTimers.scheduleAtFixedRate(() -> {
            if(state == GameState.UNLOADED)
                return;
            if(state == GameState.BATTERY_SAVER)
                return;

            for (Runnable r : every6s.values().stream().toList()) {
                try {
                    r.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (state == GameState.MENU) {
                scrollX = 256;
                scrollY = 256;

                fadeOut(228, 160, 1);

                randomX = (short) Math.round(Math.random() * 256 - 512);
                randomY = (short) Math.round(Math.random() * 256 - 512);

                vertical = (byte) Math.round(Math.random()) == 1;

//                new Pepitimer(() -> {
                int newRandomInt = (int) Math.round(Math.random() * 3);
                if (currentBG == newRandomInt) {
                    if (newRandomInt < 2) {
                        currentBG = (byte) (newRandomInt + 1);
                    }
                } else {
                    currentBG = (byte) newRandomInt;
                }
//                }, 100);
            } else if(state == GameState.GAME) {
                if(night.isRadiationModifier() && currentWaterLevel >= 640) {
                    for(GruggyCart cart : night.gruggyCarts) {
                        cart.setAddX((int) (Math.random() * 600 - 300));
                    }
                }
            } else if(state == GameState.MILLY) {
                if(night.getType().isBasement() && ((Basement) night.env).doWiresWork()) {
                    if(!doMillyFlicker) {
                        if (Math.random() < 0.5) {
                            doMillyFlicker = true;

                            sound.play("flicker" + (int) (Math.floor(Math.random()) + 1), 0.05);
                        }
                        new Pepitimer(() -> {
                            doMillyFlicker = false;
                        }, 200);
                    }
                }
            }

            countersAlive[0] = true;
            countersAlive2[0] = true;
        }, 0, (int) (6000 / universalGameSpeedModifier), TimeUnit.MILLISECONDS);

        // every second
        allTimers.scheduleAtFixedRate(() -> {
            this.actualFPS = (short) fpscnt.get();
            seconds++;

            Statistics.PLAYTIME.increment();

//            StringBuilder output = new StringBuilder("-----------------\n6s:");
//            for(String string : every6s.keySet()) {
//                output.append(" ").append(string);
//            }
//            output.append("\n1s: ");
//            for(String string : everySecond.keySet()) {
//                output.append(" ").append(string);
//            }
//            output.append("\n0.1s: ");
//            for(String string : everySecond10th.keySet()) {
//                output.append(" ").append(string);
//            }
//            output.append("\n0.05s: ");
//            for(String string : everySecond20th.keySet()) {
//                output.append(" ").append(string);
//            }
//
//            System.out.println(output);


            if(state == GameState.UNLOADED)
                return;
            if(state == GameState.BATTERY_SAVER)
                return;

            if(state == GameState.MILLY) {
                if(secondsInMillyShop < 3600) {
                    secondsInMillyShop++;
                } else {
                    if(!(endFade == 160 || endFade == 255)) {
                        music.stop();
                        fadeOut((int) (tintAlpha), 160, 1);
                        millyShopItems[(int) (Math.random() * 5)] = new MillyItem(starlightBottle, 100, starlightBottleImg);

                        sound.playRate("dreadHideaway", 0.3, 1.5);
                        sound.play("dreadHideaway", 0.3);
                        sound.playRate("dreadHideaway", 0.1, 0.8);
                        sound.playRate("dreadHideaway", 0.1, 0.5);

                        redrawMillyShop();
                        recalculateMillyRects();
                        new Pepitimer(() -> {
                            if(state == GameState.MILLY && secondsInMillyShop >= 3600) {
                                fadeOut(160, 255, 20);

                                new Pepitimer(() -> {
                                    announcerOn = false;
                                    state = GameState.GAME;
                                    jumpscare("dread", night.getId());
                                }, 200);
                            }
                        }, 7800);
                    }
                }
            } else if(state == GameState.GAME) {
                if(night.isTimerModifier()) {
                    if(!night.getTimers().isEmpty()) {
                        sound.play("timerLoop", 0.1);
                    }
                }
                if(night.isRadiationModifier()) {
                    Point rescaledPoint = new Point((int) ((keyHandler.pointerPosition.x - centerX) / widthModifier), (int) ((keyHandler.pointerPosition.y - centerY) / heightModifier));
                    
                    if(radiationCursor >= 50) {
                        if(!(manualFirstButtonHover || manualSecondButtonHover || night.gruggyX < 1000)) {
                            for (GruggyCart cart : night.gruggyCarts) {
                                Point point = new Point((int) (offsetX - 400 + cart.getCurrentX() + 197), 445 - waterLevel());

                                if (point.distance(rescaledPoint) < 290) {
                                    radiationCursor = 1;
                                    break;
                                }
                            }
                        }
                    }
                }
                if(!hallucinations.isEmpty()) {
                    if(Math.random() < 0.2) {
                        hallucinations.remove(0);
                    }
                }
                if(night.getTemperature() > 60) {
                    if(Math.random() < (night.getTemperature() - 60) / 80) {
                        if(Math.random() < 0.2) {
                            Level.playTempAmbient(sound);
                        }
                        if(Math.random() > 0.6 && hallucinations.size() < 2) {
                            hallucinations.add(new Hallucination());
                        }
                    }
                }
                
                if(sunglasses.isEnabled()) {
                    List<Integer> arrivals = new ArrayList<>();
                    for(Enemy enemy : night.getEnemies()) {
                        if(enemy.getAILevel() > 0) {
                            if(enemy.getArrival() >= 0) {
                                arrivals.add(enemy.getArrival());
                            }
                        }
                    }
                    if(night.getPepito().isEnabled()) {
                        if(night.getPepito().seconds > 0) {
                            arrivals.add((int) night.getPepito().seconds);
                        }
                    }
                    float avg = (float) arrivals.stream().mapToDouble(d -> d).average().orElse(0.0);

                    int min1 = 0;
                    if(!arrivals.isEmpty()) {
                        min1 = Collections.min(arrivals);
                    }
                    int min2 = 0;
                    if(arrivals.size() > 1) {
                        arrivals.remove((Object) min1);
                        min2 = Collections.min(arrivals);
                    }
                    
                    sgAlpha = (min1 + min2) / 2F;
                    sgGamma = avg;
                }
            } else if(state == GameState.FIELD) {
                if(Math.random() < 0.07 && !field.lockedIn) {
                    field.cancelAfter.add(new Pepitimer(() -> {
                        field.lightningStrike(this);
                    }, (int) (Math.random() * 800)));
                }
                
                if(field.isInCar()) {
                    if(field.getBlimp().underTheRadar && !field.getBlimp().lockedOn) {
                        field.getBlimp().untilNextAttack--;
                        
                        if(field.getBlimp().untilNextAttack <= 0) {
                            field.getBlimp().lockOn();
                        }
                    }
                }
            }
            if(krunlicMode) {
                if(krunlicPhase >= 2) {
                    krunlicSeconds++;
                }
                if(krunlicPhase >= 3 && state != GameState.KRUNLIC && (seconds / 2.5d) % 2 == 0) {
                    sound.playRate("boop", 0.1, 0.5);

                    if(StaticLists.achievementNotifs.size() < 9) {
                        new AchievementNotification(getString("krunlicName"), getString("krunlicDesc"), krunlicAchievement.request());
                    }
                }
                if(krunlicPhase >= 4 && state != GameState.KRUNLIC) {
                    if(Math.random() < 0.02) {
                        krunlicEyes.add(new Point((int) (254 + Math.random() * 572), (int) (117 + Math.random() * 406)));
                    }
                }

                if(state == GameState.GAME) {
                    if(krunlicPhase == 5) {
                        balloons.add(new Balloon(0));
                    }
                }
            }
            
            if (bingoCard.isTimeGoing()) {
                bingoCard.secondsSpent++;
                if (bingoCard.secondsSpent >= 1440) {
                    bingoCard.fail();
                }
            }
            for(PepitoImage image : new LinkedHashSet<>(StaticLists.loadedPepitoImages)) {
                image.tick();
            }

            for(Runnable r : everySecond.values().stream().toList()) {
                try {
                    r.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            countersAlive[1] = true;
            countersAlive2[1] = true;
        }, 0, (int) (1000 / universalGameSpeedModifier), TimeUnit.MILLISECONDS);

        //every second 10th
        allTimers.scheduleAtFixedRate(() -> {
            if(state == GameState.UNLOADED)
                return;
            if(state == GameState.BATTERY_SAVER)
                return;

            for(Runnable r : everySecond10th.values().stream().toList()) {
                try {
                    r.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            switch (state) {
                case BINGO -> {
                    if (bingoCard.isGenerating()) {
                        redrawBingoCard();
                    }
                }
                case PLAY -> {
                    if (playSelectorWaitCounter <= 0) {
                        playSelectorWaitCounter = 2;

                        if (PlayMenu.movedMouse) {
                            for (int i = 0; i < PlayMenu.getList().size(); i++) {
                                if (Math.abs(i - PlayMenu.index) < 3) {
                                    int selectOffsetX = (int) (PlayMenu.selectOffsetX);
                                    int orderOffsetX = i * 420;

                                    Rectangle rect = new Rectangle((int) ((380 + orderOffsetX - selectOffsetX) * widthModifier) + centerX, centerY, (int) (320 * widthModifier), (int) (640 * heightModifier));
                                    if (rect.contains(keyHandler.pointerPosition)) {
                                        if (PlayMenu.index != i) {
                                            sound.playRate("playMenuChange", 0.05, 2);
                                        }
                                        PlayMenu.index = i;
                                    }
                                }
                            }
                        }
                    } else {
                        playSelectorWaitCounter--;
                    }
                }
                case GAME -> {
                    if (night.getJumpscareCat().getShake() > 0) {
                        night.getJumpscareCat().setShake(night.getJumpscareCat().getShake() - 1);
                    }
                    if(sunglassesOn) {
                        if(night.getType() != GameType.HYDROPHOBIA) {
                            if (sgPolygons.size() != night.doors.size()) {
                                sgPolygons.clear();
                                for (Door door : night.doors.values()) {
                                    Polygon hitbox = new Polygon(door.getHitbox().xpoints, door.getHitbox().ypoints, door.getHitbox().npoints);
                                    sgPolygons.add(hitbox);
                                }
                            }
                        }
                    }
                }
                case FIELD -> {
                    float abs = Math.abs(field.getSpeed());
                    if(abs > 0.2F) {
                        sound.playRate("fieldCarSounds", 0.06, Math.sqrt(Math.sqrt(abs)));
                    }
                }
                case DRY_CAT_GAME -> {
                    try {
                        if (dryCatGame.timer > -0.5) {
                            float percent = dryCatGame.timer / 43;
                            if (percent < Math.random()) {
                                dryCatGame.addCat();
                            }
                        }
                    } catch (Exception ignored) { }
                }
            }

            if (staticTransparency > 0F) {
                if(waitUntilStaticChange == 0) {
                    if (tvStatic != 8) {
                        tvStatic += 1;
                    } else {
                        tvStatic = 1;
                    }
                    if(staticTransparency == endStatic) {
                        try {
                            redrawCurrentStaticImg();
                        } catch (Exception ignored) {
                        }
                    }

                    if(staticSpeed != 1) {
                        waitUntilStaticChange += staticSpeed;
                    }
                } else {
                    waitUntilStaticChange--;
                }
            }

            countersAlive[2] = true;
            countersAlive2[2] = true;
        }, 0, (int) (100 / universalGameSpeedModifier), TimeUnit.MILLISECONDS);

        //every second 20th
        allTimers.scheduleAtFixedRate(() -> {
            if(state == GameState.UNLOADED)
                return;
            if(state == GameState.BATTERY_SAVER)
                return;

            for(Runnable r : everySecond20th.values().stream().toList()) {
                try {
                    r.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            redrawBHO = true;
            
            for (Balloon balloon : balloons.stream().toList()) {
                balloon.counter++;
                if (balloon.counter >= 360) {
                    balloon.counter = 0;
                }
                balloon.addX(balloon.direction.getX());

                if (balloon.getX() < 0) {
                    balloon.direction = BalloonDirection.RIGHT;
                } else if (balloon.getX() > 1390) {
                    balloon.direction = BalloonDirection.LEFT;
                }
                if (balloon.alpha < 1) {
                    balloon.alpha += 0.025F;

                    if (balloon.alpha > 1) {
                        balloon.alpha = 1;
                    }
                }
            }
            if(basementHyperOptimization && !balloons.isEmpty()) {
                int offset = offsetX - 400;
                for (Balloon balloon : balloons.stream().toList()) {
                    if(balloon.getX() > 570 && balloon.getX() < 770) {
                        balloon.y += (short) (Math.round(Math.random() * 2) + 1);
                        
                        if(balloon.getAdder() < -400) {
                            try {
                                balloons.remove(balloon);
                            } catch (Exception ignored) { }
                        }
                    }
                    if(balloon.getAdder() < -100) {
                        if (balloon.getX() < 500) {
                            balloon.direction = BalloonDirection.RIGHT;
                        } else if (balloon.getX() > 850) {
                            balloon.direction = BalloonDirection.LEFT;
                        }
                    }
                }
            }
            
            if(starlightMillis > 0) {
                realVignetteStarlight = alphaify(vignetteStarlight.request(), Math.min(1, starlightMillis / 7000F));
            }
            if (state == GameState.CHALLENGE) {
                CustomNight.getEnemies().forEach(enemy -> {
                    if (enemy.getWobbleIntensity() > 0) {
                        enemy.setWobbleIntensity(enemy.getWobbleIntensity() - 1);
                    }
                });
                if(CustomNight.holdingEnemyFrames > 25) {
                    if (CustomNight.selectedElement instanceof CustomNightEnemy enemy) {
                        if (CustomNight.isCustom()) {
                            if (keyHandler.isRightClick) {
                                enemy.declick();
                                if(enemy.getAI() == 0) {
                                    sound.play("lowSound", 0.05);
                                } else {
                                    sound.play("aiDown", 0.05);
                                }
                            } else {
                                if(enemy.getAI() < 8) {
                                    enemy.click(true);
                                    sound.play("aiUp", 0.05);
                                }
                            }
                        }
                    }
                }
            } else if(state == GameState.GAME) {
                int totalFlicker = night.getPepito().getFlicker() + night.getFlicker();
                if(disableFlickering) {
                    totalFlicker = (int) (totalFlicker / 1.5F);
                }

                if(totalFlicker > 0) {
                    goalFlicker = (short) (Math.random() * totalFlicker);
                } else {
                    goalFlicker = 0;
                    currentFlicker = 0;
                }
                if(night.getShock().isDoom()) {
                    if(night.getShock().getDoomCountdown() > 0) {
                        night.getShock().doomCountdown -= 0.007F;

                        if(night.getShock().getDoomCountdown() < 0) {
                            night.getShock().doomCountdown = 0;
                        }
                    }
                }
                if(night.isRainModifier()) {
                    synchronized (night.raindrops) {
                        for (int i = 0; i < 30; i++) {
                            night.raindrops.add(new Raindrop());
                        }

                        night.raindrops.removeIf(raindrop -> raindrop.getY() > 640);
                    }
                }
                if(deathScreenY >= 640) {
                    if(afterDeathText.length() != afterDeathCurText.length()) {
                        String str = String.valueOf(afterDeathText.charAt(afterDeathCurText.length()));
                        afterDeathCurText += str;
                        redrawDeathScreen();
                    }
                }
                if(night.getType().isBasement()) {
                    Basement env = (Basement) night.env();

                    if(env.getStage() == 5) {
                        if (env.getRedAlarmY() < 0) {
                            env.setRedAlarmY(env.getRedAlarmY() + 1);
                        }
                    }
                }
                if (night.getColaCat().megaColaY < 1000) {
                    tintedMegaSodaImg = null;
                    tintedMegaColaImg = null;
                }
                night.setWetFloor(Math.max(0, night.getWetFloor() - 0.001F));
                
            } else if(state == GameState.MILLY) {
                if(night.getType().isBasement()) {
                    Basement env = (Basement) night.env();

                    if (doMillyFlicker) {
                        env.millyGoalFlicker = (short) (Math.random() * 80);
                    } else {
                        env.millyGoalFlicker = 0;
                        env.millyCurrentFlicker = 0;
                    }
                }
            } else if(state == GameState.FIELD) {
                field.redrawRadarImg(this);
            }

            countersAlive[3] = true;
            countersAlive2[3] = true;
        }, 0, (int) (50 / universalGameSpeedModifier), TimeUnit.MILLISECONDS);
    }

    int playSelectorWaitCounter = 2;

    int waitUntilStaticChange = 0;

    boolean[] countersAlive = new boolean[4];
    boolean[] countersAlive2 = new boolean[] {true, true, true, true};
    double untilNextCheck = 0;

    void redrawCurrentStaticImg() {
        if(fadedStaticImg[tvStatic - 1] != null) {
            currentStaticImg = alphaify(fadedStaticImg[tvStatic - 1], staticTransparency);
        }
    }

    public void generateAdblocker() {
        if (adblockerStatus == 0) {
            short adblockerChance = (short) Math.round(Math.random() * 549);
            if (adblockerChance == 0) {
                adblockerStatus = 1;
                adblockerTimer = 11;

                adblockerPoint.x = (short) (20 + Math.round(Math.random() * 1040));
                adblockerPoint.y = (short) (20 + Math.round(Math.random() * 600));
                adblockerButton = new Rectangle((short) (adblockerPoint.x * widthModifier + centerX), (short) (adblockerPoint.y * heightModifier + centerY), (short) (100 * widthModifier), (short) (100 * heightModifier));
            }
        } else if(adblockerStatus == 1) {
            adblockerTimer--;
            if(adblockerTimer == 0) {
                adblockerStatus = 0;
            }
        } else if(adblockerStatus == 2) {
            if((night.seconds - night.secondsAtStart) > (night.getDuration() / 1.5)) {
                adblockerStatus = 3;
                adblocker.safeAdd(1);
            }
        }
    }

    public short currentWaterLevel = 640;
    public byte waterSpeed = -3;
    public short currentWaterPos = 0;
    public Color currentWaterColor = new Color(0, 195, 255, 120);
    Color currentWaterColor2 = new Color(0, 140, 255, 180);
    BufferedImage currentWaterImage;

    public void resetFlood() {
        currentWaterLevel = 640;
        waterSpeed = -3;
        currentWaterPos = 0;
    }



    public byte anim = 0;

    // 0 = clean
    // 1 = random screen pos
    // 2 = dark bg
    // 3 = stopsign
    // 4 = bright bg
    // 5 = jump scary

    public void updateCam() {
        BufferedImage cam;
        
        if (night.getPepito().isNotPepito) {
            if (night.getPepito().seconds > 1) {
                cam = camStates[0];
            } else if (night.getPepito().notPepitoRunsLeft > 0) {
                cam = camStates[3];
            } else {
                cam = camStates[0];
            }
            if(night.getPepito().notPepitoAI == 0) {
                cam = camStates[0];
            }
        } else {
            if (night.getPepito().seconds > 1) {
                cam = camStates[0];
            } else if (night.getPepito().pepitoStepsLeft > 0) {
                cam = camStates[1];
            } else {
                cam = camStates[0];
            }
            if(night.getPepito().pepitoAI == 0) {
                cam = camStates[0];
            }
        }

        if (night.getPepito().pepitoScareSeconds > 0 && night.getPepito().pepitoAI > 0) {
            cam = camStates[2];
        }
        if (night.getPepito().notPepitoScareSeconds > 0 && night.getPepito().notPepitoAI > 0) {
            cam = camStates[4];
        }

        if(inCam && night.getPepito().pepitoAI > 0) {
            if(cam == camStates[4] || cam == camStates[3]) {
                BingoHandler.completeTask(BingoTask.SEE_NOTPEPITO_CAM);
            }
        }

        if(night.getEvent() == GameEvent.MAXWELL) {
            cam = camStates[3];
        }
        if(night.getType() == GameType.DAY) {
            cam = camStates[5];
        }
        

        BufferedImage finalCam = new BufferedImage(cam.getWidth(), cam.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics2D g = finalCam.createGraphics();
        
        if(type == GameType.HYDROPHOBIA) {
            int[] map = new int[]{0, 0, 0, 0, 0};

            if (night.getHydrophobia().isEnabled()) {
                try {
                    map[night.getHydrophobia().getCurrentPos()] = 1;
                } catch (IndexOutOfBoundsException ignored) { }
            }
        
            drawHCCamera(map, g);
            g.dispose();
            
            finalCam = resize(bloom(resize(finalCam, 270, 160, Image.SCALE_FAST)), 1080, 640, Image.SCALE_FAST);
        } else {
            g.drawImage(cam, 0, 0, 1080, 640, null);
            g.dispose();
            
            if(Math.random() + Math.sin(fixedUpdatesAnim / 40F) < -0.5) {
                finalCam = contrast(finalCam, 1.05F, -50);
            }
        }
        
        this.camLayer0 = finalCam;
    }
    
    private void drawHCCamera(int[] map, Graphics2D g) {
        BufferedImage skibidi = new BufferedImage(640, 640, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g0 = (Graphics2D) skibidi.getGraphics();

        g0.setColor(Color.WHITE);
        g0.setStroke(new BasicStroke(6));

        for(int i = 0; i < map.length; i++) {
            int width = 106;
            if(i == 2) {
                width = 159;
            }
            if(map[i] == 1) {
                g0.setColor(Color.RED);
                g0.fillRect(320 - width / 2, 132 * i, width, 106);
                g0.setColor(Color.WHITE);
            }

            g0.drawRect(320 - width / 2, 132 * i, width, 106);
        }

        g0.dispose();
        skibidi = rotate(skibidi, (int) (fixedUpdatesAnim / 6F));
        skibidi = resize(skibidi, (int) (skibidi.getWidth() * 1.5), (int) (skibidi.getHeight() * 0.75), Image.SCALE_FAST);
        g.drawImage(skibidi, 540 - skibidi.getWidth() / 2, 320 - skibidi.getHeight() / 2 + 20, null);
        

        BufferedImage stbiddi = new BufferedImage(640, 640, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) stbiddi.getGraphics();

        g2.setStroke(new BasicStroke(5));
        g2.setColor(Color.WHITE);
        g2.drawLine(320, 0, 320, 635);

        for(int i = 0; i < map.length; i++) {
            int width = 106;
            if(i == 2) {
                width = 159;
            }
            g2.setColor(map[i] == 1 ? Color.RED : Color.BLACK);
            g2.fillRect(320 - width / 2, 132 * i, width, 106);

            g2.setColor(Color.WHITE);
            g2.drawRect(320 - width / 2, 132 * i, width, 106);

            if(i == 2) {
                g2.setFont(yuGothicBold60);
                g2.drawString("YOU", 320 - halfTextLength(g2, "YOU"), 264+75);

                g2.setColor(Color.RED);
                Polygon polygon = getPolygon(List.of(new Point(320, 270), new Point(300, 290), new Point(340, 290)));
                g2.fillPolygon(polygon);

                g2.setColor(Color.BLUE);
                polygon = getPolygon(List.of(new Point(320, 365), new Point(300, 345), new Point(340, 345)));
                g2.fillPolygon(polygon);
            }
        }

        g2.drawImage(hydroCamOverlay.request(), 0, 0, null);

        g2.dispose();
        stbiddi = rotate(stbiddi, (int) (fixedUpdatesAnim / 6F));
        stbiddi = resize(stbiddi, (int) (stbiddi.getWidth() * 1.5), (int) (stbiddi.getHeight() * 0.75), Image.SCALE_FAST);
        g.drawImage(stbiddi, 540 - stbiddi.getWidth() / 2, 320 - stbiddi.getHeight() / 2, null);


        double radians = Math.toRadians(90 + fixedUpdatesAnim / 6F);

        boolean reverse = Math.sin(radians) > 0;
        int start = reverse ? 0 : map.length - 1;
        int increment = reverse ? 1 : -1;

        while((reverse && start < map.length) || (!reverse && start > -1)) {
            if(start == 2) {
                g.setColor(Color.GREEN);

                Polygon polygon = getPolygon(List.of(new Point(540, 300), new Point(510, 230), new Point(570, 230)));
                if((fixedUpdatesAnim / 24) % 2 == 0) {
                    polygon.translate(0, -30);
                }
                g.fillPolygon(polygon);
            }
            if(map[start] == 1) {
                int j = 132 * start - 264;
                
                Point pt2 = new Point((int) (540 + Math.cos(radians) * j * 1.5), (int) (320 + Math.sin(radians) * j * 0.75));

                g.drawImage(camScaryHydrophobia.request(), pt2.x - 65, pt2.y - 100, null);
            }

            start += increment;
        }
        
//        applyHydrophobiaFilter(g, 1080);
    }

    public short[] glitchX = new short[] {0, 0};
    public short[] glitchY = new short[] {0, 0};


    BufferedImage[] fadedStaticImg = new BufferedImage[8];
    BufferedImage[] hcNoiseImg = new BufferedImage[16];
    BufferedImage currentStaticImg;
    BufferedImage[] discordStates = new BufferedImage[2];
    BufferedImage discord;
    BufferedImage[] musicMenuStates = new BufferedImage[2];
    BufferedImage musicMenu;
    BufferedImage[] moreMenu = new BufferedImage[2];
    PepitoImage logo = new PepitoImage("/menu/logo.png");
    PepitoImage scaryLogo = new PepitoImage("/menu/scaryLogo.png");
    boolean isScaryLogo = false;
    public BufferedImage[] officeImg = new BufferedImage[17];

    public PepitoImage door1Img = new PepitoImage("/game/office/door1.png");
    public PepitoImage door2Img = new PepitoImage("/game/office/door2.png");
    
    public PepitoImage basementDoor1Img = new PepitoImage("/game/office/basementDoor1.png");
    public PepitoImage basementDoor2Img = new PepitoImage("/game/office/basementDoor2.png");
    public PepitoImage basementDoor3Img = new PepitoImage("/game/office/basementDoor3.png");
    public PepitoImage basementDoor4Img = new PepitoImage("/game/office/basementDoor4.png");
    public PepitoImage basementDoor5Img = new PepitoImage("/game/office/basementDoor5.png");
    
    public PepitoImage basementWall1 = new PepitoImage("/game/basement/basementWall1.png");
    public PepitoImage basementWall2 = new PepitoImage("/game/basement/basementWall2.png");
    public PepitoImage basementWall3 = new PepitoImage("/game/basement/basementWall3.png");
    public PepitoImage basementWall4 = new PepitoImage("/game/basement/basementWall4.png");
    

    PepitoImage[] doorButton = new PepitoImage[] {new PepitoImage("/game/office/close.png"), new PepitoImage("/game/office/open.png")};
    PepitoImage timerDoorButton = new PepitoImage("/game/office/timerFront.png");

    public BufferedImage fullOffice;
    
    public PepitoImage monitorBasic = new PepitoImage("/game/office/monitorBasic.png");
    public PepitoImage monitorNoSignal = new PepitoImage("/game/office/monitorNoSignal.png");

    BufferedImage metalPipeImg;
    BufferedImage flashlightImg;
    BufferedImage sodaImg;
    BufferedImage[] fanImg = new BufferedImage[3];
    BufferedImage rotatedFanBlade;
    BufferedImage mudseal;
    BufferedImage sensorImg;
    BufferedImage planksImg;
    BufferedImage freezeImg;
    BufferedImage miniSodaImg;
    BufferedImage soupItemImg;
    BufferedImage speedrunTimerImg;
    BufferedImage styroPipeImg;
    PepitoImage weatherStationImg = new PepitoImage("/game/items/weatherStation.png");
    PepitoImage soggyPenImg = new PepitoImage("/game/items/soggyPen.png");
    PepitoImage dabloon = new PepitoImage("/game/milly/dabloon.png");
    PepitoImage evilDabloon = new PepitoImage("/game/milly/evilDabloon.png");
    PepitoImage millyButton = new PepitoImage("/game/milly/millyButton.png");
    PepitoImage billyButton = new PepitoImage("/game/milly/billyButton.png");
    PepitoImage millySkateboard = new PepitoImage("/game/milly/millySkateboard.png");
    PepitoImage basementMillyLightSource = new PepitoImage("/game/milly/basementLight.png");
    BufferedImage basementMillyLight = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_ARGB);
    
    PepitoImage[] pepitoClock = new PepitoImage[] {new PepitoImage("/game/endless/pepitoClock.png"), new PepitoImage("/game/endless/pepitoClockHover.png")};
    int pepitoClockProgress = 0;
    
    PepitoImage neonSog = new PepitoImage("/game/endless/neonSog.png");
    PepitoImage neonSogSign = new PepitoImage("/game/endless/neonSogSign.png");
    PepitoImage neonSogBallImage = new PepitoImage("/game/endless/neonSogBallYellow.png");
    int neonSogX = -400;

    byte neonSogAnim = 0;
    float neonSogBallSize = 0;
    
    PepitoImage infinitySign = new PepitoImage("/game/endless/infinitySign.png");
    PepitoImage riftIndicator = new PepitoImage("/game/endless/riftIndicator.png");
    int riftIndicatorX = 0;

    PepitoImage energyRegenIcon = new PepitoImage("/game/office/energyRegenIcon.png");
    
    PepitoImage psyopPoster = new PepitoImage("/game/basement/psyop.png");
    PepitoImage noPartiesAllowed = new PepitoImage("/game/basement/noPartiesAllowed.png");
    PepitoImage vent = new PepitoImage("/game/basement/vent.png");
    PepitoImage ventLaying = new PepitoImage("/game/basement/ventLaying.png");
    PepitoImage redAlarm = new PepitoImage("/game/basement/redAlarm.png");
    PepitoImage redAlarmOff = new PepitoImage("/game/basement/redAlarmOff.png");
    PepitoImage evilDoor = new PepitoImage("/game/basement/evilDoor.png");
    PepitoImage evilDoorLockIcon = new PepitoImage("/game/basement/doorLock.png");
    PepitoImage evilDoorArrowIcon = new PepitoImage("/game/basement/doorArrow.png");
    PepitoImage radioThing = new PepitoImage("/game/basement/anton griddy.png");
    PepitoImage leverHandle = new PepitoImage("/game/basement/leverHandle.png");
    
    PepitoImage crateBack = new PepitoImage("/game/basement/crateBack.png");
    PepitoImage crateFront = new PepitoImage("/game/basement/crateFront.png");

    PepitoImage colaImg = new PepitoImage("/game/entities/colacat/cola.png");

    PepitoImage wiresImg = new PepitoImage("/game/entities/wires/wires.png");
    BufferedImage[] wiresText = new BufferedImage[2];
    PepitoImage frogImg = new PepitoImage("/game/entities/frogo.png");

    PepitoImage restInPeice = new PepitoImage("/game/restInPeice.png");
    PepitoImage strawber = new PepitoImage("/game/strawber.png");
    BufferedImage astartaSticker;
    PepitoImage restInPeiceHydro = new PepitoImage("/game/hydrophobia/restInPeiceHydro.png");
    PepitoImage restInPeiceHydroFull = new PepitoImage("/game/hydrophobia/restInPeiceHydroFull.png");
    PepitoImage restInPeiceField = new PepitoImage("/game/hydrophobia/field/restInPeiceField.png");
    PepitoImage deathScreenRender = new PepitoImage("/game/deathScreenRender.png");
    BufferedImage deathScreenText = new BufferedImage(456, 330, BufferedImage.TYPE_INT_ARGB);
    PepitoImage basementWinTitle = new PepitoImage("/game/basementWin.png");
    PepitoImage basementWinGradient = new PepitoImage("/game/basementWinGradient.png");


    public BufferedImage[] camStates = new BufferedImage[6]; // 0 = empty; 1 = pepitoBack; 2 = pepitoLeave; 3 = notPepitoBack; 4 = notPepitoLeave; // 5 = day
    PepitoImage hyperCam = new PepitoImage("/game/cam/hyperCam2.png");
    BufferedImage cam1A;
    BufferedImage[] astartaCam = new BufferedImage[2];
    BufferedImage makiCam;
    BufferedImage noSignal;
    PepitoImage camScaryHydrophobia = new PepitoImage("/game/cam/scaryHydrophobia.png");
    PepitoImage hydroCamOverlay = new PepitoImage("/game/cam/hydroCamOverlay.png");

    BufferedImage jumpscare;
    PepitoImage[] jumpscares = new PepitoImage[17]; // 0 - shadowpepito; 1 - pepito; 2 - notPepito; 3 - astarta; 4 - msi;
    // 5 - cocacola; 6 - maki; 7,8 - shark; 9 - boykisser; 10 - lemonade; 11 - dread; 12 - scary cat; 13 - el astarta; 14 - dsc; 15 - krunlic; 16 - shock;


    BufferedImage adblockerImage;
    BufferedImage canny;
    BufferedImage[] uncanny = new BufferedImage[2];
    PepitoImage stopSign = new PepitoImage("/game/entities/a90/stopSign.png");
    PepitoImage warningSign = new PepitoImage("/game/entities/a90/warningSign.png");


    BufferedImage fieldCanny;
    BufferedImage[] fieldUncanny = new BufferedImage[2];
    PepitoImage fieldA90StopSign = new PepitoImage("/game/hydrophobia/field/a90/stopsign.png");
    PepitoImage fieldA90WarningSign = new PepitoImage("/game/hydrophobia/field/a90/warning.png");
    

    BufferedImage smallAdblockerImage;

    public BufferedImage[] msiImage = new BufferedImage[4];
    PepitoImage msiArrow = new PepitoImage("/game/entities/msi/msiArrow.png");

    PepitoImage[] scaryCatImage = new PepitoImage[] {new PepitoImage("/game/entities/scaryCat/scaryCat.png"), new PepitoImage("/game/entities/scaryCat/scaryCatShadow.png"), new PepitoImage("/game/entities/scaryCat/scaryCat9.png")};
    PepitoImage[] scaryCatWarn = new PepitoImage[] {new PepitoImage("/game/entities/scaryCat/warn.png"), new PepitoImage("/game/entities/scaryCat/shadowWarn.png"), new PepitoImage("/game/entities/scaryCat/warn9.png")};
    PepitoImage[] scaryCatMove = new PepitoImage[] {new PepitoImage("/game/entities/scaryCat/move.png"), new PepitoImage("/game/entities/scaryCat/shadowMove.png"), new PepitoImage("/game/entities/scaryCat/move9.png")};
    PepitoImage blackScaryCat = new PepitoImage("/game/entities/scaryCat/blackScaryCat.png");
    public PepitoImage nuclearCatEye = new PepitoImage("/game/entities/scaryCat/nuclearCatEye.png");
    public List<NuclearCatEye> nuclearCatEyes = new ArrayList<>();
    
    BufferedImage astartaEyes;

    PepitoImage creditsdotpng = new PepitoImage("/menu/creditsdotpng.png");
    PepitoImage headphonesImg = new PepitoImage("/game/office/headphones/headphone.png");
    PepitoImage headphoneLeft = new PepitoImage("/game/office/headphones/kamalaHarris.png");
    PepitoImage headphoneRight = new PepitoImage("/game/office/headphones/donaldTrump.png");
    PepitoImage lockerInsideImg = new PepitoImage("/game/office/lockerInside.png");
    PepitoImage sunglassesOverlay = new PepitoImage("/game/office/sunglasses.png");

    PepitoImage millyShopColors = new PepitoImage("/game/milly/colors.png");
    BufferedImage millyShopColorsChanging;
    BufferedImage[] vignette = new BufferedImage[2];
    BufferedImage[] alphaVignette = new BufferedImage[2];

    public PepitoImage wata = new PepitoImage("/game/entities/shark/wata.png");
    PepitoImage koi = new PepitoImage("/game/entities/shark/koi.png");
    PepitoImage sharkImg = new PepitoImage("/game/entities/shark/shark.png");
    PepitoImage boykisserImg = new PepitoImage("/game/entities/boykisser/boykisser.png");
    PepitoImage sobEmoji = new PepitoImage("/game/entities/boykisser/sob.png");
    PepitoImage a120Img = new PepitoImage("/game/entities/a120/a120.png");
    PepitoImage[] lemonadeGato = new PepitoImage[] {new PepitoImage("/game/entities/lemonade/gato.png"), new PepitoImage("/game/entities/lemonade/gato2.png"),
            new PepitoImage("/game/entities/lemonade/nuclearGato.png"), new PepitoImage("/game/entities/lemonade/nuclearGato2.png")};
    PepitoImage lemonadeOxygenOverlay = new PepitoImage("/game/entities/lemonade/oxygenOverlay.png");
    PepitoImage lemonadeOxygenBar = new PepitoImage("/game/entities/lemonade/oxygenBar.png");
    PepitoImage lemonadeFog = new PepitoImage("/game/entities/lemonade/fog.png");
    public List<NuclearLemonadeFog> nuclearLemonadeFog = new ArrayList<>();
    
    BufferedImage maxwellIcon;
    BufferedImage birthdayMaxwellIcon;
    BufferedImage birthdayHatImg;

    PepitoImage lemon = new PepitoImage("/game/entities/lemonade/lemon.png");
    PepitoImage soggyBalls = new PepitoImage("/game/endless/soggy balls.png");

    BufferedImage[] itemTags = new BufferedImage[6];
    BufferedImage riftImg;
    BufferedImage riftFrame;
    PepitoImage riftMoon = new PepitoImage("/game/rift/riftMoon.png");
    BufferedImage shadowPortal;

    BufferedImage mirrorCatImg;
    BufferedImage[] mirrorCage = new BufferedImage[2];
    BufferedImage mirrorCatExplode;
    PepitoImage mirrorCatRmb = new PepitoImage("/game/entities/mirrorcat/mouseRightClick.png");

    BufferedImage lockedAchievementImg;
    BufferedImage achievementMenuArrow;

    BufferedImage manualImg;
    BufferedImage manualMissingTextImg;
    PepitoImage geigerCounter = new PepitoImage("/game/office/geigerCounter.png");

    BufferedImage[] sastartaTank = new BufferedImage[2];
    PepitoImage sastartaFast = new PepitoImage("/game/entities/astartaBoss/astartaFast.png");
    PepitoImage astartaMinecart = new PepitoImage("/game/entities/astartaBoss/minecart.png");
    BufferedImage astartaMinecartWhite;
    PepitoImage[] astartaBlackHole = new PepitoImage[] {new PepitoImage("/game/entities/astartaBoss/blackHole1.png"), new PepitoImage("/game/entities/astartaBoss/blackHole2.png")};
    PepitoImage uncannyBox = new PepitoImage("/game/entities/astartaBoss/uncannyBox.png");
    PepitoImage misterImg = new PepitoImage("/game/entities/astartaBoss/mister.png");
    PepitoImage misterGlowingImg = new PepitoImage("/game/entities/astartaBoss/misterGlowing.png");
    PepitoImage misterText = new PepitoImage("/game/entities/astartaBoss/misterText.png");
    PepitoImage astartaTarget = new PepitoImage("/game/entities/astartaBoss/target.png");
    PepitoImage astartaVignette1 = new PepitoImage("/game/entities/astartaBoss/vignette1.png");
    PepitoImage astartaVignette2 = new PepitoImage("/game/entities/astartaBoss/vignette2.png");

    PepitoImage[] checkpointHalfway = new PepitoImage[] {new PepitoImage("/game/entities/astartaBoss/checkpoint/halfway.png"), new PepitoImage("/game/entities/astartaBoss/checkpoint/halfwayBG.png")};
    PepitoImage[] checkpointAstartaBoss = new PepitoImage[] {new PepitoImage("/game/entities/astartaBoss/checkpoint/astartaBoss.png"), new PepitoImage("/game/entities/astartaBoss/checkpoint/astartaBossBG.png")};

    PepitoImage rouletteBackground = new PepitoImage("/game/entities/astartaBoss/rouletteBackground.png");
    PepitoImage[] roulette = new PepitoImage[] {new PepitoImage("/game/entities/astartaBoss/roulette1.png"), new PepitoImage("/game/entities/astartaBoss/roulette2.png"), new PepitoImage("/game/entities/astartaBoss/roulette3.png")};

    PepitoImage[] shadowblockerButton = new PepitoImage[] {new PepitoImage("/game/entities/astartaBoss/shadowblockerButton.png"), new PepitoImage("/game/entities/astartaBoss/shadowblockerButtonOutline.png")};

    BufferedImage starlightBottleImg;
    PepitoImage vignetteStarlight = new PepitoImage("/game/office/vignetteStarlight.png");
    BufferedImage realVignetteStarlight;

    BufferedImage flashlightLayer;
    
    PepitoImage pishPishImg = new PepitoImage("/game/items/pishpish.png");
    PepitoImage[] dryCatImg = new PepitoImage[] {new PepitoImage("/game/basement/dryCatGame/dryCat.png"), new PepitoImage("/game/basement/dryCatGame/dryCatRed.png")};
    PepitoImage[] soggyCatImg = new PepitoImage[] {new PepitoImage("/game/basement/dryCatGame/soggyCat.png"), new PepitoImage("/game/basement/dryCatGame/soggyCatRed.png")};
    PepitoImage dryCatExplodeImg = new PepitoImage("/game/basement/dryCatGame/explosion.png");
    PepitoImage dryCatDoorImg = new PepitoImage("/game/basement/dryCatGame/door.png");
    PepitoImage waterSprayParticles = new PepitoImage("/game/basement/dryCatGame/waterSpray.png");
    
    public static PepitoImage pishPishCompressed = new PepitoImage("/game/items/pishpishCompressed.png");
    

    PepitoImage iceBucketImg = new PepitoImage("/game/items/iceBucket.png");
    PepitoImage red40Img = new PepitoImage("/game/items/red40Icon.png");
    
    PepitoImage hcExit = new PepitoImage("/game/hydrophobia/exit.png");
    PepitoImage hcBarrier = new PepitoImage("/game/hydrophobia/nowraisethisfuckingbarrier.png");
    PepitoImage hcConditioner = new PepitoImage("/game/hydrophobia/conditioner.png");
    PepitoImage hcCompass = new PepitoImage("/game/hydrophobia/compass.png");
    PepitoImage hcCompassArrow = new PepitoImage("/game/hydrophobia/compassArrow.png");
    PepitoImage hcLocker = new PepitoImage("/game/hydrophobia/locker.png");
    PepitoImage hcTimer = new PepitoImage("/game/hydrophobia/timer.png");
    PepitoImage hcTable = new PepitoImage("/game/hydrophobia/table.png");
    PepitoImage hcHighlightedTable = new PepitoImage("/game/hydrophobia/highlightedTable.png");
    PepitoImage hcMultiplyLayer = new PepitoImage("/game/hydrophobia/multiplyLayer.png");
    PepitoImage hcRotateCompass = new PepitoImage("/game/hydrophobia/rotateCompass.png");
    PepitoImage hcBarrel = new PepitoImage("/game/hydrophobia/barrel.png");
    PepitoImage hcExitSignDark = new PepitoImage("/game/hydrophobia/exitSignDark.png");
    PepitoImage hcExitSign = new PepitoImage("/game/hydrophobia/exitSign.png");
    PepitoImage hcPipe = new PepitoImage("/game/hydrophobia/pipe.png");
    PepitoImage hcDoorReinforced = new PepitoImage("/game/hydrophobia/doorReinforced.png");
    PepitoImage hcDustonOfficeCrates = new PepitoImage("/game/hydrophobia/dustonOfficeCrates.png");
    PepitoImage hcDustonKey = new PepitoImage("/game/hydrophobia/dustonKey.png");
    PepitoImage[] hcCockroach = new PepitoImage[] {new PepitoImage("/game/hydrophobia/serious cockroach.png"), new PepitoImage("/game/hydrophobia/funny ahh cockroach.png")};
    PepitoImage hcCup = new PepitoImage("/game/hydrophobia/cup.png");
    PepitoImage coffeeParticle = new PepitoImage("/game/hydrophobia/coffeeParticle.png");
    PepitoImage hcMonitor = new PepitoImage("/game/hydrophobia/monitor.png");
    PepitoImage hcCompassNumbers = new PepitoImage("/game/hydrophobia/compassNumbers.png");
    PepitoImage hcDeathGlow = new PepitoImage("/game/hydrophobia/selectedDeathOption.png");
    public PepitoImage hcPrefieldMsiLogo = new PepitoImage("/game/hydrophobia/prefieldMsiLogo.png");
    PepitoImage blueBattery = new PepitoImage("/game/hydrophobia/blueBattery.png");
    PepitoImage impulseText = new PepitoImage("/game/hydrophobia/impulseText.png");
    
    PepitoImage fieldSky = new PepitoImage("/game/hydrophobia/field/sky.png");
    PepitoImage fieldWhiteSky = new PepitoImage("/game/hydrophobia/field/whiteSky.png");
    public static PepitoImage fieldMediumTree = new PepitoImage("/game/hydrophobia/field/treeMedium.png");
    public static PepitoImage fieldMediumTree2 = new PepitoImage("/game/hydrophobia/field/treeMedium2.png");
    public static PepitoImage fieldMediumTree3 = new PepitoImage("/game/hydrophobia/field/treeMedium3.png");
    public static PepitoImage pineMedium = new PepitoImage("/game/hydrophobia/field/pineMedium.png");
    public static PepitoImage pineMedium2 = new PepitoImage("/game/hydrophobia/field/pineMedium2.png");
    public static PepitoImage pineMedium3 = new PepitoImage("/game/hydrophobia/field/pineMedium3.png");
    public static PepitoImage treeStump = new PepitoImage("/game/hydrophobia/field/treeStump.png");
    public static PepitoImage treeDead = new PepitoImage("/game/hydrophobia/field/treeDead.png");
    public static PepitoImage fieldGrass = new PepitoImage("/game/hydrophobia/field/grass.png");
    public static PepitoImage fieldWall = new PepitoImage("/game/hydrophobia/field/WAAALLLL.png");
    public static PepitoImage fieldEndBuilding = new PepitoImage("/game/hydrophobia/field/fieldEnd.png");
    public static PepitoImage fieldLightsTower = new PepitoImage("/game/hydrophobia/field/lightsTower.png");
    public static PepitoImage fieldLandmine = new PepitoImage("/game/hydrophobia/field/landmine.png");
    public static PepitoImage fieldSpeedLimit10 = new PepitoImage("/game/hydrophobia/field/speedLimit10.png");
    public static PepitoImage fieldSurpriseSog = new PepitoImage("/game/hydrophobia/field/surpriseSog.png");
    public static PepitoImage fieldBarriers = new PepitoImage("/game/hydrophobia/field/barriers.png");
    PepitoImage fieldRoad = new PepitoImage("/game/hydrophobia/field/road.png");
    PepitoImage fieldCar = new PepitoImage("/game/hydrophobia/field/car.png");
    PepitoImage fieldWheel = new PepitoImage("/game/hydrophobia/field/wheel.png");
    PepitoImage fieldBlimp = new PepitoImage("/game/hydrophobia/field/blimp.png");
    PepitoImage fieldBlimpMirrored = new PepitoImage("/game/hydrophobia/field/blimpMirrored.png");
    PepitoImage fieldBlimpFront = new PepitoImage("/game/hydrophobia/field/blimpFront.png");
    
    PepitoImage fieldCarBehind = new PepitoImage("/game/hydrophobia/field/fieldCarBehind.png");
    PepitoImage fieldCarArrow = new PepitoImage("/game/hydrophobia/field/carArrow.png");
    PepitoImage fieldCarControls = new PepitoImage("/game/hydrophobia/field/carControls.png");
    PepitoImage fieldCup = new PepitoImage("/game/hydrophobia/field/cup.png");
    
    PepitoImage fieldCommuncationsBg = new PepitoImage("/game/hydrophobia/field/communicationsBg.png");
    PepitoImage fieldLeverBase = new PepitoImage("/game/hydrophobia/field/leverBase.png");
    PepitoImage fieldLeverHandle = new PepitoImage("/game/hydrophobia/field/leverHandle.png");
    PepitoImage fieldLeverHandleLit = new PepitoImage("/game/hydrophobia/field/leverHandleLit.png");
    PepitoImage fieldLeverGlow = new PepitoImage("/game/hydrophobia/field/leverGlow.png");
    public PepitoImage fieldRadarBg = new PepitoImage("/game/hydrophobia/field/radarBg.png");
    
    
    PepitoImage[] beastImages = new PepitoImage[6];
    PepitoImage beastGlow = new PepitoImage("/game/entities/beast/beastGlow.png");
    PepitoImage overseerFront = new PepitoImage("/game/entities/overseer/overseerFront.png");
    PepitoImage overseerBack = new PepitoImage("/game/entities/overseer/overseerBack.png");

    PepitoImage jumpscareCat = new PepitoImage("/game/entities/jumpscareCat.png");
    PepitoImage makiWarning = new PepitoImage("/game/entities/makiWarning.png");
    BufferedImage randomsog;
    PepitoImage conflictingItem = new PepitoImage("/game/items/conflictingItem.png");
    PepitoImage shadowGlitch = new PepitoImage("/game/endless/shadowGlitch.png");
    PepitoImage blizzardAnnouncement = new PepitoImage("/game/office/blizzardAnnouncement.png");
    PepitoImage notPepitoVent = new PepitoImage("/game/entities/notPepitoVent.png");

    PepitoImage discIcon = new PepitoImage("/menu/discs/discIcon.png");
    
    PepitoImage battery = new PepitoImage("/game/office/generator/battery.png");
    PepitoImage[] charge = new PepitoImage[] {new PepitoImage("/game/office/generator/uncharged.png"), new PepitoImage("/game/office/generator/charged.png")};
    PepitoImage generator = new PepitoImage("/game/office/generator/generator.png");
    PepitoImage generatorOutline = new PepitoImage("/game/office/generator/generatorOutline.png");
    PepitoImage connectText = new PepitoImage("/game/office/generator/connectText.png");
    PepitoImage sparks = new PepitoImage("/game/basement/sparks.png");
    PepitoImage basementLightShadow = new PepitoImage("/game/basement/light/basementLightShadow.png");
    public PepitoImage basementLadder = new PepitoImage("/game/basement/ladder i think.png");
    PepitoImage basementOverseer = new PepitoImage("/game/basement/overseerScary.png");
    PepitoImage basementCrateLaying = new PepitoImage("/game/basement/crateLaying.png");
    public PepitoImage basementStaticGlow = new PepitoImage("/game/basement/light/staticGlow.png");
    public PepitoImage basementBeam = new PepitoImage("/game/basement/light/beam.png");
    
    BufferedImage[] basementDisperse = new BufferedImage[3];

    boolean basementHyperOptimization = false;
    BufferedImage lastBHOImage;
    boolean redrawBHO = true;

    int basementLadderFrames = 0;
    boolean basementLadderHeld = false;
    boolean basementLadderHovering = false;

    PepitoImage greenBattery = new PepitoImage("/game/basement/generator/greenBattery.png");
    public PepitoImage greenCharge = new PepitoImage("/game/basement/generator/charge.png");
    PepitoImage escToCancel = new PepitoImage("/game/basement/generator/escToCancel.png");
    public PepitoImage basementMonitorBg = new PepitoImage("/game/basement/generator/monitorBg.png");
    public PepitoImage basementMonitorBgCharge = new PepitoImage("/game/basement/generator/monitorBgCharge.png");
    public PepitoImage basementMonitorBgConnect = new PepitoImage("/game/basement/generator/monitorBgConnect.png");
    public PepitoImage basementMonitorBgBroken = new PepitoImage("/game/basement/generator/monitorBroken.png");
    public PepitoImage basementMonitorShadow = new PepitoImage("/game/basement/generator/monitorShadow.png");
    
    public PepitoImage pipe = new PepitoImage("/game/office/pipe.png");
    

    PepitoImage fog = new PepitoImage("/game/office/fog.png");
    PepitoImage lesserFog = new PepitoImage("/game/office/lesserFog.png");
    PepitoImage msiKnows = new PepitoImage("/game/office/msiKnows.png");
    PepitoImage timerBoard = new PepitoImage("/game/office/timerBoard.png");

    PepitoImage gruggy = new PepitoImage("/game/office/radiation/gruggy.png");
    PepitoImage gruggyCart = new PepitoImage("/game/office/radiation/gruggyCart.png");
    PepitoImage gruggyRing = new PepitoImage("/game/office/radiation/gruggyRing.png");
    PepitoImage radiationVignette = new PepitoImage("/game/office/radiation/radiationVignette.png");

    PepitoImage batterySaver = new PepitoImage("/game/entities/astartaBoss/batterySaver.png");
    PepitoImage batterySaverOverlay = new PepitoImage("/game/entities/astartaBoss/batterySaverOverlay.png");
    
    PepitoImage[] kijiJumpscares = new PepitoImage[] {new PepitoImage("/game/entities/kiji/death1.png"), new PepitoImage("/game/entities/kiji/death2.png"),
            new PepitoImage("/game/entities/kiji/death3.png"), new PepitoImage("/game/entities/kiji/death4.png")};
    PepitoImage kijiCrosshair = new PepitoImage("/game/entities/kiji/crosshair.png");
    PepitoImage[] kijiText = new PepitoImage[] {new PepitoImage("/game/entities/kiji/focus.png"), new PepitoImage("/game/entities/kiji/hold.png"),
            new PepitoImage("/game/entities/kiji/release.png")};
    
    PepitoImage[] shockCat = new PepitoImage[12];
    PepitoImage shockCatIncoming = new PepitoImage("/game/entities/shock/incoming.png");
    
    PepitoImage toleToleMain = new PepitoImage("/game/entities/toletole/toletole.png");
    PepitoImage toleToleDoor = new PepitoImage("/game/entities/toletole/toleToleDoor.png");
    PepitoImage toleToleLeave = new PepitoImage("/game/entities/toletole/toleToleLeave.png");

    public static int toleToleSpawned = 0;
    public static int toleToleKilled = 0;
    public static boolean toleTolePosterSeen = true;

    PepitoImage toleTolePoster = new PepitoImage("/game/basement/toleTolePoster.png");
    
    
    PepitoImage deepSeaCreatureImage = new PepitoImage("/game/entities/dsc/bob leponge.png");
    PepitoImage harpoonBase = new PepitoImage("/game/entities/dsc/harpoonBase.png");
    PepitoImage harpoonGun = new PepitoImage("/game/entities/dsc/harpoonGun.png");
    PepitoImage harpoonSpear = new PepitoImage("/game/entities/dsc/harpoonSpear.png");
    PepitoImage harpoonGunOld = new PepitoImage("/game/entities/dsc/harpoonGunOld.png");
    PepitoImage harpoonChainPiece = new PepitoImage("/game/entities/dsc/harpoonChainPiece.png");

    PepitoImage krunlicAchievement = new PepitoImage("/menu/achievements/krunlic.png");
    PepitoImage[] krEye = new PepitoImage[] {new PepitoImage("/game/entities/krunlic/krEye1.png"), new PepitoImage("/game/entities/krunlic/krEye2.png"),
            new PepitoImage("/game/entities/krunlic/krEye3.png")};
    PepitoImage krunlicScary = new PepitoImage("/game/entities/krunlic/krunlicScary.png");


    PepitoImage mrMaze = new PepitoImage("/game/entities/mr maze.png");
    
    PepitoImage platBlock = new PepitoImage("/platformer/block.png");
    PepitoImage platKill = new PepitoImage("/platformer/kill.png");
    PepitoImage platAccurateHitbox = new PepitoImage("/platformer/accurate_hitbox.png");
    PepitoImage platCharacter = new PepitoImage("/platformer/character.png");
    PepitoImage platButton = new PepitoImage("/platformer/superpepitochallenge BAD.png");
    
    PepitoImage challengeCyanFade = new PepitoImage("/menu/challenge/cyanFade.png");
    PepitoImage challengeBlackFade = new PepitoImage("/menu/challenge/blackFade.png");
    
    PepitoImage customItemFaded = new PepitoImage("/game/items/customItemFaded.png");
    
    PepitoImage glitcherUnit = new PepitoImage("/game/entities/glitcherUnit.png");
    
    PepitoImage megaCola = new PepitoImage("/game/entities/colacat/megaCola.png");
    PepitoImage megaColaFlame = new PepitoImage("/game/entities/colacat/megaColaFlame.png");
    PepitoImage megaColaGlow = new PepitoImage("/game/entities/colacat/megaColaGlow.png");
    public BufferedImage megaColaImg;
    public BufferedImage megaColaGlowImg;
    public BufferedImage tintedMegaColaImg;
    public BufferedImage tintedMegaSodaImg;

    PepitoImage theStrip = new PepitoImage("/game/the strip.png");

    PepitoImage halfShadownightTrophy = new PepitoImage("/menu/halfShadownightTrophy.png");
    PepitoImage shadownightTrophy = new PepitoImage("/menu/shadownightTrophy.png");
    
    PepitoImage disclaimerImage = new PepitoImage("/menu/disclaimer.png");
    PepitoImage uhOh = new PepitoImage("/uhOh.png");


    PepitoImage[] snowflake = new PepitoImage[] {new PepitoImage("/game/office/snowflake.png"), new PepitoImage("/game/office/snowflake2.png")};
    List<Snowflake> snowflakes = new ArrayList<>();

    PepitoImage bubbleImage = new PepitoImage("/game/office/bubbles.png");
    List<BubbleParticle> bubbles = new ArrayList<>();
    
    
    PepitoImage invstgIcon = new PepitoImage("/menu/investigation/investigationIcon.png");
    PepitoImage invstgBg = new PepitoImage("/menu/investigation/bgRegular.png");
    PepitoImage invstgMultiplyLayer = new PepitoImage("/menu/investigation/multiplyLayer.png");
    PepitoImage invstgMilly = new PepitoImage("/menu/investigation/papers/millyShop.png");
    PepitoImage invstgChBadge = new PepitoImage("/menu/investigation/papers/challengeBadge.png");
    PepitoImage invstgEndlessBadge = new PepitoImage("/menu/investigation/papers/endlessBadge.png");
    boolean hoveringInvestigation = false;
    

    static HashMap<String, String> languageText = new HashMap<>();

    static BufferedImage balloonImg;
    public static List<Balloon> balloons = new ArrayList<>();

    public List<Hallucination> hallucinations = new ArrayList<>();
    public List<Integer> batteryRegenIcons = new ArrayList<>();


    public static boolean krunlicMode = false;
    public static int krunlicPhase = 0;
    public static long krunlicSeconds = -20;
    public static List<Point> krunlicEyes = new ArrayList<>();
    

    public boolean fanActive = false;

    public int launchedGameTime = (int) (System.currentTimeMillis() / 1000);

    boolean isPepitoBirthday;
    short birthdayAnimation = 0;

    public static boolean isAprilFools;
    public static boolean isDecember;
    Platformer platformer;
    public boolean hoveringPlatButton = false;
    

    byte recordEndlessNight = 0;
    public boolean reachedAstartaBoss = false;
    public boolean everEnteredBasement = false;
    public boolean gotEndlessNight6AfterAllNighter = false;
    public boolean everGotKrunlicFile = false;
    public boolean playedAfterBeatingBasement = false;
    public boolean joinedBefore = false;
    byte neonSogSkips = 0;
    
    

    public void save() {
        if(!canSave)
            return;
        
        Path dataPath = Path.of(gameDirectory + "\\data.txt");
        StringBuilder dataBuilder = new StringBuilder();

        try {
            dataBuilder.append("win:").append(winCount).append("\n");
            dataBuilder.append("death:").append(deathCount).append("\n");
            dataBuilder.append("night:").append(currentNight).append("\n");
            dataBuilder.append("vol:").append(volume).append("\n");
            dataBuilder.append("borders:").append((blackBorders) ? "1" : "0").append("\n");
            dataBuilder.append("headphones:").append((headphones) ? "1" : "0").append("\n");
            dataBuilder.append("record:").append(recordEndlessNight).append("\n");
            dataBuilder.append("bingo:").append((unlockedBingo) ? "1" : "0").append("\n");
            dataBuilder.append("reachedAstartaBoss:").append((reachedAstartaBoss) ? "1" : "0").append("\n");
            dataBuilder.append("everEnteredBasement:").append((everEnteredBasement) ? "1" : "0").append("\n");
            dataBuilder.append("gotEndlessNight6AfterAllNighter:").append((gotEndlessNight6AfterAllNighter) ? "1" : "0").append("\n");
            dataBuilder.append("joinedBefore:").append((joinedBefore) ? "1" : "0").append("\n");
            dataBuilder.append("neonSogSkips:").append(neonSogSkips).append("\n");
            dataBuilder.append("everGotKrunlicFile:").append((everGotKrunlicFile) ? "1" : "0").append("\n");
            dataBuilder.append("menuSong:").append(menuSong).append("\n");
            dataBuilder.append("receivedShadowblocker:").append((receivedShadowblocker) ? "1" : "0").append("\n");
            dataBuilder.append("beatShadownightBasement:").append((beatShadownightBasement) ? "1" : "0").append("\n");
            dataBuilder.append("seenEndlessDisclaimer:").append((seenEndlessDisclaimer) ? "1" : "0").append("\n");
            dataBuilder.append("playedAfterBeatingBasement:").append((playedAfterBeatingBasement) ? "1" : "0").append("\n");

            dataBuilder.append("disclaimer:").append((disclaimer) ? "1" : "0").append("\n");
            dataBuilder.append("showManual:").append((showManual) ? "1" : "0").append("\n");
            dataBuilder.append("saveScreenshots:").append((saveScreenshots) ? "1" : "0").append("\n");
            dataBuilder.append("bloom:").append((bloom) ? "1" : "0").append("\n");
            dataBuilder.append("fpsCounter:").append((fpsCounters[0]) ? "1" : "0").append("\n");
            dataBuilder.append("fpsCap:").append(fpsCap).append("\n");
            dataBuilder.append("jumpscareShake:").append(jumpscareShake).append("\n");
            dataBuilder.append("language:").append(language).append("\n");
            dataBuilder.append("screenShake:").append((screenShake) ? "1" : "0").append("\n");
            dataBuilder.append("disableFlickering:").append((disableFlickering) ? "1" : "0").append("\n");
            dataBuilder.append("fullscreen:").append((fullscreen) ? "1" : "0").append("\n");
            
            StringBuilder musicDiscList = new StringBuilder();
            for(String string : musicDiscs) {
                musicDiscList.append(",").append(string);
            }
            dataBuilder.append("musicDiscs:").append(musicDiscList.toString().replaceFirst(",", "")).append("\n");
            
            
            writeFile(dataPath, Base64.getEncoder().encodeToString(dataBuilder.toString().getBytes()));
        } catch (Exception ignored) { }


        Path itemsPath = Path.of(gameDirectory + "\\items.txt");
        StringBuilder itemsBuilder = new StringBuilder();

        try {
            for(Item item : fullItemList) {
                itemsBuilder.append(item.getId()).append(":").append(item.getAmount()).append("\n");
            }
            writeFile(itemsPath, Base64.getEncoder().encodeToString(itemsBuilder.toString().getBytes()));
        } catch (Exception ignored) { }


        Path achievementsPath = Path.of(gameDirectory + "\\achievements.txt");
        StringBuilder achievementsBuilder = new StringBuilder();

        try {
            for(Achievements achievement : Achievements.values()) {
                achievementsBuilder.append(achievement.name()).append(":").append(achievement.isObtained() ? "1" : "0").append("\n");
            }

            writeFile(achievementsPath, Base64.getEncoder().encodeToString(achievementsBuilder.toString().getBytes()));
        } catch (Exception ignored) { }

        Path statisticsPath = Path.of(gameDirectory + "\\statistics.txt");
        StringBuilder statisticsBuilder = new StringBuilder();

        try {
            for(Statistics statistic : Statistics.values()) {
                statisticsBuilder.append(statistic.toString()).append(":").append(statistic.getValue()).append("\n");
            }

            writeFile(statisticsPath, Base64.getEncoder().encodeToString(statisticsBuilder.toString().getBytes()));
        } catch (Exception ignored) { }
    }



    /** @noinspection ConstantConditions*/
    @Override
    public void run() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
        }).build();
        DiscordRPC.discordInitialize("1112426204063084686", handlers, true);

        DiscordRichPresence rich = new DiscordRichPresence.Builder
                ("In Menu")
                .setDetails("PEPITO RETURNED HOME")
                .setBigImage("menu", "PEPITO RETURNED HOME")
                .setSmallImage("pepito", "PEPITO RETURNED HOME")
                .setStartTimestamps(launchedGameTime)
                .build();

        DiscordRPC.discordUpdatePresence(rich);

        String os_name = System.getProperty("os.name").toLowerCase();

        if (os_name.toLowerCase(Locale.ROOT).contains("win")) {
            gameDirectory = Path.of(System.getenv("APPDATA") + "\\four night pepito");
        } else { //linux момент
            gameDirectory = Path.of(System.getProperty( "user.home" ) + "\\four night pepito");
        }

        Calendar calendar = Calendar.getInstance();

        isPepitoBirthday = calendar.get(Calendar.MONTH) == Calendar.SEPTEMBER && calendar.get(Calendar.DAY_OF_MONTH) == 4;
        isAprilFools = calendar.get(Calendar.MONTH) == Calendar.APRIL && calendar.get(Calendar.DAY_OF_MONTH) == 1;
        isDecember = calendar.get(Calendar.MONTH) == Calendar.DECEMBER;
        
        loadLanguage("english");
        initializeFontMetrics();

        randomX = (short) Math.round(Math.random() * 256 - 512);
        randomY = (short) Math.round(Math.random() * 256 - 512);

        bg[0] = toCompatibleImage(resize(loadImg("/game/cam/empty.png"), 1920, 1440, Image.SCALE_SMOOTH, BufferedImage.TYPE_INT_RGB));

        for(int i = 1; i < 4; i++) {
            bg[i] = toCompatibleImage(resize(loadImg("/menu/bg_" + (i + 1) + ".png"), 1920, 1440, Image.SCALE_SMOOTH, BufferedImage.TYPE_INT_RGB));
        }

        discordStates[0] = toCompatibleImage(loadImg("/menu/discord.png"));
        discordStates[1] = toCompatibleImage(darkify(discordStates[0], 2));
        discord = discordStates[0];

        musicMenuStates[0] = loadImg("/menu/menuDiscIcon.png");
        musicMenuStates[1] = darkify(musicMenuStates[0], 2);
        musicMenu = musicMenuStates[0];
        
        discMap.put("pepito", new PepitoImage("/menu/discs/pepito.png"));
        discMap.put("pepitoButCooler", new PepitoImage("/menu/discs/pepitoButCooler.png"));
        discMap.put("spookers", new PepitoImage("/menu/discs/spookers.png"));

        
        if(menuButtons.size() > 4) {
            moreMenu[0] = toCompatibleImage(loadImg("/utils/thereIsMoreMenu.png"));
            moreMenu[1] = mirror(moreMenu[0], 2);
        }
        
        camStates[0] = toCompatibleImage(resize(loadImg("/game/cam/empty.png"), 1080, 640, Image.SCALE_SMOOTH, BufferedImage.TYPE_INT_RGB));
        camStates[1] = toCompatibleImage(resize(loadImg("/game/cam/pepitoBack.png"), 1080, 640, Image.SCALE_SMOOTH, BufferedImage.TYPE_INT_RGB));
        camStates[2] = toCompatibleImage(resize(loadImg("/game/cam/pepitoOut.png"), 1080, 640, Image.SCALE_SMOOTH, BufferedImage.TYPE_INT_RGB));
        camStates[3] = toCompatibleImage(resize(loadImg("/game/cam/notPepitoBack.png"), 1080, 640, Image.SCALE_SMOOTH, BufferedImage.TYPE_INT_RGB));
        camStates[4] = toCompatibleImage(resize(loadImg("/game/cam/notPepitoOut.png"), 1080, 640, Image.SCALE_SMOOTH, BufferedImage.TYPE_INT_RGB));
        camStates[5] = toCompatibleImage(resize(loadImg("/game/cam/day.png"), 1080, 640, Image.SCALE_SMOOTH, BufferedImage.TYPE_INT_RGB));

        cam1A = toCompatibleImage(resize(loadImg("/game/cam/cam1A.png"), 300, 240, Image.SCALE_SMOOTH));
        astartaCam[0] = toCompatibleImage(loadImg("/game/cam/astartaIn.png"));
        astartaCam[1] = toCompatibleImage(loadImg("/game/cam/astartaOut.png"));
        makiCam = toCompatibleImage(loadImg("/game/cam/maki.png"));
        noSignal = toCompatibleImage(loadImg("/game/cam/no_signal.png"));

        for(int i = 0; i < 8; i++) {
            fadedStaticImg[i] = toCompatibleImage(loadImg("/tvstatic/static_" + (i + 1) + ".png"));
        }
        currentStaticImg = fadedStaticImg[0];

        for(int i = 0; i < 16; i++) {
            hcNoiseImg[i] = toCompatibleImage(loadImg("/noise/" + i + ".png"));
        }
        
        
        for(int i = 1; i < 7; i++) {
            beastImages[i - 1] = new PepitoImage("/game/entities/beast/" + i + ".png");
        }

        for(int i = 1; i < 4; i++) {
            basementDisperse[i - 1] = loadImg("/game/basement/light/basementDisperse" + i + ".png");
        }
        

        officeImg[0] = toCompatibleImage(loadImg("/game/office/office.png"));
        officeImg[1] = toCompatibleImage(loadImg("/game/office/birthdayFullOffice.png"));
        officeImg[2] = toCompatibleImage(loadImg("/game/office/officeOff.png"));
        officeImg[3] = toCompatibleImage(loadImg("/game/basement/basementRender.png"));
        officeImg[4] = toCompatibleImage(loadImg("/game/basement/basementKeyOffice.png"));
        officeImg[5] = toCompatibleImage(loadImg("/game/hydrophobia/background.png"));
        officeImg[6] = toCompatibleImage(loadImg("/game/hydrophobia/bgIndex6.png"));
        officeImg[7] = toCompatibleImage(loadImg("/game/hydrophobia/bgIndex7.png"));
        officeImg[8] = toCompatibleImage(loadImg("/game/hydrophobia/bgIndex8.png"));
        officeImg[9] = toCompatibleImage(loadImg("/game/hydrophobia/bgIndex9.png"));
        officeImg[10] = toCompatibleImage(loadImg("/game/hydrophobia/rewardRoom.png"));
        officeImg[11] = toCompatibleImage(loadImg("/game/hydrophobia/bgIndex11.png"));
        officeImg[12] = toCompatibleImage(loadImg("/game/hydrophobia/hcPrefieldFirst.png"));
        officeImg[13] = toCompatibleImage(loadImg("/game/hydrophobia/hcPrefieldSecond.png"));
        officeImg[14] = toCompatibleImage(loadImg("/game/hydrophobia/hcOffice.png"));
        officeImg[15] = toCompatibleImage(loadImg("/game/hydrophobia/dustonOffice.png"));
        officeImg[16] = toCompatibleImage(loadImg("/game/hydrophobia/hcPrefieldThird.png"));

        BufferedImage sensorIcon = toCompatibleImage(loadImg("/game/items/sensorIcon.png"));
        BufferedImage soggyImage = loadImg("/game/items/soggySubscription.png");
        BufferedImage cornImage = loadImg("/game/items/cornIcon.png");
        BufferedImage sunglassesIcon = loadImg("/game/items/sunglassesIcon.png");
        BufferedImage riftGlitchIcon = loadImg("/game/items/riftGlitch.png");
        BufferedImage weatherStationIcon = loadImg("/game/items/weatherStationIcon.png");
        BufferedImage bingoCardIcon = loadImg("/game/items/bingoCard.png");
        BufferedImage speedrunTimerIcon = loadImg("/game/items/speedrunTimerIcon.png");
        BufferedImage manualIcon = loadImg("/game/items/manualIcon.png");

        metalPipeImg = toCompatibleImage(loadImg("/game/items/metalPipe.png"));
        flashlightImg = toCompatibleImage(loadImg("/game/items/flashlight.png"));
        sodaImg = toCompatibleImage(loadImg("/game/items/SODAA.png"));
        mudseal = toCompatibleImage(loadImg("/game/items/mud seal.png"));
        fanImg[0] = toCompatibleImage(loadImg("/game/items/fan_0.png"));
        fanImg[1] = toCompatibleImage(loadImg("/game/items/fan.png"));
        fanImg[2] = toCompatibleImage(loadImg("/game/items/fanBlade.png"));
        rotatedFanBlade = fanImg[2];
        sensorImg = toCompatibleImage(loadImg("/game/items/sensor.png"));
        freezeImg = toCompatibleImage(loadImg("/game/items/freezePotion.png"));
        planksImg = toCompatibleImage(loadImg("/game/items/planks.png"));
        miniSodaImg = toCompatibleImage(loadImg("/game/items/minisoda.png"));
        maxwellIcon = toCompatibleImage(loadImg("/game/items/maxwellIcon.png"));
        soupItemImg = toCompatibleImage(loadImg("/game/items/soup.png"));
        birthdayHatImg = toCompatibleImage(loadImg("/game/items/birthdayHat.png"));
        birthdayMaxwellIcon = toCompatibleImage(loadImg("/game/items/birthdayMaxwellIcon.png"));
        speedrunTimerImg = toCompatibleImage(loadImg("/game/items/speedrunTimer.png"));
        starlightBottleImg = loadImg("/game/items/starlightBottle.png");
        styroPipeImg = toCompatibleImage(loadImg("/game/items/styroPipe.png"));

        wiresText[0] = toCompatibleImage(loadImg("/game/entities/wires/wiresText.png"));
        wiresText[1] = toCompatibleImage(loadImg("/game/entities/wires/riftText.png"));

        vignette[0] = toCompatibleImage(loadImg("/game/milly/vignette.png"));
        vignette[1] = lightify(vignette[0]);

        jumpscares[0] = new PepitoImage("/game/jumpscares/shadowPepito.png");
        jumpscares[1] = new PepitoImage("/game/jumpscares/pepito.png");
        jumpscares[2] = new PepitoImage("/game/jumpscares/notPepito.png");
        jumpscares[3] = new PepitoImage("/game/jumpscares/astarta2.png");
//        jumpscares[4] = new PepitoImage("/game/jumpscares/MSI.png");
        jumpscares[5] = new PepitoImage("/game/jumpscares/colaJumpscare.png");
        jumpscares[6] = new PepitoImage("/game/jumpscares/maki.png");
        jumpscares[7] = new PepitoImage("/game/jumpscares/sharkJumpscare.png");
        jumpscares[8] = new PepitoImage("/game/jumpscares/sharkJumpscare2.png");
        jumpscares[9] = new PepitoImage("/game/jumpscares/boykisser.png");
        jumpscares[10] = new PepitoImage("/game/jumpscares/lemonade.png");
        jumpscares[11] = new PepitoImage("/game/jumpscares/dread.png");
        jumpscares[12] = new PepitoImage("/game/jumpscares/scaryCat.png");
        jumpscares[13] = new PepitoImage("/game/jumpscares/elAstarta.png");
        jumpscares[14] = new PepitoImage("/game/jumpscares/deepSeaCreatureJumpscare.png");
        jumpscares[15] = new PepitoImage("/game/jumpscares/krunlicJumpscare.png");
        jumpscares[16] = new PepitoImage("/game/jumpscares/shockJumpscare.png");
        jumpscare = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

        adblockerImage = toCompatibleImage(resize(loadImg("/game/entities/a90/adblocker.png"), 200, 200, Image.SCALE_SMOOTH));
        canny = toCompatibleImage(resize(loadImg("/game/entities/a90/canny.png"), 200, 200, Image.SCALE_SMOOTH));
        uncanny[0] = toCompatibleImage(loadImg("/game/entities/a90/uncanny.png"));
        uncanny[1] = toCompatibleImage(redify(uncanny[0]));

        
        fieldCanny = toCompatibleImage(resize(loadImg("/game/hydrophobia/field/a90/canny.png"), 200, 200, Image.SCALE_SMOOTH));
        fieldUncanny[0] = toCompatibleImage(loadImg("/game/hydrophobia/field/a90/uncanny.png"));
        fieldUncanny[1] = toCompatibleImage(loadImg("/game/hydrophobia/field/a90/uncanny2.png"));


        smallAdblockerImage = toCompatibleImage(resize(loadImg("/game/entities/a90/adblocker.png"), 50, 50, Image.SCALE_FAST));

        msiImage[0] = toCompatibleImage(loadImg("/game/entities/msi/MSI.png"));

        if(isAprilFools || isPepitoBirthday) {
            msiImage[1] = toCompatibleImage(mirror(msiImage[0], 1));
        }
        msiImage[2] = toCompatibleImage(redify(msiImage[0]));
        msiImage[3] = toCompatibleImage(loadImg("/game/entities/msi/shadowMSI.png"));

        astartaEyes = toCompatibleImage(loadImg("/game/entities/astarta/astartaEyes.png"));

        astartaSticker = toCompatibleImage(mirror(loadImg("/game/astartaSticker.png"), 1));

        balloonImg = toCompatibleImage(loadImg("/game/office/balloon.png"));

        bingoCardImg = getDefaultBingoCard();
        bingoCompletedImg = toCompatibleImage(loadImg("/menu/bingo/completed.png"));

        itemTags[0] = toCompatibleImage(loadImg("/game/items/rift.png"));
        itemTags[1] = toCompatibleImage(loadImg("/game/items/conflict.png"));
        itemTags[2] = toCompatibleImage(loadImg("/game/items/passive.png"));
        itemTags[3] = toCompatibleImage(loadImg("/game/items/trigger.png"));
        itemTags[4] = toCompatibleImage(loadImg("/game/items/expend.png"));
        itemTags[5] = toCompatibleImage(loadImg("/game/items/special.png"));
        riftImg = toCompatibleImage(loadImg("/game/rift/rift.png"));
        riftFrame = toCompatibleImage(loadImg("/game/rift/frame.png"));
        shadowPortal = toCompatibleImage(loadImg("/game/rift/portal!.png"));
        
        mirrorCatImg = toCompatibleImage(loadImg("/game/entities/mirrorcat/mirrorcat.png"));
        mirrorCage[0] = toCompatibleImage(loadImg("/game/entities/mirrorcat/closedCage.png"));
        mirrorCage[1] = toCompatibleImage(loadImg("/game/entities/mirrorcat/openCage.png"));
        mirrorCatExplode = toCompatibleImage(loadImg("/game/entities/mirrorcat/explosion.png"));

        lockedAchievementImg = toCompatibleImage(loadImg("/menu/achievements/locked.png"));
        achievementMenuArrow = toCompatibleImage(loadImg("/menu/achievements/menuArrow.png"));

        manualImg = toCompatibleImage(loadImg("/game/office/manual.png"));
        manualMissingTextImg = toCompatibleImage(loadImg("/game/office/manualMissingText.png"));

        sastartaTank[0] = toCompatibleImage(mirror(loadImg("/game/entities/astartaBoss/astartaTank.png"), 1));
        sastartaTank[1] = silhouette(sastartaTank[0], Color.BLACK);
        astartaMinecartWhite = silhouette(loadImg("/game/entities/astartaBoss/minecart.png"), Color.WHITE);

        for(int i = 0; i < 12; i++) {
            shockCat[i] = new PepitoImage("/game/entities/shock/monitor/" + (i + 1) + ".png");
        }

        flashlightLayer = toCompatibleImage(loadImg("/game/flashlightLayer.png"));

        randomsog = resize(toCompatibleImage(loadImg("/game/office/randomsog.png")), 1080, 640, Image.SCALE_FAST);
        
        
        BufferedImage shadowTicketIcon = loadImg("/game/items/shadowTicket.png");
        BufferedImage basementKeyIcon = loadImg("/game/items/keyTexture.png");
        BufferedImage hisPictureIcon = loadImg("/game/items/hisPictureIcon.png");
        BufferedImage hisPaintingIcon = loadImg("/game/items/hisPaintingIcon.png");
        BufferedImage shadowblockerIcon = loadImg("/game/items/shadowblocker.png");
        BufferedImage megaSodaIcon = loadImg("/game/items/megaSoda.png");

        soda = new Item(resize(rotate(sodaImg, 45), 100, 100, Image.SCALE_SMOOTH), getString("sodaName"), getString("sodaDesc"), -1, "soda", "S").addTags(List.of(ItemTag.EXPEND));
        flashlight = new Item(resize(flashlightImg, 100, 80, Image.SCALE_SMOOTH), getString("flashlightName"), getString("flashlightDesc"), -1, "flashlight", "Right-Click").addTags(List.of(ItemTag.TRIGGER));
        fan = new Item(resize(rotate(fanImg[0], 0), 100, 100, Image.SCALE_SMOOTH), getString("fanName"), getString("fanDesc"), -1, "fan", "F").addTags(List.of(ItemTag.TRIGGER));
        metalPipe = new Item(itemOffset(resize(metalPipeImg, 100, 20, Image.SCALE_SMOOTH), 0, 70), getString("metalPipeName"), getString("metalPipeDesc"), -1, "metalPipe", "M").addTags(List.of(ItemTag.TRIGGER));
        sensor = new Item(resize(sensorIcon, 110, 110, Image.SCALE_SMOOTH), getString("sensorName"), getString("sensorDesc"), -1, "sensor", "").addTags(List.of(ItemTag.PASSIVE));
        adblocker = new Item(resize(adblockerImage, 100, 100, Image.SCALE_FAST), getString("adblockerName"), getString("adblockerDesc"), 1, "adblocker", "").addTags(List.of(ItemTag.RIFT, ItemTag.PASSIVE));
        maxwell = new Item(resize(maxwellIcon, 110, 100, Image.SCALE_SMOOTH), getString("maxwellName"), getString("maxwellDesc"), 0, "maxwell", "").addTags(List.of(ItemTag.RIFT, ItemTag.PASSIVE));
        freezePotion = new Item(resize(freezeImg, 100, 100, Image.SCALE_SMOOTH), getString("freezeName"), getString("freezeDesc"), 0, "freeze", "I").addTags(List.of(ItemTag.RIFT, ItemTag.EXPEND));
        planks = new Item(resize(planksImg, 120, 90, Image.SCALE_SMOOTH), getString("planksName"), getString("planksDesc"), 0, "planks", "B + door number").addTags(List.of(ItemTag.RIFT, ItemTag.EXPEND));
        miniSoda = new Item(resize(rotate(miniSodaImg, 45), 100, 100, Image.SCALE_SMOOTH), getString("miniSodaName"), getString("miniSodaDesc"), 0, "miniSoda", "D").addTags(List.of(ItemTag.RIFT, ItemTag.EXPEND));
        soup = new Item(resize(soupItemImg, 80, 100, Image.SCALE_SMOOTH), getString("soupName"), getString("soupDesc"), 0, "soup", "U").addTags(List.of(ItemTag.RIFT, ItemTag.EXPEND));
        birthdayMaxwell = new Item(resize(birthdayMaxwellIcon, 110, 100, Image.SCALE_SMOOTH), getString("bMaxwellName"), getString("bMaxwellDesc"), 0, "birthdayMaxwell", "").addTags(List.of(ItemTag.RIFT, ItemTag.SPECIAL));
        birthdayHat = new Item(resize(birthdayHatImg, 100, 100, Image.SCALE_SMOOTH), getString("birthdayHatName"), getString("birthdayHatDesc"), 0, "birthdayHat", "").addTags(List.of(ItemTag.RIFT, ItemTag.SPECIAL));
        bingoCardItem = new Item(bingoCardIcon, getString("bingoCardName"), getString("bingoCardDesc"), 0, "bingoCard", "").addTags(List.of(ItemTag.RIFT));
        starlightBottle = new Item(resize(starlightBottleImg, 110, 110, Image.SCALE_SMOOTH), getString("starlightName"), getString("starlightDesc"), 0, "starlightBottle", "L").addTags(List.of(ItemTag.EXPEND));
        shadowTicket = new Item(resize(shadowTicketIcon, 120, 90, Image.SCALE_SMOOTH), getString("sticketName"), getString("sticketDesc"), 0, "shadowTicket", "").addTags(List.of(ItemTag.SPECIAL));
        styroPipe = new Item(resize(trimImage(styroPipeImg), 110, 110, Image.SCALE_SMOOTH), getString("styroPipeName"), getString("styroPipeDesc"), 0, "styroPipe", "P").addTags(List.of(ItemTag.TRIGGER));
        basementKey = new Item(resize(trimImage(basementKeyIcon), 110, 110, Image.SCALE_SMOOTH), getString("basementKeyName"), getString("basementKeyDesc"), 0, "basementKey", "").addTags(List.of(ItemTag.RIFT, ItemTag.SPECIAL));

        soggyBallpit = new Item(resize(soggyImage, 100, 100, Image.SCALE_SMOOTH), getString("subscriptionName"), getString("subscriptionDesc"), 0, "ballpit", "").addTags(List.of(ItemTag.PASSIVE));
        manual = new Item(manualIcon, getString("manualName"), getString("manualDesc"), 0, "manual", "").addTags(List.of(ItemTag.PASSIVE));
        corn[0] = new Corn(cornImage, getString("cornName"), getString("cornDesc"), 0, "corn", loadImg("/game/items/cornStage1.png"));
        corn[1] = new Corn(cornImage, getString("cornName"), getString("cornDesc"), 0, "corn2", loadImg("/game/items/cornStage1.png"));
        hisPicture = new Item(hisPictureIcon, getString("hisPictureName"), getString("hisPictureDesc"), 0, "hisPicture", "");
        hisPainting = new Item(hisPaintingIcon, getString("hisPaintingName"), getString("hisPaintingDesc"), 0, "hisPainting", "");
        riftGlitch = new Item(riftGlitchIcon, getString("riftGlitchName"), getString("riftGlitchDesc"), 0, "riftGlitch", "").addTags(List.of(ItemTag.RIFT));
        pishPish = new Item(pishPishImg.request(), getString("pishPishName"), getString("pishPishDesc"), 0, "pishPish", "").addTags(List.of(ItemTag.SPECIAL));
        weatherStation = new Item(weatherStationIcon, getString("weatherStationName"), getString("weatherStationDesc"), 0, "weatherStation", "");
        iceBucket = new Item(iceBucketImg.request(), getString("iceBucketName"), getString("iceBucketDesc"), 0, "iceBucket", "T");
        red40 = new Item(red40Img.request(), getString("red40Name"), getString("red40Desc"), 0, "red40", "R");
        shadowblocker = new Item(resize(shadowblockerIcon, 100, 100, Image.SCALE_FAST), getString("shadowblockerName"), getString("shadowblockerDesc"), 0, "shadowblocker", "").addTags(List.of(ItemTag.PASSIVE));
        megaSoda = new Item(megaSodaIcon, getString("megaSodaName"), getString("megaSodaDesc"), 0, "megaSoda", "O").addTags(List.of(ItemTag.TRIGGER));

        soggyPen = new Item(soggyPenImg.request(), getString("soggyPenName"), getString("soggyPenDesc"), 0, "soggyPen", "SHIFT + CLICK");
        
        sunglasses = new Item(itemOffset(sunglassesIcon, 0, 60), getString("sunglassesName"), getString("sunglassesDesc"), 0, "sunglasses", "G").addTags(List.of(ItemTag.TRIGGER, ItemTag.RIFT));
        speedrunTimer = new Item(itemOffset(resize(speedrunTimerIcon, 100, 90, Image.SCALE_SMOOTH), 0, 10), "speedrun timer", "definitely uhh\ncounts down until\nsomething!", 0, "speedrunTimer", "");
        
        birthdayMaxwell.addConflicts(List.of(maxwell, adblocker, shadowTicket, shadowblocker)); // gamemode + two maxwells
        birthdayHat.addConflicts(List.of(adblocker, adblocker, shadowTicket, shadowblocker)); // gamemode
        maxwell.addConflicts(List.of(birthdayMaxwell)); // two maxwells
        adblocker.addConflicts(List.of(shadowblocker, birthdayMaxwell, birthdayHat, shadowTicket, basementKey)); // disabled in these modes + shadowblocker conflict
        shadowblocker.addConflicts(List.of(adblocker, birthdayMaxwell, birthdayHat, shadowTicket, basementKey)); // disabled in these modes + adblocker conflict
        shadowTicket.addConflicts(List.of(birthdayHat, birthdayMaxwell, adblocker, shadowblocker)); // gamemode
        basementKey.addConflicts(List.of(adblocker, shadowblocker)); // gamemode (?)

        birthdayHat.setItemLimitAdd((byte) 1);
        shadowTicket.setItemLimitAdd((byte) 6);
        basementKey.setItemLimitAdd((byte) 1);
        soggyPen.setItemLimitAdd((byte) 1);
        updateItemList();
        
        for(Item item : fullItemList) {
            for(Item conflicted : item.getConflicts()) {
                conflicted.addConflicts(Collections.singleton(item));
            }
        }

        maxwellIcon = resize(maxwellIcon, 180, 150, BufferedImage.SCALE_SMOOTH);
        birthdayMaxwellIcon = resize(birthdayMaxwellIcon, 180, 150, BufferedImage.SCALE_SMOOTH);
        
        musicDiscs.add("pepito");
        
        try {
            if(!Files.exists(gameDirectory)) {
                Files.createDirectory(gameDirectory);
            }
            if(!Files.exists(Path.of(gameDirectory + "\\screenshots"))) {
                Files.createDirectory(Path.of(gameDirectory + "\\screenshots"));
            }

            Path dataPath = Path.of(gameDirectory + "\\data.txt");

            if(Files.exists(dataPath)) {
                byte[] decodedBytes = Base64.getMimeDecoder().decode(readFile(dataPath.toString()));
                String str = new String(decodedBytes);
                String[] array = str.split("\n");

                if(str.contains(":")) {
                    try {
                        winCount = Short.parseShort(Arrays.stream(array).filter(string -> string.startsWith("win:")).findFirst().orElse("0").replace("win:",""));
                        deathCount = Short.parseShort(Arrays.stream(array).filter(string -> string.startsWith("death:")).findFirst().orElse("0").replace("death:",""));
                        currentNight = Byte.parseByte(Arrays.stream(array).filter(string -> string.startsWith("night:")).findFirst().orElse("1").replace("night:",""));
                        volume = Float.parseFloat(Arrays.stream(array).filter(string -> string.startsWith("vol:")).findFirst().orElse("0.8").replace("vol:",""));
                        blackBorders = Arrays.stream(array).filter(string -> string.startsWith("borders:")).findFirst().orElse("0").replace("borders:","").equals("1");

                        headphones = Arrays.stream(array).filter(string -> string.startsWith("headphones:")).findFirst().orElse("0").replace("headphones:","").equals("1");
                        recordEndlessNight = Byte.parseByte(Arrays.stream(array).filter(string -> string.startsWith("record:")).findFirst().orElse("0").replace("record:",""));
                        unlockedBingo = Arrays.stream(array).filter(string -> string.startsWith("bingo:")).findFirst().orElse("0").replace("bingo:","").equals("1");
                        reachedAstartaBoss = Arrays.stream(array).filter(string -> string.startsWith("reachedAstartaBoss:")).findFirst().orElse("0").replace("reachedAstartaBoss:","").equals("1");
                        everEnteredBasement = Arrays.stream(array).filter(string -> string.startsWith("everEnteredBasement:")).findFirst().orElse("0").replace("everEnteredBasement:","").equals("1");
                        gotEndlessNight6AfterAllNighter = Arrays.stream(array).filter(string -> string.startsWith("gotEndlessNight6AfterAllNighter:")).findFirst().orElse("0").replace("gotEndlessNight6AfterAllNighter:","").equals("1");
                        joinedBefore = Arrays.stream(array).filter(string -> string.startsWith("joinedBefore:")).findFirst().orElse("0").replace("joinedBefore:","").equals("1");
                        neonSogSkips = Byte.parseByte(Arrays.stream(array).filter(string -> string.startsWith("neonSogSkips:")).findFirst().orElse("0").replace("neonSogSkips:",""));
                        everGotKrunlicFile = Arrays.stream(array).filter(string -> string.startsWith("everGotKrunlicFile:")).findFirst().orElse("0").replace("everGotKrunlicFile:","").equals("1");
                        menuSong = Arrays.stream(array).filter(string -> string.startsWith("menuSong:")).findFirst().orElse("pepito").replace("menuSong:","");
                        if(!discMap.containsKey(menuSong)) {
                            menuSong = "pepito";
                        }
                        receivedShadowblocker = Arrays.stream(array).filter(string -> string.startsWith("receivedShadowblocker:")).findFirst().orElse("0").replace("receivedShadowblocker:","").equals("1");
                        beatShadownightBasement = Arrays.stream(array).filter(string -> string.startsWith("beatShadownightBasement:")).findFirst().orElse("0").replace("beatShadownightBasement:","").equals("1");
                        seenEndlessDisclaimer = Arrays.stream(array).filter(string -> string.startsWith("seenEndlessDisclaimer:")).findFirst().orElse("0").replace("seenEndlessDisclaimer:","").equals("1");
                        playedAfterBeatingBasement = Arrays.stream(array).filter(string -> string.startsWith("playedAfterBeatingBasement:")).findFirst().orElse("0").replace("playedAfterBeatingBasement:","").equals("1");
                        
                        disclaimer = Arrays.stream(array).filter(string -> string.startsWith("disclaimer:")).findFirst().orElse("1").replace("disclaimer:","").equals("1");
                        showManual = Arrays.stream(array).filter(string -> string.startsWith("showManual:")).findFirst().orElse("1").replace("showManual:","").equals("1");
                        saveScreenshots = Arrays.stream(array).filter(string -> string.startsWith("saveScreenshots:")).findFirst().orElse("1").replace("saveScreenshots:","").equals("1");
                        bloom = Arrays.stream(array).filter(string -> string.startsWith("bloom:")).findFirst().orElse("0").replace("bloom:","").equals("1");
                        fpsCounters[0] = Arrays.stream(array).filter(string -> string.startsWith("fpsCounter:")).findFirst().orElse("0").replace("fpsCounter:","").equals("1");
                        fpsCap = Short.parseShort(Arrays.stream(array).filter(string -> string.startsWith("fpsCap:")).findFirst().orElse("120").replace("fpsCap:",""));
                        thousandFPS = Math.max(1, 1000 / fpsCap);

                        jumpscareShake = Byte.parseByte(Arrays.stream(array).filter(string -> string.startsWith("jumpscareShake:")).findFirst().orElse("0").replace("jumpscareShake:",""));
                        language = Arrays.stream(array).filter(string -> string.startsWith("language:")).findFirst().orElse("english").replace("language:","");
                        screenShake = Arrays.stream(array).filter(string -> string.startsWith("screenShake:")).findFirst().orElse("1").replace("screenShake:","").equals("1");
                        disableFlickering = Arrays.stream(array).filter(string -> string.startsWith("disableFlickering:")).findFirst().orElse("0").replace("disableFlickering:","").equals("1");
                        fullscreen = Arrays.stream(array).filter(string -> string.startsWith("fullscreen:")).findFirst().orElse("0").replace("fullscreen:","").equals("1");

                        String[] discList = Arrays.stream(array).filter(string -> string.startsWith("musicDiscs:")).findFirst().orElse("pepito").replace("musicDiscs:","").split(",");
                        musicDiscs.addAll(List.of(discList));
                        musicDiscs.removeIf(thing -> !discMap.containsKey(thing));
                    } catch (Exception ignored) {
                        System.out.println("LOADING DATA FAILED LOADING DATA FAILED");
                    }
                }
            } else {
                Files.createFile(dataPath);
            }

            loadLanguage(language);
            initializeFontMetrics();
            initializeItemNames();
            reloadMenuButtons();


            Path itemsPath = Path.of(gameDirectory + "\\items.txt");

            if(Files.exists(itemsPath)) {
                byte[] decodedBytes = Base64.getMimeDecoder().decode(readFile(itemsPath.toString()));
                String str = new String(decodedBytes);
                String[] array = str.split("\n");

                if(str.contains(":")) {
                    try {
                        for (Item item : fullItemList) {
                            String cut = Arrays.stream(array)
                                    .filter(string -> string.startsWith(item.getId() + ":"))
                                    .findFirst()
                                    .orElse("0");

                            item.setAmount(Integer.parseInt(cut.replace(item.getId() + ":", "")));

                            if (item.getDefaultAmount() < 0 && item.getAmount() == 0) {
                                item.setAmount(item.getDefaultAmount());
                            }
                        }
                        updateItemList();
                    } catch (Exception ignored) {
                        System.out.println("LOADING ITEMS FAILED LOADING ITEMS FAILED");
                        for (Item item : fullItemList) {
                            item.setAmount(item.getDefaultAmount());
                        }
                    }
                }
            } else {
                Files.createFile(itemsPath);
            }


            Path achievementsPath = Path.of(gameDirectory + "\\achievements.txt");

            if(Files.exists(achievementsPath)) {
                byte[] decodedBytes = Base64.getMimeDecoder().decode(readFile(achievementsPath.toString()));
                String str = new String(decodedBytes);
                String[] array = str.split("\n");

                if(str.contains(":")) {
                    try {
                        int obtainedTotal = 0;

                        for(Achievements achievement : Achievements.values()) {
                            String cut = Arrays.stream(array)
                                    .filter(string -> string.startsWith(achievement.name() + ":"))
                                    .findFirst()
                                    .orElse(achievement.name() + ":0");

                            if(cut.replace(achievement.name() + ":", "").equals("1")) {
                                achievement.obtain();
                                obtainedTotal++;
                            }
                        }
                        float percent = (float) obtainedTotal / Achievements.values().length;
                        int percentToRound = Math.round(percent * 1000);
                        achievementPercentage = percentToRound / 10F;

                    } catch (Exception ignored) {
                        System.out.println("LOADING ACHIEVEMENTS FAILED LOADING ACHIEVEMENTS FAILED");
                    }
                }
            } else {
                Files.createFile(achievementsPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Path statisticsPath = Path.of(gameDirectory + "\\statistics.txt");

        try {
            if(Files.exists(statisticsPath)) {
                byte[] decodedBytes = Base64.getMimeDecoder().decode(readFile(statisticsPath.toString()));
                String str = new String(decodedBytes);
                String[] array = str.split("\n");

                if(str.contains(":")) {
                    try {
                        for(Statistics statistic : Statistics.values()) {
                            String cut = Arrays.stream(array)
                                    .filter(string -> string.startsWith(statistic.toString() + ":"))
                                    .findFirst()
                                    .orElse(statistic.toString() + ":0");

                            int number = Integer.parseInt(cut.replace(statistic + ":", ""));

                            if(number == 0) {
                                switch (statistic) {
                                    case WINS -> statistic.setValue(winCount);
                                    case DEATHS -> statistic.setValue(deathCount);
                                    case ENDLESS -> statistic.setValue(recordEndlessNight);
                                    default -> statistic.setValue(0);
                                }
                            } else {
                                statistic.setValue(number);
                            }
                        }

                    } catch (Exception ignored) {
                        System.out.println("LOADING STATISTICS FAILED LOADING STATISTICS FAILED");
                    }
                }
            } else {
                Files.createFile(statisticsPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if(fullscreen) {
            toFullscreen();
        }
        
        canSave = true;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            save();
            joinedBefore = true;
            System.out.println("program exploded");
        }));

        
        int scaryLogo = (int) (Math.random() * 347);
        isScaryLogo = scaryLogo == 1 && joinedBefore;
        
        
        if(!receivedShadowblocker && Achievements.SHADOWNIGHT.isObtained()) {
            shadowblocker.safeAdd(6);
            receivedShadowblocker = true;
        }
        
        if(Achievements.ALL_NIGHTER.isObtained()) {
            if ((int) (Math.random() * 400) == 1) {
                if (!GamePanel.krunlicMode) {
                    version += "krunlic";
                    initializeFontMetrics();
                    sound.play("krunlicTrigger", 0.2);
                    fadeOutStatic(1, 0.05F, 0.01F);
                    fadeOut(255, 160, 1);
                }
                GamePanel.krunlicMode = true;
            }
        }
        

        CustomNight.addNewEnemy(new CustomNightEnemy("pepitoCn", "pepito", 0));
        CustomNight.addNewEnemy(new CustomNightEnemy("notPepitoCn", "notPepito", 1));
        CustomNight.addNewEnemy(new CustomNightEnemy("glitcherCn", "glitcher", 2));
        CustomNight.addNewEnemy(new CustomNightEnemy("uncannyCatCn", "uncanny", 3));
        CustomNight.addNewEnemy(new CustomNightEnemy("msiCn", "msi", 4));
        CustomNight.addNewEnemy(new CustomNightEnemy("astartaCn", "astarta", 5));
        CustomNight.addNewEnemy(new CustomNightEnemy("sharkCn", "shark", 6));
        CustomNight.addNewEnemy(new CustomNightEnemy("boykisserCn", "boykisser", 7));
        CustomNight.addNewEnemy(new CustomNightEnemy("colaCatCn", "colaCat", 8));
        CustomNight.addNewEnemy(new CustomNightEnemy("mirrorCatCn", "zazu", 9));
        CustomNight.addNewEnemy(new CustomNightEnemy("makiCn", "maki", 10));
        CustomNight.addNewEnemy(new CustomNightEnemy("lemonadeCatCn", "lemonadeCat", 11));
        CustomNight.addNewEnemy(new CustomNightEnemy("wiresCn", "wires", 12));
        CustomNight.addNewEnemy(new CustomNightEnemy("scaryCatCn", "scaryCat", 13));
        CustomNight.addNewEnemy(new CustomNightEnemy("jumpscareCatCn", "jumpscareCat", 14));
        CustomNight.addNewEnemy(new CustomNightEnemy("elAstartaCn", "elAstarta", 15));
        CustomNight.addNewEnemy(new CustomNightEnemy("deepSeaCreatureCn", "deepSeaCreature", 16));
        CustomNight.addNewEnemy(new CustomNightEnemy("shockCn", "shock", 17));
        
//        CustomNight.addNewEnemy(new CustomNightEnemy("hydrophobiaCn", "hydrophobia", 18));
//        CustomNight.addNewEnemy(new CustomNightEnemy("beastCn", "beast", 19));
//        CustomNight.addNewEnemy(new CustomNightEnemy("overseerCn", "overseer", 20));
//        CustomNight.addNewEnemy(new CustomNightEnemy("a120Cn", "a120", 21));
        
        CustomNight.addNewModifier(new CustomNightModifier("powerOutageCn", "powerOutage"));
        CustomNight.addNewModifier(new CustomNightModifier("blizzardCn", "blizzard"));
        CustomNight.addNewModifier(new CustomNightModifier("timersCn", "timers"));
        CustomNight.addNewModifier(new CustomNightModifier("fogCn", "fog"));
        CustomNight.addNewModifier(new CustomNightModifier("radiationCn", "radiation"));
        CustomNight.addNewModifier(new CustomNightModifier("rainCn", "rain"));


        final InvestigationPaper paperParty = new InvestigationPaper("party", 653, 135, () -> {});
        paperParty.setCheckForUnlock(() -> {
            paperParty.unlocked = false;
            paperParty.languageId = "invstgUnknown";
            if(Statistics.ENDLESS.getValue() >= 3) {
                paperParty.languageId = "invstgParty1";
            }
            if(birthdayHat.getAmount() != 0) {
                paperParty.languageId = "invstgParty2";
            }
            if(Achievements.PARTY.isObtained()) {
                paperParty.languageId = "invstgParty3";
            }
            if(Statistics.GOTTEN_BURN_ENDING.getValue() > 0) {
                paperParty.languageId = "invstgDone";
                paperParty.unlocked = true;
            }
        });
        Investigation.list.add(paperParty);

        final InvestigationPaper paperShadow = new InvestigationPaper("shadow", 488, 268, () -> {});
        paperShadow.setCheckForUnlock(() -> {
            paperShadow.unlocked = false;
            paperShadow.languageId = "invstgUnknown";
            if(Statistics.ENDLESS.getValue() >= 5) {
                paperShadow.languageId = "invstgShadow1";
            }
            if(shadowTicket.getAmount() != 0) {
                paperShadow.languageId = "invstgShadow2";
            }
            if(Statistics.GOTTEN_VOID_ENDING.getValue() > 0) {
                paperShadow.languageId = "invstgDone";
                paperShadow.unlocked = true;
            }
        });
        Investigation.list.add(paperShadow);

        final InvestigationPaper paperBasement = new InvestigationPaper("basement", 662, 453, () -> {});
        paperBasement.setCheckForUnlock(() -> {
            paperBasement.unlocked = false;
            paperBasement.languageId = "invstgUnknown";
            if(Statistics.ENDLESS.getValue() >= 6) {
                paperBasement.languageId = "invstgBasement1";
            }
            if(basementKey.getAmount() != 0) {
                paperBasement.languageId = "invstgBasement2";
            }
            if(Statistics.GOTTEN_BASEMENT_ENDING.getValue() > 0) {
                paperBasement.languageId = "invstgBasement3";
            }
            if(Statistics.GOTTEN_CORN_ENDING.getValue() > 0) {
                paperBasement.languageId = "invstgDone";
                paperBasement.unlocked = true;
            }
        });
        Investigation.list.add(paperBasement);

        final InvestigationPaper paperHydro = new InvestigationPaper("hydrophobia", 860, 491, () -> {});
        paperHydro.setCheckForUnlock(() -> {
            paperHydro.unlocked = false;
            paperHydro.languageId = "invstgUnknown";
            if(Statistics.GOTTEN_BASEMENT_ENDING.getValue() > 0) {
                paperHydro.languageId = "invstgHydrophobia1";
            }
            if(Achievements.HYDROPHOBIA.isObtained()) {
                paperHydro.languageId = "invstgHydrophobia2";
            }
            if(Achievements.EXIT.isObtained()) {
                paperHydro.languageId = "invstgDone";
                paperHydro.unlocked = true;
            }
        });
        Investigation.list.add(paperHydro);

        final InvestigationPaper paperStorm = new InvestigationPaper("perfectStorm", 208, 365, () -> {});
        paperStorm.setCheckForUnlock(() -> {
            paperStorm.unlocked = false;
            paperStorm.languageId = "invstgUnknown";
            if(Achievements.GRUGGENHEIMED.isObtained()) {
                paperStorm.languageId = "invstgStorm1";
            }
            if(Achievements.PERFECT_STORM.isObtained()) {
                paperStorm.languageId = "invstgDone";
                paperStorm.unlocked = true;
            }
        });
        Investigation.list.add(paperStorm);
        

        onResizeEvent();
        recalculateButtons(GameState.MENU);
        
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        executor.schedule(() -> {
            if(isPepitoBirthday) {
                sound.playBirthdayHorn();
                executor.schedule(this::launch, 4, TimeUnit.SECONDS);
            } else {
                if(disclaimer) {
                    fadeOut(255, 0, 2);
                    state = GameState.DISCLAIMER;
                } else {
                    launch();
                }
            }
        }, 200, TimeUnit.MILLISECONDS);

        while (thread != null) {
            try {
                try {
                    update();
                } catch (Exception e) {
                    e.printStackTrace();
//                    wait7Seconds(700);
                    System.out.println("UPDATE(); ERROR");
                    System.out.println("- still the same UPDATE(); ERROR");
                    System.out.println("- STILL the same UPDATE(); ERROR");
                }
                
                if(isFocused) {
                    repaint(0, 0, getWidth(), getHeight());
                }
                Thread.sleep(thousandFPS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (thread == null) {
            try {
                audioPlayer.stop();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
    
    boolean canSave = false;

    YTVideoButton pepVoteButton = null;
    boolean hoveringPepVoteButton = false;
    boolean ytAPILoaded = false;
    
    void launch() {
        reloadMenuButtons();
        state = GameState.MENU;
        loading = false;
        fadeOut(255, 160, 1);
        music.play(menuSong, 0.15, true);
        
//        loadYoutubeVideos();
    }



    int itemLimit = 3;

    void updateItemList() {
        fullItemList = List.of(soda, flashlight, fan, metalPipe, sensor, adblocker, maxwell, freezePotion, planks, miniSoda, soup, birthdayHat, bingoCardItem,
                birthdayMaxwell, soggyBallpit, manual, corn[0], corn[1], hisPicture, hisPainting, shadowTicket, shadowblocker, megaSoda, speedrunTimer, starlightBottle,
                sunglasses, styroPipe, basementKey, riftGlitch, pishPish, weatherStation, soggyPen, iceBucket, red40);
        itemList = getItemsWithAmount();

        Set<Item> conflicts = new HashSet<>();
        for(Item item : itemList) {
            if(item.isSelected()) {
                conflicts.addAll(item.getConflicts());
            }
        }
        for(Item item : itemList) {
            item.setMarkedConflicting(conflicts.contains(item));
        }

        if(itemList.size() >= 8) {
            rows = 3;

            if(itemList.size() >= 12) {
                rows = (byte) ((byte) (itemList.size() / columns) + 1);
            }
        } else {
            rows = 2;
        }
    }

    private static void writeFile(Path path, String text) throws IOException {
        FileWriter writer = new FileWriter(path.toString(), false);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);

        bufferedWriter.write(text);

        bufferedWriter.close();
    }

    private static String readFile(String path) {
        StringBuilder string = new StringBuilder();
        try {
            FileReader reader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                string.append(line).append("\n");
            }
            reader.close();

        } catch (IOException ignored) { }

        return string.toString();
    }

    private float scrollX = 256;
    private float scrollY = 256;

    float staticTransparency = 0.05F;

    private short randomX = 0;
    private short randomY = 0;

    public short endFade = 200;
    private float intervalFade = 1;

    private boolean vertical = false; //false = horizontal

    int seconds = 0;
    boolean paused = false;
    boolean pauseDieSelected = false;
    boolean loading = true;

    float endStatic = 0.05F;
    float intervalStatic = 0.05F;


    public short soggyBallpitCap = 120;
    public boolean soggyBallpitActive = false;
    private float maxwellCounter = 0;

    private long lastTimeNanos = 0;
    private long totalFixedUpdates = 0;
    public int fixedUpdatesAnim = 0;
    
    private void update() {
        if (lastTimeNanos <= 0) {
            lastTimeNanos = System.nanoTime();
            return;
        }

        upscnt.frame();
        
        float delta = (System.nanoTime() - lastTimeNanos) * 0.000001F;
        delta *= universalGameSpeedModifier;
        
        for(short i = 0; i < StaticLists.timers.size(); i++) {
            if(StaticLists.timers.get(i) != null) {
                Pepitimer timer = StaticLists.timers.get(i);

                timer.decrease(delta);
            }
        }

        fixedUpdate(delta);

        if(state == GameState.UNLOADED) {
            lastTimeNanos = System.nanoTime();
            return;
        }

        
        untilNextCheck -= delta;

        if(untilNextCheck <= 0) {
            System.out.println("done a check");
            if(countersAlive2[0] && countersAlive2[1] && countersAlive2[2] && countersAlive2[3]) {
                countersAlive2 = new boolean[] {false, false, false, false};
                untilNextCheck = 10000;
                System.out.println("check alive");
            } else {
                if(state != GameState.KRUNLIC) {
                    System.out.println(Arrays.toString(countersAlive2));
                    wait7Seconds(700);
                    System.out.println("dead");
                }
            }
        }
        
        if (state == GameState.CUTSCENE) {
            currentCutscene.milliseconds += (long) delta;
        }
        if (starlightMillis > 0) {
            starlightMillis = (int) Math.max(0, starlightMillis - delta);
        }
        if(krunlicPhase == 6) {
            state = GameState.KRUNLIC;
        }
        if(state == GameState.GAME) {
            if(night.env() instanceof Basement basement) {
                if(basement.getStage() == 6) {
                    basement.setGasLeakWobble(basement.getGasLeakWobble() + 0.000375F * delta);
                }
            }
        }
        if(state == GameState.CORNFIELD) {
            cornField3D.update(delta);
            if(cornField3D.player.y > 500 && !cornField3D.displayHydro) {
                setCursor(keyHandler.defaultCursor);
                sound.stop();
                keyHandler.holdingW = false;
                keyHandler.holdingS = false;
                keyHandler.holdingD = false;
                keyHandler.holdingA = false;
                cornField3D.displayHydro = true;
                
                new Pepitimer(() -> {
                    stopGame(true);
                }, 2000);
            }
        }

        lastTimeNanos = System.nanoTime();
    }

    ExecutorService fixedUpdateExecutor = Executors.newSingleThreadExecutor();

    double quota = 0;
    // updates every 0.016s
    public void fixedUpdate(double delta) {
        quota += delta;
        if(quota < 16.666666)
            return;
        quota -= 16.666666;
        
        Thread threadA = new Thread(() -> {
            fixedUpdateExecutor.submit(this::actualFixedUpdate); // Delegate call to ClassB's thread
        });
        threadA.start();

        if(quota >= 16.666666) {
            fixedUpdate(0);
        }
    }
    
    public void actualFixedUpdate() {
        fupscnt.frame();

        if(state != GameState.UNLOADED && state != GameState.BATTERY_SAVER) {
            for (byte i = 0; i < StaticLists.notifs.size(); i++) {
                Notification notification = StaticLists.notifs.get(i);
                notification.go();
            }
            for (byte i = 0; i < StaticLists.achievementNotifs.size(); i++) {
                AchievementNotification notification = StaticLists.achievementNotifs.get(i);
                notification.progress();
            }

            if (startFade >= endFade) {
                if (tintAlpha > endFade) {
                    redrawBHO = true;
                    tintAlpha = mathRound(Math.max(0, Math.min(255, tintAlpha - intervalFade)));

                    if (tintAlpha < endFade) {
                        tintAlpha = endFade;
                    }
                }
            } else {
                if (tintAlpha < endFade) {
                    redrawBHO = true;
                    tintAlpha = mathRound(Math.max(0, Math.min(255, tintAlpha + intervalFade)));

                    if (tintAlpha > endFade) {
                        tintAlpha = endFade;
                    }
                }
            }
            if (currentFlicker != goalFlicker) {
                if(disableFlickering) {
                    currentFlicker = (currentFlicker * 29 + goalFlicker) / 30;
                } else {
                    currentFlicker = (currentFlicker + goalFlicker) / 2;
                }
            }

            if (staticTransparency != endStatic) {
                staticTransparency = Math.max(endStatic, staticTransparency - intervalStatic);
            }

            if (currentLeftPan > 0) {
                currentLeftPan = Math.max(0, currentLeftPan - 2);
            }
            if (currentRightPan > 0) {
                currentRightPan = Math.max(0, currentRightPan - 2);
            }

            totalFixedUpdates++;
            fixedUpdatesAnim++;
            if (fixedUpdatesAnim > 216000) {
                fixedUpdatesAnim = 0;
            }


            for (String string : everyFixedUpdate.keySet().stream().toList()) {
                if (everyFixedUpdate.containsKey(string)) {
                    Runnable r = everyFixedUpdate.get(string);

                    try {
                        r.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if(staticTransparency != endStatic) {
                try {
                    redrawCurrentStaticImg();
                } catch (Exception ignored) {
                }
            }


            switch (state) {
                case GAME -> {
                    if(holdingFlashlightFrames == 24) {
                        sound.play("flashlightSwitch", 0.1);

                        if(!flashlightOn) {
                            usage++;
                        } else {
                            usage--;
                            goalFlashlightBrightness = 0;
                            flashlightBrightness /= 3F;
                        }
                        flashlightOn = !flashlightOn;
                        
                        redrawUsage();
                    }
                    if(keyHandler.holdingFlashlight) {
                        holdingFlashlightFrames++;
                    }
                    if(flashlightOn) {
                        goalFlashlightBrightness = (goalFlashlightBrightness * 5F + 100) / 6F;
                        if(Math.random() < 0.03) {
                            goalFlashlightBrightness = Math.max(0, goalFlashlightBrightness - 40);
                        }
                    }
                    flashlightBrightness = (flashlightBrightness * 2F + goalFlashlightBrightness) / 3F;
                    
                    
                    
                    if(night.getA90() != null) {
                        if (night.getA90().margin > 0) {
                            night.getA90().margin /= 2;
                        }
                    }
                    if(night.getAstarta().isActive() && night.getAstarta().animation < 33) {
                        if(night.getAstarta().animation < 20) {
                            night.getAstarta().animation += 2;
                        } else {
                            night.getAstarta().animation++;
                        }
                    }
                    if(night.getElAstarta().isActive()) {
                        if (night.getElAstarta().isKindaActive() && night.getElAstarta().animation < 33) {
                            if (night.getElAstarta().animation < 20) {
                                night.getElAstarta().animation += 4;
                            } else {
                                night.getElAstarta().animation++;
                            }
                        }
                    }
                    if(night.getMSI().isActive()) {
                        if(night.getMSI().getAdditionalTint() > 0) {
                            night.getMSI().setAdditionalTint((short) (night.getMSI().getAdditionalTint() - 1));
                            night.getMSI().setAdditionalTint((short) Math.round(night.getMSI().getAdditionalTint() * 0.9925F));
                        }
                    }
                    if(night.getGlitcher().isEnabled()) {
                        if(night.getGlitcher().visualFormTicks > 0) {
                            night.getGlitcher().visualFormTicks--;
                        }
                    }

                    if(night.megaSodaLightsOnTicks > 0) {
                        night.megaSodaLightsOnTicks--;
                    }
                    if(night.getShadowblocker().state == 3) {
                        if(night.getShadowblocker().progress > 0) {
                            night.getShadowblocker().progress -= 0.01F;
                        } else {
                            night.getShadowblocker().progress = 0;
                            int offset = offsetX - night.env.maxOffset();
                            int x = 396;
                            while(x < 684) {
                                int y = 140;
                                while(y < 503) {
                                    night.getShadowblocker().particles.add(new ShadowParticle(x - offset, y, offset));
                                    y += 4;
                                }
                                x += 4;
                            }
                            night.getShadowblocker().state = 4;
                            new Pepitimer(() -> {
                                night.getShadowblocker().state = 5;

                                Enemy en = night.getEnemies()[night.getShadowblocker().selected];
                                en.setAILevel(0);
                                en.fullReset();

                                switch (night.getShadowblocker().selected) {
                                    case 0 -> {
                                        night.getPepito().scare();
                                        night.getPepito().setPepitoAI((byte) 0);
                                    }
                                    case 1 -> {
                                        night.getPepito().scare();
                                        night.getPepito().setNotPepitoAI((byte) 0);
                                    }
                                }
                            }, 90);
                        }
                    }

                    if(night.isRadiationModifier()) {
                        Point rescaledPoint = new Point((int) ((keyHandler.pointerPosition.x - centerX) / widthModifier), (int) ((keyHandler.pointerPosition.y - centerY) / heightModifier));

                        boolean isIrradiated = false;
                        for(GruggyCart cart : night.gruggyCarts) {
                            float d = Math.min(1.5F, cart.getAddX() / 20F);
                            cart.setCurrentX(Math.max(0, Math.min(1080, cart.getCurrentX() + d)));
                            cart.setAddX(cart.getAddX() - d);

                            if(manualFirstButtonHover || manualSecondButtonHover || night.gruggyX < 1000)
                                continue;

                            Point point = new Point((int) (offsetX - 400 + cart.getCurrentX() + 197), 445 - waterLevel());

                            if(point.distance(rescaledPoint) < 290) {
                                isIrradiated = true;
                                night.setRadiation(night.getRadiation() + 0.17F);
                            }
                        }
                        if(isIrradiated && !night.isIrradiated) {
                            radiationCursor = 1;
                        }
                        night.isIrradiated = isIrradiated;

                        if(night.getRadiation() > 0) {
                            night.setRadiation(Math.max(0, night.getRadiation() - 0.012F));

                            if(night.getRadiation() > 100) {
                                jumpscare("radiation", night.getId());
                            }
                        }
                        if(night.gruggyX < 1010) {
                            night.gruggyX += 2;
                        }
                    }

                    if(night.startUIFade < 810) {
                        night.startUIFade++;
                    }
                    for(int i = 0; i < batteryRegenIcons.size(); i++) {
                        int value = batteryRegenIcons.get(i) + 1;
                        batteryRegenIcons.set(i, value);

                        if(value > 60) {
                            batteryRegenIcons.remove(i);
                            i -= 1;
                        }
                    }

                    
                    Overseer overseer = night.getOverseer();
                    
                    if(overseer.isActive()) {
                        if(overseer.getHeight() < 500) {
                            overseer.setHeight(overseer.getHeight() + 3);

                            if(overseer.getHeight() > 500) {
                                overseer.setHeight(500);
                            }
                        }

                        if(overseer.getHeight() > 400) {
                            overseer.addRadius(-12);

                            int pos = offsetX - night.env().maxOffset() + overseer.getX();

                            if (pos > -200 && pos < 1080 && !inLocker) {
                                overseer.addRage(0.006F);

                                if(overseer.getRage() > 1.1F) {
                                    jumpscare("overseer", night.getId());
                                } else {
                                    HChamber ch = (HChamber) night.env();
                                    night.redrawHChamberTimer(ch);
                                    ch.timerText = bloom(ch.timerText);
                                }

                                if(overseer.beepTimer == null) {
                                    overseer.beepTimer = new RepeatingPepitimer(() -> {
                                        if(!night.getEvent().isInGame() || night.getOverseer() != overseer) {
                                            overseer.stopTimer();
                                            return;
                                        }

                                        sound.play("overseerAlert", Math.min(0.27F, 0.07F + overseer.getRage() / 10F));
                                        overseer.setRadius(450);
                                        
                                        overseer.setUntilRelocation(overseer.getUntilRelocation() + 1);
                                        
                                        HChamber env = (HChamber) night.env();
                                        if(env.getShake() < 55) {
                                            env.setShake(env.getShake() + 15 * (overseer.getRage() + 1));
                                        }
                                    }, 0, 1000);
                                }
                            } else {
                                night.getOverseer().addRage(-0.003F);
                                night.getOverseer().stopTimer();
                            }
                        }

                        if(night.getOverseer().getRage() > 0.3F) {
                            HChamber env = (HChamber) night.env();
                            if(env.getShake() < 13 && night.getEvent().isInGame()) {
                                env.setShake(env.getShake() + 1);
                            }
                        }
                    } else {
                        night.getOverseer().stopTimer();
                    }


                    if(night.env() instanceof Basement env) {
                        if (env.isMillyVisiting()) {
                            if(Math.abs(env.getMillyX() - env.getMillyGoalX()) > 2) {
                                float newX = (44F * env.getMillyX() + env.getMillyGoalX()) / 45F;

                                env.setMillyX(newX);
                            }
                        }
                        if(env.getVent() == 1) {
                            if(env.getVentProgress() < 1) {
                                env.setVentProgress(env.getVentProgress() + 0.3F);
                                env.setVentProgress((2 * env.getVentProgress() + 1) / 3F);

                                if(env.getVentProgress() > 1) {
                                    env.setVentProgress(1);
                                    env.setVent((byte) 2);
                                    repaintOffice();
                                    env.setShake(45);
                                    sound.play("metalPipe", 0.06);

                                    List<Point> poly = List.of(new Point(759, 400), new Point(854, 400), new Point(854, 484), new Point(759, 484));
                                    night.doors.put(4, new Door(new Point(709, 395), basementDoor5Img, new Point(759, 400), getPolygon(poly), new Point(768, 452)));
                                    night.doors.get(4).setVisualSize(0.7F);
                                }
                            }
                        }
                        if(env.isSparking()) {
                            env.sparkSize += 0.1F;
                            env.sparkSize *= 1.17F;

                            if(env.sparkSize > 2) {
                                env.setSparking(false);
                            }
                        }

                        switch (env.getStage()) {
                            case 4 -> {
                                if(env.getMonitorHeight() < 115) {
                                    env.setMonitorHeight(env.getMonitorHeight() + 0.5F);
                                    if(env.getMonitorHeight() > 115) {
                                        env.setMonitorHeight(115);
                                    }
                                }
                            }
                            case 6 -> {
                                if(env.getConnectProgress() < 1) {
                                    env.setConnectProgress(env.getConnectProgress() + 0.0008F);
                                    if(env.getConnectProgress() > 1) {
                                        env.setConnectProgress(1);
                                        sound.play("connectedToFacility", 0.1);
                                    }
                                }
                                night.redrawBasementScreen();

                                if (!env.doWiresWork()) {
                                    env.setOverseerMove(env.getOverseerMove() + 0.0025F);

                                    if(env.getOverseerMove() > 1.065F) {
                                        if (night.getMSI().isActive()) {
                                            if(!night.getMSI().killed) {
                                                night.getMSI().kill(false, false);
                                            }
                                        }
                                    }
                                }
                            }
                            case 7 -> {
                                if(env.getWhiteScreen() > 0) {
                                    env.setWhiteScreen(env.getWhiteScreen() - 0.25F);
                                    if(env.getWhiteScreen() < 0) {
                                        env.setWhiteScreen(0);
                                    }
                                }
                                if(basementLadderHeld) {
                                    basementLadderFrames++;
                                    
                                    if(basementLadderFrames > 480) {
                                        basementLadderFrames = 0;
                                        basementLadderHeld = false;
                                        basementLadderHovering = false;
                                        
                                        fadeOut(200, 0, 1);
                                        stopAllSound();
                                        fadeOutStatic(0, 0, 0);

                                        music.play("endOfYourJourney", 0.15);

                                        basementHyperOptimization = false;

                                        Cutscene cutscene = Presets.basementPreset(this);
                                        currentCutscene = cutscene;
                                        state = GameState.CUTSCENE;
                                        
                                        Statistics.GOTTEN_BASEMENT_ENDING.increment();
                                        
                                        new Pepitimer(() -> {
                                            fadeOut(200, 0, 1);
                                            cutscene.nextScene();
                                        }, 12800);

                                        new Pepitimer(() -> {
                                            fadeOut(200, 0, 1);
                                            cutscene.nextScene();
                                        }, 25300);

                                        new Pepitimer(() -> {
                                            fadeOut(200, 0, 1);
                                            cutscene.nextScene();
                                        }, 38400);

                                        new Pepitimer(() -> {
                                            fadeOut(200, 0, 1);
                                            cutscene.nextScene();
                                        }, 51200);

                                        new Pepitimer(() -> fadeOut(0, 255, 0.3F), 64000);

                                        new Pepitimer(() -> {
                                            cutscene.nextScene();
                                            fadeOut(0, 0, 0);
                                        }, 76000);

                                        new Pepitimer(() -> {
                                            state = GameState.GAME;
                                            win();
                                        }, 78000);
                                    }
                                }
                            }
                        }

                        if(env.getGasLeakMillis() > 0) {
                            env.setGasLeakMillis(env.getGasLeakMillis() - 16);
                        }
                        if(env.getShake() > 0) {
                            env.setShake(env.getShake() - 0.5F);
                        }
                    }
                    if(night.getType() == GameType.HYDROPHOBIA) {
                        if (hcNoise != 15) {
                            hcNoise++;
                        } else {
                            hcNoise = 0;
                        }

                        HChamber env = (HChamber) night.env();

                        if(inLocker) {
                            env.lockerGuidelineAlpha--;
                        } else {
                            env.cameraGuidelineAlpha--;
                        }
                        if(env.isInDustons()) {
                            if(tintAlpha < 160) {
                                env.cockroachX += 2;
                            }
                        }

                        if(env.getWobbleFade() > 0) {
                            env.setWobbleFade(env.getWobbleFade() - 1);
                        }
                        if(env.getCompassRotation() != env.getGoalCompassRotation()) {
                            int difference = (int) ((env.getCompassRotation() * 5F + env.getGoalCompassRotation()) / 6F) - env.getCompassRotation();
                            difference = Math.max(difference, 1);
                            env.setCompassRotation(env.getCompassRotation() + difference);
                        }
                        if(env.getShake() > 0) {
                            env.setShake(env.getShake() - 0.5F);
                        }

                        if(!env.coffeeParticles.isEmpty()) {
                            synchronized (env.coffeeParticles) {
                                ListIterator<CoffeeParticle> iter = env.coffeeParticles.listIterator();
                                while (iter.hasNext()) {
                                    CoffeeParticle particle = iter.next();
                                    particle.floatUp();
                                    if (particle.getY() < 0) {
                                        iter.remove();
                                    }
                                }
                            }
                        }
                    } else if(type == GameType.DAY) {
                        if(night.getNeonSogBall() != null) {
                            NeonSogBall ball = night.getNeonSogBall();
                            float dt = 0.016F * 8F;

                            ball.vY += ball.g * 2 * dt;
                            ball.h += ball.vY * dt;

                            if(ball.h > 570) {
                                ball.vY = -ball.vY / 1.25F;
                                ball.h = 570;

                                if(Math.abs(ball.vY) > 1.5) {
                                    sound.play("neonSogBallBounce", 0.1);
                                }
                            }

                            ball.vX += ball.pX * dt;
                            ball.pX /= 1.05F;
                            ball.vX /= 1.05F;
                            ball.x += ball.vX * dt;
                        }
                    }
                    if(!night.glassParticles.isEmpty()) {
                        synchronized (night.glassParticles) {
                            ListIterator<GlassParticle> iter = night.glassParticles.listIterator();
                            while(iter.hasNext()) {
                                GlassParticle particle = iter.next();
                                float dt = 0.016F * 4;

                                particle.vY += (float) (particle.g * 2 * dt + Math.random());
                                particle.y += particle.vY * dt;

                                if (particle.y > 640) {
                                    iter.remove();
                                }
                                particle.vX += particle.pX * dt;
                                particle.pX /= 1.05F;
                                particle.vX /= 1.05F;
                                particle.x += particle.vX * dt * 0.6F;
                            }
                        }
                    }

                    if(night.getShadowblocker().state == 5) {
                        synchronized(night.getShadowblocker().particles) {
                            if (!night.getShadowblocker().particles.isEmpty()) {
                                ListIterator<ShadowParticle> iter = night.getShadowblocker().particles.listIterator();
                                while(iter.hasNext()){
                                    ShadowParticle particle = iter.next();

                                    particle.x += particle.vX;
                                    particle.y += particle.vY;
                                    particle.alpha += particle.vAlpha;

                                    if (particle.alpha <= 0 || particle.y > 640 || particle.y < -4 || particle.x > 1480 || particle.x < -4) {
                                        iter.remove();
                                    }
                                }
                            }
                        }
                    }


                    if(maxwell.isEnabled()) {
                        if(night.getType().isEndless()) {
                            maxwellCounter += 0.1F + 0.1F * (endless.getNight() - 2);
                        } else if(night.getType() == GameType.CLASSIC) {
                            maxwellCounter += 0.1F + 0.1F * (currentNight - 1);
                        }
                    }
                    if(birthdayMaxwell.isEnabled()) {
                        maxwellCounter += 0.1F;
                    }

                    float doorSpeed = 0.2F;
                    if(currentWaterLevel < 0) doorSpeed = 0.05F; // dsc

                    for(Door door : night.getDoors().values()) {
                        if(door.isClosed()) {
                            if (door.getPercentClosed() < 1) {
                                door.setPercentClosed(door.getPercentClosed() + doorSpeed);
                                redrawBHO = true;
                            }
                        } else {
                            if (door.getPercentClosed() > 0) {
                                door.setPercentClosed(door.getPercentClosed() - doorSpeed);
                                redrawBHO = true;
                            }
                        }
                    }

                    if (!inCam) {
                        if (night.getEvent().isGuiEnabled()) {
                            if (announcerOn)
                                announceCounter++;
                            if(challengerAlpha > 0)
                                challengerAlpha--;
                        }

                        boolean misterHeld = true;
                        if(night.getAstartaBoss() != null) {
                            if(night.getAstartaBoss().getMister() != null) {
                                misterHeld = !night.getAstartaBoss().getMister().isBeingHeld();
                            }
                        }
                        if (keyHandler.mouseHeld && !keyHandler.isRightClick && misterHeld && !night.lockedIn) {
                            if(night.getKiji().getState() == 0 && night.getEvent().isInGame() && !keyHandler.holdingShift) {
                                byte l = (byte) (mirror ? 10 : -10);
                                int maxOffset = night.env().maxOffset();

                                if(currentWaterLevel < 0) {
                                    l /= 2; // dsc

                                    if(night.getDsc().isActive()) {
                                        night.getDsc().moved = true;
                                    }
                                }


                                if (keyHandler.pointerPosition.getX() > 540 * widthModifier + centerX) {
                                    offsetX = (short) Math.min(maxOffset, Math.max(0, offsetX + l));
                                } else {
                                    offsetX = (short) Math.min(maxOffset, Math.max(0, offsetX - l));
                                }
                                redrawBHO = true;


                                if(night.getEvent() == GameEvent.BASEMENT_KEY) {
                                    if(offsetX >= 3030) {
                                        offsetX -= 3030;
                                    } else if(offsetX <= 0) {
                                        offsetX += 3030;
                                    }
                                }
                                if(night.getType() == GameType.HYDROPHOBIA) {
                                    HChamber ch = (HChamber) night.env();
                                    ch.setHoveringConditioner(false);

                                } else if(night.getType().isBasement()) {
                                    if(offsetX > 100) {
                                        Basement env = (Basement) night.env();
                                        if (env.rumbleSog != null) {
                                            new Pepitimer(() -> {
                                                if (env.rumbleSog != null) {
                                                    env.rumbleSog.cancel(true);
                                                    env.rumbleSog = null;

                                                    for(MediaPlayer player : sound.clips) {
                                                        if(player.getMedia().getSource().contains("shitscream.mp3")) {
                                                            player.stop();
                                                            player.dispose();
                                                            sound.clips.remove(player);
                                                        }
                                                    }
                                                }
                                            }, 400);
                                        }
                                    }
                                }
//                        if(night.getScaryCat().isActive()) {
//                            int thing = night.getType() != GameType.SHADOW ? 5 : -5;
//                            if (keyHandler.pointerPosition.getX() > 540 * widthModifier + centerX) {
//                                night.getScaryCat().x -= thing;
//                            } else {
//                                night.getScaryCat().x += thing;
//                            }
//                        }

                                if (night.getMSI().isActive()) {
                                    night.getMSI().move(keyHandler.pointerPosition.getX() <= 540 * widthModifier);
                                }

                                if (night.getA90().isActive()) {
                                    night.getA90().dying = true;
                                }
                            }
                        }
                    }
                    if(night.getShark() != null) {
                        night.getShark().checkForBite();
                    }
                    if(night.frog.isActive()) {
                        if(night.frog.x == -240) {
                            AchievementHandler.obtain(this, Achievements.FROG);
                        }

                        night.frog.x += 16;
                        if(night.frog.x > 1079) {
                            night.frog.disappear();
                        }
                    }
                    if(night.isRainModifier()) {
                        synchronized (night.raindrops) {
                            for (Raindrop raindrop : night.raindrops) {
                                if (night.raindrops.contains(raindrop)) {
                                    raindrop.fall();
                                }
                            }
                        }
                    }
                    synchronized (nuclearCatEyes) {
                        ListIterator<NuclearCatEye> iter = nuclearCatEyes.listIterator();
                        while (iter.hasNext()) {
                            NuclearCatEye eye = iter.next();
                            eye.alpha -= 0.008F;

                            if (eye.alpha <= 0) {
                                iter.remove();
                            }
                        }
                    }
                    synchronized (nuclearLemonadeFog) {
                        ListIterator<NuclearLemonadeFog> iter = nuclearLemonadeFog.listIterator();
                        while (iter.hasNext()) {
                            NuclearLemonadeFog fog = iter.next();
                            fog.y -= 4 + (int) (4 * Math.random());

                            if (fog.y < -415) {
                                iter.remove();
                            }
                        }
                        if(night.getLemonadeCat().isNineBossfight()) {
                            if (Math.random() < 0.04) {
                                nuclearLemonadeFog.add(new NuclearLemonadeFog((int) (Math.random() * 1573 + night.env.maxOffset() - 493), 640));
                            }
                        }
                    }
                    synchronized (snowflakes) {
                        if(night.getBlizzardTime() > 0) {
                            snowflakes.add(new Snowflake((short) (fixedUpdatesAnim % 251), snowflake[0]));
                            snowflakes.add(new Snowflake((short) (fixedUpdatesAnim % 251), snowflake[0]));
                            snowflakes.add(new Snowflake((short) (fixedUpdatesAnim % 251), snowflake[1]));
                        }

                        ListIterator<Snowflake> iter = snowflakes.listIterator();
                        while (iter.hasNext()) {
                            Snowflake flake = iter.next();
                            flake.fall();
                            if (flake.getY() >= 680) {
                                iter.remove();
                            }
                        }
                    }
                    synchronized (bubbles) {
                        int limit = Math.min(600, currentWaterLevel);
                        ListIterator<BubbleParticle> iter = bubbles.listIterator();
                        while (iter.hasNext()) {
                            BubbleParticle bubble = iter.next();
                            bubble.floatUp();
                            if(bubble.getY() < limit) {
                                iter.remove();
                            }
                        }
                    }

                    if(night.getEvent() == GameEvent.MR_MAZE) {
                        MrMaze mrMaze = night.getMrMaze();
                        
                        if(mrMaze.fogOpacity < 1) {
                            mrMaze.fogOpacity += 0.01F;
                            if(mrMaze.fogOpacity > 1) {
                                mrMaze.fogOpacity = 1;
                            }
                        }
                        if(mrMaze.mazeAnim < 1) {
                            mrMaze.mazeAnim += 0.005F;
                            if(mrMaze.mazeAnim > 1) {
                                mrMaze.mazeAnim = 1;
                            }
                        }
                        if(mrMaze.waterHeight > 514) {
                            mrMaze.waterHeight -= 1;
                        }

                        mrMaze.updatePosition(mrMaze.moveX, mrMaze.moveY);
                        mrMaze.moveX = 0;
                        mrMaze.moveY = 0;

                        mrMaze.untilNextSound--;
                        if(mrMaze.untilNextSound < 0) {
                            mrMaze.untilNextSound = 240;
                            sound.play("mrMazeSound", Math.min(0.08, 0.1 * (1 - Math.pow(mrMaze.distance / 25F, 0.2))));
                        }   
                        
                        mrMaze.distance -= 0.005F;
                        if(mrMaze.distance < 0.5F) {
                            mrMaze.lose();
                        } else {
                            if (mrMaze.checkWin(mrMaze.playerX, mrMaze.playerY)) {
                                mrMaze.win();
                            }
                        }
                    }


                    if(night.getType() == GameType.DAY) {
                        pepitoClockProgress++;

                        if(neonSogAnim > 1) {
                            neonSogBallSize = (neonSogBallSize / 1.2F);
                            neonSogBallSize = (neonSogBallSize * 24F / 25F);

                            if(neonSogBallSize <= 0.4F) {
                                neonSogBallSize = 0.4F;

                                night.setNeonSogBall(new NeonSogBall());
                                neonSogAnim = 0;
                            }
                        }
                    }
                    if(type == GameType.ENDLESS_NIGHT) {
                        if(endless.getNight() == 3) {
                            riftIndicatorX -= 2;
                        }
                    }

                    if(night.getToleTole().isActive()) {
                        night.getToleTole().recalc();
                    }
                    switch (night.getEvent()) {
                        case LEMONADE -> {
                            if(night.getLemonadeCat().isActive()) {
                                night.getLemonadeCat().recalcY();
                                night.getLemonadeCat().recalcLemonade();
                                if(night.getLemonadeCat().getRotation() > 0.5) {
                                    night.getLemonadeCat().setRotation(night.getLemonadeCat().getRotation() * 0.9F);
                                }
                                night.getLemonadeCat().setCursorZoom(Math.max(1, night.getLemonadeCat().getCursorZoom() * 0.95F));
                            }
                        }
                        case DEEP_FLOOD -> {
                            if(night.getDsc().getCursorRotation() > 0.5) {
                                night.getDsc().setCursorRotation(night.getDsc().getCursorRotation() * 0.9F);
                            }
                            night.getDsc().setCursorZoom(Math.max(1, night.getDsc().getCursorZoom() * 0.95F));
                        }
                        case DYING -> {
                            if(astartaJumpscareCount) {
                                astartaJumpscareCounter++;
                            }
                            if(!drawCat) {
                                if(jumpscareShake == 0 && !jumpscare.equals(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)) && !jumpscareKey.equals("shadowPepito") && !jumpscareKey.equals("kiji")) {
                                    window.move((int) (window.getX() - (Math.random() * 8 - 4) * widthModifier), (int) (window.getY() - (Math.random() * 8 - 4) * heightModifier));
                                }
                            }
                        }
                        case ASTARTA -> {
                            if(night.getAstartaBoss().getMister() != null) {
                                Mister mister = night.getAstartaBoss().getMister();
                                if (mister.isSpawned()) {
                                    if (mister.getBloomTransparency() > 0) {
                                        mister.setBloomTransparency(mister.getBloomTransparency() - 0.025F);
                                    }
                                    if (Math.abs(mister.vx) >= 1 || Math.abs(mister.vy) >= 1) {
                                        float x = mister.getPoint().x;
                                        float y = mister.getPoint().y;
                                        if (Math.abs(mister.vx) >= 1) {
                                            x += mister.vx / 4F;
                                            mister.vx /= 1.05F;
                                        }
                                        if (Math.abs(mister.vy) >= 1) {
                                            y += mister.vy / 4F;
                                            mister.vy /= 1.05F;
                                        }
                                        if(!mister.isBeingHeld()) {
                                            if (x > 1210 || x < -30) {
                                                mister.vx = -mister.vx / 2;
                                            }
                                            if (y > 465 || y < -30) {
                                                mister.vy = -mister.vy / 2;
                                            }
                                        }
                                        x = Math.min(1210, Math.max(-30, x));
                                        y = Math.min(465, Math.max(-30, y));

                                        mister.setPoint(new Point(Math.round(x), Math.round(y)));

                                        if(mister.isBeingHeld()) {
                                            byte l = (byte) (Math.min(20, Math.abs(mister.vx) / 2));

                                            if(x > 910) {
                                                offsetX = (short) Math.min(400, Math.max(0, offsetX - l));
                                            } else if(x < 270) {
                                                offsetX = (short) Math.min(400, Math.max(0, offsetX + l));
                                            }

                                            pointX = (int) (((mirror ? -mister.getPoint().x : mister.getPoint().x) - offsetX + 1080 + 250) * widthModifier + centerX);
                                            pointY = (int) ((mister.getPoint().y + 100) * heightModifier + centerY);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(sunglassesOn) {
                        if(sgRadius <= 640) {
                            sgRadius += 27;
                        }
                    }
                    if(night.isRadiationModifier()) {
                        if(radiationCursor <= 50) {
                            radiationCursor += 3;
                        }
                    }
                    if(keyHandler.holdingE) {
                        holdingEFrames++;
                    }
                }

                case MENU -> {
                    if (vertical) {
                        scrollY -= 1;
                    } else {
                        scrollX -= 1;
                    }

                    synchronized (snowflakes) {
                        if(isDecember) {
                            if((fixedUpdatesAnim / 2) % 2 == 0) {
                                snowflakes.add(new Snowflake((short) (fixedUpdatesAnim % 251), snowflake[(int) Math.round(Math.random() / 1.3)]));
                            }

                            ListIterator<Snowflake> iter = snowflakes.listIterator();
                            while (iter.hasNext()) {
                                Snowflake flake = iter.next();
                                flake.fallSlow();
                                if (flake.getY() >= 680) {
                                    iter.remove();
                                }
                            }
                        }
                    }
                }
                case PLAY -> {
                    if(Math.round(PlayMenu.selectOffsetX) != PlayMenu.getGoalSelectOffsetX()) {
                        PlayMenu.selectOffsetX = (PlayMenu.selectOffsetX * 4 + PlayMenu.getGoalSelectOffsetX()) / 5F;
                    }
                }
                case ITEMS -> {
                    for(Item item : itemList) {
                        if(item.getShakeIntensity() > 0) {
                            item.setShakeIntensity((byte) (item.getShakeIntensity() - 1));
                        }
                    }
                }

                case RIFT -> {
                    riftCounter++;

                    if (riftY > 1280) {
                        riftFramesDoingNothing++;

                        if(riftFramesDoingNothing > 900) {
                            if(riftMoonAlpha < 1) {
                                riftMoonAlpha += 0.001F;

                                if(riftMoonAlpha > 1) {
                                    riftMoonAlpha = 1;
                                }
                            }
                        }
                    }
                }

                case ACHIEVEMENTS -> {
                    if(shiftingAchievements) {
                        if (achievementMargin > 0.49F) {
                            int thing = Math.round(achievementMargin * 0.2F);

                            if(achievementState) {
                                achievementsScrollX -= thing;
                            } else {
                                achievementsScrollX += thing;
                            }

                            achievementMargin *= 0.8F;
                        } else {
                            shiftingAchievements = false;
                            achievementState = !achievementState;
                        }
                    }
                }

                case DRY_CAT_GAME -> {
                    synchronized (dryCatGame.getCats()) {
                        ListIterator<DryCat> iter = dryCatGame.getCats().listIterator();
                        while(iter.hasNext()) {
                            DryCat cat = iter.next();
                            cat.process();

                            if (cat.getX() < -300 || cat.getX() > 1380) {
                                iter.remove();
                                dryCatGame.catsLost++;
                            }
                            if (cat.getSize() < -1) {
                                iter.remove();
                            }
                            if (cat.isDoor() && cat.isDead()) {
                                iter.remove();
                                music.stop();
                                music.play("windSounds", 0.1);
                                dryCatGame.openDoor();
                            }
                        }
                    }
                    synchronized (dryCatGame.particles) {
                        ListIterator<WaterParticle> iter = dryCatGame.particles.listIterator();
                        while (iter.hasNext()) {
                            WaterParticle particle = iter.next();
                            particle.alpha -= 0.01F;
                            particle.rotation += 0.628F;

                            if (particle.alpha <= 0) {
                                iter.remove();
                            }
                        }
                    }

                    dryCatGame.timer -= 0.016666F;

                    if(dryCatGame.isCrazy() && !dryCatGame.hasSpawnedDoor() && dryCatGame.timer < 20) {
                        dryCatGame.addDoor();
                    }
                    if (dryCatGame.timer < -0.5F) {
                        dryCatGame.ending(sound);

                        if (!dryCatGame.isFullWipeout() || (dryCatGame.isFullWipeout() && dryCatGame.timer < -10.5F)) {
                            state = GameState.GAME;
                            restartBasementSong();

                            GamePanel.balloons.add(new Balloon(0));
                            GamePanel.balloons.add(new Balloon(0));
                        }
                    }
                }

                case CRATE -> {
                    if(crateShake > 0) {
                        crateShake -= 0.5F;
                    }
                }

                case MILLY -> {
                    if(secondsInMillyShop >= 3600) {
                        if(dreadUntilGrayscale >= 0) {
                            dreadUntilGrayscale -= 0.01F;
                        }
                        if(dreadUntilGrayscale <= 0.3F && dreadUntilVignette >= 0) {
                            dreadUntilVignette -= 0.05F;
                        }
                    }
                    if(dreadUntilVignette < 1) {
                        alphaVignette[0] = alphaify(vignette[0], Math.min(1, 1 - dreadUntilVignette));
                        if(tintAlpha < 130 && dreadUntilVignette < 0.6F) {
                            alphaVignette[1] = alphaify(vignette[1], Math.min(1, (130 - tintAlpha) / 130));
                        }
                    }
                    if(night.env() instanceof Basement env) {
                        if(env.doWiresWork()) {
                            if (env.millyCurrentFlicker != env.millyGoalFlicker) {
                                if (disableFlickering) {
                                    env.millyCurrentFlicker = (env.millyCurrentFlicker * 19 + env.millyGoalFlicker) / 20;
                                } else {
                                    env.millyCurrentFlicker = (env.millyCurrentFlicker + env.millyGoalFlicker) / 2;
                                }
                                basementMillyLight = alphaify(basementMillyLightSource.request(), env.millyCurrentFlicker / 80F);
                            }
                        }
                    }
                }

                case MUSIC_MENU -> {
                    musicMenuDiscX = (musicMenuDiscX * 9F + 140) / 10F;
                }

                case CHALLENGE -> {
                    if(keyHandler.isInEnemiesRectangle) {
                        int oldHeight = CustomNight.enemiesRectangle.height;
                        int limit = 562;
                        if(oldHeight < limit) {
                            CustomNight.enemiesRectangle = new Rectangle(0, 0, 660, Math.min(limit, oldHeight + 40));
                        }

                        if(keyHandler.mouseHeld) {
                            if(CustomNight.isCustom()) {
                                if(CustomNight.selectedElement instanceof CustomNightEnemy) {
                                    CustomNight.holdingEnemyFrames++;
                                }
                            }
                        }
                    } else {
                        int oldHeight = CustomNight.enemiesRectangle.height;
                        if(oldHeight > 420) {
                            CustomNight.enemiesRectangle = new Rectangle(0, 0, 660, Math.max(420, oldHeight - 40));
                        }
                    }

                    for(int i = 0; i < CustomNight.getEnemies().size(); i++) {
                        int x = 105 * (i % 6);
                        int y = 130 * (i / 6);

                        CustomNightEnemy enemy = CustomNight.getEnemies().get(i);

                        if (enemy.otherX != -1) {
                            enemy.otherX = (x + enemy.otherX * 5) / 6;

                            if(Math.abs(enemy.otherX - x) < 2) {
                                enemy.otherX = -1;
                            }
                        }
                        if (enemy.otherY != -1) {
                            enemy.otherY = (y + enemy.otherY * 5) / 6;

                            if(Math.abs(enemy.otherY - y) < 2) {
                                enemy.otherY = -1;
                            }
                        }
                    }
                }
                
                case FIELD -> {
                    if(field.a90 != null) {
                        if (field.a90.margin > 0) {
                            field.a90.margin /= 2;
                        }
                    }
                    if(field.zoomCountdown > 0) {
                        field.zoomCountdown -= 0.008F;
                    }
                    if(!field.lockedIn) {
                        field.objectiveInterp += 0.0055F;
                        field.objectiveInterp *= 1.009F;
                    }
                    if(field.impulseInterp > 0) {
                        field.impulseInterp -= 0.005F;
                        field.impulseInterp /= 1.008F;
                    }

                    FieldBlimp blimp = field.getBlimp();
                    blimp.recalc(this);
                    
                    
                    if(field.isInCar()) {
                        if(field.controlsTransparency > 0) {
                            field.controlsTransparency -= 0.007F;
                        }
                        field.leverDegrees = (field.leverDegrees * 4F + field.leverDegreesGoal) / 5F;
         
                        
                        float speed = field.getSpeed();
                        float friction = 0.965F;

                        if (keyHandler.holdingW) {
                            field.setSpeed(speed + field.getAcceleration());
                            friction = 0.985F;
                        } else if (keyHandler.holdingS) {
                            field.setSpeed(speed - field.getAcceleration() / 5);
                            friction = 0.985F;
                        }

                        float sine = (field.getCarYaw() / 540F) * 0.9F;
                        float cosine = (float) Math.cos(Math.asin(sine));

                        field.addDistance(0.1F * cosine * speed);
                        field.setX(field.getX() - (int) (50 * sine * speed));


                        float firstY = field.getRoadYOffsetArray()[(int) Math.min(field.getSize() - 1, Math.max(0, Math.floor(field.getDistance()) + 3))];
                        float secondY = field.getRoadYOffsetArray()[(int) Math.min(field.getSize() - 1, Math.max(0, Math.floor(field.getDistance()) + 4))];

                        double newY = -(lerp(firstY, secondY, field.getDistance() % 1));
//                        if(newY != 0) {
                        float heightChange = (float) newY - (field.getY() - 320);
                        field.carPitch = heightChange / 6F;

//                            if(heightChange == 0) {
//                                field.carPitch /= 2;
//                            }
//                        }


                        firstY = field.getRoadYOffsetArray()[(int) Math.min(field.getSize() - 1, Math.max(0, Math.floor(field.getDistance())))];
                        secondY = field.getRoadYOffsetArray()[(int) Math.min(field.getSize() - 1, Math.max(0, Math.floor(field.getDistance()) + 1))];

                        newY = -(lerp(firstY, secondY, field.getDistance() % 1));
                        if (newY != 0) {
                            heightChange = (float) newY - (field.getY() - 320);

//                            if(heightChange > 0) {
//                                field.setSpeed(field.getSpeed() * 0.985F);
//                            } else if(heightChange < 0) {
//                                field.setSpeed(field.getSpeed() * 1.007F);
//                            }

                            field.setY(field.getY() + heightChange);
                        }


                        float turnSpeed = field.getTurnSpeed();
                        if (keyHandler.holdingA) {
                            turnSpeed += 0.03F;
                        }
                        if (keyHandler.holdingD) {
                            turnSpeed -= 0.03F;
                        }
                        turnSpeed *= 0.88F;
                        field.setTurnSpeed(turnSpeed);

                        field.carYaw = Math.min(540, Math.max(-540, field.getCarYaw() + 15 * speed * field.getTurnSpeed()));

                        if (Math.abs(Math.floor(field.carPitch)) > 0) {
                            field.setSpeed(field.getSpeed() - field.carPitch / 9600F);
                        }
                        field.setSpeed(field.getSpeed() * friction);
                    }
//                    } else {
//                        if (keyHandler.holdingW) {
//                            field.addDistance(0.2F);
//                        }
//                        if (keyHandler.holdingS) {
//                            field.addDistance(-0.2F);
//                        }
//                        
//                        if (keyHandler.holdingA) {
//                            field.setX(field.getX() - 50);
//                        }
//                        if (keyHandler.holdingD) {
//                            field.setX(field.getX() + 50);
//                        }
//                    }
                    
                    if(field.a90.isActive()) {
                        field.a90.dying = field.a90.dying || keyHandler.holdingW || keyHandler.holdingD || keyHandler.holdingS || keyHandler.holdingA;
                    }
                    
                    if(!field.isPlayingMusic()) {
                        if (field.getDistance() > 410) {
                            field.setPlayingMusic(true);

                            field.cancelAfter.add(new Pepitimer(() -> {
                                MediaPlayer sound = music.play("fieldField", 0, true);
                                music.clipVolume.put(sound, 0.155);

                                Timeline timeline = new Timeline(
                                        new KeyFrame(Duration.seconds(8),
                                                new KeyValue(sound.volumeProperty(), 0.155 * Math.sqrt(volume))));
                                timeline.play();
                            }, 1000));
                        }
                    } else if(!field.stoppingFirstSong) {
                        if(field.getDistance() > 1500) {
                            field.stoppingFirstSong = true;
                            
                            for(MediaPlayer player : music.clips) {
                                if(player.getMedia().getSource().contains("fieldField.mp3")) {
                                    music.clipVolume.put(player, 0d);

                                    Timeline timeline = new Timeline(
                                            new KeyFrame(Duration.seconds(10),
                                                    new KeyValue(player.volumeProperty(), 0)));
                                    timeline.play();

                                    field.cancelAfter.add(new Pepitimer(() -> {
                                        player.stop();
                                        player.dispose();
                                        music.clips.remove(player);
                                    }, 10000));
                                }
                            }
                        }
                    } else if(!field.getBlimp().underTheRadar) {
                        if(field.getDistance() > 1930 && field.getDistance() < 2800) {
                            field.getBlimp().underTheRadar = true;

                            sound.playRate("blimpRadarScan", 0.1, 0.5);
                            new Pepitimer(() -> sound.playRate("blimpRadarScan", 0.1, 0.5), 3000);
                            new Pepitimer(() -> sound.playRate("blimpRadarScan", 0.1, 0.5), 6000);


                            field.cancelAfter.add(new Pepitimer(() -> {
                                MediaPlayer sound = music.play("underTheRadar", 0, true);
                                music.clipVolume.put(sound, 0.155);

                                Timeline timeline = new Timeline(
                                        new KeyFrame(Duration.seconds(4),
                                                new KeyValue(sound.volumeProperty(), 0.155 * Math.sqrt(volume))));
                                timeline.play();
                            }, 8000));
                        }
                    } else if(!field.stoppingSecondSong) {
                        if(field.getDistance() > 2800) {
                            field.stoppingSecondSong = true;
                            field.getBlimp().underTheRadar = false;

                            for(MediaPlayer player : music.clips) {
                                if(player.getMedia().getSource().contains("underTheRadar.mp3")) {
                                    music.clipVolume.put(player, 0d);

                                    Timeline timeline = new Timeline(
                                            new KeyFrame(Duration.seconds(9),
                                                    new KeyValue(player.volumeProperty(), 0)));
                                    timeline.play();

                                    field.cancelAfter.add(new Pepitimer(() -> {
                                        player.stop();
                                        player.dispose();
                                        music.clips.remove(player);
                                    }, 9000));
                                }
                            }
                        }
                    }
                    
                    if(field.getDistance() > 3070) {
                        field.setAcceleration(0.01F);
                    }

                    if(field.getDistance() > 3290) {
                        if(!field.lockedIn) {
                            field.lockedIn = true;
                            
                            keyHandler.holdingW = false;
                            keyHandler.holdingA = false;
                            keyHandler.holdingS = false;
                            keyHandler.holdingD = false;
                            
                            fadeOut(1, 255, 0.4F);
                            
                            field.cancelAfter.add(new Pepitimer(() -> {
                                HChamber chamber = ((HChamber) night.env);
                                chamber.resetField();

                                sound.stop();
                                rainSound.stop();
                                music.stop();
                                
                                keyHandler.enterNewHydrophobiaRoom(chamber);
                                announceNight((byte) 1, GameType.HYDROPHOBIA);
                                
                                night.getHydrophobia().setAILevel(1);
                                night.getBeast().setAILevel(1);
                                night.getOverseer().setAILevel(1);

                                night.lockedIn = false;

                                state = GameState.GAME;
                                for(Pepitimer pepitimer : field.cancelAfter) {
                                    pepitimer.cancel();
                                }

                                fadeOut(255, 180, 0.4F);

                                DiscordRichPresence rich = new DiscordRichPresence.Builder
                                        ("In-Game")
                                        .setDetails("HYDROPHOBIA CHAMBER")
                                        .setBigImage("hydrophobia", "PEPITO RETURNED HOME")
                                        .setSmallImage("pepito", "PEPITO RETURNED HOME")
                                        .setStartTimestamps(launchedGameTime)
                                        .build();

                                DiscordRPC.discordUpdatePresence(rich);
                            }, 11000));
                        }
                    }
                    
                    
                    
//                    if(keyHandler.holdingSpace) {
//                        field.setY(field.getY() + 10);
//                    } else if(keyHandler.holdingShift) {
//                        field.setY(field.getY() - 10);
//                    }
                    
                    if(keyHandler.mouseHeld) {
                        Point rescaledPoint = new Point((int) ((keyHandler.pointerPosition.x - centerX) / widthModifier), (int) ((keyHandler.pointerPosition.y - centerY) / heightModifier));
                        Vector2D cursor = new Vector2D(rescaledPoint.x, rescaledPoint.y);
                        Vector2D center = new Vector2D(540, 320);
                        center.subtract(cursor);
                        center.divide(32);
                        
                        field.setYaw((int) Math.min(540, Math.max(-540, field.getYaw() + center.x)));
                        field.setPitch((int) Math.min(320, Math.max(-320, field.getPitch() + center.y)));
                    }

                    
                    if(field.lightningProgress > 0) {
                        field.lightningProgress -= 0.02F;
                    }
                    
                    synchronized (field.raindrops) {
                        int x = field.getX();
                        int y = (int) field.getY();

                        ListIterator<FieldRaindrop> iter = field.raindrops.listIterator();
                        while(iter.hasNext()) {
                            FieldRaindrop raindrop = iter.next();
                            raindrop.fall();
                            
                            if(raindrop.getY() > 540 + y) {
                                iter.remove();
                            }
                        }
                        
                        for (int i = 0; i < 70; i++) {
                            field.raindrops.add(new FieldRaindrop(x, y));
                        }
                    }
                    
                    
                    if(field.isInCar()) {
                        List<CollidableLandmine> landmines = new ArrayList<>();
                        float distance = field.getDistance();
                        int roadWidth = field.getRoadWidth();
                        int x = field.getX();

                        float[] roadXOffsets = field.getRoadXOffsetArray();
                        float[] roadWidths = field.getRoadWidthArray();

                        byte[] objects2ThirdsLeft = field.getObjects2ThirdsLeft();
                        byte[] objectsMiddle = field.getObjectsMiddle();
                        byte[] objects2ThirdsRight = field.getObjects2ThirdsRight();


                        for (int i = Math.min(field.getSize() - 1, (int) (distance) + 2); i > Math.max(0, (int) (distance) - 2); i--) {
                            float distanceToObject = i - distance;

                            float roadXOffset = -roadXOffsets[i];
                            float roadPieceWidth = roadWidths[i];

                            int divideBy = 10;

                            if (objects2ThirdsLeft[i] == 8) {
                                landmines.add(new CollidableLandmine(-1, i, new Circle((int) (((-(roadPieceWidth + roadWidth) * 2 / 3 / 7 * 4) + roadXOffset - x) / divideBy), (int) (distanceToObject * 16), 14)));
                            }
                            if (objectsMiddle[i] == 8) {
                                landmines.add(new CollidableLandmine(0, i, new Circle((int) ((roadXOffset - x) / divideBy), (int) (distanceToObject * 16), 14)));
                            }
                            if (objects2ThirdsRight[i] == 8) {
                                landmines.add(new CollidableLandmine(1, i, new Circle((int) ((((roadPieceWidth + roadWidth) * 2 / 3 / 7 * 4) + roadXOffset - x) / divideBy), (int) (distanceToObject * 16), 14)));
                            }
                        }
                        
                        for(CollidableLandmine landmine : landmines) {
                            Circle hitbox = landmine.hitbox;
                            if(hitbox.intersects(new BoundingBox(-3, -6, 7, 8))) {
                                switch (landmine.array) {
                                    case -1 -> field.getObjects2ThirdsLeft()[landmine.index] = 0;
                                    case 0 -> field.getObjectsMiddle()[landmine.index] = 0;
                                    case 1 -> field.getObjects2ThirdsRight()[landmine.index] = 0;
                                }
                                field.a90.spawn();
                                break;
                            }
                        }

                        int indexFirst = (int) Math.min(field.getSize() - 1, Math.max(0, Math.floor(field.getDistance())));
                        int indexSecond = (int) Math.min(field.getSize() - 1, Math.max(0, Math.floor(field.getDistance()) + 1));

                        int firstLeftX = (int) ((-(roadWidths[indexFirst] + roadWidth) / 7 * 4) - roadXOffsets[indexFirst] - x);
                        int secondLeftX = (int) ((-(roadWidths[indexSecond] + roadWidth) / 7 * 4) - roadXOffsets[indexSecond] - x);
                        int leftX = (int) -(lerp(firstLeftX, secondLeftX, field.getDistance() % 1));

                        if(leftX < 0) {
                            field.handleCollision(this);
                        }

                        int firstRightX = (int) (((roadWidths[indexFirst] + roadWidth) / 7 * 4) - roadXOffsets[indexFirst] - x);
                        int secondRightX = (int) (((roadWidths[indexSecond] + roadWidth) / 7 * 4) - roadXOffsets[indexSecond] - x);
                        int rightX = (int) -(lerp(firstRightX, secondRightX, field.getDistance() % 1));

                        if(rightX > 0) {
                            field.handleCollision(this);
                        }

                        if(distance < 0) {
                            field.lowSpeedCrash(this);
                        }


                        if(!field.isColliding) {
                            field.saveSnapshot();
                        }
                    }
                }
            }
        }


        currentWidth = (short) (1080 * widthModifier);
        currentHeight = (short) (640 * heightModifier);
    }

    private byte tvStatic = 1;
    private byte hcNoise = 0;

    public short goalFlicker = 0;
    public float currentFlicker = 0;
    public float tintAlpha = 255;
    public short offsetX = 200;
    
    float flashlightBrightness = 0;
    float goalFlashlightBrightness = 0;
    int holdingFlashlightFrames = 0;
    boolean flashlightOn = false;

    byte selectedItemX = 0;
    byte selectedItemY = 0;

    Color black80 = new Color(0, 0, 0, 80);
    Color black120 = new Color(0, 0, 0, 120);
    Color black140 = new Color(0, 0, 0, 140);
    public Color black200 = new Color(0, 0, 0, 200);
    Color white60 = new Color(255, 255, 255, 60);
    Color white100 = new Color(255, 255, 255, 100);
    Color white120 = new Color(255, 255, 255, 120);
    Color white160 = new Color(255, 255, 255, 160);
    Color white200 = new Color(255, 255, 255, 200);

    Font yuGothicPlain30 = new Font("Yu Gothic", Font.PLAIN, 30);
    Font yuGothicPlain50 = new Font("Yu Gothic", Font.PLAIN, 50);
    Font yuGothicPlain60 = new Font("Yu Gothic", Font.PLAIN, 60);
    Font yuGothicPlain80 = new Font("Yu Gothic", Font.PLAIN, 80);
    Font yuGothicPlain120 = new Font("Yu Gothic", Font.PLAIN, 120);

    Font yuGothicBold25 = new Font("Yu Gothic", Font.BOLD, 25);
    Font yuGothicBold60 = new Font("Yu Gothic", Font.BOLD, 60);

    Font yuGothicBoldItalic25 = new Font("Yu Gothic", Font.BOLD | Font.ITALIC, 25);
    Font yuGothicBoldItalic40 = new Font("Yu Gothic", Font.BOLD | Font.ITALIC, 40);

    public Font comicSansBold25 = new Font("Comic Sans MS", Font.BOLD, 25);
    Font comicSansBoldItalic40 = new Font("Comic Sans MS", Font.BOLD | Font.ITALIC, 40);
    Font comicSans20 = new Font("Comic Sans MS", Font.PLAIN, 20);
    public Font comicSans25 = new Font("Comic Sans MS", Font.PLAIN, 25);
    public Font comicSans30 = new Font("Comic Sans MS", Font.PLAIN, 30);
    public Font comicSans40 = new Font("Comic Sans MS", Font.PLAIN, 40);
    public Font comicSans50 = new Font("Comic Sans MS", Font.PLAIN, 50);
    Font comicSans60 = new Font("Comic Sans MS", Font.PLAIN, 60);
    Font comicSans80 = new Font("Comic Sans MS", Font.PLAIN, 80);

    Font debugFont = new Font("Arial", Font.BOLD, 20);
    Font consoleFont = new Font("Arial", Font.BOLD, 30);


    BufferedImage itemsMenu = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);
    short itemScrollY = 0;

    // next two are for customnight
    BufferedImage lastItemsMenu = null;
    BufferedImage greenItemsMenu = null;

    public int pointX = (int) keyHandler.pointerPosition.getX();
    public int pointY = (int) keyHandler.pointerPosition.getY();

    
    public void repaintOffice() {
        redrawBHO = true;
        
        Enviornment e = night.env();
        
        BufferedImage fullOffice = new BufferedImage(1080 + e.maxOffset(), 640, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = (Graphics2D) fullOffice.getGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        graphics2D.drawImage(officeImg[e.getBgIndex()], 0, 0, null);
        
        
        switch (e.getBgIndex()) {
            case 3 -> {
                Basement basement = ((Basement) e);
                switch (basement.getVent()) {
                    case 0 -> graphics2D.drawImage(vent.request(), 756, 398, null);
                    case 2 -> graphics2D.drawImage(ventLaying.request(), 737, 485, 144, 47, null);
                }

                for (int i : basement.blockedWalls) {
                    switch (i) {
                        case 0 -> graphics2D.drawImage(basementWall1.request(), 1256, 316, null);
                        case 1 -> graphics2D.drawImage(basementWall2.request(), 67, 317, null);
                        case 2 -> graphics2D.drawImage(basementWall3.request(), 1001, 345, null);
                        case 3 -> graphics2D.drawImage(basementWall4.request(), 369, 345, null);
                    }
                }

                if (basement.isShowPsyop()) {
                    graphics2D.drawImage(psyopPoster.request(), 1327, 20, null);
                }
                if(basement.isPartiesPoster()) {
                    graphics2D.drawImage(noPartiesAllowed.request(), 637, 274, null);
                }
                
                if(basement.getStage() == 7) {
                    graphics2D.drawImage(basementCrateLaying.request(), 307, 452, null);
                    graphics2D.drawImage(basementLightShadow.request(), 0, 0, null);

                    float h = basement.getMonitorHeight();
                    graphics2D.drawImage(basementMonitorShadow.request().getSubimage(0, (int) (144 - (h + 29)), 279, (int) h + 29), 601, 236, null);
                    graphics2D.drawImage(basementMonitorBgBroken.request().getSubimage(0, (int) (115 - h), 225, (int) h), 628, 236, null);
                }
            }
            case 4 -> {
                BufferedImage demotivator = makeDemotivator();
                demotivator = rotateINEFFICIENTBUTSMOOTHANDCUTSOFF(demotivator, (int) (Math.random() * 40 - 20), true);
                
                graphics2D.drawImage(demotivator, 800, 160, null);
                graphics2D.drawImage(demotivator, 3833, 160, null);
            }
        }
        if(type == GameType.HYDROPHOBIA) {
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
            
            HChamber ch = ((HChamber) e);

            graphics2D.drawImage(hcExit.request(), ch.exit.x, ch.exit.y, ch.exit.width, ch.exit.height, null);
            graphics2D.drawImage(hcCompass.request(), ch.compass.x, ch.compass.y, ch.compass.width, ch.compass.height, null);
            
            if(ch.hasLocker()) {
                graphics2D.drawImage(hcLocker.request(), ch.locker.x, ch.locker.y, ch.locker.width, ch.locker.height, null);
            }
            graphics2D.drawImage(hcTable.request(), ch.table.x, ch.table.y, ch.table.width, ch.table.height, null);
            if(ch.hasCup() && ch.table.height >= 10) {
                graphics2D.drawImage(hcHighlightedTable.request(), ch.table.x, ch.table.y, ch.table.width, ch.table.height / 10, null);
            }
            graphics2D.drawImage(hcTimer.request(), ch.timer.x, ch.timer.y, ch.timer.width, ch.timer.height, null);

            BufferedImage image = rotate(resize(hcBarrier.request(), ch.exit.width + 30, ch.exit.height / 10, Image.SCALE_FAST), (int) ch.getBarrierRotation());
            graphics2D.drawImage(image, ch.exit.x - 15, ch.exit.y + ch.exit.height / 3 * 2 + 25 - image.getHeight(), null);

            if(ch.hasConditioner()) {
                graphics2D.drawImage(ch.isConditionerMirrored() ? mirror(hcConditioner.request(), 1) : hcConditioner.request(), ch.conditioner.x, ch.conditioner.y, ch.conditioner.width, ch.conditioner.height, null);
            }
            for(int x : ch.getRandomPipeX()) {
                if(x >= 1480)
                    continue;
                graphics2D.drawImage(hcPipe.request(), x, 0, null);
            }
            if(ch.table.x < 1480) {
                graphics2D.drawImage(hcDustonKey.request(), ch.key.x, ch.key.y, ch.key.width, ch.key.height, null);
            }
            // MONITOR
            if(ch.isInDustons()) {
                graphics2D.drawImage(hcMonitor.request(), 300, 309, null);
            }
            if(ch.getSeed() == 31 && !ch.isInPrefield() && !ch.isRewardRoom()) {
                graphics2D.drawImage(hcMonitor.request(), 217, 316, null);
            }
            
            graphics2D.drawImage(hcCup.request(), ch.cup.x, ch.cup.y, ch.cup.width, ch.cup.height, null);
            
            
            switch (e.getBgIndex()) {
                case 10 -> {
                    graphics2D.drawImage(hcBarrel.request(), 288, 415, null);
                    graphics2D.drawImage(hcExitSignDark.request(), 670, 233, null);

                    if (ch.penExists()) {
                        graphics2D.drawImage(soggyPenImg.request(), 360, 334, null);
                    }
                }
                case 12 -> {
                    graphics2D.drawImage(hcPipe.request(), -20, 0, null);
                    graphics2D.drawImage(hcPipe.request(), 159, 0, null);

                    graphics2D.drawImage(hcBarrel.request(), 1227, 415, null);
                    
                    float percent = ch.getReinforcedDoorPercent();
                    if(percent > 0) {
                        BufferedImage door = hcDoorReinforced.request();
                        if (door.getHeight() * percent >= 1) {
                            if(percent < 1) {
                                door = door.getSubimage(0, (int) (door.getHeight() * (1 - percent)), door.getWidth(), (int) (door.getHeight() * percent));
                            }
                            graphics2D.drawImage(door, 979, 323, null);
                        }
                    }
                }
                case 13 -> {
                    graphics2D.drawImage(hcPipe.request(), -20, 0, null);
                    graphics2D.drawImage(hcPipe.request(), 159, 0, null);
                    graphics2D.drawImage(hcPipe.request(), 1212, 0, null);
                    graphics2D.drawImage(hcPipe.request(), 1391, 0, null);
                    
                    graphics2D.drawImage(hcExitSignDark.request(), 840, 293, null);
                }
            }

            graphics2D.setComposite(MultiplyComposite.Multiply);
            graphics2D.drawImage(hcMultiplyLayer.request(), 0, 0, 1480, 640, null);
            graphics2D.setComposite(AlphaComposite.SrcOver);

//            applyHydrophobiaFilter(graphics2D, 1480);
        }
        

        if(hisPicture.isEnabled()) {
            graphics2D.drawImage(loadImg("/game/items/hisPicture.png"), 250, 99, null);
        }
        if(hisPainting.isEnabled()) {
            graphics2D.drawImage(loadImg("/game/items/hisPainting.png"), 20, 50, null);
        }
        if(iceBucket.isEnabled()) {
            graphics2D.drawImage(iceBucketImg.request(), 1386, 570, null);
        }
        if(red40.isEnabled()) {
            if (type != GameType.HYDROPHOBIA) {
                graphics2D.drawImage(red40Img.request(), 630, 474, 38, 30, null);
            }
        }
        if(!type.isBasement()) {
            if (night.getShark().isEnabled() || night.getDsc().isEnabled()) {
                graphics2D.drawImage(pipe.request(), e.pipe.x, e.pipe.y, e.pipe.width, e.pipe.height, null);
            }
        }


        if(night.getElAstarta().isActive()) {
            graphics2D.setColor(new Color(73, 73, 73));
            for(int index : night.getElAstarta().getNewDoors()) {
                Polygon hitbox = night.getDoors().get(index).getHitbox();
                graphics2D.fillRect(hitbox.getBounds().x, hitbox.getBounds().y, hitbox.getBounds().width, hitbox.getBounds().height);
            }
        }
        
        if (metalPipe.isEnabled()) {
            graphics2D.drawImage(metalPipeImg, e.metalPipe.x, e.metalPipe.y, e.metalPipe.width, e.metalPipe.height, null);
        }
        if (sensor.isEnabled()) {
            graphics2D.drawImage(sensorImg, e.sensor.x, e.sensor.y, e.sensor.width, e.sensor.height, null);
        }
        if (flashlight.isEnabled()) {
            graphics2D.drawImage(flashlightImg, e.flashlight.x, e.flashlight.y, e.flashlight.width, e.flashlight.height, null);
        }
        if(miniSoda.isEnabled()) {
            graphics2D.drawImage(miniSodaImg, e.miniSoda.x, e.miniSoda.y, e.miniSoda.width, e.miniSoda.height, null);
        }
        if(planks.isEnabled()) {
            graphics2D.drawImage(planksImg, e.planks.x, e.planks.y, e.planks.width, e.planks.height, null);
        }
        if(freezePotion.isEnabled()) {
            graphics2D.drawImage(freezeImg, e.freezePotion.x, e.freezePotion.y, e.freezePotion.width, e.freezePotion.height, null);
        }
        if(starlightBottle.isEnabled()) {
            graphics2D.drawImage(starlightBottleImg, e.starlightBottle.x, e.starlightBottle.y, e.starlightBottle.width, e.starlightBottle.height, null);
        }
        if (styroPipe.isEnabled()) {
            graphics2D.drawImage(styroPipeImg, e.styroPipe.x, e.styroPipe.y, e.styroPipe.width, e.styroPipe.height, null);
        }
        if(weatherStation.isEnabled()) {
            graphics2D.drawImage(weatherStationImg.request(), e.weatherStation.x, e.weatherStation.y, e.weatherStation.width, e.weatherStation.height, null);
        }

        for(Door door : night.getDoors().values()) {
            if(door.getBlockade() > 0) {
                int xCenter = door.getHitbox().getBounds().x + door.getHitbox().getBounds().width / 2;

                int y = door.getHitbox().getBounds().y + door.getHitbox().getBounds().height - 120;
                while (y >= door.getHitbox().getBounds().y) {
                    graphics2D.drawImage(planksImg, xCenter - 83, y, null);
                    y -= 120;
                }
            }
        }
        graphics2D.dispose();
        

        if(type == GameType.SHADOW) {
            if (e instanceof Basement) {
                graphics2D = (Graphics2D) fullOffice.getGraphics();
                graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
                graphics2D.setComposite(AlphaComposite.SrcOver.derive(0.6F));
                graphics2D.drawImage(advancedPurplify(grayscale(fullOffice)), 0, 0, null);
                graphics2D.dispose();
            }
            
            fullOffice = purplify(fullOffice);
        }
        if(night.getShock().isDoom()) {
            fullOffice = night.getShock().imgFilter(fullOffice, 6, 2);
            
        } else if(night.getType().isBasement() && Math.random() < 0.01) {
            graphics2D = (Graphics2D) fullOffice.getGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            graphics2D.setComposite(MultiplyComposite.Multiply);
            graphics2D.drawImage(fullOffice, 0, 0, null);
            
            graphics2D.dispose();
        }
        this.fullOffice = fullOffice;
    }
    
    void drawFan(Graphics2D graphics2D, int offset, Enviornment e) {
        graphics2D.drawImage(fanImg[1], offset + e.fan.x, e.fan.y, null);
        
        graphics2D.drawImage(rotatedFanBlade, offset + e.fan.x + 66 - rotatedFanBlade.getWidth() / 2, e.fan.y + 69 - rotatedFanBlade.getHeight() / 2, null);

        graphics2D.drawImage(mudseal, offset + e.mudseal.x, e.mudseal.y, e.mudseal.width, e.mudseal.height, null);
    }

    Pepitimer startSimulationTimer = null;

    
    float[] cniMenuSway = new float[] {0.02F};

    public void startGameThroughItems() {
        if(type == GameType.CUSTOM) {
            lastItemsMenu = lastFullyRenderedUnshaded;
            greenItemsMenu = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_ARGB);
            
            Graphics2D graphics = (Graphics2D) greenItemsMenu.getGraphics();
            graphics.setComposite(GreenifyComposite.Greenify);
            graphics.drawImage(resize(lastFullyRenderedUnshaded, 540, 320, Image.SCALE_FAST), 0, 0, 1080, 640,null);
            graphics.setComposite(AlphaComposite.SrcOver);
            
            graphics.setColor(new Color(0, 0, 0, 100));
            graphics.fillRoundRect(160, 250, 770, 80, 40, 40);
            graphics.setColor(Color.GREEN);
            graphics.setFont(yuGothicPlain80);
            String starting = getString("startingSimulation");
            if(Math.random() < 0.001) {
                starting = getString("fartingSimulation");
            }
            graphics.drawString(starting, 540 - halfTextLength(graphics, starting), 320);
            graphics.dispose();

            sound.play("startSimulation", 0.1);
            music.stop();

            cniMenuSway = new float[] {0.02F};

            everySecond20th.put("startSimulation", () -> {
                if(cniMenuSway[0] < 1) {
                    cniMenuSway[0] *= 1.2F;
                }
                BufferedImage newImage = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2D = (Graphics2D) newImage.getGraphics();

                graphics2D.setColor(Color.BLACK);
                graphics2D.fillRect(0, 0, 1080, 640);

                graphics2D.drawImage(lastItemsMenu, 27 + (int) (14 * cniMenuSway[0] * Math.cos(fixedUpdatesAnim * 0.05)), 16 + (int) (8 * cniMenuSway[0] * Math.sin(fixedUpdatesAnim * 0.05)), 1026, 608, null);

                graphics2D.setColor(new Color(0, 0, 0, 70));
                graphics2D.fillRect(0, 0, 1080, 640);
                graphics2D.drawImage(greenItemsMenu, 0, 0, 1080, 640, null);

                graphics2D.dispose();
                lastItemsMenu = newImage;
            });

            startSimulationTimer = new Pepitimer(() -> {
                startGame();
                everySecond20th.remove("startSimulation");
            }, 4000);
        } else {
            startGame();
        }
    }

    public BufferedImage unshaded = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);
    BufferedImage lastFullyRenderedUnshaded = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);
    BufferedImage lastBeforePause = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);
    int fanDegrees = 0;

    List<String> manualText = new ArrayList<>();
    public int manualY = 640;
    boolean hoveringGenerator = false;
    boolean hoveringBasementMonitor = false;
    boolean hoveringAnyDoorButton = false;


    private void firstHalf(Graphics2D graphics2D) {
        Point rescaledPoint = new Point((int) ((keyHandler.pointerPosition.x - centerX) / widthModifier), (int) ((keyHandler.pointerPosition.y - centerY) / heightModifier));
    
        switch (state) {
            case MENU -> {
                short x = (short) (Math.max(-scrollX - randomX, 0));
                short y = (short) (Math.max(-scrollY - randomY, 0));
                graphics2D.drawImage(bg[currentBG].getSubimage(x, y, Math.min(1920 - x, 1080), Math.min(1440 - y, 640)), 0, 0, null);

                graphics2D.setColor(Color.darkGray);
                graphics2D.setFont(comicSans50);
                graphics2D.drawString("x", 1025, 50);

                if(new Rectangle(1025, 20, 35, 35).contains(rescaledPoint)) {
                    graphics2D.setColor(black80);
                    graphics2D.fillOval(1020, 17, 40, 40);
                }
            }

            case GAME -> {
                boolean proceed = true;
                if(night.getEvent() == GameEvent.DYING && !drawCat) {
                    List<String> ignore = List.of("overseer", "pepito", "beast", "msi");
                    proceed = !ignore.contains(jumpscareKey);
                }
                
                if(!night.getEvent().isInGame() && proceed) {
                    if(type == GameType.HYDROPHOBIA) {
                        if(((HChamber) night.env).displayFieldDeathScreen) {
                            graphics2D.drawImage(restInPeiceField.request(), 0, -deathScreenY, null);
                        } else {
                            graphics2D.drawImage(restInPeiceHydroFull.request(), 0, -deathScreenY, null);
                        }
                    }
                    return;
                }
                
                
                try {
                    Enviornment e = night.env();
                    int maxOffset = e.maxOffset();
                    int offset = fixedOffsetX - maxOffset;
                    
                    if(!inCam) {
                        boolean officeNotRendered = true;

                        if(night.getType() == GameType.SHADOW) {
                            if(night.getAstartaBoss() != null) {
                                if(night.getAstartaBoss().isFighting()) {
                                    officeNotRendered = false;
                                    BufferedImage office = night.getAstartaBoss().astartaOfficeStuff(fullOffice, this);

                                    if (night.getAstartaBoss().getDyingStage() < 8) {
                                        graphics2D.drawImage(office.getSubimage(maxOffset - fixedOffsetX, 0, 1080, 640), 0, 0, null);
                                    } else {
                                        graphics2D.setColor(Color.BLACK);
                                        graphics2D.fillRect(0, 0, 1080, 640);
                                        graphics2D.drawImage(office, offset + (int) (740 - 740 * night.getAstartaBoss().getEndingOfficeSize()), (int) (320 - 320 * night.getAstartaBoss().getEndingOfficeSize()), null);
                                    }
                                }
                            }
                        }

                        if(officeNotRendered) {
                            if(offsetX >= 0 && offsetX <= maxOffset) {
                                graphics2D.drawImage(fullOffice.getSubimage(maxOffset - fixedOffsetX, 0, 1080, 640), 0, 0, null);
                            } else {
                                System.out.println("CHEATER");
                                graphics2D.drawImage(msiKnows.request(), 300, 200, null);
                                graphics2D.drawImage(fullOffice, offset, 0, null);
                            }
                        }
                        
                        
                        if(night.getShock().isActive()) {
                            int index = (fixedUpdatesAnim / 4) % 12;
                            
                            Rectangle monitor = e.getMonitor();
                            graphics2D.drawImage(shockCat[index].request(), offset + monitor.x, monitor.y, monitor.width, monitor.height, null);
                        }
                        
                        if(soup.isEnabled()) {
                            graphics2D.drawImage(soupItemImg, offset + e.soup.x, e.soup.y, e.soup.width, e.soup.height, null);
                        }
                        if(megaSoda.isEnabled()) {
                            int height = e.megaSoda.height / 4 * night.megaSodaUses;
                            if(night.getColaCat().megaSodaWithheld <= 0) {
                                graphics2D.drawImage(megaSoda.getIcon().getSubimage(0, 145 - (145 / 4 * night.megaSodaUses), 109, (145 / 4 * night.megaSodaUses)), offset + e.megaSoda.x, e.megaSoda.y + e.megaSoda.height - height, e.megaSoda.width, height, null);
                            }
                        }

                        if (night.getWetFloor() > 0) {
                            if (lastReflection != null) {
                                Polygon floorClip = new Polygon(e.getFloorClip().xpoints, e.getFloorClip().ypoints, e.getFloorClip().npoints);
                                floorClip.translate(fixedOffsetX - e.maxOffset(), 0);
                                Rectangle bounds = floorClip.getBounds();
                                
                                graphics2D.setClip(floorClip);
                                graphics2D.setComposite(AlphaComposite.SrcOver.derive(night.getWetFloor()));

                                graphics2D.scale(1, -1);
                                
                                graphics2D.drawImage(lastReflection, 0, -bounds.y - bounds.height, null);
                                
                                graphics2D.scale(1, -1);
                                
                                lastReflection = null;

                                graphics2D.setColor(black80);
                                graphics2D.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                                
                                graphics2D.setClip(0, 0, 1080, 640);
                                graphics2D.setComposite(AlphaComposite.SrcOver);
                            }
                        }
                        
                        
                        if(e.getBgIndex() == 4) {
                            BasementKeyOffice office = ((BasementKeyOffice) e);
                            float percent = office.getEvilDoorPercent();
                            
                            if(percent > 0) {
                                BufferedImage image = evilDoor.request();

                                if (image.getHeight() * percent >= 1) {
                                    if(percent < 1) {
                                        image = image.getSubimage(0, (int) (image.getHeight() * (1 - percent)), image.getWidth(), (int) (image.getHeight() * percent));
                                    }

                                    graphics2D.drawImage(image, offset + 327, 301, null);
                                    graphics2D.drawImage(image, offset + 3360, 301, null);
                                }
                            }
                            if(office.getEvilDoorPercent() == 1 && office.isHoveringEvilDoor()) {
                                graphics2D.drawImage(evilDoorLockIcon.request(), offset + 403, 427, null);
                                graphics2D.drawImage(evilDoorLockIcon.request(), offset + 3436, 427, null);
                            }
                            
                            if(office.getEvilDoorPercent() == 0 && office.isHoveringEvilDoor()) {
                                graphics2D.setColor(white60);
                                graphics2D.fillRect(offset + 327, 301, 216, 316);
                                graphics2D.fillRect(offset + 3360, 301, 216, 316);
                                
                                graphics2D.drawImage(evilDoorArrowIcon.request(), offset + 403, 427, null);
                                graphics2D.drawImage(evilDoorArrowIcon.request(), offset + 3436, 427, null);
                            }
                            graphics2D.drawImage(office.getCanvas(), offset + 17, 244, null);
                            graphics2D.drawImage(office.getCanvas(), offset + 3050, 244, null);
                            
                            if(toleToleSpawned >= 4 && toleToleKilled <= 0) {
                                graphics2D.drawImage(toleTolePoster.request(), offset + 790, 328, null);
                                graphics2D.drawImage(toleTolePoster.request(), offset + 3823, 328, null);

                                toleTolePosterSeen = true;
                            }
                            if(Achievements.BASEMENT.isObtained() || Achievements.BASEMENT_PARTY.isObtained()) {
                                graphics2D.drawImage(radioThing.request(), offset + 589, 353, null);
                                graphics2D.drawImage(radioThing.request(), offset + 589 + 3033, 353, null);
                            
                                if(office.powerOff) {
                                    graphics2D.drawImage(leverHandle.request(), offset + 695, 524, null);
                                    graphics2D.drawImage(leverHandle.request(), offset + 695+3033, 524, null);
                                } else {
                                    graphics2D.drawImage(mirror(leverHandle.request(), 3), offset + 695, 524+34, null);
                                    graphics2D.drawImage(mirror(leverHandle.request(), 3), offset + 695+3033, 524+34, null);
                                }
                            }
                        } else if(e.getBgIndex() == 3) {
                            Basement env = (Basement) e;

                            if (env.getVent() == 1) {
                                int height = (int) (Math.abs(47 * env.getVentProgress()));
                                if (env.getVentProgress() < 0) {
                                    graphics2D.drawImage(mirror(ventLaying.request(), 2), offset + 737, 487 - height, 144, height, null);
                                } else {
                                    graphics2D.drawImage(ventLaying.request(), offset + 737, 484, 144, height, null);
                                }
                            }

                            if (env.getStage() >= 4 && env.getStage() < 7) {
                                graphics2D.drawImage(basementMonitorShadow.request().getSubimage(0, (int) (144 - (env.getMonitorHeight() + 29)), 279, (int) (env.getMonitorHeight()) + 29), offset + 601, 236, null);
                                graphics2D.drawImage(env.generatorMinigameMonitor.getSubimage(0, (int) (115 - env.getMonitorHeight()), 225, (int) (env.getMonitorHeight())), offset + 628, 236, null);

                                if (hoveringBasementMonitor && env.getMonitorHeight() == 115) {
                                    graphics2D.setColor(white120);
                                    graphics2D.setStroke(new BasicStroke(5));
                                    graphics2D.drawRect(offset + 625, 233, 230, 120);
                                    graphics2D.setStroke(new BasicStroke());
                                }

                                if (night.getPepito().seconds < 1 && night.getPepito().isNotPepito) {
                                    if (night.getPepito().getDoor() == 4) {
                                        graphics2D.drawImage(notPepitoVent.request(), offset + 770, 420, null);
                                    }
                                }
                            } else if(env.getStage() == 7) {
                                graphics2D.drawImage(vertWobble(basementLadder.request(), 8 + Math.round(Math.random() * 2), 4, 0.045F, 0.4F), offset + 635 + (int) ((Math.sin(fixedUpdatesAnim / 50F) + Math.cos(fixedUpdatesAnim / 90F)) * 12), 120 + (int) ((Math.sin(fixedUpdatesAnim / 70F) + Math.cos(fixedUpdatesAnim / 110F)) * 8), null);
                            
                                if(basementLadderHovering) {
                                    graphics2D.setColor(Color.WHITE);
                                    graphics2D.setStroke(new BasicStroke(4));
                                    graphics2D.drawRoundRect(offset + 544, 358, 322, 30, 20, 20);
                                    graphics2D.setStroke(new BasicStroke());
                                    float percent = basementLadderFrames / 480F;
                                    graphics2D.fillRoundRect(offset + 544, 358, (int) (Math.sqrt(percent) * 322), 30, 20, 20);
                                }
                            }

                            if (env.rumbleSog != null) {
                                graphics2D.drawImage(soggyCatImg[0].request(), offset + (int) (Math.random() * 24 - 12), 108 + (int) (Math.random() * 24 - 12), 319, 532, null);
                            }
                        }
                        
                        if(type == GameType.HYDROPHOBIA) {
                            HChamber env = (HChamber) e;
                            
                            if(env.isHoveringExit()) {
                                if(env.getBarrierRotation() > -90) {
                                    graphics2D.drawImage(evilDoorLockIcon.request(), offset + env.exit.x + env.exit.width / 2 - 32, env.exit.y + env.exit.height / 2 - 32, null);
                                } else {
                                    graphics2D.drawImage(evilDoorArrowIcon.request(), offset + env.exit.x + env.exit.width / 2 - 32, env.exit.y + env.exit.height / 2 - 32, null);
                                }
                            }
                            if(env.isHoveringLocker()) {
                                graphics2D.drawImage(evilDoorArrowIcon.request(), offset + env.locker.x + env.locker.width / 2 - 32, env.locker.y + env.locker.height / 2 - 32, null);
                            }
                            if(env.isHoveringReinforced()) {
                                if (env.getReinforcedDoorPercent() == 1) {
                                    graphics2D.drawImage(evilDoorLockIcon.request(), offset + 1043, 433, null);
                                } else if(env.getReinforcedDoorPercent() == 0) {
                                    graphics2D.drawImage(evilDoorArrowIcon.request(), offset + 1043, 433, null);
                                }
                            }
                            if(env.isInDustons()) {
                                if(tintAlpha < 160) {
                                    graphics2D.drawImage(hcCockroach[(int) ((fixedUpdatesAnim / 4F) % 2)].request(), offset + env.cockroachX, 600, null);
                                } else {
                                    graphics2D.drawImage(hcCockroach[0].request(), offset + env.cockroachX, 600, null);
                                }
                                
                                int halfMaxOffset = maxOffset / 2;
                                int x = (int) (((offset + halfMaxOffset) / 0.5) - halfMaxOffset) + 46;

                                graphics2D.drawImage(hcDustonOfficeCrates.request(), x, 300, null);
                            }
                            
                            
                            BufferedImage compassArrow = rotate(hcCompassArrow.request(), env.getCompassRotation());
                            graphics2D.drawImage(compassArrow, offset + env.compass.x + env.compass.width / 2 - compassArrow.getWidth() / 2, env.compass.y + env.compass.height / 2 - compassArrow.getHeight() / 2, null);
                            
                            if(env.getGoalCompassRotation() != env.getCompassRotation()) {
                                for(int x : env.getRandomPipeX()) {
                                    if(x >= 1480)
                                        continue;
                                    if(!env.compass.intersects(new Rectangle(x, 0, 109, 640)))
                                        continue;
                                      
                                    graphics2D.drawImage(fullOffice.getSubimage(x, 0, 109, 640), offset + x, 0, null);
                                }
                            }
                            
//                            graphics2D.drawImage(hcCompass.request(), 510, 375, 400, 400, null);
//                            compassArrow = rotate(resize(hcCompassArrow.request(), 300, 300, Image.SCALE_FAST), env.getCompassRotation());
//                            graphics2D.drawImage(compassArrow, 715 - compassArrow.getWidth() / 2, 575 - compassArrow.getHeight() / 2, null);
//                            graphics2D.drawImage(hcCompassNumbers.request(), 552, 417, null);


                            if(!env.coffeeParticles.isEmpty()) {
                                synchronized (env.coffeeParticles) {
                                    for (CoffeeParticle particle : env.coffeeParticles) {
                                        int sine = (int) (7 * Math.sin(particle.getStartingPhase() + fixedUpdatesAnim / 400F));
                                        graphics2D.drawImage(coffeeParticle.request(), offset + particle.getX() + sine, particle.getY() - 60, null);
                                    }
                                }
                            }
                            
                            
                            if(env.showCompassHint()) {
                                graphics2D.drawImage(hcRotateCompass.request(), offset + env.compass.x, env.compass.y, env.compass.width, env.compass.height, null);
                            }

                            if(env.isHoveringConditioner()) {
                                graphics2D.setColor(new Color(50, 95, 255, 40));
                                graphics2D.fillRect(offset + env.conditioner.x, env.conditioner.y, env.conditioner.width, env.conditioner.height);
                            }
                            if(env.isHoveringPen()) {
                                graphics2D.drawImage(alphaify(soggyPenImg.request(), 0.66F), offset + 360, 334, null);
                            }
                        }
                        

                        if(night.isPowerModifier()) {
                            graphics2D.drawImage(generator.request(), offset + e.generator.x, e.generator.y, e.generator.width, e.generator.height, null);
                            if(hoveringGenerator) {
                                float w = e.generator.width / 220F;
                                float h = e.generator.height / 150F;
                                
                                graphics2D.drawImage(generatorOutline.request(), offset + e.generator.x - (int) (5 * w), e.generator.y - (int) (5 * h), e.generator.width + (int) (10 * w), e.generator.height + (int) (10 * h), null);
                            }
                        }
                        

                        if (night.getAstarta().isActive()) {
                            int anim = night.getAstarta().animation;

                            BufferedImage img = astartaEyes;
                            if(night.getAstarta().blinker) {
                                img = alphaify(astartaEyes, 0.5F);
                            }
                            Door door = night.getDoors().get((int) night.getAstarta().door);
                            
                            float size = door.getVisualSize();
                            graphics2D.drawImage(img, offset + door.getAstartaEyesPos().x, door.getAstartaEyesPos().y - anim, (int) (107 * size), (int) (anim * 2 * size), null);
                        }
                        if(night.getElAstarta().isActive()) {
                            if(night.getElAstarta().isKindaActive()) {
                                int anim = night.getElAstarta().animation;

                                BufferedImage img = astartaEyes;
                                if(night.getElAstarta().blinker) {
                                    img = alphaify(astartaEyes, 0.5F);
                                }
                                Door door = night.getDoors().get((int) night.getElAstarta().door);
                                
                                float size = door.getVisualSize();
                                if(size != 1) {
                                    img = resize(img, (short) (107 * size), (short) (66 * size), Image.SCALE_SMOOTH);
                                }
                                graphics2D.drawImage(img, offset + door.getAstartaEyesPos().x, door.getAstartaEyesPos().y - anim, 107, anim * 2, null);
                            }
                        }

                        graphics2D.setColor(white60);
                        boolean flashlightCondition = flashlight.isEnabled() && flashLightCooldown <= 0;

                        int buttonXOffset = 0;
                        int buttonYOffset = 0;
                        if(night.getElAstarta().isActive()) {
                            if(night.getElAstarta().getShake() > 8) {
                                buttonXOffset -= (int) (Math.cos(fixedUpdatesAnim * 0.05) * 2 * (night.getElAstarta().getShake() - 8));
                                buttonYOffset -= (int) (Math.sin(fixedUpdatesAnim * 0.05) * (night.getElAstarta().getShake() - 8));
                            }
                        }
                        for(Door door : night.getDoors().values().stream().toList()) {
                            if(door.getPercentClosed() > 0) {
                                float percentClosed = door.getPercentClosed();
                                BufferedImage image = door.getClosedDoorTexture().request();

                                if (image.getHeight() * percentClosed >= 1) {
                                    if(door.getPercentClosed() < 1) {
                                        image = image.getSubimage(0, (int) (image.getHeight() * (1 - percentClosed)), image.getWidth(), (int) (image.getHeight() * percentClosed));
                                    }
                                    
                                    graphics2D.drawImage(image, offset + door.getClosedDoorLocation().x, door.getClosedDoorLocation().y, null);
                                }
                            }
                            if(!door.isClosed() && door.isHovering() && flashlightCondition) {
                                Polygon hitbox = new Polygon(door.getHitbox().xpoints, door.getHitbox().ypoints, door.getHitbox().npoints);
                                hitbox.translate(offset, 0);
                                graphics2D.fillPolygon(hitbox);
                            }

                            BufferedImage button = doorButton[door.isClosed() ? 1 : 0].request();
                            if(night.isTimerModifier()) {
                                button = timerDoorButton.request();
                            }
                            
                            float size = door.getVisualSize();
                            int xAdd = 0;
                            int yAdd = 0;
                            if(size != 1F) {
                                button = resize(button, (int) (51 * size), (int) (51 * size), Image.SCALE_SMOOTH);
                                xAdd = 25 - (int) (25.5 * size);
                                yAdd = 25 - (int) (25.5 * size);
                            }
                            
                            graphics2D.drawImage(button, offset + door.getButtonLocation().x + buttonXOffset + xAdd, door.getButtonLocation().y + buttonYOffset + yAdd, null);
                        }
                        
                        if(type.isBasement()) {
                            graphics2D.drawImage(pipe.request(), offset + e.pipe.x, e.pipe.y, e.pipe.width, e.pipe.height, null);
                        }
                        
                        // WATER PIPE DROPS
                        if((night.getEvent() == GameEvent.FLOOD && !night.getShark().floodReceding) || (night.getEvent() == GameEvent.DEEP_FLOOD && !night.getDsc().floodReceding)) {
                            int anchorY = (int) (e.pipe.y + e.pipe.height * 0.75);

                            if(anchorY < currentWaterLevel + 20) {
                                int anchorX = (int) (offset + e.pipe.x + e.pipe.width * 0.94);
                                graphics2D.setColor(currentWaterColor);

                                int count = Math.min(32, (640 - currentWaterLevel) * 2);

                                for (int i = 0; i < count; i++) {
                                    int dropY = (int) (anchorY + Math.random() * (640 - anchorY));

                                    if (dropY < currentWaterLevel + 32) {
                                        int range = (dropY - anchorY) / 5;
                                        int dropX = (int) (anchorX + Math.random() * (20 + range) - 10 - range / 2);
                                        int size = (int) (Math.random() * 8 + 8);

                                        graphics2D.fillOval(dropX - size / 2, dropY - size / 2, size, (int) (size * 1.2));
                                    }
                                }
                            }
                        }


                        if (night.getMirrorCat().isActive()) {
                            graphics2D.drawImage(mirrorCatImg, offset + night.getMirrorCat().getX() + currentWaterPos * 2, 540 - waterLevel(), null);
                            graphics2D.drawImage(mirrorCage[night.getMirrorCat().isClosed() ? 0 : 1], offset + 100 + night.getMirrorCat().getX() + currentWaterPos * 2, 540 - waterLevel(), null);
                            
                            if(night.getMirrorCat().isFirst()) {
                                if(night.getMirrorCat().isInside(rescaledPoint)) {
                                    BufferedImage image = mirrorCatRmb.request();
                                    if(mirror) {
                                        image = mirror(image, 1);
                                    }
                                    graphics2D.drawImage(image, offset + night.getMirrorCat().getX() + currentWaterPos * 2 + 55, 540 - waterLevel() + 4, null);
                                }
                            }
                        }
                        if (night.getMirrorCat().isExploded()) {
                            graphics2D.drawImage(mirrorCatExplode, offset + night.getMirrorCat().getX() + currentWaterPos * 2, 540 - waterLevel(), null);
                        }

                        
                        if (fan.isEnabled()) {
                            drawFan(graphics2D, offset, e);
                        }
                        if (soda.isEnabled()) {
                            graphics2D.drawImage(sodaImg, offset + e.soda.x, e.soda.y, e.soda.width, e.soda.height, null);

                            if (night.getColaCat().isActive()) {
                                graphics2D.setComposite(AlphaComposite.SrcOver.derive(night.getColaCat().currentState * 0.05F));
                                graphics2D.drawImage(colaImg.request(), offset + e.soda.x, e.soda.y, e.soda.width, e.soda.height, null);
                                graphics2D.setComposite(AlphaComposite.SrcOver.derive(1F));
                            }
                        }

                        
                        for (byte i = 0; i < 2; i++) {
                            if (corn[i].isEnabled()) {
                                graphics2D.drawImage(corn[i].getImage(), offset + corn[i].getX(), 380 - waterLevel(), null);
                            }
                        }
                        if (maxwell.isEnabled()) {
                            BufferedImage maxwell = rotate(maxwellIcon, (int) (Math.sin(maxwellCounter) * 30));
                            graphics2D.drawImage(maxwell, offset + e.maxwells.x - maxwell.getWidth() / 2, e.maxwells.y - maxwell.getHeight() / 2 - waterLevel(), null);
                        }
                        if (birthdayMaxwell.isEnabled()) {
                            BufferedImage maxwell = rotate(birthdayMaxwellIcon, (int) (Math.sin(maxwellCounter) * 30));
                            graphics2D.drawImage(maxwell, offset + e.maxwells.x - maxwell.getWidth() / 2, e.maxwells.y - maxwell.getHeight() / 2 - waterLevel(), null);
                        }
                        

                        if(!hallucinations.isEmpty()) {
                            int halfMaxOffset = maxOffset / 2;

                            for(Hallucination h : hallucinations.stream().toList()) {
                                int x = (int) ((offset + halfMaxOffset) / (h.getZ()) - halfMaxOffset) + h.getX();
                                graphics2D.drawImage(vertWobble(h.getImage().request(), 30, 4, 0.04F, 1.5F), x, h.getY(), null);
                            }
                        }
                        
                        
                        if(night.getEvent() == GameEvent.MR_MAZE) {
                            MrMaze mrMaze = night.getMrMaze();
                            
                            int n2 = fixedOffsetX - (fixedUpdatesAnim % 1480) - maxOffset;

                            graphics2D.drawImage(currentWaterImage, n2, mrMaze.waterHeight, null);
                            graphics2D.drawImage(currentWaterImage, n2 + 1480, mrMaze.waterHeight, null);
                        }
                        
                        if(currentWaterLevel < 640) {
                            int n2 = fixedOffsetX - currentWaterPos - maxOffset;

                            graphics2D.drawImage(currentWaterImage, n2, currentWaterLevel, null);
                            graphics2D.drawImage(currentWaterImage, n2 + 1480, currentWaterLevel, null);
                        }

                        switch (night.getEvent()) {
                            case FLOOD -> {
                                graphics2D.drawImage(koi.request(), 460, 511 - currentWaterLevel, null);
                            }
                            case DEEP_FLOOD -> {
                                DeepSeaCreature dsc = night.getDsc();
                                
                                graphics2D.setColor(currentWaterColor2);
                                graphics2D.fillRect(0, currentWaterLevel + 126, 1080, 640 - (currentWaterLevel + 126));

                                if(dsc.isFight()) {
                                    if(dsc.isFlash()) {
                                        graphics2D.setColor(Color.WHITE);
                                        graphics2D.fillRect(0, 0, 1080, 640);
                                    }
                                    
                                    if(dsc.isActive() || dsc.getY() > 0) {
                                        int x = (int) (offset + dsc.getX());
                                        int y = (int) (540 - Math.sin(dsc.getCurrentFunction()) * Math.cos(dsc.getCurrentFunction() / 2) * 320 + dsc.getY());
                                        float z = dsc.getZ();

                                        graphics2D.drawImage(deepSeaCreatureImage.request(), x + 250 - (int) (333 * z), y - (int) (333 * z), (int) (667 * z), (int) (667 * z), null);
                                    }
                                    
                                    Vector2D end = dsc.getEndVector();
                                    
                                    double radians = Math.asin(end.y);

                                    graphics2D.setColor(Color.RED);
                                    Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                                            0, new float[] {12}, 0);
                                    graphics2D.setStroke(dashed);
                                    graphics2D.drawLine(540, 209, (int) (540 + end.x * 720), (int) (209 + end.y * 720));
                                    graphics2D.setStroke(new BasicStroke());


                                    Point chainStart = new Point(540, 132);
                                    Point chainEnd = new Point((int) (540 + end.x * 112), (int) (209 + end.y * 112));

                                    List<Point> pointList = new ArrayList<>(List.of(chainStart));
                                    for(float i = 0.09F; i < 0.98F; i += 0.09F) {
                                        pointList.add(new Point((int) (chainStart.x * (1 - i) + chainEnd.x * i), (int) (chainStart.y * (1 - i) + chainEnd.y * i)));
                                    }
                                    pointList.add(chainEnd);

                                    for(int i = 0; i < pointList.size(); i++) {
                                        Point point = pointList.get(i);
                                        float j = (5.5F - Math.abs(5.5F - i)) / 5.5F;
                                        j = (float) Math.sin(j * 1.6) / 2F;
                                        float g = i / 11F;

                                        Point newPoint = new Point(point.x, (int) (point.y + j * 30 - g * 11));
                                        pointList.set(i, newPoint);
                                    }

                                    for(int i = 1; i < pointList.size(); i++) {
                                        Point point = pointList.get(i - 1);

                                        Vector2D lastChain = new Vector2D(point.x, point.y);
                                        Vector2D newChain = new Vector2D(pointList.get(i).x, pointList.get(i).y);
                                        newChain.subtract(lastChain);
                                        newChain.normalize();

                                        BufferedImage image = rotateRadians(harpoonChainPiece.request(), Math.asin(newChain.y), true);
                                        if(newChain.x < 0) {
                                            image = mirror(image, 1);
                                        }
                                        graphics2D.drawImage(image, point.x - image.getWidth() / 2, point.y - image.getHeight() / 2, null);
                                    }

                                    if(dsc.getGunExtend() > 0) {
                                        BufferedImage spear = rotateRadians(harpoonSpear.request(), radians, true);
                                        double newRadians = radians;
                                        if (end.x < 0) {
                                            spear = mirror(spear, 1);
                                            newRadians = Math.PI - newRadians;
                                            
                                        }
                                        int gunLength = dsc.getGunExtend();
                                        
                                        graphics2D.drawImage(spear, 540 - spear.getWidth() / 2 + (int) (Math.cos(newRadians) * gunLength), 200 - spear.getHeight() / 2 + (int) (Math.sin(newRadians) * gunLength), null);


                                        BufferedImage gun = rotateRadians(harpoonGun.request(), radians, true);
                                        if (end.x < 0) {
                                            gun = mirror(gun, 1);
                                        }
                                        graphics2D.drawImage(gun, 540 - gun.getWidth() / 2, 200 - gun.getHeight() / 2, null);
                                    } else {
                                        
                                        BufferedImage gun = rotateRadians(harpoonGunOld.request(), radians, true);
                                        if (end.x < 0) {
                                            gun = mirror(gun, 1);
                                        }
                                        graphics2D.drawImage(gun, 540 - gun.getWidth() / 2, 200 - gun.getHeight() / 2, null);
                                    }
                                    
                                    
                                    graphics2D.drawImage(harpoonBase.request(), 470, 0, null);
                                }
                            }
                            case A120 -> graphics2D.drawImage(a120Img.request(), fixedOffsetX - 200 + night.getA120().getX(), 280, null);

                            case ASTARTA -> {
                                AstartaBoss ab = night.getAstartaBoss();
                                if(ab.isFighting()) {
                                    if(!ab.getMister().isAttacking()) {
                                        if(ab.getDyingStage() >= 6) {
                                            if(ab.getEndingHoleSize() > 0.01F) {
                                                int size = (int) (400 * ab.getEndingHoleSize());
                                                int x = offset + 740 - size / 2;
                                                int y = 320 - size / 2;

                                                if(ab.getDyingStage() == 9 || ab.getDyingStage() == 10) {
                                                    x += (int) (Math.cos(fixedUpdatesAnim * 0.02) * 16 + Math.random() * 2 - 1);
                                                    y += (int) (Math.sin(fixedUpdatesAnim * 0.02) * 16 + Math.random() * 2 - 1);
                                                }

                                                graphics2D.drawImage(astartaBlackHole[0].request(), x, y, size, size, null);
                                                graphics2D.setComposite(AlphaComposite.SrcOver.derive((float) (Math.sin(fixedUpdatesAnim * 0.1 + Math.random()) / 2 + 0.5)));
                                                graphics2D.drawImage(astartaBlackHole[1].request(), x, y, size, size, null);
                                                graphics2D.setComposite(AlphaComposite.SrcOver);

                                                if(ab.getDyingStage() >= 10) {
                                                    int alpha = (int) (255 * ab.getEndingTextAlpha());

                                                    int sin = (int) (Math.sin((fixedUpdatesAnim + fixedOffsetX) * 0.02) * 8);
                                                    int cos = (int) (Math.cos((fixedUpdatesAnim + fixedOffsetX) * 0.02) * 8);

                                                    Color white = new Color(255, 255, 255, alpha);
                                                    Color gray = new Color(128, 128, 128, alpha);

                                                    if(ab.getDyingStage() == 11) {
                                                        sin = 0;
                                                        cos = 0;

                                                        if ((fixedUpdatesAnim / 8) % 2 == 0) {
                                                            white = white.darker();
                                                            gray = gray.darker();
                                                        }
                                                    }

                                                    BufferedImage text = new BufferedImage(700, 640, BufferedImage.TYPE_INT_ARGB);
                                                    Graphics2D textGraphics = (Graphics2D) text.getGraphics();
                                                    textGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                                                    textGraphics.setFont(comicSans60);
                                                    textGraphics.setColor(white);
                                                    textGraphics.drawString(getString("wouldYouLikeToEnter"), 350 - halfTextLength(textGraphics, getString("wouldYouLikeToEnter")), 140 + 40 * ab.getEndingTextAlpha() + sin);
                                                    textGraphics.drawString(getString("theVoid?"), 350 - halfTextLength(textGraphics, getString("theVoid?")), 200 + 40 * ab.getEndingTextAlpha() + sin);

                                                    textGraphics.setFont(comicSans80);
                                                    textGraphics.setColor(ab.getEndingChoice() ? white : gray);

                                                    String yes = getString("Yes");
                                                    String no = getString("No");

                                                    textGraphics.drawString("1. " + (ab.getEndingChoice() ? yes : no), 320 - textLength(textGraphics, "1. " + (ab.getEndingChoice() ? yes : no)), 630 - 40 * ab.getEndingTextAlpha() + cos);
                                                    textGraphics.setColor(ab.getEndingChoice() ? gray : white);
                                                    textGraphics.drawString("2. " + (ab.getEndingChoice() ? no : yes), 380, 630 - 40 * ab.getEndingTextAlpha() + cos);

                                                    textGraphics.dispose();
                                                    graphics2D.drawImage(mirror(text, 1), offset + 390, 0, null);
                                                }
                                            }
                                        }

                                        int randomX = (int) Math.round(Math.random() * 8);
                                        int randomY = (int) Math.round(Math.abs(Math.random() * 8));

                                        switch (ab.getPhase()) {
                                            case 1 -> {
                                                BufferedImage img = sastartaTank[0];
                                                if(ab.getDyingStage() >= 2) {
                                                    img = vertWobble(img, 20, 40, 0.02F, 0.5F);
                                                    img = vertWobble(img, -20, 40, 0.01F, 0.5F);

                                                    if(night.getAstartaBoss().getDyingStage() >= 7) {
                                                        float size = night.getAstartaBoss().getEndingAstartaSize();
                                                        if(size > 0.01F) {
                                                            img = resize(img, (int) (size * 800), (int) (size * 640), BufferedImage.SCALE_FAST);
                                                            randomX = - 380 + (int) (740 - 400 * size);
                                                            randomY = (int) (320 - 320 * size);
                                                        } else {
                                                            img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                                                        }
                                                    }
                                                }
                                                graphics2D.drawImage(img, offset + 380 + randomX, randomY, null);
                                            }
                                            case 2 -> {
                                                graphics2D.drawImage(sastartaFast.request(), offset + ab.getX() + randomX, 140 + randomY, null);
                                            }
                                        }

                                        for (int i = 0; i < ab.getMinecarts().size(); i++) {
                                            AstartaMinecart cart = ab.getMinecarts().get(i);
                                            int x = offset + cart.getX();
                                            if (x > -500 && x < 1480) {
                                                graphics2D.drawImage(astartaMinecart.request(), x, 540 + ab.getMinecartYadd(), null);
                                                if(cart.getShine() > 0) {
                                                    graphics2D.drawImage(alphaify(astartaMinecartWhite, cart.getShine()), x, 540 + ab.getMinecartYadd(), null);
                                                }

                                                BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                                                switch (cart.getItem()) {
                                                    case MSI -> image = msiImage[0];
                                                    case SCARYCAT -> image = scaryCatImage[1].request();
                                                    case WIRES -> image = purplify(wiresImg.request());
                                                    case MIRRORCAT -> image = mirrorCatImg;
                                                    case SODA -> image = sodaImg;
                                                    case MINISODA -> image = miniSodaImg;
                                                    case SOUP -> image = soupItemImg;
                                                    case MISTER -> image = misterImg.request();
                                                }
                                                graphics2D.drawImage(image, x + 250 - image.getWidth() / 2, 590 + ab.getMinecartYadd() - image.getHeight(), null);
                                            }
                                        }

                                        for(int i = 0; i < ab.getBlackHoles().size(); i++) {
                                            AstartaBlackHole blackHole = ab.getBlackHoles().get(i);
                                            int size = (int) (400 * blackHole.getSize());
                                            graphics2D.drawImage(astartaBlackHole[0].request(), offset + blackHole.getX() - size / 2, blackHole.getY() - size / 2, size, size, null);
                                            graphics2D.setComposite(AlphaComposite.SrcOver.derive((float) (Math.sin(fixedUpdatesAnim * 0.1 + Math.random()) / 2 + 0.5)));
                                            graphics2D.drawImage(astartaBlackHole[1].request(), fixedOffsetX - 400 + blackHole.getX() - size / 2, blackHole.getY() - size / 2, size, size, null);
                                            graphics2D.setComposite(AlphaComposite.SrcOver);
                                        }

                                        for (int i = 0; i < ab.getUncannyBoxes().size(); i++) {
                                            AstartaUncannyBox box = ab.getUncannyBoxes().get(i);
                                            int rand = (int) (Math.random() * 7 + 1);
                                            graphics2D.drawImage(uncannyBox.request().getScaledInstance(300 / rand, 350 / rand, Image.SCALE_FAST), fixedOffsetX - 400 + box.getX(), box.getY(), 300, 350, null);
                                        }

                                        if (ab.getMister().isSpawned()) {
                                            Mister mister = ab.getMister();
                                            Point point = mister.getPoint();

                                            if (mister.isBeingHeld()) {
                                                graphics2D.setColor(black120);
                                                graphics2D.fillRect(0, 0, 1080, 640);

                                                Rectangle h = ab.getVisualHitbox();
                                                graphics2D.drawImage(astartaTarget.request(), h.x + h.width / 2 - 60 + randomX / 2, h.y + h.height / 2 - 60 + randomY / 2, null);
                                            }
                                            if (ab.isFirstMister()) {
                                                graphics2D.drawImage(misterText.request(), offset + point.x - 50, (int) (point.y + 180 + Math.round(Math.random())), null);
                                            }
                                            graphics2D.drawImage(misterImg.request(), offset + point.x, point.y, null);
                                            if (mister.getBloomTransparency() > 0) {
                                                graphics2D.drawImage(alphaify(misterGlowingImg.request(), mister.getBloomTransparency()), offset + point.x, point.y, null);
                                            }

                                            BufferedImage timer = new BufferedImage(300, 60, BufferedImage.TYPE_INT_ARGB);
                                            Graphics2D timerGraphics = (Graphics2D) timer.getGraphics();
                                            timerGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                                            timerGraphics.setFont(comicSans50);
                                            String countdown = (Math.round(Math.max(0, mister.getCountdown()) * 100F) / 100F) + getString("s");
                                            if (mister.getCountdown() < mister.getStartCountdown() / 3) {
                                                timerGraphics.setColor(Color.RED);
                                            } else if (mister.getCountdown() < mister.getStartCountdown() / 3 * 2) {
                                                timerGraphics.setColor(Color.ORANGE);
                                            } else {
                                                timerGraphics.setColor(Color.WHITE);
                                            }
                                            timerGraphics.drawString(countdown, 100 - halfTextLength(graphics2D, countdown), 55);
                                            timerGraphics.dispose();
                                            if(mirror) {
                                                timer = mirror(timer, 1);
                                            }
                                            
                                            graphics2D.drawImage(timer, offset + point.x, point.y - 40, null);
                                        }
                                    } else { // if mister is attacking
                                        Mister mister = night.getAstartaBoss().getMister();
                                        graphics2D.drawImage(mister.getImage().request(), 0, 0, 1080, 640, null);

                                        if(mister.getFlashAlpha() > 0) {
                                            graphics2D.setColor(new Color(255, 255, 255, mister.getFlashAlpha()));
                                            graphics2D.fillRect(0, 0, 1080, 640);
                                        }
                                    }

                                    if(ab.isRoulette()) {
                                        graphics2D.drawImage(rouletteBackground.request(), 0, 0, null);
                                        graphics2D.drawImage(rouletteScreen, 275, 223, null);

                                        BufferedImage text = new BufferedImage(980, 60, BufferedImage.TYPE_INT_ARGB);
                                        Graphics2D textGraphics = (Graphics2D) text.getGraphics();
                                        textGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                                        textGraphics.setColor(Color.WHITE);
                                        textGraphics.setFont(comicSans50);

                                        String str = getString("randomEvent");
                                        if(ab.getRouletteY() >= 9660) {
                                            switch (ab.roulette1[57]) {
                                                case 0 -> str += getString("uncannyDelivery");
                                                case 1 -> str += getString("dvdEvent");
                                                case 2 -> str += getString("blackHoles");
                                            }
                                        } else {
                                            str += "???";
                                        }
                                        textGraphics.drawString(str, 490 - halfTextLength(textGraphics, str), 55);
                                        textGraphics.dispose();

                                        graphics2D.drawImage(mirror(text, 1), 50, 145, null);
                                    }
                                } else {
                                    if (ab.isStartingCutscene()) {
                                        double scy = ab.getStartCutsceneY();
                                        int y = (int) Math.max(0, scy + Math.sin(scy / 10) * 15);

                                        graphics2D.drawImage(sastartaTank[1], offset + 380, y, null);

                                        if(shadowCheckpointUsed != 0 && y < 400) {
                                            graphics2D.setFont(comicSans40);
                                            graphics2D.setColor(Color.GRAY);

                                            String pressToSkip = getString("pressToSkip");
                                            graphics2D.drawString(pressToSkip, fixedOffsetX - 200 + 540 - halfTextLength(graphics2D, pressToSkip), 620);
                                        }
                                    } else {
                                        graphics2D.drawImage(sastartaTank[0], offset + 380, 0, null);
                                    }
                                }
                            }
                        }

                        if (night.getType() == GameType.DAY) {
                            int j = night.getEvent() == GameEvent.FLOOD ? waterLevel() * 2 : 0;

                            int clockY = 330;
                            BufferedImage clock = pepitoClock[keyHandler.hoveringPepitoClock ? 1 : 0].request();
                            
                            if(pepitoClockProgress > 120) {
                                if (pepitoClockProgress < 141) {
                                    int pepitoYSqrt = pepitoClockProgress - 120;
                                    clockY = -140 + pepitoYSqrt * pepitoYSqrt;
                                    
                                    clock = rotate(clock, 357 - (pepitoClockProgress - 120) * 17);
                                } else { // if pepito clock progress >= 141
                                    if (pepitoClockProgress < 160) {
                                        double sine = Math.sin((pepitoClockProgress - 141) / 6d);
                                        clockY = (int) (330 - sine * 40);

                                        int oldWidth = clock.getWidth();
                                        clock = resize(clock, clock.getWidth(), clock.getHeight() - (int) (60 * sine), Image.SCALE_SMOOTH);
                                        clockY += (int) ((oldWidth - clock.getWidth()) / 1.5F);
                                    }
                                }
                            } else {
                                clockY = -140;
                            }

                            // will change "- 400" to "- maxOffset" whenever i feel like it idc
                            // graphics2D.drawImage(clock, fixedOffsetX - 400 + 625, clockY, null);
                            // 23.11.2024 - history has been made
                            graphics2D.drawImage(clock, fixedOffsetX - maxOffset + 465, clockY, null);
                            
                            if(pepitoClockProgress >= 160) {
                                graphics2D.setColor(Color.BLACK);
                                graphics2D.setStroke(new BasicStroke(6));
                                double hourClockRadians = Math.toRadians(night.getSeconds() / 120F - 90);
                                graphics2D.drawLine(fixedOffsetX - 400 + 517, 416, fixedOffsetX - 400 + (int) (517 + Math.cos(hourClockRadians) * 10), (int) (416 + Math.sin(hourClockRadians) * 10));

                                graphics2D.setColor(new Color(20, 20, 20));
                                graphics2D.setStroke(new BasicStroke(4));
                                double minuteClockRadians = Math.toRadians(night.getSeconds() / 10F - 90);
                                graphics2D.drawLine(fixedOffsetX - 400 + 517, 416, fixedOffsetX - 400 + (int) (517 + Math.cos(minuteClockRadians) * 30), (int) (Math.max(176, 416 + Math.sin(minuteClockRadians) * 30)));

                                graphics2D.setColor(Color.RED);
                                graphics2D.setStroke(new BasicStroke(2));
                                double secondClockRadians = Math.toRadians(night.getSeconds() * 6 - 87);
                                graphics2D.drawLine(fixedOffsetX - 400 + 517, 416, fixedOffsetX - 400 + (int) (517 + Math.cos(secondClockRadians) * 25), (int) (Math.max(176, 416 + Math.sin(secondClockRadians) * 25)));
                            }
                            
                            graphics2D.drawImage(!night.isBillyShop() ? millyButton.request() : billyButton.request(), fixedOffsetX + 685, 315 - j, null);
                            
                            if(endless.getNight() == 3) {
                                graphics2D.drawImage(resize(birthdayHatImg, 80, 100, BufferedImage.SCALE_SMOOTH), fixedOffsetX + 835, 260, null);
                            }
                            
                            graphics2D.drawImage(neonSogSign.request(), offset + 20, 0, null);
                            graphics2D.drawImage(neonSog.request(), offset + neonSogX, 170, null);
                        }

                        if(night.isRadiationModifier()) {
                            for(GruggyCart cart : night.gruggyCarts) {
                                graphics2D.drawImage(gruggyCart.request(), (int) (offset + cart.getCurrentX()), 465 - waterLevel(), null);
                            }
                            if(night.gruggyX < 1000) {
                                graphics2D.drawImage(gruggy.request(), offset + 540 + Math.max(0, night.gruggyX), 235 - waterLevel(), null);
                            }
                        }


                        if (soggyBallpitActive) {
                            graphics2D.drawImage(soggyBalls.request(), offset, 290, 1480, 350, null);
                        }
                        if(night.frog.x > -240) {
                            graphics2D.drawImage(frogImg.request(), night.frog.x, 300 + (int) (Math.sin(fixedUpdatesAnim * 0.5) * 40), 240, 300, null);
                        }
                        if(night.env().getBgIndex() != 4) {
                            graphics2D.setClip(0, 80, 1080, 560);
                            for (Balloon balloon : balloons.stream().toList()) {
                                if (offset + balloon.getX() > -90 && offset + balloon.getX() < 1080) {
                                    if (balloon.alpha < 1) {
                                        graphics2D.setComposite(AlphaComposite.SrcOver.derive(balloon.alpha));
                                    }
                                    graphics2D.drawImage(balloon.getImage(), offset + balloon.getX(), 200 + balloon.getAdder(), null);
                                    if (balloon.alpha < 1) {
                                        graphics2D.setComposite(AlphaComposite.SrcOver);
                                    }
                                }
                            }
                            graphics2D.setClip(0, 0, 1080, 640);
                        }

                        switch (night.getEvent()) {
                            case DEEP_FLOOD -> {
                                if (!night.getDsc().isFlash()) {
                                    int n1 = fixedOffsetX + currentWaterPos - maxOffset;

                                    graphics2D.drawImage(currentWaterImage, n1, currentWaterLevel, null);
                                    graphics2D.drawImage(currentWaterImage, n1 - 1480, currentWaterLevel, null);

                                    graphics2D.setColor(currentWaterColor2);
                                    graphics2D.fillRect(0, currentWaterLevel + 126, 1080, 640 - (currentWaterLevel + 126)); 
                                }
                            }
                            case MILLY_ARRIVES_BASEMENT -> {
                                Basement env = (Basement) night.env();

                                graphics2D.drawImage(millySkateboard.request(), offset + (int) (env.getMillyX()), 220, 500, 420, null);
                                
                                if(fan.isEnabled()) {
                                    drawFan(graphics2D, offset, e);
                                }
                                if (soda.isEnabled()) {
                                    graphics2D.drawImage(sodaImg, offset + e.soda.x, e.soda.y, e.soda.width, e.soda.height, null);

                                    if (night.getColaCat().isActive()) {
                                        graphics2D.setComposite(AlphaComposite.SrcOver.derive(night.getColaCat().currentState * 0.05F));
                                        graphics2D.drawImage(colaImg.request(), offset + e.soda.x, e.soda.y, e.soda.width, e.soda.height, null);
                                        graphics2D.setComposite(AlphaComposite.SrcOver.derive(1F));
                                    }
                                }
                            }
                            default -> {
                                int n1 = fixedOffsetX + currentWaterPos - maxOffset;

                                graphics2D.drawImage(currentWaterImage, n1, currentWaterLevel, null);
                                graphics2D.drawImage(currentWaterImage, n1 - 1480, currentWaterLevel, null);
                            }
                        }


                        if(night.getEvent() == GameEvent.MR_MAZE) {
                            MrMaze mrMaze = night.getMrMaze();
                            int n1 = fixedOffsetX + (fixedUpdatesAnim % 1480) - maxOffset;

                            graphics2D.drawImage(currentWaterImage, n1, mrMaze.waterHeight, null);
                            graphics2D.drawImage(currentWaterImage, n1 - 1480, mrMaze.waterHeight, null);
                        }

                        
                        if(soggyPen.isEnabled()) {
                            if(night.soggyPenCanvas != null) {
                                try {
                                    graphics2D.setComposite(MultiplyComposite.Multiply);
                                    graphics2D.drawImage(night.soggyPenCanvas.getSubimage(maxOffset - fixedOffsetX, 0, 1080, 640), 0, 0, null);
                                    graphics2D.setComposite(AlphaComposite.SrcOver);
                                } catch (RasterFormatException rfe) {
                                    fillSoggyPenCanvas();
                                }
                            }
                        }
                    }
                } catch (Exception wompwomp) {
                    wompwomp.printStackTrace();
                }
            }

            case ITEMS -> {
                if(!everySecond20th.containsKey("startSimulation")) {
                    if (type == GameType.CUSTOM) {
                        redrawItemsMenu();
                    }
                    
                    graphics2D.drawImage(itemsMenu, 0, 0, null);
                    String limitAdd = "";

                    graphics2D.setColor(black120);
                    // 130;130 cell
                    byte y = 0;
                    while (y < rows) {
                        byte x = 0;
                        while (x < columns) {
                            try {
                                if (itemList.get(x + (y * columns)).isSelected()) {
                                    graphics2D.setColor(black140);
                                    graphics2D.fillRoundRect(x * 170 + 37, y * 170 + (207 - 40 * Math.min(rows, 4)) - itemScrollY, 136, 136, 50, 50);
                                }
                            } catch (Exception ignored) {
                            }

                            if (!startButtonSelected) {
                                try {
                                    if (selectedItemX == x && selectedItemY == y) {
                                        graphics2D.setColor(white120);
                                        graphics2D.fillRoundRect(x * 170 + 37, y * 170 + (207 - 40 * Math.min(rows, 4)) - itemScrollY, 136, 136, 50, 50);

                                        Item item = itemList.get(selectedItemX + (selectedItemY * columns));
                                        
                                        graphics2D.setColor(Color.WHITE);
                                        if (item.isSelected()) {
                                            if (selectedItemX == x && selectedItemY == y) {
                                                graphics2D.setFont(yuGothicBold25);
                                                graphics2D.drawString(getString("equipped"), equippedX, 245 + item.getDescription().split("\n").length * 30 - 30);
                                            }
                                            if (itemList.get(x + (y * columns)).getItemLimitAdd() > 0) {
                                                limitAdd = " (+" + itemList.get(x + (y * columns)).getItemLimitAdd() + ")";
                                            }
                                        }

                                        if (item.getAmount() != 0) {
                                            graphics2D.setFont(yuGothicPlain60);
                                            int halfTextLength = halfTextLength(graphics2D, item.getName());
                                            int start = 890 - halfTextLength;
                                            if(890 + halfTextLength > 1080) {
                                                start = 1080 - halfTextLength * 2;
                                            }
                                            graphics2D.drawString(item.getName(), start, 130);


                                            graphics2D.setFont(yuGothicPlain30);
                                            byte i = 0;
                                            while (i < item.getDescription().split("\n").length) {
                                                graphics2D.drawString(item.getDescription().split("\n")[i], 890 - halfTextLength(graphics2D, item.getDescription().split("\n")[i]), 190 + i * 30);
                                                i++;
                                            }

                                            byte j = 0;
                                            List<ItemTag> list = new ArrayList<>(item.getTags());
                                            list.remove(ItemTag.PASSIVE);
                                            list.remove(ItemTag.TRIGGER);
                                            list.remove(ItemTag.EXPEND);
                                            while (j < list.size()) {
                                                ItemTag tag = list.get(j);
                                                graphics2D.drawImage(itemTags[tag.getOrder()], 735 + 60 * j, 345, 50, 50, null);
                                                j++;
                                            }
                                        }
                                    }
                                } catch (Exception ignored) {
                                }
                            }

                            try {
                                Item item = itemList.get(x + (y * columns));
                                
                                if (item.isMarkedConflicting()) {
                                    byte xAdd = 0;
                                    byte yAdd = 0;
                                    if (item.getShakeIntensity() > 0) {
                                        byte intensity = (byte) (item.getShakeIntensity() / 3);
                                        xAdd = (byte) ((Math.random() * intensity * 2) - intensity);
                                        yAdd = (byte) ((Math.random() * intensity) - intensity / 2);
                                    }
                                    graphics2D.drawImage(conflictingItem.request(), x * 170 + 40 + xAdd, y * 170 + (210 - 40 * Math.min(rows, 4)) - itemScrollY + yAdd, null);
                                }
                            } catch (Exception ignored) {
                            }
                            x++;
                        }
                        y++;
                    }

                    graphics2D.setColor(Color.WHITE);
                    graphics2D.setFont(yuGothicPlain30);
                    graphics2D.drawString(getString("selectedItems") + checkItemsAmount() + "/" + itemLimit + limitAdd, 735, 425);

                    graphics2D.setFont(yuGothicBoldItalic25);
                    if (!(type == GameType.CUSTOM && CustomNight.isCustom())) {
                        graphics2D.setColor(warningRed);
                        graphics2D.drawString(getString("itemsWarning1"), 735, 460);
                        graphics2D.drawString(getString("itemsWarning2"), 735, 490);
                    } else {
                        graphics2D.setColor(Color.GREEN);
                        graphics2D.drawString(getString("itemsNotConsumed1"), 735, 460);
                        graphics2D.drawString(getString("itemsNotConsumed2"), 735, 490);
                    }
                }
                
                if(type == GameType.ENDLESS_NIGHT && neonSogSkips > 0) {
                    graphics2D.setColor(Color.WHITE);
                    graphics2D.setFont(comicSans40);
                    String text = getString("neonSogSkipsLeft") + neonSogSkips + " / 5";
                    graphics2D.drawString(text, 540 - halfTextLength(graphics2D, text), 45);
                }
            }
            case BINGO -> {
                graphics2D.drawImage(camStates[0], 0, 0, null);
                graphics2D.drawImage(bingoCardImg, 500, 20, null);

                if(bingoCard.isCompleted() || bingoCard.isFailed()) {
                    graphics2D.setColor(white120);
                    graphics2D.fillRoundRect(500, 78, 540, 542, 28, 25);
                }
            }
            case ACHIEVEMENTS -> {
                graphics2D.drawImage(camStates[0], 0, 0, null);

                graphics2D.setFont(comicSans80);
                graphics2D.setColor(Color.WHITE);

                if(achievementState || shiftingAchievements) {
                    graphics2D.drawString(getString("statistics"), 1750 - achievementsScrollX, 85);
                    graphics2D.fillRect(1740 - achievementsScrollX, 105, 400, 5);
                    graphics2D.drawImage(mirror(achievementMenuArrow, 1), 1080 - achievementsScrollX, 0, null);

                    int j = Math.min(115, statisticsScrollY);

                    graphics2D.setColor(black80);
                    graphics2D.fillRect(1235 - achievementsScrollX, 115 - j, 925, 525 + j);

                    graphics2D.setColor(Color.WHITE);
                    graphics2D.fillRect(1235 - achievementsScrollX, 115 - j, 5, 525 + j);

                    graphics2D.setFont(comicSans40);

                    int i = 0;
                    for(Statistics statistic : Statistics.values()) {
                        int y = 150 + i * 40 - statisticsScrollY;
                        
                        if(y < 690 && y > -50) {
                            graphics2D.setColor(i % 2 == 0 ? Color.WHITE : new Color(190, 190, 190));

                            String value = "" + statistic.getValue();

                            if (statistic == Statistics.PLAYTIME) {
                                int seconds = statistic.getValue() % 60;
                                int minutes = (statistic.getValue() / 60) % 60;
                                int hours = statistic.getValue() / 3600;
                                value = hours + getString("h") + " " + minutes + getString("m") + " " + seconds + getString("s");
                            }

                            String string = getString(statistic.toString().toLowerCase(Locale.ROOT) + "St");

                            graphics2D.drawString(string + ": " + value, 1250 - achievementsScrollX, y);
                        }
                        i++;
                    }

                    if(keyHandler.pointerPosition.x < 140 * widthModifier + centerX) {
                        graphics2D.setColor(white60);
                        graphics2D.fillRect(1080 - achievementsScrollX, 0, 140, 640);
                        graphics2D.setColor(Color.WHITE);
                    }
                }
                if(!achievementState || shiftingAchievements) {
                    graphics2D.setFont(comicSans80);
                    graphics2D.setColor(Color.WHITE);

                    int x = 20 - achievementsScrollX; // optimization purposes i think
                    graphics2D.fillRect(x, 105, 920, 5);
                    x += 30; // result: 50 - achievementScrollX
                    graphics2D.drawString(getString("achievements") + " (" + achievementPercentage + "%)", x, 85);
                    x += 890; // result: 940 - achievementScrollX
                    graphics2D.drawImage(achievementMenuArrow, x, 0, null);

                    if(keyHandler.pointerPosition.x > 940 * widthModifier + centerX) {
                        graphics2D.setColor(white60);
                        graphics2D.fillRect(x, 0, 140, 640);
                    }

                    if(achievementsScrollX < 900) {
                        if (shiftingAchievements) {
                            graphics2D.drawImage(achievementDisplayARGB, 20 - achievementsScrollX, 110, null);
                        } else {
                            graphics2D.drawImage(achievementDisplay, 20 - achievementsScrollX, 110, null);
                        }
                    }

                    if(Achievements.SHADOWNIGHT.isObtained()) {
                        Vector2D cursor = new Vector2D(rescaledPoint.x, rescaledPoint.y);
                        Vector2D trophyVector = new Vector2D(1010 - achievementsScrollX, 530);
                        float distance = (float) trophyVector.distance(cursor);
                        float invert = 1000 / Math.max(50, distance);
                        
                        BufferedImage img = halfShadownightTrophy.request();
                        if(beatShadownightBasement) {
                            img = shadownightTrophy.request();
                        }
                        graphics2D.drawImage(img, 940 - achievementsScrollX + (int) (Math.random() * invert - invert / 2), 449 + (int) (Math.random() * invert - invert / 2), null);
                    }
                }
            }
            case CHALLENGE -> {
                graphics2D.setColor(Color.BLACK);
                graphics2D.fillRect(0, 0, 1080, 640);

                Color deselectedRed = CustomNight.isCustom() ? Color.RED : new Color(140, 0, 0);
                
                int modifiersSize = CustomNight.modifiers.size();
                for (int i = 0; i < 18; i++) {
                    int x = i / 3;
                    int y = i % 3;

                    if (i < modifiersSize) {
                        CustomNightModifier modifier = CustomNight.modifiers.get(i);

                        graphics2D.setColor(modifier.isActive() ? Color.GREEN : deselectedRed);
                        graphics2D.setStroke(new BasicStroke(3));
                        graphics2D.drawRoundRect(675 + 200 * x, 248 + 60 * y, 190, 50, 5, 5);
                        if(!modifier.isActive()) {
                            graphics2D.drawLine(677 + 200 * x, 250 + 60 * y, 865 + 200 * x, 294 + 60 * y);
                        }

                        graphics2D.setColor(Color.WHITE);
                        String name = getString(modifier.getName());
                        graphics2D.setFont(new Font("Comic Sans MS", Font.PLAIN, 52 - name.length() * 2));
                        graphics2D.drawString(name, 765 + 200 * x - halfTextLength(graphics2D, name), 288 + 60 * y);

                        if (CustomNight.selectedElement == modifier) {
                            graphics2D.setColor(CustomNight.isCustom() ? white100 : white60);
                            graphics2D.fillRoundRect(675 + 200 * x, 248 + 60 * y, 190, 50, 5, 5);
                        }
                    }
                }

                graphics2D.setFont(yuGothicPlain60);

                graphics2D.setColor(CustomNight.startSelected ? Color.WHITE : white160);
                graphics2D.drawString(">> " + getString("start"), 830, 520);
                graphics2D.setColor(CustomNight.backSelected ? Color.WHITE : white160);
                graphics2D.drawString(">> " + getString("back"), 830, 605);

//                graphics2D.setColor(Color.WHITE);
//                graphics2D.setStroke(new BasicStroke(3));
//                graphics2D.drawLine(658, 440, 1080, 440);
//                graphics2D.drawLine(658, 0, 658, 440);
//                graphics2D.drawLine(810, 440, 810, 640);
//                graphics2D.drawLine(658, 221, 1080, 221);

                graphics2D.setColor(CustomNight.isCustom() ? Color.GREEN : Color.RED);
                graphics2D.drawRect(5, 580, 365, 55);
                graphics2D.setColor(Color.WHITE);
                graphics2D.setFont(yuGothicPlain50);
                String custom = getString("custom") + ": " + (CustomNight.isCustom() ? getString("on") : getString("off"));
                graphics2D.drawString(custom, 180 - halfTextLength(graphics2D, custom), 625);

                if(CustomNight.customSelected) {
                    graphics2D.setColor(white100);
                    graphics2D.fillRect(3, 578, 370, 60);
                }

                if (CustomNight.getLoadedPreview() != null && CustomNight.selectedElement != null) {
                    graphics2D.drawImage(CustomNight.getLoadedPreview().request(), 660, 0, 420, 220, null);
                }

                BufferedImage enemies = new BufferedImage(661, 641, BufferedImage.TYPE_INT_RGB);
                Graphics2D enGraphics = (Graphics2D) enemies.getGraphics();
                enGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                enGraphics.setColor(Color.WHITE);
                enGraphics.setFont(comicSans30);
                enGraphics.drawString(getString("Enemies"), 270, 35);

                Color gray = CustomNight.isCustom() ? Color.GRAY : new Color(80, 80, 80);
                
                int enemiesSize = CustomNight.getEnemies().size();
                for (int i = 29; i > -1; i--) {
                    int x = 105 * (i % 6);
                    int y = 130 * (i / 6);
                    
                    Color unexistent = new Color(40, 40, 60);

                    if (i >= enemiesSize) {
                        enGraphics.setColor(Color.BLACK);
                        enGraphics.fillRect(20 + x, 40 + y, 95, 120);

                        enGraphics.setColor(unexistent);
                        enGraphics.setStroke(new BasicStroke(5));
                        enGraphics.drawRect(22 + x, 42 + y, 91, 116);

                        enGraphics.setFont(comicSans30);
                        enGraphics.drawString("X", 27 + x, 152 + y);
                    } else {
                        CustomNightEnemy enemy = CustomNight.getEnemies().get(i);

                        if(enemy.otherX != -1) {
                            x = (int) ((x + enemy.otherX * 6) / 7);
                        }
                        if(enemy.otherY != -1) {
                            y = (int) ((y + enemy.otherY * 6) / 7);
                        }

                        boolean enabled = enemy.getAI() > 0;

                        BufferedImage icon = enemy.getIcon().request();
                        if (!enabled) {
                            icon = grayscale(icon);
                        }
                        if (enemy.getWobbleIntensity() > 0) {
                            icon = vertWobble(icon, enemy.getWobbleIntensity(), 1, 1, 10);
                        }
                        enGraphics.drawImage(icon, 20 + x, 40 + y, null);

                        enGraphics.setStroke(new BasicStroke(2));
                        enGraphics.setColor(Color.BLACK);
                        enGraphics.drawRect(25 + x, 45 + y, 85, 110);

                        enGraphics.setColor(enabled ? Color.RED : gray);
                        if(enemy.getAI() > 8) {
                            enGraphics.setColor(Color.YELLOW);
                        }
                        enGraphics.setStroke(new BasicStroke(5));
                        enGraphics.drawRect(22 + x, 42 + y, 91, 116);

                        enGraphics.setFont(comicSans30);
                        enGraphics.drawString("" + enemy.getAI(), 27 + x, 152 + y);

                        if (CustomNight.selectedElement == enemy) {
                            enGraphics.setColor(CustomNight.isCustom() ? white100 : white60);
                            enGraphics.fillRect(20 + x, 40 + y, 96, 121);
                        }
                    }
                }
                enGraphics.dispose();
                
                if(560 - CustomNight.enemiesRectangle.height > 0) {
                    graphics2D.drawImage(enemies.getSubimage(20, CustomNight.enemiesRectangle.height, 630, 560 - CustomNight.enemiesRectangle.height), 20, 420, null);
                    graphics2D.drawImage(challengeBlackFade.request(), 20, 430, null);
                }

                graphics2D.setColor(black140);
                graphics2D.fillRect(660, 180, 420, 40);
                
                graphics2D.setColor(Color.WHITE);
                graphics2D.setFont(comicSans50);
                if(CustomNight.isCustom()) {
                    graphics2D.drawString(getString("SHUFFLE"), 15, 550);
                } else {
                    graphics2D.drawString(getString("PREV"), 15, 550);
                    graphics2D.drawString(getString("NEXT"), 795 - textLength(graphics2D, getString("NEXT")), 550);
                }

                if(!CustomNight.isCustom()) {
                    String str = getString(CustomNight.getSelectedChallengeLocalizeID());

                    graphics2D.setFont(new Font("Comic Sans MS", Font.PLAIN, 80 - str.length() * 2));
                    graphics2D.drawString(str, 405 - halfTextLength(graphics2D, str), 550);
                }
                graphics2D.setFont(comicSans40);
                String challengeText = CustomNight.isCustom() ? getString("customNight") : (getString("challenge") + " " + (CustomNight.getSelectedChallenge() + 1));
                graphics2D.drawString(challengeText, 405 - halfTextLength(graphics2D, challengeText), 475);

                graphics2D.setColor(Color.GRAY);

                if(CustomNight.isCustom()) {
                    graphics2D.drawRect(10, 505, 240, 50);

                    if(CustomNight.shuffleSelected) {
                        graphics2D.setColor(white100);
                        graphics2D.fillRect(10, 505, 240, 50);
                    }
                } else {
                    graphics2D.drawRect(10, 505, 130, 50);
                    graphics2D.drawRect(650, 505, 150, 50);

                    if(CustomNight.prevSelected) {
                        graphics2D.setColor(white100);
                        graphics2D.fillRect(10, 505, 130, 50);
                    }
                    if(CustomNight.nextSelected) {
                        graphics2D.setColor(white100);
                        graphics2D.fillRect(650, 505, 150, 50);
                    }
                }

                graphics2D.setColor(Color.WHITE);

                if (CustomNight.selectedElement != null) {
                    String name = getString(CustomNight.selectedElement.getName());
//                    if(CustomNight.selectedElement instanceof CustomNightEnemy enemy) {
////                        name += " | " + (enemy.getAI() > 0 ? enemy.getAI() : "OFF");
//                        name += " | " + enemy.getAI();
//                    }
                    graphics2D.drawString(name, 665, 215);
                    graphics2D.setFont(comicSans25);
                }
                
                graphics2D.drawImage(enemies.getSubimage(0, 0, 660, CustomNight.enemiesRectangle.height + 1), 0, 0, null);

                graphics2D.setColor(Color.WHITE);
                graphics2D.setStroke(new BasicStroke(3));
                graphics2D.drawLine(658, 440, 1080, 440);
                graphics2D.drawLine(658, 0, 658, 440);
                graphics2D.drawLine(810, 440, 810, 640);
                graphics2D.drawLine(658, 221, 1080, 221);
                // ENEMIES RECTANGLE
                graphics2D.drawLine(658, 440, 658, CustomNight.enemiesRectangle.height);

                if(keyHandler.isInEnemiesRectangle) {
                    float a = (CustomNight.enemiesRectangle.height - 420) / 284F;
                    float g = (float) (a + Math.random() / 10);
                    graphics2D.drawImage(alphaify(challengeCyanFade.request(), g), 0, CustomNight.enemiesRectangle.height, null);

                    graphics2D.setColor(new Color(255, 255, 255, (int) (a * 510)));
                    graphics2D.drawLine(0, CustomNight.enemiesRectangle.height, 658, CustomNight.enemiesRectangle.height);
                }
                
                graphics2D.setStroke(new BasicStroke());
            }
            case SETTINGS -> {
                graphics2D.drawImage(camStates[0], 0, 0, null);

                drawCloseButton(graphics2D);

                graphics2D.setColor(Color.WHITE);

                graphics2D.setFont(yuGothicPlain60);
                graphics2D.drawString(">> " + getString("volume"), 140, 150 + settingsScrollY);

                graphics2D.drawString(">> " + getString("fixedRatio"), 140, 370 + settingsScrollY);
                graphics2D.drawString(">> " + getString("headphones"), 140, 450 + settingsScrollY);
                int oa = textLength(graphics2D, ">> ");
                
                graphics2D.setFont(yuGothicBoldItalic25);
                graphics2D.drawString(getString("deafMode"), 142 + oa, 483 + settingsScrollY);
                graphics2D.setFont(yuGothicPlain60);

                
                graphics2D.drawString(getString("resetNight"), 160, 560 + settingsScrollY);

                graphics2D.drawString(">> " + getString("showDisclaimer"), 140, 690 + settingsScrollY);
                graphics2D.drawString(">> " + getString("showManual"), 140, 770 + settingsScrollY);
                graphics2D.drawString(">> " + getString("saveScreenshots"), 140, 850 + settingsScrollY);
                graphics2D.drawString(">> " + getString("rtx"), 140, 930 + settingsScrollY);
                graphics2D.drawString(">> " + getString("fpsCounter"), 140, 1010 + settingsScrollY);
                graphics2D.drawString(">> " + getString("fpsCap"), 140, 1090 + settingsScrollY);
                graphics2D.drawString(">> " + getString("jumpscareShake"), 140, 1170 + settingsScrollY);
                graphics2D.drawString(">> " + getString("languageSelect"), 140, 1350 + settingsScrollY);
                graphics2D.drawString(">> " + getString("screenShake"), 140, 1450 + settingsScrollY);
                graphics2D.drawString(">> " + getString("disableFlickering"), 140, 1530 + settingsScrollY);
                
                String fpsCap = this.fpsCap + "";
                if(this.fpsCap <= 0) {
                    fpsCap = getString("UNLIMITED");
                }
                graphics2D.drawString(fpsCap, 290 + textLength(graphics2D, getString("fpsCap")), 1090 + settingsScrollY);

                String jumpscareShake = getString("windowAndScreen");
                switch (this.jumpscareShake) {
                    case 1 -> jumpscareShake = getString("screen");
                    case 2 -> jumpscareShake = getString("noShake");
                }
                graphics2D.drawString(jumpscareShake, 245, 1260 + settingsScrollY);

                String language = getString("language");
                graphics2D.drawString(language, 290 + textLength(graphics2D, getString("languageSelect")), 1350 + settingsScrollY);

                graphics2D.setStroke(new BasicStroke(5));
                // fixed ratio
                graphics2D.drawRect(160 + textLength(graphics2D, ">> " + getString("fixedRatio")), 310 + settingsScrollY, 60, 60);
                // headphones
                graphics2D.drawRect(160 + textLength(graphics2D, ">> " + getString("headphones")), 390 + settingsScrollY, 60, 60);
                // reset night
                graphics2D.drawRect(146, 500 + settingsScrollY, 30 + textLength(graphics2D, getString("resetNight")), 80);
                // show disclaimer
                graphics2D.drawRect(160 + textLength(graphics2D, ">> " + getString("showDisclaimer")), 630 + settingsScrollY, 60, 60);
                // show manual
                graphics2D.drawRect(160 + textLength(graphics2D, ">> " + getString("showManual")), 710 + settingsScrollY, 60, 60);
                // save screenshots
                graphics2D.drawRect(160 + textLength(graphics2D, ">> " + getString("saveScreenshots")), 790 + settingsScrollY, 60, 60);
                // bloom
                graphics2D.drawRect(160 + textLength(graphics2D, ">> " + getString("rtx")), 870 + settingsScrollY, 60, 60);
                // fps counter
                graphics2D.drawRect(160 + textLength(graphics2D, ">> " + getString("fpsCounter")), 950 + settingsScrollY, 60, 60);
                // fps cap
                graphics2D.drawRect(280 + textLength(graphics2D, getString("fpsCap")), 1025 + settingsScrollY, 20 + textLength(graphics2D, fpsCap), 80);
                // jumpscare shake
                graphics2D.drawRect(230, 1200 + settingsScrollY, 30 + textLength(graphics2D, jumpscareShake), 80);
                // language
                graphics2D.drawRect(280 + textLength(graphics2D, getString("languageSelect")), 1290 + settingsScrollY, 20 + textLength(graphics2D, language), 80);
                // screen shake
                graphics2D.drawRect(160 + textLength(graphics2D, ">> " + getString("screenShake")), 1390 + settingsScrollY, 60, 60);
                // disable flickering
                graphics2D.drawRect(160 + textLength(graphics2D, ">> " + getString("disableFlickering")), 1470 + settingsScrollY, 60, 60);
                
                
                if(blackBorders) {
                    graphics2D.fillRect(168 + textLength(graphics2D, ">> " + getString("fixedRatio")), 318 + settingsScrollY, 44, 44);
                }
                if(headphones) {
                    graphics2D.fillRect(168 + textLength(graphics2D, ">> " + getString("headphones")), 398 + settingsScrollY, 44, 44);
                }
                if(disclaimer) {
                    graphics2D.fillRect(168 + textLength(graphics2D, ">> " + getString("showDisclaimer")), 638 + settingsScrollY, 44, 44);
                }
                if(showManual) {
                    graphics2D.fillRect(168 + textLength(graphics2D, ">> " + getString("showManual")), 718 + settingsScrollY, 44, 44);
                }
                if(saveScreenshots) {
                    graphics2D.fillRect(168 + textLength(graphics2D, ">> " + getString("saveScreenshots")), 798 + settingsScrollY, 44, 44);
                }
                if(bloom) {
                    graphics2D.fillRect(168 + textLength(graphics2D, ">> " + getString("rtx")), 878 + settingsScrollY, 44, 44);
                }
                if(fpsCounters[0]) {
                    graphics2D.fillRect(168 + textLength(graphics2D, ">> " + getString("fpsCounter")), 958 + settingsScrollY, 44, 44);
                }
                if(screenShake) {
                    graphics2D.fillRect(168 + textLength(graphics2D, ">> " + getString("screenShake")), 1398 + settingsScrollY, 44, 44);
                }
                if(disableFlickering) {
                    graphics2D.fillRect(168 + textLength(graphics2D, ">> " + getString("disableFlickering")), 1478 + settingsScrollY, 44, 44);
                }
                

                graphics2D.setStroke(new BasicStroke());

                graphics2D.setColor(white120);
                if(keyHandler.hoveringNightReset) {
                    graphics2D.fillRect(146, 500 + settingsScrollY, 30 + textLength(graphics2D, getString("resetNight")), 80);
                }
                if(keyHandler.hoveringFpsCap) {
                    graphics2D.fillRect(280 + textLength(graphics2D, getString("fpsCap")), 1025 + settingsScrollY, 20 + textLength(graphics2D, fpsCap), 80);
                }
                if(keyHandler.hoveringJumpscareShake) {
                    graphics2D.fillRect(230, 1200 + settingsScrollY, 30 + textLength(graphics2D, jumpscareShake), 80);
                }
                if(keyHandler.hoveringLanguage) {
                    graphics2D.fillRect(280 + textLength(graphics2D, getString("languageSelect")), 1290 + settingsScrollY, 20 + textLength(graphics2D, language), 80);
                }

                graphics2D.setColor(Color.WHITE);
                graphics2D.fillRect(1070, (int) (-settingsScrollY / 1060F * 560F), 10, 80);

                graphics2D.setColor(black120);
                graphics2D.fillRect(0, 600, 1080, 40);

                graphics2D.setColor(white160);
                graphics2D.fillRect(140, 240 + settingsScrollY, 800, 8);
                graphics2D.setColor(Color.WHITE);
                graphics2D.fillOval((short) (volume * 800) + 115, 220 + settingsScrollY, 50, 50);

                graphics2D.setFont(comicSansBold25);
                graphics2D.drawString(getString("keybindSettingsGuide"), 10, 630);

            }
            case PLAY -> {
                graphics2D.drawImage(camStates[0], 0, 0, null);
                graphics2D.setColor(black140);
                graphics2D.fillRect(0, 0, 1080, 640);

                for(int i = 0; i < PlayMenu.getList().size(); i++) {
                    if (Math.abs(i - PlayMenu.index) < 3) {
                        PlayMenuElement element = PlayMenu.getList().get(i);
                        boolean selected = i == PlayMenu.index;

                        int selectOffsetX = (int) (PlayMenu.selectOffsetX);
                        int orderOffsetX = i * 420;
                        BufferedImage image = selected ? element.getIcon() : element.getInactiveIcon();

                        int y = 320 - image.getHeight() / 2;
                        graphics2D.drawImage(image, 540 - image.getWidth() / 2 + orderOffsetX - selectOffsetX, y, null);
                        y += image.getHeight();

                        graphics2D.setColor(selected ? Color.WHITE : white160);
                        graphics2D.setFont(selected ? yuGothicPlain80 : yuGothicPlain60);
                        graphics2D.drawString(element.getText(), 540 - halfTextLength(graphics2D, element.getText()) + orderOffsetX - selectOffsetX, y - 10);

                        graphics2D.setFont(yuGothicPlain50);
                        if(!selected) {
                            graphics2D.setColor(white120);
                        }
                        graphics2D.drawString(element.getSubtext(), 540 - halfTextLength(graphics2D, element.getSubtext()) + orderOffsetX - selectOffsetX, y + 45);
                    
                        if(i == 2) {
//                            if (Achievements.ALL_NIGHTER.isObtained() && !gotEndlessNight6AfterAllNighter) {
                            if (Achievements.ALL_NIGHTER.isObtained()) {
                                float j = (float) Math.abs(Math.cos(fixedUpdatesAnim / 80F));
                                BufferedImage sign = rotate(infinitySign.request(), (int) (Math.sin(fixedUpdatesAnim / 160F) * 15));
                                
                                graphics2D.setComposite(AlphaComposite.SrcOver.derive((float) Math.abs(Math.sin(fixedUpdatesAnim / 40F)) * j));
                                graphics2D.drawImage(sign, 540 - (int) (250 * j) + orderOffsetX - selectOffsetX, 320 - (int) (150 * j), (int) (500 * j), (int) (300 * j), null);
                                graphics2D.setComposite(AlphaComposite.SrcOver);
                            }
                        }
                    }
                }
                
                if(PlayMenu.index == 1 && Achievements.ALL_NIGHTER.isObtained()) {
                    graphics2D.setColor(white100);
                    graphics2D.setFont(comicSans40);
                    
                    String text = getString("selectNightGuide");
//                    graphics2D.drawString(text, 540 - halfTextLength(graphics2D, text), 50);
                    graphics2D.drawString(text, 20, 621);
                    graphics2D.setStroke(new BasicStroke(3));

                    int j = 0;
                    for(int i = 4; i > 0; i--) {
                        graphics2D.drawString(i + "", 620 - 60 * j, 623);
                        j++;
                    }
                    graphics2D.drawRect(372 + 60 * currentNight, 588, 40, 40);
                    
                    graphics2D.setStroke(new BasicStroke());
                }
            }
            
            case ENDLESS_DISCLAIMER -> {
                graphics2D.setColor(Color.BLACK);
                graphics2D.fillRect(0, 0, 1080, 640);
                
                graphics2D.setColor(Color.WHITE);
                graphics2D.setFont(comicSans40);
                graphics2D.drawString(getString("endlessDisclaimer1"), 540 - halfTextLength(graphics2D, getString("endlessDisclaimer1")), 175);
                
                graphics2D.drawString(getString("endlessDisclaimer2"), 540 - halfTextLength(graphics2D, getString("endlessDisclaimer2")), 250);
                graphics2D.drawString(getString("endlessDisclaimer3"), 540 - halfTextLength(graphics2D, getString("endlessDisclaimer3")), 300);
                graphics2D.drawString(getString("endlessDisclaimer4"), 540 - halfTextLength(graphics2D, getString("endlessDisclaimer4")), 350);
                graphics2D.drawString(getString("endlessDisclaimer5"), 540 - halfTextLength(graphics2D, getString("endlessDisclaimer5")), 400);
                
                graphics2D.drawString(getString("endlessDisclaimer6"), 540 - halfTextLength(graphics2D, getString("endlessDisclaimer6")), 500);
            }
            
            case MUSIC_MENU -> {
                double t = fixedUpdatesAnim / 240d;
                
                for (int i = 0; i < 14; i++) {
                    for (int j = 0; j < 8; j++) {
                        if(i < 3 && j < 3)
                            continue;
                        
                        int thing = (int) Math.round(128 + 128 * noise.smoothNoise(i / 5.4d + t, j / 3.2d + t, t / 3d));

                        int contrast = Math.max(0, Math.min(255, thing * 2 - 128));
                        int highContrast = Math.max(0, Math.min(255, thing * 2 - 255));
                        graphics2D.setColor(new Color((contrast * 3 + highContrast * 2) / 5, highContrast, contrast));
                        graphics2D.fillRect(i * 80, j * 80, 80, 80);
                    }
                }
                
                BufferedImage greenOneIsHere = new BufferedImage(280, 250, BufferedImage.TYPE_INT_RGB);
                Graphics2D skibidiGraphics = greenOneIsHere.createGraphics();
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        int thing = (int) Math.round(128 + 128 * noise.smoothNoise(i / 5.4d + t, j / 3.2d + t, t / 3d));
                        int contrast = Math.max(0, Math.min(255, thing * 2 - 128));
                        int highContrast = Math.max(0, Math.min(255, thing * 2 - 255));
                        skibidiGraphics.setColor(new Color((contrast + highContrast * 2) / 3, contrast, highContrast));
                        skibidiGraphics.fillRect(i * 80, j * 80, 80, 80);
                    }
                }
                if(musicMenuDiscX < 140) {
                    skibidiGraphics.setComposite(AlphaComposite.SrcOver.derive((140 - musicMenuDiscX) / 186F));
                    for (int i = 0; i < 6; i++) {
                        int y = i * 70 - (fixedUpdatesAnim % 140);
                        if(y < -55 || y > 250)
                            continue;
                        
                        for (int j = 0; j < 6; j++) {
                            int x = j * 70 - (fixedUpdatesAnim % 140) + (i % 2) * 35;
                            if(x < -55 || x > 280)
                                continue;
                            
                            skibidiGraphics.drawImage(discIcon.request(), 5 + x, 5 + y, null);
                        }
                    }
                }
                skibidiGraphics.dispose();
                graphics2D.drawImage(greenOneIsHere, 0, 0, null);
                
                graphics2D.setColor(black200);
                graphics2D.fillRect(280, 0, 800, 250);

                // visualizer
                graphics2D.setColor(new Color(0, 255, 230, 60));
                try {
                    graphics2D.fillPolygon(getPolygon(visualizerPoints.stream().toList()));
                } catch (ConcurrentModificationException ignored) {
                    System.out.println("ruh roh");
                }
                
                graphics2D.setStroke(new BasicStroke(5));
                graphics2D.setColor(Color.WHITE);
                graphics2D.drawLine(280, 0, 280, 249);
                graphics2D.drawLine(0, 250, 1080, 250);
                
                graphics2D.setFont(comicSans40);
                graphics2D.drawString(getString("currentlyPlaying"), 300, 50);
                
                graphics2D.setFont(comicSans80);
                graphics2D.drawString(getString(menuSong + "DiscName"), 298, 140);
                
                BufferedImage rotated = rotateRadians(discMap.get(menuSong).request(), fixedUpdatesAnim / 60F - (140 - musicMenuDiscX) / 120F, true);
                graphics2D.drawImage(rotated, (int) (musicMenuDiscX) - rotated.getWidth() / 2, 125 - rotated.getHeight() / 2, null);

                graphics2D.setStroke(new BasicStroke(4));
                
                int i = 0;
                for(String id : musicDiscs) {
                    if(id.equals(menuSong)) {
                        graphics2D.setColor(Color.WHITE);
                        graphics2D.fillOval(16 + i * 190, 266, 183, 183);
                    }
                    graphics2D.drawImage(discMap.get(id).request(), 20 + i * 190, 270, 175, 175, null);

                    if(id.equals(hoveringMusicDisc)) {
                        // text
                        graphics2D.setFont(comicSans50);
                        String name = getString(id + "DiscName");
                        graphics2D.drawString(name, 530 - halfTextLength(graphics2D, name), 515);
                        
                        graphics2D.drawLine(520 - halfTextLength(graphics2D, name), 525, 540 + halfTextLength(graphics2D, name), 525);
                        
                        graphics2D.setFont(comicSans40);
                        String[] desc = getString(id + "DiscDesc").split("\n");

                        for(int j = 0; j < desc.length; j++) {
                            String str = desc[j];
                            
                            if(j == 1) {
                                switch (id) {
                                    case "pepitoButCooler" -> graphics2D.setColor(Color.getHSBColor(currentRainbow, 1, 1));
                                    case "spookers" -> graphics2D.setColor(new Color(180, 75, 32));
                                }
                            }
                            graphics2D.drawString(str, 530 - halfTextLength(graphics2D, str), 560 + j * 35);
                        }
                        
                        // hover
                        graphics2D.setColor(white60);
                        graphics2D.fillOval(16 + i * 190, 266, 183, 183);
                    }
                    i++;
                }

                drawCloseButton(graphics2D);
            }

            case MILLY -> {
                graphics2D.drawImage(millyShopImage, 0, 0, null);

                graphics2D.setColor(Color.BLACK);
                graphics2D.setStroke(new BasicStroke(6));
                double hourClockRadians = Math.toRadians(secondsInMillyShop / 120F - 120);
                graphics2D.drawLine(212, 217, (int) (212 + Math.cos(hourClockRadians) * 15), (int) (217 + Math.sin(hourClockRadians) * 15));

                graphics2D.setColor(new Color(20, 20, 20));
                graphics2D.setStroke(new BasicStroke(4));
                double minuteClockRadians = Math.toRadians(secondsInMillyShop / 10F - 90);
                graphics2D.drawLine(212, 217, (int) (212 + Math.cos(minuteClockRadians) * 50), (int) (Math.max(176, 217 + Math.sin(minuteClockRadians) * 50)));

                graphics2D.setColor(Color.RED);
                graphics2D.setStroke(new BasicStroke(2));
                double secondClockRadians = Math.toRadians(secondsInMillyShop * 6 - 87);
                graphics2D.drawLine(212, 217, (int) (212 + Math.cos(secondClockRadians) * 45), (int) (Math.max(176, 217 + Math.sin(secondClockRadians) * 45)));

                graphics2D.setColor(black140);
                graphics2D.fillRoundRect(10, 220, 250, 340, 50, 50);

                graphics2D.setColor(white200);
                graphics2D.fillRect(55, 285, 160, 3);

                int coins = 0;
                BufferedImage coinImg = dabloon.request();
                
                if(night.getType().isEndless()) {
                    if (endless.getNight() == 6) {
                        graphics2D.setStroke(new BasicStroke(4));
                        for (int i = 0; i < 8; i++) {
                            if (Math.cos(fixedUpdatesAnim / 70F + i) * 120 < 0) {
                                graphics2D.setColor(new Color(0, 255, 0, (int) (Math.max(0, Math.sin(fixedUpdatesAnim / 70F + i) * 120))));

                                graphics2D.drawLine(0, 0, (int) (Math.abs(Math.sin(fixedUpdatesAnim / 80F + i)) * 1080), (int) (1280 - Math.abs(Math.sin(fixedUpdatesAnim / 80F + i)) * 640));
                                graphics2D.drawLine(1080, 0, (int) (1080 - Math.abs(Math.sin(fixedUpdatesAnim / 80F + i)) * 1080), (int) (1280 - Math.abs(Math.sin(fixedUpdatesAnim / 80F + i)) * 640));
                            }
                        }
                        graphics2D.setStroke(new BasicStroke(2));
                    }
                    
                    coins = endless.getCoins();
                } else if(night.env() instanceof Basement env) {
                    coins = env.getCoins();
                    
                    coinImg = evilDabloon.request();
                }

                graphics2D.setColor(Color.WHITE);

                graphics2D.drawImage(coinImg, 15, 185, null);
                graphics2D.setFont(comicSans30);
                graphics2D.drawString(coins + "", 50, 210);

                graphics2D.setFont(yuGothicPlain60);

                if(millyBackButtonSelected) {
                    graphics2D.drawString(">> " + getString("back"), 20, 620);
                } else {
                    BufferedImage icon = millyShopItems[selectedMillyItem].getIcon();
                    Point coords = millyCoordinates.get((int) selectedMillyItem);

                    graphics2D.setColor(white120);
                    graphics2D.fillRoundRect(coords.x - 5, coords.y - icon.getHeight() - 5, icon.getWidth() + 10, icon.getHeight() + 8, 20, 20);
                    graphics2D.drawString(">> " + getString("back"), 20, 620);

                    graphics2D.setColor(white200);

                    Item item = millyShopItems[selectedMillyItem].getItem();

                    graphics2D.setFont(yuGothicPlain50);
                    int start = Math.max(134 - halfTextLength(graphics2D, item.getName()), 5);
                    graphics2D.drawString(item.getName(), start, 270);

                    graphics2D.setFont(yuGothicPlain30);
                    byte i = 0;
                    while (i < item.getDescription().split("\n").length) {
                        start = Math.max(134 - halfTextLength(graphics2D, item.getDescription().split("\n")[i]), 5);
                        graphics2D.drawString(item.getDescription().split("\n")[i], start, 320 + i * 30);
                        i++;
                    }

                    graphics2D.setColor(Color.WHITE);
                    graphics2D.drawString(getString("price") + ":", 20, 550);
                    graphics2D.setFont(comicSans30);

                    String priceStr = millyShopItems[selectedMillyItem].getPrice() + "";
                    graphics2D.drawString(priceStr, 110, 550);
                    graphics2D.drawImage(coinImg, textLength(graphics2D, priceStr) + 115, 525, null);

                    List<ItemTag> list = new ArrayList<>(item.getTags());
                    list.remove(ItemTag.CONFLICTS);
                    list.remove(ItemTag.PASSIVE);
                    list.remove(ItemTag.TRIGGER);
                    list.remove(ItemTag.EXPEND);
                    byte j = 0;
                    while(j < list.size()) {
                        ItemTag tag = list.get(j);
                        graphics2D.drawImage(itemTags[tag.getOrder()], 20 + 55 * j, 470, 50, 50, null);
                        j++;
                    }
                }
                
                graphics2D.drawImage(basementMillyLight, 0, 0, null);
            }
            
            case CRATE -> {
                graphics2D.setColor(Color.BLACK);
                graphics2D.fillRect(0, 0, 1080, 640);

                graphics2D.drawImage(crateBack.request(), 540 - 174, 320 - 123 + (int) (crateY), null);

                int n = crateRewards.size();
                int i = 0;

                for (Item item : crateRewards.keySet()) {
                    int amount = crateRewards.get(item);

                    int y = (int) (Math.sin(fixedUpdatesAnim / 20F + i * 1.6) * 20) - 60;
                    int supposed = 540 - 85 * (n - 1) + 170 * i;
                    int centerX = (int) (crateItemDistance * 540 + (1 - crateItemDistance) * supposed);

                    BufferedImage icon = item.getIcon();
                    graphics2D.drawImage(icon, centerX - icon.getWidth() / 2, 420 - icon.getHeight() + y, null);

                    graphics2D.setFont(new Font("Yu Gothic", Font.PLAIN, 50));
                    byte xAdder = -10;
                    if (amount > 9) {
                        xAdder -= 5;
                    }

                    graphics2D.setColor(Color.WHITE);
                    graphics2D.drawString(amount + "", centerX + xAdder + icon.getWidth() / 2, 420 + icon.getHeight() / 2 - 40 + y);

                    i++;
                }

                graphics2D.drawImage(crateFront.request(), 540 - 174, 320 - 123 + (int) (crateY), null);

                if (!everyFixedUpdate.containsKey("crateAnimation")) {
                    if (crateY > 640) {
                        graphics2D.setColor(Color.DARK_GRAY);
                        graphics2D.setFont(comicSans50);
                        graphics2D.drawString(getString("pressAnyKey"), 540 - halfTextLength(graphics2D, getString("pressAnyKey")), 580);
                    }
                }
            }

            case CUTSCENE -> {
                currentCutscene.render();
                graphics2D.drawImage(currentCutscene.getImage(), 0, 0, 1080, 640, null);

                if(currentCutscene.getID().equals("maxwell")) {
                    if (pressAnyKey) {
                        graphics2D.setColor(Color.WHITE);
                        graphics2D.setFont(comicSans60);
                        graphics2D.drawString(getString("pressAnyKey"), 540 - halfTextLength(graphics2D, getString("pressAnyKey")), 600);
                    }
                }
            }

            case BATTERY_SAVER -> {
                graphics2D.drawImage(batterySaver.request(), 0, 0, null);
                if(!night.getAstartaBoss().getBatterySaveChoice()) {
                    graphics2D.drawImage(batterySaverOverlay.request(), 125, 504, null);
                }
            }
            case DISCLAIMER -> {
                graphics2D.drawImage(disclaimerImage.request(), 0, 0, null);
            }
            
            case CORNFIELD -> {
                cornField3D.paint(graphics2D);
            }
            
            case FIELD -> {
                float oResize = 2;
                BufferedImage oSmall = null;
                Graphics2D canvasGraphics = null;
                
                if(oResize != 1) {
                    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                    canvasGraphics = graphics2D;

                    oSmall = new BufferedImage((int) (1080 / oResize), (int) (640 / oResize), BufferedImage.TYPE_INT_RGB);
                    graphics2D = (Graphics2D) oSmall.getGraphics();
                    graphics2D.scale(1 / oResize, 1 / oResize);
                }
                
                
                int roadWidth = (field.getRoadWidth());
                int cameraYaw = field.getYaw();
                int cameraPitch = field.getPitch();
                int yaw = (int) (cameraYaw + field.getCarYaw() + 540);
                int pitch = (int) (cameraPitch + field.getCarPitch() + 320);
                int x = field.getX();
                int y = (int) field.getY();
                
                
                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

//                try {

                if(field.isInCar()) {
                    graphics2D.setClip(cameraYaw - 540 + 193, cameraPitch - 320 + 332, 1774, 788);
                    // !!! NECESSARY OPTIMIZATION ADD THIS BACK WHEN DONE !!!
                }


                Color skyColor = field.getSkyColor();
                Color groundColor = field.getGroundColor();
                Color cloudColor = field.getCloudColor();
                Color pathColor = field.getPathColor();
                Color raindropColor = field.getRaindropColor();

                if(Field.lightMode) {
                    field.lightningProgress = 1;
                }
                if(field.lightningProgress > 0) {
                    Color almostWhite = new Color(160, 170, 255);
                    double progress = field.lightningProgress / 1.5F;
                    skyColor = lerpColors(almostWhite, skyColor, progress);
                    progress /= 2;
                    cloudColor = lerpColors(almostWhite, cloudColor, progress);
                    progress /= 3;
                    groundColor = lerpColors(Field.FogColor, groundColor, progress);
                    raindropColor = lerpColors(new Color(170, 200, 255), raindropColor, progress);

                    progress /= 2;
                    pathColor = lerpColors(Field.FogColor, pathColor, progress);
                }


                graphics2D.setColor(skyColor);
                graphics2D.fillRect(0, pitch - 222, 1080, 222);

                int skyX = -((fixedUpdatesAnim / 4 - yaw + 2160) % 1080);
                graphics2D.drawImage(fieldSky.request(), skyX, pitch - 320, null);
                graphics2D.drawImage(fieldSky.request(), skyX + 1080, pitch - 320, null);

                if(field.lightningProgress > 0) {
                    graphics2D.setComposite(AlphaComposite.SrcOver.derive((float) (field.lightningProgress / 3F)));
                    graphics2D.drawImage(fieldWhiteSky.request(), skyX, pitch - 320, null);
                    graphics2D.drawImage(fieldWhiteSky.request(), skyX + 1080, pitch - 320, null);

                    graphics2D.setComposite(AlphaComposite.SrcOver);
                }

                if(pitch > 320) {
                    graphics2D.setColor(cloudColor);
                    graphics2D.fillRect(0, 0, 1080, pitch - 320);
                }

                graphics2D.setColor(groundColor);
                graphics2D.fillRect(0, pitch, 1080, 640 - pitch);

                
                int renderDistance = 110;
//                int renderDistance = 15;
//                int renderDistance = (int) (Math.sin(fixedUpdatesAnim / 40F) * 200 + 210);

                int size = field.getSize();
                float distance = field.getDistance();
                int endRender = (int) Math.max(0, Math.ceil(distance));
                int startRender = Math.min(endRender + renderDistance, size);
//                    int size = Math.min(start + 2200, field.getSize());

//                System.out.println("start: " + start + " | size: " + size);

//                    BufferedImage tree = fieldTree.request();
//                    BufferedImage endBuilding = fieldEndBuilding.request();
//                    BufferedImage roadImg = fieldRoad.request();



                byte[] objectsFarLeft = field.getObjectsFarLeft();
                byte[] objectsLeft = field.getObjectsLeft();
                byte[] objects2ThirdsLeft = field.getObjects2ThirdsLeft();
                byte[] objectsMiddle = field.getObjectsMiddle();
                byte[] objects2ThirdsRight = field.getObjects2ThirdsRight();
                byte[] objectsRight = field.getObjectsRight();
                byte[] objectsFarRight = field.getObjectsFarRight();
                byte[] road = field.getRoad();

                float[] roadXOffsets = field.getRoadXOffsetArray();
                float[] roadYOffsets = field.getRoadYOffsetArray();
                float[] roadWidths = field.getRoadWidthArray();
                float[] pathWidths = field.getPathWidthArray();
                float[] pathYOffsets = field.getPathYOffset();
                float[] treeXOffsets = field.getTreeXOffsetArray();
                float[] farModifiers = field.getFarModifier();


                graphics2D.setClip(0, 0, 1080, 640);

                Point lastPoint1 = null;
                Point lastPoint2 = null;
                Point lastLeftPoint = null;
                Point lastRightPoint = null;
                Point lastROADPoint1 = null;
                Point lastROADPoint2 = null;
                
                Rectangle carRect = new Rectangle(0, 0, 0, 0);
                
                FieldBlimp blimp = field.getBlimp();
                float blimp1Z = blimp.getZ();
                
                
                for (int i = size - 1; i > Math.max(0, endRender - 1); i--) {
                    if (i >= startRender - 1) {
                        if (i != (int) blimp1Z && i != size - 2)
                            continue;
                    }

                    float distanceToObject = i - distance;
                    if (distanceToObject <= 0.15)
                        continue;
                    float invert = 1 / distanceToObject;
                    float roadXOffset = roadXOffsets[i];
                    float roadYOffset = roadYOffsets[i];
                    float roadPieceWidth = roadWidths[i];
                    float pathWidth = pathWidths[i];
                    float pathYOffset = pathYOffsets[i];
                    float treeXOffset = treeXOffsets[i];
                    float farModifier = farModifiers[i];

                    int bottom = (int) ((y + roadYOffset) * invert);
                    int sideBottom = (int) ((y + roadYOffset + pathYOffset) * invert);

//                    float interpolation = 1 - Math.max(0, Math.min(1, distanceToObject / 14F));
                    float interpolation = 1 - Math.max(0, Math.min(1, distanceToObject / 15F));
                    interpolation = 1 - (1 - interpolation * (float) (1 - field.lightningProgress));

                    if(Field.lightMode) {
                        interpolation = 1;
                    }

                    
                    if (true) {
                        float width = ((540 + pathWidth) * 2 * invert);

                        Point point1 = new Point(yaw - (int) (width / 2F + (x + roadXOffset) * invert), pitch + (int) ((y + roadYOffset) * invert));
                        Point point2 = new Point(yaw + (int) (width / 2F - (x + roadXOffset) * invert), pitch + (int) ((y + roadYOffset) * invert));


                        float ROADwidth = ((540 + roadPieceWidth) * 2 * invert);

                        Point ROADpoint1 = new Point(yaw - (int) (ROADwidth + (x + roadXOffset) * invert), pitch + (int) ((y + roadYOffset) * invert));
                        Point ROADpoint2 = new Point(yaw + (int) (ROADwidth - (x + roadXOffset) * invert), pitch + (int) ((y + roadYOffset) * invert));
                        
                        
                        if (lastPoint1 != null) {
                            Polygon polygon = GamePanel.getPolygon(List.of(lastPoint1, lastPoint2, point2, point1));

                            graphics2D.setColor(groundColor);
                            if (road[i] == 1) {
                                graphics2D.setColor(lerpColors(pathColor, Field.FogColor, interpolation));
//                                    graphics2D.setColor(pathColor);
                            }
                            graphics2D.fillPolygon(polygon);
                            

                            graphics2D.setColor(lerpColors((roadYOffset > 0 ? groundColor.darker() : groundColor), Field.FogColor, interpolation));
//                                graphics2D.setColor(roadYOffset > 0 ? groundColor.darker() : groundColor);

                            
                            float ROADwidthFAR = (((540 + roadPieceWidth) * 2 + Math.max(0, -roadYOffset)) * invert);
                            float shit = 0;
                            if(pathYOffset < 0) {
                                shit = roadYOffset + pathYOffset;
                                ROADwidthFAR = ROADwidth;
                            }
                            

                            Point left1 = new Point(yaw - (int) (ROADwidthFAR + (x + roadXOffset) * invert), pitch + (int) ((y + shit) * invert));
                            if (lastLeftPoint != null) {
                                polygon = GamePanel.getPolygon(List.of(lastPoint1, point1, ROADpoint1, left1, lastLeftPoint, lastROADPoint1));
                                graphics2D.fillPolygon(polygon);
                            }
                            lastLeftPoint = left1;


                            Point right1 = new Point(yaw + (int) (ROADwidthFAR - (x + roadXOffset) * invert), pitch + (int) ((y + shit) * invert));
                            if (lastRightPoint != null) {
                                polygon = GamePanel.getPolygon(List.of(lastPoint2, point2, ROADpoint2, right1, lastRightPoint, lastROADPoint2));
                                graphics2D.fillPolygon(polygon);
                            }
                            lastRightPoint = right1;
                        }

                        lastPoint1 = point1;
                        lastPoint2 = point2;

                        lastROADPoint1 = ROADpoint1;
                        lastROADPoint2 = ROADpoint2;
                    }
                    // ROAD ^
                    

                    float invertInterpolation = (1 - interpolation);

                    // OBEJCTS v
                    if (objectsFarLeft[i] != 0) {
                        FieldObject object = field.objects[objectsFarLeft[i] - 1];
                        float width = (object.width * invert);
                        float height = (object.height * invert);

                        graphics2D.drawImage(object.table[(int) (invertInterpolation * object.tableSize)], yaw - (int) ((roadWidth + roadPieceWidth) * farModifier * invert + width / 2F + (x + roadXOffset) * invert), pitch + (int) (sideBottom - height), (int) Math.ceil(width), (int) Math.ceil(height), null);
                    }
                    if (objectsLeft[i] != 0) {
                        FieldObject object = field.objects[objectsLeft[i] - 1];
                        float width = (object.width * invert);
                        float height = (object.height * invert);

                        graphics2D.drawImage(object.table[(int) (invertInterpolation * object.tableSize)], yaw - (int) ((roadWidth + roadPieceWidth) * invert + width / 2F + (x + roadXOffset - treeXOffset) * invert), pitch + (int) (bottom - height), (int) Math.ceil(width), (int) Math.ceil(height), null);
                    }
                    if (objects2ThirdsLeft[i] != 0) {
                        FieldObject object = field.objects[objects2ThirdsLeft[i] - 1];
                        float width = (object.width * invert);
                        float height = (object.height * invert);

                        graphics2D.drawImage(object.table[(int) (invertInterpolation * object.tableSize)], yaw - (int) ((roadWidth + roadPieceWidth) / 3 * 2 * invert + width / 2F + (x + roadXOffset - (objects2ThirdsLeft[i] == 9 ? treeXOffset * 4 : 0)) * invert), pitch + (int) (bottom - height), (int) Math.ceil(width), (int) Math.ceil(height), null);
                    }
                    if (objectsMiddle[i] != 0) {
                        FieldObject object = field.objects[objectsMiddle[i] - 1];
                        float width = (object.width * invert);
                        float height = (object.height * invert);

                        graphics2D.drawImage(object.table[(int) (invertInterpolation * object.tableSize)], yaw - (int) (width / 2F + (x + roadXOffset) * invert), pitch + (int) (bottom - height), (int) Math.ceil(width), (int) Math.ceil(height), null);
                    }
                    if (objects2ThirdsRight[i] != 0) {
                        FieldObject object = field.objects[objects2ThirdsRight[i] - 1];
                        float width = (object.width * invert);
                        float height = (object.height * invert);

                        graphics2D.drawImage(object.table[(int) (invertInterpolation * object.tableSize)], yaw + (int) ((roadWidth + roadPieceWidth) / 3 * 2 * invert - width / 2F - (x + roadXOffset + (objects2ThirdsRight[i] == 9 ? treeXOffset * 4 : 0)) * invert), pitch + (int) (bottom - height), (int) Math.ceil(width), (int) Math.ceil(height), null);
                    }
                    if (objectsRight[i] != 0) {
                        FieldObject object = field.objects[objectsRight[i] - 1];
                        float width = (object.width * invert);
                        float height = (object.height * invert);

                        graphics2D.drawImage(object.table[(int) (invertInterpolation * object.tableSize)], yaw + (int) ((roadWidth + roadPieceWidth) * invert - width / 2F - (x + roadXOffset + treeXOffset) * invert), pitch + (int) (bottom - height), (int) Math.ceil(width), (int) Math.ceil(height), null);
                    }
                    if (objectsFarRight[i] != 0) {
                        FieldObject object = field.objects[objectsFarRight[i] - 1];
                        float width = (object.width * invert);
                        float height = (object.height * invert);

                        graphics2D.drawImage(object.table[(int) (invertInterpolation * object.tableSize)], yaw + (int) ((roadWidth + roadPieceWidth) * farModifier * invert - width / 2F - (x + roadXOffset) * invert), pitch + (int) (sideBottom - height), (int) Math.ceil(width), (int) Math.ceil(height), null);
                    }



                    if (i == (int) blimp1Z) {
                        // BLIMP 1 v

                        distanceToObject = blimp1Z - distance;
                        if (distanceToObject > 0.1) {
                            invert = 1 / distanceToObject;
                            float width = (253 * 128 * invert);
                            float height = (115 * 128 * invert);

                            int objectY = blimp.getY();
                            int objectX = blimp.getX();
                            
                            boolean isFront = blimp.untilDirects < 4.5F || (Math.abs(Math.cos(fixedUpdatesAnim / 400F)) > 0.75F);
                            if(isFront) {
                                width = (115 * 128 * invert);
                                height = (115 * 128 * invert);
                            }

                            int centerY = (int) ((y + objectY) * invert);
                            graphics2D.drawImage(isFront ? fieldBlimpFront.request() : (Math.sin(fixedUpdatesAnim / 400F) > 0 ? fieldBlimpMirrored.request() : fieldBlimp.request()),
                                    yaw - (int) (width / 2F + (x + objectX) * invert), pitch + (int) (centerY - height / 2), (int) Math.ceil(width), (int) Math.ceil(height), null);
                        }

                        // BLIMP 2 / EXAMPLE BLIMP v
                        distanceToObject = 700 - distance;
                        if (distanceToObject > 0.1) {
                            invert = 1 / distanceToObject;
                            float width = (253 * 128 * invert);
                            float height = (115 * 128 * invert);

                            int objectY = -70000;
                            int objectX = 50000 - fixedUpdatesAnim * 120;

                            int centerY = (int) ((y + objectY) * invert);
                            graphics2D.drawImage(fieldBlimpMirrored.request(), yaw - (int) (width / 2F + (x + objectX) * invert), pitch + (int) (centerY - height / 2), (int) Math.ceil(width), (int) Math.ceil(height), null);
                        }
                    }

                    if (!field.isInCar() && i == 3) {
                        // CAR v
                        distanceToObject = 3.2F - distance;
                        if (distanceToObject > 0.1) {
                            invert = 1 / distanceToObject;
                            float width = (219 * 3 * invert);
                            float height = (198 * 3 * invert);

                            int objectX = -300;
                            
                            carRect = new Rectangle(yaw - (int) (width / 2F + (x + objectX) * invert), pitch + (int) (bottom - height), (int) Math.ceil(width), (int) Math.ceil(height));

                            graphics2D.drawImage(fieldCarBehind.request(), carRect.x, carRect.y, carRect.width, carRect.height, null);
                        }
                    }
                }


                synchronized (field.raindrops) {
                    int xStuffs = yaw - x - 540;
                    int yStuffs = pitch - y;
                    

                    graphics2D.setColor(lerpColors(raindropColor, Field.FogColor, 0.3));
                    for (FieldRaindrop raindrop : field.raindrops) {
                        if(raindrop.getDistance() != 1)
                            continue;
                        int width = raindrop.getWidth();
                        int height = raindrop.getHeight();

                        graphics2D.fillRect(raindrop.getX() + xStuffs - width / 2, raindrop.getY() + yStuffs - height / 2, width, height);
                    }

                    graphics2D.setColor(raindropColor);
                    for (FieldRaindrop raindrop : field.raindrops) {
                        if(raindrop.getDistance() != 0)
                            continue;
                        int width = raindrop.getWidth();
                        int height = raindrop.getHeight();

                        graphics2D.fillRect(raindrop.getX() + xStuffs - width / 2, raindrop.getY() + yStuffs - height / 2, width, height);
                    }
                }

                
                drawFieldA90(graphics2D);
                
                if(field.isInCar()) {
                    graphics2D.setClip(0, 0, 1080, 640);
                    graphics2D.setColor(Color.BLACK);
                    graphics2D.fillRect(cameraYaw - 540, cameraPitch - 320, 2160, 332);
                    graphics2D.fillRect(cameraYaw - 540, cameraPitch - 320 + 332, 193, 948);
                    graphics2D.fillRect(cameraYaw - 540 + 1967, cameraPitch - 320 + 332, 193, 948);

                    graphics2D.drawImage(fieldCar.request(), cameraYaw - 540 + 193, cameraPitch - 320 + 332, null);

//                        float sine = (field.getCarYaw() / 540F) * 0.9F;
                    float sine = ((field.getTurnSpeed() * 2 + (field.getCarYaw() / 540F) * 0.9F) / 2);
                    double angle = Math.asin(- sine);
                    
                    BufferedImage wheel = rotateRadians(fieldWheel.request(), angle, false);
                    int newWidth = wheel.getWidth() * 2;
                    float newHeight = (wheel.getHeight() * 1.75F);
                    graphics2D.drawImage(wheel, cameraYaw - 540 + 850 - newWidth / 2, cameraPitch - 320 + 820 - (int) (newHeight / 2F), newWidth, (int) (newHeight), null);
                
                    
                    graphics2D.drawImage(fieldCommuncationsBg.request(), cameraYaw - 540 + 1114, cameraPitch - 320 + 323, null);
                    float sinewave = (float) Math.sin(field.leverDegrees);
                    int leverHandleHeight = (int) (45 * sinewave);
                    BufferedImage leverBase = fieldLeverBase.request();
                    if(sinewave < 0) {
                        leverBase = mirror(leverBase, 3);
                    }
                    graphics2D.drawImage(leverBase, cameraYaw - 540 + 1129, cameraPitch - 320 + 380 - Math.max(0, leverHandleHeight), 61, Math.abs(leverHandleHeight), null);

                    graphics2D.drawImage(blimp.untilDirects < 4 ? fieldLeverHandleLit.request() : fieldLeverHandle.request(), cameraYaw - 540 + 1034, cameraPitch - 320 + 380 - leverHandleHeight - 25, null);

                    if(blimp.untilDirects < 4) {
                        Point center = new Point(cameraYaw - 540 + 1034 + 120, cameraPitch - 320 + 380 - leverHandleHeight - 25 + 25);
                        
                        if(!(new Rectangle(0, 0, 1080, 640).contains(center))) {
                            graphics2D.drawImage(fieldLeverGlow.request(), center.x - 400, center.y - 200, null);
                        }
                    }
                    
                    if(field.isHoveringLever()) {
                        graphics2D.setColor(new Color(15, 57, 157, 60));
                        graphics2D.fillRect(cameraYaw - 540 + 1034, cameraPitch - 320 + 380 - leverHandleHeight - 25, 261, 50);
                    }
                    
                    
                    // RADAR v
                    if(field.radarImg == null) {
                        field.redrawRadarImg(this);
                    }
                    BufferedImage radarImg = field.radarImg;

                    for(int i = 0; i < 161; i += 4) {
                        float percent = i / 160F;
                        int widthLeft = 161 - i;

                        graphics2D.drawImage(radarImg.getSubimage(i, 0, Math.min(widthLeft, 4), 142), cameraYaw - 540 + 1343 + i, (int) (cameraPitch - 320 + 372 + (1 - percent) * 10), Math.min(widthLeft, 4), (int) (88 + 54 * percent), null);
                    }
                    // RADAR ^
                    
                    if (night != null) {
                        if (night.env instanceof HChamber chamber) {
                            if (chamber.hasCup()) {
                                graphics2D.drawImage(fieldCup.request(), cameraYaw + 871, cameraPitch + 449, null);
                            }
                        }
                    }
                    
                    
                    if(field.controlsTransparency > 0) {
                        graphics2D.setComposite(AlphaComposite.SrcOver.derive(Math.min(1, field.controlsTransparency)));
                        graphics2D.drawImage(fieldCarControls.request(), 200, 288, null);
                        graphics2D.setComposite(AlphaComposite.SrcOver);
                    }
                } else {
                    if (field.isHoveringCar()) {
                        graphics2D.drawImage(fieldCarArrow.request(), carRect.x + carRect.width / 2 - 32, carRect.y + carRect.height / 2 - 32, null);
                    }
                }


                if(field.isInGeneratorMinigame()) {
                    graphics2D.setColor(new Color(15, 15, 38));
                    graphics2D.setStroke(new BasicStroke(5));
                    
                    float waveHeight = 200 * field.impulseInterp;
                    float waveSpeed = 10 * field.impulseInterp;
                    float waveFrequency = 20 * field.impulseInterp;
                    
                    for(int i = 0; i < 1080; i += 10) {
                        float realWaveHeight = waveHeight * (1 - Math.abs(540 - i) / 540F);
                        
                        int x1 = i - 10;
                        int x2 = i;
                        int y1 = (int) (Math.sin(((fixedUpdatesAnim * waveSpeed) + i - 10) / waveFrequency) * realWaveHeight) + 550;
                        int y2 = (int) (Math.sin(((fixedUpdatesAnim * waveSpeed) + i) / waveFrequency) * realWaveHeight) + 550;
                        
                        graphics2D.drawLine(x1, y1, x2, y2);
                    }
                    
                    
                    graphics2D.drawImage(blueBattery.request(), 220, 490, null);
                    graphics2D.setColor(new Color(15, 57, 157));

                    for(short gx : field.generatorXes.clone()) {
                        if(gx != -1) {
                            graphics2D.fillRect(220 + gx, 495, 60, 110);
                        }
                    }

                    graphics2D.setColor(new Color(255, 216, 36));

                    int barX = (int) (fixedUpdatesAnim * 6.5F) + (int) (Math.sin(fixedUpdatesAnim * 0.05) * 12);
                    barX = barX % 630;

                    graphics2D.fillRect(220 + barX, 485, 10, 130);

                    graphics2D.drawImage(impulseText.request(), 20, 425, null);
                }
                
                
                
                if(oResize != 1) {
                    graphics2D.dispose();
                    graphics2D = canvasGraphics;

                    graphics2D.drawImage(oSmall, 0, 0, 1080, 640, null);
                }
                

                graphics2D.setColor(new Color(30, 30, 60));
                graphics2D.setFont(comicSans20);
//                graphics2D.drawString("turn speed: " + field.getTurnSpeed(), 20, 40);
//                graphics2D.drawString("speed: " + field.getSpeed(), 20, 70);
//                graphics2D.drawString("x: " + x, 20, 100);
//                graphics2D.drawString("y: " + y, 20, 130);
//                graphics2D.drawString("distance: " + distance, 20, 160);
//                graphics2D.drawString("yaw: " + (field.getYaw()), 20, 190);
//                graphics2D.drawString("pitch: " + (field.getPitch()), 20, 220);
//                graphics2D.drawString("CAR yaw: " + (field.getCarYaw()), 20, 250);
//                graphics2D.drawString("CAR pitch: " + (field.getCarPitch()), 20, 280);
                


                if (!field.isInCar() && !field.lockedIn) {
                    float oldInterp = field.objectiveInterp;
                    float interp = (float) (1 / (1 + Math.pow(Math.E, -(oldInterp * 2 - 1) * 6)));

                    graphics2D.setColor(new Color(30, 30, 60, (int) (Math.min(1, oldInterp * 2) * 255)));
                    graphics2D.setFont(comicSans30);
                    String text = getString("fieldObjective");

                    Point point1 = new Point(carRect.x + carRect.width / 2 - halfTextLength(graphics2D, text), carRect.y + carRect.height / 2 - 15);
                    Point point2 = new Point(1060 - textLength(graphics2D, text), 620);

                    graphics2D.drawString(text, (int) lerp(point1.x, point2.x, Math.min(1, interp)), (int) lerp(point1.y, point2.y, Math.min(1, interp)));
                }
                
                
                
//                graphics2D.setClip(930 - 80, 320 - 70, 161, 142);



//                graphics2D.setColor(Color.GRAY);
//                graphics2D.fillRect(780, 0, 300, 640);
//
//                graphics2D.setColor(Color.RED);
//                graphics2D.fillOval(930 - 5, 320 - 5, 10, 10);


//                List<CollidableLandmine> landmines = new ArrayList<>();
//                
//                for (int i = Math.min(field.getSize() - 1, (int) (distance) + 2); i > Math.max(0, (int) (distance) - 2); i--) {
//                    float distanceToObject = i - distance;
////                        if (distanceToObject <= 0.1)
////                            continue;
//                    float roadXOffset = -roadXOffsets[i];
//                    float roadYOffset = roadYOffsets[i];
//                    float roadPieceWidth = roadWidths[i];
//
//
////                    graphics2D.setColor(Color.BLACK);
//                    
//                    int divideBy = 10;
//       
//                    // OBEJCTS v
//                    if (objectsLeft[i] != 0) {
////                        graphics2D.fillOval((int) (930 - 3 + ((-(roadPieceWidth + roadWidth) / 7 * 4) + roadXOffset - x) / divideBy), (int) (320 - 5 + distanceToObject * 16), 6, 6);
//                    }
//                    if (objectsRight[i] != 0) {
////                        graphics2D.fillOval((int) (930 - 3 + (((roadPieceWidth + roadWidth) / 7 * 4) + roadXOffset - x) / divideBy), (int) (320 - 5 + distanceToObject * 16), 6, 6);
//                    }
//
//
//                    graphics2D.setColor(Color.BLUE);
//
//                    if (objects2ThirdsLeft[i] == 8) {
//                        landmines.add(new CollidableLandmine(-1, i, new Circle((int) (((-(roadPieceWidth + roadWidth) * 2 / 3 / 7 * 4) + roadXOffset - x) / divideBy), (int) (distanceToObject * 16), 14)));
//                        
////                        graphics2D.fillOval((int) (930 - 7 + ((-(roadPieceWidth + roadWidth) * 2 / 3 / 7 * 4) + roadXOffset - x) / divideBy), (int) (320 - 7 + distanceToObject * 16), 14, 14);
//                    }
//                    if (objectsMiddle[i] == 8) {
//                        landmines.add(new CollidableLandmine(0, i, new Circle((int) ((roadXOffset - x) / divideBy), (int) (distanceToObject * 16), 14)));
//                 
////                        graphics2D.fillOval((int) (930 - 7 + (roadXOffset - x) / divideBy), (int) (320 - 7 + distanceToObject * 16), 14, 14);
//                    }
//                    if (objects2ThirdsRight[i] == 8) {
//                        landmines.add(new CollidableLandmine(1, i, new Circle((int) ((((roadPieceWidth + roadWidth) * 2 / 3 / 7 * 4) + roadXOffset - x) / divideBy), (int) (distanceToObject * 16), 14)));
//               
////                        graphics2D.fillOval((int) (930 - 7 + (((roadPieceWidth + roadWidth) * 2 / 3 / 7 * 4) + roadXOffset - x) / divideBy), (int) (320 - 7 + distanceToObject * 16), 14, 14);
//                    }
//                }
//                
////                graphics2D.setColor(Color.RED);
////
////                graphics2D.fillOval((int) (930 - 6 + (blimp.getX() - x) / 10F / 100F), (int) (320 - 6 + (field.getDistance() - blimp.getZ()) * 16F / 100F), 12, 12);
////                
//                
//                
//                for(CollidableLandmine landmine : landmines) {
//                    Circle hitbox = landmine.hitbox;
//                    if(hitbox.contains(3, 0)) {
//                        switch (landmine.array) {
//                            case -1 -> field.getObjects2ThirdsLeft()[landmine.index] = 0;
//                            case 0 -> field.getObjectsMiddle()[landmine.index] = 0;
//                            case 1 -> field.getObjects2ThirdsRight()[landmine.index] = 0;
//                        }
//                        field.a90.spawn();
//                        break;
//                    }
//                }
//
//                
//                int indexFirst = (int) Math.min(field.getSize() - 1, Math.max(0, Math.floor(field.getDistance())));
//                int indexSecond = (int) Math.min(field.getSize() - 1, Math.max(0, Math.floor(field.getDistance()) + 1));
//                
//                int firstLeftX = (int) ((-(roadWidths[indexFirst] + roadWidth) / 7 * 4) - roadXOffsets[indexFirst] - x);
//                int secondLeftX = (int) ((-(roadWidths[indexSecond] + roadWidth) / 7 * 4) - roadXOffsets[indexSecond] - x);
//                int leftX = (int) -(lerp(firstLeftX, secondLeftX, field.getDistance() % 1));
//                
//                if(leftX < 0) {
//                    field.handleCollision(this);
//                }
//                
//
//                int firstRightX = (int) (((roadWidths[indexFirst] + roadWidth) / 7 * 4) - roadXOffsets[indexFirst] - x);
//                int secondRightX = (int) (((roadWidths[indexSecond] + roadWidth) / 7 * 4) - roadXOffsets[indexSecond] - x);
//                int rightX = (int) -(lerp(firstRightX, secondRightX, field.getDistance() % 1));
//
//                if(rightX > 0) {
//                    field.handleCollision(this);
//                }
                
                
                
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
            
            case INVESTIGATION -> {
                graphics2D.drawImage(invstgBg.request(), 0, 0, null);

                graphics2D.setColor(new Color(0, 0, 0, 60));
                graphics2D.fillRect(768 + 10, 149 + 20, 192, 128);
                
                graphics2D.drawImage(invstgMilly.request(), 768, 149, null);
                graphics2D.drawImage(invstgEndlessBadge.request(), 661, 246, null);
                graphics2D.drawImage(invstgChBadge.request(), 177, 475, null);
                
                graphics2D.setColor(new Color(255, 255, 255));
                graphics2D.setFont(comicSans50);
                graphics2D.drawString(getString("invstgOngoing"), 540 - halfTextLength(graphics2D, getString("invstgOngoing")), 50);

                
                graphics2D.setColor(new Color(0, 0, 0, 60));
                for(InvestigationPaper paper : Investigation.list) {
                    BufferedImage img = paper.image.request();
                    int x = paper.x;
                    int y = paper.y;
                    Polygon polygon = rectangleToPolygon(new Rectangle(x - img.getWidth() / 2 + 10, y - img.getHeight() / 2 + 20, img.getWidth(), img.getHeight()));
                    polygon = rotatePolygon(polygon, x + 10, y + 20, Math.toRadians(paper.rotation));
                    graphics2D.fillPolygon(polygon);
                }
                
                
                for(InvestigationPaper paper : Investigation.list) {
                    BufferedImage img = rotate(paper.image.request(), paper.rotation);
                    int x = paper.x;
                    int y = paper.y;
                    
                    graphics2D.drawImage(img, x - img.getWidth() / 2, y - img.getHeight() / 2, null);
                }

                
                graphics2D.setColor(new Color(200, 200, 200));
                graphics2D.setFont(comicSans30);

                for(InvestigationPaper paper : Investigation.list) {
                    BufferedImage img = rotate(paper.image.request(), paper.rotation);
                    int x = paper.x;
                    int y = paper.y;
                    
                    graphics2D.drawString(getString(paper.languageId), x - halfTextLength(graphics2D, getString(paper.languageId)), y + img.getHeight() / 2 + 10);
                }

                graphics2D.setColor(Color.darkGray);
                graphics2D.setFont(comicSans50);
                graphics2D.drawString("x", 1025, 50);

                if(new Rectangle(1025, 20, 35, 35).contains(rescaledPoint)) {
                    graphics2D.setColor(black80);
                    graphics2D.fillOval(1020, 17, 40, 40);
                }
                
                graphics2D.setComposite(MultiplyComposite.Multiply);
                graphics2D.drawImage(invstgMultiplyLayer.request(), 0, 0, null);
                graphics2D.setComposite(AlphaComposite.SrcOver);
            }
        }

        if(state != GameState.BINGO) {
            if(bingoCard.isGenerated() && !bingoCard.isFailed() && !(bingoCard.isCompleted() && bingoCard.playedOutAnimation)) {
                graphics2D.setComposite(AlphaComposite.SrcOver.derive(state == GameState.ITEMS ? 0.2F : 0.6F));
                graphics2D.drawImage(bingoCardImg, 910, 10, 160, 180, null);
                graphics2D.setFont(yuGothicPlain30);
                graphics2D.setColor(Color.WHITE);

                String string = getString("time") + bingoCard.getMinutes() + getString("m") + " " + bingoCard.getSeconds() + getString("s");
                
                graphics2D.drawString(string, 1070 - textLength(graphics2D, string), 220);
                graphics2D.setComposite(AlphaComposite.SrcOver);
            }
        }
    }


    boolean maxwellActive = false;

    public int waterLevel() {
        try {
            return Math.max(0, (600 - currentWaterLevel) / 2) + (byte) (Math.abs(Math.sin(Math.toRadians(night.getShark().counterFloat)) * 8));
        } catch (Exception e) {
            return 0;
        }
    }

    BufferedImage millyShopImage = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);
    HashMap<Integer, Point> millyCoordinates = new HashMap<>(Map.of(0, new Point(280, 490),
            1, new Point(395, 485),
            2, new Point(525, 505),
            3, new Point(825, 427),
            4, new Point(955, 510)));
    Rectangle[] millyRects = new Rectangle[5];
    byte selectedMillyItem = 0;
    MillyItem[] millyShopItems = new MillyItem[5];
    int secondsInMillyShop = 0;
    float dreadUntilGrayscale = 1;
    float dreadUntilVignette = 1;
    boolean doMillyFlicker = false;

    boolean millyBackButtonSelected = true;

    void redrawMillyShop() {
        Graphics2D graphics2D = (Graphics2D) millyShopImage.getGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        
        BufferedImage coinImg = dabloon.request();
        
        if(night.getType().isEndless()) {
            String end = ".png";
            if (night.isBillyShop()) {
                end = "Billy.png";
            }
            graphics2D.drawImage(loadImg("/game/milly/bg" + end), 0, 0, null);

            if (secondsInMillyShop >= 3600) {
                graphics2D.drawImage(loadImg("/game/milly/bgOverlayMissing" + end), 597, 168, null);
            } else if (Arrays.equals(millyShopItems, new MillyItem[5])) {
                graphics2D.drawImage(loadImg("/game/milly/bgOverlayHappy" + end), 597, 168, null);
            }
        }
        
        if(night.getType().isBasement()) {
            graphics2D.drawImage(loadImg("/game/milly/bgBasement.png"), 0, 0, null);

            if (secondsInMillyShop >= 3600) {
                graphics2D.drawImage(loadImg("/game/milly/somewhat.png"), 597, 168, null);
                // IM SORRY I DIDNT SAVE THE PAINT NET PROJECT AND CLOSED IT TOO FAST
            } else if (Arrays.equals(millyShopItems, new MillyItem[5])) {
                graphics2D.drawImage(loadImg("/game/milly/bgOverlayHappyBasement.png"), 597, 168, null);
            }
            
            coinImg = evilDabloon.request();
        }


        graphics2D.setColor(new Color(0, 0, 0, 40));
        graphics2D.fillRect(0, 0, 1080, 640);
        
        graphics2D.setColor(Color.WHITE);
        graphics2D.setFont(comicSans30);
        int i = 0;
        while(i < 5) {
            if(millyShopItems[i] != null) {
                BufferedImage icon = millyShopItems[i].getIcon();
                String priceStr = millyShopItems[i].getPrice() + "";
                if(millyShopItems[i].getPrice() == 0) {
                    priceStr = "FREE!";
                }
                Point coords = millyCoordinates.get(i);
                short halfWidth = (short) (icon.getWidth() * 0.5);

                graphics2D.drawImage(icon, coords.x, coords.y - icon.getHeight(), null);
                graphics2D.drawImage(coinImg, coords.x + halfTextLength(graphics2D, priceStr) - 11 + halfWidth, coords.y + 5, null);

                graphics2D.drawString(priceStr, coords.x - halfTextLength(graphics2D, priceStr) - 16 + halfWidth, coords.y + 30);
            }
            i++;
        }

        if(night.getType().isEndless()) {
            if (endless.getNight() == 3) {
                if (secondsInMillyShop < 3600) {
                    graphics2D.drawImage(resize(birthdayHatImg, 135, 146, BufferedImage.SCALE_SMOOTH), 670, 94, null);
                }
            }
            if (endless.getNight() == 6) {
                graphics2D.drawImage(loadImg("/game/milly/larrySweepOverlay.png"), 0, 0, null);
            }
        }
        
        graphics2D.dispose();
    }

    void recalculateMillyRects() {
        millyRects = new Rectangle[5];

        int i = 0;
        while(i < 5) {
            if(millyShopItems[i] != null) {
                BufferedImage icon = millyShopItems[i].getIcon();
                Point coords = millyCoordinates.get(i);

                millyRects[i] = new Rectangle((int) (coords.x * widthModifier) + centerX, (int) ((coords.y - icon.getHeight()) * widthModifier) + centerY, (int) (icon.getWidth() * widthModifier), (int) (icon.getHeight() * heightModifier));
            }
            i++;
        }
    }

    BufferedImage rouletteScreen = new BufferedImage(530, 209, BufferedImage.TYPE_INT_ARGB);

    public void redrawRouletteScreen(AstartaBoss ab) {
        Graphics2D graphics2D = (Graphics2D) rouletteScreen.getGraphics();
        graphics2D.setColor(Color.BLACK);
        graphics2D.fillRect(0, 0, 530, 209);

        for(int i = 0; i < 58; i++) {
            int y = i * 170 - ab.getRouletteY();

            if(y > -151 && y < 210) {
                graphics2D.drawImage(roulette[ab.roulette1[i]].request(), 0, y, null);
                graphics2D.drawImage(roulette[ab.roulette2[i]].request(), 190, y, null);
                graphics2D.drawImage(roulette[ab.roulette3[i]].request(), 380, y, null);
            }
        }

        graphics2D.dispose();
    }


    Color warningRed = new Color(255, 40, 40, 230);
    Color transparentGray1 = new Color(160, 160, 160, 200);
    Color challengerColor = new Color(255, 0, 255, 0);
    short challengerAlpha = 0;
    BufferedImage challenger;
    String challengerString = "A NEW CHALLENGER HAS APPEARED!";

    byte columns = 4;
    byte rows = 2;

    private void secondHalf(Graphics2D graphics2D) {
        Point rescaledPoint = new Point((int) ((keyHandler.pointerPosition.x - centerX) / widthModifier), (int) ((keyHandler.pointerPosition.y - centerY) / heightModifier));
   
        switch (state) {
            case SKIBIDIDDY -> {
                graphics2D.setColor(Color.BLUE);
                graphics2D.setFont(comicSans60);
                graphics2D.drawString("bye bye", 540 - halfTextLength(graphics2D, "bye bye"), 200);
                graphics2D.drawString("mr chibert", 540 - halfTextLength(graphics2D, "mr chibert"), 270);
                
                graphics2D.drawImage(beastImages[0].request(), 40, 300, null);
            }
            case MENU -> {
                if(isDecember && !snowflakes.isEmpty()) {
                    graphics2D.setComposite(AlphaComposite.SrcOver.derive(0.6F));
                    graphics2D.setColor(Color.WHITE);

                    synchronized(snowflakes) {
                        for (Snowflake snowflake : snowflakes) {
                            int y = snowflake.getY();
                            int x = (int) (snowflake.getX() + 40 * Math.sin(snowflake.getStartingPhase() + fixedUpdatesAnim / 40F));

                            if (x < -60 || x > 1140 || y < -60)
                                continue;

                            if (snowflake.getType()) {
                                int size = (int) (snowflake.getZ() * 20);
                                int halfSize = size / 2;
                                graphics2D.fillOval(x - halfSize, snowflake.getY() - halfSize, size, size);
                            } else {
                                BufferedImage img = snowflake.getSource();
                                if(snowflake.getRotation() != 0) {
                                    img = rotateRadians(img, snowflake.getRotation(), true);
                                }
                                int halfSize = img.getWidth() / 2;

                                graphics2D.drawImage(img, x - halfSize, y - halfSize, null);
                            }
                        }
                    }

                    graphics2D.setComposite(AlphaComposite.SrcOver);
                }
                
                
                graphics2D.drawImage(!isScaryLogo ? logo.request() : scaryLogo.request(), 30, 30, null);
                
                graphics2D.setColor(white200);
                graphics2D.setFont(yuGothicPlain60);

                short x = (short) visibleMenuButtons.size();
                graphics2D.setColor(transparentGray1);
                byte z = 0;
                while (z < x) {
                    String option = visibleMenuButtons.get(z);
                    if(z == selectedOption - menuButtonOffset) {
                        graphics2D.setColor(white200);
                    }

                    graphics2D.drawString(option, 40, 580 + -10 * x * z - 40 * x + 100 * z);
                    graphics2D.setColor(transparentGray1);
                    z++;
                }
                if(menuButtons.size() > 4) {
                    if(menuButtonOffset + 4 != menuButtons.size()) {
                        graphics2D.drawImage(moreMenu[0], 185, 617, null);
                    }
                    if(menuButtonOffset > 0) {
                        graphics2D.drawImage(moreMenu[1], 185, 347, null);
                    }
                }
                
                if(musicDiscs.size() > 1) {
                    graphics2D.drawImage(musicMenu, 950, 400, null);
                }
                graphics2D.drawImage(discord, 950, 510, null);
                
                if(isAprilFools) {
                    graphics2D.drawImage(platButton.request(), 650, 50, null);
                    if(hoveringPlatButton) {
                        graphics2D.setColor(black80);
                        graphics2D.fillRect(650, 50, 400, 45);
                    }
                }
                
                graphics2D.setColor(white200);
                graphics2D.setFont(comicSansBold25);
                graphics2D.drawString(tip, 10, 630);

                graphics2D.drawString("v" + version, 1075 - versionTextLength, 635);
                
                if(pepVoteButton != null) {
                    BufferedImage thumbnail = hoveringPepVoteButton ? pepVoteButton.getThumbnailSelected() : pepVoteButton.getThumbnail();
                    thumbnail = rotateRadians(thumbnail, (Math.sin(fixedUpdatesAnim / 60F) * 0.2f), false);
                    
                    graphics2D.drawImage(thumbnail, 900 - thumbnail.getWidth() / 2, 270 - thumbnail.getHeight() / 2, null);
                    
                    String text = pepVoteButton.getTitle();
                    graphics2D.setColor(Color.WHITE);
                    graphics2D.drawString(text, 900 - halfTextLength(graphics2D, text), 370);
                }
            }
            case ITEMS -> {
                if(everySecond20th.containsKey("startSimulation")) {
                    graphics2D.drawImage(lastItemsMenu, 0, 0, 1080, 640, null);
                } else {
                    drawCloseButton(graphics2D);

                    graphics2D.setFont(yuGothicPlain80);

                    graphics2D.setColor(new Color(160, 160, 160, 200));
                    if (startButtonSelected) {
                        graphics2D.setColor(white200);

                        if (type == GameType.CUSTOM) {
                            graphics2D.setColor(Color.WHITE);
                        }
                    }
                    graphics2D.drawString(">> " + getString("start"), 730, 590);

                    if (rows >= 4) {
                        graphics2D.setColor(Color.DARK_GRAY);

                        double d = (double) (itemScrollY) / (rows * 170 + (207 - 40 * Math.min(rows, 4)) - 640);
                        int height = (640 / rows);
                        graphics2D.fillRect(0, (int) (d * (640 - height)), 10, height);
                    }
                }
            }
            case BINGO -> {
                drawCloseButton(graphics2D);

                String startBingo = ">> " + getString("startBingo");

                graphics2D.setColor(white200);
                graphics2D.setFont(comicSans60);

                if(bingoCard.isGenerated()) {
                    startBingo = ">> " + getString("stopBingo");
                }

                if(bingoCard.isCompleted()) {
                    graphics2D.drawString("You completed the Pepingo!", bingoTextLength, 300);
                    startBingo = ">> " + getString("restartBingo");
                } else if(bingoCard.isFailed()) {
                    graphics2D.drawString("You failed the Pepingo!", bingoTextLength + 20, 300);
                    startBingo = ">> " + getString("restartBingo");
                }

                graphics2D.setFont(yuGothicPlain60);
                graphics2D.drawString(startBingo, 30, 600);
                graphics2D.setFont(yuGothicPlain50);
                graphics2D.drawString(getString("timeSpent"), 30, 410);
                graphics2D.drawString(bingoCard.getMinutes() + getString("m") + " " + bingoCard.getSeconds() + getString("s"), 30, 460);

                graphics2D.setColor(white120);
                graphics2D.setFont(yuGothicBoldItalic40);
                graphics2D.drawString(getString("24minutes"), 30, 510);

                graphics2D.setFont(yuGothicPlain30);
                graphics2D.drawString(getString("sillyGame"), 30, 140);
                graphics2D.drawString(getString("completeAll"), 30, 180);
                graphics2D.drawString(getString("tasksTimeLimit"), 30, 220);
            }
            case PLAY -> {
                drawCloseButton(graphics2D);
            }
            case ACHIEVEMENTS -> {
                drawCloseButton(graphics2D);

                graphics2D.setColor(Color.GRAY);

                if(!achievementState || shiftingAchievements) {
                    if(holdingAchievementSlider) {
                        graphics2D.setColor(new Color(210, 210, 210));
                    }
                    int categories = 4;
                    double d = (double) (achievementsScrollY) / (160 + 30 + Achievements.values().length * 155 + categories * 80 - 530);
                    int height = 530 / Achievements.values().length * 2;
                    graphics2D.fillRect(-achievementsScrollX, (int) (d * (530 - height)) + 110, 10, height);
                }
                if(achievementState || shiftingAchievements) {
                    double d = (double) (statisticsScrollY) / (150 + Statistics.values().length * 40 - 650);
                    int height = 650 / Statistics.values().length * 2;
                    graphics2D.fillRect(2150 - achievementsScrollX, (int) (d * (530 - height)) + 110, 10, height);
                }
            }
            case SETTINGS -> {
                drawCloseButton(graphics2D);

                if(keyHandler.holdingVolumeButton) {
                    graphics2D.setColor(white160);
                    graphics2D.fillOval((short) (volume * 800) + 115, 220 + settingsScrollY, 50, 50);
                }
                if(keyHandler.confirmNightReset) {
                    graphics2D.setColor(warningRed);
                    graphics2D.setFont(yuGothicBoldItalic40);
                    graphics2D.drawString(getString("firstNightWarning"), 60, 600);
                }
            }
            case GAME -> {
                boolean proceed = true;
                if(night.getEvent() == GameEvent.DYING && !drawCat) {
                    List<String> ignore = List.of("overseer", "pepito", "beast", "msi");
                    proceed = !ignore.contains(jumpscareKey);
                }

                if(!night.getEvent().isInGame() && proceed)
                    return;
                
                try {
                    Enviornment e = night.env();
                    int maxOffset = e.maxOffset();
                    int offset = fixedOffsetX - maxOffset;
                    
                    
                    if (!inCam) {
                        if(keyHandler.holdingB) {
                            graphics2D.setColor(new Color(140, 80, 70));
                            graphics2D.setFont(comicSans60);

                            for (Integer numbert : night.getDoors().keySet().stream().toList()) {
                                Door door = night.getDoors().get(numbert);
                                numbert++;
                                Rectangle bounds = door.getHitbox().getBounds();
                                int centerX = offset + bounds.x + bounds.width / 2;
                                int centerY = bounds.y + bounds.height / 2;

                                graphics2D.drawString(numbert + "", centerX - halfTextLength(graphics2D, numbert + ""), centerY + 20);
                            }
                        }
                        
                        if(megaSoda.isEnabled()) {
                            if(night.getColaCat().megaSodaWithheld > 0) {
                                graphics2D.setFont(comicSans30);
                                graphics2D.setColor(white200);
                                graphics2D.drawString(getString("megaSodaWithheld"), (int) (offset + e.megaSoda.x + e.megaSoda.getWidth() / 2 - halfTextLength(graphics2D, getString("megaSodaWithheld"))), e.megaSoda.y + e.megaSoda.height / 3F);
                                graphics2D.drawString(night.getColaCat().megaSodaWithheld + "", (int) (offset + e.megaSoda.x + e.megaSoda.getWidth() / 2 - halfTextLength(graphics2D, night.getColaCat().megaSodaWithheld + "")), e.megaSoda.y + e.megaSoda.height / 3 + 40);
                            }
                            
                            if(megaSoda.isEnabled()) {
                                // colacat action
                                if (night.getColaCat().megaColaY < 1000) {
                                    int floater = (int) (-night.getColaCat().megaColaY + Math.sin(fixedUpdatesAnim / 40F) * (e.megaSoda.width / 17F));

                                    int height = e.megaSoda.height / 4 * night.megaSodaUses;
                                    if(night.getColaCat().megaSodaWithheld > 0) {
                                        if(tintedMegaSodaImg == null) {
                                            BufferedImage source = megaSoda.getIcon().getSubimage(0, 145 - (145 / 4 * night.megaSodaUses), 109, (145 / 4 * night.megaSodaUses));
                                        
                                            tintedMegaSodaImg = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
                                            Graphics2D goofyGraphics = tintedMegaSodaImg.createGraphics();
                                            goofyGraphics.drawImage(source, 0, 0, null);
                                            goofyGraphics.setComposite(MultiplyCompositeForARGB.Multiply);
                                            int tint = (int) (255 - tintAlpha);
                                            goofyGraphics.setColor(new Color(tint, tint, tint));
                                            goofyGraphics.fillRect(0, 0, source.getWidth(), source.getHeight());
                                            goofyGraphics.dispose();
                                        }
                                        
                                        graphics2D.drawImage(tintedMegaSodaImg, offset + e.megaSoda.x + (int) (night.getColaCat().megaColaX), floater + e.megaSoda.y + e.megaSoda.height - height, e.megaSoda.width, height, null);
                                    }

                                    Rectangle cola = new Rectangle((int) (e.megaSoda.x - e.megaSoda.getWidth() / 2 + night.getColaCat().megaColaX), floater + e.megaSoda.y + e.megaSoda.height / 2, (int) (e.megaSoda.getWidth() * 2), (int) (e.megaSoda.getWidth() * 1.9F));
                                    Rectangle flame = new Rectangle(0, 0, (int) (cola.getHeight() / 3), (int) (cola.getHeight() / 2.3F));

                                    int flame1x = (int) (offset + cola.x + cola.width / 3.5F);
                                    int flame2x = (int) (offset + cola.x + cola.width / 1.9F);

                                    int flame1y = cola.y + cola.height / 5 * 4;
                                    int flame2y = cola.y + cola.height / 3 * 2;

                                    double sin = Math.sin(fixedUpdatesAnim / 2F) / 3F + 0.5F;
                                    double cos = Math.cos(fixedUpdatesAnim) / 3F + 0.5F;

                                    BufferedImage mirror = mirror(megaColaFlame.request(), 1);
                                    graphics2D.drawImage(mirror, flame1x, flame1y, flame.width, (int) (flame.height / 2 + flame.height * sin), null);
                                    graphics2D.drawImage(mirror, flame2x, flame2y, flame.width, (int) (flame.height / 2 + flame.height * sin), null);

                                    graphics2D.drawImage(megaColaFlame.request(), flame1x, flame1y, flame.width, (int) (flame.height / 2 + flame.height * cos), null);
                                    graphics2D.drawImage(megaColaFlame.request(), flame2x, flame2y, flame.width, (int) (flame.height / 2 + flame.height * cos), null);

                                    if(megaColaImg == null) {
                                        megaColaImg = resize(megaCola.request(), cola.width, cola.height, Image.SCALE_FAST);
                                        megaColaGlowImg = resize(megaColaGlow.request(), cola.width, cola.height, Image.SCALE_FAST);
                                    }
                                    if(tintedMegaColaImg == null) {
                                        tintedMegaColaImg = new BufferedImage(cola.width, cola.height, BufferedImage.TYPE_INT_ARGB);
                                        Graphics2D goofyGraphics = tintedMegaColaImg.createGraphics();
                                        goofyGraphics.drawImage(megaColaImg, 0, 0, null);
                                        goofyGraphics.setComposite(MultiplyCompositeForARGB.Multiply);
                                        int tint = (int) (255 - tintAlpha);
                                        goofyGraphics.setColor(new Color(tint, tint, tint));
                                        goofyGraphics.fillRect(0, 0, cola.width, cola.height);
                                        goofyGraphics.dispose();
                                    }
                                    graphics2D.drawImage(tintedMegaColaImg, offset + cola.x, cola.y, null);

                                    graphics2D.setComposite(AlphaComposite.SrcOver.derive((float) (cos * 2F + sin * 2F + Math.random()) / 5F));
                                    graphics2D.drawImage(megaColaGlowImg, offset + cola.x, cola.y, null);
                                    graphics2D.setComposite(AlphaComposite.SrcOver);
                                }
                            }
                        }
                        
                        if(night.megaSodaLightsOnTicks > 0) {
                            iDontWannaScrollThroughThisEveryTime(graphics2D, offset, e.megaSoda);
                        }
                        
                        if(type == GameType.DAY) {
                            if(neonSogAnim > 1) {
                                int size = (int) (400 * neonSogBallSize);
                                int x = offset + 740 - size / 2;
                                int y = 320 - size / 2;

                                graphics2D.drawImage(neonSogBallImage.request(), x, y, size, size, null);
                            }
                            if(night.getNeonSogBall() != null) {
                                NeonSogBall ball = night.getNeonSogBall();
                                
                                int x = offset + 740 - 80 + Math.round(ball.x);
                                int y = Math.round(ball.h) - 80;
                                        
                                graphics2D.drawImage(neonSogBallImage.request(), x, y, 160, 160, null);
                            }
                        }
                        
                        if (night.getLemonadeCat().isActive()) {
                            LemonadeCat lemonadeCat = night.getLemonadeCat();
                            
                            int baseIndex = 0;
                            if(lemonadeCat.isNine()) {
                                baseIndex = 2;
                            }
                            if(lemonadeCat.damaged) {
                                baseIndex++;
                            }
                            // base image size 260;240
                            BufferedImage gato = lemonadeGato[baseIndex].request();
                            
                            if(lemonadeCat.getBackflipRadians() != 0) {
                                gato = rotateRadians(gato, lemonadeCat.getBackflipRadians(), false);
                            }
                            graphics2D.drawImage(gato, offset + lemonadeCat.getX() + 130 - gato.getWidth() / 2, (int) (400 - Math.sin(lemonadeCat.getCurrentFunction()) * 150 + 120 - gato.getHeight() / 2), null);

                            if(lemonadeCat.lastHitbox != null) {
                                if(lemonadeCat.hitboxAlpha > 0) {
                                    graphics2D.setColor(new Color(255, 236, 170, (int) (lemonadeCat.hitboxAlpha * 80F)));
                                    graphics2D.translate(offset, 0);
                                    graphics2D.fillPolygon(lemonadeCat.lastHitbox);
                                    graphics2D.translate(-offset, 0);
                                }
                            }
                            
                            for (byte i = 0; i < 4; i++) {
                                float zoom = lemonadeCat.lemonadeZoom[i];
                                Point point = lemonadeCat.lemonadePos[i];

                                if (zoom != 0) {
                                    if (zoom > 0.1) {
                                        graphics2D.drawImage(lemon.request(), fixedOffsetX + point.x - (int) (200 * zoom), (int) (point.y - 150 * zoom),
                                                (int) (400 * zoom), (int) (300 * zoom), null);
                                    }
                                }
                            }
                        }
                        if (night.getToleTole().isActive()) {
                            BufferedImage img = toleToleMain.request();
                            int jumpHeight = 150;
                            int yAnchor = 440;

                            if(night.getToleTole().isAimingDoor()) {
                                img = toleToleDoor.request();
                                jumpHeight = 100;
                                float h = night.getToleTole().getCurrentSize();
                                img = resize(img, (int) (180 * h), (int) (200 * h), BufferedImage.SCALE_FAST);

                                Rectangle j = night.getToleTole().getDoorToAim().getHitbox().getBounds();
                                yAnchor = (int) ((640 - 200 * h) - 640 + (j.y + j.height));
                            }
                            if(night.getToleTole().isGonnaLeave()) {
                                img = toleToleLeave.request();
                                jumpHeight = 40;
                                float h = night.getToleTole().getAlpha() * night.getToleTole().getCurrentSize();
                                img = resize(img, (int) (180 * h), (int) (200 * h), BufferedImage.SCALE_FAST);

                                Rectangle j = night.getToleTole().getDoorToAim().getHitbox().getBounds();
                                yAnchor = (int) ((640 - 200 * night.getToleTole().getCurrentSize()) - 640 + (j.y + j.height));
                            }

                            if(night.getToleTole().getX() < night.getToleTole().getGoalX()) {
                                img = mirror(img, 1);
                            }

                            graphics2D.drawImage(img, offset + night.getToleTole().getX() + 90 - img.getWidth() / 2,
                                    (int) (yAnchor - Math.sin(night.getToleTole().getY()) * jumpHeight + 100 - img.getHeight() / 2), null);
                        }
                        if(night.getToleTole().isFlash()) {
                            graphics2D.setColor(Color.WHITE);
                            graphics2D.fillRect(0, 0, 1080, 640);
                        }


                        if(e instanceof Basement env) {
                            if(env.isMillyLamp()) {
                                graphics2D.setColor(black200);
                                graphics2D.fillRect(offset, 0, 400, 640);
                                graphics2D.fillRect(offset + 1080, 0, 400, 640);

                                List<Point> triangle1 = new ArrayList<>();
                                triangle1.add(new Point(offset + 400, 0));
                                triangle1.add(new Point(offset + 700, 0));
                                triangle1.add(new Point(offset + 400, 640));
                                graphics2D.fillPolygon(getPolygon(triangle1));

                                List<Point> triangle2 = new ArrayList<>();
                                triangle2.add(new Point(offset + 780, 0));
                                triangle2.add(new Point(offset + 1080, 0));
                                triangle2.add(new Point(offset + 1080, 640));
                                graphics2D.fillPolygon(getPolygon(triangle2));
                            }
                            if(env.getStage() == 5 || env.getStage() == 6) {
                                graphics2D.drawImage(redAlarmOff.request(), offset + 660, env.getRedAlarmY(), null);
                                if (env.doWiresWork()) {
                                    graphics2D.setComposite(AlphaComposite.SrcOver.derive((float) (Math.sin(fixedUpdatesAnim / 5F) / 2F + 0.5)));
                                    graphics2D.drawImage(redAlarm.request(), offset + 225, env.getRedAlarmY(), null);
                                    graphics2D.setComposite(AlphaComposite.SrcOver);
                                }
                                
                                if(env.getGasLeakMillis() > 0) {
                                    BufferedImage newImage = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_ARGB);
                                    Graphics2D sigmaGraphics = (Graphics2D) newImage.getGraphics();
                                    sigmaGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                                    sigmaGraphics.setFont(comicSans80);
                                    sigmaGraphics.drawString("!! UNEXPECTED EVENT !!", 540 - halfTextLength(sigmaGraphics, "!! UNEXPECTED EVENT !!"), 280);
                                    sigmaGraphics.drawString("!! GAS LEAK !!", 540 - halfTextLength(sigmaGraphics, "!! GAS LEAK !!"), 360);
                                    sigmaGraphics.dispose();
                                    newImage = trimImage(newImage);

                                    sigmaGraphics = (Graphics2D) newImage.getGraphics();
                                    sigmaGraphics.setComposite(ReplaceComposite.Replace);
                                    int xOffset = (fixedUpdatesAnim * 12) % newImage.getWidth();
                                    sigmaGraphics.drawImage(theStrip.request(), -xOffset, 0, newImage.getWidth(), newImage.getHeight(), null);
                                    sigmaGraphics.drawImage(theStrip.request(), -xOffset + newImage.getWidth(), 0, newImage.getWidth(), newImage.getHeight(), null);
                                    sigmaGraphics.dispose();

                                    graphics2D.drawImage(newImage, 540 - newImage.getWidth() / 2, 320 - newImage.getHeight() / 2, null);

                                    graphics2D.setFont(comicSans80);
                                    graphics2D.setColor(new Color(255, 0, 0, (int) (255 * (Math.min(1, env.getGasLeakMillis() / 4000F)))));
                                    graphics2D.drawString("!! UNEXPECTED EVENT !!", 540 - halfTextLength(graphics2D, "!! UNEXPECTED EVENT !!"), 310);
                                    graphics2D.drawString("!! GAS LEAK !!", 540 - halfTextLength(graphics2D, "!! GAS LEAK !!"), 390);
                                }

                                if(env.getOverseerMove() > 1) {
                                    BufferedImage overseer = resize(basementOverseer.request(), (int) (152 * env.getOverseerMove()), (int) (247 * env.getOverseerMove()), Image.SCALE_FAST);
                                    graphics2D.drawImage(overseer, offset + 740 - overseer.getWidth() / 2, (int) (340 + 120 * env.getOverseerMove() - overseer.getHeight()), null);
                                }
                            }
                            if(env.isSparking()) {
                                float s = env.sparkSize;
                                graphics2D.drawImage(alphaify(sparks.request(), Math.min(1, 3 - s)), offset + 1350 - (int) (84 * s + Math.random() * 8 - 4), 13, (int) (100 * s), (int) (86 * s), null);
                                
                                s /= 2;
                                graphics2D.drawImage(alphaify(sparks.request(), Math.min(1, 3 - s)), offset + 1350 - (int) (84 * s + Math.random() * 8 - 4), 13, (int) (100 * s), (int) (86 * s), null);
                            }
                        } else if(night.getType() == GameType.HYDROPHOBIA) {
                            HChamber env = (HChamber) e;
                            
                            if(env.timerText != null) {
                                BufferedImage timerText = env.timerText;
                                
                                if(env.getWobbleFade() > 0) {
                                    timerText = vertWobble(timerText, env.getWobbleFade() / 12F, 1, 1, env.getWobbleFade() / 12F + 3);
                                    timerText = alphaify(timerText, 1 - env.getWobbleFade() / 120F);
                                }
                                
                                if(env.timer.x < 1480) {
                                    graphics2D.drawImage(timerText, offset + env.timer.x, env.timer.y, env.timer.width, env.timer.height, null);
                                    graphics2D.drawImage(timerText, offset + env.timer.x, env.timer.y, env.timer.width, env.timer.height, null);
                                }
                                
                                if(env.timer.x >= 1480 && night.getOverseer().getRage() > 0) {
                                    graphics2D.setComposite(AlphaComposite.SrcOver.derive(Math.min(1, night.getOverseer().getRadius() / 450F)));
                                    graphics2D.drawImage(timerText, 299, 197, 482, 246, null);
                                    graphics2D.setComposite(AlphaComposite.SrcOver);
                                }
                            }
                            
                            if(night.getOverseer().isActive()) {
                                graphics2D.setColor(new Color(0, 0, 0, 50));
                                graphics2D.fillRect(offset + night.getOverseer().getX(), 0, 200, 640);
                                
                                graphics2D.drawImage(overseerBack.request().getSubimage(0, 0, 200, night.getOverseer().getHeight()), offset + night.getOverseer().getX() + (int) (Math.round(Math.cos(fixedUpdatesAnim / 16F) * 4)) * 2, 140, null);
                                graphics2D.drawImage(overseerBack.request().getSubimage(0, 0, 200, night.getOverseer().getHeight()), offset + night.getOverseer().getX() + (int) (Math.round(Math.sin(fixedUpdatesAnim / 8F) * 4)) * 2, 140, null);
                                graphics2D.drawImage(overseerFront.request().getSubimage(0, 0, 200, Math.round(night.getOverseer().getHeight() / 500F * 552F)), offset + night.getOverseer().getX(), 88, null);
                                
                                if(night.getOverseer().getRadius() > 0) {
                                    int radius1 = night.getOverseer().getRadius();
                                    int radius2 = (int) (night.getOverseer().getRadius() * 0.66);
                                    
                                    graphics2D.setColor(new Color(255, 0, 0, 70));
                                    graphics2D.fillOval(offset + night.getOverseer().getX() + 100 - radius1, 135 - radius1, radius1 * 2, radius1 * 2);
                                    graphics2D.fillOval(offset + night.getOverseer().getX() + 100 - radius2, 135 - radius2, radius2 * 2, radius2 * 2);
                                }
                            }
                            
                            
                            if(env.cameraGuidelineAlpha < 300 && env.cameraGuidelineAlpha > 0) {
                                graphics2D.setFont(comicSans40);
                                int h = (int) (env.cameraGuidelineAlpha / 300d * 255d);
                                graphics2D.setColor(new Color(255, 255, 255, h));
                                graphics2D.drawString(getString("hcCamGuideline"), 540 - halfTextLength(graphics2D, getString("hcCamGuideline")), 560);
                            }
                            
                            if(env.isHoveringConditioner()) {
                                graphics2D.setColor(Color.WHITE);
                                graphics2D.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));

                                if(env.getRoom() <= 0) {
                                    graphics2D.drawString(getString("hcConditionerOff"), 540 - halfTextLength(graphics2D, getString("hcConditionerOff")), 420);
                                } else {
                                    graphics2D.drawString(getString("hcConditionerOn"), 540 - halfTextLength(graphics2D, getString("hcConditionerOn")), 420);
                                }
                            }

                            if(env.isExitSignLitUp()) {
                                if(env.getBgIndex() == 10) {
                                    graphics2D.drawImage(hcExitSign.request(), offset + 670, 233, null);
                                }
                                if(env.getBgIndex() == 13) {
                                    graphics2D.drawImage(hcExitSign.request(), offset + 840, 293, null);
                                }
                            }
                            
//                            graphics2D.setColor(white160);
//                            graphics2D.setFont(sansSerifPlain70);
//                            graphics2D.drawString("rooms: " + env.getRoom(), 400, 200);
//                            graphics2D.drawString("till key: " + env.roomsTillKey, 400, 270);
//                            if(env.hasCup()) {
//                                graphics2D.drawString("HAS CUP!!!", 100, 500);
//                            }
                        }

                        if(night.getBeast().isActive()) {
                            int h = 0;
                            if(night.getEvent() != GameEvent.DYING) {
                                h = (fixedUpdatesAnim / 2) % 6;
                            }
                            graphics2D.drawImage(beastGlow.request(), night.getBeast().getX() - 320, 0, null);
                            graphics2D.drawImage(beastImages[h].request(), night.getBeast().getX() - 240, 80, null);
                        }
                        
                        
                        if(night.getMaki().isActive()) {
                            if(night.getMaki().alpha > 0) {
                                BufferedImage img = alphaify(makiWarning.request(), night.getMaki().alpha);
                                Rectangle bounds = night.getDoors().get((int) night.getMaki().getDoor()).getHitbox().getBounds();

                                int makiWarningX = offset + bounds.x + bounds.width / 2 - 37;
                                int makiWarningY = bounds.y + bounds.height / 2 - 37;
                                graphics2D.drawImage(img, makiWarningX, makiWarningY, null);
                            }
                        }
                        if (night.getMSI().isEnabled()) {
                            if (night.getMSI().isActive()) {
                                byte msiIndex = 0;
                                if (night.getMSI().crisscross && night.getMSI().left) {
                                    msiIndex = 1;
                                }
                                if (night.getMSI().isHell) {
                                    msiIndex = 2;
                                }
                                if (night.getMSI().isShadow) {
                                    msiIndex = 3;
                                }
                                graphics2D.drawImage(msiImage[msiIndex], offset + 500, 50, null);
                                
                                
                                if (night.getMSI().firstAction) {
                                    graphics2D.setColor(Color.GREEN);
                                    graphics2D.setFont(sansSerifPlain40);

                                    if (night.getMSI().left) {
                                        graphics2D.drawImage(msiArrow.request(), 20, 280, null);
                                    } else {
                                        graphics2D.drawImage(mirror(msiArrow.request(), 1), 980, 280, null);
                                    }
                                }

                                graphics2D.setColor(Color.WHITE);
                                graphics2D.setFont(yuGothicPlain120);

                                if (night.getMSI().left) {
                                    graphics2D.drawString(getString("left"), 540 - halfTextLength(graphics2D, getString("left")), 330);
                                } else {
                                    graphics2D.drawString(getString("right"), 540 - halfTextLength(graphics2D, getString("right")), 330);
                                }
                            } else if (night.getMSI().arriving) {
                                graphics2D.setColor(Color.WHITE);
                                graphics2D.setFont(yuGothicPlain120);
                                graphics2D.drawString(getString("loading"), 540 - halfTextLength(graphics2D, getString("loading")), 330);
                            }
                        }
                        if(night.getWires().isActive()) {
                            BufferedImage wiresImg = type == GameType.SHADOW ? purplify(this.wiresImg.request()) : this.wiresImg.request();
                            BufferedImage wiresText = type == GameType.SHADOW ? purplify(this.wiresText[1]) : this.wiresText[0];
                            int add = (fixedUpdatesAnim / 60) % 2 * 10;

                            Rectangle hitbox = night.getWires().getHitbox();
                            graphics2D.drawImage(wiresImg, offset + hitbox.x + hitbox.width / 2 - 105, hitbox.y + hitbox.height / 2 - 185 + add, null);
                            graphics2D.drawImage(wiresText, offset + hitbox.x + hitbox.width / 2 - 105, hitbox.y + hitbox.height / 2 - 185 + add, null);
                        }
                        if(night.getScaryCat().isActive()) {
                            ScaryCat scaryCat = night.getScaryCat();
                            
                            float alpha = scaryCat.getAlpha();
                            int index = 0;
                            if(type == GameType.SHADOW) {
                                index = 1;
                            }
                            if(scaryCat.isNine()) {
                                index = 2;

                                graphics2D.setComposite(AdditiveComposite.Add);
                                synchronized (nuclearCatEyes) {
                                    for (NuclearCatEye eye : nuclearCatEyes) {
                                        int size = (int) ((2 - eye.alpha) * 77);
                                        int halfSize = size / 2;

                                        graphics2D.drawImage(alphaify(nuclearCatEye.request(), eye.alpha), offset + eye.x - halfSize, eye.y - halfSize, size, size, null);
                                    }
                                }
                                graphics2D.setComposite(AlphaComposite.SrcOver);
                            }

                            graphics2D.setComposite(AlphaComposite.SrcOver.derive(Math.max(0, alpha)));
                            graphics2D.drawImage(scaryCatImage[index].request(), offset + night.getScaryCat().getX(), 170, null);
                            graphics2D.setComposite(AlphaComposite.SrcOver);
                            
                            if(index == 2) {
                                int sinY = (int) (Math.sin(scaryCat.getEyesRotation()) * 340);
                                int cosX = (int) (Math.cos(scaryCat.getEyesRotation()) * 340);
                                
                                graphics2D.drawImage(nuclearCatEye.request(), offset + scaryCat.getX() + 250 + cosX - 33, 320 + sinY - 33, null);
                                graphics2D.drawImage(nuclearCatEye.request(), offset + scaryCat.getX() + 250 - cosX - 33, 320 - sinY - 33, null);
                            }
                            if(scaryCat.getDistance() < 180 && Math.random() < 0.9) {
                                BufferedImage img = scaryCatMove[index].request();
                                int randomX = (int) (Math.random() * 80 - 40);
                                int randomY = (int) (Math.random() * 20 - 10);
                                graphics2D.drawImage(img, 540 - img.getWidth() / 2 + randomX, 320 - img.getHeight() / 2 + randomY, null);
                            }
                            if(scaryCat.getDistance() < 100 && Math.random() < 0.6) {
                                BufferedImage img = scaryCatWarn[index].request();
                                int randomX = (int) (Math.random() * 10 - 5);
                                int randomY = (int) (Math.random() * 10 - 5);
                                graphics2D.drawImage(img, 540 - img.getWidth() / 2 + randomX, 320 - img.getHeight() / 2 + randomY, null);
                            }

                            if(scaryCat.getCount() > 0) {
                                graphics2D.setComposite(AlphaComposite.SrcOver.derive(Math.max(0, alpha / 2)));
                                for(int i = 0; i < scaryCat.getCount(); i++) {
                                    graphics2D.drawImage(scaryCatImage[index].request(), (int) (offset + scaryCat.getX() + Math.random() * 300 - 150), (int) (170 + Math.random() * 300 - 150), null);
                                }
                                graphics2D.setComposite(AlphaComposite.SrcOver);
                                
                                if(index == 2) {
                                    graphics2D.setColor(new Color(1, 0.8F, 0.5F, Math.max(0, alpha) / (Math.max(1, 4 - scaryCat.getCount()))));
                                } else {
                                    graphics2D.setColor(new Color(1 - (index / 2F), 1 - index, 1, Math.max(0, alpha) / (Math.max(1, 4 - scaryCat.getCount()))));
                                }
                                graphics2D.fillRect(0, 0, 1080, 640);
                            }
                        }
                        
                        if(night.getKiji().isActive()) {
                            graphics2D.drawImage(alphaify(kijiCrosshair.request(), Math.min(1F, night.getKiji().getProgress())), 235, 18, null);
                            
                            if(night.getKiji().getState() == 0) {
                                if (night.getKiji().getProgress() > 1F) {
                                    float range = night.getKiji().getRange();
                                    graphics2D.drawImage(kijiText[0].request(), 440 + (int) (Math.random() * 20 * range - 10 * range), 300 + (int) (Math.random() * 10 * range - 5 * range), null);
                                }
                            } else {
                                graphics2D.setColor(Color.BLACK);
                                graphics2D.fillRect(0, 0, 1080, 640);
                                
                                BufferedImage g = kijiText[night.getKiji().getState()].request();
                                graphics2D.drawImage(g, 540 - g.getWidth() / 2, 320 - g.getHeight() / 2, null);
                            }
                        }
                        
                        
                        if(night.getJumpscareCat().isActive()) {
                            float z = night.getJumpscareCat().getZoom();
                        
                            if(night.getJumpscareCat().isFading()) {
                                graphics2D.setComposite(AlphaComposite.SrcOver.derive(night.getJumpscareCat().getFade()));
                            }
                            graphics2D.drawImage(jumpscareCat.request(), 540 - (int) (200 * z), 320 - (int) (150 * z), (int) (400 * z), (int) (300 * z), null);
                            graphics2D.setComposite(AlphaComposite.SrcOver);
                        }

                        if(shadowTicket.isEnabled()) {
                            if(Achievements.HALFWAY.isObtained()) {
                                int firstCheckpointX = 540;
                           
                                graphics2D.setFont(comicSans40);
                                graphics2D.setColor(Color.WHITE);

                                if(reachedAstartaBoss) {
                                    firstCheckpointX = 320;
                                    Vector2D checkpointVector = new Vector2D(760, 320);
                                    checkpointVector.subtract(rescaledPoint.x, rescaledPoint.y);
                                    checkpointVector.divide(12);

                                    graphics2D.drawImage(checkpointAstartaBoss[1].request(), 604 + (int) (8 * checkpointVector.x), 163 + (int) (8 * checkpointVector.y), null);
                                    graphics2D.drawImage(checkpointAstartaBoss[1].request(), 604 + (int) (6 * checkpointVector.x), 163 + (int) (6 * checkpointVector.y), null);
                                    graphics2D.drawImage(checkpointAstartaBoss[1].request(), 604 + (int) (3 * checkpointVector.x), 163 + (int) (3 * checkpointVector.y), null);
                                }

                                Vector2D checkpointVector = new Vector2D(firstCheckpointX, 320);
                                checkpointVector.subtract(rescaledPoint.x, rescaledPoint.y);
                                checkpointVector.divide(12);

                                graphics2D.drawImage(checkpointHalfway[1].request(), firstCheckpointX - 156 + (int) (8 * checkpointVector.x), 163 + (int) (8 * checkpointVector.y), null);
                                graphics2D.drawImage(checkpointHalfway[1].request(), firstCheckpointX - 156 + (int) (6 * checkpointVector.x), 163 + (int) (6 * checkpointVector.y), null);
                                graphics2D.drawImage(checkpointHalfway[1].request(), firstCheckpointX - 156 + (int) (3 * checkpointVector.x), 163 + (int) (3 * checkpointVector.y), null);
                                graphics2D.drawImage(checkpointHalfway[0].request(), firstCheckpointX - 156, 163, null);

                                graphics2D.drawString(getString("halfwayPoint"), firstCheckpointX - halfTextLength(graphics2D, getString("halfwayPoint")), 520);
                                graphics2D.drawString(getString("click"), firstCheckpointX - halfTextLength(graphics2D, getString("click")), 560);

                                if(reachedAstartaBoss) {
                                    graphics2D.drawImage(checkpointAstartaBoss[0].request(), 604, 163, null);

                                    graphics2D.drawString(getString("astartaTime"), 760 - halfTextLength(graphics2D, getString("astartaTime")), 520);
                                    graphics2D.drawString(getString("click"), 760 - halfTextLength(graphics2D, getString("click")), 560);
                                }

                                if(shadowCheckpointUsed == 0) {
                                    graphics2D.drawString(getString("waitForStart"), 540 - halfTextLength(graphics2D, getString("waitForStart")), 60);
                                }

                                if(shadowCheckpointSelected != 0 || shadowCheckpointUsed != 0) {
                                    byte j = shadowCheckpointSelected;

                                    if(shadowCheckpointUsed != 0) {
                                        graphics2D.setColor(white160);
                                        j = shadowCheckpointUsed;
                                    } else {
                                        graphics2D.setColor(white100);
                                    }

                                    if(j == 1) {
                                        graphics2D.fillOval(firstCheckpointX - 156, 163, 312, 314);
                                    } else {
                                        graphics2D.fillOval(604, 163, 312, 314);
                                    }
                                }
                            }
                        }
                    }

                    if(night.isRadiationModifier()) {
                        float alpha = Math.max(0F, night.gruggyX) / 2000F;
                        if(night.gruggyX > 1000) {
                            alpha = 1;
                        }

                        if(alpha < 1) {
                            graphics2D.setComposite(AlphaComposite.SrcOver.derive(alpha));
                        }
                        for(GruggyCart cart : night.gruggyCarts) {
                            graphics2D.drawImage(gruggyRing.request(), (int) (offset + cart.getCurrentX() - 102), 140 - waterLevel(), null);
                        }
                        graphics2D.setComposite(AlphaComposite.SrcOver);
                    }
                    
                    
                    if(night.isFogModifier()) {
                        int x = -((fixedUpdatesAnim * 2) % 2160);
                        
                        if(night.isPerfectStorm() || (fan.isEnabled() && fanActive)) {
                            graphics2D.drawImage(lesserFog.request(), x, 0, null);
                            graphics2D.drawImage(lesserFog.request(), x + 2160, 0, null);
                        } else {
                            graphics2D.drawImage(fog.request(), x, 0, null);
                            graphics2D.drawImage(fog.request(), x + 2160, 0, null);
                        }
                    }
                    
                    if(night.getEvent() == GameEvent.MR_MAZE) {
                        MrMaze mrMaze = night.getMrMaze();

                        if(mrMaze.distance > 0) {
                            float invert = 1 / mrMaze.distance;
                            float counter = fixedUpdatesAnim / 40F;
                            Point point = new Point((int) (100 * Math.sin(counter)), (int) (100 * Math.sin(counter) * Math.cos(counter)));

                            int size = (int) (400 * invert);
                            Rectangle rect = new Rectangle(offset + 740 + (int) (point.x * invert) - size / 2, 320 + (int) (point.y * invert) - size / 2, size, size);

                            graphics2D.drawImage(this.mrMaze.request(), rect.x, rect.y, rect.width, rect.height, null);
                            graphics2D.setColor(new Color(0, 0, 0, (int) Math.max(0, Math.min(255, Math.pow(mrMaze.distance / 25F, 0.2) * 250))));
                            graphics2D.fillRect(rect.x, rect.y, rect.width, rect.height);
                        }

                        float mazeAnim = mrMaze.mazeAnim;

                        BufferedImage mazeImage = mrMaze.mazeImage;
                        double radians = fixedUpdatesAnim / 300F * 0.01745329;

                        int imageScale = 4 + (8 / mrMaze.cellSize);
                        
                        double imagePointX = mrMaze.playerX;
                        double imagePointY = mrMaze.playerY;
                        double[] rotated = simulatePixelRotation(imagePointX, imagePointY, mazeImage.getWidth(), mazeImage.getHeight(), radians);

                        mazeImage = rotateRadians(mazeImage, radians, false);
                        
                        graphics2D.setComposite(AlphaComposite.SrcOver.derive(mazeAnim / 2F));
                        graphics2D.drawImage(mazeImage, 540 - (int) (rotated[0] * imageScale), 320 - (int) (rotated[1] * imageScale), 
                                mazeImage.getWidth() * imageScale, mazeImage.getHeight() * imageScale, null);
                        
                        graphics2D.setColor(Color.WHITE);
                        graphics2D.fillOval(540 - (int) (mrMaze.cellSize * 1.5F), 320 - (int) (mrMaze.cellSize * 1.5F), mrMaze.cellSize * 3, mrMaze.cellSize * 3);

                        float sigma = (1 - mazeAnim);
                        graphics2D.setStroke(new BasicStroke(sigma * 5));
                        float beta = (1 - mazeAnim) * 6;
                        graphics2D.drawOval(539 - (int) (mrMaze.cellSize * 1.5F * beta), 319 - (int) (mrMaze.cellSize * 1.5F * beta), (int) (mrMaze.cellSize * 3 * beta), (int) (mrMaze.cellSize * 3 * beta));
                        
                        
                        int fogX = -((fixedUpdatesAnim * 2) % 2160);
                        graphics2D.setComposite(AlphaComposite.SrcOver.derive(mrMaze.fogOpacity));
                        graphics2D.drawImage(fog.request(), fogX, 0, null);
                        graphics2D.drawImage(fog.request(), fogX + 2160, 0, null);
                    }
                    
                    
                    if(night.isRainModifier()) {
                        graphics2D.setColor(Color.BLUE);
                        synchronized (night.raindrops) {
                            for (Raindrop raindrop : night.raindrops) {
                                graphics2D.fillRect(offset + raindrop.getX(), raindrop.getY(), 4, 50);
                            }
                        }
                    }
//                    if(night.getBlizzardTime() > 0) {
                    if(!snowflakes.isEmpty()) {
                        graphics2D.setColor(Color.WHITE);

                        int halfMaxOffset = maxOffset / 2;
                        synchronized(snowflakes) {
                            for (Snowflake snowflake : snowflakes) {
                                int y = snowflake.getY();
                                int xPerspectiveless = (int) (snowflake.getX() + 40 * Math.sin(snowflake.getStartingPhase() + fixedUpdatesAnim / 40F));

                                int x = (int) ((offset + halfMaxOffset) / (snowflake.getZ()) - halfMaxOffset) + xPerspectiveless;
                                
                                if (x < -60 || x > 1140 || y < -60)
                                    continue;

                                if (snowflake.getType()) {
                                    int size = (int) ((1 - snowflake.getZ()) * 60);
                                    int halfSize = size / 2;
                                    graphics2D.fillOval(x - halfSize, snowflake.getY() - halfSize, size, size);
                                } else {
                                    BufferedImage img = snowflake.getSource();
                                    if(snowflake.getRotation() != 0) {
                                        img = rotateRadians(img, snowflake.getRotation(), true);
                                    }
                                    int halfSize = img.getWidth() / 2;

                                    graphics2D.drawImage(img, x - halfSize, y - halfSize, null);
                                }
                            }
                        }
                    }
                    
         
                    if(!bubbles.isEmpty()) {
                        synchronized (bubbles) {
                            for (BubbleParticle bubble : bubbles) {
                                int sine = (int) (20 * Math.sin(bubble.getStartingPhase() + fixedUpdatesAnim / 40F));
                                graphics2D.drawImage(bubble.getSource(), offset + bubble.getX() + sine, bubble.getY(), null);
                            }
                        }
                    }
                    
                    
                    if(night.getType() == GameType.DAY) {
                        if(keyHandler.hoveringNeonSog) {
                            graphics2D.setColor(neonSogSkips < 8 ? Color.WHITE : new Color(210, 210, 210));
                            graphics2D.setFont(comicSans40);

                            String str = neonSogSkips < 8 ? getString("neonSogText") : getString("neonSogFull");
                            
                            int dy = -250;

                            graphics2D.drawString(str, offset + neonSogX + 200 - halfTextLength(graphics2D, str), 390 + dy);
                            graphics2D.drawString("100", offset + neonSogX + 188 - halfTextLength(graphics2D, "100"), 435 + dy);
                            graphics2D.drawImage(dabloon.request(), offset + neonSogX + 200 + halfTextLength(graphics2D, "100"), 400 + dy, 40, 40, null);

                            graphics2D.drawString("(" + neonSogSkips + " / 5)", offset + neonSogX + 200 - halfTextLength(graphics2D, "(" + neonSogSkips + " / 5)"), 350 + dy);
                        }
                    }
                    
    

                    if(sunglassesOn) {
                        Polygon floorGeom = new Polygon(e.getFloorGeometry().xpoints, e.getFloorGeometry().ypoints, e.getFloorGeometry().npoints);
                        floorGeom.translate(offsetX - e.maxOffset(), 0);
                        Polygon ceilGeom = new Polygon(e.getCeilGeometry().xpoints, e.getCeilGeometry().ypoints, e.getCeilGeometry().npoints);
                        ceilGeom.translate(offsetX - e.maxOffset(), 0);
                        
                        double phase = (fixedUpdatesAnim / 320F - Math.floor(fixedUpdatesAnim / 640F) * 0.03F) % 0.2;
                        for(int x = 0; x < 1080; x += 4) {
                            int y1 = getYPointsAtX(floorGeom.xpoints, floorGeom.ypoints, x).get(0).intValue();
                            int y2 = getYPointsAtX(ceilGeom.xpoints, ceilGeom.ypoints, x).get(0).intValue();

                            graphics2D.setColor(Color.GREEN);
                            for (double i = 0.2; i <= 1; i += 0.2) {
                                int y = (int) lerp(y1, y2, i - phase);

                                boolean draw = true;
                                for (Polygon polygon : sgPolygons) {
                                    if(polygon.getBounds().x > x + 4 - offset)
                                        continue;
                                    
                                    if (polygon.contains(x - offset, y)) {
                                        draw = false;
                                        break;
                                    }
                                }
                                if (draw) {
                                    graphics2D.fillRect(x, y, 4, 2);
                                }
                            }
                            graphics2D.setColor(new Color(0, 255, 0, 128));
                            graphics2D.fillRect(x, y1, 4, 2);
                            graphics2D.fillRect(x, y2, 4, 2);
                        }
                        
                        if(sgRadius < 630) {
                            graphics2D.setColor(new Color(120, 60, 30, Math.max(0, 130 - (int) (sgRadius / 4.5F))));
                            graphics2D.setStroke(new BasicStroke(20 + (float) (sgRadius / 5)));
                            graphics2D.drawOval(540 - sgRadius, 320 - sgRadius, sgRadius * 2, sgRadius * 2);
                            graphics2D.setStroke(new BasicStroke());
                        }
                        
                        int buttonXOffset = 0;
                        int buttonYOffset = 0;
                        if(night.getElAstarta().isActive()) {
                            if(night.getElAstarta().getShake() > 8) {
                                buttonXOffset -= (int) (Math.cos(fixedUpdatesAnim * 0.05) * 2 * (night.getElAstarta().getShake() - 8));
                                buttonYOffset -= (int) (Math.sin(fixedUpdatesAnim * 0.05) * (night.getElAstarta().getShake() - 8));
                            }
                        }
                        for(Door door : night.getDoors().values().stream().toList()) {
                            BufferedImage button = doorButton[door.isClosed() ? 1 : 0].request();
                            if(night.isTimerModifier()) {
                                button = timerDoorButton.request();
                            }

                            float size = door.getVisualSize();
                            int xAdd = (int) Math.round(Math.random() * 4) - 2;
                            int yAdd = (int) Math.round(Math.random() * 4) - 2;
                            if(size != 1F) {
                                button = resize(button, (int) (51 * size), (int) (51 * size), Image.SCALE_SMOOTH);
                                xAdd += 25 - (int) (25.5 * size);
                                yAdd += 25 - (int) (25.5 * size);
                            }

                            graphics2D.drawImage(button, offset + door.getButtonLocation().x + buttonXOffset + xAdd, door.getButtonLocation().y + buttonYOffset + yAdd, null);
                        }
                        graphics2D.setColor(new Color(120, 110, 40));
                        graphics2D.fillRect(offset + e.boop.x, e.boop.y, e.boop.width, e.boop.height);
                        
                        
                        //NOW THE REAL OVERLAY
                        graphics2D.drawImage(sunglassesOverlay.request(), 0, 0, null);
                        graphics2D.setFont(sansSerifPlain40);
                        graphics2D.setColor(Color.WHITE);
                        
                        int seconds = night.seconds - night.secondsAtStart;
                        int minutes = 0;
                        
                        while (seconds >= 60) {
                            seconds -= 60;
                            minutes++;
                        }
                        String entireTime = "00:" + (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
                        if(type == GameType.HYDROPHOBIA)
                            entireTime = getString("blueHour");
                        graphics2D.drawString(entireTime, 540 - halfTextLength(graphics2D, entireTime), 525);
                        
                        String str = "α: " + sgAlpha + " | " + "γ: " + BigDecimal.valueOf(sgGamma).setScale(4, RoundingMode.HALF_UP).doubleValue();

                        graphics2D.drawString(str, 540 - halfTextLength(graphics2D, str), 180);
                    }
                    
                    
                    if(inLocker) {
                        graphics2D.drawImage(lockerInsideImg.request(), 0, 0, null);
                        
                        if(type == GameType.HYDROPHOBIA) {
                            HChamber env = (HChamber) night.env();
                            
                            if (env.lockerGuidelineAlpha < 300 && env.lockerGuidelineAlpha > 0) {
                                graphics2D.setFont(comicSans40);
                                int h = (int) (env.lockerGuidelineAlpha / 300d * 255d);
                                graphics2D.setColor(new Color(255, 255, 255, h));
                                
                                String line = getString("hcLockerGuideline");
                                graphics2D.drawString(line, 540 - halfTextLength(graphics2D, line), 560);
                            }
                        }
                    }
                    if(starlightMillis > 0) {
                        graphics2D.drawImage(realVignetteStarlight, 0, 0, null);
                    }
                    if(night.isRadiationModifier()) {
                        if(night.getRadiation() > 45) {
                            graphics2D.setComposite(AlphaComposite.SrcOver.derive(Math.min(1, (night.getRadiation() - 45) / 55)));
                            graphics2D.drawImage(radiationVignette.request(), 0, 0, null);
                            graphics2D.setComposite(AlphaComposite.SrcOver);
                        }
                    }
                    if(night.getShadowblocker().state >= 1) {
                        if(night.getShadowblocker().state < 4) {
                            if (keyHandler.hoveringShadowblockerButton) {
                                graphics2D.drawImage(shadowblockerButton[1].request(), 15, 15, null);
                            }
                            graphics2D.drawImage(shadowblockerButton[0].request(), 18, 18, null);
                        }
                        
                        
                        if(night.getShadowblocker().state >= 2 && night.getShadowblocker().progress > 1) {
                            if(night.getShadowblocker().progress < 2) {
                                graphics2D.setComposite(AlphaComposite.SrcOver.derive(Math.max(0, night.getShadowblocker().progress - 1)));
                            }
                            
                            Color purple = new Color(140, 0, 255);
                            Color darkPurple = new Color(60, 25, 105);

                            int enemiesSize = CustomNight.getEnemies().size();
                            for (int i = 0; i < 18; i++) {
                                int x = 105 * (i % 6);
                                int y = 130 * (i / 6);

                                if (i >= enemiesSize) {
                                    graphics2D.setColor(Color.BLACK);
                                    graphics2D.fillRect(230 + x, 130 + y, 95, 120);

                                    graphics2D.setColor(darkPurple);
                                    graphics2D.setStroke(new BasicStroke(5));
                                    graphics2D.drawRect(232 + x, 132 + y, 91, 116);

                                    graphics2D.setFont(comicSans30);
                                    graphics2D.drawString("X", 237 + x, 242 + y);
                                } else {
                                    CustomNightEnemy enemy = CustomNight.getEnemies().get(i);

                                    Enemy en = night.getEnemies()[enemy.getId()];
                                    enemy.setAI(en.getAILevel());

                                    switch (enemy.getId()) {
                                        case 0 -> enemy.setAI(night.getPepito().pepitoAI);
                                        case 1 -> enemy.setAI(night.getPepito().notPepitoAI);
                                    }

                                    boolean enabled = enemy.getAI() > 0;

                                    BufferedImage icon = grayscale(enemy.getIcon().request());
                                    if (enabled) {
                                        graphics2D.drawImage(advancedPurplify(icon), 230 + x, 130 + y, null);

//                                        graphics2D.setStroke(new BasicStroke(3));
//                                        graphics2D.setColor(Color.WHITE);
//                                        graphics2D.drawRect(228 + x, 98 + y, 99, 124);
                                    } else {
                                        graphics2D.drawImage(icon, 230 + x, 130 + y, null);
                                    }

                                    graphics2D.setStroke(new BasicStroke(2));
                                    graphics2D.setColor(Color.BLACK);
                                    graphics2D.drawRect(235 + x, 135 + y, 85, 110);

                                    graphics2D.setColor(enabled ? purple : Color.GRAY);
                                    graphics2D.setStroke(new BasicStroke(5));
                                    graphics2D.drawRect(232 + x, 132 + y, 91, 116);

                                    graphics2D.setFont(comicSans30);
                                    graphics2D.drawString("" + enemy.getAI(), 237 + x, 242 + y);

                                    if (i == night.getShadowblocker().selected && night.getShadowblocker().state < 4) {
                                        if(enabled) {
                                            graphics2D.setFont(comicSans60);
                                            graphics2D.drawString(getString(enemy.getName()), 540 - halfTextLength(graphics2D, getString(enemy.getName())), 70);
                                        }
                                        
                                        graphics2D.setColor(enabled ? white100 : black120);
                                        graphics2D.fillRect(230 + x, 130 + y, 96, 121);
                                    }
                                }
                            }
                        }
                        graphics2D.setComposite(AlphaComposite.SrcOver);
                        
                        if(night.getShadowblocker().state == 3) {
                            graphics2D.setColor(new Color(140, 0, 255));
                            graphics2D.setFont(comicSans60);
                            graphics2D.drawString(night.getShadowblocker().slopName, 540 - halfTextLength(graphics2D, night.getShadowblocker().slopName), 70);
                            
                            int x1 = night.getShadowblocker().slopInt[0];
                            int y1 = night.getShadowblocker().slopInt[1];

                            float zoomBefore = Math.max(0, Math.min(1, night.getShadowblocker().progress - 0.5F));

                            float zoom = (float) (1 / (1 + Math.pow(Math.E, -(zoomBefore * 2 - 1) * 6)));
                            
                            int width = (int) (96 + 192 * (1 - zoom));
                            int height = (int) (121 + 242 * (1 - zoom));
                            
                            int x2 = 540 - width / 2;
                            int y2 = 320 - height / 2;
                            int x = (int) (zoom * x1 + (1 - zoom) * x2);
                            int y = (int) (zoom * y1 + (1 - zoom) * y2);
                            
                            graphics2D.drawImage(night.getShadowblocker().slop, x, y, width, height, null);
                        }
                        if(night.getShadowblocker().state == 4 || night.getShadowblocker().state == 5) {
                            int halfMaxOffset = maxOffset / 2;
                            synchronized(night.getShadowblocker().particles) {
                                for (ShadowParticle particle : night.getShadowblocker().particles) {
                                    int x = (int) ((offset + halfMaxOffset) * particle.z - halfMaxOffset) + (int) (particle.x);
                                    if(x < -4 || x > 1080)
                                        continue;
                                    graphics2D.setColor(new Color(200, 0, 255, Math.max(0, (int) particle.alpha)));
                                    graphics2D.fillRect(x, (int) particle.y, 4, 4);
                                }
                            }
                        }
                    }

                    
                    float uiAlpha = Math.min(1, Math.max(0F, night.startUIFade) / 400F);
                    if(neonSogAnim >= 1) {
                        uiAlpha = 0;
                    }
                    if(inCam) {
                        uiAlpha = 1;
                    }
                    if(uiAlpha < 1) {
                        graphics2D.setComposite(AlphaComposite.SrcOver.derive(uiAlpha));
                    }
                    

                    if (headphones) {
                        graphics2D.drawImage(headphonesImg.request(), 0, 0, null);

                        float l = currentLeftPan / 255F;
                        if (l > 0) {
                            graphics2D.drawImage(alphaify(headphoneLeft.request(), l), 5, 344, null);
                        }
                        
                        float r = currentRightPan / 255F;
                        if (r > 0) {
                            graphics2D.drawImage(alphaify(headphoneRight.request(), r), 1013, 361, null);
                        }
                    }


                    graphics2D.setComposite(AlphaComposite.SrcOver);
                    
                    if(night.getBlizzardFade() > 0) {
                        float alpha = Math.max(0F, Math.min(1F, night.getBlizzardFade() / 255F));
                        graphics2D.drawImage(alphaify(blizzardAnnouncement.request(), alpha), 220, 220, null);
                    }
                    if(night.isPowerModifier()) {
                        if(night.isInGeneratorMinigame()) {
                            graphics2D.drawImage(battery.request(), 220, 490, null);
                            graphics2D.setColor(Color.RED);
                            for(short x : night.generatorXes.clone()) {
                                if(x != -1) {
                                    graphics2D.fillRect(220 + x, 495, 35, 110);
                                }
                            }

                            graphics2D.setColor(new Color(255, 216, 0));

                            int x = (fixedUpdatesAnim * 5 + (int) (Math.sin(fixedUpdatesAnim * 0.05) * 14)) % 630;
                            graphics2D.fillRect(220 + x, 485, 10, 130);

                            graphics2D.drawImage(charge[night.generatorStage >= 1 ? 1 : 0].request(), 442, 420 - ((fixedUpdatesAnim / 40) % 2 == 0 ? 10 : 0), null);
                            graphics2D.drawImage(charge[night.generatorStage >= 2 ? 1 : 0].request(), 519, 420 - ((fixedUpdatesAnim / 40) % 2 == 0 ? 10 : 0), null);
                            graphics2D.drawImage(charge[night.generatorStage >= 3 ? 1 : 0].request(), 596, 420 - ((fixedUpdatesAnim / 40) % 2 == 0 ? 10 : 0), null);

                            graphics2D.drawImage(connectText.request(), 140, 280 - (((int) ((fixedUpdatesAnim / 80F))) % 2 == 0 ? 5 : 0), null);
                        }
                    }
                    
                    if(e instanceof Basement basement) {
                        if(basement.isInGeneratorMinigame()) {
                            int y1 = (((int) ((fixedUpdatesAnim / 60F))) % 2 == 0 ? 5 : 0);
                            int y2 = 0;

                            if(basement.getStage() == 5) {
                                y1 = (((int) ((fixedUpdatesAnim / 20F))) % 2 == 0 ? 15 : 0);
                                y2 = (((int) ((fixedUpdatesAnim / 30F))) % 2 == 0 ? 15 : 0);
                            }
                            
                            graphics2D.drawImage(greenBattery.request(), 220, 490 - y2, null);
                            graphics2D.setColor(Color.GREEN);
                            for(short x : basement.generatorXes.clone()) {
                                if(x != -1) {
                                    graphics2D.fillRect(220 + x, 495 - y2, 35, 110);
                                }
                            }

                            graphics2D.setColor(new Color(255, 216, 0));

                            int x = fixedUpdatesAnim * 5 + (int) (Math.sin(fixedUpdatesAnim * 0.05) * 14);
                            if(basement.getStage() == 5) {
                                x += fixedUpdatesAnim / 2;
                            }
                            x = x % 630;
                            graphics2D.fillRect(220 + x, 485 - y2, 10, 130);
                            
                            graphics2D.drawImage(escToCancel.request(), 288, 320 - y1, null);
                            graphics2D.drawImage(basement.generatorMinigameMonitor, 0, 0, 450, 230, null);
                        }
                        
                        if(basement.getStage() == 7) {
                            int subX = maxOffset - fixedOffsetX;
                            
                            // NORMAL GLOW
//                            graphics2D.drawImage(basementStaticGlow.request().getSubimage(subX, 0, 1080, 640), 0, 0, null);

                            // AMBIENT GLOW FROM HOLE
                            float first = (float) (Math.sin(fixedUpdatesAnim / 40F) / 2 + 0.5);
                            float second = (float) (Math.cos(fixedUpdatesAnim / 70F) / 2 + 0.5);
                            float third = (float) (Math.sin(fixedUpdatesAnim / 110F) / 2 + 0.5);

                            graphics2D.setComposite(AlphaComposite.SrcOver.derive((first * second * third) / 3F + 0.66F));
                            graphics2D.drawImage(basementBeam.request(), offset + 303, 0, null);

                            first -= 0.5F;
                            second -= 0.5F;
                            third -= 0.5F;

                            if(first > 0) {
                                graphics2D.setComposite(AlphaComposite.SrcOver.derive(first));
                                graphics2D.drawImage(basementDisperse[0].getSubimage(subX, 0, 1080, 640), 0, 0, null);
                            }
                            if(second > 0) {
                                graphics2D.setComposite(AlphaComposite.SrcOver.derive(second));
                                graphics2D.drawImage(basementDisperse[1].getSubimage(subX, 0, 1080, 640), 0, 0, null);
                            }
                            if(third > 0) {
                                graphics2D.setComposite(AlphaComposite.SrcOver.derive(third));
                                graphics2D.drawImage(basementDisperse[2].getSubimage(subX, 0, 1080, 640), 0, 0, null);
                            }
                            graphics2D.setComposite(AlphaComposite.SrcOver);
                        }
                    }

                    if(night.isTimerModifier() && timerY > -230) {
                        graphics2D.drawImage(timerBoard.request(), 10, Math.min(10, timerY), null);
                        Color oldColor = graphics2D.getColor();
                        graphics2D.setColor(Color.GREEN);
                        graphics2D.setFont(comicSans40);
                        short order = 0;
                        for (Integer number : night.getDoors().keySet()) {
                            Door door = night.getDoors().get(number);

                            if(night.getTimers().containsKey(door)) {
                                float f = night.getTimers().get(door);
                                
                                graphics2D.drawString(getString("door") + " " + (number + 1) + " - " + (Math.round(Math.max(0, f) * 100F) / 100F), 25, Math.min(10, timerY) + order * 40 + 100);
                                order++;
                            }
                        }
                        graphics2D.setColor(oldColor);
                    }
                    
                    
                    graphics2D.setComposite(AlphaComposite.SrcOver.derive(uiAlpha));
                    
                    
                    
                    if(night.getDsc().isFight()) {
                        if (night.getDsc().showSplash()) {
                            graphics2D.setColor(white200);
                            graphics2D.setFont(comicSansBoldItalic40);
                            int add = (int) (Math.round(Math.random()));
                            graphics2D.drawString(getString("dscSplash1"), 540 - halfTextLength(graphics2D, getString("dscSplash1")), 130 + add);
                            graphics2D.drawString(getString("dscSplash2"), 540 - halfTextLength(graphics2D, getString("dscSplash2")), 170 + add);
                        }

                        if(night.getDsc().isActive()) {
                            if (night.getDsc().getZ() > 1.25 && (int) (fixedUpdatesAnim / 20F) % 2 == 0) {
                                graphics2D.setColor(Color.RED);
                                graphics2D.setFont(comicSans60);
                                graphics2D.drawString(getString("dscWarning1"), 540 - halfTextLength(graphics2D, getString("dscWarning1")), 70);
                                graphics2D.drawString(getString("dscWarning2"), 540 - halfTextLength(graphics2D, getString("dscWarning2")), 630);
                            }
                        }
                    }

                    boolean noSignalFromHeat = night.getNoSignalFromHeat();
                    noSignalFromHeat = noSignalFromHeat || night.getEvent() == GameEvent.BASEMENT_KEY;

                    if (night.getEvent().isGuiEnabled() && !(inCam && noSignalFromHeat)) {
                        if (!inCam) {
                            if (announcerOn) {
                                graphics2D.setComposite(AlphaComposite.SrcOver);
                                float opacity = (float) Math.sin(Math.toRadians(announceCounter));

                                if (opacity <= 0) {
                                    announcerOn = false;
                                    announceCounter = 1;
                                } else {
                                    graphics2D.setFont(yuGothicPlain80);
                                    graphics2D.setColor(new Color(255, 255, 255, Math.round(255 * opacity)));
                                    graphics2D.drawString(nightAnnounceText, 540 - halfTextLength(graphics2D, nightAnnounceText), 330);
                                }
                                if(uiAlpha < 1) {
                                    graphics2D.setComposite(AlphaComposite.SrcOver.derive(uiAlpha));
                                }
                            }
                            if (challengerAlpha > 0) {
                                graphics2D.setComposite(AlphaComposite.SrcOver.derive(Math.min(1F, challengerAlpha / 120F)));
                                graphics2D.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));
                                graphics2D.setColor(challengerColor);

                                graphics2D.drawImage(challenger, 0, 0, null);
                                graphics2D.drawString(challengerString, 540 - halfTextLength(graphics2D, challengerString), 320);

                                graphics2D.setComposite(AlphaComposite.SrcOver.derive(1F));
                            }
                        } else { // if inCam
                            graphics2D.drawImage(camLayer0, 0, 0, null);

                            if (portalTransporting) {
                                if (riftTint > 0) {
                                    graphics2D.setColor(new Color(0, 0, 0, riftTint));
                                    graphics2D.fillRect(0, 0, 1080, 640);
                                }

                                graphics2D.setColor(Color.WHITE);
                                graphics2D.setFont(yuGothicPlain120);
                                graphics2D.drawString(getString("transporting"), 540 - halfTextLength(graphics2D, getString("transporting")), 330);
                            }
                            if (night.getAstarta().arrivalSeconds < 5 && night.getAstarta().arrivalSeconds > 0 && night.getAstarta().isEnabled()) {
                                graphics2D.drawImage(astartaCam[0], 83, 422, null);
                            }
                            if (night.getAstarta().leaveSeconds > 0) {
                                graphics2D.drawImage(astartaCam[1], 106, 308, null);
                            }

                            if (night.getMaki().isActive() && night.getMaki().makiStepsLeft > 0) {
                                if (night.getMaki().getDoor() == 0) {
                                    graphics2D.drawImage(mirror(makiCam, 1), -15, 170, null);
                                } else {
                                    graphics2D.drawImage(makiCam, 530, 60, null);
                                }
                            }

                            if (adblockerStatus == 1) {
                                graphics2D.drawImage(adblockerImage.getScaledInstance(100, 100, Image.SCALE_FAST), adblockerPoint.x, adblockerPoint.y, null);
                            }

                            if (portalActive) {
                                float random = (float) (Math.random() * 0.4F + 0.2F);
                                short randomAddX = (short) (Math.random() * 20);
                                short randomAddY = (short) (Math.random() * 40);
                                graphics2D.drawImage(shadowPortal.getScaledInstance((int) (400 * random), (int) (550 * random), Image.SCALE_FAST), 680 - randomAddX, 90 - randomAddY, 400 + randomAddX, 550 + randomAddY, null);
                            }

                            graphics2D.setStroke(new BasicStroke(5));
                            graphics2D.setColor(Color.WHITE);
                            graphics2D.drawRect(30, 30, 1020, 580);
                            graphics2D.setColor(Color.RED);
                            graphics2D.fillOval(50, 50, 50, 50);

                            if(type != GameType.HYDROPHOBIA) {
                                graphics2D.setColor(new Color(0, 0, 0, 100));
                                graphics2D.fillRect(0, 0, 1080, 640);
                                
                                graphics2D.drawImage(cam1A, 720, 340, null);
                            }
                            graphics2D.drawImage(hyperCam.request(), 130, 50, null);
                            
                            if (adblocker.isEnabled() && !adBlocked) {
                                graphics2D.drawImage(smallAdblockerImage, 50, 50, null);
                            }
                        }

                        graphics2D.setFont(sansSerifPlain40);

                        if (night.hasPower()) {
                            graphics2D.drawImage(usageImage, 0, 528, null);

                            graphics2D.setColor(white160);
                            graphics2D.drawString(getString("battery"), 40, 600);
                            graphics2D.drawString((short) (night.getEnergy() * 0.2) + "%", energyX, 600);

                            if(inCam) {
                                float temprature = Math.round(night.getTemperature() * 2.5 + 200) / 10F;
                                if(night.getType() == GameType.SHADOW) {
                                    temprature = 0F;
                                }
                                graphics2D.drawString("\uD83C\uDF21️" + temprature + "°C", 30, 520);
                            }
                            for(Integer value : batteryRegenIcons.stream().toList()) {
                                value = Math.max(0, value);
                                graphics2D.setComposite(AlphaComposite.SrcOver.derive(uiAlpha * (1 - Math.min(1, (value / 55F)))));
                                graphics2D.drawImage(energyRegenIcon.request(), 10 + energyX + textLength(graphics2D, ((short) (night.getEnergy() * 0.2) + "%")), 572 - value, null);
                                graphics2D.setComposite(AlphaComposite.SrcOver.derive(uiAlpha));
                            }
                        } else if(night.isPowerModifier()) {
                            if(night.getGeneratorEnergy() > 0) {
                                graphics2D.setColor(white160);
                                graphics2D.drawString(getString("generatorFuel"), 40, 600);
                                graphics2D.drawString((short) (night.getGeneratorEnergy() * 0.2) + "%", (short) (50 + textLength(graphics2D, getString("generatorFuel"))), 600);
                            }
                        }
                        if (type.isEndless()) {
                            int yAdd = inCam ? -40 : 0;
                   
                            graphics2D.drawString(endless.getCoins() + "", 75, 522 + yAdd);
                            graphics2D.drawImage(dabloon.request(), 40, 495 + yAdd, null);
                            
                            if(endless.getNight() == 3 && type == GameType.ENDLESS_NIGHT) {
                                boolean startOfNight = night.seconds - night.secondsAtStart < 16;
                                
                                if(startOfNight) {
                                    float j = (float) Math.abs(Math.cos(fixedUpdatesAnim / 80F));
                                    BufferedImage sign = alphaify(riftIndicator.request(), (float) Math.abs(Math.sin(fixedUpdatesAnim / 40F)) * j / 1.5F);

                                    graphics2D.drawImage(sign, 45 - (int) (30 * j), 45 - (int) (28 * j), (int) (60 * j), (int) (56 * j), null);
                                }
                                if(startOfNight) {
                                    float j = (float) Math.abs(Math.cos(fixedUpdatesAnim / 160F));
                                    BufferedImage sign = alphaify(riftIndicator.request(), (float) Math.abs(Math.sin(fixedUpdatesAnim / 80F)) * j / 4F);

                                    graphics2D.drawImage(sign, 45 - (int) (60 * j), 45 - (int) (56 * j), (int) (120 * j), (int) (112 * j), null);
                                }
                                
                                String riftStuff = getString("riftEnabled");
                                graphics2D.setFont(comicSans40);
                                int length = textLength(graphics2D, riftStuff);
                                
                                if(riftIndicatorX > -length) {
                                    graphics2D.setColor(new Color(128, 0, 255));

                                    graphics2D.drawString(riftStuff, riftIndicatorX, 45);
                                    graphics2D.drawString(riftStuff, 1080 - riftIndicatorX - length, 635);
                                }
                            }
                        }
                        if (night.getType().isBasement()) {
                            int yAdd = inCam ? -40 : 0;
   
                            graphics2D.drawString(((Basement) e).getCoins() + "", 75, 522 + yAdd);
                            graphics2D.drawImage(evilDabloon.request(), 40, 495 + yAdd, null);
                        }

                        graphics2D.setFont(sansSerifPlain70);
                        
                        if(type != GameType.HYDROPHOBIA) {
                            if(night.isClockBroken()) {
                                synchronized (night.glassParticles) {
                                    for (GlassParticle particle : night.glassParticles.stream().toList()) {
                                        graphics2D.fillRect((int) particle.x, (int) particle.y, 2, 2);
                                    }
                                }
                            } else {
                                if (inCam) {
                                    graphics2D.drawString(night.getClockString(), 1045 - textLength(graphics2D, night.getClockString()), 90);
                                } else {
                                    graphics2D.drawString(night.getClockString(), 1070 - textLength(graphics2D, night.getClockString()), 70);
                                }
                            }
                        }
                    } else if(inCam) {
                        graphics2D.drawImage(noSignal, 375, 300, null);
                    }

                    if (inCam) {
                        if (night.getEvent() == GameEvent.MAXWELL) {
                            if (maxwellActive) {
                                graphics2D.setColor(new Color(180, 250, 90));
                            } else {
                                graphics2D.setColor(new Color(64, 63, 62));
                            }
                            graphics2D.fillRect(690, 50, 340, 110);

                            graphics2D.setStroke(new BasicStroke(5));
                            graphics2D.setColor(Color.WHITE);
                            graphics2D.drawRect(690, 50, 340, 110);

                            graphics2D.setStroke(new BasicStroke());
                            graphics2D.setFont(sansSerifPlain40);
                            graphics2D.drawString(getString("maxwellTheCat"), 700, 90);
                            graphics2D.drawString(getString("button"), 700, 140);
                        }
                        if (portalActive) {
                            if (portalTransporting) {
                                graphics2D.setColor(new Color(180, 250, 90));
                            } else {
                                graphics2D.setColor(new Color(64, 63, 62));
                            }
                            graphics2D.fillRect(690, 50, 340, 110);

                            graphics2D.setStroke(new BasicStroke(5));
                            graphics2D.setColor(Color.WHITE);
                            graphics2D.drawRect(690, 50, 340, 110);

                            graphics2D.setStroke(new BasicStroke());
                            graphics2D.setFont(sansSerifPlain40);
                            graphics2D.drawString(getString("transportInto"), 700, 90);
                            graphics2D.drawString(getString("shadownight"), 700, 140);
                        }
                    } else { // if not in cam
                        if (night.getEvent() == GameEvent.FLOOD) {
                            graphics2D.setColor(white200);
                            graphics2D.setFont(comicSansBoldItalic40);
                            int add = (int) (Math.round(Math.random()));
                            graphics2D.drawString(getString("floodedAgain"), floodTextLength1, 130 + add);
                            graphics2D.drawString(getString("bewareOfSharks"), floodTextLength2, 170 + add);

                            if (night.getShark().isActive()) {
                                graphics2D.setColor(Color.GREEN);
                                graphics2D.setStroke(new BasicStroke(6));
                                graphics2D.drawRect(offset + night.getShark().getX(), 480, 234, 154);
//                                graphics2D.setFont(comicSansBoldItalic40);
//                                graphics2D.drawString(getString("moveThe"), offset + night.getShark().getX() + 10, 520);
//                                graphics2D.drawString(getString("fishAway"), offset + night.getShark().getX() + 10, 620);
                                graphics2D.setFont(comicSans80);
                                graphics2D.drawString("" + night.getShark().getLeftBeforeBite(), offset + night.getShark().getX() + 100, 590);
                                graphics2D.setStroke(new BasicStroke());

                                if((-fixedOffsetX + 880) <= (night.getShark().getX() + 240) && night.getShark().getX() <= (-fixedOffsetX + 980)) {
                                    if ((fixedUpdatesAnim / 12) % 2 == 0) {
                                        graphics2D.drawImage(msiArrow.request(), fixedOffsetX + night.getShark().getX() - 460, 510, 40, 40, null);
                                        graphics2D.drawImage(msiArrow.request(), fixedOffsetX + night.getShark().getX() - 460, 570, 40, 40, null);
                                        graphics2D.drawImage(mirror(msiArrow.request(), 1), fixedOffsetX + night.getShark().getX() - 146, 510, 40, 40, null);
                                        graphics2D.drawImage(mirror(msiArrow.request(), 1), fixedOffsetX + night.getShark().getX() - 146, 570, 40, 40, null);
                                    }
                                }

                                if (night.getShark().biting) {
                                    graphics2D.drawImage(sharkImg.request(), fixedOffsetX + night.getShark().getX() - 440, night.getShark().biteAnimation, null);
                                }
                            }
                        } else if (night.getEvent() == GameEvent.LEMONADE) {
                            graphics2D.setColor(white200);
                            graphics2D.setFont(comicSansBoldItalic40);
                            int add = (int) (Math.round(Math.random()));
                            graphics2D.drawString(getString("lemonadeCatAppears"), 220, 130 + add);
                            graphics2D.drawString(getString("giveHimLemons"), 195, 170 + add);

                            graphics2D.drawString(getString("tries") + (3 - night.getLemonadeCat().getCurrentTry()), 30, 600);
                            
                            
                            // NINE
                            LemonadeCat lemonadeCat = night.getLemonadeCat();
                            if(lemonadeCat.isNineBossfight()) {
                                synchronized (nuclearLemonadeFog) {
                                    for (NuclearLemonadeFog fog : nuclearLemonadeFog) {
                                        graphics2D.drawImage(lemonadeFog.request(), offset + fog.x, fog.y, null);
                                    }
                                }
                            }
                            if(lemonadeCat.nuclearOxygen) {
                                float amplitude = (float) (1 + (100 - Math.pow(lemonadeCat.oxygenLevel / 100F, 2) * 100F) / 10F);
                                int xShake = (int) ((Math.random() * 6 - 3) * amplitude);
                                int yShake = (int) ((Math.random() * 6 - 3) * amplitude);
                                
                                graphics2D.drawImage(lemonadeOxygenOverlay.request(), 110 - 100 + xShake, 440 - 100 + yShake, null);
                                float progress = lemonadeCat.oxygenLevel / 100F;

                                Polygon oxygenPoly = createPizzaSlicePolygon(110 + xShake, 440 + yShake, 100, progress);
                                graphics2D.setClip(polygonToShape(oxygenPoly));
                                graphics2D.drawImage(lemonadeOxygenBar.request(), 110 - 88 + xShake, 440 - 88 + yShake, null);
                                graphics2D.setClip(0, 0, 1080, 640);
                            }
                            
                            if(lemonadeCat.isNineBossfight()) {
                                graphics2D.setColor(Color.BLACK);
                                graphics2D.fillRect(10, 10, 1060, 40);
                                graphics2D.setColor(new Color(114, 93, 11));
                                float percent = (lemonadeCat.getHealth() / 22F);
                                graphics2D.fillRect(15, 15, (int) (1050 * percent), 30);

                                graphics2D.setFont(comicSans40);
                                graphics2D.setColor(Color.BLACK);
                                graphics2D.drawString(getString("lemonadeCatCn"), 395, 45);
                            }
                            
                        }
                        if(type == GameType.DAY) {
                            switch (endless.getNight()) {
                                case 3 -> {
                                    graphics2D.setColor(white200);
                                    graphics2D.setFont(comicSansBoldItalic40);
                                    int add = (int) (Math.round(Math.random()));
                                    graphics2D.drawString(getString("millyParty"), 540 - halfTextLength(graphics2D, getString("millyParty")), 170 + add);
                                }
                                case 4 -> {
                                    graphics2D.setColor(white200);
                                    graphics2D.setFont(comicSansBoldItalic40);
                                    int add = (int) (Math.round(Math.random()));
                                    graphics2D.drawString(getString("somethingChangedInCamera"), 540 - halfTextLength(graphics2D, getString("somethingChangedInCamera")), 170 + add);
                                }
                                case 6 -> {
                                    graphics2D.setColor(white200);
                                    graphics2D.setFont(comicSansBoldItalic40);
                                    int add = (int) (Math.round(Math.random()));
                                    graphics2D.drawString(getString("larrySweepAnnouncement"), 540 - halfTextLength(graphics2D, getString("larrySweepAnnouncement")), 170 + add);
                                }
                            }
                        }

                        graphics2D.setFont(comicSans30);

                        if(manualY < 640) {
                            graphics2D.setColor(Color.WHITE);
                            graphics2D.setStroke(new BasicStroke(3));
                            if(manualFirstButtonHover) {
                                graphics2D.drawRoundRect(825, manualY + 55, 91, 30, 30, 30);
                            }
                            if(manualSecondButtonHover) {
                                graphics2D.drawRoundRect(925, manualY + 55, 91, 30, 30, 30);
                            }
                            graphics2D.setStroke(new BasicStroke());

                            if (manualY < 535) {
                                if(night.isRadiationModifier()) {
                                    graphics2D.setColor(Color.BLACK);
                                    graphics2D.fillRect(620, manualY + 105, 420, 200);

                                    graphics2D.setColor(Color.GREEN);
                                    graphics2D.setFont(sansSerifPlain70);
                                    float rad = (Math.round(Math.max(0, night.getRadiation()) * 100F) / 100F);
                                    graphics2D.drawString(rad + "%", 830 - halfTextLength(graphics2D, rad + "%"), manualY + 250);

                                    graphics2D.setFont(comicSans30);
                                } else {
                                    if (manualText.isEmpty()) {
                                        graphics2D.drawImage(manualMissingTextImg, 620, manualY + 105, null);
                                    } else {
                                        graphics2D.setColor(Color.BLACK);
                                        graphics2D.fillRect(620, manualY + 105, 420, 200);

                                        graphics2D.setColor(Color.GREEN);

                                        for (int i = 0; i < manualText.size(); i++) {
                                            graphics2D.drawString(manualText.get(i), 630, manualY + 130 + i * 28);
                                        }
                                    }
                                }
                            }
                            if(night.isRadiationModifier()) {
                                graphics2D.drawImage(geigerCounter.request(), 600, manualY, null);
                            } else {
                                graphics2D.drawImage(manualImg, 600, manualY, null);
                            }

                            graphics2D.setColor(Color.WHITE);
                            graphics2D.drawString(manualY < 535 ? getString("hide") : getString("show"), 620 + 215, manualY + 73);
                            graphics2D.drawString(getString("close"), 620 + 315, manualY + 73);
                        }
                    }
                    
                    if(night.getShock().isDoom()) {
                        float sin = (float) (Math.sin(fixedUpdatesAnim / 8F) / 2 + 0.5);
                        float cos = (float) (Math.cos(fixedUpdatesAnim / 8F) / 2 + 0.5);
                        
                        BufferedImage image = resize(shockCatIncoming.request(), (int) (250 + 250 * sin), (int) (33 + 33 * cos), BufferedImage.SCALE_FAST);
                        graphics2D.drawImage(image, 540 - image.getWidth() / 2, 160 - image.getHeight() / 2, null);
                        
                        Door door = night.getDoors().get(night.getShock().getDoor());
                        Polygon hitbox = new Polygon(door.getHitbox().xpoints, door.getHitbox().ypoints, door.getHitbox().npoints);
                        hitbox.translate(offset, 0);

                        float sin2 = (float) (Math.sin(fixedUpdatesAnim / 6F) / 2 + 0.5);
                        
                        graphics2D.setColor(new Color(255, 0, 0, (int) (100 * sin2)));
                        graphics2D.fillPolygon(hitbox);
                    }

                    if (night.getBoykisser().isActive()) {
                        graphics2D.drawImage(boykisserImg.request(), 0, 0, null);
                    }

                    if(night.getType() == GameType.SHADOW && !night.getGlitcher().getShadowGlitches().isEmpty()) {
                        for(Point point : night.getGlitcher().getShadowGlitches()) {
                            graphics2D.drawImage(shadowGlitch.request(), offset + point.x, point.y, 330, 120, null);
                        }
                    }
                    
                    if(birthdayMaxwell.isEnabled()) {
                        if(night.getSeconds() - night.secondsAtStart < 215) {
                            if(night.getEvent().isInGame()) {
                                graphics2D.setColor(white200);
                                graphics2D.setFont(comicSans60);
                                int add = (int) (Math.round(Math.random()));

                                graphics2D.drawString(getString("surviveUntil4AM"), goalTextLength1, 240 + add);
                            }
                        } else {
                            night.setEvent(GameEvent.MAXWELL);
                            music.stop();
                            birthdayMaxwell.disable();

                            night.getA90().setAILevel(0);
                            night.getBoykisser().setAILevel(0);
                            night.getShark().setAILevel(0);
                        }
                    }

                    if (night.getEvent() == GameEvent.ASTARTA) {
                        AstartaBoss ab = night.getAstartaBoss();
                        if(ab.isFighting()) {
                            graphics2D.setColor(Color.BLACK);
                            graphics2D.fillRect(10, 10, 1060, 40);
                            graphics2D.setColor(new Color(128, 0, 255));
                            float percent = (ab.getHealth() / 200F);
                            graphics2D.fillRect(15 + 1050 - (int) (1050 * percent), 15, (int) (1050 * percent), 30);

                            graphics2D.setColor(new Color(70, 20, 190));
                            float darkPercent = (ab.getHealthMargin() / 200F);
                            graphics2D.fillRect(16 + 1050 - (int) (1050 * (percent + darkPercent)), 15, (int) (1050 * darkPercent), 30);

                            graphics2D.setFont(comicSans40);
                            graphics2D.setColor(Color.BLACK);
                            graphics2D.drawString(getString("shadowAstarta"), 395, 45);
                        }

                        graphics2D.setColor(new Color(140, 40, 255));
                        graphics2D.setStroke(new BasicStroke(6));

                        if(ab.getDyingStage() >= 3) {
                            if(ab.getDyingStage() == 4) {
                                graphics2D.drawImage(astartaVignette1.request(), 0, 0, null);
                            } else if(ab.getDyingStage() == 5) {
                                graphics2D.drawImage(astartaVignette2.request(), 0, 0, null);
                            }

                            switch (ab.getDeathShape()) {
                                case STAR -> ab.drawStar(graphics2D, fixedUpdatesAnim);
                                case CUBE -> ab.drawCube(graphics2D, fixedUpdatesAnim);
                                case PLANET -> ab.drawPlanet(graphics2D, fixedUpdatesAnim);
                                case INTRO -> ab.drawIntro(graphics2D, fixedUpdatesAnim);
                                case EYE -> ab.drawEye(graphics2D, fixedUpdatesAnim);
                                case ROTATE -> ab.drawRotate(graphics2D, fixedUpdatesAnim);
                                case ATOM -> ab.drawAtom(graphics2D, fixedUpdatesAnim);
                            }
                        }
                        graphics2D.setStroke(new BasicStroke(1));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                
                graphics2D.setComposite(AlphaComposite.SrcOver);

                
                if(soggyPen.isEnabled()) {
                    if(keyHandler.holdingShift) {
                        graphics2D.drawImage(soggyPenImg.request(), rescaledPoint.x - 11, rescaledPoint.y - 119, null);
                    }
                }
                

                if(keyHandler.holdingTab) {
                    graphics2D.setColor(black80);
                    graphics2D.fillRect(0, 0, 1080, 640);

                    graphics2D.setColor(Color.WHITE);
                    graphics2D.setFont(comicSans60);
                    graphics2D.drawString(getString("keybinds"), 540 - halfTextLength(graphics2D, getString("keybinds")), 70);

                    graphics2D.setColor(white200);
                    graphics2D.setFont(comicSans40);

                    short order = 0;
                    for(Item item : usedItems) {
                        if(item.getKeybind().isEmpty())
                            continue;

                        String str = "[" + item.getKeybind() + "] - " + item.getName();
                        graphics2D.drawString(str, 540 - halfTextLength(graphics2D, str), 140 + order);
                        order += 45;
                    }
                }
                
                
                if(night.getGlitcher().visualFormTicks > 0) {
                    int off = (fixedUpdatesAnim * 16) % 246;

                    BufferedImage toLimit = glitcherUnit.request();
                    if(night.getType() == GameType.SHADOW) {
                        toLimit = purplify(grayscale(toLimit));
                    }
                    graphics2D.setComposite(new LimitComposite((int) (night.getGlitcher().visualFormTicks / 25F * 893F)));
                    for(int x = 0; x < 6; x++) {
                        for(int y = 0; y < 4; y++) {
                            graphics2D.drawImage(toLimit, x * 246 - off, y * 246 - off, null);
                        }
                    }
                    graphics2D.setComposite(AlphaComposite.SrcOver);
                }
                
                
                if(night.getType() == GameType.HYDROPHOBIA) {
                    if(!inCam) {
                        graphics2D.drawImage(hcNoiseImg[hcNoise], 0, 0, null);
                    }
                }
            }
            case RIFT -> {
                drawRift(graphics2D);

                if(riftTint > 0) {
                    graphics2D.setColor(new Color(0, 0, 0, riftTint));
                    graphics2D.fillRect(0, 0, 1080, 640);
                }
            }
            case CREDITS -> graphics2D.drawImage(creditsdotpng.request(), 0, 0, null);
            case SOUNDTEST -> {
                if(keyHandler.soundTest.getCode().equals("literally_22_miliseconds_of_nothing")) {
                    BufferedImage pepitoSpaghetti = loadImg("/game/items/pepito_spaghetti.jpg");

                    for(byte x = 0; x < 12; x++) {
                        for(byte y = 0; y < 7; y++) {
                            graphics2D.drawImage(pepitoSpaghetti, x * 100, y * 100, null);
                        }
                    }
                }
                graphics2D.setFont(comicSans60);
                graphics2D.setColor(Color.WHITE);
                graphics2D.drawString(keyHandler.soundTest.getCode(), 20, 70);

//                graphics2D.drawString(".wav: " + keyHandler.soundTest.valueWav, 20, 200);
                graphics2D.drawString(".mp3: " + keyHandler.soundTest.valueMP3, 20, 300);

                graphics2D.setFont(comicSans40);
                graphics2D.drawString("current code: " + keyHandler.soundTest.currentCode, 20, 620);
            }
            case MILLY -> {
                if(night.getType().isEndless()) {
                    if (endless.getNight() == 3) {
                        graphics2D.drawImage(millyShopColorsChanging, 0, 0, null);
                    }
                }
            }
            case DRY_CAT_GAME -> {
                int amplitude = 120;
                BufferedImage dryCatImage = dryCatImg[dryCatGame.isCrazy() ? 1 : 0].request();
                BufferedImage soggyCat = soggyCatImg[dryCatGame.isCrazy() ? 1 : 0].request();
                graphics2D.setColor(Color.WHITE);
                
                if(dryCatGame.isCrazy()) {
                    amplitude = 0;
                    if(dryCatGame.timer < 20) {
                        graphics2D.setColor(Color.RED);
                    }
                }
                
                graphics2D.setFont(comicSans80);
                graphics2D.drawString(getString("intermission"), 540 - halfTextLength(graphics2D, getString("intermission")), 360);
                
                graphics2D.setFont(comicSans40);
                graphics2D.drawString(getString("soggyTime"), 10, 50);
                
                
                
                String seconds = (Math.round(Math.max(0, dryCatGame.timer) * 10) / 10) + getString("s");
                graphics2D.drawString(seconds, 1070 - textLength(graphics2D, seconds), 50);

                synchronized (dryCatGame.getCats()) {
                    for (DryCat dryCat : dryCatGame.getCats()) {
                        if (dryCat == null)
                            continue;

                        double sine = 0;
                        if (amplitude != 0) {
                            sine = Math.sin(dryCat.getFunction()) * amplitude;
                        }

                        if (dryCat.isDead()) {
                            if (150 * dryCat.getSize() > 1) {
                                graphics2D.drawImage(soggyCat, (int) (dryCat.getX() + 75 - 75 * dryCat.getSize()), (int) (dryCat.getAnchorY() + sine + 75 - 75 * dryCat.getSize()), (int) (150 * dryCat.getSize()), (int) (150 * dryCat.getSize()), null);
                            } else {
                                float size = 1 + Math.abs(dryCat.getSize());

                                graphics2D.setComposite(AlphaComposite.SrcOver.derive(Math.max(0, 1 - Math.abs(dryCat.getSize()))));
                                graphics2D.drawImage(dryCatExplodeImg.request(), (int) (dryCat.getX() + 75 - 75 * size), (int) (dryCat.getAnchorY() + sine + 75 - 75 * size), (int) (150 * size), (int) (150 * size), null);
                                graphics2D.setComposite(AlphaComposite.SrcOver);
                            }
                        } else {
                            graphics2D.drawImage(dryCatImage, (int) (dryCat.getX()), (int) (dryCat.getAnchorY() + sine), null);
                        }
                    }
                    if (dryCatGame.hasSpawnedDoor() && !dryCatGame.isDoorOpen()) {
                        for (DryCat dryCat : dryCatGame.getCats()) {
                            if (dryCat.isDoor()) {
                                graphics2D.drawImage(dryCatDoorImg.request(), (int) (dryCat.getX()), dryCat.getAnchorY(), null);
                            }
                        }
                    }
                }

                
                if(dryCatGame.isDoorOpen()) {
                    graphics2D.drawString(getString("dryCatPressE"), 540 - halfTextLength(graphics2D, getString("dryCatPressE")), 625);
                }
                
                synchronized (dryCatGame.particles) {
                    for (WaterParticle particle : dryCatGame.particles) {
                        BufferedImage img = rotateRadians(waterSprayParticles.request(), particle.rotation, true);

                        graphics2D.setComposite(AlphaComposite.SrcOver.derive(Math.max(0, particle.alpha)));
                        graphics2D.drawImage(img, particle.x - img.getWidth() / 2, particle.y - img.getHeight() / 2, null);
                        graphics2D.setComposite(AlphaComposite.SrcOver);
                    }
                }
    
                graphics2D.drawImage(pishPishImg.request(), rescaledPoint.x - 22, rescaledPoint.y - 6, null);
            }
            case PLATFORMER -> {
                // Clear the screen
                graphics2D.setColor(Color.CYAN);
                graphics2D.fillRect(0, 0, 1080, 640);
                
                int screenXOffset = platformer.screenXOffset;

                // Draw tiles
                int[][] tiles = platformer.getTiles();
                for (int y = 0; y < tiles.length; y++) {
                    for (int x = 0; x < tiles[y].length; x++) {
                        switch (tiles[y][x]) {
                            case 1 -> graphics2D.drawImage(platBlock.request(), x * platformer.TILE_SIZE + screenXOffset, y * platformer.TILE_SIZE, platformer.TILE_SIZE, platformer.TILE_SIZE, null);
                            case 2 -> graphics2D.drawImage(platKill.request(), x * platformer.TILE_SIZE + screenXOffset, y * platformer.TILE_SIZE, platformer.TILE_SIZE, platformer.TILE_SIZE, null);
                            case 3 -> graphics2D.drawImage(platAccurateHitbox.request(), x * platformer.TILE_SIZE + screenXOffset, y * platformer.TILE_SIZE, platformer.TILE_SIZE, platformer.TILE_SIZE, null);
                        }
//                        if(tiles[y][x] == 2 || tiles[y][x] == 3) {
//                            graphics2D.setColor(Color.RED);
//                            graphics2D.fillRect(x * platformer.TILE_SIZE, y * platformer.TILE_SIZE, platformer.TILE_SIZE, platformer.TILE_SIZE);
//                        }
                    }
                }

                graphics2D.drawImage(platCharacter.request(), (int)platformer.getPlayerX() + screenXOffset, (int)platformer.getPlayerY(), 20, 40, null);
            }
            case UH_OH -> {
                graphics2D.drawImage(uhOh.request(), 0, 0, null);
            }
            case KRUNLIC -> {
                graphics2D.drawImage(krunlicScary.request(), 426, 193, null);
            }
        }

        if(krunlicMode) {
            for(Point krunlicEye : krunlicEyes) {
                int x = krunlicEye.x;
                int y = krunlicEye.y;

                graphics2D.drawImage(krEye[0].request(), x - 127, y - 58, null);

                Vector2D start = new Vector2D(x, y);
                Vector2D end = new Vector2D(rescaledPoint.x, rescaledPoint.y);

                end.subtract(start);
                end.divide(Math.max(120, end.getLength()) / 15);
                
                graphics2D.drawImage(krEye[2].request(), (int) (x - 41 + end.x * 4 + Math.random() * 2 - 1), (int) (y - 41 + end.y + Math.random() * 2 - 1), null);

                graphics2D.drawImage(krEye[1].request(), x - 127, y - 58, null);
            }
        }
        
        
        graphics2D.setFont(comicSansBoldItalic40);
        graphics2D.setColor(white200);

        for(byte i = 0; i < StaticLists.notifs.size(); i++) {
            Notification notification = StaticLists.notifs.get(i);

            graphics2D.setColor(new Color(255, 255, 255, Math.round(notification.progress)));
            graphics2D.drawString(notification.string, 200, 500 - Math.round(notification.progress));
        }
    }

    Color red100 = new Color(255, 0, 0, 100);
    Color red200 = new Color(255, 0, 0, 200);

    public void drawCloseButton(Graphics2D graphics2D) {
        graphics2D.setColor(Color.darkGray);
        graphics2D.setFont(comicSans50);
        graphics2D.drawString("x", 20, 50);

        if(closeButton.contains(keyHandler.pointerPosition)) {
            graphics2D.setColor(black80);
            graphics2D.fillOval(15, 17, 40, 40);
        }
    }

    BufferedImage achievementDisplay = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    BufferedImage achievementDisplayARGB = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    public void redrawAchievements() {
        achievementDisplayARGB = new BufferedImage(920, 530, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = (Graphics2D) achievementDisplayARGB.getGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        graphics2D.setColor(black80);
        graphics2D.fillRoundRect(0, 10 - Math.max(40, achievementsScrollY), 920, 530 + Math.max(40, achievementsScrollY), 40, 40);

//            graphics2D.setColor(black80);
//            graphics2D.fillRoundRect(20, 20 + i * 155 - achievementsScrollY, 920, 150, 40, 40);
        
        
        if(Achievements.ALL_NIGHTER.isObtained()) {
            graphics2D.drawImage(invstgIcon.request(), 10, 30 - achievementsScrollY, 135, 135, null);
        } else {
            graphics2D.drawImage(lockedAchievementImg, 10, 30 - achievementsScrollY, 135, 135, null);
        }
        
        graphics2D.setColor(Color.WHITE);
        graphics2D.setFont(comicSans50);
        graphics2D.drawString(getString("invstgButtonName"), 155, 65 - achievementsScrollY);

        graphics2D.setColor(white200);
        graphics2D.setFont(comicSans30);
        graphics2D.drawString(getString("invstgButtonDesc"), 155, 100 - achievementsScrollY);
        graphics2D.drawString("(" + Investigation.getProgress() + "/" + Investigation.getMaxProgress() + ")", 155, 130 - achievementsScrollY);
        if(!Achievements.ALL_NIGHTER.isObtained()) {
            graphics2D.drawString(getString("invstgRequiresNight4"), 155, 160 - achievementsScrollY);
        }
        if(hoveringInvestigation) {
            graphics2D.setColor(white60);
            graphics2D.fillRoundRect(5, 20 - achievementsScrollY, 900, 155, 25, 25);
        }

        
        List<Achievements> allAchievements = new ArrayList<>(List.of(Achievements.values()));
        int totalSize = allAchievements.size();
        HashMap<String, List<Achievements>> categorized = new HashMap<>();
        for(Achievements achievement : allAchievements) {
            if(categorized.containsKey(achievement.getCategory())) {
                categorized.get(achievement.getCategory()).add(achievement);
            } else {
                categorized.put(achievement.getCategory(), new ArrayList<>(List.of(achievement)));
            }
        }
        List<String> ordered = new ArrayList<>(List.of("normal", "special", "statistics", "challenge"));
        boolean displayCategory = true;

        int lastYValue = 0;

//        graphics2D.setColor(white200);
//        graphics2D.setFont(comicSans30);
//        graphics2D.drawString("- normal -", 10, lastYValue + 10 - achievementsScrollY);
        
        for(int i = 0; i < totalSize; i++) {
//            if(30 + i * 155 - achievementsScrollY > 530)
//                break;
//            if(30 + i * 155 - achievementsScrollY < -135)
//                continue;

            Achievements achievement = null;
            
            if(categorized.containsKey(ordered.get(0))) {
                if(displayCategory) {
                    graphics2D.setColor(white200);
                    graphics2D.setFont(comicSans50);
                    graphics2D.drawString(getString("achievementCategory_" + ordered.get(0)), 10, 160 + 50 + lastYValue + 20 - achievementsScrollY);
                    lastYValue += 80;
                    displayCategory = false;
                }
                List<Achievements> list = categorized.get(ordered.get(0));
                if(list.isEmpty()) {
                    ordered.remove(0);
                    i--;
                    displayCategory = true;
                    continue;
                } else {
                    achievement = list.get(0);
                    list.remove(0);
                }
            }


            BufferedImage icon = lockedAchievementImg;
            if(achievement.isObtained()) {
                icon = achievement.getIcon();
            }
            graphics2D.drawImage(icon, 10, 160 + 30 + lastYValue - achievementsScrollY, 135, 135, null);

            int lastY = 0;
            graphics2D.setColor(Color.WHITE);
            graphics2D.setFont(comicSans50);

            String fullName = getString(achievement.toString().toLowerCase(Locale.ROOT) + "Name");

            for(String name : cropText(fullName, 750, graphics2D)) {
                graphics2D.drawString(name, 155, 160 + 65 + lastYValue + lastY - achievementsScrollY);
                lastY += 40;
            }

            graphics2D.setColor(white200);
            graphics2D.setFont(comicSans30);

            String fullDescription = getString(achievement.toString().toLowerCase(Locale.ROOT) + "Desc");
            String[] description = cropText(fullDescription, 750, graphics2D);

            if(achievement.isHidden() && !achievement.isObtained()) {
                description = new String[] {getString("hiddenAchievement")};
            }

            lastY -= 5;
            for(String desc : description) {
                graphics2D.drawString(desc, 155, 160 + 65 + lastYValue + lastY - achievementsScrollY);
                lastY += 30;
            }

            lastYValue += 155;
        }

        graphics2D.dispose();

        achievementDisplay = new BufferedImage(920, 530, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2DNew = (Graphics2D) achievementDisplay.getGraphics();

        graphics2DNew.drawImage(camStates[0].getSubimage(20, 110, 920, 530), 0, 0, null);
        graphics2DNew.drawImage(achievementDisplayARGB, 0, 0, null);

        graphics2DNew.dispose();
    }

    public void drawAchievementNotifications(Graphics2D graphics2D) {
        int totalMinus = 0;
        for(byte i = 0; i < StaticLists.achievementNotifs.size(); i++) {
            AchievementNotification notification = StaticLists.achievementNotifs.get(i);

            totalMinus += (int) (Math.sin(notification.getCounter() * 0.05F) * 100);
            int posY = 640 - totalMinus;

            graphics2D.setColor(new Color(40, 40, 60));
            graphics2D.fillRoundRect(760, posY, 320, 100, 5, 5);
            graphics2D.setColor(new Color(60, 60, 80));
            graphics2D.fillRoundRect(760, posY, 320, 94, 5, 5);

            graphics2D.drawImage(notification.getIcon(), 760 + 5, posY + 5, 90, 90, null);

            graphics2D.setColor(Color.WHITE);
            graphics2D.setFont(comicSans30);

            graphics2D.drawString(getString("unlockedAchievement"), 770, posY - 10);

            int nameLines = 0;

            int lastStringY = 0;
            for(String string : cropText(notification.getName(), 220, graphics2D)) {
                nameLines++;
                if(nameLines > 2)
                    break;

                graphics2D.drawString(string, 760 + 100, posY + 25 + lastStringY);
                lastStringY += 25;
            }

            graphics2D.setColor(white200);
            graphics2D.setFont(comicSans20);

            int descLines = 0;

            for(String string : cropText(notification.getDescription(), 220, graphics2D)) {
                descLines++;
                if(descLines > 3)
                    break;

                graphics2D.drawString(string, 760 + 100, posY + 25 + lastStringY);
                lastStringY += 20;
            }
        }
    }

    public String[] cropText(String str, int limit, Graphics2D graphics2D) {
        String[] array = str.split(" ");
        byte arrayIndex = 0;

        List<String> list = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        int iterations = 0;
        while (arrayIndex < array.length) {
            if(textLength(graphics2D, (currentLine + array[arrayIndex])) > limit && iterations < 200) {
                list.add(currentLine.toString().trim());
                currentLine = new StringBuilder();
                arrayIndex--;
            } else {
                currentLine.append(array[arrayIndex]).append(" ");
                iterations = 0;
            }
            arrayIndex++;
            iterations++;
        }
        list.add(currentLine.toString().trim());

        list.removeAll(Collections.singleton(""));
        String[] newArray = new String[list.size()];
        list.toArray(newArray);

        return newArray;
    }

    List<String> menuButtons = new ArrayList<>(List.of(">> placeholder"));
    List<String> visibleMenuButtons = menuButtons.subList(0, Math.min(menuButtons.size(), 4));
    byte menuButtonOffset = 0;

    void reloadMenuButtons() {
        List<String> newMenuButtons = new ArrayList<>();

        newMenuButtons.add(">> " + getString("play"));
        if(unlockedBingo) {
            newMenuButtons.add(">> " + getString("bingo"));
        }
        newMenuButtons.add(">> " + getString("achievementsSmall"));
        newMenuButtons.add(">> " + getString("settings"));

        menuButtons = newMenuButtons;

        if(menuButtons.size() > 4) {
            if(moreMenu[0] == null) {
                moreMenu[0] = toCompatibleImage(loadImg("/utils/thereIsMoreMenu.png"));
                moreMenu[1] = mirror(moreMenu[0], 2);
            }
        }

        visibleMenuButtons = menuButtons.subList(0, Math.min(menuButtons.size(), 4));
    }

    public GameType type = GameType.CLASSIC;


    BufferedImage usageImage = new BufferedImage(540, 112, BufferedImage.TYPE_INT_ARGB);

    public void redrawUsage() {
        usageImage = new BufferedImage(540, 112, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = (Graphics2D) usageImage.getGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        graphics2D.setColor(white160);
        graphics2D.setFont(sansSerifPlain40);
        graphics2D.drawString(getString("usage"), 40, 32);
        int start = 40 + textLength(graphics2D, getString("usage"));

        if (usage > 0) {
            byte i = 0;
            while (i < usage) {
                if (usage < 3) {
                    graphics2D.setColor(Color.GREEN.darker());
                } else if (usage > 4) {
                    graphics2D.setColor(Color.RED.darker());
                } else {
                    graphics2D.setColor(Color.YELLOW.darker());
                }
                if(invincible) {
                    graphics2D.setColor(Color.GREEN);
                }

                graphics2D.fillRect(start + 25 + i * 30, 0, 20, 35);
                i++;
            }
        }

        graphics2D.dispose();
        usageImage = trimImageRightBottom(usageImage);
    }

    String tip = "";

    Font sansSerifPlain40;
    public Font sansSerifPlain70 = new Font(Font.SANS_SERIF, Font.PLAIN, 70);

    short energyX;
    short equippedX;
    short floodTextLength1;
    short floodTextLength2;
    short goalTextLength1;
    short bingoTextLength;

    void initializeFontMetrics() {
        String yuGothic = "Yu Gothic";

        if(language.equals("russian")) {
            yuGothic = "Segoe UI Light";
        }

        yuGothicPlain30 = new Font(yuGothic, Font.PLAIN, 30);
        yuGothicPlain50 = new Font(yuGothic, Font.PLAIN, 50);
        yuGothicPlain60 = new Font(yuGothic, Font.PLAIN, 60);
        yuGothicPlain80 = new Font(yuGothic, Font.PLAIN, 80);
        yuGothicPlain120 = new Font(yuGothic, Font.PLAIN, 120);

        yuGothicBold25 = new Font(yuGothic, Font.BOLD, 25);
        yuGothicBold60 = new Font(yuGothic, Font.BOLD, 60);

        yuGothicBoldItalic25 = new Font(yuGothic, Font.BOLD | Font.ITALIC, 25);
        yuGothicBoldItalic40 = new Font(yuGothic, Font.BOLD | Font.ITALIC, 40);

        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = (Graphics2D) image.getGraphics();

        sansSerifPlain40 = new Font(Font.SANS_SERIF, Font.PLAIN, 40);
        graphics2D.setFont(sansSerifPlain40);
        energyX = (short) (50 + textLength(graphics2D, getString("battery")));

        graphics2D.setFont(yuGothicBold25);
        equippedX = (short) (890 - halfTextLength(graphics2D, getString("equipped")));

        graphics2D.setFont(comicSansBold25);
        versionTextLength = (short) (halfTextLength(graphics2D, "v" + version) * 2);

        graphics2D.setFont(comicSansBoldItalic40);
        floodTextLength1 = (short) (540 - halfTextLength(graphics2D, getString("floodedAgain")));
        floodTextLength2 = (short) (540 - halfTextLength(graphics2D, getString("bewareOfSharks")));

        graphics2D.setFont(comicSans60);
        goalTextLength1 = (short) (540 - halfTextLength(graphics2D, getString("surviveUntil4AM")));

        graphics2D.setFont(comicSans60);
        bingoTextLength = (short) (540 - halfTextLength(graphics2D, getString("completedPepingo")));

        graphics2D.dispose();
    }
    void initializeItemNames() {
        soda.assignText(getString("sodaName"), getString("sodaDesc"));
        flashlight.assignText(getString("flashlightName"), getString("flashlightDesc"));
        fan.assignText(getString("fanName"), getString("fanDesc"));
        metalPipe.assignText(getString("metalPipeName"), getString("metalPipeDesc"));
        sensor.assignText(getString("sensorName"), getString("sensorDesc"));
        adblocker.assignText(getString("adblockerName"), getString("adblockerDesc"));
        maxwell.assignText(getString("maxwellName"), getString("maxwellDesc"));
        freezePotion.assignText(getString("freezeName"), getString("freezeDesc"));
        planks.assignText(getString("planksName"), getString("planksDesc"));
        miniSoda.assignText(getString("miniSodaName"), getString("miniSodaDesc"));
        soup.assignText(getString("soupName"), getString("soupDesc"));
        birthdayMaxwell.assignText(getString("bMaxwellName"), getString("bMaxwellDesc"));
        birthdayHat.assignText(getString("birthdayHatName"), getString("birthdayHatDesc"));
        bingoCardItem.assignText(getString("bingoCardName"), getString("bingoCardDesc"));
        starlightBottle.assignText(getString("starlightName"), getString("starlightDesc"));
        shadowTicket.assignText(getString("sticketName"), getString("sticketDesc"));
        styroPipe.assignText(getString("styroPipeName"), getString("styroPipeDesc"));
        basementKey.assignText(getString("basementKeyName"), getString("basementKeyDesc"));
        
        soggyBallpit.assignText(getString("subscriptionName"), getString("subscriptionDesc"));
        manual.assignText(getString("manualName"), getString("manualDesc"));
        corn[0].assignText(getString("cornName"), getString("cornDesc"));
        corn[1].assignText(getString("cornName"), getString("cornDesc"));
        hisPicture.assignText(getString("hisPictureName"), getString("hisPictureDesc"));
        hisPainting.assignText(getString("hisPaintingName"), getString("hisPaintingDesc"));
        sunglasses.assignText(getString("sunglassesName"), getString("sunglassesDesc"));
        riftGlitch.assignText(getString("riftGlitchName"), getString("riftGlitchDesc"));
        pishPish.assignText(getString("pishPishName"), getString("pishPishDesc"));
        weatherStation.assignText(getString("weatherStationName"), getString("weatherStationDesc"));
        
        soggyPen.assignText(getString("soggyPenName"), getString("soggyPenDesc"));
        iceBucket.assignText(getString("iceBucketName"), getString("iceBucketDesc"));
        red40.assignText(getString("red40Name"), getString("red40Desc"));
        shadowblocker.assignText(getString("shadowblockerName"), getString("shadowblockerDesc"));
        megaSoda.assignText(getString("megaSodaName"), getString("megaSodaDesc"));
    }


    Cutscene currentCutscene;
    short fixedOffsetX = 0;
    
    BufferedImage lastReflection;

    // repaint
    public void paintComponent(Graphics preGraphics) {
        Graphics2D graphics2D = (Graphics2D) unshaded.getGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        int shakeX = 0;
        int shakeY = 0;

        if(state.equals(GameState.UNLOADED)) {
            try {
                graphics2D.setColor(Color.black);
                graphics2D.fillRect(0, 0, 1080, 640);

                if(!paused && loading) {
                    Image loading = resize(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/utils/loading.png"))), 80, 80, Image.SCALE_SMOOTH);
                    graphics2D.drawImage(loading, 980, 540, null);
                }

                graphics2D.setFont(comicSans80);
                if(isPepitoBirthday) {
                    if(birthdayAnimation < 254) {
                        birthdayAnimation += 2;
                    }
                    graphics2D.setColor(new Color(255, 255, 255, birthdayAnimation));
                    graphics2D.drawString("Happy birthday Pépito!", 130, 330);
                }

                graphics2D.setFont(comicSans60);
//                if(loading && seconds < 4) {
//                    graphics2D.setColor(Color.GRAY);
//                    graphics2D.drawString("game contains a bit of flashing", 60, 500);
//                }
                if(paused) {
                    if(mirror) {
                        graphics2D.scale(-1, 1);
                        graphics2D.translate(-1080, 0);
                    }
                    graphics2D.drawImage(lastBeforePause, 0, 0, null);

                    if(mirror) {
                        graphics2D.scale(-1, 1);
                        graphics2D.translate(-1080, 0);
                    }
                    
                    graphics2D.setColor(Color.WHITE);
                    graphics2D.drawString(getString("paused"), 130, 180);
                    
                    if(keyHandler.previous == GameState.GAME || keyHandler.previous == GameState.FIELD) {
                        if(night.getEvent().isInGame()) {
                            graphics2D.setColor(white160);
                            if (pauseDieSelected) {
                                graphics2D.setColor(Color.WHITE);
                            }
                            graphics2D.setFont(yuGothicPlain60);

                            graphics2D.drawString(">> " + getString("die"), 110, 560);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            fixedOffsetX = offsetX;

            
            Point rescaledPoint = new Point((int) ((keyHandler.pointerPosition.x - centerX) / widthModifier), (int) ((keyHandler.pointerPosition.y - centerY) / heightModifier));
            
            
            if(!basementHyperOptimization || redrawBHO) {
                if(state == GameState.GAME) {
                    if (night.getWetFloor() > 0) {
                        Enviornment e = night.env;
                        Polygon floorClip = new Polygon(e.getFloorClip().xpoints, e.getFloorClip().ypoints, e.getFloorClip().npoints);
                        floorClip.translate(fixedOffsetX - e.maxOffset(), 0);
                        graphics2D.setClip(floorClip);
                    }
                }
                
                // if wet floor: cut off firsthalf
                // else: normal firsthalf
                firstHalf(graphics2D);

                if(state == GameState.GAME) {
                    if (night.getWetFloor() > 0) {
                        Rectangle bounds = night.env.getFloorClip().getBounds();
                        
                        lastReflection = unshaded.getSubimage(0, bounds.y - bounds.height, 1080, bounds.height);
                        graphics2D = unshaded.createGraphics();
                        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

                        // if wet floor: full firsthalf
                        firstHalf(graphics2D);
                    }
                }
                
                
                // tint
                int realTint = (int) (tintAlpha);
                if (currentFlicker > 0) {
                    realTint = (int) Math.min(255, tintAlpha + currentFlicker);
                }
                if (state == GameState.GAME) {
                    if (night.getMSI().isActive()) {
                        realTint = Math.min(255, realTint + Math.min(100, night.getMSI().getAdditionalTint()));
                    }
                    if (night.getDsc().isFlash()) {
                        realTint = 0;
                    }
                }
                if (realTint > 0) {
                    if(flashlightBrightness > 1) {
                        int x = rescaledPoint.x;
                        if(mirror) {
                            x = 1080 - x;
                        }
                        int y = rescaledPoint.y;
                        
                        graphics2D.setColor(new Color(0, 0, 0, realTint));
                        graphics2D.fillRect(0, 0, x - 200, 640);
                        graphics2D.fillRect(x + 200, 0, 1080 - x - 200, 640);

                        graphics2D.fillRect(x - 200, 0, 400, y - 200);
                        graphics2D.fillRect(x - 200, y + 200, 400, 640 - y - 200);
                        
                        graphics2D.setComposite(new FlashlightMultiply((int) (flashlightBrightness), realTint));
                        graphics2D.drawImage(flashlightLayer, x - 200, y - 200, null);
                        graphics2D.setComposite(AlphaComposite.SrcOver);
                    } else {
                        graphics2D.setColor(new Color(0, 0, 0, realTint));
                        graphics2D.fillRect(0, 0, 1080, 640);
                    }
                }
            }
            
            if (basementHyperOptimization) {
                if (redrawBHO) {
                    redrawBHO = false;
                    
                    // NORMAL GLOW
                    graphics2D.drawImage(basementStaticGlow.request().getSubimage(400 - fixedOffsetX, 0, 1080, 640), 0, 0, null);

                    graphics2D.dispose();
                    lastBHOImage = unshaded;
                    
                    unshaded = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);
                    graphics2D = (Graphics2D) unshaded.getGraphics();
                    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                    graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
                    graphics2D.drawImage(lastBHOImage, 0, 0, null);
                } else {
                    graphics2D.drawImage(lastBHOImage, 0, 0, null);
                }
            }
            
            
            secondHalf(graphics2D);
            
            if(state != GameState.KRUNLIC) {
                drawAchievementNotifications(graphics2D);
            }
            
            if (staticTransparency > 0F) {
                // static
                graphics2D.drawImage(currentStaticImg, 0, 0, null);
            }
            
            if (state == GameState.GAME) {
                try {
                    int lastX = 20;
                    if(invincible) {
                        graphics2D.setColor(Color.GREEN);
                        graphics2D.fillOval(lastX, 20, 20, 20);
                        lastX += 40;
                    }
                    if(universalGameSpeedModifier != 1 || universalGameSpeedModifier != originalGameSpeedModifier) {
                        graphics2D.setColor(Color.CYAN);
                        graphics2D.setFont(comicSans40);
                        graphics2D.drawString(universalGameSpeedModifier + "x", lastX, 40);
                    }
                    
                    
                    if(!night.getEvent().isInGame()) {
                        boolean proceed = true;
                        if(night.getEvent() == GameEvent.DYING && !drawCat) {
                            List<String> ignore = List.of("overseer", "pepito", "beast", "msi");
                            proceed = !ignore.contains(jumpscareKey);
                        }
                        if(night.getType() == GameType.HYDROPHOBIA) {
                            proceed = false;
                        }
                        
                        if(proceed) {
                            graphics2D.setColor(Color.BLACK);
                            graphics2D.fillRect(0, 0, 1080, 640);

                            drawAchievementNotifications(graphics2D);
                        }
                    }
                    
                    Enviornment e = night.env();
                    int maxOffset = e.maxOffset();
                    int offset = fixedOffsetX - maxOffset;
                    
                    if(night.randomSogAlpha > 0) {
                        graphics2D.setComposite(AlphaComposite.SrcOver.derive(night.randomSogAlpha));
                        graphics2D.drawImage(randomsog, 0, 0, null);
                        graphics2D.setComposite(AlphaComposite.SrcOver);
                    }
                    if(night.getAstartaBoss() != null) {
                        if(night.getAstartaBoss().dvdEventSeconds > 0) {
                            int x = (int) (offset + night.getAstartaBoss().getDvdPosX());
                            int y = (int) night.getAstartaBoss().getDvdPosY();

                            graphics2D.setColor(new Color(0, 0, 0, 250));

                            graphics2D.fillRect(0, 0, 1080, y);
                            graphics2D.fillRect(0, y + 300, 1080, 640 - y + 300);

                            graphics2D.fillRect(0, y, x, 300);
                            graphics2D.fillRect(x + 600, y, 1080 - x + 600, 300);
                        }
                    }

                    if (inCam) {
                        if(night.getType().isEndless()) {
                            boolean isNightApproporiate = (endless.getNight() >= 4 && night.getType() == GameType.DAY) || (endless.getNight() >= 5 && night.getType() == GameType.ENDLESS_NIGHT);
                            if (isNightApproporiate && outOfLuck && !portalActive) {
                                graphics2D.setColor(new Color(100, 0, 180));
                                graphics2D.setFont(comicSans60);
                                graphics2D.drawString(getString("outOf"), 830, 340);
                                graphics2D.drawString(getString("luck"), 830, 400);
                            }
                        }

                        graphics2D.setStroke(new BasicStroke(5));
                        graphics2D.setColor(white100);
                        graphics2D.drawRect(30, 30, 1020, 580);
                        graphics2D.setColor(new Color(255, 0, 0, 100));
                        graphics2D.fillOval(50, 50, 50, 50);

                        if (adblockerStatus >= 2) {
                            graphics2D.setFont(comicSans40);

                            if (adblockerStatus == 3) {
                                graphics2D.setColor(new Color(200, 50, 50, 170));
                                graphics2D.drawString(getString("gotAdblocker"), 10, 630);
                            } else {
                                graphics2D.setColor(new Color(200, 50, 50, 200));
                                graphics2D.drawString(getString("foundAdblocker"), 10, 630);
                            }
                        }
                    } else if (night.getGlitcher().isGlitching) {
                        graphics2D.setColor(Color.BLUE);
                        if(type != null) {
                            if (type == GameType.SHADOW) {
                                graphics2D.setColor(new Color(120, 0, 200));
                            }
                        }
                        graphics2D.fillRect(glitchX[0], glitchY[0], 600, 300);
                        graphics2D.fillRect(glitchX[1], glitchY[1], 600, 300);
                    }

                    if (night.getA90().animation > 0) {
                        if(night.getA90().animation > 3) {
                            Point a90 = new Point(night.getA90().x + 100, night.getA90().y + 100);

                            for (int i = 0; i < 54; i++) {
                                for (int j = 0; j < 32; j++) {
                                    Point pixel = new Point(i * 20, j * 20);
                                    double distance = pixel.distance(a90);

                                    if(distance < night.getA90().distance + 20) {
                                        if(distance < night.getA90().distance) {
                                            graphics2D.setColor(new Color((int) (Math.random() * 70) + 185, 0, 0));
                                        } else {
                                            if(distance < night.getA90().distance + 10) {
                                                graphics2D.setColor(new Color((int) (Math.random() * 70) + 185, 0, 0, 100));
                                            } else {
                                                graphics2D.setColor(new Color((int) (Math.random() * 70) + 185, 0, 0, 50));
                                            }
                                        }
                                        graphics2D.fillRect(i * 20, j * 20, 20, 20);
                                    }
                                }
                            }
                        }

                        for(int p = 0; p < night.getA90().points.size(); p++) {
                            Point point = night.getA90().points.get(p);
                            int xShakeEach = (int) (Math.random() * 20 - 10);
                            graphics2D.setColor(Color.RED);
                            graphics2D.fillRect(point.x - 90 + xShakeEach, point.y - 25, 180, 50);
                        }
                        for(int p = 0; p < night.getA90().points.size(); p++) {
                            Point point = night.getA90().points.get(p);
                            int xShakeEach = (int) (Math.random() * 20 - 10);
                            graphics2D.setColor(Color.WHITE);
                            graphics2D.fillRect(point.x - 70 + xShakeEach, point.y - 20, 140, 40);
                        }

                        int a90X = night.getA90().x;
                        int a90Y = night.getA90().y;
                        if(night.getA90().animation > 3) {
                            boolean forgiveConditions = (night.getMSI().isActive() || night.getEvent() == GameEvent.FLOOD || night.getEvent() == GameEvent.DEEP_FLOOD);
                            
                            if(forgiveConditions) {
                                graphics2D.setColor(Color.CYAN);
                                graphics2D.fillRect(a90X - 70, a90Y + 340, (int) (340 * (night.getA90().forgive + night.getA90().margin / 100F)), 30);

                                graphics2D.setFont(comicSans50);
                                graphics2D.drawString(night.getA90().forgiveText, a90X + 100 - halfTextLength(graphics2D, night.getA90().forgiveText), a90Y + 320);
                            }
                        }
                        if (night.getA90().animation >= 1) {
                            graphics2D.drawImage(canny, a90X, a90Y, null);
                            if (night.getA90().animation == 5) {
                                graphics2D.drawImage(uncanny[anim], a90X - 50, a90Y - 50, null);
                            }
                        }
                    }
                    if(night.getA90().drawStopSign) {
                        graphics2D.drawImage(stopSign.request(), night.getA90().x - 50, night.getA90().y - 50, null);

                        if(night.getA90().shots == 0) {
                            graphics2D.drawImage(warningSign.request(), night.getA90().x - 20, night.getA90().y - 20, 250, 220, null);
                        }
                    }
                    
                    if(night.env() instanceof Basement basement) {
                        if(basement.getWhiteScreen() > 0) {
                            graphics2D.setColor(new Color(255, 255, 255, Math.round(basement.getWhiteScreen())));
                            graphics2D.fillRect(0, 0, 1080, 640);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (night.getEvent() == GameEvent.DYING) {
                    if (!drawCat) {
                        List<String> ignoreRed = List.of("a90", "overseer", "pepito", "hydrophobia", "beast", "msi", "dread", "dsc", "fieldObstacles", "fieldA90", "fieldBlimp");

                        if(!ignoreRed.contains(jumpscareKey)) {
                            graphics2D.setColor(Color.RED);
                            graphics2D.fillRect(0, 0, 1080, 640);
                        }
                        
                        int x = 0;
                        int y = 0;

                        List<String> ignoreShake = List.of("shadowPepito", "kiji", "overseer", "beast", "msi", "shock", "fieldObstacles", "fieldA90", "fieldBlimp");
                        if(jumpscareShake < 2 && !ignoreShake.contains(jumpscareKey)) {
                            x -= (int) (Math.random() * 16 - 8);
                            y -= (int) (Math.random() * 16 - 8);
                        }
                        int width = 1080;
                        int height = 640;

                        if(jumpscareKey.equals("astarta")) {
                            x -= astartaJumpscareCounter;
                            y -= (int) (astartaJumpscareCounter * 0.5);
                            width += astartaJumpscareCounter * 2;
                            height += (int) (astartaJumpscareCounter * 1.5);
                        } else if(jumpscareKey.equals("shadowPepito")) {
                            x -= astartaJumpscareCounter;
                            y -= (int) (astartaJumpscareCounter * 0.5);
                            width += astartaJumpscareCounter * 2;
                            height += (int) (astartaJumpscareCounter * 1.5);
                        }

                        graphics2D.drawImage(jumpscare, x, y, width, height, null);
                    } else {
                        if(type == GameType.HYDROPHOBIA) {
                            graphics2D.drawImage(restInPeiceHydro.request(), 50, 50 - deathScreenY, null);
                            Color scaryHydroColor = new Color(30, 40, 110);
                            graphics2D.setColor(scaryHydroColor);
                            graphics2D.setFont(comicSans50);
                            graphics2D.drawString(killedBy, 50, 530 - deathScreenY);

                            HChamber chamber = (HChamber) night.env;
                            if(chamber.showDeathOptions) {
                                graphics2D.setColor(black120);
                                graphics2D.fillRect(0, 0, 1080, 640);
                                graphics2D.setFont(comicSans80);
                                
                                if(!chamber.allowDeathButtons) {
                                    graphics2D.setComposite(AlphaComposite.SrcOver.derive(0.5F));
                                }
                                
                                graphics2D.setColor(new Color(30, 225, 255, chamber.selectedDeathOption == 0 ? 255 : 150));
                                String text1 = getString("hydroGoToMenu");
                                graphics2D.drawString(text1, 540 - halfTextLength(graphics2D, text1), 240);
                                
                                graphics2D.setColor(new Color(30, 225, 255, chamber.selectedDeathOption == 1 ? 255 : 150));
                                String text2 = getString("hydroRespawn");
                                graphics2D.drawString(text2, 540 - halfTextLength(graphics2D, text2), 480);
                                
                                graphics2D.drawImage(hcDeathGlow.request(), 288, 125 + 240 * chamber.selectedDeathOption, null);
                                graphics2D.setColor(scaryHydroColor);
                            }
                        } else {
                            graphics2D.drawImage(restInPeice.request(), 378, 185 - deathScreenY, null);
                            graphics2D.setColor(Color.WHITE);
                            graphics2D.setFont(comicSans50);
                            graphics2D.drawString(killedBy, 540 - halfTextLength(graphics2D, killedBy), 520 - deathScreenY);
                        }

                        if (killedBy.contains("silly cat")) {
                            graphics2D.drawImage(sobEmoji.request(), 776, 476 - deathScreenY, null);
                        }
                        if (jumpscareKey.equals("shadowAstarta")) {
                            graphics2D.drawImage(astartaSticker, 378, 155 - deathScreenY, null);
                            // custom astarta deathscreen
                        }
                        
                        if (pressAnyKey) {
                            if (type == GameType.ENDLESS_NIGHT) {
                                String text = getString("score") + ": " + endless.getNight() + " | " + getString("best") + ": " + recordEndlessNight;
                                graphics2D.drawString(text, 540 - halfTextLength(graphics2D, text), 165 - deathScreenY);
                            }

                            graphics2D.setFont(comicSans40);
                            
                            if(type == GameType.HYDROPHOBIA) {
                                graphics2D.drawString(getString("pressAnyKey"), 50, 580 - deathScreenY);
                            } else {
                                graphics2D.drawString(getString("pressAnyKey"), 540 - halfTextLength(graphics2D, getString("pressAnyKey")), 570 - deathScreenY);
                            }
                            
                            if (birthdayMaxwell.isEnabled()) {
                                graphics2D.drawString(getString("keptMaxwell"), 40, 80 - deathScreenY);
                            }
                        }

                        if(deathScreenY != 0) {
                            graphics2D.drawImage(deathScreenRender.request(), 0, 640 - deathScreenY, null);

                            float currentPercent = 0;
                            for (int x = 0; x < 456; x += 2) {
                                currentPercent += 1 / 228F;

                                graphics2D.drawImage(deathScreenText.getSubimage(x, 0, 2, 330), 75 + x, 135 + (int) (80 * currentPercent), 2, (int) (330 - 125 * currentPercent), null);
                            }

                            if (afterDeathCurText.length() == afterDeathText.length()) {
                                graphics2D.setColor(Color.GRAY);
                                graphics2D.setFont(comicSans40);
                                graphics2D.drawString(getString("pressAnyKey"), 540 - halfTextLength(graphics2D, getString("pressAnyKey")), 1250 - deathScreenY);
                            }
                        }
                    }
                } else if (night.getEvent() == GameEvent.WINNING) {
                    if(everySecond20th.containsKey("stopSimulation")) {
                        graphics2D.drawImage(lastWinScreen, 0, 0, 1080, 640, null);
                    } else {

                        graphics2D.setColor(Color.WHITE);
                        graphics2D.setFont(comicSans80);
                        graphics2D.drawString(getString("ggWonned"), 540 - halfTextLength(graphics2D, getString("ggWonned")), 300);
                        graphics2D.setFont(comicSans60);
                        
                        String clock = night.getClockString().toLowerCase(Locale.ROOT) + " :clock:";
                        graphics2D.drawString(clock, 540 - halfTextLength(graphics2D, clock), 230);
                        
                        if (pressAnyKey) {
                            graphics2D.drawString(getString("pressAnyKeyButAwesome"), 540 - halfTextLength(graphics2D, getString("pressAnyKeyButAwesome")), 370);
                            graphics2D.drawImage(strawber.request(), 60, 390, null);

                            graphics2D.setFont(comicSans60);
                            graphics2D.setColor(Color.getHSBColor(currentRainbow, 1, 1));

                            if (allNighter) {
                                graphics2D.drawString(getString("beat4Nights"), 20, 100);
                            } else if (type == GameType.PREPARTY) {
                                graphics2D.drawString(getString("beatPreparations"), 20, 100);
                            } else if (type == GameType.PARTY) {
                                graphics2D.drawString(getString("beatNight666"), 20, 100);
                                graphics2D.setFont(comicSans40);
                                graphics2D.drawString(getString("rewardMaxwell"), 50, 150);
                            }
                            if(!playedAfterBeatingBasement && type.isBasement()) {
                                graphics2D.drawString(getString("playBasementAgain"), 50, 150);
                            }
                            

                            if (gg) {
                                graphics2D.setFont(comicSans80);
                                graphics2D.setColor(Color.ORANGE);
                                graphics2D.drawString("GG", 790, 500);
                            }
                        }
                        
                        if (type == GameType.CUSTOM && CustomNight.isCustom()) {
                            int points = (int) Math.ceil(CustomNight.getPoints() * CustomNight.visualPointsProgress);

                            graphics2D.setFont(sansSerifPlain70);
                            graphics2D.setColor(Color.getHSBColor(currentRainbow, 1, 1));
                            graphics2D.drawString(points + " " + getString("points"), 420, 600);
                        }
                    }
                }
            }

            if(quickVolumeY > -120) {
                graphics2D.setColor(black120);
                graphics2D.fillRoundRect(550, quickVolumeY - 30, 560, 150, 50, 50);

                graphics2D.setColor(white100);
                graphics2D.fillRect(570, quickVolumeY + 30, 490, 10);

                graphics2D.setColor(white200);
                int x = Math.round(580 + 470 * volume);
                graphics2D.fillOval(x - 25, quickVolumeY + 10, 50, 50);
                graphics2D.setFont(yuGothicPlain50);
                graphics2D.drawString(getString("volume"), 570, quickVolumeY + 100);
            }

            if(riftTransparency > 0) {
                graphics2D.setComposite(AlphaComposite.SrcOver.derive(riftTransparency));
                graphics2D.drawImage(riftTransition, 0, 0, 1080, 640, null);
                graphics2D.setComposite(AlphaComposite.SrcOver.derive(1F));
            }

            if(state == GameState.MILLY) {
                if(secondsInMillyShop >= 3600) {
                    if(dreadUntilGrayscale <= 0) {
                        BufferedImage unshadedCopy = grayscale(unshaded);
                        graphics2D.drawImage(unshadedCopy, 0, 0, null);
                        shakeX += (int) (Math.random() * 10 - 5);
                        shakeY += (int) (Math.random() * 6 - 3);
                    } else {
                        BufferedImage unshadedCopy = grayscale(unshaded);
                        graphics2D.setComposite(AlphaComposite.SrcOver.derive(Math.min(1, 1 - dreadUntilGrayscale)));
                        graphics2D.drawImage(unshadedCopy, 0, 0, null);
                        graphics2D.setComposite(AlphaComposite.SrcOver);
                    }
                    if(dreadUntilVignette < 1) {
                        graphics2D = (Graphics2D) unshaded.getGraphics();

                        graphics2D.drawImage(alphaVignette[0], 0, 0, null);

                        if(tintAlpha < 130 && dreadUntilVignette < 0.6F) {
                            graphics2D.drawImage(alphaVignette[1], 0, 0, null);
                        }
                    }
                }
            }
        }

        if(state == GameState.FIELD) {
            if (field.zoomCountdown > 0) {
                float z = 1 - field.zoomCountdown;
                z = ((z * z) + z) / 2;

                graphics2D.setClip(0, 0, 1080, 640);
                graphics2D.setComposite(AlphaComposite.SrcOver.derive(field.zoomCountdown * field.zoomCountdown));
                graphics2D.drawImage(field.lastImageBeforeField, (int) (-540 * z), (int) (-320 * z), (int) (1080 + 1080 * z), (int) (640 + 640 * z), null);
            }
        }

        if(krunlicPhase >= 1) {
            BufferedImage unshadedCopy = grayscale(unshaded);
            graphics2D.drawImage(unshadedCopy, 0, 0, null);
        }
        if(shader3am) {
            BufferedImage unshadedCopy = new BufferedImage(1080, 640, BufferedImage.TYPE_BYTE_INDEXED);
            Graphics2D copyGraphics = unshadedCopy.createGraphics();
            copyGraphics.drawImage(unshaded, 0, 0, null);
            copyGraphics.dispose();
            graphics2D.drawImage(unshadedCopy, 0, 0, null);
        }
        
        applyHydrophobiaFilter(graphics2D, 1080);
        
        
        graphics2D.dispose();

        super.paintComponent(preGraphics);

        Graphics2D overGraphics2D = (Graphics2D) preGraphics;
        overGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        overGraphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        
        
        
        float contrastBrightness = 1;
        int contrastOffset = 0;

        if (state == GameState.GAME) {
            if(screenShake) {
                if (night.getJumpscareCat().getShake() > 0) {
                    int shX = night.getJumpscareCat().getShake();
                    int shY = night.getJumpscareCat().getShake() / 2;
                    shakeX += (int) (Math.random() * shX) - shX / 2;
                    shakeY += (int) (Math.random() * shY) - shY / 2;
                }
                if (night.getMSI().isActive()) {
                    int shX = night.getMSI().getShake();
                    int shY = night.getMSI().getShake() / 2;
                    shakeX += (int) (Math.random() * shX - shX / 2);
                    shakeY += (int) (Math.random() * shY - shY / 2);
                }
                if (night.getDsc().isFight()) {
                    int shX = night.getDsc().getShake();
                    int shY = night.getDsc().getShake() / 2;
                    shakeX += (int) (Math.random() * shX - shX / 2);
                    shakeY += (int) (Math.random() * shY - shY / 2);
                }
                if (night.getShock().isDoom()) {
                    float f = night.getShock().getDoomCountdown();
                    int shX = (int) (35 * f);
                    int shY = (int) (17 * f);
                    shakeX += (int) (Math.random() * shX - shX / 2);
                    shakeY += (int) (Math.random() * shY - shY / 2);
                }
                if (night.getElAstarta().isActive()) {
                    int shX = (int) (night.getElAstarta().getShake() / 1.5);
                    int shY = night.getElAstarta().getShake() / 3;
                    shakeX += (int) (Math.random() * shX - shX / 2);
                    shakeY += (int) (Math.random() * shY - shY / 2);

                    if (night.getElAstarta().getShake() > 8) {
                        int cos = (int) (Math.cos(fixedUpdatesAnim * 0.05) * 3 * (night.getElAstarta().getShake() - 8));
                        int sin = (int) (Math.sin(fixedUpdatesAnim * 0.05) * 1.5 * (night.getElAstarta().getShake() - 8));
                        unshaded = offset(unshaded, cos, sin);
                    }
                }
                if (night.getLemonadeCat().isActive()) {
                    if (night.getLemonadeCat().isNineBossfight()) {
                        int shX = 10;
                        int shY = 6;
                        shakeX += (int) (Math.random() * shX - shX / 2);
                        shakeY += (int) (Math.random() * shY - shY / 2);
                    }
                }
            }
            if (night.getAstartaBoss() != null) {
                if (night.getAstartaBoss().jumpscareBrightness > 1) {
                    contrastBrightness = night.getAstartaBoss().jumpscareBrightness;
                    contrastOffset = night.getAstartaBoss().jumpscareOffset;
                    pixelation = Math.max(2, pixelation);
                }
                if (night.getAstartaBoss().getDvdShake() > 0 && screenShake) {
                    int shX = (int) night.getAstartaBoss().getDvdShake();
                    int shY = (int) (night.getAstartaBoss().getDvdShake() / 2);
                    shakeX += (int) (Math.random() * shX - shX / 2);
                    shakeY += (int) (Math.random() * shY - shY / 2);
                }
            }
            if (night.env() instanceof Basement basement) {
                if(screenShake) {
                    float shX = basement.getShake();
                    float shY = basement.getShake() / 2;
                    shakeX += (int) ((Math.random() * shX) - shX / 2);
                    shakeY += (int) ((Math.random() * shY) - shY / 2);
                }
                
                if (night.getEvent().isInGame()) {
                    if(basement.getStage() == 6) {
                        float w = basement.getGasLeakWobble();
                        
                        if (w > 0) {
                            unshaded = vertWobble(unshaded, basement.getGasLeakWobble() / 5F, 4, 0.02F, 1);

                            if(!basement.doWiresWork()) {
                                w = (w * w) / 4;
                            }
                            
                            if (w > 5) {
                                w -= 5;
                                contrastBrightness = w / 26 + 1;
                                contrastOffset = (int) (-w * 2.3);
                            }
                        }
                    }
                    if(basement.getStage() == 7) {
                        contrastBrightness = 1.5F;
                        contrastOffset = -15;
                    }
                    if (night.getEvent().isFlood()) {
                        if (night.red40Phase > 0) {
                            float w = (float) Math.sin(fixedUpdatesAnim / 80F) * 7;
                            if (w > 0) {
                                contrastBrightness = w / 20 + 1;
                                contrastOffset = (int) (-w * 2.5);
                            }
                        }
                    }
                }
            }
            if (night.getType() == GameType.HYDROPHOBIA) {
                HChamber chamber = (HChamber) night.env();
                if(screenShake) {
                    float shX = chamber.getShake();
                    float shY = chamber.getShake() / 2;
                    shakeX += (int) ((Math.random() * shX) - shX / 2);
                    shakeY += (int) ((Math.random() * shY) - shY / 2);
                }
                
                if (chamber.daZoom > 0) {
                    int z = (int) chamber.daZoom;
                    graphics2D = (Graphics2D) unshaded.getGraphics();
                    graphics2D.drawImage(resize(unshaded.getSubimage((int) (z * 1.5), z / 2, 1080 - z * 3, 640 - z), 1080, 640, Image.SCALE_FAST), 0, 0, null);
                    graphics2D.dispose();
                }
            }
            if (night.getShadowPepito() != null) {
                if (night.getShadowPepito().jumpscareBrightness > 1) {
                    contrastBrightness = night.getShadowPepito().jumpscareBrightness;
                    contrastOffset = night.getShadowPepito().jumpscareOffset;
                    pixelation = Math.max(2, pixelation);
                }
            }
            if(night.getScaryCat().isActive()) {
                if (night.getScaryCat().isNine()) {
                    float w = Math.max(0, night.getScaryCat().getAlpha()) / (Math.max(1, 4 - night.getScaryCat().getCount())) * 50F;
                    unshaded = yellowContrast(unshaded, w / 25 + 1, (int) (-w * 2.5));
                }
            }
            if(night.getLemonadeCat().isActive()) {
                if (night.getLemonadeCat().nuclearOxygen) {
                    float w = Math.max(0, (100 - night.getLemonadeCat().oxygenLevel) / 5F + 4.5F);
                    unshaded = yellowContrast(unshaded, w / 25 + 1, (int) (-w * 2.5));
                }
            }
            if (night.getEvent().isInGame() && night.getTemperature() > 20) {
                unshaded = wobble(unshaded, (night.getTemperature() - 20) / 5F, 4, 0.02F, 1);
            }
        } else if (state == GameState.CRATE && screenShake) {
            float shX = crateShake * 2;
            float shY = crateShake;
            shakeX += (int) ((Math.random() * shX) - shX / 2);
            shakeY += (int) ((Math.random() * shY) - shY / 2);
            
        } else if(state == GameState.CUTSCENE) {
            contrastBrightness = currentCutscene.contrastBrightness;
            contrastOffset = currentCutscene.contrastOffset;
        }
        
        
        if(state == GameState.DRY_CAT_GAME) {
            if (dryCatGame.daZoom > 0) {
                int z = (int) dryCatGame.daZoom;
                graphics2D = (Graphics2D) unshaded.getGraphics();
                graphics2D.drawImage(resize(unshaded.getSubimage((int) (z * 1.5), z / 2, 1080 - z * 3, 640 - z), 1080, 640, Image.SCALE_FAST), 0, 0, null);
                graphics2D.dispose();
            }
        }

        
//        if(true) {
//            unshaded = vertWobble(unshaded, 30, 2, 0.2F, 0.5F);
//            unshaded = wobble(unshaded, 30, 2, 0.2F, 0.5F);
//            pixelation = 1 + (int) Math.abs(Math.sin(fixedUpdatesAnim / 12F) * 15F);
//
//            int xOffset = (fixedUpdatesAnim * 12) % 1080;
//            graphics2D = (Graphics2D) unshaded.getGraphics();
//            graphics2D.drawImage(theStrip.request(), -xOffset, (int) (Math.sin(fixedUpdatesAnim / 8F) * 320 + 320), null);
//            graphics2D.drawImage(theStrip.request(), -xOffset + 1080, (int) (Math.sin(fixedUpdatesAnim / 8F) * 320 + 320), null);
//            
//            graphics2D.dispose();
//        }
        
        

        if(shakeX > 0 || shakeY > 0) {
            unshaded = offset(unshaded, shakeX, shakeY);
        }

        if(contrastBrightness != 1 || contrastOffset != 0) {
            unshaded = contrast(unshaded, contrastBrightness, contrastOffset);
        }
        

        if(bloom) {
            BufferedImage blurred = gaussianBlur10H.filter(resize(unshaded, 270, 160, Image.SCALE_FAST), null);
            blurred = resize(gaussianBlur10V.filter(blurred, null), 1080, 640, Image.SCALE_SMOOTH);

            graphics2D = (Graphics2D) unshaded.getGraphics();
            graphics2D.setComposite(AdditiveComposite.Add);
//            graphics2D.setComposite(MultiplyComposite.Multiply);
            graphics2D.drawImage(blurred, 0, 0, null);
            graphics2D.dispose();
        }
        
        
        lastFullyRenderedUnshaded = unshaded;

        int totalPixelation = (int) pixelation;
        
        if(state == GameState.DRY_CAT_GAME) {
            totalPixelation = (int) (4 - (dryCatGame.timer / 43F) * 4);
        }


        if(mirror) {
//            unshaded = mirror(unshaded, 1);
            overGraphics2D.scale(-1, 1);
            overGraphics2D.translate(-width, 0);
        }
        
        
        if(totalPixelation > 1) {
            overGraphics2D.drawImage(unshaded.getScaledInstance(1080 / totalPixelation, 640 / totalPixelation, Image.SCALE_FAST), centerX, centerY, (int) (1080 * widthModifier), (int) (640 * heightModifier), null);
        } else {
            overGraphics2D.drawImage(unshaded, centerX, centerY, currentWidth, currentHeight, null);
        }


        if(mirror) {
            overGraphics2D.scale(-1, 1);
            overGraphics2D.translate(-width, 0);
        }
        
        
        overGraphics2D.setColor(Color.GREEN);
        overGraphics2D.setFont(debugFont);

        int fpsCounterY = 20;
        if(fpsCounters[0]) {
            overGraphics2D.drawString(Math.max(0, fpscnt.get()) + " FPS", (int) (5 * widthModifier) + centerX, (int) (fpsCounterY * heightModifier) + centerY);
            fpsCounterY += 20;
        }
        if(fpsCounters[1]) {
            overGraphics2D.drawString(Math.max(0, upscnt.get()) + " UPS", (int) (5 * widthModifier) + centerX, (int) (fpsCounterY * heightModifier) + centerY);
            fpsCounterY += 20;
        }
        if(fpsCounters[2])
            overGraphics2D.drawString(Math.max(0, fupscnt.get()) + " FUPS", (int) (5 * widthModifier) + centerX, (int) (fpsCounterY * heightModifier) + centerY);

//        if(debugMode) {
//            String boba = getBoba();
//            overGraphics2D.drawString(boba, (int) (5 * widthModifier) + centerX, (int) (630 * heightModifier) + centerY);
//        }

        if(debugMode) {
            BufferedImage image = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_ARGB);
            graphics2D = (Graphics2D) image.getGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            graphics2D.setFont(comicSans25);
            int last = 50;

            addDebugText(graphics2D, "- General -", last); last += 25;
            addDebugText(graphics2D, "STATE: " + state, last); last += 25;
            addDebugText(graphics2D, "Timers: " + StaticLists.timers.size(), last); last += 25;
            addDebugText(graphics2D, "Loaded images: " + StaticLists.loadedPepitoImages.size(), last); last += 25;
            addDebugText(graphics2D, "FPS: " + fpscnt.get(), last); last += 25;
            addDebugText(graphics2D, "UPS: " + upscnt.get(), last); last += 25;
            addDebugText(graphics2D, "FUPS: " + fupscnt.get(), last); last += 25;
            addDebugText(graphics2D, "Tint: " + Math.round(tintAlpha * 1000F) / 1000F, last); last += 25;
            addDebugText(graphics2D, "offsetX: " + offsetX, last); last += 25;
            addDebugText(graphics2D, "Type: " + type, last); last += 25;
            addDebugText(graphics2D, "RiftTransparency: " + riftTransparency, last); last += 25;

            if(state == GameState.GAME) {
                last += 25;

                addDebugText(graphics2D, "- Level -", last); last += 25;
                addDebugText(graphics2D, "Time: " + night.getSeconds(), last); last += 25;
                addDebugText(graphics2D, "Event: " + night.getEvent(), last); last += 25;
                addDebugText(graphics2D, "Type: " + night.getType(), last); last += 25;
                addDebugText(graphics2D, "Temperature: " + Math.round(night.getTemperature() * 1000F) / 1000F, last); last += 25;
                addDebugText(graphics2D, "IsItemless: " + night.isItemless(), last); last += 25;
                addDebugText(graphics2D, "IsSoundless: " + night.isSoundless(), last); last += 25;
                addDebugText(graphics2D, "Raindrops: " + night.raindrops.size(), last); last += 25;
                addDebugText(graphics2D, "currentWaterLevel: " + currentWaterLevel, last); last += 25;
                addDebugText(graphics2D, "currentWaterPos: " + currentWaterPos, last); last += 25;
            }

            graphics2D.dispose();
            image = trimImageRightBottom(image);
            overGraphics2D.drawImage(image, centerX, centerY, (int) (image.getWidth() * widthModifier), (int) (image.getHeight() * heightModifier), null);
        }

        if(state != GameState.UNLOADED) {
            if (sensor.isEnabled() || adBlocked) {
                overGraphics2D.setFont(consoleFont);
                byte i = 0;
                while (i < console.list.size()) {
                    String chat = console.list.get(i);
                    overGraphics2D.drawString(chat, (int) (5 * widthModifier) + centerX, (int) ((40 * i + 60) * heightModifier) + centerY);
                    i++;
                }
            }
        }
        if(Console.isOn()) {
            byte i = 0;
            while (i < Console.getLines().size()) {
                if(i == Console.getLines().size() - 1) {
                    overGraphics2D.setColor(new Color(0, 100, 0));
                    overGraphics2D.drawString(Console.possibleCommand, 405, (40 * i + 60));
                }

                String chat = Console.getLines().get(i);
                overGraphics2D.setColor(Color.GREEN);
                overGraphics2D.drawString(chat, 405, (40 * i + 60));

                if(i == Console.getLines().size() - 1) {
                    if (fixedUpdatesAnim / 16 % 2 == 0) {
                        overGraphics2D.fillRect(407 + textLength(overGraphics2D, chat), (40 * i + 35), 3, 30);
                    }
                }
                i++;
            }

            i--;
            overGraphics2D.fillRect(390, (40 * i + 30), 5, 40);
        }

        
        
        int halfCursorSize = 5;
        boolean condition = hoveringAnyDoorButton;
        if(type == GameType.DAY) {
            condition = condition || keyHandler.hoveringNeonSogSign || keyHandler.hoveringNeonSog;
        }
        if(condition) {
            halfCursorSize = 7;
        }
        

        boolean drawNormalCursor = !(state == GameState.DRY_CAT_GAME || state == GameState.CORNFIELD);
        if(state == GameState.GAME) {
            if(night.getEvent() == GameEvent.MR_MAZE) {
                drawNormalCursor = false;
            }
        }
        
        if(drawNormalCursor) {
            overGraphics2D.setStroke(new BasicStroke());
            overGraphics2D.setColor(Color.WHITE);
            overGraphics2D.fillOval(pointX - (int) (halfCursorSize * widthModifier), pointY - (int) (halfCursorSize * heightModifier), (int) (halfCursorSize * 2 * widthModifier), (int) (halfCursorSize * 2 * heightModifier));
        }
        
        if(state == GameState.GAME) {
            if(night.getLemonadeCat().isActive() || night.getDsc().isFight()) {
                float r = 0;
                float z = 0;
                
                if(night.getLemonadeCat().isActive()) {
                    r = night.getLemonadeCat().getRotation();
                    z = night.getLemonadeCat().getCursorZoom();
                }
                if(night.getDsc().isFight()) {
                    z = night.getDsc().getCursorZoom();
                    r = fixedUpdatesAnim * 0.01F + night.getDsc().getCursorRotation();
                    
                    int green = 250;
                    int blue = (int) ((Math.sin(fixedUpdatesAnim / 400F) / 3 + 0.66) * 240);

                    if(night.getDsc().getGunExtend() != 0) {
                        float percent = night.getDsc().getGunExtend() / 260F;
                        green = (int) (green - green * percent);
                        blue = (int) (blue - blue * percent);
                    }
                    overGraphics2D.setColor(new Color(255, green, blue));
                }


                overGraphics2D.setStroke(new BasicStroke(5 * overallModifier * z));
                overGraphics2D.drawOval(pointX - (int) (20 * widthModifier * z), pointY - (int) (20 * heightModifier * z), (int) (40 * widthModifier * z), (int) (40 * heightModifier * z));

                float f = fixedUpdatesAnim * 0.01F;

                for(int i = 0; i < 4; i++) {
                    double cos = Math.cos(f + i * 1.57 + r) * z * widthModifier;
                    int x1 = (int) Math.round(pointX + 20 * cos);
                    int x2 = (int) Math.round(pointX + 50 * cos);

                    double sin = Math.sin(f + i * 1.57 + r) * z * heightModifier;
                    int y1 = (int) Math.round(pointY + 20 * sin);
                    int y2 = (int) Math.round(pointY + 50 * sin);

                    overGraphics2D.drawLine(x1, y1, x2, y2);
                }
            }
            if(night.isRadiationModifier()) {
                if (radiationCursor < 50) {
                    overGraphics2D.setColor(new Color(15, 220, 15, Math.max(0, 80 - (int) (radiationCursor * 1.4F))));
                    overGraphics2D.setStroke(new BasicStroke((18 - radiationCursor / 3F) * overallModifier));
                    overGraphics2D.drawOval(pointX - (int) (radiationCursor * widthModifier) - 1, pointY - (int) (radiationCursor * heightModifier) - 1, (int) (radiationCursor * 2 * overallModifier), (int) (radiationCursor * 2 * overallModifier));
                    overGraphics2D.setStroke(new BasicStroke());
                }
            }
        }

        fpscnt.frame();

        overGraphics2D.dispose();
    }
    

    public void addDebugText(Graphics2D graphics2D, String text, int last) {
        graphics2D.setColor(black140);
        graphics2D.fillRect(0, last - 25, textLength(graphics2D, text) + 20, 25);
        graphics2D.setColor(Color.WHITE);
        graphics2D.drawString(text, 10, last);
    }

    public void loadA90Images() {
        if(!night.getType().isParty()) {
            canny = toCompatibleImage(resize(loadImg("/game/entities/a90/canny.png"), 200, 200, Image.SCALE_SMOOTH));
            uncanny[0] = toCompatibleImage(loadImg("/game/entities/a90/uncanny.png"));
        } else {
            canny = toCompatibleImage(resize(loadImg("/game/entities/a90/birthdayCanny.png"), 200, 200, Image.SCALE_SMOOTH));
            uncanny[0] = toCompatibleImage(loadImg("/game/entities/a90/birthdayUncanny.png"));
        }
        uncanny[1] = toCompatibleImage(redify(uncanny[0]));
    }

    @NotNull
    private String getBoba() {
        String boba = "";
        try {
            boba += night.getPepito().seconds + "s | ";
            boba += night.getPepito().pepitoStepsLeft + " steps | ";
            boba += night.getPepito().notPepitoChance + "% notPepito | ";
            boba += night.getA90().arrivalSeconds + "s a90 | ";
            boba += night.getMSI().arrivalSeconds + "s MSI | ";
            boba += night.getAstarta().arrivalSeconds + "s astarta | ";
            boba += offsetX + " os | ";
            boba += night.getGlitcher().counter + " gltch | ";
            boba += night.getColaCat().currentState + " cc | ";
            boba += tintAlpha + " tint";
        } catch (Exception ignored) { }
        return boba;
    }

    float currentRainbow = 0;

    boolean gg = false;

    public short centerX = 0;
    public short centerY = 0;

    Rat b;

    byte selectedOption = 0;

    public float pixelation = 1;

    String killedBy = "killed by pépito";
    String jumpscareKey = "pepito";


    public float widthModifier = width / 1080.0F;
    public float heightModifier = height / 640.0F;
    float overallModifier = (widthModifier + heightModifier) / 2;

    short currentWidth = 1080;
    short currentHeight = 640;

    public static BufferedImage toCompatibleImage(BufferedImage image) {
        if(image == null)
            return null;

        GraphicsConfiguration gfxConfig = GraphicsEnvironment.
                getLocalGraphicsEnvironment().getDefaultScreenDevice().
                getDefaultConfiguration();

        if (image.getColorModel().equals(gfxConfig.getColorModel()))
            return image;

        BufferedImage newImage = gfxConfig.createCompatibleImage(
                image.getWidth(), image.getHeight(), image.getTransparency());

        Graphics2D g2d = newImage.createGraphics();

        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return newImage;
    }

    public static BufferedImage loadImg(String text) {
        try {
            return ImageIO.read(Objects.requireNonNull(GamePanel.class.getResourceAsStream(text)));
        } catch (IOException | NullPointerException ignored) {
            System.out.println("FAILED: " + text);
        }

        return null;
    }

    public BufferedImage camLayer0;

    public String nightAnnounceText = "night one";
    byte currentNight = 1;

    boolean announcerOn = false;
    short announceCounter = 1;

    public void announceNight(byte night, GameType type) {
        if(type == GameType.ENDLESS_NIGHT) {
            announceChallenger(night, 5000, false);
        }

        announceCounter = 1;

        String string = getString("night") + " ";

        switch (night) {
            case 1 -> string += getString("one");
            case 2 -> string += getString("two");
            case 3 -> string += getString("three");
            case 4 -> string += getString("four");
            case 5 -> string += getString("five");
            case 6 -> string += getString("six");
            case 7 -> string += getString("seven");
            default -> string += night;
        }
        switch (type) {
            case SHADOW -> string = getString("SHADOWNIGHT");
            case PREPARTY -> string = getString("pepitoPartyPreparations");
            case PARTY -> string = getString("night666");
            case CUSTOM -> {
                if(CustomNight.isCustom()) {
                    string = getString("customNightSmall");
                } else {
                    string = getString("challengeSmall") + " " + (CustomNight.selectedChallenge + 1);
                }
            }
            case BASEMENT -> string = getString("basementAnnouncement");
            case BASEMENT_PARTY -> string = getString("basementPartyAnnouncement");
            case HYDROPHOBIA -> string = getString("hydrophobiaChamber");
        }

        nightAnnounceText = string;
        announcerOn = true;

        sound.playRate("nightStart", 0.08, 1);
    }

    public void announceChallenger(byte night, int delay, boolean force) {
        challengerAlpha = 0;
        challengerString = getString("newChallenger");

        challenger = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = (Graphics2D) challenger.getGraphics();

        if(type == GameType.ENDLESS_NIGHT) {
            if ((night > 1 && night < 9) || night == 18) {
                switch (night) {
                    case 2 -> {
                        graphics2D.drawImage(silhouette(astartaCam[1], Color.BLACK), 160, 150, 400, 500, null);
                        graphics2D.drawImage(silhouette(msiImage[0], Color.BLACK), 520, 150, 400, 600, null);
                    }
                    case 3 -> graphics2D.drawImage(silhouette(sharkImg.request(), Color.BLACK), 330, 150, 400, 500, null);
                    case 4 -> graphics2D.drawImage(silhouette(boykisserImg.request(), Color.BLACK), 0, 0, 1080, 640, null);
                    case 5 -> {
                        graphics2D.drawImage(silhouette(makiCam, Color.BLACK), 230, 110, 270, 550, null);
                        graphics2D.drawImage(silhouette(lemonadeGato[0].request(), Color.BLACK), 520, 170, 400, 470, null);
                    }
                    case 6 -> graphics2D.drawImage(silhouette(mirrorCatImg, Color.BLACK), 160, 120, 750, 400, null);
                    case 7 -> challengerString = getString("peace");
                    case 18 -> graphics2D.drawImage(silhouette(msiImage[3], Color.BLACK), 340, 40, 400, 600, null);
                    default -> {
                        graphics2D.dispose();
                        if(!force) {
                            return;
                        }
                    }
                }
            } else {
                graphics2D.dispose();
                if(!force) {
                    return;
                }
            }
        } else if(type == GameType.SHADOW) {
            if(night == 60) {
                challengerString = getString("newChallengers");
                graphics2D.drawImage(silhouette(wiresImg.request(), Color.BLACK), 340, 100, 400, 440, null);
            } else if(night == 61) {
                challengerString = getString("newChallengers");
                graphics2D.drawImage(silhouette(blackScaryCat.request(), Color.BLACK), 340, 190, 400, 260, null);
            }
        } else {
            graphics2D.dispose();
            if(!force) {
                return;
            }
        }

        graphics2D.dispose();

        new Pepitimer(() -> {
            sound.play("challenger", 0.5);
            challengerAlpha = 360;
            challengerColor = new Color(255, 255, 0);
            RepeatingPepitimer[] timer = new RepeatingPepitimer[1];

            timer[0] = new RepeatingPepitimer(() -> {
                challengerColor = new Color(Math.abs(255 - challengerColor.getBlue()), Math.abs(255 - challengerColor.getRed()), Math.abs(255 - challengerColor.getGreen()));
                if (challengerAlpha <= 0) {
                    timer[0].cancel(false);
                }
            }, 100, 100);
        }, delay);
    }

    private void announceDay() {
        challengerAlpha = 0;
        announceCounter = 1;

        String string = getString("day") + " ";

        switch (endless.getNight()) {
            case 1 -> string += getString("one");
            case 2 -> string += getString("two");
            case 3 -> string += getString("three");
            case 4 -> string += getString("four");
            case 5 -> string += getString("five");
            case 6 -> string += getString("six");
            case 7 -> string += getString("seven");
            default -> string += endless.getNight();
        }

        nightAnnounceText = string;
        announcerOn = true;

        sound.playRate("nightStart", 0.08, 0.7);
        sound.play("dayStart", 0.08);
    }

    void startPlayMenu() {
        PlayMenu.index = 1;
        PlayMenu.list = new ArrayList<>();
        
        String subtext1 = ">> " + getString("pmPlay");
        PlayMenuElement challenge = new PlayMenuElement("challenge", loadImg("/menu/play/challenge.png"), getString("pmChallenge"), subtext1);
        PlayMenu.list.add(challenge);
        PlayMenuElement normal = new PlayMenuElement("normal", loadImg("/menu/play/normal.png"), getString("pmNormal"), currentNight == 1 ? subtext1 : subtext1 + " - " + getString("pmNight") + " " + currentNight);
        PlayMenu.list.add(normal);
        PlayMenuElement endless = new PlayMenuElement("endless", loadImg("/menu/play/endless.png"), getString("pmEndless"), recordEndlessNight == 0 ? subtext1 : subtext1 + " - " + getString("pmBest") + " " + recordEndlessNight);
        PlayMenu.list.add(endless);
        
        if(FreStats.isEventActive) {
            PlayMenuElement fruits = new PlayMenuElement("fruits", loadImg("/menu/play/fruits.png"), getString("pmEndless"), recordEndlessNight == 0 ? subtext1 : subtext1 + " - " + getString("pmBest") + " " + recordEndlessNight);
            PlayMenu.list.add(fruits);
        }

        PlayMenu.selectOffsetX = PlayMenu.getGoalSelectOffsetX();
        PlayMenu.movedMouse = false;

        state = GameState.PLAY;
        sound.play("select", 0.1);

        staticTransparency = 0.02F;
        endStatic = 0.02F;

        fadeOut(255, 0, 3);

        recalculateButtons(GameState.PLAY);

        if(!seenEndlessDisclaimer) {
            state = GameState.ENDLESS_DISCLAIMER;
            pressAnyKey = true;
        }
    }

    void startItemSelect() {
        if(isItemUsed.isEmpty()) {
            itemLimit = 3;
        }
        
        state = GameState.ITEMS;
        sound.play("select", 0.1);
        
        for(Item item : fullItemList) {
            item.deselect();
            
            if(saveItems && isItemUsed.containsKey(item) && item.getAmount() != 0) {
                item.setSelected(isItemUsed.get(item));
            }
        }

        startButtonSelected = false;
        selectedItemX = 0;
        selectedItemY = 0;

        if(type == GameType.CUSTOM) {
            staticTransparency = 0F;
            endStatic = 0F;
            fadeOut(255, 50, 2);
        } else {
            staticTransparency = 0.02F;
            endStatic = 0.02F;
            fadeOut(255, 150, 1);
        }

        updateItemList();
        redrawItemsMenu();
        recalculateButtons(GameState.ITEMS);
    }

    void redrawItemsMenu() {
        Graphics2D graphics2D = (Graphics2D) itemsMenu.getGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        
        boolean isCustom = type == GameType.CUSTOM;

        if(isCustom) {
            graphics2D.setColor(Color.BLACK);
            graphics2D.fillRect(0, 0, 1080, 640);
        } else {
            graphics2D.drawImage(camStates[0], 0, 0, null);
        }

        graphics2D.setColor(black80);
        graphics2D.fillRoundRect(725, 50, 330, 460, 50, 50);

        graphics2D.setColor(Color.WHITE);
        graphics2D.fillRect(790, 145, 210, 3);

        if(isCustom) {
            graphics2D.setStroke(new BasicStroke(3));
            graphics2D.drawRoundRect(725, 50, 330, 460, 50, 50);
            graphics2D.setStroke(new BasicStroke());
        }

        byte y = 0;
        while (y < rows) {
            byte x = 0;
            while (x < columns) {
                graphics2D.setColor(black120);
                graphics2D.fillRoundRect(x * 170 + 40, y * 170 + (210 - 40 * Math.min(rows, 4)) - itemScrollY, 130, 130, 50, 50);

                try {
                    Item item = itemList.get(x + (y * columns));

                    if (item.isSelected() && isCustom) {
                        float hue = ((fixedUpdatesAnim / 2F) % 360) / 360F;
                        Color shufel = Color.getHSBColor(hue, 1, 1);
                        graphics2D.setColor(shufel);
                        graphics2D.fillRoundRect(x * 170 + 40, y * 170 + (210 - 40 * Math.min(rows, 4)) - itemScrollY, 130, 130, 50, 50);
                        
                        graphics2D.drawImage(customItemFaded.request(), x * 170 + 40, y * 170 + (210 - 40 * Math.min(rows, 4)) - itemScrollY, null);
                    }
                } catch (Exception ignored) { }
                

                if(isCustom) {
                    graphics2D.setColor(Color.WHITE);
                    graphics2D.setStroke(new BasicStroke(3));
                    graphics2D.drawRoundRect(x * 170 + 40, y * 170 + (210 - 40 * Math.min(rows, 4)) - itemScrollY, 130, 130, 50, 50);
                    graphics2D.setStroke(new BasicStroke());
                }

                try {
                    Item item = itemList.get(x + (y * columns));
                    
                    if(item.getAmount() != 0) {
                        BufferedImage icon = item.getIcon();
                        graphics2D.drawImage(icon, x * 170 + 40 + (65 - icon.getWidth() / 2), y * 170 + (210 - 40 * Math.min(rows, 4)) - itemScrollY + (115 - icon.getHeight()), null);

                        graphics2D.setFont(new Font("Yu Gothic", Font.PLAIN, 50));
                        byte xAdder = 20;
                        byte yAdder = -8;

                        if (item.getAmount() < 0) {
                            graphics2D.setFont(new Font("Yu Gothic", Font.BOLD, 55));

                            xAdder = 0;
                            yAdder = 0;
                        } else if(item.getAmount() > 9) {
                            xAdder -= 25;
                        }

                        graphics2D.setColor(Color.WHITE);
                        graphics2D.drawString(item.getStringAmount(), x * 170 + 115 + xAdder, y * 170 + (340 - 40 * Math.min(rows, 4)) + yAdder - itemScrollY);
                    }
                } catch (Exception ignored) {
                }
                x++;
            }
            y++;
        }
    }

    short getItemMenuLimit() {
        return (short) (rows * 170 + (225 - 40 * Math.min(rows, 4)) - itemScrollY - 40);
    }

    BufferedImage bingoCardImg;
    BufferedImage bingoCompletedImg;
    public BingoCard bingoCard = new BingoCard(this);

    public BufferedImage getDefaultBingoCard() {
        return toCompatibleImage(loadImg("/menu/bingo/card.png"));
    }

    public void redrawBingoCard() {
        bingoCardImg = getDefaultBingoCard();
        Graphics2D graphics2D = (Graphics2D) bingoCardImg.getGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        graphics2D.setColor(Color.black);
        graphics2D.setFont(comicSansBold25);

        byte i = 0;
        while(i < 16) {
            BingoTask task = bingoCard.getTasks()[i];
            if(task == null)
                return;

            byte x = (byte) (i % 4);
            byte y = (byte) (i / 4);

            if(task.isCompleted()) {
                graphics2D.drawImage(bingoCompletedImg, 15 + x * 129, 75 + y * 129, null);
            }

            if(task != BingoTask.NONE) {
                String raw = getString(task.toString().toLowerCase(Locale.ROOT) + "B");
                String[] rawLines = raw.split(" ");

                List<String> lines = new ArrayList<>();
                String currentLine = "";

                int j = 0;
                while (j < rawLines.length) {
                    currentLine += rawLines[j] + " ";

                    String lengthLine = currentLine;
                    if (rawLines[j].length() > 8) {
                        lengthLine = lengthLine.trim();
                    }
                    if (halfTextLength(graphics2D, lengthLine) > 25) {
                        lines.add(currentLine);
                        currentLine = "";
                    }
                    if (j == rawLines.length - 1) {
                        lines.add(currentLine);
                    }
                    j++;
                }

                int k = 0;
                for (String string : lines) {
                    graphics2D.drawString(string, 20 + x * 128, 100 + y * 128 + k * 28);
                    k++;
                }
            }

            i++;
        }

        graphics2D.dispose();
    }

    int achievementsScrollY = 0;
    int statisticsScrollY = 0;
    public float achievementPercentage = 0;
    int achievementsScrollX = 0;
    boolean shiftingAchievements = false;
    boolean achievementState = false; // false = achievements | true = statistics
    float achievementMargin = 0;
    boolean holdingAchievementSlider = false;

    void startSettings() {
        state = GameState.SETTINGS;
        sound.play("select", 0.1);

        staticTransparency = 0.02F;
        endStatic = 0.02F;

        recalculateButtons(GameState.SETTINGS);
        fadeOut(255, 150, 1);
    }

    public void startBingo() {
        if(bingoCard.isCompleted()) {
            bingoCard.completeAnimation();
        }
        state = GameState.BINGO;
        sound.play("select", 0.1);

        staticTransparency = 0.02F;
        endStatic = 0.02F;

        recalculateButtons(GameState.BINGO);
        fadeOut(255, 150, 1);
        redrawBingoCard();
    }

    void startAchievements() {
        achievementsScrollX = 0;
        shiftingAchievements = false;
        achievementState = false;
        achievementMargin = 0;

        state = GameState.ACHIEVEMENTS;
        sound.play("select", 0.1);

        staticTransparency = 0.02F;
        endStatic = 0.02F;

        recalculateButtons(GameState.ACHIEVEMENTS);
        fadeOut(255, 100, 2);

        for(InvestigationPaper paper : Investigation.list) {
            paper.checkForUnlock();
        }
        Investigation.checkForProgress();
        
        redrawAchievements();
    }

    void startChallengeMenu(boolean stopMusic) {
        if(!CustomNight.isCustom()) {
            CustomNight.setSelectedChallenge(CustomNight.getMaxChallenge());
            CustomNight.setEntityAIs();
        }
        keyHandler.isInEnemiesRectangle = false;
        state = GameState.CHALLENGE;
        sound.play("select", 0.1);

        if(stopMusic) {
            music.stop();
            music.play("tension", 0.05, true);
        } else {
            for (Item item : fullItemList) {
                isItemUsed.put(item, item.isSelected());
            }
        }

        staticTransparency = 0F;
        endStatic = 0F;

        recalculateButtons(GameState.CHALLENGE);
        fadeOut(255, 0, 2);

        CustomNight.startSelected = false;
        CustomNight.backSelected = false;
    }

    void startMusicMenu() {
        state = GameState.MUSIC_MENU;
        sound.play("select", 0.1);

        staticTransparency = 0F;
        endStatic = 0F;

        everySecond10th.put("rainbowText", () -> currentRainbow += 0.005F);
        recalculateButtons(GameState.MUSIC_MENU);
        fadeOut(255, 0, 2);

        CustomNight.startSelected = false;
        CustomNight.backSelected = false;
    }

    public void backToMainMenu() {
        currentFlicker = 0;
        goalFlicker = 0;
        everySecond10th.remove("rainbowText");
        
        switch (state) {
            case ITEMS -> {
                itemsMenu = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);

                for (Item item : fullItemList) {
                    isItemUsed.put(item, item.isSelected());
                }
            }
            case CHALLENGE, INVESTIGATION -> {
                music.stop();
                music.play(menuSong, 0.15, true);
            }
            case BINGO -> {
                if(!bingoCard.isCompleted() && !bingoCard.isFailed() && (bingoCard.isGenerated() || bingoCard.isGenerating())) {
                    bingoCard.setTimeGoing(true);
                }
            }
        }

        
        for (Item item : fullItemList) {
            if (item.isEnabled()) {
                item.disable();
            }
        }
        usedItems.clear();
        

        tintAlpha = 255;
        fadeOut(255, 160, 1);

        state = GameState.MENU;
        sound.play("select", 0.1);

        staticTransparency = 0.05F;
        endStatic = 0.05F;
        
        scrollX = 256;
        scrollY = 256;
        
        recalculateButtons(GameState.MENU);

        DiscordRichPresence rich = new DiscordRichPresence.Builder
                ("In Menu")
                .setDetails("PÉPITO RETURNED HOME")
                .setBigImage("menu", "PEPITO RETURNED HOME")
                .setSmallImage("pepito", "PEPITO RETURNED HOME")
                .setStartTimestamps(launchedGameTime)
                .build();

        DiscordRPC.discordUpdatePresence(rich);
    }
    
    public void startCorn() {
        sound.stop();
        music.stop();
        
        cornField3D = new CornField3D(keyHandler, sound);
        state = GameState.CORNFIELD;

        endStatic = 0;
        staticTransparency = 0;
        
        sound.play("cornfieldAmbient", 0.12, true);
        
        fadeOut(255, 0, 0.5F);

        keyHandler.defaultCursor = getCursor();
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        setCursor(blankCursor);
    }
    
    public void startPlatformer() {
        platformer = new Platformer(this, 100, 15);
        fadeOut(255, 0, 1);
        fadeOutStatic(0, 0, 0);

        music.stop();
        music.play("platformerSong", 0.12, true);

        state = GameState.PLATFORMER;

        hoveringPlatButton = false;
    }
    

    public void startInvestigation() {
        fadeOut(255, 0, 1);
        fadeOutStatic(0, 0, 0);

        music.stop();
        music.play("investigationSong", 0.12, true);

        state = GameState.INVESTIGATION;
    }


    public void fieldIntro() {
        sound.stop();
        
        field = new Field();
        field.generate(noise);
        
        field.a90 = new Hydrophobia90(this);

        field.lastImageBeforeField = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);
        field.lastImageBeforeField.setData(lastFullyRenderedUnshaded.getRaster());
        field.zoomCountdown = 1;
        
        fixedUpdatesAnim = 0;

        state = GameState.FIELD;

        music.stop();
        music.play("halfwayHallwayEnd", 0.2);

        field.cancelAfter.add(new Pepitimer(() -> {
            field.lightningStrike(this);
        }, 7000));
        
        
        int untilCarObjective = 10000;
        if(night != null) {
            if(night.env instanceof HChamber chamber) {
                if(chamber.isRespawnCheckpoint()) {
                    untilCarObjective = 1000;
                }
            }
        }

        field.cancelAfter.add(new Pepitimer(() -> {
            field.lockedIn = false;
        }, untilCarObjective));
        

        field.cancelAfter.add(new Pepitimer(() -> {
            sound.play("fieldSirens", 0.09F);
            sound.play("fieldHeavyRain", 0.175F, true);
        }, 11000));
        

        endStatic = 0;
        staticTransparency = 0;

        fadeOut(160, 0, 1.6F);

        DiscordRichPresence rich = new DiscordRichPresence.Builder
                ("In-Game")
                .setDetails("\"FIELD\" FIELD")
                .setBigImage("field", "PEPITO RETURNED HOME")
                .setSmallImage("pepito", "PEPITO RETURNED HOME")
                .setStartTimestamps(launchedGameTime)
                .build();

        DiscordRPC.discordUpdatePresence(rich);
    }
    
    
//    public void startField() {
//        field = new Field();
//        field.generate(noise);
//        
//        field.a90 = new Hydrophobia90(this);
//        
//        state = GameState.FIELD;
//
//        endStatic = 0;
//        staticTransparency = 0;
//        
//        fadeOut(255, 0, 1.6F);
//    }
    

    Level night;
    public Level getNight() {
        return night;
    }
    public EndlessGame endless;
    
    public DryCatGame dryCatGame;

    public byte usage = 0;

    public void startGame() {
        if(night != null) {
            stopEverything();
        }
        
        headphonesImg.setPath("/game/office/headphones/headphone.png");
        switch (type) {
            case SHADOW -> {
                if(shadowTicket.getAmount() >= 0) {
                    shadowTicket.setAmount(-1);
                }
                headphonesImg.setPath("/game/office/headphones/headphoneShadow.png");
            }
            case CUSTOM -> {
                birthdayHat.deselect();
                birthdayMaxwell.deselect();
                shadowTicket.deselect();
            }
            case HYDROPHOBIA -> headphonesImg.setPath("/game/office/headphones/headphoneHydro.png");
            case BASEMENT, BASEMENT_PARTY -> {
                if(Achievements.BASEMENT.isObtained() || Achievements.BASEMENT_PARTY.isObtained()) {
                    playedAfterBeatingBasement = true;
                }
                basementLadderFrames = 0;
                basementLadderHeld = false;
                basementLadderHovering = false;
            }
        }
        headphonesImg.reload();
        
      
        state = GameState.UNLOADED;

        timerY = -240;
        starlightMillis = 0;
        fadeOut(255, 255, 0);
        usage = 0;
        music.stop();
        basementSound.stop();
        sgAlpha = 0;
        sgGamma = 0;
        snowflakes.clear();
        hallucinations.clear();

        //reset all previous night's entities
        everySecond.remove("mirrorCat");
        
        console.power = true;

        
        byte nightNumber = currentNight;
        if(type == GameType.ENDLESS_NIGHT) {
            nightNumber = endless.getNight();
            if(nightNumber == 3) {
                sound.play("riftIndicator", 0.1);
                fixedUpdatesAnim = 80;
                riftIndicatorX = 1080;
            }
        }
        if(!type.isBasement() && type != GameType.HYDROPHOBIA) {
            if (type != GameType.PARTY && (birthdayHat.isSelected() || birthdayMaxwell.isSelected())) {
                type = GameType.PREPARTY;
                
                if(birthdayHat.isSelected()) {
                    if(!birthdayHat.isInfinite()) {
                        new Pepitimer(() -> {
                            new Notification(getString("infiniteBirthdayHats"));
                        }, 5000);
                    }
                }
            }
        }
        if (birthdayMaxwell.isSelected()) {
            nightNumber = 4;
            music.play("maxwellMusicBox", 0.1, true);
        }

        if(night != null) {
            night.resetTimers();
            night.getAstarta().stopService();
            //reset all previous night's entities here, too
        }
        night = new Level(this, type, nightNumber);

        int[] notificationDelay = {0};

        if(type == GameType.CLASSIC || type == GameType.PREPARTY || (type == GameType.ENDLESS_NIGHT && endless.getNight() == 1) || type == GameType.CUSTOM) {
            if(type == GameType.ENDLESS_NIGHT && endless.getNight() == 1) {
                if(neonSogSkips > 0) {
                    List<String> list = new ArrayList<>(List.of("Yellow", "Orange", "Green"));
                    Collections.shuffle(list);
                    
                    neonSogBallImage = new PepitoImage("/game/endless/neonSogBall" + list.get(0) + ".png");
                    neonSogBallSize = 180;
                    neonSogSkips--;
                    neonSogAnim = 1;

                    night.cancelAfterGame.add(new Pepitimer(() -> {
                        fadeOut((int) (tintAlpha), 255, 0.3F);

                        night.cancelAfterGame.add(new Pepitimer(() -> {
                            neonSogAnim = 2;
                            night.seconds += (short) type.getDuration();
                        }, 2000));
                    }, 4000));
                }
            }
            
            for (Item item : fullItemList) {
                if (item.isSelected()) {
                    item.enable();
                    if(!(type == GameType.CUSTOM && CustomNight.isCustom())) {
                        item.remove(1);
                    }
                    usedItems.add(item);

                    if(type == GameType.ENDLESS_NIGHT && endless.getNight() == 1) {
                        if(item.getTags().contains(ItemTag.RIFT)) {
                            riftItems.add(item);
                        }
                    }
                } else {
                    item.disable();
                }
            }
        }
        for(int i = 0; i < usedItems.size(); i++) {
            Item item = usedItems.get(i);
            item.enable();

            switch (item.getId()) {
                case "fan" -> fanStartup();
                case "bingoCard" -> {
                    unlockedBingo = true;
                    bingoCardItem.disable();
                    usedItems.remove(bingoCardItem);

                    new Pepitimer(() -> new Notification(getString("unlockedPepingo")), 2000);
                }
                case "soggyPen" -> {
                    fillSoggyPenCanvas();
                }
                case "shadowblocker" -> {
                    night.setShadowblocker(new Shadowblocker(1));
                }
            }
        }
        
        isItemUsed.clear();

        soggyBallpitActive = false;
        soggyBallpitCap = (short) (night.getDuration() * 0.6 + Math.random() * (night.getDuration() * 0.4));

        flashlightBrightness = 0;
        goalFlashlightBrightness = 0;
        holdingFlashlightFrames = 0;
        keyHandler.holdingFlashlight = false;
        flashlightOn = false;

        if(type == GameType.DAY) {
            pepitoClockProgress = 0;
            neonSogX = -400;
            keyHandler.hoveringNeonSog = false;
            keyHandler.hoveringNeonSogSign = false;
            keyHandler.hoveringPepitoClock = false;
            
            if(endless.getNight() == 4) {
                sound.play("weirdIdea", 0.1);
            }
            if(maxwell.isEnabled()) {
                int coinsAdd = (int) ((Math.random() * 42) + (Math.random() * endless.getNight() * 5));
                endless.addCoins(coinsAdd);

                sound.play("sellsYourBalls", 0.15);
                new Notification(getString("maxwellGenerated").replace("%d%", coinsAdd + ""));
                notificationDelay[0] += 1400;
            }
            for(byte i = 0; i < 2; i++) {
                if (corn[i].isEnabled() && corn[i].getStage() < 4) {
                    corn[i].increment();
                    if (corn[i].getStage() == 2) {
                        new Pepitimer(() -> new Notification(getString("cornGrew")), notificationDelay[0]);
                        notificationDelay[0] += 1000;
                    }
                    switch (corn[i].getStage()) {
                        case 1 -> corn[i].setImage(toCompatibleImage(loadImg("/game/items/cornStage2.png")));
                        case 2 -> corn[i].setImage(toCompatibleImage(loadImg("/game/items/cornStage3.png")));
                    }
                }
            }
        }


        if(keyHandler.freezeChange != null) {
            keyHandler.freezeChange.cancel();
        }
        freezeModifier = 1;
        universalGameSpeedModifier = originalGameSpeedModifier;
        allTimers.shutdown();
        startupTimers();
        

        if(inCam) {
            camOut(false);
        }
        console.clear();
        adBlocked = false;
        adblockerStatus = 0;
        sunglassesOn = false;
        inLocker = false;

        redrawUsage();
        resetFlood();
        currentWaterColor = new Color(0, 195, 255, 120);
        currentWaterColor2 = new Color(0, 140, 255, 180);
        wata.request();
        currentWaterImage = wata.request();
        
        metalPipeCooldown = 5;
        flashLightCooldown = 5;

        sound.play("select", 0.1);

        if(night.getType() == GameType.DAY) {
            announceDay();
            generateMillyItems();
        } else {
            announceNight(nightNumber, type);
        }
        
        if(!type.isEndless() && basementKey.isEnabled()) {
            night.basementKey();
        }

        staticTransparency = 0;
        endStatic = 0;

        recalculateButtons(GameState.GAME);
        repaintOffice();
        maxwellCounter = 0;

        loadA90Images();
        night.start();

        night.repaintMonitorImage();
        repaintOffice();
        
        if(isPepitoBirthday || isAprilFools) {
            balloons.add(new Balloon());
        }
        if(type == GameType.HYDROPHOBIA) {
            balloons.clear();
        }
        
        if(night.getType().isEndless()) {
            if(endless.getCoins() >= 1000) {
                AchievementHandler.obtain(this, Achievements.DABLOONS);
            }
        }

        if(usedItems.isEmpty()) {
            night.setItemless(true);
        }
        night.setUsedItemAmount((short) usedItems.size());
        
        if(volume == 0) {
            night.setSoundless(true);
        }

        final GameType finalType = type;

        new Pepitimer(() -> {
            short endValue = 200;
            float speed = 0.1F;

            switch (finalType) {
                case SHADOW, PREPARTY -> {
                    endValue = 120;
                    speed = 0.2F;
                }
                case DAY -> {
                    endValue = 100;
                    speed = 0.3F;
                }
                case BASEMENT -> {
                    endValue = 160;
                    speed = 0.2F;
                }
                case HYDROPHOBIA -> {
                    endValue = 180;
                    speed = 0.4F;
                }
            }
            if(basementKey.isEnabled()) {
                speed = 0.3F;
            }

            fadeOut(255, endValue, flashlight.isEnabled() ? speed * 2 : speed);

            state = GameState.GAME;
            recalculateButtons(GameState.GAME);

            if(shadowTicket.isEnabled()) {
                shadowCheckpointSelected = 0;
                shadowCheckpointUsed = 0;
                fadeOut(255, 180, 3);
                keyHandler.camSounds.play("shadowPortal", 0.05, true);
                usedItems.remove(shadowTicket);

                shadowTicketTimer = new Pepitimer(() -> {
                    riftAnimation(() -> {
                        shadowTicket.disable();
                        state = GameState.UNLOADED;
                        camOut(true);

                        GamePanel.mirror = true;
                        type = GameType.SHADOW;
                        fadeOutStatic(0, 0, 0);
                        state = GameState.HALFLOADED;
                        soggyBallpit.disable();
                        soggyBallpitActive = false;

                        startGame();

                        endless = null;
                        portalTransporting = false;
                        riftTint = 0;
                        portalActive = false;

                        if(shadowCheckpointUsed != 0) {
                            music.stop();
                        }
                        switch (shadowCheckpointUsed) {
                            case 1 -> night.seconds = 1300;
                            case 2 -> night.seconds = 1600;
                        }
                    });
                }, Achievements.HALFWAY.isObtained() ? 7000 : 3000);
            }
        }, 400);
        
        String details = "PEPITO RETURNED HOME";
        switch (night.getType()) {
            case CLASSIC -> details = "CLASSIC - NIGHT " + nightNumber;
            case ENDLESS_NIGHT -> details = "ENDLESS - NIGHT " + endless.getNight();
            case DAY -> details = "ENDLESS - DAY " + endless.getNight();
            case PREPARTY -> details = "PEPITO'S PARTY PREPARATIONS";
            case PARTY -> details = "PEPITO'S PARTY";
            case CUSTOM -> {
                if(CustomNight.isCustom()) {
                    details = "SIMULATION - CUSTOM NIGHT";
                } else {
                    details = "SIMULATION - " + CustomNight.getSelectedChallengeName().toUpperCase(Locale.ROOT);
                }
            }
            case SHADOW -> details = "SHADOWNIGHT - START";
            case BASEMENT -> {
                details = "BASEMENT";
            }
            case BASEMENT_PARTY -> {
                details = "BASEMENT - REAL DEAL";
            }
            case HYDROPHOBIA -> {
                details = "HYDROPHOBIA CHAMBER";
            }
        }

        DiscordRichPresence rich = new DiscordRichPresence.Builder
                ("In-Game")
                .setDetails(details)
                .setBigImage(night.getType().getDiscordID(), "PEPITO RETURNED HOME")
                .setSmallImage("pepito", "PEPITO RETURNED HOME")
                .setStartTimestamps(launchedGameTime)
                .build();

        DiscordRPC.discordUpdatePresence(rich);
    }

    Pepitimer shadowTicketTimer = null;
    Pepitimer basementEnterTimer = null;
    

    public Corn[] getCorn() {
        return corn;
    }

    public void generateMillyItems() {
        
        millyShopItems = new MillyItem[5];
        
        if(night.getType().isBasement()) {
            // BASEMENT 
            
            List<MillyItem> consumables = new ArrayList<>(List.of(
                    new MillyItem(miniSoda, 50, resize(miniSodaImg, 80, 100, BufferedImage.SCALE_SMOOTH)),
                    new MillyItem(freezePotion, 70, freezeImg),
                    new MillyItem(planks, 90, planksImg),
                    new MillyItem(soup, 112, soupItemImg),
                    new MillyItem(metalPipe, 60, metalPipeImg),
                    new MillyItem(soda, 90, sodaImg),
                    new MillyItem(flashlight, 55, flashlightImg),
                    new MillyItem(sensor, 140, sensor.getIcon()),
                    new MillyItem(fan, 120, fan.getIcon()),
                    new MillyItem(styroPipe, 20, styroPipe.getIcon()),
                    new MillyItem(iceBucket, 70, iceBucket.getIcon()),
                    new MillyItem(red40, 80, red40Img.request())
            ));
            if(Math.random() < 0.75) {
                consumables.add(new MillyItem(megaSoda, 150, resize(megaSoda.getIcon(), 147, 193, Image.SCALE_SMOOTH)));
            }

            consumables.removeIf(millyItem -> millyItem.getItem().isEnabled());

            Collections.shuffle(consumables);

            byte i = 0;
            while (i < 5 && i < consumables.size()) {
                if (!consumables.get(i).getItem().isEnabled()) {
                    millyShopItems[i] = consumables.get(i);
                }
                i++;
            }

            if(((Basement) night.env()).getStage() <= 1) {
                if (!pishPish.isEnabled()) {
                    int waterSprayIndex = (int) (Math.random() * 5);
                    millyShopItems[waterSprayIndex] = new MillyItem(pishPish, 75, pishPishImg.request());
                }
            }
            
            return;
        }
        
        
        // EVERYTHING BELOW IS FOR ENDLESS
        
        MillyItem larryKeys = new MillyItem(basementKey, 200 + endless.getNight() * 34, basementKey.getIcon());
        MillyItem larryPicture = new MillyItem(hisPicture, 0, hisPicture.getIcon());
        MillyItem larryPainting = new MillyItem(hisPainting, 3000, hisPainting.getIcon());
        MillyItem larryGlasses = new MillyItem(sunglasses, 500 + endless.getNight() * 150, sunglasses.getIcon());
        
        List<MillyItem> consumables = new ArrayList<>(List.of(
                new MillyItem(miniSoda, 20 * (endless.getNight() / 2 + 1), resize(miniSodaImg, 80, 100, BufferedImage.SCALE_SMOOTH)),
                new MillyItem(freezePotion, 40 * (endless.getNight() / 2 + 1), freezeImg),
                new MillyItem(planks, 60 * (endless.getNight() / 2 + 1), planksImg),
                new MillyItem(soup, 75 * (endless.getNight() / 2 + 1), soupItemImg),
                new MillyItem(metalPipe, 50 + endless.getNight() * 10, metalPipeImg),
                new MillyItem(soda, 60 * (endless.getNight() / 2 + 1), sodaImg),
                new MillyItem(flashlight, 50 + endless.getNight() * 5, flashlightImg),
                new MillyItem(soggyBallpit, 250 + endless.getNight() * 13, soggyBallpit.getIcon()),
                new MillyItem(corn[0], 120 + endless.getNight() * 15, corn[0].getIcon())
        ));
        if(endless.getNight() >= 2) {
            consumables.add(new MillyItem(maxwell, 100 + endless.getNight() * 25, maxwell.getIcon()));
            consumables.add(new MillyItem(sensor, 100 + endless.getNight() * 40, sensor.getIcon()));
            consumables.add(new MillyItem(fan, 120 + endless.getNight() * 10, fan.getIcon()));

            if(endless.getNight() >= 4) {
                consumables.add(new MillyItem(adblocker, 1050 + endless.getNight() * 50, adblocker.getIcon()));
                consumables.add(new MillyItem(corn[1], 360 + endless.getNight() * 45, corn[1].getIcon()));
                
                if(endless.getNight() >= 7) {
                    consumables.add(larryKeys);
                    consumables.add(larryPicture);
                    consumables.add(larryPainting);
                    consumables.add(larryGlasses);
                }
            }
        }
        consumables.removeIf(millyItem -> millyItem.getItem().isEnabled());

        Collections.shuffle(consumables);
        
        byte i = 0;
        while (i < 5 && i < consumables.size()) {
            if (!consumables.get(i).getItem().isEnabled()) {
                millyShopItems[i] = consumables.get(i);
            }
            i++;
        }

        MillyItem bHat = new MillyItem(birthdayHat, 410, resize(birthdayHatImg, 110, 200, BufferedImage.SCALE_SMOOTH));
        MillyItem bingoCard = new MillyItem(bingoCardItem, 500, bingoCardItem.getIcon());

        switch (endless.getNight()) {
            case 3 -> {
                int bHatIndex = (int) (Math.random() * 5);
                millyShopItems[bHatIndex] = bHat;

                if(!bingoCardItem.isEnabled() && !unlockedBingo && bingoCardItem.getAmount() == 0) {
                    int bingoCardIndex = (int) (Math.random() * 5);
                    while (bingoCardIndex == bHatIndex) {
                        bingoCardIndex = (int) (Math.random() * 5);
                    }

                    millyShopItems[bingoCardIndex] = bingoCard;
                }
            }
            case 6 -> {
                List<Integer> list = new ArrayList<>(List.of(0, 1, 2, 3, 4));
                Collections.shuffle(list);
                
                if(!basementKey.isEnabled()) {
                    millyShopItems[list.get(0)] = larryKeys;
                }
                millyShopItems[list.get(1)] = larryPicture;
                millyShopItems[list.get(2)] = larryPainting;
                millyShopItems[list.get(3)] = larryGlasses;
            }
            default -> {
                if(!manual.isEnabled()) {
                    millyShopItems[0] = new MillyItem(manual, 0, manual.getIcon());
                }
            }
        }
    }
    
    
    public void createCrate(int amount) {
        List<Item> firstDraft = new ArrayList<>();
        
        int i = 0;
        while(i < amount) {
            List<Item> pool = new ArrayList<>();
            pool.add(miniSoda);
            pool.add(planks);
            pool.add(soup);
            pool.add(freezePotion);
            pool.add(adblocker);
            pool.add(styroPipe);
            pool.add(maxwell);
            if(Math.random() < 0.6) {
                pool.add(megaSoda);
            }
            
            Collections.shuffle(pool);
            
            firstDraft.add(pool.get(0));
            
            i++;
        }
        
        crateRewards.clear();

        for(Item item : firstDraft) {
            if(crateRewards.containsKey(item)) {
                crateRewards.put(item, crateRewards.get(item) + 1);
            } else {
                crateRewards.put(item, 1);
            }
            
            item.safeAdd(1);
        }
        

        fadeOut(255, 0, 1);
        endStatic = 0;
        staticTransparency = 0;

        crateY = 0;
        crateItemDistance = 0.95F;

        state = GameState.CRATE;
    }
    
    

    public void fanStartup() {
        if(type == GameType.HYDROPHOBIA)
            return;
        
        keyHandler.fanSounds.play("fanSound", 0.25, true);
        sound.play("startFan", 0.15);
        usage++;
        redrawUsage();
        fanActive = true;

        everySecond20th.put("fan", () -> {
            fanDegrees += 46;
            rotatedFanBlade = rotate(fanImg[2], fanDegrees);
            redrawBHO = true;
        });
    }

    public void powerDown() {
        night.depower();
        console.power = false;
        sound.play("powerdown", 0.15);

        if(inCam) {
            camOut(true);
        }

        if(fanActive) {
            keyHandler.fanSounds.stop();
            sound.play("stopFan", 0.15);

            everySecond20th.remove("fan");
        }
        fanActive = false;

        byte newUsage = 0;
        for (Door door : night.getDoors().values()) {
            if (door.isClosed()) {
                if(night.getGeneratorEnergy() <= 0) {
                    door.setClosed(false);
                    sound.play("doorSlam", 0.08);

                    if(night.isTimerModifier()) {
                        night.getTimers().put(door, 0F);
                    }
                } else {
                    newUsage++;
                }
            }
        }

        if(night.getType() == GameType.SHADOW) {
            RepeatingPepitimer[] pepitimer = new RepeatingPepitimer[1];

            if(night.getEvent() == GameEvent.ASTARTA) {
                fadeOut((int) tintAlpha, 255, 1F);

                pepitimer[0] = new RepeatingPepitimer(() -> {
                    night.getAstartaBoss().jumpscareBrightness += 0.02F;
                    night.getAstartaBoss().jumpscareOffset -= 5;
                }, 50, 50);

                new Pepitimer(() -> {
                    pepitimer[0].cancel(false);
                    jumpscare("shadowAstarta", night.getId());
                    night.getAstartaBoss().jumpscareBrightness = 1F;
                    night.getAstartaBoss().jumpscareOffset = 0;
                }, 3000);
            } else {
                night.setShadowPepito(new ShadowPepito());

                pepitimer[0] = new RepeatingPepitimer(() -> {
                    night.getShadowPepito().jumpscareBrightness += 0.01F;
                    night.getShadowPepito().jumpscareOffset -= 3;
                }, 50, 50);

                new Pepitimer(() -> {
                    pepitimer[0].cancel(false);
                    jumpscare("shadowPepito", night.getId());
                    night.getShadowPepito().jumpscareBrightness = 1F;
                    night.getShadowPepito().jumpscareOffset = 0;
                }, 6000);
            }
        } else {
            fadeOut((int) tintAlpha, 235, 2);
        }

        usage = newUsage;
        redrawUsage();
    }

    public void lightsOn() {
        night.power();
        console.power = true;
        sound.play("lightsOn", 0.1);
        fadeOut(0, 150, 6);
    }

    public void camOut(boolean force) {
        if(inCam || force) {
            inCam = false;

            usage--;
        }
        redrawUsage();

        keyHandler.camSounds.stop();
        sound.play("camOut", 0.15);
        fadeOutStatic(0.3F, 0F, 0.01F);
    }

    int astartaJumpscareCounter = 0;
    boolean astartaJumpscareCount = false;
    String afterDeathText = "";
    String afterDeathCurText = "";

    public void jumpscare(String key, int nightId) {
        if(night.getId() != nightId)
            return;
        if(!night.getEvent().isInGame() || portalTransporting || state != GameState.GAME || (invincible && !key.equals("pause")))
            return;
        if(starlightMillis > 0 && !key.equals("pause")) {
            sound.play("reflectStarlight", 0.1F);
            return;
        }
        if(!key.equals("shadowPepito") && night.getShadowPepito() != null) {
            return;
        }
        if(night.getAstartaBoss() != null) {
            night.getAstartaBoss().reset();
        }

        jumpscareKey = key;
        
        stopAllSound();
        short miliseconds = 2000;
        deathScreenY = 0;
        afterDeathText = "";
        afterDeathCurText = "";
        redrawDeathScreen();

        shadowCheckpointSelected = 0;
        shadowCheckpointUsed = 0;
        if(shadowTicketTimer != null) {
            shadowTicketTimer.cancel();
        }
        if(basementEnterTimer != null) {
            basementEnterTimer.cancel();
            basementEnterTimer = null;
        }

        riftItems.clear();

        if(night.getType().isEndless() || type.isEndless()) {
            if(endless.getNight() > recordEndlessNight) {
                recordEndlessNight = endless.getNight();
                Statistics.ENDLESS.setValue(recordEndlessNight);
            }

            for(Item item : usedItems) {
                if(item.isEnabled()) {
                    if(item.getTags().contains(ItemTag.RIFT)) {
                        riftItems.add(item);
                    }
                }
            }
            usedItems.clear();
        }

        night.setEvent(GameEvent.DYING);
        everySecond10th.remove("energy");
        everySecond20th.remove("shark");
        night.getDsc().stopFighting();
        night.getElAstarta().stopService();

        Statistics.DEATHS.increment();

        switch (key) {
            case "pepito" -> {
                jumpscare = jumpscares[1].request();
                sound.play("pepitoScare", 0.35);
                killedBy = getString("kbPepito");

                switch ((byte) Math.round(Math.random())) {
                    case 0 -> tip = "Tip: You can hear Pépito's footsteps!";
                    case 1 -> tip = "Tip: You can hear the direction he comes from.";
                }

                if(Statistics.DIED_TO_PEPITO.getValue() == 0) {
                    afterDeathText = getString("pepitoDT1");
                } else if(Statistics.DIED_TO_PEPITO.getValue() == 1) {
                    afterDeathText = getString("pepitoDT2");
                } else if(Statistics.DIED_TO_PEPITO.getValue() > 1) {
                    afterDeathText = getString("pepitoDT3");
                }

                Statistics.DIED_TO_PEPITO.increment();
            }
            case "notPepito" -> {
                jumpscare = jumpscares[2].request();
                sound.play("notPepitoScare", 0.3);
                killedBy = getString("kbNotPepito");

                switch ((byte) Math.round(Math.random())) {
                    case 0 -> tip = "Tip: If you hear running, close the door immediately!";
                    case 1 -> tip = "Tip: Using your fan can reduce the chances of him.";
                }

                if(Statistics.DIED_TO_NOTPEPITO.getValue() == 0) {
                    afterDeathText = getString("notPepitoDT1");
                } else if(Statistics.DIED_TO_NOTPEPITO.getValue() == 1) {
                    afterDeathText = getString("notPepitoDT2");
                } else if(Statistics.DIED_TO_NOTPEPITO.getValue() > 1) {
                    afterDeathText = getString("notPepitoDT3");
                }

                Statistics.DIED_TO_NOTPEPITO.increment();

                miliseconds = 2550;
            }
            case "a90" -> {
                sound.play("a90Dead", 0.08);
                killedBy = getString("kbUncanny");

                byte randomTip = (byte) Math.round(Math.random());
                if(adblocker.getAmount() > 0) {
                    randomTip = (byte) Math.round(Math.random() * 2);
                }
                switch (randomTip) {
                    case 0 -> tip = "Tip: If you see him, stop moving or pressing keys.";
                    case 1 -> tip = "Tip: Taking your hands off the mouse might help.";
                    case 2 -> tip = "Tip: An adblocker might help.";
                }

                if(Statistics.DIED_TO_UNCANNY.getValue() == 0) {
                    afterDeathText = getString("uncannyDT1");
                } else if(Statistics.DIED_TO_UNCANNY.getValue() == 1) {
                    afterDeathText = getString("uncannyDT2");
                } else if(Statistics.DIED_TO_UNCANNY.getValue() > 1) {
                    afterDeathText = getString("uncannyDT3");
                }

                Statistics.DIED_TO_UNCANNY.increment();

                miliseconds = 2300;
            }
            case "msi" -> {
                killedBy = getString("kbMSI");
//                jumpscare = jumpscares[4].request();
                jumpscare = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

                switch ((byte) Math.round(Math.random() * 2)) {
                    case 0 -> tip = "Tip: Move your camera in the direction he tells you!";
                    case 1 -> tip = "Tip: Flashing him can save you.";
                    case 2 -> tip = "Tip: Disable the sensor, and he will never appear.";
                }

                if(Statistics.DIED_TO_MSI.getValue() == 0) {
                    afterDeathText = getString("msiDT1");
                } else if(Statistics.DIED_TO_MSI.getValue() == 1) {
                    afterDeathText = getString("msiDT2");
                } else if(Statistics.DIED_TO_MSI.getValue() > 1) {
                    afterDeathText = getString("msiDT3");
                }

                Statistics.DIED_TO_MSI.increment();
            }
            case "astarta" -> {
                jumpscare = jumpscares[3].request();
                sound.playRate("astartaAdSound", 0.2, 0.4);
                killedBy = getString("kbAstarta");

                switch ((byte) Math.round(Math.random())) {
                    case 0 -> tip = "Tip: Astarta's eyes glow! Close the door she looks at.";
                    case 1 -> tip = "Tip: Flashing Astarta makes her disappear immediately!";
                }

                if(Statistics.DIED_TO_ASTARTA.getValue() == 0) {
                    afterDeathText = getString("astartaDT1");
                } else if(Statistics.DIED_TO_ASTARTA.getValue() == 1) {
                    afterDeathText = getString("astartaDT2");
                } else if(Statistics.DIED_TO_ASTARTA.getValue() > 1) {
                    afterDeathText = getString("astartaDT3");
                }

                Statistics.DIED_TO_ASTARTA.increment();

                astartaJumpscareCount = true;
                astartaJumpscareCounter = 0;

                miliseconds = 4000;
            }
            case "colaCat" -> {
                jumpscare = resize(jumpscares[5].request(), 1080, 640, Image.SCALE_FAST);
                sound.play("colaJumpscare", 0.13);
                killedBy = getString("kbCola");

                switch ((byte) Math.round(Math.random() * 2)) {
                    case 0 -> tip = "Tip: The Soda starts changing colors! Drink it.";
                    case 1 -> tip = "Tip: Cola Cat appears from a combo of items, avoid them.";
                    case 2 -> tip = "Tip: Don't use Soda with a Metal Pipe or a Flashlight.";
                }

                if(Statistics.DIED_TO_COLACAT.getValue() == 0) {
                    afterDeathText = getString("colaDT1");
                } else if(Statistics.DIED_TO_COLACAT.getValue() == 1) {
                    afterDeathText = getString("colaDT2");
                } else if(Statistics.DIED_TO_COLACAT.getValue() > 1) {
                    afterDeathText = getString("colaDT3");
                }

                Statistics.DIED_TO_COLACAT.increment();

                miliseconds = 4800;
            }
            case "maki" -> {
                jumpscare = resize(jumpscares[6].request(), 1080, 640, Image.SCALE_FAST);
                sound.play("makiScare", 0.4);
                killedBy = getString("kbMaki");

                tip = "Tip: Look at the camera next time!";

                if(Statistics.DIED_TO_MAKI.getValue() == 0) {
                    afterDeathText = getString("makiDT1");
                } else if(Statistics.DIED_TO_MAKI.getValue() == 1) {
                    afterDeathText = getString("makiDT2");
                } else if(Statistics.DIED_TO_MAKI.getValue() > 1) {
                    afterDeathText = getString("makiDT3");
                }

                Statistics.DIED_TO_MAKI.increment();

                miliseconds = 1600;
            }
            case "shark" -> {
                switch ((byte) Math.round(Math.random())) {
                    case 0 -> jumpscare = resize(jumpscares[7].request(), 1080, 640, Image.SCALE_FAST);
                    case 1 -> jumpscare = resize(jumpscares[8].request(), 1080, 640, Image.SCALE_FAST);
                }

                sound.play("boop", 0.1);

                killedBy = getString("kbShark");

                switch ((byte) Math.round(Math.random() * 2)) {
                    case 0 -> tip = "Tip: Move your fish away from Shark!";
                    case 1 -> tip = "Tip: Use your Fan to \"skip\" the flood.";
                    case 2 -> tip = "Tip: Move your camera away!";
                }

                if(Statistics.DIED_TO_SHARK.getValue() == 0) {
                    afterDeathText = getString("sharkDT1");
                } else if(Statistics.DIED_TO_SHARK.getValue() == 1) {
                    afterDeathText = getString("sharkDT2");
                } else if(Statistics.DIED_TO_SHARK.getValue() > 1) {
                    afterDeathText = getString("sharkDT3");
                }

                Statistics.DIED_TO_SHARK.increment();
            }
            case "boykisser" -> {
                jumpscare = resize(jumpscares[9].request(), 1080, 640, Image.SCALE_FAST);

                sound.play("boop", 0.1);

                killedBy = getString("kbBoykisser");

                switch ((byte) Math.round(Math.random() * 2)) {
                    case 0 -> tip = "Tip: bro how did you die";
                    case 1 -> tip = "Tip: AND THE CROWD GOES MILD";
                    case 2 -> tip = "Tip: how";
                }

                afterDeathText = "In fact, the background is a burning Japanese city in Fukushima Prefecture. Due to the actions of the Japanese government, the Nuclear Power Plant will explode again, this time more seriously. All cities within a radius of 100 kilometers from the epicenter will burn, and radioactive fallout will fall. Due to climate change, there will be radioactive snow, which will be more dangerous, and tsunamis will play an important role in this. The cat on the right in the picture is a mutant, he has no mouth. And the one in the foreground is not jumping, he was thrown away due to the blast wave. The flash in the sky is a North Korean missile shot down by Japanese air defense.";
            }
            case "lemonadeCat" -> {
                jumpscare = resize(jumpscares[10].request(), 1080, 640, Image.SCALE_FAST);

                sound.playRate("makiSound", 0.2, 0.5);
                sound.playRate("boing", 0.1, 0.2);

                miliseconds = 4000;

                killedBy = getString("kbLemon");

                switch ((byte) Math.round(Math.random())) {
                    case 0 -> tip = "Tip: Throw the lemons at him!";
                    case 1 -> tip = "Tip: aim better";
                }

                if(Statistics.DIED_TO_LEMONADE_CAT.getValue() == 0) {
                    afterDeathText = getString("lemonDT1");
                } else if(Statistics.DIED_TO_LEMONADE_CAT.getValue() == 1) {
                    afterDeathText = getString("lemonDT2");
                } else if(Statistics.DIED_TO_LEMONADE_CAT.getValue() > 1) {
                    afterDeathText = getString("lemonDT3");
                }

                Statistics.DIED_TO_LEMONADE_CAT.increment();
            }
            case "a120" -> {

            }
            case "scaryCat" -> {
                killedBy = getString("kbScary");

                miliseconds = 5500;
                sound.play("scaryCatJumpscare", 0.2);
                jumpscare = resize(jumpscares[12].request(), 1080, 640, Image.SCALE_FAST);

                tip = "Tip: Move your camera around!";

                if(Statistics.DIED_TO_SCARY_CAT.getValue() == 0) {
                    afterDeathText = getString("scaryCatDT1");
                } else if(Statistics.DIED_TO_SCARY_CAT.getValue() == 1) {
                    afterDeathText = getString("scaryCatDT2");
                } else if(Statistics.DIED_TO_SCARY_CAT.getValue() > 1) {
                    afterDeathText = getString("scaryCatDT3");
                }

                Statistics.DIED_TO_SCARY_CAT.increment();
            }
            case "dread" -> {
                miliseconds = 1600;

                sound.playRate("dreadDead", 0.4, 0.8);
                killedBy = getString("kbDread");

                final float[] intensity = {31};
                RepeatingPepitimer[] pepitimer = new RepeatingPepitimer[1];

                BufferedImage resized = resize(jumpscares[11].request(), 1080, 640, Image.SCALE_FAST);

                pepitimer[0] = new RepeatingPepitimer(() -> {
                    jumpscare = vertWobble(resized, intensity[0], 4, 2, 1);
                    intensity[0] -= 0.5F;
                    if(jumpscare == null) {
                        pepitimer[0].cancel(false);
                    }
                }, 50, 50);

                afterDeathText = "Who...?? I do not know that entity.";

                Statistics.DIED_TO_DREAD.increment();
            }
            case "shadowAstarta" -> {
                miliseconds = 0;

                killedBy = getString("kbShadowAstarta");

                Statistics.DIED_TO_SHADOW_ASTARTA.increment();
            }
            case "shadowPepito" -> {
                miliseconds = 14000;

                fadeOut(255, 255, 0);
                jumpscare = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);

                RepeatingPepitimer[] pepitimer = new RepeatingPepitimer[1];
                pepitimer[0] = new RepeatingPepitimer(() -> {
                    if(jumpscare != null) {
                        Graphics2D graphics2D = (Graphics2D) jumpscare.getGraphics();

                        for(int x = 0; x < 113; x++) {
                            short y = (short) (Math.random() * 154);
                            Color color = new Color(jumpscares[0].request().getRGB(x, y));
                            graphics2D.setColor(color);
                            graphics2D.fillRect((x + 82) * 4, (y + 6) * 4, 4, 4);
                        }

                        graphics2D.dispose();
                    } else {
                        pepitimer[0].cancel(false);
                    }
                }, 2000, 15);

                new Pepitimer(() -> {
                    Graphics2D graphics2D = (Graphics2D) jumpscare.getGraphics();

                    for(int x = 0; x < 113; x++) {
                        for(int y = 0; y < 154; y++) {
                            Color color = new Color(jumpscares[0].request().getRGB(x, y));
                            graphics2D.setColor(color);
                            graphics2D.fillRect((x + 82) * 4, (y + 6) * 4, 4, 4);
                        }
                    }

                    graphics2D.dispose();
                    pepitimer[0].cancel(false);
                }, 10000);

                killedBy = getString("kbShadowPepito");

                Statistics.DIED_TO_SHADOW_PEPITO.increment();
            }
            case "elAstarta" -> {
                miliseconds = 5000;

                jumpscare = jumpscares[13].request();
                sound.play("elAstartaScare", 0.2);

                killedBy = getString("kbElAstarta");

                if(Statistics.DIED_TO_EL_ASTARTA.getValue() == 0) {
                    afterDeathText = getString("elAstartaDT1");
                } else if(Statistics.DIED_TO_EL_ASTARTA.getValue() == 1) {
                    afterDeathText = getString("elAstartaDT2");
                } else if(Statistics.DIED_TO_EL_ASTARTA.getValue() > 1) {
                    afterDeathText = getString("elAstartaDT3");
                }

                Statistics.DIED_TO_EL_ASTARTA.increment();
            }
            case "kiji" -> {
                killedBy = getString("kbKiji");
                miliseconds = 4000;
                sound.play("kijiKill", 0.1);
                night.getKiji().stop();

                fadeOut(255, 255, 0);

                jumpscare = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);
                BufferedImage deathtext = kijiJumpscares[(int) (Math.random() * kijiJumpscares.length)].request();
                
                RepeatingPepitimer[] pepitimer = new RepeatingPepitimer[1];
                pepitimer[0] = new RepeatingPepitimer(() -> {
                    if(jumpscare != null) {
                        jumpscare = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);
                        Graphics2D graphics2D = (Graphics2D) jumpscare.getGraphics();
                        
                        graphics2D.setColor(new Color(0, 40, 0));
                        graphics2D.setStroke(new BasicStroke(8));
                        
                        int y1 = (int) (Math.random() * 640F);
                        graphics2D.drawLine(0, y1, 1080, y1);
                        
                        graphics2D.drawImage(deathtext, 540 - deathtext.getWidth() / 2, 320 - deathtext.getHeight() / 2, null);
                        
                        graphics2D.dispose();
                    } else {
                        pepitimer[0].cancel(false);
                    }
                }, 15, 15);

                Statistics.DIED_TO_KIJI.increment();
            }
            case "shock" -> {
                killedBy = getString("kbShock");
                
                jumpscare = jumpscares[16].request();

                if(Statistics.DIED_TO_SHOCK.getValue() == 0) {
                    afterDeathText = getString("shockDT1");
                } else if(Statistics.DIED_TO_SHOCK.getValue() == 1) {
                    afterDeathText = getString("shockDT2");
                } else if(Statistics.DIED_TO_SHOCK.getValue() > 1) {
                    afterDeathText = getString("shockDT3");
                }
                
                Statistics.DIED_TO_SHOCK.increment();
            }
            case "dsc" -> {
                miliseconds = 8000;

                jumpscare = jumpscares[14].request();
                sound.play("dscJumpscare", 0.15);

                killedBy = getString("kbDsc");

                if(Statistics.DIED_TO_DEEP_SEA_CREATURE.getValue() == 0) {
                    afterDeathText = getString("dscDT1");
                } else if(Statistics.DIED_TO_DEEP_SEA_CREATURE.getValue() == 1) {
                    afterDeathText = getString("dscDT2");
                } else if(Statistics.DIED_TO_DEEP_SEA_CREATURE.getValue() > 1) {
                    afterDeathText = getString("dscDT3");
                }

                Statistics.DIED_TO_DEEP_SEA_CREATURE.increment();
            }
            case "hydrophobia" -> {
                night.getHydrophobia().setCurrentPos(50);
                miliseconds = 3000;
                fadeOut(0, 0, 0);
                
                sound.play("hydrophobiaJumpscare", 0.3);
                
                HChamber env = (HChamber) night.env();
                env.setShake(0);
                
                BufferedImage source = loadImg("/game/hydrophobia/hydrophobiaJumpscare.png");
                jumpscare = source;
                
                RepeatingPepitimer[] pepitimer = new RepeatingPepitimer[1];
                pepitimer[0] = new RepeatingPepitimer(() -> {
                    if(jumpscare != null) {
                        BufferedImage scare = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D graphics2D = (Graphics2D) scare.getGraphics();
                        
                        graphics2D.setColor(new Color((int) (Math.min(255, Math.max(0, Math.sin(fixedUpdatesAnim / 8F) * 127 + 127))), 0, 0));
                        graphics2D.fillRect(0, 0, 1080, 640);
                        graphics2D.drawImage(source, 0, 0, null);
                        graphics2D.dispose();
        
                        jumpscare = scare;
                    } else {
                        pepitimer[0].cancel(true);
                    }
                }, 15, 15);
                
                new Pepitimer(() -> {
                    pepitimer[0].cancel(true);
                    jumpscare = null;
                }, 3000);

                killedBy = getString("kbHydrophobia");
            }
            case "beast" -> {
                miliseconds = 3000;
                fadeOut(0, 0, 0);
                stopAllSound();
                
                everyFixedUpdate.remove("beastAttack");
                night.getBeast().setX(540);
                
                HChamber env = (HChamber) night.env();

                env.setShake(0);
                night.redrawHChamberTimer(env);

                jumpscare = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                
                killedBy = getString("kbBeast");
            }
            case "overseer" -> {
                night.getOverseer().active = false;
                miliseconds = 3000;
                fadeOut(0, 0, 0);
                
                BufferedImage scare = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics2D = (Graphics2D) scare.getGraphics();

                HChamber env = (HChamber) night.env();
                
                env.setShake(0);
                night.redrawHChamberTimer(env);

                BufferedImage image = resize(env.timerText, 482, 246, Image.SCALE_FAST);
                graphics2D.drawImage(bloom(image), 540 - image.getWidth() / 2, 320 - image.getHeight() / 2, null);
                graphics2D.drawImage(loadImg("/game/hydrophobia/overseerJumpscare.png"), 0, 0, null);
                graphics2D.dispose();
                
                jumpscare = scare;

                killedBy = getString("kbOverseer");
            }
            case "fieldObstacles" -> {
                killedBy = getString("kbObstacles");
                fadeOut(255, 255, 0);
            }
            case "fieldA90" -> {
                killedBy = getString("kbLandmine");
                fadeOut(255, 255, 0);
            }
            case "fieldBlimp" -> {
                killedBy = getString("kbBlimp");
                fadeOut(255, 255, 0);
            }
            case "radiation" -> {
                miliseconds = 0;
                killedBy = getString("kbRadiation");

                afterDeathText = getString("radiationDT");

                Statistics.DIED_TO_RADIATION.increment();
            }
            case "pause" -> {
                miliseconds = 0;
                killedBy = getString("kbGameDesign");

                Statistics.DEATHS.setValue(Statistics.DEATHS.getValue() - 1);
            }
            case "krunlic" -> {
                miliseconds = 100;
                killedBy = getString("kbKrunlic");

                jumpscare = jumpscares[15].request();
                sound.play("krunlicJumpscare", 0.15);

                if(krunlicPhase >= 2) {
                    sound.playRate("boop", 0.1, 0.5);
                    new AchievementNotification(getString("krunlicName"), getString("krunlicDesc"), krunlicAchievement.request());
                }
                
                Statistics.DEATHS.setValue(Statistics.DEATHS.getValue() - 1);
            }
        }

        if(pixelation > 3) {
            if((byte) Math.round(Math.random() * 2) == 0) {
                tip = "Tip: Did Glitcher ruin it? Don't use the camera TOO much.";

                if(adblocker.getAmount() > 0) {
                    if (Math.random() <= 0.5) {
                        tip = "Tip: An adblocker might help.";
                    }
                }
            }
        }

        if(birthdayMaxwell.isEnabled()) {
            birthdayMaxwell.safeAdd(1);
        }

        stopEverything();

        new Pepitimer(() -> {
            deathScreen();

            if(Statistics.DEATHS.getValue() >= 1) {
                AchievementHandler.obtain(this, Achievements.ONE_OF_MANY);

                if(Statistics.DEATHS.getValue() >= 10) {
                    AchievementHandler.obtain(this, Achievements.SKULLCRACKER);

                    if(Statistics.DEATHS.getValue() >= 100) {
                        AchievementHandler.obtain(this, Achievements.DARWIN_AWARD);

                        if(Statistics.DEATHS.getValue() >= 500) {
                            AchievementHandler.obtain(this, Achievements.KAMIKAZE);
                        }
                    }
                }
            }

            new Pepitimer(() -> pressAnyKey = true, key.equals("pause") ? 1500 : 3000);
            
            
            if(krunlicMode) {
                if(krunlicPhase == 0) {
                    krunlix();
                }
            }
            
        }, miliseconds);
    }

    public void redrawDeathScreen() {
        BufferedImage deathScreenText = new BufferedImage(456, 330, BufferedImage.TYPE_INT_ARGB);
        Graphics2D deathTextGraphics = (Graphics2D) deathScreenText.getGraphics();

        deathTextGraphics.setColor(new Color(0, 255, 0));

        deathTextGraphics.setFont(comicSans30);
        String[] splitText = cropText(afterDeathCurText, 448, deathTextGraphics);

        for(int i = 0; i < splitText.length; i++) {
            String part = splitText[i];
            deathTextGraphics.drawString(part, 10 - i / 4, 30 + i * 30);
        }

        deathTextGraphics.dispose();
        this.deathScreenText = deathScreenText;
    }

    
    private void drawFieldA90(Graphics2D graphics2D) {
        Hydrophobia90 a90 = field.a90;
        
        if (a90.animation > 0) {
            if(a90.animation > 3) {
                Point a90Point = new Point(a90.x + 100, a90.y + 100);

                for (int i = 0; i < 54; i++) {
                    for (int j = 0; j < 32; j++) {
                        Point pixel = new Point(i * 20, j * 20);
                        double distance = pixel.distance(a90Point);

                        if(distance < a90.distance + 20) {
                            if(distance < a90.distance) {
                                int rand = (int) (Math.random() * 30);
                                graphics2D.setColor(new Color(5, rand, rand + 70));
                            } else {
                                if(distance < a90.distance + 10) {
                                    int rand = (int) (Math.random() * 30);
                                    graphics2D.setColor(new Color(5, rand, rand + 70, 100));
                                } else {
                                    int rand = (int) (Math.random() * 30);
                                    graphics2D.setColor(new Color(5, rand, rand + 70, 50));
                                }
                            }
                            graphics2D.fillRect(i * 20, j * 20, 20, 20);
                        }
                    }
                }
            }

            for(int p = 0; p < a90.points.size(); p++) {
                Point point = a90.points.get(p);
                int xShakeEach = (int) (Math.random() * 20 - 10);
                graphics2D.setColor(new Color(10, 30, 100));
                graphics2D.fillRect(point.x - 90 + xShakeEach, point.y - 25, 180, 50);
            }
            for(int p = 0; p < a90.points.size(); p++) {
                Point point = a90.points.get(p);
                int xShakeEach = (int) (Math.random() * 20 - 10);
                graphics2D.setColor(Color.BLUE);
                graphics2D.fillRect(point.x - 70 + xShakeEach, point.y - 20, 140, 40);
            }

            int a90X = a90.x;
            int a90Y = a90.y;
            if (a90.animation >= 1) {
                graphics2D.drawImage(fieldCanny, a90X, a90Y, null);
                if (a90.animation == 5) {
                    graphics2D.drawImage(fieldUncanny[anim], a90X - 50, a90Y - 50, null);
                }
            }
        }
        if(a90.drawStopSign) {
            graphics2D.drawImage(fieldA90StopSign.request(), a90.x - 50, a90.y - 50, null);

            if(a90.shots == 0) {
                graphics2D.drawImage(fieldA90WarningSign.request(), a90.x - 20, a90.y - 20, 250, 220, null);
            }
        }
    }
    
    short winCount = 0;
    private short deathCount = 0;

    public void win() {
        stopAllSound();
        manualClose();

        console.clear();
        console.timer = 1;

        fadeOut(255, 255, 1);
        pixelation = 1;
        night.getA90().animation = 0;

        stopEverything();
        night.setEvent(GameEvent.WINNING);

        everySecond10th.remove("energy");
        everySecond20th.remove("shark");
        night.getDsc().stopFighting();
        
        everySecond10th.put("rainbowText", () -> currentRainbow += 0.005F);
        
        winCount++;
        Statistics.WINS.increment();

        if(night.getType() == GameType.CUSTOM && CustomNight.isCustom()) {
            int points = CustomNight.getPoints();
            
            CustomNight.visualPointsProgress = 0;
            if(points > 0) {
                new Pepitimer(() -> {
                    everySecond10th.put("visualPoints", () -> {
                        CustomNight.visualPointsProgress += 0.02F;
                        sound.playRate("customGetPoint", 0.15, 1 + CustomNight.visualPointsProgress);
                        if (CustomNight.visualPointsProgress >= 0.96) {
                            everySecond10th.remove("visualPoints");
                            
                            new Pepitimer(() -> {
                                CustomNight.visualPointsProgress = 1;
                                sound.play("boop", 0.08);
                                sound.playRate("customGetPoint", 0.15, 2);
                                sound.playRate("shitscream", 0.12, 3);
                            }, 650);
                        }
                    });
                }, 3000);
            }
            
            Statistics.POINTS_TOTAL.setValue(Statistics.POINTS_TOTAL.getValue() + points);
            if(points > Statistics.POINTS_MAX.getValue()) {
                Statistics.POINTS_MAX.setValue(points);
            }

            if(Statistics.POINTS_TOTAL.getValue() >= 100000) {
                AchievementHandler.obtain(this, Achievements.WHOLE_LOTTA_POINTS);
            }
        }

        music.play("chime", 0.15);

        tip = "";
        
        if(night.isSoundless()) {
            BingoHandler.completeTask(BingoTask.BEAT_WITHOUT_SOUND);
        }

        if(!type.isParty()) {
            if(type == GameType.CLASSIC) {
                switch (currentNight) {
                    case 1 -> {
                        AchievementHandler.obtain(this, Achievements.BEGINNER);
                        BingoHandler.completeTask(BingoTask.BEAT_NIGHT_1);
                    }
                    case 2 -> {
                        AchievementHandler.obtain(this, Achievements.ADVANCER);
                        BingoHandler.completeTask(BingoTask.BEAT_NIGHT_2);
                    }
                    case 3 -> {
                        AchievementHandler.obtain(this, Achievements.CONQUEROR);
                        BingoHandler.completeTask(BingoTask.BEAT_NIGHT_3);
                    }
                    case 4 -> {
                        AchievementHandler.obtain(this, Achievements.ALL_NIGHTER);

                        if(night.isItemless()) {
                            AchievementHandler.obtain(this, Achievements.BROKE);
                        }
                    }
                }
                if(currentNight > 1) {
                    if(night.getUsedItemAmount() <= 2) {
                        BingoHandler.completeTask(BingoTask.BEAT_WITH_LESS_ITEMS);

                        if(night.getUsedItemAmount() <= 1) {
                            BingoHandler.completeTask(BingoTask.BEAT_WITH_LESS_LESS_ITEMS);

                            if(night.getUsedItemAmount() == 0) {
                                BingoHandler.completeTask(BingoTask.BEAT_WITH_LESS_LESS_LESS_ITEMS);
                            }
                        }
                    }
                }

                if (currentNight >= 4) {
                    currentNight = 1;
                    currentRainbow = 0;
                    allNighter = true;
                } else {
                    currentNight++;
                }
            } else if(type == GameType.CUSTOM) {
                if(!CustomNight.isCustom()) switch (CustomNight.getSelectedChallenge()) {
                    case 0 -> AchievementHandler.obtain(this, Achievements.EL_ASTARTA);
                    case 1 -> AchievementHandler.obtain(this, Achievements.BLIZZARD);
                    case 2 -> AchievementHandler.obtain(this, Achievements.TIME_IS_TICKING);
                    case 3 -> AchievementHandler.obtain(this, Achievements.THE_FOG_IS_COMING);
                    case 4 -> AchievementHandler.obtain(this, Achievements.GRUGGENHEIMED);
                }
            }
        } else {
            if(type.isBasement()) {
                if(type == GameType.BASEMENT) {
                    AchievementHandler.obtain(this, Achievements.BASEMENT);
                } else if(type == GameType.BASEMENT_PARTY) {
                    AchievementHandler.obtain(this, Achievements.BASEMENT_PARTY);
                    musicDiscs.add("spookers");

                    if(((Basement) night.env).beenToHydrophobiaChamber) {
                        AchievementHandler.obtain(this, Achievements.BASEMENT_100);
                    }
                }
            } else {
                if (type == GameType.PREPARTY) {
                    AchievementHandler.obtain(this, Achievements.PREPARTY);
                } else if (type == GameType.PARTY) {
                    AchievementHandler.obtain(this, Achievements.PARTY);
                    musicDiscs.add("pepitoButCooler");
                    birthdayMaxwell.safeAdd(1);
                }
                if (birthdayHat.getAmount() >= 0) {
                    birthdayHat.setAmount(-1);
                }
            }
        }

        everySecond10th.remove("energy");

        gg = false;
        byte random = (byte) (Math.random() * 100 / Math.min(100, Math.max(winCount, 1)));
        if(random <= 0) {
            gg = true;
        }

        new Pepitimer(() -> {
            pressAnyKey = true;

            sound.play("boop", 0.02);
        }, 11000);

        save();
    }

    public void winSequence() {
        if(type == GameType.CUSTOM) {
            customWin();
        } else {
            win();
        }
    }


    // next two are for customnight
    BufferedImage lastWinScreen = null;
    BufferedImage greenWinScreen = null;

    public void customWin() {
        lastWinScreen = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_ARGB);
        Graphics2D preGraphics = (Graphics2D) lastWinScreen.getGraphics();
        preGraphics.drawImage(lastFullyRenderedUnshaded, 0, 0, null);
        preGraphics.dispose();

        greenWinScreen = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) greenWinScreen.getGraphics();
        graphics.setComposite(GreenifyComposite.Greenify);
        graphics.drawImage(resize(lastFullyRenderedUnshaded, 540, 320, Image.SCALE_FAST), 0, 0, 1080, 640,null);
        graphics.dispose();

        sound.stop();
        sound.play("stopSimulation", 0.1);
        music.stop();

        float[] sway = new float[] {0.02F};

        night.setEvent(GameEvent.WINNING);

        everySecond20th.put("stopSimulation", () -> {
            if(sway[0] < 1.5F) {
                sway[0] *= 1.075F;
            }
            BufferedImage newImage = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = (Graphics2D) newImage.getGraphics();

            graphics2D.setColor(Color.BLACK);
            graphics2D.fillRect(0, 0, 1080, 640);

            graphics2D.drawImage(greenWinScreen, 0, 0, 1080, 640, null);
            graphics2D.drawImage(lastWinScreen, 27 + (int) (14 * sway[0] * Math.cos(fixedUpdatesAnim * 0.05)), 16 + (int) (8 * sway[0] * Math.sin(fixedUpdatesAnim * 0.05)), 1026, 608, null);
            graphics2D.setColor(new Color(0, 0, 0, 40));
            graphics2D.fillRect(0, 0, 1080, 640);

            graphics2D.setColor(black80);
            graphics2D.fillRoundRect(145, 250, 800, 80, 40, 40);
            graphics2D.setColor(Color.GREEN);
            graphics2D.setFont(yuGothicPlain80);
            String stopping = "stopping simulation...";
            graphics2D.drawString(stopping, 540 - halfTextLength(graphics2D, stopping), 320);

            graphics2D.dispose();
            lastWinScreen = newImage;
        });

        startSimulationTimer = new Pepitimer(() -> {
            win();
            everySecond20th.remove("stopSimulation");
        }, 8950);
    }

    private boolean allNighter = false;

    private void stopEverything() {
        console.clear();

        resetFlood();
        manualClose();

        night.setTemperature(0);

        fadeOutStatic(0, 0, 0.05F);

        inCam = false;
        
        if(fanActive) {
            keyHandler.fanSounds.stop();
            sound.play("stopFan", 0.15);

            everySecond20th.remove("fan");
        }
        fanActive = false;
    }

    void doorTimerStuff(Door door) {
        if(!door.isClosed()) {
            usage++;
        }
        door.setClosed(true);
        float start = 0;
        if(getNight().getTimers().containsKey(door)) {
            start = getNight().getTimers().get(door);
        }
        night.getTimers().put(door, start + 4F);
        timerY = 100;
        sound.play("timerStart", 0.1);

        everyFixedUpdate.put("timerDecrease", () -> {
            if(getNight() != null) {
                for(Door timerDoor : getNight().getTimers().keySet().stream().toList()) {
                    float newFloat = getNight().getTimers().get(timerDoor) - 0.01666F;
                    if(newFloat > 0) {
                        getNight().getTimers().put(timerDoor, newFloat);
                    } else {
                        getNight().getTimers().remove(timerDoor);
                        timerDoor.setClosed(false);
                        usage--;
                        redrawUsage();

                        int doorPos = (int) (timerDoor.getHitbox().getBounds().x + timerDoor.getHitbox().getBounds().width / 2F);
                        float pan = (float) Math.sin(1.57 * (doorPos / 740F - 1)) / 1.3F;
                        sound.play("doorSlam", 0.08, pan);
                    }
                }

                if (getNight().getTimers().isEmpty()) {
                    timerY -= 4;
                    if (timerY < -230) {
                        everyFixedUpdate.remove("timerDecrease");
                        timerY = -230;
                    }
                }
            } else {
                everyFixedUpdate.remove("timerDecrease");
            }
        });
    }

    private BufferedImage riftTransition = new BufferedImage(108, 64, BufferedImage.TYPE_INT_ARGB);
    float riftTransparency = 0F;
    String riftText = "";
    private float riftY = 0;
    float riftMoonAlpha = 0;
    int riftFramesDoingNothing = 0;
    private int riftCounter = 0;
    Item selectedRiftItem = null;
    List<Item> riftItems = new ArrayList<>();
    Item[] riftItemsSelected = new Item[2];
    boolean portalTransporting = false;
    public boolean portalActive = false;
    public boolean outOfLuck = false;

    void riftAnimation(Runnable runnable) {
        final short[] x = {0};
        final byte[] run = {0};
        riftTransparency = 1F;
        final boolean[] yetToChange = {true};
        riftText = "";
        riftItemsSelected = new Item[2];

        final Graphics2D[] graphics2D = {riftTransition.createGraphics()};
        graphics2D[0].setColor(Color.WHITE);

        everySecond20th.put("riftTransition", () -> {
            if(run[0] < 2) {
                graphics2D[0].drawLine(0, x[0], x[0], 0);
                graphics2D[0].drawLine(0, x[0] + 2, x[0] + 2, 0);
                x[0] += 4;
            }

            if (run[0] == 2) {
                if(yetToChange[0]) {
                    yetToChange[0] = false;
                    runnable.run();
                }
                if (riftTransparency > 0) {
                    riftTransparency -= 0.05F;
                } else {
                    riftTransparency = 0F;
                    riftTransition = new BufferedImage(108, 64, BufferedImage.TYPE_INT_ARGB);
                    everySecond20th.remove("riftTransition");
                }
            }

            if (x[0] >= 170) {
                x[0] = 1;
                run[0]++;
            }
        });
    }

    void enterRift() {
        riftItems = riftItems
                .stream()
                .distinct()
                .collect(Collectors.toList());
        
        selectedRiftItem = riftItems.get(0);
        riftTint = 0;
        riftFramesDoingNothing = 0;
        riftMoonAlpha = 0;
        riftY = 0;

        riftAnimation(() -> {
            try {
                stopGame(false);
            } catch (Exception ignored) {
            }
            stopAllSound();
            staticTransparency = 0F;
            endStatic = 0F;

            riftCounter = 0;
            state = GameState.RIFT;

            new Pepitimer(() -> {
                final byte[] currentIndex = {0};
                final float[] acceleration = {1};

                RepeatingPepitimer[] timer = new RepeatingPepitimer[1];
                timer[0] = new RepeatingPepitimer(() -> {
                    if (currentIndex[0] < 20) {
                        riftText += "Welcome to the rift!".charAt(currentIndex[0]);
                        if (!String.valueOf("Welcome to the rift!".charAt(currentIndex[0])).equals(" ")) {
                            sound.playRate("select", 0.1, 0.75);
                        }
                        currentIndex[0]++;
                    } else {
                        timer[0].cancel(false);

                        new Pepitimer(() -> everyFixedUpdate.put("riftAccelerateUp", () -> {
                            riftY += acceleration[0] / 3;
                            acceleration[0] *= 1.033F;

                            if (riftY > 640) {
                                everyFixedUpdate.remove("riftAccelerateUp");
                                music.play("orca", 0.06, true);

                                everyFixedUpdate.put("riftAccelerateDown", () -> {
                                    riftY += acceleration[0] / 3;
                                    acceleration[0] *= 0.966F;

                                    if (riftY > 1280) {
                                        everyFixedUpdate.remove("riftAccelerateDown");
                                    }
                                });
                            }
                        }), 1500);
                    }
                }, 150, 150);
            }, 1000);
        });
    }

    short riftTint = 0;

    private void drawRift(Graphics2D graphics2D) {
        if(riftY < 640) {
            graphics2D.drawImage(riftImg, 0, (short) (riftY), null);
        }
        if(riftY > 1280 && riftMoonAlpha > 0) {
            graphics2D.setComposite(AlphaComposite.SrcOver.derive(riftMoonAlpha));
            graphics2D.drawImage(riftMoon.request(), 0, (int) (-1280 + riftY), null);
            graphics2D.setComposite(AlphaComposite.SrcOver);
            
            graphics2D.setFont(comicSans40);
            graphics2D.setColor(new Color(128, 0, 255, (int) (riftMoonAlpha * 255)));

            graphics2D.drawString(getString("riftArrowsToChange"), 540 - halfTextLength(graphics2D, getString("riftArrowsToChange")), -1280 + 580 + riftY);
            graphics2D.drawString(getString("riftSpaceToSelect"), 540 - halfTextLength(graphics2D, getString("riftSpaceToSelect")), -1280 + 620 + riftY);
        }
        
        graphics2D.setColor(Color.WHITE);
        graphics2D.setFont(comicSans80);
        graphics2D.drawString(riftText, 540 - halfTextLength(graphics2D, riftText), 620 + riftY);

        String selectTwo = getString("selectTwo");
        int center = 540 - halfTextLength(graphics2D, selectTwo);
        
        graphics2D.setColor(new Color(20, 20, 20));
        graphics2D.drawString(selectTwo, center, -1280 + 320 + riftY);
        graphics2D.setColor(new Color(60, 60, 60));
        graphics2D.drawString(selectTwo, center, -1280 + 280 + riftY);
        graphics2D.setColor(new Color(100, 100, 100));
        graphics2D.drawString(selectTwo, center, -1280 + 240 + riftY);
        graphics2D.setColor(new Color(160, 160, 160));
        graphics2D.drawString(selectTwo, center, -1280 + 200 + riftY);
        graphics2D.setColor(new Color(200, 200, 200));
        graphics2D.drawString(selectTwo, center, -1280 + 160 + riftY);

        graphics2D.drawImage(riftFrame, 540 - 160, (int) (-1280 + 210 - 130 + riftY + Math.sin(riftCounter * 0.02) * 10), null);

        if(riftItemsSelected[1] == null) {
            graphics2D.drawImage(selectedRiftItem.getIcon(), 445, (int) (-1280 + 250 - 130 + riftY + Math.sin(riftCounter * 0.02) * 10), 190, 190, null);

            graphics2D.drawString(selectedRiftItem.getName(), 540 - halfTextLength(graphics2D, selectedRiftItem.getName()),
                    (int) (-1280 + 420 + riftY + Math.sin(riftCounter * 0.02) * 6));

            graphics2D.setFont(comicSans40);
            byte i = 0;
            while (i < selectedRiftItem.getDescription().split("\n").length) {
                graphics2D.drawString(selectedRiftItem.getDescription().split("\n")[i], 540 - halfTextLength(graphics2D, selectedRiftItem.getDescription().split("\n")[i]),
                        (int) (-1280 + 460 + riftY + Math.sin(riftCounter * 0.02) * 6) + i * 40);
                i++;
            }
        } else {
            graphics2D.setComposite(AlphaComposite.SrcOver.derive(0.4F));
            graphics2D.drawImage(riftItemsSelected[1].getIcon(), 795, 35, 570, 570, null);
        }
        if(riftItemsSelected[0] != null) {
            graphics2D.setComposite(AlphaComposite.SrcOver.derive(0.4F));
            graphics2D.drawImage(riftItemsSelected[0].getIcon(), -285, 35, 570, 570, null);
        }

        graphics2D.setComposite(AlphaComposite.SrcOver.derive(1F));
    }


    boolean manualFirstButtonHover = false;
    boolean manualSecondButtonHover = false;

    void manualOpen() {
        if(!everyFixedUpdate.containsKey("manualMove") && manualY < 640) {
            // open
            everyFixedUpdate.put("manualMove", () -> {
                if (manualY > 264) {
                    manualY -= (int) ((manualY - 260) * 0.2);
                } else {
                    everyFixedUpdate.remove("manualMove");
                    manualY = 260;
                    recalcManualButtons();
                }
            });
        }
    }
    
    void manualHide() {
        if(!everyFixedUpdate.containsKey("manualMove") && manualY < 640) {
            manualSpawn();
        }
    }
    
    public void manualSpawn() {
        everyFixedUpdate.put("manualMove", () -> {
            if (manualY < 531) {
                manualY -= (int) ((manualY - 535) * 0.2);
            } else {
                everyFixedUpdate.remove("manualMove");
                manualY = 535;
                recalcManualButtons();
            }
        });
    }
    
    public void manualBetterSpawn(String string) {
        manualSpawn();
        if(string.isEmpty()) {
            manualText = new ArrayList<>();
            return;
        }
        manualText = sectionManualText(string);
    }
    
    void manualClose() {
        if(!everyFixedUpdate.containsKey("manualMove") && manualY < 640) {
            everyFixedUpdate.put("manualMove", () -> {
                if (manualY < 636) {
                    manualY -= (int) ((manualY - 640) * 0.2);
                } else {
                    everyFixedUpdate.remove("manualMove");
                    manualY = 640;
                    recalcManualButtons();
                }
            });
        }
    }
    
    private List<String> sectionManualText(String input) {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = (Graphics2D) image.getGraphics();

        graphics2D.setFont(comicSans30);
        List<String> strings = Arrays.asList(cropText(input, 400, graphics2D));

        graphics2D.dispose();
        return strings;
    }


    public static Polygon getPolygon(List<Point> list) {
        Polygon polygon = new Polygon();
        for(Point p : list) {
            polygon.addPoint(p.x, p.y);
        }
        return polygon;
    }

    private void deathScreen() {
        System.out.println("the pyramid #2 has been activated");
        
        mirror = false;
        tintAlpha = 255;
        fadeOut(255, 255, 0);
        if(type == GameType.HYDROPHOBIA) {
            fadeOut(255, 0, 0.6F);

            System.out.println(((HChamber) night.env).getDeaths() + " DEATHS");
        }
        pixelation = 1;
        night.getA90().animation = 0;
        sound.play("vineboom", 0.08);

        console.clear();

        jumpscare = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        astartaJumpscareCount = false;

        deathCount++;

        drawCat = true;
    }

    int checkItemsAmount() {
        short amount = 0;
        for(Item item : fullItemList) {
            if(item.isSelected()) {
                amount++;
            }
        }

        return amount;
    }

    private List<Item> getItemsWithAmount() {
        List<Item> items = new ArrayList<>();
        for(Item item : fullItemList) {
            if(item.getAmount() != 0) {
                items.add(item);
            }
        }

        return items;
    }

    private boolean drawCat = false;
    int deathScreenY = 0;
    boolean pressAnyKey = false;

    void stopGame(boolean toMenu) {
        basementHyperOptimization = false;
        
        white200 = new Color(255, 255, 255, 200);
        reloadMenuButtons();
        inCam = false;

        if(toMenu) {
            state = GameState.MENU;
            recalculateButtons(state);

            if(night.getEvent() == GameEvent.DYING) {
                if(bingoCard.isTimeGoing()) {
                    if(bingoCard.secondsSpent > 1200) {
                        boolean hasAnythingUncompleted = bingoCard.isTaskUncompleted(BingoTask.BEAT_WITHOUT_POWER) || bingoCard.isTaskUncompleted(BingoTask.BEAT_WITHOUT_SOUND) ||
                                bingoCard.isTaskUncompleted(BingoTask.BEAT_NIGHT_1) || bingoCard.isTaskUncompleted(BingoTask.BEAT_NIGHT_2) ||
                                bingoCard.isTaskUncompleted(BingoTask.BEAT_NIGHT_3) || bingoCard.isTaskUncompleted(BingoTask.BEAT_WITH_LESS_ITEMS) ||
                                bingoCard.isTaskUncompleted(BingoTask.BEAT_WITH_LESS_LESS_ITEMS) || bingoCard.isTaskUncompleted(BingoTask.BEAT_WITH_LESS_LESS_LESS_ITEMS);

                        if(hasAnythingUncompleted) {
                            bingoCard.fail();
                        }
                    }
                }
            }

            DiscordRichPresence rich = new DiscordRichPresence.Builder
                    ("In Menu")
                    .setDetails("PEPITO RETURNED HOME")
                    .setBigImage("menu", "PEPITO RETURNED HOME")
                    .setSmallImage("pepito", "PEPITO RETURNED HOME")
                    .setStartTimestamps(launchedGameTime)
                    .build();

            DiscordRPC.discordUpdatePresence(rich);

            
            for (Item item : fullItemList) {
                if (item.isEnabled()) {
                    item.disable();
                }
            }
            usedItems.clear();
        }
        
        type = GameType.CLASSIC;
        rainSound.stop();
        if(shadowTicketTimer != null) {
            shadowTicketTimer.cancel();
        }
        if(basementEnterTimer != null) {
            basementEnterTimer.cancel();
            basementEnterTimer = null;
        }

        night.resetTimers();

        allNighter = false;
        everySecond10th.remove("rainbowText");
        everyFixedUpdate.remove("timerDecrease");

        soggyBallpitActive = false;
        everySecond.remove("mirrorCat");
        night.getWires().stopService();
        night.getMaki().stopService();
        night.getAstarta().stopService();
        night.getBoykisser().reset();
        night.getColaCat().stopService();
        night.getScaryCat().leave();
        night.getElAstarta().stopService();
        night.getKiji().stopTimer();
        night.getShock().stopTimer();
        night.getOverseer().stopTimer();
        
        stopAllSound();
        music.play(menuSong, 0.15, true);

        resetFlood();

        staticTransparency = 0.05F;
        endStatic = 0.05F;

        snowflakes.clear();
        
        flashlightBrightness = 0;
        goalFlashlightBrightness = 0;
        holdingFlashlightFrames = 0;
        keyHandler.holdingFlashlight = false;
        flashlightOn = false;
        
        
        if(keyHandler.freezeChange != null) {
            keyHandler.freezeChange.cancel();
        }
        freezeModifier = 1;
        universalGameSpeedModifier = originalGameSpeedModifier;
        allTimers.shutdown();
        startupTimers();
        

        drawCat = false;
        night.setEvent(GameEvent.NONE);
        pressAnyKey = false;

        jumpscare = null;

        fadeOut(255, 160, 1.2F);
    }

    public void unholdMister(Mister mister) {
        mister.setBeingHeld(false);
        setCursor(keyHandler.defaultCursor);

        float oldModifier = night.getAstartaBoss().getSpeedModifier();
        night.getAstartaBoss().setSpeedModifier(1F);
        for (MediaPlayer player : music.clips) {
            player.setRate(player.getRate() / oldModifier);
        }

        try {
            Robot robot = new Robot();
            int h = -mister.getPoint().x - offsetX + 1080 + 250;
            int x = (int) ((mirror ? h : 1080 - h) * widthModifier + centerX);
            int y = (int) ((mister.getPoint().y + 100) * heightModifier + centerY);
            robot.mouseMove(window.getX() + x + 10, window.getY() + y + 30);
        } catch (AWTException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void fillSoggyPenCanvas() {
        int width = 1080 + night.env().maxOffset();
        night.soggyPenCanvas = new BufferedImage(width, 640, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = (Graphics2D) night.soggyPenCanvas.getGraphics();
        graphics2D.setColor(Color.WHITE);
        graphics2D.fillRect(0, 0, width, 640);

        graphics2D.dispose();
    }

    public static String getString(String key) {
        if(krunlicMode) {
            if(krunlicPhase >= 3) {
                if(!key.toLowerCase(Locale.ROOT).contains("krunlic")) {
                    float s = Math.min(Math.max(0, krunlicSeconds), 50) / 100F;
                    
                    if(GamePanel.disableFlickering) {
                        s /= 3;
                        if (Math.random() < s) {
                            languageText.put(key, languageText.get("krunlic"));
                            return languageText.get("krunlic").replace("%n", "\n");
                        }
                    } else {
                        if (Math.random() < s) {
                            return languageText.get("krunlic").replace("%n", "\n");
                        }
                    }
                }
            }
        }
        
        return languageText.get(key).replace("%n", "\n");
    }
    String language = "english";
    
    public void loadLanguage(String language) {
        this.language = language;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/languages/" + language + ".txt"), StandardCharsets.UTF_8));
            LinkedList<String> lines = new LinkedList<String>();
            String readLine;

            while ((readLine = in.readLine()) != null) {
                lines.add(readLine);
            }

            String[] array = lines.toArray(new String[0]);

            languageText.clear();
            for (String row : array) {
                String[] split = row.split(":");

                if(split.length == 2) {
                    String text = split[1];

                    if(row.endsWith(":")) {
                        text += ":";
                    }
                    languageText.put(split[0], text);
                } else {
                    StringBuilder connect = new StringBuilder();
                    for(int i = 1; i < split.length; i++) {
                        connect.append(split[i]);
                        if(i != split.length - 1) {
                            connect.append(":");
                        }
                    }
                    if(row.endsWith(":")) {
                        connect.append(":");
                    }

                    languageText.put(split[0], connect.toString());
                }
            }
        } catch (IOException ignored) { }
    }

    void stopAllSound() {
        sound.stop();
        music.stop();
        keyHandler.fanSounds.stop();
        keyHandler.camSounds.stop();
        scaryCatSound.stop();
        generatorSound.stop();
        rainSound.stop();
        basementSound.stop();
        shockSound.stop();
    }
    
    void pauseAllSound(boolean gamePause) {
        sound.pause(gamePause);
        music.pause(gamePause);
        keyHandler.fanSounds.pause(gamePause);
        keyHandler.camSounds.pause(gamePause);
        scaryCatSound.pause(gamePause);
        generatorSound.pause(gamePause);
        rainSound.pause(gamePause);
        basementSound.pause(gamePause);
        shockSound.pause(gamePause);
    }
    
    void resumeAllSound(boolean gameResume) {
        sound.resume(gameResume);
        music.resume(gameResume);
        keyHandler.fanSounds.resume(gameResume);
        keyHandler.camSounds.resume(gameResume);
        scaryCatSound.resume(gameResume);
        generatorSound.resume(gameResume);
        rainSound.resume(gameResume);
        basementSound.resume(gameResume);
        shockSound.resume(gameResume);
    }
    
    public void stopBasementSong() {
        basementSound.stop();
    }
    
    public void restartBasementSong() {
        if(type.isBasement()) {
            Basement basement = (Basement) night.env();

            basementSound.stop();

            switch (basement.getStage()) {
                case 0 -> basementSound.play("basementTheme1", 0.1, true);
                case 1 -> basementSound.play("basementTheme2", 0.1, true);
                case 2 -> basementSound.play("basementTheme3", 0.1, true);
                case 3 -> basementSound.play("basementTheme4", 0.1, true);
                case 5 -> basementSound.play("basementTheme5", 0.1, true);
                case 6 -> basementSound.play("basementTheme6", 0.15, false);
            }
        }
    }

    private short startFade = 255;

    public void fadeOut(int start, int end, float interval) {
        this.intervalFade = interval;
        this.endFade = (short) end;
        this.tintAlpha = start;
        this.startFade = (short) start;
    }

    public void fadeOutStatic(float start, float end, float interval) {
        this.staticTransparency = start;
        this.endStatic = end;
        this.intervalStatic = interval;
        redrawCurrentStaticImg();
    }
    
    public static BufferedImage getRandomBalloon() {
        if(krunlicMode) {
            if(krunlicPhase == 5) {
                return loadImg("/game/entities/krunlic/krunlicScaryBlack.png");
            }
        }
        return randomColor(balloonImg);
    }
    
    public void krunlix() {
        krunlicPhase = 1;

        new Pepitimer(() -> {
            krunlicPhase = 2;
            music.stop();

            new Pepitimer(() -> {
                krunlicPhase = 3;

                krunlicSound.stop();
                krunlicSound.play("krunlic1", 0.1, true);

                new Pepitimer(() -> {
                    krunlicPhase = 4;

                    krunlicEyes.add(new Point((int) (254 + Math.random() * 572), (int) (117 + Math.random() * 406)));
                    krunlicEyes.add(new Point((int) (254 + Math.random() * 572), (int) (117 + Math.random() * 406)));

                    krunlicSound.stop();
                    krunlicSound.play("krunlic2", 0.1, true);

                    new Pepitimer(() -> {
                        krunlicSound.stop();
                        krunlicSound.play("krunlic3", 0.1, true);

                        new Pepitimer(() -> {
                            krunlicPhase = 5;

                            krunlicSound.stop();
                            krunlicSound.play("krunlic4", 0.1, true);

                            new Pepitimer(() -> {
                                krunlicPhase = 6;
                                stopAllSound();
                                krunlicSound.stop();
                                allTimers.shutdown();
                                for(Pepitimer timer : StaticLists.timers) {
                                    timer.cancel();
                                }
                                fadeOutStatic(0, 0, 0);
                                krunlicEyes.clear();

                                state = GameState.KRUNLIC;
                                repaint();

                                new Pepitimer(() -> {
                                    krunlicSound.play("krunlic5", 0.1);

                                    new Pepitimer(() -> {
                                        Path path = Paths.get(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "\\krunlic message.txt");
                                        try {
                                            Files.createFile(path);
                                            Writer output;
                                            File file = new File(path.toString());
                                            output = new BufferedWriter(new FileWriter(file));

                                            output.write("press LEFT RIGHT LEFT RIGHT LEFT RIGHT LEFT RIGHT in the main menu =)");
                                            output.close();
                                        } catch (Exception ignored) { }
                                        
                                        everGotKrunlicFile = true;
                                        save();
                                        
                                        System.exit(0);
                                    }, 6110);
                                }, 20000);
                            }, 21250);

                        }, 120000);

                    }, 9640);
                }, 90000);
            }, 75000);
        }, 150000);

        every6s.remove("formatChange");
    }
    
    public void wait7Seconds(int millis) {
        untilNextCheck = 10000;

        pauseAllSound(true);
        sound.play("untitled", 0.1);
        GameState previous = state;
        state = GameState.UH_OH;

        allTimers.shutdown();
        startupTimers();

        new Pepitimer(() -> {
            state = previous;
            resumeAllSound(true);

            for(MediaPlayer player : sound.clips) {
                if(player.getMedia().getSource().contains("untitled.mp3")) {
                    player.stop();
                    player.dispose();
                    sound.clips.remove(player);
                }
            }
        }, millis);
    }




    public float mathRound(float round) {
        return Math.round(round * 1000F) * 0.001F;
    }

    public int halfTextLength(Graphics2D graphics2D, String string) {
        return (int) (graphics2D.getFontMetrics().stringWidth(string) * 0.5);
    }
    public int textLength(Graphics2D graphics2D, String string) {
        return graphics2D.getFontMetrics().stringWidth(string);
    }

    private BufferedImage rotateINEFFICIENTBUTSMOOTHANDCUTSOFF(BufferedImage image, int degrees, boolean trim) {
        if(degrees == 0) {
            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage rotated = new BufferedImage(width * 2, height * 2, BufferedImage.TYPE_INT_ARGB);
            Graphics2D a = (Graphics2D) rotated.getGraphics();
            a.drawImage(image, width / 2, height / 2, null);
            a.dispose();

            if(trim) {
                return trimImage(rotated);
            } else {
                return trimImageRightBottom(rotated);
            }
        }

        double theta = Math.toRadians(degrees);
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage rotated = new BufferedImage(width * 2, height * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D a = (Graphics2D) rotated.getGraphics();
        a.drawImage(image, width / 2, height / 2, null);
        a.dispose();

        AffineTransform tx = AffineTransform.getRotateInstance(theta, width, height);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

        if(trim) {
            return trimImage(op.filter(rotated, null));
        } else {
            return trimImageRightBottom(op.filter(rotated, null));
        }
    }
    
    BufferedImage rotate(BufferedImage image, int degrees) {
        if(degrees == 0)
            return image;
        
        int w = image.getWidth();
        int h = image.getHeight();
        double toRad = Math.toRadians(degrees);
        int hPrime = (int) (w * Math.abs(Math.sin(toRad)) + h * Math.abs(Math.cos(toRad)));
        int wPrime = (int) (h * Math.abs(Math.sin(toRad)) + w * Math.abs(Math.cos(toRad)));
        
        if(wPrime <= 0)
            wPrime = 1;
        if(hPrime <= 0)
            hPrime = 1;

        BufferedImage rotatedImage = new BufferedImage(wPrime, hPrime, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = rotatedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.translate(wPrime / 2, hPrime / 2);
        g.rotate(toRad);
        g.translate(- w / 2, - h / 2);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        
        return rotatedImage;
    }

    private BufferedImage rotateRadians(BufferedImage image, double toRad, boolean antialiasing) {
        if(toRad == 0)
            return image;
        
        int w = image.getWidth();
        int h = image.getHeight();
        int hPrime = (int) (w * Math.abs(Math.sin(toRad)) + h * Math.abs(Math.cos(toRad)));
        int wPrime = (int) (h * Math.abs(Math.sin(toRad)) + w * Math.abs(Math.cos(toRad)));

        if(wPrime <= 0)
            wPrime = 1;
        if(hPrime <= 0)
            hPrime = 1;

        BufferedImage rotatedImage = new BufferedImage(wPrime, hPrime, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = rotatedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialiasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

        g.translate(wPrime / 2, hPrime / 2);
        g.rotate(toRad);
        g.translate(- w / 2, - h / 2);
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return rotatedImage;
    }
    

    public BufferedImage makeDemotivator() {
        BufferedImage image = new BufferedImage(125, 150, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = (Graphics2D) image.getGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        BufferedImage bufferedImage = loadImg("/game/basement/demotivators/" + (int) (Math.random() * 18) + ".png");
        graphics2D.drawImage(bufferedImage, 10, 10, 105, 85, null);

        graphics2D.setColor(Color.WHITE);
        graphics2D.setStroke(new BasicStroke(2));
        graphics2D.drawRect(5, 5, 114, 94);
        graphics2D.drawRect(5, 5, 114, 94);

        ArrayList<String> titles = new ArrayList<>(List.of("SUCCESS", "SIGMA", "TEAMWORK", "SOLUTION", "LIBERTY", "FREEDOM", "WISDOM", "CHAOS", "ATMOSPHERIC IGNITION", "REMEMBER", "WAR", "MANGOES"));
        Collections.shuffle(titles);
        graphics2D.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        graphics2D.drawString(titles.get(0), 62 - halfTextLength(graphics2D, titles.get(0)), 125);

        ArrayList<String> subtitles = new ArrayList<>(List.of("i shat in the urinal", "larry zhou", "no skubriks allowed", "skibidi toilet", "mr.chibert is watching", "drink water", "its begun", "lemonade get", "held captive", "aware and cautious", "explode", "never ends", "still water"));
        Collections.shuffle(subtitles);
        graphics2D.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        graphics2D.drawString(subtitles.get(0), 62 - halfTextLength(graphics2D, subtitles.get(0)), 142);

        return image;
    }

    
    private BufferedImage itemOffset(BufferedImage image, int x, int y) {
        // what the fuck is this
        BufferedImage offsetted = new BufferedImage(image.getWidth() + x, image.getHeight() + y, BufferedImage.TYPE_INT_ARGB);
        Graphics2D a = (Graphics2D) offsetted.getGraphics();
        a.drawImage(image, x, y, null);
        a.dispose();

        return image;
    }

    public BufferedImage offset(BufferedImage image, int x, int y) {
        // gonna optimize with w subimage sometime
        BufferedImage offsetted = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D a = (Graphics2D) offsetted.getGraphics();
        a.drawImage(image, x, y, null);
        a.dispose();

        return offsetted;
    }

    public static BufferedImage trimImage(BufferedImage image) {
        WritableRaster raster = image.getAlphaRaster();
        int width = raster.getWidth();
        int height = raster.getHeight();
        int left = 0;
        int top = 0;
        int right = width - 1;
        int bottom = height - 1;
        int minRight = width - 1;
        int minBottom = height - 1;

        top:
        for (;top <= bottom; top++){
            for (int x = 0; x < width; x++){
                if (raster.getSample(x, top, 0) != 0){
                    minRight = x;
                    minBottom = top;
                    break top;
                }
            }
        }

        left:
        for (;left < minRight; left++){
            for (int y = height - 1; y > top; y--){
                if (raster.getSample(left, y, 0) != 0){
                    minBottom = y;
                    break left;
                }
            }
        }

        bottom:
        for (;bottom > minBottom; bottom--){
            for (int x = width - 1; x >= left; x--){
                if (raster.getSample(x, bottom, 0) != 0){
                    minRight = x;
                    break bottom;
                }
            }
        }

        right:
        for (;right > minRight; right--){
            for (int y = bottom; y >= top; y--){
                if (raster.getSample(right, y, 0) != 0){
                    break right;
                }
            }
        }

        return image.getSubimage(left, top, right - left + 1, bottom - top + 1);
    }

    public static BufferedImage trimImageRightBottom(BufferedImage image) {
        WritableRaster raster = image.getAlphaRaster();
        int width = raster.getWidth();
        int height = raster.getHeight();
        int left = 0;
        int top = 0;
        int right = width - 1;
        int bottom = height - 1;
        int minRight = width - 1;
        int minBottom = height - 1;

        top:
        for (;top <= bottom; top++){
            for (int x = 0; x < width; x++){
                if (raster.getSample(x, top, 0) != 0){
                    minRight = x;
                    minBottom = top;
                    break top;
                }
            }
        }

        left:
        for (;left < minRight; left++){
            for (int y = height - 1; y > top; y--){
                if (raster.getSample(left, y, 0) != 0){
                    minBottom = y;
                    break left;
                }
            }
        }

        bottom:
        for (;bottom > minBottom; bottom--){
            for (int x = width - 1; x >= 0; x--){
                if (raster.getSample(x, bottom, 0) != 0){
                    minRight = x;
                    break bottom;
                }
            }
        }

        right:
        for (;right > minRight; right--){
            for (int y = bottom; y >= 0; y--){
                if (raster.getSample(right, y, 0) != 0){
                    break right;
                }
            }
        }

        return image.getSubimage(0, 0, right + 1, bottom + 1);
    }
    

    public BufferedImage wobble(BufferedImage source, float intensity, int precision, float speed, float size) {
        //SPEED IS USUALLY 0.02
        BufferedImage newImage = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics2D newGraphics = (Graphics2D) newImage.getGraphics();
        for(int x = 0; x < source.getWidth(); x += precision) {
            int sineX = x;
            if(precision > 1) {
                int sum = 0;
                for(int i = 1; i < precision + 1; i++) {
                    sum += i;
                }
                sineX += sum / precision;
            }
            int y = (int) (Math.sin((sineX * size) * 0.02 + fixedUpdatesAnim * speed) * intensity);
            newGraphics.drawImage(source.getSubimage(x, Math.max(-y, 0), precision, source.getHeight() - Math.abs(y)), x, Math.max(y, 0), null);
        }
        newGraphics.dispose();

        return newImage;
    }

    public BufferedImage vertWobble(BufferedImage source, float intensity, int precision, float speed, float size) {
        //SPEED IS USUALLY 0.02
        BufferedImage newImage = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics2D newGraphics = (Graphics2D) newImage.getGraphics();
        for(int y = 0; y < source.getHeight(); y += precision) {
            int sineY = y;
            if(precision > 1) {
                int sum = 0;
                for(int i = 1; i < precision + 1; i++) {
                    sum += i;
                }
                sineY += sum / precision;
            }
            int x = (int) (Math.sin((sineY * size) * 0.02 + fixedUpdatesAnim * speed) * intensity);
            newGraphics.drawImage(source.getSubimage(Math.max(-x, 0), y, source.getWidth() - Math.abs(x), Math.min(precision, source.getHeight() - y)), Math.max(x, 0), y, null);
        }
        newGraphics.dispose();

        return newImage;
    }

    public BufferedImage redify(BufferedImage img) {
        BufferedImage image = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        short x = 0;
        while (x < image.getWidth()) {
            short y = 0;
            while (y < image.getHeight()) {
                Color color = new Color(img.getRGB(x, y), true);

                if(color.getAlpha() > 0) {
                    Color newColor = new Color(255, color.getGreen(), color.getBlue(), color.getAlpha());
                    image.setRGB(x, y, newColor.getRGB());
                }
                y++;
            }
            x++;
        }

        return image;
    }

    public BufferedImage redify2(BufferedImage img) {
        BufferedImage image = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        short x = 0;
        while (x < image.getWidth()) {
            short y = 0;
            while (y < image.getHeight()) {
                Color color = new Color(img.getRGB(x, y), true);

                if(color.getAlpha() > 0) {
                    Color newColor = new Color((color.getRed() + color.getGreen() + color.getBlue()) / 3, 0, 0, color.getAlpha());
                    image.setRGB(x, y, newColor.getRGB());
                }
                y++;
            }
            x++;
        }

        return image;
    }
    

    public BufferedImage scratch(BufferedImage img, int intensity, int precision) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics2D graphics2D = (Graphics2D) result.getGraphics();

        for (int x = 0; x < img.getWidth(); x += precision) {
            int h = (int) (intensity * Math.random());
            graphics2D.drawImage(img.getSubimage(x, 0, 1, 640 - h), x, 0, precision, 640, null);
        }
        graphics2D.dispose();

        return result;
    }

    public BufferedImage xor(BufferedImage img, Color color) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics2D graphics2D = (Graphics2D) result.getGraphics();

        graphics2D.drawImage(img, 0, 0, null);
        graphics2D.setXORMode(color);
        graphics2D.fillRect(0, 0, 1080, 640);
        graphics2D.dispose();

        return result;
    }
    
    public void applyHydrophobiaFilter(Graphics2D graphics2D, int width) {
        if(night == null)
            return;
        if(night.getType() != GameType.HYDROPHOBIA)
            return;
        if(night.getHydrophobia().getAILevel() <= 0)
            return;
        
        Hydrophobia hydrophobia = night.getHydrophobia();
        int secondsLeft = (Math.max(0, hydrophobia.distance() - 1)) * 25 + hydrophobia.getSecondsUntilStep();
        if(hydrophobia.distance() == 0) {
            secondsLeft = 0;
        }
        
        if(secondsLeft < 25) {
            graphics2D.setComposite(MultiplyComposite.Multiply);
            graphics2D.setColor(new Color(255, (int) (secondsLeft / 25F * 255F), (int) (secondsLeft / 25F * 255F)));
            graphics2D.fillRect(0, 0, width, 640);
            graphics2D.setComposite(AlphaComposite.SrcOver.derive(1F));
        }
    }


    public BufferedImage lightify(BufferedImage img) {
        BufferedImage image = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        short x = 0;
        while (x < image.getWidth()) {
            short y = 0;
            while (y < image.getHeight()) {
                Color color = new Color(img.getRGB(x, y), true);

                if(color.getAlpha() > 0) {
                    Color newColor = new Color(Math.min(255, 160 + color.getRed()), Math.min(255, 160 + color.getGreen()), Math.min(255, 160 + color.getBlue()), color.getAlpha());
                    image.setRGB(x, y, newColor.getRGB());
                }
                y++;
            }
            x++;
        }

        return image;
    }

    public static BufferedImage purplify(BufferedImage img) {
        BufferedImage image = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        Graphics2D graphics2D = (Graphics2D) image.getGraphics();
        graphics2D.setComposite(PurplifyComposite.Purplify);
        graphics2D.drawImage(img, 0, 0, null);
        graphics2D.dispose();
        
        return image;
    }

    public BufferedImage advancedPurplify(BufferedImage img) {
        BufferedImage image = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        Graphics2D graphics2D = (Graphics2D) image.getGraphics();
        graphics2D.setComposite(AdvancedPurplify.AdvancedPurplify);
        graphics2D.drawImage(img, 0, 0, null);
        graphics2D.dispose();

        return image;
    }

    public static BufferedImage darkify(BufferedImage img, int times) {
        BufferedImage image = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        float newTimes = 1F / times;

        short x = 0;
        while (x < image.getWidth()) {
            short y = 0;
            while (y < image.getHeight()) {
                Color color = new Color(img.getRGB(x, y), true);

                if(color.getAlpha() > 0) {
                    Color newColor = new Color((int) (color.getRed() * newTimes), (int) (color.getGreen() * newTimes), (int) (color.getBlue() * newTimes), color.getAlpha());
                    image.setRGB(x, y, newColor.getRGB());
                }
                y++;
            }
            x++;
        }

        return image;
    }

    public BufferedImage alphaify(BufferedImage source, float transparency) {
        if(transparency >= 1)
            return source;
        
        BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        if(transparency <= 0) {
            return image;
        }
        
        Graphics2D graphics2D = (Graphics2D) image.getGraphics();

        graphics2D.setComposite(AlphaComposite.SrcOver.derive(transparency));
        graphics2D.drawImage(source, 0, 0, null);

        graphics2D.dispose();
        return image;
    }

    // only works with rgb for now
    public BufferedImage contrast(BufferedImage source, float brightness, int offset) {
        BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        
        Graphics2D graphics2D = (Graphics2D) image.getGraphics();

        graphics2D.setComposite(new ContrastComposite(brightness, offset));
        graphics2D.drawImage(source, 0, 0, null);

        graphics2D.dispose();
        return image;
    }
    
    public BufferedImage yellowContrast(BufferedImage source, float brightness, int offset) {
        BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics2D = (Graphics2D) image.getGraphics();

        graphics2D.setComposite(new YellowContrastComposite(brightness, offset));
        graphics2D.drawImage(source, 0, 0, null);

        graphics2D.dispose();
        return image;
    }

    

    BufferedImage grayscale(BufferedImage source) {
        BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D a = (Graphics2D) image.getGraphics();
        a.drawImage(source, 0, 0, null);
        a.dispose();

        BufferedImage image2 = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics2D b = (Graphics2D) image2.getGraphics();
        b.drawImage(image, 0, 0, null);
        b.dispose();

        return image2;
    }

    public static BufferedImage silhouette(BufferedImage img, Color color) {
        int newType = BufferedImage.TYPE_INT_RGB;
        if(img.getColorModel().hasAlpha()) {
            newType = BufferedImage.TYPE_INT_ARGB;
        }
        BufferedImage image = new BufferedImage(img.getWidth(), img.getHeight(), newType);

        Graphics2D graphics2D = (Graphics2D) image.getGraphics();
        graphics2D.drawImage(img, 0, 0, null);
        graphics2D.setComposite(ReplaceComposite.Replace);
        graphics2D.setColor(color);
        graphics2D.fillRect(0, 0, img.getWidth(), img.getHeight());
        graphics2D.dispose();

        return image;
    }


    /** @noinspection SameParameterValue*/
    public static BufferedImage resize(BufferedImage image, int width, int height, int hints) {
        if(width == image.getWidth() && height == image.getHeight()) {
            return image;
        }
        
        BufferedImage buffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D a = (Graphics2D) buffered.getGraphics();
        if(hints == 4) {
            a.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }
        a.drawImage(image, 0, 0, width, height, null);
        a.dispose();

        return buffered;
    }

    private BufferedImage resize(BufferedImage image, int width, int height, int hints, int type) {
        if(width == image.getWidth() && height == image.getHeight()) {
            return image;
        }
        
        BufferedImage buffered = new BufferedImage(width, height, type);

        Graphics2D a = (Graphics2D) buffered.getGraphics();
        if(hints == Image.SCALE_SMOOTH) {
            a.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }
        a.drawImage(image, 0, 0, width, height, null);
        a.dispose();

        return buffered;
    }


    /** @noinspection SameParameterValue*/
    public static BufferedImage mirror(BufferedImage image, int type) {
        if(type == 0) {
            return image;
        }

        BufferedImage mirrored = new BufferedImage(image.getWidth(), image.getHeight(), image.getType() == 0 ? 2 : image.getType());
        Graphics2D g2d = mirrored.createGraphics();
        switch (type) {
            case 1 -> {
                g2d.scale(-1, 1);
                g2d.translate(-image.getWidth(), 0);
            }
            case 2 -> {
                g2d.scale(-1, -1);
                g2d.translate(-image.getWidth(), -image.getHeight());
            }
            case 3 -> {
                g2d.scale(1, -1);
                g2d.translate(0, -image.getHeight());
            }
        }
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return mirrored;
    }

    private static BufferedImage randomColor(BufferedImage image) {
        BufferedImage processed = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        if (Math.random() >= 0.8) {
            return changeHSB(image, (float) (Math.random()), 1, 1);
        }

        int first = (int) Math.round(Math.random() * 2);
        int second = (int) Math.round(Math.random() * 2);
        int third = (int) Math.round(Math.random() * 2);

        while(first != 0 && second != 0 && third != 0) {
            first = (int) Math.round(Math.random() * 2);
            second = (int) Math.round(Math.random() * 2);
            third = (int) Math.round(Math.random() * 2);
        }

        for(int x = 0; x < image.getWidth(); x++) {
            for(int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y), true);
                int alpha = color.getAlpha();
                int[] array = new int[] {color.getRed(), color.getGreen(), color.getBlue()};

                Color newColor = new Color(array[first], array[second], array[third], alpha);

                processed.setRGB(x, y, newColor.getRGB());
            }
        }

        return processed;
    }


    static BufferedImage changeHSB(BufferedImage source, float hueOffset, float saturationMod, float brightnessMod) {
        BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics2D graphics2D = (Graphics2D) image.getGraphics();
        
        graphics2D.setComposite(new HSBComposite(hueOffset, saturationMod, brightnessMod));
        graphics2D.drawImage(source, 0, 0, null);
        
        graphics2D.dispose();
        
        return image;
    }
    
    private BufferedImage multiplyColorize(BufferedImage source, Color color) {
        BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics2D a = (Graphics2D) image.getGraphics();
        
        a.drawImage(source, 0, 0, null);
        a.setComposite(MultiplyCompositeForARGB.Multiply);
        a.setColor(color);
        a.fillRect(0, 0, source.getWidth(), source.getHeight());
        a.dispose();

        return image;
    }
    
    public static BufferedImage mixImages(BufferedImage base, BufferedImage overlay, float alpha) {
        BufferedImage image = new BufferedImage(base.getWidth(), base.getHeight(), base.getType() == 0 ? 2 : base.getType());
        Graphics2D a = (Graphics2D) image.getGraphics();

        a.drawImage(base, 0, 0, null);
        a.setComposite(AlphaComposite.SrcOver.derive(alpha));
        a.drawImage(overlay, 0, 0, null);
        a.dispose();

        return image;
    }

//    private static BufferedImage blur(BufferedImage image, int[] filter, int filterWidth) {
//        if (filter.length % filterWidth != 0) {
//            throw new IllegalArgumentException("filter contains a incomplete row");
//        }
//
//        final int width = image.getWidth();
//        final int height = image.getHeight();
//        final int sum = IntStream.of(filter).sum();
//
//        int[] input = image.getRGB(0, 0, width, height, null, 0, width);
//
//        int[] output = new int[input.length];
//
//        final int pixelIndexOffset = width - filterWidth;
//        final int centerOffsetX = filterWidth / 2;
//        final int centerOffsetY = filter.length / filterWidth / 2;
//
//        // apply filter
//        for (int h = height - filter.length / filterWidth + 1, w = width - filterWidth + 1, y = 0; y < h; y++) {
//            for (int x = 0; x < w; x++) {
//                int r = 0;
//                int g = 0;
//                int b = 0;
//                for (int filterIndex = 0, pixelIndex = y * width + x;
//                     filterIndex < filter.length;
//                     pixelIndex += pixelIndexOffset) {
//                    for (int fx = 0; fx < filterWidth; fx++, pixelIndex++, filterIndex++) {
//                        int col = input[pixelIndex];
//                        int factor = filter[filterIndex];
//
//                        // sum up color channels seperately
//                        r += ((col >>> 16) & 0xFF) * factor;
//                        g += ((col >>> 8) & 0xFF) * factor;
//                        b += (col & 0xFF) * factor;
//                    }
//                }
//                r /= sum;
//                g /= sum;
//                b /= sum;
//                // combine channels with full opacity
//                output[x + centerOffsetX + (y + centerOffsetY) * width] = (r << 16) | (g << 8) | b | 0xFF000000;
//            }
//        }
//
//        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//        result.setRGB(0, 0, width, height, output, 0, width);
//        return result;
//    }

    public BufferedImage bloom(BufferedImage source) {
        BufferedImage blurred = gaussianBlur10H.filter(resize(source, source.getWidth() / 4, source.getHeight() / 4, Image.SCALE_FAST), null);
        blurred = resize(gaussianBlur10V.filter(blurred, null), source.getWidth(), source.getHeight(), Image.SCALE_SMOOTH);

        Graphics2D graphics2D = (Graphics2D) source.getGraphics();
        graphics2D.setComposite(AdditiveComposite.Add);
        graphics2D.drawImage(blurred, 0, 0, null);
        graphics2D.dispose();

        return source;
    }

    boolean bloom = false;
    public static boolean mirror = false;
    public static boolean isMirror() {
        return mirror;
    }

    ConvolveOp gaussianBlur10H = getGaussianBlurFilter(10, true);
    ConvolveOp gaussianBlur10V = getGaussianBlurFilter(10, false);
    public static ConvolveOp getGaussianBlurFilter(int radius, boolean horizontal) {
        if (radius < 1) {
            throw new IllegalArgumentException("Radius must be >= 1");
        }
        int size = radius * 2 + 1;
        float[] data = new float[size];
        float sigma = radius / 3.0f;
        float twoSigmaSquare = 2.0f * sigma * sigma;
        float sigmaRoot = (float) Math.sqrt(twoSigmaSquare * Math.PI);
        float total = 0.0f;
        for (int i = -radius; i <= radius; i++) {
            float distance = i * i;
            int index = i + radius;
            data[index] = (float) Math.exp(-distance / twoSigmaSquare)
                    / sigmaRoot;
            total += data[index];
        }
        for (int i = 0; i < data.length; i++) {
            data[i] /= total;
        }
        Kernel kernel;
        if (horizontal) {
            kernel = new Kernel(size, 1, data);
        } else {
            kernel = new Kernel(1, size, data);
        }
        return new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
    }
    
    
    private void iDontWannaScrollThroughThisEveryTime(Graphics2D graphics2D, int offset, Rectangle megaSoda) {
        Composite composite = graphics2D.getComposite();
        graphics2D.setComposite(AdditiveComposite.Add);

        Color yellow = new Color(180, 170, 0);
        
        int j = 16 - night.megaSodaLightsOnTicks;
        int j2 = (j * 16);
        int jSquared = j2 * j2;
        double z = fixedUpdatesAnim * 2.5d;
        int sodaX = megaSoda.x + offset + megaSoda.width / 2;
        int sodaY = megaSoda.y + megaSoda.height / 2;

        int limit1 = Math.max(0, sodaX - j2);
        int limit2 = Math.max(0, sodaY - j2);

        int limit3 = Math.min(1080, sodaX + j2);
        int limit4 = Math.min(640, sodaY + j2);

        for (int x = limit1; x < limit3; x += 2) {
            for (int y = limit2; y < limit4; y += 2) {
                int x2 = (x - sodaX);
                int y2 = (y - sodaY);

                if (x2 * x2 + y2 * y2 > jSquared)
                    continue;

                double noiseX = x / 2d;
                double noiseY = y / 2d;
                double h = noise.noise(-noiseX, noiseY, z);
                double s = noise.noise(noiseX, noiseY, z);

                if(h < 0.7 && s < 0.35)
                    continue;

                Color color = Color.WHITE;

                if (h > 0.7) {
                    color = yellow;
                }
                if (s > 0.35) {
                    if (s < 0.5) {
                        color = Color.BLUE;
                    } else if(s < 0.6) {
                        color = Color.CYAN;
                    }
                }
                graphics2D.setColor(color);
                graphics2D.fillRect(x, y, 2, 2);
            }
        }
        graphics2D.setComposite(composite);
    }
    
    public BufferedImage getShadowblockerThing(CustomNightEnemy enemy, Color purple) {
        BufferedImage image = new BufferedImage(96, 121, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        BufferedImage icon = grayscale(enemy.getIcon().request());
        graphics2D.drawImage(advancedPurplify(icon), 0, 0, null);

        graphics2D.setStroke(new BasicStroke(2));
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawRect(5, 5, 85, 110);

        graphics2D.setColor(purple);
        graphics2D.setStroke(new BasicStroke(5));
        graphics2D.drawRect(2, 2, 91, 116);

        graphics2D.setFont(comicSans30);
        graphics2D.drawString("" + enemy.getAI(), 7, 112);
        
        graphics2D.dispose();
        return image;
    }

    public static List<Double> getYPointsAtX(int[] xPoints, int[] yPoints, int givenX) {
        List<Double> yValues = new ArrayList<>();
        int n = xPoints.length;

        for (int i = 0; i < n; i++) {
            int x1 = xPoints[i];
            int y1 = yPoints[i];
            int x2 = xPoints[(i + 1) % n];
            int y2 = yPoints[(i + 1) % n];

            if ((x1 <= givenX && x2 >= givenX) || (x2 <= givenX && x1 >= givenX)) {
                if (x1 == x2) {
                    yValues.add((double) y1);
                    yValues.add((double) y2);
                } else {
                    double slope = (double) (y2 - y1) / (x2 - x1);
                    double yIntersect = y1 + slope * (givenX - x1);
                    yValues.add(yIntersect);
                }
            }
        }

        return yValues;
    }
    
    public BufferedImage getBubbleImage(float transparency) {
        Color color = new Color(currentWaterColor2.getRed() / 2 + 128, currentWaterColor2.getGreen() / 2 + 128, currentWaterColor2.getBlue() / 2 + 128, 255);
        return alphaify(multiplyColorize(bubbleImage.request(), color), transparency);
    }
    
    public static double lerp(double a, double b, double f) {
        return a + f * (b - a);
    }

    public Color lerpColors(Color color1, Color color2, double percent){
        double inverse_percent = 1.0 - percent;
        int redPart = (int) (color1.getRed() * percent + color2.getRed() * inverse_percent);
        int greenPart = (int) (color1.getGreen() * percent + color2.getGreen() * inverse_percent);
        int bluePart = (int) (color1.getBlue() * percent + color2.getBlue() * inverse_percent);
        return new Color(redPart, greenPart, bluePart);
    }

    public static Polygon rectangleToPolygon(Rectangle rect) {
        int[] xpoints = {rect.x, rect.x + rect.width, rect.x + rect.width, rect.x};
        int[] ypoints = {rect.y, rect.y, rect.y + rect.height, rect.y + rect.height};
        return new Polygon(xpoints, ypoints, 4);
    }

    public static double[] rotatePoint(double x, double y, double centerX, double centerY, double angleRad) {
        // Translate point to origin (relative to center)
        double translatedX = x - centerX;
        double translatedY = y - centerY;

        // Perform rotation using rotation matrix:
        // x' = x * cos(θ) - y * sin(θ)
        // y' = x * sin(θ) + y * cos(θ)
        double rotatedX = translatedX * Math.cos(angleRad) - translatedY * Math.sin(angleRad);
        double rotatedY = translatedX * Math.sin(angleRad) + translatedY * Math.cos(angleRad);

        // Translate back to original coordinate system
        double finalX = rotatedX + centerX;
        double finalY = rotatedY + centerY;

        return new double[]{finalX, finalY};
    }

    public static double[] simulatePixelRotation(double x, double y, int width, int height, double angleInRadians) {
        int w = width;
        int h = height;
        int wPrime = (int) (h * Math.abs(Math.sin(angleInRadians)) + w * Math.abs(Math.cos(angleInRadians)));
        int hPrime = (int) (w * Math.abs(Math.sin(angleInRadians)) + h * Math.abs(Math.cos(angleInRadians)));

        if(wPrime <= 0) wPrime = 1;
        if(hPrime <= 0) hPrime = 1;

        double xTranslated = x - w/2.0;
        double yTranslated = y - h/2.0;

        double xRotated = xTranslated * Math.cos(angleInRadians) - yTranslated * Math.sin(angleInRadians);
        double yRotated = xTranslated * Math.sin(angleInRadians) + yTranslated * Math.cos(angleInRadians);

        double xFinal = xRotated + wPrime/2.0;
        double yFinal = yRotated + hPrime/2.0;

        return new double[]{xFinal, yFinal};
    }

    public static Polygon rotatePolygon(Polygon original, int centerX, int centerY, double angleRad) {
        double cosTheta = Math.cos(angleRad);
        double sinTheta = Math.sin(angleRad);

        Polygon rotated = new Polygon();

        for (int i = 0; i < original.npoints; i++) {
            double dx = original.xpoints[i] - centerX;
            double dy = original.ypoints[i] - centerY;

            double newX = dx * cosTheta - dy * sinTheta;
            double newY = dx * sinTheta + dy * cosTheta;

            rotated.addPoint(
                    (int)(newX + centerX),
                    (int)(newY + centerY)
            );
        }
        return rotated;
    }

    public static Polygon createPizzaSlicePolygon(int centerX, int centerY, int maxRadius, float percentage) {
        Polygon slice = new Polygon();
        int numPoints = 36;
        
        if (percentage == 0) {
            return slice;
        }
        slice.addPoint(centerX, centerY);

        double maxAngle = 360.0 * percentage;

        int pointsToInclude = (int)(numPoints * percentage) + 1;
        pointsToInclude = Math.min(pointsToInclude, numPoints);

        for (int i = 0; i <= pointsToInclude; i++) {
            double angle = Math.toRadians(i * maxAngle / pointsToInclude);
            int x = centerX + (int)(maxRadius * Math.cos(angle));
            int y = centerY + (int)(maxRadius * Math.sin(angle));
            slice.addPoint(x, y);
        }

        return slice;
    }

    public static Shape polygonToShape(Polygon polygon) {
        GeneralPath path = new GeneralPath();
        if (polygon.npoints == 0) {
            return path;
        }
        path.moveTo(polygon.xpoints[0], polygon.ypoints[0]);
        for (int i = 1; i < polygon.npoints; i++) {
            path.lineTo(polygon.xpoints[i], polygon.ypoints[i]);
        }
        path.closePath();
        return path;
    }

    private static BufferedImage getThumbnail(PlaylistItem item) {
        try {
            ThumbnailDetails thumbnails = item.getSnippet().getThumbnails();
            if (thumbnails == null) {
                return null;
            }

            String thumbnailUrl = null;
            if (thumbnails.getHigh() != null) {
                thumbnailUrl = thumbnails.getHigh().getUrl();
            } else if (thumbnails.getMedium() != null) {
                thumbnailUrl = thumbnails.getMedium().getUrl();
            } else if (thumbnails.getDefault() != null) {
                thumbnailUrl = thumbnails.getDefault().getUrl();
            }

            if (thumbnailUrl == null) {
                return null;
            }

            URL url = new URL(thumbnailUrl);
            return ImageIO.read(url);
        } catch (Exception e) {
            System.err.println("Error retrieving thumbnail: " + e.getMessage());
            return null;
        }
    }
    
    private void loadYoutubeVideos() {
        String API_KEY = ""; // Replace with your API key
        String PLAYLIST_ID = "PLuNFwsvGKENQga1T6UkImSXCUgsHqXQek"; // Replace with your playlist ID

        try {
            YouTube youtube = new YouTube.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    null
            )
                    .setApplicationName("YourAppName")
                    .build();

            // Set up the request to list playlist items
            YouTube.PlaylistItems.List request = youtube.playlistItems().list(List.of("snippet"));
            request.setKey(API_KEY);
            request.setPlaylistId(PLAYLIST_ID);
            request.setMaxResults(50L);

            PlaylistItem latestItem = null;
            String nextPageToken = null;
            do {
                // Execute the request
                PlaylistItemListResponse response = request.setPageToken(nextPageToken).execute();

                // Process each item in the current page
                List<PlaylistItem> items = response.getItems();
                for (PlaylistItem item : items) {
                    if (latestItem == null || item.getSnippet().getPublishedAt().getValue() > latestItem.getSnippet().getPublishedAt().getValue()) {
                        latestItem = item;
                    }
                }

                // Get the next page token for pagination
                nextPageToken = response.getNextPageToken();
            } while (nextPageToken != null && !nextPageToken.isEmpty());

            // Print details of the latest video
            if (latestItem != null) {
                System.out.println("Latest Video Title: " + latestItem.getSnippet().getTitle());
                System.out.println("Video ID: " + latestItem.getSnippet().getResourceId().getVideoId());
                System.out.println("Published At: " + latestItem.getSnippet().getPublishedAt());

                BufferedImage thumbnail = getThumbnail(latestItem);
                thumbnail = resize(thumbnail, 260, 165, BufferedImage.SCALE_SMOOTH);
                Graphics2D graphics2D = (Graphics2D) thumbnail.getGraphics();

                graphics2D.setStroke(new BasicStroke(4));
                graphics2D.setColor(Color.WHITE);
                graphics2D.drawRect(0, 0, 260, 165);
                graphics2D.fillPolygon(getPolygon(List.of(new Point(115, 40), new Point(115, 120), new Point(160, 80))));

                graphics2D.dispose();

                String text = latestItem.getSnippet().getTitle();
                text = text.trim();
                if(text.length() > 25) {
                    text = text.substring(0, 25);
                    text = text.trim() + "..";
                }

                URL url = new URL("https://youtu.be/" + latestItem.getSnippet().getResourceId().getVideoId());

                pepVoteButton = new YTVideoButton(text, thumbnail, url);
            } else {
                System.out.println("No videos found in the playlist.");
            }
        } catch (Exception ignored) { }

        ytAPILoaded = true;
    }
    
    

    static boolean fullscreen = false;
    
    public void toFullscreen() {
        window.dispose();
        window.setUndecorated(true);
        window.setVisible(true);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public void removeFullscreen() {
        window.dispose();
        window.setUndecorated(false);
        window.setVisible(true);
        window.setExtendedState(JFrame.NORMAL);
        window.setSize(1096, 679);
    }
}
