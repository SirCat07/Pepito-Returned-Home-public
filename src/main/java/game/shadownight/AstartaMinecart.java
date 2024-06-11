package game.shadownight;

import main.GamePanel;

public class AstartaMinecart {
    int x = 0;
    AstartaMinecartItem item = AstartaMinecartItem.NONE;

    public AstartaMinecart(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setItem(AstartaMinecartItem item) {
        this.item = item;
    }

    public AstartaMinecartItem getItem() {
        return item;
    }

    public void move(int x) {
        this.x += x;
    }

    public void spawn(AstartaBoss boss, GamePanel g) {
        switch (item) {
            case MSI -> {
                g.getNight().getMSI().isShadow = false;
                g.getNight().getMSI().spawn();
            }
            case WIRES -> {
                g.getNight().getWires().spawn();
            }
            case SCARYCAT -> {
                g.getNight().getScaryCat().spawn();
            }
            case MIRRORCAT -> {
                g.getNight().getMirrorCat().spawn();
            }
            case SODA -> {
                g.soda.enable();
            }
            case SOUP -> {
                g.soup.enable();
                g.repaintOffice();
            }
            case MINISODA -> {
                g.miniSoda.enable();
                g.repaintOffice();
            }
            case MISTER -> {
                boss.getMister().untilMisterSpawn = 100000;
                boss.getMister().spawn();
            }
        }
        item = AstartaMinecartItem.NONE;
    }

    float shine = 0;

    public float getShine() {
        return shine;
    }
}
