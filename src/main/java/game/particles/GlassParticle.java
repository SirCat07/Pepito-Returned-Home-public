package game.particles;

public class GlassParticle {
    public GlassParticle(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public float vY = (float) (Math.random() * -20F);
    public float y;
    public float g = 9.8F;

    public float vX = 0;
    public float x;
    public float pX = (float) ((Math.round(Math.random()) * 50 - 25) * (Math.random() * 3 - 0.66));
}
