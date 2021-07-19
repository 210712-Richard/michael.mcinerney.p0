package com.revature.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Order implements Serializable{
	private List<CartItem> itemsOrdered;
	private LocalDate orderDate;
	private OrderStatus status;
	
	public Order() {
		super();
		status = OrderStatus.ORDERED;
	}
	public Order(List<CartItem> itemsOrdered, LocalDate orderDate) {
		this();
		this.itemsOrdered = itemsOrdered;
		this.orderDate = orderDate;
	}
	public List<CartItem> getItemsOrdered() {
		return itemsOrdered;
	}
	public void setItemsOrdered(List<CartItem> itemsOrdered) {
		this.itemsOrdered = itemsOrdered;
	}
	public LocalDate getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(LocalDate orderDate) {
		this.orderDate = orderDate;
	}
	public OrderStatus getStatus() {
		return status;
	}
	public void setStatus(OrderStatus status) {
		this.status = status;
	}
	@Override
	public int hashCode() {
		return Objects.hash(itemsOrdered, orderDate, status);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		return Objects.equals(itemsOrdered, other.itemsOrdered) && Objects.equals(orderDate, other.orderDate)
				&& status == other.status;
	}
	@Override
	public String toString() {
		return "Order [itemsOrdered=" + itemsOrdered + ", orderDate=" + orderDate + ", status=" + status + "]";
	}
	
}
