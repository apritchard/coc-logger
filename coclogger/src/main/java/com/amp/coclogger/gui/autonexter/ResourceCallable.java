package com.amp.coclogger.gui.autonexter;

import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.amp.coclogger.math.DataUtils;
import com.amp.coclogger.math.ResourceData;
import com.amp.coclogger.ocr.ImageUtils;
import com.amp.coclogger.prefs.PrefName;

public class ResourceCallable implements Callable<ResourceData>{
	private static final Logger logger = Logger.getLogger(ResourceCallable.class);
	
	private int timeoutNano;
	
	public ResourceCallable(int timeoutSec){
		timeoutNano = timeoutSec * 1000000000;
	}
	
	@Override
	public ResourceData call() throws Exception {
		int x = PrefName.TEXT_X.getInt();
		int y = PrefName.TEXT_Y.getInt();
		int width = PrefName.TEXT_WIDTH.getInt();
		int height = PrefName.TEXT_HEIGHT.getInt();
		
		long start = System.nanoTime();
		
		//initial fast check for image lumosity to rule out transition screen
		BufferedImage img = ImageUtils.captureScreen(x, y, width, height);
		while(ImageUtils.getLumosity(img) > 220){
			if(System.nanoTime() - start > timeoutNano){
				logger.info("Exceeded timeout, bailing");
				throw new TimeoutException();
			}
			logger.info("Waiting for next match.");
			Thread.sleep(1000);
			img = ImageUtils.captureScreen(x, y, width, height);
		}
		
		//try to parse until we get a good result or time out
		ResourceData rd = DataUtils.parseResource(ImageUtils.readImage(img));
		while(rd == null){
			logger.info("Still can't read, reattempting");
			if(System.nanoTime() - start > timeoutNano){
				logger.info("Exceeded timeout, bailing");
				throw new TimeoutException();
			}
			Thread.sleep(1000);
			rd = DataUtils.parseResource(ImageUtils.readImage(x, y, width, height));			
		}
		
		return rd;
	}

}
