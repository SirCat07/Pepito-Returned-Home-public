package enemies;

import game.Door;
import main.GamePanel;
import utils.GameEvent;
import utils.RepeatingPepitimer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElAstarta extends Enemy {
    public ElAstarta(GamePanel panel) {
        super(panel);
    }

    public boolean active = false;
    public boolean isActive() {
        return active;
    }
    public short arrivalSeconds = (short) (((Math.random() * 150 + 50 + 40 / modifier)));


    short shake = 0;
    public short getShake() {
        return shake;
    }

    public boolean kindaActive = false;
    public byte door = 0;
    RepeatingPepitimer actualTimer;
    RepeatingPepitimer timer;
    public int animation = 0;
    public boolean blinker = false;

    List<Integer> newDoors = new ArrayList<>();

    public List<Integer> getNewDoors() {
        return newDoors;
    }

    public void spawn() {
        newDoors.clear();
        shake = 3;
        active = true;
        int[] sequenceCountdown = {43000};
        g.getNight().setEvent(GameEvent.EL_ASTARTA);
        g.music.play("threat", 0.08F);

        g.getNight().addEventPercent(0.2F);

        actualTimer = new RepeatingPepitimer(() -> {
            shake++;

            sequenceCountdown[0] -= (short) (5000 / modifier);
            if(sequenceCountdown[0] <= 0) {
                actualTimer.cancel(false);
                timer.cancel(true);
                kindaActive = false;
                blinker = false;
                animation = 0;
                active = false;
                g.getNight().setEvent(GameEvent.NONE);
                g.fadeOut(255, g.endFade, 2);

                boolean atLeastOneDoorClosed = false;
                for(int newDoor : newDoors) {
                    Door door = g.getNight().getDoors().get(newDoor);
                    if(door.isClosed()) {
                        g.usage--;
                        g.redrawUsage();
                        atLeastOneDoorClosed = true;
                    }
                    g.getNight().doors.remove(newDoor);
                }
                if(atLeastOneDoorClosed) {
                    g.sound.play("doorSlam", 0.08, 0);
                }
                newDoors.clear();
                g.repaintOffice();

                arrivalSeconds = (short) (((Math.random() * 150 + 50 + 40 / modifier)));

                return;
            }
            if(kindaActive)
                return;

            if(sequenceCountdown[0] < 31000) {
                if(Math.random() < 0.5 + (AI / 30F) && newDoors.size() < 10) {
                    int doorIndex = g.getNight().getDoors().size();
                    while (g.getNight().getDoors().containsKey(doorIndex)) {
                        doorIndex++;
                    }

                    int randomX = (int) (Math.random() * 750 + 255);
                    int randomY = (int) (Math.random() * 400);
                    List<Point> poly1 = List.of(new Point(randomX, randomY), new Point(randomX + 153, randomY), new Point(randomX + 153, randomY + 120), new Point(randomX, randomY + 120));
                    Polygon polygon = GamePanel.getPolygon(poly1);

                    for(Door door : g.getNight().getDoors().values()) {
                        if(door.getHitbox().intersects(polygon.getBounds())) {
                            randomX = (int) (Math.random() * 805 + 200);
                            randomY = (int) (Math.random() * 400);
                            poly1 = List.of(new Point(randomX, randomY), new Point(randomX + 153, randomY), new Point(randomX + 153, randomY + 120), new Point(randomX, randomY + 120));
                            polygon = GamePanel.getPolygon(poly1);
                        }
                    }
                    for(Door door : g.getNight().getDoors().values()) {
                        if(door.getHitbox().intersects(polygon.getBounds())) {
                            randomX = (int) (Math.random() * 805 + 200);
                            randomY = (int) (Math.random() * 400);
                            poly1 = List.of(new Point(randomX, randomY), new Point(randomX + 153, randomY), new Point(randomX + 153, randomY + 120), new Point(randomX, randomY + 120));
                            polygon = GamePanel.getPolygon(poly1);
                        }
                    }

                    g.getNight().getDoors().put(doorIndex, new Door(new Point(randomX - 55, randomY + 3), g.door1Img, new Point(randomX, randomY), polygon, new Point(randomX + 25, randomY + 63)));
                    newDoors.add(doorIndex);
                    g.repaintOffice();
                }
            }

            List<Integer> list = new ArrayList<>(g.getNight().getDoors().keySet());
            Collections.shuffle(list);
            door = (byte) (int) (list.get(0));

            animation = 0;
            blinker = false;
            kindaActive = true;

            if (timer != null) {
                timer.cancel(true);
            }
            short[] delay = {(short) (4000 / modifier)};

            timer = new RepeatingPepitimer(() -> {
                delay[0] -= 100;
                if (delay[0] < 1000) {
                    blinker = !blinker;
                }
                if (delay[0] > 0)
                    return;

                if (kindaActive) {
                    if (g.getNight().getDoors().get((int) door).isClosed() || g.getNight().getDoors().get((int) door).getBlockade() > 0) {
                        if (g.getNight().getDoors().get((int) door).isClosed()) {
                            g.sound.play("knock", 0.08);
                        }
                        if (g.getNight().getDoors().get((int) door).getBlockade() > 0) {
                            g.getNight().getDoors().get((int) door).addBlockade(-1);
                            g.sound.play("blockadeHit", 0.2);

                            if (g.getNight().getDoors().get((int) door).getBlockade() == 0) {
                                g.sound.play("blockadeBreak", 0.2);
                                g.repaintOffice();
                            }
                        }

                        if (g.sensor.isEnabled()) {
                            byte random2 = (byte) Math.round(Math.random());
                            if (random2 == 0) {
                                g.console.add("movement detected at door N" + (door + 1));
                            }
                        }
                    } else {
                        g.jumpscare("elAstarta");
                    }
                    resetCounter();
                    kindaActive = false;
                }
            }, 100, 100);

        }, (short) (5000 / modifier), (short) (5000 / modifier));
    }

    public void leaveEarly() {
        try {
            timer.cancel(true);
        } catch (Exception ignored) { }

        try {
            g.sound.play("knock", 0.08);

            if (g.sensor.isEnabled()) {
                byte random2 = (byte) Math.round(Math.random());
                if (random2 == 0) {
                    g.console.add("movement detected at door N" + (door + 1));
                }
            }

            resetCounter();
            kindaActive = false;
        } catch (Exception ignored) { }
    }

    public boolean isKindaActive() {
        return kindaActive;
    }

    public void tick() {
        if(AI <= 0)
            return;

        if(!active) {
            arrivalSeconds--;
            if (arrivalSeconds == 0) {
                spawn();
            }
        }
    }

    public void stopService() {
        shake = 0;

        try {
            timer.cancel(true);
        } catch (Exception ignored) { }
        try {
            actualTimer.cancel(true);
        } catch (Exception ignored) { }
    }

    public void resetCounter() {
        arrivalSeconds = (short) (((Math.random() * 35 + 30) / (modifier * 1.5)) + modifier);
    }
}
