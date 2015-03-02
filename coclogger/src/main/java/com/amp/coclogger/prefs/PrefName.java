package com.amp.coclogger.prefs;

import java.util.prefs.Preferences;

public enum PrefName {
	MONITOR_DELAY("monitor-delay", PrefType.INTEGER, 1, true), 
	LANGUAGE("language", PrefType.STRING, "coc", true), 
	IMAGE_SAVE_ACTIVE("image-save-active", PrefType.BOOLEAN, false, true),
	IMAGE_SAVE_PATH("image-save-path", PrefType.DIRECTORY, "", true), 
	IMAGE_SAVE_PREFIX("image-save-prefix", PrefType.STRING, "coc_font", true),
	IMAGES_PER_PAGE("images-per-page", PrefType.INTEGER, 9, true),
	TOWN_HALL_LEVEL("townhall-level", PrefType.INTEGER, 5, true),
	LEAGUE("league", PrefType.ENUM_SINGLE, League.BRONZEIII, true),
	DATA_SAVE_LOCATION("data-save-location", PrefType.FILE, "defaultCocData.cocd", true),
	
	
	//not editable by users
	TEXT_X("text-x", PrefType.INTEGER, 0, false),
	TEXT_Y("text-y", PrefType.INTEGER, 0, false),
	TEXT_WIDTH("text-width", PrefType.INTEGER, 1, false),
	TEXT_HEIGHT("text-height", PrefType.INTEGER, 1, false),
	LEAGUE_X("league-x", PrefType.INTEGER, 0, false),
	LEAGUE_Y("league-y", PrefType.INTEGER, 0, false),
	LEAGUE_WIDTH("league-width", PrefType.INTEGER, 1, false),
	LEAGUE_HEIGHT("league-height", PrefType.INTEGER, 1, false),
	APP_X("app-x", PrefType.INTEGER, 0, false),
	APP_Y("app-y", PrefType.INTEGER, 0, false),
	APP_WIDTH("app-width", PrefType.INTEGER, 640, false),
	APP_HEIGHT("app-height", PrefType.INTEGER, 480, false);
	
	private static final Preferences prefs = Preferences.userNodeForPackage(PreferencesPanel.class);	
	
	private String pathName; 
	private PrefType type;
	private Object defaultValue;
	private boolean editable;
	
	PrefName(String pathName, PrefType type, Object defaultValue, boolean editable){
		this.pathName = pathName;
		this.type = type;
		this.defaultValue = defaultValue;
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
	
	public String defaultString(){
		return defaultValue.toString();
	}
	
	public int defaultInt(){
		try{
			int value = Integer.parseInt(defaultValue.toString());
			return value;
		} catch (NumberFormatException nfe){
			nfe.printStackTrace();
			System.out.println("Int requested from non-int preference value (" + defaultValue + ") on " + toString());
			return 0;
		}
	}
	
	public boolean defaultBoolean(){
		if(defaultValue.toString().equalsIgnoreCase("true")){
			return true;
		} else if (defaultValue.toString().equalsIgnoreCase("false")){
			return false;
		} else {
			System.out.println("Boolean requested from non-boolean preference value (" + defaultValue +") on " + toString());
			return false;
		}
	}
	
	public String get(){
		return prefs.get(pathName, defaultString());
	}
	
	public int getInt(){
		return prefs.getInt(pathName, defaultInt());
	}
	
	public boolean getBoolean(){
		return prefs.getBoolean(pathName, defaultBoolean());
	}
	
	public void put(String value){
		prefs.put(pathName, value);
	}
	
	public void putInt(int value){
		prefs.putInt(pathName, value);
	}
	
	public void putBoolean(boolean value){
		prefs.putBoolean(pathName, value);
	}
	
	
}
