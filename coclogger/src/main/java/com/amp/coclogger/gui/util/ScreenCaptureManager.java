package com.amp.coclogger.gui.util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.amp.coclogger.prefs.Capture;

/**
 * This class manages capturing a list of screen locations from the user.
 * @author alex
 *
 */
public class ScreenCaptureManager implements PointListener {
	
	Logger logger = Logger.getLogger(ScreenCaptureManager.class);

	private Map<Capture, Object> captureMap = new EnumMap<>(Capture.class);
	private SelectionListener listener;
	
	private Capture currentCapture;
	private List<Capture> captures;
	private Iterator<Capture> captureIter;
	
	public ScreenCaptureManager(SelectionListener listener, List<Capture> captures){
		this.captures = captures;
		captureIter = captures.iterator();
		this.listener = listener;
	}
	
	public void capture(){
		if(!captureIter.hasNext()){
			throw new UnsupportedOperationException("Unable to capture without a list of Captures");
		}
		capture(captureIter.next());
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
	
	/**
	 * 
	 */
	private void captureNext(){
		if(captureIter.hasNext()) {
			capture(captureIter.next());
		} else {
			listener.notify(this);
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
				logger.trace("Wait for navigation done");
				captureNext();
			}
		});	
		
	}
	
	public <T> T getData(Capture capture, Class<T> clazz){
		if(capture.getCaptureType().getClazz().equals(clazz)){
			return clazz.cast(captureMap.get(capture));
		} else {
			throw new UnsupportedOperationException("Attempting to get " + clazz + " from " + capture + " which is " + capture.getCaptureType().getClazz());
		}
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void notifySelection(int x1, int y1, int x2, int y2) {
		switch(currentCapture.getCaptureType()){
		case POINT:
			logger.trace("Adding new point at " + x2 + "," + y2);
			captureMap.put(currentCapture, new Point(x2, y2));
			break;
		case RECTANGLE:
			logger.trace(String.format("Adding new rectangle at %d,%d of size %dx%d",x1,y1, x2-x1, y2-y1));
			captureMap.put(currentCapture, new Rectangle(new Point(x1, y1), new Dimension(x2-x1, y2-y1)));
			break;
		}
		captureNext();
	}
	
}
