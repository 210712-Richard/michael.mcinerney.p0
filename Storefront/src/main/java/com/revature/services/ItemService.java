package com.revature.services;

import java.time.LocalDate;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.Item;
import com.revature.beans.ItemCategory;
import com.revature.beans.Sale;
import com.revature.data.ItemDAO;


public class ItemService {
	private ItemDAO iDAO = new ItemDAO(); // Used to get the items and write to file.
	private static final Logger log = LogManager.getLogger(ItemService.class); // Used to create logs
	
	
	public Item getItem(int itemId) {
		log.trace("App is now in getItem");
		return iDAO.getItem(itemId);
	}
	/**
	 * Get a list of items from a particular category.
	 * 
	 * @param category     The category to look up.
	 * @param isOutOfStock false means to include out of stock items, true means
	 *                     otherwise.
	 * @return A list of items from the category
	 */
	public List<Item> getItems(ItemCategory category, boolean onlyInStock) {
		log.trace("App has entered getItems");
		log.debug("getItems parameters: category: " + category + ", isOutOfStock: " + onlyInStock);
		List<Item> retList = iDAO.getItems(category, onlyInStock); // Get the list from the DAO.
		iDAO.writeToFile(); // Save the file.

		// If the list returned is empty
		if (retList.isEmpty()) {
			retList = null;
			log.debug("retList has been changed to: " + retList);
		}
		log.trace("App has returned to getItems.");
		log.trace("App is exiting getItems.");
		log.debug("getItems returning List<Item>: " + retList);
		return retList; // Return the list back.
	}

	/**
	 * Saves the list. Note: saves the whole list, does not save the item
	 * individually.
	 * 
	 * @param item The item that is being saved.
	 * @return The item that was saved.
	 */
	public Item updateItem(Item item) {
		log.trace("App has entered updateItem.");
		log.debug("addItem parameters: item: " + item);
		if(item == null) {
			log.warn("item entered was null");
			log.trace("App is exiting updateItem.");
			log.debug("updateItem returning Item: " + item);
			return null;
		}
		log.trace("App has entered updateItem.");
		log.debug("updateItem parameters: item: " + item);
		iDAO.writeToFile(); // Save the file
		log.trace("App has returned to updateItem.");
		log.trace("App is exiting updateItem.");
		log.debug("updateItem returning Item: " + item);
		return item;
	}

	/**
	 * Add an item to the inventory
	 * @param name The name of the item.
	 * @param price The price of the item.
	 * @param amount How much of the item is available for purchase.
	 * @param category The category of the item.
	 * @param desc A description of the item.
	 * @return The new item.
	 */
	public Item addItem(String name, double price, int amount, ItemCategory category, String desc) {
		log.trace("App has entered addItem.");
		log.debug("addItem parameters: name: " + name + ", price: " + price + ", amount: " + amount + ", category: "
				+ category + ", desc: " + desc);
		Item retItem = null;
		// If the name is null or blank, or if the price is less than or equal to zero,
		// or the amount is less than 0, or the category is null, or the description is
		// null
		if (name == null || name.isBlank() || price <= 0.0 || amount < 0 || category == null || desc == null) {
			log.warn("One of the parameters is incorrect.");
			log.trace("App is exiting addItem.");
			log.debug("addItem returning Item: " + retItem);
			return retItem;
		}
		retItem = iDAO.addItem(new Item(-1, name, price, amount, category, desc)); //Add the item to the DAO.
		log.trace("App has returned to addItem");
		iDAO.writeToFile();
		log.trace("App has returned to addItem");
		log.trace("App is exiting addItem.");
		log.debug("addItem returning Item: " + retItem);
		return retItem;
	}
	
	/**
	 * Returns items back to the inventory
	 * @param itemId The ID of the item
	 * @param quantity The quantity being put back
	 */
	public void addAmountToInventory(int itemId, int quantity) {
		log.trace("User has entered addAmountoInventory");
		log.debug("addAmountToInventory parameters: itemId: " + itemId + ", quantity: " + quantity);
		Item item = iDAO.getItem(itemId); //Get the item using the itemId
		
		//If the item is not null and the quantity is a non-negative number
		if (item != null && quantity >= 0) {
			//Adds the quantity to the current amount in inventory
			item.setAmountInInventory(item.getAmountInInventory() + quantity);
		}
		iDAO.writeToFile();
		log.trace("User is exiting addAmountToInventory.");
	}
	
	/**
	 * Removes a specified amount from an item's inventory
	 * @param itemId The ID of the item
	 * @param quantity The quantity being taken out
	 */
	public void removeAmountFromInventory(int itemId, int quantity) {
		log.trace("User has entered removeAmountFromInventory");
		log.debug("removeAmountFromInventory parameters: itemId: " + itemId + ", quantity: " + quantity);
		Item item = iDAO.getItem(itemId); //Get the actual item with the itemId
		
		//If the item is no not null, and the quantity is greater than zero but less than the current inventory
		if (item != null && quantity > 0 && quantity <= item.getAmountInInventory()) {
			
			//Subtract the quantity from the amount in inventory already
			item.setAmountInInventory(item.getAmountInInventory() - quantity);
			log.debug("item quantity was set to " + item.getAmountInInventory());
		}
		iDAO.writeToFile();
		log.trace("App has returned to removeAmountFromInventory.");
		log.trace("App is exiting removeAmountFromInventory.");
	}
	
	/**
	 * Ends the sale of an Item
	 * @param item The Item with the sale ending
	 */
	public void endSale(Item item) {
		log.trace("User has entered endSale");
		log.debug("endSale parameters: item: " + item);
		
		//If the item is not null
		if (item != null) {
			//Gets rid of the sale
			item.setSale(null);
			log.debug("item Sale has been set to " + item.getSale());
		}
		iDAO.writeToFile();
		log.trace("App has returned to endSale.");
		log.trace("App is exiting endSale.");
	}
	
	/**
	 * Adds a new sale to an Item
	 * @param item The Item getting the sale
	 * @param endDate The final date of the Sale
	 * @param price The price of the item during the Sale
	 */
	public void setSale(Item item, LocalDate endDate, double price) {
		log.trace("User has entered setSale");
		log.debug("setSale parameters: item: " + item, ", endDate: ", endDate, ", price: ", price);
		
		//If the item is not null, the endDate is not null, and the price is greater than zero
		if (item != null && endDate != null && price > 0.0) {
			//Adds the sale
			item.setSale(new Sale(endDate, price));
			log.debug("saleItem sale has been set to " + item.getSale());
		}
		iDAO.writeToFile();
		log.trace("App has returned to setSale.");
		log.trace("App is exiting setSale.");
	}
	
	/**
	 * Changes the amount of an Item
	 * @param item The Item being changed
	 * @param newQuantity The quantity the Item is being set to
	 */
	public void changeAmount(Item item, int newQuantity) {
		log.trace("User has entered changeAmount");
		log.debug("endSale parameters: item: " + item, ", newQuantity: "+ newQuantity);
		
		//If the item is not null and is getting a non-negative quantity
		if (item != null && newQuantity >= 0) {
			//Sets the amount
			item.setAmountInInventory(newQuantity);
			log.debug("item amountInInventory changed to " + item.getAmountInInventory());
		}
		iDAO.writeToFile();
		log.trace("App has returned to changeAmount.");
		log.trace("App is exiting changeAmount.");
	}
}
