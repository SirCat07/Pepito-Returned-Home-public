package enemies;

import main.GamePanel;

public class JumpscareCat extends Enemy {
    public JumpscareCat(GamePanel panel) {
        super(panel);
    }

    boolean active = false;
    boolean doFade = false;
    float fade = 1F;
    float zoom = 0.05F;
    int spawns = 0;
    int shake = 0;

    public void spawn() {
        if(!g.getNight().getEvent().isInGame())
            return;
        if(AI <= 0)
            return;

        doFade = false;
        zoom = 0.05F;
        fade = 1F;
        shake = 0;
        spawns++;
        active = true;

        g.getNight().addEventPercent(0.1F);

        g.sound.play("jumpscareCatScare", 0.1);

        g.everyFixedUpdate.put("jumpscareCatZoom", () -> {
           zoom *= 1.2F;
           shake++;
           if(doFade) {
               fade /= 2;
           }

           if(zoom > 2.5F) {
               doFade = true;

               if(zoom > 6F) {
                   stop();
                   shake += 10;
               }
           }
        });
    }

    public void stop() {
        active = false;
        zoom = 0.05F;
        doFade = false;
        fade = 1F;

        g.everyFixedUpdate.remove("jumpscareCatZoom");

        if(spawns > 6) {
            //pashalko
        }
    }
    public boolean isActive() {
        return active;
    }
    public float getZoom() {
        return zoom;
    }

    public boolean isFading() {
        return doFade;
    }

    public float getFade() {
        return fade;
    }

    public int getShake() {
        return shake;
    }

    public void setShake(int shake) {
        this.shake = shake;
    }
}