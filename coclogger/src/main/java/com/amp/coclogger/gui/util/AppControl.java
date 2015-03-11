package com.amp.coclogger.gui.util;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Utility methods for manipulating the clash game in bluestacks through
 * mouse clicks and keyboard entry. Does not know anything about the
 * application, other than location parameters specified by the caller.
 * @author alex
 *
 */
public class AppControl {
	
	private static Robot rob;
	static {
		try{
			System.out.println("Initializing Robot.");
			rob = new Robot();
			System.out.println("Finished!");
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
