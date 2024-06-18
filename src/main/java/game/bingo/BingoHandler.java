package game.bingo;

import main.GamePanel;
import utils.Statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BingoHandler {
    static List<BingoCard> cards = new ArrayList<>();

    public static void addToCards(BingoCard card) {
        cards.add(card);
    }

    public static void completeTask(BingoTask task) {
        int howManyCompleted = 0;

        for(BingoCard card : cards) {
            if (!card.isFailed()) {
                for (BingoTask everyTask : card.getTasks()) {
                    if (everyTask.isCompleted()) {
                        howManyCompleted++;
                    } else {
                        if (everyTask == task) {
                            everyTask.complete();
                            card.complete(task.getDifficulty(), GamePanel.getString(task.toString().toLowerCase(Locale.ROOT) + "B"));
                            howManyCompleted++;
                            Statistics.PEPINGO_TASKS.increment();
                        }
                    }
                }
                if (howManyCompleted >= 16) {
                    card.complete();
                }
            }
        }
    }
}