package com.revature.services;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.revature.beans.AccountType;
import com.revature.beans.CartItem;
import com.revature.beans.Order;
import com.revature.beans.OrderStatus;
import com.revature.beans.User;
import com.revature.data.UserDAO;
import com.revature.util.MockitoHelper;

public class UserServiceTest {
	private UserService service = null;
	private User user = null;
	private UserDAO dao = null;

	private static MockitoHelper<UserDAO> mockHelper; // Sets up a Mock UserDAO for UserService for method verification.

	@BeforeAll
	public static void beforeStart() {
		mockHelper = new MockitoHelper<UserDAO>(UserDAO.class);
	}

	@BeforeEach
	public void beforeTests() {
		service = new UserService();

		user = new User();
		user.setId(999);
		user.setUsername("Test");
		user.setPassword("password");
		user.setEmail("test@test.com");
		user.setAccountType(AccountType.CUSTOMER);
		user.setActive(true);
	}

	@Test
	public void testIsUsernameUniqueReturns() {
		// Checks if it returns true and false for unique usernames.
		assertTrue(service.isUsernameUnique("Test"), "Assert that does return true for new usernames.");
		assertFalse(service.isUsernameUnique("DefaultUser"), "Assert that does return false for registered usernames");
		assertFalse(service.isUsernameUnique(""), "Assert that empty usernames return false");
		assertFalse(service.isUsernameUnique("     "), "Assert that blank usernames return false.");
		assertFalse(service.isUsernameUnique(null), "Assert that null usernames return false.");
	}

	@Test
	public void testIsUsernameUniqueCallsMethods() {
		dao = mockHelper.setPrivateMock(service, "ud");
		String username = "Test";
		service.isUsernameUnique(username);

		Mockito.verify(dao).getUsers(); // Verifies that the getUsers function is called from
										// UserDAO.
		Mockito.verify(dao).writeToFile(); // Verifies that the writeToFile function is called from the UserDAO.

	}

	@Test
	public void testLoginReturnsValidUser() {
		String username = "DefaultUser";
		String password = "DefaultPassword";
		User loggedInTrue = service.login("DefaultUser", "DefaultPassword"); // This is the starting user for
																				// Storefront.
		// ArgumentCaptor<String> usernameCaptor =
		// ArgumentCaptor.forClass(String.class);
		// ArgumentCaptor<String> passwordCaptor =
		// ArgumentCaptor.forClass(String.class);

		assertEquals(username, loggedInTrue.getUsername(),
				"Assert that the username given is the same as the logged in user.");
		assertEquals(password, loggedInTrue.getPassword(),
				"Assert that the password given is the same as the logged in user.");

	}

	@Test
	public void testLoginCallsMethods() {
		dao = mockHelper.setPrivateMock(service, "ud");
		String username = "DefaultUser";
		String password = "DefaultPassword";

		service.login(username, password);

		ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);

		Mockito.verify(dao).getUser(usernameCaptor.capture(), passwordCaptor.capture());
		Mockito.verify(dao).writeToFile(); // Verify that writeToFile is called.

		assertEquals(username, usernameCaptor.getValue(), "Assert that method called with username given.");
		assertEquals(password, passwordCaptor.getValue(), "Assert that method called with password given.");
	}

	@Test
	public void testLoginReturnsInvalidUser() {
		User loggedInFalse = service.login("FakeUser", "FakePass");
		assertEquals(loggedInFalse, null,
				"Assert that a bad username and bad password results in a null user being returned.");
		User loggedInBadUser = service.login("FakeUser", "DefaultPassword");
		assertEquals(loggedInBadUser, null, "Assert that a bad username results in a null user being returned.");
		User loggedInBadPass = service.login("DefaultUser", "FakePass");
		assertEquals(loggedInBadPass, null, "Assert that a bad password results in a null user being returned.");

		User loggedInNull = service.login(null, null);
		assertEquals(loggedInNull, null,
				"Assert that a null username and null password results in a null user being returned.");
		User loggedInNullPass = service.login("DefaultUser", null);
		assertEquals(loggedInNullPass, null, "Assert that a null password results in a null user being returned.");
		User loggedInNullUser = service.login(null, "DefaultPassword");
		assertEquals(loggedInNullUser, null, "Assert that a null username results in a null user being returned.");
	}

	@Test
	public void testChangeUserDetails() {
		dao = mockHelper.setPrivateMock(service, "ud");
		User savedUser = service.updateUser(user);
		User nullUser = service.updateUser(null);

		Mockito.verify(dao).writeToFile(); // Verifies that writeToFile is called.

		assertTrue(user.equals(savedUser), "Assert that User passed in is the same User.");
		assertNull("Assert that null User returns null.", nullUser);
	}

	@Test
	public void testRegisterReturnsValidUser() {
		// Implement tests for registering valid users
		String username = "regiserTest";
		String password = "registerTestPassword";
		String email = "register@test.com";
		AccountType type = AccountType.CUSTOMER;

		User newUser = service.register(username, password, email, type);

		assertEquals(username, newUser.getUsername(),
				"Assert that the username given is the username of the new User.");
		assertEquals(password, newUser.getPassword(),
				"Assert that the password given is the password of the new User.");
		assertEquals(email, newUser.getEmail(), "Assert that the email given is the email of the new User.");
		assertEquals(type, newUser.getAccountType(),
				"Assert that the AccountType given is the AccountType of the new User.");

		// These are set for the new user and should the same for all new users.
		assertTrue(newUser.isActive(), "Assert that the new user isActive is set to true.");
		assertTrue(newUser.getPastOrders().isEmpty(),
				"Assert that the new user's past orders list is initialized and empty.");
		assertTrue(newUser.getCart().isEmpty(), "Assert that the new user's cart is initialized and empty.");

	}

	@Test
	public void testRegisterReturnsInvalidUser() {
		// Implement tests for registering invalid users
		String username = "username";
		String password = "password";
		String email = "email";
		AccountType type = AccountType.CUSTOMER;

		// Null Username
		User nullUsername = service.register(null, password, email, type);
		assertNull("Assert that a null username results in a null user.", nullUsername);

		// Blank Username
		User blankUsername = service.register("   ", password, email, type);
		assertNull("Assert that a blank username results in a null user.", blankUsername);

		// Null Password
		User nullPassword = service.register(username, null, email, type);
		assertNull("Assert that a null password results in a null user.", nullPassword);

		// Blank Password
		User blankPassword = service.register(username, "      ", email, type);
		assertNull("Assert that a blank password results in a null user.", blankPassword);

		// Null Email
		User nullEmail = service.register(username, password, null, type);
		assertNull("Assert that a null email results in a null user.", nullEmail);

		// Blank Email
		User blankEmail = service.register(username, password, "", type);
		assertNull("Assert that a blank email results in a null user.", blankEmail);

		// Null AccountType
		User nullType = service.register(username, password, email, null);
		assertNull("Assert that a null AccountType results in a null user.", nullType);
	}

	@Test
	public void testRegisterReturnsCallsMethods() {
		// Tests making sure method calls UserDAO methods
		dao = mockHelper.setPrivateMock(service, "ud");

		service.register(user.getUsername(), user.getPassword(), user.getEmail(), user.getAccountType());

		// Set the captors to make sure the methods are called.
		ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<AccountType> typeCaptor = ArgumentCaptor.forClass(AccountType.class);

		// Verify the methods are called
		Mockito.verify(dao).createUser(usernameCaptor.capture(), passwordCaptor.capture(), emailCaptor.capture(),
				typeCaptor.capture());
		Mockito.verify(dao).writeToFile(); // Verify that writeToFile is called.

		// Verify it is the same values
		assertEquals(user.getUsername(), usernameCaptor.getValue(),
				"Assert that createUser called with username given.");
		assertEquals(user.getPassword(), passwordCaptor.getValue(),
				"Assert that createUser called with password given.");
		assertEquals(user.getEmail(), emailCaptor.getValue(), "Assert that createUser called with email given.");
		assertEquals(user.getAccountType(), typeCaptor.getValue(),
				"Assert that createUser called with AccountType given.");

	}

	@Test
	public void testSearchUserByNameReturnsValidUserList() {
		// Tests for searching for valid usernames, types, and status
		List<User> userList = service.searchUserByName("DefaultUser", AccountType.CUSTOMER, true);

		assertFalse(userList.isEmpty(), "Assert that the returned user list is not empty.");

		// Different type than the user has
		List<User> wrongType = service.searchUserByName("DefaultUser", AccountType.MANAGER, true);
		assertNull("Assert that different AccountType returns a null.", wrongType);

		// Different status than the user has
		List<User> wrongStatus = service.searchUserByName("DefaultUser", AccountType.CUSTOMER, false);
		assertNull("Assert that different AccountType returns a null.", wrongStatus);
	}

	@Test
	public void testSearchUserByNameReturnsInvalidUserList() {
		// Tests for making sure invalid parameters returns null
		// Test for null username
		List<User> nullUsername = service.searchUserByName(null, user.getAccountType(), user.isActive());
		assertNull("Assert that null username returns null.", nullUsername);

		// Test for null AccountType
		List<User> nullType = service.searchUserByName(user.getUsername(), null, user.isActive());
		assertNull("Assert that null AccountType returns null.", nullType);

	}

	@Test
	public void testSearchUserByNameReturnsCallsFunctions() {
		// Tests making sure method calls UserDAO methods
		dao = mockHelper.setPrivateMock(service, "ud");

		service.searchUserByName(user.getUsername(), user.getAccountType(), user.isActive());

		// Set the captors to make sure the methods are called.
		ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<AccountType> typeCaptor = ArgumentCaptor.forClass(AccountType.class);
		ArgumentCaptor<Boolean> statusCaptor = ArgumentCaptor.forClass(Boolean.class);

		// Verify the methods are called
		Mockito.verify(dao).findUsersByName(usernameCaptor.capture(), typeCaptor.capture(), statusCaptor.capture());
		Mockito.verify(dao).writeToFile(); // Verify that writeToFile is called.

		// Verify it is the same values
		assertEquals(user.getUsername(), usernameCaptor.getValue(),
				"Assert that findUsersByName called with username given.");
		assertEquals(user.getAccountType(), typeCaptor.getValue(),
				"Assert that findUsersByName called with AccountType given.");
		assertEquals(user.isActive(), statusCaptor.getValue(),
				"Assert that findUsersByName called with AccountType given.");

	}

	@Test
	public void testAddToCart() {
		// Test if CartItem gets added to cart
		dao = mockHelper.setPrivateMock(service, "ud");

		int itemId = 0;
		double price = 20.0;
		int quantity = 5;
		CartItem cartItem = new CartItem(itemId, quantity, price);
		service.addToCart(user, itemId, quantity, price);

		assertTrue(user.getCart().contains(cartItem), "Assert that the Item and it's properties are in the cart.");
		// Use Mockito to make sure writeToFile is called
		Mockito.verify(dao).writeToFile();

		// Make sure the item is simply modified and the size of the cart does not
		// change
		int add = 10;
		cartItem.setQuantity(cartItem.getQuantity() + add);
		int size = user.getCart().size();
		service.addToCart(user, itemId, add, price);
		assertTrue(user.getCart().contains(cartItem), "Assert that the changes to the item were made and in the cart.");
		assertEquals(user.getCart().size(), size,
				"Assert that the size of the cart did not change when the same item was passed in.");

		// Make sure 0.0 price and 0 quantity don't add the item to cart
		CartItem zeroPrice = new CartItem(itemId, quantity, 0.0);
		service.addToCart(user, itemId, quantity, 0.0);
		assertFalse(user.getCart().contains(zeroPrice),
				"Assert that a negative or zero price does not add the item to the cart.");

		CartItem zeroQuantity = new CartItem(itemId, 0, price);
		service.addToCart(user, itemId, 0, price);
		assertFalse(user.getCart().contains(zeroQuantity),
				"Assert that a negative or quantity price does not add the item to the cart.");
	}

	@Test
	public void testCreateOrder() {
		dao = mockHelper.setPrivateMock(service, "ud");

		// Make sure the user passed in creates the order
		user.addToCart(0, 10, 20.0); // Makes sure the cart isn't empty
		service.createOrder(user);
		assertFalse(user.getPastOrders().isEmpty(), "Assert that the cart was added to the orders.");

		// Mockito verification for writeToFile
		Mockito.verify(dao).writeToFile();

		// Make sure empty cart does not create an order
		int size = user.getPastOrders().size();
		service.createOrder(user);
		assertEquals(size, user.getPastOrders().size(), "Assert that the empty cart did not add to the orders.");
	}

	@Test
	public void testChangeActiveStatus() {
		dao = mockHelper.setPrivateMock(service, "ud");

		// Make sure passed in user gets the passed in status
		boolean status = false;
		service.changeActiveStatus(user, status);
		assertEquals(status, user.isActive(), "Assert that the user's status was changed.");

		// Mockito verification for writeToFile
		Mockito.verify(dao).writeToFile();

		// If user is being deactivated and has a cart, cart should be emptied
		user.addToCart(0, 10, 20.0); // Makes sure the cart isn't empty
		service.changeActiveStatus(user, status);
		assertTrue(user.getCart().isEmpty(), "Assert that the cart was empty before User was deactivated.");

	}

	@Test
	public void testChangeQuantityInCart() {
		dao = mockHelper.setPrivateMock(service, "ud");
		// Make sure quantity passed in changes user's cart
		int oldQuantity = 15;
		int newQuantity = 20;
		int itemId = 0;
		double price = 10.0;

		user.addToCart(itemId, oldQuantity, price);
		CartItem item = user.getCart().get(0);
		service.changeQuantityInCart(item, newQuantity);
		assertEquals(item.getQuantity(), newQuantity, "Assert that the quantity changed to the new quantity.");
		// Mockito verification for writeToFile
		Mockito.verify(dao).writeToFile();

		// Zero or negative quantity doesn't do anything
		service.changeQuantityInCart(item, 0);
		assertEquals(item.getQuantity(), newQuantity, "Assert that the quantity didn't change to the new quantity.");

	}

	@Test
	public void testChangeOrderStatus() {
		dao = mockHelper.setPrivateMock(service, "ud");
		// status changes for user order
		OrderStatus status = OrderStatus.CANCELLED;
		int quantity = 15;
		int itemId = 0;
		double price = 10.0;

		user.addToCart(itemId, quantity, price);
		user.createOrder();

		Order order = user.getPastOrders().get(0);
		service.changeOrderStatus(order, status);
		assertEquals(status, order.getStatus(), "Assert that the order status was changed.");

		// Mockito verification for writeToFile
		Mockito.verify(dao).writeToFile();

		// /Null status changes nothing
		service.changeOrderStatus(order, null);
		assertEquals(status, order.getStatus(), "Assert that the order status was not changed to null.");


	}

	@Test
	public void testChangeCartItemPrice() {
		dao = mockHelper.setPrivateMock(service, "ud");
		// Make sure price passed in changes user's cart
		double oldPrice = 10.0;
		double newPrice = 30.0;
		int itemId = 0;
		int quantity = 1;

		user.addToCart(itemId, quantity, oldPrice);
		CartItem item = user.getCart().get(0);
		service.changeCartItemPrice(item, newPrice);
		assertEquals(item.getPrice(), newPrice, "Assert that the price changed to the new price.");
		// Mockito verification for writeToFile
		Mockito.verify(dao).writeToFile();

		// Zero or negative price doesn't do anything
		service.changeCartItemPrice(item, 0.0);
		assertEquals(item.getPrice(), newPrice, "Assert that the price didn't change to the new price.");

	}

}
