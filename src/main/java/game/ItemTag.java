package game;

public enum ItemTag {
    RIFT((byte) 0),
    CONFLICTS((byte) 1);

    final byte order;

    ItemTag(byte order) {
         this.order = order;
    }

    public byte getOrder() {
        return order;
    }
}
