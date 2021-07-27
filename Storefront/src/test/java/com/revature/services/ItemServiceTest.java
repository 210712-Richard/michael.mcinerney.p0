package com.revature.services;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.revature.beans.Item;
import com.revature.beans.ItemCategory;
import com.revature.beans.Sale;
import com.revature.data.ItemDAO;
import com.revature.data.UserDAO;
import com.revature.util.MockitoHelper;

public class ItemServiceTest {
	private ItemService service;
	private static Item item;

	private static MockitoHelper<ItemDAO> mock; // Used to create the mock used for the ItemDAO.
	private ItemDAO dao; // Used to verify ItemDAO methods are called.
	private UserService userService;


	@BeforeAll
	public static void beforeStart() {
		mock = new MockitoHelper<ItemDAO>(ItemDAO.class);
		item = new Item(0, "PC Desktop", 2000.00, 5, ItemCategory.DESKTOP_COMPUTER, "A Windows 10 personal computer.");

	}

	@BeforeEach
	public void beforeTests() {
		service = new ItemService();

	}

	@Test
	public void testGetItemsReturnsValidList() {
		// Get a list of items given a category and whether or not to include out of
		// stock items.
		List<Item> falseList = service.getItems(ItemCategory.DESKTOP_COMPUTER, false);
		assertFalse(falseList.isEmpty(), "Assert that list is returned with out of stock items.");
		List<Item> trueList = service.getItems(ItemCategory.DESKTOP_COMPUTER, true);
		assertFalse(trueList.isEmpty(), "Assert that list is returned without out of stock items.");

		assertTrue(falseList.size() > trueList.size(),
				"Assert that list that returns no out of stock items is smaller than one that does.");
	}

	@Test
	public void testGetItemsReturnsInvalidList() {
		// Should return null if category given is null.
		List<Item> nullTrueList = service.getItems(null, true);
		assertNull("Assert that null category returns a null list.", nullTrueList);

	}

	@Test
	public void testGetItemsCallsMethods() {
		// Get item should call ItemDAO methods writeToFile() and getItems(ItemCategory,
		// boolean) on correct items

		// Set the parameters
		ItemCategory category = ItemCategory.DESKTOP_COMPUTER;
		Boolean bool = true;
		// Set the mock ItemDAO and call the function
		dao = mock.setPrivateMock(service, "iDAO");
		service.getItems(category, bool);

		// Setup argument captors to compare.
		ArgumentCaptor<ItemCategory> categoryCaptor = ArgumentCaptor.forClass(ItemCategory.class);
		ArgumentCaptor<Boolean> boolCaptor = ArgumentCaptor.forClass(Boolean.class);

		// Verify methods are called.
		Mockito.verify(dao).writeToFile();
		Mockito.verify(dao).getItems(categoryCaptor.capture(), boolCaptor.capture());

		// Make sure the parameters originally passed in are the same as the ones passed
		// to ItemDAO
		ItemCategory categoryCapture = categoryCaptor.getValue();
		Boolean boolCapture = boolCaptor.getValue();
		assertEquals(categoryCapture, category, "Assert that category remains the same.");
		assertEquals(boolCapture, bool, "Assert that outOfStock remains the same.");
	}

	@Test
	public void testAddItemReturnsValidItem() {
		// addItem should return Item if valid parameters were sent.
		String name = "TestAddItem";
		double price = 20.00;
		int amount = 10;
		ItemCategory category = ItemCategory.COMPUTER_ACCESSORY;
		String desc = "This is a test for add item.";

		// Call function and get the item added.
		Item i = service.addItem(name, price, amount, category, desc);

		// Make sure that the item created has the same fields as the parameters.
		assertEquals(i.getName(), name, "Assert that name remains the same.");
		assertEquals(i.getPrice(), price, "Assert that price remains the same.");
		assertEquals(i.getAmount(), amount, "Assert that amount remains the same.");
		assertEquals(i.getCategory(), category, "Assert that the category remains the same.");
		assertEquals(i.getDescription(), desc, "Assert that the description remains the same.");
		assertNull("Assert that the sale object is null.", i.getSale());
		

	}

	@Test
	public void testAddItemReturnsInvalidItem() {
		// addItem should return Item if any parameter is null or (for Strings)
		// blank.
		String name = "TestAddItem";
		double price = 20.00;
		int amount = 10;
		ItemCategory category = ItemCategory.COMPUTER_ACCESSORY;
		String desc = "This is a test for add item.";

		// Null and blank usernames should return null.
		Item i = service.addItem("   ", price, amount, category, desc);
		assertNull("Assert blank username returns null.", i);
		i = service.addItem(null, price, amount, category, desc);
		assertNull("Assert null username returns null.", i);

		// Less than and equal to zero price and negative amount returns null.
		i = service.addItem(name, -1.00, amount, category, desc);
		assertNull("Assert price less than zero returns null.", i);
		i = service.addItem(name, 0.00, amount, category, desc);
		assertNull("Assert price equal to zero returns null.", i);
		i = service.addItem(name, price, -1, category, desc);
		assertNull("Assert negative amount returns null.", i);

		// Assert that null category and null description returns null
		i = service.addItem(name, price, amount, null, desc);
		assertNull("Assert null category returns null.", i);
		i = service.addItem(name, price, amount, category, null);
		assertNull("Assert null description returns null.", i);
	}

	@Test
	public void testAddItemCallsMethods() {
		// addItem should call writeToFile() and addItem from ItemDAO(Item)
		dao = mock.setPrivateMock(service, "iDAO");
		String name = "TestAddItem";
		double price = 20.00;
		int amount = 10;
		ItemCategory category = ItemCategory.COMPUTER_ACCESSORY;
		String desc = "This is a test for add item.";
		service.addItem(name, price, amount, category, desc);

		// Verify the methods are called and get the parameters for the called methods.
		ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
		Mockito.verify(dao).addItem(captor.capture());
		Mockito.verify(dao).writeToFile();

		// Make sure that the item passed in has the same fields as the one passed in.
		Item i = captor.getValue();
		assertEquals(i.getName(), name, "Assert that name remains the same.");
		assertEquals(i.getPrice(), price, "Assert that price remains the same.");
		assertEquals(i.getAmount(), amount, "Assert that amount remains the same.");
		assertEquals(i.getCategory(), category, "Assert that the category remains the same.");
		assertEquals(i.getDescription(), desc, "Assert that the description remains the same.");

	}


	
	@Test
	public void testGetItem() {
		int id = item.getId();
		Item retItem = service.getItem(id);
		assertEquals(item.getId(), retItem.getId(), "Assert that the item returned equals the one with the id that was passed in.");
		
		Item nullItem = service.getItem(-1);
		assertNull("Assert that an invalid id results in a null Item.", nullItem);
		dao = mock.setPrivateMock(service, "iDAO");
		
		service.getItem(id);
		
		ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
		
		Mockito.verify(dao).getItem(captor.capture());
		
		assertEquals(id, captor.getValue(), "Assert that the number passed in is the same");
	}
	
	@Test
	public void testAddAmountToInventory() {
		//Mockito verification for getItem and writeToFile
		dao = mock.setPrivateMock(service, "iDAO");
		service.addAmountToInventory(item.getId(), 1);
		
		ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
		Mockito.verify(dao).getItem(captor.capture());
		Mockito.verify(dao).writeToFile();
		
		assertEquals(captor.getValue(), item.getId(), "Assert that Id passed in is the same as the one used to get item");
	}
	
	@Test
	public void testRemoveAmountFromInventory() {
		//Mockito verification for getItem and writeToFile
				dao = mock.setPrivateMock(service, "iDAO");
				service.removeAmountFromInventory(item.getId(), 1);
				
				ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
				Mockito.verify(dao).getItem(captor.capture());
				Mockito.verify(dao).writeToFile();
				
				assertEquals(captor.getValue(), item.getId(), "Assert that Id passed in is the same as the one used to get item");
	}
	
	@Test
	public void testEndSale() {
		dao = mock.setPrivateMock(service, "iDAO");
		userService = new MockitoHelper<UserService>(UserService.class).setPrivateMock(service, "userService");
		item.setSale(new Sale());
		service.endSale(item);
		
		ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
		ArgumentCaptor<Sale> saleCaptor = ArgumentCaptor.forClass(Sale.class);
		Mockito.verify(dao).writeToFile();
		Mockito.verify(userService).setSaleInCarts(idCaptor.capture(), saleCaptor.capture());
		
		assertNull("Assert that the Sale was set to null", item.getSale());
		assertNull("Assert that the Sale passed to UserDAO was null", saleCaptor.getValue());
		assertEquals(idCaptor.getValue(), item.getId(), "Assert that the id passed into UserDAO was the item's id.");

	}
	
	@Test
	public void testSetSale() {
		dao = mock.setPrivateMock(service, "iDAO");
		userService = new MockitoHelper<UserService>(UserService.class).setPrivateMock(service, "userService");

		double price = 20.00;
		LocalDate endDate = LocalDate.now();
		service.setSale(item, endDate, price);
		
		ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
		ArgumentCaptor<Sale> saleCaptor = ArgumentCaptor.forClass(Sale.class);
		Mockito.verify(dao).writeToFile();
		Mockito.verify(userService).setSaleInCarts(idCaptor.capture(), saleCaptor.capture());

		
		assertEquals(price, item.getSale().getSalePrice(), "Assert that the sale price set is the same as the one entered.");
		assertEquals(endDate, item.getSale().getEndDate(), "Assert that the end date set is the same as the one entered");
		
		//Make sure values passed in are correct
		assertEquals(idCaptor.getValue(), item.getId(), "Assert that the id passed into UserDAO was the item's id.");
		assertEquals(saleCaptor.getValue(), item.getSale(), "Assert that the id passed into UserDAO was the item's id.");
		
		item.setSale(null);
		service.setSale(item, null, price);
		assertNull("Assert that the Sale was set to null with a null date", item.getSale());
		
		service.setSale(item, endDate, 0.0);
		assertNull("Assert that the Sale was set to null with a 0.0 price", item.getSale());

	}
	
	@Test
	public void testChangeAmount() {
		dao = mock.setPrivateMock(service, "iDAO");
		int newQuantity = 10;
		service.changeAmount(item, newQuantity);
		
		assertEquals(item.getAmount(), newQuantity, "Assert that the amount in inventory is the same as the one entered.");
		
		Mockito.verify(dao).writeToFile();
		
		service.changeAmount(item, -1);
		assertEquals(item.getAmount(), newQuantity, "Assert that the amount in inventory did not change with invalid quantity.");

	}
}
