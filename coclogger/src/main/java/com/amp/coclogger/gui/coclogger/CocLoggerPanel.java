package com.amp.coclogger.gui.coclogger;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import net.sourceforge.vietocr.ImageHelper;

import com.amp.coclogger.external.Binarization;
import com.amp.coclogger.gui.util.ScreenCaptureManager;
import com.amp.coclogger.gui.util.SelectionListener;
import com.amp.coclogger.gui.util.SelectionMode;
import com.amp.coclogger.math.CocData;
import com.amp.coclogger.math.CocResult;
import com.amp.coclogger.ocr.ImageCombiner;
import com.amp.coclogger.ocr.ImageUtils;
import com.amp.coclogger.prefs.Capture;
import com.amp.coclogger.prefs.ImageFileType;
import com.amp.coclogger.prefs.League;
import com.amp.coclogger.prefs.PrefName;
import com.amp.coclogger.prefs.PreferenceListener;
import com.amp.coclogger.prefs.Townhall;

public class CocLoggerPanel extends JPanel implements SelectionListener, PreferenceListener {
	private static final long serialVersionUID = 1L;

	private ImageCombiner processedImageCombiner;
	private ImageCombiner rawImageCombiner;
	private static Map<String, Integer> prefixNameCounters = new HashMap<>();
	
	Rectangle textRect;
	Rectangle leagueRect;
	
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
		textRect = new Rectangle(
				new Point(		PrefName.TEXT_X.getInt(), 		PrefName.TEXT_Y.getInt()), 
				new Dimension(	PrefName.TEXT_WIDTH.getInt(), 	PrefName.TEXT_HEIGHT.getInt()));
		leagueRect = new Rectangle(
				new Point(		PrefName.ENEMY_LEAGUE_X.getInt(), 		PrefName.ENEMY_LEAGUE_Y.getInt()), 
				new Dimension(	PrefName.ENEMY_LEAGUE_WIDTH.getInt(), 	PrefName.ENEMY_LEAGUE_HEIGHT.getInt()));
		
		final SelectionListener selectionListener = this;
		setLayout(new MigLayout());
		
		JButton btnCalibrate = new JButton("Calibrate Monitor");
		btnCalibrate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getTopLevelAncestor().setVisible(false);
				ScreenCaptureManager scm = new ScreenCaptureManager(selectionListener, Arrays.asList(Capture.values()));
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
		lblTownHallLevel = new JLabel("Town Hall Level " + PrefName.TOWNHALL.get());
		textParsedValue = new JTextPane();
		
		String league = PrefName.LEAGUE.get();
//		lblLeagueWindow.setIcon(new ImageIcon(League.valueOf(league).getImage()));
		try{
			lblLeagueWindow.setIcon(new ImageIcon(ImageUtils.captureScreen(leagueRect)));
			lblMonitorWindow.setIcon(new ImageIcon(ImageUtils.captureScreen(textRect)));
		} catch (Exception e){
			e.printStackTrace();
		}
		
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
	
	private void startScreenMonitor(){
		int delaySeconds = Math.max(PrefName.MONITOR_DELAY.getInt(), 1);
		
//		try {
//			bar();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		baz();
//		if(screenMonitorHandle == null || screenMonitorHandle.getDelay(TimeUnit.MILLISECONDS) <= 0){
//			System.out.println("Starting new monitor with delay of " + delaySeconds + " seconds");
//			screenMonitorHandle = monitorService.scheduleAtFixedRate(screenMonitor,
//					0, delaySeconds, TimeUnit.SECONDS);
//			
//			processedImageCombiner = new ImageCombiner(textRect.width, textRect.height, PrefName.IMAGES_PER_PAGE.getInt());
//			rawImageCombiner = new ImageCombiner(textRect.width, textRect.height, PrefName.IMAGES_PER_PAGE.getInt());
//		}		
	}
	
	private void bar() throws IOException {
		int x = PrefName.COC_X.getInt();
		int y = PrefName.COC_Y.getInt();
		int width = PrefName.COC_WIDTH.getInt();
		int height = PrefName.COC_HEIGHT.getInt();
		BufferedImage fullScreen = ImageUtils.captureScreen(x, y, width, height);
		
		findImage("attack", fullScreen);
		findImage("end-battle", fullScreen);
		findImage("find-a-match", fullScreen);
		findImage("next", fullScreen);
		findImage("th8", fullScreen);
		findImage("trophy", fullScreen);
		
	}
	
	private void baz(){
		int x = PrefName.TEXT_X.getInt();
		int y = PrefName.TEXT_Y.getInt();
		int width = PrefName.TEXT_WIDTH.getInt();
		int height = PrefName.TEXT_HEIGHT.getInt();
		final BufferedImage resources = ImageUtils.captureScreen(x, y, width, height);
		System.out.println("Threshold of text view: " + ImageUtils.getLumosity(resources));
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				lblMonitorWindow.setIcon(new ImageIcon(resources));
			}
		});
	}
	
	private void findImage(String findable, BufferedImage fullScreen) throws IOException{
		URL url = ClassLoader.getSystemResource("findables/" + findable + ".png");
		BufferedImage image = ImageIO.read(url);
		System.out.println(findable + ": " + ImageUtils.findMatch(fullScreen, image));
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
	
	private void updateImageLabel(final JLabel label, final BufferedImage img){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				label.setIcon(new ImageIcon(img));
			}
		});
	}

	class ScreenMonitor implements Runnable {
		
		private BufferedImage prevImg;
		private String prevValues;

		
		@Override
		public void run() {
			int THRESHOLD = 190;
			System.out.println("Monitor running");
			final BufferedImage img = ImageUtils.captureScreen(textRect);
			final BufferedImage binImg = ImageUtils.erosion(Binarization.getBinarizedImage(img, THRESHOLD));
			
			if(bufferedImagesEqual(binImg, prevImg)){
				System.out.println("Identical image");
				return;
			}
			prevImg = binImg;
			
			final String values = ImageUtils.readImage(binImg);
			
			if (!values.equalsIgnoreCase(prevValues)) {

				try {
					League league = League.valueOf(PrefName.LEAGUE.get());
					Townhall townhall = Townhall.valueOf(PrefName.TOWNHALL.get());
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
			

			
			final BufferedImage leagueImg = ImageUtils.captureScreen(leagueRect);
			League closestLeague = null;
			try{
				closestLeague = ImageUtils.identifyLeague(img);
			} catch (Exception e){
				e.printStackTrace();
			}
			
			final String leagueName = closestLeague == null ? "unknown" : closestLeague.toString(); 
			
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					lblMonitorWindow.setIcon(new ImageIcon(img));
					lblMonitorEnhanceWindow.setIcon(new ImageIcon(binImg));
					lblLeagueWindow.setIcon(new ImageIcon(leagueImg));
					lblTownHallLevel.setText("League Name: " + leagueName);
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
		System.out.println(String.format("Capturing (%d,%d) %dx%d", textRect.x,
				textRect.y, textRect.width, textRect.height));
		final BufferedImage img = ImageUtils.captureScreen(textRect);
		final BufferedImage binarizedImg = Binarization.getBinarizedImage(img);
		final BufferedImage binarizedImg2 = Binarization.getBinarizedImage(img, 180);
		Image enlargedImg = binarizedImg2.getScaledInstance(textRect.width*2, textRect.height*2, Image.SCALE_SMOOTH);
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
		
		String nums = ImageUtils.readImage(img);
		System.out.println("Text: " + nums);
//		System.out.println("BinarText: " + readImage(binarizedImg));
		System.out.println("BinarText2: " + ImageUtils.readImage(binarizedImg2));
		System.out.println("Enlarged: " + ImageUtils.readImage(enlargedBi));
//		System.out.println("bi3: " + readImage(bi3));
//		System.out.println("gs1: " + readImage(gs1));
//		System.out.println("ebi1: " + readImage(ebi1));
//		System.out.println("egs1: " + readImage(egs1));
//		System.out.println("i1: " + readImage(i1));
//		System.out.println("i2: " + readImage(i2));
		
		System.out.println("di1: " + ImageUtils.readImage(di1));
//		System.out.println("di2: " + readImage(di2));
//		System.out.println("di3: " + readImage(di3));
		System.out.println("bi1: " + ImageUtils.readImage(bdi1));
//		System.out.println("bdi2: " + readImage(bdi2));
//		System.out.println("bdi3: " + readImage(bdi3));
		System.out.println("idi1: " + ImageUtils.readImage(idi1));


		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				
				BufferedImage enlargedBi2 = new BufferedImage(textRect.width*2, textRect.height*2, BufferedImage.TYPE_BYTE_BINARY);
				
				AffineTransform at = new AffineTransform();
				at.scale(2.0, 2.0);
				AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
				enlargedBi2 = scaleOp.filter(binarizedImg2, enlargedBi2);
				System.out.println("Enlarged2: " + ImageUtils.readImage(enlargedBi2));
				
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
//				lblLeagueWindow.setIcon(new ImageIcon(League.valueOf(pref.get()).getImage()));
				break;
			case TOWNHALL:
				lblTownHallLevel.setText("Town Hall Level " + pref.get());
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
				capturePlayerLeague(screenCaptureManager.getData(capture, Rectangle.class));
				break;
			case ATTACK_ICON:
				captureAttackIcon(screenCaptureManager.getData(capture, Point.class));
				break;
			case FIND_A_MATCH_ICON:
				captureFindAMatchIcon(screenCaptureManager.getData(capture, Point.class));
				break;
			case PLAYER_TOWNHALL:
				capturePlayerTownhall(screenCaptureManager.getData(capture, Rectangle.class));
				break;
			case POSITION_CLASH:
				break;
			}
		}
	}
	
	
	private void capturePlayerTownhall(Rectangle r) {
		PrefName.PLAYER_TH_X.putInt(r.x);
		PrefName.PLAYER_TH_Y.putInt(r.y);
		PrefName.PLAYER_TH_WIDTH.putInt(r.width);
		PrefName.PLAYER_TH_HEIGHT.putInt(r.height);	
	}

	private void captureFindAMatchIcon(Point p) {
		PrefName.FIND_X.putInt(p.x);
		PrefName.FIND_Y.putInt(p.y);
	}

	private void captureAttackIcon(Point p) {
		PrefName.ATTACK_X.putInt(p.x);
		PrefName.ATTACK_Y.putInt(p.y);		
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
		textRect = r;
		PrefName.TEXT_X.putInt(textRect.x);
		PrefName.TEXT_Y.putInt(textRect.y);
		PrefName.TEXT_WIDTH.putInt(textRect.width);
		PrefName.TEXT_HEIGHT.putInt(textRect.height);
		updateImageLabel(lblMonitorWindow, ImageUtils.captureScreen(r));
	}
	
	private void capturePlayerLeague(Rectangle r){
		PrefName.PLAYER_LEAGUE_X.putInt(r.x);
		PrefName.PLAYER_LEAGUE_Y.putInt(r.y);
		PrefName.PLAYER_LEAGUE_WIDTH.putInt(r.width);
		PrefName.PLAYER_LEAGUE_HEIGHT.putInt(r.height);
	}
	
}
