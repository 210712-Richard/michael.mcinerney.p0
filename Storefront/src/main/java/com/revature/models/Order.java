package com.revature.models;

import java.time.LocalDate;
import java.util.List;

public class Order {
	private List<CartItem> itemsOrdered;
	private LocalDate orderDate;
	private OrderStatus status;
}
