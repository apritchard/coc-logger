package com.amp.coclogger.prefs;

import java.awt.Point;
import java.awt.Rectangle;

public enum CaptureType {
	POINT(Point.class), RECTANGLE(Rectangle.class), DELAY(Boolean.class);
	
	Class<?> clazz;

	<T> CaptureType(Class<T> clazz){
		this.clazz = clazz;
	}
	
	public Class<?> getClazz() {
		return clazz;
	}
}
