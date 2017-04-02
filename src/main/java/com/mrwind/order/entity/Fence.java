package com.mrwind.order.entity;

import java.io.Serializable;

public class Fence implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7447875263060014604L;
	private String id;
	private String name;

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
}
