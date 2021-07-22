package com.revature.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
}
