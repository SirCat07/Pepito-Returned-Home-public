package game.achievements;

import main.GamePanel;

import java.awt.image.BufferedImage;

public enum Achievements {
    BEGINNER(false, "beginner", "normal"),
    ADVANCER(false, "advancer", "normal"),
    CONQUEROR(false, "conqueror", "normal"),
    ALL_NIGHTER(false, "all_nighter", "normal"),
    MILLY(false, "milly", "normal"),
    SHOPPING_SPREE(false, "shopping_spree", "normal"),
    DABLOONS(false, "dabloons", "normal"),
    SURVIVOR(false, "survivor", "normal"),
    IS_IT_POSSIBLE(true, "is_it_possible", "normal"),
    ONE_OF_MANY(false, "one_of_many", "statistics"),
    SKULLCRACKER(false, "skullcracker", "statistics"),
    DARWIN_AWARD(false, "darwin_award", "statistics"),
    KAMIKAZE(true, "kamikaze", "statistics"),
    HOW_DID_WE_GET_HERE(false, "how_did_we_get_here", "normal"),
    WHOLE_LOTTA_POINTS(false, "whole_lotta_points", "statistics"),
    BROKE(false, "broke", "normal"),
    VISIT_PARTY(false, "visit_party", "normal"),
    PREPARTY(false, "preparty", "special"),
    PARTY(true, "party", "special"),
    FROG(false, "frog", "normal"),
    BINGUS(false, "bingo", "normal"),
    RIFT(false, "rift", "special"),
    HALFWAY(false, "halfway", "special"),
    SHADOWNIGHT(true, "shadownight", "special"),
    BASEMENT(false, "basement", "special"),
    BASEMENT_PARTY(true, "basement_party", "special"),
    BASEMENT_100(true, "basement_100", "special"),
//    SHADOWBASEMENT(true, "shadowbasement", "special"),
    HYDROPHOBIA(true, "hydrophobia", "special"),
    EXIT(true, "exit", "special"),
    EL_ASTARTA(false, "el_astarta", "challenge"),
    BLIZZARD(false, "blizzard", "challenge"),
    TIME_IS_TICKING(false, "time_is_ticking", "challenge"),
    THE_FOG_IS_COMING(false, "the_fog_is_coming", "challenge"),
    GRUGGENHEIMED(false, "gruggenheimed", "challenge"),
    PERFECT_STORM(true, "perfect_storm", "challenge");

    private final boolean hidden;
    private final BufferedImage icon;
    public final String category;
    private boolean obtained = false;


    Achievements(boolean hidden, String iconPath, String category) {
        this.hidden = hidden;
        this.category = category;

        this.icon = GamePanel.toCompatibleImage(GamePanel.loadImg("/menu/achievements/" + iconPath + ".png"));
    }


    public boolean isHidden() {
        return hidden;
    }

    public BufferedImage getIcon() {
        return icon;
    }

    public String getCategory() {
        return category;
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
