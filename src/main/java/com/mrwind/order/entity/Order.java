package com.mrwind.order.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Order extends OrderBase {
	
	private Date planTime;
	private List<Date> duiTimes;
	
	public List<Date> getDuiTimes() {
		return duiTimes;
	}

	public void setDuiTimes(List<Date> duiTimes) {
		this.duiTimes = duiTimes;
	}

	public Date getPlanTime() {
		return planTime;
	}

	public void setPlanTime(Date planTime) {
		this.planTime = planTime;
	}
}
