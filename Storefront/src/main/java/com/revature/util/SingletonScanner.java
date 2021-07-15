package com.revature.util;

import java.util.Scanner;

public class SingletonScanner {
	
	private static SingletonScanner inst;
	
	private Scanner scanner;
	
	private SingletonScanner() {
		scanner = new Scanner(System.in);
	}
	
	public static synchronized SingletonScanner getInstance() {
		if (inst == null) {
			inst = new SingletonScanner();
		}
		return inst;
	}
	
	public Scanner getScan() {
		return scanner;
	}
}
