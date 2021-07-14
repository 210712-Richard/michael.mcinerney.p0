package com.revature.models;

/**
 * The current status of the order.
 * @author Michael McInerney
 *
 */
public enum OrderStatus {
	/**
	 * The order has been processed.
	 */
	ORDERED,
	/**
	 * The order was cancelled and refunded.
	 */
	CANCELLED,
	/**
	 * The order was refunded, but not cancelled. 
	 * Usually means the customer was refunded after the order was already processed.
	 */
	REFUNDED
}
