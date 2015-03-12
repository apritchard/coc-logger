package com.amp.coclogger.math;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class DataUtils {
	private static final Logger logger = Logger.getLogger(DataUtils.class);
	
	public static ResourceData parseResource(String ocr){
		List<String> lines = new ArrayList<>();
		for(String s : ocr.split("\n")){
			String temp = s.replaceAll("\\s+", "");
			if(!temp.isEmpty()){
				lines.add(temp);
			}
		}
		
		int trophyLine = 3;
		switch(lines.size()){
		case 3:
			trophyLine--;
			break;
		case 4:
			break;
		default:
			logger.info("Invalid resource string: " + ocr);
			return null;			
		}
		
		String goldStr = lines.get(0);
		String elixirStr = lines.get(1);
		String darkElixirStr = lines.size() == 5 ? "0" : lines.get(2);
		String trophiesStr = lines.get(trophyLine);
//		String loseTrophiesStr = lines.get(4+lineOffset);

		ResourceData rd = new ResourceData();
		try{
			rd.setGold(Integer.parseInt(goldStr));
			rd.setElixir(Integer.parseInt(elixirStr));
			rd.setDarkElixir(Integer.parseInt(darkElixirStr));
			rd.setTrophiesWon(Integer.parseInt(trophiesStr));
//			rd.setTrophiesLost(Integer.parseInt(loseTrophiesStr));
			logger.info("Returning " + rd);
			return rd;
		} catch (NumberFormatException nfe){
			logger.info("Invalid resource string: " + ocr);
			return null;
		}
	}

}
