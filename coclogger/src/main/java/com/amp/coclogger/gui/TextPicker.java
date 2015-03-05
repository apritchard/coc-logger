package com.amp.coclogger.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class TextPicker extends JFrame {
	
	private int x1, x2, y1, y2;
	
	private SelectionListener selectionListener;
	
	public static void main(String[] args) {
		TextPicker tp = new TextPicker(new SelectionListener() {
			
			@Override
			public void notifySelection(int x, int y, int width, int height) {
				System.out.println("got it");
				
			}
		}, "Text to display");
		tp.setVisible(true);
	}
	
	public TextPicker(final SelectionListener selectionListener, String text){
		setUndecorated(true);
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		setSize(width, height);
		setBackground(new Color(0f, 0f, 0f, 0f));
		
		DrawPanel drawPanel = new DrawPanel(text);
		add(drawPanel);
		
		
		this.selectionListener = selectionListener;
		DragBoxAdapter adapter = new DragBoxAdapter();
		addMouseListener(adapter);
		addMouseMotionListener(adapter);
	}
	
	class DrawPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		public DrawPanel(String text){
			setLayout(new MigLayout());
			JLabel label = new JLabel(text);
			label.setFont(new Font("Helvetica", Font.PLAIN, 48));
			label.setForeground(Color.RED);
			setBackground(new Color(0f, 0f, 0f, 0.1f));
			add(label, "push, align center");
		}
		
		public void paint(Graphics g){
			super.paint(g);
			g.setColor(new Color(0f, 0f, 0f, 0.1f));
			g.fillRect(x1, y1, (x2-x1), (y2-y1));
		}
	}
	
	class DragBoxAdapter extends MouseAdapter {

		@Override
			public void mousePressed(MouseEvent e){
				x1 = e.getX();
				y1 = e.getY();
			}
			
			@Override
			public void mouseDragged(MouseEvent e){
				x2 = e.getX();
				y2 = e.getY();
				repaint();
			}
			
			@Override
			public void mouseReleased(MouseEvent e){
				int x = Math.min(x1,x2);
				int y = Math.min(y1, y1);
				int width = Math.max(x1, x2)-x;
				int height = Math.max(y1, y2)-y;
				selectionListener.notifySelection(x, y, width, height);
				setVisible(false);
				dispose();
			}		
	}
}
