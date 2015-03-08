package com.amp.coclogger.prefs;

public enum Capture {
//	FULL_SCREEN(CaptureType.RECTANGLE, "Drag a box around the entire clash screen"),
//	PLAYER_LEAGUE(CaptureType.POINT, "Click your own league icon"),
//	SWITCH_TO_COMBAT(CaptureType.DELAY, "Switch to the battle screen - Attack->Find A Match, Then press OK"),
	NUMS(CaptureType.RECTANGLE, "Drag a rectangle around all text below \"Available Loot\", but no icons"){ 
//	NEXT_BUTTON(CaptureType.POINT, "Click on the \"Next\" button"){
        @Override
        public Capture next() {
            return null; // last capture returns null on "next"
        };
	};

	
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
	
    public Capture next() {
        // No bounds checking required here, because the last instance overrides
        return values()[ordinal() + 1];
    }
	
}
