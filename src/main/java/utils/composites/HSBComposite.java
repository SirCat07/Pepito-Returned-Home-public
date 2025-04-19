package utils.composites;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class HSBComposite implements Composite, CompositeContext {

    public HSBComposite(float hueOffset, float saturationMod, float brightnessMod) {
        this.hueOffset = hueOffset;
        this.saturationMod = saturationMod;
        this.brightnessMod = brightnessMod;
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
            dstIn.getDataElements(0, y, width, 1, dstPixels);
            
            for (x=0; x < width; x++) {
                dstPixels[x] = mixPixel(srcPixels[x]);
            }

            dstOut.setDataElements(0, y, width, 1, dstPixels);
        }
    }

    private int mixPixel(int x) {
        int xb = (x) & 0xFF;
        int xg = (x >> 8) & 0xFF;
        int xr = (x >> 16) & 0xFF;
        int xa = (x >> 24) & 0xFF;
        
        float[] hsb = new float[3];
        Color.RGBtoHSB(xr, xg, xb, hsb);

        Color hsbColor = Color.getHSBColor(hsb[0] + hueOffset, hsb[1] * saturationMod, hsb[2] * brightnessMod);
        
        return (hsbColor.getBlue()) | (hsbColor.getGreen() << 8) | (hsbColor.getRed() << 16) | (xa << 24);
    }

    float hueOffset;
    float saturationMod;
    float brightnessMod;

    @Override
    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
        return this;
    }

    @Override
    public void dispose() {

    }
    
}