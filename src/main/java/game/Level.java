package game;

import enemies.*;
import game.achievements.AchievementHandler;
import game.achievements.Achievements;
import game.bingo.BingoHandler;
import game.bingo.BingoTask;
import game.custom.CustomNight;
import game.custom.CustomNightEnemy;
import game.custom.CustomNightModifier;
import game.shadownight.AstartaBoss;
import game.shadownight.ShadowPepito;
import main.GamePanel;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import utils.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Level {
    public byte night = 1;

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

    public float getEnergy() {
        return energy;
    }
    public void addEnergy(Float f) {
        energy += f;
    }
    public void setEnergy(Float f) {
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

        List<Point> poly1 = List.of(new Point(475, 497), new Point(628, 497), new Point(628, 617), new Point(475, 617));
        doors.put(0, new Door(new Point(420, 500), g.door1Img, new Point(475, 497), GamePanel.getPolygon(poly1), new Point(500, 560)));

        int x1 = 1269;
        int x2 = 1451;
        int[] xArray = new int[] {x1, x2, x2, x1};
        int[] yArray = new int[] {269, 264, 635, 620};

        doors.put(1, new Door(new Point(1160, 280), g.door2Img, new Point(1262, 264), new Polygon(xArray, yArray, 4), new Point(1300, 487)));

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

        frog = new Frog(g);

        sortTime();
    }

    public int afk = 15;

    AstartaBoss astartaBoss;
    ShadowPepito shadowPepito;

    RepeatingPepitimer shirtfart;

    public short usedMetalPipes = 0;


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
                g.nightAnnounceText = "perfect storm";
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

        System.out.println("shirtfartings arise...");

        shirtfart = new RepeatingPepitimer(() -> {
            if(g.state != GameState.GAME)
                return;
            if(reachedHalfway || event == GameEvent.ASTARTA)
                return;

            if (event.isInGame()) {
                float hPercent = getTemperature() / 100F;
                float ePercent = (1 - (energy / maxEnergy)) / 4 * 3 + 0.25F;
                float tPercent = (float) ((seconds - secondsAtStart) / (type.getDuration() + Math.abs(secondsAtStart)));
                float vPercent = Math.min(1, eventPercent);

                float average = (hPercent + ePercent + tPercent + vPercent) / 4;
                float eventHeavy = (average + eventPercent * 3) / 4;

                shirtfart.setDelay((int) (12000 - 9000 * average));
                shirtfart.setMiliseconds((int) (12000 - 9000 * average));

                if(average > 0.6F && eventPercent > 1.2F)
                    return;

                float rate = (Math.round(Math.random() * 10F)) / (20F - 15F * eventHeavy) + 0.2F + Math.round(6F * average) / 10F;
                float volume = 0.25F * average + 0.08F;

                if(volume < 0.13F)
                    return;

                g.sound.playRate("shirtfart", volume, rate);
            }
        }, 5000, 12000);

        g.every6s.put("game", () -> {
            if(g.state != GameState.GAME)
                return;
            if(event == GameEvent.ASTARTA) {
                g.pixelation = Math.max(1, g.pixelation - 0.5F);

                if(astartaBoss.getDyingStage() > 0) {
                    return;
                }
            }

            g.pixelation = Math.max(1, g.pixelation - 0.1F);

            g.sound.play("literally_22_miliseconds_of_nothing", 0.1);

            if (event.isInGame()) {
                int random = (int) (Math.round(Math.random() * 20));
                switch (random) {
                    case 0 -> g.sound.play("a90Alert", 0.03, Math.random() / 3 - 0.33);
                    case 1 -> g.sound.play("randomScare", 0.04, Math.random() / 3 - 0.33);
                    case 4 -> g.sound.play("cat_sounds", 0.1, Math.random() / 3 - 0.33);
                    case 5 -> g.sound.play("fakeWalk", 0.03, Math.random() * 2 - 1);
                }
                if (g.sensor.isEnabled() && !power) {
                    g.console.add("");
                }
            }

            GamePanel.balloons.forEach(Balloon::changeDirection);
        });

        g.everySecond.put("game", () -> {
            if(g.state != GameState.GAME)
                return;
            afk--;

            if(energy < 150) {
                if (Math.random() < 0.015) {
                    flicker = 100;
                    g.sound.play("flicker", 0.05);
                }
            }
            if(powerModifier && energy > 0) {
                if (power) {
                    if(Math.random() < 0.02 && !elAstarta.isActive()) {
                        g.powerDown();
                    }
                } else {
                    if(Math.random() < 0.05) {
                        g.lightsOn();
                    }
                }
            }
            if(blizzardModifier) {
                if(Math.random() < 0.01 && blizzardTime <= 0) {
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

            if(event != GameEvent.MAXWELL) {
                if(event.canTimeTick()) {
                    seconds++;
                    updateClockString();
                    if (power) {
                        g.generateAdblocker();
                    }
                }

                if (type == GameType.CLASSIC || type.isParty()) {
                    if (checkForCompletion()) {
                        g.win();
                    }
                } else switch (type) {
                    case CUSTOM -> {
                        if (checkForCompletion() && !startedCustomWin) {
                            g.winSequence();
                            startedCustomWin = true;
                        }
                    }
                    case ENDLESS_NIGHT -> {
                        if (checkForCompletion()) {
                            g.type = GameType.DAY;
                            g.endless.addCoins(g.endless.getNight() * 100);
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
                }

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
                    if(type != GameType.DAY) {
                        if (event.canSpawnEntities()) {
                            pepito.tick();
                            msi.tick(type == GameType.SHADOW);
                            colaCat.tick();
                            astarta.tick();
                            maki.tick();
                            lemonadeCat.tick();
                            mirrorCat.tick();
                            wires.tick();
                            boykisser.tick();
                            scaryCat.tick();
                            elAstarta.tick();

                            if(jumpscareCat.isEnabled()) {
                                if(afk <= jumpscareCat.getAILevel()) {
                                    int millis = ((int) (Math.random() * 7 + 3)) * 1000;
                                    new Pepitimer(jumpscareCat::spawn, millis);
                                    afk = 16;
                                }
                            }
                        }
                        if (!g.adblocker.isEnabled() && !g.adBlocked) {
                            if (g.inCam) {
                                glitcher.increaseCounter();
                            } else {
                                glitcher.decreaseCounter();
                            }
                        }

                        shark.tick(); // checks for canspawnentities
                    }
                }

                if(!g.adblocker.isEnabled() && !g.adBlocked) {
                    a90.tick();
                }

                if (event == GameEvent.FLOOD) {
                    if (g.waterSpeed < (3 + shark.getAILevel() * 2)) {
                        g.waterSpeed++;
                    }
                    if (g.fanActive) {
                        shark.floodDuration--;
                    }
                }
            }

            if(event.isUsingSensor()) {
                if(g.sensor.isEnabled()) {
                    if(!g.console.isRatting && a90.arrivalSeconds < 2) {
                        int random = (int) Math.round(Math.random() * 2);
                        if (random == 0) {
                            g.console.add("a90 appearing soon");
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

            if(g.fan.isEnabled()) {
                if (g.fanActive) {
                    energyToll += 0.08F;

                    temperature = Math.min(Math.max(0, temperature - 0.06F), 100);

                    if (pepito.notPepitoChance > 8) {
                        pepito.notPepitoChance = g.mathRound(pepito.notPepitoChance - 0.01F);
                        if (pepito.notPepitoChance < 8.01 && g.sensor.isEnabled()) {
                            g.console.add("turn off your fan");
                        }
                    }
                } else if(isTemperatureEnabled) { // if fan isn't active
                    temperature = Math.min(Math.max(0, temperature + 0.03F), 100);
                }
            } else if(isTemperatureEnabled) { // if fan isn't enabled
                temperature = Math.min(Math.max(0, temperature + 0.015F), 100);
            }

            if(!noSignalFromHeat && (Math.random() * 0.9) > heatDistort()) {
                noSignalFromHeat = true;
                new Pepitimer(() -> noSignalFromHeat = false, 300);
            }

            if(g.usage >= 4) {
                BingoHandler.completeTask(BingoTask.FOUR_USAGE_BARS);
            }

            if(event == GameEvent.NONE || event == GameEvent.ASTARTA || event == GameEvent.EL_ASTARTA) {
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
        });

        switch (type) {
            case SHADOW -> {
                g.music.play("theShadow", 0.1);
                energy = 1003;
            }
            case PREPARTY -> {
                for(int i = 0; i < 5; i++){
                    GamePanel.balloons.add(new Balloon());
                }
            }
            case PARTY -> {
                g.sound.play("explode", 0.5);
                energy = 528;
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
    }

    boolean startedCustomWin = false;

    public float randomSogAlpha = 0;

    HouseLocation location = HouseLocation.OFFICE;

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
    }

    float eventPercent = 0;
    public void addEventPercent(float f) {
        eventPercent += f;
    }
    public float getEventPercent() {
        return eventPercent;
    }

    private void sortAILevels(Corn[] corns) {
        if(type == GameType.CUSTOM) {
            for(CustomNightEnemy enemy : CustomNight.getEnemies()) {
                int enemyAI = enemy.getAI();
                if(isPerfectStorm()) {
                    enemyAI = 7;
                }

                switch (enemy.getId()) {
                    case 0 -> pepito.setPepitoAI((byte) enemyAI);
                    case 1 -> pepito.setNotPepitoAI((byte) enemyAI);
                    case 2 -> glitcher.setAILevel(enemyAI);
                    case 3 -> a90.setAILevel(enemyAI);
                    case 4 -> msi.setAILevel(enemyAI);
                    case 5 -> astarta.setAILevel(enemyAI);
                    case 6 -> shark.setAILevel(enemyAI);
                    case 7 -> boykisser.setAILevel(enemyAI);
                    case 8 -> colaCat.setAILevel(enemyAI);
                    case 9 -> mirrorCat.setAILevel(enemyAI);
                    case 10 -> maki.setAILevel(enemyAI);
                    case 11 -> lemonadeCat.setAILevel(enemyAI);
                    case 12 -> wires.setAILevel(enemyAI);
                    case 13 -> scaryCat.setAILevel(enemyAI);
                    case 14 -> jumpscareCat.setAILevel(enemyAI);
                    case 15 -> elAstarta.setAILevel(enemyAI);
                }
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

        if(g.soda.isEnabled() && (g.flashlight.isEnabled() || g.metalPipe.isEnabled())) {
            colaCat.setAILevel(generateAILevel(night, 1, 0, corns));
        }

        astarta.setAILevel(generateAILevel(night, 2, 0, corns));
        jumpscareCat.setAILevel(generateAILevel(night, 1, 0));

        if(g.sensor.isEnabled() || night == 18) {
            if(night == 18) {
                msi.isShadow = true;
                msi.arrivalSeconds = 18;
            }

            msi.setAILevel(generateAILevel(night, 2, 0));
        }

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
                shark.setAILevel(6);
                boykisser.setAILevel(5);
                colaCat.setAILevel(4);
                mirrorCat.setAILevel(4);
            }
            case PREPARTY -> {
                shark.setAILevel(4);
                boykisser.setAILevel(4);
                glitcher.setAILevel(4);
                a90.setAILevel(4);
                colaCat.setAILevel(4);
            }
            case PARTY -> {
                pepito.setPepitoAI((byte) 7);
                pepito.setNotPepitoAI((byte) 7);
                glitcher.setAILevel(8);
                a90.setAILevel(6);
                astarta.setAILevel(6);
                msi.setAILevel(6);
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
        }
    }

    private void spawnManuals(byte night, GameType type) {
        if(type == GameType.ENDLESS_NIGHT && g.showManual) {
            switch (night) {
                case 2 -> g.manualBetterSpawn(GamePanel.getString("manualMSIAstarta"));
                case 3 -> g.manualBetterSpawn(GamePanel.getString("manualShark"));
                case 4 -> g.manualBetterSpawn(GamePanel.getString("manualBoykisser"));
                case 5 -> g.manualBetterSpawn(GamePanel.getString("manualMakiLemonade"));
                case 6 -> g.manualBetterSpawn(GamePanel.getString("manualZazu"));
                case 7 -> g.manualBetterSpawn("");
                case 8 -> g.manualBetterSpawn("LEFT. RIGHT. LEFT. RIGHT. LEFT. RIGHT. LEFT. RIGHT. LEFT. RIGHT. LEFT. RIGHT. LEFT. RIGHT. LEFT. RIGHT. LEFT. RIGHT. LEFT. RIGHT. LEFT. RIGHT. LEFT. RIGHT");
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
    }

    public Enemy clearUnusedEntities(Enemy enemy) {
        if(enemy.getAILevel() > 0) {
            return enemy;
        }
        return null;
    }

    public String generateClock() {
        String ampm;
        int newHour = (int) ((seconds + 1) / (duration / 6.0));

        if (newHour > 0) {
            ampm = newHour + " AM";
        } else if(newHour == 0 && seconds >= 0) {
            ampm = "12 AM";
        } else {
            ampm = 11 + newHour + " PM";
        }
        if(!ampm.contains("10")) {
            ampm = ampm.replaceAll("0", "12");
        }
        return ampm;
    }

    public void updateClockString() {
        clockString = generateClock();
    }
    boolean completed = false;

    private boolean checkForCompletion() {
        if(completed)
            return false;

        if(type == GameType.DAY) {
            completed = seconds >= -60;
            return seconds >= -60;
        }
        completed = seconds >= duration - 1;
        return seconds >= duration - 1;
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


    // shadownight ONLY
    private boolean checkIfPassedHalfway() {
        return seconds >= 100;
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
        shark.setAILevel(8);
        boykisser.setAILevel(8);
        colaCat.setAILevel(0);
        mirrorCat.setAILevel(7);
        lemonadeCat.setAILevel(3); // 1
//        a120.setAILevel(1); // 1
        wires.setAILevel(5); // 2
        scaryCat.setAILevel(3); // 2

        energy += 500;
        maxEnergy += 500;

        g.announceNight((byte) 60, GameType.SHADOW);
        g.nightAnnounceText = GamePanel.getString("halfwayPointCaps");
        if(g.shadowCheckpointUsed != 2) {
            new Pepitimer(() -> g.announceChallenger((byte) 60, 0), 8000);
            new Pepitimer(() -> g.announceChallenger((byte) 61, 0), 13000);
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



    private float generatorEnergy = -1;
    public byte generatorStage = 0;
    public short[] generatorXes = new short[] {-1, -1, -1};
    public boolean inGeneratorMinigame = false;
    boolean powerModifier = false;
    public boolean isPowerModifier() {
        return powerModifier;
    }
    public boolean isInGeneratorMinigame() {
        return inGeneratorMinigame;
    }

    public void startGeneratorMinigame() {
        generatorStage = 0;
        generatorXes[0] = (short) (30 + Math.random() * 560);
        generatorXes[1] = (short) (30 + Math.random() * 560);
        generatorXes[2] = (short) (30 + Math.random() * 560);
        inGeneratorMinigame = true;

        g.generatorSound.play("connectToGenerator", 0.1, true);
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

    boolean rainModifier = false;
    public ArrayList<Raindrop> raindrops = new ArrayList<>();
    public boolean isRainModifier() {
        return rainModifier;
    }

    public boolean isPerfectStorm() {
        return blizzardModifier && fogModifier && rainModifier && !powerModifier && !timerModifier && !radiationModifier;
    }


    public void setShadowPepito(ShadowPepito shadowPepito) {
        this.shadowPepito = shadowPepito;
    }

    public HashMap<Integer, Door> getDoors() {
        return doors;
    }
}
