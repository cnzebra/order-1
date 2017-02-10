package com.mrwind.order.entity;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;

public class Line {
	
	private Integer index;
	private String fromAddress;
	private String toAddress;
	private Date beginTime;
	private Date endTime;
	private User executorUser;
	
	public Line(){
		
	}
	
	public Line(JSONObject json){
		
	}
	
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public String getFromAddress() {
		return fromAddress;
	}
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	public String getToAddress() {
		return toAddress;
	}
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	public User getExecutorUser() {
		return executorUser;
	}
	public void setExecutorUser(User executorUser) {
		this.executorUser = executorUser;
	}
	public Date getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}
