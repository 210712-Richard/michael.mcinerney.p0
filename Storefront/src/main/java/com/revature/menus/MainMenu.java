package com.revature.menus;

import java.util.List;
import java.util.Scanner;

import com.revature.models.AccountType;
import com.revature.models.User;
import com.revature.services.UserService;
import com.revature.util.SingletonScanner;

public class MainMenu {
	
	private static Scanner scanner = SingletonScanner.getInstance().getScan();
	
	private static UserService us = new UserService();
	
	private static User activeUser = null;
	
	public static void start() {
		openMainMenu();
		
	}
	
	private static void openMainMenu() {
		main: while(true) {
			System.out.println("Welcome to the Storefront!");
			System.out.println("Please Select an Option:");
			System.out.println("\t1. Login");
			System.out.println("\t2. Register");
			System.out.println("\t3. Close");
			
			switch (getUserInput()) {
			case 1:
				//Login
				//Have the user input their username and password
				System.out.println("Please enter your username:");
				String username = scanner.nextLine();
				System.out.println("Please enter your password");
				String password = scanner.nextLine();
				
				System.out.println("Logging you in...");
				activeUser = us.login(username, password); //Will check if the user exists.
				
				if (activeUser == null) { //If the user was not found.
					System.out.println("Login details did not match, please try again.\n");
				}
				else if (activeUser.isActive() == false) { //If the user has been deactivated.
					System.out.println("Your account has been deactivated. Please contact an administrator.\n");
					break main;
				}
				else if (activeUser.getAccountType() == AccountType.CUSTOMER) { //User is a customer
					openCustomerMenu();
				}
				else if(activeUser.getAccountType() == AccountType.MANAGER) { //User is a manager
					openManagerMenu();
				}
				else if(activeUser.getAccountType() == AccountType.ADMINISTRATOR) { //User is a admin
					openAdminMenu();
				}
				else { //Somehow, the user is none of those things.
					System.out.println("There is a problem with your account. Please contact an administrator.");
				}
				break;
			case 2:
				//Register for a customer account
				activeUser = createAccount(AccountType.CUSTOMER);
				if (activeUser == null) {
					System.out.println("There was a problem setting up your account. Please try again.\n");
				} else {
					openCustomerMenu();
				}
				break;
			case 3:
				//Close
				System.out.println("Have a nice day!");
				System.out.println("Closing Storefront...");
				break main;
			default:
				//Error
				System.out.println("Invalid Input. Please try again.\n");
				break;
			}
		}
	}
	
	private static void openCustomerMenu() {
		System.out.println("Hello " + activeUser.getUsername()+ "! What would you like to do?");
		customerLoop: while(true) {
			System.out.println("\t1. Search for Items"); 
			System.out.println("\t2. Go to Cart");
			System.out.println("\t3. Edit Account Settings");
			System.out.println("\t4. Logout");
			
			switch(getUserInput()) {
			case 1:
				//TODO: Search for items.
				break;
			case 2:
				//TODO: View, edit, and checkout cart
				break;
			case 3:
				//TODO: Add ability to edit password and email
				openCustomerSettings();
				if (activeUser == null) {
					break customerLoop;
				}
				break;
			case 4:
				//Logout
				System.out.println("Logging you out...\n");
				activeUser = null;
				break customerLoop;
			default:
				//Input error
				System.out.println("Please enter a valid option:");
				break;
				
			}
		}
	}
	
	private static void openManagerMenu() {
		System.out.println("Hello " + activeUser.getUsername() + "! What would you like to do?");
		managerLoop: while(true) {
			System.out.println("\t1. Edit Inventory");
			System.out.println("\t2. Deactivate Customer Account");
			System.out.println("\t3. Refund Order");
			System.out.println("\t4. Edit password");
			System.out.println("\t5. Logout");
			
			switch(getUserInput()) {
			case 1:
				//TODO: Edit Inventory
				break;
			case 2:
				//Deactivate Customer Account
				changeActiveStatusMenu(false, AccountType.CUSTOMER, false);
				break managerLoop;
			case 3:
				//TODO: Refund an order
				break;
			case 4:
				//Edit Password for Managers
				changePassword();
				break;
			case 5:
				//Logout
				System.out.println("Logging you out...");
				activeUser = null;
				break managerLoop;
			default:
				//Input error
				System.out.println("Please enter a valid option:");
				break;
			}
		}
		
		
	}
	
	private static void openAdminMenu() {
		System.out.println("Hello " + activeUser.getUsername() + "! What would you like to do?");
		adminLoop: while(true) {
			System.out.println("\t1. Create Manager Account");
			System.out.println("\t2. Create Admin Account");
			System.out.println("\t3. Reactivate an Account");
			System.out.println("\t4. Deactivate an Account");
			System.out.println("\t5. Edit Password");
			System.out.println("\t6. Logout");
			
			switch(getUserInput()) {
			case 1:
				//Create manager accounts
				User newManager = createAccount(AccountType.MANAGER);
				if (newManager == null) {
					System.out.println("Error creating account. Please try again.\n");
				} else {
					System.out.println("Account Successfully Created! Please give login information to the manager.");
				}
				break;
			case 2:
				//Create manager accounts
				User newAdmin = createAccount(AccountType.MANAGER);
				if (newAdmin == null) {
					System.out.println("Error creating account. Please try again.\n");
				} else {
					System.out.println("Account Successfully Created! Please give login information to the administrator.");
				}
				break;
			case 3:
				//Reactivate an account
				adminAccountStatusMenu(true);
				break;
			case 4:
				//Edit if an account is active or not
				adminAccountStatusMenu(false);
				break;
			case 5:
				//Edit password
				changePassword();
				break;
			case 6:
				//Logout
				System.out.println("Logging you out...");
				activeUser = null;
				break adminLoop;
			default:
				//Input error
				System.out.println("Please enter a valid option:");
				break;
			}
		}
	}
	private static void openCustomerSettings() {
		
		customerSettingsLoop: while(true) {
			System.out.println("Settings Page. Please select an option:");
			System.out.println("\t1. Change email");
			System.out.println("\t2. Change password");
			System.out.println("\t3. Deactivate Account");
			System.out.println("\t4. Go Back");
			
			switch(getUserInput()) {
			case 1:
				changeEmail();
				break;
			case 2:
				changePassword();
				break;
			case 3:
				//Deactivate account
				changeActiveStatusMenu(true, AccountType.CUSTOMER, true);
				if (activeUser == null) {
					break customerSettingsLoop;
				}
				break;
			case 4:
				//Go Back
				break customerSettingsLoop;
			}
		}
	}
	private static int getUserInput() {
		int userInput;
		try{
			userInput = Integer.parseInt(scanner.nextLine());
		} catch(Exception e) {
			userInput = -1;
		}
		System.out.println(); //Puts in a linebreak.
		return userInput;
		
	}
	
	private static User createAccount(AccountType type) {
		String newUsername = "";
		do {
			System.out.println("Please enter a username:");
			newUsername = scanner.nextLine();
			//Checks to see if the username has been taken or not.
			//False means that it is in use already, true otherwise.
			if (!us.isUsernameUnique(newUsername)) {
				System.out.println(newUsername + " is already is use. Please try again.\n");
			}
		} while(!us.isUsernameUnique(newUsername));
		
		System.out.println("Please enter a password:");
		String newPassword = scanner.nextLine();
		System.out.println("Please enter an email address:");
		String newEmail = scanner.nextLine();
		System.out.println("Registering your account...");
		
		//This will create the new customer account.
		return us.register(newUsername, newPassword, newEmail, type);
	}
	
	private static void changeEmail() {
		System.out.println("Your current email address is "+ activeUser.getEmail()+ ". Please enter a new email address: ");
		String newEmail = scanner.nextLine();
		System.out.println("Saving email address...");
		activeUser.setEmail(newEmail);
		activeUser = us.changeUserDetails(activeUser);
		System.out.println("Email address saved.");
	}
	
	private static void changePassword() {
		String currentPassword = "";
		do {
			System.out.println("Please enter current password:");
			currentPassword = scanner.nextLine();
			if (!activeUser.getPassword().equals(currentPassword)) {
				System.out.println("That password was incorrect. Please try again.\n");
			}
		} while(!activeUser.getPassword().equals(currentPassword));
		
		String newPassword = "";
		String confirmPassword = " ";
		do {
			System.out.println("Please enter your new password:");
			newPassword = scanner.nextLine();
			System.out.println("Please reenter to confirm your password:");
			confirmPassword = scanner.nextLine();
			if (!newPassword.equals(confirmPassword)) {
				System.out.println("Those passwords did not match. Please try again.\n");
			}
		} while(!newPassword.equals(confirmPassword));
		
		System.out.println("Saving password...");
		activeUser.setPassword(newPassword);
		us.changeUserDetails(activeUser);
		System.out.println("Password Saved.");
	}
	
	private static void changeActiveStatusMenu(boolean editingSelf, AccountType type, boolean status) {
		if (editingSelf) {
			System.out.println("\nAre you sure you want to deactivate your account? This option cannot be undone: [Y]es/[n]o");
			String affirm = scanner.nextLine();
			
			switch(affirm) {
			case "Y":
				System.out.println("Deactivating account...");
				activeUser.setActive(false);
				us.changeUserDetails(activeUser);
				activeUser = null;
				System.out.println("Your account has been deactivated. If you want to reactivate it, please contact an administrator.\n");
			default:
				break;
			}
		}
		else {
			String activateString = status ? "reactivate" : "deactivate";
			if (
				(type == AccountType.CUSTOMER && (activeUser.getAccountType() == AccountType.MANAGER || activeUser.getAccountType() == AccountType.ADMINISTRATOR))
				|| type != AccountType.ADMINISTRATOR && (activeUser.getAccountType() == AccountType.ADMINISTRATOR)) {
				
				List<User> userList = null;
				do {
					System.out.println("Please enter a username to search with. If you want to see all users, just press the 'Enter' key.");
					System.out.println("To Quit, type 'quit':");
					String searchString = scanner.nextLine();
					if (searchString == "quit") {
						return;
					}
					System.out.println("Searching for users...");
					userList = us.searchUserByName(searchString, type, !status);
					if (userList == null) {
						System.out.println("No accounts were found with that name. Please try again.\n");
					}
				} while(userList == null);
				
				boolean validOptionSelected = false;
				User selectedUser = null;
				do {
					System.out.println("Please select a username from the list or quit:");
					for (int i = 0; i < userList.size(); i++) {
						if (userList.get(i).getUsername() != activeUser.getUsername()) {
							System.out.println("\t" + Integer.toString(i+1) + ". " + userList.get(i).getUsername());
						}
						System.out.println("\n\t" + Integer.toString(userList.size()+1)+ ". Quit");
					}
					int option = Integer.parseInt(scanner.nextLine())-1;
					if (option >= 0 && option < userList.size()) {
						validOptionSelected = true;
						selectedUser = userList.get(option);
					}
					else if(option == userList.size()) {
						return;
					}
					else {
						System.out.println("Please select a valid option.\n");
					}
				} while(!validOptionSelected);
				
				System.out.println("Are you sure you want to "+ activateString +" "+ selectedUser.getUsername() +"? [Y]es/[n]o");
				String affirm = scanner.nextLine();
				switch(affirm) {
				case "Y":
					System.out.println("Trying to "+activateString+" this account...");
					selectedUser.setActive(status);
					us.changeUserDetails(selectedUser);
					System.out.println("Account "+ activateString +"d.");
				default:
					break;
					
				}
			}
			else {
				System.out.println("You are not authorized to deactivate these kinds of accounts.");
			}
		}
	}
	
	private static void adminAccountStatusMenu(boolean isReactivating) {
		adminAccountLoop: while(true) {
			System.out.println("\nWhich type of account do you want to "+ (isReactivating ? "reactivate" : "deactivate")+"?");
			System.out.println("\t1. Customer");
			System.out.println("\t2. Manager");
			System.out.println("\t3. Back");
			AccountType type;
			switch(getUserInput()) {
			case 1:
				changeActiveStatusMenu(false, AccountType.CUSTOMER, isReactivating);
				break adminAccountLoop;
			case 2:
				changeActiveStatusMenu(false, AccountType.MANAGER, isReactivating);
				break adminAccountLoop;
			case 3:
				break adminAccountLoop;
			default:
					System.out.println("Invalid input. Please try again.");
					break;
			}
		}
		
	}
}
