package com.revature.menus;

import java.util.Scanner;

import com.revature.models.AccountType;
import com.revature.models.User;
import com.revature.services.UserService;
import com.revature.util.SingletonScanner;

public class Menu {
	
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
					System.out.println("Your account has been deactivated. Please contact a manager.\n");
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
					System.out.println("There is a problem with your account. Please contact a manager or administrator.");
				}
				break;
			case 2:
				//Register for a customer account
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
				System.out.println("Please enter your email address:");
				String newEmail = scanner.nextLine();
				System.out.println("Registering your account...");
				
				//This will create the new customer account.
				activeUser = us.register(newUsername, newPassword, newEmail, AccountType.CUSTOMER);
				if (activeUser == null) {
					System.out.println("There was a problem setting up your account. Please try again!");
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
			System.out.println("\t3. Edit Settings");
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
			System.out.println("\t4. Edit Settings");
			System.out.println("\t5. Logout");
			
			switch(getUserInput()) {
			case 1:
				//TODO: Edit Inventory
				break;
			case 2:
				//TODO: Deactivate Customer Account
				break;
			case 3:
				//TODO: Refund an order
				break;
			case 4:
				//TODO: Edit Settings
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
		managerLoop: while(true) {
			System.out.println("\t1. Create Manager Account");
			System.out.println("\t2. Deactivate or Reactivate an Account");
			System.out.println("\t3. Edit Settings");
			System.out.println("\t4. Logout");
			
			switch(getUserInput()) {
			case 1:
				//TODO: Create manager accounts
				break;
			case 2:
				//TODO: Edit if an account is active or not
				break;
			case 3:
				//TODO: Edit email and password
				break;
			case 4:
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
}
