package cutscenes;

import java.awt.*;

public class CutscenePolygon {
    private Polygon polygon;
    private Color color;

    public CutscenePolygon(Polygon polygon, Color color) {
        this.polygon = polygon;
        this.color = color;
    }

    public Polygon getPolygon() {
        return polygon;
    }
    public Color getColor() {
        return color;
    }
    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
