package utils;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public class StaticLists {
    public static LinkedList<Pepitimer> timers = new LinkedList<>();
    public static LinkedList<Notification> notifs = new LinkedList<>();
    public static LinkedList<AchievementNotification> achievementNotifs = new LinkedList<>();
    public static Set<PepitoImage> loadedPepitoImages = new LinkedHashSet<>();
}
