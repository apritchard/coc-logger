package com.amp.coclogger.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.amp.coclogger.prefs.Capture;

public class ScreenCaptureManager implements PointListener {

	private Map<Capture, Object> captureMap = new EnumMap<>(Capture.class);
	private SelectionListener listener;
	
	private Capture currentCapture;
	
	public ScreenCaptureManager(SelectionListener listener){
		this.listener = listener;
	}
	
	public void capture(){
		capture(Capture.values()[0]);
	}
	
	/**
	 * Capture the next set of data based on the capture type.
	 * @param c Type of data to capture. Should not be null.
	 */
	private void capture(Capture c){
		currentCapture = c;
		switch(c.getCaptureType()){
		case DELAY:
			waitForNavigation(c.getHintText());
			break;
		case POINT:
			launchSelectionPicker(this, c.getHintText(), false);
			break;
		case RECTANGLE:
			launchSelectionPicker(this, c.getHintText(), true);
			break;
		}
	}
	
	private void captureNext(){
		Capture next = currentCapture.next();
		if(next == null){
			listener.notify(this);
		} else {
			capture(next);
		}
	}
	
	private void launchSelectionPicker(final PointListener pl, final String hintText, final boolean showRectangle){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SelectionPicker sp = new SelectionPicker(pl, hintText, showRectangle);
				sp.setVisible(true);
			}
		});		
	}
	
	private void waitForNavigation(final String hintText){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, hintText);
				System.out.println("Done");
				captureNext();
			}
		});	
		
	}
	
	public <T> T getData(Capture capture, Class<T> clazz){
		if(capture.getCaptureType().getClazz().equals(clazz)){
			return clazz.cast(captureMap.get(capture));
		} else {
			System.out.println("Attempting to get " + clazz + " from " + capture + " which is " + capture.getCaptureType().getClazz());
			return null;
		}
	}

	@Override
	public void notifySelection(int x1, int y1, int x2, int y2) {
		switch(currentCapture.getCaptureType()){
		case POINT:
			System.out.println("Adding new point at " + x2 + "," + y2);
			captureMap.put(currentCapture, new Point(x2, y2));
			break;
		case RECTANGLE:
			System.out.println(String.format("Adding new rectangle at %d,%d of size %dx%d",x1,y1, x2-x1, y2-y1));
			captureMap.put(currentCapture, new Rectangle(new Point(x1, y1), new Dimension(x2-x1, y2-y1)));
			break;
		}
		captureNext();
	}
	
//	@Override
//	public void notifySelection(int x, int y, int width, int height) {
//		switch(selectionMode){
//		case LEAGUE:
////			this.leagueX = x;
////			this.leagueY = y;
////			this.leagueWidth = width;
////			this.leagueHeight = height;
////			prefs.putInt(PrefName.LEAGUE_X.path(), x);
////			prefs.putInt(PrefName.LEAGUE_Y.path(), y);
////			prefs.putInt(PrefName.LEAGUE_WIDTH.path(), width);
////			prefs.putInt(PrefName.LEAGUE_HEIGHT.path(), height);
////			lblLeagueWindow.setIcon(new ImageIcon(captureScreen(leagueX, leagueY, leagueWidth, leagueHeight)));
////			getTopLevelAncestor().setVisible(true);
//			break;
//		case NEXT:
//			break;
//		case NONE:
//			break;
//		case TEXT:
//			this.textX = x;
//			this.textY = y;
//			this.textWidth = width;
//			this.textHeight = height;
//			PrefName.TEXT_X.putInt(x);
//			PrefName.TEXT_Y.putInt(y);
//			PrefName.TEXT_WIDTH.putInt(width);
//			PrefName.TEXT_HEIGHT.putInt(height);
//			lblMonitorWindow.setIcon(new ImageIcon(captureScreen(textX, textY, textWidth,textHeight)));
////			selectionMode = SelectionMode.LEAGUE;
////			pickArea(this,	"Drag a box around enemy league icon");
//			getTopLevelAncestor().setVisible(true);
//			break;
//		default:
//			break;
//		
//		}
//	}
	
}
