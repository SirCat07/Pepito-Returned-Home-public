package game.field;

import main.GamePanel;

public class FieldBlimp {
    int x = 0;
    int y = 0;
    float z = 0;
    
    public boolean lockedOn = false;
    public float untilDirects = 5;
    public float playerInterp = 0;
    
    public int untilNextAttack = 15;
    public boolean underTheRadar = false;
    
    public void recalc(GamePanel g) {
        if(lockedOn) {
            if(untilDirects > 0) {
                untilDirects -= 0.016666F;
            } else {
                playerInterp += 0.0045F;
                playerInterp *= 1.0004F;
                
                if(playerInterp > 0.99F) {
                    playerInterp = 1;
                    
                    // KILL DEATH VIOLENCE
                    g.field.kill(g, "fieldBlimp");
                }
            }
        } else {
            playerInterp /= 1.0003F;
            playerInterp -= 0.003F;
            
            if(playerInterp < 0) {
                playerInterp = 0;
            }
        }
        
        
        x = (int) (50000 * Math.cos(g.fixedUpdatesAnim / 400F));
        y = -70000;
        z = 2500 + (int) (100 * Math.sin(g.fixedUpdatesAnim / 400F));
        
        
        Field field = g.field;
        
        x = (int) GamePanel.lerp(x, field.getX(), playerInterp);
        y = (int) GamePanel.lerp(y, -field.getY(), playerInterp);
        z = (float) GamePanel.lerp(z, field.getDistance() + 3, playerInterp);
    }
    
    public void lockOn() {
        lockedOn = true;
        untilDirects = 5;
        playerInterp = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public float getZ() {
        return z;
    }
}
