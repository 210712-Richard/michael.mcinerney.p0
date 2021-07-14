package com.revature.models;

/**
 * The type of the account the user has
 * @author Michael McInerney
 *
 */
public enum AccountType {
	/**
	 * Customers can order items and modify the items in their cart, but not the ones out of their cart
	 */
	CUSTOMER,
	/**
	 * Managers can edit item prices and item quantities, add sales, and deactivate user accounts
	 */
	MANAGER,
	/**
	 * Administrators can create and deactive manager accounts
	 */
	ADMINISTRATOR
}
