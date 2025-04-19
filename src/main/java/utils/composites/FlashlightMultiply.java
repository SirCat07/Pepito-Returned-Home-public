package utils.composites;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class FlashlightMultiply implements Composite, CompositeContext {
    protected void checkRaster(Raster r) {
        if (r.getSampleModel().getDataType() != DataBuffer.TYPE_INT) {
            throw new IllegalStateException("Expected integer sample type");
        }
    }

    int glow; // 255 - fully bright; 1 - nothing
    int tint; // just the game tintAlpha

    public FlashlightMultiply(int glow, int tint) {
        this.glow = glow;
        this.tint = tint;
    }
    
    @Override
    public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
        checkRaster(src);
        checkRaster(dstIn);
        checkRaster(dstOut);

        int width = Math.min(src.getWidth(), dstIn.getWidth());
        int height = Math.min(src.getHeight(), dstIn.getHeight());
        int x, y;
        int[] srcPixels = new int[width];
        int[] dstPixels = new int[width];

        for (y=0; y < height; y++) {
            src.getDataElements(0, y, width, 1, srcPixels);
            dstIn.getDataElements(0, y, width, 1, dstPixels);

            for (x=0; x < width; x++) {
                dstPixels[x] = mixPixel(srcPixels[x], dstPixels[x]);
            }

            dstOut.setDataElements(0, y, width, 1, dstPixels);
        }
    }

    private int mixPixel(int x, int y) {
        float glow = this.glow / 255F;
        float tint = this.tint / 256F;
        
        int xb = (x) & 0xFF;
        int xg = (x >> 8) & 0xFF;
        int xr = (x >> 16) & 0xFF;
        
        xr = (int) ((xr * glow) * tint + 256 * (1 - tint));
        xg = (int) ((xg * glow) * tint + 256 * (1 - tint));
        xb = (int) ((xb * glow) * tint + 256 * (1 - tint));
        
        int yb = (y) & 0xFF;
        int b = (xb * yb) / 255;

        int yg = (y >> 8) & 0xFF;
        int g = (xg * yg) / 255;

        int yr = (y >> 16) & 0xFF;
        int r = (xr * yr) / 255;

        int ya = (y >> 24) & 0xFF;

        return (b) | (g << 8) | (r << 16) | (ya << 24);
    }


    @Override
    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
        return this;
    }

    @Override
    public void dispose() {

    }

}