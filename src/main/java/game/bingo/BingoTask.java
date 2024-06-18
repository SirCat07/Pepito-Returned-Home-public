package game.bingo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum BingoTask {
    METAL_PIPE_USE(BingoDifficulty.EASY),
    METAL_PIPE_USE_6_TIMES(BingoDifficulty.EASY),
    BOOP_PEPITO(BingoDifficulty.EASY),
    SURVIVE_ASTARTA(BingoDifficulty.EASY),
    SURVIVE_PEPITO(BingoDifficulty.EASY),
    SURVIVE_MAKI(BingoDifficulty.EASY),
    SURVIVE_A90(BingoDifficulty.EASY),
    SURVIVE_BOYKISSER(BingoDifficulty.EASY),
    ENCOUNTER_RAT(BingoDifficulty.EASY),
    SURVIVE_COLA_CAT(BingoDifficulty.NORMAL),
    BEAT_NIGHT_1(BingoDifficulty.NORMAL),
    SURVIVE_MSI(BingoDifficulty.NORMAL),
    SURVIVE_FLOOD(BingoDifficulty.NORMAL),
    USE_SODA_AT_1_ENERGY(BingoDifficulty.NORMAL),
    FLASH_MSI(BingoDifficulty.NORMAL),
    FOUR_USAGE_BARS(BingoDifficulty.NORMAL),
    A90_FORGIVE(BingoDifficulty.NORMAL),
    SEE_NOTPEPITO_CAM(BingoDifficulty.NORMAL),
    BEAT_WITH_LESS_ITEMS(BingoDifficulty.NORMAL),
    FIND_ADBLOCKER(BingoDifficulty.HARD),
    BEAT_NIGHT_2(BingoDifficulty.HARD),
    BEAT_NIGHT_3(BingoDifficulty.HARD),
    BEAT_WITHOUT_SOUND(BingoDifficulty.HARD),
    BEAT_WITHOUT_POWER(BingoDifficulty.HARD),
    SURVIVE_A90_WITH_MSI(BingoDifficulty.HARD),
    BEAT_WITH_LESS_LESS_ITEMS(BingoDifficulty.HARD),
    BEAT_WITH_LESS_LESS_LESS_ITEMS(BingoDifficulty.HARD),
    NONE(BingoDifficulty.NONE);

    final BingoDifficulty difficulty;
    boolean done = false;

    BingoTask(BingoDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public BingoDifficulty getDifficulty() {
        return difficulty;
    }
    

    //    public static BingoTask random(BingoCard card) {
//        List<BingoTask> list = new ArrayList<>(Arrays.asList(values()));
//        list.remove(NONE);
//        list.removeAll(Arrays.asList(card.getTasks()));
//
//        Collections.shuffle(list);
//
//        return list.get(0);
//    }
    public static List<BingoTask> random(BingoCard card, BingoDifficulty difficulty, int howMany) {
        List<BingoTask> newTasks = new ArrayList<>();
        for(BingoTask task : values()) {
            if(task.getDifficulty() == difficulty) {
                newTasks.add(task);
            }
        }

        newTasks.removeAll(Arrays.asList(card.getTasks()));

        Collections.shuffle(newTasks);

        return newTasks.subList(0, howMany);
    }

    public void complete() {
        done = true;
    }

    public void uncomplete() {
        done = false;
    }

    public boolean isCompleted() {
        return done;
    }
}