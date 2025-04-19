package game.enviornments;

import main.GamePanel;
import utils.RepeatingPepitimer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Basement extends Enviornment {
    int bgIndex = 3;
    int maxOffset = 400;
    Rectangle monitor = new Rectangle(559, 375, 78, 36);
    

    public int getBgIndex() {
        return bgIndex;
    }

    public int maxOffset() {
        return maxOffset;
    }

    public Rectangle getMonitor() {
        return monitor;
    }
    
    public List<Integer> blockedWalls = new ArrayList<>();

    boolean millyVisiting = false;
    float millyX = 1480;
    float millyGoalX = 1480;
    boolean millyLamp = false;
    
    public short millyGoalFlicker = 0;
    public float millyCurrentFlicker = 0;

    byte vent = 0; // 0 - closed, 1 - animation, 2 - out
    float ventProgress = -2.64F;

    float shake = 0;
    

    boolean endingEncounter = false;
    int redAlarmY = -100;
    
    byte generatorStage = 0;
    public short[] generatorXes = new short[] {-1, -1, -1};
    boolean inGeneratorMinigame = false;
    // idk how to name it in the game so it will probably be called generator in the code forever
    // 20.03.2025 upd: it'll now be called generator minigame everywhere forever LMFAOOO
    
    int coins = 0;
    
    public BufferedImage generatorMinigameMonitor = new BufferedImage(225, 115, BufferedImage.TYPE_INT_RGB);
    
    float charge = 0;
    float monitorHeight = 1;

    float connectProgress = 0;
    
    boolean doWiresWork = true;
    float overseerMove = -1;
    
    boolean showPsyop = false;
    boolean partiesPoster = false; // otherwise skubriks
    public RepeatingPepitimer rumbleSog = null;
    
    public boolean beenToHydrophobiaChamber = false;
    
    boolean sparking = false;
    public float sparkSize = 0.02F;
    
    int gasLeakMillis = 0;
    float gasLeakWobble = 0;
    float whiteScreen = 0;
    
    byte stage = -1;

    public Basement() {
        floorClip = GamePanel.getPolygon(List.of(new Point(1, 639), new Point(262, 554), new Point(262, 558), new Point(308, 558), new Point(323, 547),
                new Point(323, 534), new Point(463, 489), new Point(463, 491), new Point(489, 491), new Point(497, 484), new Point(533, 484),
                new Point(533, 496), new Point(544, 496), new Point(544, 484), new Point(687, 484), new Point(687, 496), new Point(699, 496),
                new Point(699, 484), new Point(885, 484), new Point(889, 492), new Point(916, 492), new Point(920, 484), new Point(936, 484),
                new Point(944, 490), new Point(968, 490), new Point(968, 486), new Point(1131, 537), new Point(1131, 546), new Point(1148, 557),
                new Point(1192, 557), new Point(1192, 553), new Point(1474, 639)));
        
        floorGeometry = GamePanel.getPolygon(List.of(new Point(0, 639), new Point(477, 484), new Point(963, 484), new Point(968, 486),
                new Point(1130, 537), new Point(1160, 543), new Point(1480, 639)));
    
        ceilGeometry = GamePanel.getPolygon(List.of(new Point(0, -87), new Point(473, 244), new Point(965, 244), new Point(1480, -97)));
        
        
        metalPipe = new Rectangle(610, 480, 144, 20);
        sensor = new Rectangle(447, 406, 107, 104);
        flashlight = new Rectangle(656, 412, 29, 19);
        miniSoda = new Rectangle(549, 459, 28, 36);
        planks = new Rectangle(1056, 446, 165, 120);
        freezePotion = new Rectangle(528, 391, 40, 40);
        starlightBottle = new Rectangle(715, 263, 58, 57);
        styroPipe = new Rectangle(919, 433, 68, 70);
//        weatherStation = new Rectangle(-17, 389, 213, 257);
        megaSoda = new Rectangle(614, 344, 67, 88);
        
        soup = new Rectangle(611, 381, 42, 50);
        mudseal = new Rectangle(533, 588, 104, 70);
        soda = new Rectangle(943, 526, 67, 105);

        maxwells = new Point(0, 0);
        fan = new Point(405, 478);

        generator = new Rectangle(889, 431, 98, 67);
        pipe = new Rectangle(-10, 556, 115, 74);
        boop = new Rectangle(645, 313, 14, 14);
    }
    

    public boolean isMillyVisiting() {
        return millyVisiting;
    }

    public float getMillyX() {
        return millyX;
    }

    public void setMillyX(float millyX) {
        this.millyX = millyX;
    }

    public void setMillyGoalX(float millyGoalX) {
        this.millyGoalX = millyGoalX;
    }

    public float getMillyGoalX() {
        return millyGoalX;
    }

    public void setMillyVisiting(boolean millyVisiting) {
        this.millyVisiting = millyVisiting;
    }

    public boolean isMillyLamp() {
        return millyLamp;
    }

    public void setMillyLamp(boolean millyLamp) {
        this.millyLamp = millyLamp;
    }
    

    public byte getVent() {
        return vent;
    }

    public void setVent(byte vent) {
        this.vent = vent;
    }

    public float getVentProgress() {
        return ventProgress;
    }

    public void setVentProgress(float ventProgress) {
        this.ventProgress = ventProgress;
    }

    public byte getStage() {
        return stage;
    }

    public void setStage(byte stage) {
        this.stage = stage;
        endingEncounter = stage == 4 || stage == 5;
    }

    public boolean isEndingEncounter() {
        return endingEncounter;
    }
    

    public int getRedAlarmY() {
        return redAlarmY;
    }

    public void setRedAlarmY(int redAlarmY) {
        this.redAlarmY = redAlarmY;
    }

    
    public byte getGeneratorStage() {
        return generatorStage;
    }

    public boolean isInGeneratorMinigame() {
        return inGeneratorMinigame;
    }

    public void setInGeneratorMinigame(boolean inGeneratorMinigame) {
        this.inGeneratorMinigame = inGeneratorMinigame;
    }

    public void setGeneratorStage(byte generatorStage) {
        this.generatorStage = generatorStage;
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

    public void addCoins(int add) {
        this.coins += add;
    }

    public int getCoins() {
        return coins;
    }

    public void setMonitorHeight(float monitorHeight) {
        this.monitorHeight = monitorHeight;
    }

    public float getMonitorHeight() {
        return monitorHeight;
    }

    public void setCharge(float charge) {
        this.charge = charge;
    }

    public float getCharge() {
        return charge;
    }

    public float getShake() {
        return shake;
    }

    public void setShake(float shake) {
        this.shake = shake;
    }

    public float getConnectProgress() {
        return connectProgress;
    }

    public void setConnectProgress(float connectProgress) {
        this.connectProgress = connectProgress;
    }

    public boolean isShowPsyop() {
        return showPsyop;
    }

    public void setShowPsyop(boolean showPsyop) {
        this.showPsyop = showPsyop;
    }

    public void setPartiesPoster(boolean partiesPoster) {
        this.partiesPoster = partiesPoster;
    }

    public boolean isPartiesPoster() {
        return partiesPoster;
    }

    public boolean doWiresWork() {
        return doWiresWork;
    }

    public void setDoWiresWork(boolean doWiresWork) {
        this.doWiresWork = doWiresWork;
    }
    
    public void spark() {
        sparkSize = 0.02F;
        sparking = true;
    }

    public boolean isSparking() {
        return sparking;
    }
    public void setSparking(boolean sparking) {
        this.sparking = sparking;
    }

    public void setGasLeakMillis(int gasLeakMillis) {
        this.gasLeakMillis = gasLeakMillis;
    }
    public int getGasLeakMillis() {
        return gasLeakMillis;
    }

    public float getGasLeakWobble() {
        return gasLeakWobble;
    }
    public void setGasLeakWobble(float gasLeakWobble) {
        this.gasLeakWobble = gasLeakWobble;
    }

    public void setWhiteScreen(float whiteScreen) {
        this.whiteScreen = whiteScreen;
    }
    public float getWhiteScreen() {
        return whiteScreen;
    }

    
    public void setOverseerMove(float overseerMove) {
        this.overseerMove = overseerMove;
    }
    public float getOverseerMove() {
        return overseerMove;
    }
}
