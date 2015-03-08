package com.amp.coclogger.gui.autonexter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import com.amp.coclogger.prefs.IntegerFilter;


public class IntegerParameter extends Parameter {
	
	public IntegerParameter(String name) {
		super(name);
		addTextField(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	
	public IntegerParameter(String name, int min, int max){
		super(name);
		addTextField(min, max);
	}
	
	private void addTextField(int min, int max){
		JTextField txtField = new JTextField();
		((PlainDocument)txtField.getDocument()).setDocumentFilter(new IntegerFilter(min, max));
		setComponent(txtField);
	}
	
	public int getInt(){
		return Integer.parseInt(((JTextField)getComponent()).getText());
	}

}
