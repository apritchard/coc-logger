package com.amp.coclogger.ocr;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

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

import com.amp.coclogger.prefs.League;

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
	
	public static League identifyLeague(BufferedImage image){
		BufferedImage enlargedImage = new BufferedImage(image.getWidth()*2, image.getHeight()*2, image.getType());
		
		AffineTransform at = new AffineTransform();
		at.scale(2.0, 2.0);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
		enlargedImage= scaleOp.filter(image, enlargedImage);
		
		TemplateMatching<ImageFloat32> matcher = FactoryTemplateMatching.createMatcher(TemplateScoreType.SUM_DIFF_SQ, ImageFloat32.class);
		

		League bestLeague = League.BRONZEIII;
		double bestScore = Double.NEGATIVE_INFINITY;
		
//		ImageFloat32 if32 = ConvertBufferedImage.convertFrom(image, (ImageFloat32)null);
		ImageFloat32 if32 = ConvertBufferedImage.convertFrom(enlargedImage, (ImageFloat32)null);
		for(League l : League.values()){
			showMatchIntensity(if32, l.getImageFloat32(), l.toString());
			System.out.println("Checking " + l);
			ImageFloat32 template = l.getImageFloat32();
			matcher.setTemplate(template, 10);
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
	
	public static void convolve(BufferedImage small, BufferedImage large){
		TemplateMatching<ImageFloat32> matcher = FactoryTemplateMatching.createMatcher(TemplateScoreType.SUM_DIFF_SQ, ImageFloat32.class);
		ImageFloat32 template = ConvertBufferedImage.convertFrom(small, (ImageFloat32)null);
		ImageFloat32 image = ConvertBufferedImage.convertFrom(large, (ImageFloat32)null);
		matcher.setTemplate(template, 1);
		matcher.process(image);
		List<Match> templates = matcher.getResults().toList();
		templates.get(0);
		
		
				
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
