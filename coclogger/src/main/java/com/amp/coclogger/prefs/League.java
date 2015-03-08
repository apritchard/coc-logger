package com.amp.coclogger.prefs;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import boofcv.core.image.ConvertBufferedImage;
import boofcv.struct.image.ImageFloat32;

public enum League {
	BRONZE3, BRONZE2, BRONZE1,
	SILVER3, SILVER2, SILVER1,
	GOLD3, GOLD2, GOLD1,
	CRYSTAL3, CRYSTAL2, CRYSTAL1,
	MASTER3, MASTER2, MASTER1,
	CHAMPION;
	
	private BufferedImage image;
	private ImageFloat32 if32;
	
	League(){
		URL url = ClassLoader.getSystemResource("seen-leagues/" + this.toString() + ".png");
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
