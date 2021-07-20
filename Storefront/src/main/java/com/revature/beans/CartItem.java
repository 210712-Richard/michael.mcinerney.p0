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
	private double price;
	
	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public CartItem() {
		super();
		quantity = 1;
	}
	
	public CartItem(Item item) {
		this();
		this.item = item;
	}
	public CartItem(Item item, int quantity) {
		this();
		this.item = item;
		this.quantity = quantity;
		this.price = item.getPrice();
	}
	
	public CartItem(Item item, int quantity, double price) {
		this();
		this.item = item;
		this.quantity = quantity;
		this.price = price;
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
		return Objects.hash(item, price, quantity);
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
		return Objects.equals(item, other.item)
				&& Double.doubleToLongBits(price) == Double.doubleToLongBits(other.price) && quantity == other.quantity;
	}

	@Override
	public String toString() {
		return "CartItem [item=" + item + ", quantity=" + quantity + ", price=" + price + "]";
	}


	
}
