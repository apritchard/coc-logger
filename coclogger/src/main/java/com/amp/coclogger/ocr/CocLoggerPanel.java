package com.amp.coclogger.ocr;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.vietocr.ImageHelper;

import com.amp.coclogger.external.Binarization;
import com.amp.coclogger.prefs.PrefName;
import com.amp.coclogger.prefs.PreferencesPanel;

public class CocLoggerPanel extends JPanel implements SelectionListener {
	private static final long serialVersionUID = 1L;

	private static final Preferences prefs = Preferences.userNodeForPackage(PreferencesPanel.class);
	private static Map<String, Integer> prefixNameCounters = new HashMap<>();
	
	int textX, textY, textWidth, textHeight;

	JLabel lblMonitorWindow;
	JTextPane textParsedValue;
	ScheduledExecutorService monitorService = Executors
			.newScheduledThreadPool(1);
	ScreenMonitor screenMonitor = new ScreenMonitor();
	ScheduledFuture<?> screenMonitorHandle;

	public CocLoggerPanel() {
		super();

		final SelectionListener selectionListener = this;
		setLayout(new MigLayout());
		
		JButton btnCalibrate = new JButton("Calibrate");
		btnCalibrate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelScreenMonitor();
				pickArea(selectionListener,
						"Drag a box around resource numbers");
			}
		});
		add(btnCalibrate, "split 2, flowy");

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelScreenMonitor();
			}
		});
		add(btnCancel);
		lblMonitorWindow = new JLabel("");
		add(lblMonitorWindow, "height 300px, width 300px, wrap");

		add(new JLabel("Parsed Value"));
		textParsedValue = new JTextPane();
		add(textParsedValue, "wrap, push, grow");

	}

	@Override
	public void notifySelection(int x, int y, int width, int height) {
		this.textX = x;
		this.textY = y;
		this.textWidth = width;
		this.textHeight = height;

		int delaySeconds = Math.max(prefs.getInt(PrefName.MONITOR_DELAY.getPathName(), 1), 1);
		
		foo();
		
//		if(screenMonitorHandle == null || screenMonitorHandle.getDelay(TimeUnit.MILLISECONDS) <= 0){
//			System.out.println("Starting new monitor with delay of " + delaySeconds + " seconds");
//			screenMonitorHandle = monitorService.scheduleAtFixedRate(screenMonitor,
//					0, delaySeconds, TimeUnit.SECONDS);
//		}
	}

	private void pickArea(final SelectionListener selectionListener,
			final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				TextPicker tp = new TextPicker(selectionListener, text);
			}
		});
	}

	private void foo() {
		System.out.println(String.format("Capturing (%d,%d) %dx%d", textX,
				textY, textWidth, textHeight));
		final BufferedImage img = captureScreen(textX, textY, textWidth,
				textHeight);
		final BufferedImage binarizedImg = Binarization.getBinarizedImage(img);
		final BufferedImage binarizedImg2 = Binarization.getBinarizedImage(img, 180);
		Image enlargedImg = binarizedImg2.getScaledInstance(textWidth*2, textHeight*2, Image.SCALE_SMOOTH);
		final BufferedImage enlargedBi = new BufferedImage(enlargedImg.getWidth(null), enlargedImg.getHeight(null), BufferedImage.TYPE_BYTE_BINARY);
		enlargedBi.getGraphics().drawImage(enlargedImg, 0, 0, null);
		
		final BufferedImage bi3 = ImageHelper.convertImageToBinary(img);
		final BufferedImage gs1 = ImageHelper.convertImageToGrayscale(img);
		final BufferedImage ebi1 = ImageHelper.getScaledInstance(bi3, bi3.getWidth()*2, bi3.getHeight()*2);
		final BufferedImage egs1 = ImageHelper.getScaledInstance(gs1, gs1.getWidth()*2, gs1.getHeight()*2);
		
		final BufferedImage i1 = ImageHelper.invertImageColor(binarizedImg2);
		final BufferedImage i2 = Binarization.getBinarizedImage(ImageHelper.invertImageColor(img));
		

		
		
		String nums = readImage(img);
		System.out.println("Text: " + nums);
//		System.out.println("BinarText: " + readImage(binarizedImg));
		System.out.println("BinarText2: " + readImage(binarizedImg2));
		System.out.println("Enlarged: " + readImage(enlargedBi));
//		System.out.println("bi3: " + readImage(bi3));
//		System.out.println("gs1: " + readImage(gs1));
//		System.out.println("ebi1: " + readImage(ebi1));
//		System.out.println("egs1: " + readImage(egs1));
//		System.out.println("i1: " + readImage(i1));
//		System.out.println("i2: " + readImage(i2));


		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				
				BufferedImage enlargedBi2 = new BufferedImage(textWidth*2, textHeight*2, BufferedImage.TYPE_BYTE_BINARY);
				
				AffineTransform at = new AffineTransform();
				at.scale(2.0, 2.0);
				AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
				enlargedBi2 = scaleOp.filter(binarizedImg2, enlargedBi2);
				System.out.println("Enlarged2: " + readImage(enlargedBi2));
				
				JPanel panel = new JPanel();
				panel.add(new JLabel(new ImageIcon(img)));
//				panel.add(new JLabel(new ImageIcon(binarizedImg)));
				panel.add(new JLabel(new ImageIcon(binarizedImg2)));
//				panel.add(new JLabel(new ImageIcon(bi3)));
//				panel.add(new JLabel(new ImageIcon(gs1)));
//				panel.add(new JLabel(new ImageIcon(i1)));
//				panel.add(new JLabel(new ImageIcon(i2)));
				
				panel.add(new JLabel(new ImageIcon(enlargedBi)));
				panel.add(new JLabel(new ImageIcon(enlargedBi2)));
				JFrame frame = new JFrame();
				frame.setBounds(100, 100, img.getWidth()*2 + 50,
						(img.getHeight() * 5) + 50);
				frame.getContentPane().add(panel);
				frame.setVisible(true);
			}
		});
	}

	private BufferedImage captureScreen(int x, int y, int width, int height) {
		try {
			Robot robot = new Robot();
			Rectangle screenRectangle = new Rectangle(x, y, width, height);
			BufferedImage image = robot.createScreenCapture(screenRectangle);
			return image;
		} catch (AWTException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to capture screen");
		}
	}

	public String readImage(BufferedImage bi) {

		Tesseract tess = Tesseract.getInstance();
		tess.setConfigs(Arrays.asList(new String[] { "digits" }));
		tess.setPageSegMode(6);
		tess.setLanguage("coc");

		try {
			String text = tess.doOCR(bi);
			return text;
		} catch (TesseractException e) {
			e.printStackTrace();
		}
		return "Error parsing image";

	}
	
	private void cancelScreenMonitor(){
		if (screenMonitorHandle != null
				&& !screenMonitorHandle.isCancelled()) {
			System.out.println("Cancelling screen monitor");
			screenMonitorHandle.cancel(true);
		}
	}

	class ScreenMonitor implements Runnable {
		
		private BufferedImage prevImg;
		private String prevValues;
		
		@Override
		public void run() {
			int THRESHOLD = 180;
			System.out.println("Monitor running");
			final BufferedImage img = captureScreen(textX, textY, textWidth,textHeight);
			final BufferedImage binImg = Binarization.getBinarizedImage(img, THRESHOLD);
			
			if(bufferedImagesEqual(binImg, prevImg)){
				System.out.println("Identical image");
				return;
			}
			prevImg = binImg;
			
			final String values = readImage(binImg);
			if(values.equalsIgnoreCase(prevValues)){
				System.out.println("Same values");
				return;
			}
			prevValues = values;
			
			if(prefs.getBoolean(PrefName.IMAGE_SAVE_ACTIVE.getPathName(), false)){
				saveImageToTif(binImg);
			}
			
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					lblMonitorWindow.setIcon(new ImageIcon(img));
					textParsedValue.setText(values);
				}
			});
		}
		
		private void saveImageToTif(BufferedImage binImg) {
			String path = prefs.get(PrefName.IMAGE_SAVE_PATH.getPathName(), "");
			String prefix = prefs.get(PrefName.IMAGE_SAVE_PREFIX.getPathName(), "");
			String language = prefs.get(PrefName.LANGUAGE.getPathName(), "foo");
			if(prefixNameCounters.get(prefix) == null){
				prefixNameCounters.put(prefix, 0);
			}
			Integer suffixNum = prefixNameCounters.get(prefix);
			prefixNameCounters.put(prefix, suffixNum+1);
			String fullName = path + "\\" + language + "." + prefix + ".exp" + suffixNum + ".tif";
			File file = new File(fullName);
			file.mkdirs();
			try {
				ImageIO.write(binImg, "TIFF", file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		boolean bufferedImagesEqual(BufferedImage img1, BufferedImage img2) {
			if(img1 == null && img2 == null){
				return true;
			} 
			if(img1 == null || img2 == null){
				return false;
			}
		    if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
		        for (int x = 0; x < img1.getWidth(); x++) {
		            for (int y = 0; y < img1.getHeight(); y++) {
		                if (img1.getRGB(x, y) != img2.getRGB(x, y))
		                    return false;
		            }
		        }
		    } else {
		        return false;
		    }
		    return true;
		}

	}

}
