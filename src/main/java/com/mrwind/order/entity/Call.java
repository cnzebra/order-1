package com.mrwind.order.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Call {

	@Id
	private String id;
	private Product product;
	@Indexed
	private User sender;
	private Integer count;
	private String status;
	private Date createTime;
	private Date updateTime;
	@Indexed
	private Shop shopInfo;

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public Shop getShopInfo() {
		return shopInfo;
	}

	public void setShopInfo(Shop shopInfo) {
		this.shopInfo = shopInfo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Call [id=" + id + ", sender=" + sender + ", count=" + count + ", status=" + status + ", createTime="
				+ createTime + ", updateTime=" + updateTime + ", shopInfo=" + shopInfo + "]";
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
}
