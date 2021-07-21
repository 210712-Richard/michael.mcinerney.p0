package com.revature.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id; //Unique ID of each user
	private String username; //The unique username of the user
	private String password; //The password used to log in
	private String email; //The email used to register
	private List<CartItem> cart; //The items the user currently has in their cart
	private List<Order> pastOrders; //The orders the user has made in the past
	private AccountType accountType; //What kind of account the user is
	private boolean isActive; //True if the account is still active. False if it has been deactivated.
	
	public User() {
		super();
		this.cart = new ArrayList<CartItem>();
		this.pastOrders = new ArrayList<Order>();
	}
	
	public User(int id, String username, String password, String email, AccountType accountType, boolean isActive) {
		this();
		this.id = id;
		this.username = username;
		this.password = password;
		this.email = email;
		this.accountType = accountType;
		this.isActive = isActive;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<CartItem> getCart() {
		return cart;
	}
	public void setCart(List<CartItem> cart) {
		this.cart = cart;
	}
	public List<Order> getPastOrders() {
		return pastOrders;
	}
	public void setPastOrders(List<Order> pastOrders) {
		this.pastOrders = pastOrders;
	}
	public AccountType getAccountType() {
		return accountType;
	}
	public void setAccountType(AccountType accountType) {
		if (accountType == null) {
			return;
		}
		this.accountType = accountType;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public void addToCart(Item item, int quantity, double price) {
		//item can't be null, and neither quantity nor price can be negative or zero
		if (item == null || quantity <= 0 || price <= 0.0) {
			return;
		}
		cart.add(new CartItem(item, quantity, price));
	}
	
	public void createOrder() {
		pastOrders.add(new Order(cart, LocalDate.now()));
		cart = new ArrayList<CartItem>(); //Get rid of the items in the cart since it has been ordered.
	}
	@Override
	public int hashCode() {
		return Objects.hash(accountType, cart, email, id, isActive, password, pastOrders, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return accountType == other.accountType && Objects.equals(cart, other.cart)
				&& Objects.equals(email, other.email) && id == other.id && isActive == other.isActive
				&& Objects.equals(password, other.password) && Objects.equals(pastOrders, other.pastOrders)
				&& Objects.equals(username, other.username);
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", email=" + email + ", cart="
				+ cart + ", pastOrders=" + pastOrders + ", accountType=" + accountType + ", isActive=" + isActive + "]";
	}
	
	
}
