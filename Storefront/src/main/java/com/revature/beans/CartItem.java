package com.revature.beans;

import java.io.Serializable;
import java.util.Objects;

public class CartItem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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

	@Override
	public int hashCode() {
		return Objects.hash(item, quantity);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CartItem other = (CartItem) obj;
		return Objects.equals(item, other.item) && quantity == other.quantity;
	}

	@Override
	public String toString() {
		return "CartItem [item=" + item + ", quantity=" + quantity + "]";
	}
	
	
}
