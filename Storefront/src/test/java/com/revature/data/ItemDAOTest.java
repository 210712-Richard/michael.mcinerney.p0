package com.revature.data;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.revature.beans.Item;
import com.revature.beans.ItemCategory;
import com.revature.util.MockitoHelper;

public class ItemDAOTest {
	private ItemDAO dao;
	private static Item item;

	private ArrayList<Item> inv; // Inventory list to make sure methods are called.
	private static MockitoHelper<ArrayList> mock; // Mock to verify methods. Note: Mockito does not work well with
													// static fields.

	@BeforeAll
	public static void beforeStart() {
		// The default item added to the inventory.

		mock = new MockitoHelper<ArrayList>(ArrayList.class);
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


}
