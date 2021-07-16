package com.revature.services;

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

		Mockito.verify(dao).checkUsername("Test"); //Verifies that the checkUsername function is called from UserDAO.
		Mockito.verify(dao).writeToFile(); //Verifies that the writeToFile function is called from the UserDAO.
	}
	
	@Test
	public void testLogin() {
		User loggedIn = service.login("DefaultUser", "DefaultPassword"); //This is the starting user for Storefront.
		
	}

	//Function to set the UserDAO dao to a Mockito mock to verify the functions are being called.
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
