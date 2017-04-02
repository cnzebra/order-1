package com.mrwind.order.entity.vo;

import java.io.Serializable;
import java.util.Date;

import com.mrwind.order.entity.User;


public class MapExpressVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1941977575835759107L;
	/**
	 * 
	 */
	private String expressNo;
	private User receiver;
	private User sender;
	private String bindExpressNo;
	private Date dueTime;
	public String getExpressNo() {
		return expressNo;
	}
	public void setExpressNo(String expressNo) {
		this.expressNo = expressNo;
	}
	public User getReceiver() {
		return receiver;
	}
	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}
	public User getSender() {
		return sender;
	}
	public void setSender(User sender) {
		this.sender = sender;
	}
	public String getBindExpressNo() {
		return bindExpressNo;
	}
	public void setBindExpressNo(String bindExpressNo) {
		this.bindExpressNo = bindExpressNo;
	}
	public Date getDueTime() {
		return dueTime;
	}
	public void setDueTime(Date dueTime) {
		this.dueTime = dueTime;
	}
	
}
