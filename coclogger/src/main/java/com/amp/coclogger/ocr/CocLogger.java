package com.amp.coclogger.ocr;

import javax.swing.JFrame;

public class CocLogger {
	public static void main(String[] args) {
		
		JFrame frame = new JFrame();
		frame.setSize(600, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		CocLoggerPanel panel = new CocLoggerPanel();
		frame.getContentPane().add(panel);
		frame.setVisible(true);
	}
}
