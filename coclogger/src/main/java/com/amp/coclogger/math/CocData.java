package com.amp.coclogger.math;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public enum CocData {
	INSTANCE;
	
	private CocStats cocStats;
	private List<CocResult> data;
	
	public static CocData getInstance(){
		return INSTANCE;
	}
	
	public void readFile(String dataLocation){
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataLocation))){
			@SuppressWarnings("unchecked")
			List<CocResult> result = (List<CocResult>)ois.readObject();
			data = result;
			cocStats = new CocStats(result);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			cocStats = new CocStats(new ArrayList<CocResult>());
		}
	}
	
	public void writeFile(String dataLocation){
		if(dataLocation == null){
			//TODO
			dataLocation = "defaultCocData.cocd";
		}
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataLocation))){
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
	}
	

}
