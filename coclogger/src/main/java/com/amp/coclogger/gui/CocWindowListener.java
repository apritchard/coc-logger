package com.amp.coclogger.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

import com.amp.coclogger.prefs.PrefName;
import com.amp.coclogger.prefs.PreferencesPanel;

public class CocWindowListener 	implements WindowListener{
	private static final Preferences prefs = Preferences.userNodeForPackage(PreferencesPanel.class);
	JFrame frame;
	
	public CocWindowListener(JFrame frame){
		this.frame = frame;
	}


	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		prefs.putInt(PrefName.APP_X.path(), frame.getX());
		prefs.putInt(PrefName.APP_Y.path(), frame.getY());
		prefs.putInt(PrefName.APP_WIDTH.path(), frame.getWidth());
		prefs.putInt(PrefName.APP_HEIGHT.path(), frame.getHeight());
		
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
