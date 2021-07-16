package com.revature.models;

import java.io.Serializable;

public class CartItem implements Serializable{
	private Item item;
	private int quantity;
	
	public CartItem() {
		super();
		quantity = 1;
	}
	
	public CartItem(Item item) {
		this();
		this.item = item;
	}
	public CartItem(Item item, int quantity) {
		super();
		this.item = item;
		this.quantity = quantity;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		if (quantity <= 0) {
			return;
		}
		this.quantity = quantity;
	}
	
	
}
