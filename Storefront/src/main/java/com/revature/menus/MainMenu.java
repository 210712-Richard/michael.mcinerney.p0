package com.revature.menus;

import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.models.AccountType;
import com.revature.models.User;
import com.revature.services.UserService;
import com.revature.util.SingletonScanner;

public class MainMenu {

	private static final Logger log = LogManager.getLogger(MainMenu.class); // Used to create log

	private static Scanner scanner = SingletonScanner.getInstance().getScan(); // Used to get user input for menus

	private static UserService us = new UserService(); // Used to modify user states

	private static User activeUser = null; // The current logged in user

	/**
	 * The first method. Opens the main menu.
	 */
	public static void start() {
		log.trace("Starting Storefront. start()");
		openMainMenu();

	}

	/**
	 * The Main Menu of the application
	 */
	private static void openMainMenu() {
		main: while (true) {
			System.out.println("Welcome to the Storefront!");
			System.out.println("Please Select an Option:");
			System.out.println("\t1. Login");
			System.out.println("\t2. Register");
			System.out.println("\t3. Close");

			log.trace("User selecting input for openMainMenu");
			switch (getUserInput()) {
			case 1:
				// Login
				// Have the user input their username and password
				System.out.println("Please enter your username:");
				String username = scanner.nextLine();
				log.debug("User entered username " + username);
				System.out.println("Please enter your password");
				String password = scanner.nextLine();
				log.debug("User entered password " + password);

				System.out.println("Logging you in...");
				log.trace("Going into UserService login");
				activeUser = us.login(username, password); // Will check if the user exists.

				if (activeUser == null) { // If the user was not found.
					log.trace("User was not found. Username: " + username);
					System.out.println("Login details did not match, please try again.\n");
				} else if (activeUser.isActive() == false) { // If the user has been deactivated.
					log.trace("User attempted to login with deactivated account. Username: " + username);
					System.out.println("Your account has been deactivated. Please contact an administrator.\n");
					break main;
				} else if (activeUser.getAccountType() == AccountType.CUSTOMER) { // User is a customer
					log.trace("User is a customer. Username " + username);
					openCustomerMenu();
				} else if (activeUser.getAccountType() == AccountType.MANAGER) { // User is a manager
					log.trace("User is a manager. Username " + username);
					openManagerMenu();
				} else if (activeUser.getAccountType() == AccountType.ADMINISTRATOR) { // User is a admin
					log.trace("User is an administrator. Username " + username);
					openAdminMenu();
				} else { // Somehow, the user is none of those things.
					log.error("Problem with the user account");
					System.out.println("There is a problem with your account. Please contact an administrator.");
				}
				break;
			case 2:
				// Register for a customer account
				activeUser = createAccount(AccountType.CUSTOMER);
				if (activeUser == null) {
					System.out.println("There was a problem setting up your account. Please try again.\n");
				} else {
					openCustomerMenu();
				}
				break;
			case 3:
				// Close
				log.trace("User is closing the application.");
				System.out.println("Have a nice day!");
				System.out.println("Closing Storefront...");
				break main;
			default:
				// Error
				log.trace("User tried to enter an invalid input.");
				System.out.println("Invalid Input. Please try again.\n");
				break;
			}
		}
		log.trace("Closing application");
	}

	/**
	 * The Main Customer Menu
	 */
	private static void openCustomerMenu() {
		log.trace(activeUser.getUsername() + " is in the customer menu.");
		System.out.println("Hello " + activeUser.getUsername() + "! What would you like to do?");
		customerLoop: while (true) {
			System.out.println("\t1. Search for Items");
			System.out.println("\t2. Go to Cart");
			System.out.println("\t3. Edit Account Settings");
			System.out.println("\t4. Logout");

			log.trace("User selecting input for openCustomerMenu");
			switch (getUserInput()) {
			case 1:
				// TODO: Search for items.
				break;
			case 2:
				// TODO: View, edit, and checkout cart
				break;
			case 3:
				// TODO: Add ability to edit password and email
				openCustomerSettings();
				log.trace(
						((activeUser == null) ? "User" : activeUser.getUsername()) + " is back in openCustomerMenu().");
				if (activeUser == null) {
					log.trace("Customer has successfully deactivated account.");
					break;
				}
				break;
			case 4:
				// Logout
				log.trace(activeUser.getUsername() + " is logging out.");
				System.out.println("Logging you out...\n");
				activeUser = null;
				log.debug("activeUser is null: " + activeUser == null);
				break customerLoop;
			default:
				// Input error
				log.trace(activeUser.getUsername() + " tried to enter an invalid input.");
				System.out.println("Please enter a valid option:");
				break;

			}
		}
		log.trace(((activeUser == null) ? "User" : activeUser.getUsername()) + " is exiting openCustomerMenu.");
	}

	/**
	 * The Main Manager menu
	 */
	private static void openManagerMenu() {
		log.trace(activeUser.getUsername() + " is in openManagerMenu()");
		System.out.println("Hello " + activeUser.getUsername() + "! What would you like to do?");
		managerLoop: while (true) {
			System.out.println("\t1. Edit Inventory");
			System.out.println("\t2. Deactivate Customer Account");
			System.out.println("\t3. Refund Order");
			System.out.println("\t4. Edit password");
			System.out.println("\t5. Logout");

			log.trace(activeUser.getUsername() + " is entering getUserInput from openManagerMenu");
			switch (getUserInput()) {
			case 1:
				// TODO: Edit Inventory
				break;
			case 2:
				// Deactivate Customer Account
				changeActiveStatusMenu(false, AccountType.CUSTOMER, false);
				log.trace(activeUser.getUsername() + " is back in openManagerMenu().");

				break managerLoop;
			case 3:
				// TODO: Refund an order
				break;
			case 4:
				// Edit Password for Managers
				changePassword();
				log.trace(activeUser.getUsername() + " is back in openManagerMenu().");

				break;
			case 5:
				// Logout
				log.trace(activeUser.getUsername() + " is logging out.");
				System.out.println("Logging you out...");
				activeUser = null;
				log.debug("activeUser is null: " + activeUser == null);
				break managerLoop;
			default:
				// Input error
				log.trace(activeUser.getUsername() + " tried to enter an invalid input.");
				System.out.println("Please enter a valid option:");
				break;
			}
		}
		log.trace("User is exiting openManagerMenu");
	}

	/**
	 * The Main Admin menu
	 */
	private static void openAdminMenu() {
		log.trace(activeUser.getUsername() + " is in openAdminMenu().");
		System.out.println("Hello " + activeUser.getUsername() + "! What would you like to do?");
		adminLoop: while (true) {
			System.out.println("\t1. Create Manager Account");
			System.out.println("\t2. Create Admin Account");
			System.out.println("\t3. Reactivate an Account");
			System.out.println("\t4. Deactivate an Account");
			System.out.println("\t5. Edit Password");
			System.out.println("\t6. Logout");

			log.trace(activeUser.getUsername() + " is going into getUserInput from openAdminMenu.");
			switch (getUserInput()) {
			case 1:
				// Create manager accounts
				User newManager = createAccount(AccountType.MANAGER);
				log.trace(activeUser.getUsername() + " is back in openAdminMenu().");
				if (newManager == null) {
					log.warn(activeUser.getUsername() + " had a problem creating the manager account.");
					System.out.println("Error creating account. Please try again.\n");
				} else {
					log.trace(activeUser.getUsername() + " succesfully made Manager account with username "
							+ newManager.getUsername());
					System.out.println("Account Successfully Created! Please give login information to the manager.");
				}
				break;
			case 2:
				// Create admin accounts
				log.trace(activeUser.getUsername() + " is going to createAccount from openAdminMenu.");
				User newAdmin = createAccount(AccountType.ADMINISTRATOR);
				log.trace(activeUser.getUsername() + " is back in openAdminMenu().");

				if (newAdmin == null) {
					log.warn(activeUser.getUsername() + " had a problem creating the admin account.");
					System.out.println("Error creating account. Please try again.\n");
				} else {
					log.trace(activeUser.getUsername() + " succesfully made Administrator account with username "
							+ newAdmin.getUsername());
					System.out.println(
							"Account Successfully Created! Please give login information to the administrator.");
				}
				break;
			case 3:
				// Reactivate an account
				adminAccountStatusMenu(true);
				log.trace(activeUser.getUsername() + " is back in openAdminMenu().");
				break;
			case 4:
				// Edit if an account is active or not
				adminAccountStatusMenu(false);
				log.trace(activeUser.getUsername() + " is back in openAdminMenu().");
				break;
			case 5:
				// Edit password
				changePassword();
				log.trace(activeUser.getUsername() + " is back in openAdminMenu().");
				break;
			case 6:
				// Logout
				log.trace(activeUser.getUsername() + " is logging out.");
				System.out.println("Logging you out...");
				activeUser = null;
				log.debug("activeUser is null: " + activeUser == null);
				break adminLoop;
			default:
				// Input error
				log.trace(activeUser.getUsername() + " entered an invalid input.");
				System.out.println("Please enter a valid option:");
				break;
			}
		}
		log.trace("User is exiting openManagerMenu");

	}

	/**
	 * The Customer Settings menu
	 */
	private static void openCustomerSettings() {
		log.trace(activeUser.getUsername() + " is in openCustomerSettings()");

		customerSettingsLoop: while (true) {
			System.out.println("Settings Page. Please select an option:");
			System.out.println("\t1. Change email");
			System.out.println("\t2. Change password");
			System.out.println("\t3. Deactivate Account");
			System.out.println("\t4. Go Back");

			log.trace(activeUser.getUsername() + " is going into getUserInput from openCustomerSettings.");
			switch (getUserInput()) {
			case 1:
				changeEmail();
				log.trace(activeUser.getUsername() + " is back in openCustomerSettings().");
				break;
			case 2:
				changePassword();
				log.trace(activeUser.getUsername() + " is back in openCustomerSettings().");

				break;
			case 3:
				// Deactivate account
				changeActiveStatusMenu(true, AccountType.CUSTOMER, true);
				log.debug("activeUser is null: " + activeUser == null);
				if (activeUser == null) {
					break customerSettingsLoop;
				}
				break;
			case 4:
				// Go Back
				break customerSettingsLoop;
			default:
				// Input error
				log.trace(activeUser.getUsername() + " entered an invalid input.");
				System.out.println("Please enter a valid option:");
				break;
			}

		}
		log.trace(activeUser.getUsername() + " is exiting openCustomerSettings.");
	}

	/**
	 * Uses the scanner to get the user inputs for the menu
	 * 
	 * @return Integer value of the user's input
	 */
	private static int getUserInput() {
		int userInput;
		try {
			userInput = Integer.parseInt(scanner.nextLine());
		} catch (Exception e) {
			log.warn("User inputted an invalid value.");
			userInput = -1;
		}
		System.out.println(); // Puts in a linebreak.
		log.trace(((activeUser == null) ? "User" : activeUser.getUsername()) + " is exiting getUserInput.");
		log.debug(((activeUser == null) ? "User" : activeUser.getUsername()) + " inputted " + userInput);
		return userInput;

	}

	/**
	 * Used to create user accounts. Users can create customer accounts. Admins can
	 * create manager and admin accounts.
	 * 
	 * @param type The type of account to create
	 * @return The brand new user.
	 */
	private static User createAccount(AccountType type) {
		log.trace(((activeUser == null) ? "User" : activeUser.getUsername()) + " is entering createAccount.");
		log.debug("In createAccount with parameters type = " + type);
		String newUsername = "";
		do {
			System.out.println("Please enter a username:");
			newUsername = scanner.nextLine();
			log.debug(((activeUser == null) ? "User" : activeUser.getUsername()) + " entered newUsername: "
					+ newUsername);
			// Checks to see if the username has been taken or not.
			// False means that it is in use already, true otherwise.
			if (!us.isUsernameUnique(newUsername)) {
				System.out.println(newUsername + " is already is use. Please try again.\n");
			}
		} while (!us.isUsernameUnique(newUsername));

		System.out.println("Please enter a password:");
		String newPassword = scanner.nextLine(); // User enters new password
		log.debug(((activeUser == null) ? "User" : activeUser.getUsername()) + " entered newPassword: " + newPassword);
		System.out.println("Please enter an email address:");
		String newEmail = scanner.nextLine(); // User enters new email
		log.debug(((activeUser == null) ? "User" : activeUser.getUsername()) + " entered newEmail: " + newEmail);

		System.out.println("Registering your account...");

		// This will create the new customer account.

		log.trace(((activeUser == null) ? "User" : activeUser.getUsername()) + " is leaving createAccount.");
		User u = us.register(newUsername, newPassword, newEmail, type);
		log.trace(((activeUser == null) ? "User" : activeUser.getUsername()) + " is exiting createAccount.");
		log.debug("Returning User: " + u);
		return u;

	}

	/**
	 * Allows customers to change their account email address
	 */
	private static void changeEmail() {
		log.trace(((activeUser == null) ? "User" : activeUser.getUsername()) + " is entering changeEmail.");
		System.out.println(
				"Your current email address is " + activeUser.getEmail() + ". Please enter a new email address: ");
		String newEmail = scanner.nextLine(); // User enters new email
		log.debug(activeUser.getUsername() + " entered newEmail: " + newEmail);

		System.out.println("Saving email address...");
		activeUser.setEmail(newEmail); // Set the email
		activeUser = us.changeUserDetails(activeUser); // Save the data to the file
		System.out.println("Email address saved.");
		log.debug(activeUser.getUsername() + " has changed their email address to newEmail " + newEmail + ": "
				+ activeUser.getEmail() == newEmail);

		log.trace(activeUser.getUsername() + " is now leaving changeEmail.");
	}

	/**
	 * Allows all registered users to change their passwords.
	 */
	private static void changePassword() {
		log.trace(activeUser.getUsername() + " is in changePassword");
		String currentPassword = "";
		do {
			System.out.println("Please enter current password:");
			currentPassword = scanner.nextLine(); // User confirms their current password
			log.debug(activeUser + " entered currentPassword: " + currentPassword
					+ ". Same as actual current password: " + currentPassword == activeUser.getPassword());
			if (!activeUser.getPassword().equals(currentPassword)) {
				System.out.println("That password was incorrect. Please try again.\n");
			}
		} while (!activeUser.getPassword().equals(currentPassword));

		// Objects to save new password and confirmation of new password
		String newPassword = "";
		String confirmPassword = " ";
		do { // Loop until the newPassword and confirmPassword are not blank and are the
				// same.
			System.out.println("Please enter your new password:");
			newPassword = scanner.nextLine(); // Enter new password.
			log.debug(activeUser.getUsername() + " entered newPassword: " + newPassword);
			System.out.println("Please reenter to confirm your password:");
			confirmPassword = scanner.nextLine(); // Confirm password
			log.debug(activeUser.getUsername() + " entered confirmPassword: " + confirmPassword);
			log.debug(activeUser.getUsername() + " entered the same password for both fields: "
					+ currentPassword == newPassword);

			if (!newPassword.equals(confirmPassword) && !newPassword.isBlank()) { // If the password fields don't match
																					// or if they are blank (only
																					// whitespace)
				System.out.println("Those passwords did not match. Please try again.\n");
			}
		} while (!newPassword.equals(confirmPassword));

		System.out.println("Saving password...");
		activeUser.setPassword(newPassword);
		us.changeUserDetails(activeUser);
		log.trace(activeUser.getUsername() + " is back in changePassword.");
		System.out.println("Password Saved.");
		log.trace(activeUser.getUsername() + " is exiting changePassword.");
	}

	/**
	 * Allows customers to deactivate their own accounts Allows managers to
	 * deactivate customer accounts Allows admins to reactivate or deactivate
	 * customer and manager accounts.
	 * 
	 * @param editingSelf If the user is editing themselves.
	 * @param type        The type of account that the user wants to edit.
	 * @param status      Used by admins to determine if the user is activating or
	 *                    deactivating an account.
	 */
	private static void changeActiveStatusMenu(boolean editingSelf, AccountType type, boolean status) {
		log.trace(activeUser.getUsername() + " is now in changeActiveStatusMenu.");
		log.debug("changeActiveStatusMenu with Parameters: editingSelf: " + editingSelf + ", type: " + type
				+ ", status: " + status);
		if (editingSelf) {
			// Menu when customer wants to deactivate their account.
			System.out.println(
					"\nAre you sure you want to deactivate your account? This option cannot be undone: [Y]es/[n]o");
			String affirm = scanner.nextLine();
			log.debug(activeUser.getUsername() + " entered affirm: " + affirm);
			switch (affirm) {
			case "Y": // User has to enter "Y"
				System.out.println("Deactivating account...");
				activeUser.setActive(false);
				activeUser = us.changeUserDetails(activeUser); // Saves data into the file.
				log.trace(activeUser.getUsername() + " back in changeActiveStatusMenu.");
				log.debug(activeUser.getUsername() + " has active status of " + activeUser.isActive());
				activeUser = null; // Logs the user out now that they're inactive.
				log.debug("activeUser is null: " + activeUser == null);
				System.out.println(
						"Your account has been deactivated. If you want to reactivate it, please contact an administrator.\n");
			default: // Any other input will have the user leave the menu.
				break;
			}
		} else {
			String activateString = status ? "reactivate" : "deactivate"; // Used for the console
			log.debug("activateString was set to " + activateString + " from status = " + status);
			// If the user is the correcet account type to modify and is modifying the
			// correct account type
			// Managers can only deactivate customers
			// Admins can reactivate and deactivate customers and managers
			// Customers cannot edit any accounts except themselves
			if ((type == AccountType.CUSTOMER && (activeUser.getAccountType() == AccountType.MANAGER
					|| activeUser.getAccountType() == AccountType.ADMINISTRATOR))
					|| type != AccountType.ADMINISTRATOR
							&& (activeUser.getAccountType() == AccountType.ADMINISTRATOR)) {

				List<User> userList = null; // The list of users with the correct type and right search terms
				do { // Go in once and loops as long as the userList is null
					System.out.println(
							"Please enter a username to search with. If you want to see all users, just press the 'Enter' key.");
					System.out.println("To Quit, type 'quit':");
					String searchString = scanner.nextLine(); // Enter a username to search or 'quit' to quit
					log.debug(activeUser.getUsername() + " entered searchString: " + searchString);
					if (searchString.equals("quit")) { // If 'quit' was entered, leave the application.
						log.trace(((activeUser == null) ? "User" : activeUser.getUsername())
								+ " is exiting changeActiveStatusMenu.");
						return;
					}
					System.out.println("Searching for users...");
					userList = us.searchUserByName(searchString, type, !status); // Search for the user using a basic
																					// .contains search
					log.trace(activeUser.getUsername() + " is back in changeActiveStatusMenu.");
					if (userList == null) { // If that search term found no users, user has to try again.
						System.out.println("No accounts were found with that name. Please try again.\n");
					}
				} while (userList == null);

				boolean validOptionSelected = false; // Used to keep track if a valid input was entered on the next
														// menu.
				User selectedUser = null; // Used to keep track of of what user is selected.
				do { // Enters once, then loops until the user picks a valid selection
					System.out.println("Please select a username from the list or quit:");
					for (int i = 0; i < userList.size(); i++) {
						if (userList.get(i).getUsername() != activeUser.getUsername()) {
							System.out.println("\t" + Integer.toString(i + 1) + ". " + userList.get(i).getUsername());
						}
						System.out.println("\n\t" + Integer.toString(userList.size() + 1) + ". Quit");
					}
					int option = getUserInput() - 1; // This will get the input from the user and subtract it by one to
														// make it usable for the array.
					log.trace(activeUser.getUsername() + " is back in changeActiveStatusMenu.");
					log.debug("Selection modified to option: " + option);
					if (option >= 0 && option < userList.size()) { // The option is in the correct range.
						validOptionSelected = true; // This breaks the loop.
						selectedUser = userList.get(option); // Get the selected user to modify their status.
					} else if (option == userList.size()) { // This will get the last option, which will always be quit.
						return;
					} else { // They did not enter an option in the correct range.
						System.out.println("Please select a valid option.\n");
					}
					log.debug("validOptionSelected set to: " + validOptionSelected);
				} while (!validOptionSelected);

				System.out.println("Are you sure you want to " + activateString + " " + selectedUser.getUsername()
						+ "? [Y]es/[n]o");
				String affirm = scanner.nextLine(); // Get the users affirmative status
				log.debug(activeUser.getUsername() + " entered affirm: " + affirm);
				switch (affirm) {
				case "Y": // User entered "Y"
					System.out.println("Trying to " + activateString + " this account...");
					selectedUser.setActive(status); // Change the status to the new status.

					selectedUser = us.changeUserDetails(selectedUser); // Save the details to the file and return the
																		// user object.
					log.trace(activeUser.getUsername() + " is back in changeActiveStatusMenu.");
					log.debug("selectedUser now set to new status: " + selectedUser.isActive());
					System.out.println("Account " + activateString + "d.");
				default: // Any input that isn't "Y" will leave the menu
					break;

				}
			} else {
				System.out.println("You are not authorized to deactivate these kinds of accounts.");
			}
		}
		log.trace(((activeUser == null) ? "User" : activeUser.getUsername()) + " is exiting changeActiveStatusMenu.");
	}

	/**
	 * The Admin's Account Status Management menu
	 * @param isReactivating If the admin is reactivating or deactivating accounts. True means reactivate, false means deactivate.
	 */
	private static void adminAccountStatusMenu(boolean isReactivating) {
		log.trace(activeUser.getUsername() + " is entering adminAccountStatusMenu.");
		log.debug("In adminAccountStatusMenu with parameters isReactivating = " + isReactivating);
		adminAccountLoop: while (true) {
			System.out.println(
					"\nWhich type of account do you want to " + (isReactivating ? "reactivate" : "deactivate") + "?");
			System.out.println("\t1. Customer");
			System.out.println("\t2. Manager");
			System.out.println("\t3. Back");
			log.trace(activeUser.getUsername() + " is now entering getUserInput() from adminAccountStatusMenu()");
			switch (getUserInput()) {
			case 1:
				// User editing customer account
				changeActiveStatusMenu(false, AccountType.CUSTOMER, isReactivating);
				log.trace(activeUser.getUsername() + " is back in adminAccountStatusMenu.");
				break adminAccountLoop;
			case 2:
				// User editing manager account
				changeActiveStatusMenu(false, AccountType.MANAGER, isReactivating);
				log.trace(activeUser.getUsername() + " is back in adminAccountStatusMenu.");
				break adminAccountLoop;
			case 3: // Going back
				break adminAccountLoop;
			default:
				// Input error
				log.trace(activeUser.getUsername() + " entered an invalid input.");
				System.out.println("Please enter a valid option:");
				break;
			}
		}
		log.trace(activeUser.getUsername() + " is now leaving adminAccountStatusMenu.");
	}
}
