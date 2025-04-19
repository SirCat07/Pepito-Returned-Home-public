package enemies;

import game.Level;
import game.enviornments.Enviornment;
import main.GamePanel;
import utils.GameEvent;
import utils.Pepitimer;
import utils.Vector2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DeepSeaCreature extends Enemy {
    public short floodStartSeconds = (short) ((Math.random() * 130 + 155 / modifier));
    public byte floodDuration = 0;
    public boolean active = false;

    public int counterFloat = 0;

    public boolean floodReceding = false;
    
    float gunExtend = 0;
    boolean cantShoot = false;
    
    boolean fight = false;
    boolean flash = false;
    
    float x = 0;
    float currentFunction = 0;
    float z = 0; // minimum is 0.4, max is 1.5, attacks at 1.5, basically how many percents you need to scale the image
    float y = 0;
    
    int shake = 0;

    float cursorRotation = 0;
    float cursorZoom = 1;
    

    public DeepSeaCreature(GamePanel panel) {
        super(panel);
    }

    public void startFlood() {
        Enviornment env = g.getNight().env();
        
        z = 0.5F;
        y = 0;
        currentFunction = 0;
        if(Math.random() < 0.5) {
            x = 1480 + env.maxOffset();
        } else {
            x = -400;
        }
        
        if(g.getNight().getEvent().isInGame()) {
            g.getNight().setEvent(GameEvent.DEEP_FLOOD);
        }
        g.everySecond20th.put("deepSeaCreature", () -> {
            checkForFloodChanges();
            counterFloat++;
        });
        g.everyFixedUpdate.put("deepSeaCreature", () -> {
            if(gunExtend > 0.5) {
                gunExtend /= 1 + (0.012F * modifier);

                if(gunExtend < 3.5 / modifier) {
                    gunExtend = 0.5F;
                    g.sound.play("harpoonReload", 0.12);
                    cantShoot = true;
                    
                    new Pepitimer(() -> {
                        gunExtend = 0;
                        cantShoot = false;
                    }, 500);
                }
            }
            if(shake > 0) {
                shake--;
            }

            if(active) {
                int camX = 1080 - env.maxOffset() - g.offsetX;
                float newX = ((119 * x) + camX) / (120);
                float diff = newX - x;

                float minDiff = Math.min(4, 1.2F * modifier);
                if(Math.abs(diff) < minDiff) {
                    if(diff > 0) {
                        diff = minDiff;
                    } else {
                        diff = -minDiff;
                    }
                }
                x += diff;
                
                currentFunction += 0.01F * modifier;

                if(moved) {
                    z += 0.001F * modifier;
                } else {
                    z += 0.002F * modifier;
                }
                moved = false;
                
                float endDiff = Math.abs(camX - x);
                float chance = Math.min(200, endDiff) / 200;
                
                if(z > 1.45 + chance * 0.15) {
                    g.jumpscare("dsc", g.getNight().getId());
                }
            }
        });
        

        g.getNight().addEventPercent(0.2F);
        fight = false;
        
        floodDuration = 90;
//        floodDuration = 10;
        limit = 515;

        timers.add(new Pepitimer(() -> {
            limit = -126;
            g.basementSound.pause(false);
        }, 6600));
//        }, 200));

        timers.add(new Pepitimer(() -> {
            Point rescaledPoint = new Point((int) ((g.keyHandler.pointerPosition.x - g.centerX) / g.widthModifier), (int) ((g.keyHandler.pointerPosition.y - g.centerY) / g.heightModifier));
            recalculateEndVector(rescaledPoint);
            
            fight = true;
            g.music.play("deepSeaCreature", 0.1);

            timers.add(new Pepitimer(this::spawn, 5550));
        }, 20000));
//        }, 200));

        g.sound.play("waterLoop", 0.1, true);

        g.getNight().getPepito().scare();
        g.getNight().getAstarta().leaveEarly();
        g.getNight().getMaki().scare();
    }

    
    public void setAILevel(byte AIlevel) {
        AI = AIlevel;
        modifier = (float) Math.max(AI / 3.9 + 0.6, 1);
        floodStartSeconds = (short) ((Math.random() * 120 + 150 / modifier));
//        System.out.println(getClass().getName() + " got loaded with a modifier of " + modifier);
    }
    
    int endFadeBeforeBlinks = 0;
    public boolean moved = false;

    public void spawn() {
        active = true;

        endFadeBeforeBlinks = g.endFade;
        g.fadeOut(255, g.endFade, 1.5F);
        
        final boolean[] isEveryOtherSecond = {false};
        final boolean[] fadePhase = {false};
        final boolean[] isFirstFade = {true};

        g.everySecond.put("deepSeaCreatureBlinking", () -> {
            isEveryOtherSecond[0] = !isEveryOtherSecond[0];
            if(isEveryOtherSecond[0]) {
                fadePhase[0] = !fadePhase[0];
                if(fadePhase[0]) {
                    if(isFirstFade[0]) {
                        g.fadeOut((int) g.tintAlpha, 245, 1.5F);
                        isFirstFade[0] = false;
                    } else {
                        g.fadeOut(180, 245, 1.5F);
                    }
                } else {
                    g.fadeOut(245, 180, 1.5F);
                }
            }
        });
    }
    
    List<Pepitimer> timers = new ArrayList<>();
    
    public void stopFighting() {
        fight = false;
        g.everySecond20th.remove("deepSeaCreature");
        g.everyFixedUpdate.remove("deepSeaCreature");
        g.everySecond.remove("deepSeaCreatureBlinking");
        for(Pepitimer timer : timers) {
            timer.cancel();
        }
        g.basementSound.resume(false);
        
        g.fadeOut(255, endFadeBeforeBlinks, 1);
        flash = false;
        shake = 0;
        gunExtend = 0;
    }
    
    boolean showSplash = true;

    public boolean showSplash() {
        return showSplash;
    }

    public void attack(Point point) {
        showSplash = false;

        cursorRotation = 9;
        cursorZoom = 2.4F;
        
        int x = (int) (g.offsetX - g.getNight().env().maxOffset() + this.x);
        int y = (int) (540 - Math.sin(currentFunction) * Math.cos(currentFunction / 2) * 320);
        
        g.sound.play("harpoonShoot", 0.16);
        
        Rectangle hitbox = new Rectangle(x + 250 - (int) (333 * z), y - (int) (333 * z), (int) (580 * z), (int) (500 * z));
        
        if(active && hitbox.contains(point)) {
            System.out.println("HIT");
            
            g.sound.play("harpoonSuccess", 0.12);
            z = 0.5F;
            this.x += (float) (Math.random() * 600F - 300);
            this.currentFunction += (float) (Math.random() * Math.PI - Math.PI / 2);

            Level night = g.getNight();
            night.getA90().forgive = Math.min(night.getA90().forgive + 0.01F, 1);
        } else {
            System.out.println("MISS");
        }
    }
    
    Vector2D endVector = new Vector2D(0, 0);
    
    public void recalculateEndVector(Point rescaledPoint) {
        Vector2D end = new Vector2D(rescaledPoint.x, rescaledPoint.y);
        Vector2D start = new Vector2D(540, 209);

        end.subtract(start);
        end.normalize();

        endVector = end;
    }

    public Vector2D getEndVector() {
        return endVector;
    }
    

    public void tick() { 
        if(AI <= 0)
            return;

        if (g.getNight().getEvent() == GameEvent.DEEP_FLOOD) {
            floodDuration--;
            if (floodDuration <= 0) {
                floodReceding = true;
                active = false;

                g.everyFixedUpdate.put("deepSeaCreatureY", () -> {
                    y += 0.4F;
                    y *= 1.012F;
                    if(g.getNight().getEvent() != GameEvent.DEEP_FLOOD) {
                        g.everyFixedUpdate.remove("deepSeaCreatureY");
                    }
                });
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
            g.getNight().setWetFloor(0.6F);
            
            if(g.currentWaterLevel < 639) {
                g.currentWaterLevel += 2;
            } else {
                floodReceding = false;

                if(g.getNight().getEvent() == GameEvent.DEEP_FLOOD) {
                    g.getNight().setEvent(GameEvent.NONE);
                }
                stopFighting();
                g.sound.stop();
                g.resetFlood();
                floodStartSeconds = (short) ((Math.random() * 120 + 150 / modifier));


                g.getNight().seconds += 10;
                g.getNight().updateClockString();
            }
        } else {
            if (g.currentWaterLevel > limit) {
                g.currentWaterLevel -= (short) (Math.round(Math.random() * 2) + 1);

                if (g.currentWaterLevel > 480) {
                    g.currentWaterPos += 1;
                }
                if (limit == -126) {
                    g.currentWaterPos += 3;
                    g.currentWaterLevel -= (short) (Math.round(Math.random()));
                }
            } else {
                g.currentWaterPos += (short) (g.waterSpeed + (g.fanActive ? 1 : 0) * 4);
            }
            if (g.currentWaterPos > 1480) {
                g.currentWaterPos -= 1480;
            }
        }
    }
    
    public int limit = 515;
    
    public boolean isActive() {
        return active;
    }

    public boolean isFight() {
        return fight;
    }

    public int getGunExtend() {
        return (int) gunExtend;
    }
    public void setGunExtend(int gunExtend) {
        this.gunExtend = gunExtend;
    }

    public void setFlash(boolean flash) {
        this.flash = flash;
    }
    public boolean isFlash() {
        return flash;
    }

    public int getShake() {
        return shake;
    }

    public void setShake(int shake) {
        this.shake = shake;
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public float getCurrentFunction() {
        return currentFunction;
    }

    public float getCursorRotation() {
        return cursorRotation;
    }

    public void setCursorRotation(float rotation) {
        this.cursorRotation = rotation;
    }

    public float getCursorZoom() {
        return cursorZoom;
    }

    public void setCursorZoom(float cursorZoom) {
        this.cursorZoom = cursorZoom;
    }

    public boolean cantShoot() {
        return cantShoot;
    }

    public float getY() {
        return y;
    }
    

    @Override
    public int getArrival() {
        return floodStartSeconds;
    }

    @Override
    public void fullReset() {
        for(Pepitimer timer : timers) {
            timer.cancel();
        }
        floodDuration = 0;
        floodReceding = true;
        active = false;
    }
}
