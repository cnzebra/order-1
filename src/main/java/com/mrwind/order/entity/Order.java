package com.mrwind.order.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Order {
	
	private long id;
	private String type;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
