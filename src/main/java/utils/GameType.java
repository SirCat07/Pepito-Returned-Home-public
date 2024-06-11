package utils;

public enum GameType {
    CLASSIC(0, 300, false, false, "classic"),
    ENDLESS_NIGHT(0, 300, false, true, "endless"),
    SHADOW(-200, 400, false, false, "shadownight"),
    DAY(-110, 60, false, true, "endless"),
    PREPARTY(-15, 300, true, false, "preparty"),
    PARTY(-15, 300, true, false, "party"),
    CUSTOM(0, 300, false, false, "custom");

    final int seconds;
    final int duration;
    final boolean party;
    final boolean endless;
    final String discordID;

    GameType(int seconds, int duration, boolean party, boolean endless, String discordID) {
        this.seconds = seconds;
        this.duration = duration;
        this.party = party;
        this.endless = endless;
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

    public String getDiscordID() {
        return discordID;
    }
}
