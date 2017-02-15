package com.mrwind.order.entity;

import java.math.BigDecimal;

public class ServiceType{
	
	public ServiceType(){}
	private String id;
    private String name;
    private BigDecimal price;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}
