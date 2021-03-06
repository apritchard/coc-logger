package com.amp.coclogger.gui.coclogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import com.amp.coclogger.gui.autonexter.AutoNexter;
import com.amp.coclogger.gui.util.CocWindowListener;
import com.amp.coclogger.math.CocData;
import com.amp.coclogger.math.CocStats;
import com.amp.coclogger.prefs.League;
import com.amp.coclogger.prefs.PrefName;
import com.amp.coclogger.prefs.PreferencesPanel;

public class CocLogger{
	
	private final static Logger logger = Logger.getLogger(CocLogger.class);
	private final static PreferencesPanel preferencesPanel = new PreferencesPanel();
	private final static CocLoggerPanel cocLoggerPanel = new CocLoggerPanel();
	
	public static void main(String[] args) {
		int appX = PrefName.APP_X.getInt();
		int appY = PrefName.APP_Y.getInt();
		int appWidth = PrefName.APP_WIDTH.getInt();
		int appHeight = PrefName.APP_HEIGHT.getInt();
		
		JFrame frame = new JFrame();
		frame.setSize(appWidth, appHeight);
		frame.setLocation(appX, appY);
		frame.setTitle("Text Parser");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new CocWindowListener(frame));
		
		frame.setJMenuBar(buildMenuBar());
		frame.getContentPane().add(cocLoggerPanel);
		
		String saveData = PrefName.DATA_SAVE_LOCATION.get();
		CocData.getInstance().readFile(saveData);
		
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
				frame.getContentPane().add(preferencesPanel);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.pack();
				frame.setVisible(true);
			}
		});
		fileMenu.add(preferencesMenuItem);
		
		JMenuItem statisticsMenuItem = new JMenuItem("Statistics", KeyEvent.VK_S);
		statisticsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		statisticsMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				logger.info("Running stats");
				CocStats stats = CocData.getInstance().getStats();
				for(League l : League.values()){
					for(int i = 1 ; i <= 10 ; i++){
						if(stats.getStat(l, i).getEntries() > 0){
							logger.info(l + " as Townhall " + i);
							logger.info(stats.getStat(l, i).displayString());
						}
					}
				}
				
			}
		});
		fileMenu.add(statisticsMenuItem);
		
		JMenuItem autoNexterMenuItem = new JMenuItem("Auto Nexter", KeyEvent.VK_A);
		autoNexterMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
		autoNexterMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AutoNexter an = new AutoNexter();
				an.setVisible(true);
			}
		});
		fileMenu.add(autoNexterMenuItem);
		
		return menuBar;
	}
}
