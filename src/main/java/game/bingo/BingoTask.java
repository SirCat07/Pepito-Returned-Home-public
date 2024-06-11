package game.bingo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum BingoTask {
    METAL_PIPE_USE(BingoDifficulty.EASY, "use metal pipe"),
    METAL_PIPE_USE_6_TIMES(BingoDifficulty.EASY, "use metal pipe 6 times"),
    BOOP_PEPITO(BingoDifficulty.EASY, "boop Pépito's nose"),
    SURVIVE_ASTARTA(BingoDifficulty.EASY, "survive Astarta"),
    SURVIVE_PEPITO(BingoDifficulty.EASY, "survive Pépito"),
    SURVIVE_MAKI(BingoDifficulty.EASY, "survive Maki"),
    SURVIVE_A90(BingoDifficulty.EASY, "survive a90"),
    SURVIVE_BOYKISSER(BingoDifficulty.EASY, "survive boykisser"),
    ENCOUNTER_RAT(BingoDifficulty.EASY, "encounter Rat"),
    SURVIVE_COLA_CAT(BingoDifficulty.NORMAL, "survive cola cat"),
    BEAT_NIGHT_1(BingoDifficulty.NORMAL, "beat night 1"),
    SURVIVE_MSI(BingoDifficulty.NORMAL, "survive MSI"),
    SURVIVE_FLOOD(BingoDifficulty.NORMAL, "survive flood"),
    USE_SODA_AT_1_ENERGY(BingoDifficulty.NORMAL, "use soda at 1% energy"),
    FLASH_MSI(BingoDifficulty.NORMAL, "flash MSI"),
    FOUR_USAGE_BARS(BingoDifficulty.NORMAL, "have 4 usage bars"),
    A90_FORGIVE(BingoDifficulty.NORMAL, "get a90's forgival"),
    SEE_NOTPEPITO_CAM(BingoDifficulty.NORMAL, "find NotPepito on camera"),
    BEAT_WITH_LESS_ITEMS(BingoDifficulty.NORMAL, "beat night 1+ with <3 items"),
    FIND_ADBLOCKER(BingoDifficulty.HARD, "find an ad blocker"),
    BEAT_NIGHT_2(BingoDifficulty.HARD, "beat night 2"),
    BEAT_NIGHT_3(BingoDifficulty.HARD, "beat night 3"),
    BEAT_WITHOUT_SOUND(BingoDifficulty.HARD, "beat the game with 0 volume"),
    BEAT_WITHOUT_POWER(BingoDifficulty.HARD, "beat the game with no power"),
    SURVIVE_A90_WITH_MSI(BingoDifficulty.HARD, "survive a90 with MSI"),
    BEAT_WITH_LESS_LESS_ITEMS(BingoDifficulty.HARD, "beat night 1+ with <2 item"),
    BEAT_WITH_LESS_LESS_LESS_ITEMS(BingoDifficulty.HARD, "beat night 1+ with no items"),
    NONE(BingoDifficulty.NONE, "");

    final BingoDifficulty difficulty;
    final String name;
    boolean done = false;

    BingoTask(BingoDifficulty difficulty, String name) {
        this.difficulty = difficulty;
        this.name = name;
    }

    public BingoDifficulty getDifficulty() {
        return difficulty;
    }

    public String getName() {
        return name;
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