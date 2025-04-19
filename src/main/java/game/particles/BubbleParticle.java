package game.particles;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BubbleParticle {
    int x;
    float y = -180;
    short startingPhase;
    float vY;
    int halfHeight;
    BufferedImage source;
    
    public void floatUp() {
        y -= vY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return (int) y;
    }

    public BubbleParticle(int x, int y, short startingPhase, int range, BufferedImage source) {
        int size = (int) (10 * (3 + Math.random()));

        halfHeight = size / 2;
        vY = (float) (Math.random() * 2 + 3);

        this.x = x + (short) (Math.random() * range - range / 2 - size / 2F);
        this.y = y + (short) (Math.random() * range - range / 2 - size / 2F);
        
        this.startingPhase = startingPhase;
        
        this.source = GamePanel.resize(source, size, size, Image.SCALE_SMOOTH);
    }
    

    public short getStartingPhase() {
        return startingPhase;
    }

    public BufferedImage getSource() {
        return source;
    }

    public int getHalfHeight() {
        return halfHeight;
    }
}
