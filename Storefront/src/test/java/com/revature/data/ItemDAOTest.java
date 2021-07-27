package com.revature.data;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.revature.beans.Item;
import com.revature.beans.ItemCategory;

public class ItemDAOTest {
	private ItemDAO dao;
	private static Item item;

	private ArrayList<Item> inv; // Inventory list to make sure methods are called.

	@BeforeAll
	public static void beforeStart() {

	}

	@BeforeEach
	public void beforeTests() {
		dao = new ItemDAO();
		item = new Item(0, "PC Desktop", 2000.00, 5, ItemCategory.DESKTOP_COMPUTER, "A Windows 10 personal computer.");

	}

	@Test
	public void testGetItems() {
		// Make sure that returns a list of items with onlyInStock is true.
		List<Item> retList = dao.getItems(item.getCategory(), true);
		assertFalse(retList.isEmpty(), "Assert that items are returned from the list when outOfStock is true.");

		// Make sure that returns a list of items with onlyInStock is false.
		retList = dao.getItems(item.getCategory(), false);
		assertFalse(retList.isEmpty(), "Assert that items are returned from the list when outOfStock is false.");

		// If item category is null, return an empty list.
		retList = dao.getItems(null, true);
		assertTrue(retList.isEmpty(), "Assert that a null category returns an empty list.");
	}

	@Test
	public void testAddItem() {
		// Create the Item object and add it to the list.
		Item i = new Item(-1, "TestAddItem", 20.00, 10, ItemCategory.COMPUTER_ACCESSORY, "This is a test.");
		Item isAdded = dao.addItem(i);
		
		assertTrue(dao.getInventory().contains(isAdded), "Assert that the item was added to inventory.");

	}
	
	@Test
	public void testGetItem() {
		Item i = dao.getItem(item.getId());
		
		assertEquals(i.getId(), item.getId(), "Assert that the item returned has the same Id");
		
		Item wrongItem = dao.getItem(999);
		assertNull("Assert that an invalid id results in a null item being returned.", wrongItem);
		
	}


}
