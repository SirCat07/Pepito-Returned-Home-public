package game.field;

import game.enviornments.HChamber;
import game.particles.FieldRaindrop;
import main.GamePanel;
import utils.GameState;
import utils.NoiseGenerator;
import utils.Pepitimer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Field {
    float distance = 0.5F;
    int roadWidth = 550;
    int yaw = 0;
    float y = 320;
    int pitch = 45;
    int x = 0;
//    float acceleration = 0.01F;
    
    float acceleration = 0.034F;
//    float acceleration = 0.04F;
//    float acceleration = 0.06F;
//    float acceleration = 0.2F;
    
    float speed = 0;
    float turnSpeed = 0;
    

    public float carYaw = 0;
    public float carPitch = 0;
    
    public List<FieldRaindrop> raindrops = new ArrayList<>();
    
    boolean inCar = false;
    public boolean lockedIn = true;
    boolean hoveringCar = false;
    
    boolean playingMusic = false;
    
    public static Color FogColor = new Color(2, 4, 15);
//    public static Color FogColor = new Color(0, 120, 0);
//    public static Color FogColor = new Color(120, 0 ,0);
    public static boolean lightMode = false;

    
    byte[] objectsFarLeft;
    byte[] objectsLeft;
    byte[] objects2ThirdsLeft;
    byte[] objectsMiddle;
    byte[] objects2ThirdsRight;
    byte[] objectsRight;
    byte[] objectsFarRight;
    byte[] road;
    
    float[] roadXOffsetArray;
    float[] roadYOffsetArray;
    float[] roadWidthArray;
    float[] pathWidthArray;
    float[] treeXOffsetArray;
    float[] pathYOffset;
    float[] farModifier;
    float[] landmineChanceMultiplier;
    
    public double lightningProgress = 0;
    
    public Hydrophobia90 a90;
    
    Color skyColor = new Color(0, 4, 26);
    Color groundColor = new Color(4, 5, 17);
    Color cloudColor = new Color(6, 10, 29);
    Color pathColor = new Color(13, 6, 22);
    Color raindropColor = new Color(0, 0, 70);
    
    public FieldObject[] objects;
    
    FieldBlimp blimp = new FieldBlimp();

    public List<Pepitimer> cancelAfter = new ArrayList<>();
    
    
    
//    public int getSize() {
//        return 3600;
//    }
    public int getSize() {
        return 3300;
//        return 2200;
//        return 2800;
//        return 1600;
//        return 1200;
//        return 400;
//        return 100;
    }


    int spawnGrassLeft = 0;
    int spawnGrassRight = 0;
    
    
    public void generate(NoiseGenerator noise) {
        int size = getSize();
        objectsFarLeft = new byte[size];
        objectsLeft = new byte[size];
        objects2ThirdsLeft = new byte[size];
        objectsMiddle = new byte[size];
        objects2ThirdsRight = new byte[size];
        objectsRight = new byte[size];
        objectsFarRight = new byte[size];
        
        road = new byte[size];
        
        roadXOffsetArray = new float[size];
        roadYOffsetArray = new float[size];
        roadWidthArray = new float[size];
        pathWidthArray = new float[size];
        treeXOffsetArray = new float[size];
        pathYOffset = new float[size];
        farModifier = new float[size];
        landmineChanceMultiplier = new float[size];
        
        
        float seed = (int) (Math.random() * 100);
        
        FieldBiome currentBiome = FieldBiome.STRAIGHT;
        FieldBiome oldBiome = FieldBiome.STRAIGHT;
        
        int biomes = 0;
       
        int biomeLength = 300;
        int interpPeriods = 200;
        float inverseInterp = 1F / interpPeriods;
        int howMuchBiomeLeft = biomeLength;
        float currentInterp = 1;
        
//        List<FieldBiome> biomesToUse = new ArrayList<>();
//        biomesToUse.add(FieldBiome.WILD_MOUNTAINS);
//        biomesToUse.add(FieldBiome.RAVINE);
        
        for(int i = 0; i < size; i++) {
            howMuchBiomeLeft--;
            if(howMuchBiomeLeft <= 0) {
                oldBiome = currentBiome;

//                if(!biomesToUse.isEmpty()) {
//                    currentBiome = biomesToUse.get(0);
//                    biomesToUse.remove(0);
//                }
                while (currentBiome == oldBiome) {
                    currentBiome = FieldBiome.values()[(int) (Math.random() * FieldBiome.values().length)];
                }
                biomes++;
                if(biomes == 6) {
                    currentBiome = FieldBiome.RAVINE;
                }
                currentInterp = 0;
                howMuchBiomeLeft = biomeLength + interpPeriods;

                System.out.println(biomes + " - " + currentBiome);
            }
            if(currentInterp < 1) {
                currentInterp += inverseInterp;
                if(currentInterp > 1) {
                    currentInterp = 1;
                }
            }

            roadYOffsetArray[i] = (float) GamePanel.lerp(oldBiome.getY(noise, i, seed), currentBiome.getY(noise, i, seed), currentInterp);
            roadXOffsetArray[i] = (float) GamePanel.lerp(oldBiome.getX(noise, i, seed), currentBiome.getX(noise, i, seed), currentInterp);
            pathWidthArray[i] = (float) GamePanel.lerp(oldBiome.getPathWidth(), currentBiome.getPathWidth(), currentInterp);
            if(pathWidthArray[i] < -140) {
                pathWidthArray[i] = -540;
            }
            roadWidthArray[i] = (float) GamePanel.lerp(oldBiome.getRoadWidth(), currentBiome.getRoadWidth(), currentInterp);
            landmineChanceMultiplier[i] = (float) GamePanel.lerp(oldBiome.getLandmineChanceMultiplier(), currentBiome.getLandmineChanceMultiplier(), currentInterp);
            
            pathYOffset[i] = (float) GamePanel.lerp(oldBiome.getPathY(), currentBiome.getPathY(), currentInterp);
            
            farModifier[i] = (float) Math.max(oldBiome.getFarModifier(), currentBiome.getFarModifier());

            treeXOffsetArray[i] = (int) (Math.random() * 100 - 50);
            
            
            generateObjects(currentBiome == FieldBiome.RAVINE, i);
        }
        
        
        objectsMiddle[size - 2] = (byte) 13;
        objectsFarLeft[size - 2] = (byte) 7;
        
        objectsRight[3100] = 14;
//        objectsMiddle[3100] = 10;
        
        road[1] = 0;
        objectsFarLeft[1] = 1;
        objectsFarRight[1] = 1;
        
        x = (int) -roadXOffsetArray[0];
        y = (int) -roadYOffsetArray[0] + 320;


        // every object index is 1 above sorry
        objects = new FieldObject[14];
        
        objects[0] = new FieldObject(8, GamePanel.fieldMediumTree.request(), 305, 386, 5);
        objects[1] = new FieldObject(8, GamePanel.fieldMediumTree2.request(), 305, 396, 5);
        objects[2] = new FieldObject(8, GamePanel.fieldMediumTree3.request(), 305, 393, 5);
        objects[3] = new FieldObject(8, GamePanel.pineMedium.request(), 305, 500, 5);
        objects[4] = new FieldObject(8, GamePanel.pineMedium2.request(), 305, 500, 5);
        objects[5] = new FieldObject(8, GamePanel.pineMedium3.request(), 305, 496, 5);
        objects[6] = new FieldObject(8, GamePanel.fieldLightsTower.request(), 126, 700, 24);
        objects[7] = new FieldObject(8, GamePanel.fieldLandmine.request(), 105, 45, 2);
        objects[8] = new FieldObject(8, GamePanel.fieldGrass.request(), 167, 30, 4);
//        objects[9] = new FieldObject(3, GamePanel.fieldSurpriseSog.request(), 305, 396, 3);
//        objects[9].table[1] = GamePanel.silhouette(GamePanel.fieldSurpriseSog.request(), FogColor);
//        objects[9].table[2] = GamePanel.silhouette(GamePanel.fieldSurpriseSog.request(), FogColor);

        objects[10] = new FieldObject(8, GamePanel.treeStump.request(), 223, 128, 2);
        objects[11] = new FieldObject(8, GamePanel.treeDead.request(), 192, 344, 4);
        
        objects[12] = new FieldObject(1, GamePanel.fieldEndBuilding.request(), 1064, 577, 6);
        
        objects[13] = new FieldObject(8, GamePanel.fieldSpeedLimit10.request(), 153, 280, 4);
//        objects[14] = new FieldObject(8, GamePanel.fieldBarriers.request(), 229, 130, 3);
    }
    
    public void generateObjects(boolean isRavine, int i) {
        objectsFarLeft[i] = 0;
        objectsLeft[i] = (byte) (Math.ceil(Math.random() * 5));

        objects2ThirdsLeft[i] = (byte) ((Math.random() < 0.006 * landmineChanceMultiplier[i] && i > 50 && i < 3200) ? 8 : (spawnGrassLeft > 0 ? 9 : 0));
        objectsMiddle[i] = (byte) ((Math.random() < 0.003 * landmineChanceMultiplier[i] && i > 50 && i < 3200) ? 8 : 0);
        objects2ThirdsRight[i] = (byte) ((Math.random() < 0.006 * landmineChanceMultiplier[i] && i > 50 && i < 3200) ? 8 : (spawnGrassRight > 0 ? 9 : 0));

        objectsRight[i] = (byte) (Math.ceil(Math.random() * 5));
        objectsFarRight[i] = 0;
        
//        if(isWalled) {
//            objectsLeft[i] = 11;
//            objectsRight[i] = 11;
//        }
        if(isRavine) {
            objectsFarLeft[i] = (byte) (Math.ceil(Math.random() * 5));
            objectsLeft[i] = (byte) (11 + Math.round(Math.random()));
            objectsRight[i] = (byte) (11 + Math.round(Math.random()));
            objectsFarRight[i] = (byte) (Math.ceil(Math.random() * 5));
            
//            if(Math.random() < 0.3) {
//                objectsLeft[i] = 15;
//                objectsRight[i] = 15;
//            }
        }

//        if(Math.random() < 0.1) {
//            objectsLeft[i] = 7;
//        }
//        if(Math.random() < 0.1) {
//            objectsRight[i] = 7;
//        }

        spawnGrassLeft--;
        spawnGrassRight--;

        if(Math.random() < 0.04) {
            spawnGrassLeft = (int) (3 + Math.ceil(Math.random() * 2));
        }
        if(Math.random() < 0.04) {
            spawnGrassRight = (int) (3 + Math.ceil(Math.random() * 2));
        }

        road[i] = 1;
    }
    
    public void lightningStrike(GamePanel g) {
        lightningProgress = 1;
        g.sound.play("thunder" + (int) (Math.random() * 3 + 1), 0.1);
    }
    
    
    public void handleCollision(GamePanel g) {
        if(Math.abs(speed) < 1) {
            lowSpeedCrash(g);
        } else {
            highSpeedCrash(g);
        }
    }
    
    public void lowSpeedCrash(GamePanel g) {
        isColliding = true;
        loadSnapshot();
    
        speed = -speed / 2F;
        turnSpeed = 0;

        if(Math.abs(speed) > 0.1F) {
            g.sound.playRate("fieldCarHit", 0.1, 0.8 + (0.2 * Math.random()));
        }
        
        isColliding = false;
    }

    public void highSpeedCrash(GamePanel g) {
        kill(g, "fieldObstacles");
    }
    
    public void kill(GamePanel g, String id) {
        g.getNight().startUIFade = -200;
        g.fadeOut(255, 255, 0);
        ((HChamber) g.getNight().env).timerText = null;
        
        ((HChamber) g.getNight().env).displayFieldDeathScreen = true;
                
        g.state = GameState.GAME;
        for(Pepitimer pepitimer : cancelAfter) {
            pepitimer.cancel();
        }
        
        g.keyHandler.holdingW = false;
        g.keyHandler.holdingA = false;
        g.keyHandler.holdingS = false;
        g.keyHandler.holdingD = false;
        
        g.jumpscare(id, g.getNight().getId());
    }
    
    public boolean isColliding = false;
    
    public float[] lastStableSnapshot = new float[] {0.5F, 0, 320F, 45, 0, 0F, 0F};
    
    public void saveSnapshot() {
        lastStableSnapshot[0] = distance;
        lastStableSnapshot[1] = yaw;
        lastStableSnapshot[2] = y;
        lastStableSnapshot[3] = pitch;
        lastStableSnapshot[4] = x;
        lastStableSnapshot[5] = carYaw;
        lastStableSnapshot[6] = carPitch;
    }

    public void loadSnapshot() {
        distance = lastStableSnapshot[0];
        yaw = (int) lastStableSnapshot[1];
        y = lastStableSnapshot[2];
        pitch = (int) lastStableSnapshot[3];
        x = (int) lastStableSnapshot[4];
        carYaw = lastStableSnapshot[5];
        carPitch = lastStableSnapshot[6];
    }
    
    

    public float getDistance() {
        return distance;
    }
    public void addDistance(float add) {
        distance += add;
    }
    public float getY() {
        return y;
    }
    public void setY(float y) {
        this.y = y;
    }
    public int getRoadWidth() {
        return roadWidth;
    }
    public void setRoadWidth(int roadWidth) {
        this.roadWidth = roadWidth;
    }
    public int getYaw() {
        return yaw;
    }
    public void setYaw(int yaw) {
        this.yaw = yaw;
    }
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getPitch() {
        return pitch;
    }
    public void setPitch(int pitch) {
        this.pitch = pitch;
    }
    public float getCarYaw() {
        return carYaw;
    }
    public float getCarPitch() {
        return carPitch;
    }

    public float getSpeed() {
        return speed;
    }
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    public float getAcceleration() {
        return acceleration;
    }
    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public float getTurnSpeed() {
        return turnSpeed;
    }
    public void setTurnSpeed(float turnSpeed) {
        this.turnSpeed = turnSpeed;
    }


    public byte[] getObjectsFarLeft() {
        return objectsFarLeft;
    }
    public byte[] getObjectsLeft() {
        return objectsLeft;
    }
    public byte[] getObjects2ThirdsLeft() {
        return objects2ThirdsLeft;
    }
    public byte[] getObjectsMiddle() {
        return objectsMiddle;
    }
    public byte[] getObjects2ThirdsRight() {
        return objects2ThirdsRight;
    }
    public byte[] getObjectsRight() {
        return objectsRight;
    }
    public byte[] getObjectsFarRight() {
        return objectsFarRight;
    }
    public byte[] getRoad() {
        return road;
    }

    public float[] getRoadWidthArray() {
        return roadWidthArray;
    }
    public float[] getRoadXOffsetArray() {
        return roadXOffsetArray;
    }
    public float[] getRoadYOffsetArray() {
        return roadYOffsetArray;
    }
    public float[] getPathWidthArray() {
        return pathWidthArray;
    }
    public float[] getPathYOffset() {
        return pathYOffset;
    }
    public float[] getFarModifier() {
        return farModifier;
    }
    public float[] getTreeXOffsetArray() {
        return treeXOffsetArray;
    }


    public boolean isInCar() {
        return inCar;
    }
    public void setInCar(boolean inCar) {
        this.inCar = inCar;
    }

    
    public Color getCloudColor() {
        return cloudColor;
    }
    public Color getGroundColor() {
        return groundColor;
    }
    public Color getPathColor() {
        return pathColor;
    }
    public Color getSkyColor() {
        return skyColor;
    }
    public Color getRaindropColor() {
        return raindropColor;
    }

    
    public boolean isHoveringCar() {
        return hoveringCar;
    }
    public void setHoveringCar(boolean hoveringCar) {
        this.hoveringCar = hoveringCar;
    }

    public boolean isPlayingMusic() {
        return playingMusic;
    }
    public void setPlayingMusic(boolean playingMusic) {
        this.playingMusic = playingMusic;
    }
    public boolean stoppingFirstSong = false;
    public boolean playingSecondSong = false;
    public boolean stoppingSecondSong = false;


    
    
    public short[] generatorXes = new short[] {-1, -1};
    boolean inGeneratorMinigame = false;
    public float impulseInterp = 0;
    boolean hoveringLever = false;

    public boolean isHoveringLever() {
        return hoveringLever;
    }
    public void setHoveringLever(boolean hoveringLever) {
        this.hoveringLever = hoveringLever;
    }
    
    public boolean isInGeneratorMinigame() {
        return inGeneratorMinigame;
    }
    public void setInGeneratorMinigame(boolean inGeneratorMinigame) {
        this.inGeneratorMinigame = inGeneratorMinigame;
    }
    public void regenerateGeneratorXes() {
        generatorXes[0] = (short) (70 + Math.random() * 490);
        generatorXes[1] = (short) (70 + Math.random() * 490);

        Rectangle firstRect = new Rectangle(220 + generatorXes[0], 495, 60, 1);
        Rectangle secondRect = new Rectangle(220 + generatorXes[1], 495, 60, 1);

        if(firstRect.intersects(secondRect)) {
            regenerateGeneratorXes();
        }
    }

    public void quitGenerator() {
        inGeneratorMinigame = false;
        leverDegreesGoal = (float) (Math.PI / 2);
    }

    public FieldBlimp getBlimp() {
        return blimp;
    }
    
    
    public BufferedImage radarImg = null;
    
    public void redrawRadarImg(GamePanel g) {
        BufferedImage radarImg = new BufferedImage(161, 142, BufferedImage.TYPE_INT_RGB);
        Graphics2D radarGraphics = (Graphics2D) radarImg.getGraphics();

        radarGraphics.drawImage(g.fieldRadarBg.request(), 0, 0, null);
        int radarX = (int) (80 - 6 + (x - blimp.getX()) / 50F / 90F);
        int radarY = (int) (70 - 6 + (distance - blimp.getZ()) * 10F / 90F);

        if(radarX > 0 && radarX < 161 - 12) {
            if(radarY > 0 && radarY < 142 - 12) {
                radarGraphics.setColor(Color.RED);
                radarGraphics.fillOval(radarX, radarY, 12, 12);
            }
        }

        int radarX2 = (int) (80 - 6 + (x - (50000 - g.fixedUpdatesAnim * 120)) / 50F / 90F);
        int radarY2 = (int) (70 - 6 + (distance - 700) * 10F / 90F);

        if(radarX2 > 0 && radarX2 < 161 - 12) {
            if(radarY2 > 0 && radarY2 < 142 - 12) {
                radarGraphics.setColor(Color.RED);
                radarGraphics.fillOval(radarX2, radarY2, 12, 12);
            }
        }
        
        if(blimp.lockedOn && blimp.untilDirects < 4.5) {
            float until = Math.max(0, Math.round(blimp.untilDirects * 100F) / 100F);
            
            if(until > 0 || (g.fixedUpdatesAnim / 12) % 2 == 0) {
                radarGraphics.setFont(g.comicSans50);
                radarGraphics.setColor(Color.RED);

                String str = until + "s";
                radarGraphics.drawString(str, 80 - g.halfTextLength(radarGraphics, str), 96);
            }
        }

        radarGraphics.dispose();

        radarImg = g.bloom(radarImg);
        
        this.radarImg = radarImg;
    }
    
    

    public float zoomCountdown = 0;
    public BufferedImage lastImageBeforeField;

    public float objectiveInterp = 0;
    public float controlsTransparency = 2.2F;

    public float leverDegrees = (float) (Math.PI / 2);
    public float leverDegreesGoal = (float) (Math.PI / 2);
}