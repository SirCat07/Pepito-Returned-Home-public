package game.dryCat;

import game.particles.WaterParticle;
import main.SoundMP3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DryCatGame {
    public List<DryCat> cats = new ArrayList<>();
    public List<WaterParticle> particles = new ArrayList<>();
    
    public float timer = 43;

    int points = 0;
    int totalCats = 0;
    public int catsLost = 0;
    
    boolean crazy;
    
    boolean doorSpawned = false;
    boolean doorOpen = false;
    
    public float daZoom = 0;
    
    public DryCatGame(boolean crazy) {
        this.crazy = crazy;
    }
    
    public void attack(Point point) {
        for(DryCat dryCat : cats) {
            if(!dryCat.isDoor())
                continue;

            Rectangle hitbox = new Rectangle((int) (dryCat.getX()), dryCat.getAnchorY(), 150, 150);

            if(hitbox.contains(point) && !dryCat.isDead()) {
                dryCat.dead = true;
                points++;
                return;
            }
            break;
        }
        
        
        for(DryCat dryCat : cats) {
            if(dryCat.isDoor())
                continue;
            
            int sin = 120;
            if(crazy) {
                sin = 0;
            }
            
            Rectangle hitbox = new Rectangle((int) (dryCat.getX()), (int) (dryCat.getAnchorY() + Math.sin(dryCat.getFunction()) * sin), 150, 150);
            
            if(hitbox.contains(point) && !dryCat.isDead()) {
                dryCat.dead = true;
                points++;
                break;
            }
        }
    }
    
    
    public void addCat() {
        synchronized (cats) {
            cats.add(new DryCat());
        }
        
        totalCats++;
    }

    public void addDoor() {
        doorSpawned = true;
        
        DryCat door = new DryCat();
        door.door = true;
        cats.add(door);

        totalCats++;
    }
    
    boolean doneEnding = false;
    
    public void ending(SoundMP3 sound) {
        if(!doneEnding) {
            if(isFullWipeout()) {
                cats.clear();
                sound.play("wowYayYippee", 0.3);
            }
            System.out.println("points: " + points);
            System.out.println("total cats: " + totalCats);
            System.out.println("cats lost: " + catsLost);
        }
        doneEnding = true;
    }

    public boolean isDoorOpen() {
        return doorOpen;
    }

    public boolean hasSpawnedDoor() {
        return doorSpawned;
    }

    public boolean isFullWipeout() {
        return catsLost == 0;
    }
    
    public List<DryCat> getCats() {
        return cats;
    }

    public boolean isCrazy() {
        return crazy;
    }
    
    public void openDoor() {
        doorOpen = true;
    }
}

