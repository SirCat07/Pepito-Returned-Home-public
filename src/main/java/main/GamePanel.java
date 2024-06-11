package main;

import cutscenes.Cutscene;
import enemies.Rat;
import game.SensorConsole;
import game.*;
import game.achievements.AchievementHandler;
import game.achievements.Achievements;
import game.bingo.BingoCard;
import game.bingo.BingoHandler;
import game.bingo.BingoTask;
import game.custom.CustomNight;
import game.custom.CustomNightEnemy;
import game.custom.CustomNightModifier;
import game.playmenu.PlayMenu;
import game.playmenu.PlayMenuElement;
import game.shadownight.*;
import javafx.scene.media.MediaPlayer;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import org.jetbrains.annotations.NotNull;
import utils.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class GamePanel extends JPanel implements Runnable {

    public short fpsCap = 60; // -1 - unlocked
    public int thousandFPS = 1000 / fpsCap; // update this when changing fps, minimal is 1, max is 1000 / FPS
    private int actualFPS = 60;

    public boolean inCam = false;

    float volume = 0.8F;
    boolean blackBorders = false; // fixed ratio
    boolean headphones = false;
    boolean[] fpsCounters = new boolean[] {true, false, false}; // fps, ups, fups
    public byte shake = 1; // 0 - window + image shake, 1 - image shake, 2 - no shake
    int staticSpeed = 1; // the more - the slower; default - 1; <=0 does nothing
    public boolean showManual = true; // shows manual, ignores geiger counter
    boolean saveScreenshots = true; // save screenshots in a folder
    boolean disclaimer = true; // disclaimer at the start of the game

    int settingsScrollY = 0;

    public SoundMP3 music = new SoundMP3(this, "music");
    public SoundMP3 sound = new SoundMP3(this);
    public SoundMP3 scaryCatSound = new SoundMP3(this, "scaryCat");
    public SoundMP3 generatorSound = new SoundMP3(this, "generator");
    public SoundMP3 rainSound = new SoundMP3(this, "rain");
    public SoundMP3 bingoSound = new SoundMP3(this, "bingo");

    short quickVolumeSeconds = 0;
    short quickVolumeY = -120;

    Path gameDirectory;

    boolean debugMode = false;

    public KeyHandler keyHandler = new KeyHandler(this);

    final FPSCounter fpscnt;
    private final FPSCounter upscnt;
    private final FPSCounter fupscnt;

    boolean isFocused = true;

    public GameState state = GameState.UNLOADED;

    String version = "2.0.0final-test";
    short versionTextLength = 0;

    Color currentLeftPan = new Color(0, 0, 0);
    Color currentRightPan = new Color(0, 0, 0);

    public Item soda;
    public Item flashlight;
    public Item fan;
    public Item metalPipe;
    public Item sensor;
    Item maxwell;
    public Item adblocker;
    Item freezePotion;
    Item planks;
    public Item miniSoda;
    public Item soup;
    Item birthdayHat;
    Item birthdayMaxwell;
    Item bingoCardItem;
    public Item soggyBallpit;
    Corn[] corn = new Corn[2];
    Item sunglasses;
    public boolean sunglassesOn = false;
    Item speedrunTimer;
    int timerY = -240;
    Item starlightBottle;
    int starlightMillis = 0;
    Item shadowTicket;
    byte shadowCheckpointSelected = 0; // 0 - nothing; 1 - halfway; 2 - astarta
    public byte shadowCheckpointUsed = 0; // 0 - nothing; 1 - halfway; 2 - astarta

    public boolean inLocker = false;

    public static float freezeModifier = 1;
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


    Rectangle boopButton = new Rectangle(485, 270, 20, 20);
    Rectangle discordButton = new Rectangle(950, 510, 100, 100);
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
            case SETTINGS -> {
                closeButton = new Rectangle((int) (20 * widthModifier) + centerX, (int) (20 * heightModifier) + centerY, (int) (35 * widthModifier), (int) (35 * heightModifier));
            }
            case MENU -> {
                discordButton = new Rectangle((int) (950 * widthModifier) + centerX, (int) (510 * heightModifier) + centerY, (int) (100 * widthModifier), (int) (100 * heightModifier));
                huh = new Rectangle((int) (470 * widthModifier) + centerX, (int) (250 * heightModifier) + centerY, (int) (35 * widthModifier), (int) (35 * heightModifier));
            }
            case GAME -> {
                if(mirror) {
                    adblockerButton = new Rectangle((short) ((1080 - adblockerPoint.x - 100) * widthModifier + centerX), (short) (adblockerPoint.y * heightModifier + centerY), (short) (100 * widthModifier), (short) (100 * heightModifier));
                    boopButton = new Rectangle((int) ((1080 - offsetX - 485 - 20) * widthModifier) + centerX, (int) (270 * heightModifier) + centerY, (int) (20 * widthModifier), (int) (20 * heightModifier));
                } else {
                    adblockerButton = new Rectangle((short) (adblockerPoint.x * widthModifier + centerX), (short) (adblockerPoint.y * heightModifier + centerY), (short) (100 * widthModifier), (short) (100 * heightModifier));
                    boopButton = new Rectangle((int) ((offsetX + 485) * widthModifier) + centerX, (int) (270 * heightModifier) + centerY, (int) (20 * widthModifier), (int) (20 * heightModifier));
                }
                recalcManualButtons();
            }
            case ITEMS, BINGO, ACHIEVEMENTS, PLAY -> closeButton = new Rectangle((int) (20 * widthModifier) + centerX, (int) (20 * heightModifier) + centerY, (int) (35 * widthModifier), (int) (35 * heightModifier));
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

    public void startThread() {
        thread = new Thread(this);
        thread.start();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        every6s.put("formatChange", () -> {
            everyMinuteCounter--;
            if(everyMinuteCounter <= 0) {
                // checking if its 3 AM to add the creepy filter / else change it back
                Calendar cal = Calendar.getInstance();
                if (cal.get(Calendar.HOUR_OF_DAY) == 3) {
                    if(unshaded.getType() != BufferedImage.TYPE_BYTE_INDEXED) {
                        unshaded = new BufferedImage(1080, 640, BufferedImage.TYPE_BYTE_INDEXED);
                    }
                } else if(unshaded.getType() == BufferedImage.TYPE_BYTE_INDEXED) {
                    unshaded = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);
                }

                save();
                everyMinuteCounter = 10;
            }
        });

        // every 6 seconds
        executor.scheduleAtFixedRate(() -> {
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
                if(night.isRadiationModifier()) {
                    for(GruggyCart cart : night.gruggyCarts) {
                        cart.setAddX((int) (Math.random() * 600 - 300));
                    }
                }
            }

            countersAlive[0] = true;
        }, 0, (int) (6000 / universalGameSpeedModifier), TimeUnit.MILLISECONDS);

        // every second
        executor.scheduleAtFixedRate(() -> {
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

                        sound.play("dreadHideaway", 0.3);
                        sound.playRate("dreadHideaway", 0.05, 0.8);

                        redrawMillyShop();
                        recalculateMillyRects();
                        new Pepitimer(() -> {
                            if(state == GameState.MILLY && secondsInMillyShop >= 3600) {
                                fadeOut(160, 255, 20);

                                new Pepitimer(() -> {
                                    announcerOn = false;
                                    state = GameState.GAME;
                                    jumpscare("dread");
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
                if(!hallucinations.isEmpty()) {
                    if(Math.random() < 0.2) {
                        hallucinations.remove(0);
                    }
                }
                if(night.getTemperature() > 60) {
                    if(Math.random() < (night.getTemperature() - 60) / 80) {
                        int number = (int) (Math.random() * 3 + 1);

                        if(Math.random() < 0.2) {
                            sound.playRate("tempAmbient" + number, 0.1, 0.3F + Math.random() / 2.5F);
                        }
                        if(Math.random() > 0.6 && hallucinations.size() < 2) {
                            hallucinations.add(new Hallucination());
                        }
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
        }, 0, (int) (1000 / universalGameSpeedModifier), TimeUnit.MILLISECONDS);

        //every second 10th
        executor.scheduleAtFixedRate(() -> {
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
                    if(bingoCard.isGenerating()) {
                        redrawBingoCard();
                    }
                }
                case PLAY -> {
                    if(playSelectorWaitCounter <= 0) {
                        playSelectorWaitCounter = 2;

                        if(PlayMenu.movedMouse) {
                            for (int i = 0; i < PlayMenu.getList().size(); i++) {
                                if (Math.abs(i - PlayMenu.index) < 3) {
                                    int selectOffsetX = (int) (PlayMenu.selectOffsetX);
                                    int orderOffsetX = i * 420;

                                    Rectangle rect = new Rectangle((int) ((380 + orderOffsetX - selectOffsetX) * widthModifier) + centerX, centerY, (int) (320 * widthModifier), (int) (640 * heightModifier));
                                    if (rect.contains(keyHandler.pointerPosition)) {
                                        if(PlayMenu.index != i) {
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
                    if(night.getJumpscareCat().getShake() > 0) {
                        night.getJumpscareCat().setShake(night.getJumpscareCat().getShake() - 1);
                    }
                }
            }

            if (staticTransparency > 0F) {
                if(waitUntilStaticChange == 0) {
                    if (tvStatic != 8) {
                        tvStatic += 1;
                    } else {
                        tvStatic = 1;
                    }
                    try {
                        redrawCurrentStaticImg();
                    } catch (Exception ignored) {}

                    if(staticSpeed != 1) {
                        waitUntilStaticChange += staticSpeed;
                    }
                } else {
                    waitUntilStaticChange--;
                }
            }

            countersAlive[2] = true;
        }, 0, (int) (100 / universalGameSpeedModifier), TimeUnit.MILLISECONDS);

        //every second 20th
        executor.scheduleAtFixedRate(() -> {
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

            for(Balloon balloon : balloons) {
                balloon.counter++;
                if(balloon.counter >= 360) {
                    balloon.counter = 0;
                }
                balloon.addX(balloon.direction.getX());

                if(balloon.getX() < 0) {
                    balloon.direction = BalloonDirection.RIGHT;
                } else if(balloon.getX() > 1390) {
                    balloon.direction = BalloonDirection.LEFT;
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
            } else if(state == GameState.GAME) {
                if(night.getPepito().getFlicker() > 0) {
                    goalFlicker = (short) (Math.random() * night.getPepito().getFlicker());
                } else {
                    goalFlicker = 0;
                    currentFlicker = 0;
                }
                if(night.isRainModifier()) {
                    if(!isRainBeingProcessed) {
                        isRainBeingProcessed = true;
                        for (int i = 0; i < 25; i++) {
                            night.raindrops.add(new Raindrop());
                        }

                        Set<Raindrop> forRemoval = new HashSet<>();
                        for (Raindrop raindrop : night.raindrops) {
                            if (raindrop.getY() > 640) {
                                forRemoval.add(raindrop);
                            }
                        }
                        night.raindrops.removeAll(forRemoval);
                        isRainBeingProcessed = false;
                    }
                }
                if(deathScreenY >= 640) {
                    if(afterDeathText.length() != afterDeathCurText.length()) {
                        String str = String.valueOf(afterDeathText.charAt(afterDeathCurText.length()));
                        afterDeathCurText += str;
                        redrawDeathScreen();
                    }
                }
            }

            countersAlive[3] = true;
        }, 0, (int) (50 / universalGameSpeedModifier), TimeUnit.MILLISECONDS);

        Console.initialize(this);
    }

    int playSelectorWaitCounter = 2;

    int waitUntilStaticChange = 0;

    boolean[] countersAlive = new boolean[4];

    void redrawCurrentStaticImg() {
        if(fadedStaticImg[tvStatic - 1] != null) {
            currentStaticImg = alphaify(fadedStaticImg[tvStatic - 1], staticTransparency);
        }
    }

    public void generateAdblocker() {
        if (adblockerStatus == 0) {
            short adblockerChance = (short) Math.round(Math.random() * 599);
            if (adblockerChance == 0) {
                adblockerStatus = 1;
                adblockerTimer = 10;

                adblockerPoint.x = (short) (20 + Math.round(Math.random() * 1040));
                adblockerPoint.y = (short) (20 + Math.round(Math.random() * 600));
                adblockerButton = new Rectangle((short) (adblockerPoint.x * widthModifier + centerX), (short) (adblockerPoint.y * heightModifier + centerY), (short) (100 * widthModifier), (short) (100 * heightModifier));
            }
        } else if(adblockerStatus == 1) {
            adblockerTimer--;
            if(adblockerTimer == 0) {
                adblockerStatus = 0;
            }
        } else if(adblockerStatus == 2){
            if(night.seconds > (night.getDuration() / 1.5)) {
                adblockerStatus = 3;
                adblocker.safeAdd(1);
            }
        }
    }

    public short currentWaterLevel = 640;
    public byte waterSpeed = -3;
    public short currentWaterPos = 0;

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
        if(night.getEvent() == GameEvent.MAXWELL) {
            cam = camStates[3];
            return;
        }
        if(night.getType() == GameType.DAY) {
            cam = camStates[5];
            return;
        }

        if (night.getPepito().isNotPepito) {
            if (night.getPepito().seconds > 0) {
                cam = camStates[0];
            } else if (night.getPepito().notPepitoRunsLeft > 0) {
                cam = camStates[3];
            } else {
                cam = camStates[0];
            }
        } else {
            if (night.getPepito().seconds > 0) {
                cam = camStates[0];
            } else if (night.getPepito().pepitoStepsLeft > 0) {
                cam = camStates[1];
            } else {
                cam = camStates[0];
            }
        }

        if (night.getPepito().pepitoScareSeconds > 0) {
            cam = camStates[2];
        }
        if (night.getPepito().notPepitoScareSeconds > 0) {
            cam = camStates[4];
        }

        if(inCam) {
            if(cam == camStates[4] || cam == camStates[3]) {
                BingoHandler.completeTask(BingoTask.SEE_NOTPEPITO_CAM);
            }
        }
    }

    public short[] glitchX = new short[] {0, 0};
    public short[] glitchY = new short[] {0, 0};


    BufferedImage[] fadedStaticImg = new BufferedImage[8];
    BufferedImage currentStaticImg;
    BufferedImage[] discordStates = new BufferedImage[2];
    BufferedImage discord;
    BufferedImage[] moreMenu = new BufferedImage[2];
    PepitoImage logo = new PepitoImage("/menu/logo.png");
    public BufferedImage officeImg;
    BufferedImage birthdayOfficeImg;

    public PepitoImage door1Img = new PepitoImage("/game/office/door1.png");
    public PepitoImage door2Img = new PepitoImage("/game/office/door2.png");

    PepitoImage[] doorButton = new PepitoImage[] {new PepitoImage("/game/office/close.png"), new PepitoImage("/game/office/open.png")};
    PepitoImage timerDoorButton = new PepitoImage("/game/office/timerFront.png");

    BufferedImage fullOffice;

    BufferedImage metalPipeImg;
    BufferedImage flashlightImg;
    BufferedImage sodaImg;
    BufferedImage[] fanImg = new BufferedImage[3];
    BufferedImage mudseal;
    BufferedImage sensorImg;
    BufferedImage planksImg;
    BufferedImage freezeImg;
    BufferedImage miniSodaImg;
    BufferedImage soupItemImg;
    BufferedImage speedrunTimerImg;
    PepitoImage dabloon = new PepitoImage("/game/endless/dabloon.png");
    PepitoImage millyButton = new PepitoImage("/game/endless/millyButton.png");

    PepitoImage colaImg = new PepitoImage("/game/entities/colacat/cola.png");

    PepitoImage wiresImg = new PepitoImage("/game/entities/wires/wires.png");
    BufferedImage[] wiresText = new BufferedImage[2];
    PepitoImage frogImg = new PepitoImage("/game/entities/frogo.png");

    PepitoImage restInPeice = new PepitoImage("/game/restInPeice.png");
    PepitoImage strawber = new PepitoImage("/game/strawber.png");
    BufferedImage astartaSticker;
    PepitoImage deathScreenRender = new PepitoImage("/game/deathScreenRender.png");
    BufferedImage deathScreenText = new BufferedImage(456, 330, BufferedImage.TYPE_INT_ARGB);


    public BufferedImage[] camStates = new BufferedImage[6]; // 0 = empty; 1 = pepitoBack; 2 = pepitoLeave; 3 = notPepitoBack; 4 = notPepitoLeave; // 5 = day
    PepitoImage hyperCam = new PepitoImage("/game/cam/hyperCam2.png");
    BufferedImage cam1A;
    BufferedImage[] astartaCam = new BufferedImage[2];
    BufferedImage makiCam;
    BufferedImage noSignal;

    BufferedImage jumpscare;
    PepitoImage[] jumpscares = new PepitoImage[14]; // 0 - shadowpepito; 1 - pepito; 2 - notPepito; 3 - astarta; 4 - msi;
    // 5 - cocacola; 6 - maki; 7,8 - shark; 9 - boykisser; 10 - lemonade; 11 - dread; 12 - scary cat; 13 - el astarta;


    BufferedImage adblockerImage;
    BufferedImage canny;
    BufferedImage[] uncanny = new BufferedImage[2];
    BufferedImage stopSign;

    BufferedImage smallAdblockerImage;

    public BufferedImage[] msiImage = new BufferedImage[4];
    PepitoImage msiArrow = new PepitoImage("/game/entities/msi/msiArrow.png");

    PepitoImage[] scaryCatImage = new PepitoImage[] {new PepitoImage("/game/entities/scaryCat/scaryCat.png"), new PepitoImage("/game/entities/scaryCat/scaryCatShadow.png")};
    PepitoImage[] scaryCatWarn = new PepitoImage[] {new PepitoImage("/game/entities/scaryCat/warn.png"), new PepitoImage("/game/entities/scaryCat/shadowWarn.png")};
    PepitoImage[] scaryCatMove = new PepitoImage[] {new PepitoImage("/game/entities/scaryCat/move.png"), new PepitoImage("/game/entities/scaryCat/shadowMove.png")};
    BufferedImage astartaEyes;

    PepitoImage creditsdotpng = new PepitoImage("/menu/creditsdotpng.png");
    PepitoImage headphonesImg = new PepitoImage("/game/office/headphone.png");
    PepitoImage lockerInsideImg = new PepitoImage("/game/office/lockerInside.png");
    PepitoImage sunglassesOverlay = new PepitoImage("/game/office/sunglasses.png");

    PepitoImage millyShopColors = new PepitoImage("/game/endless/colors.png");
    BufferedImage millyShopColorsChanging;
    BufferedImage[] vignette = new BufferedImage[2];
    BufferedImage[] alphaVignette = new BufferedImage[2];

    PepitoImage wata = new PepitoImage("/game/entities/shark/wata.png");
    PepitoImage koi = new PepitoImage("/game/entities/shark/koi.png");
    PepitoImage sharkImg = new PepitoImage("/game/entities/shark/shark.png");
    PepitoImage boykisserImg = new PepitoImage("/game/entities/boykisser/boykisser.png");
    PepitoImage sobEmoji = new PepitoImage("/game/entities/boykisser/sob.png");
    PepitoImage a120Img = new PepitoImage("/game/entities/a120/a120.png");
    public BufferedImage lemonadeGato;
    BufferedImage maxwellIcon;
    BufferedImage birthdayMaxwellIcon;
    BufferedImage birthdayHatImg;

    PepitoImage lemon = new PepitoImage("/game/entities/lemonade/lemon.png");
    PepitoImage soggyBalls = new PepitoImage("/game/endless/soggy balls.png");

    BufferedImage[] itemTags = new BufferedImage[2];
    BufferedImage riftImg;
    BufferedImage riftFrame;
    BufferedImage shadowPortal;

    BufferedImage mirrorCatImg;
    BufferedImage[] mirrorCage = new BufferedImage[2];
    BufferedImage mirrorCatExplode;

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


    BufferedImage starlightBottleImg;
    PepitoImage vignetteStarlight = new PepitoImage("/game/office/vignetteStarlight.png");
    BufferedImage realVignetteStarlight;

    PepitoImage jumpscareCat = new PepitoImage("/game/entities/jumpscareCat.png");
    PepitoImage makiWarning = new PepitoImage("/game/entities/makiWarning.png");
    PepitoImage randomsog = new PepitoImage("/game/office/randomsog.png");
    PepitoImage conflictingItem = new PepitoImage("/game/items/conflictingItem.png");
    PepitoImage shadowGlitch = new PepitoImage("/game/endless/shadowGlitch.png");
    PepitoImage blizzardAnnouncement = new PepitoImage("/game/office/blizzardAnnouncement.png");

    PepitoImage battery = new PepitoImage("/game/office/generator/battery.png");
    PepitoImage[] charge = new PepitoImage[] {new PepitoImage("/game/office/generator/uncharged.png"), new PepitoImage("/game/office/generator/charged.png")};
    PepitoImage generator = new PepitoImage("/game/office/generator/generator.png");
    PepitoImage generatorOutline = new PepitoImage("/game/office/generator/generatorOutline.png");
    PepitoImage connectText = new PepitoImage("/game/office/generator/connectText.png");

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

    PepitoImage disclaimerImage = new PepitoImage("/menu/disclaimer.png");




    static BufferedImage balloonImg;
    public static List<Balloon> balloons = new ArrayList<>();

    List<Hallucination> hallucinations = new ArrayList<>();


    public boolean fanActive = false;

    public int launchedGameTime = (int) (System.currentTimeMillis() / 1000);

    boolean isPepitoBirthday;
    short birthdayAnimation = 0;

    boolean isAprilFools;
    Platformer platformer;

    byte recordEndlessNight = 0;
    public boolean reachedAstartaBoss = false;


    public void save() {
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

            dataBuilder.append("disclaimer:").append((disclaimer) ? "1" : "0").append("\n");
            dataBuilder.append("showManual:").append((showManual) ? "1" : "0").append("\n");
            dataBuilder.append("saveScreenshots:").append((saveScreenshots) ? "1" : "0").append("\n");
            dataBuilder.append("bloom:").append((bloom) ? "1" : "0").append("\n");
            dataBuilder.append("fpsCounter:").append((fpsCounters[0]) ? "1" : "0").append("\n");
            dataBuilder.append("fpsCap:").append(fpsCap).append("\n");
            dataBuilder.append("shake:").append(shake).append("\n");

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
                .setDetails("PÉPITO RETURNED HOME")
                .setBigImage("menu", "PÉPITO RETURNED HOME")
                .setSmallImage("pepito", "PÉPITO RETURNED HOME")
                .setStartTimestamps(launchedGameTime)
                .build();

        DiscordRPC.discordUpdatePresence(rich);

        String os_name = System.getProperty( "os.name").toLowerCase();

        if (os_name.toLowerCase(Locale.ROOT).contains("win")) {
            gameDirectory = Path.of(System.getenv("APPDATA") + "\\four night pepito");
        } else { //linux момент
            gameDirectory = Path.of(System.getProperty( "user.home" ) + "\\four night pepito");
        }

        Calendar calendar = Calendar.getInstance();

        isPepitoBirthday = calendar.get(Calendar.MONTH) == Calendar.SEPTEMBER && calendar.get(Calendar.DAY_OF_MONTH) == 4;
        isAprilFools = calendar.get(Calendar.MONTH) == Calendar.APRIL && calendar.get(Calendar.DAY_OF_MONTH) <= 7;
//        isAprilFools = true;

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

        officeImg = toCompatibleImage(loadImg("/game/office/office.png"));
        birthdayOfficeImg = toCompatibleImage(loadImg("/game/office/birthdayFullOffice.png"));

        BufferedImage sensorIcon = toCompatibleImage(loadImg("/game/items/sensorIcon.png"));
        BufferedImage soggyImage = loadImg("/game/items/soggySubscription.png");
        BufferedImage cornImage = loadImg("/game/items/cornIcon.png");
        BufferedImage sunglassesIcon = loadImg("/game/items/sunglassesIcon.png");
        BufferedImage bingoCardIcon = loadImg("/game/items/bingoCard.png");
        BufferedImage speedrunTimerIcon = loadImg("/game/items/speedrunTimerIcon.png");
        BufferedImage shadowTicketIcon = loadImg("/game/items/shadowTicket.png");

        metalPipeImg = toCompatibleImage(loadImg("/game/items/metalPipe.png"));
        flashlightImg = toCompatibleImage(loadImg("/game/items/flashlight.png"));
        sodaImg = toCompatibleImage(loadImg("/game/items/SODAA.png"));
        mudseal = toCompatibleImage(loadImg("/game/items/mud seal.png"));
        fanImg[0] = toCompatibleImage(loadImg("/game/items/fan_0.png"));
        fanImg[1] = toCompatibleImage(loadImg("/game/items/fan.png"));
        fanImg[2] = toCompatibleImage(loadImg("/game/items/fanBlade.png"));
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

        lemonadeGato = toCompatibleImage(loadImg("/game/entities/lemonade/gato.png"));

        wiresText[0] = toCompatibleImage(loadImg("/game/entities/wires/wiresText.png"));
        wiresText[1] = toCompatibleImage(loadImg("/game/entities/wires/riftText.png"));

        vignette[0] = toCompatibleImage(loadImg("/game/endless/vignette.png"));
        vignette[1] = lightify(vignette[0]);

        jumpscares[0] = new PepitoImage("/game/jumpscares/shadowPepito.png");
        jumpscares[1] = new PepitoImage("/game/jumpscares/pepito.png");
        jumpscares[2] = new PepitoImage("/game/jumpscares/notPepito.png");
        jumpscares[3] = new PepitoImage("/game/jumpscares/astarta2.png");
//        jumpscares[4] = new PepitoImage("/game/jumpscares/MSI.png");
        jumpscares[5] = new PepitoImage("/game/jumpscares/colaCat.png");
        jumpscares[6] = new PepitoImage("/game/jumpscares/maki.png");
        jumpscares[7] = new PepitoImage("/game/jumpscares/sharkJumpscare.png");
        jumpscares[8] = new PepitoImage("/game/jumpscares/sharkJumpscare2.png");
        jumpscares[9] = new PepitoImage("/game/jumpscares/boykisser.png");
        jumpscares[10] = new PepitoImage("/game/jumpscares/lemonade.png");
        jumpscares[11] = new PepitoImage("/game/jumpscares/dread.png");
        jumpscares[12] = new PepitoImage("/game/jumpscares/scaryCat.png");
        jumpscares[13] = new PepitoImage("/game/jumpscares/elAstarta.png");
        jumpscare = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

        adblockerImage = toCompatibleImage(resize(loadImg("/game/entities/a90/adblocker.png"), 200, 200, Image.SCALE_SMOOTH));
        canny = toCompatibleImage(resize(loadImg("/game/entities/a90/canny.png"), 200, 200, Image.SCALE_SMOOTH));
        uncanny[0] = toCompatibleImage(loadImg("/game/entities/a90/uncanny.png"));
        uncanny[1] = toCompatibleImage(redify(uncanny[0]));
        stopSign = toCompatibleImage(loadImg("/game/entities/a90/stopSign.png"));

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
        sastartaTank[1] = completelyBlack(sastartaTank[0]);
        astartaMinecartWhite = completelyWhite(loadImg("/game/entities/astartaBoss/minecart.png"));

        soda = new Item(resize(rotate(sodaImg, 45, true), 100, 100, Image.SCALE_SMOOTH), "SODAA", "very tasty refreshing\n[S]: gives +40% energy", -1, "soda");
        flashlight = new Item(resize(flashlightImg, 100, 80, Image.SCALE_SMOOTH), "flashlight", "too dark cant see\n[RMB]: flashes a light\nscares Astarta\ncooldown: 28s", -1, "flashlight");
        fan = new Item(resize(rotate(fanImg[0], 0, true), 100, 100, Image.SCALE_SMOOTH), "fan", "is it that hot\n[F]: lowers NotPepito\nchance;\nclears flood faster", -1, "fan");
        metalPipe = new Item(itemOffset(resize(metalPipeImg, 100, 20, Image.SCALE_SMOOTH), 0, 70), "METAL PIPE", "METAL PIPE\n[M]: scares Pépito\ncooldown: 22s", -1, "metalPipe");
        sensor = new Item(resize(sensorIcon, 110, 110, Image.SCALE_SMOOTH), "SENSOR", "can detect movement.\nside effects may\ninclude: MSI", -1, "sensor");
        adblocker = new Item(resize(adblockerImage, 100, 100, Image.SCALE_FAST), "adblocker", "game on easy mode!\n- removes uncanny\n- removes glitcher", 1, "adblocker").addTags(List.of(ItemTag.RIFT));
        maxwell = new Item(resize(maxwellIcon, 110, 100, Image.SCALE_SMOOTH), "maxwell", "this is a maxwell\nendless: generates\ndabloons", 0, "maxwell").addTags(List.of(ItemTag.RIFT));
        freezePotion = new Item(resize(freezeImg, 100, 100, Image.SCALE_SMOOTH), "ice potion", "makes enemies slower\n[I]: freeze everything\nfor 30s", 0, "freeze").addTags(List.of(ItemTag.RIFT));
        planks = new Item(resize(planksImg, 120, 90, Image.SCALE_SMOOTH), "planks", "blockades doors\nlasts 12 knocks\n[B+1]: door 1 \n[B+2] door 2", 0, "planks").addTags(List.of(ItemTag.RIFT));
        miniSoda = new Item(resize(rotate(miniSodaImg, 45, true), 100, 100, Image.SCALE_SMOOTH), "MINNESOTA", "mini soda\n[D]: gives +10% energy", 0, "miniSoda").addTags(List.of(ItemTag.RIFT));
        soup = new Item(resize(soupItemImg, 80, 100, Image.SCALE_SMOOTH), "canned soup", "WARNING: DANGEROUS\nSPECIES INSIDE\n[U]: summon SOUP\nto nullify everything", 0, "soup").addTags(List.of(ItemTag.RIFT));
        birthdayMaxwell = new Item(resize(birthdayMaxwellIcon, 110, 100, Image.SCALE_SMOOTH), "party maxwell", "this is a maxwell\n:activates an ending:", 0, "birthdayMaxwell").addTags(List.of(ItemTag.RIFT));
        birthdayHat = new Item(resize(birthdayHatImg, 100, 100, Image.SCALE_SMOOTH), "birthday hat", "gives you a try\nat Pepito's Pre-Party\n1 item rifts into 10", 0, "birthdayHat").addTags(List.of(ItemTag.RIFT));
        bingoCardItem = new Item(bingoCardIcon, "bingo card", "unlocks pepingo forever\nuse in a game once", 0, "bingoCard").addTags(List.of(ItemTag.RIFT));
        starlightBottle = new Item(resize(starlightBottleImg, 110, 110, Image.SCALE_SMOOTH), "bottle of starlight", "[L]: use bottle\ncontains starlight\n\nnot stolen from\nroblox doors\ni swear", 0, "starlightBottle");
        shadowTicket = new Item(resize(shadowTicketIcon, 120, 90, Image.SCALE_SMOOTH), "shadowticket", "sends you into\nshadownight", 0, "shadowTicket");

        soggyBallpit = new Item(resize(soggyImage, 100, 100, Image.SCALE_SMOOTH), "subscription", "ever wanted a\nwormhole through time\ninside a ballpit?\nThis is your answer!\nSubscribe now!", 0, "ballpit");
        corn[0] = new Corn(cornImage, "corn", "corn!\ngrows 3 nights\ndecreases enemy\nAI by 1\nwhen grown", 0, "corn", loadImg("/game/items/cornStage1.png"));
        corn[1] = new Corn(cornImage, "corn", "corn!\ngrows 3 nights\ndecreases enemy\nAI by 1\nwhen grown", 0, "corn2", loadImg("/game/items/cornStage1.png"));
        sunglasses = new Item(itemOffset(sunglassesIcon, 0, 60), "sunglasses", "protection from a120\n[G]: wear sunglasses", 0, "sunglasses");
        speedrunTimer = new Item(itemOffset(resize(speedrunTimerIcon, 100, 90, Image.SCALE_SMOOTH), 0, 10), "speedrun timer", "definitely uhh\ncounts down until\nsomething!", 0, "speedrunTimer");

        birthdayMaxwell.addConflicts(List.of(maxwell, adblocker, shadowTicket)); // gamemode
        birthdayHat.addConflicts(List.of(adblocker, adblocker, shadowTicket)); // gamemode
        maxwell.addConflicts(List.of(birthdayMaxwell)); // two maxwells
        adblocker.addConflicts(List.of(birthdayMaxwell, birthdayHat, shadowTicket)); // disabled in these modes
        shadowTicket.addConflicts(List.of(birthdayHat, birthdayMaxwell, adblocker)); // gamemode

        birthdayHat.setItemLimitAdd((byte) 1);
        shadowTicket.setItemLimitAdd((byte) 6);
        updateItemList();

        maxwellIcon = resize(maxwellIcon, 180, 150, BufferedImage.SCALE_SMOOTH);
        birthdayMaxwellIcon = resize(birthdayMaxwellIcon, 180, 150, BufferedImage.SCALE_SMOOTH);

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
                        winCount = Short.parseShort(Arrays.stream(array).filter(string -> string.startsWith("win:")).findFirst().orElse("0").replaceAll("win:",""));
                        deathCount = Short.parseShort(Arrays.stream(array).filter(string -> string.startsWith("death:")).findFirst().orElse("0").replaceAll("death:",""));
                        currentNight = Byte.parseByte(Arrays.stream(array).filter(string -> string.startsWith("night:")).findFirst().orElse("1").replaceAll("night:",""));
                        volume = Float.parseFloat(Arrays.stream(array).filter(string -> string.startsWith("vol:")).findFirst().orElse("0.8").replaceAll("vol:",""));
                        blackBorders = Arrays.stream(array).filter(string -> string.startsWith("borders:")).findFirst().orElse("0").replaceAll("borders:","").equals("1");

                        headphones = Arrays.stream(array).filter(string -> string.startsWith("headphones:")).findFirst().orElse("0").replaceAll("headphones:","").equals("1");
                        recordEndlessNight = Byte.parseByte(Arrays.stream(array).filter(string -> string.startsWith("record:")).findFirst().orElse("0").replaceAll("record:",""));
                        unlockedBingo = Arrays.stream(array).filter(string -> string.startsWith("bingo:")).findFirst().orElse("0").replaceAll("bingo:","").equals("1");
                        reachedAstartaBoss = Arrays.stream(array).filter(string -> string.startsWith("reachedAstartaBoss:")).findFirst().orElse("0").replaceAll("reachedAstartaBoss:","").equals("1");

                        disclaimer = Arrays.stream(array).filter(string -> string.startsWith("disclaimer:")).findFirst().orElse("1").replaceAll("disclaimer:","").equals("1");
                        showManual = Arrays.stream(array).filter(string -> string.startsWith("showManual:")).findFirst().orElse("1").replaceAll("showManual:","").equals("1");
                        saveScreenshots = Arrays.stream(array).filter(string -> string.startsWith("saveScreenshots:")).findFirst().orElse("1").replaceAll("saveScreenshots:","").equals("1");
                        bloom = Arrays.stream(array).filter(string -> string.startsWith("bloom:")).findFirst().orElse("0").replaceAll("bloom:","").equals("1");
                        fpsCounters[0] = Arrays.stream(array).filter(string -> string.startsWith("fpsCounter:")).findFirst().orElse("1").replaceAll("fpsCounter:","").equals("1");
                        fpsCap = Short.parseShort(Arrays.stream(array).filter(string -> string.startsWith("fpsCap:")).findFirst().orElse("120").replaceAll("fpsCap:",""));
                        thousandFPS = Math.max(1, 1000 / fpsCap);

                        shake = Byte.parseByte(Arrays.stream(array).filter(string -> string.startsWith("shake:")).findFirst().orElse("0").replaceAll("shake:",""));
                    } catch (Exception ignored) {
                        System.out.println("LOADING DATA FAILED LOADING DATA FAILED");
                    }
                }
            } else {
                Files.createFile(dataPath);
            }


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

                            item.setAmount(Integer.parseInt(cut.replaceAll(item.getId() + ":", "")));

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

                            if(cut.replaceAll(achievement.name() + ":", "").equals("1")) {
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

                            int number = Integer.parseInt(cut.replaceAll(statistic.toString() + ":", ""));

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

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            save();
            System.out.println("program exploded");
        }));

        CustomNight.addNewEnemy(new CustomNightEnemy("Pépito", "pepito", 0));
        CustomNight.addNewEnemy(new CustomNightEnemy("NotPepito", "notPepito", 1));
        CustomNight.addNewEnemy(new CustomNightEnemy("Glitcher", "glitcher", 2));
        CustomNight.addNewEnemy(new CustomNightEnemy("Uncanny Cat", "uncanny", 3));
        CustomNight.addNewEnemy(new CustomNightEnemy("MSI", "msi", 4));
        CustomNight.addNewEnemy(new CustomNightEnemy("Astarta", "astarta", 5));
        CustomNight.addNewEnemy(new CustomNightEnemy("Shark", "shark", 6));
        CustomNight.addNewEnemy(new CustomNightEnemy("Boykisser", "boykisser", 7));
        CustomNight.addNewEnemy(new CustomNightEnemy("ColaCat", "colaCat", 8));
        CustomNight.addNewEnemy(new CustomNightEnemy("Zazu", "zazu", 9));
        CustomNight.addNewEnemy(new CustomNightEnemy("Maki", "maki", 10));
        CustomNight.addNewEnemy(new CustomNightEnemy("Lemonade Cat", "lemonadeCat", 11));
        CustomNight.addNewEnemy(new CustomNightEnemy("Wires", "wires", 12));
        CustomNight.addNewEnemy(new CustomNightEnemy("Scary Cat", "scaryCat", 13));
        CustomNight.addNewEnemy(new CustomNightEnemy("Jumpscare Cat", "jumpscareCat", 14));
        CustomNight.addNewEnemy(new CustomNightEnemy("El Astarta", "elAstarta", 15));

        CustomNight.addNewModifier(new CustomNightModifier("Power Outage", "placeholder"));
        CustomNight.addNewModifier(new CustomNightModifier("Blizzard", "placeholder"));
        CustomNight.addNewModifier(new CustomNightModifier("Timers", "placeholder"));
        CustomNight.addNewModifier(new CustomNightModifier("Fog", "placeholder"));
        CustomNight.addNewModifier(new CustomNightModifier("Radiation", "placeholder"));
        CustomNight.addNewModifier(new CustomNightModifier("Rain", "placeholder"));

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
                update();

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


    void launch() {
        reloadMenuButtons();
        state = GameState.MENU;
        loading = false;
        fadeOut(255, 160, 1);
        music.play("pepito", 0.2, true);
    }



    int itemLimit = 3;

    void updateItemList() {
        fullItemList = List.of(soda, flashlight, fan, metalPipe, sensor, adblocker, maxwell, freezePotion, planks, miniSoda, soup, birthdayHat, bingoCardItem,
                birthdayMaxwell, soggyBallpit, corn[0], corn[1], shadowTicket, speedrunTimer, starlightBottle, sunglasses);
        itemList = getItemsWithAmount();

        List<Item> conflicts = new ArrayList<>();
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
                rows = (byte) ((byte) (itemList.size() / 4) + 1);
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

    private float staticTransparency = 0.05F;

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

    private long lastTimeMilis = 0;
    private long totalFixedUpdates = 0;
    int fixedUpdatesAnim = 0;
    private void update() {
        if (lastTimeMilis <= 0) {
            lastTimeMilis = System.currentTimeMillis();
            return;
        }

        upscnt.frame();

        long delta = System.currentTimeMillis() - lastTimeMilis;
        for(short i = 0; i < StaticLists.timers.size(); i++) {
            StaticLists.timers.get(i).decrease((int) delta);
        }

        fixedUpdate(delta);

        if(state == GameState.UNLOADED) {
            lastTimeMilis = System.currentTimeMillis();
            return;
        }

        if (state == GameState.CUTSCENE) {
            currentCutscene.milliseconds += delta;
        }
        if (starlightMillis > 0) {
            starlightMillis = (int) Math.max(0, starlightMillis - delta);
        }

        lastTimeMilis = System.currentTimeMillis();
    }

    double quota = 0;
    // updates every 0.016s
    public void fixedUpdate(long delta) {
        delta = (long) (delta * universalGameSpeedModifier);

        quota += (int) delta;
        if(quota < 16.66)
            return;
        quota -= 16.66;

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
                    tintAlpha = mathRound(Math.max(0, Math.min(255, tintAlpha - intervalFade)));
                }
            } else {
                if (tintAlpha < endFade) {
                    tintAlpha = mathRound(Math.max(0, Math.min(255, tintAlpha + intervalFade)));
                }
            }
            if (currentFlicker != goalFlicker) {
                currentFlicker = (currentFlicker + goalFlicker) / 2;
            }

            if (staticTransparency != endStatic) {
                staticTransparency = Math.max(endStatic, staticTransparency - intervalStatic);
            }

            if (currentLeftPan.getGreen() > 0) {
                currentLeftPan = new Color(0, Math.max(currentLeftPan.getGreen() - 2, 0), 0);
            }
            if (currentRightPan.getGreen() > 0) {
                currentRightPan = new Color(0, Math.max(currentRightPan.getGreen() - 2, 0), 0);
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

            switch (state) {
                case GAME -> {
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
                        }
                    }
                    if(night.isRadiationModifier()) {
                        for(GruggyCart cart : night.gruggyCarts) {
                            float d = Math.min(1.5F, cart.getAddX() / 20F);
                            cart.setCurrentX(Math.max(0, Math.min(1080, cart.getCurrentX() + d)));
                            cart.setAddX(cart.getAddX() - d);

                            if(manualFirstButtonHover || manualSecondButtonHover || night.gruggyX < 1000)
                                continue;

                            Point point = new Point((int) (offsetX - 400 + cart.getCurrentX() + 197), 445);
                            Point rescaledPoint = new Point((int) ((keyHandler.pointerPosition.x - centerX) / widthModifier), (int) ((keyHandler.pointerPosition.y - centerY) / heightModifier));

                            if(point.distance(rescaledPoint) < 290) {
                                night.setRadiation(night.getRadiation() + 0.17F);
                            }
                        }
                        if(night.getRadiation() > 0) {
                            night.setRadiation(Math.max(0, night.getRadiation() - 0.012F));

                            if(night.getRadiation() > 100) {
                                jumpscare("radiation");
                            }
                        }
                        if(night.gruggyX < 1010) {
                            night.gruggyX += 2;
                        }
                    }

                    if(maxwell.isEnabled()) {
                        if(type == GameType.ENDLESS_NIGHT || type == GameType.DAY) {
                            maxwellCounter += 0.1F + 0.1F * (endless.getNight() - 2);
                        } else if(type == GameType.CLASSIC) {
                            maxwellCounter += 0.1F + 0.1F * (currentNight - 1);
                        }
                    }
                    if(birthdayMaxwell.isEnabled()) {
                        maxwellCounter += 0.1F;
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
                        if (keyHandler.mouseHeld && !keyHandler.isRightClick && misterHeld) {
                            byte l = (byte) (mirror ? 10 : -10);

                            if (keyHandler.pointerPosition.getX() > 540 * widthModifier + centerX) {
                                offsetX = (short) Math.min(400, Math.max(0, offsetX + l));
                            } else {
                                offsetX = (short) Math.min(400, Math.max(0, offsetX - l));
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
                        if(!isRainBeingProcessed) {
                            isRainBeingProcessed = true;
                            for (Raindrop raindrop : night.raindrops) {
                                if (night.raindrops.contains(raindrop)) {
                                    raindrop.fall();
                                }
                            }
                            isRainBeingProcessed = false;
                        }
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
                        case DYING -> {
                            if(astartaJumpscareCount) {
                                astartaJumpscareCounter++;
                            }
                            if(!drawCat) {
                                if(shake == 0 && !jumpscare.equals(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)) && !killedBy.equals("killed by Shadow Pépito")) {
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
                }

                case MENU -> {
                    if (vertical) {
                        scrollY -= 1;
                    } else {
                        scrollX -= 1;
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

                case RIFT -> riftCounter++;

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
                }

                case CHALLENGE -> {
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
            }
        }


        currentWidth = (short) (1080 * widthModifier);
        currentHeight = (short) (640 * heightModifier);

        if(quota >= 16.66) {
            fixedUpdate(0);
        }
    }
    boolean isRainBeingProcessed = false;

    private byte tvStatic = 1;

    public short goalFlicker = 0;
    public float currentFlicker = 0;
    public float tintAlpha = 255;
    public short offsetX = 200;

    byte selectedItemX = 0;
    byte selectedItemY = 0;

    Color black80 = new Color(0, 0, 0, 80);
    Color black120 = new Color(0, 0, 0, 120);
    Color black140 = new Color(0, 0, 0, 140);
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

    Font yuGothicBoldItalic25 = new Font("Yu Gothic", Font.BOLD | Font.ITALIC, 25);
    Font yuGothicBoldItalic40 = new Font("Yu Gothic", Font.BOLD | Font.ITALIC, 40);

    Font comicSansBold25 = new Font("Comic Sans MS", Font.BOLD, 25);
    Font comicSansBoldItalic40 = new Font("Comic Sans MS", Font.BOLD | Font.ITALIC, 40);
    Font comicSans20 = new Font("Comic Sans MS", Font.PLAIN, 20);
    Font comicSans25 = new Font("Comic Sans MS", Font.PLAIN, 25);
    Font comicSans30 = new Font("Comic Sans MS", Font.PLAIN, 30);
    Font comicSans40 = new Font("Comic Sans MS", Font.PLAIN, 40);
    Font comicSans50 = new Font("Comic Sans MS", Font.PLAIN, 50);
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
        BufferedImage fullOffice = new BufferedImage(1480, 640, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = (Graphics2D) fullOffice.getGraphics();

        if(night.getType().isParty()) {
            graphics2D.drawImage(birthdayOfficeImg, 0, 0, null);
        } else {
            graphics2D.drawImage(officeImg, 0, 0, null);
        }

        if(night.getElAstarta().isActive()) {
            graphics2D.setColor(new Color(73, 73, 73));
            for(int index : night.getElAstarta().getNewDoors()) {
                Polygon hitbox = night.getDoors().get(index).getHitbox();
                graphics2D.fillRect(hitbox.getBounds().x, hitbox.getBounds().y, hitbox.getBounds().width, hitbox.getBounds().height);
            }
        }

        if (metalPipe.isEnabled()) {
            graphics2D.drawImage(metalPipeImg, 654, 586, null);
        }
        if (sensor.isEnabled()) {
            graphics2D.drawImage(sensorImg, 90, 430, null);
        }
        if (flashlight.isEnabled()) {
            graphics2D.drawImage(flashlightImg, 328, 428, null);
        }
        if(miniSoda.isEnabled()) {
            graphics2D.drawImage(miniSodaImg, 300, 540, 70, 90, null);
        }
        if(soup.isEnabled()) {
            graphics2D.drawImage(soupItemImg, 415, 349, null);
        }
        if(planks.isEnabled()) {
            graphics2D.drawImage(planksImg, 1015, 395, null);
        }
        if(freezePotion.isEnabled()) {
            graphics2D.drawImage(freezeImg, 1130, 150, null);
        }
        if(starlightBottle.isEnabled()) {
            graphics2D.drawImage(starlightBottleImg, 615, 500, null);
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

        if(type == GameType.SHADOW) {
            fullOffice = purplify(fullOffice);
        }
        this.fullOffice = fullOffice;
    }

    Pepitimer startSimulationTimer = null;

    public void startGameThroughItems() {
        if(type == GameType.CUSTOM) {
            lastItemsMenu = lastFullyRenderedUnshaded;
            greenItemsMenu = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = (Graphics2D) greenItemsMenu.getGraphics();
            for(int x = 0; x < 1080; x += 2) {
                for(int y = 0; y < 640; y += 2) {
                    Color color = new Color(lastFullyRenderedUnshaded.getRGB(x, y));
                    if(color.getRed() < 5 && color.getGreen() < 5 && color.getBlue() < 5)
                        continue;

                    graphics.setColor(new Color(0, 255, 0, (int) (0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue())));
                    graphics.fillRect(x, y, 2, 2);
                }
            }
            graphics.setColor(black80);
            graphics.fillRoundRect(160, 250, 770, 80, 40, 40);
            graphics.setColor(Color.GREEN);
            graphics.setFont(yuGothicPlain80);
            String starting = "starting simulation...";
            if(Math.random() < 0.001) {
                starting = "farting simulation...";
            }
            graphics.drawString(starting, 540 - halfTextLength(graphics, starting), 320);
            graphics.dispose();

            sound.play("startSimulation", 0.1);
            music.stop();

            float[] sway = new float[] {0.02F};

            everySecond20th.put("startSimulation", () -> {
                if(sway[0] < 1) {
                    sway[0] *= 1.2F;
                }
                BufferedImage newImage = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2D = (Graphics2D) newImage.getGraphics();

                graphics2D.setColor(Color.BLACK);
                graphics2D.fillRect(0, 0, 1080, 640);

                graphics2D.drawImage(lastItemsMenu, 27 + (int) (14 * sway[0] * Math.cos(fixedUpdatesAnim * 0.05)), 16 + (int) (8 * sway[0] * Math.sin(fixedUpdatesAnim * 0.05)), 1026, 608, null);

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

    BufferedImage unshaded = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);
    BufferedImage lastFullyRenderedUnshaded = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);
    BufferedImage lastBeforePause = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);
    int fanDegrees = 0;

    List<String> manualText = new ArrayList<>();
    int manualY = 640;
    boolean hoveringGenerator = false;


    private void firstHalf(Graphics2D graphics2D) {
        switch (state) {
            case MENU -> {
                short x = (short) (Math.max(-scrollX - randomX, 0));
                short y = (short) (Math.max(-scrollY - randomY, 0));
                graphics2D.drawImage(bg[currentBG].getSubimage(x, y, Math.min(1920 - x, 1080), Math.min(1440 - y, 640)), 0, 0, null);
            }

            case GAME -> {
                if(!night.getEvent().isInGame())
                    return;

                try {
                    if(!inCam) {
                        boolean officeNotRendered = true;

                        if(night.getType() == GameType.SHADOW) {
                            if(night.getAstartaBoss() != null) {
                                if(night.getAstartaBoss().isFighting()) {
                                    officeNotRendered = false;
                                    BufferedImage office = night.getAstartaBoss().astartaOfficeStuff(fullOffice, this);

                                    if (night.getAstartaBoss().getDyingStage() < 8) {
                                        graphics2D.drawImage(office.getSubimage(400 - fixedOffsetX, 0, 1080, 640), 0, 0, null);
                                    } else {
                                        graphics2D.setColor(Color.BLACK);
                                        graphics2D.fillRect(0, 0, 1080, 640);
                                        graphics2D.drawImage(office, fixedOffsetX - 400 + (int) (740 - 740 * night.getAstartaBoss().getEndingOfficeSize()), (int) (320 - 320 * night.getAstartaBoss().getEndingOfficeSize()), null);
                                    }
                                }
                            }
                        }

                        if(officeNotRendered) {
                            if(offsetX >= 0 && offsetX <= 400) {
                                BufferedImage office = fullOffice.getSubimage(400 - fixedOffsetX, 0, 1080, 640);
                                graphics2D.drawImage(office, 0, 0, null);
                            } else {
                                System.out.println("CHEATER");
                                graphics2D.drawImage(msiKnows.request(), 300, 200, null);
                                graphics2D.drawImage(fullOffice, fixedOffsetX - 400, 0, null);
                            }
                        }



                        if(night.isPowerModifier()) {
                            graphics2D.drawImage(generator.request(), fixedOffsetX - 400 + 790, 480, null);
                            if(hoveringGenerator) {
                                graphics2D.drawImage(generatorOutline.request(), fixedOffsetX - 400 + 785, 475, null);
                            }
                        }
                        if (fan.isEnabled()) {
                            graphics2D.drawImage(fanImg[1], fixedOffsetX + 188, 304, null);
                            graphics2D.drawImage(rotate(fanImg[2], fanDegrees, false), fixedOffsetX + 124, 245, null);
                            graphics2D.drawImage(mudseal, fixedOffsetX + 279, 418, null);
                        }
                        if (soda.isEnabled()) {
                            graphics2D.drawImage(sodaImg, fixedOffsetX + 265, 355, null);

                            if (night.getColaCat().isActive()) {
                                graphics2D.setComposite(AlphaComposite.SrcOver.derive(night.getColaCat().currentState * 0.05F));
                                graphics2D.drawImage(colaImg.request(), fixedOffsetX + 265, 355, null);
                                graphics2D.setComposite(AlphaComposite.SrcOver.derive(1F));
                            }
                        }

                        if(!hallucinations.isEmpty()) {
                            for(Hallucination h : hallucinations.stream().toList()) {
                                graphics2D.drawImage(vertWobble(h.getImage().request(), 30, 2, 0.04F, 1.5F), fixedOffsetX - 400 + h.getX(), h.getY(), null);
                            }
                        }

                        if (night.getAstarta().isActive()) {
                            int anim = night.getAstarta().animation;

                            BufferedImage img = astartaEyes;
                            if(night.getAstarta().blinker) {
                                img = alphaify(astartaEyes, 0.5F);
                            }
                            Door door = night.getDoors().get((int) night.getAstarta().door);
                            graphics2D.drawImage(img, fixedOffsetX - 400 + door.getAstartaEyesPos().x, door.getAstartaEyesPos().y - anim, 107, anim * 2, null);
                        }
                        if(night.getElAstarta().isActive()) {
                            if(night.getElAstarta().isKindaActive()) {
                                int anim = night.getElAstarta().animation;

                                BufferedImage img = astartaEyes;
                                if(night.getElAstarta().blinker) {
                                    img = alphaify(astartaEyes, 0.5F);
                                }
                                Door door = night.getDoors().get((int) night.getElAstarta().door);
                                graphics2D.drawImage(img, fixedOffsetX - 400 + door.getAstartaEyesPos().x, door.getAstartaEyesPos().y - anim, 107, anim * 2, null);
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
                            if(door.isClosed()) {
                                graphics2D.drawImage(door.getClosedDoorTexture().request(), fixedOffsetX - 400 + door.getClosedDoorLocation().x, door.getClosedDoorLocation().y, null);
                            } else if(door.isHovering() && flashlightCondition) {
                                Polygon hitbox = new Polygon(door.getHitbox().xpoints, door.getHitbox().ypoints, door.getHitbox().npoints);
                                hitbox.translate(fixedOffsetX - 400, 0);
                                graphics2D.fillPolygon(hitbox);
                            }

                            BufferedImage button = doorButton[door.isClosed() ? 1 : 0].request();
                            if(night.isTimerModifier()) {
                                button = timerDoorButton.request();
                            }
                            graphics2D.drawImage(button, fixedOffsetX - 400 + door.getButtonLocation().x + buttonXOffset, door.getButtonLocation().y + buttonYOffset, null);
                        }


                        if (night.getMirrorCat().isActive()) {
                            graphics2D.drawImage(mirrorCatImg, fixedOffsetX - 400 + night.getMirrorCat().getX() + currentWaterPos * 2, 540 - kys(), null);
                            graphics2D.drawImage(mirrorCage[night.getMirrorCat().isClosed() ? 0 : 1], fixedOffsetX - 300 + night.getMirrorCat().getX() + currentWaterPos * 2, 540 - kys(), null);
                        }
                        if (night.getMirrorCat().isExploded()) {
                            graphics2D.drawImage(mirrorCatExplode, fixedOffsetX - 400 + night.getMirrorCat().getX() + currentWaterPos * 2, 540 - kys(), null);
                        }

                        for (byte i = 0; i < 2; i++) {
                            if (corn[i].isEnabled()) {
                                graphics2D.drawImage(corn[i].getImage(), fixedOffsetX - 400 + corn[i].getX(), 380 - kys(), null);
                            }
                        }
                        if (maxwell.isEnabled()) {
                            graphics2D.drawImage(rotate(maxwellIcon, (int) (Math.sin(maxwellCounter) * 30), false), fixedOffsetX + 240, 420 - kys(), null);
                        }
                        if (birthdayMaxwell.isEnabled()) {
                            graphics2D.drawImage(rotate(birthdayMaxwellIcon, (int) (Math.sin(maxwellCounter) * 30), false), fixedOffsetX + 240, 420 - kys(), null);
                        }

                        switch (night.getEvent()) {
                            case FLOOD -> {
                                int n1 = fixedOffsetX + currentWaterPos - 400;

                                graphics2D.drawImage(wata.request(), n1, currentWaterLevel, null);
                                graphics2D.drawImage(wata.request(), n1 - 1480, currentWaterLevel, null);

                                int n2 = fixedOffsetX - currentWaterPos - 400;

                                graphics2D.drawImage(wata.request(), n2, currentWaterLevel, null);
                                graphics2D.drawImage(wata.request(), n2 + 1480, currentWaterLevel, null);

                                graphics2D.drawImage(koi.request(), 460, 511 - currentWaterLevel, null);
                            }
                            case A120 -> graphics2D.drawImage(a120Img.request(), fixedOffsetX - 200 + night.getA120().getX(), 280, null);

                            case ASTARTA -> {
                                AstartaBoss ab = night.getAstartaBoss();
                                if(ab.isFighting()) {
                                    if(!ab.getMister().isAttacking()) {
                                        if(ab.getDyingStage() >= 6) {
                                            if(ab.getEndingHoleSize() > 0.01F) {
                                                int size = (int) (400 * ab.getEndingHoleSize());
                                                int x = fixedOffsetX - 400 + 740 - size / 2;
                                                int y = 320 - size / 2;

                                                if(ab.getDyingStage() == 9 || ab.getDyingStage() == 10) {
                                                    x += (int) (Math.cos(fixedUpdatesAnim * 0.02) * 16 + Math.random() * 2 - 1);
                                                    y += (int) (Math.sin(fixedUpdatesAnim * 0.02) * 16 + Math.random() * 2 - 1);
                                                }

                                                graphics2D.drawImage(astartaBlackHole[0].request(), x, y, size, size, null);
                                                graphics2D.drawImage(alphaify(astartaBlackHole[1].request(), (float) (Math.sin(fixedUpdatesAnim * 0.1 + Math.random()) / 2 + 0.5)), x, y, size, size, null);

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
                                                    textGraphics.drawString("Would you like to enter", 350 - halfTextLength(textGraphics, "Would you like to enter"), 140 + 40 * ab.getEndingTextAlpha() + sin);
                                                    textGraphics.drawString("The Void?", 350 - halfTextLength(textGraphics, "The Void?"), 200 + 40 * ab.getEndingTextAlpha() + sin);

                                                    textGraphics.setFont(comicSans80);
                                                    textGraphics.setColor(ab.getEndingChoice() ? white : gray);
                                                    textGraphics.drawString("1. " + (ab.getEndingChoice() ? "Yes" : "No"), 320 - textLength(textGraphics, "1. " + (ab.getEndingChoice() ? "Yes" : "No")), 630 - 40 * ab.getEndingTextAlpha() + cos);
                                                    textGraphics.setColor(ab.getEndingChoice() ? gray : white);
                                                    textGraphics.drawString("2. " + (ab.getEndingChoice() ? "No" : "Yes"), 380, 630 - 40 * ab.getEndingTextAlpha() + cos);

                                                    textGraphics.dispose();
                                                    graphics2D.drawImage(mirror(text, 1), fixedOffsetX - 400 + 390, 0, null);
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
                                                graphics2D.drawImage(img, fixedOffsetX - 400 + 380 + randomX, randomY, null);
                                            }
                                            case 2 -> {
                                                graphics2D.drawImage(sastartaFast.request(), fixedOffsetX - 400 + ab.getX() + randomX, 140 + randomY, null);
                                            }
                                        }

                                        for (int i = 0; i < ab.getMinecarts().size(); i++) {
                                            AstartaMinecart cart = ab.getMinecarts().get(i);
                                            int x = fixedOffsetX - 400 + cart.getX();
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
                                            graphics2D.drawImage(astartaBlackHole[0].request(), fixedOffsetX - 400 + blackHole.getX() - size / 2, blackHole.getY() - size / 2, size, size, null);
                                            graphics2D.drawImage(alphaify(astartaBlackHole[1].request(), (float) (Math.sin(fixedUpdatesAnim * 0.1 + Math.random()) / 2 + 0.5)), fixedOffsetX - 400 + blackHole.getX() - size / 2, blackHole.getY() - size / 2, size, size, null);
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
                                                graphics2D.drawImage(misterText.request(), fixedOffsetX - 400 + point.x - 50, (int) (point.y + 180 + Math.round(Math.random())), null);
                                            }
                                            graphics2D.drawImage(misterImg.request(), fixedOffsetX - 400 + point.x, point.y, null);
                                            if (mister.getBloomTransparency() > 0) {
                                                graphics2D.drawImage(alphaify(misterGlowingImg.request(), mister.getBloomTransparency()), fixedOffsetX - 400 + point.x, point.y, null);
                                            }

                                            BufferedImage timer = new BufferedImage(300, 60, BufferedImage.TYPE_INT_ARGB);
                                            Graphics2D timerGraphics = (Graphics2D) timer.getGraphics();
                                            timerGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                                            timerGraphics.setFont(comicSans50);
                                            String countdown = (Math.round(Math.max(0, mister.getCountdown()) * 100F) / 100F) + "s";
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
                                            graphics2D.drawImage(timer, fixedOffsetX - 400 + point.x, point.y - 40, null);
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

                                        String str = "RANDOM EVENT: ";
                                        if(ab.getRouletteY() >= 9660) {
                                            switch (ab.roulette1[57]) {
                                                case 0 -> str += "UNCANNY DELIVERY";
                                                case 1 -> str += "DVD EVENT";
                                                case 2 -> str += "BLACK HOLES";
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

                                        graphics2D.drawImage(sastartaTank[1], fixedOffsetX - 400 + 380, y, null);

                                        if(shadowCheckpointUsed != 0 && y < 400) {
                                            graphics2D.setFont(comicSans40);
                                            graphics2D.setColor(Color.GRAY);
                                            graphics2D.drawString("[press to skip]", fixedOffsetX - 200 + 540 - halfTextLength(graphics2D, "[press to skip]"), 620);
                                        }
                                    } else {
                                        graphics2D.drawImage(sastartaTank[0], fixedOffsetX - 400 + 380, 0, null);
                                    }
                                }
                            }
                        }

                        if (night.getType() == GameType.DAY) {
                            graphics2D.drawImage(millyButton.request(), fixedOffsetX + 685, 315, null);

                            if(endless.getNight() == 6) {
                                graphics2D.drawImage(resize(birthdayHatImg, 80, 100, BufferedImage.SCALE_SMOOTH), fixedOffsetX + 835, 260, null);
                            }
                        }

                        if(night.isRadiationModifier()) {
                            for(GruggyCart cart : night.gruggyCarts) {
                                graphics2D.drawImage(gruggyCart.request(), (int) (fixedOffsetX - 400 + cart.getCurrentX()), 465, null);
                            }
                            if(night.gruggyX < 1000) {
                                graphics2D.drawImage(gruggy.request(), fixedOffsetX - 400 + 540 + Math.max(0, night.gruggyX), 235, null);
                            }
                        }


                        if (soggyBallpitActive) {
                            graphics2D.drawImage(soggyBalls.request(), fixedOffsetX - 400, 290, 1480, 350, null);
                        }
                        if(night.frog.x > -240) {
                            graphics2D.drawImage(frogImg.request(), night.frog.x, 300 + (int) (Math.sin(fixedUpdatesAnim * 0.5) * 40), 240, 300, null);
                        }
                        for (Balloon balloon : balloons) {
                            graphics2D.drawImage(balloon.getImage(), fixedOffsetX + balloon.getX() - 400, 200 + balloon.getAdder(), null);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            case ITEMS -> {
                if(!everySecond20th.containsKey("startSimulation")) {
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
                                                graphics2D.drawString("EQUIPPED", equippedX, 245 + item.getDescription().split("\n").length * 30 - 30);
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
                                            while (j < item.getTags().size()) {
                                                ItemTag tag = item.getTags().get(j);
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
                    graphics2D.drawString("Selected Items: " + checkItemsAmount() + "/" + itemLimit + limitAdd, 735, 425);

                    graphics2D.setFont(yuGothicBoldItalic25);
                    if (!(type == GameType.CUSTOM && CustomNight.isCustom())) {
                        graphics2D.setColor(warningRed);
                        graphics2D.drawString("Warning: the items will", 735, 460);
                        graphics2D.drawString("get consumed on start", 735, 490);
                    } else {
                        graphics2D.setColor(Color.GREEN);
                        graphics2D.drawString("Items will NOT get", 735, 460);
                        graphics2D.drawString("consumed on start!", 735, 490);
                    }
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
                    graphics2D.drawString("Statistics", 1750 - achievementsScrollX, 85);
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
                        graphics2D.setColor(i % 2 == 0 ? Color.WHITE : new Color(190, 190, 190));

                        String value = "" + statistic.getValue();

                        if(statistic == Statistics.PLAYTIME) {
                            int seconds = statistic.getValue() % 60;
                            int minutes = (statistic.getValue() / 60) % 60;
                            int hours = statistic.getValue() / 3600;
                            value = hours + "h " + minutes + "m " + seconds + "s";
                        }
                        graphics2D.drawString(statistic.getName() + ": " + value, 1250 - achievementsScrollX, 150 + i * 40 - statisticsScrollY);
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
                    graphics2D.drawString("Achievements (" + achievementPercentage + "%)", x, 85);
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
                }
            }
            case CHALLENGE -> {
                graphics2D.setColor(Color.BLACK);
                graphics2D.fillRect(0, 0, 1080, 640);

                Color deselectedRed = CustomNight.isCustom() ? Color.RED : new Color(140, 0, 0);
                Color gray = CustomNight.isCustom() ? Color.GRAY : new Color(80, 80, 80);

                int enemiesSize = CustomNight.getEnemies().size();
                for (int i = 17; i > -1; i--) {
                    int x = 105 * (i % 6);
                    int y = 130 * (i / 6);

                    if (i >= enemiesSize) {
                        graphics2D.setColor(Color.BLACK);
                        graphics2D.fillRect(20 + x, 40 + y, 95, 120);

                        graphics2D.setColor(new Color(40, 40, 60));
                        graphics2D.setStroke(new BasicStroke(5));
                        graphics2D.drawRect(22 + x, 42 + y, 91, 116);

                        graphics2D.setFont(comicSans30);
                        graphics2D.drawString("X", 27 + x, 152 + y);
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
                        graphics2D.drawImage(icon, 20 + x, 40 + y, null);

                        graphics2D.setStroke(new BasicStroke(2));
                        graphics2D.setColor(Color.BLACK);
                        graphics2D.drawRect(25 + x, 45 + y, 85, 110);

                        graphics2D.setColor(enabled ? Color.RED : gray);
                        graphics2D.setStroke(new BasicStroke(5));
                        graphics2D.drawRect(22 + x, 42 + y, 91, 116);

                        graphics2D.setFont(comicSans30);
                        graphics2D.drawString("" + enemy.getAI(), 27 + x, 152 + y);

                        if (CustomNight.selectedElement == enemy) {
                            graphics2D.setColor(CustomNight.isCustom() ? white100 : white60);
                            graphics2D.fillRect(20 + x, 40 + y, 95, 120);
                        }
                    }
                }

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
                        graphics2D.setFont(new Font("Comic Sans MS", Font.PLAIN, 52 - modifier.getName().length() * 2));
                        graphics2D.drawString(modifier.getName(), 765 + 200 * x - halfTextLength(graphics2D, modifier.getName()), 288 + 60 * y);

                        if (CustomNight.selectedElement == modifier) {
                            graphics2D.setColor(CustomNight.isCustom() ? white100 : white60);
                            graphics2D.fillRoundRect(675 + 200 * x, 248 + 60 * y, 190, 50, 10, 10);
                        }
                    }
                }

                graphics2D.setFont(yuGothicPlain60);

                graphics2D.setColor(CustomNight.startSelected ? Color.WHITE : white160);
                graphics2D.drawString(">> start", 830, 520);
                graphics2D.setColor(CustomNight.backSelected ? Color.WHITE : white160);
                graphics2D.drawString(">> back", 830, 605);

                graphics2D.setColor(Color.WHITE);
                graphics2D.setStroke(new BasicStroke(3));
                graphics2D.drawLine(0, 440, 1080, 440);
                graphics2D.drawLine(658, 0, 658, 440);
                graphics2D.drawLine(810, 440, 810, 640);
                graphics2D.drawLine(658, 221, 1080, 221);

                graphics2D.setColor(CustomNight.isCustom() ? Color.GREEN : Color.RED);
                graphics2D.drawRect(5, 580, 365, 55);
                graphics2D.setColor(Color.WHITE);
                graphics2D.setFont(yuGothicPlain50);
                String custom = "custom: " + (CustomNight.isCustom() ? "on" : "off");
                graphics2D.drawString(custom, 180 - halfTextLength(graphics2D, custom), 625);

                if(CustomNight.customSelected) {
                    graphics2D.setColor(white100);
                    graphics2D.fillRect(3, 578, 370, 60);
                }

                if (CustomNight.getLoadedPreview() != null && CustomNight.selectedElement != null) {
                    graphics2D.drawImage(CustomNight.getLoadedPreview().request(), 660, 0, 420, 220, null);
                }

                graphics2D.setColor(black140);
                graphics2D.fillRect(660, 180, 420, 40);

                graphics2D.setColor(Color.WHITE);
                graphics2D.setFont(comicSans30);
                graphics2D.drawString("Enemies", 270, 35);

                graphics2D.setFont(comicSans50);
                if(CustomNight.isCustom()) {
                    graphics2D.drawString("SHUFFLE", 15, 550);
                } else {
                    graphics2D.drawString("PREV", 15, 550);
                    graphics2D.drawString("NEXT", 795 - textLength(graphics2D, "NEXT"), 550);
                }

                if(!CustomNight.isCustom()) {
                    graphics2D.setFont(new Font("Comic Sans MS", Font.PLAIN, 80 - CustomNight.getSelectedChallengeName().length() * 2));
                    graphics2D.drawString(CustomNight.getSelectedChallengeName(), 405 - halfTextLength(graphics2D, CustomNight.getSelectedChallengeName()), 550);
                }
                graphics2D.setFont(comicSans40);
                String challengeText = CustomNight.isCustom() ? "Custom Night" : ("Challenge " + (CustomNight.getSelectedChallenge() + 1));
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
                    graphics2D.drawString(CustomNight.selectedElement.getName(), 665, 215);
                    graphics2D.setFont(comicSans25);
                }

                graphics2D.setStroke(new BasicStroke());
            }
            case SETTINGS -> {
                graphics2D.drawImage(camStates[0], 0, 0, null);

                drawCloseButton(graphics2D);

                graphics2D.setColor(Color.WHITE);

                graphics2D.setFont(yuGothicPlain60);
                graphics2D.drawString(">> volume", 140, 150 + settingsScrollY);

                graphics2D.drawString(">> fixed ratio", 140, 370 + settingsScrollY);
                graphics2D.drawString(">> headphones", 140, 450 + settingsScrollY);

                graphics2D.drawString("reset night", 160, 560 + settingsScrollY);

                graphics2D.drawString(">> show disclaimer", 140, 690 + settingsScrollY);
                graphics2D.drawString(">> show manual", 140, 770 + settingsScrollY);
                graphics2D.drawString(">> save screenshots", 140, 850 + settingsScrollY);
                graphics2D.drawString(">> RTX super cool bloom", 140, 930 + settingsScrollY);
                graphics2D.drawString(">> fps counter", 140, 1010 + settingsScrollY);
                graphics2D.drawString(">> fps cap: ", 140, 1090 + settingsScrollY);
                graphics2D.drawString(">> jumpscares shake: ", 140, 1170 + settingsScrollY);

                String fpsCap = this.fpsCap + "";
                if(this.fpsCap <= 0) {
                    fpsCap = "UNLIMITED";
                }
                graphics2D.drawString(fpsCap, 500, 1090 + settingsScrollY);

                String shake = "window + screen";
                switch (this.shake) {
                    case 1 -> shake = "screen";
                    case 2 -> shake = "no shake";
                }
                graphics2D.drawString(shake, 240, 1260 + settingsScrollY);

                graphics2D.setStroke(new BasicStroke(5));
                // fixed ratio
                graphics2D.drawRect(550, 310 + settingsScrollY, 60, 60);
                // headphones
                graphics2D.drawRect(612, 390 + settingsScrollY, 60, 60);
                // reset night
                graphics2D.drawRect(146, 500 + settingsScrollY, 328, 80);
                // show disclaimer
                graphics2D.drawRect(720, 630 + settingsScrollY, 60, 60);
                // show manual
                graphics2D.drawRect(640, 710 + settingsScrollY, 60, 60);
                // save screenshots
                graphics2D.drawRect(750, 790 + settingsScrollY, 60, 60);
                // bloom
                graphics2D.drawRect(880, 870 + settingsScrollY, 60, 60);
                // fps counter
                graphics2D.drawRect(590, 950 + settingsScrollY, 60, 60);
                // fps cap
                graphics2D.drawRect(490, 1025 + settingsScrollY, 20 + textLength(graphics2D, fpsCap), 80);
                // jumpscare shake
                graphics2D.drawRect(230, 1200 + settingsScrollY, 20 + textLength(graphics2D, shake), 80);


                if(blackBorders) {
                    graphics2D.fillRect(558, 318 + settingsScrollY, 44, 44);
                }
                if(headphones) {
                    graphics2D.fillRect(620, 398 + settingsScrollY, 44, 44);
                }
                if(disclaimer) {
                    graphics2D.fillRect(728, 638 + settingsScrollY, 44, 44);
                }
                if(showManual) {
                    graphics2D.fillRect(648, 718 + settingsScrollY, 44, 44);
                }
                if(saveScreenshots) {
                    graphics2D.fillRect(758, 798 + settingsScrollY, 44, 44);
                }
                if(bloom) {
                    graphics2D.fillRect(888, 878 + settingsScrollY, 44, 44);
                }
                if(fpsCounters[0]) {
                    graphics2D.fillRect(598, 958 + settingsScrollY, 44, 44);
                }

                graphics2D.setStroke(new BasicStroke());

                graphics2D.setColor(white120);
                if(keyHandler.hoveringNightReset) {
                    graphics2D.fillRect(146, 500 + settingsScrollY, 328, 80);
                }
                if(keyHandler.hoveringFpsCap) {
                    graphics2D.fillRect(490, 1025 + settingsScrollY, 20 + textLength(graphics2D, fpsCap), 80);
                }
                if(keyHandler.hoveringJumpscareShake) {
                    graphics2D.fillRect(230, 1200 + settingsScrollY, 20 + textLength(graphics2D, shake), 80);
                }

                graphics2D.setColor(Color.WHITE);
                graphics2D.fillRect(1070, (int) (-settingsScrollY / 720F * 540F), 10, 100);

                graphics2D.setColor(black120);
                graphics2D.fillRect(0, 600, 1080, 40);

                graphics2D.setColor(white160);
                graphics2D.fillRect(140, 240 + settingsScrollY,800, 8);
                graphics2D.setColor(Color.WHITE);
                graphics2D.fillOval((short) (volume * 800) + 115, 220 + settingsScrollY, 50, 50);

                graphics2D.setFont(comicSansBold25);
                graphics2D.drawString("Shift+Z - Screenshot | Escape - Pause | -+ - Quick volume change", 10, 630);

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
                    }
                }
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

                graphics2D.setColor(Color.WHITE);

                graphics2D.drawImage(dabloon.request(), 15, 185, null);
                graphics2D.setFont(comicSans30);
                graphics2D.drawString(endless.getCoins() + "", 50, 210);

                graphics2D.setFont(yuGothicPlain60);

                if(millyBackButtonSelected) {
                    graphics2D.drawString(">> back", 20, 620);
                } else {
                    BufferedImage icon = millyShopItems[selectedMillyItem].getIcon();
                    Point coords = millyCoordinates.get((int) selectedMillyItem);

                    graphics2D.setColor(white120);
                    graphics2D.fillRoundRect(coords.x - 5, coords.y - icon.getHeight() - 5, icon.getWidth() + 10, icon.getHeight() + 8, 20, 20);
                    graphics2D.drawString(">> back", 20, 620);

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
                    graphics2D.drawString("Price:", 20, 550);
                    graphics2D.setFont(comicSans30);

                    String priceStr = millyShopItems[selectedMillyItem].getPrice() + "";
                    graphics2D.drawString(priceStr, 110, 550);
                    graphics2D.drawImage(dabloon.request(), textLength(graphics2D, priceStr) + 115, 525, null);

                    byte j = 0;
                    while(j < item.getTags().size()) {
                        ItemTag tag = item.getTags().get(j);
                        graphics2D.drawImage(itemTags[tag.getOrder()], 20 + 55 * j, 470, 50, 50, null);
                        j++;
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
                        graphics2D.drawString("[press any key]", 540 - halfTextLength(graphics2D, "[press any key]"), 600);
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
        }

        if(state != GameState.BINGO) {
            if(bingoCard.isGenerated() && !bingoCard.isFailed() && !(bingoCard.isCompleted() && bingoCard.playedOutAnimation)) {
                if(state == GameState.ITEMS) {
                    graphics2D.setComposite(AlphaComposite.SrcOver.derive(0.2F));
                } else {
                    graphics2D.setComposite(AlphaComposite.SrcOver.derive(0.6F));
                }
                graphics2D.drawImage(bingoCardImg, 910, 10, 160, 180, null);
                graphics2D.setFont(yuGothicPlain30);
                graphics2D.setColor(Color.WHITE);
                short x = 910;
                x -= (short) (String.valueOf(bingoCard.getMinutes()).length() * 10);
                x -= (short) (String.valueOf(bingoCard.getSeconds()).length() * 10);

                graphics2D.drawString("Time: " + bingoCard.getMinutes() + "m " + bingoCard.getSeconds() + "s", x, 220);
                graphics2D.setComposite(AlphaComposite.SrcOver.derive(1F));
            }
        }
    }


    boolean maxwellActive = false;

    int kys() {
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

    boolean millyBackButtonSelected = true;

    void redrawMillyShop() {
        Graphics2D graphics2D = (Graphics2D) millyShopImage.getGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        graphics2D.drawImage(loadImg("/game/endless/bg.png"), 0, 0, null);

        if(secondsInMillyShop >= 3600) {
            graphics2D.drawImage(loadImg("/game/endless/bgOverlayMissing.png"), 597, 168, null);
        } else if(Arrays.equals(millyShopItems, new MillyItem[5])) {
            graphics2D.drawImage(loadImg("/game/endless/bgOverlayHappy.png"), 597, 168, null);
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
                Point coords = millyCoordinates.get(i);
                short halfWidth = (short) (icon.getWidth() * 0.5);

                graphics2D.drawImage(icon, coords.x, coords.y - icon.getHeight(), null);
                graphics2D.drawImage(dabloon.request(), coords.x + halfTextLength(graphics2D, priceStr) - 11 + halfWidth, coords.y + 5, null);

                graphics2D.drawString(priceStr, coords.x - halfTextLength(graphics2D, priceStr) - 16 + halfWidth, coords.y + 30);
            }
            i++;
        }

        if(endless.getNight() == 6) {
            if(secondsInMillyShop < 3600) {
                graphics2D.drawImage(resize(birthdayHatImg, 135, 146, BufferedImage.SCALE_SMOOTH), 670, 94, null);
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
        switch (state) {
            case MENU -> {
                graphics2D.drawImage(logo.request(), 30, 30, null);

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

                graphics2D.drawImage(discord, 950, 510, null);

                graphics2D.setColor(white200);
                graphics2D.setFont(comicSansBold25);
                graphics2D.drawString(tip, 10, 630);

                graphics2D.drawString("v" + version, 1075 - versionTextLength, 635);
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
                    graphics2D.drawString(">> start", 730, 590);

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

                String startBingo = ">> start bingo";

                graphics2D.setColor(white200);
                graphics2D.setFont(comicSans60);

                if(bingoCard.isGenerated()) {
                    startBingo = ">> stop bingo";
                }

                if(bingoCard.isCompleted()) {
                    graphics2D.drawString("You completed the Pepingo!", bingoTextLength, 300);
                    startBingo = ">> restart bingo";
                } else if(bingoCard.isFailed()) {
                    graphics2D.drawString("You failed the Pepingo!", bingoTextLength + 20, 300);
                    startBingo = ">> restart bingo";
                }

                graphics2D.setFont(yuGothicPlain60);
                graphics2D.drawString(startBingo, 30, 600);
                graphics2D.setFont(yuGothicPlain50);
                graphics2D.drawString("Time spent: " + bingoCard.getMinutes() + "m " + bingoCard.getSeconds() + "s", 30, 460);

                graphics2D.setColor(white120);
                graphics2D.setFont(yuGothicBoldItalic40);
                graphics2D.drawString("You have 24 minutes!", 30, 510);

                graphics2D.setFont(yuGothicPlain30);
                graphics2D.drawString("Silly little game where", 30, 140);
                graphics2D.drawString("you need to complete all", 30, 180);
                graphics2D.drawString("tasks with a time limit!", 30, 220);
            }
            case PLAY -> {
                drawCloseButton(graphics2D);
            }
            case ACHIEVEMENTS -> {
                drawCloseButton(graphics2D);

                graphics2D.setColor(Color.GRAY);

                if(!achievementState || shiftingAchievements) {
                    double d = (double) (achievementsScrollY) / (30 + Achievements.values().length * 155 - 530);
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
                    graphics2D.drawString("Warning: your night will be set to the FIRST night", 60, 600);
                }
            }
            case GAME -> {
                if(!night.getEvent().isInGame())
                    return;

                try {
                    if (!inCam) {
                        if(night.getMaki().isActive()) {
                            if(night.getMaki().alpha > 0) {
                                BufferedImage img = alphaify(makiWarning.request(), night.getMaki().alpha);
                                Rectangle bounds = night.getDoors().get((int) night.getMaki().getDoor()).getHitbox().getBounds();

                                int makiWarningX = fixedOffsetX - 400 + bounds.x + bounds.width / 2 - 37;
                                int makiWarningY = bounds.y + bounds.height / 2 - 37;
                                graphics2D.drawImage(img, makiWarningX, makiWarningY, null);
                            }
                        }
                        if (night.getMSI().isEnabled()) {
                            if (night.getMSI().isActive()) {
                                if (!night.getMSI().isShadow) {
                                    if (night.getMSI().isHell) {
                                        graphics2D.drawImage(msiImage[2], 300 + fixedOffsetX, 50, null);
                                    } else {
                                        if (night.getMSI().crisscross) {
                                            if (night.getMSI().left) {
                                                graphics2D.drawImage(msiImage[1], 300 + fixedOffsetX, 50, null);
                                            } else {
                                                graphics2D.drawImage(msiImage[0], 300 + fixedOffsetX, 50, null);
                                            }
                                        } else {
                                            graphics2D.drawImage(msiImage[0], 300 + fixedOffsetX, 50, null);
                                        }
                                    }
                                } else {
                                    graphics2D.drawImage(msiImage[3], 300 + fixedOffsetX, 50, null);
                                }
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
                                    graphics2D.drawString("left", 540 - halfTextLength(graphics2D, "left"), 330);
                                } else {
                                    graphics2D.drawString("right", 540 - halfTextLength(graphics2D, "right"), 330);
                                }
                            } else if (night.getMSI().arriving) {
                                graphics2D.setColor(Color.WHITE);
                                graphics2D.setFont(yuGothicPlain120);
                                graphics2D.drawString("loading...", 540 - halfTextLength(graphics2D, "loading..."), 330);
                            }
                        }
                        if(night.getWires().isActive()) {
                            BufferedImage wiresImg = type == GameType.SHADOW ? purplify(this.wiresImg.request()) : this.wiresImg.request();
                            BufferedImage wiresText = type == GameType.SHADOW ? purplify(this.wiresText[1]) : this.wiresText[0];
                            int add = (fixedUpdatesAnim / 60) % 2 * 10;

                            switch (night.getWires().getState()) {
                                case 0 -> {
                                    graphics2D.drawImage(wiresImg, fixedOffsetX - 400 + 790, 95 + add, null);
                                    graphics2D.drawImage(wiresText, fixedOffsetX - 400 + 790, 95 + add, null);
                                }
                                case 1 -> {
                                    graphics2D.drawImage(wiresImg, fixedOffsetX - 400 + 340, 340 + add, null);
                                    graphics2D.drawImage(wiresText, fixedOffsetX - 400 + 340, 340 + add, null);
                                }
                                case 2 -> {
                                    graphics2D.drawImage(wiresImg, fixedOffsetX - 400 + 1080, 120 + add, null);
                                    graphics2D.drawImage(wiresText, fixedOffsetX - 400 + 1080, 120 + add, null);
                                }
                            }
                        }
                        if(night.getScaryCat().isActive()) {
                            float alpha = night.getScaryCat().getAlpha();
                            int index = 0;
                            if(type == GameType.SHADOW) {
                                index = 1;
                            }

                            graphics2D.drawImage(alphaify(scaryCatImage[index].request(), Math.max(0, alpha)), fixedOffsetX - 400 + night.getScaryCat().getX(), 170, null);

                            if(night.getScaryCat().getDistance() < 180 && Math.random() < 0.9) {
                                BufferedImage img = scaryCatMove[index].request();
                                int randomX = (int) (Math.random() * 80 - 40);
                                int randomY = (int) (Math.random() * 20 - 10);
                                graphics2D.drawImage(img, 540 - img.getWidth() / 2 + randomX, 320 - img.getHeight() / 2 + randomY, null);
                            }
                            if(night.getScaryCat().getDistance() < 100 && Math.random() < 0.6) {
                                BufferedImage img = scaryCatWarn[index].request();
                                int randomX = (int) (Math.random() * 10 - 5);
                                int randomY = (int) (Math.random() * 10 - 5);
                                graphics2D.drawImage(img, 540 - img.getWidth() / 2 + randomX, 320 - img.getHeight() / 2 + randomY, null);
                            }

                            if(night.getScaryCat().getCount() > 0) {
                                BufferedImage img = alphaify(scaryCatImage[index].request(), Math.max(0, alpha / 2));
                                for(int i = 0; i < night.getScaryCat().getCount(); i++) {
                                    graphics2D.drawImage(img, (int) (fixedOffsetX - 400 + night.getScaryCat().getX() + Math.random() * 300 - 150), (int) (170 + Math.random() * 300 - 150), null);
                                }

                                graphics2D.setColor(new Color(1 - (index / 2F), 1 - index, 1, Math.max(0, alpha) / (Math.max(1, 4 - night.getScaryCat().getCount()))));
                                graphics2D.fillRect(0, 0, 1080, 640);
                            }
                        }
                        if(night.getJumpscareCat().isActive()) {
                            float z = night.getJumpscareCat().getZoom();
                            BufferedImage image = jumpscareCat.request();
                            if(night.getJumpscareCat().isFading()) {
                                image = alphaify(image, night.getJumpscareCat().getFade());
                            }
                            graphics2D.drawImage(image, 540 - (int) (200 * z), 320 - (int) (150 * z), (int) (400 * z), (int) (300 * z), null);
                        }

                        if(shadowTicket.isEnabled()) {
                            if(Achievements.HALFWAY.isObtained()) {
                                int firstCheckpointX = 540;
                                Point rescaledPoint = new Point((int) ((keyHandler.pointerPosition.x - centerX) / widthModifier), (int) ((keyHandler.pointerPosition.y - centerY) / heightModifier));

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

                                graphics2D.drawString("Halfway Point", firstCheckpointX - halfTextLength(graphics2D, "Halfway Point"), 520);
                                graphics2D.drawString("[click]", firstCheckpointX - halfTextLength(graphics2D, "[click]"), 560);

                                if(reachedAstartaBoss) {
                                    graphics2D.drawImage(checkpointAstartaBoss[0].request(), 604, 163, null);

                                    graphics2D.drawString("ASTARTA TIME", 760 - halfTextLength(graphics2D, "ASTARTA TIME"), 520);
                                    graphics2D.drawString("[click]", 760 - halfTextLength(graphics2D, "[click]"), 560);
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
                        float alpha = Math.max(0F, night.gruggyX) / 500F;

                        for(GruggyCart cart : night.gruggyCarts) {
                            if(alpha < 1) {
                                graphics2D.drawImage(alphaify(gruggyRing.request(), alpha), (int) (fixedOffsetX - 400 + cart.getCurrentX() - 102), 140, null);
                            } else {
                                graphics2D.drawImage(gruggyRing.request(), (int) (fixedOffsetX - 400 + cart.getCurrentX() - 102), 140, null);
                            }
                        }
                    }
                    
                    if(night.isFogModifier()) {
                        int x = - fixedUpdatesAnim * 2;
                        while(x < -2160) {
                            x += 2160;
                        }
                        if(!night.isPerfectStorm()) {
                            graphics2D.drawImage(fog.request(), x, 0, null);
                            graphics2D.drawImage(fog.request(), x + 2160, 0, null);
                        } else {
                            graphics2D.drawImage(lesserFog.request(), x, 0, null);
                            graphics2D.drawImage(lesserFog.request(), x + 2160, 0, null);
                        }
                    }
                    if(night.isRainModifier()) {
                        graphics2D.setColor(Color.BLUE);
                        try {
                            isRainBeingProcessed = true;
                            for (Raindrop raindrop : night.raindrops) {
                                if (raindrop != null) {
                                    graphics2D.fillRect(fixedOffsetX - 400 + raindrop.getX(), raindrop.getY(), 4, 50);
                                }
                            }
                            isRainBeingProcessed = false;
                        } catch (Exception ignored) { }
                    }
                    if(night.getBlizzardTime() > 0) {
                        graphics2D.setColor(Color.WHITE);

                        int yAdd = fixedUpdatesAnim * 2 + (int) (Math.sin(fixedUpdatesAnim * 0.02) * 15);
                        while (yAdd > 120) {
                            yAdd -= 120;
                        }
                        int xAdd = fixedUpdatesAnim;
                        while (xAdd > 200) {
                            xAdd -= 200;
                        }

                        for(int x = -20; x < 1100 + xAdd; x += 200) {
                            for(int y = -20; y < 660; y += 120) {
                                graphics2D.fillOval(x - xAdd, y + yAdd, 20, 20);
                            }
                        }
                        for(int x = -20; x < 1100 + xAdd; x += 200) {
                            for(int y = -60; y < 660; y += 120) {
                                graphics2D.fillOval(x + xAdd, y + yAdd, 20, 20);
                            }
                        }
                    }


                    if(sunglassesOn) {
                        graphics2D.drawImage(sunglassesOverlay.request(), 0, 0, null);
                    }
                    if(inLocker) {
                        graphics2D.drawImage(lockerInsideImg.request(), 0, 0, null);
                    }
                    if(starlightMillis > 0) {
                        graphics2D.drawImage(realVignetteStarlight, 0, 0, null);
                    }
                    if(night.isRadiationModifier()) {
                        if(night.getRadiation() > 80) {
                            graphics2D.drawImage(alphaify(radiationVignette.request(), Math.min(1, (night.getRadiation() - 80) / 20)), 0, 0, null);
                        }
                    }
                    if (headphones) {
                        graphics2D.setColor(currentLeftPan);
                        graphics2D.fillRect(39, 345, 71, 193);
                        graphics2D.setColor(currentRightPan);
                        graphics2D.fillRect(1014, 361, 66, 190);

                        graphics2D.drawImage(headphonesImg.request(), 0, 0, null);
                    }



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
                                    graphics2D.fillRect(220 + x, 495, 25, 110);
                                }
                            }

                            graphics2D.setColor(new Color(255, 216, 0));

                            int x = fixedUpdatesAnim * 5 + (int) (Math.sin(fixedUpdatesAnim * 0.05) * 14);
                            while (x > 630) {
                                x -= 630;
                            }
                            graphics2D.fillRect(220 + x, 485, 10, 130);

                            graphics2D.drawImage(charge[night.generatorStage >= 1 ? 1 : 0].request(), 403, 420 - ((fixedUpdatesAnim / 40) % 2 == 0 ? 10 : 0), null);
                            graphics2D.drawImage(charge[night.generatorStage >= 2 ? 1 : 0].request(), 480, 420 - ((fixedUpdatesAnim / 40) % 2 == 0 ? 10 : 0), null);
                            graphics2D.drawImage(charge[night.generatorStage >= 3 ? 1 : 0].request(), 557, 420 - ((fixedUpdatesAnim / 40) % 2 == 0 ? 10 : 0), null);
                            graphics2D.drawImage(charge[night.generatorStage >= 4 ? 1 : 0].request(), 634, 420 - ((fixedUpdatesAnim / 40) % 2 == 0 ? 10 : 0), null);

                            graphics2D.drawImage(connectText.request(), 140, 280 - (((int) ((fixedUpdatesAnim / 80F))) % 2 == 0 ? 5 : 0), null);
                        }
                    }

                    if(night.isTimerModifier()) {
                        if (timerY > -230) {
                            graphics2D.drawImage(timerBoard.request(), 10, Math.min(10, timerY), null);
                            Color oldColor = graphics2D.getColor();
                            graphics2D.setColor(Color.GREEN);
                            graphics2D.setFont(comicSans40);
                            short order = 0;
                            for (Integer number : night.getDoors().keySet()) {
                                Door door = night.getDoors().get(number);

                                if(night.getTimers().containsKey(door)) {
                                    graphics2D.drawString("Door " + number + " - " + (Math.round(Math.max(0, night.getTimers().get(door)) * 100F) / 100F), 25, Math.min(10, timerY) + order * 40 + 100);
                                    order++;
                                }
                            }
                            graphics2D.setColor(oldColor);
                        }
                    }

                    boolean noSignalFromHeat = night.getNoSignalFromHeat();

                    if (night.getEvent().isGuiEnabled() && !(inCam && noSignalFromHeat)) {
                        if (!inCam) {
                            if (announcerOn) {
                                float opacity = (float) Math.sin(Math.toRadians(announceCounter));

                                if (opacity <= 0) {
                                    announcerOn = false;
                                    announceCounter = 1;
                                } else {
                                    graphics2D.setFont(yuGothicPlain80);
                                    graphics2D.setColor(new Color(255, 255, 255, Math.round(255 * opacity)));
                                    graphics2D.drawString(nightAnnounceText, 540 - halfTextLength(graphics2D, nightAnnounceText), 330);
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
                        } else {
                            graphics2D.drawImage(cam, 0, 0, 1080, 640, null);

                            if (portalTransporting) {
                                if (riftTint > 0) {
                                    graphics2D.setColor(new Color(0, 0, 0, riftTint));
                                    graphics2D.fillRect(0, 0, 1080, 640);
                                }

                                graphics2D.setColor(Color.WHITE);
                                graphics2D.setFont(yuGothicPlain120);
                                graphics2D.drawString("transporting...", 540 - halfTextLength(graphics2D, "transporting..."), 330);
                            }
                            if (night.getAstarta().arrivalSeconds < 4 && night.getAstarta().arrivalSeconds > 0) {
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

                            graphics2D.setColor(new Color(0, 0, 0, 100));
                            graphics2D.fillRect(0, 0, 1080, 640);

                            graphics2D.drawImage(hyperCam.request(), 130, 50, null);
                            graphics2D.drawImage(cam1A, 720, 340, null);

                            if (adblocker.isEnabled() && !adBlocked) {
                                graphics2D.drawImage(smallAdblockerImage, 50, 50, null);
                            }
                        }

                        graphics2D.setFont(sansSerifPlain40);

                        if (night.hasPower()) {
                            graphics2D.drawImage(usageImage, 0, 0, null);

                            graphics2D.setColor(white160);
                            graphics2D.drawString("Battery:", 40, 600);
                            graphics2D.drawString((short) (night.getEnergy() * 0.2) + "%", energyX, 600);
                        } else if(night.isPowerModifier()) {
                            if(night.getGeneratorEnergy() > 0) {
                                graphics2D.setColor(white160);
                                graphics2D.drawString("Generator Fuel:", 40, 600);
                                graphics2D.drawString((short) (night.getGeneratorEnergy() * 0.2) + "%", (short) (50 + textLength(graphics2D, "Generator Fuel:")), 600);
                            }
                        }
                        if (type.isEndless()) {
                            graphics2D.drawString(endless.getCoins() + "", 75, 522);
                            graphics2D.drawImage(dabloon.request(), 40, 495, null);
                        }

                        graphics2D.setFont(sansSerifPlain70);
                        graphics2D.drawString(night.getClockString(), 860, 70);

                    } else if(inCam) {
                        graphics2D.drawImage(noSignal, 350, 300, null);
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
                            graphics2D.drawString("Maxwell the Cat", 700, 90);
                            graphics2D.drawString("button", 700, 140);
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
                            graphics2D.drawString("Transport into", 700, 90);
                            graphics2D.drawString("Shadownight", 700, 140);
                        }
                    } else { // if not in cam
                        if (night.getEvent() == GameEvent.FLOOD) {
                            graphics2D.setColor(white200);
                            graphics2D.setFont(comicSansBoldItalic40);
                            int add = (int) (Math.round(Math.random()));
                            graphics2D.drawString("uh oh! looks like the office got flooded again!", floodTextLength1, 130 + add);
                            graphics2D.drawString("beware of sharks!", floodTextLength2, 170 + add);

                            if (night.getShark().isActive()) {
                                graphics2D.setColor(Color.GREEN);
                                graphics2D.setStroke(new BasicStroke(6));
                                graphics2D.drawRect(fixedOffsetX + night.getShark().getX() - 400, 480, 234, 154);
                                graphics2D.setFont(comicSansBoldItalic40);
                                graphics2D.drawString("move the", fixedOffsetX + night.getShark().getX() - 390, 520);
                                graphics2D.drawString("fish away!", fixedOffsetX + night.getShark().getX() - 390, 620);
                                graphics2D.setFont(comicSans80);
                                graphics2D.drawString("" + night.getShark().getLeftBeforeBite(), fixedOffsetX + night.getShark().getX() - 300, 590);
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
                            if (night.getLemonadeCat().isActive()) {
                                graphics2D.drawImage(lemonadeGato, fixedOffsetX - 400 + night.getLemonadeCat().getX(), (int) (400 - Math.sin(night.getLemonadeCat().getY()) * 150), null);

                                for (byte i = 0; i < 4; i++) {
                                    float zoom = night.getLemonadeCat().lemonadeZoom[i];
                                    Point point = night.getLemonadeCat().lemonadePos[i];

                                    if (zoom != 0) {
                                        if (zoom > 0.1) {
                                            graphics2D.drawImage(lemon.request(), fixedOffsetX + point.x - (int) (200 * zoom), (int) (point.y - 150 * zoom),
                                                    (int) (400 * zoom), (int) (300 * zoom), null);
                                        }
                                    }
                                }
                            }

                            graphics2D.setColor(white200);
                            graphics2D.setFont(comicSansBoldItalic40);
                            int add = (int) (Math.round(Math.random()));
                            graphics2D.drawString("uh oh! the lemonade cat appears!", 220, 130 + add);
                            graphics2D.drawString("right click to give him some lemons!", 195, 170 + add);

                            graphics2D.drawString("Tries: " + (3 - night.getLemonadeCat().getCurrentTry()), 30, 600);
                        }
                        if(type == GameType.DAY) {
                            if(endless.getNight() == 3) {
                                graphics2D.setColor(white200);
                                graphics2D.setFont(comicSansBoldItalic40);
                                int add = (int) (Math.round(Math.random()));
                                graphics2D.drawString("Something changed in your camera...", 540 - halfTextLength(graphics2D, "Something changed in your camera..."), 170 + add);
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
                            graphics2D.drawString(manualY < 535 ? "hide" : "show", 620 + 215, manualY + 73);
                            graphics2D.drawString("close", 620 + 315, manualY + 73);
                        }
                    }

                    if (night.getBoykisser().isActive()) {
                        graphics2D.drawImage(boykisserImg.request(), 0, 0, null);
                    }

                    if(night.getType() == GameType.SHADOW && !night.getGlitcher().getShadowGlitches().isEmpty()) {
                        for(Point point : night.getGlitcher().getShadowGlitches()) {
                            graphics2D.drawImage(shadowGlitch.request(), fixedOffsetX - 400 + point.x, point.y, 330, 120, null);
                        }
                    }

                    if(birthdayMaxwell.isEnabled()) {
                        if(!night.getClockString().equals("4 AM")) {
                            if(night.getEvent().isInGame()) {
                                graphics2D.setColor(white200);
                                graphics2D.setFont(comicSans60);
                                int add = (int) (Math.round(Math.random()));

                                graphics2D.drawString("Goal: Survive until 4 AM", goalTextLength1, 240 + add);
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
                            graphics2D.drawString("Shadow Astarta", 395, 45);
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
                if(endless.getNight() == 6) {
                    graphics2D.drawImage(millyShopColorsChanging, 0, 0, null);
                }
            }
            case PLATFORMER -> {
                graphics2D.setColor(Color.CYAN);
                graphics2D.fillRect(0, 0, 1080, 640);

                for(int x = 0; x < platformer.getWidth(); x++) {
                    for(int y = 0; y < platformer.getHeight(); y++) {
                        if(platformer.map[x][y] == 1) {
                            graphics2D.setColor(Color.BLACK);
                            graphics2D.fillRect(((x) * 48) + 320, ((y) * 48) + 320, 48, 48);
                        }
                    }
                }
                graphics2D.setColor(Color.RED);
                graphics2D.fillRect((int) (platformer.getX() * 48) + 320, (int) (platformer.getY() * 48 - 48) + 320, 48, 48);
            }
        }

        graphics2D.setFont(comicSansBoldItalic40);

        for(byte i = 0; i < StaticLists.notifs.size(); i++) {
            Notification notification = StaticLists.notifs.get(i);

            graphics2D.setColor(new Color(255, 255, 255, Math.round(notification.progress)));
            graphics2D.drawString(notification.string, 200, 500 - Math.round(notification.progress));
        }

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

            graphics2D.drawString("unlocked achievement", 770, posY - 10);

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

        for(int i = 0; i < Achievements.values().length; i++) {
            if(30 + i * 155 - achievementsScrollY > 530)
                break;
            if(30 + i * 155 - achievementsScrollY < -135)
                continue;

            Achievements achievement = Achievements.values()[i];

            BufferedImage icon = lockedAchievementImg;
            if(achievement.isObtained()) {
                icon = achievement.getIcon();
            }
            graphics2D.drawImage(icon, 10, 30 + i * 155 - achievementsScrollY, 135, 135, null);

            int lastY = 0;
            graphics2D.setColor(Color.WHITE);
            graphics2D.setFont(comicSans50);

            for(String name : cropText(achievement.getName(), 750, graphics2D)) {
                graphics2D.drawString(name, 155, 65 + i * 155 + lastY - achievementsScrollY);
                lastY += 40;
            }

            graphics2D.setColor(white200);
            graphics2D.setFont(comicSans30);
            String[] description = cropText(achievement.getDescription(), 750, graphics2D);

            if(achievement.isHidden() && !achievement.isObtained()) {
                description = new String[] {"This achievement is hidden..."};
            }

            lastY -= 5;
            for(String desc : description) {
                graphics2D.drawString(desc, 155, 65 + i * 155 + lastY - achievementsScrollY);
                lastY += 30;
            }
        }

        graphics2D.dispose();

        achievementDisplay = new BufferedImage(920, 530, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2DNew = (Graphics2D) achievementDisplay.getGraphics();

        graphics2DNew.drawImage(camStates[0].getSubimage(20, 110, 920, 530), 0, 0, null);
        graphics2DNew.drawImage(achievementDisplayARGB, 0, 0, null);

        graphics2DNew.dispose();
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

        newMenuButtons.add(">> play");
        if(unlockedBingo) {
            newMenuButtons.add(">> bingo");
        }
        newMenuButtons.add(">> achievements");
        newMenuButtons.add(">> settings");

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


    BufferedImage usageImage = new BufferedImage(540, 640, BufferedImage.TYPE_INT_ARGB);

    public void redrawUsage() {
        usageImage = new BufferedImage(540, 640, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = (Graphics2D) usageImage.getGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

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

                graphics2D.fillRect(190 + i * 30, 528, 20, 35);
                i++;
            }
        }

        graphics2D.setColor(white160);
        graphics2D.setFont(sansSerifPlain40);
        graphics2D.drawString("Usage:", 40, 560);

        graphics2D.dispose();
    }

    public Point getPointerPosition() {
        return keyHandler.pointerPosition;
    }

    String tip = "";

    Font sansSerifPlain40;
    Font sansSerifPlain70 = new Font(Font.SANS_SERIF, Font.PLAIN, 70);

    short energyX;
    short equippedX;
    short floodTextLength1;
    short floodTextLength2;
    short goalTextLength1;
    short bingoTextLength;

    private void initializeFontMetrics() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = (Graphics2D) image.getGraphics();

        sansSerifPlain40 = new Font(Font.SANS_SERIF, Font.PLAIN, 40);
        graphics2D.setFont(sansSerifPlain40);
        energyX = (short) (50 + textLength(graphics2D, "Battery:"));

        graphics2D.setFont(yuGothicBold25);
        equippedX = (short) (890 - halfTextLength(graphics2D, "EQUIPPED"));

        graphics2D.setFont(comicSansBold25);
        versionTextLength = (short) (halfTextLength(graphics2D, "v" + version) * 2);

        graphics2D.setFont(comicSansBoldItalic40);
        floodTextLength1 = (short) (540 - halfTextLength(graphics2D, "uh oh! looks like the office got flooded again!"));
        floodTextLength2 = (short) (540 - halfTextLength(graphics2D, "beware of sharks!"));

        graphics2D.setFont(comicSans60);
        goalTextLength1 = (short) (540 - halfTextLength(graphics2D, "Goal: Survive until 4 AM"));

        graphics2D.setFont(comicSans60);
        bingoTextLength = (short) (540 - halfTextLength(graphics2D, "You completed the Pepingo!"));

        graphics2D.dispose();
    }


    Cutscene currentCutscene;
    short fixedOffsetX = 0;

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
                if(loading && seconds < 4) {
//                    graphics2D.setColor(Color.GRAY);
//                    graphics2D.drawString("game contains a bit of flashing", 60, 500);
                }
                if(paused) {
                    graphics2D.drawImage(lastBeforePause, 0, 0, null);
                    graphics2D.setColor(Color.WHITE);
                    graphics2D.drawString("paused", 130, 180);

                    if(keyHandler.previous == GameState.GAME) {
                        if(night.getEvent().isInGame()) {
                            graphics2D.setColor(white160);
                            if (pauseDieSelected) {
                                graphics2D.setColor(Color.WHITE);
                            }
                            graphics2D.setFont(yuGothicPlain60);

                            graphics2D.drawString(">> die", 110, 560);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            fixedOffsetX = offsetX;

            firstHalf(graphics2D);

            // tint
            int realTint = (int) (tintAlpha);
            if(currentFlicker > 0) {
                realTint = (int) Math.min(255, tintAlpha + currentFlicker);
            }
            if(state == GameState.GAME) {
                if(night.getMSI().isActive()) {
                    realTint = Math.min(255, realTint + night.getMSI().getAdditionalTint());
                }
            }
            graphics2D.setColor(new Color(0, 0, 0, realTint));
            graphics2D.fillRect(0, 0, 1080, 640);

            secondHalf(graphics2D);

            if (staticTransparency > 0F) {
                // static
                graphics2D.drawImage(currentStaticImg, 0, 0, 1080, 640, null);
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
                graphics2D.drawString("volume", 570, quickVolumeY + 100);
            }

            if (state == GameState.GAME) {
                try {
                    if(night.randomSogAlpha > 0) {
                        graphics2D.drawImage(alphaify(randomsog.request(), night.randomSogAlpha), 0, 0, 1080, 640, null);
                    }
                    if(night.getAstartaBoss() != null) {
                        if(night.getAstartaBoss().dvdEventSeconds > 0) {
                            int x = (int) (fixedOffsetX - 400 + night.getAstartaBoss().getDvdPosX());
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
                            boolean isNightApproporiate = (endless.getNight() >= 3 && night.getType() == GameType.DAY) || (endless.getNight() >= 4 && night.getType() == GameType.ENDLESS_NIGHT);
                            if (isNightApproporiate && outOfLuck && !portalActive) {
                                graphics2D.setColor(new Color(100, 0, 180));
                                graphics2D.setFont(comicSans60);
                                graphics2D.drawString("out of", 830, 340);
                                graphics2D.drawString("luck", 830, 400);
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
                                graphics2D.drawString("You got the adblocker!", 10, 630);
                            } else {
                                graphics2D.setColor(new Color(200, 50, 50, 200));
                                graphics2D.drawString("You found an adblocker! Secure it until 4 AM to get it.", 10, 630);
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
                            if(night.getMSI().isActive() || night.getShark().isActive()) {
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
                        graphics2D.drawImage(stopSign, night.getA90().x - 50, night.getA90().y - 50, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (night.getEvent() == GameEvent.DYING) {
                    if (!drawCat) {
                        int x = 0;
                        int y = 0;

                        if(shake < 2 && !killedBy.equals("killed by Shadow Pépito")) {
                            x -= (int) (Math.random() * 16 - 8);
                            y -= (int) (Math.random() * 16 - 8);
                        }
                        int width = 1080;
                        int height = 640;

                        if(killedBy.equals("killed by Astarta")) {
                            x -= astartaJumpscareCounter;
                            y -= (int) (astartaJumpscareCounter * 0.5);
                            width += astartaJumpscareCounter * 2;
                            height += (int) (astartaJumpscareCounter * 1.5);
                        } else if(killedBy.equals("killed by Shadow Pépito")) {
                            x -= astartaJumpscareCounter;
                            y -= (int) (astartaJumpscareCounter * 0.5);
                            width += astartaJumpscareCounter * 2;
                            height += (int) (astartaJumpscareCounter * 1.5);
                        }

                        graphics2D.drawImage(jumpscare, x, y, width, height, null);
                    } else {
                        graphics2D.drawImage(restInPeice.request(), 378, 185 - deathScreenY, null);
                        graphics2D.setColor(Color.WHITE);
                        graphics2D.setFont(comicSans50);
                        graphics2D.drawString(killedBy, 540 - halfTextLength(graphics2D, killedBy), 520 - deathScreenY);

                        if (killedBy.contains("silly cat")) {
                            graphics2D.drawImage(sobEmoji.request(), 776, 476 - deathScreenY, null);
                        }
                        if (killedBy.contains("killed by Shadow Astarta")) {
                            graphics2D.drawImage(astartaSticker, 378, 155 - deathScreenY, null);
                            // custom astarta deathscreen
                        }
                        if (pressAnyKey) {
                            if (type == GameType.ENDLESS_NIGHT) {
                                graphics2D.drawString("Score: " + endless.getNight() + " | Best: " + recordEndlessNight,
                                        540 - halfTextLength(graphics2D, "Score: " + endless.getNight() + " | Best: " + recordEndlessNight), 165 - deathScreenY);
                            }

                            graphics2D.setFont(comicSans40);
                            graphics2D.drawString("[press any key]", 540 - halfTextLength(graphics2D, "[press any key]"), 570 - deathScreenY);

                            if (birthdayMaxwell.isEnabled()) {
                                graphics2D.drawString("YOU KEPT YOUR MAXWELL!!!", 40, 80 - deathScreenY);
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
                                graphics2D.drawString("[press any key]", 540 - halfTextLength(graphics2D, "[press any key]"), 1250 - deathScreenY);
                            }
                        }
                    }
                } else if (night.getEvent() == GameEvent.WINNING) {
                    if(everySecond20th.containsKey("stopSimulation")) {
                        graphics2D.drawImage(lastWinScreen, 0, 0, 1080, 640, null);
                    } else {

                        graphics2D.setColor(Color.WHITE);
                        graphics2D.setFont(comicSans80);
                        graphics2D.drawString("gg you wonned", 540 - halfTextLength(graphics2D, "gg you wonned"), 300);
                        graphics2D.setFont(comicSans60);
                        graphics2D.drawString("6 am :clock:", 540 - halfTextLength(graphics2D, "6 am :clock:"), 230);
                        if (pressAnyKey) {
                            graphics2D.drawString("press any key!!!!", 540 - halfTextLength(graphics2D, "press any key!!!!"), 370);
                            graphics2D.drawImage(strawber.request(), 60, 390, null);

                            graphics2D.setFont(comicSans60);
                            graphics2D.setColor(Color.getHSBColor(currentRainbow, 1, 1));

                            if (allNighter) {
                                graphics2D.drawString("You beat the entire 4 nights!!!", 20, 100);
                            } else if (type == GameType.PREPARTY) {
                                graphics2D.drawString("You beat Pépito's Party Preparations!", 20, 100);
                            } else if (type == GameType.PARTY) {
                                graphics2D.drawString("You beat Night 666 - Pépito's Party!!!", 20, 100);
                                graphics2D.setFont(comicSans40);
                                graphics2D.drawString("reward: x1 party maxwell", 50, 150);
                            }
                            if (type == GameType.CUSTOM && CustomNight.isCustom()) {
                                int points = 0;
                                for (CustomNightEnemy enemy : CustomNight.getEnemies()) {
                                    points += 100 * enemy.getAI();
                                }
                                int multiplier = 1;
                                for (CustomNightModifier modifier : CustomNight.getModifiers()) {
                                    if (modifier.isActive()) {
                                        multiplier++;
                                    }
                                }
                                points *= multiplier;

                                graphics2D.setFont(sansSerifPlain70);
                                graphics2D.drawString(points + " points", 420, 600);
                            }

                            if (gg) {
                                graphics2D.setFont(comicSans80);
                                graphics2D.setColor(Color.ORANGE);
                                graphics2D.drawString("GG", 790, 500);
                            }
                        }
                    }
                }
            }

            if(riftTransparency > 0) {
                graphics2D.setComposite(AlphaComposite.SrcOver.derive(riftTransparency));
                graphics2D.drawImage(riftTransition, 0, 0, 1080, 640, null);
                graphics2D.setComposite(AlphaComposite.SrcOver.derive(1F));
            }

            if(state == GameState.MILLY) {
                if(secondsInMillyShop >= 3600) {
                    if(dreadUntilGrayscale <= 0) {
                        unshaded = grayscale(unshaded);
                        shakeX += (int) (Math.random() * 10 - 5);
                        shakeY += (int) (Math.random() * 6 - 3);
                    } else {
                        BufferedImage unshadedCopy = grayscale(unshaded);
                        graphics2D.drawImage(alphaify(unshadedCopy, Math.min(1, 1 - dreadUntilGrayscale)), 0, 0, null);
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

        graphics2D.dispose();

        super.paintComponent(preGraphics);

        Graphics2D overGraphics2D = (Graphics2D) preGraphics;
        overGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        overGraphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        if(mirror) {
            unshaded = mirror(unshaded, 1);
        }

        if(state == GameState.GAME) {
            if (night.getEvent().isInGame() && night.getTemperature() > 20) {
                unshaded = wobble(unshaded, (night.getTemperature() - 20) / 5F, 4, 0.02F, 1);
            }
            if(night.getJumpscareCat().getShake() > 0) {
                int shX = night.getJumpscareCat().getShake();
                int shY = night.getJumpscareCat().getShake() / 2;
                shakeX += (int) (Math.random() * shX) - shX / 2;
                shakeY += (int) (Math.random() * shY) - shY / 2;
            }
            if(night.getMSI().isActive()) {
                int shX = night.getMSI().getShake();
                int shY = night.getMSI().getShake() / 2;
                shakeX += (int) (Math.random() * shX - shX / 2);
                shakeY += (int) (Math.random() * shY - shY / 2);
            }
            if(night.getElAstarta().isActive()) {
                int shX = (int) (night.getElAstarta().getShake() / 1.5);
                int shY = night.getElAstarta().getShake() / 3;
                shakeX += (int) (Math.random() * shX - shX / 2);
                shakeY += (int) (Math.random() * shY - shY / 2);

                if(night.getElAstarta().getShake() > 8) {
                    int cos = (int) (Math.cos(fixedUpdatesAnim * 0.05) * 3 * (night.getElAstarta().getShake() - 8));
                    int sin = (int) (Math.sin(fixedUpdatesAnim * 0.05) * 1.5 * (night.getElAstarta().getShake() - 8));
                    unshaded = offset(unshaded, cos, sin);
                }
            }
            if(night.getAstartaBoss() != null) {
                if (night.getAstartaBoss().jumpscareBrightness > 1) {
                    RescaleOp op = new RescaleOp(night.getAstartaBoss().jumpscareBrightness, night.getAstartaBoss().jumpscareOffset, null);
                    BufferedImage newImage = new BufferedImage(540, 320, BufferedImage.TYPE_INT_RGB);
                    op.filter(resize(unshaded, 540, 320, BufferedImage.SCALE_FAST), newImage);
                    unshaded = resize(newImage, 1080, 640, BufferedImage.SCALE_FAST);
                }
                if(night.getAstartaBoss().getDvdShake() > 0) {
                    int shX = (int) night.getAstartaBoss().getDvdShake();
                    int shY = (int) (night.getAstartaBoss().getDvdShake() / 2);
                    shakeX += (int) (Math.random() * shX - shX / 2);
                    shakeY += (int) (Math.random() * shY - shY / 2);
                }
            }
            if(night.getShadowPepito() != null) {
                if (night.getShadowPepito().jumpscareBrightness > 1) {
                    RescaleOp op = new RescaleOp(night.getShadowPepito().jumpscareBrightness, night.getShadowPepito().jumpscareOffset, null);
                    BufferedImage newImage = new BufferedImage(540, 320, BufferedImage.TYPE_INT_RGB);
                    op.filter(resize(unshaded, 540, 320, BufferedImage.SCALE_FAST), newImage);
                    unshaded = resize(newImage, 1080, 640, BufferedImage.SCALE_FAST);
                }
            }
        }

        if(shakeX > 0 || shakeY > 0) {
            unshaded = offset(unshaded, shakeX, shakeY);
        }

        if(bloom) {
            BufferedImageOp blur = getGaussianBlurFilter(10, true);
            BufferedImage blurred = blur.filter(resize(unshaded, 270, 160, Image.SCALE_FAST), null);
            BufferedImageOp blur2 = getGaussianBlurFilter(10, false);
            blurred = resize(blur2.filter(blurred, null), 1080, 640, Image.SCALE_SMOOTH);

            for(int x = 0; x < unshaded.getWidth(); x++) {
                for(int y = 0; y < unshaded.getHeight(); y++) {
                    Color oldColor = new Color(blurred.getRGB(x, y));
                    Color newColor = new Color(unshaded.getRGB(x, y));

                    unshaded.setRGB(x, y, new Color(Math.min(255, oldColor.getRed() + newColor.getRed()), Math.min(255, oldColor.getGreen() + newColor.getGreen()), Math.min(255, oldColor.getBlue() + newColor.getBlue())).getRGB());
                }
            }
        }

        lastFullyRenderedUnshaded = unshaded;

        if(pixelation > 1) {
            overGraphics2D.drawImage(unshaded.getScaledInstance(1080 / (int) pixelation, 640 / (int) pixelation, Image.SCALE_FAST), centerX, centerY, (int) (1080 * widthModifier), (int) (640 * heightModifier), null);
        } else {
            overGraphics2D.drawImage(unshaded, centerX, centerY, currentWidth, currentHeight, null);
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

        if(debugMode) {
            String boba = getBoba();
            overGraphics2D.drawString(boba, (int) (5 * widthModifier) + centerX, (int) (630 * heightModifier) + centerY);
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
                i++;
            }

            i--;
            overGraphics2D.fillRect(390, (40 * i + 30), 5, 40);
        }

        overGraphics2D.setStroke(new BasicStroke());
        overGraphics2D.setColor(Color.WHITE);
        overGraphics2D.fillOval(pointX - (int) (5 * widthModifier), pointY - (int) (5 * heightModifier), (int) (10 * widthModifier), (int) (10 * heightModifier));

        if(state == GameState.GAME) {
            if(night.getLemonadeCat().isActive()) {
                float r = night.getLemonadeCat().getRotation();
                float z = night.getLemonadeCat().getCursorZoom();

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
        }

        fpscnt.frame();

        overGraphics2D.dispose();
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


    float widthModifier = width / 1080.0F;
    float heightModifier = height / 640.0F;
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

    public BufferedImage cam;

    public String nightAnnounceText = "night one";
    byte currentNight = 1;

    boolean announcerOn = false;
    short announceCounter = 1;

    public void announceNight(byte night, GameType type) {
        if(type == GameType.ENDLESS_NIGHT) {
            announceChallenger(night, 5000);
        }

        announceCounter = 1;

        String string = "night ";

        switch (night) {
            case 1 -> string += "one";
            case 2 -> string += "two";
            case 3 -> string += "three";
            case 4 -> string += "four";
            case 5 -> string += "five";
            case 6 -> string += "six";
            case 7 -> string += "seven";
            default -> string += night;
        }
        switch (type) {
            case SHADOW -> string = "SHADOWNIGHT";
            case PREPARTY -> string = "Pépito's Party Preparations";
            case PARTY -> string = "night 666";
            case CUSTOM -> {
                if(CustomNight.isCustom()) {
                    string = "custom night";
                } else {
                    string = "challenge " + (CustomNight.selectedChallenge + 1);
                }
            }
        }

        nightAnnounceText = string;
        announcerOn = true;

        sound.playRate("nightStart", 0.08, 1);
    }

    public void announceChallenger(byte night, int delay) {
        challengerAlpha = 0;
        challengerString = "A NEW CHALLENGER HAS APPEARED!";

        challenger = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = (Graphics2D) challenger.getGraphics();

        if(type == GameType.ENDLESS_NIGHT) {
            if ((night > 1 && night < 9) || night == 18) {
                switch (night) {
                    case 2 -> {
                        graphics2D.drawImage(completelyBlack(astartaCam[1]), 160, 150, 400, 500, null);
                        graphics2D.drawImage(completelyBlack(msiImage[0]), 520, 150, 400, 600, null);
                    }
                    case 3 -> graphics2D.drawImage(completelyBlack(sharkImg.request()), 330, 150, 400, 500, null);
                    case 4 -> graphics2D.drawImage(completelyBlack(boykisserImg.request()), 0, 0, 1080, 640, null);
                    case 5 -> {
                        graphics2D.drawImage(completelyBlack(makiCam), 230, 110, 270, 550, null);
                        graphics2D.drawImage(completelyBlack(lemonadeGato), 520, 170, 400, 470, null);
                    }
                    case 6 -> graphics2D.drawImage(completelyBlack(mirrorCatImg), 160, 120, 750, 400, null);
                    case 7 -> challengerString = "PEACE!";
                    case 18 -> graphics2D.drawImage(completelyBlack(msiImage[3]), 340, 40, 400, 600, null);
                    default -> {
                        graphics2D.dispose();
                        return;
                    }
                }
            } else {
                graphics2D.dispose();
                return;
            }
        } else if(type == GameType.SHADOW) {
            if(night == 60 || night == 61) {
                challengerString = "NEW CHALLENGERS APPEAR!";
            }
        } else {
            graphics2D.dispose();
            return;
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

        String string = "day ";

        switch (endless.getNight()) {
            case 1 -> string += "one";
            case 2 -> string += "two";
            case 3 -> string += "three";
            case 4 -> string += "four";
            case 5 -> string += "five";
            case 6 -> string += "six";
            case 7 -> string += "seven";
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
        PlayMenuElement challenge = new PlayMenuElement("challenge", loadImg("/menu/play/challenge.png"), "Challenge", ">> play");
        PlayMenuElement normal = new PlayMenuElement("normal", loadImg("/menu/play/normal.png"), "Normal", currentNight == 1 ? ">> play" : ">> play - night " + currentNight);
        PlayMenuElement endless = new PlayMenuElement("endless", loadImg("/menu/play/endless.png"), "Endless", recordEndlessNight == 0 ? ">> play" : ">> play - best " + recordEndlessNight);

        PlayMenu.list = List.of(challenge, normal, endless);

        PlayMenu.selectOffsetX = PlayMenu.getGoalSelectOffsetX();
        PlayMenu.movedMouse = false;

        state = GameState.PLAY;
        sound.play("select", 0.1);

        staticTransparency = 0.02F;
        endStatic = 0.02F;

        fadeOut(255, 0, 3);

        recalculateButtons(GameState.PLAY);
    }

    void startItemSelect() {
        itemLimit = 3;
        state = GameState.ITEMS;
        sound.play("select", 0.1);

        for(Item item : fullItemList) {
            item.deselect();
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

        if(type == GameType.CUSTOM) {
            graphics2D.setColor(Color.BLACK);
            graphics2D.fillRect(0, 0, 1080, 640);
        } else {
            graphics2D.drawImage(camStates[0], 0, 0, null);
        }

        graphics2D.setColor(black80);
        graphics2D.fillRoundRect(725, 50, 330, 460, 50, 50);

        graphics2D.setColor(Color.WHITE);
        graphics2D.fillRect(790, 145, 210, 3);

        if(type == GameType.CUSTOM) {
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

                if(type == GameType.CUSTOM) {
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
    BingoCard bingoCard = new BingoCard(this);

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

            String raw = task.getName();
            String[] rawLines = raw.split(" ");

            List<String> lines = new ArrayList<>();
            String currentLine = "";

            int j = 0;
            while(j < rawLines.length) {
                currentLine += rawLines[j] + " ";

                String lengthLine = currentLine;
                if(rawLines[j].length() > 8) {
                    lengthLine = lengthLine.trim();
                }
                if(halfTextLength(graphics2D, lengthLine) > 25) {
                    lines.add(currentLine);
                    currentLine = "";
                }
                if(j == rawLines.length - 1) {
                    lines.add(currentLine);
                }
                j++;
            }

            int k = 0;
            for(String string : lines) {
                graphics2D.drawString(string, 20 + x * 128, 100 + y * 128 + k * 28);
                k++;
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

        redrawAchievements();
    }

    void startChallengeMenu(boolean stopMusic) {
        if(!CustomNight.isCustom()) {
            CustomNight.setSelectedChallenge(CustomNight.getMaxChallenge());
            CustomNight.setEntityAIs();
        }
        state = GameState.CHALLENGE;
        sound.play("select", 0.1);

        if(stopMusic) {
            music.stop();
            music.play("tension", 0.05, true);
        }

        staticTransparency = 0F;
        endStatic = 0F;

        recalculateButtons(GameState.CHALLENGE);
        fadeOut(255, 0, 2);
    }

    void backToMainMenu() {
        switch (state) {
            case ITEMS -> {
                itemsMenu = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);
            }
            case CHALLENGE -> {
                music.stop();
                music.play("pepito", 0.2, true);
            }
            case BINGO -> {
                if(!bingoCard.isCompleted() && !bingoCard.isFailed() && (bingoCard.isGenerated() || bingoCard.isGenerating())) {
                    bingoCard.setTimeGoing(true);
                }
            }
        }

        tintAlpha = 255;
        fadeOut(255, 160, 1);

        state = GameState.MENU;
        sound.play("select", 0.1);

        staticTransparency = 0.05F;
        endStatic = 0.05F;

        recalculateButtons(GameState.MENU);

        DiscordRichPresence rich = new DiscordRichPresence.Builder
                ("In Menu")
                .setDetails("PÉPITO RETURNED HOME")
                .setBigImage("menu", "PÉPITO RETURNED HOME")
                .setSmallImage("pepito", "PÉPITO RETURNED HOME")
                .setStartTimestamps(launchedGameTime)
                .build();

        DiscordRPC.discordUpdatePresence(rich);
    }

    Level night;
    public Level getNight() {
        return night;
    }
    public EndlessGame endless;

    public byte usage = 0;

    public void startGame() {
        if(type == GameType.SHADOW) {
            if(shadowTicket.getAmount() >= 0) {
                shadowTicket.setAmount(-1);
            }
        } else if(type == GameType.CUSTOM) {
            birthdayHat.deselect();
            birthdayMaxwell.deselect();
            shadowTicket.deselect();
        }

        state = GameState.UNLOADED;
        loading = true;

        starlightMillis = 0;
        fadeOut(255, 255, 0);
        usage = 0;
        music.stop();

        byte nightNumber = currentNight;
        if(type == GameType.ENDLESS_NIGHT) {
            nightNumber = endless.getNight();
        }
        if (type != GameType.PARTY && (birthdayHat.isSelected() || birthdayMaxwell.isSelected())) {
            type = GameType.PREPARTY;
        }
        if (birthdayMaxwell.isSelected()) {
            nightNumber = 4;
            music.play("maxwellMusicBox", 0.1, true);
        }

        if(night != null) {
            night.resetTimers();
        }
        night = new Level(this, type, nightNumber);

        int[] notificationDelay = {0};

        if(type == GameType.CLASSIC || type == GameType.PREPARTY || (type == GameType.ENDLESS_NIGHT && endless.getNight() == 1) || type == GameType.CUSTOM) {
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

                    new Pepitimer(() -> new Notification("+ unlocked Pepingo!"), 2000);
                }
            }
        }

        soggyBallpitActive = false;
        soggyBallpitCap = (short) (night.getDuration() * 0.6 + Math.random() * (night.getDuration() * 0.4));

        if(type == GameType.DAY) {
            if(maxwell.isEnabled()) {
                int coinsAdd = (int) ((Math.random() * 42) + (Math.random() * endless.getNight() * 5));
                endless.addCoins(coinsAdd);

                sound.play("sellsYourBalls", 0.15);
                new Notification("Your Maxwell generated " + coinsAdd + " dabloons!");
                notificationDelay[0] += 1400;
            }
            for(byte i = 0; i < 2; i++) {
                if (corn[i].isEnabled() && corn[i].getStage() < 4) {
                    corn[i].increment();
                    if (corn[i].getStage() == 2) {
                        new Pepitimer(() -> new Notification("Your Corn fully grew!"), notificationDelay[0]);
                        notificationDelay[0] += 1000;
                    }
                    switch (corn[i].getStage()) {
                        case 1 -> corn[i].setImage(toCompatibleImage(loadImg("/game/items/cornStage2.png")));
                        case 2 -> corn[i].setImage(toCompatibleImage(loadImg("/game/items/cornStage3.png")));
                    }
                }
            }
            if(endless.getNight() == 6) {
                new Pepitimer(() -> new Notification("Milly is hosting a party at the shop!"), notificationDelay[0]);
                notificationDelay[0] += 1600;
            }
        }

        freezeModifier = 1;

        if(inCam) {
            camOut(false);
        }
        console.clear();
        adBlocked = false;
        adblockerStatus = 0;

        redrawUsage();
        resetFlood();

        itemsMenu = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);

        metalPipeCooldown = 5;
        flashLightCooldown = 5;

        sound.play("select", 0.1);

        if(night.getType() == GameType.DAY) {
            announceDay();
            generateMillyItems();
        } else {
            announceNight(nightNumber, type);
        }

        staticTransparency = 0;
        endStatic = 0;

        recalculateButtons(GameState.GAME);
        repaintOffice();
        maxwellCounter = 0;

        loadA90Images();
        night.start();
        
        if(night.getType().isEndless()) {
            if(endless.getCoins() >= 1000) {
                AchievementHandler.obtain(this, Achievements.DABLOONS);
            }
        }

        if(usedItems.isEmpty()) {
            night.setItemless(true);
        }
        night.setUsedItemAmount((short) usedItems.size());

        final GameType finalType = type;

        new Pepitimer(() -> {
            short endValue = 200;
            float speed = 0.1F;

            switch (finalType) {
                case SHADOW -> {
                    endValue = 120;
                    speed = 0.2F;
                }
                case DAY -> {
                    endValue = 80;
                    speed = 0.3F;
                }
                case PREPARTY -> {
                    endValue = 120;
                    speed = 0.2F;
                }
            }

            fadeOut(255, endValue, flashlight.isEnabled() ? speed * 2 : speed);

            state = GameState.GAME;
            loading = false;
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
                        loading = true;
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
                            case 1 -> night.seconds = 100;
                            case 2 -> night.seconds = 395;
                        }
                    });
                }, Achievements.HALFWAY.isObtained() ? 8000 : 3000);
            }
        }, 400);
        
        String details = "PÉPITO RETURNED HOME";
        switch (night.getType()) {
            case CLASSIC -> details = "CLASSIC - NIGHT " + nightNumber;
            case ENDLESS_NIGHT -> details = "ENDLESS - NIGHT " + endless.getNight();
            case DAY -> details = "ENDLESS - DAY " + endless.getNight();
            case PREPARTY -> details = "PÉPITO'S PARTY PREPARATIONS";
            case PARTY -> details = "PÉPITO'S PARTY";
            case CUSTOM -> details = "SIMULATION - CUSTOM NIGHT";
            case SHADOW -> details = "SHADOWNIGHT - START";
        }

        DiscordRichPresence rich = new DiscordRichPresence.Builder
                ("In-Game")
                .setDetails(details)
                .setBigImage(night.getType().getDiscordID(), "PÉPITO RETURNED HOME")
                .setSmallImage("pepito", "PÉPITO RETURNED HOME")
                .setStartTimestamps(launchedGameTime)
                .build();

        DiscordRPC.discordUpdatePresence(rich);
    }

    Pepitimer shadowTicketTimer = null;

    public Corn[] getCorn() {
        return corn;
    }

    public void generateMillyItems() {
        millyShopItems = new MillyItem[5];
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

                if(endless.getNight() == 6) {
                    consumables.add(new MillyItem(birthdayHat, 1080, resize(birthdayHatImg, 110, 200, BufferedImage.SCALE_SMOOTH)));

                    if(!bingoCardItem.isEnabled() && !unlockedBingo && bingoCardItem.getAmount() == 0) {
                        consumables.add(new MillyItem(bingoCardItem, 1200, bingoCardItem.getIcon()));
                    }
                }
//                if(endless.getNight() >= 7) {
//                    consumables.add(new MillyItem(sunglasses, 400 + endless.getNight() * 40, sunglasses.getIcon()));
//                }
            }
        }
        consumables.removeIf(millyItem -> millyItem.getItem().isEnabled());

        Collections.shuffle(consumables);

        byte i = 0;
        while (i < 5 && i < consumables.size()) {
            if(!consumables.get(i).getItem().isEnabled()) {
                millyShopItems[i] = consumables.get(i);
            }
            i++;
        }
    }

    public void fanStartup() {
        keyHandler.fanSounds.play("fanSound", 0.25, true);
        sound.play("startFan", 0.15);
        usage++;
        redrawUsage();
        fanActive = true;

        everySecond20th.put("fan", () -> fanDegrees += 46);
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
                    jumpscare("shadowAstarta");
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
                    jumpscare("shadowPepito");
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

    public void jumpscare(String key) {
        if(!night.getEvent().isInGame() || portalTransporting || state != GameState.GAME || invincible)
            return;
        if(starlightMillis > 0) {
            sound.play("reflectStarlight", 0.1F);
            return;
        }
        if(night.getAstartaBoss() != null) {
            night.getAstartaBoss().reset();
        }

        stopAllSound();
        short miliseconds = 2000;
        deathScreenY = 0;
        afterDeathText = "";
        afterDeathCurText = "";
        redrawDeathScreen();

        shadowCheckpointSelected = 0;
        shadowCheckpointUsed = 0;

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
        night.getElAstarta().stopService();

        Statistics.DEATHS.increment();

        switch (key) {
            case "pepito" -> {
                jumpscare = jumpscares[1].request();
                sound.play("pepitoScare", 0.35);
                killedBy = "killed by pépito";

                switch ((byte) Math.round(Math.random())) {
                    case 0 -> tip = "Tip: You can hear Pépito's footsteps!";
                    case 1 -> tip = "Tip: You can hear the direction he comes from.";
                }

                if(Statistics.DIED_TO_PEPITO.getValue() == 0) {
                    afterDeathText = "Pépito... Main enemy of the game. You'll hear his footsteps from left or right, close the respective door. Sound is directional.";
                } else if(Statistics.DIED_TO_PEPITO.getValue() == 1) {
                    afterDeathText = "Pépito again? Use the Metal Pipe to scare him away immediately. Enable headphones in settings if you're struggling with the direction.";
                } else if(Statistics.DIED_TO_PEPITO.getValue() > 1) {
                    afterDeathText = "You can figure this one out on your own. I believe in you.";
                }

                Statistics.DIED_TO_PEPITO.increment();
            }
            case "notPepito" -> {
                jumpscare = jumpscares[2].request();
                sound.play("notPepitoScare", 0.3);
                killedBy = "killed by NotPepito";

                switch ((byte) Math.round(Math.random())) {
                    case 0 -> tip = "Tip: If you hear running, close the door immediately!";
                    case 1 -> tip = "Tip: Using your fan can reduce the chances of him.";
                }

                if(Statistics.DIED_TO_NOTPEPITO.getValue() == 0) {
                    afterDeathText = "NotPepito... He's kinda like Pépito. Just faster. Way faster. React immediately. Lights will flicker.";
                } else if(Statistics.DIED_TO_NOTPEPITO.getValue() == 1) {
                    afterDeathText = "NotPepito again.. gonna be a tough one. Lights flicker and you hear his footsteps. Just need to react faster.";
                } else if(Statistics.DIED_TO_NOTPEPITO.getValue() > 1) {
                    afterDeathText = "Eventually you'll get used to it.";
                }

                Statistics.DIED_TO_NOTPEPITO.increment();

                miliseconds = 2550;
            }
            case "a90" -> {
                sound.play("a90Dead", 0.08);
                killedBy = "killed by uncanny cat";

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
                    afterDeathText = "Uncanny Cat... when you see him appear stop moving or pressing keys IMMEDIATELY. He will give you two strikes until you die.";
                } else if(Statistics.DIED_TO_UNCANNY.getValue() == 1) {
                    afterDeathText = "Uncanny Cat again.. do. not. MOVE.                                    You can use the adblocker to suppress him for one night. Adblockers appear in the camera.";
                } else if(Statistics.DIED_TO_UNCANNY.getValue() > 1) {
                    afterDeathText = "Chill, stop moving so much already.";
                }

                Statistics.DIED_TO_UNCANNY.increment();

                miliseconds = 2300;
            }
            case "msi" -> {
                killedBy = "killed by MSI";
//                jumpscare = jumpscares[4].request();
                jumpscare = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

                switch ((byte) Math.round(Math.random() * 2)) {
                    case 0 -> tip = "Tip: Move your camera in the direction he tells you!";
                    case 1 -> tip = "Tip: Flashing him can save you.";
                    case 2 -> tip = "Tip: Disable the sensor, and he will never appear.";
                }

                if(Statistics.DIED_TO_MSI.getValue() == 0) {
                    afterDeathText = "MSI... Scary one. Move left or right at his command. Left, then right, then left, then right, then lef...";
                } else if(Statistics.DIED_TO_MSI.getValue() == 1) {
                    afterDeathText = "MSI again, huh. If you're really struggling use the flashlight on him, but that will summon Glitcher. Move LEFT and RIGHT";
                } else if(Statistics.DIED_TO_MSI.getValue() > 1) {
                    afterDeathText = "LEFT, RIGHT, LEFT, RIGHT, LEFT, RIGHT, LEFT, RIGHT. You'll learn it eventually.";
                }

                Statistics.DIED_TO_MSI.increment();
            }
            case "astarta" -> {
                jumpscare = jumpscares[3].request();
                sound.playRate("astartaAdSound", 0.2, 0.4);
                killedBy = "killed by astarta";

                switch ((byte) Math.round(Math.random())) {
                    case 0 -> tip = "Tip: Astarta's eyes glow! Close the door she looks at.";
                    case 1 -> tip = "Tip: Flashing Astarta makes her disappear immediately!";
                }

                if(Statistics.DIED_TO_ASTARTA.getValue() == 0) {
                    afterDeathText = "Astarta... Her eyes will appear in the door frames. After some time they will flicker, and then she will attack. Close the door.";
                } else if(Statistics.DIED_TO_ASTARTA.getValue() == 1) {
                    afterDeathText = "Astarta again. Close the doors earlier if you want to, or just use the Flashlight in the door frame to scare her away.";
                } else if(Statistics.DIED_TO_ASTARTA.getValue() > 1) {
                    afterDeathText = "Astarta. You'll meet this one a lot.";
                }

                Statistics.DIED_TO_ASTARTA.increment();

                astartaJumpscareCount = true;
                astartaJumpscareCounter = 0;

                miliseconds = 4000;
            }
            case "colaCat" -> {
                jumpscare = resize(jumpscares[5].request(), 1080, 640, Image.SCALE_FAST);
                sound.play("colaJumpscare", 0.13);
                killedBy = "killed by cola cat";

                switch ((byte) Math.round(Math.random() * 2)) {
                    case 0 -> tip = "Tip: The Soda starts changing colors! Drink it.";
                    case 1 -> tip = "Tip: Cola Cat appears from a combo of items, avoid them.";
                    case 2 -> tip = "Tip: Don't use Soda with a Metal Pipe or a Flashlight.";
                }

                if(Statistics.DIED_TO_COLACAT.getValue() == 0) {
                    afterDeathText = "ColaCat... Your soda will start corrupting and become red after some time, and if you don't drink it you will die.";
                } else if(Statistics.DIED_TO_COLACAT.getValue() == 1) {
                    afterDeathText = "ColaCat again. In Classic he appears only when Soda is  used with Metal Pipe or Flashlight. Mini Soda does not get affected by ColaCat.";
                } else if(Statistics.DIED_TO_COLACAT.getValue() > 1) {
                    afterDeathText = "Soda go red -> Drink Soda.";
                }

                Statistics.DIED_TO_COLACAT.increment();

                miliseconds = 4800;
            }
            case "maki" -> {
                jumpscare = resize(jumpscares[6].request(), 1080, 640, Image.SCALE_FAST);
                sound.play("makiScare", 0.4);
                killedBy = "killed by maki";

                tip = "Tip: Look at the camera next time!";

                if(Statistics.DIED_TO_MAKI.getValue() == 0) {
                    afterDeathText = "Maki... Maki's footsteps are not directional. You will need to look in the camera to see what direction she will go to.";
                } else if(Statistics.DIED_TO_MAKI.getValue() == 1) {
                    afterDeathText = "Maki again? Maki will indicate her direction with a red exclamation mark too. Your last resort will be using the Metal Pipe.";
                } else if(Statistics.DIED_TO_MAKI.getValue() > 1) {
                    afterDeathText = "How.";
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

                killedBy = "killed by shark";

                switch ((byte) Math.round(Math.random() * 2)) {
                    case 0 -> tip = "Tip: Move your fish away from Shark!";
                    case 1 -> tip = "Tip: Use your Fan to \"skip\" the flood.";
                    case 2 -> tip = "Tip: Move your camera away!";
                }

                if(Statistics.DIED_TO_SHARK.getValue() == 0) {
                    afterDeathText = "Shark... The Flood is an entire 1 minute sequence. You can make the Flood go 2x faster using the Fan. Green squares will appear, make the fish avoid them at all cost.";
                } else if(Statistics.DIED_TO_SHARK.getValue() == 1) {
                    afterDeathText = "Shark again? You can make the Flood go 2x faster using the Fan. Green squares will appear, make the fish avoid them at all cost.";
                } else if(Statistics.DIED_TO_SHARK.getValue() > 1) {
                    afterDeathText = "Avoid green squares.";
                }

                Statistics.DIED_TO_SHARK.increment();
            }
            case "boykisser" -> {
                jumpscare = resize(jumpscares[9].request(), 1080, 640, Image.SCALE_FAST);

                sound.play("boop", 0.1);

                killedBy = "his name is silly cat....    ";

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

                killedBy = "killed by lemonade cat";

                switch ((byte) Math.round(Math.random())) {
                    case 0 -> tip = "Tip: Throw the lemons at him!";
                    case 1 -> tip = "Tip: aim better";
                }

                if(Statistics.DIED_TO_LEMONADE_CAT.getValue() == 0) {
                    afterDeathText = "Lemonade Cat... Right-Click to throw lemons at him. Lemons take a bit to reach him, so go a bit ahead. Lemonade Cat will bounce up and down too.";
                } else if(Statistics.DIED_TO_LEMONADE_CAT.getValue() == 1) {
                    afterDeathText = "Lemonade Cat again. Aim BETTER. THROW THOSE LEMONS. DONT RUN OUT.";
                } else if(Statistics.DIED_TO_LEMONADE_CAT.getValue() > 1) {
                    afterDeathText = "Aim better PLEASE.";
                }

                Statistics.DIED_TO_LEMONADE_CAT.increment();
            }
            case "a120" -> {

            }
            case "scaryCat" -> {
                killedBy = "killed by scary cat";

                miliseconds = 5500;
                sound.play("scaryCatJumpscare", 0.2);
                jumpscare = resize(jumpscares[12].request(), 1080, 640, Image.SCALE_FAST);

                tip = "Tip: Move your camera around!";

                if(Statistics.DIED_TO_SCARY_CAT.getValue() == 0) {
                    afterDeathText = "Scary Cat... he will try to go in the center of your view. Move and don't let him reach you. After some time he will disappear.";
                } else if(Statistics.DIED_TO_SCARY_CAT.getValue() == 1) {
                    afterDeathText = "Scary Cat is very forgiving, i don't know how you are dying. But move your camera away from him.";
                } else if(Statistics.DIED_TO_SCARY_CAT.getValue() > 1) {
                    afterDeathText = "Move your camera.";
                }

                Statistics.DIED_TO_SCARY_CAT.increment();
            }
            case "dread" -> {
                miliseconds = 1600;

                sound.playRate("dreadDead", 0.4, 0.8);
                killedBy = "killed by dread";

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

                afterDeathText = "Who...?? I do not know that entity. It is not from our world.";

                Statistics.DIED_TO_DREAD.increment();
            }
            case "shadowAstarta" -> {
                miliseconds = 0;

                killedBy = "killed by Shadow Astarta";

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

                killedBy = "killed by Shadow Pépito";

                Statistics.DIED_TO_SHADOW_PEPITO.increment();
            }
            case "elAstarta" -> {
                miliseconds = 5000;

                jumpscare = jumpscares[13].request();
                sound.play("elAstartaScare", 0.2);

                killedBy = "killed by El Astarta";

                if(Statistics.DIED_TO_EL_ASTARTA.getValue() == 0) {
                    afterDeathText = "El Astarta... Sequence that lasts 40 seconds. El Astarta's eyes will appear in door frames and strike very fast. New doors will appear. Survive.";
                } else if(Statistics.DIED_TO_EL_ASTARTA.getValue() == 1) {
                    afterDeathText = "El Astarta again... Close doors with eyes. Do not get confused. Survive.";
                } else if(Statistics.DIED_TO_EL_ASTARTA.getValue() > 1) {
                    afterDeathText = "Survive.";
                }

                Statistics.DIED_TO_EL_ASTARTA.increment();
            }
            case "radiation" -> {
                miliseconds = 0;
                killedBy = "died from radiation";

                afterDeathText = "Avoid your cursor from entering green circles, they indicate radiation. If you reach too much radiation you WILL die.";

                Statistics.DIED_TO_RADIATION.increment();
            }
            case "pause" -> {
                miliseconds = 0;
                killedBy = "killed by intentional game design";

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

                        if(Statistics.DEATHS.getValue() >= 1000) {
                            AchievementHandler.obtain(this, Achievements.KAMIKAZE);
                        }
                    }
                }
            }

            new Pepitimer(() -> pressAnyKey = true, 3000);
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

    short winCount = 0;
    short deathCount = 0;

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
        winCount++;
        Statistics.WINS.increment();

        if(night.getType() == GameType.CUSTOM && CustomNight.isCustom()) {
            int points = 0;
            for (CustomNightEnemy enemy : CustomNight.getEnemies()) {
                points += 100 * enemy.getAI();
            }
            int multiplier = 1;
            for (CustomNightModifier modifier : CustomNight.getModifiers()) {
                if (modifier.isActive()) {
                    multiplier++;
                }
            }
            points *= multiplier;

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
                if(night.isPerfectStorm()) {
                    AchievementHandler.obtain(this, Achievements.PERFECT_STORM);
                }
            }
        } else {
            if(type == GameType.PREPARTY) {
                AchievementHandler.obtain(this, Achievements.PREPARTY);
            } else if(type == GameType.PARTY) {
                AchievementHandler.obtain(this, Achievements.PARTY);
            }
            if(birthdayHat.getAmount() >= 0) {
                birthdayHat.setAmount(-1);
            }
        }

        everySecond10th.remove("energy");
        everySecond10th.put("rainbowText", () -> currentRainbow += 0.005F);

        gg = false;
        byte random = (byte) (Math.random() * 100 / Math.min(100, Math.max(winCount, 1)));
        if(random <= 0) {
            gg = true;
        }

        new Pepitimer(() -> {
            pressAnyKey = true;

            sound.play("boop", 0.02);
        }, 11000);
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
        for(int x = 0; x < 1080; x += 2) {
            for(int y = 0; y < 640; y += 2) {
                Color color = new Color(lastWinScreen.getRGB(x, y));
                if(color.getRed() < 5 && color.getGreen() < 5 && color.getBlue() < 5)
                    continue;

                graphics.setColor(new Color(0, 255, 0, (int) (0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue())));
                graphics.fillRect(x, y, 2, 2);
            }
        }
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

    boolean allNighter = false;

    private void stopEverything() {
        console.clear();

        resetFlood();
        manualClose();

        night.setTemperature(0);

        fadeOutStatic(0, 0, 0.05F);

        inCam = false;
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
                    riftTransparency = 1F;
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
        selectedRiftItem = riftItems.get(0);
        riftTint = 0;

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
        graphics2D.setColor(Color.WHITE);
        graphics2D.setFont(comicSans80);
        graphics2D.drawString(riftText, 540 - halfTextLength(graphics2D, riftText), 620 + riftY);

        graphics2D.setColor(new Color(20, 20, 20));
        graphics2D.drawString("Select two.", 540 - halfTextLength(graphics2D, "Select two."), -1280 + 320 + riftY);
        graphics2D.setColor(new Color(60, 60, 60));
        graphics2D.drawString("Select two.", 540 - halfTextLength(graphics2D, "Select two."), -1280 + 280 + riftY);
        graphics2D.setColor(new Color(100, 100, 100));
        graphics2D.drawString("Select two.", 540 - halfTextLength(graphics2D, "Select two."), -1280 + 240 + riftY);
        graphics2D.setColor(new Color(160, 160, 160));
        graphics2D.drawString("Select two.", 540 - halfTextLength(graphics2D, "Select two."), -1280 + 200 + riftY);
        graphics2D.setColor(new Color(200, 200, 200));
        graphics2D.drawString("Select two.", 540 - halfTextLength(graphics2D, "Select two."), -1280 + 160 + riftY);

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

    public void manualOpen() {
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
    public void manualHide() {
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
    public void manualClose() {
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
    List<String> sectionManualText(String input) {
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
        mirror = false;
        fadeOut(255, 255, 1);
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

    boolean drawCat = false;
    int deathScreenY = 0;
    public boolean pressAnyKey = false;

    public void stopGame(boolean toMenu) {
        reloadMenuButtons();
        inCam = false;

        if(toMenu) {
            state = GameState.MENU;
            recalculateButtons(state);

            if(night.getEvent() == GameEvent.DYING) {
                if(bingoCard.isTimeGoing()) {
                    if(bingoCard.secondsSpent > 1140) {
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
                    .setDetails("PÉPITO RETURNED HOME")
                    .setBigImage("menu", "PÉPITO RETURNED HOME")
                    .setSmallImage("pepito", "PÉPITO RETURNED HOME")
                    .setStartTimestamps(launchedGameTime)
                    .build();

            DiscordRPC.discordUpdatePresence(rich);
        }
        type = GameType.CLASSIC;
        rainSound.stop();

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

        stopAllSound();
        music.play("pepito", 0.2, true);

        resetFlood();

        staticTransparency = 0.05F;
        endStatic = 0.05F;

        for(Item item : fullItemList) {
            if(item.isEnabled()) {
                item.disable();
            }
        }
        usedItems.clear();

        if(keyHandler.freezeChange != null) {
            keyHandler.freezeChange.cancel();
        }

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

    public void stopAllSound() {
        sound.stop();
        music.stop();
        keyHandler.fanSounds.stop();
        keyHandler.camSounds.stop();
        scaryCatSound.stop();
        generatorSound.stop();
        rainSound.stop();
    }
    public void pauseAllSound() {
        sound.pause();
        music.pause();
        keyHandler.fanSounds.pause();
        keyHandler.camSounds.pause();
        scaryCatSound.pause();
        generatorSound.pause();
        rainSound.pause();
    }
    public void resumeAllSound() {
        sound.resume();
        music.resume();
        keyHandler.fanSounds.resume();
        keyHandler.camSounds.resume();
        scaryCatSound.resume();
        generatorSound.resume();
        rainSound.resume();
    }

    short startFade = 255;

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
        return randomColor(balloonImg);
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

    private BufferedImage rotate(BufferedImage image, int degrees, boolean trim) {
        double theta = Math.toRadians (degrees);
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

    private static BufferedImage trimImage(BufferedImage image) {
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

    private static BufferedImage trimImageRightBottom(BufferedImage image) {
        WritableRaster raster = image.getAlphaRaster();
        int width = raster.getWidth();
        int height = raster.getHeight();
        int right = width - 1;
        int bottom = height - 1;
        int minRight = width - 1;
        int minBottom = height - 1;

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
            newGraphics.drawImage(source.getSubimage(Math.max(-x, 0), y, source.getWidth() - Math.abs(x), precision), Math.max(x, 0), y, null);
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

    public BufferedImage scratch(BufferedImage img, int intensity, int precision) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics2D graphics2D = (Graphics2D) result.getGraphics();

        for (int x = 0; x < img.getWidth(); x += precision) {
            int h = (int) (intensity * Math.random());
            graphics2D.drawImage(img.getSubimage(x, 0, 1, 640 - h), x, 0, 1, 640, null);
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

    public BufferedImage purplify(BufferedImage img) {
        BufferedImage image = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        short x = 0;
        while (x < image.getWidth()) {
            short y = 0;
            while (y < image.getHeight()) {
                Color color = new Color(img.getRGB(x, y), true);

                if(color.getAlpha() > 0) {
                    Color newColor = new Color((int) (color.getRed() * 0.5), 0, color.getBlue(), color.getAlpha());
                    image.setRGB(x, y, newColor.getRGB());
                }
                y++;
            }
            x++;
        }

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

    public BufferedImage alphaify(BufferedImage img, float transparency) {
        BufferedImage image = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = (Graphics2D) image.getGraphics();

        graphics2D.setComposite(AlphaComposite.SrcOver.derive(transparency));
        graphics2D.drawImage(img, 0, 0, null);

        graphics2D.dispose();
        return image;
    }

    private BufferedImage grayscale(BufferedImage source) {
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

    private BufferedImage completelyBlack(BufferedImage img) {
        BufferedImage image = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics2D graphics2D = (Graphics2D) image.getGraphics();

        short x = 0;
        while (x < image.getWidth()) {
            short y = 0;
            while (y < image.getHeight()) {
                Color color = new Color(img.getRGB(x, y), true);

                if(color.getAlpha() > 0) {
                    graphics2D.setColor(new Color(0, 0, 0, color.getAlpha()));
                    graphics2D.fillRect(x, y, 2, 2);
                }
                y++;
            }
            x++;
        }

        graphics2D.dispose();
        return image;
    }

    private BufferedImage completelyWhite(BufferedImage img) {
        BufferedImage image = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics2D graphics2D = (Graphics2D) image.getGraphics();

        short x = 0;
        while (x < image.getWidth()) {
            short y = 0;
            while (y < image.getHeight()) {
                Color color = new Color(img.getRGB(x, y), true);

                if(color.getAlpha() > 0) {
                    graphics2D.setColor(new Color(255, 255, 255, color.getAlpha()));
                    graphics2D.fillRect(x, y, 2, 2);
                }
                y++;
            }
            x++;
        }

        graphics2D.dispose();
        return image;
    }

    /** @noinspection SameParameterValue*/
    public static BufferedImage resize(BufferedImage image, int width, int height, int hints) {
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
    private BufferedImage mirror(BufferedImage image, int type) {
        if(type == 0) {
            return image;
        }

        BufferedImage mirrored = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
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
            return changeHue(image, (float) (Math.random()));
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


    public static BufferedImage changeHue(BufferedImage source, float hueOffset) {
        BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());

        for (int x = 0; x < source.getWidth(); x++) {
            for (int y = 0; y < source.getHeight(); y++) {
                Color color = new Color(source.getRGB(x, y), true);
                int alpha = color.getAlpha();
                if(alpha == 0)
                    continue;

                float[] hsb = new float[3];
                Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);

                Color hsbColor = Color.getHSBColor(hsb[0] + hueOffset, hsb[1], hsb[2]);
                Color newColor = new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), alpha);
                image.setRGB(x, y, newColor.getRGB());
            }
        }

        return image;
    }

    private static BufferedImage blur(BufferedImage image, int[] filter, int filterWidth) {
        if (filter.length % filterWidth != 0) {
            throw new IllegalArgumentException("filter contains a incomplete row");
        }

        final int width = image.getWidth();
        final int height = image.getHeight();
        final int sum = IntStream.of(filter).sum();

        int[] input = image.getRGB(0, 0, width, height, null, 0, width);

        int[] output = new int[input.length];

        final int pixelIndexOffset = width - filterWidth;
        final int centerOffsetX = filterWidth / 2;
        final int centerOffsetY = filter.length / filterWidth / 2;

        // apply filter
        for (int h = height - filter.length / filterWidth + 1, w = width - filterWidth + 1, y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int r = 0;
                int g = 0;
                int b = 0;
                for (int filterIndex = 0, pixelIndex = y * width + x;
                     filterIndex < filter.length;
                     pixelIndex += pixelIndexOffset) {
                    for (int fx = 0; fx < filterWidth; fx++, pixelIndex++, filterIndex++) {
                        int col = input[pixelIndex];
                        int factor = filter[filterIndex];

                        // sum up color channels seperately
                        r += ((col >>> 16) & 0xFF) * factor;
                        g += ((col >>> 8) & 0xFF) * factor;
                        b += (col & 0xFF) * factor;
                    }
                }
                r /= sum;
                g /= sum;
                b /= sum;
                // combine channels with full opacity
                output[x + centerOffsetX + (y + centerOffsetY) * width] = (r << 16) | (g << 8) | b | 0xFF000000;
            }
        }

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        result.setRGB(0, 0, width, height, output, 0, width);
        return result;
    }

    public BufferedImage bloom(BufferedImage source) {
        BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());

        BufferedImageOp blur = getGaussianBlurFilter(10, true);
        BufferedImage blurred = blur.filter(resize(source, source.getWidth() / 4, source.getHeight() / 4, Image.SCALE_FAST), null);
        BufferedImageOp blur2 = getGaussianBlurFilter(10, false);
        blurred = resize(blur2.filter(blurred, null), source.getWidth(), source.getHeight(), Image.SCALE_SMOOTH);

        for (int x = 0; x < source.getWidth(); x++) {
            for (int y = 0; y < source.getHeight(); y++) {
                Color oldColor = new Color(blurred.getRGB(x, y), true);
                Color newColor = new Color(source.getRGB(x, y), true);

                image.setRGB(x, y, new Color(Math.min(255, oldColor.getRed() + newColor.getRed()), Math.min(255, oldColor.getGreen() + newColor.getGreen()), Math.min(255, oldColor.getBlue() + newColor.getBlue()), Math.min(255, oldColor.getAlpha() + newColor.getAlpha())).getRGB());
            }
        }

        return image;
    }

    boolean bloom = false;
    public static boolean mirror = false;
    public static boolean isMirror() {
        return mirror;
    }

    public static ConvolveOp getGaussianBlurFilter(int radius,
                                                   boolean horizontal) {
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
}
