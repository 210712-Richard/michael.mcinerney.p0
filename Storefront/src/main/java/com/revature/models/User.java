package com.revature.models;

import java.util.List;

public class User {
	private int id; //Unique ID of each user
	private String username; //The unique username of the user
	private String password; //The password used to log in
	private String email; //The email used to register
	private List<CartItem> cart; //The items the user currently has in their cart
	private List<Order> pastOrders; //The orders the user has made in the past
	private AccountType accountType; //What kind of account the user is
	private boolean isActive; //True if the account is still active. False if it has been deactivated.
}
