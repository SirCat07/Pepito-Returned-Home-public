package utils;

public enum GameEvent {
    NONE(true, true, true, true, true, true, true, false),
    FLOOD(false, true, true, true, false, false, false, true),
    LEMONADE(false, true, true, true, false, false, false, false),
    A120(true, true, true, true, false, false, false, false),
    EL_ASTARTA(true, true, true, true, false, false, true, false),
    KIJI(true, true, true, true, false, false, false, false),
    MAXWELL(true, true, true, true, false, false, false, false),
    BASEMENT_KEY(true, true, true, true, false, false, false, false),
    ASTARTA(true, true, true, true, false, false, true, false),
    DEEP_FLOOD(false, true, true, true, false, false, false, true),
    MILLY_ARRIVES_BASEMENT(true, true, true, true, false, false, false, false),
    VENT_OFF_BASEMENT(true, true, true, true, false, false, false, false),
    MR_MAZE(true, true, true, true, false, false, false, false),
    ENDING_BASEMENT(true, true, true, true, false, false, true, false),
    DYING(false, false, false, false, false, false, false, false),
    WINNING(false, false, false, false, false, false, false, false);

    private final boolean gui;
    private final boolean items;
    private final boolean sensor;
    private final boolean isInGame;
    private final boolean spawnEntities;
    private final boolean timeTicks;
    private final boolean energy;
    private final boolean flood;

    GameEvent(boolean gui, boolean items, boolean sensor, boolean isInGame, boolean spawnEntities, boolean timeTicks, boolean energy, boolean flood) {
        this.gui = gui;
        this.items = items;
        this.sensor = sensor;
        this.isInGame = isInGame;
        this.spawnEntities = spawnEntities;
        this.timeTicks = timeTicks;
        this.energy = energy;
        this.flood = flood;
    }

    public boolean isGuiEnabled() {
        return gui;
    }

    public boolean canUseItems() {
        return items;
    }
    public boolean isUsingSensor() {
        return sensor;
    }
    public boolean isInGame() {
        return isInGame;
    }
    public boolean canSpawnEntities() {
        return spawnEntities;
    }

    public boolean canTimeTick() {
        return timeTicks;
    }

    public boolean canProcessEnergy() {
        return energy;
    }

    public boolean isFlood() {
        return flood;
    }
}
