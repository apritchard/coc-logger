package com.amp.coclogger.ocr;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.amp.coclogger.prefs.ImageFileType;
import com.amp.coclogger.prefs.PrefName;

public class ImageCombiner {
	private Queue<BufferedImage> images;
	int width, height;
	int imagesPerPage;
	
	public ImageCombiner(int width, int height, int imagesPerPage){
		this.width = width;
		this.height = height;
		
		this.imagesPerPage = imagesPerPage;
		
		images = new LinkedList<>();
	}
	
	public void add(BufferedImage bi){
		images.add(bi);
	}
	
	
	public List<BufferedImage> combine(){
		List<BufferedImage> imagesToPrint = new ArrayList<>();
		
		double rowsD = Math.floor(Math.sqrt((double)imagesPerPage));
		double colsD = Math.ceil(imagesPerPage/rowsD);
		int rows = (int)rowsD;
		int cols = (int)colsD;
		
		int w = width * cols;
		int h = height * rows;
		
		System.out.println(String.format("Printing %d rows of %d columns at (%d x %d, %d x %d per pic)",
				rows, cols, w, h, width, height ));
		
		
		int biType = BufferedImage.TYPE_3BYTE_BGR;
		switch(ImageFileType.valueOf(PrefName.IMAGE_FILE_TYPE.get())){
		case BMP:
			break;
		case GIF:
			break;
		case JPEG:
			break;
		case PNG:
			break;
		case TIFF:
			break;
		default:
			break;
		
		}
		
		while(!images.isEmpty()){
			BufferedImage temp = new BufferedImage(w,h, biType);
			Graphics2D g2d = temp.createGraphics();
			for(int i = 0; i < rows; i++){
				for(int j = 0; j < cols; j++){
					BufferedImage img = images.poll();
					System.out.println(String.format("Drawing img of width %d at location %d", img.getWidth(), width*j));
					g2d.drawImage(img, null, width*j, height*i);
					if(images.isEmpty()){
						imagesToPrint.add(temp);
						return imagesToPrint;
					}
				}
			}
			imagesToPrint.add(temp);
		}
		//shouldn't get here, but...
		return imagesToPrint;
	}
	
	
	

}
