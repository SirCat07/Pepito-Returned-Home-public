package utils;

import java.awt.image.BufferedImage;

public class AchievementNotification {
    float counter = 0;
    float hold = 0;
    String name;
    String description;
    BufferedImage icon;

    public AchievementNotification(String name, String description, BufferedImage icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;

        StaticLists.achievementNotifs.add(this);
        holdLimit = 240 * StaticLists.achievementNotifs.size();
    }
    int holdLimit;

    public void progress() {
        if (Math.round(counter) == 31 && hold < holdLimit) {
            hold++;
        } else {
            counter++;
            if (counter >= 62) {
                StaticLists.achievementNotifs.remove(this);
            }
        }
    }

    public float getCounter() {
        return counter;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }


    public BufferedImage getIcon() {
        return icon;
    }

}
