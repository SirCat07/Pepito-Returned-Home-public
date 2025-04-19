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
				"playMenuChange", "startSimulation", "checkpointSelect", "thunder1", "thunder2", "thunder3", "timerStart", "timerLoop", "stopSimulation", 
				"flicker1", "flicker2", "riftIndicator", "weirdIdea", "dabloonGet", "endlessClockSound", "neonSogBallBounce", "krunlicTrigger", 
				"shitscream", "glassShatteringSound", "sparkSound", "nuclearCatTeleport", "connectedToFacility", "explosionSound",
				"imHere", "cornfieldAmbient", "cornfieldAmbient2", "cornSiren", "wowYayYippee", "helicopter", "geiger1", "geiger2", "geiger3", "geiger4",
				"customGetPoint", "flashlightSwitch", "clockTickHydro"};
		for (String s : miscCodes) {
			soundSet.put(s, new Media(getClass().getResource("/sound/misc/" + s + ".mp3").toExternalForm()));
		}

		String[] itemCodes = new String[]{"fanSound", "metalPipe", "startFan", "stopFan", "sodaOpen", "minnesota", "megasota", "planks", "blockadeHit", "blockadeBreak",
				"icePotionUse", "blip", "styrofoamPipe", "larryGlasses", "waterSpray1", "waterSpray2", "waterSpray3", "drinkStarlight", "reflectStarlight",
				"shadowblockerErase"};
		for (String s : itemCodes) {
			soundSet.put(s, new Media(getClass().getResource("/sound/items/" + s + ".mp3").toExternalForm()));
		}

		String[] challengeCodes = new String[]{"lowSound", "aiUp", "aiDown", "buttonPress", "five alert", "nine alert"};
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


		String[] glitcherCodes = new String[]{"glitch1", "glitch2", "shadowGlitch", "glitcherScare"};

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
		soundSet.put("colaCatFly", new Media(getClass().getResource("/sound/enemies/cola/colaCatFly.mp3").toExternalForm()));

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
		
		soundSet.put("mrMazeSound", new Media(getClass().getResource("/sound/enemies/mrMazeSound.mp3").toExternalForm()));

		soundSet.put("randomScare", new Media(getClass().getResource("/sound/ambience/randomScare.mp3").toExternalForm()));
		soundSet.put("cat_sounds", new Media(getClass().getResource("/sound/ambience/cat_sounds.mp3").toExternalForm()));
		soundSet.put("fakeWalk", new Media(getClass().getResource("/sound/ambience/fakeWalk.mp3").toExternalForm()));
		soundSet.put("shirtfart", new Media(getClass().getResource("/sound/ambience/shirtfart.mp3").toExternalForm()));
		soundSet.put("tempAmbient1", new Media(getClass().getResource("/sound/ambience/tempAmbient1.mp3").toExternalForm()));
		soundSet.put("tempAmbient2", new Media(getClass().getResource("/sound/ambience/tempAmbient2.mp3").toExternalForm()));
		soundSet.put("tempAmbient3", new Media(getClass().getResource("/sound/ambience/tempAmbient3.mp3").toExternalForm()));

		soundSet.put("waterLoop", new Media(getClass().getResource("/sound/enemies/shark/waterLoop.mp3").toExternalForm()));
		soundSet.put("waterLoopFaker", new Media(getClass().getResource("/sound/enemies/shark/waterLoopFaker.mp3").toExternalForm()));
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
		
		soundSet.put("kijiKill", new Media(getClass().getResource("/sound/enemies/kiji/kijiKill.mp3").toExternalForm()));
		soundSet.put("kijiHold", new Media(getClass().getResource("/sound/enemies/kiji/kijiHold.mp3").toExternalForm()));
		soundSet.put("kijiRelease", new Media(getClass().getResource("/sound/enemies/kiji/kijiRelease.mp3").toExternalForm()));
		soundSet.put("kijiAppear", new Media(getClass().getResource("/sound/enemies/kiji/kijiAppear.mp3").toExternalForm()));
		soundSet.put("kijiSuccess", new Media(getClass().getResource("/sound/enemies/kiji/kijiSuccess.mp3").toExternalForm()));
		soundSet.put("kijiFail", new Media(getClass().getResource("/sound/enemies/kiji/kijiFail.mp3").toExternalForm()));
		
		soundSet.put("shockFakeout", new Media(getClass().getResource("/sound/enemies/shock/shockFakeout.mp3").toExternalForm()));
		
		soundSet.put("evilDoorOpen", new Media(getClass().getResource("/sound/basement/evilDoorOpen.mp3").toExternalForm()));
		soundSet.put("evilDoorEnter", new Media(getClass().getResource("/sound/basement/evilDoorEnter.mp3").toExternalForm()));
		soundSet.put("evilDoorEnterShort", new Media(getClass().getResource("/sound/basement/evilDoorEnterShort.mp3").toExternalForm()));
		soundSet.put("evilDoorSqueak1", new Media(getClass().getResource("/sound/basement/squeak1.mp3").toExternalForm()));
		soundSet.put("evilDoorSqueak2", new Media(getClass().getResource("/sound/basement/squeak2.mp3").toExternalForm()));
		soundSet.put("evilDoorSqueak3", new Media(getClass().getResource("/sound/basement/squeak3.mp3").toExternalForm()));
		soundSet.put("evilDoorSqueak4", new Media(getClass().getResource("/sound/basement/squeak4.mp3").toExternalForm()));
		soundSet.put("evilDoorSqueak5", new Media(getClass().getResource("/sound/basement/squeak5.mp3").toExternalForm()));
		soundSet.put("evilDoorSqueak6", new Media(getClass().getResource("/sound/basement/squeak6.mp3").toExternalForm()));
		soundSet.put("basementDoorReblock", new Media(getClass().getResource("/sound/basement/basementDoorReblock.mp3").toExternalForm()));
		soundSet.put("scarySkibidiSound", new Media(getClass().getResource("/sound/basement/scarySkibidiSound.mp3").toExternalForm()));
		
		soundSet.put("beep", new Media(getClass().getResource("/sound/hchamber/beep.mp3").toExternalForm()));
		soundSet.put("compassTurn", new Media(getClass().getResource("/sound/hchamber/compassTurn.mp3").toExternalForm()));
		soundSet.put("hcTimerWindup", new Media(getClass().getResource("/sound/hchamber/hcTimerWindup.mp3").toExternalForm()));
		soundSet.put("overseerAlert", new Media(getClass().getResource("/sound/hchamber/overseerAlert.mp3").toExternalForm()));
		soundSet.put("lockerEnter", new Media(getClass().getResource("/sound/hchamber/lockerEnter.mp3").toExternalForm()));
		soundSet.put("lockerOut", new Media(getClass().getResource("/sound/hchamber/lockerOut.mp3").toExternalForm()));
		soundSet.put("conditionerSounds", new Media(getClass().getResource("/sound/hchamber/conditionerSounds.mp3").toExternalForm()));
		soundSet.put("barrierRising", new Media(getClass().getResource("/sound/hchamber/barrierRising.mp3").toExternalForm()));
		soundSet.put("enterNewRoom", new Media(getClass().getResource("/sound/hchamber/enterNewRoom.mp3").toExternalForm()));
		soundSet.put("hydrophobiaJumpscare", new Media(getClass().getResource("/sound/hchamber/hydrophobiaJumpscare.mp3").toExternalForm()));
		soundSet.put("reinforcedDoorOpen", new Media(getClass().getResource("/sound/hchamber/reinforcedDoorOpen.mp3").toExternalForm()));
		//
		soundSet.put("dustonTune", new Media(getClass().getResource("/sound/music/dustonTune.mp3").toExternalForm()));
		//
		soundSet.put("smokeAlarmShort", new Media(getClass().getResource("/sound/hchamber/smokeAlarmShort.mp3").toExternalForm()));
		soundSet.put("smokeAlarmLong", new Media(getClass().getResource("/sound/hchamber/smokeAlarmLong.mp3").toExternalForm()));
		
		
		soundSet.put("fieldHeavyRain", new Media(getClass().getResource("/sound/hchamber/field/heavyRain.mp3").toExternalForm()));
		soundSet.put("fieldSirens", new Media(getClass().getResource("/sound/hchamber/field/fieldSirens.mp3").toExternalForm()));
		soundSet.put("fieldCarDoorClose", new Media(getClass().getResource("/sound/hchamber/field/carDoorClose.mp3").toExternalForm()));
		soundSet.put("fieldCarSounds", new Media(getClass().getResource("/sound/hchamber/field/carSounds.mp3").toExternalForm()));
		soundSet.put("blimpRadarScan", new Media(getClass().getResource("/sound/hchamber/field/blimpRadarScan.mp3").toExternalForm()));
		soundSet.put("fieldLever", new Media(getClass().getResource("/sound/hchamber/field/fieldLever.mp3").toExternalForm()));
		soundSet.put("fieldCarHit", new Media(getClass().getResource("/sound/hchamber/field/carHit.mp3").toExternalForm()));
		
		
		soundSet.put("dscJumpscare", new Media(getClass().getResource("/sound/enemies/dsc/deepSeaCreatureJumpscare.mp3").toExternalForm()));
		soundSet.put("harpoonShoot", new Media(getClass().getResource("/sound/enemies/dsc/harpoonShoot.mp3").toExternalForm()));
		soundSet.put("harpoonReload", new Media(getClass().getResource("/sound/enemies/dsc/harpoonReload.mp3").toExternalForm()));
		soundSet.put("harpoonSuccess", new Media(getClass().getResource("/sound/enemies/dsc/harpoonSuccess.mp3").toExternalForm()));

		soundSet.put("beastWarnLeft", new Media(getClass().getResource("/sound/enemies/beast/beastWarnLeft.mp3").toExternalForm()));
		soundSet.put("beastWarnRight", new Media(getClass().getResource("/sound/enemies/beast/beastWarnRight.mp3").toExternalForm()));
		soundSet.put("beastRush", new Media(getClass().getResource("/sound/enemies/beast/beastRush.mp3").toExternalForm()));
		
		soundSet.put("platWompWomp", new Media(getClass().getResource("/sound/platformer/wompWomp.mp3").toExternalForm()));
		
		soundSet.put("krunlicJumpscare", new Media(getClass().getResource("/sound/enemies/krunlicJumpscare.mp3").toExternalForm()));
		
		String[] makiCodes = new String[]{"makiWalk", "makiSound", "makiScare"};

		for (String s : makiCodes) {
			soundSet.put(s, new Media(getClass().getResource("/sound/enemies/maki/" + s + ".mp3").toExternalForm()));
		}


		String[] msiCodes = new String[]{"msiArrival", "left", "right", "msiOut", "msiKill", "tfel", "thgir", "msiFakeout"};

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
			soundSet.put("deepSeaCreature", new Media(getClass().getResource("/sound/music/deepSeaCreature.mp3").toExternalForm()));
			soundSet.put("larrySweep", new Media(getClass().getResource("/sound/music/larrySweep.mp3").toExternalForm()));
			soundSet.put("dryCats", new Media(getClass().getResource("/sound/music/dryCats.mp3").toExternalForm()));
			soundSet.put("windSounds", new Media(getClass().getResource("/sound/music/windSounds.mp3").toExternalForm()));
			soundSet.put("scaryAhhDoor", new Media(getClass().getResource("/sound/music/scaryAhhDoor.mp3").toExternalForm()));
			soundSet.put("hydrophobiaSounds", new Media(getClass().getResource("/sound/music/hydrophobiaSounds.mp3").toExternalForm()));
			soundSet.put("halfwayHallway", new Media(getClass().getResource("/sound/music/halfwayHallway.mp3").toExternalForm()));
			soundSet.put("halfwayHallwayEnd", new Media(getClass().getResource("/sound/music/halfwayHallwayEnd.mp3").toExternalForm()));
			soundSet.put("basementKeyOfficeSong", new Media(getClass().getResource("/sound/music/basementKeyOfficeSong.mp3").toExternalForm()));
			soundSet.put("brokenRadioSong", new Media(getClass().getResource("/sound/music/brokenRadioSong.mp3").toExternalForm()));
			soundSet.put("fieldField", new Media(getClass().getResource("/sound/music/fieldField.mp3").toExternalForm()));
			soundSet.put("underTheRadar", new Media(getClass().getResource("/sound/music/underTheRadar.mp3").toExternalForm()));
			soundSet.put("endOfYourJourney", new Media(getClass().getResource("/sound/music/endOfYourJourneySHORT.mp3").toExternalForm()));
			soundSet.put("yourEventualDemise", new Media(getClass().getResource("/sound/music/yourEventualDemise.mp3").toExternalForm()));
			soundSet.put("platformerSong", new Media(getClass().getResource("/sound/music/platformerSong.mp3").toExternalForm()));
			soundSet.put("lemonadeCat9", new Media(getClass().getResource("/sound/music/lemonadeCat9.mp3").toExternalForm()));
			soundSet.put("investigationSong", new Media(getClass().getResource("/sound/music/investigationSong.mp3").toExternalForm()));
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
			soundSet.put("nuclear cat", new Media(getClass().getResource("/sound/enemies/scaryCat/nuclear cat.mp3").toExternalForm()));
			soundSet.put("scaryCatFakeout", new Media(getClass().getResource("/sound/enemies/scaryCat/scaryCatFakeout.mp3").toExternalForm()));
		}
		if(initializeGroups.equals("generator") || initializeGroups.equals("every")) {
			soundSet.put("connectToGenerator", new Media(getClass().getResource("/sound/generator/connectToGenerator.mp3").toExternalForm()));
			soundSet.put("connectToGeneratorEmpty", new Media(getClass().getResource("/sound/generator/connectToGeneratorEmpty.mp3").toExternalForm()));
			soundSet.put("generatorOver", new Media(getClass().getResource("/sound/generator/generatorOver.mp3").toExternalForm()));
			soundSet.put("generatorSuccess", new Media(getClass().getResource("/sound/generator/generatorSuccess.mp3").toExternalForm()));
			soundSet.put("generatorFail", new Media(getClass().getResource("/sound/generator/generatorFail.mp3").toExternalForm()));
			soundSet.put("generatorNextStage", new Media(getClass().getResource("/sound/generator/generatorNextStage.mp3").toExternalForm()));
			soundSet.put("basementTheme5Generator", new Media(getClass().getResource("/sound/generator/basementTheme5Generator.mp3").toExternalForm()));
		}
		if(initializeGroups.equals("rain") || initializeGroups.equals("every")) {
			soundSet.put("rain", new Media(getClass().getResource("/sound/rain.mp3").toExternalForm()));
		}
		if(initializeGroups.equals("basement") || initializeGroups.equals("every")) {
			soundSet.put("mandatoryMillySection", new Media(getClass().getResource("/sound/music/basement/mandatoryMillySection.mp3").toExternalForm()));
			soundSet.put("basementTheme1", new Media(getClass().getResource("/sound/music/basement/basementTheme1.mp3").toExternalForm()));
			soundSet.put("basementTheme2", new Media(getClass().getResource("/sound/music/basement/basementTheme2.mp3").toExternalForm()));
			soundSet.put("basementTheme3", new Media(getClass().getResource("/sound/music/basement/basementTheme3.mp3").toExternalForm()));
			soundSet.put("basementTheme4", new Media(getClass().getResource("/sound/music/basement/basementTheme4.mp3").toExternalForm()));
			soundSet.put("basementTheme5", new Media(getClass().getResource("/sound/music/basement/basementTheme5.mp3").toExternalForm()));
			soundSet.put("basementTheme6", new Media(getClass().getResource("/sound/music/basement/basementTheme6.mp3").toExternalForm()));
			soundSet.put("gasLeakSound", new Media(getClass().getResource("/sound/music/basement/gasLeakSound.mp3").toExternalForm()));
		}
		if(initializeGroups.equals("krunlic") || initializeGroups.equals("every")) {
			soundSet.put("krunlic1", new Media(getClass().getResource("/sound/music/krunlic/krunlic1.mp3").toExternalForm()));
			soundSet.put("krunlic2", new Media(getClass().getResource("/sound/music/krunlic/krunlic2.mp3").toExternalForm()));
			soundSet.put("krunlic3", new Media(getClass().getResource("/sound/music/krunlic/krunlic3.mp3").toExternalForm()));
			soundSet.put("krunlic4", new Media(getClass().getResource("/sound/music/krunlic/krunlic4.mp3").toExternalForm()));
			soundSet.put("krunlic5", new Media(getClass().getResource("/sound/music/krunlic/krunlic5.mp3").toExternalForm()));
		}
		if(initializeGroups.equals("shock") || initializeGroups.equals("every")) {
			soundSet.put("shockSfx", new Media(getClass().getResource("/sound/enemies/shock/shockSfx.mp3").toExternalForm()));
		}
		if(initializeGroups.equals("every")) {
			normalSounds();
		}
	}


	public MediaPlayer getMP3(String key, double volume) {
		Media media = soundSet.get(key);
		MediaPlayer player = new MediaPlayer(media);
		player.setVolume(volume);
		player.setRate(GamePanel.universalGameSpeedModifier);

		player.play();
		return player;
	}
	public MediaPlayer getMP3Rate(String key, double volume, double rate) {
		Media media = soundSet.get(key);
		MediaPlayer player = new MediaPlayer(media);
		player.setVolume(volume);
		player.setRate(rate * GamePanel.universalGameSpeedModifier);

		player.play();
		return player;
	}
	public MediaPlayer getMP3RateLooped(String key, double volume, double rate) {
		Media media = soundSet.get(key);
		MediaPlayer player = new MediaPlayer(media);
		player.setVolume(volume);
		player.setRate(rate * GamePanel.universalGameSpeedModifier);
		player.setCycleCount(MediaPlayer.INDEFINITE);

		player.play();
		return player;
	}
	public MediaPlayer getMP3(String key, double volume, double pan) {
		Media media = soundSet.get(key);
		MediaPlayer player = new MediaPlayer(media);
		player.setVolume(volume);
		player.setBalance(pan);
		player.setRate(GamePanel.universalGameSpeedModifier);
		
		player.play();

		return player;
	}
	public MediaPlayer getMP3ButPan(String key, double volume, double pan) {
		Media media = soundSet.get(key);
		MediaPlayer player = new MediaPlayer(media);
		player.setVolume(volume);
//		player.setPan(pan); NEEDS A FIX PLEASE
		player.setBalance(pan / 2);
		player.setRate(GamePanel.universalGameSpeedModifier);

		player.play();
		return player;
	}
	
	public MediaPlayer getMP3(String key, double volume, double pan, int cycleCount) {
		Media media = soundSet.get(key);
		MediaPlayer player = new MediaPlayer(media);
		player.setVolume(volume);
		player.setBalance(pan);
		player.setCycleCount(cycleCount);
		player.setRate(GamePanel.universalGameSpeedModifier);

		player.play();
		return player;
	}

}
