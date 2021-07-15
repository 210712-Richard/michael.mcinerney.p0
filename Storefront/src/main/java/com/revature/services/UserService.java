package com.revature.services;

import com.revature.data.UserDAO;
import com.revature.models.User;

public class UserService {
	
	private UserDAO ud = new UserDAO();
	
	public User login(String username, String password) {
		User u = ud.getUser(username, password);
		saveChanges();
		return u;
	}
	
	public void saveChanges() {
		ud.writeToFile();
	}
	
}
