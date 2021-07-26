package com.revature.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.AccountType;
import com.revature.beans.CartItem;
import com.revature.beans.Item;
import com.revature.beans.Order;
import com.revature.beans.OrderStatus;
import com.revature.beans.User;
import com.revature.services.ItemService;
import com.revature.services.UserService;

import io.javalin.http.Context;

public class UserController {
	private static Logger log = LogManager.getLogger(UserController.class);
	private UserService userService = new UserService();
	private ItemService itemService = new ItemService();

	/**
	 * Used to let the user login
	 * 
	 * @param ctx The context
	 */
	public void login(Context ctx) {
		log.trace("App has entered login.");
		log.debug("Request body: " + ctx.body());

		// Create a user from the body
		User user = ctx.bodyAsClass(User.class);
		log.debug("User serialized from context: " + user);

		// Call the login method to get the user
		user = userService.login(user.getUsername(), user.getPassword());
		log.debug("user has been set to " + user);

		if (user != null) { // The user was found

			// If the user has been deactivated
			if (!user.isActive()) {
				ctx.status(403);
				ctx.html("This account is deactivated.");
				log.trace("App is leaving login");

				return;
			}
			// Save the user for the session
			ctx.sessionAttribute("loggedUser", user);

			// Send the user back.
			ctx.json(user);
			log.trace("App is leaving login");
			return;
		}
		// The user couldn't be found.
		ctx.status(401);
		log.trace("App is leaving login");
	}

	/**
	 * Logs the user out
	 * 
	 * @param ctx The context
	 */
	public void logout(Context ctx) {
		log.trace("App has entered logout.");
		log.debug("Request body: " + ctx.body());

		// This gets rid of the session
		ctx.req.getSession().invalidate();
		ctx.status(204);
		log.trace("App is exiting logout.");
	}

	/**
	 * Register a new user
	 * 
	 * @param ctx The context
	 */
	public void register(Context ctx) {
		log.trace("App has entered register.");
		log.debug("Request body: " + ctx.body());

		User loggedUser = ctx.sessionAttribute("loggedUser");
		log.debug("loggedUser: " + loggedUser);
		// If the user is logged in and is not an admin
		if (loggedUser != null && loggedUser.getAccountType() != AccountType.ADMINISTRATOR) {
			ctx.status(403);
			log.trace("App is leaving register");
			return;
		} else {
			// Get the user
			User newUser = ctx.bodyAsClass(User.class);
			log.debug("newUser: " + newUser);

			if (!userService.isUsernameUnique(newUser.getUsername())) {
				ctx.status(409);
				ctx.html("Username has been taken");
				log.trace("App is leaving register");
				return;
			}

			// If the account is not a customer and user is logged in and not an
			// administrator
			if (newUser.getAccountType().equals(AccountType.CUSTOMER)
					|| (loggedUser != null && loggedUser.getAccountType().equals(AccountType.ADMINISTRATOR))) {
				// Register the user
				newUser = userService.register(newUser.getUsername(), newUser.getPassword(), newUser.getEmail(),
						newUser.getAccountType());
				log.debug("newUser: " + newUser);

				if (newUser == null) { // Error with registration
					ctx.status(401);
					ctx.html("Invalid input with account");
					log.trace("App is leaving register");
					return;
				}

				// Return the new user
				ctx.status(201);
				ctx.json(newUser);
				log.trace("App is leaving register");
				return;
			}
			ctx.status(403);
			log.trace("App is leaving register.");

		}
	}

	/**
	 * Allows the user to change their password
	 * 
	 * @param ctx The context
	 */
	public void changePassword(Context ctx) {
		log.trace("App has entered changePassword.");
		log.debug("Request body: " + ctx.body());

		// Get the logged user
		User loggedUser = ctx.sessionAttribute("loggedUser");
		log.debug("loggedUser: " + loggedUser);

		// Get the username from the path
		String username = ctx.pathParam("username");

		// If the user is not logged in or is trying to change the wrong user's password
		if (loggedUser == null || !loggedUser.getUsername().equals(username)) {
			ctx.status(403);
			log.trace("App is leaving changePassword.");
			return;
		}

		// Get the parameters from the body
		User user = ctx.bodyAsClass(User.class);
		log.debug("User serialized from context: " + user);

		// If the object is null or if the password wasn't entered
		if (user == null || user.getPassword() == null || user.getPassword().isBlank()) {
			ctx.status(406);
			ctx.html("The data entered can not be used to change the password.");
			log.trace("App is leaving changePassword.");
			return;
		}

		// Update the password and send back the new loggedUser
		userService.updatePassword(loggedUser, user.getPassword());
		log.debug("loggedUser is now " + loggedUser);
		ctx.json(loggedUser);
		log.trace("App is leaving changePassword.");

	}

	/**
	 * Allows the customer to change their email
	 * 
	 * @param ctx The context
	 */
	public void changeEmail(Context ctx) {
		log.trace("App has entered changeEmail.");
		log.debug("Request body: " + ctx.body());

		// Get the logged in user
		User loggedUser = ctx.sessionAttribute("loggedUser");
		log.debug("loggedUser: " + loggedUser);

		// Get the username from the path
		String username = ctx.pathParam("username");

		// If the user is not logged in or is trying to change the wrong user's password
		// Or if the user is not a CUSTOMER
		if (loggedUser == null || !loggedUser.getUsername().equals(username)
				|| !loggedUser.getAccountType().equals(AccountType.CUSTOMER)) {
			ctx.status(403);
			log.trace("App is leaving changeEmail.");
			return;
		}

		// Get the parameters from the body
		User user = ctx.bodyAsClass(User.class);
		log.debug("User serialized from context: " + user);

		// If the object is null or if the password wasn't entered
		if (user == null || user.getEmail() == null || user.getEmail().isBlank()) {
			ctx.status(406);
			ctx.html("The data entered can not be used to change the password.");
			log.trace("App is leaving changeEmail.");
			return;
		}

		// Update the password and send back the new loggedUser
		userService.updateEmail(loggedUser, user.getEmail());
		log.debug("loggedUser is now " + loggedUser);
		ctx.json(loggedUser);
		log.trace("App is leaving changeEmail.");

	}

	/**
	 * Allows the user to change if their account is active or not Allows admins to
	 * deactivate and reactivate accounts
	 * 
	 * @param ctx The context
	 */
	public void changeActiveStatus(Context ctx) {
		log.trace("App has entered changeActiveStatus.");
		log.debug("Request body: " + ctx.body());

		// Get the logged in user
		User loggedUser = ctx.sessionAttribute("loggedUser");
		log.debug("loggedUser: " + loggedUser);

		// Get the username from the path
		String username = ctx.pathParam("username");

		// If the user is not logged in
		if (loggedUser == null) {
			ctx.status(403);
			log.trace("App is leaving changeActiveStatus.");
			return;
		}

		// Get the parameters from the body
		User user = ctx.bodyAsClass(User.class);
		if (user == null) {
			ctx.status(406);
			ctx.html("Incorrect parameters in body.");
			log.trace("App is leaving changeActiveStatus.");
			return;
		}
		boolean status = user.isActive();
		log.debug("User serialized from context: " + user);
		AccountType loggedType = loggedUser.getAccountType();
		// If the user is a customer and is trying to deactivate their account
		if (loggedType.equals(AccountType.CUSTOMER) && !(!status && loggedUser.getUsername().equals(username))) {

			// Change the active status of the user.
			userService.changeActiveStatus(loggedUser, status);

			// Log the user out
			ctx.req.getSession().invalidate();

			ctx.status(204);
			log.trace("App is leaving changeActiveStatus.");
			return;
		}

		// Get the user from the data
		User userToChange = userService.getUser(username);

		// If the user is a manager and trying to deactivate a customer account
		if (loggedType.equals(AccountType.MANAGER)
				&& (!status && userToChange.getAccountType().equals(AccountType.CUSTOMER))) {
			// Change the active status of the user.
			userService.changeActiveStatus(userToChange, status);

			ctx.json(userToChange);
			log.trace("App is leaving changeActiveStatus.");
			return;
		}

		// If the user is an admin and trying to change a non-admin account
		if (loggedType.equals(AccountType.ADMINISTRATOR)
				&& (!userToChange.getAccountType().equals(AccountType.ADMINISTRATOR))) {
			// Change the active status of the user.
			userService.changeActiveStatus(userToChange, status);

			ctx.json(userToChange);
			log.trace("App is leaving changeActiveStatus.");
			return;
		}

		// The user is trying to do something they are not authorized for
		ctx.status(403);
		log.trace("App is leaving changeActiveStatus.");
		return;
	}

	/**
	 * Add an item to the logged in user's cart
	 * 
	 * @param ctx The context. Should have the item added to cart in body
	 */
	public void addToCart(Context ctx) {
		log.trace("App has entered addToCart.");
		log.debug("Request body: " + ctx.body());

		// Get the logged in user
		User loggedUser = ctx.sessionAttribute("loggedUser");
		log.debug("loggedUser: " + loggedUser);

		// Get the username from the path
		String username = ctx.pathParam("username");

		// If the user is not logged in, is trying to access another user's cart, or is
		// not a customer
		if (loggedUser == null || !loggedUser.getUsername().equals(username)
				|| !loggedUser.getAccountType().equals(AccountType.CUSTOMER)) {
			ctx.status(403);
			log.trace("App is leaving addToCart.");
			return;
		}

		// Get the item from the body.
		CartItem cartItem = ctx.bodyAsClass(CartItem.class);
		// If the item is null or object in body is null
		if (cartItem == null || cartItem.getItem() == null) {
			ctx.status(404);
			ctx.html("No cart item was found.");
			log.trace("App is leaving addToCart.");
			return;
		}
		
		//Get the item from the service
		Item item = itemService.getItem(cartItem.getItem().getId());
		log.debug("Item from itemService: " + item);
		
		//If the item was not found
		if (item == null) {
			ctx.status(404);
			ctx.html("No item was found.");
			log.trace("App is leaving addToCart.");
			return;
		}
		// If the item quantity was less than or equal to zero, or greater than the
		// item's current quantity
		if (cartItem.getQuantity() <= 0 || cartItem.getQuantity() > item.getAmount()
				|| cartItem.getItem().getAmount() <= 0) {
			ctx.status(400);
			ctx.html("Quantity is invalid.");
			log.trace("App is leaving addToCart.");
			return;
		}
		// Add the item to the cart.
		userService.addToCart(loggedUser, item, cartItem.getQuantity());
		log.trace("App has returned to addToCart.");

		ctx.json(loggedUser.getCart());
		log.trace("App is leaving addToCart.");
	}

	/**
	 * Remove an item from the cart
	 * 
	 * @param ctx
	 */
	public void removeFromCart(Context ctx) {
		log.trace("App has entered removeFromCart.");
		log.debug("Request body: " + ctx.body());

		// Get the logged in user
		User loggedUser = ctx.sessionAttribute("loggedUser");
		log.debug("loggedUser: " + loggedUser);

		// Get the username and cartItemId from the path
		String username = ctx.pathParam("username");
		log.debug("Username from path: " + username);
		int cartItemId = Integer.parseInt(ctx.pathParam("cartItemId"));
		log.debug("cartItemId from path: " + cartItemId);

		// If the user is not logged in, is trying to access another user's cart, or is
		// not a customer
		if (loggedUser == null || !loggedUser.getUsername().equals(username)
				|| !loggedUser.getAccountType().equals(AccountType.CUSTOMER)) {
			ctx.status(403);
			log.trace("App is leaving removeFromCart.");
			return;
		}

		// Add the item to the cart.
		userService.removeFromCart(loggedUser, cartItemId);
		log.trace("App has returned to removeFromCart.");

		ctx.json(loggedUser.getCart());
		log.trace("App is leaving removeFromCart.");
	}

	/**
	 * Change the quantity of an item in the cart
	 * 
	 * @param ctx The context
	 */
	public void changeQuantityOfCartItem(Context ctx) {

		log.trace("App has entered changeQuantityOfCartItem.");
		log.debug("Request body: " + ctx.body());

		// Get the logged in user
		User loggedUser = ctx.sessionAttribute("loggedUser");
		log.debug("loggedUser: " + loggedUser);

		// Get the username and cartItemId from the path
		String username = ctx.pathParam("username");
		log.debug("Username from path: " + username);
		int cartItemId = Integer.parseInt(ctx.pathParam("cartItemId"));
		log.debug("cartItemId from path: " + cartItemId);

		// If the user is not logged in, is trying to access another user's cart, or is
		// not a customer
		if (loggedUser == null || !loggedUser.getUsername().equals(username)
				|| !loggedUser.getAccountType().equals(AccountType.CUSTOMER)) {
			ctx.status(403);
			log.trace("App is leaving changeQuantityOfCartItem.");
			return;
		}

		// Get the CartItem from the users cart using cartItemId
		CartItem cartItem = loggedUser.getCart().stream().filter((cI) -> cI.getId() == cartItemId).findFirst()
				.orElse(null);
		log.debug("CartItem from cart: " + cartItem);

		// Get the quantity from the body.
		CartItem cartItemQuantity = ctx.bodyAsClass(CartItem.class);
		log.debug("CartItem from ctx.body(): " + cartItemQuantity);

		// If the object in body is null
		if (cartItemQuantity == null || cartItem == null) {
			ctx.status(404);
			ctx.html("No item was found.");
			log.trace("App is leaving changeQuantityOfCartItem.");
			return;
		}

		// Change the quantity in the cart.
		userService.changeQuantityInCart(cartItem, cartItemQuantity.getQuantity());
		log.trace("App has returned to changeQuantityOfCartItem.");

		ctx.json(loggedUser.getCart());
		log.trace("App is leaving changeQuantityOfCartItem.");
	}

	/**
	 * Creates an order using the logged in user's cart
	 * 
	 * @param ctx The context. The body will not be read
	 */
	public void createOrder(Context ctx) {
		log.trace("App has entered createOrder.");
		log.debug("Request body: " + ctx.body());

		User loggedUser = ctx.sessionAttribute("loggedUser");
		log.debug("loggedUser: " + loggedUser);

		// Get the username and cartItemId from the path
		String username = ctx.pathParam("username");
		log.debug("Username from path: " + username);

		// If the user is not logged in, is trying to access another user's cart, or is
		// not a customer
		if (loggedUser == null || !loggedUser.getUsername().equals(username)
				|| !loggedUser.getAccountType().equals(AccountType.CUSTOMER)) {
			ctx.status(403);
			log.trace("App is leaving createOrder.");
			return;
		}

		// If the user does not have a cart to make into an order
		if (loggedUser.getCart().isEmpty()) {
			ctx.status(404);
			ctx.html("There is nothing in the cart to order.");
			log.trace("App is leaving createOrder.");
			return;
		}

		// Create the order
		userService.createOrder(loggedUser);

		// Return no content
		ctx.status(204);
		log.trace("App is leaving createOrder.");
	}
	
	/**
	 * Change the status of an order
	 * @param ctx The context
	 */
	public void changeOrderStatus(Context ctx) {
		log.trace("App has entered changeOrderStatus.");
		log.debug("Request body: " + ctx.body());

		User loggedUser = ctx.sessionAttribute("loggedUser");
		log.debug("loggedUser: " + loggedUser);

		// Get the username and cartItemId from the path
		String username = ctx.pathParam("username");
		log.debug("Username from path: " + username);
		int orderId = Integer.parseInt(ctx.pathParam("orderId"));
		log.debug("orderId from path: " + orderId);

		// If the user is not logged in, or is an admin, or is a customer and is trying
		// to access another user's order
		if (loggedUser == null || loggedUser.getAccountType().equals(AccountType.ADMINISTRATOR)
				|| (loggedUser.getAccountType().equals(AccountType.CUSTOMER)
						&& !loggedUser.getUsername().equals(username))) {
			ctx.status(403);
			log.trace("App is leaving changeOrderStatus.");
			return;
		}

		// Get the order status and order to change
		OrderStatus orderStatus = ctx.bodyAsClass(Order.class).getStatus();
		Order order = loggedUser.getUsername().equals(username)
				
				//The user is logged in so the order will come from there
				? loggedUser.getOrders().stream()
				.filter((o) -> o.getId() == orderId)
				.findFirst()
				.orElse(null)
				
				//A manager is trying to change another user so the order will come from there
				: userService.getUser(username).getOrders().stream()
				.filter((o) -> o.getId() == orderId)
				.findFirst()
				.orElse(null);
		log.debug("Order is: "+ order);
		
		AccountType type = loggedUser.getAccountType();
		
		// If the order or status is null
		if (order == null || orderStatus == null) {
			ctx.status(404);
			ctx.html("No order or order status to change.");
			log.trace("App is leaving changeOrderStatus.");
			return;
		}
		
		// If the user is a customer, the user can only change ordered orders to
		// cancelled.
		if (type.equals(AccountType.CUSTOMER) && orderStatus.equals(OrderStatus.CANCELLED)
				&& order.getStatus().equals(OrderStatus.ORDERED)) {
			userService.changeOrderStatus(order, orderStatus);
			ctx.json(order);
			log.trace("App is leaving changeOrderStatus.");
			return;
		}
		
		//If the user is a manager, and is 
		//trying to cancel an ordered order 
		//or refund a shipped order
		if (type.equals(AccountType.MANAGER) && ((orderStatus.equals(OrderStatus.CANCELLED)
				&& order.getStatus().equals(OrderStatus.ORDERED)) 
				|| (orderStatus.equals(OrderStatus.REFUNDED)
				&& order.getStatus().equals(OrderStatus.SHIPPED)))) {
			userService.changeOrderStatus(order, orderStatus);
			ctx.json(order);
			log.trace("App is leaving changeOrderStatus.");
			return;
		}
		
		//Left the order without making the change
		ctx.status(403);
		log.trace("App is leaving changeOrderStatus.");

	}
}
