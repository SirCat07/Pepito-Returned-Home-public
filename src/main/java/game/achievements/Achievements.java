package game.achievements;

import main.GamePanel;

import java.awt.image.BufferedImage;

public enum Achievements {
    BEGINNER("Beginner", "Beat Night 1", false, "beginner"),
    ADVANCER("Advancer", "Beat Night 2", false, "advancer"),
    CONQUEROR("Conqueror", "Beat Night 3", false, "conqueror"),
    ALL_NIGHTER("All-nighter with Pépito", "Beat Night 4", false, "all_nighter"),
    MILLY("Ding", "Buy an item from Milly's Shop", false, "milly"),
    SHOPPING_SPREE("Shopping Spree", "Buy every item in Milly's Shop", false, "shopping_spree"),
    DABLOONS("Dabloons", "Get 1000 Dabloons in endless", false, "dabloons"),
    SURVIVOR("Survivor", "Reach Endless Night 8", false, "survivor"),
    IS_IT_POSSIBLE("Is it even possible?", "Reach Endless Night 14", true, "is_it_possible"),
    ONE_OF_MANY("One of Many", "Die for the first time", false, "one_of_many"),
    SKULLCRACKER("Skullcracker", "Die 10 times", false, "skullcracker"),
    DARWIN_AWARD("Darwin Award", "Die 100 times", false, "darwin_award"),
    KAMIKAZE("Kamikaze", "Die 1000 times", true, "kamikaze"),
    WHOLE_LOTTA_POINTS("Whole Lotta Points", "Get 100000 total custom night points", false, "whole_lotta_points"),
    BROKE("Broke", "Complete Night 4 with no items", false, "broke"),
    PREPARTY("Pépito Pre-Partygoer", "Beat Pépito's Pre-Party", false, "preparty"),
    PARTY("Pépito Partygoer", "Beat Pépito's Party (the real one)", true, "party"),
    FROG("nyoom", "type FROG and see what happens", false, "frog"),
    BINGUS("Bingus", "Complete a Pepingo", false, "bingo"),
    RIFT("Swinging right to you", "Rift out an item", false, "rift"),
    HALFWAY("Shadowlurker", "thginwodahS fo tnioP yawflaH eht hcaeR", false, "halfway"),
    SHADOWNIGHT("Was it all a dream?", "thginwodahS taeB", true, "shadownight"),
    EL_ASTARTA("El Astarta", "Beat Challenge 1: El Astarta", false, "el_astarta"),
    BLIZZARD("Blizzard", "Beat Challenge 2: Blizzard", false, "blizzard"),
    TIME_IS_TICKING("Time is Ticking", "Beat Challenge 3: Time is Ticking", false, "time_is_ticking"),
    THE_FOG_IS_COMING("The Fog is Coming", "Beat Challenge 4: The Fog is Coming", false, "the_fog_is_coming"),
    GRUGGENHEIMED("Gruggenheimed", "Beat Challenge 5: Gruggenheimed", false, "gruggenheimed"),
    PERFECT_STORM("Perfect Storm", "Beat a night with Blizzard, Fog and Rain", true, "perfect_storm");

    private final String name;
    private final String description;
    private final boolean hidden;
    private final BufferedImage icon;
    private boolean obtained = false;


    Achievements(String name, String description, boolean hidden, String iconPath) {
        this.name = name;
        this.description = description;
        this.hidden = hidden;

        this.icon = GamePanel.toCompatibleImage(GamePanel.loadImg("/menu/achievements/" + iconPath + ".png"));
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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
