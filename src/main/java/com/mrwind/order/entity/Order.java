package com.mrwind.order.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Order extends OrderBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8853475781658025423L;
	/**
	 * 
	 */
	private List<Date> dueTimes;

	public List<Date> getDueTimes() {
		return dueTimes;
	}

	public void setDueTimes(List<Date> dueTimes) {
		this.dueTimes = dueTimes;
	}
}
