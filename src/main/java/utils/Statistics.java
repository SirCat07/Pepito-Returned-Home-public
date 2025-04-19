package utils;

public enum Statistics {
    WINS(),
    DEATHS(),
    PLAYTIME(),
    ENDLESS(),
    PEPINGO(),
    PEPINGO_TASKS(),
    ITEMS_BOUGHT(),
    ITEMS_RIFTED(),
    POINTS_TOTAL(),
    POINTS_MAX(),
    BALLOONS_POPPED(),
    GOTTEN_BURN_ENDING(),
    GOTTEN_VOID_ENDING(),
    GOTTEN_BASEMENT_ENDING(),
    GOTTEN_CORN_ENDING(),
    DIED_TO_PEPITO(),
    DIED_TO_NOTPEPITO(),
    DIED_TO_UNCANNY(),
    DIED_TO_MSI(),
    DIED_TO_ASTARTA(),
    DIED_TO_SHARK(),
    DIED_TO_COLACAT(),
    DIED_TO_MAKI(),
    DIED_TO_LEMONADE_CAT(),
    DIED_TO_SCARY_CAT(),
    DIED_TO_EL_ASTARTA(),
    DIED_TO_RADIATION(),
    DIED_TO_SHADOW_PEPITO(),
    DIED_TO_SHADOW_ASTARTA(),
    DIED_TO_KIJI(),
    DIED_TO_SHOCK(),
    DIED_TO_DEEP_SEA_CREATURE(),
    DIED_TO_DREAD();
    
    private int value = 0;


    Statistics() {
        
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
