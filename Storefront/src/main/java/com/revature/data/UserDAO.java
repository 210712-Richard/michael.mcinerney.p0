package com.revature.data;

import java.util.List;

import com.revature.models.AccountType;
import com.revature.models.User;

public class UserDAO {
	private static List<User> users;
	
	private static String filename = "users.dat";
	
	
	static {
		//TODO: implement reading from the file in static block and populating if no file
		users = new Serializer<User>().readObjectsFromFile(filename);
		if(users == null) {
			users.add(new User(users.size(), "DefaultUser", "DefaultPassword", "defaultUser@email.com", AccountType.CUSTOMER, true));
			users.add(new User(users.size(), "DefaultUser", "DefaultPassword", "defaultManager@email.com", AccountType.MANAGER, true));
			users.add(new User(users.size(), "admin", "123password@123", "admin@email.com", AccountType.ADMINISTRATOR, true));
		}
	}
	
	public User getUser(String username, String password) {
		
		for(User u : users) {
			if (username == u.getUsername() && password == u.getPassword()) {
				return u;
			}
		}
		return null;
	}
	
	public void writeToFile() {
		new Serializer<User>().writeObjectsToFile(users, filename);
	}
}
