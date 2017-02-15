package com.mrwind.order.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/***
 * 定金收据
 * @author imacyf0012
 *
 */
@Document
public class OrderDownMoneyReceipt {

	@Id
	private String id;
	private Long expressNo;
	private BigDecimal price;
	private Date createTime;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getExpressNo() {
		return expressNo;
	}
	public void setExpressNo(Long expressNo) {
		this.expressNo = expressNo;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}
