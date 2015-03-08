package com.amp.coclogger.prefs;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import boofcv.core.image.ConvertBufferedImage;
import boofcv.struct.image.ImageFloat32;

public enum Townhall {
	TH1, TH2, TH3, TH4, TH5, TH6, TH7, TH8, TH9, TH10;
	
	private BufferedImage image;
	private ImageFloat32 if32;
	
	Townhall(){
		URL url = ClassLoader.getSystemResource("townhalls/" + this.toString() + ".png");
		try{
			if(url == null){
				image = null;
				if32 = null;
			} else {
				image = ImageIO.read(url);
				if32 = ConvertBufferedImage.convertFrom(image, (ImageFloat32)null);
			}
		} catch (IOException ioe){
			image = null;
			if32 = null;
//			ioe.printStackTrace();
		}
	}
	
	public BufferedImage getImage(){
		return image;
	}
	
	public ImageFloat32 getImageFloat32(){
		return if32;
	}
	
}
