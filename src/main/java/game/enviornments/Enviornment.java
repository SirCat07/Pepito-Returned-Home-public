package game.enviornments;

import main.GamePanel;

import java.awt.*;
import java.util.List;

public class Enviornment {
    int bgIndex;
    int maxOffset;
    Rectangle monitor;
    Polygon floorClip = GamePanel.getPolygon(List.of(new Point(0, 640), new Point(1, 640), new Point(2, 640)));
    Polygon floorGeometry = GamePanel.getPolygon(List.of(new Point(0, 640), new Point(1, 640), new Point(2, 640)));
    Polygon ceilGeometry = GamePanel.getPolygon(List.of(new Point(0, 0), new Point(1, 0), new Point(2, 0)));

    public int getBgIndex() {
        return bgIndex;
    }

    public int maxOffset() {
        return maxOffset;
    }

    public Rectangle getMonitor() {
        return monitor;
    }

    public Polygon getFloorClip() {
        return floorClip;
    }

    public Polygon getFloorGeometry() {
        return floorGeometry;
    }

    public Polygon getCeilGeometry() {
        return ceilGeometry;
    }

    public Enviornment() {

    }

    public Rectangle metalPipe;
    public Rectangle sensor;
    public Rectangle flashlight;
    public Rectangle miniSoda;
    public Rectangle planks;
    public Rectangle freezePotion;
    public Rectangle starlightBottle;
    public Rectangle styroPipe;
    public Rectangle weatherStation;
    public Rectangle megaSoda;
    
    public Rectangle soup;
    public Point fan;
    public Rectangle mudseal;
    public Rectangle soda;

    public Point maxwells;

    public Rectangle generator;
    public Rectangle pipe;
    public Rectangle boop;
}
