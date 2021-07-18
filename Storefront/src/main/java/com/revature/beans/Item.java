package com.revature.beans;

import java.io.Serializable;
import java.util.Objects;

public class Item implements Serializable{
	private int id;
	private String name;
	private int amountInInventory;
	private ItemCategory category;
	private String description;
	private Sale sale;
	
	public Item(int id, String name, int amountInInventory, ItemCategory category, String description) {
		super();
		this.id = id;
		this.name = name;
		this.amountInInventory = amountInInventory;
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

	public int getAmountInInventory() {
		return amountInInventory;
	}

	public void setAmountInInventory(int amountInInventory) {
		this.amountInInventory = amountInInventory;
	}

	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
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
