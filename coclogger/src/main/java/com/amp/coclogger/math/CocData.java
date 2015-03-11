package com.amp.coclogger.math;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public enum CocData {
	INSTANCE;
	
	private static final Logger logger = Logger.getLogger(CocData.class);
	private CocStats cocStats;
	private List<CocResult> data;
	
	public static CocData getInstance(){
		return INSTANCE;
	}
	
	public void readFile(String dataLocation){
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataLocation))){
			@SuppressWarnings("unchecked")
			List<CocResult> result = (List<CocResult>)ois.readObject();
			if(result == null){
				result = new ArrayList<>();
				logger.info("No previous data, initializing as blank");
			}
			data = result;
			cocStats = new CocStats(result);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			logger.info("No previous data, initializing as blank");
			cocStats = new CocStats(new ArrayList<CocResult>());
		}
	}
	
	public void writeFile(String dataLocation){
		if(dataLocation == null){
			//TODO
			dataLocation = "defaultCocData.cocd";
		}
		try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(dataLocation)))){
			logger.info("Writing data to file");
			oos.writeObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addData(CocResult result){
		if(cocStats == null){
			throw new IllegalStateException("No File Selected, unable to save data");
		}
		cocStats.add(result);
		data.add(result);
	}
	
	public CocStats getStats(){
		return cocStats;
	}
	

}
