package game.shadownight;

public class AstartaUncannyBox {
    int x;
    int y = -350;
    boolean left;

    AstartaUncannyBox() {
        x = (int) (Math.random() * 1080 + 200);
        left = Math.random() < 0.5;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
