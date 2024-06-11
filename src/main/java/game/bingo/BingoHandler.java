package game.bingo;

import utils.Statistics;

import java.util.ArrayList;
import java.util.List;

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
                            card.complete(task.getDifficulty(), task.getName());
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