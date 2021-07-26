package com.revature.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Order implements Serializable{
	private static final long serialVersionUID = 1L;
	private int id;
	private List<CartItem> itemsOrdered;
	private LocalDate orderDate;
	private LocalDate shipDate;
	private OrderStatus status;
	private double total;
	
	public Order() {
		super();
		status = OrderStatus.ORDERED;
	}
	public Order(int id, List<CartItem> itemsOrdered, LocalDate orderDate, LocalDate shipDate, double total) {
		this();
		this.id = id;
		this.itemsOrdered = itemsOrdered;
		this.orderDate = orderDate;
		this.shipDate = shipDate;
		this.total = total;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public LocalDate getShipDate() {
		return shipDate;
	}
	public void setShippedDate(LocalDate shipDate) {
		this.shipDate = shipDate;
	}
	public OrderStatus getStatus() {
		return status;
	}
	public void setStatus(OrderStatus status) {
		this.status = status;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public void setShipDate(LocalDate shipDate) {
		this.shipDate = shipDate;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id, itemsOrdered, orderDate, shipDate, status, total);
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
		return id == other.id && Objects.equals(itemsOrdered, other.itemsOrdered)
				&& Objects.equals(orderDate, other.orderDate) && Objects.equals(shipDate, other.shipDate)
				&& status == other.status && Double.doubleToLongBits(total) == Double.doubleToLongBits(other.total);
	}
	@Override
	public String toString() {
		return "Order [id=" + id + ", itemsOrdered=" + itemsOrdered + ", orderDate=" + orderDate + ", shipDate="
				+ shipDate + ", status=" + status + ", total=" + total + "]";
	}
	
}
