package com.revature.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.menus.MainMenu;
import com.revature.models.AccountType;
import com.revature.models.User;

public class UserDAO {
	public static List<User> users; //List of all the users
	
	private static String filename = "users.dat"; //Name of the file where the users are stored at.
	
	private static final Logger log = LogManager.getLogger(UserDAO.class); // Used to create log

	
	
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
	
	public List<User> findUsersByName(String searchString, AccountType type, boolean status){
		ArrayList<User> retUsers = new ArrayList<User>();
		for (User user : users) {
			if (user.getUsername().contains(searchString) && user.getAccountType() == type && user.isActive() == status) {
				retUsers.add(user);
			}
		}
		return retUsers;
	}
}
