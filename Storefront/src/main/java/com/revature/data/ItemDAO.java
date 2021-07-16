package com.revature.data;

import java.util.ArrayList;
import java.util.List;

import com.revature.models.AccountType;
import com.revature.models.Item;
import com.revature.models.ItemCategory;
import com.revature.models.User;

public class ItemDAO {
	private static List<Item> inventory;
	
	private static String filename = "inventory.dat";
	
	
	static {
		inventory = new Serializer<Item>().readObjectsFromFile(filename);
		if(inventory == null) {
			inventory = new ArrayList<Item>();
			inventory.add(new Item(inventory.size(), "PC Desktop", 5, ItemCategory.DESKTOP_COMPUTER, "A Windows 10 personal computer."));
			inventory.add(new Item(inventory.size(), "MacBook", 5, ItemCategory.LAPTOP_COMPUTER, "An Apple laptop."));
			inventory.add(new Item(inventory.size(), "Mechanical Keyboard", 5, ItemCategory.COMPUTER_ACCESSORY, "A heavy-duty keyboard."));
			inventory.add(new Item(inventory.size(), "1TB External HDD", 5, ItemCategory.STORAGE_DEVICE, "A portable hard drive with 1 TB in it."));
			inventory.add(new Item(inventory.size(), "144Hz 32\" Monitor", 5, ItemCategory.MONITOR, "A 32-inch monitor that runs up to 144 FPS."));
			inventory.add(new Item(inventory.size(), "Windows 10 Home Edition", 5, ItemCategory.SOFTWARE, "A copy of Windows 10 for home computers."));
		}
	}
	
	/**
	 * Gets all items in the inventory.
	 * @return
	 */
	public List<Item> getInventory(){
		return inventory;
	}
	
	public List<Item> getItems(ItemCategory category, boolean onlyInStock){
		ArrayList<Item> retList = new ArrayList<Item>();
		for (Item item : inventory) {
			if (item.getCategory() == category && (!onlyInStock || item.getAmountInInventory() > 0)) {
				retList.add(item);
			}
		}
		return retList;
	}
	
	public void writeToFile() {
		new Serializer<Item>().writeObjectsToFile(inventory, filename);
	}
}