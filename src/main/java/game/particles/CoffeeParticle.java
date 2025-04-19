package game.particles;

public class CoffeeParticle {
    int x;
    float y;
    short startingPhase;
    float vY;

    public void floatUp() {
        y -= vY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return (int) y;
    }

    public CoffeeParticle(int x, int y, short startingPhase, int range) {
        vY = (float) (Math.random() * 0.05F + 0.2F);

        this.x = x + (short) (Math.random() * range);
        this.y = y;
        
        this.startingPhase = startingPhase;
    }
    

    public short getStartingPhase() {
        return startingPhase;
    }
}
