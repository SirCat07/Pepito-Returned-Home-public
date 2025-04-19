package game.investigation;

import java.util.ArrayList;
import java.util.List;

public class Investigation {
    public static List<InvestigationPaper> list = new ArrayList<>();
    static int progress;
    
    public static void checkForProgress() {
        int newProgress = 0;
        for(InvestigationPaper paper : list) {
            if(paper.isUnlocked()) {
                newProgress++;
            }
        }
        progress = newProgress;
    }

    public static int getProgress() {
        return progress;
    }
    
    public static int getMaxProgress() {
        return list.size();
    }
}
