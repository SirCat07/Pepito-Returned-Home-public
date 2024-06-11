package game;

import java.awt.image.BufferedImage;

public class MillyItem {
    private final Item item;
    private final short price;
    private final BufferedImage icon;

    public MillyItem(Item item, int price, BufferedImage icon) {
        this.item = item;
        this.price = (short) price;
        this.icon = icon;
    }

    public Item getItem() {
        return item;
    }

    public short getPrice() {
        return price;
    }

    public BufferedImage getIcon() {
        return icon;
    }
}
