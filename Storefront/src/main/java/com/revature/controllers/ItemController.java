package com.revature.controllers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.AccountType;
import com.revature.beans.Item;
import com.revature.beans.User;
import com.revature.services.ItemService;
import com.revature.services.UserService;

import io.javalin.http.Context;

public class ItemController {
	private static Logger log = LogManager.getLogger(ItemController.class);
	private UserService userService = new UserService();
	private ItemService itemService = new ItemService();
	
	/**
	 * Gets all the items by category
	 * @param ctx The context. The body should have the category.
	 */
	public void getItems(Context ctx) {
		log.trace("App has entered getItems");
		log.debug("getItems ctx.body(): " + ctx.body());
		
		//See if the user is logged in
		User loggedUser = ctx.sessionAttribute("loggedUser");
		log.debug("loggedUser: " + loggedUser);
		
		//If the user is a customer or not logged in, they shouldn't see the out of stock items
		boolean outOfStock = false;
		if (loggedUser == null || loggedUser.getAccountType().equals(AccountType.CUSTOMER)) {
			outOfStock = true;
		}
		
		//Check and make sure there is a category to search by.
		Item item = ctx.bodyAsClass(Item.class);
		log.debug("Item from the body: " + item);
		
		//If the item is null or there is no category
		if (item == null || item.getCategory() == null) {
			ctx.status(406);
			ctx.html("No valid category was passed in");
			log.trace("App is exiting getItems");
			return;
		}
		
		//Get the list of items and send them back
		List<Item> items = itemService.getItems(item.getCategory(), outOfStock);
		log.debug("items returned: " + items);
		ctx.json(items);
		log.trace("App is exiting getItems");
	}
}
