package com.amp.coclogger.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class SelectionPicker extends JFrame {
	
	private int x1, x2, y1, y2;
	private int width, height;
	private boolean showRectangle;
	
	private PointListener pointListener;
	
	public static void main(String[] args) {
		SelectionPicker sp = new SelectionPicker(new PointListener() {
			
			@Override
			public void notifySelection(int x, int y, int width, int height) {
				System.out.println("got it");
				
			}
		}, "Text to display", true);
		sp.setVisible(true);
	}
	
	public SelectionPicker(final PointListener pointListener, String text, boolean showRectangle){
		this.showRectangle = showRectangle;
		
		setUndecorated(true);
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		width = gd.getDisplayMode().getWidth();
		height = gd.getDisplayMode().getHeight();
		setSize(width, height);
		setBackground(new Color(0f, 0f, 0f, 0f));
		
		DrawPanel drawPanel = new DrawPanel(text);
		add(drawPanel);
		
		
		this.pointListener = pointListener;
		DragBoxAdapter adapter = new DragBoxAdapter();
		addMouseListener(adapter);
		addMouseMotionListener(adapter);
	}
	
	class DrawPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		String text; 
		
		public DrawPanel(String text){
			this.text = text;
			setBackground(new Color(0f, 0f, 0f, 0.1f));
		}
		
		public void paint(Graphics g){
			super.paint(g);
			if(showRectangle){
				g.setColor(new Color(0f, 0f, 0f, 0.1f));
				g.fillRect(x1, y1, (x2-x1), (y2-y1));
			}
			
			Graphics2D g2d = (Graphics2D) g;
			FontRenderContext frc = g2d.getFontRenderContext(); 
			
			Font font = new Font("Helvetica", Font.BOLD, 62);
			GlyphVector gv = font.createGlyphVector(frc, text);
			Rectangle2D box = gv.getVisualBounds();
			int xOff = width/2 + (int)-box.getX();
			int yOff = height/2 + (int)-box.getY();
			Shape shape = gv.getOutline(xOff, yOff);
			g2d.setClip(shape);
			g2d.setColor(Color.WHITE);
			g2d.fill(shape);
			
			g2d.setStroke(new BasicStroke(2f));
			g2d.setColor(Color.BLACK);;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.draw(shape);
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
				pointListener.notifySelection(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2));
				setVisible(false);
				dispose();
			}		
	}
}
