package com.revature.controllers;

import java.time.LocalDate;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.AccountType;
import com.revature.beans.Item;
import com.revature.beans.Sale;
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
	 * 
	 * @param ctx The context. The body should have the category.
	 */
	public void getItems(Context ctx) {
		log.trace("App has entered getItems");
		log.debug("getItems ctx.body(): " + ctx.body());

		// See if the user is logged in
		User loggedUser = ctx.sessionAttribute("loggedUser");
		log.debug("loggedUser: " + loggedUser);

		// If the user is a customer or not logged in, they shouldn't see the out of
		// stock items
		boolean outOfStock = false;
		if (loggedUser == null || loggedUser.getAccountType().equals(AccountType.CUSTOMER)) {
			outOfStock = true;
		}

		// Check and make sure there is a category to search by.
		Item item = ctx.bodyAsClass(Item.class);
		log.debug("Item from the body: " + item);

		// If the item is null or there is no category
		if (item == null || item.getCategory() == null) {
			ctx.status(406);
			ctx.html("No valid category was passed in");
			log.trace("App is exiting getItems");
			return;
		}

		// Get the list of items and send them back
		List<Item> items = itemService.getItems(item.getCategory(), outOfStock);
		log.debug("items returned: " + items);

		// If the items list returned null
		if (items == null) {
			ctx.status(404);
			ctx.html("No items in that category");
			log.trace("App is exiting getItems");
			return;
		}
		ctx.json(items);
		log.trace("App is exiting getItems");
	}

	/**
	 * Add the item to the inventory
	 * 
	 * @param ctx The context
	 */
	public void addItem(Context ctx) {
		log.trace("App has entered addItem");
		log.debug("getItems ctx.body(): " + ctx.body());

		// See if the user is logged in
		User loggedUser = ctx.sessionAttribute("loggedUser");
		log.debug("loggedUser: " + loggedUser);

		// If the user is not logged in or is not a manager
		if (loggedUser == null || !loggedUser.getAccountType().equals(AccountType.MANAGER)) {
			ctx.status(403);
			log.trace("App is exiting addItem");
			return;
		}

		// Get the item from the body
		Item item = ctx.bodyAsClass(Item.class);
		log.debug("Item from body: " + item);

		// Add the item to the inventory
		item = itemService.addItem(item.getName(), item.getPrice(), item.getAmount(), item.getCategory(),
				item.getDescription());
		log.trace("App is exiting addItem");
		log.debug("Item added to inventory: " + item);

		// If item has missing or incorrect details
		if (item == null) {
			ctx.status(400);
			ctx.html("Item details were not entered correctly");
			log.trace("App is exiting addItem");
			return;
		}

		// Return the item
		ctx.json(item);
		log.trace("App is exiting addItem");
	}

	/**
	 * Change the amount of item in inventory
	 * 
	 * @param ctx The context
	 */
	public void changeAmountOfItem(Context ctx) {
		log.trace("App has entered changeAmountOfItem");
		log.debug("getItems ctx.body(): " + ctx.body());

		// See if the user is logged in
		User loggedUser = ctx.sessionAttribute("loggedUser");
		log.debug("loggedUser: " + loggedUser);
		int itemId = Integer.parseInt(ctx.pathParam("itemId"));
		log.debug("itemId from path: " + itemId);

		// If the user is not logged in or is not a manager
		if (loggedUser == null || !loggedUser.getAccountType().equals(AccountType.MANAGER)) {
			ctx.status(403);
			log.trace("App is exiting changeAmountOfItem");
			return;
		}

		// Get the item from the body
		int amount = ctx.bodyAsClass(Item.class).getAmount();
		log.debug("Quantity from body: " + amount);

		// Get the item from inventory
		Item item = itemService.getItem(itemId);
		log.debug("Item from inventory: " + item);

		// If the item was not found
		if (item == null) {
			ctx.status(404);
			ctx.html("No item was found");
			log.trace("App is exiting changeAmountOfItem");
			return;
		}

		// If the amount was negative
		if (amount < 0) {
			ctx.status(400);
			ctx.html("Amount should be positive or 0");
			log.trace("App is exiting changeAmountOfItem");
			return;
		}

		// Change the amount of the item
		itemService.changeAmount(item, amount);
		log.trace("App has returned to changeAmountOfItem");
		ctx.json(item);
		log.trace("App is exiting changeAmountOfItem");

	}
	
	/**
	 * Put an item on sale
	 * @param ctx The context
	 */
	public void putSaleInItem(Context ctx) {
		log.trace("App has entered putSaleInItem");
		log.debug("getItems ctx.body(): " + ctx.body());

		// See if the user is logged in
		User loggedUser = ctx.sessionAttribute("loggedUser");
		log.debug("loggedUser: " + loggedUser);
		int itemId = Integer.parseInt(ctx.pathParam("itemId"));
		log.debug("itemId from path: " + itemId);

		// If the user is not logged in or is not a manager
		if (loggedUser == null || !loggedUser.getAccountType().equals(AccountType.MANAGER)) {
			ctx.status(403);
			log.trace("App is exiting putSaleInItem");
			return;
		}
		
		//Get the sale
		Sale sale = ctx.bodyAsClass(Sale.class);
		
		//If the sale is null, the price is 0 or negative, or the date is incorrect (null or after today)
		if (sale == null || sale.getSalePrice() <= 0.00 || sale.getEndDate() == null
				|| LocalDate.now().isAfter(sale.getEndDate())) {
			
			ctx.status(400);
			ctx.html("Sale details are incorrect.");
			log.trace("App is exiting putSaleInItem");
		}
		
		// Get the item from inventory
		Item item = itemService.getItem(itemId);
		log.debug("Item from inventory: " + item);

		// If the item was not found
		if (item == null) {
			ctx.status(404);
			ctx.html("No item was found");
			log.trace("App is exiting putSaleInItem");
			return;
		}
		
		//Create the sale
		itemService.setSale(item, sale.getEndDate(), sale.getSalePrice());
		ctx.json(item);
		log.trace("App is exiting putSaleInItem");
	}
	
	/**
	 * End the sale of an item
	 * @param ctx The context
	 */
	public void removeSaleInItem(Context ctx) {
		log.trace("App has entered putSaleInItem");
		log.debug("getItems ctx.body(): " + ctx.body());

		// See if the user is logged in
		User loggedUser = ctx.sessionAttribute("loggedUser");
		log.debug("loggedUser: " + loggedUser);
		int itemId = Integer.parseInt(ctx.pathParam("itemId"));
		log.debug("itemId from path: " + itemId);

		// If the user is not logged in or is not a manager
		if (loggedUser == null || !loggedUser.getAccountType().equals(AccountType.MANAGER)) {
			ctx.status(403);
			log.trace("App is exiting putSaleInItem");
			return;
		}
		
		// Get the item from inventory
		Item item = itemService.getItem(itemId);
		log.debug("Item from inventory: " + item);

		// If the item was not found
		if (item == null) {
			ctx.status(404);
			ctx.html("No item was found");
			log.trace("App is exiting putSaleInItem");
			return;
		}
		
		//Create the sale
		itemService.endSale(item);
		ctx.json(item);
		log.trace("App is exiting putSaleInItem");

	}
	
	/**
	 * Change the price of an item
	 * @param ctx The context
	 */
	public void changePriceOfItem(Context ctx) {
		log.trace("App has entered changePriceOfItem");
		log.debug("getItems ctx.body(): " + ctx.body());

		// See if the user is logged in
		User loggedUser = ctx.sessionAttribute("loggedUser");
		log.debug("loggedUser: " + loggedUser);
		int itemId = Integer.parseInt(ctx.pathParam("itemId"));
		log.debug("itemId from path: " + itemId);

		// If the user is not logged in or is not a manager
		if (loggedUser == null || !loggedUser.getAccountType().equals(AccountType.MANAGER)) {
			ctx.status(403);
			log.trace("App is exiting changePriceOfItem");
			return;
		}

		// Get the item from the body
		double price = ctx.bodyAsClass(Item.class).getPrice();
		log.debug("Price from body: " + price);

		// Get the item from inventory
		Item item = itemService.getItem(itemId);
		log.debug("Item from inventory: " + item);

		// If the item was not found
		if (item == null) {
			ctx.status(404);
			ctx.html("No item was found");
			log.trace("App is exiting changePriceOfItem");
			return;
		}

		// If the amount was negative
		if (price <= 0.0) {
			ctx.status(400);
			ctx.html("Price should be greater than 0");
			log.trace("App is exiting changePriceOfItem");
			return;
		}

		// Change the amount of the item
		itemService.changePrice(item, price);
		log.trace("App has returned to changePriceOfItem");
		ctx.json(item);
		log.trace("App is exiting changePriceOfItem");
	}
}
