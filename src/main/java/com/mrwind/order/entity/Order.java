package com.mrwind.order.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Order extends OrderBase {
	
	private String bindExpressNo;
	
	private List<Date> dueTimes;

	public List<Date> getDueTimes() {
		return dueTimes;
	}

	public void setDueTimes(List<Date> dueTimes) {
		this.dueTimes = dueTimes;
	}

	public String getBindExpressNo() {
		return bindExpressNo;
	}

	public void setBindExpressNo(String bindExpressNo) {
		this.bindExpressNo = bindExpressNo;
	}

}
