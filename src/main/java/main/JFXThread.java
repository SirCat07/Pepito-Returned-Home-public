package main;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.HashMap;
import java.util.Locale;

public class JFXThread {

	HashMap<String, Media> soundSet = new HashMap<>();

	public void normalSounds() {
		String[] miscCodes = new String[]{"nightStart", "vineboom", "boop", "select", "selectFail", "riftSelect", "knock",
				"doorSlam", "powerdown", "lightsOn", "dayStart", "sellsYourBalls", "sogBallpitAppear", "challenger", "balloonPop", "explode", "untitled",
				"playMenuChange", "drinkStarlight", "reflectStarlight", "startSimulation", "checkpointSelect", "thunder1", "thunder2", "thunder3",
				"timerStart", "timerLoop", "stopSimulation", "flicker"};
		for (String s : miscCodes) {
			soundSet.put(s, new Media(getClass().getResource("/sound/misc/" + s + ".mp3").toExternalForm()));
		}

		String[] itemCodes = new String[]{"fanSound", "metalPipe", "startFan", "stopFan", "sodaOpen", "minnesota",
				"planks", "blockadeHit", "blockadeBreak", "icePotionUse", "blip"};
		for (String s : itemCodes) {
			soundSet.put(s, new Media(getClass().getResource("/sound/items/" + s + ".mp3").toExternalForm()));
		}

		String[] challengeCodes = new String[]{"lowSound", "aiUp", "aiDown", "buttonPress"};
		for (String s : challengeCodes) {
			soundSet.put(s, new Media(getClass().getResource("/sound/challenge/" + s + ".mp3").toExternalForm()));
		}

		String[] notPepitoCodes = new String[]{"Scare", "Run", "Sound", "Reflect"};
		for (String s : notPepitoCodes) {
			soundSet.put("notPepito" + s, new Media(getClass().getResource("/sound/enemies/notPepito/notPepito" + s + ".mp3").toExternalForm()));
		}


		String[] pepitoCodes = new String[]{"Walk", "Scare"};

		for (String s : pepitoCodes) {
			soundSet.put("pepito" + s, new Media(getClass().getResource("/sound/enemies/pepito/pepito" + s + ".mp3").toExternalForm()));
		}


		String[] camCodes = new String[]{"buzzlight", "camOut", "camPull", "error"};

		for (String s : camCodes) {
			soundSet.put(s, new Media(getClass().getResource("/sound/cam/" + s + ".mp3").toExternalForm()));
		}


		String[] glitcherCodes = new String[]{"glitch1", "glitch2", "shadowGlitch"};

		for (String s : glitcherCodes) {
			soundSet.put(s, new Media(getClass().getResource("/sound/enemies/glitch/" + s + ".mp3").toExternalForm()));
		}
		soundSet.put("glitchArrive", new Media(getClass().getResource("/sound/enemies/glitch/arrival.mp3").toExternalForm()));


		String[] a90Codes = new String[]{"Alert", "Alive", "Dead"};
		for (String s : a90Codes) {
			soundSet.put("a90" + s, new Media(getClass().getResource("/sound/enemies/a90/" + s.toLowerCase(Locale.ROOT) + ".mp3").toExternalForm()));
		}
		soundSet.put("a90Arrive", new Media(getClass().getResource("/sound/enemies/a90/arrival.mp3").toExternalForm()));
		soundSet.put("a90FuckingDies", new Media(getClass().getResource("/sound/enemies/a90/fucking_dies.mp3").toExternalForm()));

		soundSet.put("astartaAdSound", new Media(getClass().getResource("/sound/enemies/astarta/astartaAdSound.mp3").toExternalForm()));
		soundSet.put("colaJumpscare", new Media(getClass().getResource("/sound/enemies/cola/colaJumpscare.mp3").toExternalForm()));

		soundSet.put("boing", new Media(getClass().getResource("/sound/enemies/lemonade/boing.mp3").toExternalForm()));
		soundSet.put("throw", new Media(getClass().getResource("/sound/enemies/lemonade/throw.mp3").toExternalForm()));
		soundSet.put("lemonHit", new Media(getClass().getResource("/sound/enemies/lemonade/lemonHit.mp3").toExternalForm()));

		soundSet.put("mirrorCatAppear", new Media(getClass().getResource("/sound/enemies/mirrorCat/mcat_appeaars.mp3").toExternalForm()));
		soundSet.put("mirrorCatTick", new Media(getClass().getResource("/sound/enemies/mirrorCat/tick.mp3").toExternalForm()));
		soundSet.put("cageClose", new Media(getClass().getResource("/sound/enemies/mirrorCat/closeCage.mp3").toExternalForm()));
		soundSet.put("cageExplode", new Media(getClass().getResource("/sound/enemies/mirrorCat/cageExplode.mp3").toExternalForm()));

		soundSet.put("wiresHit", new Media(getClass().getResource("/sound/enemies/wiresHit.mp3").toExternalForm()));
		soundSet.put("jumpscareCatScare", new Media(getClass().getResource("/sound/enemies/jumpscareCatScare.mp3").toExternalForm()));
		soundSet.put("banjo", new Media(getClass().getResource("/sound/enemies/banjo.mp3").toExternalForm()));

		soundSet.put("scaryCatAttack", new Media(getClass().getResource("/sound/enemies/scaryCat/scaryCatAttack.mp3").toExternalForm()));
		soundSet.put("scaryCatAttackSlow", new Media(getClass().getResource("/sound/enemies/scaryCat/scaryCatAttackSlow.mp3").toExternalForm()));
		soundSet.put("scaryCatJumpscare", new Media(getClass().getResource("/sound/enemies/scaryCat/scaryCatJumpscare.mp3").toExternalForm()));

		soundSet.put("dreadHideaway", new Media(getClass().getResource("/sound/enemies/dread/hideaway.mp3").toExternalForm()));
		soundSet.put("dreadDead", new Media(getClass().getResource("/sound/enemies/dread/dead.mp3").toExternalForm()));

		soundSet.put("randomScare", new Media(getClass().getResource("/sound/ambience/randomScare.mp3").toExternalForm()));
		soundSet.put("cat_sounds", new Media(getClass().getResource("/sound/ambience/cat_sounds.mp3").toExternalForm()));
		soundSet.put("fakeWalk", new Media(getClass().getResource("/sound/ambience/fakeWalk.mp3").toExternalForm()));
		soundSet.put("shirtfart", new Media(getClass().getResource("/sound/ambience/shirtfart.mp3").toExternalForm()));
		soundSet.put("tempAmbient1", new Media(getClass().getResource("/sound/ambience/tempAmbient1.mp3").toExternalForm()));
		soundSet.put("tempAmbient2", new Media(getClass().getResource("/sound/ambience/tempAmbient2.mp3").toExternalForm()));
		soundSet.put("tempAmbient3", new Media(getClass().getResource("/sound/ambience/tempAmbient3.mp3").toExternalForm()));

		soundSet.put("waterLoop", new Media(getClass().getResource("/sound/enemies/shark/waterLoop.mp3").toExternalForm()));
		soundSet.put("a120SoundRight", new Media(getClass().getResource("/sound/enemies/a120/a120SoundRight.mp3").toExternalForm()));
		soundSet.put("a120SoundLeft", new Media(getClass().getResource("/sound/enemies/a120/a120SoundLeft.mp3").toExternalForm()));
		soundSet.put("elAstartaScare", new Media(getClass().getResource("/sound/enemies/elAstartaScare.mp3").toExternalForm()));

		soundSet.put("astartaSiren", new Media(getClass().getResource("/sound/enemies/astartaBoss/siren.mp3").toExternalForm()));
		soundSet.put("uncannyBoxSound", new Media(getClass().getResource("/sound/enemies/astartaBoss/uncannyBoxSound.mp3").toExternalForm()));
		soundSet.put("astartaDamage", new Media(getClass().getResource("/sound/enemies/astartaBoss/astartaDamage.mp3").toExternalForm()));
		soundSet.put("astartaDeath", new Media(getClass().getResource("/sound/enemies/astartaBoss/astartaDeath.mp3").toExternalForm()));
		soundSet.put("astartaEvilSound", new Media(getClass().getResource("/sound/enemies/astartaBoss/astartaEvilSound.mp3").toExternalForm()));
		soundSet.put("astartaBlackHole", new Media(getClass().getResource("/sound/enemies/astartaBoss/blackHole.mp3").toExternalForm()));
		soundSet.put("phaseChange1", new Media(getClass().getResource("/sound/enemies/astartaBoss/phaseChange1.mp3").toExternalForm()));
		soundSet.put("phaseChange2", new Media(getClass().getResource("/sound/enemies/astartaBoss/phaseChange2.mp3").toExternalForm()));
		soundSet.put("eventRoulette", new Media(getClass().getResource("/sound/enemies/astartaBoss/eventRoulette.mp3").toExternalForm()));

		String[] makiCodes = new String[]{"makiWalk", "makiSound", "makiScare"};

		for (String s : makiCodes) {
			soundSet.put(s, new Media(getClass().getResource("/sound/enemies/maki/" + s + ".mp3").toExternalForm()));
		}


		String[] msiCodes = new String[]{"msiArrival", "left", "right", "msiOut", "msiKill", "tfel", "thgir"};

		for (String s : msiCodes) {
			soundSet.put(s, new Media(getClass().getResource("/sound/enemies/msi/" + s + ".mp3").toExternalForm()));
		}

		String[] boykisserCodes = new String[]{"boykisser", "boykisserLong", "boykisserOut"};

		for (String s : boykisserCodes) {
			soundSet.put(s, new Media(getClass().getResource("/sound/enemies/boykisser/" + s + ".mp3").toExternalForm()));
		}

		soundSet.put("bubbles", new Media(getClass().getResource("/sound/enemies/shark/bubbles.mp3").toExternalForm()));
		soundSet.put("sogMeow", new Media(getClass().getResource("/sound/enemies/shark/sogMeow.mp3").toExternalForm()));

		soundSet.put("literally_22_miliseconds_of_nothing", new Media(getClass().getResource("/sound/literally_22_miliseconds_of_nothing.mp3").toExternalForm()));
	}

	public JFXThread() {
		normalSounds();
	}

	public JFXThread(String initializeGroups) {
		if(initializeGroups.equals("fan") || initializeGroups.equals("every")) {
			soundSet.put("fanSound", new Media(getClass().getResource("/sound/items/fanSound.mp3").toExternalForm()));
		}
		if(initializeGroups.equals("cam") || initializeGroups.equals("every")) {
			soundSet.put("buzzlight", new Media(getClass().getResource("/sound/cam/buzzlight.mp3").toExternalForm()));
			soundSet.put("shadowPortal", new Media(getClass().getResource("/sound/misc/latrop_sdunos.mp3").toExternalForm()));
		}
		if(initializeGroups.equals("music") || initializeGroups.equals("every")) {
			soundSet.put("pepito", new Media(getClass().getResource("/sound/music/pepito.mp3").toExternalForm()));
			soundSet.put("zut", new Media(getClass().getResource("/sound/music/zut.mp3").toExternalForm()));
			soundSet.put("chime", new Media(getClass().getResource("/sound/music/chime.mp3").toExternalForm()));
			soundSet.put("millyShop", new Media(getClass().getResource("/sound/music/millyShop.mp3").toExternalForm()));
			soundSet.put("orca", new Media(getClass().getResource("/sound/music/orca.mp3").toExternalForm()));
			soundSet.put("theShadow", new Media(getClass().getResource("/sound/music/the_shadow.mp3").toExternalForm()));
			soundSet.put("void", new Media(getClass().getResource("/sound/music/void.mp3").toExternalForm()));
			soundSet.put("timesEnd", new Media(getClass().getResource("/sound/music/times_end.mp3").toExternalForm()));
			soundSet.put("partyFavors", new Media(getClass().getResource("/sound/music/partyFavors.mp3").toExternalForm()));
			soundSet.put("pepitoButCooler", new Media(getClass().getResource("/sound/music/pepitoButCooler.mp3").toExternalForm()));
			soundSet.put("spookers", new Media(getClass().getResource("/sound/music/spookers.mp3").toExternalForm()));
			soundSet.put("maxwellMusicBox", new Media(getClass().getResource("/sound/music/maxwell_music_box_shortver.mp3").toExternalForm()));
			soundSet.put("malh", new Media(getClass().getResource("/sound/music/malh.mp3").toExternalForm()));
			soundSet.put("astartaFight", new Media(getClass().getResource("/sound/music/astarta_fight.mp3").toExternalForm()));
			soundSet.put("tension", new Media(getClass().getResource("/sound/music/tension.mp3").toExternalForm()));
			soundSet.put("limbos", new Media(getClass().getResource("/sound/music/limbos.mp3").toExternalForm()));
			soundSet.put("secretPepitoTheme", new Media(getClass().getResource("/sound/music/secretPepitoTheme.mp3").toExternalForm()));
			soundSet.put("threat", new Media(getClass().getResource("/sound/music/threat.mp3").toExternalForm()));
			soundSet.put("stormfury", new Media(getClass().getResource("/sound/music/stormfury.mp3").toExternalForm()));
		}
		if(initializeGroups.equals("bingo") || initializeGroups.equals("every")) {
			soundSet.put("jingleEasy", new Media(getClass().getResource("/sound/bingo/jingle_easy.mp3").toExternalForm()));
			soundSet.put("jingleNormal", new Media(getClass().getResource("/sound/bingo/jingle_normal.mp3").toExternalForm()));
			soundSet.put("jingleHard", new Media(getClass().getResource("/sound/bingo/jingle_hard.mp3").toExternalForm()));
			soundSet.put("jingleFinal", new Media(getClass().getResource("/sound/bingo/jingle_finle.mp3").toExternalForm()));
			soundSet.put("jingleFail", new Media(getClass().getResource("/sound/bingo/jingle_fail.mp3").toExternalForm()));
		}
		if(initializeGroups.equals("scaryCat") || initializeGroups.equals("every")) {
			soundSet.put("scaryCat", new Media(getClass().getResource("/sound/enemies/scaryCat/scaryCat.mp3").toExternalForm()));
			soundSet.put("scaryCatShadow", new Media(getClass().getResource("/sound/enemies/scaryCat/scaryCatShadow.mp3").toExternalForm()));
		}
		if(initializeGroups.equals("generator") || initializeGroups.equals("every")) {
			soundSet.put("connectToGenerator", new Media(getClass().getResource("/sound/generator/connectToGenerator.mp3").toExternalForm()));
			soundSet.put("generatorOver", new Media(getClass().getResource("/sound/generator/generatorOver.mp3").toExternalForm()));
			soundSet.put("generatorSuccess", new Media(getClass().getResource("/sound/generator/generatorSuccess.mp3").toExternalForm()));
			soundSet.put("generatorFail", new Media(getClass().getResource("/sound/generator/generatorFail.mp3").toExternalForm()));
			soundSet.put("generatorNextStage", new Media(getClass().getResource("/sound/generator/generatorNextStage.mp3").toExternalForm()));
		}
		if(initializeGroups.equals("rain") || initializeGroups.equals("every")) {
			soundSet.put("rain", new Media(getClass().getResource("/sound/rain.mp3").toExternalForm()));
		}
		if(initializeGroups.equals("every")) {
			normalSounds();
		}
	}


	public MediaPlayer getMP3(String key, double volume) {
		Media media = soundSet.get(key);
		MediaPlayer player = new MediaPlayer(media);
		player.setVolume(volume);
		player.setRate(1);

		player.play();
		return player;
	}
	public MediaPlayer getMP3Rate(String key, double volume, double rate) {
		Media media = soundSet.get(key);
		MediaPlayer player = new MediaPlayer(media);
		player.setVolume(volume);
		player.setRate(rate);

		player.play();
		return player;
	}
	public MediaPlayer getMP3RateLooped(String key, double volume, double rate) {
		Media media = soundSet.get(key);
		MediaPlayer player = new MediaPlayer(media);
		player.setVolume(volume);
		player.setRate(rate);
		player.setCycleCount(MediaPlayer.INDEFINITE);

		player.play();
		return player;
	}
	public MediaPlayer getMP3(String key, double volume, double pan) {
		Media media = soundSet.get(key);
		MediaPlayer player = new MediaPlayer(media);
		player.setVolume(volume);
		player.setBalance(pan);
		player.setRate(1);

		player.play();
		return player;
	}
	public MediaPlayer getMP3ButPan(String key, double volume, double pan) {
		Media media = soundSet.get(key);
		MediaPlayer player = new MediaPlayer(media);
		player.setVolume(volume);
//		player.setPan(pan); NEEDS A FIX PLEASE
		player.setBalance(pan / 2);
		player.setRate(1);

		player.play();
		return player;
	}

	public MediaPlayer getMP3ButPanWithFreeze(String key, double volume, double pan) {
		Media media = soundSet.get(key);
		MediaPlayer player = new MediaPlayer(media);
		player.setVolume(volume);
//		player.setPan(pan); NEEDS A FIX PLEASE
		player.setBalance(pan / 2);
		player.setRate(GamePanel.freezeModifier);

		player.play();
		return player;
	}
	public MediaPlayer getMP3(String key, double volume, double pan, int cycleCount) {
		Media media = soundSet.get(key);
		MediaPlayer player = new MediaPlayer(media);
		player.setVolume(volume);
		player.setBalance(pan);
		player.setCycleCount(cycleCount);
		player.setRate(1);

		player.play();
		return player;
	}

}
