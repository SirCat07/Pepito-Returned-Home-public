package game.custom;

import utils.PepitoImage;

public class CustomNightEnemy extends CustomNightPrevieweable {
    PepitoImage icon;
    int AI = 0;
    byte loops = 0;
    float wobbleIntensity = 0;
    byte id;

    public float otherX = -1;
    public float otherY = -1;

    public CustomNightEnemy(String name, String fileName, int id) {
        this.name = name;
        this.id = (byte) id;
        this.icon = new PepitoImage("/menu/challenge/enemyIcons/" + fileName + ".png");
        this.previewPath = "/menu/challenge/enemyPreviews/" + fileName + ".png";
    }

    public void click(boolean hold) {
        if(AI == 0) {
            wobbleIntensity = 12;
        } else {
            wobbleIntensity = 6;
        }

        AI++;
        if(AI > 8) {
            if(!hold && (id == 13 || id == 11) && AI < 10 && (Math.random() < 0.09 || loops >= 4)) {
                // nine....
                return;
            }
            
            AI = 0;
            loops++;
        }
    }

    public void declick() {
        wobbleIntensity = 6;

        if(AI > 0) {
            AI--;
        }
    }

    public PepitoImage getIcon() {
        return icon;
    }

    public float getWobbleIntensity() {
        return wobbleIntensity;
    }

    public int getAI() {
        return AI;
    }

    public void setWobbleIntensity(float wobbleIntensity) {
        this.wobbleIntensity = wobbleIntensity;
    }

    public void setAI(int AI) {
        this.AI = AI;
    }

    public byte getId() {
        return id;
    }
}
