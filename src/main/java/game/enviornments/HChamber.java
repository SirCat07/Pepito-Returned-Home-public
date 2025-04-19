package game.enviornments;

import game.Level;
import game.particles.CoffeeParticle;
import main.GamePanel;
import utils.Pepitimer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class HChamber extends Enviornment {
    int bgIndex = 5;
    int maxOffset = 400;
    Rectangle monitor = new Rectangle(1480, 640, 1, 1);

    public int getBgIndex() {
        return bgIndex;
    }

    public int maxOffset() {
        return maxOffset;
    }

    public Rectangle getMonitor() {
        return monitor;
    }
    


    public Rectangle conditioner;
    public Rectangle locker;
    public Rectangle timer;
    public Rectangle compass;
    public Rectangle table;
    public Rectangle exit;
    
    public Rectangle key = new Rectangle(2000, 1000, 1, 1);
    public Rectangle cup = new Rectangle(2000, 1000, 1, 1);

    
    boolean hasConditioner;
    boolean hasLocker;
    
    
    public BufferedImage timerText;
    int wobbleFade = 120;
    boolean timerBroken = false;
    
    int untilDoor = 30;
    float barrierRotation = 0;
    
    int goalCompassRotation = 0;
    int compassRotation = 0;
    
    public int cameraGuidelineAlpha = 600;
    public int lockerGuidelineAlpha = 540;
    
    int rooms = 0;


    float shake = 0;
    
    
    
    boolean hoveringLocker = false;
    boolean hoveringCompass = false;
    boolean hoveringExit = false;
    boolean hoveringConditioner = false;
    boolean hoveringPen = false;
    boolean hoveringReinforced = false;
    boolean hoveringKey = false;
    boolean hoveringCup = false;
    
    boolean showCompassHint = false;
    
    Level oldNight;
    
    public float daZoom = 0;
    int deaths = 0;
    public boolean showDeathOptions = false;
    public boolean allowDeathButtons = false;
    public byte selectedDeathOption = 1;
    
    boolean respawnCheckpoint = false;
    public int checkpoint = 0;
    public boolean displayFieldDeathScreen = false;
    

    int[] randomPipeX = {1480, 1480};
    boolean conditionerMirrored = false;

    public HChamber() {
        floorGeometry = GamePanel.getPolygon(List.of(new Point(0, 635), new Point(217, 617), new Point(1267, 612), new Point(1480, 633)));
        ceilGeometry = GamePanel.getPolygon(List.of(new Point(0, 5), new Point(213, 48), new Point(1269, 52), new Point(1480, 5)));
        
        metalPipe = new Rectangle(1480, 640, 1, 1);
        sensor = new Rectangle(1480, 640, 1, 1);
        flashlight = new Rectangle(1480, 640, 1, 1);
        miniSoda = new Rectangle(1480, 640, 1, 1);
        planks = new Rectangle(1480, 640, 1, 1);
        freezePotion = new Rectangle(1480, 640, 1, 1);
        starlightBottle = new Rectangle(1480, 640, 1, 1);
        styroPipe = new Rectangle(1480, 640, 1, 1);

        soup = new Rectangle(1480, 640, 1, 1);
        mudseal = new Rectangle(1480, 640, 1, 1);
        soda = new Rectangle(1480, 640, 1, 1);
        megaSoda = new Rectangle(2000, 640, 1, 1);

        maxwells = new Point(1480, 640);
        fan = new Point(1480, 640);

        generator = new Rectangle(1480, 640, 1, 1);
        pipe = new Rectangle(1480, 640, 1, 1);
        boop = new Rectangle(1480, 640, 1, 1);

        conditioner = new Rectangle(244, 67, 325, 195);
        locker = new Rectangle(263, 317, 206, 318);
        timer = new Rectangle(606, 94, 241, 123);
        compass = new Rectangle(905, 305, 120, 120);
        table = new Rectangle(767, 455, 459, 175);
        exit = new Rectangle(557, 326, 196, 297);

        hasConditioner = true;
        hasLocker = true;
    }
    
    public boolean hasConditioner() {
        return hasConditioner;
    }
    public boolean hasLocker() {
        return hasLocker;
    }

    
    int seed = 0;
    
    public void regenerateSeed(boolean requiresLocker) {
        seed = (int) (Math.random() * (requiresLocker ? 6 : 7));
        
        if(rooms > 2 && !requiresLocker) {
            if((int) (Math.random() * 12) == 0) {
                seed = 30;
            } else if((int) (Math.random() * 14) == 0) {
                seed = 31;
            }
        }
    }
    
    boolean lightUpExitSign = false;
    
    boolean pendingRewardRoom = false;
    
    boolean pendingPrefieldRoom = false;
    int prefieldCount = 0;
    boolean inPrefield = false;
    float reinforcedDoorPercent = 1;
    boolean inDustons;
    public int cockroachX = 520;
    public int roomsTillKey = (int) Math.floor(Math.random() * 4 + 1);
    boolean hasKey = false;
    boolean hasCup = false;
    public List<CoffeeParticle> coffeeParticles = new ArrayList<>();
    
    public byte[] ermWhatTheSigma = new byte[] {0,0,0,0,1,1,1,0,2,1,2,0,1,1,0,2,1,1,1,0,1,1,0,2,1,1,0,1,1,0,2,1,2,1,0,1,2,0,2};
    public int ermIndex = 0;
    public byte ermTimer = 0;

    public boolean afterField = false;
    
    boolean rewardRoom = false;
    boolean penExists = true;
    
    public boolean shownPrefieldMSILogo = false;
    
    
    
    public void regenerateFurniture() {
        synchronized (coffeeParticles) {
            coffeeParticles.clear();
        }
        conditionerMirrored = false;
        randomPipeX = new int[] {1480, 1480};
        cup = new Rectangle(2000, 1000, 1, 1);
        shownPrefieldMSILogo = false;
        timerBroken = (rooms >= 2) && (Math.random() < 0.025F);
        
        
        if(pendingRewardRoom) {
            floorGeometry = GamePanel.getPolygon(List.of(new Point(0, 637), new Point(212, 615), new Point(543, 615), new Point(552, 598), new Point(930, 598),
                    new Point(940, 615), new Point(1268, 615), new Point(1480, 637)));
            ceilGeometry = GamePanel.getPolygon(List.of(new Point(0, 3), new Point(212, 50), new Point(543, 50), new Point(552, 80), new Point(930, 80),
                    new Point(940, 50), new Point(1268, 50), new Point(1480, 3)));
            bgIndex = 10;

            conditioner = new Rectangle(959, 95, 291, 173);
            table = new Rectangle(891, 465, 459, 175);
            exit = new Rectangle(642, 306, 196, 297);
            timer = new Rectangle(256, 110, 241, 123);

            locker = new Rectangle(1480, 640, 1, 1);
            compass = new Rectangle(1480, 640, 120, 120);

            hasConditioner = true;
            hasLocker = false;
            
            rewardRoom = true;
            
            return;
        }
        if(pendingPrefieldRoom) {
            if(inDustons) {
                floorGeometry = GamePanel.getPolygon(List.of(new Point(0, 0), new Point(258, 0), new Point(259, 614), new Point(263, 602),
                        new Point(1015, 598), new Point(1022, 610), new Point(1023, 0), new Point(1280, 0)));
                ceilGeometry = GamePanel.getPolygon(List.of(new Point(0, 640), new Point(258, 640), new Point(259, 64), new Point(263, 80),
                        new Point(1015, 80), new Point(1022, 64), new Point(1023, 640), new Point(1280, 640)));
                bgIndex = 15;

                conditioner = new Rectangle(675, 89, 325, 195);
                exit = new Rectangle(34, 343, 196, 297);
                table = new Rectangle(272, 450, 459, 175);

                compass = new Rectangle(1480, 640, 120, 120);
                timer = new Rectangle(1480, 640, 241, 123);
                locker = new Rectangle(1480, 640, 1, 1);

                hasConditioner = true;
                hasLocker = false;

                if(!hasCup) {
                    cup = new Rectangle(596, 406, 75, 55);
                }
                return;
            }
            
            locker = new Rectangle(1480, 640, 1, 1);
            compass = new Rectangle(1480, 640, 120, 120);
            table = new Rectangle(1480, 640, 1, 1);

            hasConditioner = true;
            hasLocker = false;
            
            switch (prefieldCount) {
                case 1 -> {
                    floorGeometry = GamePanel.getPolygon(List.of(new Point(0, 636), new Point(347, 601), new Point(832, 599), new Point(839, 617), new Point(1268, 615), new Point(1480, 637)));
                    ceilGeometry = GamePanel.getPolygon(List.of(new Point(0, 0), new Point(347, 80), new Point(832, 80), new Point(839, 46), new Point(1268, 49), new Point(1480, 3)));
                    bgIndex = 12;

                    conditioner = new Rectangle(910, 103, 325, 195);
                    exit = new Rectangle(514, 374, 152, 230);
                    timer = new Rectangle(570, 185, 241, 123);
                }
                case 2 -> {
                    floorGeometry = GamePanel.getPolygon(List.of(new Point(0, 640), new Point(212, 621), new Point(353, 621), new Point(364, 603),
                            new Point(1116, 603), new Point(1125, 621), new Point(1268, 621), new Point(1480, 640)));
                    ceilGeometry = GamePanel.getPolygon(List.of(new Point(0, 0), new Point(212, 47), new Point(353, 47), new Point(364, 80),
                            new Point(1116, 80), new Point(1125, 47), new Point(1268, 47), new Point(1480, 0)));
                    bgIndex = 13;

                    exit = new Rectangle(494, 374, 152, 230);
                    timer = new Rectangle(451, 216, 241, 123);
                    conditioner = new Rectangle(734, 101, 275, 164);
                    table = new Rectangle(694, 354, 379, 142);
                }
                case 5 -> {
                    floorGeometry = GamePanel.getPolygon(List.of(new Point(0, 640), new Point(212, 621), new Point(353, 621), new Point(364, 603),
                            new Point(1116, 603), new Point(1125, 621), new Point(1268, 621), new Point(1480, 640)));
                    ceilGeometry = GamePanel.getPolygon(List.of(new Point(0, 0), new Point(212, 47), new Point(353, 47), new Point(364, 80),
                            new Point(1116, 80), new Point(1125, 47), new Point(1268, 47), new Point(1480, 0)));
                    bgIndex = 16;
                    
                    conditioner = new Rectangle(1480, 640, 1, 1);
                    
                    exit = new Rectangle(620, 287, 238, 317);
                    timer = new Rectangle(587, 99, 304, 154);

                    checkpoint = 1;
                }
            }
            return;
        }

        conditionerMirrored = Math.random() < 0.08;
        
        
        switch (seed) {
            case 0 -> {
                floorGeometry = GamePanel.getPolygon(List.of(new Point(0, 635), new Point(217, 617), new Point(1267, 612), new Point(1480, 633)));
                ceilGeometry = GamePanel.getPolygon(List.of(new Point(0, 5), new Point(213, 48), new Point(1269, 52), new Point(1480, 5)));
                bgIndex = 5;
                
                conditioner = new Rectangle(244, 67, 325, 195);
                locker = new Rectangle(263, 317, 206, 318);
                timer = new Rectangle(606, 94, 241, 123);
                compass = new Rectangle(905, 305, 120, 120);
                table = new Rectangle(767, 455, 459, 175);
                exit = new Rectangle(557, 326, 196, 297);

                hasConditioner = true;
                hasLocker = true;
            }
            case 1 -> {
                floorGeometry = GamePanel.getPolygon(List.of(new Point(0, 636), new Point(347, 601), new Point(832, 599), new Point(839, 617), new Point(1268, 615), new Point(1480, 637)));
                ceilGeometry = GamePanel.getPolygon(List.of(new Point(0, 0), new Point(347, 80), new Point(832, 80), new Point(839, 46), new Point(1268, 49), new Point(1480, 3)));
                bgIndex = 6;

                locker = new Rectangle(1030, 317, 206, 318);
                timer = new Rectangle(520, 256, 173, 88);
                compass = new Rectangle(987, 113, 120, 120);
                exit = new Rectangle(528, 373, 152, 230);

                table = new Rectangle(1480, 640, 1, 1);
                conditioner = new Rectangle(1480, 640, 1, 1);
                
                hasConditioner = false;
                hasLocker = true;
            }
            case 2 -> {
                floorGeometry = GamePanel.getPolygon(List.of(new Point(1480, 636), new Point(1133, 601), new Point(648, 599), new Point(641, 617), new Point(212, 615), new Point(0, 637)));
                ceilGeometry = GamePanel.getPolygon(List.of(new Point(1480, 0), new Point(1133, 80), new Point(648, 80), new Point(641, 46), new Point(212, 49), new Point(0, 3)));
                bgIndex = 7;

                locker = new Rectangle(235, 308, 206, 318);
                timer = new Rectangle(296, 134, 241, 123);
                compass = new Rectangle(1240, 281, 120, 120);
                exit = new Rectangle(787, 374, 152, 230);
                table = new Rectangle(1008, 528, 459, 175);
                
                conditioner = new Rectangle(1480, 640, 1, 1);

                hasConditioner = false;
                hasLocker = true;
            }
            case 3 -> {
                ceilGeometry = GamePanel.getPolygon(List.of(new Point(0, 0), new Point(347, 80), new Point(531, 80), new Point(532, 47), new Point(640, 47), new Point(647, 80),
                        new Point(831, 80), new Point(840, 47), new Point(933, 47), new Point(934, 80), new Point(1131, 80), new Point(1480, 0)));
                floorGeometry = GamePanel.getPolygon(List.of(new Point(0, 637), new Point(347, 600), new Point(531, 600), new Point(532, 615), new Point(640, 615), new Point(647, 600), 
                        new Point(831, 600), new Point(840, 615), new Point(933, 615), new Point(934, 600), new Point(1131, 600), new Point(1480, 637)));
                bgIndex = 8;

                timer = new Rectangle(289, 134, 241, 123);
                compass = new Rectangle(827, 386, 120, 120);
                exit = new Rectangle(664, 372, 152, 230);
                locker = new Rectangle(1136, 345, 188, 290);

                if(Math.random() < 0.3) {
                    table = new Rectangle(56, 490, 459, 175);
                } else {
                    table = new Rectangle(1480, 640, 1, 1);
                }
                
                conditioner = new Rectangle(1480, 640, 1, 1);

                hasConditioner = false;
                hasLocker = true;
            }
            case 4 -> {
                floorGeometry = GamePanel.getPolygon(List.of(new Point(0, 635), new Point(217, 617), new Point(1267, 612), new Point(1480, 633)));
                ceilGeometry = GamePanel.getPolygon(List.of(new Point(0, 5), new Point(213, 48), new Point(1269, 52), new Point(1480, 5)));
                bgIndex = 5;

                compass = new Rectangle(678, 295, 120, 120);
                exit = new Rectangle(287, 328, 196, 297);
                locker = new Rectangle(1000, 315, 206, 318);

                if(Math.random() < 0.4) {
                    table = new Rectangle(531, 472, 427, 162);
                } else {
                    table = new Rectangle(1480, 640, 1, 1);
                }

                timer = new Rectangle(1480, 640, 241, 123);
                conditioner = new Rectangle(1480, 640, 1, 1);
                
                hasConditioner = false;
                hasLocker = true;
            }
            case 5 -> {
                ceilGeometry = GamePanel.getPolygon(List.of(new Point(0, 0), new Point(347, 80), new Point(531, 80), new Point(532, 47), 
                        new Point(933, 47), new Point(934, 80), new Point(1131, 80), new Point(1480, 0)));
                floorGeometry = GamePanel.getPolygon(List.of(new Point(0, 637), new Point(347, 600), new Point(531, 600), new Point(532, 615), 
                        new Point(933, 615), new Point(934, 600), new Point(1131, 600), new Point(1480, 637)));
                bgIndex = 11;

                exit = new Rectangle(956, 374, 152, 230);
                table = new Rectangle(521, 472, 427, 162);
                compass = new Rectangle(674, 303, 120, 120);
                timer = new Rectangle(614, 120, 241, 123);
                locker = new Rectangle(138, 314, 206, 318);

                conditioner = new Rectangle(1480, 640, 1, 1);

                hasConditioner = false;
                hasLocker = true;
            }
            case 6 -> {
                floorGeometry = GamePanel.getPolygon(List.of(new Point(1480, 636), new Point(1133, 601), new Point(648, 599), new Point(641, 617), new Point(212, 615), new Point(0, 637)));
                ceilGeometry = GamePanel.getPolygon(List.of(new Point(1480, 0), new Point(1133, 80), new Point(648, 80), new Point(641, 46), new Point(212, 49), new Point(0, 3)));
                bgIndex = 7;

                timer = new Rectangle(673, 259, 215, 109);
                compass = new Rectangle(956, 312, 120, 120);
                exit = new Rectangle(316, 323, 196, 297);
                table = new Rectangle(651, 477, 379, 144);
                conditioner = new Rectangle(251, 79, 325, 195);

                locker = new Rectangle(1480, 640, 1, 1);

                hasConditioner = true;
                hasLocker = false;
            }
            case 30 -> {
                ceilGeometry = GamePanel.getPolygon(List.of(new Point(0, 2), new Point(211, 48), new Point(638, 47), new Point(646, 80),
                        new Point(833, 80), new Point(839, 47), new Point(1268, 47), new Point(1480, 2)));
                floorGeometry = GamePanel.getPolygon(List.of(new Point(0, 637), new Point(211, 614), new Point(638, 617), new Point(646, 600),
                        new Point(833, 600), new Point(839, 617), new Point(1268, 616), new Point(1480, 637)));
                bgIndex = 9;

                compass = new Rectangle(354, 330, 120, 120);
                exit = new Rectangle(664, 372, 152, 230);
                timer = new Rectangle(942, 329, 241, 123);
                
                conditioner = new Rectangle(1480, 640, 1, 1);
                table = new Rectangle(1480, 640, 1, 1);
                locker = new Rectangle(1480, 640, 1, 1);

                hasConditioner = false;
                hasLocker = false;
            }
            case 31 -> {
                floorGeometry = GamePanel.getPolygon(List.of(new Point(0, 635), new Point(217, 617), new Point(1267, 612), new Point(1480, 633)));
                ceilGeometry = GamePanel.getPolygon(List.of(new Point(0, 5), new Point(213, 48), new Point(1269, 52), new Point(1480, 5)));
                bgIndex = 14;

                exit = new Rectangle(643, 324, 196, 297);
                compass = new Rectangle(876, 339, 120, 120);
                conditioner = new Rectangle(775, 89, 325, 195);
                timer = new Rectangle(224, 316, 217, 116);
                table = new Rectangle(150, 455, 459, 175);
                
                locker = new Rectangle(1480, 640, 1, 1);

                hasConditioner = true;
                hasLocker = false;
            }
        }

        tryPipeGeneration();
        
        if(table.x < 1480 && roomsTillKey <= 0) {
            float size = (table.width / 459F);
            float range = table.width * 0.9F;
            int height = (int) (19 * size);
            int width = (int) (67 * size);
            key = new Rectangle((int) (table.x + table.width / 20 + Math.random() * (range - width)), (int) (table.y + table.height / 14F - height), width, height);
        } else {
            key = new Rectangle(2000, 1000, 1, 1);
        }
    }
    
    void tryPipeGeneration() {
        if(rooms >= 3) {
            if(Math.random() < 0.25) {
                randomPipeX[0] = 80;
            }
            if(Math.random() < 0.25) {
                randomPipeX[1] = 1320;
            }
        }
    }


    public void resetResettable() {
        timerText = null;
        wobbleFade = 120;

        untilDoor = 30;
        barrierRotation = 0;

        hoveringCompass = false;
        hoveringExit = false;
        hoveringLocker = false;
        hoveringReinforced = false;
        
        lightUpExitSign = false;
        
        if(inPrefield) {
            untilDoor = 10;
        }
    }

    public void resetField() {
        displayFieldDeathScreen = false;
        afterField = true;
        pendingPrefieldRoom = false;
        respawnCheckpoint = false;
        inPrefield = false;
        prefieldCount = 0;
        checkpoint = 2;
    }
    

    

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public int getSeed() {
        return seed;
    }

    public int getWobbleFade() {
        return wobbleFade;
    }
    
    public boolean isTimerBroken() {
        return timerBroken;
    }

    public void setWobbleFade(int wobbleFade) {
        this.wobbleFade = wobbleFade;
    }

    public void setUntilDoor(int untilDoor) {
        this.untilDoor = untilDoor;
        
        if(untilDoor <= 0) {
            lightUpExitSign = true;
            flickerExitSign();
        }
    }

    public int getUntilDoor() {
        return untilDoor;
    }
    
    private void flickerExitSign() {
        new Pepitimer(() -> {
            lightUpExitSign = false;

            new Pepitimer(() -> {
                lightUpExitSign = true;
                
                if(Math.random() < 0.3) {
                    flickerExitSign();
                }
            }, (int) (10 + Math.random() * 20));
        }, (int) (10 + Math.random() * 50));
    }

    public boolean isExitSignLitUp() {
        return lightUpExitSign;
    }

    public float getBarrierRotation() {
        return barrierRotation;
    }

    public void setBarrierRotation(float barrierRotation) {
        this.barrierRotation = barrierRotation;
    }

    public boolean isHoveringCompass() {
        return hoveringCompass;
    }

    public boolean isHoveringExit() {
        return hoveringExit;
    }

    public boolean isHoveringLocker() {
        return hoveringLocker;
    }

    public boolean isHoveringConditioner() {
        return hoveringConditioner;
    }

    public boolean isHoveringPen() {
        return hoveringPen;
    }

    public void setHoveringCompass(boolean hoveringCompass) {
        this.hoveringCompass = hoveringCompass;
    }

    public void setHoveringExit(boolean hoveringExit) {
        this.hoveringExit = hoveringExit;
    }

    public void setHoveringLocker(boolean hoveringLocker) {
        this.hoveringLocker = hoveringLocker;
    }

    public void setHoveringConditioner(boolean hoveringConditioner) {
        this.hoveringConditioner = hoveringConditioner;
    }

    public void setHoveringPen(boolean hoveringPen) {
        this.hoveringPen = hoveringPen;
    }

    public boolean isHoveringReinforced() {
        return hoveringReinforced;
    }
    
    public void setHoveringReinforced(boolean hoveringReinforced) {
        this.hoveringReinforced = hoveringReinforced;
    }

    public boolean penExists() {
        return penExists;
    }
    
    public void setPen(boolean penExists) {
        this.penExists = penExists;
    }

    public int getGoalCompassRotation() {
        return goalCompassRotation;
    }

    public void setGoalCompassRotation(int goalCompassRotation) {
        this.goalCompassRotation = goalCompassRotation;
    }

    public int getCompassRotation() {
        return compassRotation;
    }

    public void setCompassRotation(int compassRotation) {
        this.compassRotation = compassRotation;
    }

    public boolean showCompassHint() {
        return showCompassHint;
    }

    public void setShowCompassHint(boolean showCompassHint) {
        this.showCompassHint = showCompassHint;
    }

    public float getShake() {
        return shake;
    }

    public void setShake(float shake) {
        this.shake = shake;
    }

    public boolean isPendingRewardRoom() {
        return pendingRewardRoom;
    }

    public void setPendingRewardRoom(boolean pendingRewardRoom) {
        this.pendingRewardRoom = pendingRewardRoom;
    }

    public boolean isRewardRoom() {
        return rewardRoom;
    }

    public void setRewardRoom(boolean rewardRoom) {
        this.rewardRoom = rewardRoom;
    }

    public boolean isPendingPrefield() {
        return pendingPrefieldRoom;
    }

    public void setPendingPrefieldRoom(boolean pendingPrefieldRoom) {
        this.pendingPrefieldRoom = pendingPrefieldRoom;
    }

    public boolean isAfterField() {
        return afterField;
    }
    
    public void setAfterField(boolean afterField) {
        this.afterField = afterField;
    }

    public int getPrefieldCount() {
        return prefieldCount;
    }

    public void setPrefieldCount(int prefieldCount) {
        this.prefieldCount = prefieldCount;
    }

    public boolean isInPrefield() {
        return inPrefield;
    }

    public void setInPrefield(boolean inPrefield) {
        this.inPrefield = inPrefield;
    }

    public float getReinforcedDoorPercent() {
        return reinforcedDoorPercent;
    }

    public void setReinforcedDoorPercent(float reinforcedDoorPercent) {
        this.reinforcedDoorPercent = reinforcedDoorPercent;
    }

    public void setInDustons(boolean inDustons) {
        this.inDustons = inDustons;
    }

    public boolean isInDustons() {
        return inDustons;
    }

    public boolean isConditionerMirrored() {
        return conditionerMirrored;
    }

    public boolean hasKey() {
        return hasKey;
    }
    public void setHoveringKey(boolean hoveringKey) {
        this.hoveringKey = hoveringKey;
    }
    public boolean isHoveringKey() {
        return hoveringKey;
    }
    public void setHasKey(boolean hasKey) {
        this.hasKey = hasKey;
    }
    public boolean isHoveringCup() {
        return hoveringCup;
    }
    public void setHoveringCup(boolean hoveringCup) {
        this.hoveringCup = hoveringCup;
    }
    public void setHasCup(boolean hasCup) {
        this.hasCup = hasCup;
    }
    public boolean hasCup() {
        return hasCup;
    }

    
    
    
    
    public int getRoom() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public int[] getRandomPipeX() {
        return randomPipeX;
    }
    
    public Level getOldNight() {
        return oldNight;
    }

    public void setOldNight(Level oldNight) {
        this.oldNight = oldNight;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public boolean isRespawnCheckpoint() {
        return respawnCheckpoint;
    }

    public void setRespawnCheckpoint(boolean respawnCheckpoint) {
        this.respawnCheckpoint = respawnCheckpoint;
    }

    public void setMaxOffset(int maxOffset) {
        this.maxOffset = maxOffset;
    }
    
}
