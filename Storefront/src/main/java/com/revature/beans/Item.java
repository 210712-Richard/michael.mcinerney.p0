package com.revature.beans;

import java.io.Serializable;
import java.util.Objects;

public class Item implements Serializable{
	private int id;
	private String name;
	private double price;
	private int amountInInventory;
	private ItemCategory category;
	private String description;
	private Sale sale;
	
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
		return Objects.hash(amountInInventory, category, description, id, name);
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
				&& Objects.equals(description, other.description) && id == other.id && Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", amountInInventory=" + amountInInventory + ", category="
				+ category + ", description=" + description + "]";
	}
	
}
