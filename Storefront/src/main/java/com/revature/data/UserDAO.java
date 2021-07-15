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
	
	public User createUser(String username, String password, String email, AccountType type) {
		User newUser = new User(users.size(), username, password, email, type, true);
		users.add(newUser);
		return newUser;
	}
	
	
	public boolean checkUsername(String username) {
		for (User user : users) {
			if (username.equals(user.getUsername())) { //Checks to make sure the username is unique
				return false;
			}
		}
		return true;
	}
	
	public User editUser(User user) {
		//Just a placeholder for now.
		return null;
	}
}
