package game.particles;

public class Raindrop {
    short x;
    short y = -150;
    public void fall() {
        y += (short) (41 + Math.random() * 5);
        x -= (byte) (Math.random() * 3);
    }

    public short getX() {
        return x;
    }

    public short getY() {
        return y;
    }

    public Raindrop() {
        x = (short) (Math.random() * 2200);
        y += (short) (Math.random() * 100);
    }
}
