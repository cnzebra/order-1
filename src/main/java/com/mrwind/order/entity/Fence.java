package com.mrwind.order.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Fence {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
