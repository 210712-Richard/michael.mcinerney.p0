package com.revature;

import com.revature.menus.Menu;

public class Driver {
	public static void main(String[] args) {
		Menu.start();
		
		//Used to make sure dates are not being returned as LocalDate object
//		ObjectMapper jackson = new ObjectMapper();
//		jackson.registerModule(new JavaTimeModule());
//		jackson.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//		JavalinJackson.configure(jackson);
//		
//		Javalin app = Javalin.create().start(8080);
//		
//		UserController userControl = new UserController();
//		
//		//As a user, I can log in
//		app.post("/users", userControl::login);
//		
//		//As a user, I can logout
//		app.delete("/users", userControl::logout);
//		
//		//As a customer, I can register for a customer account
//		//As an administrator, I can create manager accounts.
//		app.put("/users/:username", userControl::register);
	}
}
