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
    
    int climate = 2;
    // 0 - blizzard
    // 1 - rain
    // 2 - normal
    // 3 - hot
    
    public int getClimate() {
        return climate;
    }

    public void setClimate(int climate) {
        this.climate = climate;
    }

    byte night = 1;
    int coins = 0;
}
