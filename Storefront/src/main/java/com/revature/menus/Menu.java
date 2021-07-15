package com.revature.menus;

import java.util.Scanner;

import com.revature.util.SingletonScanner;

public class Menu {
	
	Scanner scanner = SingletonScanner.getInstance().getScan();
	
	public static void start() {
		openMainMenu();
	}
	
	private static void openMainMenu() {
		while(true) {
			System.out.println("Welcome to the Storefront!");
			System.out.println("Please Select an Option:");
			System.out.println("\t1. Login");
			System.out.println("\t2. Register");
			System.out.println("\t3. Close");
		}
	}
	
	private static int getUserInput() {
		
		int retVal = 0;
		return 0;
		
	}
}
