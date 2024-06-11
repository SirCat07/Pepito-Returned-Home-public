package game.achievements;

import main.GamePanel;
import utils.AchievementNotification;

public class AchievementHandler {
    public static void obtain(GamePanel g, Achievements achievement) {
        if(achievement.isObtained())
            return;

        achievement.obtain();

        updateAchievementPercentage(g);

        g.redrawAchievements();
        new AchievementNotification(achievement);
    }

    public static void updateAchievementPercentage(GamePanel g) {
        int obtainedTotal = 0;
        for(Achievements ach : Achievements.values()) {
            if(ach.isObtained()) {
                obtainedTotal++;
            }
        }

        float percent = (float) obtainedTotal / Achievements.values().length;
        int percentToRound = Math.round(percent * 1000);

        g.achievementPercentage = percentToRound / 10F;
        g.sound.play("boop", 0.1);
    }
}
