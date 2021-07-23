package com.revature.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.Item;
import com.revature.beans.ItemCategory;

public class ItemDAO {
	private static List<Item> inventory; //All the items in the inventory
 
	private static String filename = "inventory.dat"; //The file where inventory is saved.

	private static final Logger log = LogManager.getLogger(ItemDAO.class); // Used to create logs

	/**
	 * Loads the file. If the file does not exist, will create items to add to the
	 * list.
	 */
	static {
		try {
			log.trace("ItemDAO is getting " + filename);
			inventory = new Serializer<Item>().readObjectsFromFile(filename);
		} catch (Exception e) { // Logs the error, the app will continue as usual
			log.warn(filename + " was not found.");
		}
		if (inventory == null) { // If no inventory was found.
			inventory = new ArrayList<Item>();
			inventory.add(new Item(inventory.size(), "PC Desktop", 2000.00, 5, ItemCategory.DESKTOP_COMPUTER,
					"A Windows 10 personal computer."));
			inventory.add(new Item(inventory.size(), "Mac Desktop", 1000.00, 0, ItemCategory.DESKTOP_COMPUTER,
					"An Apple Desktop."));
			inventory.add(new Item(inventory.size(), "MacBook", 1500.00, 5, ItemCategory.LAPTOP_COMPUTER,
					"An Apple laptop."));
			inventory.add(new Item(inventory.size(), "Mechanical Keyboard", 150.00, 5, ItemCategory.COMPUTER_ACCESSORY,
					"A heavy-duty keyboard."));
			inventory.add(new Item(inventory.size(), "1TB External HDD", 80.00, 5, ItemCategory.STORAGE_DEVICE,
					"A portable hard drive with 1 TB in it."));
			inventory.add(new Item(inventory.size(), "144Hz 32\" Monitor", 130.00, 5, ItemCategory.MONITOR,
					"A 32-inch monitor that runs up to 144 FPS."));
			inventory.add(new Item(inventory.size(), "Windows 10 Home Edition", 90.00, 5, ItemCategory.SOFTWARE,
					"A copy of Windows 10 for home computers."));
		}

		// This will loop through each item, and if the sale is past, set the Sale to
		// null
		inventory.stream()
				.filter((item) -> item.getSale() != null && item.getSale().getEndDate().isBefore(LocalDate.now()))
				.forEach((item) -> item.setSale(null));
	}

	/**
	 * Gets all items in the inventory.
	 * 
	 * @return Current inventory
	 */
	public List<Item> getInventory() {
		return inventory;
	}
	
	public Item getItem(int itemId) {
		return inventory.stream()
				.filter((item)->item.getId() == itemId)
				.findFirst()
				.orElse(null);
	}

	/**
	 * Gets all items for a specified category. Will return out of stock items based
	 * on flag passed in.
	 * 
	 * @param category    The category of items to return.
	 * @param onlyInStock true means any items with no stock will not be returned,
	 *                    false means all items regardless of inventory will be
	 *                    returned.
	 * @return List of items
	 */
	public List<Item> getItems(ItemCategory category, boolean onlyInStock) {
		log.trace("App has entered getItems.");
		log.debug("getItems Parameters: category: " + category + ", onlyInStock: " + onlyInStock);
		ArrayList<Item> retList = new ArrayList<Item>(); // Initialize a new list to return.
		if (category != null) { // If the category passed in is null.
			for (Item item : inventory) { // Loop through each item in the inventory
				// If the item belongs to the correct category and is either in stock or
				// onlyInStock is false.
				if (item.getCategory() == category && (!onlyInStock || item.getAmountInInventory() > 0)) {
					log.debug("Item added to list: " + item);
					retList.add(item); // Add the item to the return list.
				}
			}
		}
		log.trace("App is exiting getItems.");
		log.debug("App is returning List<Item>: " + retList);
		return retList; // Return the list.
	}

	/**
	 * Add an item to the inventory
	 * 
	 * @param item The item being added to the inventory
	 * @return The item added to inventory.
	 */
	public Item addItem(Item item) {
		log.trace("App has entered addItem.");
		log.debug("addItem parameters: item: " + item);
		if (item == null) { // If item is null, return null.
			log.warn("App tried to enter a null Item.");
			log.trace("App is now exiting addItem.");
			log.debug("addItem is returning Item: " + null);
			return null;
		}
		item.setId(inventory.size());
		log.debug("Parameter item has changed to :" + item);
		inventory.add(item);
		log.debug("Inventory list has correctly added the item to the list: " + inventory.contains(item));
		log.trace("App is now exiting addItem.");
		log.debug("addItem is returning Item: " + item);
		return item;

	}
	
	/**
	 * Returns items back to the inventory
	 * @param itemId The ID of the item
	 * @param quantity The quantity being put back
	 */
	public void addAmountToInventory(int itemId, int quantity) {
		log.trace("User has entered addAmountoInventory");
		log.debug("addAmountToInventory parameters: itemId: " + itemId + ", quantity: " + quantity);
		Item item = getItem(itemId); //Get the item using the itemId
		
		//If the item is not null and the quantity is a non-negative number
		if (item != null && quantity >= 0) {
			//Adds the quantity to the current amount in inventory
			item.setAmountInInventory(item.getAmountInInventory() + quantity);
		}
		writeToFile();
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
		Item item = getItem(itemId); //Get the actual item with the itemId
		
		//If the item is no not null, and the quantity is greater than zero but less than the current inventory
		if (item != null && quantity > 0 && quantity <= item.getAmountInInventory()) {
			
			//Subtract the quantity from the amount in inventory already
			item.setAmountInInventory(item.getAmountInInventory() - quantity);
			log.debug("item quantity was set to " + item.getAmountInInventory());
		}
		writeToFile();
		log.trace("App has returned to removeAmountFromInventory.");
		log.trace("App is exiting removeAmountFromInventory.");
	}

	/**
	 * Save to the inventory file.
	 */
	public void writeToFile() {
		log.trace("App has entered writeToFile.");
		new Serializer<Item>().writeObjectsToFile(inventory, filename);
		log.trace("App is exiting writeToFile");
	}
}