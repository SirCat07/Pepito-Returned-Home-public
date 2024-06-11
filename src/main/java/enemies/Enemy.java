package enemies;

import main.GamePanel;

public class Enemy {
    byte AI = 0;
    float modifier = (float) Math.max(AI / 3.9 + 0.6, 1);

    GamePanel g;
    public Enemy(GamePanel panel) {
        this.g = panel;
    }
    
    public boolean isEnabled() {
        return AI > 0;
    }

    public void setAILevel(byte AIlevel) {
        AI = AIlevel;
        modifier = (float) Math.max(AI / 3.9 + 0.6, 1);
//        System.out.println(getClass().getName() + " got loaded with a modifier of " + modifier);
    }

    public void setAILevel(int AIlevel) {
        AI = (byte) AIlevel;
        modifier = (float) Math.max(AI / 3.9 + 0.6, 1);
//        System.out.println(getClass().getName() + " got loaded with a modifier of " + modifier);
    }

    public int getAILevel() {
        return AI;
    }
}
