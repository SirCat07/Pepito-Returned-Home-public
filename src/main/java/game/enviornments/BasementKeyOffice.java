package game.enviornments;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class BasementKeyOffice extends Enviornment {
    int bgIndex = 4;
    int maxOffset = 3030;
    Rectangle monitor = new Rectangle(236+1280, 329, 196, 90);

    public int getBgIndex() {
        return bgIndex;
    }

    public int maxOffset() {
        return maxOffset;
    }

    public Rectangle getMonitor() {
        return monitor;
    }
    
    float evilDoorPercent = 1;
    boolean hoveringEvilDoor = false;
    BufferedImage canvas;
    boolean hoveringCanvas = false;
    public boolean powerOff = false;

    public float getEvilDoorPercent() {
        return evilDoorPercent;
    }

    public void setEvilDoorPercent(float evilDoorPercent) {
        this.evilDoorPercent = evilDoorPercent;
    }


    public boolean isHoveringEvilDoor() {
        return hoveringEvilDoor;
    }

    public void setHoveringEvilDoor(boolean hoveringEvilDoor) {
        this.hoveringEvilDoor = hoveringEvilDoor;
    }

    public boolean isHoveringCanvas() {
        return hoveringCanvas;
    }

    public void setHoveringCanvas(boolean hoveringCanvas) {
        this.hoveringCanvas = hoveringCanvas;
    }

    public BufferedImage getCanvas() {
        return canvas;
    }

    
    // NORMAL OFFICE IS +1280 X PIXELS OFF
    // DUPLICATE EVERY BUTTON TWICE WITH +3033 PIXELS
    
    public BasementKeyOffice() {
        floorGeometry = GamePanel.getPolygon(List.of(new Point(0, 616), new Point(1064, 616), new Point(1279, 639), new Point(1495, 616),
                new Point(2503, 616), new Point(2763, 639), new Point(3017, 616), new Point(4097, 616), new Point(4110, 618)));
        
        ceilGeometry = GamePanel.getPolygon(List.of(new Point(0, 42), new Point(1064, 42), new Point(1280, 0), new Point(1495, 41),
                new Point(2502, 41), new Point(2760, 0), new Point(3018, 42), new Point(4097, 42), new Point(4110, 40)));
        

        metalPipe = new Rectangle(654+1280, 586, 304, 42);
        sensor = new Rectangle(15+1280, 430, 213, 210);
        flashlight = new Rectangle(168+1280, 428, 61, 41);
        miniSoda = new Rectangle(480+1280, 540, 70, 90);
        planks = new Rectangle(1015+1280, 395, 165, 120);
        freezePotion = new Rectangle(1130+1280, 150, 130, 130);
        starlightBottle = new Rectangle(615+1280, 500, 130, 130);
        styroPipe = new Rectangle(76+1280, 466, 164, 172);
        weatherStation = new Rectangle(-17+1280, 389, 213, 257);
        megaSoda = new Rectangle(404+1280, 240, 171, 229);

        soup = new Rectangle(255+1280, 349, 100, 120);
        mudseal = new Rectangle(519+1280, 418, 78, 50);
        soda = new Rectangle(505+1280, 355, 67, 114);

        maxwells = new Point(815+1280, 565);
        fan = new Point(428+1280, 304);

        generator = new Rectangle(790+1280, 480, 220, 150);
        pipe = new Rectangle(785+1280, 489, 172, 76);
        boop = new Rectangle(885+1280, 270, 20, 20);
        
        canvas = new BufferedImage(278, 174, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = (Graphics2D) canvas.getGraphics();
        graphics2D.drawImage(GamePanel.loadImg("/game/basement/canvas.png"), 0, 0, null);
        graphics2D.dispose();
    }
}
