package game.particles;

public class WaterParticle {
    public float rotation;
    public float alpha = 1;
    
    public int x;
    public int y;
    
    public WaterParticle(int x, int y) {
        this.x = x;
        this.y = y;
        rotation = (float) (Math.random() * 6.28F);
    }
}
