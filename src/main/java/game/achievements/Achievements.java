package game.achievements;

import main.GamePanel;

import java.awt.image.BufferedImage;

public enum Achievements {
    BEGINNER(false, "beginner"),
    ADVANCER(false, "advancer"),
    CONQUEROR(false, "conqueror"),
    ALL_NIGHTER(false, "all_nighter"),
    MILLY(false, "milly"),
    SHOPPING_SPREE(false, "shopping_spree"),
    DABLOONS(false, "dabloons"),
    SURVIVOR(false, "survivor"),
    IS_IT_POSSIBLE(true, "is_it_possible"),
    ONE_OF_MANY(false, "one_of_many"),
    SKULLCRACKER(false, "skullcracker"),
    DARWIN_AWARD(false, "darwin_award"),
    KAMIKAZE(true, "kamikaze"),
    WHOLE_LOTTA_POINTS(false, "whole_lotta_points"),
    BROKE(false, "broke"),
    PREPARTY(false, "preparty"),
    PARTY(true, "party"),
    FROG(false, "frog"),
    BINGUS(false, "bingo"),
    RIFT(false, "rift"),
    HALFWAY(false, "halfway"),
    SHADOWNIGHT(true, "shadownight"),
    EL_ASTARTA(false, "el_astarta"),
    BLIZZARD(false, "blizzard"),
    TIME_IS_TICKING(false, "time_is_ticking"),
    THE_FOG_IS_COMING(false, "the_fog_is_coming"),
    GRUGGENHEIMED(false, "gruggenheimed"),
    PERFECT_STORM(true, "perfect_storm");

    private final boolean hidden;
    private final BufferedImage icon;
    private boolean obtained = false;


    Achievements(boolean hidden, String iconPath) {
        this.hidden = hidden;

        this.icon = GamePanel.toCompatibleImage(GamePanel.loadImg("/menu/achievements/" + iconPath + ".png"));
    }


    public boolean isHidden() {
        return hidden;
    }

    public BufferedImage getIcon() {
        return icon;
    }

    public boolean isObtained() {
        return obtained;
    }

    public void obtain() {
        obtained = true;
    }

    public void unobtain() {
        obtained = false;
    }
}
