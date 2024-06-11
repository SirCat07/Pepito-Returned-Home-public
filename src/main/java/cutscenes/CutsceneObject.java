package cutscenes;

import main.GamePanel;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class CutsceneObject {
    public int x;
    public int y;
    public int width;
    public int height;

    private Runnable recalculation;
    private List<Integer> scenes = new ArrayList<>();
    private final CutsceneObjectType type;

    public int z = 0;

    public CutsceneObject(int x, int y, int width, int height, String imagePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.imagePath = imagePath;
        type = CutsceneObjectType.IMAGE;
    }
    public CutsceneObject(CutscenePolygon polygon) {
        this.polygon = polygon;
        type = CutsceneObjectType.POLYGON;
    }

    public CutsceneObject addScene(int scene) {
        this.scenes.add(scene);
        return this;
    }
    public CutsceneObject addScenes(List<Integer> scenes) {
        this.scenes = scenes;
        return this;
    }


    public void setZOrder(int z) {
        this.z = z;
    }

    public String imagePath;
    public BufferedImage image = null;
    public BufferedImage sourceImage;
    private boolean loaded = false;
    public boolean isLoaded() {
        if(type == CutsceneObjectType.POLYGON)
            return true;

        return loaded;
    }
    void load() {
        if(type != CutsceneObjectType.IMAGE)
            return;

        image = GamePanel.loadImg(imagePath);
        if(imageInstructions != null) {
            imageInstructions.run();
        }
        sourceImage = image;
        loaded = true;
    }
    void unload() {
        image = null;
        sourceImage = null;
        loaded = false;
    }

    Runnable imageInstructions;
    public void setImageInstructions(Runnable runnable) {
        imageInstructions = runnable;
    }

    public void setRecalculationStrat(Runnable runnable) {
        this.recalculation = runnable;
    }

    public void recalculate() {
        if(recalculation != null) {
            recalculation.run();
        }
    }

    public boolean shouldBePresent(int scene) {
        return scenes.contains(scene);
    }

    public BufferedImage getImage() {
        return image;
    }

    public CutscenePolygon polygon;
    public CutscenePolygon getPolygon() {
        return polygon;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public CutsceneObjectType getType() {
        return type;
    }
}