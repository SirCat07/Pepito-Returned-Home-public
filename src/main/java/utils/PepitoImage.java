package utils;

import main.GamePanel;

import java.awt.image.BufferedImage;
import java.util.Objects;

public class PepitoImage {
    public BufferedImage image;
    private String path;
    private int sinceRequest = 0;

    public PepitoImage(String path) {
        this.path = path;
    }

    public void tick() {
        if(sinceRequest > 0) {
            sinceRequest--;
        } else if(image != null) {
            image = null;
            StaticLists.loadedPepitoImages.remove(this);
        }
    }

    public BufferedImage request() {
        if(GamePanel.krunlicMode) {
            if(GamePanel.krunlicPhase == 5) {
                if(!Objects.equals(path, "/game/entities/krunlic/krunlicScaryBlack.png")) {
                    if(!path.contains("krEye") && !path.contains("achievements")) {
                        float s = 0.2F;

                        if (Math.random() < s) {
                            setPath("/game/entities/krunlic/krunlicScaryBlack.png");
                            reload();
                        }
                    }
                }
            }
        }
        
        StaticLists.loadedPepitoImages.add(this);
        if(image == null) {
            reload();
        }
        sinceRequest = 6;
        return image;
    }

    public void reload() {
        image = GamePanel.toCompatibleImage(GamePanel.loadImg(path));
    }

    public void setPath(String path) {
        this.path = path;
    }
    // should always be used with a reload


    public String getPath() {
        return path;
    }
}
