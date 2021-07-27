package com.revature.data;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.revature.beans.AccountType;
import com.revature.beans.Item;
import com.revature.beans.ItemCategory;
import com.revature.beans.User;

public class UserDAOTest {

	private UserDAO dao;
	private static User user;

	@BeforeAll
	public static void beforeStart() {
		user = new User(0, "DefaultUser", "DefaultPassword", "defaultUser@email.com", AccountType.CUSTOMER, true);

	}

	@BeforeEach
	public void beforeTests() {
		dao = new UserDAO();
		// The default user of the application.

	}

	@Test
	public void testGetUserReturnsValidUser() {
		// Returns a valid user given the same correct username and password
		// combination.
		User retUser = dao.getUser(user.getUsername(), user.getPassword());
		assertTrue(user.equals(retUser), "Assert that the user returned is the correct User.");
	}

	@Test
	public void testGetUserReturnsInvalidUser() {
		// Returns null if the wrong username or password is used.
		User invalidUser = dao.getUser("WrongUsername", user.getPassword());
		assertNull("Assert that the wrong username results in a null User.", invalidUser);
		invalidUser = dao.getUser(user.getUsername(), "WrongPassword");
		assertNull("Assert that the wrong password results in a null User.", invalidUser);

		// Returns null if either the username or password is null
		invalidUser = dao.getUser(null, user.getPassword());
		assertNull("Assert that a null username results in a null User.", invalidUser);
		invalidUser = dao.getUser(user.getUsername(), null);
		assertNull("Assert that a null password results in a null User.", invalidUser);

	}

	@Test
	public void testCreateUserReturnsValidUser() {
		// Returns a valid user that was added to the list
		String username = "NewTestUser";
		String password = "Password";
		String email = "newTest@email.com";
		AccountType type = AccountType.CUSTOMER;

		User newUser = dao.createUser(username, password, email, type);

		// The parameters are set.
		assertEquals(username, newUser.getUsername(), "Assert that the new User has the same username passed in.");
		assertEquals(password, newUser.getPassword(), "Assert that the new User has the same password passed in.");
		assertEquals(email, newUser.getEmail(), "Assert that the new User has the same password passed in.");
		assertEquals(type, newUser.getAccountType(), "Assert that the new User has the same type passed in.");

		// The default values are set.
		assertTrue(newUser.getCart().isEmpty(), "Assert that the new User has an empty cart initialized.");
		assertTrue(newUser.getOrders().isEmpty(), "Assert that the new User has an empty order list initialized.");
		assertTrue(newUser.isActive(), "Assert that the new User is active.");
	}

	@Test
	public void testCreateUserReturnsInvalidUser() {
		// Blank or null username results in null User
		assertNull("Assert that a blank username returns a null User.",
				dao.createUser("  ", user.getPassword(), user.getEmail(), user.getAccountType()));
		assertNull("Assert that a null username returns a null User.",
				dao.createUser(null, user.getPassword(), user.getEmail(), user.getAccountType()));

		// Blank or null password results in null User
		assertNull("Assert that a blank password returns a null User.",
				dao.createUser(user.getUsername(), "  ", user.getEmail(), user.getAccountType()));
		assertNull("Assert that a null password returns a null User.",
				dao.createUser(user.getUsername(), null, user.getEmail(), user.getAccountType()));

		// Blank or null email results in null User
		assertNull("Assert that a blank email returns a null User.",
				dao.createUser(user.getUsername(), user.getPassword(), "   ", user.getAccountType()));
		assertNull("Assert that a null email returns a null User.",
				dao.createUser(user.getUsername(), user.getPassword(), null, user.getAccountType()));

		// Blank or null AccountType results in null User
		assertNull("Assert that a null AccountType returns a null User.",
				dao.createUser(user.getUsername(), user.getPassword(), user.getEmail(), null));
	}

	@Test
	public void testFindUsersByNameReturnsValidList() {
		// Returns a non-empty list if the search string is in a User username with the
		// type and status.
		List<User> retList = dao.getUsersByName(user.getUsername(), user.getAccountType(), user.isActive());
		assertFalse(retList.isEmpty(), "Assert that users are returned even if part of username is passed in.");
		
		// Ensure empty string returns list of users
		retList = dao.getUsersByName("", user.getAccountType(), user.isActive());
		assertFalse(retList.isEmpty(), "Assert that users are returned even if blank username is passed.");
	}

	@Test
	public void testFindUsersByNameReturnsEmptyList() {
		// Returns empty if no search term matches
		List<User> retList = dao.getUsersByName("WrongUsernameDefault", user.getAccountType(), user.isActive());
		assertTrue(retList.isEmpty(), "Assert that if no username matches, return an empty list.");

		// Returns empty if null search term is used.
		retList = dao.getUsersByName(null, user.getAccountType(), user.isActive());
		assertTrue(retList.isEmpty(), "Assert that null search term returns an empty list.");

		// Returns empty if null or wrong AccountType is used
		retList = dao.getUsersByName(user.getUsername(), AccountType.MANAGER, user.isActive());
		assertTrue(retList.isEmpty(), "Assert that wrong AccountType returns empty list.");
		retList = dao.getUsersByName(user.getUsername(), null, user.isActive());
		assertTrue(retList.isEmpty(), "Assert that null AccountType returns empty list.");

		// Returns empty if wrong active status is used
		retList = dao.getUsersByName(user.getUsername(), AccountType.MANAGER, false);
		assertTrue(retList.isEmpty(), "Assert that wrong status returns empty list.");
	}
	
	@Test
	public void testGetUserByName() {
		
		User u = dao.getUserByName(user.getUsername());
		
		assertEquals(u.getUsername(), user.getUsername(), "Assert that the user returned has the same username given.");
		
		User wrongUser = dao.getUserByName("Random Name");
		
		assertNull("Assert that an incorrect username returns a null user.", wrongUser);
		
		User nullUser = dao.getUserByName(null);
		
		assertNull("Assert that a null username returns a null user.", nullUser);
		
		

	}
}
