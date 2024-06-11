package utils;

public enum Statistics {
    WINS("Won games"),
    DEATHS("Died"),
    PLAYTIME("Playtime"),
    ENDLESS("Endless record"),
    PEPINGO("Pepingo cards filled"),
    PEPINGO_TASKS("Pepingo tasks completed"),
    ITEMS_BOUGHT("Items bought from Milly"),
    ITEMS_RIFTED("Items rifted"),
    POINTS_TOTAL("Total points gotten"),
    POINTS_MAX("Highest points night"),
    DIED_TO_PEPITO("Killed by Pépito"),
    DIED_TO_NOTPEPITO("Killed by NotPepito"),
    DIED_TO_UNCANNY("Killed by Uncanny Cat"),
    DIED_TO_MSI("Killed by MSI"),
    DIED_TO_ASTARTA("Killed by Astarta"),
    DIED_TO_SHARK("Killed by Shark"),
    DIED_TO_COLACAT("Killed by Cola Cat"),
    DIED_TO_MAKI("Killed by Maki"),
    DIED_TO_LEMONADE_CAT("Killed by Lemonade Cat"),
    DIED_TO_SCARY_CAT("Killed by Scary Cat"),
    DIED_TO_EL_ASTARTA("Killed by El Astarta"),
    DIED_TO_RADIATION("Killed by Radiation"),
    DIED_TO_SHADOW_PEPITO("Killed by Shadow Pépito"),
    DIED_TO_SHADOW_ASTARTA("Killed by Shadow Astarta"),
    DIED_TO_DREAD("Killed by dread");

    private final String name;
    private int value = 0;


    Statistics(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    public void increment() {
        value++;
    }
}
