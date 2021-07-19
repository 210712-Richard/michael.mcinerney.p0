package com.revature.data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Serializer <T>{
	
	public List<T> readObjectsFromFile(String filename){
		List<T> objects = null;
		
		try(ObjectInputStream o = new ObjectInputStream(new FileInputStream(filename));){
			objects = (List<T>) o.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objects;
	}
	
	public void writeObjectsToFile(List<T> objects, String filename) {
		try(ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(filename));){
			o.writeObject(objects);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
