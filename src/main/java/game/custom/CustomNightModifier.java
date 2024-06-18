package game.custom;

public class CustomNightModifier extends CustomNightPrevieweable {
    public CustomNightModifier(String name, String id) {
        this.name = name;
        this.previewPath = "/menu/challenge/modifierPreviews/" + id + ".png";
    }

    boolean active = false;

    public void toggle() {
        active = !active;
    }

    public void on() {
        active = true;
    }
    public void off() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public void set(boolean b) {
        active = b;
    }
}
