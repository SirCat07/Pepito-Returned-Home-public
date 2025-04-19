package game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class YTVideoButton {
    BufferedImage thumbnail;
    BufferedImage thumbnailSelected;
    String title;
    URL url;

    public YTVideoButton(String title, BufferedImage thumbnail, URL url) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.url = url;
        
        thumbnailSelected = new BufferedImage(260, 165, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = (Graphics2D) thumbnailSelected.getGraphics();
        graphics2D.drawImage(thumbnail, 0, 0, null);
        graphics2D.setColor(new Color(255, 255, 255, 100));
        graphics2D.fillRect(0, 0, 260, 165);
        graphics2D.dispose();
    }

    public BufferedImage getThumbnail() {
        return thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public URL getUrl() {
        return url;
    }

    public BufferedImage getThumbnailSelected() {
        return thumbnailSelected;
    }
}
