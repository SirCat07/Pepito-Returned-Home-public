package utils;

public enum GameType {
    CLASSIC(0, 300, false, false, false, "classic"),
    ENDLESS_NIGHT(0, 300, false, true, false, "endless"),
    SHADOW(1000, 600, false, false, false, "shadownight"),
    DAY(650, 102, false, true, false, "endless"),
    PREPARTY(1185, 315, true, false, false, "preparty"),
    PARTY(1185, 315, true, false, false, "party"),
    BASEMENT(1175, 400, true, false, true, "basement"),
    BASEMENT_PARTY(1175, 400, true, false, true, "basement_party"),
    HYDROPHOBIA(0, 400, false, false, false, "hydrophobia"),
    CUSTOM(0, 300, false, false, false, "custom");

    final int seconds;
    final int duration;
    final boolean party;
    final boolean endless;
    final boolean basement;
    final String discordID;

    GameType(int seconds, int duration, boolean party, boolean endless, boolean basement, String discordID) {
        this.seconds = seconds;
        this.duration = duration;
        this.party = party;
        this.endless = endless;
        this.basement = basement;
        this.discordID = discordID;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isParty() {
        return party;
    }
    public boolean isEndless() {
        return endless;
    }

    public boolean isBasement() {
        return basement;
    }

    public String getDiscordID() {
        return discordID;
    }
}
