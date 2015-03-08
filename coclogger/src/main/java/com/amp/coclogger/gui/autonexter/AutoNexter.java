package com.amp.coclogger.gui.autonexter;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.amp.coclogger.prefs.PrefName;

public class AutoNexter extends JFrame {

	public AutoNexter(){
		Parameter elixir = new IntegerParameter("Minimum elixir", 0, 2000000);
		Parameter gold = new IntegerParameter("Minimum gold", 0, 2000000);
		Parameter darkElixir = new IntegerParameter("Dark elixir", 0, 5000);
		Parameter trophiesWin = new IntegerParameter("Trophies win", 0, 50);
		Parameter trophiesLose = new IntegerParameter("Trophies lose", 0, 50);
		
		List<Parameter> parameters = new ArrayList<>();
		parameters.add(elixir);
		parameters.add(gold);
		parameters.add(darkElixir);
		parameters.add(trophiesWin);
		parameters.add(trophiesLose);
		
		JPanel parmForm = new ParameterForm("AutoNext Parameters", parameters);
		getContentPane().add(parmForm);
		pack();
		
		int appX = PrefName.AUTO_X.getInt();
		int appY = PrefName.AUTO_Y.getInt();
		int appWidth = PrefName.AUTO_WIDTH.getInt();
		int appHeight = PrefName.AUTO_HEIGHT.getInt();
		
		JFrame frame = new JFrame();
		frame.setSize(appWidth, appHeight);
		frame.setLocation(appX, appY);
		frame.setTitle("AutoNexter");
	}
}
