package com.revature.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;

import com.revature.data.UserDAO;
import com.revature.models.User;

public class UserServiceTest {
	private UserService service = null;
	private User user = null;
	private UserDAO dao = null;

	@BeforeEach
	public void beforeTests() {
		service = new UserService();

		user = new User();
		user.setId(999);
		user.setUsername("Test");
		user.setPassword("password");
		user.setEmail("test@test.com");

	}

	@Test
	public void testIsUsernameUniqueReturns() {
		// Checks if it returns true and false for unique usernames.
		assertTrue(service.isUsernameUnique("Test"), "Asserts that does return true for new usernames.");
		assertFalse(service.isUsernameUnique("DefaultUser"), "Asserts that does return false for registered usernames");
		assertFalse(service.isUsernameUnique(""), "Assert that empty usernames return false");
		assertFalse(service.isUsernameUnique("     "), "Assert that blank usernames return false.");
		assertFalse(service.isUsernameUnique(null), "Assert that null usernames return false.");
	}

	@Test
	public void testIsUsernameUniqueCallsFunctions() {
		setMockDAO();

		service.isUsernameUnique("Test");

		Mockito.verify(dao).checkUsername("Test"); // Verifies that the checkUsername function is called from UserDAO.
		Mockito.verify(dao).writeToFile(); // Verifies that the writeToFile function is called from the UserDAO.
	}

	@Test
	public void testLoginReturnsValidUser() {
		String username = "DefaultUser";
		String password = "DefaultPassword";
		User loggedInTrue = service.login("DefaultUser", "DefaultPassword"); // This is the starting user for
																				// Storefront.
		assertEquals(username, loggedInTrue.getUsername(),
				"Assert that the username given is the same as the logged in user.");
		assertEquals(password, loggedInTrue.getPassword(),
				"Assert that the password given is the same as the logged in user.");

	}

	@Test
	public void testLoginReturnsInvalidUser() {
		User loggedInFalse = service.login("FakeUser", "FakePass");
		assertEquals(loggedInFalse, null, "Assert that a bad username and bad password results in a null user being returned.");
		User loggedInBadUser = service.login("FakeUser", "DefaultPassword");
		assertEquals(loggedInBadUser, null, "Assert that a bad username results in a null user being returned.");
		User loggedInBadPass = service.login("DefaultUser", "FakePass");
		assertEquals(loggedInBadPass, null, "Assert that a bad password results in a null user being returned.");
		
		User loggedInNull = service.login(null, null);
		assertEquals(loggedInNull, null, "Assert that a null username and null password results in a null user being returned.");
		User loggedInNullPass = service.login("DefaultUser", null);
		assertEquals(loggedInNullPass, null, "Assert that a null password results in a null user being returned.");
		User loggedInNullUser = service.login(null, "DefaultPassword");
		assertEquals(loggedInNullUser, null, "Assert that a null username results in a null user being returned.");
	}

	// Function to set the UserDAO dao to a Mockito mock to verify the functions are
	// being called.
	private void setMockDAO() {
		// Using reflection to get the private dao.
		dao = Mockito.mock(UserDAO.class);
		try {
			FieldSetter.setField(service, service.getClass().getDeclaredField("ud"), dao);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
}
