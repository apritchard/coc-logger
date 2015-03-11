package com.amp.coclogger.math;

import org.apache.log4j.Logger;

public class DataUtils {
	private static final Logger logger = Logger.getLogger(DataUtils.class);
	
	public static ResourceData parseResource(String ocr){
		String[] lines = ocr.split("\n");
		
		int lineOffset = 0;
		switch(lines.length){
		case 5:
			break;
		case 6:
			lineOffset = 1;
			break;
		default:
			logger.info("Invalid resource string: " + ocr);
			return null;			
		}
		
		String goldStr = lines[0].replaceAll("\\s+", "");
		String elixirStr = lines[1].replaceAll("\\s+", "");
		String darkElixirStr = lines.length == 5 ? "0" : lines[2].replaceAll("\\s+", "");
		String trophiesStr = lines[2+lineOffset].replaceAll("\\s+", "");
		String loseTrophiesStr = lines[4+lineOffset].replaceAll("\\s+", "");

		ResourceData rd = new ResourceData();
		try{
			rd.setGold(Integer.parseInt(goldStr));
			rd.setElixir(Integer.parseInt(elixirStr));
			rd.setDarkElixir(Integer.parseInt(darkElixirStr));
			rd.setTrophiesWon(Integer.parseInt(trophiesStr));
			rd.setTrophiesLost(Integer.parseInt(loseTrophiesStr));
			return rd;
		} catch (NumberFormatException nfe){
			logger.info("Invalid resource string: " + ocr);
			return null;
		}
	}

}
