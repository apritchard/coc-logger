package com.amp.coclogger.gui.autonexter;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import com.amp.coclogger.prefs.PrefName;

public class NexterWindowListener implements WindowListener {
	private static final Logger logger = Logger.getLogger(NexterWindowListener.class);
	
	private JFrame frame;
	
	public NexterWindowListener(JFrame frame){
		this.frame = frame;
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		logger.debug(String.format("Saving location %d,%d %dx%d", frame.getX(), frame.getY(), frame.getWidth(), frame.getHeight()));
		PrefName.AUTO_X.putInt(frame.getX());
		PrefName.AUTO_Y.putInt(frame.getY());
		PrefName.AUTO_WIDTH.putInt(frame.getWidth());
		PrefName.AUTO_HEIGHT.putInt(frame.getHeight());

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
