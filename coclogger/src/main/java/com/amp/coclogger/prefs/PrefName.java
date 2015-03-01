package com.amp.coclogger.prefs;

public enum PrefName {
	MONITOR_DELAY("monitor-delay", PrefType.INTEGER, true), 
	LANGUAGE("language", PrefType.STRING, true), 
	IMAGE_SAVE_ACTIVE("image-save-active", PrefType.BOOLEAN, true),
	IMAGE_SAVE_PATH("image-save-path", PrefType.DIRECTORY, true), 
	IMAGE_SAVE_PREFIX("image-save-prefix", PrefType.STRING, true),
	IMAGES_PER_PAGE("images-per-page", PrefType.INTEGER, true),
	TOWN_HALL_LEVEL("townhall-level", PrefType.INTEGER, true),
	LEAGUE("league", PrefType.ENUM_SINGLE, true),
	
	
	//not editable by users
	TEXT_X("text-x", PrefType.INTEGER, false),
	TEXT_Y("text-y", PrefType.INTEGER, false),
	TEXT_WIDTH("text-width", PrefType.INTEGER, false),
	TEXT_HEIGHT("text-height", PrefType.INTEGER, false),
	LEAGUE_X("league-x", PrefType.INTEGER, false),
	LEAGUE_Y("league-y", PrefType.INTEGER, false),
	LEAGUE_WIDTH("league-width", PrefType.INTEGER, false),
	LEAGUE_HEIGHT("league-height", PrefType.INTEGER, false),
	APP_X("app-x", PrefType.INTEGER, false),
	APP_Y("app-y", PrefType.INTEGER, false),
	APP_WIDTH("app-width", PrefType.INTEGER, false),
	APP_HEIGHT("app-height", PrefType.INTEGER, false);
	
	
	private String pathName; 
	private PrefType type;
	private boolean editable;
	
	PrefName(String pathName, PrefType type, boolean editable){
		this.pathName = pathName;
		this.type = type;
		this.editable = editable;
	}
	
	public String path(){
		return pathName;
	}
	
	public PrefType getType(){
		return type;
	}
	
	public boolean isEditable(){
		return editable;
	}
}
