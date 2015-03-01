package com.amp.coclogger.math;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CocData {
	
	private static String dataLocation;
	
	private static void setDataLocation(String dataLocation){
		CocData.dataLocation = dataLocation;
	}
	
	private static List<CocResult> readFile(){
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataLocation))){
			@SuppressWarnings("unchecked")
			List<CocResult> result = (List<CocResult>)ois.readObject();
			return result;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return new ArrayList<CocResult>();
		}
	}
	
	private static void writeFile(List<CocResult> data){
		if(dataLocation == null){
			dataLocation = "defaultCocData.cocd";
		}
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataLocation))){
			oos.writeObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
