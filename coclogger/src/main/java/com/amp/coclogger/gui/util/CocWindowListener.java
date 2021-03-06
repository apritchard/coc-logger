package com.amp.coclogger.gui.util;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import com.amp.coclogger.math.CocData;
import com.amp.coclogger.prefs.PrefName;

public class CocWindowListener 	implements WindowListener{
	private JFrame frame;
	
	public CocWindowListener(JFrame frame){
		this.frame = frame;
	}


	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		PrefName.APP_X.putInt(frame.getX());
		PrefName.APP_Y.putInt(frame.getY());
		PrefName.APP_WIDTH.putInt(frame.getWidth());
		PrefName.APP_HEIGHT.putInt(frame.getHeight());
		
		CocData.getInstance().writeFile(PrefName.DATA_SAVE_LOCATION.get());
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
