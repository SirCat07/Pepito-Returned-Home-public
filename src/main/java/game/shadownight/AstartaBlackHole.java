package game.shadownight;

public class AstartaBlackHole {
    short x = 0;
    short y = 0;
    int lifetimeSeconds = 0;
    float size = 1;
    float goalSize = 1;

    public AstartaBlackHole(int x, int y) {
        this.x = (short) x;
        this.y = (short) y;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public int getLifetimeSeconds() {
        return lifetimeSeconds;
    }

    public void setLifetimeSeconds(int lifetimeSeconds) {
        this.lifetimeSeconds = lifetimeSeconds;
    }

    public void expand() {
        if(size != goalSize)
            return;

        goalSize = 8;
    }
    public void shrink() {
        if(size != goalSize)
            return;

        goalSize = 0;
    }

    public float getSize() {
        return size;
    }
}
