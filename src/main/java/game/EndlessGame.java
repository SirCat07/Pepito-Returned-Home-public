package game;

public class EndlessGame {
    public byte getNight() {
        return night;
    }
    public void setNight(byte night) {
        this.night = night;
    }
    public void nextNight() {
        night++;
    }

    public int getCoins() {
        return coins;
    }

    public void addCoins(int coins) {
        this.coins += coins;
    }

    byte night = 1;
    int coins = 0;
}
