package com.amp.coclogger.gui.autonexter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.prefs.Preferences;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ParameterPanel extends JPanel {
	private static final Preferences prefs = Preferences.userNodeForPackage(Parameter.class);
	private static final String PARM_PREF = "saved-parameters"; 
	private static final Logger logger = Logger.getLogger(ParameterPanel.class);

	public ParameterPanel(String formName, List<Parameter> parameters) {

		setLayout(new MigLayout());

		for (Parameter parameter : parameters) {
			parameter.addToPanel(this);
		}
		
		loadPreferences(formName, parameters);
	}

	private static void loadPreferences(String formName, List<Parameter> parameters) {
		Gson gson = new Gson();
		String json = prefs.get(formName + "-" + PARM_PREF, "");
		if(json.isEmpty()){
			return; //no previous preferences
		}
		Type type = new TypeToken<HashMap<String, String>>(){}.getType();
		Map<String, String> parmMap = gson.fromJson(json, type);
		if(parmMap == null){
			logger.warn("Error reading previous previous parms for " + formName);
			return;
		}
		for(Parameter parameter : parameters){
			String val = parmMap.get(parameter.getName());
			if(val != null && !val.isEmpty()){
				parameter.setComponentValue(val);
			}
		}
	}

	private static void savePreferences(String formName, List<Parameter> parameters) {
		Map<String, String> parmMap = new HashMap<>();
		for(Parameter parameter : parameters){
			parmMap.put(parameter.getName(), parameter.getSerialString());
		}
		Gson gson = new Gson();
		String json = gson.toJson(parmMap);
		prefs.put(formName + "-" + PARM_PREF, json);
	}

}
