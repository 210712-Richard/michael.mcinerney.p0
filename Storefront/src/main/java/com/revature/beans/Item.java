package com.revature.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Item implements Serializable{
	private static final long serialVersionUID = 1L; //Default serial version
	private int id; //The ID of the item
	private String name; //Name of the item
	private double price; //The price of the item
	private int amountInInventory; //The amount in the inventory
	private ItemCategory category; //The category of the item
	private String description; //A description of the item
	private Sale sale; //A sale for the item. Will be null if no sale
	
	public Item() {
		super();
		sale = null;
	}
	public Item(int id, String name, double price, int amountInInventory, ItemCategory category, String description) {
		this();
		this.id = id;
		this.name = name;
		this.price = price;
		this.amountInInventory = amountInInventory;
		if (amountInInventory < 0) {
			this.amountInInventory = 0;
		}
		this.category = category;
		this.description = description;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		if (price <= 0.00) {
			return;
		}
		this.price = price;
	}

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}
	
	public void setSale(LocalDate date, double price) {
		this.sale = new Sale(date, price);
	}

	public int getAmountInInventory() {
		return amountInInventory;
	}

	public void setAmountInInventory(int amountInInventory) {
		if(amountInInventory < 0) {
			return;
		}
		this.amountInInventory = amountInInventory;
	}

	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
		if (category == null) {
			return;
		}
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		return Objects.hash(amountInInventory, category, description, id, name, price, sale);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		return amountInInventory == other.amountInInventory && category == other.category
				&& Objects.equals(description, other.description) && id == other.id && Objects.equals(name, other.name)
				&& Double.doubleToLongBits(price) == Double.doubleToLongBits(other.price)
				&& Objects.equals(sale, other.sale);
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", price=" + price + ", amountInInventory=" + amountInInventory
				+ ", category=" + category + ", description=" + description + ", sale=" + sale + "]";
	}
	
}
