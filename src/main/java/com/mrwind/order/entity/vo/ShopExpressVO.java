package com.mrwind.order.entity.vo;

import java.util.Date;

import com.mrwind.order.entity.User;

public final class ShopExpressVO {
	
	private String expressNo;
	private User receiver;
	private String remark;
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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
