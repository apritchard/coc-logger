package com.amp.coclogger.gui.autonexter;

import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import com.amp.coclogger.prefs.IntegerFilter;


public class StringParameter extends Parameter {
	
	public StringParameter(String name) {
		super(name);
		JTextField txtField = new JTextField();
		setComponent(txtField);
	}
	
	public String get(){
		return ((JTextField)getComponent()).getText();
	}

}
