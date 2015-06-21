package com.amp.coclogger.gui.util;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.apache.log4j.Logger;

/**
 * Utility methods for manipulating the clash game in bluestacks through
 * mouse clicks and keyboard entry. Does not know anything about the
 * application, other than location parameters specified by the caller.
 * 
 * At some point, replace with OS-specific control? Since Robot breaks
 * mouse default move speed on windows.
 * @author alex
 *
 */
public class AppControl {
	private static final Logger logger = Logger.getLogger(AppControl.class); 
	private static Robot rob;
	static {
		try{
			logger.info("Initializing Robot.");
			rob = new Robot();
			logger.info("Finished!");
		} catch (AWTException awte){
			awte.printStackTrace();
		}
	}
	
	/**
	 * Zooms all the way out in the target app
	 * @param x X coordinate somewhere on the target app, to click it once
	 * @param y Y coordinate somewhere on the target app
	 */
	public static void zoomOutFull(int x, int y){
		clickMouse(x, y);
		for(int i = 0; i<15 ; i++){
			pressKey(KeyEvent.VK_DOWN);
		}
	}
	
	public static void pressKey(int keycode){
		rob.setAutoDelay(100);
		rob.keyPress(keycode);
		rob.keyRelease(keycode);
	}
	
	public static void clickMouse(int x, int y){
		rob.mouseMove(x, y);
		rob.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		rob.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
}
