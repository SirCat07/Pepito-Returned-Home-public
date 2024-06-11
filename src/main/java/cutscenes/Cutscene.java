package cutscenes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Cutscene {
    public List<CutsceneObject> allObjects = new ArrayList<>();
    public List<CutsceneObject> visibleObjects = new ArrayList<>();

    public short scene = 0;
    public long milliseconds = 0;
    String id;


    BufferedImage lastRenderedScreen;
    boolean lockRendering = false;

    boolean antiAliasing = false;
    boolean smoothing = false;

    public float quality = 1;

    public void render() {
        if(lockRendering)
            return;
        List<CutsceneObject> unmodifiable = new ArrayList<>(visibleObjects);

        Graphics2D graphics2D = (Graphics2D) lastRenderedScreen.getGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, smoothing ? RenderingHints.VALUE_RENDER_QUALITY : RenderingHints.VALUE_RENDER_SPEED);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAliasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

        graphics2D.setColor(Color.BLACK);
        graphics2D.fillRect(0, 0, 1080, 640);

        for(CutsceneObject object : unmodifiable) {
            object.recalculate();

            if(object.getType() == CutsceneObjectType.IMAGE) {
                int x = (int) (object.getX() * quality);
                int y = (int) (object.getY() * quality);
                int width = (int) (object.getWidth() * quality);
                int height = (int) (object.getHeight() * quality);

                if (x <= lastRenderedScreen.getWidth() && y <= lastRenderedScreen.getHeight()) {
                    if (x + lastRenderedScreen.getWidth() >= 0 && y + lastRenderedScreen.getHeight() >= 0) {
                        graphics2D.drawImage(object.getImage(), x, y, width, height, null);
                    }
                }
            } else if(object.getType() == CutsceneObjectType.POLYGON) {
                graphics2D.setColor(object.getPolygon().getColor());

                Polygon oldPoly = object.getPolygon().getPolygon();

                if(quality == 1) {
                    graphics2D.fillPolygon(oldPoly);
                } else {
                    Polygon newPoly = new Polygon();
                    for (int i = 0; i < oldPoly.npoints; i++) {
                        newPoly.addPoint((int) (oldPoly.xpoints[i] * quality), (int) (oldPoly.ypoints[i] * quality));
                    }

                    graphics2D.fillPolygon(newPoly);
                }
            }
        }

        graphics2D.dispose();
    }

    public void setSmoothing(boolean smoothing) {
        this.smoothing = smoothing;
    }
    public void setAntiAliasing(boolean antiAliasing) {
        this.antiAliasing = antiAliasing;
    }

    public void setQuality(float quality) {
        this.quality = quality;

        int width = lastRenderedScreen.getWidth();
        int height = lastRenderedScreen.getHeight();
        int type = lastRenderedScreen.getType();
        lastRenderedScreen = new BufferedImage((int) (width * quality), (int) (height * quality), type);
    }

    public Cutscene(String id, int width, int height, int type) {
        this.id = id;
        lastRenderedScreen = new BufferedImage(width, height, type);
    }

    public String getID() {
        return id;
    }

    public short getScene() {
        return scene;
    }

    public void recognizeObjects() {
        visibleObjects.clear();

        for(CutsceneObject object : allObjects) {
            if(object.shouldBePresent(scene)) {
                if(!object.isLoaded()) {
                    object.load();
                }
                visibleObjects.add(object);
            } else {
                object.unload();
            }
        }
    }

    public void nextScene() {
        lockRendering = true;

        milliseconds = 0;
        scene++;

        recognizeObjects();

        lockRendering = false;
    }

    public BufferedImage getImage() {
        return lastRenderedScreen;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public void addObject(CutsceneObject object) {
        allObjects.add(object);
    }
    public void addObjects(List<CutsceneObject> objects) {
        allObjects.addAll(objects);
    }

    public float getQuality() {
        return quality;
    }

    public float q() {
        return quality;
    }
}
