package com.mrwind.order.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Order extends OrderBase {
	
	private List<Date> duiTimes;
	
	public List<Date> getDuiTimes() {
		return duiTimes;
	}

	public void setDuiTimes(List<Date> duiTimes) {
		this.duiTimes = duiTimes;
	}

}
