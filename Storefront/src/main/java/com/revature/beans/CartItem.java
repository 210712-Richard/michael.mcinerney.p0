package com.revature.beans;

import java.io.Serializable;
import java.util.Objects;

public class CartItem implements Serializable{
	private static final long serialVersionUID = 1L; //Default Serial version
	private int itemId; //The item in the cart
	private int quantity; //The amount of the item in the cart
	private double price; //The price of the item
	
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
	public CartItem(int itemId, int quantity, double price) {
		this();
		this.itemId = itemId;
		this.quantity = quantity;
		this.price = price;
	}
	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
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
		return Objects.hash(itemId, price, quantity);
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
		return itemId == other.itemId && Double.doubleToLongBits(price) == Double.doubleToLongBits(other.price)
				&& quantity == other.quantity;
	}

	@Override
	public String toString() {
		return "CartItem [itemId=" + itemId + ", quantity=" + quantity + ", price=" + price + "]";
	}


	
}
