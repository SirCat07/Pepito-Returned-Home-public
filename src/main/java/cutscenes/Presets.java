package cutscenes;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Presets {
    public static Cutscene maxwellEnding(GamePanel g) {
        Cutscene cutscene = new Cutscene("maxwell", 540, 320, BufferedImage.TYPE_INT_ARGB);

        CutsceneObject bg1 = new CutsceneObject(0, 0, 540, 320, "/game/cutscenes/maxwellEnding/bg1.png").addScenes(List.of(0, 1, 2));
        CutsceneObject house = new CutsceneObject(350, 145, 108, 77, "/game/cutscenes/maxwellEnding/house.png").addScenes(List.of(0, 1, 2));
        CutsceneObject maxwell = new CutsceneObject(20, 160, 175, 110, "/game/cutscenes/maxwellEnding/maxwell.png").addScene(0);
        maxwell.setRecalculationStrat(() -> maxwell.y = (int) (180 - (cutscene.getMilliseconds() * 0.025)));

        CutsceneObject maxwellTurned = new CutsceneObject(20, 130, 175, 110, "/game/cutscenes/maxwellEnding/maxwellButTurned.png").addScene(1);
        maxwellTurned.setRecalculationStrat(() -> {
            maxwellTurned.x = (int) (20 + cutscene.getMilliseconds() * 0.11);
            maxwellTurned.y = (int) (130 + cutscene.getMilliseconds() * 0.015);
            maxwellTurned.width = (int) (175 / (cutscene.getMilliseconds() * 0.004 + 1));
            maxwellTurned.height = (int) (110 / (cutscene.getMilliseconds() * 0.004 + 1));
        });

        CutsceneObject fire = new CutsceneObject(350, 145, 103, 77, "/game/cutscenes/maxwellEnding/fire.png").addScene(2);
        CutsceneObject smoke = new CutsceneObject(0, 0, 540, 320, "/game/cutscenes/maxwellEnding/explode.png").addScene(2);
        smoke.setRecalculationStrat(() -> {
            smoke.y = (int) (cutscene.getMilliseconds() * 0.02);
            smoke.image = g.alphaify(smoke.sourceImage, 1 - cutscene.getMilliseconds() * 0.0001F);
        });

        Polygon polygon = new Polygon(new int[] {0, 540, 540, 0}, new int[] {0, 0, 320, 320}, 4);

        CutsceneObject bigOrangeThing = new CutsceneObject(new CutscenePolygon(polygon, new Color(255, 150, 20))).addScene(2);
        bigOrangeThing.setRecalculationStrat(() -> {
            if(bigOrangeThing.isLoaded()) {
                float trans = (255 - cutscene.getMilliseconds() * 0.05F);

                if (trans < 0) {
                    cutscene.allObjects.remove(bigOrangeThing);
                    cutscene.visibleObjects.remove(bigOrangeThing);
                } else {
                    bigOrangeThing.getPolygon().setColor(new Color(255, 150, 20, (int) trans));
                }
            }
        });

        CutsceneObject office = new CutsceneObject(-100, 0, 740, 320, "/game/cutscenes/maxwellEnding/house.png").addScene(3);
        office.setImageInstructions(() -> office.image = g.officeImg);

        CutsceneObject flames2 = new CutsceneObject(0, 0, 540, 320, "/game/cutscenes/maxwellEnding/flames2.png").addScene(3);
        flames2.setRecalculationStrat(() -> {
            flames2.y = (int) (Math.cos(cutscene.getMilliseconds() * 0.0025) * 20) + 20;
        });
        CutsceneObject flames1 = new CutsceneObject(0, 0, 540, 320, "/game/cutscenes/maxwellEnding/flames.png").addScene(3);
        flames1.setRecalculationStrat(() -> {
            flames1.y = (int) (Math.sin(cutscene.getMilliseconds() * 0.0025) * 20) + 20;
        });

        CutsceneObject burning = new CutsceneObject(0, 0, 540, 540, "/game/cutscenes/maxwellEnding/burning.png").addScene(4);
        burning.setRecalculationStrat(() -> burning.y = (int) (cutscene.getMilliseconds() * -0.03));

        CutsceneObject ending = new CutsceneObject(0, -160, 540, 480, "/game/cutscenes/maxwellEnding/end.png").addScene(5);
        ending.setRecalculationStrat(() -> {
            ending.y = (int) (Math.min(0, cutscene.getMilliseconds() * 0.02 - 160));
        });
        CutsceneObject pepto = new CutsceneObject(10, 200, 462, 221, "/game/cutscenes/maxwellEnding/pepito.png").addScene(5);
        pepto.setRecalculationStrat(() -> {
            pepto.y = (int) (100 + cutscene.getMilliseconds() * 0.04);
        });

        cutscene.addObjects(List.of(bg1, house, maxwell, maxwellTurned, fire, smoke, bigOrangeThing, office, flames2, flames1, burning, ending, pepto));
        cutscene.recognizeObjects();

        return cutscene;
    }

    public static Cutscene voidEnding(GamePanel g) {
        Cutscene cutscene = new Cutscene("void", 540, 320, BufferedImage.TYPE_INT_ARGB);

        CutsceneObject background = new CutsceneObject(0, -160, 540, 480, "/game/cutscenes/voidEnding/background.png").addScene(0);
        background.setRecalculationStrat(() -> {
            background.y = (int) (Math.max(-160, 320 - cutscene.getMilliseconds() * 0.018));
        });
        CutsceneObject pepto = new CutsceneObject(0, 133, 326, 187, "/game/cutscenes/voidEnding/pepto.png").addScene(0);
        pepto.setRecalculationStrat(() -> {
            pepto.y = (int) (Math.max(133, 133 + 320 - cutscene.getMilliseconds() * 0.012));
        });

        CutsceneObject enemies = new CutsceneObject(0, 0, 540, 320, "/game/cutscenes/voidEnding/enemies.png").addScene(1);
        if(g.shake != 2) {
            enemies.setRecalculationStrat(() -> {
                enemies.x = (int) (Math.random() * 6 - 3);
                enemies.y = (int) (Math.random() * 4 - 2);
            });
        }

        cutscene.addObjects(List.of(background, pepto, enemies));
        cutscene.recognizeObjects();

        return cutscene;
    }
}
