package game.items;

public enum ItemTag {
    RIFT((byte) 0),
    CONFLICTS((byte) 1),
    PASSIVE((byte) 2),
    TRIGGER((byte) 3),
    EXPEND((byte) 4),
    SPECIAL((byte) 5);

    final byte order;

    ItemTag(byte order) {
         this.order = order;
    }

    public byte getOrder() {
        return order;
    }
}
