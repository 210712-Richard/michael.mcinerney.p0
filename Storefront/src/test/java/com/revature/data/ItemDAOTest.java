package com.revature.data;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

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
		// Note: Test only works this way because it comes after testGetItem. If that
		// changes or if more tests are added, will need to comment out Mockito items.
		inv = mock.setPrivateMock(dao, "inventory");
		// Create the Item object and add it to the list.
		Item i = new Item(-1, "TestAddItem", 20.00, 10, ItemCategory.COMPUTER_ACCESSORY, "This is a test.");
		Item isAdded = dao.addItem(i);

		// Verify size and add were called.
		ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
		Mockito.verify(inv).add(captor.capture());
		Mockito.verify(inv).size();

		// Making sure the item matches the one passed in.
		Item capture = captor.getValue(); // Get the method that was passed in.
		assertEquals(i.getName(), capture.getName(), "Assert that item added has the same name.");
		assertEquals(i.getPrice(), capture.getPrice(), "Assert that the item added has the same price.");
		assertEquals(i.getAmountInInventory(), capture.getAmountInInventory(),
				"Assert that the item added has the same amount in inventory.");
		assertEquals(i.getCategory(), capture.getCategory(), "Assert that the item added has the same category.");
		assertEquals(i.getDescription(), capture.getDescription(),
				"Assert that the item added has the same description.");
		assertNull("Assert that there is no sale added to the item.", capture.getSale());

		// Make sure a successful add returns item.
		assertNotNull(isAdded, "Assert that the method returns true upon success.");

		// If the item added was null, it should return null.
		assertNull("Assert that null item returns null", dao.addItem(null));
		
		

	}
}
