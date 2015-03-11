package com.amp.coclogger.ocr;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import boofcv.alg.feature.detect.template.TemplateMatching;
import boofcv.alg.feature.detect.template.TemplateMatchingIntensity;
import boofcv.alg.misc.ImageStatistics;
import boofcv.alg.misc.PixelMath;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.factory.feature.detect.template.FactoryTemplateMatching;
import boofcv.factory.feature.detect.template.TemplateScoreType;
import boofcv.gui.image.ShowImages;
import boofcv.gui.image.VisualizeImageData;
import boofcv.struct.feature.Match;
import boofcv.struct.image.ImageFloat32;

import com.amp.coclogger.external.Binarization;
import com.amp.coclogger.math.CocResult;
import com.amp.coclogger.prefs.League;
import com.amp.coclogger.prefs.Townhall;

public class ImageUtils {

	public static void saveImageToFile(BufferedImage bi, String path, String type) {
		File file = new File(path);
		file.mkdirs();
		try {
			ImageIO.write(bi, type, file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static BufferedImage dilate(BufferedImage bi){
		BufferedImage dil = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
		Kernel kernel = new Kernel(3, 3, new float[] {
				1f, 1f, 1f, 
				1f, 1f, 1f, 
				1f, 1f, 1f
			 });		
		ConvolveOp op = new ConvolveOp(kernel);
		op.filter(bi, dil);
		return dil;
	}
	
	public static double findMatch(BufferedImage large, BufferedImage small){
		ImageFloat32 if32Large = ConvertBufferedImage.convertFrom(large, (ImageFloat32)null);
		ImageFloat32 if32Small = ConvertBufferedImage.convertFrom(small, (ImageFloat32)null);
		
		TemplateMatching<ImageFloat32> matcher = FactoryTemplateMatching.createMatcher(TemplateScoreType.SUM_DIFF_SQ, ImageFloat32.class);
		matcher.setTemplate(if32Small, 1);
		matcher.process(if32Large);
		double score = matcher.getResults().get(0).score;
		return score;
	}
	
	public static int getLumosity(BufferedImage image){
		return Binarization.otsuThreshold(image);
	}
	
	public static League identifyLeague(BufferedImage image){
		
		TemplateMatching<ImageFloat32> matcher = FactoryTemplateMatching.createMatcher(TemplateScoreType.SUM_DIFF_SQ, ImageFloat32.class);

		League bestLeague = League.BRONZE3;
		double bestScore = Double.NEGATIVE_INFINITY;
		
		ImageFloat32 if32 = ConvertBufferedImage.convertFrom(image, (ImageFloat32)null);
		for(League l : League.values()){
//			showMatchIntensity(if32, l.getImageFloat32(), l.toString());
			System.out.println("Checking " + l);
			ImageFloat32 template = l.getImageFloat32();
			if(template == null) continue;
			matcher.setTemplate(template, 1);
			matcher.process(if32);
			for(Match match : matcher.getResults().toList()){
				System.out.println("Got results: " + match.score + " (old: " + bestScore +")");
				if(match.score > bestScore){
					bestLeague = l;
					bestScore = match.score;
					System.out.println("Better match: " + l + " score: " + bestScore);
				}
			}
		}
		
		return bestLeague;
	}
	
	public static String readImage(Rectangle r){
		return readImage(captureScreen(r));
	}
	
	public static String readImage(int x, int y, int width, int height){
		return readImage(captureScreen(x, y, width, height));
	}
	
	public static String readImage(BufferedImage bi) {
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
		return null;
	}
	
	public static void convolve(BufferedImage small, BufferedImage large){
		TemplateMatching<ImageFloat32> matcher = FactoryTemplateMatching.createMatcher(TemplateScoreType.SUM_DIFF_SQ, ImageFloat32.class);
		ImageFloat32 template = ConvertBufferedImage.convertFrom(small, (ImageFloat32)null);
		ImageFloat32 image = ConvertBufferedImage.convertFrom(large, (ImageFloat32)null);
		matcher.setTemplate(template, 1);
		matcher.process(image);
		List<Match> templates = matcher.getResults().toList();
		templates.get(0);
	}
	

	public static BufferedImage captureScreen(Rectangle r){
		return captureScreen(r.x, r.y, r.width, r.height);
	}


	public static BufferedImage captureScreen(int x, int y, int width, int height) {
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
	
	public static CocResult parseCocResult(String text, League league, Townhall townhall){
		String[] lines = text.split("\n");
		if(lines.length < 3){
			System.out.println("Invalid result");
			return null;
		}
		String goldStr = lines[0].replaceAll("\\s+", "");
		String elixirStr = lines[1].replaceAll("\\s+", "");
		String darkElixirStr = lines.length < 4 ? "0" : lines[2].replaceAll("\\s+", "");
		
		int gold = Integer.parseInt(goldStr);
		int elixir = Integer.parseInt(elixirStr);
		int darkElixir = Integer.parseInt(darkElixirStr);
		
		int maxGe = 0;
		int maxDe = 0;
		
		switch(townhall){
		case TH10:
			maxGe = 1075000;
			maxDe = 7600;
			break;
		case TH9:
			maxGe = 900000;
			maxDe = 5400;
			break;
		case TH8:
			maxGe = 870000;
			maxDe = 2650;
			break;
		case TH7:
			maxGe = 640000;
			maxDe = 1100;
			break;
		default:
			maxGe = 520000;
			maxDe = 1100;
		}
		
		if(gold > maxGe || gold < 0 || elixir > maxGe || elixir < 0 || darkElixir > maxDe || darkElixir < 0){
			System.out.println(String.format("Potentially invalid data g:%d, e:%d, de:%d",gold,elixir,darkElixir));
			return null;
		}
		return new CocResult(gold, elixir, darkElixir, league, townhall);
		
	}
	
	/**
	 * Computes the template match intensity image and displays the results. Brighter intensity indicates
	 * a better match to the template.
	 */
	public static void showMatchIntensity(ImageFloat32 image, ImageFloat32 template, String label) {
 
		// create algorithm for computing intensity image
		TemplateMatchingIntensity<ImageFloat32> matchIntensity =
				FactoryTemplateMatching.createIntensity(TemplateScoreType.SUM_DIFF_SQ, ImageFloat32.class);
 
		// apply the template to the image
		matchIntensity.process(image, template);
 
		// get the results
		ImageFloat32 intensity = matchIntensity.getIntensity();
 
		// adjust the intensity image so that white indicates a good match and black a poor match
		// the scale is kept linear to highlight how ambiguous the solution is
		float min = ImageStatistics.min(intensity);
		float max = ImageStatistics.max(intensity);
		float range = max - min;
		PixelMath.plus(intensity, -min, intensity);
		PixelMath.divide(intensity, range, intensity);
		PixelMath.multiply(intensity, 255.0f, intensity);
 
		BufferedImage output = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_BGR);
		VisualizeImageData.grayMagnitude(intensity, output, -1);
		ShowImages.showWindow(output, "Match Intensity for " + label);
	}
	
	public static BufferedImage dilatation(BufferedImage image) {
		BufferedImage output = new BufferedImage(image.getWidth(),
				image.getHeight(), image.getType());
		int[] b = { 1, 1, 1, 1 };
		int max = 0;
		int[] newPixel = new int[b.length];
		for (int i = 0; i < image.getWidth() - 1; i++) {
			for (int j = 0; j < image.getHeight() - 1; j++) {
				newPixel[0] = image.getRGB(i, j) * b[0];
				newPixel[1] = image.getRGB(i + 1, j) * b[1];
				newPixel[2] = image.getRGB(i, j + 1) * b[2];
				newPixel[3] = image.getRGB(i + 1, j + 1) * b[3];
				Arrays.sort(newPixel);
				max = newPixel[3];
				output.setRGB(i, j, max);
			}
		}
		return output;
	}

	public static BufferedImage erosion(BufferedImage image) {
		BufferedImage output = new BufferedImage(image.getWidth(),
				image.getHeight(), image.getType());
		int[] b = { 1, 1, 1, 1 };
		int min = 1;
		int[] newPixel = new int[b.length];
		for (int i = 0; i < image.getWidth() - 1; i++) {
			for (int j = 0; j < image.getHeight() - 1; j++) {
				newPixel[0] = image.getRGB(i, j) * b[0];
				newPixel[1] = image.getRGB(i + 1, j) * b[1];
				newPixel[2] = image.getRGB(i, j + 1) * b[2];
				newPixel[3] = image.getRGB(i + 1, j + 1) * b[3];
				Arrays.sort(newPixel);
				min = newPixel[0];
				output.setRGB(i, j, min);
			}
		}
		return output;
	}
}
