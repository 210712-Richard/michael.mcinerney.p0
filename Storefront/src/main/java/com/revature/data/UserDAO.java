package com.revature.data;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.models.AccountType;
import com.revature.models.User;

public class UserDAO {
	public static List<User> users; //List of all the users
	
	private static String filename = "users.dat"; //Name of the file where the users are stored at.
	
	private static final Logger log = LogManager.getLogger(UserDAO.class); // Used to create log

	
	/**
	 * static block that loads the file, or populates the array with default accounts if no file exists.
	 */
	static {
		try {
			log.trace("UserDAO is getting " + filename);
			users = new Serializer<User>().readObjectsFromFile(filename);
		} catch (Exception e) { //Logs the error, the app will continue as usual
			log.warn(filename + " was not found.");
		}
		
		if(users == null) { //If users ends up being null
			users = new ArrayList<User>();
			users.add(new User(users.size(), "DefaultUser", "DefaultPassword", "defaultUser@email.com", AccountType.CUSTOMER, true));
			users.add(new User(users.size(), "DefaultManager", "DefaultPassword", "defaultManager@email.com", AccountType.MANAGER, true));
			users.add(new User(users.size(), "admin", "123password@123", "admin@email.com", AccountType.ADMINISTRATOR, true));
			users.add(new User(users.size(), "badUser", "pass", "bad@user.com", AccountType.CUSTOMER, false));
			log.debug("Initialized list of default users: " + users);
		}
	}
	
	/**
	 * Get the user based on the username and password
	 * @param username The username of the User to get
	 * @param password The password of the User to get
	 * @return The User with the same username and password
	 */
	public User getUser(String username, String password) {
		log.trace("App has entered getUser.");
		log.debug("getUser Parameters: username: " + username+ ", password: " + password);
		for(User u : users) { //Iterate through each User
			if (username.equals(u.getUsername()) && password.equals(u.getPassword())) { //If the username and password of a User is the same as the parameters
				log.trace("App is now leaving getUser.");
				log.debug("getUser is returning User: " + u);
				return u; //Return the correct User
			}
		}
		log.trace("App is now leaving getUser.");
		log.debug("getUser is returning User: " + null);
		return null; //Only returns null if no matching user was found.
	}
	
	/**
	 * Save the current list of users to the file
	 */
	public void writeToFile() {
		log.trace("App is now in writeToFile.");
		new Serializer<User>().writeObjectsToFile(users, filename); //Call the serializer to write to the file
		log.trace("App is exiting writeToFile.");
	}
	
	/**
	 * Creates a new User and adds it to the list
	 * @param username The username of the new User
	 * @param password The password of the new User
	 * @param email The email of the new User
	 * @param type The type of account of the new User
	 * @return The new User added to the list
	 */
	public User createUser(String username, String password, String email, AccountType type) {
		log.trace("App has entered createUser.");
		log.debug("createUser Parameters: username: " + username+ ", password: " + password + ", email: " + email + ", type: "+ type);
		User newUser = new User(users.size(), username, password, email, type, true); //Create the new user
		log.debug("newUser has been created: " + newUser);
		users.add(newUser); //Add the new user to the list
		log.debug("New user was added to the list: " + users.contains(newUser));
		log.trace("App is now leaving createUser.");
		log.debug("createUser is returning User: " + newUser);
		return newUser;
	}
	
	/**
	 * Check to see if the username has been taken already.
	 * @param username The username to check
	 * @return true if the username is unique. false otherwise
	 */
	public Boolean checkUsername(String username) {
		log.trace("App has entered checkUsername.");
		log.debug("checkUsername Parameters: username: " + username);
		for (User user : users) { //Iterate through the list of users.
			if (username.equals(user.getUsername())) { //If the username has been taken
				log.debug(username + " has been found: " + user.getUsername());
				log.trace("App is now leaving checkUsername.");
				log.debug("checkUsername is returning boolean: " + false);
				return false;
			}
		}
		log.trace("App is now leaving checkUsername.");
		log.debug("checkUsername is returning boolean: " + true);
		return true; //Means the username is unique
	}
	
	/**
	 * Searches for a User by username, account type, and active status
	 * @param searchString The username to search by
	 * @param type The types of accounts to filter by
	 * @param status The active status of accounts to filter by
	 * @return A list of users that contain the username and match the set type and status. An empty List otherwise.
	 */
	public List<User> findUsersByName(String searchString, AccountType type, boolean status){
		log.trace("App has entered findUsersByName.");
		log.debug("findUsersByName Parameters: searchString: " + searchString + ", type: " + type + ", status: " + status);
		ArrayList<User> retUsers = new ArrayList<User>(); //Initialize the list
		for (User user : users) { //Iterate through the list of users
			//If the User username contains the parameter searchString, and has the same type and active status as the parameter type and active status.
			if (user.getUsername().contains(searchString) && user.getAccountType() == type && user.isActive() == status) {
				log.debug("Adding User to the list: " + user);
				retUsers.add(user);
			}
		}
		log.trace("App is now leaving findUsersByName.");
		log.debug("findUsersByName is returning List<User>: " + retUsers);
		return retUsers;
	}
}
