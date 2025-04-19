package game.custom;

import game.achievements.Achievements;
import main.GamePanel;
import utils.PepitoImage;
import utils.RepeatingPepitimer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CustomNight {
    public static CustomNightPrevieweable selectedElement = null;
    static List<CustomNightEnemy> enemies = new ArrayList<>();
    public static List<CustomNightModifier> modifiers = new ArrayList<>();
    static PepitoImage loadedPreview = null;
    public static boolean custom = false;
    public static boolean customSelected = false;
    public static boolean startSelected = false;
    public static boolean backSelected = false;
    public static boolean prevSelected = false;
    public static boolean nextSelected = false;
    public static boolean shuffleSelected = false;
    public static int selectedChallenge = 0;
    public static int holdingEnemyFrames = 0;
    public static float visualPointsProgress = 0;

    public static boolean isCustom() {
        return custom;
    }

    public static void addNewEnemy(CustomNightEnemy enemy) {
        enemies.add(enemy);
    }
    public static void addNewModifier(CustomNightModifier modifier) {
        modifiers.add(modifier);
    }

    public static void setLoadedPreviewPath(String path) {
        if(loadedPreview == null) {
            loadedPreview = new PepitoImage(path);
        } else {
            if(!loadedPreview.getPath().equals(path)) {
                loadedPreview.setPath(path);
                loadedPreview.reload();
            }
        }
    }

    public static PepitoImage getLoadedPreview() {
        return loadedPreview;
    }

    public static RepeatingPepitimer[] limboTimer = new RepeatingPepitimer[1];
    public static int limboId = -1;
    
    public static Rectangle enemiesRectangle = new Rectangle(0, 0, 660, 420);

    public static void limbo(GamePanel g) {
        final int[] n = {0};

        limboTimer[0] = new RepeatingPepitimer(() -> {
            for (int i = 0; i < enemies.size(); i++) {
                CustomNightEnemy enemy = enemies.get(i);
                int x = 105 * (i % 6);
                int y = 130 * (i / 6);

                enemy.otherX = x;
                enemy.otherY = y;
                if (Math.random() > 0.3) {
                    enemy.setAI((int) Math.round(Math.random() * 8));
                } else {
                    enemy.setAI(0);
                }
            }
            if (n[0] % 2 == 0) {
                Collections.shuffle(enemies);
            } else if (n[0] % 3 == 0) {
                Collections.reverse(enemies);
            } else {
                Collections.rotate(enemies, -2);
            }
            for(CustomNightModifier modifier : modifiers) {
                if(Math.random() < 0.5) {
                    modifier.toggle();
                }
            }

            n[0]++;
            if (n[0] > 31) {
                resetEntityPositions();
                if(limboTimer[0] != null) {
                    limboTimer[0].cancel(false);
                    limboTimer[0] = null;
                    limboId = -1;
                    g.music.play("tension", 0.05, true);
                }
            }
        }, 0, 300);
    }
    
    public static void resetEntityPositions() {
        List<CustomNightEnemy> oldEnemies = new ArrayList<>(enemies);
        for (CustomNightEnemy enemy : oldEnemies) {
            int position = enemy.getId();
            int x = 105 * (position % 6);
            int y = 130 * (position / 6);
            enemy.otherX = (x + enemy.otherX * 6) / 7;
            enemy.otherY = (y + enemy.otherY * 6) / 7;
            enemies.set(position, enemy);
        }
    }
    
    public static HashMap<CustomNightEnemy, Integer> customEnemyAIs = new HashMap<>();
    public static HashMap<CustomNightModifier, Boolean> customModifiers = new HashMap<>();


    public static void nextChallenge() {
        selectedChallenge++;

        if(selectedChallenge > getMaxChallenge()) {
            selectedChallenge = 0;
        }
        setEntityAIs();
    }
    public static void previousChallenge() {
        selectedChallenge--;

        if(selectedChallenge < 0) {
            selectedChallenge = getMaxChallenge();
        }
        setEntityAIs();
    }
    public static int getMaxChallenge() {
        int maxChallenge = 0;
        if(Achievements.EL_ASTARTA.isObtained()) {
            maxChallenge = 1;
            if(Achievements.BLIZZARD.isObtained()) {
                maxChallenge = 2;
                if(Achievements.TIME_IS_TICKING.isObtained()) {
                    maxChallenge = 3;
                    if(Achievements.THE_FOG_IS_COMING.isObtained()) {
                        maxChallenge = 4;
                    }
                }
            }
        }
        return maxChallenge;
    }

    public static void setEntityAIs() {
        if(CustomNight.isCustom()) {
            for (CustomNightEnemy enemy : enemies) {
                if (customEnemyAIs.isEmpty()) {
                    enemy.setAI(0);
                } else {
                    enemy.setAI(customEnemyAIs.get(enemy));
                }
            }
            for (CustomNightModifier modifier : modifiers) {
                if (customModifiers.isEmpty()) {
                    modifier.off();
                } else {
                    modifier.set(customModifiers.get(modifier));
                }
            }
        } else {
            for (CustomNightEnemy enemy : enemies) {
                enemy.setAI(0);
            }
            for (CustomNightModifier modifier : modifiers) {
                modifier.off();
            }
        }
        

        if(!CustomNight.isCustom()) {
            switch (selectedChallenge) {
                case 0 -> {
                    enemies.get(PEPITO).setAI(4); // pepito
                    enemies.get(NOTPEPITO).setAI(4); // notpepito
                    enemies.get(GLITCHER).setAI(4); // glitcher
                    enemies.get(A90).setAI(3); // a90
                    enemies.get(MSI).setAI(3); // msi
                    enemies.get(ASTARTA).setAI(6); // astarta
                    enemies.get(SHARK).setAI(2); // shark
                    enemies.get(MIRRORCAT).setAI(3); // mirrorcat
                    enemies.get(WIRES).setAI(2); // wires
                    enemies.get(JUMPSCARE_CAT).setAI(1); // jumpscare cat
                    enemies.get(EL_ASTARTA).setAI(2); // el astarta
                }

                case 1 -> {
                    enemies.get(PEPITO).setAI(5); // pepito
                    enemies.get(NOTPEPITO).setAI(5); // notpepito
                    enemies.get(GLITCHER).setAI(1); // glitcher
                    enemies.get(A90).setAI(3); // a90
                    enemies.get(MSI).setAI(4); // msi
                    enemies.get(ASTARTA).setAI(5); // astarta
                    enemies.get(SHARK).setAI(4); // shark
                    enemies.get(COLACAT).setAI(1); // colacat
                    enemies.get(MIRRORCAT).setAI(5); // mirrorcat
                    enemies.get(MAKI).setAI(1); // maki
                    enemies.get(LEMONADE_CAT).setAI(0); // lemonade
                    enemies.get(WIRES).setAI(5); // wires
                    enemies.get(SCARY_CAT).setAI(1); // scary cat
                    enemies.get(JUMPSCARE_CAT).setAI(1); // jumpscare cat

                    modifiers.get(0).on();
                    modifiers.get(1).on();
                }

                case 2 -> {
                    enemies.get(PEPITO).setAI(6); // pepito
                    enemies.get(NOTPEPITO).setAI(6); // notpepito
                    enemies.get(GLITCHER).setAI(7); // glitcher
                    enemies.get(A90).setAI(3); // a90
                    enemies.get(MSI).setAI(4); // msi
                    enemies.get(ASTARTA).setAI(6); // astarta
                    enemies.get(SHARK).setAI(5); // shark
                    enemies.get(BOYKISSER).setAI(4); // boykisser
                    enemies.get(MIRRORCAT).setAI(8); // mirrorcat
                    enemies.get(LEMONADE_CAT).setAI(1); // lemonade
                    enemies.get(WIRES).setAI(3); // wires
                    enemies.get(SCARY_CAT).setAI(4); // scary cat
                    enemies.get(JUMPSCARE_CAT).setAI(1); // jumpscare cat

                    modifiers.get(2).on();
                }

                case 3 -> {
                    enemies.get(PEPITO).setAI(7); // pepito
                    enemies.get(NOTPEPITO).setAI(7); // notpepito
                    enemies.get(A90).setAI(6); // a90
                    enemies.get(MSI).setAI(4); // msi
                    enemies.get(ASTARTA).setAI(6); // astarta
                    enemies.get(SHARK).setAI(8); // shark
                    enemies.get(BOYKISSER).setAI(2); // boykisser
                    enemies.get(COLACAT).setAI(1); // colacat
                    enemies.get(MIRRORCAT).setAI(5); // mirrorcat
                    enemies.get(LEMONADE_CAT).setAI(3); // lemonade
                    enemies.get(WIRES).setAI(3); // wires
                    enemies.get(SCARY_CAT).setAI(1); // scary cat
                    enemies.get(JUMPSCARE_CAT).setAI(1); // jumpscare cat

                    modifiers.get(3).on();
                }

                case 4 -> {
                    enemies.get(PEPITO).setAI(7); // pepito
                    enemies.get(NOTPEPITO).setAI(7); // notpepito
                    enemies.get(GLITCHER).setAI(7); // glitcher
                    enemies.get(A90).setAI(7); // a90
                    enemies.get(MSI).setAI(5); // msi
                    enemies.get(ASTARTA).setAI(6); // astarta
                    enemies.get(SHARK).setAI(7); // shark
                    enemies.get(BOYKISSER).setAI(6); // boykisser
                    enemies.get(COLACAT).setAI(1); // colacat
                    enemies.get(MIRRORCAT).setAI(8); // mirrorcat
                    enemies.get(MAKI).setAI(7); // maki
                    enemies.get(LEMONADE_CAT).setAI(6); // lemonade
                    enemies.get(WIRES).setAI(7); // wires
                    enemies.get(SCARY_CAT).setAI(4); // scary cat
                    enemies.get(JUMPSCARE_CAT).setAI(1); // jumpscare cat

                    modifiers.get(4).on();
                }
            }
        }

//            case 0 -> pepito.setPepitoAI((byte) enemy.getAI());
//            case 1 -> pepito.setNotPepitoAI((byte) enemy.getAI());
//            case 2 -> glitcher.setAILevel(enemy.getAI());
//            case 3 -> a90.setAILevel(enemy.getAI());
//            case 4 -> msi.setAILevel(enemy.getAI());
//            case 5 -> astarta.setAILevel(enemy.getAI());
//            case 6 -> shark.setAILevel(enemy.getAI());
//            case 7 -> boykisser.setAILevel(enemy.getAI());
//            case 8 -> colaCat.setAILevel(enemy.getAI());
//            case 9 -> mirrorCat.setAILevel(enemy.getAI());
//            case 10 -> maki.setAILevel(enemy.getAI());
//            case 11 -> lemonadeCat.setAILevel(enemy.getAI());
//            case 12 -> wires.setAILevel(enemy.getAI());
//            case 13 -> scaryCat.setAILevel(enemy.getAI());
//            case 14 -> jumpscareCat.setAILevel(enemy.getAI());
//            case 15 -> elAstarta.setAILevel(enemy.getAI());
    }

    public static void setSelectedChallenge(int selectedChallenge) {
        CustomNight.selectedChallenge = selectedChallenge;
    }

    public static int getSelectedChallenge() {
        return selectedChallenge;
    }

    public static String getSelectedChallengeName() {
        if(CustomNight.isCustom())
            return "Custom Night";

        String result = "MSI is watching";
        switch (selectedChallenge) {
            case 0 -> result = "El Astarta";
            case 1 -> result = "Blizzard";
            case 2 -> result = "Time is Ticking";
            case 3 -> result = "The Fog is Coming";
            case 4 -> {
                if(GamePanel.isAprilFools)
                    return "URAANIUM FEVER";

                return "Gruggenheimed";
            }
        }
        return result;
    }

    public static String getSelectedChallengeLocalizeID() {
        if(CustomNight.isCustom())
            return "customNight";

        String result = "msiIsWatchingL";
        switch (selectedChallenge) {
            case 0 -> result = "elAstartaL";
            case 1 -> result = "blizzardL";
            case 2 -> result = "timeIsTickingL";
            case 3 -> result = "fogIsComingL";
            case 4 -> {
                if(GamePanel.isAprilFools)
                    return "uraniumFeverL";

                return "gruggenheimedL";
            }
        }
        return result;
    }
    
    public static int getPoints() {
        int points = 0;
        for (CustomNightEnemy enemy : enemies) {
            points += 100 * enemy.getAI();
        }
        int multiplier = 1;
        for (CustomNightModifier modifier : modifiers) {
            if (modifier.isActive()) {
                multiplier++;
            }
        }
        points *= multiplier;
        
        return points;
    }

    public static boolean isStartSelected() {
        return startSelected;
    }

    public static boolean isBackSelected() {
        return backSelected;
    }

    public static List<CustomNightEnemy> getEnemies() {
        return enemies;
    }

    public static List<CustomNightModifier> getModifiers() {
        return modifiers;
    }


    
    public static int PEPITO = 0;
    public static int NOTPEPITO = 1;
    public static int GLITCHER = 2;
    public static int A90 = 3;
    public static int MSI = 4;
    public static int ASTARTA = 5;
    public static int SHARK = 6;
    public static int BOYKISSER = 7;
    public static int COLACAT = 8;
    public static int MIRRORCAT = 9;
    public static int MAKI = 10;
    public static int LEMONADE_CAT = 11;
    public static int WIRES = 12;
    public static int SCARY_CAT = 13;
    public static int JUMPSCARE_CAT = 14;
    public static int EL_ASTARTA = 15;
    public static int DEEP_SEA_CREATURE = 16;
    public static int SHOCK = 17;
}
