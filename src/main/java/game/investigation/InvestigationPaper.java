package game.investigation;

import utils.PepitoImage;

public class InvestigationPaper {
    public String id;
    public String languageId;
    public int x;
    public int y;
    public PepitoImage image;
    
    public int rotation;
    
    public boolean unlocked;
    private Runnable checkForUnlock;
    
    public InvestigationPaper(String id, int x, int y, Runnable checkForUnlock) {
        this.id = id;
        languageId = id;
        this.x = x;
        this.y = y;
        this.checkForUnlock = checkForUnlock;

        rotation = (int) (Math.random() * 16 - 8);
        checkForUnlock();

        String path = "/menu/investigation/papers/";
        path += unlocked ? "done/" : "undone/";
        path += id + ".png";
        
        image = new PepitoImage(path);
    }
    
    public void checkForUnlock() {
        boolean unlockedBefore = unlocked;
        checkForUnlock.run();
        
        if(unlockedBefore != unlocked) {
            String path = "/menu/investigation/papers/";
            path += unlocked ? "done/" : "undone/";
            path += id + ".png";

            image.setPath(path);
            image.reload();
        }
    }
    
    public void setCheckForUnlock(Runnable checkForUnlock) {
        this.checkForUnlock = checkForUnlock;
        checkForUnlock();
    }

    public boolean isUnlocked() {
        return unlocked;
    }
}
