package com.revature.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.AccountType;
import com.revature.beans.Item;
import com.revature.beans.ItemCategory;
import com.revature.beans.User;

public class ItemDAO {
	private static List<Item> inventory;

	private static String filename = "inventory.dat";

	private static final Logger log = LogManager.getLogger(ItemDAO.class); // Used to create logs

	/**
	 * Loads the file. If the file does not exist, will create items to add to the list.
	 */
	static {
		try {
			log.trace("ItemDAO is getting " + filename);
			inventory = new Serializer<Item>().readObjectsFromFile(filename);
		} catch (Exception e) { // Logs the error, the app will continue as usual
			log.warn(filename + " was not found.");
		}
		if (inventory == null) { //If no inventory was found.
			inventory = new ArrayList<Item>();
			inventory.add(new Item(inventory.size(), "PC Desktop", 5, ItemCategory.DESKTOP_COMPUTER,
					"A Windows 10 personal computer."));
			inventory.add(new Item(inventory.size(), "Mac Desktop", 0, ItemCategory.DESKTOP_COMPUTER,
					"A Windows 10 personal computer."));
			inventory.add(new Item(inventory.size(), "MacBook", 5, ItemCategory.LAPTOP_COMPUTER, "An Apple laptop."));
			inventory.add(new Item(inventory.size(), "Mechanical Keyboard", 5, ItemCategory.COMPUTER_ACCESSORY,
					"A heavy-duty keyboard."));
			inventory.add(new Item(inventory.size(), "1TB External HDD", 5, ItemCategory.STORAGE_DEVICE,
					"A portable hard drive with 1 TB in it."));
			inventory.add(new Item(inventory.size(), "144Hz 32\" Monitor", 5, ItemCategory.MONITOR,
					"A 32-inch monitor that runs up to 144 FPS."));
			inventory.add(new Item(inventory.size(), "Windows 10 Home Edition", 5, ItemCategory.SOFTWARE,
					"A copy of Windows 10 for home computers."));
		}
	}

	/**
	 * Gets all items in the inventory.
	 * 
	 * @return Current Inventory
	 */
	public List<Item> getInventory() {
		return inventory;
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
		for (Item item : inventory) { // Loop through each item in the inventory
			// If the item belongs to the correct category and is either in stock or
			// onlyInStock is false.
			if (item.getCategory() == category && (!onlyInStock || item.getAmountInInventory() > 0)) {
				log.debug("Item added to list: " + item);
				retList.add(item); // Add the item to the return list.
			}
		}
		log.trace("App is exiting getItems.");
		log.debug("App is returning List<Item>: " + retList);
		return retList; // Return the list.
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