package utils;

public enum GameEvent {
    NONE(true, true, true, true, true, true),
    FLOOD(false, true, true, true, false, false),
    LEMONADE(false, true, true, true, false, false),
    A120(true, true, true, true, false, false),
    EL_ASTARTA(true, true, true, true, false, false),
    MAXWELL(true, true, true, true, false, false),
    ASTARTA(true, true, true, true, false, false),
    DYING(false, false, false, false, false, false),
    WINNING(false, false, false, false, false, false);

    private final boolean gui;
    private final boolean items;
    private final boolean sensor;
    private final boolean isInGame;
    private final boolean spawnEntities;
    private final boolean timeTicks;

    GameEvent(boolean gui, boolean items, boolean sensor, boolean isInGame, boolean spawnEntities, boolean timeTicks) {
        this.gui = gui;
        this.items = items;
        this.sensor = sensor;
        this.isInGame = isInGame;
        this.spawnEntities = spawnEntities;
        this.timeTicks = timeTicks;
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
}
