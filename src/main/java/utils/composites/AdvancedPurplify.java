package utils.composites;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class AdvancedPurplify implements Composite, CompositeContext {
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
                dstPixels[x] = mixPixel(srcPixels[x]);
            }

            dstOut.setDataElements(0, y, width, 1, dstPixels);
        }
    }

    private static int mixPixel(int x) {
        int xb = (x) & 0xFF;
        int xr = (x >> 16) & 0xFF;
        int xa = (x >> 24) & 0xFF;

        int xr1 = Math.min(255, xr * xr / 160);
        int xb1 = Math.min(255, xb * xb / 70);

        int xr2 = xr / 2;

        xr = (xr1 * 2 + xr2) / 3;
        xb = (xb1 * 2 + xb) / 3;
        
        return (xb) | (0) | (xr << 16) | (xa << 24);
    }


    @Override
    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
        return this;
    }

    @Override
    public void dispose() {

    }

    public static final AdvancedPurplify AdvancedPurplify = new AdvancedPurplify();

}