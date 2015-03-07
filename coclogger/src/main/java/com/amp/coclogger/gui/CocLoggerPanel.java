package com.amp.coclogger.gui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.Point;
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
import com.amp.coclogger.math.CocData;
import com.amp.coclogger.math.CocResult;
import com.amp.coclogger.ocr.ImageCombiner;
import com.amp.coclogger.ocr.ImageUtils;
import com.amp.coclogger.prefs.Capture;
import com.amp.coclogger.prefs.ImageFileType;
import com.amp.coclogger.prefs.League;
import com.amp.coclogger.prefs.PrefName;
import com.amp.coclogger.prefs.PreferenceListener;

public class CocLoggerPanel extends JPanel implements SelectionListener, PreferenceListener {
	private static final long serialVersionUID = 1L;

	private ImageCombiner processedImageCombiner;
	private ImageCombiner rawImageCombiner;
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

		textX = PrefName.TEXT_X.getInt();
		textY = PrefName.TEXT_Y.getInt();
		textWidth = PrefName.TEXT_WIDTH.getInt();
		textHeight = PrefName.TEXT_HEIGHT.getInt();
//		leagueX = prefs.getInt(PrefName.LEAGUE_X.path(), 0);
//		leagueY = prefs.getInt(PrefName.LEAGUE_Y.path(), 0);
//		leagueWidth = prefs.getInt(PrefName.LEAGUE_WIDTH.path(), 1);
//		leagueHeight = prefs.getInt(PrefName.LEAGUE_HEIGHT.path(), 1);
		
		final SelectionListener selectionListener = this;
		setLayout(new MigLayout());
		
		JButton btnCalibrate = new JButton("Calibrate Monitor");
		btnCalibrate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getTopLevelAncestor().setVisible(false);
				ScreenCaptureManager scm = new ScreenCaptureManager(selectionListener);
				scm.capture();
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
		lblTownHallLevel = new JLabel("Town Hall Level " + PrefName.TOWN_HALL_LEVEL.getInt());
		textParsedValue = new JTextPane();
		
		String league = PrefName.LEAGUE.get();
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
		int delaySeconds = Math.max(PrefName.MONITOR_DELAY.getInt(), 1);
		
		if(screenMonitorHandle == null || screenMonitorHandle.getDelay(TimeUnit.MILLISECONDS) <= 0){
			System.out.println("Starting new monitor with delay of " + delaySeconds + " seconds");
			screenMonitorHandle = monitorService.scheduleAtFixedRate(screenMonitor,
					0, delaySeconds, TimeUnit.SECONDS);
			
			processedImageCombiner = new ImageCombiner(textWidth, textHeight, PrefName.IMAGES_PER_PAGE.getInt());
			rawImageCombiner = new ImageCombiner(textWidth, textHeight, PrefName.IMAGES_PER_PAGE.getInt());
		}		
	}
	
	private void cancelScreenMonitor(){
		if (screenMonitorHandle != null
				&& !screenMonitorHandle.isCancelled()) {
			System.out.println("Cancelling screen monitor");
			screenMonitorHandle.cancel(true);
			if(PrefName.IMAGE_SAVE_PROCESSED.getBoolean()){
				for(BufferedImage img : processedImageCombiner.combine()){
					saveImageToFile(img, false, ImageFileType.valueOf(PrefName.IMAGE_FILE_TYPE.get()));
				}
			}
			if(PrefName.IMAGE_SAVE_RAW.getBoolean()){
				for(BufferedImage img : rawImageCombiner.combine()){
					saveImageToFile(img, true, ImageFileType.valueOf(PrefName.IMAGE_FILE_TYPE.get()));
				}
			}
		}
		
	}
	
	private void saveImageToFile(BufferedImage binImg, boolean raw, ImageFileType fileType) {
		String path = PrefName.IMAGE_SAVE_PATH.get();
		String prefix = PrefName.IMAGE_SAVE_PREFIX.get();
		String language = PrefName.LANGUAGE.get();
		String suffix = raw ? PrefName.RAW_IMAGE_SUFFIX.get() : PrefName.PROCESSED_IMAGE_SUFFIX.get();
		if(!suffix.isEmpty()){
			suffix = "." + suffix;
		}
		if(prefixNameCounters.get(prefix) == null){
			prefixNameCounters.put(prefix, 0);
		}
		Integer suffixNum = prefixNameCounters.get(prefix);
		prefixNameCounters.put(prefix, suffixNum+1);
		String fullName = path + "\\" + language + "." + prefix + ".exp" + suffix + suffixNum + "." + fileType.getExtension();

		ImageUtils.saveImageToFile(binImg, fullName, fileType.toString());
		
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
			
			if (!values.equalsIgnoreCase(prevValues)) {

				try {
					League league = League.valueOf(PrefName.LEAGUE.get());
					int townhall = PrefName.TOWN_HALL_LEVEL.getInt();
					CocResult result = ImageUtils.parseCocResult(values,
							league, townhall);
					CocData cocData = CocData.getInstance();
					cocData.addData(result);
				} catch (Exception e) {
					System.out.println("Unable to create data from captured image");
					e.printStackTrace();
				}
			} else {
				System.out.println("Same values, not logging statistics");
			}
			
			prevValues = values;
			
			if(PrefName.IMAGE_SAVE_PROCESSED.getBoolean()){
				processedImageCombiner.add(binImg);
			}
			if(PrefName.IMAGE_SAVE_RAW.getBoolean()){
				rawImageCombiner.add(img);
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
				lblLeagueWindow.setIcon(new ImageIcon(League.valueOf(pref.get()).getImage()));
				break;
			case TOWN_HALL_LEVEL:
				lblTownHallLevel.setText("Town Hall Level " + pref.getInt());
				break;
			default:
				break;
			
			}
		}
		
	}


	@Override
	public void notify(ScreenCaptureManager screenCaptureManager) {
		getTopLevelAncestor().setVisible(true);
		for(Capture capture : Capture.values()){
			switch(capture){
			case FULL_SCREEN:
				captureFullScreen(screenCaptureManager.getData(capture, Rectangle.class));
				break;
			case NEXT_BUTTON:
				captureNextButton(screenCaptureManager.getData(capture, Point.class));
				break;
			case NUMS:
				captureNums(screenCaptureManager.getData(capture, Rectangle.class));
				break;
			case PLAYER_LEAGUE:
				capturePlayerLeague(screenCaptureManager.getData(capture, Point.class));
				break;
			case SWITCH_TO_COMBAT:
				break;
			default:
				break;
			
			}
		}
	}
	
	private void captureFullScreen(Rectangle r){
		PrefName.COC_X.putInt(r.x);
		PrefName.COC_Y.putInt(r.y);
		PrefName.COC_WIDTH.putInt(r.width);
		PrefName.COC_HEIGHT.putInt(r.height);
	}
	
	private void captureNextButton(Point p){
		PrefName.NEXT_X.putInt(p.x);
		PrefName.NEXT_Y.putInt(p.y);
	}
	
	private void captureNums(Rectangle r){
		PrefName.TEXT_X.putInt(r.x);
		PrefName.TEXT_Y.putInt(r.y);
		PrefName.TEXT_WIDTH.putInt(r.width);
		PrefName.TEXT_HEIGHT.putInt(r.height);
	}
	
	private void capturePlayerLeague(Point p){
		PrefName.PLAYER_LEAGUE_X.putInt(p.x - 30);
		PrefName.PLAYER_LEAGUE_Y.putInt(p.y - 30);
		PrefName.PLAYER_LEAGUE_WIDTH.putInt(60);
		PrefName.PLAYER_LEAGUE_HEIGHT.putInt(60);
	}
	
}
