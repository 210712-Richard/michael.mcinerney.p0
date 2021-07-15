package com.revature.services;

import com.revature.data.UserDAO;
import com.revature.models.AccountType;
import com.revature.models.User;

public class UserService {
	
	private UserDAO ud = new UserDAO();
	
	public User login(String username, String password) {
		User u = ud.getUser(username, password);
		ud.writeToFile();
		return u;
	}
	
	public User register(String username, String password, String email, AccountType type) {
		User u = ud.createUser(username, password, email, type);
		ud.writeToFile();
		return u;
	}
	
	/**
	 * Checks to see if the user's desired username has been registered or not
	 * @param username The username the user wants to register with
	 * @return false if the username has already been registers, true otherwise
	 */
	public boolean isUsernameUnique(String username) {
		boolean isUnique = ud.checkUsername(username);
		ud.writeToFile();
		return isUnique;
	}
	
}
