package com.revature.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.AccountType;
import com.revature.beans.CartItem;
import com.revature.beans.Order;
import com.revature.beans.OrderStatus;
import com.revature.beans.User;
import com.revature.data.UserDAO;

public class UserService {

	private UserDAO ud = new UserDAO(); // The data handler for users

	private static final Logger log = LogManager.getLogger(UserService.class); // Used to create log

	/**
	 * Allows the user to login by making sure their username and password
	 * combination is correct.
	 * 
	 * @param username The username of the user trying to login.
	 * @param password The password of the user trying to login.
	 * @return The user that has the same username and password. Null otherwise.
	 */
	public User login(String username, String password) {
		log.trace("App has entered login.");
		log.debug("login Parameters: username: " + username + ", password: " + password);

		User u = ud.getUser(username, password); // Retrieve the user from the
		log.trace("App has returned to login.");
		ud.writeToFile();
		log.trace("App has returned to login.");
		log.trace("App is exiting login.");
		log.debug("login is returning User: " + u);
		return u;
	}

	/**
	 * Used to create a new account.
	 * 
	 * @param username The username of the new user. Must be unique from other
	 *                 users.
	 * @param password The password of the new user.
	 * @param email    The email of the new user.
	 * @param type     The account type of the new user.
	 * @return The newly created user.
	 */
	public User register(String username, String password, String email, AccountType type) {
		log.trace("App has entered register.");
		log.debug("register Parameters: username: " + username + ", password: " + password + ", email: " + email
				+ ", type: ", type);
		User u = ud.createUser(username, password, email, type); // Sends the parameters to the DAO to create a new user
		log.trace("App has returned to register.");
		ud.writeToFile(); // Save the new user to file.
		log.trace("App has returned to register.");
		log.trace("App is exiting register.");
		log.debug("register is returning User: " + u);
		return u; // Return the new user object.
	}

	/**
	 * Checks to see if the user's desired username has been registered or not
	 * 
	 * @param username The username the user wants to register with
	 * @return false if the username has already been registers, true otherwise
	 */
	public Boolean isUsernameUnique(String username) {
		log.trace("App has entered isUsernameUnique.");
		log.debug("isUsernameUnique Parameters: username: " + username);
		if (username == null || username.isBlank()) { // If the username entered was null or blank.
			log.warn("User entered a null or blank username");
			log.trace("App is leaving isUsernameUnique.");
			log.debug("App is returning Boolean: " + false);
			return false;
		}
		List<User> users = ud.getUsers();
		for (User user : users) { // Iterate through the list of users.
			if (username.equals(user.getUsername())) { // If the username has been taken
				log.debug(username + " has been found: " + user.getUsername());
				log.trace("App is now exiting isUsernameUnique.");
				log.debug("isUsernameUnique is returning Boolean: " + false);
				return false;
			}
		}
		ud.writeToFile(); // Save the file
		log.trace("App has returned to isUsernameUnique.");
		log.trace("App is exiting isUsernameUnique.");
		log.debug("register is returning boolean: " + true);
		return true;
	}

	/**
	 * Used to save the new user details to the file
	 * 
	 * @param user
	 * @return
	 */
	public User updateUser(User user) {
		log.trace("App has entered updateUser.");
		log.debug("isUsernameUnique Parameters: user: " + user);
		if (user == null) { // If the user passed in was null
			log.warn("user passed in was null");
			log.trace("App is exiting updateUser.");
			log.debug("updateUser is returning User: " + null);
			return null;
		}
		ud.writeToFile(); // Save the file.
		log.trace("App has returned to updateUser.");
		log.trace("App is exiting updateUser.");
		log.debug("updateUser is returning User: " + user);
		return user;
	}

	/**
	 * Search for the user by their name, account type, and activation status
	 * 
	 * @param searchString The username to search with. If the name is empty, will
	 *                     return all based on other two parameters.
	 * @param type         The type of account being searched for
	 * @param status       Whether the list of users should include active or
	 *                     deactivated users. True means active, false means
	 *                     deactive.
	 * @return The list of users or null if none were found
	 */
	public List<User> searchUserByName(String searchString, AccountType type, boolean status) {
		log.trace("App has entered searchUserByName.");
		log.debug("searchUserByName Parameters: searchString: " + searchString + ", type: " + type + ", status: "
				+ status);
		List<User> userList = ud.findUsersByName(searchString, type, status); // Search for users from the DAO
		log.trace("App has returned to searchUserByName.");
		log.debug("userList has been set to: " + userList);
		ud.writeToFile();
		log.trace("App has returned to searchUserByName.");
		if (userList.isEmpty()) { // If the list returned nothing, just return null
			userList = null;
		}
		log.trace("App is exiting searchUserByName.");
		log.debug("searchUserByName is returning List<User>: " + userList);
		return userList;
	}

	public void addToCart(User activeUser, int itemId, int quantity, double price) {
		
		if (quantity > 0 && price > 0) {
			// Use a stream to see if the item is already in the cart. Null otherwise.
			CartItem inCart = activeUser.getCart().stream().filter(c -> c.getItemId() == itemId).findFirst().orElse(null);
	
			// The item was not in the cart
			if (inCart == null) {
				activeUser.addToCart(itemId, quantity, price);
			}
			// The item is in the cart
			else {
				inCart.setQuantity(quantity + inCart.getQuantity());
				log.debug("User changed the quantity to " + inCart.getQuantity());
			}
	
			log.debug(activeUser.getUsername() + " now has a cart of " + activeUser.getCart());
	
			ud.writeToFile();
		}
		

	}

	public void createOrder(User activeUser) {
		if (!activeUser.getCart().isEmpty()) {
			activeUser.createOrder(); // Creates the order inside User bean
			ud.writeToFile();
		}

	}

	public void changeActiveStatus(User activeUser, boolean status) {
		if (activeUser != null) {
			activeUser.setActive(status);
			if (!activeUser.getCart().isEmpty()) {
				activeUser.setCart(new ArrayList<CartItem>()); // Empty the cart.
			}
			ud.writeToFile();
		}
		
	}

	public void changeQuantityInCart(CartItem cartItem, int quantity) {
		if (cartItem != null && quantity > 0) {
			cartItem.setQuantity(quantity);
			ud.writeToFile();
		}
		

	}

	public void changeOrderStatus(Order order, OrderStatus status) {
		if (order != null && status != null) {
			order.setStatus(status);
			ud.writeToFile();
		}
		

	}

	public void changeCartItemPrice(CartItem cartItem, double price) {
		if (cartItem != null && price > 0.0) {
			cartItem.setPrice(price);
			ud.writeToFile();
		}
		
	}

}
