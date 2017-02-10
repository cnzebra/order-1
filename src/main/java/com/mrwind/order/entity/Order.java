package com.mrwind.order.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Order extends OrderBase {
	
	
	private List<Date> duiTimes;
	
	private List<Date> unDuiTimes;
	public List<Date> getDuiTimes() {
		return duiTimes;
	}

	public void setDuiTimes(List<Date> duiTimes) {
		this.duiTimes = duiTimes;
	}

	public List<Date> getUnDuiTimes() {
		return unDuiTimes;
	}

	public void setUnDuiTimes(List<Date> unDuiTimes) {
		this.unDuiTimes = unDuiTimes;
	}
}
