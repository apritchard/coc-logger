package com.amp.coclogger.prefs;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import boofcv.core.image.ConvertBufferedImage;
import boofcv.struct.image.ImageFloat32;

public enum League {
	BRONZEIII, BRONZEII, BRONZEI,
	SILVERIII, SILVERII, SILVERI,
	GOLDIII, GOLDII, GOLDI,
	CRYSTALIII, CRYSTALII, CRYSTALI,
	MASTERIII, MASTERII, MASTERI,
	CHAMPION;
	
	private BufferedImage image;
	private ImageFloat32 if32;
	
	League(){
		URL url = ClassLoader.getSystemResource("league-icons/" + this.toString() + ".png");
		try{
			image = ImageIO.read(url);
			if32 = ConvertBufferedImage.convertFrom(image, (ImageFloat32)null);
		} catch (IOException ioe){
			
			ioe.printStackTrace();
		}
	}
	
	public BufferedImage getImage(){
		return image;
	}
	
	public ImageFloat32 getImageFloat32(){
		return if32;
	}
	
}
