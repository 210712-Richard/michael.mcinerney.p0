package com.revature.beans;

import java.io.Serializable;
import java.time.LocalDate;

public class Sale implements Serializable{
	private static final long serialVersionUID = 1L;
	private LocalDate endDate; //The last date for the sale
	private double salePrice;
	
	public Sale() {
		super();
		endDate = LocalDate.now();
		salePrice = 0.0;
	}
	public Sale(LocalDate endDate, double salePrice) {
		this();
		this.endDate = endDate;
		this.salePrice = salePrice;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	public double getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}
}
