package com.revature.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.AccountType;
import com.revature.beans.User;
import com.revature.services.UserService;

import io.javalin.http.Context;

public class UserController {
	private static Logger log = LogManager.getLogger(UserController.class);
	private UserService userService = new UserService();
	
	/**
	 * Used to let the user login
	 * @param ctx The context
	 */
	public void login(Context ctx) {
		log.trace("App has entered login.");
		log.debug("Request body: " + ctx.body());
		
		//Create a user from the body
		User user = ctx.bodyAsClass(User.class);
		log.debug("User serialized from context: " + user);


		//Call the login method to get the user
		user = userService.login(user.getUsername(), user.getPassword());
		log.debug("user has been set to " + user);
		
		if (user != null) { //The user was found
			//Save the user for the session
			ctx.sessionAttribute("loggedUser", user);
			
			//Send the user back.
			ctx.json(user);
			log.trace("App is leaving login");
			return;
		}
		//The user couldn't be found.
		ctx.status(401);
		log.trace("App is leaving login");
	}
	
	/**
	 * Register a new user
	 * @param ctx The context
	 */
	public void register(Context ctx) {
		log.trace("App has entered register.");
		log.debug("Request body: " + ctx.body());
		
		User loggedUser = ctx.sessionAttribute("loggedUser");
		log.debug("loggedUser: " + loggedUser);
		//If the user is logged in and is not an admin
		if (loggedUser != null && loggedUser.getAccountType() != AccountType.ADMINISTRATOR) {
			ctx.status(403);
			log.trace("App is leaving register");
			return;
		}
		else {
			//Get the user
			User newUser = ctx.bodyAsClass(User.class);
			log.debug("newUser: " + newUser);
			
			if (!userService.isUsernameUnique(newUser.getUsername())) {
				ctx.status(209);
				ctx.html("Username has been taken");
				log.trace("App is leaving register");
				return;
			}
			
			//Register the user
			newUser = userService.register(newUser.getUsername(), newUser.getPassword(), newUser.getEmail(), newUser.getAccountType());
			log.debug("newUser: " + newUser);

			if (newUser == null) { //Error with registration
				ctx.status(401);
				ctx.html("Invalid input with account");
				log.trace("App is leaving register");
				return;
			}
			
			ctx.status(201);
			ctx.json(newUser);
			log.trace("App is leaving register");
		}
	}
}
