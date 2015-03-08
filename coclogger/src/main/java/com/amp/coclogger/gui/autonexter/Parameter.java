package com.amp.coclogger.gui.autonexter;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public abstract class Parameter  {
	private String name;
	private JComponent component;
	private String defaultValue;

	public Parameter(String name){
		this.name = name;
	}
	
	public void setComponentValue(String value) {
		((JTextField)getComponent()).setText(value);		
	}

	public String getSerialString() {
		return ((JTextField)getComponent()).getText();
	}
	
	public void addToPanel(JPanel panel){
		panel.add(new JLabel(getName()));
		panel.add(getComponent(), "w 60%, wrap");
	}
	
	public String getName() {
		return name;
	}

	public JComponent getComponent() {
		return component;
	}

	public void setComponent(JComponent component) {
		this.component = component;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
}
