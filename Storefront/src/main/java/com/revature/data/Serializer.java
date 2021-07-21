package com.revature.data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Serializer <T>{
	private static final Logger log = LogManager.getLogger(Serializer.class); // Used to create logs

	public List<T> readObjectsFromFile(String filename){
		log.trace("App has entered readObjectsFromFile.");
		log.debug("readObjectsFromFile parameters: filename: " + filename);
		List<T> objects = null;
		
		try(ObjectInputStream o = new ObjectInputStream(new FileInputStream(filename));){
			objects = (List<T>) o.readObject();
		} catch (Exception e) {
			log.error("App has an exception: " + e.getMessage());
		}
		log.trace("App is exiting readObjectsFromFile.");
		log.debug("readObjectsFromFile is returning List<T> " + objects);
		return objects;
	}
	
	public void writeObjectsToFile(List<T> objects, String filename) {
		log.trace("App has entered readObjectsFromFile.");
		log.debug("writeObjectsToFile parameters: objects: " + objects + ", filename: " + filename);
		try(ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(filename));){
			o.writeObject(objects);
		} catch (Exception e) {
			log.error("App has an exception: " + e.getMessage());
			e.printStackTrace();
		}
		log.trace("App is exiting writeObjectsFromFile.");
	}
}
