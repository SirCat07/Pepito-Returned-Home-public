package cutscenes;

import main.GamePanel;
import utils.composites.AdditiveComposite;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Presets {
    public static Cutscene maxwellEnding(GamePanel g) {
        Cutscene cutscene = new Cutscene("maxwell", 540, 320, BufferedImage.TYPE_INT_RGB);

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
        office.setImageInstructions(() -> office.image = g.officeImg[0]);

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
        Cutscene cutscene = new Cutscene("void", 540, 320, BufferedImage.TYPE_INT_RGB);

        CutsceneObject background = new CutsceneObject(0, -160, 540, 480, "/game/cutscenes/voidEnding/background.png").addScene(0);
        background.setRecalculationStrat(() -> {
            background.y = (int) (Math.max(-160, 320 - cutscene.getMilliseconds() * 0.019));
        });
        CutsceneObject pepto = new CutsceneObject(0, 133, 326, 187, "/game/cutscenes/voidEnding/pepto.png").addScene(0);
        pepto.setRecalculationStrat(() -> {
            pepto.y = (int) (Math.max(133, 133 + 320 - cutscene.getMilliseconds() * 0.013));
        });

        CutsceneObject enemies = new CutsceneObject(0, 0, 540, 320, "/game/cutscenes/voidEnding/enemies.png").addScene(1);
        if(g.jumpscareShake != 2) {
            enemies.setRecalculationStrat(() -> {
                if(g.screenShake) {
                    enemies.x = (int) (Math.random() * 8 - 4);
                    enemies.y = (int) (Math.random() * 6 - 3);
                }
            });
        }

        cutscene.addObjects(List.of(background, pepto, enemies));
        cutscene.recognizeObjects();

        return cutscene;
    }

    public static Cutscene voidEndingPEPITOLESS(GamePanel g) {
        Cutscene cutscene = new Cutscene("void", 540, 320, BufferedImage.TYPE_INT_RGB);

        CutsceneObject background = new CutsceneObject(0, -160, 540, 480, "/game/cutscenes/voidEnding/background.png").addScene(0);
        background.setRecalculationStrat(() -> {
            background.y = (int) (Math.max(-160, 320 - cutscene.getMilliseconds() * 0.019));
        });
        CutsceneObject pepto = new CutsceneObject(0, 133, 326, 187, "/game/cutscenes/voidEnding/pepitoless.png").addScene(0);
        pepto.setRecalculationStrat(() -> {
            pepto.y = (int) (Math.max(133, 133 + 320 - cutscene.getMilliseconds() * 0.013));
        });

        CutsceneObject enemies = new CutsceneObject(0, 0, 540, 320, "/game/cutscenes/voidEnding/enemies.png").addScene(1);
        if(g.jumpscareShake != 2) {
            enemies.setRecalculationStrat(() -> {
                if(g.screenShake) {
                    enemies.x = (int) (Math.random() * 8 - 4);
                    enemies.y = (int) (Math.random() * 6 - 3);
                }
            });
        }

        cutscene.addObjects(List.of(background, pepto, enemies));
        cutscene.recognizeObjects();

        return cutscene;
    }
    
    public static Cutscene basementPreset(GamePanel g) {
        Cutscene cutscene = new Cutscene("basement", 1080, 640, BufferedImage.TYPE_INT_RGB);

        cutscene.contrastBrightness = 1.5F;
        cutscene.contrastOffset = -15;

        
        CutsceneObject basement = new CutsceneObject(-200, 0, 1480, 640, "/game/cutscenes/basementEnding/outside/sky.png").addScene(0);
        
        basement.setImageInstructions(() -> {
            basement.image = g.fullOffice;
            
            Graphics2D graphics2D = (Graphics2D) basement.image.getGraphics();
            graphics2D.drawImage(g.fullOffice, 0, 0, null);
            
            graphics2D.setColor(new Color(0, 0, 0, 160));
            graphics2D.fillRect(0, 0, 1480, 640);

            graphics2D.drawImage(g.basementLadder.request(), 635, 120, null);
            
            graphics2D.drawImage(g.basementStaticGlow.request(), 0, 0, null);
            graphics2D.drawImage(g.basementBeam.request(), 303, 0, null);
            graphics2D.dispose();
        });
        
        basement.setRecalculationStrat(() -> {
            float millis = cutscene.getMilliseconds() / 12000F;
            
            basement.x = (int) (540 - (-40) - 740 * (millis + 1));
            basement.width = (int) (1480 * (millis + 1));
            basement.height = (int) (640 * (millis + 1));

            cutscene.contrastBrightness = 1.5F + (int) (millis / 20F);
            cutscene.contrastOffset = -15 - (int) (10 * (millis / 40F));
        });

        

        CutsceneObject oSky = new CutsceneObject(0, 0, 1080, 640, "/game/cutscenes/basementEnding/outside/sky.png").addScene(1);
       
        CutsceneObject oBackGround = new CutsceneObject(0, 0, 1080, 640, "/game/cutscenes/basementEnding/outside/back ground.png").addScene(1);
        
        oBackGround.setRecalculationStrat(() -> {
            float millis = cutscene.getMilliseconds() / 1000F;
            oBackGround.y = (int) (millis * 8);
        });
        
        CutsceneObject oFrontGround = new CutsceneObject(0, 0, 1080, 640, "/game/cutscenes/basementEnding/outside/front ground.png").addScene(1);

        oFrontGround.setRecalculationStrat(() -> {
            float millis = cutscene.getMilliseconds() / 1000F;
            oFrontGround.y = (int) (millis * 16);
        });


        CutsceneObject oLadder1 = new CutsceneObject(420, 0, 214, 640, "/game/cutscenes/basementEnding/outside/lader.png").addScene(1);
        oLadder1.setRecalculationStrat(() -> {
            float millis = cutscene.getMilliseconds() / 1000F;
            oLadder1.y = (int) (millis * 320) % 640;
        });
        CutsceneObject oLadder2 = new CutsceneObject(420, -640, 214, 640, "/game/cutscenes/basementEnding/outside/lader.png").addScene(1);
        oLadder2.setRecalculationStrat(() -> {
            float millis = cutscene.getMilliseconds() / 1000F;
            oLadder2.y = (int) (millis * 320) % 640 - 640;
        });



        CutsceneObject mSky = new CutsceneObject(0, 0, 1080, 640, "/game/cutscenes/basementEnding/mountains/sky.png").addScene(2);
     
        CutsceneObject mGround1 = new CutsceneObject(0, 0, 1080, 640, "/game/cutscenes/basementEnding/mountains/ground.png").addScene(2);
        mGround1.setRecalculationStrat(() -> {
            float millis = cutscene.getMilliseconds() / 1000F;
            mGround1.x = (int) (millis * 200 * 3) % 1080;
        });
        CutsceneObject mGround2 = new CutsceneObject(0, 0, 1080, 640, "/game/cutscenes/basementEnding/mountains/ground.png").addScene(2);
        mGround2.setRecalculationStrat(() -> {
            float millis = cutscene.getMilliseconds() / 1000F;
            mGround2.x = (int) (millis * 200 * 3) % 1080 - 1080;
        });

        CutsceneObject mTreesBacker1 = new CutsceneObject(0, 0, 1080, 640, "/game/cutscenes/basementEnding/mountains/trees backer.png").addScene(2);
        mTreesBacker1.setRecalculationStrat(() -> {
            float millis = cutscene.getMilliseconds() / 1000F;
            mTreesBacker1.x = (int) (millis * 240 * 3) % 1080;
        });
        CutsceneObject mTreesBacker2 = new CutsceneObject(0, 0, 1080, 640, "/game/cutscenes/basementEnding/mountains/trees backer.png").addScene(2);
        mTreesBacker2.setRecalculationStrat(() -> {
            float millis = cutscene.getMilliseconds() / 1000F;
            mTreesBacker2.x = (int) (millis * 240 * 3) % 1080 - 1080;
        });

        CutsceneObject mHelicopter = new CutsceneObject(497 - 2160 + 4320, 200, 202, 112, "/game/cutscenes/basementEnding/mountains/mHelicopter.png").addScene(2);
        mHelicopter.setRecalculationStrat(() -> {
            float millis = cutscene.getMilliseconds() / 3000F;
            float skibidi = Math.max(0, millis - 2);

            mHelicopter.x = (int) (497 + 1080 - 2160 * skibidi);
        });

        CutsceneObject mTreesBack1 = new CutsceneObject(0, 0, 1080, 640, "/game/cutscenes/basementEnding/mountains/trees back.png").addScene(2);
        mTreesBack1.setRecalculationStrat(() -> {
            float millis = cutscene.getMilliseconds() / 1000F;
            mTreesBack1.x = (int) (millis * 280 * 3) % 1080;
        });
        CutsceneObject mTreesBack2 = new CutsceneObject(0, 0, 1080, 640, "/game/cutscenes/basementEnding/mountains/trees back.png").addScene(2);
        mTreesBack2.setRecalculationStrat(() -> {
            float millis = cutscene.getMilliseconds() / 1000F;
            mTreesBack2.x = (int) (millis * 280 * 3) % 1080 - 1080;
        });

        CutsceneObject mTreesFront1 = new CutsceneObject(0, 0, 1080, 640, "/game/cutscenes/basementEnding/mountains/trees front.png").addScene(2);
        mTreesFront1.setRecalculationStrat(() -> {
            float millis = cutscene.getMilliseconds() / 1000F;
            mTreesFront1.x = (int) (millis * 320 * 3) % 1080;
        });
        CutsceneObject mTreesFront2 = new CutsceneObject(0, 0, 1080, 640, "/game/cutscenes/basementEnding/mountains/trees front.png").addScene(2);
        mTreesFront2.setRecalculationStrat(() -> {
            float millis = cutscene.getMilliseconds() / 1000F;
            mTreesFront2.x = (int) (millis * 320 * 3) % 1080 - 1080;
        });
        


        CutsceneObject bBackground = new CutsceneObject(-200, 0, 1480, 640, "/game/basement/basementRender.png").addScene(3);

        bBackground.setImageInstructions(() -> {
            Graphics2D graphics2D = (Graphics2D) bBackground.image.getGraphics();
            graphics2D.setColor(new Color(0, 0, 0, 210));
            graphics2D.fillRect(0, 0, 1480, 640);
            graphics2D.dispose();
        });

        CutsceneObject bEvilBeast = new CutsceneObject(337 + 2160, 400, 372, 187, "/game/cutscenes/basementEnding/basement/evilBeast.png").addScene(3);
        bEvilBeast.setRecalculationStrat(() -> {
            bEvilBeast.x = Math.max(337, 337 + (int) (2160 - cutscene.getMilliseconds() / 3F));
        });

        CutsceneObject bMSI = new CutsceneObject(219 - 2160, 326, 119, 181, "/game/cutscenes/basementEnding/basement/msi.png").addScene(3);
        bMSI.setRecalculationStrat(() -> {
            int x = Math.min(219, 219 - (int) (2160 - cutscene.getMilliseconds() / 3.8F));
            if(x > 192) {
                bMSI.x = x;
            }
        });

        CutsceneObject bMSI2 = new CutsceneObject(643 + 2160, 326, 119, 181, "/game/cutscenes/basementEnding/basement/msi.png").addScene(3);
        bMSI2.setImageInstructions(() -> {
            bMSI2.image = GamePanel.mirror(bMSI2.image, 1);
        });
        bMSI2.setRecalculationStrat(() -> {
            int x = Math.max(643, 643 + (int) (2160 - cutscene.getMilliseconds() / 3.9F));
            if(x < 790) {
                bMSI2.x = x;
            }
        });

        CutsceneObject bHole = new CutsceneObject(0, 0, 1080, 640, "/game/cutscenes/basementEnding/basement/eveningHole.png").addScene(3);
        
        CutsceneObject bBeam = new CutsceneObject(0, 0, 1080, 640, "/game/cutscenes/basementEnding/basement/eveningBeam.png").setLayeringOption(AdditiveComposite.Add).addScene(3);



        CutsceneObject eSky = new CutsceneObject(0, 0, 1080, 640, "/game/cutscenes/basementEnding/evening/bg.png").addScene(4);
        
        CutsceneObject eGround = new CutsceneObject(0, 0, 1080, 640, "/game/cutscenes/basementEnding/evening/ground.png").addScene(4);

        CutsceneObject eBiker = new CutsceneObject(631, 202, 50, 58, "/game/cutscenes/basementEnding/evening/my exquisite biker.png").addScene(4);
        eBiker.setRecalculationStrat(() -> {
            float millis = cutscene.getMilliseconds() / 4000F;
            eBiker.x = (int) GamePanel.lerp(631, 371, millis);
            eBiker.y = (int) GamePanel.lerp(202, 422, millis);
            eBiker.width = (int) GamePanel.lerp(50, 187, millis);
            eBiker.height = (int) GamePanel.lerp(58, 217, millis);
        });
        
        CutsceneObject eTrees = new CutsceneObject(0, 0, 1080, 640, "/game/cutscenes/basementEnding/evening/trees.png").addScene(4);
        
        
        
        CutsceneObject manyMsis = new CutsceneObject(0, 0, 1080, 640, "/game/cutscenes/basementEnding/manyMsis.png").addScene(5);
        
        
        
        cutscene.addObjects(List.of(basement,
                oSky, oBackGround, oFrontGround, oLadder1, oLadder2,
                mSky, mGround1, mGround2, mTreesBacker1, mTreesBacker2, mHelicopter, mTreesBack1, mTreesBack2, mTreesFront1, mTreesFront2,
                bBackground, bMSI, bMSI2, bEvilBeast, bHole, bBeam,
                eSky, eGround, eBiker, eTrees,
                manyMsis));
        cutscene.recognizeObjects();

        return cutscene;
    }
}
