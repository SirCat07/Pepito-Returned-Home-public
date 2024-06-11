package main;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;

public class SoundMP3 {
	JFXThread jfxPlayer;
	public LinkedList<MediaPlayer> clips = new LinkedList<>();
	public HashMap<MediaPlayer, Double> clipVolume = new HashMap<>();
	GamePanel panel;

	public SoundMP3(GamePanel panel) {
		this.panel = panel;
		jfxPlayer = new JFXThread();
	}

	public SoundMP3(GamePanel panel, String initializeGroups) {
		this.panel = panel;
		jfxPlayer = new JFXThread(initializeGroups);
	}

	public void play(String key, double volume, boolean loop) {
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

		panel.currentRightPan = new Color(0, Math.min(255, panel.currentRightPan.getGreen() + 50),0);
		panel.currentLeftPan = new Color(0, Math.min(255, panel.currentLeftPan.getGreen() + 50),0);
	}

	public void playBirthdayHorn() {
		MediaPlayer clip = new MediaPlayer(new Media(getClass().getResource("/sound/misc/birthdayHorn.mp3").toExternalForm()));
		clip.setVolume(0.1);

		clip.play();
		clip.setOnEndOfMedia(() -> {
			clip.dispose();
			clips.remove(clip);
		});
		clips.add(clip);
	}

	public void play(String key, double volume, double pan) {
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

		if(pan > 0) {
			panel.currentRightPan = new Color(0, (int) Math.min(255, panel.currentRightPan.getGreen() + Math.abs(pan) * 255),0);
		} else {
			panel.currentLeftPan = new Color(0, (int) Math.min(255, panel.currentLeftPan.getGreen() + Math.abs(pan) * 255),0);
		}
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

		if(pan > 0) {
			panel.currentRightPan = new Color(0, (int) Math.min(255, panel.currentRightPan.getGreen() + Math.abs(pan) * 255),0);
		} else {
			panel.currentLeftPan = new Color(0, (int) Math.min(255, panel.currentLeftPan.getGreen() + Math.abs(pan) * 255),0);
		}
	}

	public void playButPanWithFreeze(String key, double volume, double pan) {
		if(GamePanel.mirror)
			pan = -pan;

		// ахуеть что я сделал нахуй
		MediaPlayer clip = jfxPlayer.getMP3ButPanWithFreeze(key, volume * Math.sqrt(panel.volume), pan);
		clip.setOnEndOfMedia(() -> {
			clip.dispose();
			clips.remove(clip);
			clipVolume.remove(clip);
		});
		clips.add(clip);
		clipVolume.put(clip, volume);

		if(pan > 0) {
			panel.currentRightPan = new Color(0, (int) Math.min(255, panel.currentRightPan.getGreen() + Math.abs(pan) * 255),0);
		} else {
			panel.currentLeftPan = new Color(0, (int) Math.min(255, panel.currentLeftPan.getGreen() + Math.abs(pan) * 255),0);
		}
	}

	public void stop() {
		for(short i = 0; i < clips.size(); i++) {
			MediaPlayer clip = clips.get(i);
			synchronized (clip) {
				clip.setCycleCount(0);
				clip.stop();
			}
		}

		LinkedList<MediaPlayer> clipsForRemoval = new LinkedList<>();
		for (int i = 0; i < clips.size(); i++) {
			MediaPlayer clip = clips.get(i);
			clip.dispose();
			clipsForRemoval.add(clip);
			clipVolume.remove(clip);
		}
		if(!clipsForRemoval.isEmpty()) {
			System.out.println("deleted " + clipsForRemoval.size() + " clips");
		}
		clips.removeAll(clipsForRemoval);
	}

	public void pause() {
		for(short i = 0; i < clips.size(); i++) {
			MediaPlayer clip = clips.get(i);
			synchronized (clip) {
				clip.pause();
			}
		}
	}

	public void resume() {
		for(short i = 0; i < clips.size(); i++) {
			MediaPlayer clip = clips.get(i);
			synchronized (clip) {
				clip.play();
			}
		}
	}
}
