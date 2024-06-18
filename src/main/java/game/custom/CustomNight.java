package game.custom;

import game.achievements.Achievements;
import main.GamePanel;
import utils.PepitoImage;
import utils.RepeatingPepitimer;

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
    public static byte limboId = -1;

    public static void limbo(GamePanel g) {
        final int[] n = {0};

        limboTimer[0] = new RepeatingPepitimer(() -> {
            for (int i = 0; i < enemies.size(); i++) {
                CustomNightEnemy enemy = enemies.get(i);
                int x = 105 * (i % 6);
                int y = 130 * (i / 6);

                enemy.otherX = x;
                enemy.otherY = y;
                if (Math.random() > 0.5) {
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
                if(limboTimer[0] != null) {
                    limboTimer[0].cancel(false);
                    limboTimer[0] = null;
                    limboId = -1;
                    g.music.play("tension", 0.05, true);
                }
            }
        }, 0, 300);
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
        for (CustomNightEnemy enemy : enemies) {
            if(customEnemyAIs.isEmpty()) {
                enemy.setAI(0);
            } else {
                enemy.setAI(customEnemyAIs.get(enemy));
            }
        }
        for (CustomNightModifier modifier : modifiers) {
            if(customModifiers.isEmpty()) {
                modifier.off();
            } else {
                modifier.set(customModifiers.get(modifier));
            }
        }
        

        if(!CustomNight.isCustom()) {
            switch (selectedChallenge) {
                case 0 -> {
                    enemies.get(0).setAI(5);
                    enemies.get(1).setAI(5);
                    enemies.get(2).setAI(4);
                    enemies.get(3).setAI(3);
                    enemies.get(4).setAI(3);
                    enemies.get(5).setAI(6);
                    enemies.get(6).setAI(2);
                    enemies.get(9).setAI(5);
                    enemies.get(12).setAI(4);
                    enemies.get(14).setAI(1);
                    enemies.get(15).setAI(2);
                }

                case 1 -> {
                    enemies.get(0).setAI(5);
                    enemies.get(1).setAI(5);
                    enemies.get(2).setAI(1);
                    enemies.get(3).setAI(3);
                    enemies.get(4).setAI(4);
                    enemies.get(5).setAI(5);
                    enemies.get(6).setAI(4);
                    enemies.get(8).setAI(1);
                    enemies.get(9).setAI(5);
                    enemies.get(10).setAI(1);
                    enemies.get(11).setAI(1);
                    enemies.get(12).setAI(5);
                    enemies.get(13).setAI(1);
                    enemies.get(14).setAI(1);

                    modifiers.get(0).on();
                    modifiers.get(1).on();
                }

                case 2 -> {
                    enemies.get(0).setAI(6);
                    enemies.get(1).setAI(6);
                    enemies.get(2).setAI(7);
                    enemies.get(3).setAI(3);
                    enemies.get(4).setAI(4);
                    enemies.get(5).setAI(6);
                    enemies.get(6).setAI(5);
                    enemies.get(7).setAI(4);
                    enemies.get(9).setAI(8);
                    enemies.get(11).setAI(1);
                    enemies.get(12).setAI(3);
                    enemies.get(13).setAI(4);
                    enemies.get(14).setAI(1);

                    modifiers.get(2).on();
                }

                case 3 -> {
                    enemies.get(0).setAI(7);
                    enemies.get(1).setAI(7);
                    enemies.get(3).setAI(6);
                    enemies.get(4).setAI(4);
                    enemies.get(5).setAI(6);
                    enemies.get(6).setAI(8);
                    enemies.get(7).setAI(2);
                    enemies.get(8).setAI(1);
                    enemies.get(9).setAI(5);
                    enemies.get(11).setAI(3);
                    enemies.get(12).setAI(3);
                    enemies.get(13).setAI(1);
                    enemies.get(14).setAI(1);

                    modifiers.get(3).on();
                }

                case 4 -> {
                    enemies.get(0).setAI(7);
                    enemies.get(1).setAI(7);
                    enemies.get(2).setAI(7);
                    enemies.get(3).setAI(7);
                    enemies.get(4).setAI(5);
                    enemies.get(5).setAI(6);
                    enemies.get(6).setAI(7);
                    enemies.get(7).setAI(6);
                    enemies.get(8).setAI(1);
                    enemies.get(9).setAI(8);
                    enemies.get(10).setAI(7);
                    enemies.get(11).setAI(6);
                    enemies.get(12).setAI(7);
                    enemies.get(13).setAI(5);
                    enemies.get(14).setAI(1);
                    enemies.get(15).setAI(1);

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
}
