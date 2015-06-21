package com.amp.coclogger.gui.util;

import java.io.BufferedInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;

public enum AudioUtil {
	DONE("done.wav"),
	FAIL("fail.wav");
	
	private final Logger logger = Logger.getLogger(AudioUtil.class);
	
	private String soundFile;

	private AudioUtil(String soundFile){
		this.soundFile = soundFile;
	}
	
	public void play(){
		Clip clip;
		try (AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(AudioUtil.class.getResourceAsStream("/audio/" + soundFile)))){
			clip = AudioSystem.getClip();
			clip.open(ais); 
			clip.start();
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			logger.error("Unable to play sound " + this, e);
		}

	}
	
}
