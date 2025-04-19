package game.particles;

import utils.Vector2D;

public class ShadowParticle {
    public ShadowParticle(int x, int y, int offset) {
        this.x = x;
        this.y = y;
        this.z = (float) (Math.sqrt(Math.sqrt(Math.random())));

        Vector2D center = new Vector2D(540 - offset, 320);
        Vector2D point = new Vector2D(x, y);
        center.subtract(point);
        
        int divisor = 86;
        if(Math.random() < 0.5) {
            divisor = 18;
        }

        vX = (float) (-center.x / divisor + Math.random() * 1.5 - 0.75);
        vY = (float) (-center.y / divisor + Math.random() * 1.5 - 0.75);
        vAlpha = (float) (-Math.random() / 5);
    }
    
    public float y;
    public float x;
    public float z;
    public float alpha = 255;

    public float vY;
    public float vX;
    public float vAlpha;
}
