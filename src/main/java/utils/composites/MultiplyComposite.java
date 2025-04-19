package utils.composites;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class MultiplyComposite implements Composite, CompositeContext {
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
            dstIn.getDataElements(0, y, width, 1, dstPixels);

            for (x=0; x < width; x++) {
                dstPixels[x] = mixPixel(srcPixels[x], dstPixels[x]);
            }

            dstOut.setDataElements(0, y, width, 1, dstPixels);
        }
    }

    private static int mixPixel(int x, int y) {
//        int xa = 255 - ((x >> 24) & 0xFF);
        
        int xb = (x) & 0xFF;
        int yb = (y) & 0xFF;
//        xb = Math.min(255, xb + xa);
        int b = (xb * yb) / 255;

        int xg = (x >> 8) & 0xFF;
        int yg = (y >> 8) & 0xFF;
//        xg = Math.min(255, xg + xa);
        int g = (xg * yg) / 255;

        int xr = (x >> 16) & 0xFF;
        int yr = (y >> 16) & 0xFF;
//        xr = Math.min(255, xr + xa);
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

    public static final MultiplyComposite Multiply = new MultiplyComposite();

}