package game;

import enemies.Rat;
import game.bingo.BingoHandler;
import game.bingo.BingoTask;
import utils.Pepitimer;

import java.util.ArrayList;
import java.util.List;

public class SensorConsole {
    public List<String> list = new ArrayList<>();

    public byte timer = 3;
    public boolean isRatting = false;
    public boolean power = true;

    public void add(String string) {
        if(!isRatting) {
            if(power) {
                list.add(string);
            } else {
                int b = (int) Math.round(Math.random());

                if(b == 0) {
                    list.add("No connection! Please connect to your network.");
                    timer = 2;
                }
            }
        }
        timer++;
    }

    public void add(String string, int times) {
        for(int i = 0; i < times; i++) {
            add(string);
        }
    }

    public void removeLast() {
        if(!list.isEmpty() && !isRatting) {
            list.remove(0);
        }
    }

    public void clear() {
        isRatting = false;
        list.clear();
    }

    Rat noway = new Rat();

    public void rat() {
        isRatting = true;

        BingoHandler.completeTask(BingoTask.ENCOUNTER_RAT);

        new Pepitimer(() -> {
            list.add(noway.a);
            new Pepitimer(() -> {
                list.add(noway.b);
                new Pepitimer(() -> {
                    list.add(noway.c);
                    new Pepitimer(() -> {
                        list.add(noway.d);
                        isRatting = false;
                    }, 1000);
                }, 1000);
            }, 1000);
        }, 1000);
    }
}
