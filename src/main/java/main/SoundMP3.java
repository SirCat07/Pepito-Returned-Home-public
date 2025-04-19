package main;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

public class SoundMP3 {
	JFXThread jfxPlayer;
	public LinkedList<MediaPlayer> clips = new LinkedList<>();
	public HashMap<MediaPlayer, Double> clipVolume = new HashMap<>();
	GamePanel panel;
	String initializeGroups = "";

	public SoundMP3(GamePanel panel) {
		this.panel = panel;
		jfxPlayer = new JFXThread();
	}

	public SoundMP3(GamePanel panel, String initializeGroups) {
		this.panel = panel;
		this.initializeGroups = initializeGroups;
		jfxPlayer = new JFXThread(initializeGroups);
	}

	public MediaPlayer play(String key, double volume, boolean loop) {
		if(GamePanel.krunlicPhase >= 2) {
			if(initializeGroups.equals("music"))
				return null;
		}
		
		MediaPlayer clip;

		if(loop) {
			clip = jfxPlayer.getMP3(key, volume * Math.sqrt(panel.volume), 0, MediaPlayer.INDEFINITE);
		} else {
			clip = jfxPlayer.getMP3(key, volume * Math.sqrt(panel.volume), 0);
		}
		
		clip.setOnEndOfMedia(() -> {
			if(clip.getCycleCount() == MediaPlayer.INDEFINITE)
				return;
			clip.dispose();
			clips.remove(clip);
			clipVolume.remove(clip);
		});
		clips.add(clip);
		clipVolume.put(clip, volume);


		if(initializeGroups.equals("music")) {
			clip.setAudioSpectrumListener(((timestamp, duration, magnitudes, phases) -> {
				panel.visualizerPoints.clear();
				panel.visualizerPoints.add(new Point(280, 250));
				for (int i = 0; i < phases.length; i++) {
					panel.visualizerPoints.add(new Point(i * 24 + 280, (int) (Math.sqrt(-magnitudes[i] * 60) * 12) - 475));
				}
				panel.visualizerPoints.add(new Point(1080, 250));
			}));
		}

//		try {
//			clip.wait();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		while (clips.get(clip)) {
//			clip.play();
//			try {
//				clip.wait();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}

		return clip;
	}

	public void play(String key, double volume) {
		MediaPlayer clip = jfxPlayer.getMP3(key, volume * Math.sqrt(panel.volume));
		clip.setOnEndOfMedia(() -> {
			clip.dispose();
			clips.remove(clip);
			clipVolume.remove(clip);
		});
		clips.add(clip);
		clipVolume.put(clip, volume);

		panel.currentRightPan = Math.min(255, panel.currentRightPan + 80);
		panel.currentLeftPan = Math.min(255, panel.currentLeftPan + 80);
	}

	public void playFromSeconds(String key, double volume, int start) {
		MediaPlayer clip = jfxPlayer.getMP3(key, volume * Math.sqrt(panel.volume));
		clip.setStartTime(Duration.seconds(start));
		clip.setOnEndOfMedia(() -> {
			clip.dispose();
			clips.remove(clip);
			clipVolume.remove(clip);
		});
		clips.add(clip);
		clipVolume.put(clip, volume);

		panel.currentRightPan = Math.min(255, panel.currentRightPan + 80);
		panel.currentLeftPan = Math.min(255, panel.currentLeftPan + 80);
	}

	public void playBirthdayHorn() {
		MediaPlayer clip = new MediaPlayer(new Media(getClass().getResource("/sound/misc/birthdayHorn.mp3").toExternalForm()));
		clip.setVolume(0.1 * panel.volume);

		clip.play();
		clip.setOnEndOfMedia(() -> {
			clip.dispose();
			clips.remove(clip);
		});
		clips.add(clip);
	}

	public void play(String key, double volume, double pan) {
		if(pan > 0) {
			panel.currentRightPan = (int) Math.min(255, panel.currentRightPan + Math.abs(pan) * 255);
		} else {
			panel.currentLeftPan = (int) Math.min(255, panel.currentLeftPan + Math.abs(pan) * 255);
		}
		
		if(GamePanel.mirror)
			pan = -pan;

		MediaPlayer clip = jfxPlayer.getMP3(key, volume * Math.sqrt(panel.volume), pan);
		clip.setOnEndOfMedia(() -> {
			clip.dispose();
			clips.remove(clip);
			clipVolume.remove(clip);
		});
		clips.add(clip);
		clipVolume.put(clip, volume);
	}

	public void playRate(String key, double volume, double rate) {
		MediaPlayer clip = jfxPlayer.getMP3Rate(key, volume * Math.sqrt(panel.volume), rate);
		clip.setOnEndOfMedia(() -> {
			clip.dispose();
			clips.remove(clip);
			clipVolume.remove(clip);
		});
		clips.add(clip);
		clipVolume.put(clip, volume);

		panel.currentRightPan = Math.min(255, panel.currentRightPan + 80);
		panel.currentLeftPan = Math.min(255, panel.currentLeftPan + 80);
	}

	public void playRateLooped(String key, double volume, double rate) {
		MediaPlayer clip = jfxPlayer.getMP3RateLooped(key, volume * Math.sqrt(panel.volume), rate);
		clip.setOnEndOfMedia(() -> {
			if(clip.getCycleCount() == MediaPlayer.INDEFINITE)
				return;
			clip.dispose();
			clips.remove(clip);
			clipVolume.remove(clip);
		});
		clips.add(clip);
		clipVolume.put(clip, volume);
	}

	public void playButPan(String key, double volume, double pan) {
		if(pan > 0) {
			panel.currentRightPan = (int) Math.min(255, panel.currentRightPan + Math.abs(pan) * 255);
		} else {
			panel.currentLeftPan = (int) Math.min(255, panel.currentLeftPan + Math.abs(pan) * 255);
		}
		
		if(GamePanel.mirror)
			pan = -pan;

		MediaPlayer clip = jfxPlayer.getMP3ButPan(key, volume * Math.sqrt(panel.volume), pan);
		clip.setOnEndOfMedia(() -> {
			clip.dispose();
			clips.remove(clip);
			clipVolume.remove(clip);
		});
		clips.add(clip);
		clipVolume.put(clip, volume);
	}

	public void stop() {
		try {
			synchronized (clips) {
				ListIterator<MediaPlayer> iter = clips.stream().toList().listIterator();

				int clipsRemoved = 0;
				while(iter.hasNext()) {
					MediaPlayer clip = iter.next();
					clip.setCycleCount(0);
					clip.stop();
					clip.dispose();
					clips.remove(clip);
					clipsRemoved++;
				}

				if(clipsRemoved > 0) {
					System.out.println("deleted " + clipsRemoved + " clips");
				}
			}
		} catch (Exception UhOh) {
			UhOh.printStackTrace();
		}
	}

	boolean paused = false;
	public boolean isPaused() {
		return paused;
	}

	boolean gamePaused = false;
	public boolean isGamePaused() {
		return gamePaused;
	}
	

	public void pause(boolean isGamePause) {
		synchronized (clips) {
			for (short i = 0; i < clips.size(); i++) {
				MediaPlayer clip = clips.get(i);
				clip.pause();
			}
		}
		if(isGamePause) {
			gamePaused = true;
		} else {
			paused = true;
		}
	}

	public void resume(boolean isGameResume) {
		if(gamePaused && isGameResume) {
			if(paused) {
                gamePaused = false;
                return;
			}
		}

		synchronized (clips) {
			for (short i = 0; i < clips.size(); i++) {
				MediaPlayer clip = clips.get(i);
				clip.play();
			}
		}
		
		if(isGameResume) {
			gamePaused = false;
		} else {
			paused = false;
		}
	}
}
