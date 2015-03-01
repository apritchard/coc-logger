package com.amp.coclogger.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.amp.coclogger.prefs.PrefName;
import com.amp.coclogger.prefs.PreferencesPanel;

public class CocLogger{
	private final static Preferences prefs = Preferences.userNodeForPackage(PreferencesPanel.class);
	
	private final static PreferencesPanel preferencesPanel = new PreferencesPanel();
	private final static CocLoggerPanel cocLoggerPanel = new CocLoggerPanel();
	
	public static void main(String[] args) {
		int appX = prefs.getInt(PrefName.APP_X.path(), 0);
		int appY = prefs.getInt(PrefName.APP_Y.path(), 0);
		int appWidth = prefs.getInt(PrefName.APP_WIDTH.path(), 600);
		int appHeight = prefs.getInt(PrefName.APP_HEIGHT.path(), 480);
		
		JFrame frame = new JFrame();
		frame.setSize(appWidth, appHeight);
		frame.setLocation(appX, appY);
		frame.setTitle("Text Parser");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new CocWindowListener(frame));
		
		frame.setJMenuBar(buildMenuBar());
		frame.getContentPane().add(cocLoggerPanel);
		
		preferencesPanel.addListener(cocLoggerPanel);
		frame.setVisible(true);
	}
	
	private static JMenuBar buildMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);
		
		JMenuItem preferencesMenuItem = new JMenuItem("Preferences", KeyEvent.VK_P);
		preferencesMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.ALT_MASK));
		preferencesMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				frame.setSize(400,300);
				frame.getContentPane().add(preferencesPanel);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setVisible(true);
			}
		});
		fileMenu.add(preferencesMenuItem);
		
		return menuBar;
	}
}
