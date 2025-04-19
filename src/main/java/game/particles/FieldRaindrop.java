package game.particles;

public class FieldRaindrop {
    int x;
    int y = -150;
    int width;
    int height;
    
    public void fall() {
        y += (short) (41 + Math.random() * 5);
        x += (byte) (Math.random() * 8 - 4);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public FieldRaindrop(int offX, int offY) {
        x = (int) (Math.random() * 4320 - 1520 + offX);
        y += (int) (Math.random() * 100 - 640 + offY);
        double z = Math.random() / 3 + 0.33;
        
        width = (int) (5 * z + 1);
        height = (int) (42 * z + 1);
        
        distance = (byte) Math.floor(Math.random() * 2);
    }
    
    byte distance;

    public byte getDistance() {
        return distance;
    }
}
