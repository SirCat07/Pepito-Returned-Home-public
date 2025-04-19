package game.particles;

import main.GamePanel;
import utils.PepitoImage;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Snowflake {
    float x;
    float y = -180;
    float z;
    boolean type;
    short startingPhase;
    float rotation;
    float vRotation;
    float vX;
    float vY;
    BufferedImage source;
    
    public void fall() {
        x -= vX;
        y += vY;
        rotation += vRotation;
    }

    public void fallSlow() {
        x -= vX / 2;
        y += vY / 2;
        rotation += vRotation / 2;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public Snowflake(short startingPhase, PepitoImage source) {
        vX = (float) (Math.random() * 5 - 2.5F);
        vY = (float) (Math.random() * 3 + 4);

        x = (short) (Math.random() * 2200 - 360);
        y += (short) (Math.random() * 100);
        z = (float) (1 - (Math.max(0.3F, Math.sqrt(Math.random())) / 3F));
        
        type = Math.random() < 0.6;
        this.startingPhase = startingPhase;


        if(!type) {
            int size = (int) (180 * (1 - z));
            this.source = GamePanel.resize(source.request(), size, size, Image.SCALE_FAST);

            if (Math.random() > 0.4) {
                rotation = (float) (Math.random() * Math.PI * 2);
                vRotation = (float) (Math.random() * Math.PI / 16 - Math.PI / 32);
            }
        }
    }

    public float getZ() {
        return z;
    }

    public float getRotation() {
        return rotation;
    }

    public boolean getType() {
        return type;
    }

    public short getStartingPhase() {
        return startingPhase;
    }

    public BufferedImage getSource() {
        return source;
    }
    
}
