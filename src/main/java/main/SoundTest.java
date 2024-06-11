package main;

public class SoundTest {
//    public Sound sound;
    public SoundMP3 sound;
    public int currentCode = 0;

//    public float valueWav = -30F;
    public double valueMP3 = 0.02;

    SoundTest(GamePanel panel) {
//        sound = new Sound(panel, "every");
        sound = new SoundMP3(panel, "every");
    }

    public String getCode() {
        return sound.jfxPlayer.soundSet.keySet().stream().toList().get(currentCode);
    }

    public double mathRound(double d) {
        d *= 100;
        d = Math.round(d);
        d /= 100;
        return d;
    }
}
