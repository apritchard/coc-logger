package com.amp.coclogger.prefs;

public enum PrefName {
	MONITOR_DELAY("monitor-delay", PrefType.INTEGER), 
	LANGUAGE("language", PrefType.STRING), 
	IMAGE_SAVE_ACTIVE("image-save-active", PrefType.BOOLEAN),
	IMAGE_SAVE_PATH("image-save-path", PrefType.DIRECTORY), 
	IMAGE_SAVE_PREFIX("image-save-prefix", PrefType.STRING);
	
	private String pathName; 
	private PrefType type;
	
	PrefName(String pathName, PrefType type){
		this.pathName = pathName;
		this.type = type;
	}
	
	public String getPathName(){
		return pathName;
	}
	
	public PrefType getType(){
		return type;
	}
}
