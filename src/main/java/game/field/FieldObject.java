package game.field;

import main.GamePanel;

import java.awt.image.BufferedImage;

public class FieldObject {
    public int width;
    public int height;
    public int tableSize;
    public BufferedImage[] table;
    
    public FieldObject(int tableSize, BufferedImage trueImage, int width, int height, int size) {
        this.width = width * size;
        this.height = height * size;

        table = new BufferedImage[tableSize];
        BufferedImage silhouette = GamePanel.silhouette(trueImage, Field.FogColor);
        this.tableSize = tableSize - 1;
        
        for(int i = 0; i < tableSize; i++) {
            table[i] = GamePanel.mixImages(trueImage, silhouette, (i / (float) (tableSize)));
        }
    }
}
