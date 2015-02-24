package com.amp.coclogger.ocr;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.amp.coclogger.prefs.PreferencesPanel;

public class CocLogger {
	public static void main(String[] args) {
		
		JFrame frame = new JFrame();
		frame.setSize(600, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setJMenuBar(buildMenuBar());
		
		CocLoggerPanel panel = new CocLoggerPanel();
		frame.getContentPane().add(panel);
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
				PreferencesPanel p = new PreferencesPanel();
				JFrame frame = new JFrame();
				frame.setSize(400,300);
				frame.getContentPane().add(p);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setVisible(true);
			}
		});
		fileMenu.add(preferencesMenuItem);
		
		return menuBar;
	}
}
