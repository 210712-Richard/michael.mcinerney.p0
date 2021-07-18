package com.revature.services;

import java.util.List;

import com.revature.beans.Item;
import com.revature.beans.ItemCategory;
import com.revature.data.ItemDAO;

public class ItemService {
	private ItemDAO iDAO = new ItemDAO();
	
	public List<Item> getItems(ItemCategory category, boolean isOutOfStock){
		return null;
	}
	
	public Item updateItem(Item item) {
		return null;
	}
	
	public Item addItem(String name, double price, int amount, ItemCategory category, String desc) {
		return null;
	}
}
