package com.amp.coclogger.prefs;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import net.miginfocom.swing.MigLayout;

public class PreferencesPanel extends JPanel {
	
	Preferences prefs = Preferences.userNodeForPackage(PreferencesPanel.class);
	
	private Map<JTextField, String> textFields = new HashMap<>();
	private Map<JTextField, String> integerFields = new HashMap<>();
	private Map<JCheckBox, String> checkBoxes = new HashMap<>();
	
	JLabel statusLabel = new JLabel("");

	public PreferencesPanel(){
		setLayout(new MigLayout());
		
		for(PrefName prefName : PrefName.values()){
			switch(prefName.getType()){
			case BOOLEAN:
				addCheckbox(prefName);
				break;
			case DIRECTORY:
				addDirectory(prefName);
				break;
			case INTEGER:
				addInteger(prefName);
				break;
			case STRING:
				addString(prefName);
				break;
			}
		}
		
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(Entry<JTextField, String> entry : textFields.entrySet()){
					prefs.put(entry.getValue(), entry.getKey().getText());
				}
				
				for(Entry<JTextField, String> entry : integerFields.entrySet()){
					int value = entry.getKey().getText().isEmpty() ? 
							0 : Integer.parseInt(entry.getKey().getText());
					prefs.putInt(entry.getValue(), value);
				}
				
				for(Entry<JCheckBox, String> entry : checkBoxes.entrySet()){
					prefs.putBoolean(entry.getValue(), entry.getKey().isSelected());
				}
				
				statusLabel.setText("Preferences saved.");
			}
		});
		add(saveButton);
		add(statusLabel);
	}

	private void addString(PrefName prefName) {
		add(new JLabel(prefName.getPathName()));
		JTextField textField = new JTextField();
		textField.setText(prefs.get(prefName.getPathName(), ""));
		add(textField, "width 200px, wrap");
		textFields.put(textField, prefName.getPathName());
	}

	private void addCheckbox(PrefName prefName) {
		add(new JLabel(prefName.getPathName()));
		JCheckBox ckbx = new JCheckBox();
		ckbx.setSelected(prefs.getBoolean(prefName.getPathName(), false));
		add(ckbx, "width 100px, wrap");
		checkBoxes.put(ckbx, prefName.getPathName());
	}
	
	private void addInteger(PrefName prefName){
		add(new JLabel(prefName.getPathName()));
		JTextField textField = new JTextField();
		((PlainDocument)textField.getDocument()).setDocumentFilter(new IntegerFilter());
		textField.setText("" + prefs.getInt(prefName.getPathName(),  0));
		add(textField, "width 60px, wrap");
		integerFields.put(textField, prefName.getPathName());
	}
	
	private void addDirectory(final PrefName prefName){
		add(new JLabel(prefName.getPathName()));
		
		final JTextField textField = new JTextField();
		final Component chooserParent = this;
		
		textField.setText(prefs.get(prefName.getPathName(), ""));
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseDirectory(textField, prefName, chooserParent);
			}
		});		
		add(textField, "width 300px");
		
		JButton browseButton = new JButton("Browse");
		browseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseDirectory(textField, prefName, chooserParent);
			}
		});
		add(browseButton, "wrap");
	}
	
	private void chooseDirectory(JTextField textField, PrefName prefName, Component chooserParent){
		JFileChooser chooser = new JFileChooser();
		String previousDirectory = textField.getText();
		if(!previousDirectory.isEmpty()){
			chooser.setCurrentDirectory(new File(previousDirectory));
		} else {
			chooser.setCurrentDirectory(new File("."));
		}
		chooser.setDialogTitle("Select " + prefName.getPathName() + " Directory");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		
		if (chooser.showOpenDialog(chooserParent) == JFileChooser.APPROVE_OPTION){
			textField.setText(chooser.getSelectedFile().getAbsolutePath());
		}
		

	}
	
}
