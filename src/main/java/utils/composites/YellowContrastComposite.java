package utils.composites;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class YellowContrastComposite implements Composite, CompositeContext {
    public YellowContrastComposite(float brightness, int offset) {
        this.brightness = brightness;
        this.offset = offset;
    }

    protected void checkRaster(Raster r) {
        if (r.getSampleModel().getDataType() != DataBuffer.TYPE_INT) {
            throw new IllegalStateException("Expected integer sample type");
        }
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

            for (x=0; x < width; x++) {
                dstPixels[x] = mixPixel(srcPixels[x]);
            }

            dstOut.setDataElements(0, y, width, 1, dstPixels);
        }
    }

    private int mixPixel(int x) {
        int xb = (x) & 0xFF;
        xb = Math.min(255, Math.max(0, xb + offset));

        int xg = (x >> 8) & 0xFF;
        xg = (int) Math.min(255, Math.max(0, xg + offset) * brightness);

        int xr = (x >> 16) & 0xFF;
        xr = (int) Math.min(255, Math.max(0, xr + offset) * brightness);

        return (xb) | (xg << 8) | (xr << 16) | (255 << 24);
    }
    
    float brightness;
    int offset;


    @Override
    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
        return this;
    }

    @Override
    public void dispose() {

    }
}