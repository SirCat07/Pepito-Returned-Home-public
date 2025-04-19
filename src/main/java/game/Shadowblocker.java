package game;

import game.particles.ShadowParticle;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Shadowblocker {
    public Shadowblocker(int state) {
        this.state = (byte) state;
    }

    public byte state; // 0 - isnt here, 1 - selected, 2 - menu enabled, 3 - zooming (i think?), 4 - made particles, 5 - disassemble
    public byte selected = -1;
    public BufferedImage slop;
    public int[] slopInt = new int[2];
    public String slopName;
    public float progress = 2;
    // max(0, progress - 1) = alpha of everything that isnt selected
    // max(0, progress - 0.5) = progress of zoom and movement of the selected element to the center
    public List<ShadowParticle> particles = new ArrayList<>();
}
