package game.enviornments;

import main.GamePanel;

import java.awt.*;
import java.util.List;

public class Office extends Enviornment {
    int bgIndex = 0;
    int maxOffset = 400;
    Rectangle monitor = new Rectangle(236, 329, 196, 90);

    public int getBgIndex() {
        return bgIndex;
    }

    public int maxOffset() {
        return maxOffset;
    }

    public Rectangle getMonitor() {
        return monitor;
    }
    
    public Office() {
        floorClip = GamePanel.getPolygon(List.of(new Point(-2, 640), new Point(173, 621), new Point(173, 625), new Point(198, 625), new Point(198, 618), 
                new Point(215, 616), new Point(561, 616), new Point(561, 625), new Point(586, 625), new Point(586, 616), new Point(1055, 616),
                new Point(1059, 623), new Point(1133, 623), new Point(1136, 616), new Point(1228, 616), new Point(1498, 640)));
        
        floorGeometry = GamePanel.getPolygon(List.of(new Point(0, 641), new Point(214, 616), new Point(1224, 616), new Point(1480, 638)));
        ceilGeometry = GamePanel.getPolygon(List.of(new Point(0, -2), new Point(214, 42), new Point(1224, 42), new Point(1480, 0)));
        
        metalPipe = new Rectangle(654, 586, 304, 42);
        sensor = new Rectangle(15, 430, 213, 210);
        flashlight = new Rectangle(168, 428, 61, 41);
        miniSoda = new Rectangle(480, 540, 70, 90);
        planks = new Rectangle(1015, 395, 165, 120);
        freezePotion = new Rectangle(1130, 150, 130, 130);
        starlightBottle = new Rectangle(615, 500, 130, 130);
        styroPipe = new Rectangle(76, 466, 164, 172);
        weatherStation = new Rectangle(-17, 389, 213, 257);
        megaSoda = new Rectangle(404, 240, 171, 229);
        
        soup = new Rectangle(255, 349, 100, 120);
        mudseal = new Rectangle(519, 418, 78, 50);
        soda = new Rectangle(505, 355, 67, 114);

        maxwells = new Point(815, 565);
        fan = new Point(428, 304);

        generator = new Rectangle(790, 480, 220, 150);
        pipe = new Rectangle(785, 489, 172, 76);
        boop = new Rectangle(885, 270, 20, 20);
    }
}
