package com.amp.coclogger.prefs;

import java.util.prefs.Preferences;

public enum PrefName {
	MONITOR_DELAY("monitor-delay", PrefType.INTEGER, 1, true), 
	LANGUAGE("language", PrefType.STRING, "coc", true), 
	IMAGE_SAVE_RAW("save-raw-image", PrefType.BOOLEAN, false, true),
	IMAGE_SAVE_PROCESSED("save-processed-image", PrefType.BOOLEAN, false, true),
	IMAGE_SAVE_PATH("image-save-path", PrefType.DIRECTORY, "", true),
	DATA_SAVE_LOCATION("data-save-location", PrefType.FILE, "defaultCocData.cocd", true),
	IMAGE_SAVE_PREFIX("image-save-prefix", PrefType.STRING, "coc_font", true),
	RAW_IMAGE_SUFFIX("raw-image-suffix", PrefType.STRING, "raw", true),
	PROCESSED_IMAGE_SUFFIX("processed-image-suffix", PrefType.STRING, "" , true),
	IMAGE_FILE_TYPE("image-file-type", PrefType.ENUM_SINGLE, ImageFileType.TIFF.toString(), true),
	IMAGES_PER_PAGE("images-per-page", PrefType.INTEGER, 9, true),
	TOWNHALL("townhall-level", PrefType.ENUM_SINGLE, Townhall.TH5, true),
	LEAGUE("league", PrefType.ENUM_SINGLE, League.BRONZE3, true),
	
	
	//not editable by users
	TEXT_X("text-x", PrefType.INTEGER, 0, false),
	TEXT_Y("text-y", PrefType.INTEGER, 0, false),
	TEXT_WIDTH("text-width", PrefType.INTEGER, 1, false),
	TEXT_HEIGHT("text-height", PrefType.INTEGER, 1, false),
	COC_X("coc-x", PrefType.INTEGER, 0, false),
	COC_Y("coc-y", PrefType.INTEGER, 0, false),
	COC_WIDTH("coc-width", PrefType.INTEGER, 1, false),
	COC_HEIGHT("coc-height", PrefType.INTEGER, 1, false),
	PLAYER_LEAGUE_X("player-league-x", PrefType.INTEGER, 0, false),
	PLAYER_LEAGUE_Y("player-league-y", PrefType.INTEGER, 0, false),
	PLAYER_LEAGUE_WIDTH("player-league-width", PrefType.INTEGER, 1, false),
	PLAYER_LEAGUE_HEIGHT("player-league-height", PrefType.INTEGER, 1, false),
	ENEMY_LEAGUE_X("enemy-league-x", PrefType.INTEGER, 0, false),
	ENEMY_LEAGUE_Y("enemy-league-y", PrefType.INTEGER, 0, false),
	ENEMY_LEAGUE_WIDTH("enemy-league-width", PrefType.INTEGER, 1, false),
	ENEMY_LEAGUE_HEIGHT("enemy-league-height", PrefType.INTEGER, 1, false),
	PLAYER_TH_X("player-th-x", PrefType.INTEGER, 0, false),
	PLAYER_TH_Y("player-th-y", PrefType.INTEGER, 0, false),
	PLAYER_TH_WIDTH("player-th-width", PrefType.INTEGER, 1, false),
	PLAYER_TH_HEIGHT("player-th-height", PrefType.INTEGER, 1, false),
	NEXT_X("next-x", PrefType.INTEGER, 0, false),
	NEXT_Y("next-y", PrefType.INTEGER, 0, false),
	ATTACK_X("attack-x", PrefType.INTEGER, 0, false),
	ATTACK_Y("attack-y", PrefType.INTEGER, 0, false),
	FIND_X("find-x", PrefType.INTEGER, 0, false),
	FIND_Y("find-y", PrefType.INTEGER, 0, false),
	APP_X("app-x", PrefType.INTEGER, 0, false),
	APP_Y("app-y", PrefType.INTEGER, 0, false),
	APP_WIDTH("app-width", PrefType.INTEGER, 640, false),
	APP_HEIGHT("app-height", PrefType.INTEGER, 480, false),
	AUTO_X("auto-x", PrefType.INTEGER, 0, false),
	AUTO_Y("auto-y", PrefType.INTEGER, 0, false),
	AUTO_WIDTH("auto-width", PrefType.INTEGER, 640, false),
	AUTO_HEIGHT("auto-height", PrefType.INTEGER, 480, false);
	
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
