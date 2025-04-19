package enemies;

import game.bingo.BingoHandler;
import game.bingo.BingoTask;
import main.GamePanel;
import utils.GameEvent;
import utils.RepeatingPepitimer;

public class Shark extends Enemy {
    public short floodStartSeconds = (short) ((Math.random() * 170 + 100));
    public byte arrivalSeconds = (byte) ((Math.random() * 6 + 5) / modifier);
    public byte floodDuration = 0;
    public boolean active = false;
    public boolean biting = false;
    public short biteAnimation = 640;

    public int counterFloat = 0;

    short x = 0;

    public Shark(GamePanel panel) {
        super(panel);
    }

    public void startFlood() {
        if(g.getNight().getEvent().isInGame()) {
            g.getNight().setEvent(GameEvent.FLOOD);
        }
        g.everySecond20th.put("shark", () -> {
            checkForFloodChanges();
            counterFloat++;
        });

        g.getNight().addEventPercent(0.2F);

        arrivalSeconds = (byte) ((Math.random() * 6 + 5) / modifier);
        floodDuration = (byte) (55 + Math.random() * 6);

        g.sound.play("waterLoop", 0.1, true);

        g.getNight().getPepito().scare();
        g.getNight().getAstarta().leaveEarly();
        g.getNight().getMaki().scare();
    }

    public void spawn() {
        leftBeforeBite = 2;
        active = true;

        g.getNight().addEventPercent(0.05F);

        x = (short) Math.round((-g.offsetX + 640) + Math.random() * 320);

        RepeatingPepitimer[] timer = new RepeatingPepitimer[1];

        timer[0] = new RepeatingPepitimer(() -> {
            leftBeforeBite--;

            if(leftBeforeBite == 0) {
                arrivalSeconds = (byte) ((Math.random() * 5 + 2) / modifier);
                biting = true;
                g.sound.play("sogMeow", 0.05);

                timer[0].cancel(true);
            }
        }, (int) (1500 / modifier), (int) (1100 / modifier));
    }

    byte leftBeforeBite;

    public void tick() {
        if(AI <= 0)
            return;

        if (g.getNight().getEvent() == GameEvent.FLOOD) {
            if (g.waterSpeed >= 3) {
                if(!active) {
                    if(!floodReceding) {
                        arrivalSeconds--;
                        if (arrivalSeconds == 0) {
                            spawn();
                        }
                    }
                }
                if(arrivalSeconds < -2) {
                    arrivalSeconds = 2;
                }
            }
            floodDuration--;
            if (floodDuration <= 0) {
                floodReceding = true;

                BingoHandler.completeTask(BingoTask.SURVIVE_FLOOD);
            }
        } else if(g.getNight().getEvent().canSpawnEntities()) {
            floodStartSeconds--;
            if (floodStartSeconds == 0) {
                startFlood();
            }
        }
    }

    public void checkForFloodChanges() {
        if(floodReceding) {
            g.getNight().setWetFloor(0.5F);
            
            if(g.currentWaterLevel < 639) {
                g.currentWaterLevel++;
            } else {
                floodReceding = false;

                if(g.getNight().getEvent() == GameEvent.FLOOD) {
                    g.getNight().setEvent(GameEvent.NONE);
                }
                g.everySecond20th.remove("shark");
                g.sound.stop();
                g.resetFlood();
                floodStartSeconds = (short) ((Math.random() * 180 + 70));

                g.getNight().seconds += 10;
                g.getNight().updateClockString();
            }
        } else {
            if (g.currentWaterLevel > 515) {
                g.currentWaterLevel -= (short) (Math.round(Math.random()) + 1);

                if (g.currentWaterLevel > 480) {
                    g.currentWaterPos += 1;
                }
            } else {
                g.currentWaterPos += (short) (g.waterSpeed + (g.fanActive ? 1 : 0) * 4);
            }
            if (g.currentWaterPos > 1480) {
                g.currentWaterPos -= 1480;
            }
        }
    }

    public void checkForBite() {
        if(biting) {
            biteAnimation -= (short) (Math.sqrt(biteAnimation) * 1.5);

            if(biteAnimation <= 220) {
                biteAnimation = 640;
                biting = false;
                active = false;

                g.getNight().getA90().forgive = Math.min(g.getNight().getA90().forgive + 0.005F, 1);

                if((-g.offsetX + 880) <= (x + 240) && x <= (-g.offsetX + 980)) {
                    g.jumpscare("shark", g.getNight().getId());
                }
            }
        }
    }

    public boolean floodReceding = false;

    public short getX() {
        return x;
    }

    public int getLeftBeforeBite() {
        return leftBeforeBite;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public int getArrival() {
        return floodStartSeconds;
    }

    @Override
    public void fullReset() {
        floodDuration = 0;
        floodReceding = true;
        active = false;
    }
}