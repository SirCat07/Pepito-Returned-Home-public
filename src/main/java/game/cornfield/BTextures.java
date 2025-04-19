package game.cornfield;

import main.GamePanel;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class BTextures {
    static HashMap<String, BufferedImage> wallImages = new HashMap<>();
    static HashMap<String, BufferedImage[][]> wallImagesArray = new HashMap<>();


    static void initialize(CornField3D corn) {
        wallImages.put("corn", GamePanel.loadImg("/game/cornCell.png"));
        wallImages.put("scaryDoor", GamePanel.loadImg("/game/scaryDoor.png"));

        for(String string : wallImages.keySet()) {
            wallImagesArray.put(string, new BufferedImage[8][wallImages.get(string).getWidth()]);
            
            BufferedImage normalImage = wallImages.get(string);
            BufferedImage silhouette = GamePanel.silhouette(normalImage, corn.shadows);

            for (int i = 0; i < wallImagesArray.get(string)[0].length; i++) {
                BufferedImage slice = wallImages.get(string).getSubimage(i, 0, 1, wallImages.get(string).getHeight());

                for(int interp = 0; interp < 8; interp++) {
                    wallImagesArray.get(string)[interp][i] = GamePanel.mixImages(slice, silhouette.getSubimage(i, 0, 1, wallImages.get(string).getHeight()), (interp / 8F));
                }
            }
        }
    }
}
