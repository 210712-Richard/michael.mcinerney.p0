package com.revature.beans;

/**
 * The current status of the order.
 *
 */
public enum OrderStatus {
	/**
	 * The order has been Ordered.
	 */
	ORDERED,
	/**
	 * The order was cancelled and refunded.
	 */
	CANCELLED,
	/**
	 * The order was refunded, but not cancelled. 
	 * Usually means the customer was refunded after the order was already shipped.
	 */
	REFUNDED,
	/**
	 * The order has shipped and can't be cancelled anymore
	 */
	SHIPPED,
	
}
