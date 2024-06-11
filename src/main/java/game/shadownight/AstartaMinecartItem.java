package game.shadownight;

public enum AstartaMinecartItem {
    NONE(false),
    MSI(false),
    SCARYCAT(false),
    WIRES(false),
    MIRRORCAT(false),
    SODA(true),
    MINISODA(true),
    SOUP(true),
    MISTER(true);

    final private boolean isBuff;

    AstartaMinecartItem(boolean isBuff) {
        this.isBuff = isBuff;
    }

    public boolean isBuff() {
        return isBuff;
    }
}
