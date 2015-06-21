package com.amp.coclogger.math;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class DataUtils {
	private static final Logger logger = Logger.getLogger(DataUtils.class);
	
	private static final int MAX_GOLD = 2000000;
	private static final int MAX_ELIXIR = 2000000;
	private static final int MAX_DE = 6000;
	private static final int MAX_TROPHY = 100;
	
	private boolean isValid;
	
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
		} catch (NumberFormatException nfe){
			logger.info("Invalid resource string: " + ocr);
			return null;
		}
		
		logger.debug("Returning " + rd);
		return rd;
	}
	
	public static boolean isValid(ResourceData rd){
		return 
				rd != null &&
				rd.getGold() < MAX_GOLD &&
				rd.getGold() >= 0 &&
				rd.getElixir() < MAX_ELIXIR &&
				rd.getElixir() >= 0 &&
				rd.getDarkElixir() < MAX_DE &&
				rd.getDarkElixir() >= 0;// &&
//				rd.getTrophiesWon() < MAX_TROPHY &&
//				rd.getTrophiesWon() > 0;
	}

}
