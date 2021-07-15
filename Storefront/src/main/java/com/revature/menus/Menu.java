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
				//TODO: Login
				System.out.println("Please enter your username:");
				String username = scanner.nextLine();
				System.out.println("Please enter your password");
				String password = scanner.nextLine();
				System.out.println("Logging you in...");
				activeUser = us.login(username, password);
				if (activeUser == null) {
					System.out.println("Login details did not match, please try again.\n");
				}
				else if (activeUser.isActive() == false) {
					System.out.println("Your account has been deactivated. Please contact a manager.\n");
					break main;
				}
				System.out.println("\nHello " + activeUser.getUsername() + "! What would you like to do?");
				if (activeUser.getAccountType() == AccountType.CUSTOMER) {
					openCustomerMenu();
				}
				else if(activeUser.getAccountType() == AccountType.MANAGER) {
					openManagerMenu();
				}
				else if(activeUser.getAccountType() == AccountType.ADMINISTRATOR) {
					openAdminMenu();
				}
				else {
					System.out.println("There is a problem with your account. Please contact a manager or administrator.");
				}
				break;
			case 2:
				//TODO: Register
			case 3:
				//Close
				System.out.println("Have a nice day!");
				System.out.println("Closing Storefront...");
				break main;
			default:
				//Error
				System.out.println("Invalid Input. Please try again.\n");			}
		}
	}
	
	private static void openCustomerMenu() {
		//TODO: Add customer functionality
		System.out.println("Functionality coming soon!");
	}
	
	private static void openManagerMenu() {
		//TODO: Add manager functionality
		System.out.println("Functionality coming soon!");
	}
	
	private static void openAdminMenu() {
		//TODO: Add admin functionality
		System.out.println("Functionality coming soon!");
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
