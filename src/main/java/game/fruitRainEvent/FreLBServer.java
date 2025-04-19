package game.fruitRainEvent;

import java.util.HashMap;
import java.util.UUID;

public class FreLBServer {
    public static boolean connectedToServer = false;
    public static boolean disqualified = false;

    public static HashMap<String, Integer> lastHashedLeaderboard = new HashMap<>();
    
    public static HashMap<String, Integer> retrieveLeaderboard() {
        HashMap<String, Integer> map = lastHashedLeaderboard;
        
        if(connectedToServer) {
            // set map to real map
            // preferrably sort it by count right here
            // we'd have to retrieve UUIDs, then their counts and names
            
            lastHashedLeaderboard = map;
        }
        return map;
    }
    
    public static void sendLeaderboardCount(UUID uuid, int addCount) {
        if(disqualified)
            return;
        
        // adds addCount to uuid lb
    }
    
    public static void sendLeaderboardName(UUID uuid, String string) {
        // sets name to string
    }
}
