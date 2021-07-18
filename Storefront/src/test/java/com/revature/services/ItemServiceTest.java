package com.revature.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.revature.data.ItemDAO;
import com.revature.util.MockitoHelper;

public class ItemServiceTest {
	private ItemService service;
	
	private static MockitoHelper<ItemDAO> mock; //Used to create the mock used for the ItemDAO.
	private ItemDAO dao; //Used to verify ItemDAO methods are called.
	
	@BeforeAll
	public static void beforeStart() {
		mock = new MockitoHelper<ItemDAO>(ItemDAO.class);
	}
	
	@BeforeEach
	public void beforeTests() {
		service = new ItemService();
	}
	
	@Test
	public void testGetItemsReturnsValidList() {
		//TODO: Get a list of items given a category and whether or not to include out of stock items.
	}
	
	@Test
	public void testGetItemsReturnsInvalidList() {
		//TODO: Should return null if any field given is null.
	}
	
	@Test
	public void testGetItemsCallsMethods() {
		//TODO: Get item should call ItemDAO methods writeToFile() and getItems(ItemCategory, boolean) on correct items
	}
	
	@Test
	public void testAddItemReturnsValidItem() {
		//TODO: addItem should return Item if valid parameters were sent.
		
	}
	
	@Test
	public void testAddItemReturnsInvalidItem() {
		//TODO: addItem should return Item if any parameter is null or (for Strings) blank.
	}
	
	@Test
	public void testAddItemCallsMethods() {
		//TODO: addItem should call writeToFile() and addItem from ItemDAO(Item)
	}
	
	@Test
	public void testEditItem() {
		//TODO: editItem should return Item and should call writeToFile();
	}
}
