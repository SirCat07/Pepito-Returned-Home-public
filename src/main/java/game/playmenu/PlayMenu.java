package game.playmenu;

import java.util.ArrayList;
import java.util.List;

public class PlayMenu {
    public static int index = 1;
    public static int counter = 0;
    public static float selectOffsetX = 0;
    public static List<PlayMenuElement> list = new ArrayList<>();
    public static boolean movedMouse = false;

    public static List<PlayMenuElement> getList() {
        return list;
    }

    public static int getGoalSelectOffsetX() {
        return Math.max(260, Math.min(PlayMenu.getList().size() * 420 - 680, index * 420));
    }
}
