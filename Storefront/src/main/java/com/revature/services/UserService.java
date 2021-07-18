package com.revature.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.AccountType;
import com.revature.beans.User;
import com.revature.data.UserDAO;
import com.revature.menus.MainMenu;

public class UserService {
	
	private UserDAO ud = new UserDAO(); //The data handler for users
	
	private static final Logger log = LogManager.getLogger(UserService.class); // Used to create log

	 /**
	  * Allows the user to login by making sure their username and password combination is correct.
	  * @param username The username of the user trying to login.
	  * @param password The password of the user trying to login.
	  * @return The user that has the same username and password. Null otherwise.
	  */
	public User login(String username, String password) {
		log.trace("App has entered login.");
		log.debug("login Parameters: username: " + username + ", password: " + password);
		
		User u = ud.getUser(username, password); //Retrieve the user from the 
		log.trace("App has returned to login.");
		ud.writeToFile();
		log.trace("App has returned to login.");
		log.trace("App is exiting login.");
		log.debug("login is returning User: " + u);
		return u;
	}
	
	/**
	 * Used to create a new account.
	 * @param username The username of the new user. Must be unique from other users.
	 * @param password The password of the new user.
	 * @param email The email of the new user.
	 * @param type The account type of the new user. 
	 * @return The newly created user.
	 */
	public User register(String username, String password, String email, AccountType type) {
		log.trace("App has entered register.");
		log.debug("register Parameters: username: " + username + ", password: " + password + ", email: " + email + ", type: ", type);
		User u = ud.createUser(username, password, email, type); //Sends the parameters to the DAO to create a new user
		log.trace("App has returned to register.");
		ud.writeToFile(); //Save the new user to file.
		log.trace("App has returned to register.");
		log.trace("App is exiting register.");
		log.debug("register is returning User: " + u);
		return u; //Return the new user object.
	}
	
	/**
	 * Checks to see if the user's desired username has been registered or not
	 * @param username The username the user wants to register with
	 * @return false if the username has already been registers, true otherwise
	 */
	public Boolean isUsernameUnique(String username) {
		log.trace("App has entered isUsernameUnique.");
		log.debug("isUsernameUnique Parameters: username: " + username);
		boolean isUnique = ud.checkUsername(username); //Check the DAO to see if username is taken
		log.trace("App has returned to isUsernameUnique.");
		ud.writeToFile(); //Save the file
		log.trace("App has returned to isUsernameUnique.");
		log.trace("App is exiting isUsernameUnique.");
		log.debug("register is returning boolean: " + isUnique);
		return isUnique;
	}
	
	/**
	 * Used to save the new user details to the file
	 * @param user
	 * @return
	 */
	public User changeUserDetails(User user) {
		if (user == null) {
			return null;
		}
		log.trace("App has entered changeUserDetails.");
		log.debug("isUsernameUnique Parameters: user: " + user);
		ud.writeToFile(); //Save the file.
		log.trace("App has returned to changeUserDetails.");
		log.trace("App is exiting changeUserDetails.");
		log.debug("register is returning User: " + user);
		return user;
	}
	
	/**
	 * Search for the user by their name, account type, and activation status
	 * @param searchString The username to search with. If the name is empty, will return all based on other two parameters.
	 * @param type The type of account being searched for
	 * @param status Whether the list of users should include active or deactivated users. True means active, false means deactive.
	 * @return The list of users or null if none were found
	 */
	public List<User> searchUserByName(String searchString, AccountType type, boolean status){
		log.trace("App has entered searchUserByName.");
		log.debug("searchUserByName Parameters: searchString: " + searchString+ ", type: " + type + ", status: " + status);
		List<User> userList = ud.findUsersByName(searchString, type, status); //Search for users from the DAO
		log.trace("App has returned to searchUserByName.");
		log.debug("userList has been set to: " + userList);
		ud.writeToFile();
		log.trace("App has returned to searchUserByName.");
		if (userList.isEmpty()) { //If the list returned nothing, just return null
			userList = null;
		}
		log.trace("App is exiting searchUserByName.");
		log.debug("searchUserByName is returning List<User>: " + userList);
		return userList;
	}
	
}
