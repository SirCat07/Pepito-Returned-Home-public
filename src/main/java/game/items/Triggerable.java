package game.items;

import java.awt.image.BufferedImage;

public class Triggerable extends Item {
    Runnable action;
    String key;
    
    public Triggerable(BufferedImage icon, String name, String description, int amount, String id, String keybind) {
        super(icon, name, description, amount, id, keybind);
    }
    
    public void configureAbility(Runnable action, String key) {
        this.action = action;
        this.key = key;
    }

    public Runnable getAction() {
        return action;
    }
    
    public void run() {
        action.run();
    }

    public String getKey() {
        return key;
    }
}
