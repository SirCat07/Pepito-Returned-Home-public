package game.items;

import java.awt.image.BufferedImage;

public class Expendable extends Triggerable {
    public Expendable(BufferedImage icon, String name, String description, int amount, String id, String keybind) {
        super(icon, name, description, amount, id, keybind);
    }

    int uses = 1;
    
    public void configureAbility(Runnable action, String key, int uses) {
        super.configureAbility(action, key);
        this.uses = uses;
    }
    
    @Override
    public void run() {
        if(uses > 0) {
            action.run();

            uses--;
            if (uses <= 0) {
                this.disable();
            }
        }
    }
}
