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

import com.revature.data.UserDAO;
import com.revature.models.AccountType;
import com.revature.models.User;
import com.revature.util.MockitoHelper;

public class UserServiceTest {
	private UserService service = null;
	private User user = null;
	private UserDAO dao = null;
	
	private static MockitoHelper<UserDAO> mockHelper; //Sets up a Mock UserDAO for UserService for method verification.
	
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

		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(dao).checkUsername(captor.capture()); // Verifies that the checkUsername function is called from
																// UserDAO.
		Mockito.verify(dao).writeToFile(); // Verifies that the writeToFile function is called from the UserDAO.

		assertEquals(username, captor.getValue(), "Assert that username provided is used.");

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
		User savedUser = service.changeUserDetails(user);
		User nullUser = service.changeUserDetails(null);

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
	public void testRegisterReturnsCallsFunctions() {
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

}
