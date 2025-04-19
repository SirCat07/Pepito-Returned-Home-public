package enemies;

import main.GamePanel;

public class Enemy {
    byte AI = 0;
    public float modifier = (float) Math.max(AI / 3.9 + 0.6, 1);

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
    
    public int getArrival() {
        System.out.println("NO ARRIVAL OVERRIDE FOR CLASS: " + getClass().getName());
        System.out.println("THIS FUNCTION NEEDS TO ATLEAST RETURN 0 OR BE CHANGED");
        return -1;
    }

    public void fullReset() {
        System.out.println("NO FULL RESET OVERRIDE FOR CLASS: " + getClass().getName());
        System.out.println("THIS FUNCTION NEEDS TO RESET THE ENTITY OR DO NOTHING");
    }
}
