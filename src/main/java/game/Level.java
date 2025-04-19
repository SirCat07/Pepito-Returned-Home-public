package game;

import enemies.*;
import game.achievements.AchievementHandler;
import game.achievements.Achievements;
import game.bingo.BingoHandler;
import game.bingo.BingoTask;
import game.custom.CustomNight;
import game.custom.CustomNightEnemy;
import game.custom.CustomNightModifier;
import game.dryCat.DryCatGame;
import game.enviornments.*;
import game.particles.CoffeeParticle;
import game.particles.GlassParticle;
import game.particles.Raindrop;
import game.shadownight.AstartaBoss;
import game.shadownight.ShadowPepito;
import javafx.scene.media.MediaPlayer;
import main.GamePanel;
import main.SoundMP3;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import utils.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;

public class Level {
    public byte night = 1;
    public int id = (int) (Math.random() * 1000000);

    short duration = 300;
    public short seconds = 0;
    public short secondsAtStart = 0;
    String clockString = "12 AM";

    GameEvent event = GameEvent.NONE;
    GameType type;

    private float energy = 503;
    private float maxEnergy = 503;
    private boolean power = true;
//    public boolean[] door = new boolean[] {false, false};
//    public byte[] blockade = new byte[] {0, 0};
    public HashMap<Integer, Door> doors = new HashMap<>();
    
    public Enviornment env = new Office();
    public Enviornment env() {
        return env;
    }
    
    BufferedImage monitorImage = new BufferedImage(196, 90, BufferedImage.TYPE_INT_RGB);

    private final PepitoAndNotPepito pepito;
    private final Glitcher glitcher;
    private final A90 a90;
    private final MSI msi;
    private final Astarta astarta;
    private final ColaCat colaCat;
    private final Maki maki;
    private final Shark shark;
    private final Boykisser boykisser;
    private final LemonadeCat lemonadeCat;
    private final MirrorCat mirrorCat;
    private final A120 a120;
    private final Wires wires;
    private final ScaryCat scaryCat;
    private final JumpscareCat jumpscareCat;
    private final ElAstarta elAstarta;
    private final Kiji kiji;
    private final Shock shock;
    private final ToleTole toleTole;
    private final DeepSeaCreature dsc;
    private final Hydrophobia hydrophobia;
    private final Beast beast;
    private final Overseer overseer;
    private final MrMaze mrMaze;

    public PepitoAndNotPepito getPepito() {
        return pepito;
    }

    public Glitcher getGlitcher() {
        return glitcher;
    }

    public A90 getA90() {
        return a90;
    }

    public MSI getMSI() {
        return msi;
    }

    public Astarta getAstarta() {
        return astarta;
    }

    public ColaCat getColaCat() {
        return colaCat;
    }

    public Maki getMaki() {
        return maki;
    }

    public Shark getShark() {
        return shark;
    }
    public DeepSeaCreature getDsc() {
        return dsc;
    }

    public Boykisser getBoykisser() {
        return boykisser;
    }

    public LemonadeCat getLemonadeCat() {
        return lemonadeCat;
    }

    public MirrorCat getMirrorCat() {
        return mirrorCat;
    }

    public A120 getA120() {
        return a120;
    }

    public Wires getWires() {
        return wires;
    }

    public ScaryCat getScaryCat() {
        return scaryCat;
    }
    public JumpscareCat getJumpscareCat() {
        return jumpscareCat;
    }


    public ElAstarta getElAstarta() {
        return elAstarta;
    }
    public Kiji getKiji() { return kiji; }
    public Shock getShock() { return shock; }
    public ToleTole getToleTole() { return toleTole; }

    public MrMaze getMrMaze() {
        return mrMaze;
    }

    public Hydrophobia getHydrophobia() {
        return hydrophobia;
    }

    public Beast getBeast() {
        return beast;
    }

    public Overseer getOverseer() {
        return overseer;
    }

    public float getEnergy() {
        return energy;
    }
    public void addEnergy(float f) {
        energy += f;
        if(f > 0) {
            g.batteryRegenIcons.add(-5);
        }
    }
    public void addEnergyLimited(float f) {
        if(energy + f < maxEnergy) {
            if (f > 0) {
                g.batteryRegenIcons.add(-5);
            }
        }
        energy = Math.min(maxEnergy, energy + f);
    }
    public void setEnergy(float f) {
//        if(f > energy) {
//            g.batteryRegenIcons.add(-5);
//        }
        energy = f;
    }

    public float getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(float maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public Frog frog;

    GamePanel g;

    public Level(GamePanel g, GameType type, int night) {
        this.type = type;
        this.g = g;
        this.night = (byte) night;
        sortEnviornment();

        switch (type) {
            case BASEMENT, BASEMENT_PARTY -> {
                doors.clear();
                setDefaultBasementDoors();
            }
            case HYDROPHOBIA -> {
                
            }
            
            default -> {
                List<Point> poly1 = List.of(new Point(325, 497), new Point(478, 497), new Point(478, 617), new Point(325, 617));
                doors.put(0, new Door(new Point(270, 500), g.door1Img, new Point(325, 497), GamePanel.getPolygon(poly1), new Point(350, 560)));

                int x1 = 1269;
                int x2 = 1451;
                int[] xArray = new int[] {x1, x2, x2, x1};
                int[] yArray = new int[] {269, 264, 635, 620};

                doors.put(1, new Door(new Point(1160, 280), g.door2Img, new Point(1262, 264), new Polygon(xArray, yArray, 4), new Point(1300, 487)));
            }
        }
        
        pepito = new PepitoAndNotPepito(g);
        a90 = new A90(g);
        msi = new MSI(g, g.msiImage[1] != null);
        glitcher = new Glitcher(g);
        astarta = new Astarta(g);
        colaCat = new ColaCat(g);
        maki = new Maki(g);
        shark = new Shark(g);
        boykisser = new Boykisser(g);
        lemonadeCat = new LemonadeCat(g);
        mirrorCat = new MirrorCat(g);
        a120 = new A120(g);
        wires = new Wires(g);
        scaryCat = new ScaryCat(g);
        jumpscareCat = new JumpscareCat(g);
        elAstarta = new ElAstarta(g);
        kiji = new Kiji(g);
        shock = new Shock(g);
        toleTole = new ToleTole(g);
        dsc = new DeepSeaCreature(g);
        hydrophobia = new Hydrophobia(g);
        beast = new Beast(g);
        overseer = new Overseer(g);
        
        frog = new Frog(g);
        mrMaze = new MrMaze(g);

        sortTime();
    }
    

    public int afk = 15;

    AstartaBoss astartaBoss;
    ShadowPepito shadowPepito;
    
    NeonSogBall neonSogBall = null;
    
    public byte megaSodaUses = 4;
    public short megaSodaLightsOnTicks = 0;
    
    Shadowblocker shadowblocker = new Shadowblocker(0);
    
    public boolean lockedIn = false;
    

    public RepeatingPepitimer shirtfart;

    public short usedMetalPipes = 0;
    boolean isBillyShop = false;

    public boolean isBillyShop() {
        return isBillyShop;
    }
    

    public void start() {
        if(type == GameType.CUSTOM) {
            for (CustomNightModifier modifier : CustomNight.getModifiers()) {
                switch (modifier.getName()) {
                    case "powerOutageCn" -> powerModifier = modifier.isActive();
                    case "blizzardCn" -> blizzardModifier = modifier.isActive();
                    case "timersCn" -> timerModifier = modifier.isActive();
                    case "fogCn" -> fogModifier = modifier.isActive();
                    case "radiationCn" -> radiationModifier = modifier.isActive();
                    case "rainCn" -> rainModifier = modifier.isActive();
                }
            }
            if(isPerfectStorm()) {
                g.nightAnnounceText = GamePanel.getString("perfectStorm");
            }
            
            boolean isEveryModifierOn = true;
            for(CustomNightModifier modifier : CustomNight.getModifiers()) {
                if(!modifier.isActive()) {
                    isEveryModifierOn = false;
                    break;
                }
            }
            if(isEveryModifierOn) {
                AchievementHandler.obtain(g, Achievements.HOW_DID_WE_GET_HERE);
            }
        }
        if(radiationModifier) {
            gruggyCarts.add(new GruggyCart());
            gruggyCarts.add(new GruggyCart());

            if(GamePanel.isAprilFools) {
                gruggyCarts.add(new GruggyCart());
                gruggyCarts.add(new GruggyCart());
                gruggyCarts.add(new GruggyCart());
                gruggyCarts.add(new GruggyCart());
            }
        }

        sortAILevels(g.getCorn());
        spawnManuals(night, type);

        secondsAtStart = seconds;
        updateClockString();

        if(powerModifier) {
            int max = Math.round(type.getDuration() / 60F);
            int m = Math.max(2, max - (int) (Math.random() * max));
            
            int twentyFifth = type.getDuration() / 4;
            int lastOutage = type.getDuration() - twentyFifth / m - (int) (twentyFifth * Math.random());
            
            for(int i = 1; i <= m; i++) {
                outages.put(secondsAtStart + lastOutage / m * i, twentyFifth / m);
                System.out.println("Outage at: " + (secondsAtStart + lastOutage / m * i) + ", duration: " + (twentyFifth / m));
            }
        }
        

        g.offsetX = (short) (env.maxOffset() / 2);
        if(env.getBgIndex() == 4 && g.everEnteredBasement) {
            g.offsetX = 10;
            BasementKeyOffice office = (BasementKeyOffice) env;
            office.setEvilDoorPercent(0);
        }
        
        System.out.println("shirtfartings arise...");

        shirtfart = new RepeatingPepitimer(() -> {
            if(g.state != GameState.GAME)
                return;
            if(reachedHalfway || event == GameEvent.ASTARTA)
                return;

            if (event.isInGame()) {
                float hPercent = temperature / 100F;
                float ePercent = (1 - (energy / maxEnergy)) / 4 * 3 + 0.25F;
                float tPercent = (((seconds - secondsAtStart) * 1F) / (type.getDuration() + Math.abs(secondsAtStart))) / 2F;
                float vPercent = Math.min(1, eventPercent);

                float average = (hPercent + ePercent + tPercent + vPercent) / 4;
                float eventHeavy = (average + eventPercent * 3) / 4;

                shirtfart.setDelay((int) (12000 - 9000 * average));
                shirtfart.setMiliseconds((int) (12000 - 9000 * average));

                if(average > 0.6F && eventPercent > 1.4F)
                    return;

                float rate = (Math.round(Math.random() * 10F)) / (20F - 15F * eventHeavy) + 0.3F + Math.round(6F * average) / 10F;
                float volume = 0.25F * average + 0.08F;

                if(volume < 0.13F)
                    return;

                g.sound.playRate("shirtfart", volume, rate);
            }
        }, 5000, 12000);

        g.every6s.put("game", () -> {
            g.pixelation = Math.max(1, g.pixelation - 0.12F);
            
            if(g.state != GameState.GAME)
                return;
            if(event == GameEvent.ASTARTA) {
                g.pixelation = Math.max(1, g.pixelation - 0.5F);

                if(astartaBoss.getDyingStage() > 0) {
                    return;
                }
            }
            

            g.sound.play("literally_22_miliseconds_of_nothing", 0.1);

            if (event.isInGame()) {
                int random = (int) (Math.floor(Math.random() * 18));
                cancelAfterGame.add(new Pepitimer(() -> {
                    switch (random) {
                        case 0 -> g.sound.play("a90Alert", 0.04, Math.random() / 3 - 0.33);
                        case 1 -> g.sound.play("randomScare", 0.04, Math.random() / 3 - 0.33);
                        case 2 -> g.sound.play("cat_sounds", 0.15, Math.random() / 3 - 0.33);
                        case 3 -> g.sound.play("fakeWalk", 0.04, Math.random() * 2 - 1);
                    }
                }, (int) (Math.floor(Math.random() * 5000))));
                
                
                if(event.canTimeTick()) {
                    //fakeouts
                    // water level
                    if ((shark.isEnabled() && shark.floodStartSeconds > 15) || (dsc.isEnabled() && dsc.floodStartSeconds > 15)) {
                        if (Math.random() < 0.0014 && !event.isFlood() && !g.everySecond20th.containsKey("sogFakeout1") && !g.everySecond20th.containsKey("sogFakeout2")) {
                            cancelAfterGame.add(new Pepitimer(() -> {
                                g.sound.play("waterLoopFaker", 0.2);
                                g.everySecond20th.put("sogFakeout1", () -> {
                                    g.currentWaterPos = (short) ((g.currentWaterPos + 2) % 1480);
                                    if (g.currentWaterLevel > 580) {
                                        g.currentWaterLevel--;
                                    } else {
                                        g.everySecond20th.remove("sogFakeout1");

                                        new Pepitimer(() -> g.everySecond20th.put("sogFakeout2", () -> {
                                            g.currentWaterPos = (short) ((g.currentWaterPos + 2) % 1480);
                                            if (g.currentWaterLevel < 640) {
                                                g.currentWaterLevel++;
                                            } else {
                                                g.everySecond20th.remove("sogFakeout2");
                                                g.currentWaterPos = 0;
                                            }
                                        }), 1000);
                                    }
                                });
                            }, (int) (Math.random() * 4500)));
                        }
                    }
                    //scarycat
                    if(scaryCat.isEnabled() && scaryCat.arrivalSeconds > 10) {
                        if(Math.random() < 0.0016) {
                            cancelAfterGame.add(new Pepitimer(() -> g.scaryCatSound.play("scaryCatFakeout", 0.14), (int) (Math.random() * 4500)));
                        }
                    }
                    //msi
                    if(msi.isEnabled() && msi.arrivalSeconds > 10) {
                        if(Math.random() < 0.0016) {
                            cancelAfterGame.add(new Pepitimer(() -> g.sound.play("msiFakeout", 0.1), (int) (Math.random() * 4500)));
                        }
                    }
                    //shock
                    if(shock.isEnabled() && shock.arrivalSeconds > 10) {
                        if(Math.random() < 0.00145) {
                            cancelAfterGame.add(new Pepitimer(() -> g.sound.play("shockFakeout", 0.1), (int) (Math.random() * 4500)));
                        }
                    }
                }
                
                
                if (g.sensor.isEnabled() && !power) {
                    g.console.add("");
                }
            }
            if(env instanceof HChamber chamber) {
                if(chamber.cup.x < 1990 && Math.random() < 0.92) {
                    chamber.coffeeParticles.add(new CoffeeParticle(chamber.cup.x + (int) (chamber.cup.width * 0.2), chamber.cup.y, (short) (g.fixedUpdatesAnim % 2512), (int) (chamber.cup.width * 0.7)));
                }
            }

            GamePanel.balloons.forEach(Balloon::changeDirection);
        });

        g.everySecond.put("game", () -> {
            if(g.state != GameState.GAME)
                return;
            afk--;

            if(!type.isBasement()) {
                if (energy < 150 && energy > 0) {
                    if (Math.random() < 0.015) {
                        flicker = 100;
                        g.sound.play("flicker" + (int) (Math.floor(Math.random()) + 1), 0.05);
                    }
                }
            } else {
                Basement basement = (Basement) env;
                
                if (energy < ((basement.getStage() == 5) ? 300 : 150) && energy > 0) {
                    if (Math.random() < ((basement.getStage() == 5) ? 0.015 : 0.05)) {
                        flicker = 100;
                        g.sound.play("flicker" + (int) (Math.floor(Math.random()) + 1), 0.05);
                    }
                }
            }
            
            if(type == GameType.HYDROPHOBIA) {
                HChamber env = (HChamber) this.env;
                
                if(env.timerText == null) {
                    env.setUntilDoor(env.isInPrefield() ? (env.isRespawnCheckpoint() ? 3 : 10) : 30);
                    env.setWobbleFade(120);
                    
                    g.sound.play("hcTimerWindup", 0.12);
                    
                } else if (env.getUntilDoor() > 0) {
                    if(!g.inLocker) {
                        if(env.getUntilDoor() > 0) {
                            env.setUntilDoor(env.getUntilDoor() - 1);
                        }
                        
                        g.sound.playRate("beep", 0.08, env.getUntilDoor() > 10 ? 1 : 0.75);

                        if (env.getUntilDoor() <= 10 || Math.random() > env.getUntilDoor() / 20F) {
                            env.setWobbleFade(30);
                        }
                    }
                    
                    if(env.getUntilDoor() == 0) {
                        g.sound.playRate("barrierRising", 0.1, 1.1F);
                        
                        g.everyFixedUpdate.put("hcBarrierRotation", () -> {
                            env.setBarrierRotation(env.getBarrierRotation() - 0.5F);
                            env.setBarrierRotation(env.getBarrierRotation() * 1.1F);
                            
                            if(env.getBarrierRotation() <= -90) {
                                env.setBarrierRotation(-90);
                                g.everyFixedUpdate.remove("hcBarrierRotation");
                            }
                            g.repaintOffice();
                        });
                    }
                } else {
                    g.sound.playRate("clockTickHydro", 0.07, 0.85 + Math.random() * 0.1);
      
                    if(env.getPrefieldCount() == 5 && !env.shownPrefieldMSILogo) {
                        env.setWobbleFade(60);
                        env.shownPrefieldMSILogo = true;
                    }
                }

                redrawHChamberTimer(env);
                
                if(hydrophobia.distance() == 1) {
                    g.repaintOffice();
                }
                
                if(overseer.getRage() > 0.6F) {
                    flicker = 100;
                    g.sound.playRate("flicker" + (int) (Math.floor(Math.random()) + 1), 0.05, 0.8F + Math.random() * 0.4F);
                }

                new Pepitimer(() -> {
                    if(env.timerText == null)
                        return;
                    
                    boolean different = env.timer.width != 241 || env.timer.height != 123;
                    
                    BufferedImage imageToBloom = env.timerText;
                    if(different) imageToBloom = GamePanel.resize(imageToBloom, 241, 123, Image.SCALE_FAST);
                    
                    imageToBloom = g.bloom(imageToBloom);
                    if(different) imageToBloom = GamePanel.resize(imageToBloom, env.timer.width, env.timer.height, Image.SCALE_FAST);
                    
                    env.timerText = imageToBloom;
                }, 40);
            }
            
            if(powerModifier && energy > 0) {
                if(event.canTimeTick()) {
                    if (power) {
                        if (!elAstarta.isActive() && !outages.isEmpty()) {
                            Iterator<Integer> iter = outages.keySet().iterator();

                            while (iter.hasNext()) {
                                Integer second = iter.next();
                                if (seconds >= second) {
                                    outageDuration += outages.get(second);
                                    iter.remove();
                                    g.powerDown();
                                    g.repaintOffice();
                                }
                            }
                        }
                    } else {
                        outageDuration--;
                        if (outageDuration <= 0) {
                            g.lightsOn();
                        }
                    }
                }
                repaintMonitorImage();
            }
            if(blizzardModifier) {
                if(Math.random() < 0.008 && blizzardTime <= 0) {
                    startBlizzard();
                }
                if(blizzardTime > 0) {
                    blizzardTime--;
                }
            }
            if(Math.random() < 0.05) {
                if(isPerfectStorm()) {
                    g.fadeOut(0, g.endFade, 2);
                    g.sound.play("thunder" + (int) (Math.random() * 3 + 1), 0.1);
                }
            }

            if(event != GameEvent.MAXWELL && event != GameEvent.BASEMENT_KEY) {
                if(event.canTimeTick()) {
                    seconds++;
                    updateClockString();
                    if (power) {
                        g.generateAdblocker();
                    }
                }

                if (type == GameType.CLASSIC || type.isParty() && !type.isBasement()) {
                    if (checkForCompletion()) {
                        g.win();
                    }
                } else switch (type) {
                    case CUSTOM -> {
                        if(isPerfectStorm() && seconds >= 150) {
                            AchievementHandler.obtain(g, Achievements.PERFECT_STORM);
                        }
                        if (checkForCompletion() && !startedCustomWin) {
                            g.winSequence();
                            startedCustomWin = true;
                        }
                    }
                    case ENDLESS_NIGHT -> {
                        if (checkForCompletion()) {
                            g.type = GameType.DAY;
                            g.endless.addCoins(g.endless.getNight() * 100);
                            if(g.endless.getNight() == 3) {
                                g.endless.addCoins(100);
                            } else if(g.endless.getNight() == 6) {
                                g.endless.addCoins(210);

                                if(Achievements.ALL_NIGHTER.isObtained()) {
                                    g.gotEndlessNight6AfterAllNighter = true;
                                }
                            }
                            g.startGame();
                        }
                        if (g.soggyBallpit.isEnabled()) {
                            if (g.soggyBallpitActive) {
                                seconds += 16;
                                updateClockString();
                            } else if (seconds > g.soggyBallpitCap && g.type == GameType.ENDLESS_NIGHT) {
                                g.soggyBallpitActive = true;
                                g.sound.play("sogBallpitAppear", 0.2);
                            }
                        }
                    }
                    case DAY -> {
                        if (checkForCompletion()) {
                            g.type = GameType.ENDLESS_NIGHT;
                            g.endless.nextNight();
                            g.startGame();

                            if (g.endless.getNight() == 8)
                                AchievementHandler.obtain(g, Achievements.SURVIVOR);
                            if (g.endless.getNight() == 14)
                                AchievementHandler.obtain(g, Achievements.IS_IT_POSSIBLE);
                        }
                    }
                    case SHADOW -> {
                        if(checkIfPassedHalfway()) {
                            secondHalfShadowNight();
                        }
                        if(checkForCompletion()) {
                            if(event != GameEvent.ASTARTA) {
                                astartaBoss = new AstartaBoss(g);
                                astartaBoss.start();
                                g.reachedAstartaBoss = true;
                                event = GameEvent.ASTARTA;

                                DiscordRichPresence rich = new DiscordRichPresence.Builder
                                        ("In-Game")
                                        .setDetails("SHADOWNIGHT - SHADOW ASTARTA")
                                        .setBigImage("shadownight", "PÉPITO RETURNED HOME")
                                        .setSmallImage("pepito", "PÉPITO RETURNED HOME")
                                        .setStartTimestamps(g.launchedGameTime)
                                        .build();

                                DiscordRPC.discordUpdatePresence(rich);
                            }
                        }
                    }
                    case BASEMENT, BASEMENT_PARTY -> {
                        Basement basement = (Basement) env;

                        if(basement.getStage() < 0 && seconds >= secondsAtStart + 25) {
                            basement.setStage((byte) 0);
                            g.restartBasementSong();
                        }
                        if(basement.getStage() < 1 && seconds >= secondsAtStart + 120 && event != GameEvent.MILLY_ARRIVES_BASEMENT) {
                            g.stopBasementSong();
                            basementMillyArrive();
                        }
                        if(basement.getStage() < 2 && seconds >= secondsAtStart + 210 && event != GameEvent.VENT_OFF_BASEMENT) {
                            g.stopBasementSong();
                            basementVentOff();
                        }
                        if(basement.getStage() < 3 && seconds >= secondsAtStart + 333 && event != GameEvent.MILLY_ARRIVES_BASEMENT) {
                            g.stopBasementSong();
                            basementMillyArrive();
                        }
                        if(basement.getStage() < 4 && seconds >= secondsAtStart + 393) {
                            g.stopBasementSong();
                            basementEndingEncounter();
                        }
                        
                        
                        if(type == GameType.BASEMENT && event.canTimeTick()) {
                            for (int i = 75; i < 400; i += 50) {
                                if (seconds == secondsAtStart + i) {
                                    basementDoorBlocks();
                                }
                            }
                        }
                        
                        if(!basement.isShowPsyop() && g.offsetX > 153) {
                            if(Math.random() < 0.0007) {
                                basement.setShowPsyop(true);
                                g.repaintOffice();
                            }
                        }
                        if(Math.random() < 0.0008) {
                            for(int i = 0; i < (int) (Math.random() * 2 + 4); i++) {
                                GamePanel.balloons.add(new Balloon(0));
                            }
                        }
                        if(Math.random() < 0.00045) {
                            g.sound.play("scarySkibidiSound", 0.1);
                        }
                        if(Math.random() < 0.0007) {
                            if(event == GameEvent.NONE) {
                                if (g.offsetX < 80) {
                                    if (basement.rumbleSog == null) {
                                        basement.rumbleSog = new RepeatingPepitimer(() -> {
                                            g.sound.play("shitscream", 0.2, -0.5);
                                            
                                            if(event != GameEvent.NONE) {
                                                basement.rumbleSog.cancel(true);
                                                basement.rumbleSog = null;

                                                for(MediaPlayer player : g.sound.clips) {
                                                    if(player.getMedia().getSource().contains("shitscream.mp3")) {
                                                        player.stop();
                                                        player.dispose();
                                                        g.sound.clips.remove(player);
                                                    }
                                                }
                                            }
                                        }, 0, 3300);

                                        cancelAfterGame.add(basement.rumbleSog);
                                    }
                                }
                            }
                        }
                        if(Math.random() < 0.00015 && ((seconds - secondsAtStart) > 50)) {
                            if(event == GameEvent.NONE) {
                                g.getNight().getMrMaze().spawn();
                            }
                        }
                        if(basement.doWiresWork() && Math.random() < 0.015 && event.canTimeTick()) {
                            basement.spark();
                            g.sound.playRate("sparkSound", 0.1, 0.9 + Math.random() / 5F);
                        }
                    }
                    case HYDROPHOBIA -> {
                        HChamber hChamber = (HChamber) env;
                        
                        if(hChamber.isInPrefield()) {
                            seconds--;
                        } else {
                            if(!hChamber.isAfterField()) {
                                if (seconds > 210) {
                                    hChamber.setPendingPrefieldRoom(true);
                                }
                            }
                            if (checkForCompletion()) {
                                hChamber.setPendingRewardRoom(true);
                            }
                        }
                    }
                }
                
                
                float reconsideration = 0.9F;
                if(pepito.isNotPepito) {
                    if(pepito.seconds <= 0 && pepito.notPepitoRunsLeft < 2 && pepito.notPepitoKnocksLeft > 0) reconsideration += 0.9F;
                } else {
                    if(pepito.seconds <= 0 && pepito.pepitoStepsLeft < 3 && pepito.pepitoKnocksLeft > 1) reconsideration += 0.6F;
                }
                if(msi.arriving) reconsideration += 0.1F;
                if(msi.isActive()) reconsideration += 1F;
                if(astarta.blinker) reconsideration += 0.5F;
                if(scaryCat.isActive()) reconsideration += 0.7F;
                if(shock.isDoom()) reconsideration += 0.7F;
                if(boykisser.isActive()) reconsideration += 0.4F;
                

                if (event.isInGame()) {
                    if(Math.random() < 0.0001 && (int) (randomSogAlpha) == 0) {
                        RepeatingPepitimer[] timer = new RepeatingPepitimer[1];
                        short[] times = new short[] {0};

                        timer[0] = new RepeatingPepitimer(() -> {
                            times[0]++;
                            if(times[0] < 101) {
                                randomSogAlpha += 0.0007F;
                            } else if(times[0] > 200) {
                                randomSogAlpha -= 0.0007F;

                                if(times[0] > 300) {
                                    randomSogAlpha = 0;
                                    timer[0].cancel(false);
                                }
                            }
                        }, 50, 50);
                    }

                    if (g.inCam) {
                        g.updateCam();
                    }
                    
                    shark.tick(); // checks for canspawnentities
                    dsc.tick(); // checks for canspawnentities
                    
                    if(type != GameType.DAY) {
                        if (event.canSpawnEntities()) {
                            elAstarta.tick();
                            lemonadeCat.tick();
                            kiji.tick();
                            
                            pepito.tick(reconsideration - 0.6F);
                            msi.tick(type == GameType.SHADOW);
                            colaCat.tick();
                            astarta.tick(reconsideration - 0.6F);
                            maki.tick();
                            mirrorCat.tick();
                            wires.tick(reconsideration - 0.5F);
                            boykisser.tick(reconsideration - 0.6F);
                            scaryCat.tick();
                            shock.tick();
                            toleTole.tick();
                            
                            hydrophobia.tick();
                            beast.tick();
                            overseer.tick();

                            if(jumpscareCat.isEnabled()) {
                                if(afk <= jumpscareCat.getAILevel()) {
                                    int millis = ((int) (Math.random() * 7 + 3)) * 1000;
                                    new Pepitimer(jumpscareCat::spawn, millis);
                                    afk = 16;
                                }
                            }
                        } else if(event == GameEvent.ENDING_BASEMENT) {
                            Basement basement = (Basement) env;
                            if(basement.getStage() == 5) {
                                pepito.tick(0);
                            }
                        }
                        if (!g.adblocker.isEnabled() && !g.adBlocked) {
                            if (g.inCam) {
                                glitcher.increaseCounter();
                            } else {
                                glitcher.decreaseCounter();
                            }
                        }
                    }
                }

                if(!g.adblocker.isEnabled() && !g.adBlocked) {
                    if(g.bingoCard.isTimeGoing())
                        reconsideration = 0;
                    
                    a90.tick(reconsideration);
                }

                if (event == GameEvent.FLOOD) {
                    if (g.waterSpeed < (3 + shark.getAILevel() * 2)) {
                        g.waterSpeed++;
                    }
                    if (g.fanActive) {
                        shark.floodDuration--;
                    }
                }
                if (event == GameEvent.DEEP_FLOOD) {
                    if (g.waterSpeed < (3 + dsc.getAILevel() * 2)) {
                        g.waterSpeed++;
                    }
//                    if (g.fanActive) {
//                        dsc.floodDuration--;
//                    }
                }
            } else if(event == GameEvent.BASEMENT_KEY) {
                if(Math.random() < 0.065) {
                    g.fadeOut(g.endFade / 2, g.endFade, 2);
                    g.sound.play("thunder" + (int) (Math.random() * 3 + 1), 0.1);
                }
            }

            
            if(event.isUsingSensor()) {
                if(g.sensor.isEnabled()) {
                    if(!g.console.isRatting && a90.arrivalSeconds < 2) {
                        int random = (int) Math.round(Math.random() * 2);
                        if (random == 0) {
                            g.console.add(GamePanel.getString("sensorA90"));
                        }
                    }

                    int random = (int) Math.round(Math.random() * 159);
                    if(random == 0) {
                        g.console.rat();
                    }
                }
                
                if(g.console.timer == 0) {
                    g.console.removeLast();
                    g.console.timer = 4;
                }
                g.console.timer--;
            }

            if(event.canUseItems()) {
                g.metalPipeCooldown = (byte) Math.max(0, g.metalPipeCooldown - 1);
                g.flashLightCooldown = (byte) Math.max(0, g.flashLightCooldown - 1);
            }
        });

        g.everySecond10th.put("energy", () -> {
            if(g.state != GameState.GAME)
                return;
            if(energy > maxEnergy) {
                maxEnergy = energy;
            }

            if(flicker > 0) {
                flicker -= 4;
            }

            eventPercent = Math.max(0, eventPercent - 0.003F);

            float energyToll = 0;
            boolean isTemperatureEnabled = !(type == GameType.SHADOW || (type == GameType.CUSTOM && !CustomNight.isCustom() && CustomNight.selectedChallenge == 1));
            // if not shadow && if not challenge 2

            if(g.fan.isEnabled() && type != GameType.HYDROPHOBIA) {
                if (g.fanActive) {
                    energyToll += 0.08F;

                    temperature = Math.min(Math.max(0, temperature - 0.06F), 100);

                    if (pepito.notPepitoChance > 8) {
                        pepito.notPepitoChance = g.mathRound(pepito.notPepitoChance - 0.01F);
                    }
                } else if(isTemperatureEnabled) { // if fan isn't active
                    temperature = Math.min(Math.max(0, temperature + 0.03F), 100);
                }
            } else if(isTemperatureEnabled) { // if fan isn't enabled
                temperature = Math.min(Math.max(0, temperature + 0.025F), 100);
                
                if(type == GameType.HYDROPHOBIA) {
                    temperature = Math.min(Math.max(0, temperature + 0.015F), 100);
                }
            }
            
            if(type == GameType.HYDROPHOBIA) {
                HChamber chamber = (HChamber) env;
                if(chamber.hasConditioner() && chamber.getRoom() > 0) {
                    temperature = Math.min(Math.max(0, temperature - 0.12F), 100);
                }
                
                if(!chamber.hasCup() && chamber.cup.x < 1990 && !chamber.isInDustons()) {
                    if (chamber.ermIndex < chamber.ermWhatTheSigma.length) {
                        chamber.ermTimer--;
                        if (chamber.ermTimer <= 0) {
                            switch (chamber.ermWhatTheSigma[chamber.ermIndex]) {
                                case 1 -> g.sound.play("smokeAlarmShort", 0.1);
                                case 2 -> g.sound.play("smokeAlarmLong", 0.1);
                            }
                            chamber.ermIndex++;
                            chamber.ermTimer = 4;
                        }
                    } else {
                        chamber.ermIndex = 0;
                    }
                }
            }
            
            if(event.isFlood() || blizzardTime > 0) {
                if(g.fan.isEnabled()) {
                    temperature = Math.min(Math.max(0, temperature - 0.035F), 100);
                } else {
                    temperature = Math.min(Math.max(0, temperature - 0.02F), 100);
                }
            }
            
            if(type.isBasement()) {
                Basement basement = (Basement) env;
                if(basement.getStage() == 6) {
                    temperature = Math.min(Math.max(0, temperature + 0.05F), 100);
                } else if(basement.getStage() == 7) {
                    temperature = 0;
                }
            }
            

            if(!noSignalFromHeat && (Math.random() * 0.9) > heatDistort()) {
                noSignalFromHeat = true;
                repaintMonitorImage();
                
                new Pepitimer(() -> {
                    noSignalFromHeat = false;
                    repaintMonitorImage();
                }, 300);
            }

            if(g.usage >= 4) {
                BingoHandler.completeTask(BingoTask.FOUR_USAGE_BARS);
            }
            
            if(g.keyHandler.holdingFlashlight) {
                energyToll += 0.09F;
            }
            if(event.canProcessEnergy())  {
                energyToll -= Math.max(0, g.usage / 6.0F);
                if(event == GameEvent.ASTARTA) {
                    if(astartaBoss.isFighting() && astartaBoss.getDyingStage() < 6) {
                        energyToll -= 0.5F;
                        if(astartaBoss.getDyingStage() > 0) {
                            energyToll += 0.25F;
                        }
                    }
                }

                for (Door d : doors.values()) {
                    if (d.isClosed()) {
                        energyToll -= 0.12F;
                    }
                }

                if(power) {
                    if (energy > 0) {
                        if (elAstarta.isActive()) {
                            energyToll /= 2.5F;
                        }
                        energy += energyToll;
                    } else {
                        g.powerDown();
                    }

                } else if(powerModifier && generatorEnergy > 0) {
                    generatorEnergy = Math.max(0, generatorEnergy + energyToll / 1.5F);

                    if(generatorEnergy <= 0) {
                        for(Door door : doors.values()) {
                            if(door.isClosed()) {
                                door.setClosed(false);
                                g.usage--;
                                g.sound.play("doorSlam", 0.08);
                                g.redrawUsage();
                            }
                        }
                    }
                }
            }
            
            if (type.isBasement()) {
                if(event.isFlood()) {
                    if (red40Phase > 0) {
                        if (Math.random() < 0.055) {
                            g.fadeOut(255, g.endFade, 0.5F);
                            g.sound.play("scaryCatAttackSlow", 0.15);
                        }
                        if (Math.random() < 0.025) {
                            g.hallucinations.add(new Hallucination());

                            playTempAmbient(g.sound);
                        }
                        if (Math.random() < 0.025) {
                            jumpscareCat.spawn();
                        }
                    }
                }
            }
            if(radiationModifier && radiation > 10) {
                float chance = Math.max(0, radiation - 35) / 120F;
                if(isIrradiated) {
                    chance *= 1.5F;
                    chance += 0.09F;
                }
                if(Math.random() < chance) {
                    g.sound.play("geiger" + (int) (Math.random() * 4 + 1), g.manualY < 535 ? 0.11 : 0.04);
                    // additional sound
                    if(Math.random() < chance) {
                        new Pepitimer(() -> {
                            g.sound.play("geiger" + (int) (Math.random() * 4 + 1), g.manualY < 535 ? 0.11 : 0.04);
                        }, 40 + (int) (Math.random() * 20));
                    }
                }
            }
        });

        switch (type) {
            case SHADOW -> {
                g.music.play("theShadow", 0.1);
                energy = 503;
            }
            case PREPARTY, BASEMENT -> {
                for(int i = 0; i < 5; i++){
                    GamePanel.balloons.add(new Balloon());
                }
            }
            case PARTY, BASEMENT_PARTY -> {
                g.sound.play("explode", 0.5);
                energy = 528;
            }
            case HYDROPHOBIA -> {
                g.music.playFromSeconds("hydrophobiaSounds", 0.18, 0);

                AchievementHandler.obtain(g, Achievements.HYDROPHOBIA);
            }
            case CUSTOM -> {
                if(isPerfectStorm()) {
                    g.music.play("stormfury", 0.15);
                    energy = 528;
                }
                if(isRainModifier()) {
                    g.rainSound.play("rain", 0.15, true);
                }
            }
        }
        
        if(night > 1 && type == GameType.DAY) {
            if (Math.random() < 0.0727) {
                isBillyShop = true;
            }
        }
    }

    boolean startedCustomWin = false;

    public float randomSogAlpha = 0;


    int flicker = 0;

    public int getFlicker() {
        return flicker;
    }

    float temperature = 0;

    public void resetTimers() {
        g.every6s.remove("game");
        g.everySecond.remove("game");
        g.everySecond10th.remove("energy");

        if(shirtfart != null) {
            shirtfart.cancel(true);
        }
        
        for(Pepitimer timer : cancelAfterGame) {
            if(timer != null) {
                timer.cancel();
            }
        }
    }

    float eventPercent = 0;
    public void addEventPercent(float f) {
        eventPercent += f;
    }
    public float getEventPercent() {
        return eventPercent;
    }

    private void sortAILevels(Corn[] corns) {
        if(GamePanel.krunlicPhase > 0)
            return;
        
        if(type == GameType.CUSTOM) {
            for(CustomNightEnemy enemy : CustomNight.getEnemies()) {
                int enemyAI = enemy.getAI();
                if(isPerfectStorm()) {
                    enemyAI = 7;
                }

                Enemy en = getEnemies()[enemy.getId()];
                en.setAILevel(enemyAI);
                System.out.println("put CustomNightEnemy " + enemy.getName() + " to Enemy " + en.getClass().getName());
                
                switch (enemy.getId()) {
                    case 0 -> pepito.setPepitoAI((byte) enemyAI);
                    case 1 -> pepito.setNotPepitoAI((byte) enemyAI);
                }
            }
            if(scaryCat.getAILevel() == 9) {
                System.out.println("ermmm... awkward!!!!! :v");
                System.out.println("set scary cat back to AI 8");
                System.out.println("activated nuclear scary cat");
                scaryCat.setAILevel(8);
                scaryCat.setNine(true);
            }
            if(lemonadeCat.getAILevel() == 9) {
                System.out.println("ermmm... awkward!!!!! :v");
                System.out.println("set LEMONADE CAT back to AI 8");
                System.out.println("activated nuclear LEMONADE CAT");
                lemonadeCat.setAILevel(8);
                lemonadeCat.setNine(true);
            }
            return;
        }

        if(type == GameType.DAY) {
            a90.setAILevel(1);
            
            return;
        }

        pepito.setPepitoAI(generateAILevel(night, 1, 0, corns));
        pepito.setNotPepitoAI(generateAILevel(night, 1, 0, corns));

        if(!g.adblocker.isEnabled()) {
            glitcher.setAILevel(generateAILevel(night, 1, 0, corns));
            a90.setAILevel(generateAILevel(night, 1, 0, corns));
        }

        if((g.soda.isEnabled() && (g.flashlight.isEnabled() || g.metalPipe.isEnabled())) || g.megaSoda.isEnabled()) {
            colaCat.setAILevel(generateAILevel(night, 1, 0, corns));
        }

        astarta.setAILevel(generateAILevel(night, 2, 0, corns));
        jumpscareCat.setAILevel(generateAILevel(night, 1, 0));
        
        if(type.isEndless()) {
            if (night == 18) {
                msi.isShadow = true;
                msi.arrivalSeconds = 18;
            }
        }
        msi.setAILevel(generateAILevel(night, 2, 0));
        msi.resetCounter();

        switch (type) {
            case CLASSIC -> {
                jumpscareCat.setAILevel(generateAILevel(night, 2, 0));
                astarta.setAILevel(generateAILevel(night, 2, -1));
                shark.setAILevel(generateAILevel(night, 3, 0));
                boykisser.setAILevel(generateAILevel(night, 4, 0));
            }
            case SHADOW -> {
                pepito.setPepitoAI((byte) 4);
                pepito.setNotPepitoAI((byte) 4);
                glitcher.setAILevel(4);
                a90.setAILevel(7);
                astarta.setAILevel(3);
                msi.setAILevel(2);
                shark.setAILevel(5);
                boykisser.setAILevel(5);
                colaCat.setAILevel(4);
                mirrorCat.setAILevel(4);
            }
            case PREPARTY -> {
                pepito.setPepitoAI(4);
                pepito.setNotPepitoAI(3);
                astarta.setAILevel(4);

                msi.setAILevel(2);
                msi.resetCounter();
                
                shark.setAILevel(4);
                boykisser.setAILevel(4);
                glitcher.setAILevel(4);
                a90.setAILevel(4);
                colaCat.setAILevel(4);
            }
            case PARTY -> {
                pepito.setPepitoAI((byte) 7);
                pepito.setNotPepitoAI((byte) 7);
                glitcher.setAILevel(7);
                a90.setAILevel(6);
                astarta.setAILevel(6);
                msi.setAILevel(5);
                shark.setAILevel(6);
                boykisser.setAILevel(7);
                colaCat.setAILevel(4);
            }
            case ENDLESS_NIGHT -> {
                shark.setAILevel(generateAILevel(night, 3, 2, corns));

                boykisser.setAILevel(generateAILevel(night, 4, 3, corns));

                maki.setAILevel(generateAILevel(night, 5, 4, corns));
                lemonadeCat.setAILevel(generateAILevel(night, 5, 4, corns));

                mirrorCat.setAILevel(generateAILevel(night, 6, 4, corns));

//                a120.setAILevel(generateAILevel(night, 8, 6, corns));
            }
            case BASEMENT -> {
                pepito.setPepitoAI((byte) 4);
                pepito.setNotPepitoAI((byte) 3);
                astarta.setAILevel(3);
                jumpscareCat.setAILevel(1);
                
                msi.setAILevel(3);
                msi.resetCounter();
                
                shark.setAILevel(2);
                boykisser.setAILevel(2);
                glitcher.setAILevel(1);
                a90.setAILevel(4);
                colaCat.setAILevel(1);
                
                dsc.setAILevel(1);
                shock.setAILevel(1);
                
                toleTole.setAILevel(1);
            }
            case BASEMENT_PARTY -> {
                pepito.setPepitoAI((byte) 4);
                pepito.setNotPepitoAI((byte) 4);
                astarta.setAILevel(3);
                jumpscareCat.setAILevel(1);

                msi.setAILevel(4);
                msi.resetCounter();

                shark.setAILevel(3);
                boykisser.setAILevel(3);
                glitcher.setAILevel(2);
                a90.setAILevel(4);
                colaCat.setAILevel(1);

                dsc.setAILevel(3);
                shock.setAILevel(2);

                toleTole.setAILevel(1);
            }
            case HYDROPHOBIA -> {
                pepito.setPepitoAI((byte) 0);
                pepito.setNotPepitoAI((byte) 0);
                astarta.setAILevel(0);
                jumpscareCat.setAILevel(0);
                msi.setAILevel(0);
                shark.setAILevel(0);
                boykisser.setAILevel(0);
                glitcher.setAILevel(0);
                a90.setAILevel(0);
                colaCat.setAILevel(0);
                
                hydrophobia.setAILevel(1);
                beast.setAILevel(1);
                overseer.setAILevel(1);
            }
        }
    }
    
    public void sortEnviornment() {
        if(type.isParty()) {
            env = new BirthdayOffice();
        }
        if(type == GameType.BASEMENT || type == GameType.BASEMENT_PARTY) {
            env = new Basement();
        }
        if(type == GameType.HYDROPHOBIA) {
            env = new HChamber();
        }
        g.megaColaImg = null;
        g.megaColaGlowImg = null;
        g.tintedMegaColaImg = null;
        g.tintedMegaSodaImg = null;
    }
    
    public void basementKey() {
        doors.clear();
        List<Point> poly1 = List.of(new Point(325+1280, 497), new Point(478+1280, 497), new Point(478+1280, 617), new Point(325+1280, 617));
        doors.put(0, new Door(new Point(270+1280, 500), g.door1Img, new Point(325+1280, 497), GamePanel.getPolygon(poly1), new Point(350+1280, 560)));

        g.rainSound.play("rain", 0.15, true);
        g.music.play("basementKeyOfficeSong", 0.08, true);
        
        if(Achievements.BASEMENT.isObtained() || Achievements.BASEMENT_PARTY.isObtained()) {
            g.music.play("brokenRadioSong", 0.1, true);
        }
        
        
        int x1 = 1269+1280;
        int x2 = 1451+1280;
        int[] xArray = new int[] {x1, x2, x2, x1};
        int[] yArray = new int[] {269, 264, 635, 620};

        doors.put(1, new Door(new Point(1160+1280, 280), g.door2Img, new Point(1262+1280, 264), new Polygon(xArray, yArray, 4), new Point(1300+1280, 487)));
        
        env = new BasementKeyOffice();
        event = GameEvent.BASEMENT_KEY;

        g.fadeOut(255, 180, 0.1F);
    }

    private void spawnManuals(byte night, GameType type) {
        if(type == GameType.ENDLESS_NIGHT && g.showManual && g.manual.isEnabled()) {
            switch (night) {
                case 2 -> g.manualBetterSpawn(GamePanel.getString("manualMSIAstarta"));
                case 3 -> g.manualBetterSpawn(GamePanel.getString("manualShark"));
                case 4 -> g.manualBetterSpawn(GamePanel.getString("manualBoykisser"));
                case 5 -> g.manualBetterSpawn(GamePanel.getString("manualMakiLemonade"));
                case 6 -> g.manualBetterSpawn(GamePanel.getString("manualZazu"));
                case 7 -> g.manualBetterSpawn("");
                case 8, 18 -> g.manualBetterSpawn("LEFT. RIGHT. LEFT. RIGHT. LEFT. RIGHT. LEFT. RIGHT. LEFT. RIGHT. LEFT. RIGHT. LEFT. RIGHT. LEFT. RIGHT. LEFT. RIGHT. LEFT. RIGHT. LEFT. RIGHT. LEFT. RIGHT");
            }
        }
        if(isRadiationModifier()) {
            g.manualSpawn();
        }
    }

    private void sortTime() {
        if(type == GameType.CUSTOM)
            return;

        seconds = (short) type.getSeconds();
        duration = (short) type.getDuration();
        
        if(type == GameType.CLASSIC && night == 1) {
            duration = 200;
        }
        if(type == GameType.ENDLESS_NIGHT) {
            if(g.endless.getNight() == 1) {
                duration = 200;
            }
        }
    }

    public short getSeconds() {
        return seconds;
    }

    public Enemy clearUnusedEntities(Enemy enemy) {
        if(enemy.getAILevel() > 0) {
            return enemy;
        }
        return null;
    }

    public String generateClock() {
        int hour = (seconds / 50) % 24;
        return ((hour > 12) ? hour - 12 : ((hour == 0) ? 12 : hour)) + " " + ((hour > 11) ? "PM" : "AM");
    }

    public void updateClockString() {
        clockString = generateClock();
    }
    
    
    public List<GlassParticle> glassParticles = new ArrayList<>();
    boolean clockBroken = false;
    public void breakClock() {
        g.sound.play("glassShatteringSound", 0.08);
        
        BufferedImage fred = new BufferedImage(1080, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = (Graphics2D) fred.getGraphics();
        graphics2D.setFont(g.sansSerifPlain70);
        int length = g.textLength(graphics2D, clockString);
        
        if (g.inCam) {
            graphics2D.drawString(clockString, 1045 - length, 90);
        } else {
            graphics2D.drawString(clockString, 1070 - length, 70);
        }
        graphics2D.setColor(Color.BLACK);
        graphics2D.setStroke(new BasicStroke(4));
        graphics2D.drawLine((int) (1080 - length + Math.random() * length), (int) (Math.random() * 100), (int) (1080 - length + Math.random() * length), (int) (Math.random() * 100));
        graphics2D.drawLine((int) (1080 - length + Math.random() * length), (int) (Math.random() * 100), (int) (1080 - length + Math.random() * length), (int) (Math.random() * 100));
        
        graphics2D.dispose();
        fred = GamePanel.trimImageRightBottom(fred);
        
        for(int x = 540; x < fred.getWidth(); x += 2) {
            for(int y = 0; y < fred.getHeight(); y += 2) {
                if (fred.getRGB(x, y) == -1) {
                    glassParticles.add(new GlassParticle(x, y));
                }
            }
        }
        clockBroken = true;
    }

    boolean completed = false;

    private boolean checkForCompletion() {
        if(completed)
            return false;
        if(GamePanel.krunlicPhase > 0) {
            if(seconds >= secondsAtStart + duration) {
                g.unshaded = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);
                g.jumpscare("krunlic", id);
                g.repaint();
                g.unshaded = new BufferedImage(1080, 640, BufferedImage.TYPE_BYTE_GRAY);
                return false;
            }
        }

        completed = seconds >= secondsAtStart + duration;
        return seconds >= secondsAtStart + duration;
    }

    boolean itemless = false;
    public void setItemless(boolean itemless) {
        this.itemless = itemless;
    }
    public boolean isItemless() {
        return itemless;
    }
    
    boolean soundless = false;
    public void setSoundless(boolean soundless) {
        this.soundless = soundless;
    }
    public boolean isSoundless() {
        return soundless;
    }

    short usedItemAmount = 0;
    public void setUsedItemAmount(short usedItemAmount) {
        this.usedItemAmount = usedItemAmount;
    }
    public short getUsedItemAmount() {
        return usedItemAmount;
    }

    
    public BufferedImage soggyPenCanvas;
    

    // shadownight ONLY
    private boolean checkIfPassedHalfway() {
        if(GamePanel.krunlicPhase > 0) {
            if(seconds >= 1300) {
                g.unshaded = new BufferedImage(1080, 640, BufferedImage.TYPE_INT_RGB);
                g.jumpscare("krunlic", id);
                g.repaint();
                g.unshaded = new BufferedImage(1080, 640, BufferedImage.TYPE_BYTE_GRAY);
                return false;
            }
        }
        
        return seconds >= 1300;
    }
    boolean reachedHalfway = false;

    public boolean hasReachedHalfway() {
        return reachedHalfway;
    }

    private void secondHalfShadowNight() {
        if(reachedHalfway)
            return;
        reachedHalfway = true;

        g.music.play("timesEnd", 0.1, true);

        pepito.setPepitoAI((byte) 6);
        pepito.setNotPepitoAI((byte) 6);
        glitcher.setAILevel(5);
        a90.setAILevel(7);
        astarta.setAILevel(5);
        msi.setAILevel(4);
        shark.setAILevel(7);
        boykisser.setAILevel(8);
        colaCat.setAILevel(0);
        mirrorCat.setAILevel(7);
        lemonadeCat.setAILevel(3); // 1
//        a120.setAILevel(1); // 1
        wires.setAILevel(5); // 2
        scaryCat.setAILevel(3); // 2

        energy += 400;
        maxEnergy += 400;

        g.announceNight((byte) 60, GameType.SHADOW);
        g.nightAnnounceText = GamePanel.getString("halfwayPointCaps");
        if(g.shadowCheckpointUsed != 2) {
            cancelAfterGame.add(new Pepitimer(() -> g.announceChallenger((byte) 60, 0, false), 8000));
            cancelAfterGame.add(new Pepitimer(() -> g.announceChallenger((byte) 61, 0, false), 13000));
        }

        AchievementHandler.obtain(g, Achievements.HALFWAY);

        g.fadeOut(80, 190, 1);

        DiscordRichPresence rich = new DiscordRichPresence.Builder
                ("In-Game")
                .setDetails("SHADOWNIGHT - HALFWAY POINT")
                .setBigImage("shadownight", "PÉPITO RETURNED HOME")
                .setSmallImage("pepito", "PÉPITO RETURNED HOME")
                .setStartTimestamps(g.launchedGameTime)
                .build();

        DiscordRPC.discordUpdatePresence(rich);
    }
    
    
    public void basementMillyArrive() {
        g.generateMillyItems();
        
        Basement basement = (Basement) env;
        event = GameEvent.MILLY_ARRIVES_BASEMENT;
        
        g.basementSound.stop();
        g.basementSound.play("mandatoryMillySection", 0.08);
        
        int endFadeBeforeEverything = g.endFade;
        g.fadeOut(255, g.endFade + (255 - g.endFade) / 2, 0.1F);
        
        basement.setPartiesPoster(Math.random() < 0.5);
        g.repaintOffice();

        g.getNight().getPepito().scare();
        g.getNight().getAstarta().leaveEarly();
        g.getNight().getMaki().scare();

        cancelAfterGame.add(new Pepitimer(() -> {
            basement.setMillyVisiting(true);
            basement.setMillyGoalX(490);
            // milly goes in

            cancelAfterGame.add(new Pepitimer(() -> {
                g.fadeOut(g.endFade + (255 - g.endFade) / 2, basement.doWiresWork() ? 120 : 210, basement.doWiresWork() ? 2.5F : 0.2F);
                basement.setMillyLamp(basement.doWiresWork());
                
                // lamp
            }, 1000));

            cancelAfterGame.add(new Pepitimer(() -> {
                g.fadeOut(g.endFade, g.endFade + (255 - g.endFade) / 2, 0.4F);
                
                basement.setMillyGoalX(basement.getStage() > 1 ? 1480 : -600);
                basement.setMillyLamp(false);
                // milly goes out

                cancelAfterGame.add(new Pepitimer(() -> {
                    // game continues normal

                    event = GameEvent.NONE;
                    g.fadeOut(255, endFadeBeforeEverything, 0.5F);
                    basement.setStage((byte) (basement.getStage() + 1));
                    
                    if(g.pishPish.isEnabled()) {
                        g.dryCatGame = new DryCatGame(basement.getStage() > 1);
                        
                        g.state = GameState.DRY_CAT_GAME;
                        g.music.play("dryCats", 0.1);
                    } else {
                        g.restartBasementSong();

                        GamePanel.balloons.add(new Balloon(0));
                        GamePanel.balloons.add(new Balloon(0));
                    }
                }, 3000));
            }, 16000));
        }, 6000));
    }
    
    public void basementVentOff() {
        Basement basement = (Basement) env;
        basement.setVentProgress(-2.46F);
        basement.setVent((byte) 1);
        g.repaintOffice();

        event = GameEvent.VENT_OFF_BASEMENT;

        g.getNight().getPepito().scare();
        g.getNight().getAstarta().leaveEarly();
        g.getNight().getMaki().scare();

        cancelAfterGame.add(new Pepitimer(() -> {
            event = GameEvent.NONE;
            basement.setStage((byte) (basement.getStage() + 1));

            g.restartBasementSong();
        }, 5000));
    }
    
    public void basementEndingEncounter() {
        new Pepitimer(this::breakClock, 1000);
        
        Basement basement = (Basement) env;
        
        redrawBasementScreen();
        
        basement.setStage((byte) 4);
        basement.setGeneratorStage((byte) 0);
        basement.regenerateGeneratorXes();
        
        event = GameEvent.ENDING_BASEMENT;

        g.getNight().setTemperature(0);
        
        g.stopBasementSong();
    }
    
    public void redrawBasementScreen() {
        Basement basement = (Basement) env;
        
        BufferedImage image = new BufferedImage(225, 115, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = (Graphics2D) image.getGraphics();

        graphics2D.setColor(Color.GREEN);
        
        if(basement.getCharge() < 1) {
            graphics2D.drawImage(g.basementMonitorBgCharge.request(), 0, 0, null);
            
            graphics2D.fillRect(20, 59, (int) (basement.getCharge() * 185), 40);
        } else if(basement.getStage() < 6) {
            graphics2D.drawImage(g.basementMonitorBg.request(), 0, 0, null);

            graphics2D.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 40));

            String string = "" + basement.getGeneratorStage();
            int halfTextLength = g.halfTextLength(graphics2D, string);

            graphics2D.drawString(string, 123 - halfTextLength, 94);
            graphics2D.drawImage(g.greenCharge.request(), 112 - halfTextLength - 20, 64, null);

            graphics2D.fillRect(20, 59, (int) (basement.getGeneratorStage() / 16F * 185F), 40);
        } else {
            if (basement.getConnectProgress() < 1) {
                graphics2D.drawImage(g.basementMonitorBgConnect.request(), 0, 0, null);

                graphics2D.fillRect(20, 59, (int) (basement.getConnectProgress() * 185), 40);
            } else {
                graphics2D.drawImage(g.basementMonitorBgConnect.request(), 0, 0, null);
                
                graphics2D.setColor(new Color(43, 42, 42));
                graphics2D.fillRect(17, 13, 191, 89);
                
                List<Point> poly = new ArrayList<>();
                poly.add(new Point(17 + (int) (191 * (Math.sin(g.fixedUpdatesAnim / 20F) / 2 + 0.5F)), 13));
                poly.add(new Point(17 + (int) (191 * (Math.cos(g.fixedUpdatesAnim / 20F) / 2 + 0.5F)), 13));

                poly.add(new Point(17 + (int) (191 * (Math.sin(g.fixedUpdatesAnim / 35F) / 2 + 0.5F)), 102));
                poly.add(new Point(17 + (int) (191 * (Math.cos(g.fixedUpdatesAnim / 35F) / 2 + 0.5F)), 102));
                
                graphics2D.setColor(Color.GREEN);
                graphics2D.fillPolygon(GamePanel.getPolygon(poly));
            }
        }
        
        graphics2D.dispose();

        basement.generatorMinigameMonitor = image;
    }
    
    
    public void setDefaultBasementDoors() {
        if(!doors.containsKey(0)) {
            List<Point> poly1 = List.of(new Point(1256, 326), new Point(1386, 316), new Point(1386, 612), new Point(1257, 573));
            doors.put(0, new Door(new Point(1408, 315), g.basementDoor1Img, new Point(1256, 316), GamePanel.getPolygon(poly1), new Point(1264, 468)));
        }

        if(!doors.containsKey(1)) {
            List<Point> poly2 = List.of(new Point(69, 319), new Point(197, 329), new Point(197, 576), new Point(69, 618));
            doors.put(1, new Door(new Point(10, 322), g.basementDoor2Img, new Point(67, 317), GamePanel.getPolygon(poly2), new Point(84, 476)));
        }

        if(!doors.containsKey(2)) {
            List<Point> poly3 = List.of(new Point(1003, 351), new Point(1072, 346), new Point(1072, 517), new Point(1003, 497));
            doors.put(2, new Door(new Point(1072, 336), g.basementDoor3Img, new Point(1001, 345), GamePanel.getPolygon(poly3), new Point(1004, 454)));
            doors.get(2).setVisualSize(0.66F);
        }

        if(!doors.containsKey(3)) {
            List<Point> poly4 = List.of(new Point(370, 348), new Point(439, 353), new Point(439, 497), new Point(370, 519));
            doors.put(3, new Door(new Point(322, 338), g.basementDoor4Img, new Point(369, 345), GamePanel.getPolygon(poly4), new Point(371, 451)));
            doors.get(3).setVisualSize(0.66F);
        }
    }
    
    public void basementDoorBlocks() {
        setDefaultBasementDoors();
        
        Basement basement = (Basement) env;
        
        int blockedDoorRight = Math.random() < 0.5 ? 0 : 2;
        int blockedDoorLeft = Math.random() < 0.5 ? 1 : 3;

        if(!basement.blockedWalls.isEmpty()) {
            while (blockedDoorRight == basement.blockedWalls.get(0) && blockedDoorLeft == basement.blockedWalls.get(1)) {
                blockedDoorRight = Math.random() < 0.5 ? 0 : 2;
                blockedDoorLeft = Math.random() < 0.5 ? 1 : 3;
            }
        }
        
        
        List<Integer> iterate = new ArrayList<>(List.of(blockedDoorRight, blockedDoorLeft));
        
        if(pepito.isNotPepito && pepito.seconds < 2) {
            iterate.remove((Integer) ((int) pepito.getDoor()));
            pepito.setFlicker(0);
        }

        basement.blockedWalls.clear();

        for(int i : iterate) {
            if(doors.containsKey(i)) {
                if (doors.get(i).isLocked()) {
                    g.usage--;
                    g.sound.play("doorSlam", 0.08, Level.getDoorPan(doors.get(i), g.getNight().getType()));
                }
            }

            doors.remove(i);
            basement.blockedWalls.add(i);

            if(pepito.getDoor() == i && pepito.seconds < 2) {
                pepito.scare();
            }
            if(astarta.door == i && astarta.isActive()) {
                astarta.leaveEarly();
            }
            if(maki.getDoor() == i && maki.isActive()) {
                maki.scare();
            }
            if(shock.getDoor() == i && shock.isActive()) {
                shock.stop();
            }
            if(toleTole.isAimingDoor() || toleTole.isGonnaLeave()) {
                toleTole.gonnaLeave = false;
                toleTole.findDoor();
            }
        }
        
        g.redrawUsage();

        g.sound.play("basementDoorReblock", 0.16);
        basement.setShake(30);

        g.repaintOffice();
    }
    
    
    public void redrawHChamberTimer(HChamber env) {
        env.timerText = new BufferedImage(env.timer.width, env.timer.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = (Graphics2D) env.timerText.getGraphics();

        if(env.shownPrefieldMSILogo) {
            graphics2D.drawImage(g.hcPrefieldMsiLogo.request(), env.timer.width / 2 - 62, env.timer.height / 2 - 62, null);
        } else {
            int size = (int) (env.timer.height / 1.5);

            float limited = Math.min(1, overseer.getRage());
            graphics2D.setColor(new Color((int) (255 * limited), (int) (255 * (1 - limited)), 0));

            if (env.isRewardRoom() || env.getPrefieldCount() > 0) {
                graphics2D.setColor(Color.ORANGE);
            }

            
            if(!env.isTimerBroken()) {
                graphics2D.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, size));

                String str = "0:" + (env.getUntilDoor() < 10 ? "0" : "") + env.getUntilDoor();
                graphics2D.drawString(str, env.timer.width / 2 - g.halfTextLength(graphics2D, str), env.timer.height / 2 + size / 3);
                
                
            } else {
                String str1 = "- MSI -";
                String str2 = "no connection";
                
                graphics2D.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, size / 5 * 3));
                graphics2D.drawString(str1, env.timer.width / 2 - g.halfTextLength(graphics2D, str1), env.timer.height / 2);
                
                graphics2D.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, size / 3 + 1));
                graphics2D.drawString(str2, env.timer.width / 2 - g.halfTextLength(graphics2D, str2), env.timer.height / 4 * 3);
            }
        }

        graphics2D.dispose();
    }
    
    public void repaintMonitorImage() {
        Rectangle monitor = env.getMonitor();
        monitorImage = new BufferedImage(196, 90, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = monitorImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        graphics2D.setColor(new Color(42, 42, 43));
        graphics2D.fillRect(0, 0, monitor.width, monitor.height);
        
        if(type == GameType.DAY) {
            int[] events = new int[] {6, 4, 3};
            String nextEvent = GamePanel.getString("monitorForgot");
            boolean noNextEvent = true;

            for (int event : events) {
                if (g.endless.getNight() < event) {
                    nextEvent = "" + event;
                    noNextEvent = false;
                }
            }
            graphics2D.setColor(new Color(135, 117, 28));
            graphics2D.setFont(g.comicSans40);
            if(noNextEvent) {
                graphics2D.setFont(g.comicSans25);
            }
            graphics2D.drawString(GamePanel.getString("monitorDay") + nextEvent, monitor.width / 2 -  g.halfTextLength(graphics2D, GamePanel.getString("monitorDay") + nextEvent), 60);
            
        } else if (powerModifier) {
            graphics2D.setColor(new Color(160, 130, 28));
            graphics2D.setFont(g.comicSans30);
            String next = GamePanel.getString("monitorOutage");
            String number;
            if(!power) {
                next = GamePanel.getString("monitorPowerBack");
                number = outageDuration + "";
                graphics2D.setFont(g.comicSansBold25);
            } else {
                if(!outages.isEmpty()) {
                    List<Integer> outages = new ArrayList<>(this.outages.keySet());
                    Collections.sort(outages);
                    number = (outages.get(0) + secondsAtStart - seconds) + "";
                } else {
                    number = "???";
                }
            }

            graphics2D.drawString(next, monitor.width / 2 - g.halfTextLength(graphics2D, next), 40);
            graphics2D.setFont(g.comicSans30);
            graphics2D.drawString(number, monitor.width / 2 - g.halfTextLength(graphics2D, number), 75);
        } else {
            graphics2D.drawImage(g.monitorBasic.request(), 0, 0, null);
        }

        boolean noSignal = noSignalFromHeat || event == GameEvent.BASEMENT_KEY;
        if (!event.isGuiEnabled() || noSignal) {
            graphics2D.drawImage(g.monitorNoSignal.request(), 0, 0, null);
        }
        
        monitorImage = GamePanel.resize(monitorImage, env.getMonitor().width, env.getMonitor().height, Image.SCALE_SMOOTH);

        if(type == GameType.SHADOW) {
            monitorImage = GamePanel.purplify(monitorImage);
        }
        
        Graphics2D officeGraphics = (Graphics2D) g.fullOffice.getGraphics();
        officeGraphics.drawImage(monitorImage, env.getMonitor().x, env.getMonitor().y, null);
        officeGraphics.dispose();
    }
    
    
    public Enemy[] getEnemies() {
//        return new Enemy[] {a90, msi, glitcher, astarta, colaCat, maki, shark, boykisser, lemonadeCat, mirrorCat, a120, wires, scaryCat, jumpscareCat, elAstarta, kiji,
//                shock, toleTole, dsc, hydrophobia, beast, overseer};
        return new Enemy[] {pepito.getPepito(), pepito.getNotPepito(), glitcher, a90, msi, astarta,
                            shark, boykisser, colaCat, mirrorCat, maki, lemonadeCat,
                            wires, scaryCat, jumpscareCat, elAstarta, dsc, shock,
                            hydrophobia, beast, overseer, a120};
    }
    
    
    public List<Pepitimer> cancelAfterGame = new ArrayList<>();

    private byte generateAILevel(int night, int startNight, int minus, Corn[] corns) {
        byte AI = generateAILevel(night, startNight, minus);

        for(byte i = 0; i < 2; i++) {
            if (corns[i].isEnabled() && corns[i].getStage() >= 2 && AI >= 2) {
                AI--;
            }
        }

        return AI;
    }

    private byte generateAILevel(int night, int startNight, int minus) {
        byte AI;

        if(night >= 12) {
            AI = night >= 18 ? (byte) (10) : (byte) (9);
        } else {
            AI = (byte) Math.min(night, 8);
        }

        AI -= (byte) minus;

        if(night < startNight) {
            return 0;
        }

        return AI;
    }

    public GameEvent getEvent() {
        return event;
    }
    public void setEvent(GameEvent event) {
        this.event = event;
    }

    public GameType getType() {
        return type;
    }

    public void setType(GameType type) {
        this.type = type;
    }

    public String getClockString() {
        return clockString;
    }

    public short getDuration() {
        return duration;
    }

    public boolean hasPower() {
        return power;
    }

    public void depower() {
        power = false;
        g.console.power = false;
    }
    public void power() {
        power = true;
        g.console.power = true;
    }
    public AstartaBoss getAstartaBoss() {
        return astartaBoss;
    }
    public ShadowPepito getShadowPepito() {
        return shadowPepito;
    }

    public float getTemperature() {
        return temperature;
    }
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    // sound and timer distortion from temperature
    // 1 at 0 temp
    // 1 at 20 temp
    // 0.6 at 100 temp
    public float heatDistort() {
        return 1 - (Math.max(0, (temperature - 20)) * 0.005F);
    }
    boolean noSignalFromHeat = false;

    public boolean getNoSignalFromHeat() {
        return noSignalFromHeat;
    }

    public void setFlicker(int flicker) {
        this.flicker = flicker;
    }

    public BufferedImage getMonitorImage() {
        return monitorImage;
    }

    public boolean isClockBroken() {
        return clockBroken;
    }
    
    
    

    private float generatorEnergy = -1;
    public byte generatorStage = 0;
    public short[] generatorXes = new short[] {-1, -1, -1};
    public boolean inGeneratorMinigame = false;
    boolean powerModifier = false;
    public HashMap<Integer, Integer> outages = new HashMap<>();
    int outageDuration = 0;
    public boolean isPowerModifier() {
        return powerModifier;
    }
    public boolean isInGeneratorMinigame() {
        return inGeneratorMinigame;
    }

    public void startGeneratorMinigame() {
        generatorStage = 0;
        regenerateGeneratorXes();
        inGeneratorMinigame = true;

        g.generatorSound.play("connectToGenerator", 0.15, true);
    }
    
    public void regenerateGeneratorXes() {
        generatorXes[0] = (short) (50 + Math.random() * 520);
        generatorXes[1] = (short) (50 + Math.random() * 520);
        generatorXes[2] = (short) (50 + Math.random() * 520);

        Rectangle firstRect = new Rectangle(220 + generatorXes[0], 495, 35, 1);
        Rectangle secondRect = new Rectangle(220 + generatorXes[1], 495, 35, 1);
        Rectangle thirdRect = new Rectangle(220 + generatorXes[2], 495, 35, 1);

        if(firstRect.intersects(secondRect) || secondRect.intersects(thirdRect) || thirdRect.intersects(firstRect)) {
            regenerateGeneratorXes();
        }
    }
    
    public float getGeneratorEnergy() {
        return generatorEnergy;
    }
    public void setGeneratorEnergy(float generatorEnergy) {
        this.generatorEnergy = generatorEnergy;
    }
    public void addGeneratorEnergy(float add) {
        this.generatorEnergy += add;
    }




    short blizzardTime = 0;
    short blizzardFade = 0;
    boolean blizzardModifier = false;
    public boolean isBlizzardModifier() {
        return blizzardModifier;
    }
    public short getBlizzardFade() {
        return blizzardFade;
    }
    public short getBlizzardTime() {
        return blizzardTime;
    }
    public void startBlizzard() {
        g.sound.play("icePotionUse", 0.1);
        blizzardTime = 35;
        blizzardFade = 400;
        if(pepito.seconds > 1) {
            pepito.seconds = 1;
        }
        if(astarta.arrivalSeconds > 4) {
            astarta.arrivalSeconds = 4;
        }

        g.everyFixedUpdate.put("blizzardFade", () -> {
            blizzardFade--;
            if(blizzardFade <= 0) {
                g.everyFixedUpdate.remove("blizzardFade");
                blizzardFade = 0;
            }
        });
    }

    boolean timerModifier = false;
    HashMap<Door, Float> timers = new HashMap<>();
    public boolean isTimerModifier() {
        return timerModifier;
    }
    public HashMap<Door, Float> getTimers() {
        return timers;
    }


    boolean fogModifier = false;
    public boolean isFogModifier() {
        return fogModifier;
    }


    public short startUIFade = -200;
    public short gruggyX = -600;
    float radiation = 0;
    boolean radiationModifier = false;
    public boolean isRadiationModifier() {
        return radiationModifier;
    }
    public List<GruggyCart> gruggyCarts = new ArrayList<>();
    public float getRadiation() {
        return radiation;
    }
    public void setRadiation(float radiation) {
        this.radiation = radiation;
    }
    public boolean isIrradiated = false;

    boolean rainModifier = false;
    public ArrayList<Raindrop> raindrops = new ArrayList<>();
    public boolean isRainModifier() {
        return rainModifier;
    }

    public boolean isPerfectStorm() {
        return blizzardModifier && fogModifier && rainModifier && !powerModifier && !timerModifier && !radiationModifier;
    }

    public NeonSogBall getNeonSogBall() {
        return neonSogBall;
    }

    public void setNeonSogBall(NeonSogBall neonSogBall) {
        this.neonSogBall = neonSogBall;
    }

    public Shadowblocker getShadowblocker() {
        return shadowblocker;
    }

    public void setShadowblocker(Shadowblocker shadowblocker) {
        this.shadowblocker = shadowblocker;
    }
    
    public int red40Phase = 0;
    
    
    float wetFloor = 0;
    public float getWetFloor() {
        return wetFloor;
    }
    public void setWetFloor(float wetFloor) {
        this.wetFloor = wetFloor;
    }

    public void setShadowPepito(ShadowPepito shadowPepito) {
        this.shadowPepito = shadowPepito;
    }

    public HashMap<Integer, Door> getDoors() {
        return doors;
    }
    public static float getDoorPan(Door d, GameType type) {
        int doorPos = (int) (d.getHitbox().getBounds().x + d.getHitbox().getBounds().width / 2F);
        float pan = (float) (Math.sin(1.57 * (doorPos / 740F - 1)) / 1.3F);
        
        if(type.isBasement()) {
            pan = (doorPos / 740F - 1) / 1.1F;
        }
        return pan;
    }
    
    public static void playTempAmbient(SoundMP3 sound) {
        int number = (int) (Math.random() * 3 + 1);
        sound.playRate("tempAmbient" + number, 0.1, 0.3F + Math.random() / 2.5F);
    }

    public int getId() {
        return id;
    }
}
