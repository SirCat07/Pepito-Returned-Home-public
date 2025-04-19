package game;

public class NeonSogBall {
    public float vY = -30F;
    public float h = 320;
    public float g = 9.8F;
    
    public float vX = 0;
    public float x = 0;
    public float pX = (float) ((Math.round(Math.random()) * 60 - 30) * (Math.random() * 3 - 0.66));
}
