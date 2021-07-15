package com.revature.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class Order implements Serializable{
	private List<CartItem> itemsOrdered;
	private LocalDate orderDate;
	private OrderStatus status;
}
