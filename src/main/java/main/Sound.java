package main;

import enemies.Rat;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class Sound {

    Clip clip;
    public boolean musicPlaying;
    String key = "nothing";
    HashMap<String, URL> soundSet = new HashMap<>();

    GamePanel panel;

    Path gameDirectory;

    List<Clip> clips = new ArrayList<>();

    public Sound(GamePanel panel) {
        String os_name = System.getProperty( "os.name").toLowerCase();

        if (os_name.contains("win")) {
            gameDirectory = Path.of(System.getenv("APPDATA") + "\\.four-night-pepito");
        } else { //linux момент
            gameDirectory = Path.of(System.getProperty( "user.home" ) + "\\.four-night-pepito");
        }

        this.panel = panel;

        String[] miscCodes = new String[]{"fanSound",  "metalPipe", "nightStart", "startFan",  "stopFan", "vineboom",
                         "boop", "select", "selectFail", "knock", "sodaOpen", "doorSlam"};
        for(String s : miscCodes) {
            soundSet.put(s, getClass().getResource("/sound/misc/" + s + ".mp3"));
        }

        String[] notPepitoCodes = new String[] {"Scare", "Run", "Sound"};
        for (String s : notPepitoCodes) {
            soundSet.put("notPepito" + s, getClass().getResource("/sound/enemies/notPepito/notPepito" + s + ".mp3"));
        }

        String[] pepitoCodes = new String[] {"Walk", "Scare"};

        for (String s : pepitoCodes) {
            soundSet.put("pepito" + s, getClass().getResource("/sound/enemies/pepito/pepito" + s + ".mp3"));
        }

        soundSet.put("buzzlight", getClass().getResource("/sound/cam/buzzlight.mp3"));
        soundSet.put("camOut", getClass().getResource("/sound/cam/camOut.mp3"));
        soundSet.put("camPull", getClass().getResource("/sound/cam/camPull.mp3"));
        soundSet.put("error", getClass().getResource("/sound/cam/error.mp3"));

        soundSet.put("glitchArrive", getClass().getResource("/sound/enemies/glitch/arrival.mp3"));
        soundSet.put("glitch1", getClass().getResource("/sound/enemies/glitch/glitch1.mp3"));
        soundSet.put("glitch2", getClass().getResource("/sound/enemies/glitch/glitch2.mp3"));

        String[] a90Codes = new String[] {"Alert", "Alive", "Dead"};
        for (String s : a90Codes) {
            soundSet.put("a90" + s, getClass().getResource("/sound/enemies/a90/" + s.toLowerCase(Locale.ROOT) + ".mp3"));
        }
        soundSet.put("a90Arrive", getClass().getResource("/sound/enemies/a90/arrival.mp3"));

        soundSet.put("astartaAdSound", getClass().getResource("/sound/enemies/astarta/astartaAdSound.mp3"));

        soundSet.put("colaJumpscare", getClass().getResource("/sound/enemies/cola/colaJumpscare.mp3"));

        soundSet.put("randomScare", getClass().getResource("/sound/ambience/randomScare.mp3"));
        soundSet.put("cat_sounds", getClass().getResource("/sound/ambience/cat_sounds.mp3"));

        soundSet.put("msiArrival", getClass().getResource("/sound/enemies/msi/msiArrival.mp3"));
        soundSet.put("left", getClass().getResource("/sound/enemies/msi/left.mp3"));
        soundSet.put("right", getClass().getResource("/sound/enemies/msi/right.mp3"));
        soundSet.put("msiOut", getClass().getResource("/sound/enemies/msi/msiOut.mp3"));
        soundSet.put("msiCrucify", getClass().getResource("/sound/enemies/msi/msiKill.mp3"));
    }

    public Sound(GamePanel panel, String initializeGroups) {
        this.panel = panel;

        if(initializeGroups.equals("fan")) {
            soundSet.put("fanSound", getClass().getResource("/sound/items/fanSound.mp3"));
        }
        if(initializeGroups.equals("cam")) {
            soundSet.put("buzzlight", getClass().getResource("/sound/cam/buzzlight.mp3"));
        }
        if(initializeGroups.equals("music")) {
            soundSet.put("pepito", getClass().getResource("/sound/music/pepito.mp3"));
            soundSet.put("powerdown", getClass().getResource("/sound/misc/powerdown.mp3"));
            soundSet.put("zut", getClass().getResource("/sound/music/zut.mp3"));
            soundSet.put("chime", getClass().getResource("/sound/music/chime.mp3"));
        }

        if(initializeGroups.equals("every")) {
            String[] miscCodes = new String[]{"fanSound",  "metalPipe", "nightStart", "startFan",  "stopFan", "vineboom",
                    "boop", "select", "selectFail", "knock", "sodaOpen", "doorSlam"};
            for(String s : miscCodes) {
                soundSet.put(s, getClass().getResource("/sound/misc/" + s + ".mp3"));
            }

            String[] notPepitoCodes = new String[] {"Scare", "Run", "Sound"};
            for (String s : notPepitoCodes) {
                soundSet.put("notPepito" + s, getClass().getResource("/sound/enemies/notPepito/notPepito" + s + ".mp3"));
            }

            String[] pepitoCodes = new String[] {"Walk", "Scare"};

            for (String s : pepitoCodes) {
                soundSet.put("pepito" + s, getClass().getResource("/sound/enemies/pepito/pepito" + s + ".mp3"));
            }

            soundSet.put("buzzlight", getClass().getResource("/sound/cam/buzzlight.mp3"));
            soundSet.put("camOut", getClass().getResource("/sound/cam/camOut.mp3"));
            soundSet.put("camPull", getClass().getResource("/sound/cam/camPull.mp3"));
            soundSet.put("error", getClass().getResource("/sound/cam/error.mp3"));

            soundSet.put("glitchArrive", getClass().getResource("/sound/enemies/glitch/arrival.mp3"));
            soundSet.put("glitch1", getClass().getResource("/sound/enemies/glitch/glitch1.mp3"));
            soundSet.put("glitch2", getClass().getResource("/sound/enemies/glitch/glitch2.mp3"));

            String[] a90Codes = new String[] {"Alert", "Alive", "Dead"};
            for (String s : a90Codes) {
                soundSet.put("a90" + s, getClass().getResource("/sound/enemies/a90/" + s.toLowerCase(Locale.ROOT) + ".mp3"));
            }
            soundSet.put("a90Arrive", getClass().getResource("/sound/enemies/a90/arrival.mp3"));

            soundSet.put("astartaAdSound", getClass().getResource("/sound/enemies/astarta/astartaAdSound.mp3"));

            soundSet.put("colaJumpscare", getClass().getResource("/sound/enemies/cola/colaJumpscare.mp3"));

            soundSet.put("randomScare", getClass().getResource("/sound/ambience/randomScare.mp3"));
            soundSet.put("cat_sounds", getClass().getResource("/sound/ambience/cat_sounds.mp3"));

            soundSet.put("chime", getClass().getResource("/sound/music/chime.mp3"));

            soundSet.put("pepito", getClass().getResource("/sound/music/pepito.mp3"));
            soundSet.put("powerdown", getClass().getResource("/sound/misc/powerdown.mp3"));
            soundSet.put("zut", getClass().getResource("/sound/music/zut.mp3"));

            soundSet.put("msiArrival", getClass().getResource("/sound/enemies/msi/msiArrival.mp3"));
            soundSet.put("left", getClass().getResource("/sound/enemies/msi/left.mp3"));
            soundSet.put("right", getClass().getResource("/sound/enemies/msi/right.mp3"));
            soundSet.put("msiOut", getClass().getResource("/sound/enemies/msi/msiOut.mp3"));
            soundSet.put("msiCrucify", getClass().getResource("/sound/enemies/msi/msiKill.mp3"));

            soundSet.put("literally_22_miliseconds_of_nothing", getClass().getResource("/sound/literally_22_miliseconds_of_nothing.mp3"));
        }

    }
    Rat a;

    protected URL getResource(String key) {
//        System.out.println(key);
//        System.out.println(key + format);
//        System.out.println(soundSet.entrySet());
//        System.out.println(soundSet.get(key));
        return soundSet.get(key);
    }

//    protected URL getResourceOnDisk()

    public void setFile(String key) {
        if (clip != null) {
            if(!clip.isRunning()) {
                clip.close();
            }
        }
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(getResource(key));
            clip = AudioSystem.getClip();
            clip.open(ais);
            ais.close();

            this.key = key;
        } catch (Exception ignored) {
        }
    }

    public void play(String key, double volume, boolean loop) {
        if(panel.volume != 0) {
            try {
                setFile(key);

                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue((float) (volume + (1 - panel.volume) * volume + 5F));
            } catch (Exception ignored) {
            }

            clips.add(clip);

            clip.start();
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }

            musicPlaying = true;
        }
    }

    public void play(String key, double volume) {
        if(panel.volume != 0) {
            try {
                setFile(key);

                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue((float) (volume + (1 - panel.volume) * volume + 5F));
            } catch (Exception ignored) {
            }

            clips.add(clip);

            clip.start();

            musicPlaying = true;
        }
    }

//    public void playMP3(String key) {
//        AudioClip buzzer = new AudioClip(getResource(key, ".mp3").toString());
//        buzzer.play();
//    }
//    public void playMP3(String key, @Range(from = 0, to = 1) Double volume) {
//        AudioClip buzzer = new AudioClip(getResource(key, ".mp3").toExternalForm());
//        buzzer.setVolume(volume);
//        buzzer.play();
//    }
//    public void playMP3(String key, @Range(from = 0, to = 1)Double volume, @Range(from = -1, to = 1)Double pan) {
//        AudioClip buzzer = new AudioClip(getResource(key, ".mp3").toExternalForm());
//        buzzer.setVolume(volume);
//        buzzer.setPan(pan);
//        buzzer.play();
//    }
//    public void playMP3(String key, int loopCount) {
//        AudioClip buzzer = new AudioClip(getResource(key, ".mp3").toExternalForm());
//        buzzer.setCycleCount(loopCount);
//        buzzer.play();
//    }
//    public void playMP3(String key, @Range(from = 0, to = 1) Double volume, int loopCount) {
//        AudioClip buzzer = new AudioClip(getResource(key, ".mp3").toExternalForm());
//        buzzer.setVolume(volume);
//        buzzer.setCycleCount(loopCount);
//        buzzer.play();
//    }
//    public void playMP3(String key, @Range(from = 0, to = 1)Double volume, @Range(from = -1, to = 1)Double pan, int loopCount) {
//        AudioClip buzzer = new AudioClip(getResource(key, ".mp3").toExternalForm());
//        buzzer.setVolume(volume);
//        buzzer.setPan(pan);
//        buzzer.setCycleCount(loopCount);
//        buzzer.play();
//    }

    public void stop() {
        if(musicPlaying) {
            clip.stop();
        }
        if(clip != null) {
            clip.close();
        }
        musicPlaying = false;
    }
    Rat b;
}
