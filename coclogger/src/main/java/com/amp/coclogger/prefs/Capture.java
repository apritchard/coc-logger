package com.amp.coclogger.prefs;

public enum Capture {
	POSITION_CLASH(CaptureType.DELAY, "Position Clash on your screen with no windows covering it, then press OK"),
	FULL_SCREEN(CaptureType.RECTANGLE, "Drag a box around the entire clash screen"),
	PLAYER_LEAGUE(CaptureType.RECTANGLE, "Drag a box around your league icon"),
	ATTACK_ICON(CaptureType.POINT, "Click the \"Attack\" button"),
	ZOOM_OUT(CaptureType.DELAY, "Zoom all the way out, then press OK"),
	PLAYER_TOWNHALL(CaptureType.RECTANGLE, "Drag a box around your town hall"),
	SWITCH_TO_FIND(CaptureType.DELAY, "Switch to the Find a Match screen by pressing Attack, then click OK"),
	FIND_A_MATCH_ICON(CaptureType.POINT, "Click the \"Find a Match\" icon"),
	SWITCH_TO_COMBAT(CaptureType.DELAY, "Switch to the combat screen by pressing \"Find a Match\", then click OK"),
	NUMS(CaptureType.RECTANGLE, "Drag a box around all text below \"Available Loot\", but not the icons"),
	NEXT_BUTTON(CaptureType.POINT, "Click on the \"Next\" button");

	
	CaptureType captureType;
	String hintText;
	
	private Capture(CaptureType captureType, String hintText){
		this.captureType = captureType;
		this.hintText = hintText;
	}

	public CaptureType getCaptureType() {
		return captureType;
	}

	public String getHintText() {
		return hintText;
	}
	
}
