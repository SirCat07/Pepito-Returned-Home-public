package utils;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;

public class ClipboardUtils {
    private static final Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
    private static final ClipboardOwner owner = new Owner();

    public static void copy(BufferedImage img) {
        c.setContents(new TransferableImage(img), owner);
    }
}
class Owner implements ClipboardOwner {

    @Override
    public void lostOwnership(Clipboard arg0, Transferable arg1) {
        System.out.println( "Lost Clipboard Ownership" );
    }

}
class TransferableImage implements Transferable {

    Image i;

    public TransferableImage(Image i) {
        this.i = i;
    }

    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException {
        if (flavor.equals(DataFlavor.imageFlavor) && i != null ) {
            return i;
        }
        else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public DataFlavor[] getTransferDataFlavors() {
        DataFlavor[] flavors = new DataFlavor[1];
        flavors[0] = DataFlavor.imageFlavor;
        return flavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        DataFlavor[] flavors = getTransferDataFlavors();
        for (DataFlavor dataFlavor : flavors) {
            if (flavor.equals(dataFlavor)) {
                return true;
            }
        }

        return false;
    }
}