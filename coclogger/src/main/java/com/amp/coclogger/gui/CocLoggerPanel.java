package com.amp.coclogger.gui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

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
import com.amp.coclogger.ocr.ImageCombiner;
import com.amp.coclogger.ocr.ImageUtils;
import com.amp.coclogger.prefs.League;
import com.amp.coclogger.prefs.PrefName;
import com.amp.coclogger.prefs.PreferenceListener;
import com.amp.coclogger.prefs.PreferencesPanel;

public class CocLoggerPanel extends JPanel implements SelectionListener, PreferenceListener {
	private static final long serialVersionUID = 1L;

	private static final Preferences prefs = Preferences.userNodeForPackage(PreferencesPanel.class);
	
	private static int count = 0;
	
	private ImageCombiner imageCombiner;
	private static Map<String, Integer> prefixNameCounters = new HashMap<>();
	
	int textX, textY, textWidth, textHeight;
	int leagueX, leagueY, leagueWidth, leagueHeight;

	JLabel lblMonitorWindow;
	JLabel lblMonitorEnhanceWindow;
	JLabel lblLeagueWindow;
	JLabel lblTownHallLevel;
	
	JTextPane textParsedValue;
	ScheduledExecutorService monitorService = Executors
			.newScheduledThreadPool(1);
	ScreenMonitor screenMonitor = new ScreenMonitor();
	ScheduledFuture<?> screenMonitorHandle;
	
	SelectionMode selectionMode;

	public CocLoggerPanel() {
		super();

		textX = prefs.getInt(PrefName.TEXT_X.path(), 0);
		textY = prefs.getInt(PrefName.TEXT_Y.path(), 0);
		textWidth = prefs.getInt(PrefName.TEXT_WIDTH.path(), 1);
		textHeight = prefs.getInt(PrefName.TEXT_HEIGHT.path(), 1);
		leagueX = prefs.getInt(PrefName.LEAGUE_X.path(), 0);
		leagueY = prefs.getInt(PrefName.LEAGUE_Y.path(), 0);
		leagueWidth = prefs.getInt(PrefName.LEAGUE_WIDTH.path(), 1);
		leagueHeight = prefs.getInt(PrefName.LEAGUE_HEIGHT.path(), 1);
		
		final SelectionListener selectionListener = this;
		setLayout(new MigLayout());
		
		JButton btnCalibrate = new JButton("Select Monitor Location");
		btnCalibrate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getTopLevelAncestor().setVisible(false);
				selectionMode = SelectionMode.TEXT;
				pickArea(selectionListener,	"Drag a box around resource numbers");
			}
		});

		JButton btnMonitor = new JButton("Begin Monitoring");
		btnMonitor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startScreenMonitor();
			}
		});
	
		JButton btnCancel = new JButton("Cancel Monitoring");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelScreenMonitor();
			}
		});
		
		lblMonitorWindow = new JLabel("");
		lblMonitorEnhanceWindow = new JLabel("");
		lblLeagueWindow = new JLabel("");
		lblTownHallLevel = new JLabel("Town Hall Level " + prefs.getInt(PrefName.TOWN_HALL_LEVEL.path(), 1));
		textParsedValue = new JTextPane();
		
		String league = prefs.get(PrefName.LEAGUE.path(), "BRONZEI");
		lblLeagueWindow.setIcon(new ImageIcon(League.valueOf(league).getImage()));
//		lblLeagueWindow.setIcon(new ImageIcon(captureScreen(leagueX, leagueY, leagueWidth, leagueHeight)));
		lblMonitorWindow.setIcon(new ImageIcon(captureScreen(textX, textY, textWidth,textHeight)));
		
		add(btnCalibrate, "w 100%, span 2, wrap");
		add(new JLabel("Original Image"));
		add(new JLabel("Enhanced Image"), "wrap");
		add(lblMonitorWindow, "push, grow, w 50%, h 40%");
		add(lblMonitorEnhanceWindow, "push, grow, w 50, h 40%, wrap");
		add(new JLabel("Parsed Value"));
		add(lblTownHallLevel, "center, wrap");
		add(textParsedValue, "push, grow, w 50%, h 40%");
		add(lblLeagueWindow, "center, wrap");
		add(btnMonitor, "w 50%");
		add(btnCancel, "w 50%");
	}

	@Override
	public void notifySelection(int x, int y, int width, int height) {
		switch(selectionMode){
		case LEAGUE:
//			this.leagueX = x;
//			this.leagueY = y;
//			this.leagueWidth = width;
//			this.leagueHeight = height;
//			prefs.putInt(PrefName.LEAGUE_X.path(), x);
//			prefs.putInt(PrefName.LEAGUE_Y.path(), y);
//			prefs.putInt(PrefName.LEAGUE_WIDTH.path(), width);
//			prefs.putInt(PrefName.LEAGUE_HEIGHT.path(), height);
//			lblLeagueWindow.setIcon(new ImageIcon(captureScreen(leagueX, leagueY, leagueWidth, leagueHeight)));
//			getTopLevelAncestor().setVisible(true);
			break;
		case NEXT:
			break;
		case NONE:
			break;
		case TEXT:
			this.textX = x;
			this.textY = y;
			this.textWidth = width;
			this.textHeight = height;
			prefs.putInt(PrefName.TEXT_X.path(), x);
			prefs.putInt(PrefName.TEXT_Y.path(), y);
			prefs.putInt(PrefName.TEXT_WIDTH.path(), width);
			prefs.putInt(PrefName.TEXT_HEIGHT.path(), height);
			lblMonitorWindow.setIcon(new ImageIcon(captureScreen(textX, textY, textWidth,textHeight)));
//			selectionMode = SelectionMode.LEAGUE;
//			pickArea(this,	"Drag a box around enemy league icon");
			getTopLevelAncestor().setVisible(true);
			break;
		default:
			break;
		
		}
		
//		foo();
	}

	private void pickArea(final SelectionListener selectionListener,
			final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				TextPicker tp = new TextPicker(selectionListener, text);
				tp.setVisible(true);
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
	
	private void startScreenMonitor(){
		int delaySeconds = Math.max(prefs.getInt(PrefName.MONITOR_DELAY.path(), 1), 1);
		
		if(screenMonitorHandle == null || screenMonitorHandle.getDelay(TimeUnit.MILLISECONDS) <= 0){
			System.out.println("Starting new monitor with delay of " + delaySeconds + " seconds");
			screenMonitorHandle = monitorService.scheduleAtFixedRate(screenMonitor,
					0, delaySeconds, TimeUnit.SECONDS);
			
			imageCombiner = new ImageCombiner(textWidth, textHeight, prefs.getInt(PrefName.IMAGES_PER_PAGE.path(), 9));
		}		
	}
	
	private void cancelScreenMonitor(){
		if (screenMonitorHandle != null
				&& !screenMonitorHandle.isCancelled()) {
			System.out.println("Cancelling screen monitor");
			screenMonitorHandle.cancel(true);
			if(prefs.getBoolean(PrefName.IMAGE_SAVE_ACTIVE.path(), false)){
				for(BufferedImage img : imageCombiner.combine()){
					saveImageToTif(img);
				}
			}
		}
		
	}
	
	private void saveImageToTif(BufferedImage binImg) {
		String path = prefs.get(PrefName.IMAGE_SAVE_PATH.path(), "");
		String prefix = prefs.get(PrefName.IMAGE_SAVE_PREFIX.path(), "");
		String language = prefs.get(PrefName.LANGUAGE.path(), "foo");
		if(prefixNameCounters.get(prefix) == null){
			prefixNameCounters.put(prefix, 0);
		}
		Integer suffixNum = prefixNameCounters.get(prefix);
		prefixNameCounters.put(prefix, suffixNum+1);
		String fullName = path + "\\" + language + "." + prefix + ".exp" + suffixNum + ".tif";

		ImageUtils.saveImageToFile(binImg, fullName, "TIFF");
		
	}

	class ScreenMonitor implements Runnable {
		
		private BufferedImage prevImg;
		private String prevValues;

		
		@Override
		public void run() {
			int THRESHOLD = 190;
			System.out.println("Monitor running");
			final BufferedImage img = captureScreen(textX, textY, textWidth,textHeight);
			final BufferedImage binImg = ImageUtils.erosion(Binarization.getBinarizedImage(img, THRESHOLD));
			
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
			
			if(prefs.getBoolean(PrefName.IMAGE_SAVE_ACTIVE.path(), false)){
				imageCombiner.add(binImg);
			}
			
//			final BufferedImage leagueImg = captureScreen(leagueX, leagueY, leagueWidth, leagueHeight);
//			ImageUtils.saveImageToFile(leagueImg, "C:\\Tesseract\\league-images\\league" + count++ + ".png", "PNG");
//			League closestLeague = null;
//			try{
//				closestLeague = ImageUtils.identifyLeague(leagueImg);
//			} catch (Exception e){
//				e.printStackTrace();
//			}
//			
//			final String leagueName = closestLeague == null ? "unknown" : closestLeague.toString(); 
			
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					lblMonitorWindow.setIcon(new ImageIcon(img));
					lblMonitorEnhanceWindow.setIcon(new ImageIcon(binImg));
//					lblLeagueWindow.setIcon(new ImageIcon(leagueImg));
//					lblLeagueName.setText("League Name: " + leagueName);
					textParsedValue.setText(values);
				}
			});
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
		
		final BufferedImage di1 = ImageUtils.erosion(img);
//		final BufferedImage di2 = ImageUtils.dilate(img, 1);
//		final BufferedImage di3 = ImageUtils.dilate(img, 2);
		
		final BufferedImage bdi1 = ImageUtils.erosion(binarizedImg2);
//		final BufferedImage bdi2 = ImageUtils.dilate(binarizedImg2, 1);
//		final BufferedImage bdi3 = ImageUtils.dilate(binarizedImg2, 2);
		
		final BufferedImage idi1 = ImageUtils.erosion(i2);
		
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
		
		System.out.println("di1: " + readImage(di1));
//		System.out.println("di2: " + readImage(di2));
//		System.out.println("di3: " + readImage(di3));
		System.out.println("bi1: " + readImage(bdi1));
//		System.out.println("bdi2: " + readImage(bdi2));
//		System.out.println("bdi3: " + readImage(bdi3));
		System.out.println("idi1: " + readImage(idi1));


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
				
//				panel.add(new JLabel(new ImageIcon(enlargedBi)));
//				panel.add(new JLabel(new ImageIcon(enlargedBi2)));
				
				panel.add(new JLabel(new ImageIcon(di1)));
//				panel.add(new JLabel(new ImageIcon(di2)));
//				panel.add(new JLabel(new ImageIcon(di3)));
				panel.add(new JLabel(new ImageIcon(bdi1)));
				panel.add(new JLabel(new ImageIcon(idi1)));
//				panel.add(new JLabel(new ImageIcon(bdi2)));
//				panel.add(new JLabel(new ImageIcon(bdi3)));
				JFrame frame = new JFrame();
				frame.setBounds(100, 100, img.getWidth()*2 + 50,
						(img.getHeight() * 5) + 50);
				frame.getContentPane().add(panel);
				frame.setVisible(true);
			}
		});
	}

	@Override
	public void notify(List<PrefName> changedPrefs) {
		for(PrefName pref : changedPrefs){
			switch(pref){
			case LEAGUE:
				lblLeagueWindow.setIcon(new ImageIcon(League.valueOf(prefs.get(pref.path(), "BRONZEIII")).getImage()));
				break;
			case TOWN_HALL_LEVEL:
				lblTownHallLevel.setText("Town Hall Level " + prefs.getInt(pref.path(), 1));
				break;
			default:
				break;
			
			}
		}
		
	}

	
}
