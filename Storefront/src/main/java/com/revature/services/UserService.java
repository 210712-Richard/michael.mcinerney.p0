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
	
	public User getUser(String username) {
		log.trace("App is now in getUser.");
		log.debug("getUser parameters: username: " + username);
		
		User retUser = ud.getUser(username, username);
		log.trace("App is exiting getUser");
		log.debug("getUser returning User: " + retUser);
		return retUser;
	}

	/**
	 * Checks to see if the user's desired username has been registered or not
	 * 
	 * @param username The username the user wants to register with
	 * @return false if the username has already been registered, true otherwise
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
	
	/**
	 * Adds an item to the user's cart
	 * @param activeUser The user adding the item
	 * @param itemId The Id of the item
	 * @param quantity How much of the item is going into the cart
	 * @param price The current price of the item
	 */
	public void addToCart(User activeUser, int itemId, int quantity, double price) {
		log.trace("App has entered addToCart.");
		log.debug("addToCart Parameters: activeUser: " + activeUser + ", itemId: " + itemId + ", quantity: "
				+ quantity + ", price: " + price);
		if (quantity > 0 && price > 0 && activeUser != null) {
			// Use a stream to see if the item is already in the cart. Null otherwise.
			CartItem inCart = activeUser.getCart().stream().filter(c -> c.getItemId() == itemId).findFirst().orElse(null);
			log.debug("Cart item was found in cart: " + inCart);
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
			log.trace("App has returned to addToCart.");
		}
		log.trace("App is exiting addToCart.");

	}
	/**
	 * Creates the order for the user from their cart
	 * @param activeUser The user creating the order
	 */
	public void createOrder(User activeUser) {
		log.trace("App has entered createOrder.");
		log.debug("createOrder Parameters: activeUser: " + activeUser);
		
		//Makes sure the user actually has something inside their cart
		if (!activeUser.getCart().isEmpty()) {
			activeUser.createOrder(); // Creates the order inside User bean
			ud.writeToFile();
			log.trace("App has returned to createOrder.");
		}
		log.trace("App is exiting createOrder.");
	}
	
	/**
	 * Changes the active status of the user
	 * @param activeUser The user
	 * @param status The status the user is being changed to
	 */
	public void changeActiveStatus(User activeUser, boolean status) {
		log.trace("App has entered changeActiveStatus.");
		log.debug("changeActiveStatus Parameters: activeUser: " + activeUser + ", status: " + status);
		
		//If the user entered is not null
		if (activeUser != null) {
			activeUser.setActive(status);
			log.debug("User status has changed to " + activeUser.isActive());
			
			//If the user has stuff in their cart, this will empty their cart
			if (!activeUser.getCart().isEmpty()) {
				activeUser.setCart(new ArrayList<CartItem>()); // Empty the cart.
				log.debug("User status has changed to " + activeUser.getCart());
			}
			ud.writeToFile();
			log.trace("App has returned to changeActiveStatus.");

		}
		log.trace("App is exiting changeActiveStatus.");
	}
	
	/**
	 * Changes the quantity to the cartItem to the one specified
	 * @param cartItem The CartItem being changed
	 * @param quantity The quantity to change to
	 */
	public void changeQuantityInCart(CartItem cartItem, int quantity) {		
		log.trace("App has entered changeQuantityInCart.");
		log.debug("changeQuantityInCart Parameters: cartItem: " + cartItem + ", quantity: " + quantity);
		
		//The CartItem passed in isn't null and the quantity is greater than zero
		if (cartItem != null && quantity > 0) {
			cartItem.setQuantity(quantity); //Set the quantity
			log.debug("cartItem quantity is now " + cartItem.getQuantity());
			ud.writeToFile();
			log.trace("App has returned to changeQuantityInCart.");

		}
		log.trace("App is exiting changeQuantityInCart.");
	}
	
	/**
	 * Changes the OrderStatus of a specified Order
	 * @param order The Order being modified
	 * @param status The OrderStatus the Order is setting
	 */
	public void changeOrderStatus(Order order, OrderStatus status) {
		log.trace("App has entered changeOrderStatus.");
		log.debug("changeOrderStatus Parameters: order: " + order + ", status: " + status);
		
		//If the order and status are not null
		if (order != null && status != null) {
			order.setStatus(status); //Changes the status
			log.debug("order status is now " + order.getStatus());
			ud.writeToFile();
			log.trace("App has returned to changeOrderStatus.");
		}
		log.trace("App is exiting changeOrderStatus.");
	}
	
	/**
	 * Changes the CartItem price to the one specified
	 * @param cartItem The CartItem being modified
	 * @param price The price the CartItem is being set to
	 */
	public void changeCartItemPrice(CartItem cartItem, double price) {
		log.trace("App has entered changeCartItemPrice.");
		log.debug("changeCartItemPrice Parameters: cartItem: " + cartItem + ", price: " + price);
		
		//If the cart item passed in wasn't and null and the price is greater than zero
		if (cartItem != null && price > 0.0) {
			cartItem.setPrice(price); //Set the price
			log.debug("cartItem price is now " + cartItem.getPrice());
			ud.writeToFile();
			log.trace("App has returned to changeCartItemPrice.");

		}
		log.trace("App is exiting changeCartItemPrice.");
	}

}
