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

	private String origin = "批量导入";
	/**
	 * 
	 */
	private List<Date> dueTimes;

	private String expressNo;

	public List<Date> getDueTimes() {
		return dueTimes;
	}

	public void setDueTimes(List<Date> dueTimes) {
		this.dueTimes = dueTimes;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getExpressNo() {
		return expressNo;
	}

	public void setExpressNo(String expressNo) {
		this.expressNo = expressNo;
	}
}
