package game.dryCat;

public class DryCat {
    float x = -150;
    boolean goLeft;
    float function;
    float size = 1;
    short anchorY;
    boolean door = false;
    
    public DryCat() {
        anchorY = (short) (Math.random() * 400 + 120);
        function = (float) (Math.random() * 6.28F);
        goLeft = Math.round(Math.random()) == 1;
        
        if(goLeft) {
            x = 1230;
        }
    }
    
    public float getX() {
        return x;
    }

    public float getFunction() {
        return function;
    }

    public void process() {
        if(dead) {
            size -= 0.01F;
        } else {
            x += goLeft ? -3F : 3F;
            function += 0.1F;
        }
    }
    
    boolean dead = false;

    public boolean isDead() {
        return dead;
    }

    public short getAnchorY() {
        return anchorY;
    }

    public float getSize() {
        return size;
    }

    public boolean isDoor() {
        return door;
    }
}
