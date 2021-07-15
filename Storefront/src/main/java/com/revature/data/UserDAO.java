package com.revature.data;

import java.util.ArrayList;
import java.util.List;

import com.revature.models.AccountType;
import com.revature.models.User;

public class UserDAO {
	private static List<User> users;
	
	private static String filename = "users.dat";
	
	
	static {
		users = new Serializer<User>().readObjectsFromFile(filename);
		if(users == null) {
			users = new ArrayList<User>();
			users.add(new User(users.size(), "DefaultUser", "DefaultPassword", "defaultUser@email.com", AccountType.CUSTOMER, true));
			users.add(new User(users.size(), "DefaultManager", "DefaultPassword", "defaultManager@email.com", AccountType.MANAGER, true));
			users.add(new User(users.size(), "admin", "123password@123", "admin@email.com", AccountType.ADMINISTRATOR, true));
			users.add(new User(users.size(), "badUser", "pass", "bad@user.com", AccountType.CUSTOMER, false));
		}
	}
	
	public User getUser(String username, String password) {
		
		for(User u : users) {
			if (username.equals(u.getUsername()) && password.equals(u.getPassword())) {
				return u;
			}
		}
		return null;
	}
	
	public void writeToFile() {
		new Serializer<User>().writeObjectsToFile(users, filename);
	}
	
	public User register(String username, String password, String email, AccountType type) {
		return null;
	}
}
