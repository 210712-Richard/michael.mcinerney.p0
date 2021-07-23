package com.revature.beans;

import java.io.Serializable;
import java.util.Objects;

public class CartItem implements Serializable{
	private static final long serialVersionUID = 1L; //Default Serial version
	private int id;
	private Item item; //The item in the cart
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

	
	public CartItem(int id, Item item, int quantity, double price) {
		super();
		this.id = id;
		this.item = item;
		this.quantity = quantity;
		this.price = price;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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
		return Objects.hash(id, item, price, quantity);
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
		return id == other.id && Objects.equals(item, other.item)
				&& Double.doubleToLongBits(price) == Double.doubleToLongBits(other.price) && quantity == other.quantity;
	}

	@Override
	public String toString() {
		return "CartItem [id=" + id + ", item=" + item + ", quantity=" + quantity + ", price=" + price + "]";
	}


	
}
