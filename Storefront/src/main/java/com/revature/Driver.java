package com.revature;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.revature.controllers.ItemController;
import com.revature.controllers.UserController;

import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJackson;

public class Driver {
	public static void main(String[] args) {
		// Menu.start();

		// Used to make sure dates are not being returned as LocalDate object
		ObjectMapper jackson = new ObjectMapper();
		jackson.registerModule(new JavaTimeModule());
		jackson.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		JavalinJackson.configure(jackson);

		Javalin app = Javalin.create().start(8080);

		UserController userControl = new UserController();
		ItemController itemControl = new ItemController();

		// As a user, I can log in
		app.post("/users", userControl::login);

		// As a user, I can logout
		app.delete("/users", userControl::logout);

		// As a customer, I can register for a customer account
		// As an administrator, I can create manager accounts.
		app.put("/users/:username", userControl::register);

		// As a user, I can edit my password
		app.put("/users/:username/password", userControl::changePassword);

		// As a customer, I can edit my account details (email)
		app.put("/users/:username/email", userControl::changeEmail);

		// As a customer, I can edit my account details (Active Status)
		// As a manager, I can deactivate customer accounts
		// As an administrator, I can deactivate and reactivate customer and manager
		// accounts
		app.put("/users/:username/status", userControl::changeActiveStatus);

		// As a customer, I can add items to my cart.
		app.post("/users/:username/cart", userControl::addToCart);

		// Get the item list
		app.get("/items", itemControl::getItems);

		// As a customer, I can remove items from my cart
		app.delete("/users/:username/cart/:cartItemId", userControl::removeFromCart);

		// As a customer, I can modify the quantity of items in my cart.
		app.put("/users/:username/cart/:cartItemId/quantity", userControl::changeQuantityOfCartItem);

		// As a customer, I can create an order for the items in my cart.
		app.post("/users/:username/orders", userControl::createOrder);
		
		//As a manager, I can add items to the inventory.
		
		//As a manager, I can edit amount in inventory of items.
		
		//As a manager, I can add limited time deals for items.
		
		//As a manager, I can refund a customer's order.
	}
}
