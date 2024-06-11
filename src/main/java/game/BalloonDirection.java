package game;

public enum BalloonDirection {
    LEFT((byte) -2),
    LEFT_LOW((byte) -1),
    NONE((byte) 0),
    RIGHT_LOW((byte) 1),
    RIGHT((byte) 2);

    final byte x;

    BalloonDirection(byte x) {
        this.x = x;
    }

    public byte getX() {
        return x;
    }

    static BalloonDirection random() {
        return values()[(int) (Math.round(Math.random() * (values().length - 1)))];
    }
}