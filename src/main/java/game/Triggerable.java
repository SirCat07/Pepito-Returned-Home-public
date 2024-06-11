package game;

import java.awt.image.BufferedImage;

public class Triggerable extends Item {
    public Triggerable(BufferedImage icon, String name, String description, int amount, String id, int trigger, Runnable action) {
        super(icon, name, description, amount, id);
        this.trigger = trigger;
        this.action = action;
    }

    int trigger;
    Runnable action;

    public int getTrigger() {
        return trigger;
    }
    public void run() {
        action.run();
    }
}
