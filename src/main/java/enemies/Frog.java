package enemies;

import main.GamePanel;

import java.util.Locale;

public class Frog {
    public short x = -240;
    public String frogActivation = "";

    GamePanel panel;
    public Frog(GamePanel panel) {
        this.panel = panel;
    }

    public boolean isActive() {
        if(frogActivation.toLowerCase(Locale.ROOT).contains("frog")) {
            if(x == -240) {
                panel.sound.play("banjo", 0.1);

                if(!panel.getNight().getA90().forgiveText.equals("FROGIVE")) {
                    panel.getNight().getA90().forgive = 1;
                    panel.getNight().getA90().forgiveText = "FROGIVE";
                }
            }

            return true;
        }
        return false;
    }


    public void disappear() {
        x = -240;
        frogActivation = "";
    }
}