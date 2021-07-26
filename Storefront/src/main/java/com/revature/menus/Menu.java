package com.revature.menus;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.chrono.IsoChronology;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.AccountType;
import com.revature.beans.CartItem;
import com.revature.beans.Item;
import com.revature.beans.ItemCategory;
import com.revature.beans.Order;
import com.revature.beans.OrderStatus;
import com.revature.beans.Sale;
import com.revature.beans.User;
import com.revature.services.ItemService;
import com.revature.services.UserService;
import com.revature.util.SingletonScanner;

public class Menu {

	private static final Logger log = LogManager.getLogger(Menu.class); // Used to create log

	private static Scanner scanner = SingletonScanner.getInstance().getScan(); // Used to get user input for menus

	private static UserService us = new UserService(); // Used to modify user states

	private static ItemService is = new ItemService(); //Used to modify inventory

	private static User activeUser = null; // The current logged in user
	
	private static DecimalFormat priceFormat = new DecimalFormat("#.##"); //Used to format prices correctly

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
			System.out.println("\t3. View Orders");
			System.out.println("\t4. Edit Account Settings");
			System.out.println("\t5. Logout");

			log.trace("User selecting input for openCustomerMenu");
			switch (getUserInput()) {
			case 1:
				// Search for items.
				Item item = searchItemMenu(true);
				log.trace(activeUser.getUsername() + " has returned to openCustomerMenu");
				if (item != null) { // User did find an item they want to add to their cart.

					while (true) {
						System.out.println("How many of " + item.getName() + " would you like?");
						System.out.println("Enter 0 if do not want to add this item.");
						final int quantity = getUserInput();
						// If the quantity is greater than zero and under or equal to the amount in
						// inventory
						if (quantity > 0 && quantity <= item.getAmountInInventory()) {
							// Reduce the inventory amount by the quantity.
							// If the item is on sale, get the sale price, otherwise, get the item price
							

							us.addToCart(activeUser, item, quantity);
							continue customerLoop;
						}
						// If the quantity is zero, it will go back to the menu
						else if (quantity == 0) {
							System.out.println("Item not added to cart.");
							continue customerLoop;
						} else { // Less than 0 or greater than the item's amount will loop again.
							log.debug("quantity was incorrect, so it was changed to " + quantity);
							System.out.println("Quantity was invalid. Please try again.\n");
						}
					}

				}
				break;
			case 2:
				// View, edit, and checkout cart

				// If the cart is empty

				System.out.println("Here is the cart.");
				customerCartMenu();
				log.trace(activeUser.getUsername() + " has returned to openCustomerMenu");
				break;
			case 3:
				// View and edit orders
				System.out.println("Here are you orders: ");
				orderEditMenu(activeUser.getOrders(), true);
				log.trace(activeUser.getUsername() + " has returned to openCustomerMenu");
				break;
			case 4:
				// Edit Account Details (email, password, or active status)
				openCustomerSettings();
				log.trace(
						((activeUser == null) ? "User" : activeUser.getUsername()) + " is back in openCustomerMenu().");
				if (activeUser == null) {
					log.info("Customer has successfully deactivated account.");
					break customerLoop;
				}
				break;
			case 5:
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

	/*
	 * The menu for a customer to edit their cart
	 */
	private static void customerCartMenu() {
		// Implement cart functionality
		log.trace(activeUser.getUsername() + " is now in customerCartMenu.");
		cartLoop: while (true) {
			if (activeUser.getCart().isEmpty()) {
				System.out.println("Your cart is empty!");
				break;
			}
			// Loop through the cart to show the user the contents.
			viewCartItemsFromList(activeUser.getCart(), false);
			// Checkout user prompt
			System.out.println("\t" + Integer.toString(activeUser.getCart().size() + 1) + ". Checkout");
			// Back user prompt
			System.out.println("\t" + Integer.toString(activeUser.getCart().size() + 2) + ". Back");

			// Get the input and subtract is by one and return it.
			int selection = getUserInput() - 1;
			System.out.println("Please enter if you would like to modify the quantity of an item");
			int cartSize = activeUser.getCart().size(); // The max size.
			log.debug("selection has turned getUserInput into: " + selection);
			log.debug("cartSize is " + cartSize);

			if (selection >= 0 && selection < cartSize) { // If the selection is in the index range of the cart.
				Item i = activeUser.getCart().get(selection).getItem();
				log.debug("Item selected is " + i);
				int maxQuantity = activeUser.getCart().get(selection).getQuantity() + i.getAmountInInventory();

				log.debug("maxQuantity is set to: " + maxQuantity);

				// Print out for the user.
				System.out.println("What much would you like of " + i.getName() + " in your cart?");
				System.out.println(
						"To remove item, enter 0. Max quantity is " + maxQuantity + ". Other inputs will go back.");

				// Get the input from the user
				int quantity = getUserInput();
				if (quantity == 0) { // If quantity is zero
					log.info(activeUser.getUsername() + " is removing " + i.getName());
					// Remove the item from the cart.
					us.removeFromCart(activeUser, selection);
					
					log.trace(activeUser.getUsername() + " is back in customerCartMenu");
					log.debug("items new amount is now " + i.getAmountInInventory());
					System.out.println(i.getName() + " removed.");
					
				//Changes the quantity of one of the items in the cart
				} else if (quantity > 0 && quantity <= maxQuantity) {
					// Set the quantity in the cart to the quantity entered
					us.changeQuantityInCart(activeUser.getCart().get(selection), quantity);
					
					// Change the amount in inventory to reflect the quantity in cart now
					is.changeAmount(i, maxQuantity - activeUser.getCart().get(selection).getQuantity());
					
					log.trace(activeUser.getUsername() + " is back in customerCartMenu");
					log.debug("Items in cart quantity set to " + activeUser.getCart().get(selection).getQuantity());
					System.out.println("Quantity changed.");
				}

			} else if (selection == cartSize) { // If the selection was set to checkout
				log.trace(activeUser.getUsername() + " is creating the order");
				System.out.println("Creating your order now...");
				us.createOrder(activeUser);
				System.out.println("The Order has been made!");
				break cartLoop;
			} else if (selection == cartSize + 1) { // If Back was selected.
				break cartLoop;
			} else {
				log.warn("Incorrect input was entered.");
				System.out.println("Invalid input. Please try again.");
			}

		}
		log.trace(activeUser.getUsername() + " is now exiting customerCartMenu.");

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
				changeEmailMenu();
				log.trace(activeUser.getUsername() + " is back in openCustomerSettings().");
				break;
			case 2:
				changePasswordMenu();
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
		log.trace("User is exiting openCustomerSettings.");
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
			System.out.println("\t3. Cancel or Refund Order");
			System.out.println("\t4. Edit password");
			System.out.println("\t5. Logout");

			log.trace(activeUser.getUsername() + " is entering getUserInput from openManagerMenu");
			switch (getUserInput()) {
			case 1:
				// Edit Inventory
				editInventoryMenu();
				log.trace(activeUser.getUsername() + " has returned to openManagerMenu.");
				break;
			case 2:
				// Deactivate Customer Account
				changeActiveStatusMenu(false, AccountType.CUSTOMER, false);
				log.trace(activeUser.getUsername() + " is back in openManagerMenu().");

				break;
			case 3:
				// Refund an order
				User user = searchUsernameMenu(AccountType.CUSTOMER, false); // Search for the user
				log.trace(activeUser.getUsername() + " is back in openManagerMenu.");
				if (user != null) { // The user selected a valid User
					orderEditMenu(user.getOrders(), false); // Find the order to refund
					log.trace(activeUser.getUsername() + " is back in openManagerMenu.");
				}

				break;
			case 4:
				// Edit Password for Managers
				changePasswordMenu();
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
	 * Opens a menu to add an item or edit an existing item
	 */
	private static void editInventoryMenu() {
		log.trace(activeUser.getUsername() + " has entered editInventoryMenu.");
		// Users selects if they want to add item or edit item quantity
		editLoop: while (true) {

			System.out.println("Edit Inventory Menu.");
			System.out.println("Please select if you want to add a new item or edit an existing item's quantity: ");
			System.out.println("\t1. Add Item");
			System.out.println("\t2. Add or Remove Sale");
			System.out.println("\t3. Edit Exisiting Item Quantity");
			System.out.println("\t4. Back");
			int selection = getUserInput();
			log.trace(activeUser.getUsername() + " has returned to editInventoryMenu");
			switch (selection) {
			case 1:
				// Add Item
				System.out.println("Add Item");
				// Have the user input the name, price, category, amount, and description.
				System.out.println("Please select the category of the item: ");

				// Enter the category
				ItemCategory newCategory = itemCategoryMenu();
				log.trace(activeUser.getUsername() + " has returned to editInventoryMenu.");
				log.debug("category selected: " + newCategory);
				if (newCategory == null) { // The user selected to go back
					break;
				}

				// Enter the name of the item
				System.out.println("Please enter the name of the item: ");
				String newName = scanner.nextLine();
				log.debug(activeUser.getUsername() + " has entered newName " + newName);

				// Enter the description of the item
				System.out.println("Please enter a description the item: ");
				String newDesc = scanner.nextLine();
				log.debug(activeUser.getUsername() + " has entered newName " + newDesc);

				// Enter the price of the item
				System.out.println("Please enter the price of the item: ");
				double newPrice = getUserDouble();
				log.trace(activeUser.getUsername() + " is back in editInventoryMenu");

				// Enter the quantity of the item
				System.out.println("Please enter the amount of the item in inventory: ");
				int newAmount = getUserInput();
				log.trace(activeUser.getUsername() + " has returned to editInventoryMenu.");
				log.debug(activeUser.getUsername() + " has entered newAmount " + newAmount);

				// Add the item to inventory
				System.out.println("Adding item to inventory...");
				Item newItem = is.addItem(newName, newPrice, newAmount, newCategory, newDesc);
				log.trace(activeUser.getUsername() + " has returned to editInventoryMenu.");

				if (newItem == null) { // The item entered had at least one bad parameter
					log.warn("The new item was not added. One of the parameters was wrong");
					System.out.println("There was an error with an input. Please try again.");
					break;
				}

				// Save the file and go back to the last menu
				log.trace(activeUser.getUsername() + " has returned to editInventoryMenu.");
				log.debug("newItem is now: " + newItem);
				System.out.println("Item Added!\n");
				break;
			case 2:
				// Add or remove sale
				System.out.println("Add or Remove Sale");

				// Get the item to add a sale to
				Item saleItem = searchItemMenu(false);
				log.debug(activeUser.getUsername() + " selected saleItem " + saleItem);
				if (saleItem == null) {
					break;
				}
				if (saleItem.getSale() != null) {
					System.out.println("That item has a sale already. Do you want to end the sale now?");
					System.out.println("\t1. Yes");
					System.out.println("\t2. No");
					int affirm = getUserInput();
					switch (affirm) {
					case 1:
						// The user wants to end the sale
						System.out.println("Ending the sale...");
						is.endSale(saleItem);
						log.trace(activeUser.getUsername() + " has returned to editInventoryMenu.");
						System.out.println("The sale has ended.");
						log.trace(activeUser.getUsername() + " has returned to editInventoryMenu.");
						continue editLoop;
					default:
						// Invalid input or the user does not want to end the sale.
						System.out.println("No sale was modified");
					}
				}
				// Enter the price of the item during the sale
				System.out.println("Please enter the price would like for " + saleItem.getName() + " for the sale?");
				double salePrice = getUserDouble();
				log.trace(activeUser.getUsername() + " has returned to editInventoryMenu.");

				// Price cannot be 0 or less or be greater than the item's current price
				if (salePrice <= 0.0 || salePrice >= saleItem.getPrice()) {
					log.warn(activeUser.getUsername() + " tried to enter an invalid price.");
					System.out.println(
							"Invalid price. Please make sure price is greater than 0 and less than the item's normal price.");
					break;
				}

				// The final date of the sale
				System.out.println("Please enter the final date of the sale.");
				System.out.println("Format for the date: MM/DD/YYYY");
				List<Integer> dateList = Stream.of(scanner.nextLine().split("/")).map((str) -> Integer.parseInt(str))
						.collect(Collectors.toList());
				log.debug(activeUser.getUsername() + " has entered " + dateList);
				LocalDate finalDate = null;
				try {
					finalDate = LocalDate.of(dateList.get(2), dateList.get(0), dateList.get(1));

				} catch (Exception e) { // If LocalDate throws an exception.
					log.warn("There was a problem with the date entered.");
					System.out.println("The date was not valid or inputted incorrectly. Please try again.");
					break;
				}
				log.debug("finalDate is " + finalDate);
				if (finalDate.isBefore(LocalDate.now())) {
					System.out.println("That date has already past. Please try again.");
					log.debug("Is finalDate after current date?: " + finalDate.isBefore(LocalDate.now()));
					break;
				}
				// Add the sale to the item
				System.out.println("Adding the sale...");
				is.setSale(saleItem, finalDate, salePrice);
				log.debug("saleItem has sale " + saleItem.getSale());
				log.trace(activeUser.getUsername() + " has returned to editInventoryMenu.");
				log.trace(activeUser.getUsername() + " has returned to editInventoryMenu.");
				System.out.println("The sale has been added!");
				break;
			case 3:
				// Edit Existing Item Amount
				System.out.println("Edit Inventory Amount");
				Item item = searchItemMenu(false);
				log.trace(activeUser.getUsername() + " has returned to editInventoryMenu.");
				if (item == null) { // User wanted to go back
					log.info(activeUser.getUsername() + " is going back.");
					break;
				}
				System.out.println("Please enter the new quantity of " + item.getName() + " in inventory: ");
				int newQuantity = getUserInput();
				if (newQuantity > 0) { // The user entered a positive or 0 quantity
					log.info("Item quantity is being changed.");
					is.changeAmount(item, newQuantity);
					log.trace(activeUser.getUsername() + " has returned to editInventoryMenu.");
					log.debug("item has been set to " + item);
					System.out.println(item.getName() + " amount is now " + item.getAmountInInventory());
					break editLoop;
				} else {
					log.info(activeUser.getUsername() + " entered an invalid quantity.");
					System.out.println("Invalid input. Please try again.");
					break;
				}
			case 4:
				// Go back
				break editLoop;
			default:
				// Invalid Input
				log.warn(activeUser.getUsername() + " entered invalid input " + selection);
				System.out.println("Invalid input. Please try again.");
				break;
			}
		}
		log.trace(activeUser.getUsername() + " is exiting editInventoryMenu.");
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
				changePasswordMenu();
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
	 * The Admin's Account Status Management menu
	 * 
	 * @param isReactivating If the admin is reactivating or deactivating accounts.
	 *                       True means reactivate, false means deactivate.
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

	/**
	 * Search for a user by typing in a username. Filters by AccountType and if the
	 * account is active.
	 * 
	 * @param type   The type of account to look up
	 * @param status The active status of accounts to filter by
	 * @return The User that the user selects
	 */
	private static User searchUsernameMenu(AccountType type, boolean status) {
		log.trace(activeUser.getUsername() + " is now in searchUsernameMenu.");
		log.debug("searchUsernameMenu with Parameters: type: " + type + ", status: " + status);
		List<User> userList = null; // The list of users with the correct type and right search terms
		do { // Go in once and loops as long as the userList is null
			System.out.println(
					"Please enter a username to search with. If you want to see all users, just press the 'Enter' key.");
			System.out.println("To Quit, type 'quit':");
			String searchString = scanner.nextLine(); // Enter a username to search or 'quit' to quit
			log.debug(activeUser.getUsername() + " entered searchString: " + searchString);
			if (searchString.equals("quit")) { // If 'quit' was entered, leave the application.
				log.trace(
						((activeUser == null) ? "User" : activeUser.getUsername()) + " is exiting searchUsernameMenu.");
				log.debug("searchUsernameMenu is returning User: " + null);
				return null;
			}
			System.out.println("Searching for users...");
			userList = us.searchUserByName(searchString, type, !status); // Search for the user using a basic
																			// .contains search
			log.trace(activeUser.getUsername() + " is back in searchUsernameMenu.");
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

			}
			System.out.println("\n\t" + Integer.toString(userList.size() + 1) + ". Quit");
			int option = getUserInput() - 1; // This will get the input from the user and subtract it by one to
												// make it usable for the array.
			log.trace(activeUser.getUsername() + " is back in searchUsernameMenu.");
			log.debug("Selection modified to option: " + option);
			if (option >= 0 && option < userList.size()) { // The option is in the correct range.
				validOptionSelected = true; // This breaks the loop.
				selectedUser = userList.get(option); // Get the selected user to modify their status.
			} else if (option == userList.size()) { // This will get the last option, which will always be quit.
				break;
			} else { // They did not enter an option in the correct range.
				System.out.println("Please select a valid option.\n");
			}
			log.debug("validOptionSelected set to: " + validOptionSelected);
		} while (!validOptionSelected);

		log.trace(((activeUser == null) ? "User" : activeUser.getUsername()) + " is exiting searchUsernameMenu.");
		log.debug("searchUsernameMenu is returning User: " + selectedUser);
		return selectedUser;
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
	private static void changeEmailMenu() {
		log.trace(((activeUser == null) ? "User" : activeUser.getUsername()) + " is entering changeEmail.");
		System.out.println(
				"Your current email address is " + activeUser.getEmail() + ". Please enter a new email address: ");
		String newEmail = scanner.nextLine(); // User enters new email
		log.debug(activeUser.getUsername() + " entered newEmail: " + newEmail);
		if (newEmail.isBlank()) {
			System.out.println("The email you entered was blank. Please try again.");
			log.trace(activeUser.getUsername() + " is now leaving changeEmail.");
			return;
		}
		System.out.println("Saving email address...");
		us.updateEmail(activeUser, newEmail); // Save the data to the file
		System.out.println("Email address saved.");
		log.debug(activeUser.getUsername() + " has changed their email address to newEmail " + newEmail + ": "
				+ activeUser.getEmail() == newEmail);

		log.trace(activeUser.getUsername() + " is now leaving changeEmail.");
	}

	/**
	 * Allows all registered users to change their passwords.
	 */
	private static void changePasswordMenu() {
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
		us.updatePassword(activeUser, newPassword);
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
			System.out.println("\nAre you sure you want to deactivate your account? This option cannot be undone.");
			System.out.println("\t1. Yes");
			System.out.println("\t2. No");
			int affirm = getUserInput();
			log.debug(activeUser.getUsername() + " entered affirm: " + affirm);
			switch (affirm) {
			case 1: // User has to enter 1
				System.out.println("Deactivating account...");
				
				us.changeActiveStatus(activeUser, status);
				
				log.trace(activeUser.getUsername() + " back in changeActiveStatusMenu.");
				log.debug(activeUser.getUsername() + " has active status of " + activeUser.isActive());
				activeUser = null; // Logs the user out now that they're inactive.
				log.debug("activeUser is null: " + activeUser == null);
				System.out.println(
						"Your account has been deactivated. If you want to reactivate it, please contact an administrator.\n");
			default: // Any other input will have the user leave the menu.
				System.out.println("Deactivation cancelled.");
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

				User selectedUser = searchUsernameMenu(type, status);
				if (selectedUser != null) {
					System.out.println(
							"Are you sure you want to " + activateString + " " + selectedUser.getUsername() + "?");
					System.out.println("\t1. Yes");
					System.out.println("\t2. No");
					int affirm = getUserInput(); // Get the users affirmative status
					log.debug(activeUser.getUsername() + " entered affirm: " + affirm);
					switch (affirm) {
					case 1: // User entered 1
						System.out.println("Trying to " + activateString + " this account...");
						
					
							
						us.changeActiveStatus(selectedUser, status);
						log.trace(activeUser.getUsername() + " is back in changeActiveStatusMenu.");
						log.debug("selectedUser now set to new status: " + selectedUser.isActive());
						System.out.println("Account " + activateString + "d.");
					default: // Any input that isn't 1 will leave the menu
						System.out.println("User's status has not changed.");
						break;
					}
				}
			} else {
				System.out.println("You are not authorized to deactivate these kinds of accounts.");
			}
		}
		log.trace(((activeUser == null) ? "User" : activeUser.getUsername()) + " is exiting changeActiveStatusMenu.");
	}

	/**
	 * View the list of carts from either a cart or an order
	 */
	private static void viewCartItemsFromList(List<CartItem> activeCart, boolean isOrderList) {
		log.trace(activeUser.getUsername() + " has entered viewCartItemsFromLis");
		log.debug("viewCartItemsFromList parameters: " + activeCart);
		if (activeCart == null) {
			log.warn("Null object was past in!");
			System.out.println("No Items were found, please try again.");
			return;
		}
		double total = 0.0; // The total for the whole cart.
		
		String extraTab = isOrderList ? "\t" : ""; // Add an extra tab for formatting.
		for (int i = 0; i < activeCart.size(); i++) { // Loop through the list
			CartItem cartItem = activeCart.get(i); // For easier access
			log.debug("Current cartItem in loop: " + cartItem);
			// Print out the details of the item
			
			//Get the price that should be displayed:
			double price = cartItem.getPrice();
			// Add an extra tab for formatting or the number if it is a cart we're seeing.
			String print = isOrderList ? "\t" : Integer.toString(i + 1) + ". ";
			System.out.println("\t" + print + cartItem.getItem().getName());
			System.out.println(extraTab + "\tUnit Price: $" + priceFormat.format(price));
			System.out.println(extraTab + "\tQuantity In Cart: " + cartItem.getQuantity() + "\n");

			total = price * cartItem.getQuantity(); // Add to the total
			log.debug("Current total of the cart: " + total);
		} // Printing the total
		System.out.println(extraTab + "Total: $" + priceFormat.format(total));

		log.trace(activeUser.getUsername() + " is now exiting viewCartItemsFromList.");
	}

	/**
	 * Menu to search for items by category.
	 */
	private static Item searchItemMenu(boolean onlyInStock) {
		log.trace(activeUser.getUsername() + " is now in searchItemMenu.");
		log.debug("searchItemMenu parameters: onlyInStock: " + onlyInStock);

		Item selectedItem = null;
		searchItemLoop: while (selectedItem == null) {
			ItemCategory selectedCategory = itemCategoryMenu();
			log.trace(activeUser.getUsername() + " has returned to searchItemMenu.");
			if (selectedCategory == null) { // This means the user inputted to go back.
				break searchItemLoop;
			}
			System.out.println("Searching for list of items...");
			List<Item> itemList = is.getItems(selectedCategory, onlyInStock);
			log.trace(activeUser.getUsername() + " has returned to searchItemMenu.");
			System.out.println("Please select an item from the list: ");
			boolean validItemSelection = false;

			if (itemList == null) { // If the item list returned is null
				System.out.println("No items for that category were found!\n");
				continue; // This will restart the loop.
			}
			do {
				for (int i = 0; i < itemList.size(); i++) { // Loop through the returned list
					Item item = itemList.get(i);
					System.out.println("\t" + Integer.toString(i + 1) + ". " + item.getName());

					// If there is a sale for this item right now
					String priceString = "Price: $" + priceFormat.format(item.getPrice());
					if (item.getSale() != null) {
						priceString = "SALE! (ends " + item.getSale().getEndDate() + ") Sale Price: $"
								+ priceFormat.format(item.getSale().getSalePrice());
					}
					// Print the price and amount in stock
					System.out.println("\t\t" + priceString + " Amount In Stock: " + item.getAmountInInventory());
					System.out.println("\t\t" + item.getDescription()); // Print the description
					System.out.println(""); // New line to seperate items.
				}
				// Line for choice to go back to category menu
				System.out.println("\n\t" + Integer.toString(itemList.size() + 1) + ". Back");
				int selection = getUserInput() - 1;
				log.debug("selection is now: " + selection);

				// The selection was in the right range
				if (selection >= 0 && selection < itemList.size()) {
					selectedItem = itemList.get(selection); // This will save the category that will be searched
					log.debug("selectedItem is now " + selectedItem);
					validItemSelection = true; // This will break the loop.
				}
				// If the user selected the quit option.
				else if (selection == itemList.size()) {
					break; // Will only break from this loop and continue to do the main loop
				} else { // If the user entered an invalid input
					log.warn(activeUser.getUsername() + " has entered an invalid input.");
					System.out.println("That was an invalid selection. Please try again.");
				}
			} while (!validItemSelection);
		}
		log.trace(activeUser.getUsername() + " is now exiting searchItemMenu.");
		log.debug("searchItemMenu is returning" + selectedItem);
		return selectedItem;
	}

	/**
	 * Loops through the ItemCategory enum and displays them for the user to pick
	 * one.
	 * 
	 * @return the selected ItemCategory
	 */
	private static ItemCategory itemCategoryMenu() {
		log.trace(activeUser.getUsername() + " has entered categoryMenu");
		ItemCategory selectedCategory = null;
		boolean isValidSelection = false;
		do {
			System.out.println("Please select an item category to search through: ");
			ItemCategory[] categoryList = ItemCategory.values();
			for (int i = 0; i < categoryList.length; i++) { // Loop through each valid category
				System.out.println("\t" + Integer.toString(i + 1) + ". " + categoryList[i].toString());
			}
			// Option for going back to last menu.
			System.out.println("\n\t" + Integer.toString(categoryList.length + 1) + ". Back");

			log.trace(activeUser.getUsername() + " is entering an input.");
			int selection = getUserInput() - 1; // Get the user input and subtract it by one
			log.debug("Input has been put into selection as " + selection);
			// If the user input is in the range of the category values array
			if (selection >= 0 && selection < categoryList.length) {
				selectedCategory = categoryList[selection]; // This will save the category that will be searched
															// with.
				isValidSelection = true; // This will break the loop.
			}
			// If the user selected the quit option.
			else if (selection == categoryList.length) {
				log.info(activeUser.getUsername() + " inputted to go back.");
				isValidSelection = true;
			} else { // If the user entered an invalid input
				log.warn(activeUser.getUsername() + " has entered an invalid input.");
				System.out.println("That was an invalid selection. Please try again.");
			}

		} while (!isValidSelection);

		log.trace(activeUser.getUsername() + " is exiting categoryMenu");
		log.debug("categoryMenu is returning ItemCategory " + selectedCategory);
		return selectedCategory;
	}

	/**
	 * Used to edit an order's status
	 * 
	 * @param orders The list of orders available to edit
	 * @param status The status to set the selected order to
	 */
	private static void orderEditMenu(List<Order> orders, boolean isCustomer) {
		log.trace(activeUser.getUsername() + " is now in orderEditMenu.");
		log.debug("orderEditMenu parameters: orders: " + orders + ", isCustomer: " + isCustomer);

		if (orders == null) { // If the orders or status are null, will just return.
			log.warn(((orders == null) ? "orders" : "status") + " was passed in as null.");
			System.out.println("There was a problem getting orders. Please try again.");
		}

		int selection = -1; // Save the selection
		while (selection < 0) { // Infinite loop until input is correct.
			for (int i = 0; i < orders.size(); i++) { // Loop through each item in the cart.
				Order order = orders.get(i);
				log.debug("order is set to " + order);
				System.out.println("\t" + Integer.toString(i + 1) + ". " + order.getOrderDate());
				viewCartItemsFromList(order.getItemsOrdered(), true); // Will print the items in the list.
				log.trace(activeUser.getUsername() + " is back in orderEditMenu.");
				System.out.println("\tStatus: " + order.getStatus() + "\n");
				System.out.println("\tShip Date: " + order.getShipDate() + "\n");
			}
			System.out.println("\t" + Integer.toString(orders.size() + 1) + ". Back");
			selection = getUserInput() - 1; // Get the user's input
			log.trace(activeUser.getUsername() + " is back in orderEditMenu.");
			log.debug("selection has been set to user input - 1:  " + selection);

			// The selection is in the range of the orders list
			if (selection >= 0 && selection < orders.size()) {

				// Get the status of the current order
				OrderStatus currentStatus = orders.get(selection).getStatus();
				// If the user is a customer and is trying to change a order without the ORDERED
				// status
				if ((isCustomer && !currentStatus.equals(OrderStatus.ORDERED)) ||
				// Or the user is not a customer and the order status is not ordered or shipped
						(!isCustomer && !(currentStatus.equals(OrderStatus.ORDERED)
								|| currentStatus.equals(OrderStatus.SHIPPED)))) {

					log.info(activeUser.getUsername() + " picked an order with order status "
							+ orders.get(selection).getStatus());
					System.out.println("That order cannot be changed. Please pick another one.");
					selection = -1; // This will keep the user in the loop.

				} else { // Continue with changing the order status

					// Set the status to change to CANCELLED by default
					OrderStatus status = OrderStatus.CANCELLED;

					// If the user is not a customer and trying to change an already shipped order
					if (!isCustomer && currentStatus.equals(OrderStatus.SHIPPED)) {
						status = OrderStatus.REFUNDED;
					}
					System.out.println("Are you sure you want to "
							+ (((status.equals(OrderStatus.CANCELLED)) ? "Cancel" : "Refund") + " this order?"));
					System.out.println("\t1. Yes");
					System.out.println("\t2. No");
					int affirm = getUserInput();
					log.debug("affirm has been set to user input:  " + affirm);
					switch (affirm) {
					case 1: // Order status will be changed
						us.changeOrderStatus(orders.get(selection), status);

						
						log.debug("order status has been set to " + orders.get(selection).getStatus());
						System.out.println("Order status has been changed to " + status);
						break;
					default: // All other inputs will break
						System.out.println("No orders were changed.");
						break;
					}
				}
			} else if (selection == orders.size()) { // If the user selected the Back option
				log.info("User is leaving with input " + selection);
			} else { // User entered an invalid input
				log.warn(activeUser.getUsername() + " entered an invalid input: " + selection);
				selection = -1; // This will keep the user in the loop
				log.debug("selection is now " + selection);
				System.out.println("Invalid input. Please try again.");
			}
		}
		log.trace(activeUser.getUsername() + " is now exiting orderEditMenu.");
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
	 * Used to parse a double from a user's input
	 * 
	 * @return The double the user entered. 0.0 if the user did not input a valid
	 *         double
	 */
	private static double getUserDouble() {
		log.trace("User is now in getUserDouble");
		double userInput;
		try {
			userInput = Double.parseDouble(scanner.nextLine());
		} catch (Exception e) {
			log.warn("User inputted an invalid value.");
			userInput = 0.0;
		}
		System.out.println(); // Puts in a linebreak.
		log.trace(((activeUser == null) ? "User" : activeUser.getUsername()) + " is exiting getUserDouble.");
		log.debug(((activeUser == null) ? "User" : activeUser.getUsername()) + " inputted " + userInput);
		return userInput;

	}
}
