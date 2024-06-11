package main;

import cutscenes.Cutscene;
import cutscenes.CutsceneObject;
import game.Balloon;
import game.Item;
import game.Platformer;
import game.achievements.AchievementHandler;
import game.achievements.Achievements;
import game.bingo.BingoHandler;
import game.bingo.BingoTask;
import game.custom.CustomNight;
import game.shadownight.Mister;
import utils.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Console {
    private static boolean isOn = false;
    private static List<String> lines = new ArrayList<>();
    static String currentlyTyping = "";
    private static GamePanel g;

    static void initialize(GamePanel panel) {
        g = panel;

        normalCommands = List.of("energy", "msi", "pepito", "notPepito", "mirror", "mk", "bloom", "a90", "astarta", "elastarta", "blizzard", "jmpcat", "roulette", "uncannyEventSec", "dvdEventSec", "holeEventSec", "mister", "astartaSpeed", "setABhealth", "lemon", "scarycat", "a120", "wires", "shadow", "shart", "makeballoons", "jumpscare", "bright", "starlight", "nightSeconds", "setEndlessNight", "locker", "sunglasses", "temp", "maki", "lag", "notify", "skipAB", "event", "kys", "win");
        opCommands = List.of("debug", "itemLimit", "item", "ie", "volume", "timers", "countfps", "limbo", "infinitemoneyglitch", "reset", "state", "freesoda", "godmode", "getAch", "completePepingo", "remAch", "portal", "rift", "sigma", "testCutscene", "plat", "endlessCheck", "eventCheck", "help", "prikol");;
    }

    static void type(String sequence) {
        currentlyTyping += sequence;
        getPossibleCommand();
    }
    static String possibleCommand = "";
    private static void getPossibleCommand() {
        possibleCommand = "";
        if(currentlyTyping.isEmpty())
            return;

        List<String> possibleCommands = new ArrayList<>();

        for(String str : normalCommands) {
            if(str.startsWith(currentlyTyping)) {
                possibleCommands.add(str);
            }
        }
        for(String str : opCommands) {
            if(str.startsWith(currentlyTyping)) {
                possibleCommands.add(str);
            }
        }

        if(!possibleCommands.isEmpty()) {
            possibleCommand = possibleCommands.get(0);
        }
    }
    static void autoFill() {
        currentlyTyping = possibleCommand;
        getPossibleCommand();
    }

    static void removeLast() {
        if(!currentlyTyping.isEmpty()) {
            currentlyTyping = currentlyTyping.substring(0, currentlyTyping.length() - 1);
        }
        getPossibleCommand();
    }

    static void enter(boolean isOp) {
        ArrayList<String> returnValues = new ArrayList<>();
        String[] args = currentlyTyping.split(" ");

        boolean commandSent = sendCommand(args[0], args);

        if(!isOp && !commandSent) {
            possibleCommand = "";
            currentlyTyping = "";
            return;

        } else if(isOp) {
            switch (args[0]) {
                case "debug" -> {
                    g.debugMode = true;
                }
                case "itemLimit" -> {
                    g.itemLimit = Integer.parseInt(args[1]);
                }
                case "item" -> {
                    String id = args[1];
                    int amount = Integer.parseInt(args[2]);
                    boolean failed = true;

                    for(Item item : g.fullItemList) {
                        if(item.getId().equals(id)) {
                            item.setAmount(amount);
                            failed = false;

                            g.updateItemList();
                            g.redrawItemsMenu();
                        }
                    }
                    if(failed) {
                        returnValues.add("-failed");
                    }
                }
                case "ie" -> {
                    String id = args[1];
                    boolean failed = true;

                    for(Item item : g.fullItemList) {
                        if(item.getId().equals(id)) {
                            item.enable();
                            failed = false;
                        }
                    }
                    if(failed) {
                        returnValues.add("-failed");
                    }
                }
                case "volume" -> {
                    g.volume = Float.parseFloat(args[1]);
                }
                case "timers" -> {
                    g.countersAlive = new boolean[] {false, false, false, false};
                    returnValues.add("-waiting for response (7s)");

                    new Pepitimer(() -> {
                        lines.add("-6s: " + (g.countersAlive[0] ? "ALIVE" : "DEAD"));
                        lines.add("-1s: " + (g.countersAlive[1] ? "ALIVE" : "DEAD"));
                        lines.add("-10th: " + (g.countersAlive[2] ? "ALIVE" : "DEAD"));
                        lines.add("-20th: " + (g.countersAlive[3] ? "ALIVE" : "DEAD"));
                    }, (int) (7000 / GamePanel.universalGameSpeedModifier));
                }
                case "countfps" -> {
                    ArrayList<Integer> fpsValues = new ArrayList<>();
                    returnValues.add("-waiting for response (10s)");

                    int[] times = new int[] {0};
                    RepeatingPepitimer[] timer = new RepeatingPepitimer[1];
                    timer[0] = new RepeatingPepitimer(() -> {
                        times[0]++;
                        fpsValues.add(g.fpscnt.get());

                        if(times[0] > 9) {
                            timer[0].cancel(false);
                            Collections.sort(fpsValues);
                            lines.add("-min: " + fpsValues.get(0));
                            lines.add("-max: " + fpsValues.get(9));
                            int average = 0;
                            for(Integer number : fpsValues) {
                                average += number;
                            }
                            average /= 10;
                            lines.add("-average: " + average);
                            lines.add("-median: " + (fpsValues.get(4) + fpsValues.get(5)) / 2);
                        }
                    }, (int) (1000 / GamePanel.universalGameSpeedModifier), (int) (1000 / GamePanel.universalGameSpeedModifier));
                }
                case "limbo" -> {
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
                case "infinitemoneyglitch" -> {
                    g.keyHandler.infiniteMoneyGlitch = true;
                }
                case "bloom" -> {
                    g.bloom = !g.bloom;
                }
                case "reset" -> {
                    lines.clear();
                }
                case "state" -> {
                    g.state = GameState.valueOf(args[1].toUpperCase(Locale.ROOT));
                }
                case "freesoda" -> {
                    new RepeatingPepitimer(() -> {
                        g.soda.enable();
                    }, 1, 1);
                }
                case "godmode" -> {
                    g.invincible = !g.invincible;
                }
                case "completePepingo" -> {
                    for(BingoTask task : g.bingoCard.getTasks()) {
                        BingoHandler.completeTask(task);
                    }
                }
                case "getAch" -> {
                    for(String string : args) {
                        if(!string.equals("getAch")) {
                            try {
                                AchievementHandler.obtain(g, Achievements.valueOf(string));
                            } catch (Exception ignored) {
                                if(string.equals("all")) {
                                    for(Achievements achievement : Achievements.values()) {
                                        AchievementHandler.obtain(g, achievement);
                                    }
                                    AchievementHandler.updateAchievementPercentage(g);
                                } else {
                                    returnValues.add("-no such achievement ID");
                                }
                            }
                        }
                    }
                }
                case "remAch" -> {
                    for(String string : args) {
                        if(!string.equals("remAch")) {
                            try {
                                Achievements.valueOf(string).unobtain();

                                AchievementHandler.updateAchievementPercentage(g);
                            } catch (Exception ignored) {
                                if(string.equals("all")) {
                                    for(Achievements achievement : Achievements.values()) {
                                        achievement.unobtain();
                                    }
                                    AchievementHandler.updateAchievementPercentage(g);
                                } else {
                                    returnValues.add("-no such achievement ID");
                                }
                            }
                        }
                    }
                }
                case "portal" -> {
                    g.portalActive = true;
                    g.keyHandler.camSounds.play("shadowPortal", 0.1, true);
                }
                case "rift" -> {
                    g.enterRift();
                }
                case "winbingo" -> {
                    g.bingoCard.complete();
                }
                case "sigma" -> {
                    g.portalTransporting = true;

                    g.everySecond20th.put("riftTint", () -> {
                        if(g.riftTint < 251) {
                            g.riftTint += 4;
                        } else {
                            g.everySecond20th.remove("riftTint");
                        }
                    });

                    g.loading = true;
                    g.state = GameState.UNLOADED;
                    g.camOut(true);

                    GamePanel.mirror = true;
                    g.type = GameType.SHADOW;
                    g.fadeOutStatic(0, 0, 0);
                    g.state = GameState.HALFLOADED;
                    g.soggyBallpit.disable();
                    g.soggyBallpitActive = false;

                    g.startGame();

                    g.endless = null;
                    g.portalTransporting = false;
                    g.riftTint = 0;
                    g.portalActive = false;
                }
                case "testCutscene" -> {
                    Cutscene cutscene = new Cutscene("test", 1080, 640, BufferedImage.TYPE_INT_RGB);

                    CutsceneObject sigma = new CutsceneObject(50, 50, 100, 100, "/game/rift/frame.png").addScene(0);
                    sigma.setRecalculationStrat(() -> {
                        sigma.x = (int) (Math.cos(cutscene.getMilliseconds() / 320.0) * 450 + 500);
                        sigma.y = (int) (Math.sin(cutscene.getMilliseconds() / 320.0) * 250 + 280);
                    });
                    CutsceneObject alpha = new CutsceneObject(50, 50, 200, 100, "/game/cam/no_signal.png").addScene(0);
                    alpha.setRecalculationStrat(() -> {
                        alpha.x = (int) (Math.sin(cutscene.getMilliseconds() / 320.0) * 450 + 500);
                        alpha.y = (int) (Math.cos(cutscene.getMilliseconds() / 320.0) * 250 + 280);
                    });

                    cutscene.addObject(sigma);
                    cutscene.addObject(alpha);
                    cutscene.recognizeObjects();
                    g.currentCutscene = cutscene;

                    g.state = GameState.CUTSCENE;
                }
                case "plat" -> {
                    g.platformer = new Platformer(g);

                    g.state = GameState.PLATFORMER;
                }
                case "endlessCheck" -> {
                    returnValues.add("-endless night: " + g.endless.getNight());
                }
                case "eventCheck" -> {
                    returnValues.add("-event: " + g.getNight().getEvent());
                }
                case "help" -> {
                    returnValues.add("-THERE IS NO HELP");
                }
                case "prikol" -> {
                    g.sound.play("untitled", 0.2);
                    final float[] rate = {0.9F};

                    new RepeatingPepitimer(() -> {
                        g.sound.playRate("untitled", 0.2, rate[0]);
                        rate[0] -= 0.1F;
                    }, 60000, 60000);
                }
                default -> {
                    if(!commandSent) {
                        possibleCommand = "";
                        currentlyTyping = "";
                        return;
                    }
                }
            }
        }

        lines.add(currentlyTyping + "!");
        if(!returnValues.isEmpty()) {
            lines.addAll(returnValues);
        }
        currentlyTyping = "";
        possibleCommand = "";

        if(lines.size() > 7) {
            lines = lines.subList(1 + returnValues.size(), lines.size());
        }
    }

    static boolean sendCommand(String command, String[] args) {
        switch (command) {
            case "energy" -> {
                g.getNight().setEnergy(Float.valueOf(args[1]));
            }
            case "msi" -> {
                if(g.getNight().getMSI().getAILevel() <= 0) {
                    g.getNight().getMSI().setAILevel(1);
                }
                g.getNight().getMSI().spawn();
            }
            case "mirror" -> {
                if(g.getNight().getMirrorCat().getAILevel() <= 0) {
                    g.getNight().getMirrorCat().setAILevel(1);
                }
                g.getNight().getMirrorCat().spawn();
            }
            case "mk" -> {
                g.getNight().getMirrorCat().kill();
            }
            case "a90" -> {
                g.getNight().getA90().spawn();
            }
            case "astarta" -> {
                g.getNight().getAstarta().spawn();
            }
            case "elastarta" -> {
                g.getNight().getElAstarta().spawn();
            }
            case "blizzard" -> {
                g.getNight().startBlizzard();
            }
            case "jmpcat" -> {
                g.getNight().getJumpscareCat().spawn();
            }
            case "setABhealth" -> {
                g.getNight().getAstartaBoss().setHealth(Float.parseFloat(args[1]));
            }
            case "uncannyEventSec" -> {
                g.getNight().getAstartaBoss().uncannyEventSeconds = Byte.parseByte(args[1]);
            }
            case "roulette" -> {
                g.getNight().getAstartaBoss().rouletteTimer = 1;
            }
            case "dvdEventSec" -> {
                g.getNight().getAstartaBoss().dvdEventSeconds = Byte.parseByte(args[1]);
            }
            case "holeEventSec" -> {
                g.getNight().getAstartaBoss().holeEventSeconds = Byte.parseByte(args[1]);
            }
            case "mister" -> {
                Mister mister = g.getNight().getAstartaBoss().getMister();
                mister.spawn();
            }
            case "lemon" -> {
                g.getNight().getLemonadeCat().spawn();
            }
            case "scarycat" -> {
                try {
                    g.getNight().getScaryCat().setAILevel(Integer.parseInt(args[1]));
                } catch (ArrayIndexOutOfBoundsException e) {
                    if (g.getNight().getScaryCat().getAILevel() <= 0) {
                        g.getNight().getScaryCat().setAILevel(1);
                    }
                }
                g.getNight().getScaryCat().spawn();
            }
            case "a120" -> {
                g.getNight().getA120().spawn();
            }
            case "wires" -> {
                g.getNight().getWires().setAILevel(8);
                g.getNight().getWires().spawn();
            }
            case "shadow" -> {
                g.getNight().getMSI().isShadow = true;
            }
            case "shart" -> {
                if(g.getNight().getShark().getAILevel() <= 0) {
                    g.getNight().getShark().setAILevel(1);
                }
                g.getNight().getShark().floodStartSeconds = 2;
            }
            case "makeballoons" -> {
                int balloonAmount = Integer.parseInt(args[1]);

                for(int i = 0; i < balloonAmount; i++){
                    GamePanel.balloons.add(new Balloon());
                }
            }
            case "jumpscare" -> {
                g.jumpscare(args[1]);
            }
            case "bright" -> {
                int brightness = Integer.parseInt(args[1]);
                g.fadeOut(brightness, brightness, brightness);
            }
            case "starlight" -> {
                g.starlightMillis = Integer.parseInt(args[1]);
            }
            case "nightSeconds" -> {
                g.getNight().seconds = Short.parseShort(args[1]);
            }
            case "setEndlessNight" -> {
                g.endless.setNight(Byte.parseByte(args[1]));
            }
            case "locker" -> {
                g.inLocker = !g.inLocker;
            }
            case "sunglasses" -> {
                g.sunglassesOn = !g.sunglassesOn;
            }
            case "temp" -> {
                g.getNight().setTemperature(Float.parseFloat(args[1]));
            }
            case "maki" -> {
                g.getNight().getMaki().setAILevel(1);
                g.getNight().getMaki().secondsUntilMaki = 2;
            }
            case "pepito" -> {
                g.getNight().getPepito().setPepitoAI((byte) (1));
                g.getNight().getPepito().seconds = 2;
            }
            case "notPepito" -> {
                g.getNight().getPepito().isNotPepito = true;
                g.getNight().getPepito().setNotPepitoAI((byte) (1));
                g.getNight().getPepito().seconds = 2;
            }
            case "lag" -> {
                for(int i = 0; i < 10000000; i++) {
                    double j = Math.sin(i) + Math.cos(i) + Math.tan(i);
                    double k = j / 100000000 + Math.sin(j) + Math.cos(j) + Math.tan(j);
                }
            }
            case "notify" -> {
                new Notification(args[1]);
            }
            case "skipAB" -> {
                g.getNight().getAstartaBoss().skipStartCutscene();
            }
            case "event" -> {
                g.getNight().setEvent(GameEvent.valueOf(args[1].toUpperCase(Locale.ROOT)));
            }
            case "kys" -> {
                g.jumpscare("pepito");
            }
            case "win" -> {
                g.winSequence();
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    static List<String> normalCommands = new ArrayList<>();
    static List<String> opCommands = new ArrayList<>();

    static void toggle() {
        isOn = !isOn;
    }

    static boolean isOn() {
        return isOn;
    }
    static List<String> getLines() {
        List<String> newLines = new ArrayList<>(lines);
        newLines.add(currentlyTyping);
        return newLines;
    }
}
