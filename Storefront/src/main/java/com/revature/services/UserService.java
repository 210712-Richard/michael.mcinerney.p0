package com.revature.services;

import java.util.List;

import com.revature.data.UserDAO;
import com.revature.models.AccountType;
import com.revature.models.User;

public class UserService {
	
	private UserDAO ud = new UserDAO();
	
	public User login(String username, String password) {
		if (username == null || password == null || username.isBlank() || password.isBlank()) {
			return null;
		}
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
		if(username == null || username.isBlank() ) {
			return false;
		}
		boolean isUnique = ud.checkUsername(username);
		ud.writeToFile();
		return isUnique;
	}
	
	public User changeUserDetails(User user) {
		//Will not actually do anything except save the file. Will be used later when its time to work
		//with a backend.
		ud.writeToFile();
		return user;
	}
	
	public List<User> searchUserByName(String searchString, AccountType type, boolean status){
		List<User> userList = ud.findUsersByName(searchString, type, status);
		ud.writeToFile();
		if (userList.isEmpty()) {
			return null;
		}
		return userList;
	}
	
}
