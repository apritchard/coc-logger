package com.amp.coclogger.gui.autonexter;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.amp.coclogger.prefs.PrefName;

public class AutoNexter extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public static final String PARAM_ELIXIR = "Minimum elixir";
	public static final String PARAM_GOLD = "Minimum gold";
	public static final String PARAM_DARK_ELIXIR = "Dark elixir";
	public static final String PARAM_TROPHIES_WIN = "Trophies win";
//	public static final String PARAM_TROPHIES_LOST = "Trophies lost";
	public static final String PARAM_TIMEOUT = "Maximum wait";

	public AutoNexter(){
		addWindowListener(new NexterWindowListener(this));
		
		Parameter elixir = new IntegerParameter(PARAM_ELIXIR, 0, 2000000);
		Parameter gold = new IntegerParameter(PARAM_GOLD, 0, 2000000);
		Parameter darkElixir = new IntegerParameter(PARAM_DARK_ELIXIR, 0, 5000);
		Parameter trophiesWin = new IntegerParameter(PARAM_TROPHIES_WIN, 0, 50);
//		Parameter trophiesLose = new IntegerParameter(PARAM_TROPHIES_LOST, 0, 50);
		Parameter delay = new IntegerParameter(PARAM_TIMEOUT, 1, 60);
		
		List<Parameter> parameters = new ArrayList<>();
		parameters.add(elixir);
		parameters.add(gold);
		parameters.add(darkElixir);
		parameters.add(trophiesWin);
//		parameters.add(trophiesLose);
		parameters.add(delay);
		
		JPanel parmForm = new ParameterPanel("AutoNext Parameters", parameters);
		getContentPane().setLayout(new MigLayout());
		getContentPane().add(parmForm);
		getContentPane().add(new NexterPanel(parameters));
		pack();
		
		int appX = PrefName.AUTO_X.getInt();
		int appY = PrefName.AUTO_Y.getInt();
		int appWidth = PrefName.AUTO_WIDTH.getInt();
		int appHeight = PrefName.AUTO_HEIGHT.getInt();
		
		setSize(appWidth, appHeight);
		setLocation(appX, appY);
		setTitle("AutoNexter");
	}
}
