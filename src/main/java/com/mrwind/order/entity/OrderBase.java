package com.mrwind.order.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import com.alibaba.fastjson.annotation.JSONField;

public class OrderBase implements Serializable{
	private static final long serialVersionUID = 1L;
	@JSONField(serialize=false)
	@Id
	protected String id;
	@Indexed
	protected String status;
	protected String subStatus;
	protected ShopUser shop;
	protected User sender;
	protected User receiver;
	protected Category category;
	protected String remark;
	@Indexed
	protected String bindExpressNo;
	protected Date createTime;
	protected Date updateTime;

	public User getSender() {
		return sender;
	}
	public void setSender(User sender) {
		this.sender = sender;
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
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public ShopUser getShop() {
		return shop;
	}
	public void setShop(ShopUser shop) {
		this.shop = shop;
	}

//	public String getOrderUserType() {
//		return orderUserType;
//	}
//	public void setOrderUserType(String orderUserType) {
//		this.orderUserType = orderUserType;
//	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSubStatus() {
		return subStatus;
	}
	public void setSubStatus(String subStatus) {
		this.subStatus = subStatus;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBindExpressNo() {
		return bindExpressNo;
	}
	public void setBindExpressNo(String bindExpressNo) {
		this.bindExpressNo = bindExpressNo;
	}
}
