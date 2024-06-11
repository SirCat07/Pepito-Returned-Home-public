package game.bingo;

import game.achievements.AchievementHandler;
import game.achievements.Achievements;
import main.GamePanel;
import utils.Notification;
import utils.Pepitimer;
import utils.RepeatingPepitimer;
import utils.Statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BingoCard {
    BingoTask[] tasks = new BingoTask[16];
    GamePanel g;

    public BingoCard(GamePanel panel) {
        int i = 0;
        while (i < 16) {
            tasks[i] = BingoTask.NONE;
            i++;
        }

        g = panel;

        BingoHandler.addToCards(this);
    }

    public BingoTask[] getTasks() {
        return tasks;
    }

    boolean generating = false;
    boolean generated = false;

    public void generate() {
        generated = false;
        completed = false;
        timeGoing = false;
        failed = false;
        playedOutAnimation = false;
        secondsSpent = 0;

        g.redrawBingoCard();

        final int[] i = {0};

        generating = true;

        tasks = new BingoTask[16];

        List<BingoTask> list = new ArrayList<>();
        list.addAll(BingoTask.random(this, BingoDifficulty.EASY, 4));
        list.addAll(BingoTask.random(this, BingoDifficulty.NORMAL, 7));
        list.addAll(BingoTask.random(this, BingoDifficulty.HARD, 5));

        Collections.shuffle(list);

        RepeatingPepitimer[] timer = new RepeatingPepitimer[1];
        timer[0] = new RepeatingPepitimer(() -> {
            tasks[i[0]] = list.get(i[0]);
            tasks[i[0]].uncomplete();

            i[0]++;

            if(i[0] == 16) {
                timer[0].cancel(false);
                new Pepitimer(() -> {
                    generating = false;
                    generated = true;
                    g.redrawBingoCard();
                }, 110);
            }
        }, 50, 50);
    }

    public void complete(BingoDifficulty difficulty, String string) {
        g.redrawBingoCard();

        new Notification("+ " + string, 2);

        switch (difficulty) {
            case EASY -> g.bingoSound.play("jingleEasy", 0.1);
            case NORMAL -> g.bingoSound.play("jingleNormal", 0.1);
            case HARD -> g.bingoSound.play("jingleHard", 0.1);
            case EXTREME -> {
                g.bingoSound.play("jingleEasy", 0.1);

                new Pepitimer(() -> {
                    g.bingoSound.play("jingleNormal", 0.1);

                    new Pepitimer(() -> {
                        g.bingoSound.play("jingleHard", 0.1);
                    }, 1800);

                }, 1500);
            }
        }
    }

    boolean failed = false;

    public void fail() {
        failed = true;
        timeGoing = false;

        new Notification("You failed the Pepingo!", 2);

        g.bingoSound.play("jingleFail", 0.1);
    }

    public boolean isFailed() {
        return failed;
    }

    public byte getMinutes() {
        return (byte) (secondsSpent / 60);
    }
    public byte getSeconds() {
        return (byte) (secondsSpent % 60);
    }

    public short secondsSpent = 0;

    public boolean playedOutAnimation = false;
    public boolean completed = false;

    public boolean isCompleted() {
        return completed;
    }

    public void complete() {
        completed = true;
        timeGoing = false;
    }


    public void completeAnimation() {
        if(!playedOutAnimation) {
            g.bingoSound.play("jingleFinal", 0.1);
            AchievementHandler.obtain(g, Achievements.BINGUS);
            Statistics.PEPINGO.increment();

            playedOutAnimation = true;
        }
    }

    public boolean isTaskUncompleted(BingoTask task) {
        if(Arrays.asList(tasks).contains(task)) {
            return !task.isCompleted();
        }

        return false;
    }

    public boolean isTimeGoing() {
        return timeGoing;
    }

    public void setTimeGoing(boolean timeGoing) {
        this.timeGoing = timeGoing;
    }

    boolean timeGoing = false;

    public boolean isGenerating() {
        return generating;
    }

    public boolean isGenerated() {
        return generated;
    }
}